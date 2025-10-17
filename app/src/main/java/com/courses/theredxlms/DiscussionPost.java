package com.courses.theredxlms;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class DiscussionPost {

    // These variable names MUST EXACTLY MATCH the field names in your Firestore documents
    private String authorName;
    private String postText;
    private String authorProfileUrl;
    @ServerTimestamp
    private Date timestamp;

    // This empty constructor is REQUIRED for Firestore
    public DiscussionPost() {
    }

    // --- Getters and Setters ---
    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public String getAuthorProfileUrl() {
        return authorProfileUrl;
    }

    public void setAuthorProfileUrl(String authorProfileUrl) {
        this.authorProfileUrl = authorProfileUrl;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
    