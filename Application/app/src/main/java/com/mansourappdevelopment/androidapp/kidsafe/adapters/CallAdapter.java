package com.mansourappdevelopment.androidapp.kidsafe.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.interfaces.OnCallDeleteClickListener;
import com.mansourappdevelopment.androidapp.kidsafe.utils.Call;

import java.util.ArrayList;

public class CallAdapter extends RecyclerView.Adapter<CallAdapter.CallAdapterViewHolder> {
    private static final String TAG = "CallAdapterTAG";
    private Context context;
    private ArrayList<Call> calls;
    private OnCallDeleteClickListener onCallDeleteClickListener;

    public void setOnCallDeleteClickListener(OnCallDeleteClickListener onCallDeleteClickListener) {
        this.onCallDeleteClickListener = onCallDeleteClickListener;
    }

    public CallAdapter(Context context, ArrayList<Call> calls) {
        this.context = context;
        this.calls = calls;
    }


    public class CallAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView txtCallType;
        private TextView txtCallerPhoneNumber;
        private TextView txtContactName;
        private TextView txtCallTime;
        private TextView txtCallDuration;
        private Button btnDeleteCall;


        public CallAdapterViewHolder(@NonNull final View itemView) {
            super(itemView);
            txtCallType = (TextView) itemView.findViewById(R.id.txtCallType);
            txtCallerPhoneNumber = (TextView) itemView.findViewById(R.id.txtCallerPhoneNumber);
            txtContactName = (TextView) itemView.findViewById(R.id.txtContactName);
            txtCallTime = (TextView) itemView.findViewById(R.id.txtCallTime);
            txtCallDuration = (TextView) itemView.findViewById(R.id.txtCallDuration);
            btnDeleteCall = (Button) itemView.findViewById(R.id.btnDeleteCall);
            btnDeleteCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getPosition();
                    onCallDeleteClickListener.onCallDeleteClick(calls.get(position));
                    deleteCall(position);
                }
            });
        }
    }

    @NonNull
    @Override
    public CallAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_call, viewGroup, false);
        return new CallAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CallAdapterViewHolder callAdapterViewHolder, int i) {
        Call call = calls.get(i);
        callAdapterViewHolder.txtCallType.setText(call.getCallType());
        callAdapterViewHolder.txtCallerPhoneNumber.setText(call.getPhoneNumber());
        callAdapterViewHolder.txtContactName.setText(call.getContactName());
        callAdapterViewHolder.txtCallTime.setText(call.getCallTime());
        callAdapterViewHolder.txtCallDuration.setText(call.getCallDurationInSeconds() + "s");

    }

    @Override
    public int getItemCount() {
        return calls.size();
    }

    private void deleteCall(int position) {
        calls.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, calls.size());
    }
}
