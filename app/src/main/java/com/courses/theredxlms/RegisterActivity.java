package com.courses.theredxlms;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.courses.theredxlms.databinding.ActivityRegisterBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // We will define the roles here
    private final String[] ROLES = new String[]{"Student", "Instructor"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // --- SETUP FOR THE ROLE DROPDOWN ---
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, ROLES);
        // The AutoCompleteTextView is inside the binding object
        binding.autoCompleteRole.setAdapter(adapter);
        // ---------------------------------

        // Set up click listeners
        binding.btnSignUp.setOnClickListener(v -> registerUser());
        binding.tvLoginLink.setOnClickListener(v -> {
            // Navigate back to LoginActivity
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        // Get values from the input fields using the binding object
        String name = binding.etRegisterName.getText().toString().trim();
        String email = binding.etRegisterEmail.getText().toString().trim();
        String phone = binding.etRegisterPhone.getText().toString().trim();
        String password = binding.etRegisterPassword.getText().toString().trim();
        String role = binding.autoCompleteRole.getText().toString().trim();

        // --- Input Validation ---
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(role)) {
            Toast.makeText(this, "Please select a role.", Toast.LENGTH_SHORT).show();
            binding.autoCompleteRole.setError("Role is required");
            return;
        }
        if (!binding.cbTerms.isChecked()) {
            Toast.makeText(this, "You must agree to the Terms and Conditions.", Toast.LENGTH_SHORT).show();
            return;
        }
        // (You can add more detailed validation for email, password length etc.)

        // Create user in Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            // Auth user created, now save all info to Firestore
                            saveUserInfoToFirestore(firebaseUser.getUid(), name, email, phone, role.toLowerCase()); // Save role in lowercase
                        }
                    } else {
                        // Registration failed
                        Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveUserInfoToFirestore(String uid, String name, String email, String phone, String role) {
        // Create a map to hold the user data
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("name", name);
        userInfo.put("email", email);
        userInfo.put("phone", phone);
        userInfo.put("role", role); // role will be "student" or "instructor"

        // Save the user data to a 'users' collection in Firestore
        db.collection("users").document(uid).set(userInfo)
                .addOnSuccessListener(aVoid -> {
                    // Firestore write was successful
                    Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();

                    // Send user back to the login screen to log in with their new account
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    // Firestore write failed
                    Toast.makeText(RegisterActivity.this, "Error saving user profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
