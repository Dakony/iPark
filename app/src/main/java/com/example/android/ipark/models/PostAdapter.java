package com.example.android.ipark.models;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.ipark.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by DAKONY on 10/10/2019.
 */

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    ArrayList<User>users;
    private ChildEventListener mChildEventListener;
    private FirebaseDatabase mfirebaseDatabase;
    private DatabaseReference mDatabaseReferece;
    private CircleImageView post_profile_image;

    public PostAdapter(){
        mfirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReferece = FirebaseUtil.mDatabaseReference;
        users = FirebaseUtil.mUSers;
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User td = dataSnapshot.getValue(User.class);
                Log.d("Deal",td.getName());
                td.setName(dataSnapshot.getKey());
                users.add(td);
                notifyItemInserted(users.size()-1);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabaseReferece.addChildEventListener(mChildEventListener);
    }


    @Override
    public PostViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.all_posts_layout,parent,false);
        return new PostViewHolder(itemView);


    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        User posts = users.get(position);
        holder.bind(posts);

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView post_user_name;
        TextView post_date;
        TextView post_time;
        TextView post_description;
        public PostViewHolder(View itemView) {
            super(itemView);
            post_user_name = (TextView)itemView.findViewById(R.id.post_user_name);
            post_date = (TextView)itemView.findViewById(R.id.post_date);
            post_time = (TextView)itemView.findViewById(R.id.post_time);
            post_description = (TextView)itemView.findViewById(R.id.post_description);
            post_profile_image = (CircleImageView)itemView.findViewById(R.id.post_profile_image);
            itemView.setOnClickListener(this);
        }

        public void bind(User posts)
        {
            post_user_name.setText(posts.getName());
            post_date.setText(posts.getSaveCurrentDate());
            post_time.setText(posts.getSaveCurrentTime());
            post_description.setText(posts.getPostDescription());
            showImage(posts.getProfile_image());

        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Log.d("Click",String.valueOf(position));
            User selectedPost = users.get(position);

        }
    }

    private void showImage(String url)
    {
        if(url != null && url.isEmpty()==false){
            Picasso.with(post_profile_image.getContext())
                    .load(url)
                    .into(post_profile_image);
        }
    }
}
