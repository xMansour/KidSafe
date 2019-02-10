package com.mansourappdevelopment.androidapp.kidsafe.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.utils.App;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.AppAdapterViewHolder> {
    private Context context;
    private ArrayList<App> apps;

    public AppAdapter(Context context, ArrayList<App> apps) {
        this.context = context;
        this.apps = apps;
    }


    public class AppAdapterViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView imgApp;
        private TextView txtAppName;
        private Switch switchAppState;

        public AppAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            imgApp = itemView.findViewById(R.id.imgApp);
            txtAppName = itemView.findViewById(R.id.txtAppName);
            switchAppState = itemView.findViewById(R.id.switchAppState);
            switchAppState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //TODO:: update the app state in the firebase database
                }
            });
        }
    }


    @NonNull
    @Override
    public AppAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.app_card, viewGroup, false);
        return new AppAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppAdapterViewHolder appAdapterViewHolder, int i) {
        App app = apps.get(i);
        appAdapterViewHolder.txtAppName.setText(app.getAppName());
        appAdapterViewHolder.switchAppState.setChecked(app.isBlocked());
        //TODO:: upload the apps to the firebase storage and get them back here
        appAdapterViewHolder.imgApp.setImageResource(R.drawable.ic_app);

    }

    @Override
    public int getItemCount() {
        return apps.size();
    }
}
