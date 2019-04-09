package com.mansourappdevelopment.androidapp.kidsafe.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.utils.Message;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageAdapterViewHolder> {
    private Context context;
    private ArrayList<Message> messages;

    public MessageAdapter(Context context, ArrayList<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    public class MessageAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView txtSenderPhoneNumber;
        private TextView txtMessageBody;
        private TextView txtTimeReceived;


        public MessageAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            txtSenderPhoneNumber = (TextView) itemView.findViewById(R.id.txtSenderPhoneNumber);
            txtMessageBody = (TextView) itemView.findViewById(R.id.txtMessageBody);
            txtTimeReceived = (TextView) itemView.findViewById(R.id.txtTimeReceived);


        }
    }

    @NonNull
    @Override
    public MessageAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_message, viewGroup, false);
        return new MessageAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapterViewHolder messageAdapterViewHolder, int i) {
        Message message = messages.get(i);
        messageAdapterViewHolder.txtSenderPhoneNumber.setText(message.getSenderPhoneNumber());
        messageAdapterViewHolder.txtMessageBody.setText(message.getMessageBody());
        messageAdapterViewHolder.txtTimeReceived.setText(message.getTimeReceived());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
