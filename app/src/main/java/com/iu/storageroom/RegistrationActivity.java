package com.iu.storageroom;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.iu.storageroom.utils.FirebaseUtil;

import java.util.List;

public class RegistrationActivity extends AppCompatActivity {

    private TextInputEditText editTextEmail, editTextPassword, editTextReplyPassword;
    private Button buttonReg;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private TextView textView;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is already logged in.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            FirebaseUtil.checkEmailVerification(this, StorageroomActivity.class);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextReplyPassword = findViewById(R.id.reply_password);
        buttonReg = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.loginNow);

        // Set click listener for "Login Now" text view
        textView.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Set click listener for register button
        buttonReg.setOnClickListener(v -> registerUser());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void registerUser() {
        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Get user input
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String repeat_password = editTextReplyPassword.getText().toString().trim();

        // Perform input validation
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, getString(R.string.enter_email), Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, getString(R.string.password), Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        if (TextUtils.isEmpty(repeat_password)) {
            Toast.makeText(this, getString(R.string.repeat_password), Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        if (!password.equals(repeat_password)) {
            Toast.makeText(this, getString(R.string.password_mismatch), Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        // Use the FirebaseUtil to sign up the user
        FirebaseUtil.signUp(email, password, this, new FirebaseUtil.FirebaseCallback() {
            @Override
            public void onCallback(boolean isSuccess) {
                progressBar.setVisibility(View.GONE);
                if (isSuccess) {
                    Toast.makeText(RegistrationActivity.this, getString(R.string.reg_successful), Toast.LENGTH_SHORT).show();
                    sendVerificationEmail();
                } else {
                    Toast.makeText(RegistrationActivity.this, getString(R.string.auth_failed), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCallback(List<Object> list) {
                // Not used in this context
            }
        });
    }

    private void sendVerificationEmail() {
        FirebaseUser user = FirebaseUtil.getCurrentUser();
        if (user != null) {
            FirebaseUtil.sendVerificationEmail(user, new FirebaseUtil.FirebaseCallback() {
                @Override
                public void onCallback(boolean isSuccess) {
                    if (isSuccess) {
                        Toast.makeText(RegistrationActivity.this, getString(R.string.verification_email_sent), Toast.LENGTH_SHORT).show();
                        FirebaseUtil.checkEmailVerification(RegistrationActivity.this, StorageroomOverviewActivity.class);
                    } else {
                        Toast.makeText(RegistrationActivity.this, getString(R.string.verification_email_failed), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCallback(List<Object> list) {
                    // Not used in this context
                }
            });
        } else {
            Toast.makeText(this, getString(R.string.user_not_found), Toast.LENGTH_SHORT).show();
        }
    }
}
