package com.cse110easyeat.easyeat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.mindorks.placeholderview.SwipePlaceHolderView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private SwipePlaceHolderView mSwipeView;
    private Context mContext;
    private FirebaseAuth authenticator;
    private DrawerLayout dl;
    private ActionBarDrawerToggle abdt;
    private FirebaseAuth mAuth;

    private TextView userFullName;
    private TextView userEmail;

    private Button loginButton;
    private EditText emailField;
    private EditText passwordField;
    private TextView signUpLink;

    private ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final NavigationView nav_view =  (NavigationView)findViewById(R.id.nav_view);
        View header = nav_view.getHeaderView(0);
        userEmail = (TextView) header.findViewById(R.id.navHeaderEmail);
        userFullName = (TextView) header.findViewById(R.id.navHeaderName);

        dl=(DrawerLayout)findViewById(R.id.dl);
        abdt = new ActionBarDrawerToggle(this,dl,R.string.Open,R.string.Close);
        abdt.setDrawerIndicatorEnabled(true);

        dl.addDrawerListener(abdt);
        abdt.syncState();

        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem Item) {

                int id = Item.getItemId();
                if (id == R.id.setting){
                    dl.closeDrawer(Gravity.LEFT);
                    Toast.makeText(MainActivity.this, "Setting", Toast.LENGTH_SHORT);
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    // Replace the contents of the container with the new fragment
                    ft.replace(R.id.mainFragment, new inputFragment());
                    // or ft.add(R.id.your_placeholder, new FooFragment());
                    // Complete the changes added above
                    ft.commit();
                }
//                else if (id == R.id.history){
//                    Toast.makeText(MainActivity.this, "History", Toast.LENGTH_SHORT);
//                    // TODO: TEST PAGE TEST
//                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                    ft.replace(R.id.mainFragment, new swipeCardFragment());
//                    ft.commit();
//                }
                else if (id == R.id.logout){
                    Toast.makeText(MainActivity.this, "Log Out", Toast.LENGTH_SHORT);
                    authenticator = FirebaseAuth.getInstance();
                    authenticator.getInstance().signOut();

                    /** Reset the input page back to its initial state (closing the drawer) **/
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.mainFragment, new inputFragment());
                    ft.commit();
                    dl.closeDrawer(Gravity.LEFT);

                    /** Starts the LoginActivity to allow another user to login to the app **/
                    Intent logOutIntent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivityForResult(logOutIntent, 1);
                }

                return true;
            }

        });
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainFragment, new inputFragment());
        ft.commit();

        /**
         *  Start LoginActivity and await result to be used to update the navHeader
         */
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return abdt.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * Description:
     * This function will be called after calling another Activity with startActivityForResult
     * and that particular function finishes (it's removed from the back stack)
     *
     * @param requestCode = Integer
     * @param resultCode = Integer returned by the activity started for result that indicates
     *                   success or failure
     * @param data = Contains the arguments that is returned by the called activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            Log.d(TAG, "email result: " + data.getStringExtra("email"));
            userEmail.setText(data.getStringExtra("email"));
            userFullName.setText(data.getStringExtra("name"));
        }
    }

    /**
     * Function that checks the backstack to control the fragments on the backstack. It
     * will only pop the fragment from the backstack if there is more than one fragment
     * on the backstack, which means that there is another fragment that
     * exists before the current fragment
     */
    @Override
    public void onBackPressed() {
        int backStackCount = getSupportFragmentManager().getBackStackEntryCount();

        if (backStackCount > 0) {
            super.onBackPressed();
           // getSupportFragmentManager().popBackStack();
        }
    }
}
