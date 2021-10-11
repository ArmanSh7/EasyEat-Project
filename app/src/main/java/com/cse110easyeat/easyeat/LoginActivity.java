package com.cse110easyeat.easyeat;

import com.cse110easyeat.accountservices.User;
import com.cse110easyeat.controller.EasyEatController;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cse110easyeat.easyeat.R;
import com.cse110easyeat.network.listener.NetworkListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class LoginActivity extends Activity {
    private Button loginButton;
    private EditText emailField;
    private EditText passwordField;

    private EasyEatController backendController;
    private FirebaseAuth mAuth;
    private CheckBox showPassword;

    private TextView signUpLink;
    private TextView forgetPasswordLink;

    private ProgressDialog progressBar;
    private static final String TAG = "LoginActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        mAuth = FirebaseAuth.getInstance();
        backendController = new EasyEatController(getApplicationContext());
        progressBar = new ProgressDialog(this);


        loginButton = (Button) findViewById(R.id.loginBtn);
        emailField = (EditText) findViewById(R.id.login_emailid);
        passwordField = (EditText) findViewById(R.id.login_password);
        signUpLink = (TextView) findViewById(R.id.createAccount);
        showPassword = (CheckBox) findViewById(R.id.show_hide_password);
        forgetPasswordLink = (TextView) findViewById(R.id.forgot_password);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean loginSuccess = false;
                // Check for input
                Log.d(TAG, "LOGIN BUTTON CLICKED");

                if (emailField.getText().length() > 0 && passwordField.getText().length() > 0) {
                    String email = emailField.getText().toString();
                    String password = passwordField.getText().toString();
                    progressBar.setMessage("Logging in...");
                    progressBar.show();
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.hide();
                                    if (task.isSuccessful()) {
                                        //Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        if (user.isEmailVerified()) {
                                            final String userId = user.getEmail().replaceAll("\\.","_");
                                            backendController.getDatabaseService().getDataFromDatabase(userId, new NetworkListener<User>() {
                                                @Override
                                                public void getResult(User result) {
                                                    progressBar.dismiss();
                                                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                                    Intent returnIntent = new Intent();
                                                    Log.d(TAG, "LOGS OF CHECKING ARGS ON INTENT");
                                                    Log.d(TAG, "Name found after login: " + result.getEmail());
                                                    Log.d(TAG, "Email found from firebase: " + result.getFullName());
                                                    returnIntent.putExtra("email", result.getEmail());
                                                    returnIntent.putExtra("name", result.getFullName());
                                                    setResult(RESULT_OK, returnIntent);
                                                    // TODO: instead of finish add it to backstack?
                                                    finish();
                                                }
                                            });
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Please verify your email!", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Login failed!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        // TODO: NEED TO HAVE A BACK BUTTON
        signUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignupActivity.class));
            }
        });

        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "SHOW PASSWORD FIELD CHECKED");
               if (isChecked) {
                   passwordField.setTransformationMethod(null);
               } else {
                   passwordField.setTransformationMethod(new PasswordTransformationMethod());
               }
            }
        });

        // TODO: IMPLEMENT FORGET PASSWORD
        forgetPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a new forgotpassword activity
                Intent resetPassword = new Intent(getApplicationContext(),
                        resetPasswordActivity.class);
               startActivity(resetPassword);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reset the fields
        emailField.setText("");
        passwordField.setText("");
        if (showPassword.isChecked()) {
            showPassword.toggle();
        }
    }

    /**
     * Empty implementation of onBackPressed to avoid the user being able to bypass the login page
     * **/
    @Override
    public void onBackPressed() { }

    /**
     * Override onDestroy method to remove listeners and free memory
     */
    @Override
    public void onDestroy() {
        /**
         * Set the listeners to null
         * */
        signUpLink.setOnClickListener(null);
        forgetPasswordLink.setOnClickListener(null);
        showPassword.setOnCheckedChangeListener(null);
        loginButton.setOnClickListener(null);
        super.onDestroy();
    }
}
