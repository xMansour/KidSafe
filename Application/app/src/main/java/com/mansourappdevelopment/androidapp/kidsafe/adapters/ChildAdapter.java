package com.mansourappdevelopment.androidapp.kidsafe.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.utils.User;

import java.util.ArrayList;

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ChildAdapterViewHolder> {
    private Context context;
    private ArrayList<User> childs;

    public ChildAdapter(Context context, ArrayList<User> childs) {
        this.context = context;
        this.childs = childs;
    }

    public class ChildAdapterViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgChild;
        private TextView txtChildName;

        public ChildAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            imgChild = itemView.findViewById(R.id.imgChild);
            txtChildName = itemView.findViewById(R.id.txtChildName);
        }
    }


    @NonNull
    @Override
    public ChildAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.child_card, viewGroup, false);
        return new ChildAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildAdapterViewHolder childAdapterViewHolder, int i) {
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
