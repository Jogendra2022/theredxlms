// Create this file if it doesn't exist: app/src/main/java/com/courses/theredxlms/Course.java
package com.courses.theredxlms;

import com.google.firebase.firestore.Exclude; // <-- IMPORTANT: Add this import

public class Course {

    // --- NEW FIELD TO FIX THE ERROR ---
    private String id; // This will hold the unique ID from the Firestore document

    // --- Existing Fields ---
    private String title;
    private String status;
    private String description;
    private double price;
    private String instructorName;
    private String videoUrl;
    private String thumbnailUrl;
    private boolean isApproved; // Note: using "isApproved" can sometimes cause issues with Firestore. "approved" is safer.
    private double rating;
    private int ratingCount;

    // --- Constructor (optional, but good practice) ---
    public Course() {
        // Default constructor required for calls to DataSnapshot.getValue(Course.class)
    }

    // --- NEW GETTER AND SETTER TO FIX THE ERROR ---
    // The @Exclude annotation tells Firestore to ignore this field when writing data.
    // This is crucial because the document ID is metadata and not part of the document's data itself.
    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    // --- Existing Getters and Setters ---

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public String getVideoUrl() {
        return videoUrl;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }
}
