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
import com.mansourappdevelopment.androidapp.kidsafe.interfaces.OnPermissionExplainationListener;
import com.mansourappdevelopment.androidapp.kidsafe.utils.Constant;


public class PermissionExplanationDialogFragment extends DialogFragment {
    private TextView txtPermissionHeader;
    private Button btnOkPermission;
    private Button btnCancelPermission;
    private OnPermissionExplainationListener onPermissionExplainationListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_permission_explanation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        txtPermissionHeader = (TextView) view.findViewById(R.id.txtPermissionHeader);
        btnOkPermission = (Button) view.findViewById(R.id.btnOkPermission);
        btnCancelPermission = (Button) view.findViewById(R.id.btnCancelPermission);
        onPermissionExplainationListener = (OnPermissionExplainationListener) getTargetFragment();

        Bundle bundle = getArguments();
        final int requestCode = bundle.getInt(Constant.PERMISSION_REQUEST_CODE);
        final int switchId = bundle.getInt(Constant.SWITCH_ID);

        switch (requestCode) {
            case Constant.SEND_SMS_PERMISSION_REQUEST_CODE:
                txtPermissionHeader.setText(getString(R.string.send_sms_explanation));
                break;
            case Constant.READ_SMS_PERMISSION_REQUEST_CODE:
                txtPermissionHeader.setText(getString(R.string.read_sms_explanation));
                break;
            case Constant.RECEIVE_SMS_PERMISSION_REQUEST_CODE:
                txtPermissionHeader.setText(getString(R.string.receive_sms_explanation));
                break;
            case Constant.READ_PHONE_STATE_PERMISSION_REQUEST_CODE:
                txtPermissionHeader.setText(getString(R.string.read_phone_state_explanation));
                break;
            case Constant.READ_CALL_LOG_PERMISSION_REQUEST_CODE:
                txtPermissionHeader.setText(getString(R.string.read_call_log_explanation));
                break;
            case Constant.READ_CONTACTS_PERMISSION_REQUEST_CODE:
                txtPermissionHeader.setText(getString(R.string.read_contacts_explanation));
                break;
            case Constant.LOCATION_PERMISSION_REQUEST_CODE:
                txtPermissionHeader.setText(getString(R.string.location_permission_explanation));
                break;
            case Constant.WRITE_SETTINGS_PERMISSION_REQUEST_CODE:
                txtPermissionHeader.setText(getString(R.string.write_settings_permission_explanation));
                break;
            case Constant.OVERLAY_PERMISSION_REQUEST_CODE:
                txtPermissionHeader.setText(getString(R.string.overlay_permission_explanation));
                break;
            case Constant.PACKAGE_USAGE_PERMISSION_REQUEST_CODE:
                txtPermissionHeader.setText(getString(R.string.package_usage_permission_explanation));
                break;
        }

        btnOkPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPermissionExplainationListener.onOk(requestCode);
                dismiss();
            }
        });

        btnCancelPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPermissionExplainationListener.onCancel(switchId);
                dismiss();
            }
        });

    }
}
