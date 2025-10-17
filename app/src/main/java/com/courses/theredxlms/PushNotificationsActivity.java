package com.courses.theredxlms;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.functions.FirebaseFunctions;
import java.util.HashMap;
import java.util.Map;

public class PushNotificationsActivity extends AppCompatActivity {

    private TextInputEditText etTitle;
    private TextInputEditText etMessage;
    private MaterialButton btnSend;
    private FirebaseFunctions mFunctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Make sure your layout file is named correctly
        setContentView(R.layout.activity_push_notifications);

        // Make sure these IDs match the ones in your XML layout
        etTitle = findViewById(R.id.et_notification_title);
        etMessage = findViewById(R.id.et_notification_message);
        btnSend = findViewById(R.id.btn_send_notification);

        // Initialize the Firebase Functions SDK
// Initialize the Firebase Functions SDK, specifying the region
        mFunctions = FirebaseFunctions.getInstance("us-central1");
        btnSend.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String body = etMessage.getText().toString().trim();

            if (title.isEmpty() || body.isEmpty()) {
                Toast.makeText(this, "Title and message cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // Call the method to send the notification
            sendNotification(title, body);
        });
    }

    /**
     * Calls the 'sendNotificationToTopic' Cloud Function.
     * @param title The title of the notification.
     * @param body The main message content of the notification.
     */
    private void sendNotification(String title, String body) {
        // Create the data payload to send to the function.
        // The keys ("title", "body") MUST match what your Cloud Function expects.
        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("body", body);

        // Disable the button to prevent multiple clicks while sending
        btnSend.setEnabled(false);
        Toast.makeText(this, "Sending...", Toast.LENGTH_SHORT).show();

        mFunctions
                .getHttpsCallable("sendNotificationToTopic") // This is the name of your function in index.js
                .call(data)
                .addOnCompleteListener(task -> {
                    // Re-enable the button once the task is complete
                    btnSend.setEnabled(true);

                    if (task.isSuccessful()) {
                        // This block runs if the function executed successfully on the server
                        Toast.makeText(PushNotificationsActivity.this, "Notification sent successfully!", Toast.LENGTH_LONG).show();
                        // Clear the input fields for the next message
                        etTitle.setText("");
                        etMessage.setText("");
                    } else {
                        // This block runs if the function failed to execute
                        // The exception will contain details from the server
                        Toast.makeText(PushNotificationsActivity.this, "Failed to send notification: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
