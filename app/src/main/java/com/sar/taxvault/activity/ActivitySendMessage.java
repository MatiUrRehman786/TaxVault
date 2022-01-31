package com.sar.taxvault.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.room.Database;

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
import com.sar.taxvault.Model.Chat;
import com.sar.taxvault.Model.SelectedManager;
import com.sar.taxvault.Model.UserModel;
import com.sar.taxvault.MyApplication;
import com.sar.taxvault.databinding.ActivitySendMessageBinding;
import com.sar.taxvault.utils.UIUpdate;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ActivitySendMessage extends BaseActivity {

    ActivitySendMessageBinding binding;

    UserModel user;

    private final int PICK_FILE_REQUEST_CODE = 1;

    Chat msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivitySendMessageBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        getExtrasFromIntent();

        binding.includeView.attachmentIV.setVisibility(View.VISIBLE);

        binding.includeView.attachmentIV.setOnClickListener(v -> checkPermissions());

        getMyProfile();

        setListeners();
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

    private void getExtrasFromIntent() {

        Bundle b = getIntent().getExtras();

        if (b != null) {

            if (b.getParcelable("chat") != null) {

                msg = b.getParcelable("chat");

            }

        }

    }


    private void openFilePicker() {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        intent.addCategory(Intent.CATEGORY_OPENABLE);

        String[] mimeTypes =
                {"application/pdf"};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");

            if (mimeTypes.length > 0) {

                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

            }

        } else {

            String mimeTypesStr = "";

            for (String mimeType : mimeTypes) {

                mimeTypesStr += mimeType + "|";

            }

            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        }


        MyApplication.enteredBackground = false;
//        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"application/pdf"});

        startActivityForResult(Intent.createChooser(intent, "ChooseFile"), PICK_FILE_REQUEST_CODE);
    }


    @Override
    protected void onResume() {

        MyApplication.enteredBackground = false;

        super.onResume();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);

        if (requestCode == PICK_FILE_REQUEST_CODE

                && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.

            if (resultData != null) {

                uri = resultData.getData();

                binding.attachmentIV.setVisibility(View.VISIBLE);
                binding.attachmentTV.setVisibility(View.VISIBLE);
                binding.attachmentTV.setText(getFileName(uri));
            }
        }
    }

    Uri uri;

    private void uploadImage(Uri uri) {

        showLoader();

        String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final String name = uri.getLastPathSegment();

        final StorageReference mChildStorage = FirebaseStorage.getInstance().getReference().child("files").child(myId + uri.getLastPathSegment() + "." + getMimeType(uri));

        UploadTask uploadTask = mChildStorage.putFile(uri);

        uploadTask.addOnSuccessListener(taskSnapshot -> mChildStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                attachmentName = name;

                url = uri.toString();

                sendMessage();

            }
        })
                .addOnFailureListener(e -> onFailed(e)));
    }

    private void onFailed(Exception e) {

        hideLoader();

    }


    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void setListeners() {

        binding.sendBt.setOnClickListener(v -> validateAndSendMsg());

        binding.includeView.menuIV.setOnClickListener(v -> finish());

        binding.includeView.titleTV.setText("Send Message");

    }

    public String attachmentName;

    public String url;

    private void validateAndSendMsg() {

        if (validate()) {

            if (uri != null)
                uploadImage(uri);
            else
                sendMessage();
        }

    }

    private void checkPermissions() {

        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};


        Permissions.check(this/*context*/, permissions, null, null, new PermissionHandler() {
            @Override
            public void onGranted() {

                openFilePicker();

            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {

                showErrorAlert("Please allow storage permissions first");

            }
        });
    }

    private void sendMessage() {

        Chat chat = new Chat();

        chat.isRead = false;

        chat.messageTime = new Date().getTime();

        if (attachmentName != null) {
            chat.fileName = attachmentName;
        }

        chat.fromUserName = user.firstName + " " + user.lastName;
        chat.fromUser = user.userId;
        chat.type = "text";


        if (url != null) {

            chat.fileUrl = url;
            chat.type = "attach";
        }

        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("chat");

        String key = chatRef.push().getKey();

        if (!binding.messageET.getText().toString().isEmpty())

            chat.message = binding.messageET.getText().toString();

        if (msg != null) {

            chat.toUser = msg.fromUser;

            chat.toUserName = msg.fromUserName;

        } else {

            if (manager != null) {

                chat.toUser = manager.id;

                chat.toUserName = manager.businessName;

            }

        }

        chat.messageType = "outgoing";

        chatRef.child(user.getUserId()).child(key).setValue(chat);

        chat.messageType = "incoming";

        chatRef.child(chat.toUser).child(key).setValue(chat);

        finish();

    }

    private boolean validate() {

        if (msg == null && manager == null) {

            showErrorAlert("Please select manager first");

            return false;
        }

        if (user == null) {

            showErrorAlert("Please check your internet connection and try again");

            return false;
        }

        if (binding.messageET.getText().toString().isEmpty() && url == null) {


            showErrorAlert("Message Required");

            return false;
        }

        return true;

    }

    private void getMyProfile() {

        showLoader();

        FirebaseDatabase.getInstance().getReference("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NotNull DataSnapshot snapshot) {

                        hideLoader();

                        if (snapshot.getValue() != null) {

                            user = snapshot.getValue(UserModel.class);

                            if (user != null) {

                                user.setUserId(snapshot.getKey());

                            }

                            getManager();
                        }
                    }

                    @Override
                    public void onCancelled(@NotNull DatabaseError error) {

                        hideLoader();

                        UIUpdate.GetUIUpdate(ActivitySendMessage.this).showAlertDialog("Alert", error.getMessage());
                    }
                });
    }

    ArrayList<SelectedManager> managersList;
    List<String> names;

    private void getManager() {

        if (msg != null) {

            setDetails();

            return;

        }

        showLoader();


        FirebaseDatabase.getInstance().getReference("userBusiness").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NotNull DataSnapshot dataSnapshot) {

                        managersList = new ArrayList<>();
                        names = new ArrayList<>();

                        hideLoader();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren())

                            if (snapshot.getValue() != null) {

                                SelectedManager manager = snapshot.getValue(SelectedManager.class);

                                if (manager != null) {

                                    manager.id = snapshot.getKey();

                                    names.add(manager.businessName);

                                }

                                managersList.add(manager);


                            }

                        setDetails();
                    }

                    @Override
                    public void onCancelled(@NotNull DatabaseError error) {

                        hideLoader();

                        UIUpdate.GetUIUpdate(ActivitySendMessage.this).showAlertDialog("Alert", error.getMessage());
                    }
                });

    }

    SelectedManager manager;

    private void setDetails() {

        if (user != null) {

            binding.fromTV.setText(user.getFirstName() + " " + user.getLastName());

        }

        if (msg != null) {

            binding.toTV.setText(msg.fromUserName);

        } else {

            binding.toTV.setVisibility(View.INVISIBLE);
            binding.spinnerSP.setVisibility(View.VISIBLE);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, names);

            binding.spinnerSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    manager = managersList.get(i);

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            binding.spinnerSP.setAdapter(adapter);


        }

    }

}
