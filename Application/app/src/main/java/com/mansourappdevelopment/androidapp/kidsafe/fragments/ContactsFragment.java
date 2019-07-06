package com.mansourappdevelopment.androidapp.kidsafe.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.adapters.ContactsAdapter;
import com.mansourappdevelopment.androidapp.kidsafe.interfaces.OnContactClickListener;
import com.mansourappdevelopment.androidapp.kidsafe.models.Contact;
import com.mansourappdevelopment.androidapp.kidsafe.utils.Constant;

import java.util.ArrayList;

public class ContactsFragment extends Fragment implements OnContactClickListener/*, OnPermissionExplanationListener*/ {
	private Context context;
	private RecyclerView recyclerViewContacts;
	private ContactsAdapter contactsAdapter;
	private TextView txtNoContacts;
	private Bundle bundle;
	private ArrayList<Contact> contacts;
	
	
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_contacts, container, false);
	}
	
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		context = getContext();
		getData();
		
		recyclerViewContacts = view.findViewById(R.id.recyclerViewContacts);
		txtNoContacts = view.findViewById(R.id.txtNoContacts);
		
		if (contacts.isEmpty()) {
			txtNoContacts.setVisibility(View.VISIBLE);
			recyclerViewContacts.setVisibility(View.GONE);
		} else {
			txtNoContacts.setVisibility(View.GONE);
			recyclerViewContacts.setVisibility(View.VISIBLE);
			recyclerViewContacts.setHasFixedSize(true);
			recyclerViewContacts.setLayoutManager(new LinearLayoutManager(getContext()));
			initializeAdapter(this);
			
		}
	}
	
	
	public void getData() {
		bundle = getActivity().getIntent().getExtras();
		if (bundle != null) {
			contacts = bundle.getParcelableArrayList(Constant.CHILD_CONTACTS_EXTRA);
		}
	}
	
	public void initializeAdapter(OnContactClickListener onContactClickListener) {
		contactsAdapter = new ContactsAdapter(contacts, context);
		contactsAdapter.setOnContactClickListener(onContactClickListener);
		recyclerViewContacts.setAdapter(contactsAdapter);
	}
	
	
	@Override
	public void onCallClick(String contactNumber) {
		Intent intent = new Intent(Intent.ACTION_DIAL);
		intent.setData(Uri.parse("tel:" + contactNumber));
		startActivity(intent);
	}
	
	@Override
	public void onMessageClick(String contactNumber) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("sms:" + contactNumber));
		startActivity(intent);
	}
	
}
