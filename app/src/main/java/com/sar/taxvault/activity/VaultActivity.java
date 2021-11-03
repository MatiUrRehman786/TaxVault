package com.sar.taxvault.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.Toast;

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
import com.sar.taxvault.adapters.RecyclerViewAdapterFiles;
import com.sar.taxvault.databinding.ActivityVaultBinding;
import com.sar.taxvault.utils.UIUpdate;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class VaultActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    ActivityVaultBinding binding;

    String category = "";

    private static final int RC_FILE_PICKER_PERMISSIONS = 123;

    private static final String[] LOCATION_AND_CONTACTS =
            {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CONTACTS};

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

      getData();
    }

    private void getData() {

        UIUpdate.GetUIUpdate(this).setProgressDialog();

        valueEventListener = FirebaseDatabase.getInstance().getReference("Files").child(category)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        UIUpdate.GetUIUpdate(VaultActivity.this).dismissProgressDialog();

                        if (snapshot.getValue() != null)
                            parseSnapshot(snapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                        UIUpdate.GetUIUpdate(VaultActivity.this).dismissProgressDialog();

                        UIUpdate.GetUIUpdate(VaultActivity.this).showAlertDialog("Alert",error.getMessage());
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        UIUpdate.GetUIUpdate(this).destroy();

        valueEventListener = null;
    }

    private void parseSnapshot(DataSnapshot snapshot) {

        List<Document> documents = new ArrayList<>();

        for (DataSnapshot child : snapshot.getChildren()) {

            if (child.getValue() != null)
                documents.add(child.getValue(Document.class));
        }

        setAdapter(documents);
    }

    private void setAdapter(List<Document> documents) {

        LinearLayoutManager layoutManager5 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        binding.filesRV.setLayoutManager(layoutManager5);

        RecyclerViewAdapterFiles adapter = new RecyclerViewAdapterFiles(this, documents);

        binding.filesRV.setItemAnimator(new DefaultItemAnimator());

        binding.filesRV.setAdapter(adapter);
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

        binding.includeView.yearSpinner.setVisibility(View.VISIBLE);

    }

    private void setListeners() {

        binding.includeView.backIV.setOnClickListener(view -> onBackPressed());
        binding.loginBtn3.setOnClickListener(view -> checkPermissions());
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

    private void uploadImage(Uri uri) {

        UIUpdate.GetUIUpdate(this).setProgressDialog();

        String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String name = uri.getLastPathSegment();

        final StorageReference mChildStorage = FirebaseStorage.getInstance().getReference().child("files").child(myId + uri.getLastPathSegment());

        UploadTask uploadTask = mChildStorage.putFile(uri);

        uploadTask.addOnSuccessListener(taskSnapshot -> mChildStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                UIUpdate.GetUIUpdate(VaultActivity.this).dismissProgressDialog();

                final Uri imageUrl = uri;

                uploadDataToFirebase(uri, name);
            }
        }).addOnFailureListener(e -> onFailed(e)));
    }

    private void uploadDataToFirebase(Uri uri, String name) {

        Long timeStamp = new Date().getTime();

        Double size = getFileSize(uri);

        Document document = new Document(name, timeStamp, size+" GB", uri.toString());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Files").child(category);

        String key = reference.push().getKey();

        reference.child(key).setValue(document);
    }

    private Double getFileSize(Uri uri) {

        File file = new File(uri.toString());

        try {
            return new Long(file.length()).doubleValue()/1024;
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
            openFilePicker();
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(
                    this,
                    "Please provide storage permissions to continue",
                    RC_FILE_PICKER_PERMISSIONS,
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

            openFilePicker();

        } else {
            //todo enable all permissions popup
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {

        if (EasyPermissions.somePermissionPermanentlyDenied(this, list)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }
}