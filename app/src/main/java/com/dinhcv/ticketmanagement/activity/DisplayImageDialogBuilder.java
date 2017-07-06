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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.dinhcv.ticketmanagement.R;
import com.dinhcv.ticketmanagement.utils.Debug;
import com.dinhcv.ticketmanagement.utils.Utils;

import java.io.File;


class DisplayImageDialogBuilder {
    private ImageView imv;
    private Dialog mDialog = null;


    public Dialog create(Context cnt, String filePath)
    {
        //create the actual dialog
        mDialog = new DialogBase( cnt );

        mDialog.setTitle(R.string.input_lisence);

        mDialog.setCancelable(false);
        //override layout
        mDialog.setContentView(R.layout.dialog_display_image);

        imv = (ImageView) mDialog.findViewById(R.id.imv);
        //imv.setRotation(90);

        Debug.normal("File path 1: "+filePath);
        File imgFile = new File(filePath);

        if(imgFile.exists()){

            Debug.normal("File image exist" +imgFile.getAbsolutePath());
            Bitmap myBitmap = BitmapFactory.decodeFile(filePath);

            if (!Utils.isPantech() && !Utils.isOppo()) {
                Debug.normal("Rotation image 90");
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                Bitmap bMap = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), matrix, true);

                imv.setImageBitmap(bMap);
            }else imv.setImageBitmap(myBitmap);


        }

        mDialog.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
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
