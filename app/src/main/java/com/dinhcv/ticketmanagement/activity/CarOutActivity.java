package com.dinhcv.ticketmanagement.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.print.sdk.PrinterInstance;
import com.dinhcv.ticketmanagement.R;
import com.dinhcv.ticketmanagement.TicketManagermentApplication;
import com.dinhcv.ticketmanagement.model.LogModel;
import com.dinhcv.ticketmanagement.model.TicketModel;
import com.dinhcv.ticketmanagement.model.database.entities.m_setting_block;
import com.dinhcv.ticketmanagement.model.structure.TicketInfo;
import com.dinhcv.ticketmanagement.printer.PrintUtils;
import com.dinhcv.ticketmanagement.utils.Debug;
import com.dinhcv.ticketmanagement.utils.Utils;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.io.File;
import java.util.Date;

public class CarOutActivity extends AppCompatActivity {

    private TicketInfo mTicketInfo;
    private TicketModel mTicketModel;
    private TextView tv_lisence;
    private PrinterInstance mPrinter;
    private m_setting_block settingBlock;
    private LogModel mLogModel;

    private ImageView image1;
    private ImageView image2;

    private String filePath;
    private String filePath1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_out);

        mTicketModel = new TicketModel();
        mLogModel = new LogModel();


        getParameter();

        if (mTicketInfo == null) {
            Debug.error("Ticket info is null");
            return;
        }

        TicketManagermentApplication app = (TicketManagermentApplication) getApplication();
        mPrinter = app.getIPrinter();

        initView();


    }

    private void getParameter() {
        mTicketInfo = (TicketInfo) getIntent().getSerializableExtra(MainActivity.WHICH_TICKET);

        settingBlock = mLogModel.getSettingBlock();
        if (settingBlock == null) {
            Debug.error("Setting Block is null");
        }

    }

    private void initView() {
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar_top);
        toolBar.setTitle(getString(R.string.carout_title));
        toolBar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolBar);
        toolBar.showOverflowMenu();

        tv_lisence = (TextView) findViewById(R.id.tv_lisence);
        TextView tv_barcode = (TextView) findViewById(R.id.tv_barcode);
        TextView tv_timeIn = (TextView) findViewById(R.id.tv_timeIn);
        TextView tv_timeOut = (TextView) findViewById(R.id.tv_timeOut);
        TextView tv_revenue = (TextView) findViewById(R.id.tv_revenue);
        LinearLayout ll_timeTotal = (LinearLayout) findViewById(R.id.ll_timeTotal);
        TextView tv_timeTotal = (TextView) findViewById(R.id.tv_timeTotal);
        ImageButton btn_printBill = (ImageButton) findViewById(R.id.btn_printBill);

        TextView tv_timeIn1 = (TextView) findViewById(R.id.tv_timeIn1);
        TextView tv_timeOut1 = (TextView) findViewById(R.id.tv_timeOut1);

        image1 = (ImageView) findViewById(R.id.image1);
        image2 = (ImageView) findViewById(R.id.image2);
        if (!Utils.isPantech() && !Utils.isOppo()) {
            Debug.normal("Rotation image 90");
            image1.setRotation(90);
            image2.setRotation(90);
        }

        tv_timeTotal.setText(Utils.getTotalTime(mTicketInfo.getTimeIn(), mTicketInfo.getTimeOut()));



        tv_lisence.setText(mTicketInfo.getLisencePlate());
        tv_barcode.setText(mTicketInfo.getLisenceCode());
        String timein = Utils.convertDateInToString(mTicketInfo.getTimeIn());
        String timeout = Utils.convertDateInToString(mTicketInfo.getTimeOut());
        tv_revenue.setText(Utils.convertFeeToString(mTicketInfo.getFee()));
        tv_timeIn.setText(timein);
        tv_timeIn1.setText(timein);
        tv_timeOut.setText(timeout);
        tv_timeOut1.setText(timeout);

        filePath = mTicketInfo.getCarInImagePath();
        Debug.normal("File path: " + filePath);
        File imgFile = new File(filePath);

        if (imgFile.exists()) {

            Debug.normal("File image exist" + imgFile.getAbsolutePath());
            Bitmap myBitmap = BitmapFactory.decodeFile(filePath);

            image1.setImageBitmap(myBitmap);
        }


        filePath1 = mTicketInfo.getCarOutImagePath();
        Debug.normal("File path 2: " + filePath1);
        File imgFile1 = new File(filePath1);

        if (imgFile1.exists()) {

            Debug.normal("File image exist" + imgFile1.getAbsolutePath());
            Bitmap myBitmap1 = BitmapFactory.decodeFile(filePath1);

            image2.setImageBitmap(myBitmap1);
        }


        btn_printBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //if (mPrinter != null) {
                    new SaveCarOutTicketTask().execute();
//                } else {
//                    AlertDialog.Builder alert = new AlertDialog.Builder(CarOutActivity.this);
//                    alert.setTitle(R.string.error_title);
//                    alert.setMessage(R.string.none_found);
//                    alert.setIcon(R.drawable.dialog_warning_icon);
//                    alert.setPositiveButton(R.string.ok, null);
//                    alert.show();
//                }
            }
        });


        handleEvent();
    }

    private void handleEvent(){
        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayImage(filePath);
            }
        });

        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filePath1 != null){
                    displayImage(filePath1);
                }
            }
        });

    }

    private void displayImage(String filePath){

        Dialog dialog = new DisplayImageDialogBuilder().create(CarOutActivity.this, filePath);
        dialog.show();
    }

    private void initData() {
        String filePath = mTicketInfo.getCarOutImagePath();
        Bitmap bMap = BitmapFactory.decodeFile(filePath);
        String contents = null;

        int[] intArray = new int[bMap.getWidth() * bMap.getHeight()];
        bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());

        LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Reader reader = new MultiFormatReader();
        Result result = null;
        try {
            result = reader.decode(bitmap);
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }

        if (result != null) {
            contents = result.getText();
        }

        Debug.normal("Content image: " + contents);

    }


    private class SaveCarOutTicketTask extends AsyncTask<String, Void, Boolean> {
        ProgressDialog progressDialog = null;

        @Override
        protected void onPreExecute() {

            progressDialog = new ProgressDialog(CarOutActivity.this);

            progressDialog.setTitle("Loading...");

            progressDialog.setCancelable(false);
            progressDialog.show();

            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {

            mTicketInfo.setStatus(2);
            mTicketInfo.setTicketType(2);

            boolean save = mTicketModel.saveTicket(mTicketInfo);
            if (!save) {
                Debug.error("Can not save car out ticket ");
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean isOk) {
            super.onPostExecute(isOk);

            progressDialog.dismiss();

            if (isOk) {

                if (mPrinter == null) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(CarOutActivity.this);
                    alert.setTitle(R.string.warning_title);
                    alert.setMessage(R.string.cannot_print);
                    alert.setIcon(R.drawable.dialog_info_icon);
                    alert.setPositiveButton(R.string.ok, null);
                    alert.show();

                } else {
                    PrintUtils.printBill(CarOutActivity.this.getResources(), mPrinter, mTicketInfo);

                    AlertDialog.Builder alert = new AlertDialog.Builder(CarOutActivity.this);
                    alert.setTitle(   R.string.warning_title );
                    alert.setMessage( R.string.print_bill_success );
                    alert.setIcon(    R.drawable.dialog_info_icon );
                    alert.setPositiveButton(R.string.ok, null );
                    alert.show();
                }

                initView();
            } else {
                AlertDialog.Builder alert = new AlertDialog.Builder(CarOutActivity.this);
                alert.setTitle(R.string.error_title);
                alert.setMessage(R.string.carout_save_error);
                alert.setIcon(R.drawable.dialog_warning_icon);
                alert.setPositiveButton(R.string.ok, null);
                alert.show();
            }

        }
    }


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
