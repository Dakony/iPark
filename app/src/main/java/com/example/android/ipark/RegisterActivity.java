package com.example.android.ipark;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private static final String DOMAIN_NAME = "dplangjosiah@yahoo.com";

    //widgets
    private EditText mEmail, mPassword, mConfirmPassword;
    private Button mRegister;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEmail = (EditText) findViewById(R.id.etEmail);
        mPassword = (EditText) findViewById(R.id.etPassword);
        mConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword);
        mRegister = (Button) findViewById(R.id.btnNext);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar2);

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check for null valued EditText fields
                if(!isEmpty(mEmail.getText().toString())
                        && !isEmpty(mPassword.getText().toString())
                        && !isEmpty(mConfirmPassword.getText().toString())) {

                    //check if user has a company email address


                        //check if passwords match
                        if(doStringsMatch(mPassword.getText().toString(), mConfirmPassword.getText().toString())){
                            //Initiate registration task
                            registerNewEmail(mEmail.getText().toString(), mPassword.getText().toString());

                        }else {
                            Toast.makeText(RegisterActivity.this, "Passwords do not Match", Toast.LENGTH_SHORT).show();
                        }

                }else {
                    Toast.makeText(RegisterActivity.this, "You must fill out all the fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        hideSoftKeyboard();
    }

    public void registerNewEmail(final String email, String password){

        showDialog();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if (task.isSuccessful()){
                            Log.d(TAG, "onComplete: AuthState: " + FirebaseAuth.getInstance().getCurrentUser().getUid());

                            //insert some default data
                           // User user = new User();
                            //user.setName(email.substring(0, email.indexOf("@")));
                            //user.setPhone("1");
                            //user.setProfile_image("");
                           // user.setPlate_number("1");
                           // user.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            //FirebaseDatabase.getInstance().getReference()
                                    //.child(getString(R.string.dbnode_users))
                                    //.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    //.setValue(user)
                                    //.addOnCompleteListener(new OnCompleteListener<Void>() {
                                       // @Override
                                        //public void onComplete(@NonNull Task<Void> task) {

                                            //redirect the user to the setUp screen
                                            redirectSetUpScreen();

                                        //}
                                   // }).addOnFailureListener(new OnFailureListener() {
                                //@Override
                               // public void onFailure(@NonNull Exception e) {
                                    //Toast.makeText(RegisterActivity.this, "something went wrong.", Toast.LENGTH_SHORT).show();
                                   // FirebaseAuth.getInstance().signOut();

                                    //redirect the user to the login screen
                                   // redirectLoginScreen();

                               // }
                            //});




                        }
                        if (!task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Unable to Register",
                                    Toast.LENGTH_SHORT).show();
                        }
                        hideDialog();

                        // ...
                    }
                });
    }

    private void redirectLoginScreen()
    {
        Log.d(TAG, "redirectLoginScreen: redirecting to login screen.");

        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void redirectSetUpScreen()
    {
        Intent intent = new Intent(RegisterActivity.this, SetupActivity.class);
        startActivity(intent);
    }

    private void showDialog(){
        mProgressBar.setVisibility(View.VISIBLE);

    }

    private void hideDialog(){
        if(mProgressBar.getVisibility() == View.VISIBLE){
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    private boolean doStringsMatch(String s1, String s2){
        return s1.equals(s2);
    }

    private boolean isValidDomain(String email){
        Log.d(TAG, "isValidDomain: verifying email has correct domain: " + email);
        String domain = email.substring(email.indexOf("@") + 1).toLowerCase();
        Log.d(TAG, "isValidDomain: users domain: " + domain);
        return domain.equals(DOMAIN_NAME);
    }

    private boolean isEmpty(String string){
        return string.equals("");
    }
    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}
