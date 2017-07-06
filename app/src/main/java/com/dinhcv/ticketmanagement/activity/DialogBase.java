/*
 * DialogBase.java
 * Dialog base

 * Author  : tupn
 * Created : 2/16/2016
 * Modified: $Date: 2016-09-01 17:56:45 +0700 (Thu, 01 Sep 2016) $

 * Copyright © 2015 www.mdi-astec.vn
 **************************************************************************************************/

package com.dinhcv.ticketmanagement.activity;

import android.content.Context;
import android.support.v7.app.AppCompatDialog;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

import com.dinhcv.ticketmanagement.utils.Debug;

/**
 * Dialog base
 */
class DialogBase extends AppCompatDialog {

    public DialogBase(Context context) {
        super(context);
    }

    public DialogBase(Context context, int style) {
        super(context, style);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // In order to not be too narrow, set the window size based on the screen resolution:
        final int screenWidth    = getContext().getResources().getDisplayMetrics().widthPixels;
        final int newScreenWidth = screenWidth * 90 / 100;
        WindowManager.LayoutParams layout = getWindow().getAttributes();
        layout.width = Math.max(layout.width, newScreenWidth );
        getWindow().setAttributes(layout);

        TextView textView = (TextView) findViewById(android.R.id.title);

        if(textView != null) {
            Debug.normal("Set title dialog ----------------------------------");
            textView.setSingleLine(false);
            textView.setGravity(Gravity.CENTER);
        }
    }
}
/***************************************************************************************************
 * End of file
 **************************************************************************************************/
