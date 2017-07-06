package com.dinhcv.ticketmanagement.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.dinhcv.ticketmanagement.R;
import com.dinhcv.ticketmanagement.model.Settings;
import com.dinhcv.ticketmanagement.model.UserModel;
import com.dinhcv.ticketmanagement.model.structure.UserInfo;
import com.dinhcv.ticketmanagement.utils.Debug;

import java.util.List;

public class UserListActivity extends AppCompatActivity {

    private UserModel mUserModel;
    private ListView mListView;
    private Dialog dialogDetail;
    private TextView tv_userTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        mUserModel = new UserModel();

        initView();

        initData();

    }

    private void initView(){

        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar_top);
        toolBar.setTitle(getString(R.string.user_manager_title));
        toolBar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolBar);
        toolBar.showOverflowMenu();

        ImageButton btn_addUser = (ImageButton) findViewById(R.id.btn_addUser);
        tv_userTotal = (TextView) findViewById(R.id.tv_userTotal);
        mListView = (ListView) findViewById(R.id.lv_user);

        btn_addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });

    }


    /**
     * Get account data
     */
    private void initData() {

        List<UserInfo> accountInfoList = mUserModel.getUserList();
        if ((accountInfoList != null) && (accountInfoList.size() >0)) {
            tv_userTotal.setText(String.valueOf(accountInfoList.size()));
            AccountAdapter accountAdapter = new AccountAdapter(UserListActivity.this, accountInfoList);
            mListView.setAdapter(accountAdapter);
        }

    }


    public class AccountAdapter extends BaseAdapter {
        private List<UserInfo> mListAccount;
        private Context mContext;

        AccountAdapter(Context context, List<UserInfo> list) {
            this.mContext = context;
            mListAccount = list;
        }

        @Override
        public int getCount() {
            return mListAccount.size();
        }

        @Override
        public Object getItem(int position) {
            return mListAccount.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.activity_user_list_row, null, false);
            }

            final UserInfo accountInfo = mListAccount.get(position);
            if (accountInfo == null) {
                return null;
            }


            final TextView tv_no = ((TextView) convertView.findViewById(R.id.tv_no));
            final TextView tv_account = ((TextView) convertView.findViewById(R.id.tv_account));
            final TextView tv_name= ((TextView) convertView.findViewById(R.id.tv_name));
            final TextView tv_working = ((TextView) convertView.findViewById(R.id.tv_workingShift));
            final Button btn_delete = ((Button) convertView.findViewById(R.id.btn_delete));

            int count = parent.getChildCount();

            //set other background by odd/even
            convertView.setBackgroundResource((count % 2 == 0) ?
                    R.drawable.table_background_row_even_selector :
                    R.drawable.table_background_row_odd_selector);

            //set data
            tv_no.setText(String.valueOf(position + 1));
            tv_account.setText(accountInfo.getAccount());
            tv_name.setText(accountInfo.getName());
            String permission = getResources().getString(R.string.look_car);
            if (accountInfo.getPermission() == 1 ){
                permission = getResources().getString(R.string.manager);
            }
            tv_working.setText(permission);


            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoAccountDetail(accountInfo);
                }
            });

            //handle event
            btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(UserListActivity.this);
                    alert.setTitle(R.string.delete_confirm_title);
                    alert.setMessage(R.string.delete_confirm);
                    alert.setIcon(R.drawable.dialog_warning_icon);
                    alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            deleteAccount(accountInfo);
                        }
                    });
                    alert.setNegativeButton(R.string.cancel, null);
                    alert.show();
                }
            });


            return convertView;
        }
    }

    private void gotoAccountDetail(UserInfo accountInfo) {
        dialogDetail = new UserDetailDialogBuilder().create(UserListActivity.this, accountInfo, new UserDetailDialogBuilder.OnRereadingStatusListener() {
            @Override
            public void onRereadingStatus(int id, String account, String password, String name, int working, int sex) {
                if (account == null) {
                    dialogDetail.dismiss();
                }

                Debug.normal("String account: " + account + " name: " + name +" permission: "+sex);
                // Save reinspect comment information
                saveAccountInfo( id, account, password, name, working, sex);
            }
        });

        dialogDetail.show();
    }

    private void saveAccountInfo(int id, String account, String password, String name, int working, int permission) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(id);
        userInfo.setAccount(account);
        userInfo.setPassword(password);
        userInfo.setName(name);
        userInfo.setWorkingShift(working);
        userInfo.setPermission(permission);

        boolean save = mUserModel.saveUser(userInfo);
        if (!save) {
            Debug.error("ERROR: Can not save account info");
        }
        //load data
        initData();
    }

    /**
     * Delete account
     *
     * @param accountInfo account info
     */
    private void deleteAccount(UserInfo accountInfo) {

        String currentUser = Settings.getUsername();
        if (currentUser.equals(accountInfo.getAccount())){
            AlertDialog.Builder alert = new AlertDialog.Builder(UserListActivity.this);
            alert.setTitle(R.string.error_title);
            alert.setMessage(R.string.canot_delete);
            alert.setIcon(R.drawable.dialog_warning_icon);
            alert.setPositiveButton(R.string.ok, null);
            alert.show();

            return;
        }


        boolean status = mUserModel.deleteAccount(accountInfo.getId());
        if (!status) {
            Debug.error("Can not delete account infomation");
        }

        //load data
        initData();
    }

    private void createAccount() {
        dialogDetail = new UserDetailDialogBuilder().create(UserListActivity.this, null, new UserDetailDialogBuilder.OnRereadingStatusListener() {
            @Override
            public void onRereadingStatus(int id, String account, String password, String name, int working, int permission) {
                if (account == null) {
                    dialogDetail.dismiss();
                }

                Debug.normal("String account: " + account + " name: " + name);
                // Save reinspect comment information
                saveAccountInfo(id, account, password, name, working, permission);
            }
        });

        dialogDetail.show();
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
