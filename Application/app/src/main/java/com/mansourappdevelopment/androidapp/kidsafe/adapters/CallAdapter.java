package com.mansourappdevelopment.androidapp.kidsafe.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.models.Call;
import com.mansourappdevelopment.androidapp.kidsafe.utils.BackgroundGenerator;
import com.mansourappdevelopment.androidapp.kidsafe.utils.Constant;
import com.mansourappdevelopment.androidapp.kidsafe.utils.DateUtils;

import java.util.ArrayList;

public class CallAdapter extends RecyclerView.Adapter<CallAdapter.CallAdapterViewHolder> {
	private static final String TAG = "CallAdapterTAG";
	private Context context;
	private ArrayList<Call> calls;
	//private OnCallDeleteClickListener onCallDeleteClickListener;

    /*public void setOnCallDeleteClickListener(OnCallDeleteClickListener onCallDeleteClickListener) {
        this.onCallDeleteClickListener = onCallDeleteClickListener;
    }*/
	
	public CallAdapter(Context context, ArrayList<Call> calls) {
		this.context = context;
		this.calls = calls;
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
		callAdapterViewHolder.txtCallerPhoneNumber.setText(call.getPhoneNumber());
		callAdapterViewHolder.txtContactName.setText(call.getContactName());
		callAdapterViewHolder.txtCallTime.setText(DateUtils.getFormattedDate(call.getCallTime(), context));
		callAdapterViewHolder.txtCallDuration.setText(call.getCallDurationInSeconds() + "s");
		
		if (call.getCallType().equals(Constant.INCOMING_CALL)) {
			callAdapterViewHolder.imgCallType.setBackgroundResource(R.drawable.ic_call_received);
		} else {
			callAdapterViewHolder.imgCallType.setBackgroundResource(R.drawable.ic_call_made);
		}
		
		if (call.getContactName().equals(Constant.UNKNOWN_NUMBER)) {
			callAdapterViewHolder.txtCallBackground.setText("#");
			callAdapterViewHolder.txtCallBackground.setBackground(BackgroundGenerator.getBackground(context));
		} else {
			callAdapterViewHolder.txtCallBackground.setText(BackgroundGenerator.getFirstCharacters(call.getContactName()));
			callAdapterViewHolder.txtCallBackground.setBackground(BackgroundGenerator.getBackground(context));
		}
	}
	
	@Override
	public int getItemCount() {
		return calls.size();
	}
	
	public class CallAdapterViewHolder extends RecyclerView.ViewHolder {
		private ImageView imgCallType;
		private TextView txtCallerPhoneNumber;
		private TextView txtContactName;
		private TextView txtCallTime;
		private TextView txtCallDuration;
		private TextView txtCallBackground;
		//private Button btnDeleteCall;
		
		
		public CallAdapterViewHolder(@NonNull final View itemView) {
			super(itemView);
			imgCallType = itemView.findViewById(R.id.imgCallType);
			txtCallerPhoneNumber = itemView.findViewById(R.id.txtCallerPhoneNumber);
			txtContactName = itemView.findViewById(R.id.txtContactName);
			txtCallTime = itemView.findViewById(R.id.txtCallTime);
			txtCallDuration = itemView.findViewById(R.id.txtCallDuration);
			txtCallBackground = itemView.findViewById(R.id.txtCallBackground);
            /*btnDeleteCall = (Button) itemView.findViewById(R.id.btnDeleteCall);
            btnDeleteCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    onCallDeleteClickListener.onCallDeleteClick(calls.get(position));
                    deleteCall(position);
                }
            });*/
		}
	}

    /*private void deleteCall(int position) {
        calls.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, calls.size());
    }*/
	
}
