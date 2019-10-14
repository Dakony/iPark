package com.example.android.ipark.models;

import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.android.ipark.MainActivity;
import com.example.android.ipark.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by DAKONY on 7/31/2019.
 */

public class FirebaseUtil {
    public static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference mDatabaseReference;
    public static FirebaseAuth mFirebaseAuth;
    private static FirebaseUtil firebaseUtil;
    public static FirebaseStorage mStorage;
    public static StorageReference mStorageRef;
    public static FirebaseAuth.AuthStateListener mAuthListener;
    private static MainActivity caller;
    public static ArrayList<User> mUSers;
    private FirebaseUtil(){}





    public static void openFbReference(String ref, final MainActivity callerActivity){
        if(firebaseUtil == null){
            firebaseUtil = new FirebaseUtil();
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mFirebaseAuth = FirebaseAuth.getInstance();
            caller = callerActivity;

            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if(firebaseAuth.getCurrentUser() == null){

                    }else {
                        String userId = firebaseAuth.getCurrentUser().getUid();

                    }
                   // Toast.makeText(callerActivity.getBaseContext(), "Welcome Back", Toast.LENGTH_SHORT).show();
                }
            };
            connectStorage();

        }
        mUSers = new ArrayList<User>();
        mDatabaseReference = mFirebaseDatabase.getReference().child(ref);
    }

    public static void attachListeer(){
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }
    public static void detachListener(){
        mFirebaseAuth.removeAuthStateListener(mAuthListener);
    }
    public static void connectStorage(){
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference().child("profile_image");
    }
}
