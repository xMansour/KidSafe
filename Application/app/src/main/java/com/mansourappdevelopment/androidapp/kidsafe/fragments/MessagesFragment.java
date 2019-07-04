package com.mansourappdevelopment.androidapp.kidsafe.fragments;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.adapters.MessageAdapter;
import com.mansourappdevelopment.androidapp.kidsafe.interfaces.OnMessageDeleteClickListener;
import com.mansourappdevelopment.androidapp.kidsafe.models.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static com.mansourappdevelopment.androidapp.kidsafe.activities.ParentSignedInActivity.CHILD_EMAIL_EXTRA;
import static com.mansourappdevelopment.androidapp.kidsafe.activities.ParentSignedInActivity.CHILD_MESSAGES_EXTRA;

public class MessagesFragment extends Fragment /*implements OnMessageDeleteClickListener*/ {
    private static final String TAG = "MessagesFragmentTAG";
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private HashMap<String, Message> messages;
    private ArrayList<Message> messagesList;
    private String childEmail;
    private RecyclerView recyclerViewMessages;
    private MessageAdapter messageAdapter;
    private TextView txtNoMessages;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_messages, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");

        recyclerViewMessages = (RecyclerView) view.findViewById(R.id.recyclerViewMessages);
        txtNoMessages = (TextView) view.findViewById(R.id.txtNoMessages);

        getData();

        if (messagesList.isEmpty()) {
            txtNoMessages.setVisibility(View.VISIBLE);
            recyclerViewMessages.setVisibility(View.GONE);
        } else {
            txtNoMessages.setVisibility(View.GONE);
            recyclerViewMessages.setVisibility(View.VISIBLE);
            recyclerViewMessages.setHasFixedSize(true);
            recyclerViewMessages.setLayoutManager(new LinearLayoutManager(getContext()));

            Collections.sort(messagesList, Collections.<Message>reverseOrder());    //descending order
            initializeAdapter(messagesList/*, this*/);
            initializeItemTouchHelper();
        }


    }

    private void getData() {
        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null) {
            messages = (HashMap<String, Message>) bundle.getSerializable(CHILD_MESSAGES_EXTRA);
            messagesList = new ArrayList<>(messages.values());
            childEmail = bundle.getString(CHILD_EMAIL_EXTRA);
        }
    }

    private void initializeAdapter(ArrayList<Message> messages/*, OnMessageDeleteClickListener onMessageDeleteClickListener*/) {
        messageAdapter = new MessageAdapter(getContext(), messages);
        //messageAdapter.setOnMessageDeleteClickListener(onMessageDeleteClickListener);
        recyclerViewMessages.setAdapter(messageAdapter);
    }

    private void initializeItemTouchHelper() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                int position = viewHolder.getAdapterPosition();

                deleteMessage(messagesList.get(position));
                messagesList.remove(position);
                messageAdapter.notifyItemRemoved(position);
                messageAdapter.notifyItemRangeChanged(position, messagesList.size());
                if (messagesList.isEmpty()) {
                    txtNoMessages.setVisibility(View.VISIBLE);
                    recyclerViewMessages.setVisibility(View.GONE);
                }
            }
    
            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                final ColorDrawable background = new ColorDrawable(Color.RED);
                background.setBounds(viewHolder.itemView.getLeft() + 10, viewHolder.itemView.getTop(), viewHolder.itemView.getRight() + (int) dX, viewHolder.itemView.getBottom());
                background.draw(c);
            }
        };
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerViewMessages);
    }

    private void deleteMessage(final Message message) {
        Query query = databaseReference.child("childs").orderByChild("email").equalTo(childEmail);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot nodeShot = dataSnapshot.getChildren().iterator().next();
                final String key = nodeShot.getKey();
                Log.i(TAG, "onDataChange: key: " + key);
                Query query = databaseReference.child("childs").child(key).child("messages").orderByChild("timeReceived").equalTo(message.getTimeReceived());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            DataSnapshot snapshot = dataSnapshot.getChildren().iterator().next();
                            snapshot.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
