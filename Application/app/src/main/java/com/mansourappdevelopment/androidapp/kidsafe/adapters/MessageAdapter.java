package com.mansourappdevelopment.androidapp.kidsafe.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.interfaces.OnMessageDeleteClickListener;
import com.mansourappdevelopment.androidapp.kidsafe.models.Call;
import com.mansourappdevelopment.androidapp.kidsafe.models.Message;
import com.mansourappdevelopment.androidapp.kidsafe.utils.BackgroundGenerator;
import com.mansourappdevelopment.androidapp.kidsafe.utils.Constant;
import com.mansourappdevelopment.androidapp.kidsafe.utils.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageAdapterViewHolder> {
    private Context context;
    private ArrayList<Message> messages;
    //private OnMessageDeleteClickListener onMessageDeleteClickListener;

    /*public void setOnMessageDeleteClickListener(OnMessageDeleteClickListener onMessageDeleteClickListener) {
        this.onMessageDeleteClickListener = onMessageDeleteClickListener;
    }*/

    public MessageAdapter(Context context, ArrayList<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    public class MessageAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView txtSenderPhoneNumber;
        private TextView txtMessageBody;
        private TextView txtTimeReceived;
        private TextView txtSenderName;
        private TextView txtMessageBackground;
        //private Button btnDeleteMessage;


        public MessageAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            txtSenderPhoneNumber = (TextView) itemView.findViewById(R.id.txtSenderPhoneNumber);
            txtMessageBody = (TextView) itemView.findViewById(R.id.txtMessageBody);
            txtTimeReceived = (TextView) itemView.findViewById(R.id.txtTimeReceived);
            txtSenderName = (TextView) itemView.findViewById(R.id.txtSenderName);
            txtMessageBackground = (TextView) itemView.findViewById(R.id.txtMessageBackground);
            /*btnDeleteMessage = (Button) itemView.findViewById(R.id.btnDeleteMessage);
            btnDeleteMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getPosition();
                    onMessageDeleteClickListener.onMessageDeleteClick(messages.get(position));
                    deleteMessage(position);
                }
            });*/

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
        messageAdapterViewHolder.txtTimeReceived.setText(DateUtils.getFormattedDate(message.getTimeReceived(), context));
        messageAdapterViewHolder.txtSenderName.setText(message.getContactName());
        if (message.getContactName().equals(Constant.UNKNOWN_NUMBER)){
            messageAdapterViewHolder.txtMessageBackground.setText("#");
            messageAdapterViewHolder.txtMessageBackground.setBackground(BackgroundGenerator.getBackground(context));
        }else{
            messageAdapterViewHolder.txtMessageBackground.setText(BackgroundGenerator.getFirstCharacters(message.getContactName()));
            messageAdapterViewHolder.txtMessageBackground.setBackground(BackgroundGenerator.getBackground(context));
        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    /*private void deleteMessage(int position) {
        messages.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, messages.size());
    }*/

}
