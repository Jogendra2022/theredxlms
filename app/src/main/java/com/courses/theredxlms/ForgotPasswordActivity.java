package com.courses.theredxlms;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.courses.theredxlms.databinding.ActivityForgotPasswordBinding;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ActivityForgotPasswordBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        // Set listener for the 'Send Reset Link' button
        binding.btnSendResetLink.setOnClickListener(v -> sendResetLink());

        // Set listener for the 'Back to Login' text
        binding.tvBackToLogin.setOnClickListener(v -> {
            // Simply finish this activity to go back to the previous one (LoginActivity)
            finish();
        });
    }

    /**
     * Validates the email and sends a password reset link using Firebase.
     */
    private void sendResetLink() {
        String email = binding.etForgotPasswordEmail.getText().toString().trim();

        // Validate the email input
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etForgotPasswordEmail.setError("Please enter a valid email address.");
            binding.etForgotPasswordEmail.requestFocus();
            return;
        }

        // Use Firebase Auth to send the password reset email
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ForgotPasswordActivity.this, "Password reset link sent to your email.", Toast.LENGTH_LONG).show();
                        // Go back to the login screen after successfully sending the link
                        finish();
                    } else {
                        // Show an error message if it fails
                        Toast.makeText(ForgotPasswordActivity.this, "Failed to send reset link: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
