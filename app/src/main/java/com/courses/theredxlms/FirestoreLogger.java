package com.courses.theredxlms;

import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

/**
 * A helper class to log user activities to Firestore.
 */
public class FirestoreLogger {

    private static final String TAG = "FirestoreLogger";
    private static final String COLLECTION_NAME = "user_activity_logs";

    // An enum to define the types of actions we can log.
    public enum UserAction {
        LOGIN,
        LOGOUT,
        ADD_COURSE, // Example for future use
        APPROVE_COURSE // Example for future use
    }

    /**
     * Logs a specific action performed by the current user.
     * @param action The action to log (e.g., UserAction.LOGIN).
     */
    public static void logUserActivity(UserAction action) {
        // Get the currently signed-in user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // We can only log if a user is actually signed in.
        if (currentUser == null) {
            Log.w(TAG, "Cannot log activity: Firebase user is null.");
            return;
        }

        // Prepare the data for Firestore
        Map<String, Object> logData = new HashMap<>();
        logData.put("userId", currentUser.getUid());
        logData.put("email", currentUser.getEmail());
        logData.put("action", action.toString()); // Converts LOGIN to the string "LOGIN"
        logData.put("timestamp", FieldValue.serverTimestamp()); // Tells Firebase to use its own server time

        // Get the Firestore database instance and add the log document
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(COLLECTION_NAME)
                .add(logData) // .add() creates a new document with a unique ID
                .addOnSuccessListener(documentReference -> Log.d(TAG, "Activity '" + action + "' logged successfully."))
                .addOnFailureListener(e -> Log.e(TAG, "Error logging activity '" + action + "'.", e));
    }
}
