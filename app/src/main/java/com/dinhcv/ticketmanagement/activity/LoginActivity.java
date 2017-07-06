package com.dinhcv.ticketmanagement.activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dinhcv.ticketmanagement.R;
import com.dinhcv.ticketmanagement.model.LogModel;
import com.dinhcv.ticketmanagement.model.Settings;
import com.dinhcv.ticketmanagement.model.UserModel;
import com.dinhcv.ticketmanagement.model.structure.LogInfo;
import com.dinhcv.ticketmanagement.model.structure.UserInfo;
import com.dinhcv.ticketmanagement.utils.Debug;

import java.util.Date;

public class LoginActivity extends AppCompatActivity {

    private EditText edt_user;
    private EditText edt_password;
    private UserModel mUserModel;
    private LogModel mLogModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUserModel = new UserModel();
        mLogModel = new LogModel();

        String account = Settings.getUsername();
        String pass = Settings.getPassword();
        if ((account == null) || (pass == null)){
            initView();
        }else {
            Debug.normal("User is loging: "+Settings.getUsername());
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

    }


    private void initView(){
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar_top);
        toolBar.setTitle(getString(R.string.login_title));
        toolBar.setLogo(R.mipmap.ic_launcher);
        toolBar.showOverflowMenu();

        edt_user = (EditText) findViewById(R.id.edt_user);
        edt_password = (EditText) findViewById(R.id.edt_password);
        Button btnLogin = (Button) findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String user = edt_user.getText().toString();
                String pass = edt_password.getText().toString();

                // verify
                boolean isOk = verifyData(user, pass);
                if (!isOk){
                    return;
                }

                // login
                new LoginTask().execute(user, pass);

            }
        });

    }



    private boolean verifyData(String user, String pass){

        if (user.isEmpty()){
            AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);

            alert.setTitle(   R.string.error_title );
            alert.setMessage( R.string.account_is_null );
            alert.setIcon(    R.drawable.dialog_warning_icon );
            alert.setPositiveButton(R.string.ok, null );
            alert.show();
            return false;
        }

        if (pass.isEmpty()){
            AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);

            alert.setTitle(   R.string.error_title );
            alert.setMessage( R.string.password_is_null );
            alert.setIcon(    R.drawable.dialog_warning_icon );
            alert.setPositiveButton(R.string.ok, null );
            alert.show();
            return false;
        }

        return true;
    }

    class LoginTask extends AsyncTask<String, Void, Boolean > {

        ProgressDialog progressDialog = null;
        @Override
        protected void onPreExecute() {

            progressDialog = new ProgressDialog( LoginActivity.this );

            progressDialog.setTitle("Loading...");

            progressDialog.setCancelable(false);
            progressDialog.show();

            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String user = params[0];
            String pass = params[1];

            UserInfo userInfo = mUserModel.login(user, pass);
            if (userInfo == null){
                Debug.error("Login error");
                return false;
            }else {
                Settings.setUsername(userInfo.getAccount());
                Settings.setPassword(userInfo.getPassword());
                Settings.setCurrentUserid(String.valueOf(userInfo.getId()));
                Settings.settingPermission(userInfo.getPermission());

                // save log
                LogInfo logInfo = new LogInfo();
                logInfo.setUserid(userInfo.getId());
                logInfo.setAccount(userInfo.getAccount());
                Date date = new Date();
                logInfo.setTimein(date);
                logInfo.setTimeout(date);

                boolean savelog = mLogModel.saveLog(logInfo);
                Debug.normal("Status save log: "+savelog);
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean isLogin) {
            super.onPostExecute(isLogin);

            progressDialog.dismiss();

            if (isLogin){
                Debug.normal("Login success! User: "+Settings.getUsername());
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }else {
                AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);

                alert.setTitle(   R.string.login_error_title );
                alert.setMessage( R.string.login_error );
                alert.setIcon(    R.drawable.dialog_warning_icon );
                alert.setPositiveButton(R.string.ok, null );
                alert.show();
            }

        }
    }
}
