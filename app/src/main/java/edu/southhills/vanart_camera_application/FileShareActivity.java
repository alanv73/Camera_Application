package edu.southhills.vanart_camera_application;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileShareActivity extends AppCompatActivity {
    // The path to the "images" subdirectory
    private File mImagesDir;
    // Array of files in the images subdirectory
    File[] mImageFiles;
    // Array of filenames corresponding to imageFiles
    String[] mImageFilenames;
    Intent mResultIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_share);

        // Set up an Intent to send back to apps that request a file
        mResultIntent =
                new Intent("edu.southhills.vanart_camera_application.ACTION_RETURN_FILE");

        // Get the files/images subdirectory;
        mImagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        // Get the files in the images subdirectory
        mImageFiles = mImagesDir.listFiles();
        // Set the Activity's result to null to begin with
        setResult(Activity.RESULT_CANCELED, null);
        /*
         * Display the file names in the ListView fileListView.
         * Back the ListView with the array imageFilenames, which
         * you can create by iterating through imageFiles and
         * calling File.getAbsolutePath() for each File
         */
        int i = 0;
        mImageFilenames = new String[mImageFiles.length];
        try{
            for (File f : mImageFiles) {
                mImageFilenames[i] = f.getAbsolutePath();
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ListView mFileListView = findViewById(R.id.mFileListView);

        // Create a List from String Array elements
        final List<String> fileList = new ArrayList<>(Arrays.asList(mImageFilenames));

        // Create an ArrayAdapter from List
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>
                (this, android.R.layout.simple_list_item_1, fileList);

        // DataBind ListView with items from ArrayAdapter
        mFileListView.setAdapter(arrayAdapter);

        mFileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView,
                                    View view,
                                    int position,
                                    long rowId) {
                /*
                 * Get a File for the selected file name.
                 * Assume that the file names are in the
                 * imageFilename array.
                 */
                File requestFile = new File(mImageFilenames[position]);
                /*
                 * Most file-related method calls need to be in
                 * try-catch blocks.
                 */
                // Use the FileProvider to get a content URI
                Uri fileUri = null;
                try {
                    fileUri = FileProvider.getUriForFile(
                            FileShareActivity.this,
                            "edu.southhills.vanart_camera_application",
                            requestFile);
                } catch (IllegalArgumentException e) {
                    Log.e("File Selector",
                            "The selected file can't be shared: " + requestFile.toString());
                }

                if(fileUri != null){
                    // Grant temporary read permission to the content URI
                    mResultIntent.addFlags(
                            Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    // Put the Uri and MIME type in the result Intent
                    mResultIntent.setDataAndType(
                            fileUri,
                            getContentResolver().getType(fileUri));
                    // Set the result
                    FileShareActivity.this.setResult(Activity.RESULT_OK,
                            mResultIntent);
                } else {
                    mResultIntent.setDataAndType(null, "");
                    FileShareActivity.this.setResult(RESULT_CANCELED,
                            mResultIntent);
                }
            }
        });

        mFileListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView,
                                            View view,
                                            int position,
                                            long rowId) {
                /*
                 * Get a File for the selected file name.
                 * Assume that the file names are in the
                 * imageFilename array.
                 */
                File requestFile = new File(mImageFilenames[position]);
                /*
                 * Most file-related method calls need to be in
                 * try-catch blocks.
                 */
                // Use the FileProvider to get a content URI
                Uri fileUri = null;
                try {
                    fileUri = FileProvider.getUriForFile(
                            FileShareActivity.this,
                            "edu.southhills.vanart_camera_application",
                            requestFile);
                } catch (IllegalArgumentException e) {
                    Log.e("File Selector",
                            "The selected file can't be shared: " + requestFile.toString());
                }

                if(fileUri != null){
                    String[] toField = new String[] {"alanv73@gmail.com"};
                    String subjectField = "file attached";
                    String messageText = "look for the file";
                    sendEmail(toField, subjectField, messageText, fileUri);
                } else {
                    mResultIntent.setDataAndType(null, "");
                    FileShareActivity.this.setResult(RESULT_CANCELED,
                            mResultIntent);
                }

                return false;
            }
        });
    }

    public void onDoneClick(View v){
        // Associate a method with the Done button
        finish();
    }

    public void sendEmail(String[] toField, String subject, String message, Uri fileAttachment){
        Intent mailIntent = new Intent(Intent.ACTION_SEND);

        mailIntent.setType("plain/text");
        mailIntent.putExtra(Intent.EXTRA_EMAIL, toField);
        mailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        mailIntent.putExtra(Intent.EXTRA_TEXT, message);
        mailIntent.putExtra(Intent.EXTRA_STREAM, fileAttachment);

        Intent chooser = Intent.createChooser(mailIntent, "Send Email");

        try{
            startActivity(chooser);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(FileShareActivity.this, "eMail Client not Found", Toast.LENGTH_SHORT).show();
        }

    }
}
