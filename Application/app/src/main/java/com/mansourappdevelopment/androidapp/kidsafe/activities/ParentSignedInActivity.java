package com.mansourappdevelopment.androidapp.kidsafe.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.adapters.ChildAdapter;
import com.mansourappdevelopment.androidapp.kidsafe.interfaces.OnChildClickListener;
import com.mansourappdevelopment.androidapp.kidsafe.models.User;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ParentSignedInActivity extends AppCompatActivity implements OnChildClickListener {
    public static final String TAG = "ParentSignedInActivity";
    public static final String APPS_EXTRA = "com.mansourappdevelopment.androidapp.kidsafe.activities.APPS_EXTRA";
    public static final String CHILD_NAME_EXTRA = "com.mansourappdevelopment.androidapp.kidsafe.activities.CHILD_NAME_EXTRA";
    public static final String CHILD_EMAIL_EXTRA = "com.mansourappdevelopment.androidapp.kidsafe.activities.CHILD_EMAIL_EXTRA";
    public static final String PARENT_EMAIL_EXTRA = "com.mansourappdevelopment.androidapp.kidsafe.activities.PARENT_EMAIL_EXTRA";
    public static final String CHILD_IMG_EXTRA = "com.mansourappdevelopment.androidapp.kidsafe.activities.CHILD_IMG_EXTRA";
    public static final String CHILD_MESSAGES_EXTRA = "com.mansourappdevelopment.androidapp.kidsafe.activities.CHILD_MESSAGES_EXTRA";
    public static final String CHILD_CALLS_EXTRA = "com.mansourappdevelopment.androidapp.kidsafe.activities.CHILD_CALLS_EXTRA";
    private RecyclerView recyclerViewChilds;
    private ChildAdapter childAdapter;
    private ArrayList<User> childs;
    private CircleImageView imgParent;
    private TextView txtParentName;
    //private TextView txtChildCount;
    private ProgressBar progressBar;
    private LinearLayout linearLayout;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_signed_in);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");

        imgParent = findViewById(R.id.imgParent);
        txtParentName = findViewById(R.id.txtParentName);
        //txtChildCount = findViewById(R.id.txtChildCount);
        linearLayout = findViewById(R.id.linearLayoutParentSignedInActivity);
        progressBar = findViewById(R.id.progressBarParentSignedInActivity);
        progressBar.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.GONE);

        recyclerViewChilds = findViewById(R.id.recyclerViewChilds);
        recyclerViewChilds.setHasFixedSize(true);
        recyclerViewChilds.setLayoutManager(new LinearLayoutManager(this));
        String parentEmail = user.getEmail();
        getChilds(parentEmail);
        getParentName(parentEmail);

    }

    public void getChilds(String parentEmail) {
        Query query = databaseReference.child("childs").orderByChild("parentEmail").equalTo(parentEmail);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //long childCount = dataSnapshot.getChildrenCount();
                    //txtChildCount.setText(String.valueOf(childCount));

                    childs = new ArrayList<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        childs.add(child.getValue(User.class));
                    }

                    initializeAdapter();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void initializeAdapter() {
        childAdapter = new ChildAdapter(this, childs);
        childAdapter.setOnChildClickListener(this);
        recyclerViewChilds.setAdapter(childAdapter);
    }

    public void getParentName(String parentEmail) {
        Query query = databaseReference.child("parents").orderByChild("email").equalTo(parentEmail);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot nodeShot = dataSnapshot.getChildren().iterator().next();
                //String key = dataSnapshot.getKey();
                User parent = nodeShot.getValue(User.class);
                String parentName = parent.getName();
                progressBar.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
                txtParentName.setText(parentName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onItemClick(View view, int position) {
        User child = childs.get(position);
        Intent intent = new Intent(this, ChildDetailsActivity.class);
        intent.putExtra(APPS_EXTRA, child.getApps());
        intent.putExtra(PARENT_EMAIL_EXTRA, user.getEmail());
        intent.putExtra(CHILD_NAME_EXTRA, child.getName());
        intent.putExtra(CHILD_EMAIL_EXTRA, child.getEmail());

        intent.putExtra(CHILD_MESSAGES_EXTRA, child.getMessages());
        intent.putExtra(CHILD_CALLS_EXTRA, child.getCalls());

        /*Bundle bundle = new Bundle();
        bundle.putSerializable(CHILD_MESSAGES_EXTRA, child.getMessages());
        bundle.putSerializable(CHILD_CALLS_EXTRA, child.getCalls());
        intent.putExtras(bundle);*/
        //TODO:: put child's image as an extra

        startActivity(intent);
    }

    @Override
    public void onWebFilterClick(boolean checked, User child) {
        String childEmail = child.getEmail();
        if (checked) {
            Toast.makeText(this, "Web Filter Enabled", Toast.LENGTH_SHORT).show();
            Query query = databaseReference.child("childs").orderByChild("email").equalTo(childEmail);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        DataSnapshot nodeShot = dataSnapshot.getChildren().iterator().next();
                        nodeShot.getRef().child("webFilter").setValue(true);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            Toast.makeText(this, "Web Filter Disabled", Toast.LENGTH_SHORT).show();
            Query query = databaseReference.child("childs").orderByChild("email").equalTo(childEmail);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        DataSnapshot nodeShot = dataSnapshot.getChildren().iterator().next();
                        nodeShot.getRef().child("webFilter").setValue(false);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

}
