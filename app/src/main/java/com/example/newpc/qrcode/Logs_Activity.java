package com.example.newpc.qrcode;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.device.ScanDevice;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.newpc.adapters.LogsAdapter;
import com.example.newpc.model.LoginUserModel;
import com.example.newpc.model.LogsModel;
import com.example.newpc.model.LogsResponse;
import com.example.newpc.services.APIClient;
import com.example.newpc.storage.SharedPrefManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.VISIBLE;

public class Logs_Activity extends AppCompatActivity {
    ScanDevice sm;
    private String barcodeStr;
    private final static String SCAN_ACTION = "scan.rcv.message";
    public RecyclerView recyclerView;
    LogsAdapter adapter;
    private List<LogsModel> logsModels;
    LoginUserModel loginUserModel= SharedPrefManager.getInstance(this).getUser();
    String auth="",patno="",user;
    private ProgressDialog dialog;
    FloatingActionButton Logsscanner;
    SwitchMaterial calendar_switch ;
    CalendarView LogCalendar;
    int holdyear=0,holdmonth=0,holdday=0;
    int limit=10;
    public View v;
    private Parcelable recyclerViewState;

    public BroadcastReceiver mScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            byte[] barocode = intent.getByteArrayExtra("barocode");
            int barocodelen = intent.getIntExtra("length", 0);
            byte temp = intent.getByteExtra("barcodeType", (byte) 0);
            byte[] aimid = intent.getByteArrayExtra("aimid");
            barcodeStr = new String(barocode, 0, barocodelen);
            patno=barcodeStr;

            dialog.setMessage("Fetching Data . . .");
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.show();
            dialog.setCancelable(false);
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            Call<LogsResponse> call = APIClient.getInstance().getApi().getlogsbypatno("Bearer "+auth,patno);
            call.enqueue(new Callback<LogsResponse>() {
                @Override
                public void onResponse(Call<LogsResponse> call, Response<LogsResponse> response) {
                try{
                    logsModels=response.body().getData();
                    adapter=new LogsAdapter(Logs_Activity.this,logsModels);
                    recyclerView.setAdapter(adapter);
                    if(logsModels.toString()=="[]"){
                        snack(findViewById(android.R.id.content),"No Data to Show");
                       // Toast.makeText(getApplicationContext(),"No Data to Show", Toast.LENGTH_SHORT).show();
                    }

                    dialog.dismiss();
                }catch (Exception e){
                    dialog.dismiss();
                    Snackbar.make(v , "No Data to show", Snackbar.LENGTH_SHORT).show();

                }

                }
                @Override
                public void onFailure(Call<LogsResponse> call, Throwable t) {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);
        auth=loginUserModel.getAccess_token();
        dialog = new ProgressDialog(this);
        recyclerView=findViewById(R.id.log_recycler);
        Logsscanner=findViewById(R.id.Logsscanner);
        calendar_switch = (SwitchMaterial) findViewById(R.id.switch_calendar);
        LogCalendar=findViewById(R.id.LogCalendar);
        user= getIntent().getExtras().getString("user");
        sm = new ScanDevice();
        sm.openScan();

        fetchDataLogs(limit);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Logsscanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sm.startScan();
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE) {
                    recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
                    limit=limit+10;
                    fetchDataLogs(limit);
                }
            }
        });
        calendar_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    LogCalendar.setVisibility(VISIBLE);
                }else {
                    LogCalendar.setVisibility(View.GONE);
                }


            }
        });
        LogCalendar.setVisibility(View.GONE);
        LogCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                //Log.d("calendar"," " + year+" " + month +1 +" "+ dayOfMonth);
                holdyear=year;
                holdmonth=month;
                holdday=dayOfMonth;
                fetchDataLogsBydate(holdyear,holdmonth,holdday);
            }
        });
    }
private void snack(View v,String Message){
        Snackbar.make(v, Message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
    private void fetchDataLogs(final int limits){
        dialog.setMessage("Fetching Data . . .");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();
        dialog.setCancelable(false);

        Call<LogsResponse> call = APIClient.getInstance().getApi().getLogs("Bearer "+auth,user,limits);
        call.enqueue(new Callback<LogsResponse>() {
            @Override
            public void onResponse(Call<LogsResponse> call, Response<LogsResponse> response) {

                try{

                        logsModels=response.body().getData();
                        adapter=new LogsAdapter(Logs_Activity.this,logsModels);
                        recyclerView.setAdapter(adapter);
                        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
                        if(logsModels.toString()=="[]"){
                            snack(findViewById(android.R.id.content),"No Data to Show");
                        }


                    dialog.dismiss();
                }catch (Exception e){
                    dialog.dismiss();

                    snack(findViewById(android.R.id.content),"No Data to Show");
                    //Toast.makeText(getApplicationContext(),"No Data to Show", Toast.LENGTH_SHORT).show();

                }
            }
            @Override
            public void onFailure(Call<LogsResponse> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void fetchDataLogsBydate(int year,int month,int dayofmonth){
        dialog.setMessage("Fetching Data . . .");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();
        dialog.setCancelable(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Call<LogsResponse> call = APIClient.getInstance().getApi().getLogsbydate("Bearer "+auth,year,month+1,dayofmonth);
        call.enqueue(new Callback<LogsResponse>() {
            @Override
            public void onResponse(Call<LogsResponse> call, Response<LogsResponse> response) {

                try{
                    logsModels=response.body().getData();
                    adapter=new LogsAdapter(Logs_Activity.this,logsModels);
                    recyclerView.setAdapter(adapter);
                    if(logsModels.toString()=="[]"){
                        snack(findViewById(android.R.id.content),"No Data to Show");
                    }

                    dialog.dismiss();
                }catch (Exception e){
                    dialog.dismiss();
                    snack(findViewById(android.R.id.content),"No Data to Show");
               //     Toast.makeText(getApplicationContext(),"No Data to Show", Toast.LENGTH_SHORT).show();

                }
            }
            @Override
            public void onFailure(Call<LogsResponse> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mScanReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(SCAN_ACTION);
        registerReceiver(mScanReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sm != null) {
            sm.stopScan();
            sm.setScanLaserMode(8);
            sm.closeScan();
        }
    }
}
