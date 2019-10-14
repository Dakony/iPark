package com.example.android.ipark;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {

    //Firebase
    private FirebaseAuth mAuth;

    //Widget
    private TextView mRegister, mForgetPassword;
    private EditText mEmail, mPassword;
    private Button mLogin;
    private ProgressDialog loadingBar;

    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mRegister = (TextView) findViewById(R.id.input_register);
        mForgetPassword = (TextView)findViewById(R.id.input_forgetpassword);
        mEmail = (EditText) findViewById(R.id.input_email);
        mPassword = (EditText)findViewById(R.id.input_password);
        mLogin = (Button)findViewById(R.id.btn_login);

        mAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);





        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                final String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                if(email.isEmpty()){
                    mEmail.setError(getString(R.string.email_empty));
                    mEmail.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    mEmail.setError(getString(R.string.valid_email));
                    mEmail.requestFocus();
                    return;
                }
                if (password.isEmpty()){
                    mPassword.setError(getString(R.string.password_empty));
                    mPassword.requestFocus();
                    return;
                }
                showDialog();

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        loadingBar.dismiss();
                        if(task.isSuccessful()){
                            LoginInUser();

                        }else{
                            Toast.makeText(getApplicationContext(),"Sorry incorrect Password or email",Toast.LENGTH_LONG).show();

                        }

                    }
                });
            }
        });


        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(HomeActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
       // FirebaseUser currentUser = mAuth.getCurrentUser();
        //if(currentUser != null)
        //{

            //LoginInUser();
        //}
    }

    private void LoginInUser()
    {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showDialog()
    {
        loadingBar.setTitle(getString(R.string.dialog_login));
        loadingBar.setMessage(getString(R.string.login_message));
        loadingBar.setCanceledOnTouchOutside(true);
        loadingBar.show();
    }
}
