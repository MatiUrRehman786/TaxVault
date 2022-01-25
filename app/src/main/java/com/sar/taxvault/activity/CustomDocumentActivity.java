package com.sar.taxvault.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.pdftron.pdf.config.ViewerBuilder;
import com.pdftron.pdf.config.ViewerBuilder2;
import com.pdftron.pdf.config.ViewerConfig;
import com.pdftron.pdf.controls.PdfViewCtrlTabFragment2;
import com.pdftron.pdf.controls.PdfViewCtrlTabHostFragment;
import com.pdftron.pdf.controls.PdfViewCtrlTabHostFragment.TabHostListener;
import com.pdftron.pdf.controls.PdfViewCtrlTabHostFragment2;
import com.pdftron.pdf.dialog.SoundDialogFragment;
import com.pdftron.pdf.model.FileInfo;
import com.pdftron.pdf.tools.R.drawable;
import com.pdftron.pdf.tools.R.id;
import com.pdftron.pdf.tools.R.layout;
import com.pdftron.pdf.tools.R.menu;
import com.pdftron.pdf.tools.R.style;
import com.pdftron.pdf.utils.AppUtils;
import com.pdftron.pdf.utils.PdfViewCtrlTabsManager;
import com.pdftron.pdf.utils.ShortcutHelper;
import com.pdftron.pdf.utils.Utils;
import com.sar.taxvault.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CustomDocumentActivity extends AppCompatActivity implements TabHostListener, com.pdftron.pdf.controls.PdfViewCtrlTabHostFragment2.TabHostListener {

    public static final String EXTRA_FILE_URI = "extra_file_uri";
    public static final String EXTRA_FILE_RES_ID = "extra_file_res_id";
    public static final String EXTRA_FILE_PASSWORD = "extra_file_password";
    public static final String EXTRA_CONFIG = "extra_config";
    public static final String EXTRA_CUSTOM_HEADERS = "extra_custom_headers";
    public static final String EXTRA_FILE_EXTENSION = "extra_file_extension";
    public static final String EXTRA_FILE_URI_LIST = "extra_file_uri_list";
    public static final String EXTRA_NAV_ICON = "extra_nav_icon";
    public static final String EXTRA_NEW_UI = "extra_new_ui";
    public static final String EXTRA_UI_THEME = "extra_ui_theme";

    @DrawableRes
    public static final int DEFAULT_NAV_ICON_ID;

    private static final String SAVE_INSTANCE_TABBED_HOST_FRAGMENT_TAG = "tabbed_host_fragment";

    protected PdfViewCtrlTabHostFragment mPdfViewCtrlTabHostFragment;

    protected PdfViewCtrlTabHostFragment2 mPdfViewCtrlTabHostFragment2;

    protected ViewerConfig mViewerConfig;

    protected boolean mUseNewUi = true;

    @StyleRes
    protected int mTheme;

    @DrawableRes
    protected int mNavigationIconId;

    protected JSONObject mCustomHeaders;

    protected ArrayList<Uri> mFileUris;

    private boolean mShouldOpenDocuments;

    protected int mSampleRes;

    protected int[] mToolbarMenuResArray;

    public CustomDocumentActivity() {

        this.mNavigationIconId = DEFAULT_NAV_ICON_ID;

        this.mSampleRes = 0;

        this.mToolbarMenuResArray = new int[]{menu.fragment_viewer_new};

    }

    public static void openDocument(Context packageContext, Uri fileUri) {

        openDocument(packageContext, fileUri, "");

    }

    public static void openDocument(Context packageContext, Uri fileUri, @Nullable ViewerConfig config) {

        openDocument(packageContext, fileUri, "", config);

    }

    public static void openDocument(Context packageContext, int resId) {

        openDocument(packageContext, resId, "");

    }

    public static void openDocument(Context packageContext, int resId, @Nullable ViewerConfig config) {

        openDocument(packageContext, resId, "", config);

    }

    public static void openDocument(Context packageContext, Uri fileUri, String password) {

        openDocument(packageContext, fileUri, password, (ViewerConfig)null);

    }

    public static void openDocument(Context packageContext, int resId, String password) {

        openDocument(packageContext, resId, password, (ViewerConfig)null);

    }

    public static void openDocument(Context packageContext, Uri fileUri, String password, @Nullable ViewerConfig config) {

        openDocument(packageContext, fileUri, password, (JSONObject)null, config);

    }

    public static void openDocument(Context packageContext, Uri fileUri, String password, @Nullable JSONObject customHeaders, @Nullable ViewerConfig config) {

        openDocument(packageContext, fileUri, password, customHeaders, config, DEFAULT_NAV_ICON_ID);

    }

    public static void openDocument(Context packageContext, Uri fileUri, String password, @Nullable JSONObject customHeaders, @Nullable ViewerConfig config, @DrawableRes int navIconId) {

        openDocument(packageContext, fileUri, password, customHeaders, config, navIconId, true);

    }

    public static void openDocument(Context packageContext, Uri fileUri, String password, @Nullable JSONObject customHeaders, @Nullable ViewerConfig config, @DrawableRes int navIconId, boolean newUi) {

        IntentBuilder builder = IntentBuilder.fromActivityClass(packageContext, CustomDocumentActivity.class).withUri(fileUri).usingPassword(password).usingConfig(config).usingCustomHeaders(customHeaders).usingNavIcon(navIconId).usingNewUi(newUi);

        packageContext.startActivity(builder.build());

    }

    public static void openDocuments(Context packageContext, @NonNull ArrayList<Uri> fileUris, @Nullable ViewerConfig config) {

        IntentBuilder builder = IntentBuilder.fromActivityClass(packageContext, CustomDocumentActivity.class).withUris(fileUris).usingConfig(config);

        packageContext.startActivity(builder.build());

    }

    public static void openDocument(Context packageContext, @RawRes int resId, String password, @Nullable ViewerConfig config) {

        IntentBuilder builder = IntentBuilder.fromActivityClass(packageContext, CustomDocumentActivity.class).withFileRes(resId).usingPassword(password).usingConfig(config);

        packageContext.startActivity(builder.build());

    }



    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {

            AppUtils.initializePDFNetApplication(this.getApplicationContext());

        } catch (Exception var8) {

            var8.printStackTrace();

            return;
        }

        if (!Utils.applyDayNight(this)) {

            if (this.getIntent() != null && this.getIntent().getExtras() != null) {

                this.mViewerConfig = (ViewerConfig)this.getIntent().getExtras().getParcelable("extra_config");

                this.mNavigationIconId = this.getIntent().getExtras().getInt("extra_nav_icon", DEFAULT_NAV_ICON_ID);

                try {

                    String headers = this.getIntent().getExtras().getString("extra_custom_headers");

                    if (headers != null) {

                        this.mCustomHeaders = new JSONObject(headers);

                    }

                } catch (JSONException var7) {

                }

                this.mUseNewUi = this.getIntent().getExtras().getBoolean("extra_new_ui", true);

                this.mToolbarMenuResArray = this.getToolbarMenuResArray();

                this.mTheme = this.getIntent().getExtras().getInt("extra_ui_theme", this.mUseNewUi ? style.PDFTronAppTheme : style.CustomAppTheme);

            }

            if (savedInstanceState != null) {

                Fragment savedFragment = this.getSupportFragmentManager().getFragment(savedInstanceState, "tabbed_host_fragment");

                if (savedFragment instanceof PdfViewCtrlTabHostFragment2) {

                    this.mPdfViewCtrlTabHostFragment2 = (PdfViewCtrlTabHostFragment2)savedFragment;

                } else if (savedFragment instanceof PdfViewCtrlTabHostFragment) {

                    this.mPdfViewCtrlTabHostFragment = (PdfViewCtrlTabHostFragment)savedFragment;

                }

                if (this.mPdfViewCtrlTabHostFragment2 != null) {

                    this.mPdfViewCtrlTabHostFragment2.addHostListener(this);

                } else if (this.mPdfViewCtrlTabHostFragment != null) {

                    this.mPdfViewCtrlTabHostFragment.addHostListener(this);

                }

                FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();

                List<Fragment> fragments = this.getSupportFragmentManager().getFragments();

                Iterator var5 = fragments.iterator();

                label72:

                while(true) {

                    Fragment fragment;

                    do {

                        if (!var5.hasNext()) {

                            ft.commitNow();

                            break label72;

                        }

                        fragment = (Fragment)var5.next();

                    } while(!(fragment instanceof PdfViewCtrlTabFragment2) && !(fragment instanceof DialogFragment));

                    ft.remove(fragment);

                }

            }

            this.setContentView(layout.activity_document);

            ShortcutHelper.enable(true);

            if (null == this.mPdfViewCtrlTabHostFragment2 && null == this.mPdfViewCtrlTabHostFragment) {

                this.onDocumentSelected();

            }

        }

    }

    protected void onResume() {
        super.onResume();
    }

    protected void onDestroy() {

        super.onDestroy();

        if (null != this.mPdfViewCtrlTabHostFragment2) {

            this.mPdfViewCtrlTabHostFragment2.removeHostListener(this);

        }

        if (null != this.mPdfViewCtrlTabHostFragment) {

            this.mPdfViewCtrlTabHostFragment.removeHostListener(this);

        }

    }

    protected void onSaveInstanceState(Bundle outState) {

        Log.v("LifeCycle", "Main.onSaveInstanceState");

        super.onSaveInstanceState(outState);

        FragmentManager fm = this.getSupportFragmentManager();

        List<Fragment> fragments = fm.getFragments();

        if (this.mPdfViewCtrlTabHostFragment2 != null && fragments.contains(this.mPdfViewCtrlTabHostFragment2)) {

            fm.putFragment(outState, "tabbed_host_fragment", this.mPdfViewCtrlTabHostFragment2);

        } else if (this.mPdfViewCtrlTabHostFragment != null && fragments.contains(this.mPdfViewCtrlTabHostFragment)) {

            fm.putFragment(outState, "tabbed_host_fragment", this.mPdfViewCtrlTabHostFragment);

        }

    }

    public void onBackPressed() {

        boolean handled = false;

        if (this.mPdfViewCtrlTabHostFragment2 != null) {

            handled = this.mPdfViewCtrlTabHostFragment2.handleBackPressed();

        } else if (this.mPdfViewCtrlTabHostFragment != null) {

            handled = this.mPdfViewCtrlTabHostFragment.handleBackPressed();

        }

        if (!handled) {

            super.onBackPressed();

        }

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 10015) {

            Fragment fragment = this.getSupportFragmentManager().findFragmentByTag(SoundDialogFragment.TAG);

            if (fragment != null && fragment instanceof SoundDialogFragment) {

                fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);

            }

        } else {

            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }

    }

    protected int[] getToolbarMenuResArray() {

        return this.mUseNewUi ? new int[]{R.menu.fragment_viewer_custom} : new int[]{R.menu.fragment_viewer_custom};

    }

    protected void onDocumentSelected(Uri fileUri) {

        this.onDocumentSelected(fileUri, "");

    }

    protected void onDocumentSelected(Uri fileUri, String password) {

        int theme = this.mUseNewUi ? style.PDFTronAppTheme : style.CustomAppTheme;

        if (this.mTheme != 0) {

            theme = this.mTheme;

        }

        String fileExtension = null;

        if (this.getIntent() != null && this.getIntent().getExtras() != null) {

            fileExtension = this.getIntent().getExtras().getString("extra_file_extension");

        }

        if (this.mUseNewUi) {

            ViewerBuilder2 builder = ViewerBuilder2.withUri(fileUri, password).usingTheme(theme);

            if (fileExtension != null) {

                builder.usingFileExtension(fileExtension);

            }

            this.startTabHostFragment2(builder);

        } else {

            ViewerBuilder builder = ViewerBuilder.withUri(fileUri, password).usingTheme(theme);

            this.startTabHostFragment(builder);

        }

    }

    protected void onDocumentsSelected(ArrayList<Uri> fileUris) {

        this.mFileUris = fileUris;

        if (this.mFileUris != null && !this.mFileUris.isEmpty()) {

            this.mShouldOpenDocuments = true;

            this.onDocumentSelected((Uri)this.mFileUris.get(0));

        }

    }

    protected void onDocumentSelected() {

        if (!this.isFinishing()) {

            Uri fileUri = null;

            ArrayList<Uri> fileUris = null;

            String password = "";

            try {

                int fileResId;

                File file;

                if (this.getIntent() != null && this.getIntent().getExtras() != null) {

                    fileUri = (Uri)this.getIntent().getExtras().getParcelable("extra_file_uri");

                    fileUris = this.getIntent().getExtras().getParcelableArrayList("extra_file_uri_list");

                    if (fileUris != null && fileUris.size() > 0) {

                        this.onDocumentsSelected(fileUris);

                        return;

                    }

                    fileResId = this.getIntent().getExtras().getInt("extra_file_res_id", 0);

                    password = this.getIntent().getExtras().getString("extra_file_password");

                    if (null == fileUri && fileResId != 0) {

                        file = Utils.copyResourceToLocal(this, fileResId, "untitled", ".pdf");

                        if (null != file && file.exists()) {

                            fileUri = Uri.fromFile(file);

                        }

                    }

                }

                fileResId = PdfViewCtrlTabsManager.getInstance().getDocuments(this).size();

                if (null == fileUri && fileResId == 0 && this.mSampleRes != 0) {

                    file = Utils.copyResourceToLocal(this, this.mSampleRes, "getting_started", ".pdf");

                    if (null != file && file.exists()) {

                        fileUri = Uri.fromFile(file);

                        password = "";

                    }

                }

            } catch (Exception var6) {

                var6.printStackTrace();

            }

            this.onDocumentSelected(fileUri, password);
        }

    }

    @SuppressLint("RestrictedApi")
    protected void startTabHostFragment(@NonNull ViewerBuilder builder) {

        if (!this.isFinishing()) {

            builder.usingQuitAppMode(true).usingNavIcon(this.mNavigationIconId).usingCustomToolbar(this.mToolbarMenuResArray).usingConfig(this.mViewerConfig).usingCustomHeaders(this.mCustomHeaders);

            if (this.mPdfViewCtrlTabHostFragment != null) {

                this.mPdfViewCtrlTabHostFragment.onOpenAddNewTab(builder.createBundle(this));

            } else {

                FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();

                this.mPdfViewCtrlTabHostFragment = builder.build(this);

                this.mPdfViewCtrlTabHostFragment.addHostListener(this);

                ft.replace(id.container, this.mPdfViewCtrlTabHostFragment, (String)null);

                ft.commitAllowingStateLoss();

            }

        }

    }

    protected void startTabHostFragment2(@NonNull ViewerBuilder2 builder) {

        if (!this.isFinishing()) {

            builder.usingNavIcon(this.mNavigationIconId).usingCustomToolbar(this.mToolbarMenuResArray).usingConfig(this.mViewerConfig).usingCustomHeaders(this.mCustomHeaders);

            if (this.mPdfViewCtrlTabHostFragment2 != null) {

                this.mPdfViewCtrlTabHostFragment2.onOpenAddNewTab(builder.createBundle(this));

            } else {

                FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();

                this.mPdfViewCtrlTabHostFragment2 = builder.build(this);

                this.mPdfViewCtrlTabHostFragment2.addHostListener(this);

                ft.replace(id.container, this.mPdfViewCtrlTabHostFragment2, (String)null);

                ft.commitAllowingStateLoss();

            }

        }

    }

    public void onTabHostShown() {

        if (this.mShouldOpenDocuments) {

            this.mShouldOpenDocuments = false;

            if (this.mFileUris != null) {

                for(int i = 0; i < this.mFileUris.size(); ++i) {

                    if (i != 0) {

                        Uri fileUri = (Uri)this.mFileUris.get(i);

                        this.onDocumentSelected(fileUri);

                    }

                }

            }

        }

    }

    public void onTabHostHidden() {
    }

    public void onLastTabClosed() {
        this.finish();
    }

    public void onTabChanged(String tag) {
    }

    public boolean onOpenDocError() {
        return false;
    }

    public void onNavButtonPressed() {
        this.finish();
    }

    public void onShowFileInFolder(String fileName, String filepath, int itemSource) {
    }

    public boolean canShowFileInFolder() {
        return false;
    }

    public boolean canShowFileCloseSnackbar() {
        return true;
    }

    public boolean onToolbarCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        return false;
    }

    public boolean onToolbarPrepareOptionsMenu(Menu menu) {
        return false;
    }

    public boolean onToolbarOptionsItemSelected(MenuItem item) {
        return false;
    }

    public void onStartSearchMode() {
    }

    public void onExitSearchMode() {
    }

    public boolean canRecreateActivity() {
        return true;
    }

    public void onTabPaused(FileInfo fileInfo, boolean isDocModifiedAfterOpening) {
    }

    public void onJumpToSdCardFolder() {
    }

    public void onTabDocumentLoaded(String tag) {
    }

    static {
        DEFAULT_NAV_ICON_ID = drawable.ic_arrow_back_white_24dp;
    }

    public static class IntentBuilder {

        @NonNull
        private Intent mIntent;

        private IntentBuilder(@NonNull Intent intent) {
            this.mIntent = intent;
        }

        public static IntentBuilder fromActivityClass(@NonNull Context context, @NonNull Class<CustomDocumentActivity> activityClass) {

            return new IntentBuilder(new Intent(context, activityClass));

        }

        public IntentBuilder withUri(@NonNull Uri fileUri) {

            this.mIntent.putExtra("extra_file_uri", fileUri);

            return this;

        }

        public IntentBuilder withUris(@NonNull ArrayList<Uri> fileUris) {

            this.mIntent.putParcelableArrayListExtra("extra_file_uri_list", fileUris);

            return this;

        }

        public IntentBuilder withFileRes(@RawRes int resId) {

            this.mIntent.putExtra("extra_file_res_id", resId);

            return this;

        }

        public IntentBuilder usingPassword(@Nullable String password) {

            this.mIntent.putExtra("extra_file_password", password != null ? password : "");

            return this;

        }

        public IntentBuilder usingCustomHeaders(@Nullable JSONObject customHeaders) {

            if (null != customHeaders) {

                this.mIntent.putExtra("extra_custom_headers", customHeaders.toString());

            }

            return this;

        }

        public IntentBuilder usingNewUi(boolean newUi) {

            this.mIntent.putExtra("extra_new_ui", newUi);

            return this;

        }

        public IntentBuilder usingNavIcon(@DrawableRes int navIconRes) {

            this.mIntent.putExtra("extra_nav_icon", navIconRes);

            return this;

        }

        public IntentBuilder usingConfig(@Nullable ViewerConfig config) {

            this.mIntent.putExtra("extra_config", config);

            return this;

        }

        public IntentBuilder usingTheme(@StyleRes int theme) {

            this.mIntent.putExtra("extra_ui_theme", theme);

            return this;

        }

        public IntentBuilder usingFileExtension(@NonNull String extension) {

            this.mIntent.putExtra("extra_file_extension", extension);

            return this;

        }

        public Intent build() {
            return this.mIntent;
        }

    }

}
