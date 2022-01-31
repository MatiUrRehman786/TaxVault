package com.sar.taxvault.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.pdf.PdfDocument;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.pdftron.pdf.PDFNet;
import com.pdftron.pdf.config.ViewerConfig;
import com.sar.taxvault.BuildConfig;
import com.sar.taxvault.Model.Document;
import com.sar.taxvault.Model.UserModel;
import com.sar.taxvault.MyApplication;
import com.sar.taxvault.R;
import com.sar.taxvault.adapters.RecyclerViewAdapterFiles;
import com.sar.taxvault.databinding.ActivityVaultBinding;
import com.sar.taxvault.utils.UIUpdate;
import com.sar.taxvault.utils.Utils;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static com.pdftron.pdf.utils.Utils.getRealPathFromURI;
import static com.sar.taxvault.utils.Utils.getExternalFilesDirPath;
import static com.sar.taxvault.utils.Utils.getRootDirPath;

public class VaultActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {

    ActivityVaultBinding binding;

    String category = "";

    String year;

    Uri imageUri = null;

    Boolean isUploading;

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

        isUploading = false;

        init();

        setView();

        setListeners();

        shouldShowLoader = true;

    }

    @Override
    protected void onResume() {

        MyApplication.enteredBackground = false;

        super.onResume();

        if (shouldShowLoader)

            getCurrentUser();


        shouldShowLoader = true;
    }

    int selectedYear;

    private void createYearPicker() {


        final Calendar today = Calendar.getInstance();

        selectedYear = today.get(Calendar.YEAR);

        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(VaultActivity.this, (selectedMonth, selectedYear) -> {


            Log.d(TAG, "selectedMonth : " + selectedMonth + " selectedYear : " + selectedYear);

            this.selectedYear = selectedYear;

            uploadImage(pdfUri);

            Toast.makeText(VaultActivity.this, "Date set with month" + selectedMonth + " year " + selectedYear, Toast.LENGTH_SHORT).show();

        }, today.get(Calendar.YEAR), today.get(Calendar.MONTH));

        builder.setActivatedMonth(Calendar.JULY)
                .setMinYear(2000)
                .setMaxYear(today.get(Calendar.YEAR) + 10)
                .showYearOnly()
                .setMinMonth(Calendar.FEBRUARY)
                .setTitle("Select Year")
                .setMonthRange(Calendar.FEBRUARY, Calendar.NOVEMBER)
                .setOnMonthChangedListener(selectedMonth -> {
                    Log.d(TAG, "Selected month : " + selectedMonth);
                    // Toast.makeText(MainActivity.this, " Selected month : " + selectedMonth, Toast.LENGTH_SHORT).show();
                })
                .setOnYearChangedListener(selectedYear -> {
                    Log.d(TAG, "Selected year : " + selectedYear);
                    // Toast.makeText(MainActivity.this, " Selected year : " + selectedYear, Toast.LENGTH_SHORT).show();
                })
                .build()
                .show();
    }

    private void getData(String date) {

        showLoader();

        valueEventListener = FirebaseDatabase.getInstance().getReference("ManagerFiles").child(user.getUserId())

                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NotNull DataSnapshot snapshot) {

                        if (snapshot.getValue() != null)

                            parseSnapshotBusinessFiles(snapshot, date);

                        else

                            setAdapter(new ArrayList<>());

                    }

                    @Override
                    public void onCancelled(@NotNull DatabaseError error) {

                        hideLoader();

                        UIUpdate.GetUIUpdate(VaultActivity.this).showAlertDialog("Alert", error.getMessage());
                    }
                });
    }

    private void getCurrentUser() {

        hideLoader();

        FirebaseDatabase.getInstance().getReference("User").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())

                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        hideLoader();

                        if (snapshot.getValue() != null) {

                            user = snapshot.getValue(UserModel.class);

                            if (user != null) {
                                user.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            }

                            getData("");
                        } else

                            hideLoader();

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                        hideLoader();

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

    private void parseSnapshotBusinessFiles(DataSnapshot snapshot, String date) {

        List<Document> documents = new ArrayList<>();

        for (DataSnapshot child : snapshot.getChildren()) {

            if (child.getValue() != null) {

                Document document = child.getValue(Document.class);

                if (document != null) {

                    document.setId(child.getKey());

                }

                if (document.getType().equalsIgnoreCase(category) && document.getTime().contains(year))

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

        if (!isUploading)

            hideLoader();

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

        AppCompatTextView moveTV = dialog.findViewById(R.id.moveTV);
        AppCompatTextView accessTV = dialog.findViewById(R.id.accessTV);
        AppCompatImageView accessIV = dialog.findViewById(R.id.accessIV);
        AppCompatImageView moveIV = dialog.findViewById(R.id.moveIV);

        moveIV.setOnClickListener(v -> {
            dialog.dismiss();

            MoveHereActivity.start(VaultActivity.this, document);
        });

        moveTV.setOnClickListener(v -> {
            dialog.dismiss();

            MoveHereActivity.start(VaultActivity.this, document);
        });

        SwitchCompat accessSwitch = dialog.findViewById(R.id.accessSwitch);

        accessSwitch.setChecked(document.getHasAccessToShare());

        accessIV.setOnClickListener(view -> accessSwitch.setChecked(!accessSwitch.isChecked()));

        accessTV.setOnClickListener(view -> accessSwitch.setChecked(!accessSwitch.isChecked()));

        accessSwitch.setOnCheckedChangeListener((compoundButton, b) -> {

            document.setHasAccessToShare(b);

            FirebaseDatabase.getInstance().getReference("ManagerFiles").child(document.getUserId()).child(document.getId()).setValue(document);

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

        showLoader();

        FirebaseDatabase.getInstance().getReference("ManagerFiles").child(user.getUserId()).child(document.getId()).removeValue()

                .addOnCompleteListener(task -> {

                    StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(document.getUrl());

                    if (!task.isSuccessful() && task.getException() != null)

                        UIUpdate.GetUIUpdate(VaultActivity.this).showAlertDialog("Alert", task.getException().getLocalizedMessage());

                    reference.delete().addOnCompleteListener(task1 -> {

                        hideLoader();

                        getData("");

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
        year = b.getString("year");

    }

    private void setView() {

        binding.includeView.titleTV.setText("Vault");

        binding.includeView.yearSpinner.setVisibility(View.INVISIBLE);

    }

    private void setListeners() {

        binding.includeView.backIV.setOnClickListener(view -> onBackPressed());

        binding.loginBtn3.setOnClickListener(view -> checkPermissions());


    }

    private void openFilePicker() {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        intent.addCategory(Intent.CATEGORY_OPENABLE);

        String[] mimeTypes =
                {"application/*"};

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

        startActivityForResult(Intent.createChooser(intent, "ChooseFile"), PICK_FILE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);

        isUploading = true;

        if (requestCode == PICK_FILE_REQUEST_CODE

                && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            Uri uri = null;

            if (resultData != null) {

                uri = resultData.getData();

                pdfEditLauncher.launch(MainActivity.start(VaultActivity.this, uri));

            }
        }
    }

    private void openEditor(Uri uri) {
        ViewerConfig config = new ViewerConfig.Builder().openUrlCachePath(this.getCacheDir().getAbsolutePath()).build();

        // intent builder

        Intent intent = CustomDocumentActivity.IntentBuilder.fromActivityClass(this, CustomDocumentActivity.class)
                .withUri(uri)
                .usingConfig(config)
                .usingTheme(R.style.PDFTronAppTheme)
                .build();

        startActivity(intent);

    }

    ActivityResultLauncher<Intent> pdfEditLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    shouldShowLoader = false;

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Here, no request code
                        pdfUri = result.getData().getData();

                        createYearPicker();

                    }
                }
            });

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
        isUploading = true;

        showLoader();

        String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String name = uri.getLastPathSegment();

        final StorageReference mChildStorage = FirebaseStorage.getInstance().getReference().child("files").child(myId + uri.getLastPathSegment() + "." + getMimeType(uri));

        UploadTask uploadTask = mChildStorage.putFile(uri);

        uploadTask.addOnSuccessListener(taskSnapshot -> mChildStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                uploadDataToFirebase(uri, name, taskSnapshot.getMetadata().getSizeBytes());
            }
        })
                .addOnFailureListener(e -> onFailed(e)));
    }

    Uri pdfUri;

    private void uploadDataToFirebase(Uri uri, String name, long sizeBytes) {

        shouldShowLoader = true;

        FirebaseDatabase.getInstance().getReference("User")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                        if (snapshot.getValue() != null) {

                            UserModel user = snapshot.getValue(UserModel.class);

                            user.setUserId(snapshot.getKey());

                            Long timeStamp = new Date().getTime();

                            String str = selectedYear + "-03-04 11:30: 40";

                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                            try {

                                Date date = format.parse(str);

                                timeStamp = date.getTime();

                            } catch (ParseException e) {

                                e.printStackTrace();

                            }


                            Document document = new Document(name, timeStamp, sizeBytes, uri.toString());

                            document.setBusinessId(user.getBusinessId());

                            isUploading = false;

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ManagerFiles")
                                    .child(user.getUserId());

                            String key = reference.push().getKey();

                            document.setType(category);

                            document.setUserName(user.getFirstName() + " " + user.getLastName());

                            reference.child(key).setValue(document);

                            String id = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            FirebaseDatabase.getInstance().getReference("User").child(id).setValue(user);
//                            getCurrentUser();

                        } else {

                            hideLoader();

                        }


                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

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

        hideLoader();

        showErrorAlert(e.getLocalizedMessage());
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
                    Manifest.permission.CAMERA,
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
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA);
        }
    }

    private boolean hasStoragePermissions() {
        return EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA);
    }

    @Override
    public void onBackPressed() {

        finish();

//        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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

        showLoader();

        final String filename = document.getName() + new Date().toString();

        String url = document.getUrl();
        String urlWithExtension = url.substring(0, url.lastIndexOf('?'));
        String extension = urlWithExtension.substring(urlWithExtension.lastIndexOf('.'));

        File f = new File(getRootDirPath(VaultActivity.this), filename + extension);

        if (!f.exists()) {

            try {

                f.createNewFile();

            } catch (IOException e) {

                hideLoader();

                UIUpdate.GetUIUpdate(this).showAlertDialog("Alert", e.getLocalizedMessage());

                e.printStackTrace();

            }

        }

        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(document.getUrl());

        storageRef.getFile(f).addOnSuccessListener(taskSnapshot -> {

            hideLoader();

            Intent intentShareFile = new Intent(Intent.ACTION_SEND);

            Uri photoURI = FileProvider.getUriForFile(VaultActivity.this, "com.sar.taxvault.provider", f);

            intentShareFile.setType("text/*");

            intentShareFile.putExtra(Intent.EXTRA_STREAM, photoURI);
            intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "MyApp File Share: " + f.getName());
            intentShareFile.putExtra(Intent.EXTRA_TEXT, "MyApp File Share: " + f.getName());

            this.startActivity(Intent.createChooser(intentShareFile, f.getName()));

        }).addOnFailureListener(exception -> {

            hideLoader();
            UIUpdate.GetUIUpdate(VaultActivity.this).showAlertDialog("Alert", exception.getLocalizedMessage());
        });

    }


    private void

    check(UserModel user) {

        showLoader();

        FirebaseDatabase.getInstance().getReference("ManagerFiles")
                .child(user.getUserId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {


                        if (snapshot.getValue() != null)

                            parseSnapshotBytesCalculation(snapshot);

                        else {

                            showImagePickerDialog();

                            hideLoader();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                        hideLoader();

                        UIUpdate.GetUIUpdate(VaultActivity.this).showAlertDialog("Alert", error.getMessage());
                    }
                });
    }

    public void showImagePickerDialog() {

        // setup the alert builder

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose");
        // add a list

        String[] animals = {"Camera", "Gallery", "PDF File"};
        builder.setItems(animals, (dialog, which) -> {
            switch (which) {
                case 0:
                    dispatchTakePictureIntent();
                    break;
                case 1:
                    openGallery();
                    break;
                case 2:
                    openFilePicker();
                    break;
            }
        });

        // create and show the alert dialog

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    String currentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    Boolean shouldShowLoader = true;

    private void dispatchTakePictureIntent() {

        hideLoader();

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                showErrorAlert("Unable to access mobile storage");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.sar.taxvault.provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                shouldShowLoader = false;

                dispatchTakePictureLauncher.launch(takePictureIntent);
            }
        }
    }

    private void openGallery() {

        hideLoader();

        Intent intent = new Intent();

        intent.setType("image/*");

        intent.setAction(Intent.ACTION_GET_CONTENT);

        shouldShowLoader = false;

        galleryIntentLauncher.launch(Intent.createChooser(intent, "Pick Image"));

    }

    ActivityResultLauncher<Intent> galleryIntentLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {

                    Bitmap photo = null;

                    if (result.getData() != null) {

                        imageUri = result.getData().getData();

                        try {
                            makePDF(new File(imageUri.toString()).getAbsolutePath()
                            );
                        } catch (IOException e) {
                            e.printStackTrace();
                            showErrorAlert(e.getLocalizedMessage());
                        } catch (DocumentException e) {
                            e.printStackTrace();
                            showErrorAlert(e.getLocalizedMessage());
                        }
                    }

                }
            });

    ActivityResultLauncher<Intent> dispatchTakePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {

                    imageUri = Uri.fromFile(new File(currentPhotoPath));

//                    Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
                    try {
                        makePDF(new File(imageUri.toString()).getAbsolutePath());
                    } catch (IOException e) {
                        e.printStackTrace();
                        showErrorAlert(e.getLocalizedMessage());
                    } catch (DocumentException e) {
                        e.printStackTrace();
                        showErrorAlert(e.getLocalizedMessage());
                    }
//                    pdfEditLauncher.launch(MainActivity.start(VaultActivity.this, imageUri));
//                        compressImage(currentPhotoPath);

//                    }

                }
            });

    PdfDocument pdfDocument;

    public void makePDF(String path) throws IOException, DocumentException {

        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        byte[] bytesData = stream.toByteArray();

        stream.close();

        com.itextpdf.text.Document document = new com.itextpdf.text.Document();

        String directoryPath = getRootDirPath(VaultActivity.this);

        String path1 = getExternalFilesDirPath() + "/" + UUID.randomUUID().toString() + ".pdf";

        PdfWriter.getInstance(document, new FileOutputStream(path1)); //  Change pdf's name.

        document.open();

        Image image = Image.getInstance(bytesData);
//
        image.setAlignment(Image.ALIGN_CENTER);

        document.add(image);

        document.close();

        File file = new File(path1);

        Uri uri = FileProvider.getUriForFile(VaultActivity.this,
                BuildConfig.APPLICATION_ID + ".provider", file);

        pdfEditLauncher.launch(MainActivity.start(VaultActivity.this, uri));
    }

    public void saveFile() {
        if (pdfDocument == null) {
            Log.i("local-dev", "pdfDocument in 'saveFile' function is null");
            return;
        }
        File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "ImgToPDF");
        boolean isDirectoryCreated = root.exists();
        if (!isDirectoryCreated) {
            isDirectoryCreated = root.mkdir();
        }
//        if (UUID.randomUUID().toString()+".pdf" !
//        ) {
        String userInputFileName = UUID.randomUUID().toString() + ".pdf";
        File file = new File(root, userInputFileName);
        try {

            FileOutputStream fileOutputStream = new FileOutputStream(file);

            pdfDocument.writeTo(fileOutputStream);

            pdfDocument.close();

            pdfEditLauncher.launch(MainActivity.start(VaultActivity.this, Uri.fromFile(file)));

        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Unable to open this file");
        }
//        }
//        Log.i("local-dev", "'saveFile' function successfully done");
//        Toast.makeText(this, "Successful! PATH:\n" + "Internal Storage/" + Environment.DIRECTORY_DOWNLOADS, Toast.LENGTH_SHORT).show();
    }
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == ACCESS_Gallery && resultCode == Activity.RESULT_OK) {
//
//            imageUri = data.getData();
//
//            binding.userImageIV.setImageURI(imageUri);
//
//            uploadImage(null);
//        }


    public Uri getImageUri(Context inContext, Bitmap inImage) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, new Date().toString(), null);

        return Uri.parse(path);
    }


    private void parseSnapshotBytesCalculation(DataSnapshot snapshot) {

        Long bytes = 0l;

        for (DataSnapshot child : snapshot.getChildren()) {

            Document document = child.getValue(Document.class);
            document.setId(child.getKey());

            bytes = bytes + document.getSize();
        }


        long GB = 1073741824;

        if (hasSubscription())
            GB = GB * 3;

        hideLoader();

        if (bytes < GB || hasSubscription()) {

            showImagePickerDialog();

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


    //PATH

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "SureBet");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
//        Log.d("IMAGES",""+uriSting);
        return uriSting;
    }

    //compressor
    public void compressImage(String imageUri) {
        Uri contentUri = Uri.parse(imageUri);
        String filePath = getRealPathFromURI(this, contentUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
//            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
//                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
//                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
//                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        uploadImage(Uri.fromFile(new File(filename)));

//        try {
        //display
//            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(new File(filename)));
//            imageView1.setImageBitmap(bitmap);
//            imgPath = filename;
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.d("ERROR","Error "+e.getMessage());
//        }
//        return filename;

//    }
    }

}