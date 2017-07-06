package com.dinhcv.ticketmanagement.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.print.sdk.PrinterInstance;
import com.dinhcv.ticketmanagement.R;
import com.dinhcv.ticketmanagement.TicketManagermentApplication;
import com.dinhcv.ticketmanagement.model.LogModel;
import com.dinhcv.ticketmanagement.model.Settings;
import com.dinhcv.ticketmanagement.model.database.entities.m_setting_block;
import com.dinhcv.ticketmanagement.model.structure.TicketInfo;
import com.dinhcv.ticketmanagement.printer.PrintUtils;
import com.dinhcv.ticketmanagement.utils.Debug;
import com.dinhcv.ticketmanagement.utils.Utils;

import java.io.File;
import java.io.StringReader;
import java.util.Set;

public class ConfigActivity extends AppCompatActivity {
    private TextView tv_time1;
    private TextView tv_time2;
    private TextView tv_money1;
    private TextView tv_money2;
    private Button btn_save;
    private Button btn_cancel;


    private TextView edt_parking, edt_address, edt_hotline, edt_website;

    private LogModel mLogModel;
    private m_setting_block settingBlock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mLogModel = new LogModel();


        initView();


        initData();


    }

    private void initData(){

        settingBlock = mLogModel.getSettingBlock();
        if (settingBlock != null) {
            // update view

            tv_time1.setText(String.valueOf(settingBlock.time1));
            tv_time2.setText(String.valueOf(settingBlock.time2));
            tv_money1.setText(String.valueOf(settingBlock.money1));
            tv_money2.setText(String.valueOf(settingBlock.money2));

        }

        edt_parking.setText(Settings.getParking());
        edt_address.setText(Settings.getAddress());
        edt_hotline.setText(Settings.getHotline());
        edt_website.setText(Settings.getWebsite());

    }

    private void initView(){
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar_top);
        toolBar.setTitle(getString(R.string.config));
        toolBar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolBar);
        toolBar.showOverflowMenu();

        tv_time1 = (TextView) findViewById(R.id.tv_time1);
        tv_money1 = (TextView) findViewById(R.id.tv_money1);
        tv_time2 = (TextView) findViewById(R.id.tv_time2);
        tv_money2 = (TextView) findViewById(R.id.tv_money2);


        edt_parking = (EditText) findViewById(R.id.edt_parking);
        edt_address = (EditText) findViewById(R.id.edt_address);
        edt_hotline = (EditText) findViewById(R.id.edt_hotline);
        edt_website = (EditText) findViewById(R.id.edt_website);

        btn_save = (Button) findViewById(R.id.btn_save);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);

        Button btn_log = (Button) findViewById(R.id.btn_log);
        Button btn_about = (Button) findViewById(R.id.btn_about);

        btn_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ConfigActivity.this, LogActivity.class);
                startActivity(intent);

            }
        });

        btn_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dialog dialog = new AboutDialogBuilder().create(ConfigActivity.this);
                dialog.show();

            }
        });


        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // update block
                updateBlock();

            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    private void updateBlock(){

        String time1  = tv_time1.getText().toString().trim();
        String time2 = tv_time1.getText().toString().trim();
        String money1 = tv_money1.getText().toString().trim();
        String money2 = tv_money2.getText().toString().trim();

        // ticket infor
        String parking  = edt_parking.getText().toString().trim();
        String address = edt_address.getText().toString().trim();
        String hotline = edt_hotline.getText().toString().trim();
        String website = edt_website.getText().toString().trim();

        if ((time1.isEmpty()) || (money1.isEmpty())) {
            AlertDialog.Builder alert = new AlertDialog.Builder(ConfigActivity.this);
            alert.setTitle(   R.string.error_title );
            alert.setMessage( R.string.block1_null );
            alert.setIcon(    R.drawable.dialog_warning_icon );
            alert.setPositiveButton(R.string.ok, null );
            alert.show();
            return;
        }

        if ((time2.isEmpty()) || (money2.isEmpty())) {
            AlertDialog.Builder alert = new AlertDialog.Builder(ConfigActivity.this);
            alert.setTitle(   R.string.error_title );
            alert.setMessage( R.string.block2_null );
            alert.setIcon(    R.drawable.dialog_warning_icon );
            alert.setPositiveButton(R.string.ok, null );
            alert.show();
            return;
        }


        // update setting block
        if (settingBlock == null){
            AlertDialog.Builder alert = new AlertDialog.Builder(ConfigActivity.this);
            alert.setTitle(   R.string.error_title );
            alert.setMessage( R.string.setting_block_null );
            alert.setIcon(    R.drawable.dialog_warning_icon );
            alert.setPositiveButton(R.string.ok, null );
            alert.show();
            return;
        }

        settingBlock.time1 = Integer.valueOf(time1);
        settingBlock.time2 = Integer.valueOf(time2);
        settingBlock.money1 = Integer.valueOf(money1);
        settingBlock.money2 = Integer.valueOf(money2);

        boolean isSuccess = mLogModel.saveSettingBlock(settingBlock);

        // Tiket infor
        Settings.setParking(parking);
        Settings.setAddress(address);
        Settings.setHotline(hotline);
        Settings.setWebsite(website);

        if (isSuccess){
            Toast.makeText(ConfigActivity.this, R.string.save_setting_block_success, Toast.LENGTH_SHORT).show();
        }else {
            AlertDialog.Builder alert = new AlertDialog.Builder(ConfigActivity.this);
            alert.setTitle(   R.string.error_title );
            alert.setMessage( R.string.save_setting_block_error );
            alert.setIcon(    R.drawable.dialog_warning_icon );
            alert.setPositiveButton(R.string.ok, null );
            alert.show();
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
