package com.cse110easyeat.accountservices.login;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseLoginService {
    private FirebaseAuth authenticator;
    private final String TAG = "FirebaseLoginService";
    private Activity appActivity;

    public FirebaseLoginService(Activity authenticationActivity) {
        authenticator = FirebaseAuth.getInstance();
        appActivity = authenticationActivity;
    }

    public boolean checkIfUserSignedIn(FirebaseUser userToCheck) {
        if (userToCheck != null) {
            // TODO: Perform if user is verified actions on the UI
            Log.d(TAG, "User is currently logged in: " + userToCheck.toString());
            return true;
        }

        return false;
    }

    // NOTE VERIFICATION MUST BE BEFORE CALLING THIS FUNCTION
    // NOTE throws a FirebaseAuthUserCollisionException when there is another email in use
    public void createNewAccount(String email, String password) {
        authenticator.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(appActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Successful creation of account");
                            // TODO: PASS IT TO UI OR SOMETHING

                        } else {
                            Log.d(TAG, "Something went wrong when creating the account" +
                                                            task.getException());
                            // TODO: PASS IT TO UI
                        }
                    }
                });
    }


    public void validateAccount(String email, String password) {
        authenticator.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(appActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "Finished validation");
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Verification is ok");
                            FirebaseUser user = authenticator.getCurrentUser();
                            if (!user.isEmailVerified()) {
                                Log.d(TAG, "YO USER HASN'T VERIFIED THEIR EMAIL");
                            }
                            // TODO: SHOW IT ON THE UI OR SOMETHING
                        } else {
                            Log.d(TAG, "Signup failure" + task.getException());
                            // TODO: SHOW THE USER FAILURE
                        }
                    }
                });
    }

    public void signOut() {
        Log.d(TAG, "User signed out");
        authenticator.getInstance().signOut();
        // TODO: UPDATE UI
    }

    // TODO: NEED TO CHECK FOR EMAIL
    public void sendEmailVerification() {
        final FirebaseUser user = authenticator.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(appActivity, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent successfully");
                        } else {
                            Log.d(TAG, "Email not sent successfully");
                        }
                    }
                });
    }
}
