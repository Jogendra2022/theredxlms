package com.courses.theredxlms;

import android.Manifest;
import android.graphics.pdf.PdfDocument; // <-- Make sure this import is added at the top of your file
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CertificateActivity extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_CODE = 101;
    private ImageView certificateImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate);

        certificateImageView = findViewById(R.id.certificate_image_view);
        Button downloadButton = findViewById(R.id.download_button);
        Button shareButton = findViewById(R.id.share_button);

        // Ensure you have a 'my_certificate.png' or similar in your drawable folder
        certificateImageView.setImageResource(R.drawable.my_certificate);

        downloadButton.setOnClickListener(v -> checkPermissionsAndSave());
        shareButton.setOnClickListener(v -> shareCertificate());
    }

    private Bitmap getCertificateBitmap() {
        // Get the bitmap from the ImageView
        return ((BitmapDrawable) certificateImageView.getDrawable()).getBitmap();
    }

    // --- PERMISSION HANDLING FOR DOWNLOAD ---
    private void checkPermissionsAndSave() {
        // Permission is only needed for Android 9 (SDK 28) and below
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // Permission is already granted
                saveCertificateToDownloads();
            } else {
                // Request permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
            }
        } else {
            // No runtime permission needed for Android 10+ for saving to Downloads
            saveCertificateToDownloads();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted
                saveCertificateToDownloads();
            } else {
                // Permission was denied
                Toast.makeText(this, "Storage permission is required to download the certificate", Toast.LENGTH_LONG).show();
            }
        }
    }
    // --- END OF PERMISSION HANDLING ---


    private void saveCertificateToDownloads() {
        // Get the bitmap from the ImageView that is displaying the certificate
        Bitmap bitmap = getCertificateBitmap();
        // 1. FIX: Change file extension to .pdf
        String fileName = "MyCertificate_" + System.currentTimeMillis() + ".pdf";
        // This will hold our file output stream
        OutputStream fos = null;

        try {
            // Check which Android version the device is running
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // ANDROID 10 (Q) AND HIGHER
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                // 2. FIX: Change MIME type to application/pdf
                values.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

                Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                if (uri == null) {
                    throw new IOException("Failed to create new MediaStore record.");
                }
                fos = getContentResolver().openOutputStream(uri);
            } else {
                // ANDROID 9 (P) AND LOWER
                File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File file = new File(downloadsDir, fileName);
                fos = new FileOutputStream(file);
            }

            // CRITICAL SECTION: Write the file and close the stream
            if (fos != null) {
                // Create a new PDF document
                PdfDocument pdfDocument = new PdfDocument();
                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
                PdfDocument.Page page = pdfDocument.startPage(pageInfo);
                page.getCanvas().drawBitmap(bitmap, 0, 0, null);
                pdfDocument.finishPage(page);
                pdfDocument.writeTo(fos);
                pdfDocument.close();
                fos.close(); // This is crucial

                Toast.makeText(this, "Certificate saved as PDF in Downloads", Toast.LENGTH_LONG).show();
            } else {
                throw new IOException("File output stream was null. Cannot save file.");
            }

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving certificate: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    private void shareCertificate() {
        Bitmap bitmap = getCertificateBitmap();
        try {
            // Save bitmap to a temporary file in the cache directory
            File cachePath = new File(getCacheDir(), "images");
            cachePath.mkdirs(); // Create the directory if it doesn't exist
            File file = new File(cachePath, "certificate.png");
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

            // Get a content URI for the file using FileProvider
            Uri contentUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);

            if (contentUri != null) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // Grant temporary read permission
                shareIntent.setDataAndType(contentUri, getContentResolver().getType(contentUri));
                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My Certificate");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out my new certificate from The Redx LMS!");

                startActivity(Intent.createChooser(shareIntent, "Share Certificate via"));
            }

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error sharing certificate", Toast.LENGTH_SHORT).show();
        }
    }
}
