package com.sar.taxvault.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.pdftron.common.PDFNetException;
import com.pdftron.pdf.config.ViewerBuilder2;
import com.pdftron.pdf.config.ViewerConfig;
import com.pdftron.pdf.controls.PdfViewCtrlTabHostFragment;
import com.pdftron.pdf.controls.PdfViewCtrlTabHostFragment2;
import com.pdftron.pdf.model.FileInfo;
import com.pdftron.pdf.utils.Utils;
import com.pdftron.pdf.widget.toolbar.builder.AnnotationToolbarBuilder;
import com.pdftron.pdf.widget.toolbar.builder.ToolbarButtonType;
import com.pdftron.pdf.widget.toolbar.component.DefaultToolbars;
import com.sar.taxvault.R;
import com.sar.taxvault.custom.CustomAnnotationToolbar;
import com.sar.taxvault.custom.CustomLinkClick;
import com.sar.taxvault.custom.CustomQuickMenu;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.sql.Date;

public class MainActivity extends BaseActivity implements PdfViewCtrlTabHostFragment.TabHostListener {

    String TAG = "MainActivity";

    private PdfViewCtrlTabHostFragment2 mPdfViewCtrlTabHostFragment;

    public static final String NOTES_TOOLBAR_TAG = "notes_toolbar";

    public static final String SHAPES_TOOLBAR_TAG = "shapes_toolbar";

    File f = null;

    Uri path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_doc);

        getExtrasFromIntent();

        if (path != null) {

            ViewerConfig viewerConfig = new ViewerConfig.Builder()
                    .addToolbarBuilder(buildNotesToolbar())
                    .toolbarTitle("Edit Document")
                    .saveCopyExportPath(getFilesDir().getPath())
                    .build();

            try {

                mPdfViewCtrlTabHostFragment = ViewerBuilder2.withFile(toFile(path))
                        .usingCustomToolbar(new int[]{R.menu.my_custom_options_toolbar})
                        .usingNavIcon(R.drawable.ic_baseline_arrow_back_ios_24)
                        .usingConfig(viewerConfig)
                        .usingTheme(R.style.Theme_TaxVault)
                        .build(this);

            } catch (IOException e) {

                e.printStackTrace();

            }


            mPdfViewCtrlTabHostFragment.addHostListener(this);

            new CustomQuickMenu(MainActivity.this, mPdfViewCtrlTabHostFragment);
            new CustomLinkClick(MainActivity.this, mPdfViewCtrlTabHostFragment);
            new CustomAnnotationToolbar(MainActivity.this, mPdfViewCtrlTabHostFragment);

            // Add the fragment to our activity
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, mPdfViewCtrlTabHostFragment);
            ft.commit();

        }
    }

    private File toFile(Uri uri) throws IOException {

        String displayName = "";

        Cursor cursor = getContentResolver().query(uri, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {

            try {

                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));

            } finally {

                cursor.close();

            }
        }

        File file = File.createTempFile(
                FilenameUtils.getBaseName(displayName),
                "." + FilenameUtils.getExtension(displayName)
        );

        InputStream inputStream = getContentResolver().openInputStream(uri);

        FileUtils.copyInputStreamToFile(inputStream, file);

        return file;

    }

    public static Intent start(Context c, Uri path) {

        return new Intent(c, MainActivity.class)

                .putExtra("path", path);

    }

    private void getExtrasFromIntent() {

        Bundle b = getIntent().getExtras();

        if (b != null) {

            path = (Uri) this.getIntent().getExtras().getParcelable("path");

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPdfViewCtrlTabHostFragment != null) {
            mPdfViewCtrlTabHostFragment.removeHostListener(this);
//            Toast.makeText(this, f.getName(), Toast.LENGTH_SHORT).show();
        }
    }

    private AnnotationToolbarBuilder buildNotesToolbar() {
        return AnnotationToolbarBuilder.withTag(NOTES_TOOLBAR_TAG) // Identifier for toolbar
                .setToolbarName("Notes Toolbar") // Name used when displaying toolbar
                .addToolButton(ToolbarButtonType.INK, 1)
                .addToolButton(ToolbarButtonType.STICKY_NOTE, 2)
                .addToolButton(ToolbarButtonType.TEXT_HIGHLIGHT, 3)
                .addToolButton(ToolbarButtonType.TEXT_UNDERLINE, 4)
                .addToolButton(ToolbarButtonType.TEXT_STRIKEOUT, 5)
                .addToolStickyButton(ToolbarButtonType.UNDO, DefaultToolbars.ButtonId.UNDO.value())
                .addToolStickyButton(ToolbarButtonType.REDO, DefaultToolbars.ButtonId.REDO.value());
    }

    @Override
    public void onTabDocumentLoaded(String s) {


    }

    @Override
    public boolean onToolbarOptionsItemSelected(MenuItem menuItem) {

        if (menuItem.getItemId() == R.id.action_show_toast) {

            mPdfViewCtrlTabHostFragment.onSaveAsOptionSelected();
//            mPdfViewCtrlTabHostFragment.onSaveAsOptionSelected();

        }

        return false;
    }

    @Override
    public void onTabHostShown() {


        Log.d(TAG, "onTabHostShown: ");
    }

    @Override
    public void onTabHostHidden() {
        Log.d(TAG, "onTabHostHidden: ");
    }

    @Override
    public void onLastTabClosed() {
        Log.d(TAG, "onLastTabClosed: ");
    }

    @Override
    public void onTabChanged(String s) {
        Log.d(TAG, "onTabChanged: ");
    }

    @Override
    public boolean onOpenDocError() {

        Log.d(TAG, "onOpenDocError: ");
        return false;
    }

    @Override
    public void onNavButtonPressed() {

        Log.d(TAG, "onNavButtonPressed: ");

    }

    @Override
    public void onShowFileInFolder(String s, String s1, int i) {

        Log.d(TAG, "onShowFileInFolder: ");

    }

    @Override
    public boolean canShowFileInFolder() {

        Log.d(TAG, "canShowFileInFolder: ");

        return false;
    }

    @Override
    public boolean canShowFileCloseSnackbar() {

        Log.d(TAG, "canShowFileCloseSnackbar: ");

        return false;
    }

    @Override
    public boolean onToolbarCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {

        Log.d(TAG, "onToolbarCreateOptionsMenu: ");

        return false;
    }

    @Override
    public boolean onToolbarPrepareOptionsMenu(Menu menu) {

        Log.d(TAG, "onToolbarPrepareOptionsMenu: ");

        return false;
    }

    @Override
    public void onStartSearchMode() {

        Log.d(TAG, "onStartSearchMode: ");

    }

    @Override
    public void onExitSearchMode() {

        Log.d(TAG, "onExitSearchMode: ");

    }

    @Override
    public boolean canRecreateActivity() {

        Log.d(TAG, "canRecreateActivity: ");

        return true;
    }

    @Override
    public void onTabPaused(FileInfo fileInfo, boolean b) {

        Log.d(TAG, "onTabPaused: " + b);
//        if(b) {
        if (fileInfo.getFile() != null) {

            if (fileInfo.getFile().exists()) {

                Intent intent = new Intent();

                intent.setData(Uri.fromFile(fileInfo.getFile()));

                setResult(RESULT_OK, intent);

                finish();
            }
            //
        }

//        Log.d(TAG, "onTabPaused: b" + b);

    }

    @Override
    public void onJumpToSdCardFolder() {

        Log.d(TAG, "onJumpToSdCardFolder: ");

    }
}
