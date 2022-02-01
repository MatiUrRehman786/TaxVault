package com.sar.taxvault.utils;

import android.content.Context;
import android.util.Log;

import com.pdftron.common.PDFNetException;
import com.pdftron.pdf.Convert;
import com.pdftron.pdf.DocumentConversion;
import com.pdftron.pdf.OfficeToPDFOptions;
import com.pdftron.pdf.PDFDoc;
import com.pdftron.pdf.PDFNet;
import com.pdftron.sdf.SDFDoc;
import com.sar.taxvault.MyApplication;
import com.sar.taxvault.R;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

/** * The following sample illustrates how to use the PDF.Convert utility class to convert * .docx files to PDF * <p> * This conversion is performed entirely within the PDFNet and has *no* external or * system dependencies dependencies -- Conversion results will be the sam whether * on Windows, Linux or Android. * <p> * Please contact us if you have any questions. */
public class OfficeToPDFTest extends PDFNetSample {

//    private static OutputListener mOutputListener;

//    private static ArrayList<String> mFileList = new ArrayList<>();

    private static String sLayoutSmartPluginPath;
    public static String filePath;
    public static String fileName;

    public OfficeToPDFTest(Context context) {

        try {

            String layoutPluginPath = Utils.copyResourceToTempFolder(context, R.raw.pdftron_layout_resources, false, "pdftron_layout_resources.plugin");

            PDFNet.addResourceSearchPath(layoutPluginPath);

            sLayoutSmartPluginPath = Utils.copyResourceToTempFolder(context, R.raw.pdftron_smart_substitution, false, "pdftron_smart_substitution.plugin");

            PDFNet.addResourceSearchPath(sLayoutSmartPluginPath);

        } catch (Exception e) {


            Log.d(TAG, "OfficeToPDFTest: ");
//            mOutputListener.printError(e.getStackTrace());

        }

        setTitle(R.string.sample_officetopdf_title);

        setDescription(R.string.sample_officetopdf_description);
    }

    @Override
    public void run(OutputListener outputListener) {
        super.run(outputListener);

//        mOutputListener = outputListener;

//        mFileList.clear();

        printHeader(outputListener);

        flexibleDocxConvert(UUID.randomUUID().toString()+".pdf", outputListener);

        printFooter(outputListener);
    }


//    public static void simpleDocxConvert(String inputFilename, String outputFilename) {
//        try {

//            // perform the conversion with no optional parameters
//            PDFDoc pdfdoc = new PDFDoc();
//
//            Convert.officeToPdf(pdfdoc,filePath , null);
//
//            // save the result
//            pdfdoc.save(Utils.createExternalFile(outputFilename, mFileList).getAbsolutePath(), SDFDoc.SaveMode.INCREMENTAL, null);
//
//            // And we're done!
//            mOutputListener.println("Done conversion " + Utils.createExternalFile(outputFilename, mFileList).getAbsolutePath());
//
//            File file = new File(MyApplication.getInstance().getExternalFilesDir(null), outputFilename);
//
//            mOutputListener.fileConverted(file);
//
//
//        } catch (PDFNetException e) {
//            mOutputListener.println("Unable to convert MS Office document, error:");
//            mOutputListener.printError(e.getStackTrace());
//            mOutputListener.printError(e.getStackTrace());
//        }
//    }

    public static void flexibleDocxConvert(String outputFilename, OutputListener outputListener) {
        try {
            OfficeToPDFOptions options = new OfficeToPDFOptions();
            options.setSmartSubstitutionPluginPath(sLayoutSmartPluginPath);

            // create a conversion object -- this sets things up but does not yet
            // perform any conversion logic.
            // in a multithreaded environment, this object can be used to monitor
            // the conversion progress and potentially cancel it as well
            Log.d(TAG, "flexibleDocxConvert: File Path"+filePath);

            DocumentConversion conversion = Convert.streamingPdfConversion(
                    filePath, options);

//            mOutputListener.println(fileName + ": " + Math.round(conversion.getProgress() * 100.0)
//                    + "% " + conversion.getProgressLabel());

            // actually perform the conversion
            while (conversion.getConversionStatus() == DocumentConversion.e_incomplete) {
                conversion.convertNextPage();
//                mOutputListener.println(fileName + ": " + Math.round(conversion.getProgress() * 100.0)
//                        + "% " + conversion.getProgressLabel());
            }

            if (conversion.tryConvert() == DocumentConversion.e_success) {
                int num_warnings = conversion.getNumWarnings();

                // print information about the conversion
                for (int i = 0; i < num_warnings; ++i) {

                    Log.d(TAG, "flexibleDocxConvert: Warning: " + conversion.getWarningString(i));

                }

                // save the result
                PDFDoc doc = conversion.getDoc();

                File file = new File(MyApplication.getInstance().getExternalFilesDir(null), outputFilename);

                doc.save(file.getAbsolutePath(), SDFDoc.SaveMode.INCREMENTAL, null);

                outputListener.fileConverted(file);

                if(file.exists()) {

                    Log.d(TAG, "flexibleDocxConvert: File exists");

                    Log.d(TAG, "flexibleDocxConvert: "+file.getName());

                    Log.d(TAG, "flexibleDocxConvert: "+file.getPath());

                    Log.d(TAG, "flexibleDocxConvert: "+file.getAbsolutePath());

                } else {

                    Log.d(TAG, "flexibleDocxConvert: Not File exist");

                }

//                mOutputListener.fileConverted(file);
                Log.d(TAG, "flexibleDocxConvert: Successfully saved");
                // done
            } else {
                outputListener.printError("Unable to convert the file. Please try again");
//                mOutputListener.println("Encountered an error during conversion: " + conversion.getErrorString());
                Log.d(TAG, "Encountered an error during conversion:"+ conversion.getErrorString());

            }
        } catch (PDFNetException e) {

            outputListener.printError("Unable to convert the file. "+e.getLocalizedMessage());

            Log.d(TAG, "flexibleDocxConvert: Unable to convert MS Office document, error"+e.getLocalizedMessage());
        }
    }

}