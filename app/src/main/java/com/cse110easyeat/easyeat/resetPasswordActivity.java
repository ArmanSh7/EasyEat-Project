package com.cse110easyeat.easyeat;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cse110easyeat.accountservices.User;
import com.cse110easyeat.database.service.FirebaseHandlerService;
import com.cse110easyeat.network.listener.NetworkListener;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class resetPasswordActivity extends AppCompatActivity {
    private static final String TAG = "resetPasswordActivity";

    private EditText emailTextField;
    private Button resetButton;

    private FirebaseAuth mAuth;
    private FirebaseHandlerService firebaseDb;

    private ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        emailTextField = (EditText) findViewById(R.id.reset_email_password);
        resetButton = (Button) findViewById(R.id.resetBtn);

        progressBar = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        firebaseDb = new FirebaseHandlerService();
        firebaseDb.connectToDatabase();

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setMessage("Verifying...");
                progressBar.show();
                final String email = emailTextField.getText().toString();
                String id = email.replaceAll("\\.", "_");
                Log.d(TAG, "email to send reset link: " + id);
                if (email.matches("")) {
                    progressBar.hide();
                    Toast.makeText(getApplicationContext(), "Please enter a valid email",
                            Toast.LENGTH_SHORT).show();
                }

                /** First check if the user is already registered */
                firebaseDb.getDataFromDatabase(id, new NetworkListener<User>() {
                    @Override
                    public void getResult(User result) {
                        Log.d(TAG, "Finish getting data from database");
                        progressBar.hide();
                        if (result != null && result.getEmail().equals(email)) {
                            Toast.makeText(getApplicationContext(), "Email reset link sent",
                                    Toast.LENGTH_SHORT).show();
                            mAuth.sendPasswordResetEmail(email);
                            progressBar.dismiss();
                            finish();
                        } else {
                            Log.d(TAG, "User not found in firebase");
                            Toast.makeText(getApplicationContext(), "You are not registered. " +
                                    "please sign up!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }


    /**
     * Override onDestroy method to remove listeners and free memory
     */
    public void onDestroy() {
        /**
         * Set the listeners to null
         * */
        resetButton.setOnClickListener(null);
        super.onDestroy();
    }
}
