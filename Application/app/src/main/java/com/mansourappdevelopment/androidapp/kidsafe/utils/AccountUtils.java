package com.mansourappdevelopment.androidapp.kidsafe.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.activities.LoginActivity;
import com.mansourappdevelopment.androidapp.kidsafe.models.User;

public class AccountUtils {

    public static void changePassword(final Context context) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.change_password)
                .setMessage(R.string.do_you_want_to_change_password)
                //.setIcon(R.drawable.ic_home_)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LinearLayout parentLayout = new LinearLayout(context);
                        parentLayout.setOrientation(LinearLayout.VERTICAL);

                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        layoutParams.setMargins(dpToPx(20), dpToPx(20), dpToPx(20), dpToPx(20));

                        final EditText txtOldPassword = new EditText(context);
                        txtOldPassword.setHint(R.string.enter_old_password);
                        txtOldPassword.setLayoutParams(layoutParams);
                        txtOldPassword.setSingleLine();
                        txtOldPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

                        final EditText txtNewPassword = new EditText(context);
                        txtNewPassword.setHint(R.string.enter_new_password);
                        txtNewPassword.setLayoutParams(layoutParams);
                        txtNewPassword.setSingleLine();
                        txtNewPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);


                        parentLayout.addView(txtOldPassword, 0);
                        parentLayout.addView(txtNewPassword, 1);

                        new AlertDialog.Builder(context)
                                .setTitle(R.string.change_password)
                                //.setMessage(R.string.enter_your_password)
                                .setView(parentLayout)
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (isValid(txtOldPassword.getText().toString(), context)) {
                                            if (Validators.isValidPassword(txtNewPassword.getText().toString()))
                                                updatePassword(txtNewPassword.getText().toString(), context);
                                            else
                                                Toast.makeText(context, R.string.enter_a_valid_new_email, Toast.LENGTH_SHORT).show();

                                        } else {
                                            Toast.makeText(context, R.string.old_password_is_wrong, Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                })
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(context, R.string.canceled, Toast.LENGTH_SHORT).show();
                                    }
                                }).create().show();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, R.string.canceled, Toast.LENGTH_SHORT).show();
                    }
                }).create().show();

    }

    public static void logout(final Context context) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.logout)
                .setMessage(R.string.do_you_want_to_logout)
                //.setIcon(R.drawable.ic_home_)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        context.startActivity(new Intent(context, LoginActivity.class));
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, R.string.canceled, Toast.LENGTH_SHORT).show();
                    }
                }).create().show();

    }

    public static void deleteAccount(final Context context) {
        final String providerId = FirebaseAuth.getInstance().getCurrentUser().getProviders().get(0);
        new AlertDialog.Builder(context)
                .setTitle(R.string.delete_account)
                .setMessage(R.string.do_you_want_to_delete_account)
                //.setIcon(R.drawable.ic_home_)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (providerId.equals("google.com")) {
                            deleteAccountData(providerId, null, context);
                        } else {
                            LinearLayout parentLayout = new LinearLayout(context);
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                            layoutParams.setMargins(dpToPx(20), dpToPx(20), dpToPx(20), dpToPx(20));
                            final EditText txtPassword = new EditText(context);
                            txtPassword.setHint(R.string.enter_your_password);
                            txtPassword.setLayoutParams(layoutParams);
                            txtPassword.setSingleLine();
                            parentLayout.addView(txtPassword);
                            new AlertDialog.Builder(context)
                                    .setTitle(R.string.delete_account)
                                    //.setMessage(R.string.enter_your_password)
                                    .setView(parentLayout)
                                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String password = txtPassword.getText().toString();
                                            if (isValid(password, context)) {
                                                deleteAccountData(providerId, password, context);

                                            } else {
                                                Toast.makeText(context, R.string.wrong_password, Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    })
                                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(context, R.string.canceled, Toast.LENGTH_SHORT).show();
                                        }
                                    }).create().show();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, R.string.canceled, Toast.LENGTH_SHORT).show();
                    }
                }).create().show();
    }

    private static void deleteAccountData(final String providerId, final String password, final Context context) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Query query = databaseReference.child("parents").orderByChild("email").equalTo(user.getEmail());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    databaseReference.child("parents").child(user.getUid()).removeValue();
                } else {
                    databaseReference.child("childs").child(user.getUid()).removeValue();
                }

                DataSnapshot nodeShot = dataSnapshot.getChildren().iterator().next();
                User dbUser = nodeShot.getValue(User.class);
                String imgUrl = dbUser.getProfileImage();
                if (imgUrl.contains("https://firebasestorage.googleapis.com")) {
                    StorageReference profileImageStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imgUrl);
                    profileImageStorageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            deleteUser(providerId, password, context);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
                } else {
                    deleteUser(providerId, password, context);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private static void deleteUser(String providerId, final String password, final Context context) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential;
        if (providerId.equals("google.com"))
            credential = GoogleAuthProvider.getCredential(user.getEmail(), null);

        else
            credential = EmailAuthProvider.getCredential(user.getEmail(), password);

        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        user.delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(context, R.string.account_deleted, Toast.LENGTH_SHORT).show();
                                        context.startActivity(new Intent(context, LoginActivity.class));
                                    }
                                });
                    }
                });
    }

    private static void updatePassword(String newPassword, final Context context) {
        FirebaseAuth.getInstance().getCurrentUser().updatePassword(newPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(context, R.string.password_updated, Toast.LENGTH_SHORT).show();
                        context.startActivity(new Intent(context, LoginActivity.class));
                    }
                });
    }

    private static boolean isValid(String password, final Context context) {
        String savedPassword = SharedPrefsUtils.getStringPreference(context, Constant.PASSWORD);
        return password.equals(savedPassword);

    }

    private static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}
