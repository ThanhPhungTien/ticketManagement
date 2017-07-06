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
import android.widget.Button;
import android.widget.EditText;

import com.dinhcv.ticketmanagement.R;
import com.dinhcv.ticketmanagement.utils.Debug;


class AboutDialogBuilder {
    private EditText edt_lisence;
    private Dialog mDialog = null;

    public Dialog create(Context cnt)
    {
        //create the actual dialog
        mDialog = new DialogBase( cnt );

        mDialog.setTitle(R.string.about);

        mDialog.setCancelable(false);
        //override layout
        mDialog.setContentView(R.layout.dialog_about);

        mDialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDialog.dismiss();
            }
        });

        return mDialog;
    }

}
/***************************************************************************************************
 * End of file
 **************************************************************************************************/
