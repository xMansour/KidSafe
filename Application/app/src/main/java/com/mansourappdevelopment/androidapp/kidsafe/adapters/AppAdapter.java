package com.mansourappdevelopment.androidapp.kidsafe.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.interfaces.OnAppClickListener;
import com.mansourappdevelopment.androidapp.kidsafe.utils.App;

import java.util.ArrayList;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.AppAdapterViewHolder> {
    private Context context;
    private ArrayList<App> apps;
    private OnAppClickListener onAppClickListener;
    private Random random;

    public void setOnAppClickListener(OnAppClickListener onAppClickListener) {
        this.onAppClickListener = onAppClickListener;
    }

    public AppAdapter(Context context, ArrayList<App> apps) {
        this.context = context;
        this.apps = apps;
        random = new Random();
    }


    public class AppAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView txtAppBackground;
        private TextView txtAppName;
        private Switch switchAppState;

        public AppAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            txtAppBackground = itemView.findViewById(R.id.txtAppBackground);
            txtAppName = itemView.findViewById(R.id.txtAppName);
            switchAppState = itemView.findViewById(R.id.switchAppState);
            switchAppState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (buttonView.isPressed())
                        onAppClickListener.onItemClick(txtAppName.getText().toString(), isChecked);
                }
            });
        }
    }


    @NonNull
    @Override
    public AppAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_app, viewGroup, false);
        return new AppAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppAdapterViewHolder appAdapterViewHolder, int i) {
        App app = apps.get(i);
        appAdapterViewHolder.txtAppName.setText(app.getAppName());
        appAdapterViewHolder.switchAppState.setChecked(app.isBlocked());
        appAdapterViewHolder.txtAppBackground.setText(getFirstCharacters(app.getAppName()));
        appAdapterViewHolder.txtAppBackground.setBackground(getBackground());


    }

    @Override
    public int getItemCount() {
        return apps.size();
    }

    private Drawable getBackground() {
        Drawable drawable = DrawableCompat.wrap(context.getResources().getDrawable(R.drawable.app_background).mutate());
        DrawableCompat.setTint(drawable, getRandomColor());
        return drawable;
    }

    private int getRandomColor() {
        int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
        return color;
    }

    private String getFirstCharacters(String appName) {
        StringBuilder firstCharacters = new StringBuilder();
        for (String word : appName.split(" ")) {
            if (Character.isAlphabetic(word.charAt(0)))
                firstCharacters.append(word.charAt(0));
        }

        return firstCharacters.toString().toUpperCase();
    }
}
