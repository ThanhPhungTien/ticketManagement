package com.dinhcv.ticketmanagement.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.dinhcv.ticketmanagement.R;
import com.dinhcv.ticketmanagement.model.DatabaseDummy;



public class WelcomeActivity extends AppCompatActivity implements Runnable {

    private final Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_main);

        //if( Debug.isDebugEnable() ){
            new GenerateDummyDataTask().execute();
        //}else {
            //   mHandler.postDelayed( this , 3000);
        //}
    }


    @Override
    protected void onDestroy() {

        mHandler.removeCallbacks( this );
        super.onDestroy();
    }

    @Override
    public void run() {
        final Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }


    class GenerateDummyDataTask extends AsyncTask<Void, Void, Void >{

        ProgressDialog progressDialog = null;
        @Override
        protected void onPreExecute() {

            progressDialog = new ProgressDialog( WelcomeActivity.this );

            progressDialog.setTitle("Loading...");

            progressDialog.setCancelable(false);
            progressDialog.show();

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            //dummy data
            DatabaseDummy.generateDummy( getApplicationContext() );

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            progressDialog.dismiss();

            mHandler.postDelayed( WelcomeActivity.this, 3000 );
        }
    }



}
