package com.dinhcv.ticketmanagement.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dinhcv.ticketmanagement.R;
import com.dinhcv.ticketmanagement.model.TicketModel;
import com.dinhcv.ticketmanagement.model.structure.TicketInfo;
import com.dinhcv.ticketmanagement.model.structure.UserInfo;
import com.dinhcv.ticketmanagement.utils.Debug;
import com.dinhcv.ticketmanagement.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CarInParkActivity extends AppCompatActivity {

    private static final int PAGE_SIZE = 20;
    private TicketModel mTicketModel;
    private ListView mListview;
    private TextView tv_carTotal;
    private final List<TicketInfo> mTicketList = new ArrayList<>();
    private Boolean mBusy = false;
    private int mPageNext = 0;
    private boolean mIsLoad = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_in_park);
        mTicketModel = new TicketModel();

        initView();

        initData();
    }

    private void initView(){

        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar_top);
        toolBar.setTitle(getString(R.string.car_in_parking));
        toolBar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolBar);
        toolBar.showOverflowMenu();

        tv_carTotal = (TextView) findViewById(R.id.tv_carTotal);
        mListview = (ListView) findViewById(R.id.lv_ticket);

        registerEvent();

        tv_carTotal.setText(String.valueOf(countTicket()));

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
                Debug.normal("Ticket count: " + countTicket());
                int lastInScreen = firstVisibleItem + visibleItemCount;
                if(lastInScreen == totalItemCount) {
                    if (!mBusy) {
                        new LoadTicketDataTask().execute(mPageNext, PAGE_SIZE);
                    }

                }
            }
        });
    }

    private int countTicket(){
        int count = mTicketModel.getTotalCarInPark();
        Debug.normal("Num car in park: "+count);
        return count;
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
                convertView = inflater.inflate(R.layout.activity_car_in_park_row, null, false);
            }

            final TicketInfo ticketInfo = mListTicket.get(position);
            if (ticketInfo == null) {
                return null;
            }

            final TextView tv_no = ((TextView) convertView.findViewById(R.id.tv_no));
            final ImageView img_carType = (ImageView) convertView.findViewById(R.id.img_carType);
            final TextView tv_lisence_plate= ((TextView) convertView.findViewById(R.id.tv_lisence_plate));
            final TextView tv_time = ((TextView) convertView.findViewById(R.id.tv_time));

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
            String time = Utils.convertDateInToString(ticketInfo.getTimeIn());
            tv_time.setText(time);

            return convertView;
        }
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
            List<TicketInfo> found = mTicketModel.getTicketInParkingList(pageIndex, pageSize);
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
        AlertDialog.Builder dialog = new AlertDialog.Builder(CarInParkActivity.this);

        dialog.setTitle(R.string.warning_title);
        dialog.setMessage(R.string.list_is_null);
        dialog.setIcon(R.drawable.dialog_info_icon);
        dialog.setPositiveButton(R.string.ok, null);
        dialog.show();

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
