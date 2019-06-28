package com.mansourappdevelopment.androidapp.kidsafe.interfaces;

import android.view.View;

import com.mansourappdevelopment.androidapp.kidsafe.models.User;

public interface OnChildClickListener {
    void onItemClick(/*View view, */int position);

    void onWebFilterClick(boolean checked, User child);

    void onBtnLockClick(boolean checked, User child);

    void onLockPhoneSet(int hours, int minutes);

    void onLockCanceled();

    //void onLockDismiss();


}
