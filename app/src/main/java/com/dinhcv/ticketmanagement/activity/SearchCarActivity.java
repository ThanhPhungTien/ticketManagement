package com.dinhcv.ticketmanagement.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.dinhcv.ticketmanagement.R;
import com.dinhcv.ticketmanagement.model.TicketModel;
import com.dinhcv.ticketmanagement.model.structure.TicketInfo;
import com.dinhcv.ticketmanagement.utils.Debug;
import com.dinhcv.ticketmanagement.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class SearchCarActivity extends AppCompatActivity {


    private Date mFromDate;
    private Date mToDate;
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

    private TextView edt_dateFrom;
    private TextView edt_dateTo;
    private CheckBox cb_1;
    private CheckBox cb_2;
    private CheckBox cb_3;
    private EditText edt_lisence;
    private RadioGroup radioGroup;

    private TicketModel mTicketModel;

    public static final String WHICH_TICKET = "which_ticket";
    private static final int PAGE_SIZE = 20;
    private final List<TicketInfo> mTicketList = new ArrayList<>();
    private Boolean mBusy = false;
    private int mPageNext = 0;
    private boolean mIsLoad = false;
    private ListView mListview;
    private int mStatus = 0;
    private String mLisence = "";
    private boolean isSetTime = true;

    //field in the class
    private  int RB1_ID = 0;//first radio button id
    private  int RB2_ID = 1;//second radio button id
    private  int RB3_ID = 2;//third radio button id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_car);

        mTicketModel = new TicketModel();

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
        edt_lisence = (EditText) findViewById(R.id.edt_lisence);
        cb_1 = (CheckBox) findViewById(R.id.cb_1);
        cb_2 = (CheckBox) findViewById(R.id.cb_2);
        cb_3 = (CheckBox) findViewById(R.id.cb_3);
        Button btn_search = (Button) findViewById(R.id.btn_search);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        RadioButton rd_all = (RadioButton) findViewById(R.id.rd_all);
        RadioButton rd_in = (RadioButton) findViewById(R.id.rd_in);
        RadioButton rd_out = (RadioButton) findViewById(R.id.rd_out);
        RB1_ID = rd_in.getId();
        RB2_ID = rd_out.getId();
        RB3_ID = rd_all.getId();
        radioGroup.check(RB3_ID);
        edt_dateFrom = (TextView) findViewById(R.id.edt_dateFrom);
        edt_dateTo = (TextView) findViewById(R.id.edt_dateTo);
        edt_dateFrom.setKeyListener(null);
        edt_dateTo.setKeyListener(null);
        edt_dateTo.setFocusable(false);
        edt_dateFrom.setFocusable(false);


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


        cb_1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked){
                    edt_lisence.setText("");
                }
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
        registerEvent();
    }

    private void handleSearchCar(){
        int position = radioGroup.getCheckedRadioButtonId();
        Debug.normal("Radio group id button: "+position);
        Debug.normal("Radio group id RB1: "+RB1_ID + " id RB1: "+RB2_ID + " id RB1: "+RB3_ID);
        if (position == RB1_ID){
            mStatus = 1;
        }else if (position == RB2_ID){
            mStatus = 2;
        }else {
            mStatus = 0;
        }

        // checkbox 3
        //if (!cb_3.isChecked()){
        //   mStatus = 0;
        //}

        // checkbox 2
        if (cb_2.isChecked()){
            isSetTime = true;

            if (!checkTimeInvalid()){
                AlertDialog.Builder dialog = new AlertDialog.Builder(SearchCarActivity.this);
                dialog.setTitle(getString(R.string
                        .error_title))
                        .setMessage(getString(R.string.set_time_invalid))
                        .setCancelable(true)
                        .setPositiveButton(getString(R.string.ok), null)
                        .show();
                return;
            }
        }else {
            isSetTime = false;
        }

        // time invalid

        mLisence = edt_lisence.getText().toString();

        //load from beginning
        mPageNext = 0;
        mIsLoad = true;

        //delete all current view
        mTicketList.clear();

        new LoadTicketDataTask().execute(0, PAGE_SIZE);
    }

    private void initData(){
        //load from beginning
        mPageNext = 0;
        mIsLoad = true;

        //delete all current view
        mTicketList.clear();

        //reload all data from index 0
        new LoadTicketDataTask().execute(0, PAGE_SIZE);
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
                        new LoadTicketDataTask().execute(mPageNext, PAGE_SIZE);
                    }

                }
            }
        });
    }

    public class TicketAdapter extends BaseAdapter {
        private List<TicketInfo> mListTicket;
        private Context mContext;

        TicketAdapter(Context context, List<TicketInfo> list) {
            this.mContext = context;
            mListTicket = list;
        }

        @Override
        public int getCount() {
            return mListTicket.size();
        }

        @Override
        public Object getItem(int position) {
            return mListTicket.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.activity_search_car_row, null, false);
            }

            final TicketInfo ticketInfo = mListTicket.get(position);
            if (ticketInfo == null) {
                return null;
            }

            final TextView tv_no = ((TextView) convertView.findViewById(R.id.tv_no));
            final ImageView img_carType = (ImageView) convertView.findViewById(R.id.img_carType);
            final TextView tv_lisence_plate= ((TextView) convertView.findViewById(R.id.tv_lisence_plate));
            final TextView tv_timeIn = ((TextView) convertView.findViewById(R.id.tv_timeIn));
            final TextView tv_timeOut = ((TextView) convertView.findViewById(R.id.tv_timeOut));

            int count = parent.getChildCount();

            //set other background by odd/even
            convertView.setBackgroundResource((count % 2 == 0) ?
                    R.drawable.table_background_row_even_selector :
                    R.drawable.table_background_row_odd_selector);

            //set data
            tv_no.setText(String.valueOf(position + 1));
            if (ticketInfo.getTicketType() == 1){
                img_carType.setImageResource(R.drawable.carin);
            }else {
                img_carType.setImageResource(R.drawable.carout);
            }
            tv_lisence_plate.setText(ticketInfo.getLisencePlate());
            String timeIn = Utils.convertDateInToString(ticketInfo.getTimeIn());
            tv_timeIn.setText(timeIn);
            String timeOut = "-----";
            if (ticketInfo.getStatus() == 2){
                timeOut = Utils.convertDateInToString(ticketInfo.getTimeOut());
            }
            tv_timeOut.setText(timeOut);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gotoTicketDetail(ticketInfo);
                }
            });

            return convertView;
        }
    }

    private void gotoTicketDetail(TicketInfo ticketInfo){
        Intent intent = new Intent(SearchCarActivity.this, DetailInfoActivity.class);
        intent.putExtra(WHICH_TICKET, ticketInfo);
        startActivityForResult(intent, 1001);
    }


    private class LoadTicketDataTask extends AsyncTask<Integer, Void, List<TicketInfo>> {

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
        protected List<TicketInfo> doInBackground(Integer... params) {

            if (isCancelled()) {
                Debug.normal("Skip load ticket when system busy");
                return Collections.emptyList();
            }

            //range of loading page
            int pageIndex = params[0];
            int pageSize = params[1];

            Debug.normal("Load ticket: pageIndex(%d) pageSize(%d)", pageIndex, pageSize);

            //load data
            List<TicketInfo> found = mTicketModel.searchTicket(mLisence, isSetTime, mFromDate, mToDate, mStatus, pageIndex, pageSize);
            if ((found == null) || (found.size() < pageSize)) {
                Debug.normal("Load to end position of database, Can not load more");
                mIsLoad = false;
            }

            return found;
        }

        @Override
        protected void onPostExecute(List<TicketInfo> ticketInfos) {
            super.onPostExecute(ticketInfos);
            if (isFinishing()) {
                return;
            }

            mBusy = false;

            if ((ticketInfos == null) || (ticketInfos.isEmpty())) {
                Debug.normal("Cannot load more ticketInfos");
                emptyLoad();
                return;
            }

            //next page index
            mPageNext++;

            mTicketList.addAll(ticketInfos);
            //add to view
            addTicketToListView(mTicketList);
        }
    }


    private void addTicketToListView(List<TicketInfo> ticketInfoList){

        if ((ticketInfoList == null) || (ticketInfoList.isEmpty())){
            Debug.normal("Room list is null");
            mListview.setAdapter(null);
            return;
        }

        // get listview current position - used to maintain scroll position
        int currentPosition = mListview.getLastVisiblePosition();

        TicketAdapter listAdapter = new TicketAdapter(getApplicationContext(), ticketInfoList);
        mListview.setAdapter(listAdapter);

        // Setting new scroll position
        mListview.setSelectionFromTop( currentPosition + 1, 0);
    }

    private void emptyLoad() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(SearchCarActivity.this);

        dialog.setTitle(R.string.warning_title);
        dialog.setMessage(R.string.list_is_null);
        dialog.setIcon(R.drawable.dialog_warning_icon);
        dialog.setPositiveButton(R.string.ok, null);
        dialog.show();

        Debug.normal("Room list is null");
        mListview.setAdapter(null);

    }


    /**
     * Show from date picker dialog
     */
    private void handleFromDatePicker() {

        new DatePickerDialog(SearchCarActivity.this, new DatePickerDialog.OnDateSetListener() {
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

        new DatePickerDialog(SearchCarActivity.this, new DatePickerDialog.OnDateSetListener() {
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

        new TimePickerDialog(SearchCarActivity.this, new TimePickerDialog.OnTimeSetListener() {

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

        new TimePickerDialog(SearchCarActivity.this, new TimePickerDialog.OnTimeSetListener() {

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
    private boolean checkTimeInvalid(){

        long fromDate = mFromDate.getTime();
        long toDate = mToDate.getTime();

        long sub = toDate - fromDate ;
        if (sub < 0) {
           AlertDialog.Builder dialog = new AlertDialog.Builder(SearchCarActivity.this);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001){
            if (resultCode ==  RESULT_OK){
                Debug.normal("Result ok, Reload data");
                initData();
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
