package com.mansourappdevelopment.androidapp.kidsafe.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.adapters.CallAdapter;
import com.mansourappdevelopment.androidapp.kidsafe.models.Call;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static com.mansourappdevelopment.androidapp.kidsafe.activities.ParentSignedInActivity.CHILD_CALLS_EXTRA;
import static com.mansourappdevelopment.androidapp.kidsafe.activities.ParentSignedInActivity.CHILD_EMAIL_EXTRA;


public class CallsFragment extends Fragment /*implements OnCallDeleteClickListener */ {
	private static final String TAG = "CallsFragmentTAG";
	private FirebaseDatabase firebaseDatabase;
	private DatabaseReference databaseReference;
	private HashMap<String, Call> calls;
	private ArrayList<Call> callsList;
	private String childEmail;
	private RecyclerView recyclerViewCalls;
	private CallAdapter callAdapter;
	private TextView txtNoCalls;
	
	
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_calls, container, false);
	}
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		firebaseDatabase = FirebaseDatabase.getInstance();
		databaseReference = firebaseDatabase.getReference("users");
		getData();
		
		recyclerViewCalls = view.findViewById(R.id.recyclerViewCalls);
		txtNoCalls = view.findViewById(R.id.txtNoCalls);
		
		if (callsList.isEmpty()) {
			txtNoCalls.setVisibility(View.VISIBLE);
			recyclerViewCalls.setVisibility(View.GONE);
		} else {
			txtNoCalls.setVisibility(View.GONE);
			recyclerViewCalls.setVisibility(View.VISIBLE);
			recyclerViewCalls.setHasFixedSize(true);
			recyclerViewCalls.setLayoutManager(new LinearLayoutManager(getContext()));
			
			Collections.sort(callsList, Collections.<Call>reverseOrder());  //descending order
			initializeAdapter(callsList/*, this*/);
			initializeItemTouchHelper();
		}
	}
	
	private void getData() {
		Bundle bundle = getActivity().getIntent().getExtras();
		if (bundle != null) {
			calls = (HashMap<String, Call>) bundle.getSerializable(CHILD_CALLS_EXTRA);
			callsList = new ArrayList<>(calls.values());
			childEmail = bundle.getString(CHILD_EMAIL_EXTRA);
		}
	}
	
	private void initializeAdapter(ArrayList<Call> calls/*, OnCallDeleteClickListener onCallDeleteClickListener*/) {
		callAdapter = new CallAdapter(getContext(), calls);
		//callAdapter.setOnCallDeleteClickListener(onCallDeleteClickListener);
		recyclerViewCalls.setAdapter(callAdapter);
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
				deleteCall(callsList.get(position));
				callsList.remove(position);
				callAdapter.notifyItemRemoved(position);
				callAdapter.notifyItemRangeChanged(position, callsList.size());
				if (callsList.isEmpty()) {
					txtNoCalls.setVisibility(View.VISIBLE);
					recyclerViewCalls.setVisibility(View.GONE);
				}
			}
		};
		
		new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerViewCalls);
		
	}
	
	private void deleteCall(final Call call) {
		Query query = databaseReference.child("childs").orderByChild("email").equalTo(childEmail);
		query.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				DataSnapshot nodeShot = dataSnapshot.getChildren().iterator().next();
				final String key = nodeShot.getKey();
				Log.i(TAG, "onDataChange: key: " + key);
				Query query = databaseReference.child("childs").child(key).child("calls").orderByChild("callTime").equalTo(call.getCallTime());
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
