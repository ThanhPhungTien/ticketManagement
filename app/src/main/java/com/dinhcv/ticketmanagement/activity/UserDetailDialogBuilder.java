/*
 * ReinspectCommentDialogBuilder.java
 * Reinspect comment dialog builder

 * Author  : tupn
 * Created : 2/16/2016
 * Modified: $Date: 2016-09-20 09:07:54 +0700 (Tue, 20 Sep 2016) $

 * Copyright © 2015 www.mdi-astec.vn
 **************************************************************************************************/

package com.dinhcv.ticketmanagement.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.dinhcv.ticketmanagement.R;
import com.dinhcv.ticketmanagement.model.structure.UserInfo;
import com.dinhcv.ticketmanagement.utils.Debug;


class UserDetailDialogBuilder {
    private EditText edt_account;
    private EditText edt_password;
    private EditText edt_name;
    //private EditText edt_workingShift;
    private RadioGroup radioGroup;
    private Dialog mDialog = null;
    private int mId = 0;

    interface OnRereadingStatusListener{
        void onRereadingStatus(int id, String account, String password, String name, int workingShift,  int permistion);
    }

    public Dialog create(Context cnt, UserInfo userInfo, final OnRereadingStatusListener listener)
    {
        //create the actual dialog
        mDialog = new DialogBase( cnt );

        mDialog.setTitle(R.string.user_infor);
        //override layout
        mDialog.setContentView(R.layout.dialog_add_account);

        final RadioGroup radioGroup = (RadioGroup) mDialog.findViewById(R.id.rdoGroup);
        edt_account = (EditText) mDialog.findViewById(R.id.edt_account);
        edt_password = (EditText) mDialog.findViewById(R.id.edt_password);
        edt_name = (EditText) mDialog.findViewById(R.id.edt_name);
        //edt_workingShift = (EditText) mDialog.findViewById(R.id.edt_workingShift);

        if (userInfo != null){
            mId = userInfo.getId();
            edt_account.setText(userInfo.getAccount());
            edt_password.setText(userInfo.getPassword());
            edt_name.setText(userInfo.getName());
            //edt_workingShift.setText(String.valueOf(userInfo.getWorkingShift()));

            ((RadioButton) radioGroup.getChildAt(userInfo.getPermission())).setChecked(true);


        }


        mDialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        final Button btnSave = (Button)mDialog.findViewById(R.id.btn_save);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = edt_account.getText().toString().trim();
                String password = edt_password.getText().toString().trim();
                String name = edt_name.getText().toString().trim();
                //String workingShift = edt_workingShift.getText().toString().trim();
                Debug.normal("String account: "+ account);

                // get selected radio button from radioGroup
                int selectedId = radioGroup.getCheckedRadioButtonId();
                View radioButton = radioGroup.findViewById(selectedId);
                int flag = radioGroup.indexOfChild(radioButton);
                Debug.normal("id radio selected: "+flag);
                // find the radiobutton by returned id

                if ( account.isEmpty() ) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(mDialog.getContext());

                    alert.setTitle(   R.string.error_title );
                    alert.setMessage( R.string.account_is_null );
                    alert.setIcon(    R.drawable.dialog_warning_icon );
                    alert.setPositiveButton(R.string.ok, null );
                    alert.show();
                    return;
                }

                if ( password.isEmpty() ) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(mDialog.getContext());

                    alert.setTitle(   R.string.error_title );
                    alert.setMessage( R.string.password_is_null );
                    alert.setIcon(    R.drawable.dialog_warning_icon );
                    alert.setPositiveButton(R.string.ok, null );
                    alert.show();
                    return;
                }

                if ( name.isEmpty() ) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(mDialog.getContext());

                    alert.setTitle(   R.string.error_title );
                    alert.setMessage( R.string.name_is_null );
                    alert.setIcon(    R.drawable.dialog_warning_icon );
                    alert.setPositiveButton(R.string.ok, null );
                    alert.show();
                    return;
                }

//                if ( workingShift.isEmpty() ) {
//                    AlertDialog.Builder alert = new AlertDialog.Builder(mDialog.getContext());
//
//                    alert.setTitle(   R.string.error_title );
//                    alert.setMessage( R.string.working_shift_is_null );
//                    alert.setIcon(    R.drawable.dialog_warning_icon );
//                    alert.setPositiveButton(R.string.ok, null );
//                    alert.show();
//                    return;
//                }

                int working =  1;//Integer.valueOf(workingShift);

                if (listener != null) {
                    listener.onRereadingStatus(mId, account, password, name, working, flag );
                    mDialog.dismiss();
                }
            }
        });

        return mDialog;
    }

}
/***************************************************************************************************
 * End of file
 **************************************************************************************************/
