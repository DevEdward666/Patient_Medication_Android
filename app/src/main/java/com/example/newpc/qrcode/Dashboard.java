package com.example.newpc.qrcode;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.newpc.model.AppVersionModel;
import com.example.newpc.model.AppVersionResponse;
import com.example.newpc.model.LoginResponse;
import com.example.newpc.model.LoginUserModel;
import com.example.newpc.model.UserModel;
import com.example.newpc.model.UserModelResponse;
import com.example.newpc.services.APIClient;
import com.example.newpc.storage.SharedPrefManager;
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.AppUpdaterUtils;
import com.github.javiersantos.appupdater.enums.AppUpdaterError;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.github.javiersantos.appupdater.objects.Update;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class Dashboard extends AppCompatActivity {
    CardView cardScan,cardLogout,cardlogs;
    String auth,auth2="";
    TextView txt_dashusername,txt_dashempname;
    LoginUserModel loginUserModel= SharedPrefManager.getInstance(this).getUser();
    AppUpdaterUtils appUpdaterUtils;
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    DownloadManager downloadmanager;
    private ProgressDialog dialog;
    long enq;
    String version,appversion;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        StrictMode.setThreadPolicy(policy);
        downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        dialog = new ProgressDialog(this);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    long downloadId = intent.getLongExtra(
                            DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                    openDownloadedAttachment(context, downloadId);
                }
            }

        };
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        cardScan=findViewById(R.id.card_scan);
        cardLogout=findViewById(R.id.card_logout);
        cardlogs=findViewById(R.id.card_logs);
        txt_dashusername=findViewById(R.id.txt_dashusername);
        txt_dashempname=findViewById(R.id.txt_dashempname);

        getdefaults();

       getAppVersion();
        cardScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Dashboard.this,Scanner_Fragment.class);
                startActivity(intent);
            }
        });
        cardlogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Dashboard.this,Logs_Activity.class);
                Bundle bundle = new Bundle();
                bundle.putString("user",txt_dashempname.getText().toString());
                intent.putExtras(bundle);
                startActivity(intent);


            }
        });

        cardLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefManager.getInstance(Dashboard.this).clear();
                Intent intent=new Intent(Dashboard.this, Splash.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }



    public void OpenFile(String name){
        File file = new File(name);

        Uri uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID+".provider", file);
        String mime = getContentResolver().getType(uri);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, mime);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!SharedPrefManager.getInstance(this).isLoggedIn()){
            Intent intent=new Intent(this,Login_Activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
    void installNewVersion() {

        Uri uri = Uri.parse("http://192.168.1.40:45458/api/ns/getapk");
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setMimeType(getMimeType(uri.toString()+".apk"));
        request.setTitle("Patient Medication Update");
        request.setDescription("Downloading");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES,"patient_info");
        enq=downloadmanager.enqueue(request);
dialog.dismiss();
    }
    private String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }
    private void openDownloadedAttachment(final Context context, final long downloadId) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);
        Cursor cursor = downloadManager.query(query);
        if (cursor.moveToFirst()) {
            int downloadStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            String downloadLocalUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
            String downloadMimeType = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE));
            if ((downloadStatus == DownloadManager.STATUS_SUCCESSFUL) && downloadLocalUri != null) {
                openDownloadedAttachment(context, Uri.parse(downloadLocalUri), downloadMimeType);
            }
        }
        cursor.close();
    }
    private void openDownloadedAttachment(final Context context, Uri attachmentUri, final String attachmentMimeType) {
        if(attachmentUri!=null) {
            // Get Content Uri.
            if (ContentResolver.SCHEME_FILE.equals(attachmentUri.getScheme())) {
                // FileUri - Convert it to contentUri.
                File file = new File(attachmentUri.getPath());
                attachmentUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID+".provider", file);;
            }

            Intent openAttachmentIntent = new Intent(Intent.ACTION_VIEW);
            openAttachmentIntent.setDataAndType(attachmentUri, attachmentMimeType);
            openAttachmentIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                context.startActivity(openAttachmentIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
    public void CheckAppVersion(){
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
             version = pInfo.versionName;
            Log.d("CheckAppVersion", version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void getdefaults() {

            auth = loginUserModel.getAccess_token();
            Call<UserModelResponse> call2 = APIClient.getInstance().getApi().db_get_active_user("Bearer " + auth);
            call2.enqueue(new Callback<UserModelResponse>() {
                @Override
                public void onResponse(Call<UserModelResponse> call2, Response<UserModelResponse> response) {

                    UserModel userModels = response.body().getData();
                    if (userModels != null) {
                        txt_dashusername.setText(userModels.getEmpname());
                        txt_dashempname.setText(userModels.getUsername());

                    } else {
                        Toast.makeText(Dashboard.this, "Network Error", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<UserModelResponse> call, Throwable t) {
                    Toast.makeText(Dashboard.this, "Network Error", Toast.LENGTH_SHORT).show();


                }
            });

    }
    public void getAppVersion() {
        CheckAppVersion();
        auth = loginUserModel.getAccess_token();
        Call<AppVersionResponse> call2 = APIClient.getInstance().getApi().AndroidVersion("Bearer " + auth,"Patient Medication");
        call2.enqueue(new Callback<AppVersionResponse>() {
            @Override
            public void onResponse(Call<AppVersionResponse> call2, Response<AppVersionResponse> response) {
                try {
                    List<AppVersionModel> appVersionModel = response.body().getData();
                    if (appVersionModel != null) {
                        for (int i = 0; i < appVersionModel.size(); i++) {
                            appversion = appVersionModel.get(i).getAppVersion();
                        }
                        if (!version.equals(appversion)) {
                            installNewVersion();
                            dialog.setMessage("Installing New Update");
                            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            dialog.show();
                            Log.d("CheckAppNewVersion", appversion);
                        }
                    } else {
                        Toast.makeText(Dashboard.this, "Network Error", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(Dashboard.this, "Your application is up-to-date", Toast.LENGTH_SHORT).show();
                }

            }
            @Override
            public void onFailure(Call<AppVersionResponse> call, Throwable t) {
                Toast.makeText(Dashboard.this, "Network Error", Toast.LENGTH_SHORT).show();


            }
        });

    }
}
