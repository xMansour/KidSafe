package com.mansourappdevelopment.androidapp.kidsafe.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.interfaces.OnChildClickListener;
import com.mansourappdevelopment.androidapp.kidsafe.utils.User;

import java.util.ArrayList;

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ChildAdapterViewHolder> {
    private Context context;
    private ArrayList<User> childs;
    private OnChildClickListener onChildClickListener;

    public void setOnChildClickListener(OnChildClickListener listener) {
        this.onChildClickListener = listener;
    }

    public ChildAdapter(Context context, ArrayList<User> childs) {
        this.context = context;
        this.childs = childs;
    }

    public class ChildAdapterViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgChild;
        private TextView txtChildName;
        private Switch switchWebFilter;

        public ChildAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            imgChild = itemView.findViewById(R.id.imgChild);
            txtChildName = itemView.findViewById(R.id.txtChildName);
            switchWebFilter = (Switch) itemView.findViewById(R.id.switchWebFilter);
            switchWebFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (buttonView.isPressed()) {
                        int position = getPosition();
                        onChildClickListener.onWebFilterClick(isChecked, childs.get(position));
                    }

                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onChildClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION)
                            onChildClickListener.onItemClick(v, position);
                    }
                }
            });
        }
    }


    @NonNull
    @Override
    public ChildAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_child, viewGroup, false);
        return new ChildAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChildAdapterViewHolder childAdapterViewHolder, int i) {
        User child = childs.get(i);
        childAdapterViewHolder.txtChildName.setText(child.getName());
        //TODO:: get the image from the user class
        childAdapterViewHolder.imgChild.setImageResource(R.drawable.ic_face);
    }

    @Override
    public int getItemCount() {
        return childs.size();
    }


}
