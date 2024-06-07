package com.iu.storageroom;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.iu.storageroom.utils.FirebaseUtil;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private ProgressBar progressBar;
    private TextView registerView, passwordResetView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseUtil.initializeFirebase();
        FirebaseUtil.signOut();

        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progressBar);
        registerView = findViewById(R.id.registerNow);
        passwordResetView = findViewById(R.id.passwordReset);

        registerView.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), RegistrationActivity.class);
            startActivity(intent);
            finish();
        });

        buttonLogin.setOnClickListener(v -> loginUser());

        passwordResetView.setOnClickListener(v -> passwordReset());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize AuthStateListener
        FirebaseUtil.initializeAuthListener(this, ProductActivity.class);
    }

    private void loginUser() {

        progressBar.setVisibility(View.VISIBLE);
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, getString(R.string.enter_email), Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, getString(R.string.enter_password), Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        FirebaseUtil.signIn(email, password, this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    Log.d(TAG, "signInWithEmail:success");
                    Toast.makeText(LoginActivity.this, getString(R.string.login_successful), Toast.LENGTH_SHORT).show();
                    FirebaseUser user = FirebaseUtil.getCurrentUser();
                    if (user != null) {
                        FirebaseUtil.openFbReference("products/" + user.getUid());
                        Intent intent = new Intent(getApplicationContext(), StorageroomOverviewActivity.class);
                        intent.putExtra("userId", user.getUid());
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void passwordReset() {

        String email = editTextEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, getString(R.string.enter_email), Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        // Send password reset email
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Password reset email sent successfully
                        Toast.makeText(this,
                                "Password reset email sent to " + email,
                                Toast.LENGTH_SHORT).show();
                    } else {
                        // Failed to send password reset email
                        Toast.makeText(this,
                                "Failed to send password reset email",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onStart() {
        // workaround -> forced sign-out
        FirebaseUtil.signOut();
        super.onStart();
        FirebaseUtil.addAuthStateListener(FirebaseUtil.mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (FirebaseUtil.mAuthListener != null) {
            FirebaseUtil.removeAuthStateListener(FirebaseUtil.mAuthListener);
        }
    }
}
