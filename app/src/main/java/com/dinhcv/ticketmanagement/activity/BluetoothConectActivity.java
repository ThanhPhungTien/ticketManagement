package com.dinhcv.ticketmanagement.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.android.print.sdk.PrinterConstants.Connect;
import com.android.print.sdk.PrinterInstance;
import com.dinhcv.ticketmanagement.R;
import com.dinhcv.ticketmanagement.TicketManagermentApplication;
import com.dinhcv.ticketmanagement.printer.BluetoothOperation;
import com.dinhcv.ticketmanagement.printer.IPrinterOpertion;

public class BluetoothConectActivity extends AppCompatActivity {
	private Context mContext;

	private static boolean isConnected;
	private IPrinterOpertion myOpertion;
	private PrinterInstance mPrinter;
	// Intent request codes
    public static final int CONNECT_DEVICE = 1;
    public static final int ENABLE_BT = 2;
	private boolean connect = false;

    private Button connectButton;

	private ProgressDialog dialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bluetooth_connect_activity_main);
		mContext = this;
		InitView();
	}

	private void InitView() {
		Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar_top);
		toolBar.setTitle(getString(R.string.connect_printer));
		toolBar.setLogo(R.mipmap.ic_launcher);
		setSupportActionBar(toolBar);
		toolBar.showOverflowMenu();

		connectButton = (Button) findViewById(R.id.connect);
		connectButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				openConn();
			}
		});

		dialog = new ProgressDialog(BluetoothConectActivity.this);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setTitle("Đang kết nối...");
		dialog.setMessage("Xin chờ trong ít phút...");
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);

		updateButtonState();
	}

	private void updateButtonState(){
		if(!isConnected)
		{
			String connStr = getResources().getString(R.string.connect_printer);
			connectButton.setText(connStr);
		}else{
			connectButton.setText(R.string.disconnect_printer);
		}

	}

	@Override
	protected void onActivityResult(final int requestCode, int resultCode, final Intent data) {
		switch (requestCode) {
        case CONNECT_DEVICE:
            if (resultCode == Activity.RESULT_OK) {
            	dialog.show();
            	new Thread(new Runnable(){
                    public void run() {
                    	myOpertion.open(data);
                    }
                }).start();
            }
        	break;
        case ENABLE_BT:
            if (resultCode == Activity.RESULT_OK){
            	myOpertion.chooseDevice();
            }else{
            	Toast.makeText(this, R.string.bt_not_enabled, Toast.LENGTH_SHORT).show();
            }
        }
	}

	private void openConn(){
		TicketManagermentApplication app = (TicketManagermentApplication) getApplication();
		if (!isConnected) {
			myOpertion = new BluetoothOperation(BluetoothConectActivity.this, mHandler);
			app.setIPrinterOpertion(myOpertion);
			myOpertion.chooseDevice();
			connect = true;
		} else {
			if (myOpertion == null){
				myOpertion= app.getIPrinterOpertion();
				isConnected = false;
				updateButtonState();
			}

			myOpertion.close();
			mPrinter = null;

			updateButtonState();
			app.setIPinter(mPrinter);
		}
	}

	private void updateButtonState1(){
		if(!connect)
		{
			String connStr = getResources().getString(R.string.connect_printer);
			connectButton.setText(connStr);
		}else{
			connectButton.setText(R.string.disconnect_printer);
		}

	}

	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Connect.SUCCESS:
				isConnected = true;
				mPrinter = myOpertion.getPrinter();
				TicketManagermentApplication app = (TicketManagermentApplication) getApplication();
				app.setIPinter(mPrinter);
				break;
			case Connect.FAILED:
				isConnected = false;
				Toast.makeText(mContext, "Lỗi kết nối...", Toast.LENGTH_SHORT).show();
				break;
			case Connect.CLOSED:
				isConnected = false;
				Toast.makeText(mContext, "Đóng kết nối...", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}

			updateButtonState();

			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
		}

	};

}