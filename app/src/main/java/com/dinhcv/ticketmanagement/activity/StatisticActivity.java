package com.dinhcv.ticketmanagement.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.dinhcv.ticketmanagement.R;
import com.dinhcv.ticketmanagement.model.TicketModel;
import com.dinhcv.ticketmanagement.model.structure.StatisticInfo;
import com.dinhcv.ticketmanagement.model.structure.TicketInfo;
import com.dinhcv.ticketmanagement.utils.Debug;
import com.dinhcv.ticketmanagement.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class StatisticActivity extends AppCompatActivity {


    private Date mFromDate;
    private Date mToDate;
    private Date TODAY;
    private Calendar mCalendar;

    private int mAnaYear;
    private int mAnaMonth;
    private int mAnaDay;

    private EditText edtStartHour;
    private EditText edtEndHour;

    private int mStartHour = 0;
    private int mStartMinute = 0;
    private int mEndHour = 23;
    private int mEndMinute = 59;

    private int mToDay;
    private int mToMonth;
    private int mToYear;

    private TextView edt_dateFrom;
    private TextView edt_dateTo;
    private TextView tv_carInOnDay;
    private TextView tv_carOutOnDay;
    private TextView tv_revenueOnDay;
    private TextView tv_carinTotal;
    private TextView tv_caroutTotal;
    private Button btn_search;

    private TextView tv_revenueTotal;
    private TicketModel mTicketModel;

    private static final int PAGE_SIZE = 20;
    private final List<StatisticInfo> mStatisticList = new ArrayList<>();
    private boolean isTodayFirst = false;
    private long totalRevenue = 0;

    private long mRevenue = 0;

    private int mCarinTotal = 0;
    private int mCaroutTotal = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        mTicketModel = new TicketModel();

        initDateData();

        initView();

        initData();
    }

    private void initView() {

        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar_top);
        toolBar.setTitle(getString(R.string.statistic_title));
        toolBar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolBar);
        toolBar.showOverflowMenu();

        tv_carinTotal = (TextView) findViewById(R.id.tv_carinTotal);
        tv_caroutTotal = (TextView) findViewById(R.id.tv_caroutTotal);
        tv_revenueTotal = (TextView) findViewById(R.id.tv_revenueTotal);
        TextView tv_carTotal = (TextView) findViewById(R.id.tv_carTotal);
        tv_carInOnDay = (TextView) findViewById(R.id.tv_carInOnDay);
        tv_carOutOnDay = (TextView) findViewById(R.id.tv_carOutOnDay);
        tv_revenueOnDay = (TextView) findViewById(R.id.tv_revenueOnDay);
        edt_dateFrom = (TextView) findViewById(R.id.edt_dateFrom);
        edt_dateTo = (TextView) findViewById(R.id.edt_dateTo);
        edt_dateFrom.setKeyListener(null);
        edt_dateTo.setKeyListener(null);
        btn_search = (Button) findViewById(R.id.btn_search);

        tv_carTotal.setText(String.valueOf(countTicket()));
        countOnDay();

        edtStartHour = (EditText) findViewById(R.id.edt_timeFrom);
        edtEndHour = (EditText) findViewById(R.id.edt_timeTo);
        edtStartHour.setKeyListener(null);
        edtEndHour.setKeyListener(null);


        edtStartHour.setText(Utils.convertTimeToString(mStartHour, mStartMinute));
        edtEndHour.setText(Utils.convertTimeToString(mEndHour, mEndMinute));

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

        edtStartHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // open dialog

                handleStartTimePicker();
            }
        });

        edtEndHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // open dialog

                handleEndTimePicker();
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // handle search
                Debug.normal("Click search");
                handleSearchCar();
            }
        });

        // register event
    }

    private void handleSearchCar() {

        if (!checkTimeInvalid()) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(StatisticActivity.this);
            dialog.setTitle(getString(R.string
                    .error_title))
                    .setMessage(getString(R.string.set_time_invalid))
                    .setCancelable(true)
                    .setPositiveButton(getString(R.string.ok), null)
                    .show();
            return;
        }


        new LoadStatisticDataTask().execute();
    }

    private void initData() {

        //reload all data from index 0
        new LoadStatisticDataTask().execute();
    }


    private class LoadStatisticDataTask extends AsyncTask<Integer, Void, List<StatisticInfo>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<StatisticInfo> doInBackground(Integer... params) {

            if (isCancelled()) {
                Debug.normal("Skip load statistic data when system busy");
                return Collections.emptyList();
            }


            Debug.normal("Load data task");

            if (!isTodayFirst) {
                totalRevenue = mTicketModel.getRevenueToday(TODAY);
                isTodayFirst = true;
            }

            //load data
            List<StatisticInfo> found = mTicketModel.searchStatisticData(mFromDate, mToDate);
            if (found == null) {
                Debug.normal("Load to end position of database, Can not load more");
            }else {

                for (int i = 0; i < found.size(); i++){
                    mCarinTotal = found.get(i).getCountIn();
                    mCaroutTotal = found.get(i).getCountOut();
                }

            }

            mRevenue = mTicketModel.getRevenueStatisticData(mFromDate, mToDate);

            return found;
        }

        @Override
        protected void onPostExecute(List<StatisticInfo> statisticInfos) {
            super.onPostExecute(statisticInfos);
            if (isFinishing()) {
                return;
            }

            tv_revenueOnDay.setText(Utils.convertFeeToString(totalRevenue) + " vnd");
            if ((statisticInfos == null) || (statisticInfos.isEmpty())) {
                Debug.normal("Cannot load more statisticInfos");
                emptyLoad();
                return;
            }

            //add to view
            tv_revenueTotal.setText(Utils.convertFeeToString(mRevenue) + " Vnd");
            tv_carinTotal.setText(Utils.convertFeeToString(mCarinTotal) );
            tv_caroutTotal.setText(Utils.convertFeeToString(mCaroutTotal) );

        }
    }


    private void emptyLoad() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(StatisticActivity.this);

        dialog.setTitle(R.string.warning_title);
        dialog.setMessage(R.string.list_is_null);
        dialog.setIcon(R.drawable.dialog_warning_icon);
        dialog.setPositiveButton(R.string.ok, null);
        dialog.show();

    }


    private int countTicket() {
        int count = mTicketModel.getTotalCarInPark();
        Debug.normal("Num car in park: " + count);
        return count;
    }

    private void countOnDay() {
        int countCarin = mTicketModel.getTotalCarInOnDay(TODAY);
        int countCarout = mTicketModel.getTotalCarOutOnDay(TODAY);
        Debug.normal("Num car in On day: " + countCarin);
        Debug.normal("Num car out On day: " + countCarout);

        tv_carInOnDay.setText(String.valueOf(countCarin));
        tv_carOutOnDay.setText(String.valueOf(countCarout));

    }


    /**
     * Show from date picker dialog
     */
    private void handleFromDatePicker() {

        new DatePickerDialog(StatisticActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                mCalendar = Calendar.getInstance();
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, monthOfYear);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                mCalendar.set(Calendar.HOUR_OF_DAY, mStartHour);
                mCalendar.set(Calendar.MINUTE, mStartMinute);
                mCalendar.set(Calendar.SECOND, 0);
                mCalendar.set(Calendar.MILLISECOND, 0);
                mFromDate = mCalendar.getTime();
                mAnaYear = year;
                mAnaMonth = monthOfYear;
                mAnaDay = dayOfMonth;
                boolean isOk = checkTimeInvalid();
                if (isOk) edt_dateFrom.setText(Utils.convertOnlyDateToString(mFromDate));
            }
        }, mAnaYear, mAnaMonth, mAnaDay).show();

    }

    /**
     * Show from date picker dialog
     */
    private void handleToDatePicker() {

        new DatePickerDialog(StatisticActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                mCalendar = Calendar.getInstance();
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, monthOfYear);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                mCalendar.set(Calendar.HOUR_OF_DAY, mEndHour);
                mCalendar.set(Calendar.MINUTE, mEndMinute);
                mCalendar.set(Calendar.SECOND, 0);
                mCalendar.set(Calendar.MILLISECOND, 0);
                mToDate = mCalendar.getTime();
                mAnaYear = year;
                mAnaMonth = monthOfYear;
                mAnaDay = dayOfMonth;
                boolean isOk = checkTimeInvalid();
                if (isOk) edt_dateTo.setText(Utils.convertOnlyDateToString(mToDate));
            }
        }, mAnaYear, mAnaMonth, mAnaDay).show();

    }


    /**
     * Show date picker dialog
     */
    private void handleStartTimePicker() {

        new TimePickerDialog(StatisticActivity.this, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                mCalendar = Calendar.getInstance();
                mCalendar.setTime(mFromDate);
                mCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                mCalendar.set(Calendar.MINUTE, selectedMinute);
                mCalendar.set(Calendar.SECOND, 0);
                mCalendar.set(Calendar.MILLISECOND, 0);
                mFromDate = mCalendar.getTime();
                boolean isOk = checkTimeInvalid();
                if (isOk) edtStartHour.setText(Utils.convertTimeToString(selectedHour, selectedMinute));
            }
        }, mStartHour, mStartMinute, true).show();

    }

    /**
     * Show date picker dialog
     */
    private void handleEndTimePicker() {

        new TimePickerDialog(StatisticActivity.this, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                mCalendar = Calendar.getInstance();
                mCalendar.setTime(mToDate);
                mCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                mCalendar.set(Calendar.MINUTE, selectedMinute);
                mCalendar.set(Calendar.SECOND, 0);
                mCalendar.set(Calendar.MILLISECOND, 0);
                mToDate = mCalendar.getTime();

                boolean isOk = checkTimeInvalid();
                if (isOk) edtEndHour.setText(Utils.convertTimeToString(selectedHour, selectedMinute));

            }
        }, mEndHour, mEndMinute, true).show();

    }


    private void initDateData() {

        Calendar now = Calendar.getInstance();
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.HOUR_OF_DAY, 0);

        mAnaYear = now.get(Calendar.YEAR);
        mAnaMonth = now.get(Calendar.MONTH);
        mAnaDay = now.get(Calendar.DAY_OF_MONTH);

        TODAY = now.getTime();
        Debug.normal("Today: " + now);

        // set start date
        mCalendar = Calendar.getInstance();
        mCalendar.setTime(new Date());
        mCalendar.set(Calendar.HOUR_OF_DAY, mStartHour);
        mCalendar.set(Calendar.MINUTE, mStartMinute);
        mCalendar.set(Calendar.SECOND, 0);
        mCalendar.set(Calendar.MILLISECOND, 0);
        mFromDate = mCalendar.getTime();

        // set end date
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, mEndHour);
        cal.set(Calendar.MINUTE, mEndMinute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        mToDate = cal.getTime();

    }

    /**
     * Check time invalid
     */
    private boolean checkTimeInvalid() {

        long fromDate = mFromDate.getTime();
        long toDate = mToDate.getTime();

        long sub = toDate - fromDate;
        if (sub < 0) {
            Debug.error("Error setting time");
            AlertDialog.Builder dialog = new AlertDialog.Builder(StatisticActivity.this);
            dialog.setTitle(getString(R.string
                    .error_title))
                    .setMessage(getString(R.string.set_time_invalid))
                    .setCancelable(true)
                    .setPositiveButton(getString(R.string.ok), null)
                    .show();
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
