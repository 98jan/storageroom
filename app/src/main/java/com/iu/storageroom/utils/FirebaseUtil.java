package com.iu.storageroom.utils;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.iu.storageroom.R;

import java.util.ArrayList;
import java.util.List;

public class FirebaseUtil {

    public static FirebaseDatabase mFireDatabase;
    public static DatabaseReference mDatabaseReference;
    public static FirebaseAuth mAuth;
    public static FirebaseAuth.AuthStateListener mAuthListener;
    private static FirebaseStorage mFirebaseStorage;
    private static StorageReference mStorageReference;

    // initialize Firebase authentication and database instances
    public static void initializeFirebase() {
        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }
        if (mFireDatabase == null) {
            mFireDatabase = FirebaseDatabase.getInstance();
        }

        if (mStorageReference == null) {
            mStorageReference = mFirebaseStorage.getInstance().getReference();
        }
    }

    public interface FirebaseCallback {
        void onCallback(boolean isSuccess);

        void onCallback(List<Object> list);
    }

    // Sign up a new user with an email and password
    public static void signUp(String email, String password, Activity activity, FirebaseCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(activity, task -> {
            callback.onCallback(task.isSuccessful());
        });
    }

    // Send a verification email to the user
    public static void sendVerificationEmail(FirebaseUser user, FirebaseCallback callback) {
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    callback.onCallback(true);
                } else {
                    callback.onCallback(false);
                }
            });
        } else {
            callback.onCallback(false);
        }
    }

    // Check if the user's email is verified
    public static void isEmailVerified(FirebaseCallback firebaseCallback) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.reload().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    firebaseCallback.onCallback(user.isEmailVerified());
                } else {
                    firebaseCallback.onCallback(false);
                }
            });
        } else {
            firebaseCallback.onCallback(false);
        }
    }

    // Check email verification status and handle the result
    public static void checkEmailVerification(Activity activity, Class<?> targetActivity) {
        isEmailVerified(new FirebaseCallback() {
            @Override
            public void onCallback(boolean isVerified) {
                if (isVerified) {
                    Intent intent = new Intent(activity, targetActivity);
                    activity.startActivity(intent);
                    activity.finish();
                } else {
                    Toast.makeText(activity, activity.getString(R.string.verify_email_prompt), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCallback(List<Object> list) {
                // Not used in this context
            }
        });
    }

    // signs in a user with an email and password
    public static void signIn(String email, String password, Activity activity, OnCompleteListener<AuthResult> listener) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(activity, listener);

    }

    // sets a specific path for Firebase database
    public static void openFbReference(String ref) {
        if (mFireDatabase == null) {
            mFireDatabase = FirebaseDatabase.getInstance();
        }
        mDatabaseReference = mFireDatabase.getReference().child(ref);
    }

    // returns the current authenticated Firebase user
    public static FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    // signs out the current authenticated user
    public static void signOut() {
        mAuth.signOut();
    }

    // initialize authentication state listener
    public static void initializeAuthListener(Activity activity, Class<?> targetActivity) {
        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                Intent intent = new Intent(activity, targetActivity);
                intent.putExtra("userId", user.getUid());
                activity.startActivity(intent);
                activity.finish();
            }
        };
    }

    // adds authentication state listener to FirebaseAuth instance
    public static void addAuthStateListener(FirebaseAuth.AuthStateListener listener) {
        mAuth.addAuthStateListener(listener);
    }

    // removes authentication state listener from FirebaseAuth instance
    public static void removeAuthStateListener(FirebaseAuth.AuthStateListener listener) {
        mAuth.removeAuthStateListener(listener);
    }

    // generic method for saving data in Firebase database
    public static <T> void saveData(String path, T data, FirebaseCallback firebaseCallback) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(path);
        databaseReference.push().setValue(data).addOnCompleteListener(task -> {
            firebaseCallback.onCallback(task.isSuccessful());
        });
    }

    // generic method for reading data from Firebase database
    public static <T> void readData(String path, Class<T> clazz, FirebaseCallback firebaseCallback) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(path);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Object> dataList = new ArrayList<>();
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        T data = dataSnapshot.getValue(clazz);
                        if (data != null) {
                            dataList.add(data);
                        }
                    }
                    firebaseCallback.onCallback(dataList);
                } else {
                    firebaseCallback.onCallback(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                firebaseCallback.onCallback(null);
            }
        });
    }

    // generic method for updating data from Firebase database
    public static <T> void updateData(String path, String key, T data, FirebaseCallback firebaseCallback) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(path).child(key);
        databaseReference.setValue(data).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                firebaseCallback.onCallback(null);
            } else {
                firebaseCallback.onCallback(false);
            }
        });
    }

    // generic method for deleting data from Firebase database
    public static void deleteData(String path, String key, FirebaseCallback firebaseCallback) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(path).child(key);
        databaseReference.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                firebaseCallback.onCallback(null);
            } else {
                firebaseCallback.onCallback(false);
            }
        });
    }
}
