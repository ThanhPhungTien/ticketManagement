package com.dinhcv.ticketmanagement.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.print.sdk.PrinterInstance;
import com.dinhcv.ticketmanagement.R;
import com.dinhcv.ticketmanagement.TicketManagermentApplication;
import com.dinhcv.ticketmanagement.model.Settings;
import com.dinhcv.ticketmanagement.model.TicketModel;
import com.dinhcv.ticketmanagement.model.structure.TicketInfo;
import com.dinhcv.ticketmanagement.printer.IPrinterOpertion;
import com.dinhcv.ticketmanagement.printer.PrintUtils;
import com.dinhcv.ticketmanagement.utils.Debug;
import com.dinhcv.ticketmanagement.utils.Utils;

import java.io.File;
import java.util.Date;

public class CarInActivity extends AppCompatActivity {

    private TicketInfo mTicketInfo;
    private TicketModel mTicketModel;
    private PrinterInstance mPrinter;
    private String mImagePath = null;
    private ImageView image;
    private boolean isPrinted = false;
    private int lisenceCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_in);

        mTicketModel = new TicketModel();

        getParameter();

        if (mTicketInfo == null){
            Debug.error("Ticket info is null");
            return;
        }
        TicketManagermentApplication app = (TicketManagermentApplication) getApplication();
        mPrinter = app.getIPrinter();

        initView();


    }

    private void getParameter(){
        mTicketInfo = (TicketInfo) getIntent().getSerializableExtra(MainActivity.WHICH_TICKET);

    }

    private void initView(){
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar_top);
        toolBar.setTitle(getString(R.string.carin_title));
        toolBar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolBar);
        toolBar.showOverflowMenu();

        LinearLayout ll_carInPark = (LinearLayout) findViewById(R.id.ll_carInPark);
        TextView tv_lisence = (TextView) findViewById(R.id.tv_lisence);
        TextView tv_timeIn = (TextView) findViewById(R.id.tv_timeIn);
        TextView tv_timeIn1 = (TextView) findViewById(R.id.tv_timeIn1);
        ImageButton btn_printTicket = (ImageButton) findViewById(R.id.btn_printTicket);
        Button btn_recapture = (Button) findViewById(R.id.btn_recapture);
        if (isPrinted){
            btn_recapture.setEnabled(false);
        }

        if (mTicketInfo.getStatus() == 1){
            ll_carInPark.setVisibility(View.VISIBLE);
        }else {
            ll_carInPark.setVisibility(View.GONE);
        }


        image = (ImageView) findViewById(R.id.image);
        if (!Utils.isPantech() && !Utils.isOppo()) {
            Debug.normal("Rotation image 90");
            image.setRotation(90);
        }

        String manu = android.os.Build.MANUFACTURER;
        tv_lisence.setText(mTicketInfo.getLisencePlate() );
        String timein = Utils.convertDateInToString(mTicketInfo.getTimeIn());
        tv_timeIn.setText(timein);
        tv_timeIn1.setText(timein);

        String filePath = mTicketInfo.getCarInImagePath();
        mImagePath = filePath;
        Debug.normal("File path: "+filePath);
        File imgFile = new File(filePath);

        if(imgFile.exists()){

            getDropboxIMGSize(filePath);

            Debug.normal("File image exist" +imgFile.getAbsolutePath());
            Bitmap myBitmap = BitmapFactory.decodeFile(filePath);

            image.setImageBitmap(myBitmap);
        }

        btn_printTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPrinter != null) {
                    if (!isPrinted) {
                        Debug.normal("Save car new");
                        new SaveCarInTicketTask().execute();
                    }else {
                        Debug.normal("Dont Save car new. Only print");
                        PrintUtils.printTicket(CarInActivity.this.getResources(), mPrinter, mTicketInfo);
                    }

                }else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(CarInActivity.this);
                    alert.setTitle(R.string.error_title);
                    alert.setMessage(R.string.none_found);
                    alert.setIcon(R.drawable.dialog_warning_icon);
                    alert.setPositiveButton(R.string.ok, null);
                    alert.show();
                }

            }
        });


        btn_recapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CarInActivity.this, ReCaptureActivity.class);
                startActivityForResult(intent, 900);
            }
        });


    }



    private void getDropboxIMGSize(String file){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file, options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;

        Debug.normal("Image Width: "+ imageWidth +" Image height: "+ imageHeight);

    }


    private class SaveCarInTicketTask extends AsyncTask<String, Void, Boolean> {
        ProgressDialog progressDialog = null;
        @Override
        protected void onPreExecute() {

            progressDialog = new ProgressDialog( CarInActivity.this );

            progressDialog.setTitle("Loading...");

            progressDialog.setCancelable(false);
            progressDialog.show();

            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {

            lisenceCode = Utils.getLicenseCode(CarInActivity.this) + 1;
            Utils.setLicenseCode(CarInActivity.this, lisenceCode);
            String lisence = Utils.convertToStringAroundNumber(lisenceCode);

            mTicketInfo.setStatus(1);
            mTicketInfo.setTicketType(1);

            mTicketInfo.setCarInImagePath(mImagePath);
            mTicketInfo.setLisenceCode(lisence);
            boolean save = mTicketModel.saveTicket(mTicketInfo);
            if (!save){
                Debug.error("Can not save car in ticket ");
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean isOk) {
            super.onPostExecute(isOk);

            progressDialog.dismiss();

            if (isOk){
                if (mPrinter != null) {
                    PrintUtils.printTicket(CarInActivity.this.getResources(), mPrinter, mTicketInfo);

                    // carin success
                    AlertDialog.Builder alert = new AlertDialog.Builder(CarInActivity.this);
                    alert.setTitle(R.string.warning_title);
                    alert.setMessage(R.string.print_ticket_success);
                    alert.setIcon(R.drawable.dialog_info_icon);
                    alert.setPositiveButton(R.string.ok, null);
                    alert.show();

                    isPrinted = true;
                    initView();

                }else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(CarInActivity.this);
                    alert.setTitle(   R.string.error_title );
                    alert.setMessage( R.string.none_found );
                    alert.setIcon(    R.drawable.dialog_warning_icon );
                    alert.setPositiveButton(R.string.ok, null );
                    alert.show();
                }
            }else {
                AlertDialog.Builder alert = new AlertDialog.Builder(CarInActivity.this);
                alert.setTitle(   R.string.error_title );
                alert.setMessage( R.string.carout_save_error );
                alert.setIcon(    R.drawable.dialog_warning_icon );
                alert.setPositiveButton(R.string.ok, null );
                alert.show();
            }

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 900) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra(ReCaptureActivity.FILE_IMAGE_PATH);
                if (result != null){
                    mImagePath = result;

                    Debug.normal("File path: "+mImagePath);
                    File imgFile = new File(mImagePath);

                    if(imgFile.exists()){

                        getDropboxIMGSize(mImagePath);

                        Debug.normal("File image exist" +imgFile.getAbsolutePath());
                        Bitmap myBitmap = BitmapFactory.decodeFile(mImagePath);

                        image.setImageBitmap(myBitmap);


                    }
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.back_menu:
                finish();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);


    }


}
