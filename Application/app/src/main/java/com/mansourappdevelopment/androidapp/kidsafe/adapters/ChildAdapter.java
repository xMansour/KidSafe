package com.mansourappdevelopment.androidapp.kidsafe.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.interfaces.OnChildClickListener;
import com.mansourappdevelopment.androidapp.kidsafe.models.Child;
import com.mansourappdevelopment.androidapp.kidsafe.models.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ChildAdapterViewHolder> {
    private Context context;
    private ArrayList<Child> childs;
    private OnChildClickListener onChildClickListener;


    public void setOnChildClickListener(OnChildClickListener listener) {
        this.onChildClickListener = listener;
    }

    public ChildAdapter(Context context, ArrayList<Child> childs) {
        this.context = context;
        this.childs = childs;
    }

    public class ChildAdapterViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView imgChild;
        private TextView txtChildName;
        private Switch switchWebFilter;
        private Switch switchLockPhone;
        private LinearLayout layoutDeletedApp;
        private TextView txtDeletedApp;

        public ChildAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            imgChild = (CircleImageView) itemView.findViewById(R.id.imgChild);
            txtChildName = (TextView) itemView.findViewById(R.id.txtChildName);
            switchWebFilter = (Switch) itemView.findViewById(R.id.switchWebFilter);
            switchWebFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (buttonView.isPressed()) {
                        int position = getAdapterPosition();
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
                            //onChildClickListener.onItemClick(v, position);
                            onChildClickListener.onItemClick(position);
                    }
                }
            });

            switchLockPhone = (Switch) itemView.findViewById(R.id.switchLockPhone);
            switchLockPhone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (buttonView.isPressed()) {
                        int position = getAdapterPosition();
                        onChildClickListener.onBtnLockClick(isChecked, childs.get(position));
                    }
                }
            });

            layoutDeletedApp = (LinearLayout) itemView.findViewById(R.id.layoutDeletedApp);
            layoutDeletedApp.setVisibility(View.GONE);
            txtDeletedApp = (TextView) itemView.findViewById(R.id.txtDeletedApp);

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
        Child child = childs.get(i);
        childAdapterViewHolder.txtChildName.setText(child.getName());

        if (child.getScreenLock() != null) {
            childAdapterViewHolder.switchLockPhone.setChecked(child.getScreenLock().isLocked());
        }
        Picasso.get().load(child.getProfileImage()).placeholder(R.drawable.ic_profile_image).error(R.drawable.ic_profile_image).into(childAdapterViewHolder.imgChild);
        if (child.isAppDeleted()) {
            childAdapterViewHolder.layoutDeletedApp.setVisibility(View.VISIBLE);
            childAdapterViewHolder.txtDeletedApp.setText(child.getName() + " " + context.getResources().getString(R.string.deleted_the_app));
            childAdapterViewHolder.imgChild.setEnabled(false);
            childAdapterViewHolder.txtChildName.setEnabled(false);
            childAdapterViewHolder.switchLockPhone.setEnabled(false);
            childAdapterViewHolder.switchLockPhone.setClickable(false);
        }
    }

    @Override
    public int getItemCount() {
        return childs.size();
    }


}
