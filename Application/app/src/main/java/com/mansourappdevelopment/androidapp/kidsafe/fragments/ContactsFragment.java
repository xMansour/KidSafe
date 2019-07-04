package com.mansourappdevelopment.androidapp.kidsafe.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
		
		recyclerViewContacts = (RecyclerView) view.findViewById(R.id.recyclerViewContacts);
		txtNoContacts = (TextView) view.findViewById(R.id.txtNoContacts);
		
		if (contacts.isEmpty()) {
			txtNoContacts.setVisibility(View.VISIBLE);
			recyclerViewContacts.setVisibility(View.GONE);
		} else {
			txtNoContacts.setVisibility(View.GONE);
			recyclerViewContacts.setVisibility(View.VISIBLE);
			recyclerViewContacts.setHasFixedSize(true);
			recyclerViewContacts.setLayoutManager(new LinearLayoutManager(getContext()));
			initializeAdapter(this);
			initializeItemTouchHelper();
			
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
	
	private void initializeItemTouchHelper() {
		ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
			@Override
			public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
				return false;
			}
			
			@Override
			public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
				int position = viewHolder.getAdapterPosition();
			}
			
			@Override
			public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
				super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
				View itemView = viewHolder.itemView;
				
				if (dX > 0.0) {
					final ColorDrawable background = new ColorDrawable(getResources().getColor(R.color.flatui_turquoise));
					background.setBounds(viewHolder.itemView.getLeft() - (int) dX + 10, viewHolder.itemView.getTop(), viewHolder.itemView.getRight() + (int) dX - 10, viewHolder.itemView.getBottom());
					background.draw(c);
					Drawable callIcon = ContextCompat.getDrawable(context, R.drawable.ic_call_white);
					callIcon.setBounds(20,20,20,20);
					callIcon.draw(c);
				} else {
					final ColorDrawable background = new ColorDrawable(getResources().getColor(R.color.flatui_alizarin));
					background.setBounds(viewHolder.itemView.getLeft() + (int) dX + 10, viewHolder.itemView.getTop(), viewHolder.itemView.getRight() - (int) dX - 10, viewHolder.itemView.getBottom());
					background.draw(c);
				}
			}
		};
		new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerViewContacts);
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
