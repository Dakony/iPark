package com.example.android.ipark;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.ipark.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class PostActivity extends AppCompatActivity {

    private static final String TAG = "PostActivity";
    private static final int REQUEST_LOCATION = 1;
    //private TextInputView mPost;
    private EditText mPost;
    private Button mSend;

    private DatabaseReference usersRef,PostsRef;
    private String current_user_id,saveCurrentDate, saveCurrentTime,postDescription;
    private FirebaseAuth mAuth;
    private long countPost = 0 ;
    LocationManager locationManager;
    String latitude, longitude;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        loadingBar = new ProgressDialog(this);

        usersRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.dbnode_users));
        PostsRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.db_post));

        mPost = (EditText) findViewById(R.id.post);
        mSend = (Button) findViewById(R.id.btn_send);

        getLocation();

        mSend.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {


                postDescription = mPost.getText().toString();
                if(TextUtils.isEmpty(postDescription)){
                    Toast.makeText(PostActivity.this, "Please Provide description of what you want to post", Toast.LENGTH_SHORT).show();
                }else {
                    showDialog();
                    Calendar calForDate = Calendar.getInstance();
                    SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                    saveCurrentDate = currentDate.format(calForDate.getTime());


                    Calendar calForTime = Calendar.getInstance();
                    SimpleDateFormat currentTime = new SimpleDateFormat("HH-mm");
                    saveCurrentTime = currentTime.format(calForTime.getTime());


                    PostsRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            if(dataSnapshot.exists())
                            {
                                countPost = dataSnapshot.getChildrenCount();
                            }
                            else
                            {
                                countPost = 0;
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    usersRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                final String fullname = dataSnapshot.child(getString(R.string.field_name)).getValue().toString();
                                final String userProfileImage = dataSnapshot.child(getString(R.string.field_profile_image)).getValue().toString();
                                HashMap postMap = new HashMap();
                                postMap.put("uid", current_user_id);
                                postMap.put("date", saveCurrentDate);
                                postMap.put("time", saveCurrentTime);
                                postMap.put("longitude", longitude);
                                postMap.put("latitude", latitude);
                                postMap.put("description", postDescription);
                                postMap.put("profileImage", userProfileImage);
                                postMap.put(getString(R.string.field_name), fullname);
                                postMap.put("counter", countPost);



                                PostsRef.push().setValue(postMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task)
                                    {
                                        if(task.isSuccessful())
                                        {
                                            SendUserToMainActivity();
                                            Toast.makeText(PostActivity.this, "Your Post is Successfully", Toast.LENGTH_SHORT).show();
                                            loadingBar.dismiss();
                                        }else
                                        {
                                            String message = task.getException().getMessage();
                                            Toast.makeText(PostActivity.this, "Error has Occurred while posting" + message, Toast.LENGTH_SHORT).show();
                                            loadingBar.dismiss();
                                        }
                                    }
                                });

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }

    private void showDialog()
    {
        loadingBar.setTitle("Posting");
        loadingBar.setMessage("Posting .....");
        loadingBar.setCanceledOnTouchOutside(true);
        loadingBar.show();
    }

    private void getLocation()
    {
        locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //check permissions
        if(ActivityCompat.checkSelfPermission(PostActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(PostActivity.this,
               Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
         {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION);
        }else{
            Location locationGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location locationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location locationPassive = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if(locationGps != null){
                final double lat = locationGps.getLatitude();
                final double longi = locationGps.getLongitude();
                latitude = String.valueOf(lat);
                longitude = String.valueOf(longi);
                Log.d(TAG, "Logitude: " + longi +"latitude :" + lat);

            }else if (locationNetwork != null){
                final double lat = locationNetwork.getLatitude();
                final double longi = locationNetwork.getLongitude();
                latitude = String.valueOf(lat);
                longitude = String.valueOf(longi);
                Log.d(TAG, "Logitude: " + longi +"latitude :" + lat);
            }else if(locationPassive != null){
                final double lat = locationPassive.getLatitude();
                final double longi = locationPassive.getLongitude();
                latitude = String.valueOf(lat);
                longitude = String.valueOf(longi);
                Log.d(TAG, "Logitude: " + longi +"latitude :" + lat);
            }
        }
    }

    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(PostActivity.this, MainActivity.class);
        startActivity(mainIntent);
    }
}
