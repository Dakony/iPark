package com.example.android.ipark;

import android.*;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class SetupActivity extends AppCompatActivity implements ValueEventListener,View.OnClickListener {

    private static final String TAG = "SettingsActivity";



    //private static final String DOMAIN_NAME = "tabian.ca";
    final static int Gallery_Pick = 1;



    //firebase

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    private StorageReference UserProfileImageRef;


    //widgets
    private EditText  mName, mPhone, mPlate;
    private Button mSave;
    private CircleImageView mProfileImage;
    private ProgressBar mProgressBar;


    //vars

     String currentUserId;
    private ProgressDialog loadingBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        Log.d(TAG, "onCreate: started.");

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.dbnode_users)).child(currentUserId);
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child(getString(R.string.profile_images));

        mName = (EditText) findViewById(R.id.input_name);
        mPhone = (EditText) findViewById(R.id.input_phone);
        mName = (EditText) findViewById(R.id.input_name);
        mPlate = (EditText) findViewById(R.id.input_plate);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProfileImage = (CircleImageView) findViewById(R.id.setup_profile_image);
        mSave = (Button) findViewById(R.id.btn_submit);

        UsersRef.addValueEventListener(this);
        mProfileImage.setOnClickListener(this);
        mSave.setOnClickListener(this);

        loadingBar = new ProgressDialog(this);



    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.btn_submit:
                SaveAccountInformation();
                break;
            case R.id.setup_profile_image:
                setupUpProfileImage();
                break;
        }
    }

    private void setupUpProfileImage() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,Gallery_Pick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null)
        {
            Uri imageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK)
            {
                loadingBar.setTitle("Profile Image");
                loadingBar.setMessage("Please Wait while we are uploading your Image");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(true);


                Uri resultUri = result.getUri();
                StorageReference filePath = UserProfileImageRef.child(currentUserId + ".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(SetupActivity.this,"Profile Image Save successfully to Firebase storage",Toast.LENGTH_SHORT).show();
                            final String downloadUri = task.getResult().getDownloadUrl().toString();
                            UsersRef.child(getString(R.string.field_profile_image)).setValue(downloadUri)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                Intent selfIntent = new Intent(SetupActivity.this, SetupActivity.class);
                                                startActivity(selfIntent);
                                                Toast.makeText(SetupActivity.this,"Profile Image Save successfully to Firebase database",Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }
                                            else
                                            {
                                                String message = task.getException().getMessage();
                                                Toast.makeText(SetupActivity.this,"Error Occurred" + message,Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }
                                        }
                                    });
                        }
                    }
                });
            }
            else
            {
                Toast.makeText(this, "Error Occurred Image can not be Crop, Try again Later", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }

    }

    private void SaveAccountInformation()
    {
        String name = mName.getText().toString();
        String phone = mPhone.getText().toString();
        String plate = mPlate.getText().toString();

        if(TextUtils.isEmpty(name)){
            mName.setError("Please Provide a username");
            mName.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(phone)){
            mPhone.setError("Please Type your Full name");
            mPhone.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(plate)){
            mPlate.setError("Please Type your Phone number");
            mPlate.requestFocus();
            return;
        }else{

            mProgressBar.setVisibility(View.VISIBLE);
            HashMap userMap = new HashMap<>();
            userMap.put(getString(R.string.field_user_id),currentUserId);
            userMap.put(getString(R.string.field_name),name);
            userMap.put(getString(R.string.field_phone),phone);
            userMap.put(getString(R.string.field_plate_number),plate);
            UsersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    mProgressBar.setVisibility(View.GONE);
                    if(task.isSuccessful()){
                        sendUserToMainActivity();
                        Toast.makeText(SetupActivity.this, "Account Created Successfully", Toast.LENGTH_LONG).show();
                    }else
                    {
                        String message = task.getException().getMessage();
                        Toast.makeText(SetupActivity.this, "Error has Occurred" + message, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }


    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(SetupActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot)
    {
        if(dataSnapshot.exists())
        {
            if(dataSnapshot.hasChild(getString(R.string.field_profile_image)))
            {
                String image = dataSnapshot.child(getString(R.string.field_profile_image)).getValue().toString();
                Picasso.with(this).load(image).placeholder(R.drawable.profile).into(mProfileImage);
            }else{
                Toast.makeText(this, "Please Provide a Profile Image...", Toast.LENGTH_SHORT).show();
            }


        }

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
