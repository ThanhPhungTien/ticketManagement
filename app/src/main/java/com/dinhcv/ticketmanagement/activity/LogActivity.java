package com.dinhcv.ticketmanagement.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.dinhcv.ticketmanagement.R;
import com.dinhcv.ticketmanagement.model.LogModel;
import com.dinhcv.ticketmanagement.model.structure.LogInfo;
import com.dinhcv.ticketmanagement.utils.Debug;
import com.dinhcv.ticketmanagement.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class LogActivity extends AppCompatActivity {


    private Date mFromDate;
    private Date mToDate;
    private Calendar mCalendar;

    private int mAnaYear;
    private int mAnaMonth;
    private int mAnaDay;

    private TextView edt_dateFrom;
    private TextView edt_dateTo;

    private LogModel mLogModel;

    private static final int PAGE_SIZE = 20;
    private final List<LogInfo> mLogList = new ArrayList<>();
    private Boolean mBusy = false;
    private int mPageNext = 0;
    private boolean mIsLoad = false;
    private ListView mListview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        mLogModel = new LogModel();

        initDateData();

        initView();

        initData();
    }

    private void initView(){

        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar_top);
        toolBar.setTitle(getString(R.string.search_title));
        toolBar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolBar);
        toolBar.showOverflowMenu();

        mListview = (ListView) findViewById(R.id.lv_ticket);
        Button btn_search = (Button) findViewById(R.id.btn_search);
        edt_dateFrom = (TextView) findViewById(R.id.edt_dateFrom);
        edt_dateTo = (TextView) findViewById(R.id.edt_dateTo);
        edt_dateFrom.setKeyListener(null);
        edt_dateTo.setKeyListener(null);
        edt_dateTo.setFocusable(false);
        edt_dateFrom.setFocusable(false);

        String fromDate = Utils.convertOnlyDateToString(mFromDate);
        String toDate = Utils.convertOnlyDateToString(mToDate);
        edt_dateFrom.setText(fromDate);
        edt_dateTo.setText(toDate);

        edt_dateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // open dialog

                handleFromDatePicker();
            }
        });

        edt_dateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // open dialog

                handleToDatePicker();
            }
        });


        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // handle search
                handleSearchCar();
            }
        });

        // register event
        registerEvent();
    }

    private void handleSearchCar(){

        // checkbox 2

        if (!checkTimeInvalid()) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(LogActivity.this);
            dialog.setTitle(getString(R.string
                    .error_title))
                    .setMessage(getString(R.string.set_time_invalid))
                    .setCancelable(true)
                    .setPositiveButton(getString(R.string.ok), null)
                    .show();
            return;
        }

        //load from beginning
        mPageNext = 0;
        mIsLoad = true;

        //delete all current view
        mLogList.clear();

        new LoadLogDataTask().execute(0, PAGE_SIZE);
    }

    private void initData(){
        //load from beginning
        mPageNext = 0;
        mIsLoad = true;

        //delete all current view
        mLogList.clear();

        //reload all data from index 0
        new LoadLogDataTask().execute(0, PAGE_SIZE);
    }

    private void registerEvent(){
        //load more when scrolling end of table
        mListview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //load more data
                int lastInScreen = firstVisibleItem + visibleItemCount;
                if(lastInScreen == totalItemCount) {
                    if (!mBusy) {
                        new LoadLogDataTask().execute(mPageNext, PAGE_SIZE);
                    }

                }
            }
        });
    }

    public class LogAdapter extends BaseAdapter {
        private List<LogInfo> mListLog;
        private Context mContext;

        LogAdapter(Context context, List<LogInfo> list) {
            this.mContext = context;
            mListLog = list;
        }

        @Override
        public int getCount() {
            return mListLog.size();
        }

        @Override
        public Object getItem(int position) {
            return mListLog.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.activity_log_row, null, false);
            }

            final LogInfo logInfo = mListLog.get(position);
            if (logInfo == null) {
                return null;
            }

            final TextView tv_no = ((TextView) convertView.findViewById(R.id.tv_no));
            final TextView tv_userId = (TextView) convertView.findViewById(R.id.tv_userId);
            final TextView tv_account= ((TextView) convertView.findViewById(R.id.tv_account));
            final TextView tv_timeIn = ((TextView) convertView.findViewById(R.id.tv_timeIn));
            final TextView tv_timeOut = ((TextView) convertView.findViewById(R.id.tv_timeOut));

            int count = parent.getChildCount();

            //set other background by odd/even
            convertView.setBackgroundResource((count % 2 == 0) ?
                    R.drawable.table_background_row_even_selector :
                    R.drawable.table_background_row_odd_selector);

            //set data
            tv_no.setText(String.valueOf(position + 1));

            tv_userId.setText(String.valueOf(logInfo.getUserid()));
            tv_account.setText(logInfo.getAccount());
            String timeIn = Utils.convertDateInToString(logInfo.getTimein());
            tv_timeIn.setText(timeIn);
            String timeOut = "-----";
            if (logInfo.getTimein().getTime() != logInfo.getTimeout().getTime()){
                timeOut = Utils.convertDateInToString(logInfo.getTimeout());
            }
            tv_timeOut.setText(timeOut);

            return convertView;
        }
    }



    private class LoadLogDataTask extends AsyncTask<Integer, Void, List<LogInfo>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mBusy || (!mIsLoad)) {
                this.cancel(false);
            }else {
                mBusy = true;
            }
        }

        @Override
        protected List<LogInfo> doInBackground(Integer... params) {

            if (isCancelled()) {
                Debug.normal("Skip load ticket when system busy");
                return Collections.emptyList();
            }

            //range of loading page
            int pageIndex = params[0];
            int pageSize = params[1];

            Debug.normal("Load log: pageIndex(%d) pageSize(%d)", pageIndex, pageSize);

            //load data
            List<LogInfo> found = mLogModel.getLogList(mFromDate, mToDate, pageIndex, pageSize);
            if ((found == null) || (found.size() < pageSize)) {
                Debug.normal("Load to end position of database, Can not load more");
                mIsLoad = false;
            }

            return found;
        }

        @Override
        protected void onPostExecute(List<LogInfo> logInfos) {
            super.onPostExecute(logInfos);
            if (isFinishing()) {
                return;
            }

            mBusy = false;

            if ((logInfos == null) || (logInfos.isEmpty())) {
                Debug.normal("Cannot load more logInfos");
                emptyLoad();
                return;
            }

            //next page index
            mPageNext++;

            mLogList.addAll(logInfos);
            //add to view
            addLogToListView(mLogList);
        }
    }


    private void addLogToListView(List<LogInfo> logInfos){

        if ((logInfos == null) || (logInfos.isEmpty())){
            Debug.normal("Log list is null");
            mListview.setAdapter(null);
            return;
        }

        // get listview current position - used to maintain scroll position
        int currentPosition = mListview.getLastVisiblePosition();

        LogAdapter listAdapter = new LogAdapter(getApplicationContext(), logInfos);
        mListview.setAdapter(listAdapter);

        // Setting new scroll position
        mListview.setSelectionFromTop( currentPosition + 1, 0);
    }

    private void emptyLoad() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(LogActivity.this);

        dialog.setTitle(R.string.error_title);
        dialog.setMessage(R.string.list_is_null);
        dialog.setIcon(R.drawable.dialog_warning_icon);
        dialog.setPositiveButton(R.string.ok, null);
        dialog.show();

    }


    /**
     * Show from date picker dialog
     */
    private void handleFromDatePicker() {

        new DatePickerDialog(LogActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                mCalendar = Calendar.getInstance();
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, monthOfYear);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                mCalendar.set(Calendar.HOUR_OF_DAY, 0);
                mCalendar.set(Calendar.MINUTE, 0);
                mCalendar.set(Calendar.SECOND, 0);
                mCalendar.set(Calendar.MILLISECOND, 0);
                mFromDate = mCalendar.getTime();
                mAnaYear = year;
                mAnaMonth = monthOfYear;
                mAnaDay = dayOfMonth;
                edt_dateFrom.setText(Utils.convertOnlyDateToString(mFromDate));
            }
        }, mAnaYear, mAnaMonth, mAnaDay).show();

    }

    /**
     * Show from date picker dialog
     */
    private void handleToDatePicker() {

        new DatePickerDialog(LogActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                mCalendar = Calendar.getInstance();
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, monthOfYear);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                mCalendar.set(Calendar.HOUR_OF_DAY, 23);
                mCalendar.set(Calendar.MINUTE, 59);
                mCalendar.set(Calendar.SECOND, 0);
                mCalendar.set(Calendar.MILLISECOND, 0);
                mToDate = mCalendar.getTime();
                mAnaYear = year;
                mAnaMonth = monthOfYear;
                mAnaDay = dayOfMonth;
                edt_dateTo.setText(Utils.convertOnlyDateToString(mToDate));
            }
        }, mAnaYear, mAnaMonth, mAnaDay).show();

    }


    private void initDateData(){

        Calendar now = Calendar.getInstance();
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.HOUR_OF_DAY, 0);

        mAnaYear = now.get(Calendar.YEAR);
        mAnaMonth = now.get(Calendar.MONTH);
        mAnaDay = now.get(Calendar.DAY_OF_MONTH);

        Debug.normal("Today: "+now);

        // set start date
        mCalendar = Calendar.getInstance();
        mCalendar.setTime(new Date());
        mCalendar.set(Calendar.HOUR_OF_DAY, 0);
        mCalendar.set(Calendar.MINUTE, 0);
        mCalendar.set(Calendar.SECOND, 0);
        mCalendar.set(Calendar.MILLISECOND, 0);
        mFromDate = mCalendar.getTime();

        // set end date
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        mToDate = cal.getTime();

    }

    /**
     * Check time invalid
     */
    private boolean checkTimeInvalid(){

        long fromDate = mFromDate.getTime();
        long toDate = mToDate.getTime();

        long sub = toDate - fromDate ;
        if (sub < 0) {
            Debug.error("Error setting time");
            return false;
        }

        return true;
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
