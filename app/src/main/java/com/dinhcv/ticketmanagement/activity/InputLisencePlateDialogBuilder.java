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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.dinhcv.ticketmanagement.R;
import com.dinhcv.ticketmanagement.model.structure.UserInfo;
import com.dinhcv.ticketmanagement.utils.Debug;


class InputLisencePlateDialogBuilder {
    private EditText edt_lisence;
    private Dialog mDialog = null;

    interface OnRereadingStatusListener{
        void onRereadingStatus(String lisence);
    }

    public Dialog create(Context cnt, final OnRereadingStatusListener listener)
    {
        //create the actual dialog
        mDialog = new DialogBase( cnt );

        mDialog.setTitle(R.string.input_lisence);

        mDialog.setCancelable(false);
        //override layout
        mDialog.setContentView(R.layout.dialog_input_lisence_plate);

        edt_lisence = (EditText) mDialog.findViewById(R.id.edt_lisence);
        mDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


        mDialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onRereadingStatus(null);
                    mDialog.dismiss();
                }
                mDialog.dismiss();
            }
        });

        final Button btnSave = (Button)mDialog.findViewById(R.id.btn_save);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lisence = edt_lisence.getText().toString().trim();
                Debug.normal("String lisence: "+ lisence);

                // get selected radio button from radioGroup

                if ( lisence.isEmpty() ) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(mDialog.getContext());

                    alert.setTitle(   R.string.error_title );
                    alert.setMessage( R.string.lisence_is_null );
                    alert.setIcon(    R.drawable.dialog_warning_icon );
                    alert.setPositiveButton(R.string.ok, null );
                    alert.show();
                    return;
                }


                if (listener != null) {
                    listener.onRereadingStatus(lisence);
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
