package com.mansourappdevelopment.androidapp.kidsafe.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.utils.Constant;

import de.hdodenhof.circleimageview.CircleImageView;

public class ModeSelectionActivity extends AppCompatActivity {
    private CircleImageView imgParentSignUp;
    private TextView txtParentSignUp;

    private CircleImageView imgChildSignUp;
    private TextView txtChildSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_selection);


        txtParentSignUp = (TextView) findViewById(R.id.txtParentSignUp);
        imgParentSignUp = (CircleImageView) findViewById(R.id.imgParentSignUp);
        imgParentSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startParentSignUp();
            }
        });


        txtChildSignUp = (TextView) findViewById(R.id.txtChildSignUp);
        imgChildSignUp = (CircleImageView)findViewById(R.id.imgChildSignUp);
        imgChildSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startChildSignUp();
            }
        });

    }

    private void startParentSignUp(){
        Intent intent = new Intent(this, SignUpActivity.class);
        intent.putExtra(Constant.PARENT_SIGN_UP, true);
        startActivity(intent);
    }

    private void startChildSignUp(){
        Intent intent = new Intent(this, SignUpActivity.class);
        intent.putExtra(Constant.PARENT_SIGN_UP, false);
        startActivity(intent);

    }
}
