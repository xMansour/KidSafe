package com.mansourappdevelopment.androidapp.kidsafe.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.interfaces.OnConfirmationListener;
import com.mansourappdevelopment.androidapp.kidsafe.utils.Constant;

public class ConfirmationFragment extends DialogFragment {
    private Button btnConfirm;
    private Button btnCancelConfirm;
    private TextView txtConfirmationHeader;
    private OnConfirmationListener onConfirmationListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_confirmation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onConfirmationListener = (OnConfirmationListener) getActivity();
        Bundle bundle = getArguments();
        String confirmationMessage = bundle.getString(Constant.CONFIRMATION_MESSAGE);

        txtConfirmationHeader = (TextView) view.findViewById(R.id.txtConfirmationHeader);
        btnConfirm = (Button) view.findViewById(R.id.btnConfirm);
        btnCancelConfirm = (Button) view.findViewById(R.id.btnCancelConfirm);


        txtConfirmationHeader.setText(confirmationMessage);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConfirmationListener.onConfirm();
                dismiss();
            }

        });

        btnCancelConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConfirmationListener.onConfirmationCancel();
                dismiss();
            }
        });

    }
}
