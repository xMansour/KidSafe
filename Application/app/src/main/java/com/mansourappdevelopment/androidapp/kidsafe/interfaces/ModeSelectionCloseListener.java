package com.mansourappdevelopment.androidapp.kidsafe.interfaces;

import android.content.DialogInterface;

public interface ModeSelectionCloseListener {

    public void onModeSelectionClose(DialogInterface dialogInterface);

    public void sendUserData(String parentEmail, boolean isChild);
}
