package com.sar.taxvault.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.sar.taxvault.Model.UserModel;
import com.sar.taxvault.MyApplication;
import com.sar.taxvault.R;
import com.sar.taxvault.classes.SessionManager;
import com.sar.taxvault.databinding.ActivitySettingsBinding;
import com.sar.taxvault.utils.UIUpdate;
import com.williammora.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class SettingsActivity extends BaseActivity {

    ActivitySettingsBinding binding;

    private FirebaseAuth mAuth;

    public FirebaseDatabase rootNode;

    public DatabaseReference mDatabase;

    Uri imageUri = null;

    final int ACCESS_Gallery = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivitySettingsBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();

        setContentView(view);

        initFireBase();

        setView();

        setListeners();

        if (isOnline()) {

            getUserData();

        } else {

            showMessage("Check your internet connection!");

        }

    }

    private void initFireBase() {

        rootNode = FirebaseDatabase.getInstance();

        mDatabase = rootNode.getReference("User");

        mAuth = FirebaseAuth.getInstance();

    }

    private void checkPermissions() {

        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};


        Permissions.check(this/*context*/, permissions, null, null, new PermissionHandler() {
            @Override
            public void onGranted() {

                showImagePickerDialog();

            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {

                showMessage("Please allow storage and camera permissions first");

            }
        });
    }


    public void showImagePickerDialog() {

        // setup the alert builder

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose");

        // add a list

        String[] animals = {"Camera", "Gallery"};
        builder.setItems(animals, (dialog, which) -> {
            switch (which) {
                case 0:
                    dispatchTakePictureIntent();
                    break;
                case 1:
                    openGallery();
                    break;
            }
        });

        // create and show the alert dialog

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        dispatchTakePictureLauncher.launch(takePictureIntent);

    }

    ActivityResultLauncher<Intent> dispatchTakePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {

                    Bitmap photo = null;

                    if (result.getData() != null) {

                        photo = (Bitmap) result.getData().getExtras().get("data");

                        imageUri = getImageUri(SettingsActivity.this, photo);

                        binding.profileImageCIV.setImageBitmap(photo);

                        uploadImage();

                    }

                }
            });

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACCESS_Gallery && resultCode == Activity.RESULT_OK) {

            imageUri = data.getData();

            binding.profileImageCIV.setImageURI(imageUri);

            uploadImage();
        }
    }

    public String getMimeType(Uri uri) {

        String extension;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }

        return extension;
    }

    private void uploadImage() {
//        isUploading = true;

        showLoader();

        String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final StorageReference mChildStorage = FirebaseStorage.getInstance().getReference().child("files").child(myId + imageUri.getLastPathSegment() + "." + getMimeType(imageUri));

        UploadTask uploadTask = mChildStorage.putFile(imageUri);

        uploadTask.addOnSuccessListener(taskSnapshot -> mChildStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                final Uri imageUrl = uri;

                FirebaseDatabase.getInstance().getReference("User")
                        .child(myId)
                        .child("imageUrl")
                        .setValue(imageUrl.toString());

                hideLoader();

            }
        })
                .addOnFailureListener(e -> onFailed(e)));
    }

    private void onFailed(Exception e) {

        hideLoader();

        showErrorAlert(e.getLocalizedMessage());
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, new Date().toString(), null);

        return Uri.parse(path);
    }

    private void openGallery() {

        Intent intent = new Intent();

        intent.setType("image/*");

        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_image)), ACCESS_Gallery);

    }


    @Override
    protected void onResume() {

        MyApplication.enteredBackground = false;

        super.onResume();

//        getCurrentUser();

    }

    private void getUserData() {


        UIUpdate.GetUIUpdate(this).destroy();
        showLoader();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        DatabaseReference usersRef = rootRef.child("User").child(mAuth.getCurrentUser().getUid());

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hideLoader();

                UserModel user = dataSnapshot.getValue(UserModel.class);

                setUserData(user);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("userDataResponse", databaseError.getMessage());
            }
        };
        usersRef.addListenerForSingleValueEvent(valueEventListener);

    }

    private void setUserData(UserModel user) {

        binding.userNameTV.setText(user.getFirstName() + " " + user.getLastName());

        if(user.imageUrl != null) {

            Glide.with(SettingsActivity.this)
                    .load(user.imageUrl)
                    .apply(new RequestOptions().centerCrop())
                    .into(binding.profileImageCIV);

        }

        binding.emailTV.setText(user.getEmail());

        if (user.getUserType() != null) {

            binding.typeTV.setText(user.getUserType());
        }

        binding.userFullNameTV.setText(user.getFirstName() + " " + user.getLastName());

        binding.userEmailAddressTV.setText(user.getEmail());

        binding.userPhoneNumberTV.setText(user.getPhoneNumber());

    }

    private void setView() {

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        binding.includeView.titleTV.setText("Profile");

    }

    private void setListeners() {

        binding.includeView.backIV.setOnClickListener(view -> {

            finish();

        });

        binding.includeView.backIV.setOnClickListener(v -> finish());
        binding.logoutBtn.setOnClickListener(v -> logOut());
        binding.updateBtn.setOnClickListener(v -> updateProfile());
        binding.profileImageCIV.setOnClickListener(v -> checkPermissions());

    }

    private void updateProfile() {

        String name = binding.userFullNameTV.getText().toString();

        if (name.contains(" ")) {

            String[] data = name.split(" ");

            mDatabase.child(mAuth.getCurrentUser().getUid()).child("firstName").setValue(data[0]);

            mDatabase.child(mAuth.getCurrentUser().getUid()).child("lastName").setValue(data[1]);

        } else {

            mDatabase.child(mAuth.getCurrentUser().getUid()).child("firstName").setValue(name);

        }

        mDatabase.child(mAuth.getCurrentUser().getUid()).child("phoneNumber").setValue(binding.userPhoneNumberTV.getText().toString());

        Toast.makeText(SettingsActivity.this, "Profile Updated!", Toast.LENGTH_SHORT).show();

        getUserData();

    }

    private void logOut() {

        FirebaseAuth.getInstance().signOut();

        SessionManager.getInstance().setUser(null, SettingsActivity.this);

        Intent intent = new Intent(SettingsActivity.this, Login.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);

//        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }

    private void showMessage(String message) {

        Snackbar.with(SettingsActivity.this)
                .text(message)
                .show(SettingsActivity.this);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();

//        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }
}