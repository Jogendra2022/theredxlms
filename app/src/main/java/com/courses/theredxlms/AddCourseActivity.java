package com.courses.theredxlms;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class AddCourseActivity extends AppCompatActivity {

    private static final String TAG = "AddCourseActivity";

    // --- UI COMPONENTS ---
    private TextInputEditText etCourseTitle, etCourseDescription, etCoursePrice;
    private Button btnSelectVideo, btnSelectThumbnail, btnAddCourseFinal;
    private TextView tvSelectedVideoName;
    private ImageView ivThumbnailPreview;
    private ProgressBar progressBarUpload;

    // --- URIs ---
    private Uri videoUri;
    private Uri thumbnailUri;

    // --- FIREBASE ---
    private FirebaseStorage storage;
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;

    // --- ACTIVITY LAUNCHERS ---
    private final ActivityResultLauncher<Intent> videoPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                    videoUri = result.getData().getData();
                    // Persist permission to read the file
                    getContentResolver().takePersistableUriPermission(videoUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    String fileName = getFileNameFromUri(videoUri);
                    tvSelectedVideoName.setText("Video: " + fileName);
                    tvSelectedVideoName.setVisibility(View.VISIBLE);
                }
            });

    private final ActivityResultLauncher<Intent> thumbnailPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                    thumbnailUri = result.getData().getData();
                    // Persist permission to read the file
                    getContentResolver().takePersistableUriPermission(thumbnailUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Glide.with(this).load(thumbnailUri).into(ivThumbnailPreview);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ======================= THE FIX =======================
        // The line break was missing here, causing a syntax error and making the app unresponsive.
        setContentView(R.layout.activity_add_course);
        // =====================================================

        // --- This code is now correct ---
        etCourseTitle = findViewById(R.id.et_course_title);
        etCourseDescription = findViewById(R.id.et_course_description);
        etCoursePrice = findViewById(R.id.et_course_price);
        btnSelectVideo = findViewById(R.id.btn_select_video);
        btnSelectThumbnail = findViewById(R.id.btn_select_thumbnail);
        btnAddCourseFinal = findViewById(R.id.btn_add_course_final);
        tvSelectedVideoName = findViewById(R.id.tv_selected_video_name);
        ivThumbnailPreview = findViewById(R.id.iv_thumbnail_preview);
        progressBarUpload = findViewById(R.id.progress_bar_upload);

        // --- INITIALIZE FIREBASE ---
        storage = FirebaseStorage.getInstance();
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // --- SET ONCLICK LISTENERS ---
        btnSelectVideo.setOnClickListener(v -> openFileChooser("video/*", videoPickerLauncher));
        btnSelectThumbnail.setOnClickListener(v -> openFileChooser("image/*", thumbnailPickerLauncher));
        btnAddCourseFinal.setOnClickListener(v -> validateAndUpload());
    }

    private void openFileChooser(String type, ActivityResultLauncher<Intent> launcher) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(type);
        launcher.launch(intent);
    }

    private void validateAndUpload() {
        String title = etCourseTitle.getText().toString().trim();
        String description = etCourseDescription.getText().toString().trim();
        String priceStr = etCoursePrice.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) || TextUtils.isEmpty(priceStr)) {
            Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show(); return;
        }
        if (videoUri == null) {
            Toast.makeText(this, "Please select a course video", Toast.LENGTH_SHORT).show(); return;
        }
        if (thumbnailUri == null) {
            Toast.makeText(this, "Please select a course thumbnail", Toast.LENGTH_SHORT).show(); return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Authentication error. Please log in again.", Toast.LENGTH_SHORT).show(); return;
        }

        uploadFiles(title, description, Double.parseDouble(priceStr), currentUser.getUid());
    }

    private void uploadFiles(String title, String description, double price, String instructorId) {
        progressBarUpload.setVisibility(View.VISIBLE);
        btnAddCourseFinal.setEnabled(false);

        AtomicReference<String> videoDownloadUrl = new AtomicReference<>();
        AtomicReference<String> thumbnailDownloadUrl = new AtomicReference<>();
        AtomicInteger uploadsCompleted = new AtomicInteger(2); // Countdown from 2

        // 1. Upload Thumbnail
        StorageReference thumbRef = storage.getReference().child("course_thumbnails/" + UUID.randomUUID().toString());
        uploadSingleFile(thumbRef, thumbnailUri, "Thumbnail", url -> {
            thumbnailDownloadUrl.set(url);
            if (uploadsCompleted.decrementAndGet() == 0) {
                saveCourseToFirestore(title, description, price, instructorId, videoDownloadUrl.get(), thumbnailDownloadUrl.get());
            }
        });

        // 2. Upload Video
        StorageReference videoRef = storage.getReference().child("course_videos/" + UUID.randomUUID().toString());
        uploadSingleFile(videoRef, videoUri, "Video", url -> {
            videoDownloadUrl.set(url);
            if (uploadsCompleted.decrementAndGet() == 0) {
                saveCourseToFirestore(title, description, price, instructorId, videoDownloadUrl.get(), thumbnailDownloadUrl.get());
            }
        });
    }

    // ======================= THE MAIN FIX =======================
    // This method now uses putStream for direct, reliable uploads.
    private void uploadSingleFile(StorageReference storageRef, Uri fileUri, String fileType, OnUploadSuccessListener listener) {
        try {
            // Get an InputStream directly from the content URI
            InputStream stream = getContentResolver().openInputStream(fileUri);
            if (stream == null) {
                throw new IOException("Unable to open InputStream for " + fileUri);
            }

            // Upload from the stream
            UploadTask uploadTask = storageRef.putStream(stream);

            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return storageRef.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                try {
                    stream.close(); // Important: close the stream
                } catch (IOException e) {
                    Log.e(TAG, "Failed to close stream", e);
                }

                if (task.isSuccessful()) {
                    listener.onSuccess(task.getResult().toString());
                } else {
                    Toast.makeText(AddCourseActivity.this, "Failed to upload " + fileType, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Upload failed for " + fileType, task.getException());
                    resetUI();
                }
            });

        } catch (IOException e) {
            Toast.makeText(this, "Failed to prepare " + fileType + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "File preparation failed for " + fileType, e);
            resetUI();
        }
    }
    // ==========================================================

    interface OnUploadSuccessListener {
        void onSuccess(String url);
    }

    private void saveCourseToFirestore(String title, String description, double price, String instructorId, String videoUrl, String thumbnailUrl) {
        // Using a Map is safer and ensures all fields, including 'status', are set correctly.
        Map<String, Object> course = new HashMap<>();
        course.put("title", title);
        course.put("description", description);
        course.put("price", price);
        course.put("instructorId", instructorId); // Storing the user's unique ID
        course.put("videoUrl", videoUrl);
        course.put("thumbnailUrl", thumbnailUrl);
        course.put("rating", 0.0);
        course.put("ratingCount", 0);
        course.put("status", "pending"); // This is crucial for the approval screen

        firestore.collection("courses").add(course)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AddCourseActivity.this, "Course submitted for approval!", Toast.LENGTH_SHORT).show();
                    finish(); // Go back to the previous screen
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddCourseActivity.this, "Failed to save course: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    resetUI();
                });
    }

    private String getFileNameFromUri(Uri uri) {
        String fileName = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        fileName = cursor.getString(nameIndex);
                    }
                }
            }
        }
        if (fileName == null) {
            fileName = uri.getLastPathSegment();
        }
        return fileName;
    }

    private void resetUI() {
        progressBarUpload.setVisibility(View.GONE);
        btnAddCourseFinal.setEnabled(true);
    }
}
