package com.mansourappdevelopment.androidapp.kidsafe.interfaces;

public interface OnPermissionExplanationListener {
    void onOk(int requestCode);

    void onCancel(int switchId);
}
