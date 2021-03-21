package com.example.newpc.qrcode;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.device.ScanDevice;
import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newpc.adapters.MedicationAdapter;
import com.example.newpc.adapters.MedicationListAdapter;
import com.example.newpc.adapters.PatientListAdapter;
import com.example.newpc.model.LoginUserModel;
import com.example.newpc.model.MedsData;
import com.example.newpc.model.MedsResponse;
import com.example.newpc.model.MedsSpecificData;
import com.example.newpc.model.MedsSpeciificResponse;
import com.example.newpc.model.PatientData;
import com.example.newpc.model.PatientDataResponse;
import com.example.newpc.qrcode.R;
import com.example.newpc.services.APIClient;
import com.example.newpc.storage.SharedPrefManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.os.Environment.getExternalStoragePublicDirectory;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class Scanner_Fragment extends Activity implements GestureDetector.OnGestureListener{
    ScanDevice sm;

    private FloatingActionButton saveButton ;
    private FloatingActionButton redrawButton ;
    float x1=0,x2=0,y1=0,y2=0;
    private final static String SCAN_ACTION = "scan.rcv.message";
    private String barcodeStr;
    public FloatingActionButton btn_capture;
    View v;
    public String patno=null,medpatno=null,medchargeslip=null,medstockcode=null;
    public EditText showScanResult;
    public RecyclerView recyclerView,medicationsrecycler,medsrecycler;
     PatientListAdapter adapter;
     MedicationListAdapter medicaitonsadapter;
     MedicationAdapter medsadapter;
    private List<MedsSpecificData> medslist;
    private List<MedsData> meds;
    private List<PatientData> patientList;
    private GestureDetector gestureDetector;
    private static int MIN_DISTANCE=150;
    private ImageView img;
    LinearLayout container_sign;
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 1;
    private ProgressDialog dialog;
    private GestureOverlayView gestureOverlayView = null;
    String imagePath,filepath,pathToFile;
    String filename,auth="";
    LoginUserModel loginUserModel= SharedPrefManager.getInstance(this).getUser();
    FloatingActionButton openScanner;
    public BroadcastReceiver mScanReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            byte[] barocode = intent.getByteArrayExtra("barocode");
            int barocodelen = intent.getIntExtra("length", 0);
            byte temp = intent.getByteExtra("barcodeType", (byte) 0);
            byte[] aimid = intent.getByteArrayExtra("aimid");
            barcodeStr = new String(barocode, 0, barocodelen);



            if(barcodeStr.contains(":")){
                if(patno==null){
                    Toast.makeText(getApplicationContext(), "Scan Patient Number First", Toast.LENGTH_SHORT).show();
                }else{
                    dialog.setMessage("Fetching Data . . .");
                    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    dialog.show();
                    dialog.setCancelable(false);
//                    v.setVisibility(View.VISIBLE);
                    String currentString = barcodeStr;
                    String[] separated = currentString.split(":");
                    medpatno=separated[0].trim();
                    medchargeslip=separated[1].trim();
                    medstockcode=separated[2].trim();
                    medicationsrecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    Call<MedsSpeciificResponse> call = APIClient.getInstance().getApi().getspecificmedications("Bearer "+auth,patno,medchargeslip,medstockcode);
                    call.enqueue(new Callback<MedsSpeciificResponse>() {
                        @Override
                        public void onResponse(Call<MedsSpeciificResponse> call, Response<MedsSpeciificResponse> response) {
                            medslist=response.body().getData();
                            medicaitonsadapter=new MedicationListAdapter(getApplicationContext(),medslist);
                            medicationsrecycler.setAdapter(medicaitonsadapter);
                       //     sm.closeScan();
                            medsrecycler.setVisibility(GONE);
                            if(medslist.toString()=="[]"){
                                Toast.makeText(getApplicationContext(),"No Data to Show", Toast.LENGTH_SHORT).show();
                            }else{
                                img.setVisibility(VISIBLE);
                                btn_capture.setVisibility(VISIBLE);
                                container_sign.setVisibility(VISIBLE);
                            }
                           // v.setVisibility(View.GONE);
                            dialog.dismiss();
                        }
                        @Override
                        public void onFailure(Call<MedsSpeciificResponse> call, Throwable t) {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }else{
                    dialog.setMessage("Fetching Data . . .");
                    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    dialog.show();
                    dialog.setCancelable(false);
                    //v.setVisibility(View.VISIBLE);
                    patno=barcodeStr;
                    filename=patno;
                    medsrecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    Call<MedsResponse> callmeds = APIClient.getInstance().getApi().getallmedications("Bearer "+auth,patno);
                    callmeds.enqueue(new Callback<MedsResponse>() {
                        @Override
                        public void onResponse(Call<MedsResponse> call, Response<MedsResponse> response) {
                            try{
                            meds=response.body().getData();
                            medsadapter=new MedicationAdapter(getApplicationContext(),meds);
                            medsrecycler.setAdapter(medsadapter);
                           // sm.closeScan();
                            dialog.dismiss();
                            // v.setVisibility(View.GONE);
                            }catch (Exception e){
                                //sm.closeScan();
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(), "No Medication for this patient", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<MedsResponse> call, Throwable t) {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    Call<PatientDataResponse> call = APIClient.getInstance().getApi().PatientList("Bearer "+auth,patno);
                    call.enqueue(new Callback<PatientDataResponse>() {
                        @Override
                        public void onResponse(Call<PatientDataResponse> call, Response<PatientDataResponse> response) {
                            try{
                                patientList=response.body().getData();
                                adapter=new PatientListAdapter(getApplicationContext(),patientList);
                                recyclerView.setAdapter(adapter);
//                                sm.stopScan();
                            }catch (Exception e){
                                Log.d("response","error "+e.getMessage());
//                                sm.stopScan();
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(), "No Medication for this patient", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<PatientDataResponse> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


            }
            sm.stopScan();
        }
    };
    public void Patientshow(){
        recyclerView.setVisibility(VISIBLE);
        medicationsrecycler.setVisibility(View.INVISIBLE);
    }
    public void Medicationsshow(){
        recyclerView.setVisibility(View.INVISIBLE);
        medicationsrecycler.setVisibility(VISIBLE);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanner);
        sm = new ScanDevice();
        sm.openScan();

        init();
        dialog = new ProgressDialog(this);
         v = findViewById(R.id.llProgressBar);
        gestureOverlayView.addOnGesturePerformedListener(new CustomGestureListener());
        auth=loginUserModel.getAccess_token();
        img=findViewById(R.id.img_signage);

        recyclerView=findViewById(R.id.reclycer_user);
        medicationsrecycler=findViewById(R.id.reclycer_medications);
        medsrecycler=findViewById(R.id.reclycer_meds);


        container_sign=findViewById(R.id.container_sign);
        btn_capture=findViewById(R.id.btn_capture_image);
        redrawButton=findViewById(R.id.btn_redraw_image);
        saveButton=findViewById(R.id.btn_save_image);
        openScanner=findViewById(R.id.openScanner);
        redrawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                gestureOverlayView.clear(false);
            }

        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //            UploadFile();
                checkPermissionAndSaveSignature();
            }
        });
        btn_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchpics();
            }
        });
        openScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sm.startScan();
            }
        });
//
//        btn_med_info.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                recyclerView.setVisibility(View.GONE);
//                medicationsrecycler.setVisibility(View.VISIBLE);
//                btn_med_info.setPressed(true);
//                btn_pat_info.setPressed(false);
//            }
//        });

//        sm.setOutScanMode(0);//启动就是广播模式
//        Spinner spinner = (Spinner) findViewById(R.id.spinner);
//        String[] arr = { getString(R.string.brodcast_mode), getString(R.string.input_mode) };
//        //创建ArrayAdapter对象
//        ArrayAdapter<String> adapter =
//                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arr);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (position == 0) {
//                    sm.setOutScanMode(0);
//                } else if (position == 1) {
//                    sm.setOutScanMode(1);
//                }
//            }
//            @Override public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//        sm.setOutScanMode(0);

    }

//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.openScanner:
//                System.out.println("openScanner = " + sm.getOutScanMode());
//                    sm.startScan();
//                break;
//            case R.id.closeScanner:
//                sm.closeScan();
//                break;
//            case R.id.startDecode:
//                sm.startScan();
//                break;
//            case R.id.stopDecode:
//                sm.stopScan();
//                break;
//            case R.id.start_continue:
//                sm.setScanLaserMode(4);
//                break;
//            case R.id.stop_continue:
//                sm.setScanLaserMode(8);
//                break;
//            case R.id.clear:
//                showScanResult.setText("");
//                break;
//            default:
//                break;
//        }
//    }

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

//    @Override
//    public boolean onTouchEvent(MotionEvent touchEvent){
//        gestureDetector.onTouchEvent(touchEvent);
//        switch(touchEvent.getAction()){
//            case MotionEvent.ACTION_DOWN:
//                x1 = touchEvent.getX();
//                y1 = touchEvent.getY();
//                break;
//            case MotionEvent.ACTION_UP:
//                x2 = touchEvent.getX();
//                y2 = touchEvent.getY();
//                float valueX=x2-x1;
//                float valueY=y2-y1;
//                if(Math.abs(valueX)>MIN_DISTANCE){
//                    if(x2>x1){
//                        Toast.makeText(getApplicationContext(), "Right", Toast.LENGTH_SHORT).show();
//                        recyclerView.setVisibility(View.VISIBLE);
//                        medicationsrecycler.setVisibility(View.GONE);
//                    }else{
//                        Toast.makeText(getApplicationContext(), "Left", Toast.LENGTH_SHORT).show();
//                        recyclerView.setVisibility(View.GONE);
//                        medicationsrecycler.setVisibility(View.VISIBLE);
//                    }
//
//                }
//
//        }
//        return super.onTouchEvent(touchEvent);
//    }
    private void dispatchpics(){
        Intent takepic=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takepic.resolveActivity(getPackageManager())!=null){
            File photofile=null;
            photofile=createPhotoFile();
            if(photofile!=null){
                pathToFile=photofile.getAbsolutePath();
                Uri photoURI= FileProvider.getUriForFile(Scanner_Fragment.this,BuildConfig.APPLICATION_ID+".provider",photofile);
                takepic.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
                startActivityForResult(takepic,1);
            }
        }
    }
    private File createPhotoFile(){
        String name=new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir=getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image=null;
        try{
            image=File.createTempFile(name,".jpg",storageDir);
        }catch (Exception e){
            Log.d("myLog","Excep"+e.toString());
        }
        return image;

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){

            Bitmap bitmap= BitmapFactory.decodeFile(pathToFile);
            img.setImageBitmap(bitmap);
            if(data==null){

                Toast.makeText(this,"Unable to Choose Image",Toast.LENGTH_LONG).show();
                return;
            }
            Uri imageUri=data.getData();
            imagePath=getRealPathFromUri(imageUri);
        }
    }
    private String getRealPathFromUri(Uri uri){
        String[] projection={MediaStore.Images.Media.DATA};
        CursorLoader loader=new CursorLoader(getApplicationContext(),uri,projection,null,null,null);
        Cursor cursor=loader.loadInBackground();
        int column_idx=cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result=cursor.getString(column_idx);
        cursor.close();
        return result;
    }
    private void init()
    {
        if(gestureOverlayView==null)
        {
            gestureOverlayView = findViewById(R.id.sign_pad);
        }

        if(redrawButton==null)
        {
            redrawButton = (FloatingActionButton)findViewById(R.id.redraw_button);
        }

        if(saveButton==null)
        {
            saveButton = (FloatingActionButton)findViewById(R.id.save_button);
        }
    }
private  void insertmedicationhistory(){
    Call<ResponseBody> call = APIClient.getInstance().getApi().inserttomedicationhistory("Bearer "+auth,patno,medchargeslip,medstockcode);
    call.enqueue(new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            try{
                dialog.dismiss();

                // sm.closeScan();

             //   Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_SHORT).show();
                // v.setVisibility(View.GONE);
            }catch (Exception e){
                //sm.closeScan();
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "No Medication for this patient", Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            dialog.dismiss();
            Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    });
}

    private void checkPermissionAndSaveSignature()
    {
        try {

            // Check whether this app has write external storage permission or not.
            int writeExternalStoragePermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

            // If do not grant write external storage permission.
            if(writeExternalStoragePermission!= PackageManager.PERMISSION_GRANTED)
            {
                // Request user to grant write external storage permission.
                ActivityCompat.requestPermissions(Scanner_Fragment.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
            }else
            {
                saveSignature();
            }

        } catch (Exception e) {
            Log.v("Signature Gestures", e.getMessage());
            e.printStackTrace();
        }
    }


    private void saveSignature()
    {
        Snackbar.make(v, "Saved Successfully", Snackbar.LENGTH_LONG).show();
        insertmedicationhistory();
        String name=new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        dialog.setMessage("Saving Data");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();
        dialog.setCancelable(false);
        try {
            // First destroy cached image.
            gestureOverlayView.destroyDrawingCache();
            // Enable drawing cache function.
            gestureOverlayView.setDrawingCacheEnabled(true);
            // Get drawing cache bitmap.
            Bitmap drawingCacheBitmap = gestureOverlayView.getDrawingCache();
            // Create a new bitmap
            Bitmap bitmap = Bitmap.createBitmap(drawingCacheBitmap);
            String filePath = Environment.getExternalStorageDirectory().toString();
            File file = new File(filePath+"/"+filename+name+".png");
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            // Compress bitmap to png image.
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            // Flush bitmap to image file.
            fileOutputStream.flush();
            // Close the output stream.
            fileOutputStream.close();

            File Newfile = new File(file+"/test.png");
            RequestBody photoContent=RequestBody.create(MediaType.parse("image/*"),file);
            MultipartBody.Part photo=MultipartBody.Part.createFormData("photo",file.getName(),photoContent);
            RequestBody description=RequestBody.create(MediaType.parse("text/plain"),filename);
            Call<RequestBody> call=APIClient.getInstance().getApi().upload(photo,description);
            call.enqueue(new Callback<RequestBody>() {
                @Override
                public void onResponse(Call<RequestBody> call, Response<RequestBody> response) {

                    dialog.dismiss();
                    if(response.isSuccessful()){
                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "Not Success"+response.body(), Toast.LENGTH_LONG).show();

                    }
                }

                @Override
                public void onFailure(Call<RequestBody> call, Throwable t) {
                    dialog.dismiss();
//                    Toast.makeText(getApplicationContext(), "Error" + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e) {
            dialog.dismiss();
            Toast.makeText(getApplicationContext(), "Errors" + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.v("Signature Gestures", e.getMessage());
            e.printStackTrace();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION) {
            int grantResultsLength = grantResults.length;
            if (grantResultsLength > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveSignature();
            } else {
                Toast.makeText(getApplicationContext(), "You denied write external storage permission.", Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
