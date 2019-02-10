package com.mansourappdevelopment.androidapp.kidsafe.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.adapters.ChildAdapter;
import com.mansourappdevelopment.androidapp.kidsafe.interfaces.OnChildClickListener;
import com.mansourappdevelopment.androidapp.kidsafe.utils.App;
import com.mansourappdevelopment.androidapp.kidsafe.utils.User;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ParentSignedInActivity extends AppCompatActivity implements OnChildClickListener {
    public static final String TAG = "ParentSignedInActivityTag";
    private RecyclerView recyclerViewChilds;
    private ChildAdapter childAdapter;
    private ArrayList<User> childs;
    private CircleImageView imgParent;
    private TextView txtParentName;
    private TextView txtChildCount;
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
        txtChildCount = findViewById(R.id.txtChildCount);

        recyclerViewChilds = findViewById(R.id.recyclerViewChilds);
        recyclerViewChilds.setHasFixedSize(true);
        recyclerViewChilds.setLayoutManager(new LinearLayoutManager(this));
        getChilds();

    }

    public void getChilds() {
        String parentEmail = user.getEmail();
        //TODO:: query the parent with that email and get his name
        //String parentName = user.getDisplayName();    from the database
        //txtParentName.setText(parentName);
        txtParentName.setText(parentEmail);

        Query query = databaseReference.child("childs").orderByChild("parentEmail").equalTo(parentEmail);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long childCount = dataSnapshot.getChildrenCount();
                    txtChildCount.setText(String.valueOf(childCount));

                    childs = new ArrayList<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        childs.add(child.getValue(User.class));
                    }

                    initializeAdapter();

                    //Log.i(TAG, "onDataChange: dataSnapshot children count: " + dataSnapshot.getChildrenCount());
                    //Log.i(TAG, "onDataChange: dataSnapshot key: "+ dataSnapshot.getKey());
                    //Log.i(TAG, "onDataChange: dataSnapshot Children: "+ dataSnapshot.getChildren());
                    //Log.i(TAG, "onDataChange: dataSnapshot Ref: "+ dataSnapshot.getRef());
                    //Log.i(TAG, "onDataChange: dataSnapshot Value: " + dataSnapshot.getValue());
                    //Log.i(TAG, "onDataChange: dataSnapshot As String: "+ dataSnapshot.toString());

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

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(this, ChildDetailsActivity.class);
        startActivity(intent);
    }
}
