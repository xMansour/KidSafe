package com.mansourappdevelopment.androidapp.kidsafe.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.adapters.AppAdapter;
import com.mansourappdevelopment.androidapp.kidsafe.adapters.MessageAdapter;
import com.mansourappdevelopment.androidapp.kidsafe.utils.Message;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import static com.mansourappdevelopment.androidapp.kidsafe.activities.ParentSignedInActivity.CHILD_MESSAGES_EXTRA;

public class MessagesFragment extends Fragment {
    private static final String TAG = "MessagesFragmentTAG";
    private HashMap<String, Message> messages;
    private RecyclerView recyclerViewMessages;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerViewMessages = (RecyclerView) view.findViewById(R.id.recyclerViewMessages);
        recyclerViewMessages.setHasFixedSize(true);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(getContext()));

        initializeAdapter(getData());
    }

    private ArrayList<Message> getData() {
        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null) {
            messages = (HashMap<String, Message>) bundle.getSerializable(CHILD_MESSAGES_EXTRA);
            return new ArrayList<>(messages.values());
        }

        return null;
    }

    private void initializeAdapter(ArrayList<Message> messages) {
        MessageAdapter messageAdapter = new MessageAdapter(getContext(), messages);
        recyclerViewMessages.setAdapter(messageAdapter);
    }
}
