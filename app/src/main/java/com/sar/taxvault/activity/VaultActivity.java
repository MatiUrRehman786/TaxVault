package com.sar.taxvault.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.CompoundButton;

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
import com.sar.taxvault.Model.Document;
import com.sar.taxvault.Model.UserModel;
import com.sar.taxvault.R;
import com.sar.taxvault.adapters.RecyclerViewAdapterFiles;
import com.sar.taxvault.databinding.ActivityVaultBinding;
import com.sar.taxvault.utils.UIUpdate;
import com.sar.taxvault.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class VaultActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    ActivityVaultBinding binding;

    String category = "";

    UserModel user;

    Document selectedDocument;

    private static final int RC_FILE_PICKER_PERMISSIONS = 123;
    private static final int RC_SHARE_FILE_PICKER_PERMISSIONS = 12;

    private final int PICK_FILE_REQUEST_CODE = 1;

    ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityVaultBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        getExtrasFromIntent();

        init();

        setView();

        setListeners();

        getCurrentUser();

    }

    private void getData(String date) {

        UIUpdate.GetUIUpdate(this).setProgressDialog();

        valueEventListener = FirebaseDatabase.getInstance().getReference("Files").child(category)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        UIUpdate.GetUIUpdate(VaultActivity.this).dismissProgressDialog();

                        if (snapshot.getValue() != null)
                            parseSnapshot(snapshot, date);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                        UIUpdate.GetUIUpdate(VaultActivity.this).dismissProgressDialog();

                        UIUpdate.GetUIUpdate(VaultActivity.this).showAlertDialog("Alert", error.getMessage());
                    }
                });
    }

    private void getCurrentUser() {

        UIUpdate.GetUIUpdate(this).setProgressDialog();

        FirebaseDatabase.getInstance().getReference("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid())

                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        UIUpdate.GetUIUpdate(VaultActivity.this).dismissProgressDialog();

                        if (snapshot.getValue() != null) {

                            user = snapshot.getValue(UserModel.class);

                            getData("");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                        UIUpdate.GetUIUpdate(VaultActivity.this).dismissProgressDialog();

                        UIUpdate.GetUIUpdate(VaultActivity.this).showAlertDialog("Alert", error.getMessage());
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        UIUpdate.GetUIUpdate(this).destroy();

        valueEventListener = null;
    }

    private void parseSnapshot(DataSnapshot snapshot, String date) {

        List<Document> documents = new ArrayList<>();

        for (DataSnapshot child : snapshot.getChildren()) {

            if (child.getValue() != null) {

                Document document = child.getValue(Document.class);

                document.setId(child.getKey());

                if (document.belongsToCurrentUser()) {

                    if (date.equals("")) {

                        documents.add(document);

                    } else if (document.getTime().contains(date)) {

                        documents.add(document);

                    }

                }
            }
        }

        setAdapter(documents);
    }


    private void setAdapter(List<Document> documents) {

        LinearLayoutManager layoutManager5 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        binding.filesRV.setLayoutManager(layoutManager5);

        RecyclerViewAdapterFiles adapter = new RecyclerViewAdapterFiles(this, documents);

        binding.filesRV.setItemAnimator(new DefaultItemAnimator());

        binding.filesRV.setAdapter(adapter);

        adapter.setCallback(this::showDialog);
    }

    void showDialog(Document document) {

        Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_file_options);

        AppCompatTextView accessTV = dialog.findViewById(R.id.accessTV);
        AppCompatImageView accessIV = dialog.findViewById(R.id.accessIV);

        SwitchCompat accessSwitch = dialog.findViewById(R.id.accessSwitch);

        accessSwitch.setChecked(document.getHasAccessToShare());

        accessIV.setOnClickListener(view -> accessSwitch.setChecked(!accessSwitch.isChecked()));
        accessTV.setOnClickListener(view -> accessSwitch.setChecked(!accessSwitch.isChecked()));

        accessSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            document.setHasAccessToShare(b);
            FirebaseDatabase.getInstance().getReference("Files").child(category).child(document.getId()).setValue(document);
        });

        AppCompatTextView shareTV = dialog.findViewById(R.id.shareTV);
        AppCompatImageView shareIV = dialog.findViewById(R.id.shareIV);

        AppCompatTextView deleteTV = dialog.findViewById(R.id.deleteTV);
        AppCompatImageView deleteIV = dialog.findViewById(R.id.deleteIV);

        shareIV.setOnClickListener(view -> hasAccessToShare(document));
        shareIV.setOnClickListener(view -> hasAccessToShare(document));

        deleteIV.setOnClickListener(view -> deleteDocument(document, dialog));
        deleteTV.setOnClickListener(view -> deleteDocument(document, dialog));

        shareIV.setOnClickListener(view -> checkSharePermissions(document, dialog));
        shareTV.setOnClickListener(view -> checkSharePermissions(document, dialog));

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void hasAccessToShare(Document document) {
    }

    private void deleteDocument(Document document, Dialog dialog) {

        dialog.dismiss();

        UIUpdate.GetUIUpdate(this).setProgressDialog();

        FirebaseDatabase.getInstance().getReference("Files").child(category).child(document.getId()).removeValue()

                .addOnCompleteListener(task -> {

                    StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(document.getUrl());

                    if (!task.isSuccessful() && task.getException() != null)

                        UIUpdate.GetUIUpdate(VaultActivity.this).showAlertDialog("Alert", task.getException().getLocalizedMessage());

                    reference.delete().addOnCompleteListener(task1 -> {

                        UIUpdate.GetUIUpdate(VaultActivity.this).dismissProgressDialog();

                        if (!task1.isSuccessful() && task.getException() != null)

                            UIUpdate.GetUIUpdate(VaultActivity.this).showAlertDialog(
                                    "Alert",
                                    task.getException().getLocalizedMessage()
                            );

                    });
                });

    }

    private void init() {

        UIUpdate.GetUIUpdate(this).destroy();
    }

    private void getExtrasFromIntent() {

        if (getIntent().getExtras() == null)

            return;

        Bundle b = getIntent().getExtras();

        category = b.getString("category");

    }

    private void setView() {

        binding.includeView.titleTV.setText("Vault");

        binding.includeView.yearSpinner.setVisibility(View.INVISIBLE);

    }

    private void setListeners() {

        binding.includeView.backIV.setOnClickListener(view -> onBackPressed());

        binding.loginBtn3.setOnClickListener(view -> checkPermissions());

        binding.includeView.yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i != 0) {

                    getData(binding.includeView.yearSpinner.getSelectedItem().toString());

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void openFilePicker() {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        intent.addCategory(Intent.CATEGORY_OPENABLE);

        intent.setType("application/pdf|image/*");

        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "application/pdf"});

        startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);

        if (requestCode == PICK_FILE_REQUEST_CODE
                && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            Uri uri = null;

            if (resultData != null) {

                uri = resultData.getData();

                uploadImage(uri);
            }
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

    private void uploadImage(Uri uri) {

        UIUpdate.GetUIUpdate(this).setProgressDialog();

        String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String name = uri.getLastPathSegment();

        final StorageReference mChildStorage = FirebaseStorage.getInstance().getReference().child("files").child(myId + uri.getLastPathSegment() + "." + getMimeType(uri));

        UploadTask uploadTask = mChildStorage.putFile(uri);

        uploadTask.addOnSuccessListener(taskSnapshot -> mChildStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                UIUpdate.GetUIUpdate(VaultActivity.this).dismissProgressDialog();

                final Uri imageUrl = uri;


                uploadDataToFirebase(uri, name, taskSnapshot.getMetadata().getSizeBytes());
            }
        }).addOnFailureListener(e -> onFailed(e)));
    }

    private void uploadDataToFirebase(Uri uri, String name, long sizeBytes) {

        Long timeStamp = new Date().getTime();


        Document document = new Document(name, timeStamp, sizeBytes, uri.toString());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Files").child(category);

        String key = reference.push().getKey();

        reference.child(key).setValue(document);

        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference("User").child(id).setValue(user);
    }

    private Double getFileSize(Uri uri) {

        File file = new File(uri.toString());

        try {
            return new Long(file.length()).doubleValue() / 1024;
        } catch (Exception e) {

        }

        return 0.0;
    }

    private void onFailed(Exception e) {

        UIUpdate.GetUIUpdate(VaultActivity.this).dismissProgressDialog();

        UIUpdate.GetUIUpdate(VaultActivity.this).showAlertDialog("Alert", e.getLocalizedMessage());
    }


    @AfterPermissionGranted(RC_FILE_PICKER_PERMISSIONS)
    public void checkPermissions() {

        if (hasStoragePermissions()) {
            // Have permission, do the thing!
            check(user);

        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(
                    this,
                    "Please provide storage permissions to continue",
                    RC_FILE_PICKER_PERMISSIONS,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            );
        }
    }

    @AfterPermissionGranted(RC_SHARE_FILE_PICKER_PERMISSIONS)
    public void checkSharePermissions(Document document, Dialog dialog) {

        dialog.dismiss();

        if (hasStoragePermissions()) {
            // Have permission, do the thing!
            share(document);
        } else {

            selectedDocument = document;

            // Ask for one permission
            EasyPermissions.requestPermissions(
                    this,
                    "Please provide storage permissions to continue",
                    RC_SHARE_FILE_PICKER_PERMISSIONS,
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    private boolean hasStoragePermissions() {
        return EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @Override
    public void onBackPressed() {

        finish();

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {

        if (list.size() == 2) {

            if (RC_FILE_PICKER_PERMISSIONS == requestCode)
                check(user);
            else
                share(selectedDocument);

        } else {

            UIUpdate.GetUIUpdate(this).showAlertDialog("Alert", "Please allow all storage permissions to continue.");

        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {

        if (EasyPermissions.somePermissionPermanentlyDenied(this, list)) {

            new AppSettingsDialog.Builder(this).build().show();

        }
    }

    private void share(Document document) {

        UIUpdate.GetUIUpdate(this).setProgressDialog();

        final String filename = document.getName() + new Date().toString();

        String url = document.getUrl();
        String urlWithExtension = url.substring(0, url.lastIndexOf('?'));
        String extension = urlWithExtension.substring(urlWithExtension.lastIndexOf('.'));

        File f = new File(Utils.getRootDirPath(VaultActivity.this), filename + extension);

        if (!f.exists()) {
            try {
                f.createNewFile();

            } catch (IOException e) {
                UIUpdate.GetUIUpdate(this).dismissProgressDialog();
                UIUpdate.GetUIUpdate(this).showAlertDialog("Alert", e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(document.getUrl());

        storageRef.getFile(f).addOnSuccessListener(taskSnapshot -> {

            UIUpdate.GetUIUpdate(VaultActivity.this).dismissProgressDialog();

            Intent intentShareFile = new Intent(Intent.ACTION_SEND);

            Uri photoURI = FileProvider.getUriForFile(VaultActivity.this, getApplicationContext().getPackageName() + ".provider", f);

            intentShareFile.setType("text/*");

            intentShareFile.putExtra(Intent.EXTRA_STREAM, photoURI);
            intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "MyApp File Share: " + f.getName());
            intentShareFile.putExtra(Intent.EXTRA_TEXT, "MyApp File Share: " + f.getName());

            this.startActivity(Intent.createChooser(intentShareFile, f.getName()));

        }).addOnFailureListener(exception -> {

            UIUpdate.GetUIUpdate(VaultActivity.this).dismissProgressDialog();
            UIUpdate.GetUIUpdate(VaultActivity.this).showAlertDialog("Alert", exception.getLocalizedMessage());
        });

    }


    private void check(UserModel user) {

        UIUpdate.GetUIUpdate(VaultActivity.this).setProgressDialog();

        FirebaseDatabase.getInstance().getReference("Files")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {


                        if (snapshot.getValue() != null)
                            parseSnapshotBytesCalculation(snapshot);
                        else
                            UIUpdate.GetUIUpdate(VaultActivity.this).dismissProgressDialog();

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                        UIUpdate.GetUIUpdate(VaultActivity.this).dismissProgressDialog();

                        UIUpdate.GetUIUpdate(VaultActivity.this).showAlertDialog("Alert", error.getMessage());
                    }
                });
    }


    private void parseSnapshotBytesCalculation(DataSnapshot snapshot) {

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Long bytes = 0l;

        for (DataSnapshot child : snapshot.getChildren()) {

            for (DataSnapshot documentSnapshot : child.getChildren()) {

                Document document = documentSnapshot.getValue(Document.class);

                if (document.getUserId().equalsIgnoreCase(userId)) {

                    bytes = bytes + document.getSize();
                }
            }
        }

        long GB = 1073741824;

        UIUpdate.GetUIUpdate(VaultActivity.this).dismissProgressDialog();


        if (bytes < GB || hasSubscription()) {

            openFilePicker();

        } else {

            UIUpdate.GetUIUpdate(this).showAlertDialog(
                    "Alert",
                    "You've reached max upload limit. Purchase premium to get unlimited uploads."
            );

        }
    }

    private Boolean hasSubscription() {

        if (user.getCurrentPackage() != null) {

            Long ts = user.getPurchasedTSp();

            int diffInDays = (int) ((ts - new Date().getTime())
                    / (1000 * 60 * 60 * 24));

            if (user.getCurrentPackage().equalsIgnoreCase("monthly")) {

                if (diffInDays < 30) {
                    return true;
                }

            }


            if (user.getCurrentPackage().equalsIgnoreCase("yearly")) {

                if (diffInDays < 365) {

                    return true;
                }

            }

        }

        return false;

    }

}