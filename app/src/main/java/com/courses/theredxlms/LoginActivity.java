package com.courses.theredxlms;

import android.content.Intent;
import android.util.Log;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.courses.theredxlms.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();

        binding.btnLogin.setOnClickListener(v -> loginUser());
        binding.tvCreateAccount.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
        binding.tvForgotPassword.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class)));
    }

    private void loginUser() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter email and password.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // 1. Use the standard Android Log for debugging
                        Log.d("LoginActivity", "signInWithEmail:success");

                        // 2. Log the LOGIN event to Firestore
                        FirestoreLogger.logUserActivity(FirestoreLogger.UserAction.LOGIN);

                        // 3. Get the current user and call your role-checking method
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            checkUserRole(user.getUid());
                        }
                    } else {
                        // Handle failed login
                        Toast.makeText(LoginActivity.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void checkUserRole(String uid) {
        FirebaseFirestore.getInstance().collection("users").document(uid).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            String role = document.getString("role");
                            if ("instructor".equals(role)) {
                                // Role is 'instructor', go to Admin Dashboard
                                Toast.makeText(LoginActivity.this, "Login Successful. Welcome Instructor!", Toast.LENGTH_SHORT).show();
                                navigateTo(AdminDashboardActivity.class);
                            } else {
                                // Role is 'student' or anything else, go to Home Activity
                                Toast.makeText(LoginActivity.this, "Login Successful.", Toast.LENGTH_SHORT).show();
                                navigateTo(HomeActivity.class);
                            }
                        } else {
                            // Fallback if user document doesn't exist
                            Toast.makeText(LoginActivity.this, "Login Successful. Profile not found.", Toast.LENGTH_SHORT).show();
                            navigateTo(HomeActivity.class);
                        }
                    } else {
                        // Fallback if Firestore read fails
                        Toast.makeText(LoginActivity.this, "Login Successful. Could not verify role.", Toast.LENGTH_SHORT).show();
                        navigateTo(HomeActivity.class);
                    }
                });
    }

    private void navigateTo(Class<?> activityClass) {
        Intent intent = new Intent(LoginActivity.this, activityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
