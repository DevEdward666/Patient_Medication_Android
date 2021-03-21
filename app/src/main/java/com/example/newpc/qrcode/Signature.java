package com.example.newpc.qrcode;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.loader.content.CursorLoader;

import com.example.newpc.services.APIClient;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class Signature extends AppCompatActivity {

    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 1;

    private GestureOverlayView gestureOverlayView = null;

    private Button redrawButton = null;
    String imagePath,filepath,pathToFile;
    private Button saveButton = null;
    private Button btn_choose=null;
    private ImageView img;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_signature);
        img=findViewById(R.id.image_sign);
        setTitle("");
        init();

        gestureOverlayView.addOnGesturePerformedListener(new CustomGestureListener());
        btn_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchpics();
            }
        });
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


    }
    private void dispatchpics(){
        Intent takepic=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takepic.resolveActivity(getPackageManager())!=null){
            File photofile=null;
            photofile=createPhotoFile();
            if(photofile!=null){
                pathToFile=photofile.getAbsolutePath();
                Uri photoURI= FileProvider.getUriForFile(Signature.this,"com.example.newpc.fileprovider",photofile);
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
    private void uploadImage()  {

        File file=new File(filepath);
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"),file);
        MultipartBody.Part parts= MultipartBody.Part.createFormData("newimage",file.getName(),requestBody);
        RequestBody description=RequestBody.create(MediaType.parse("text/plain"),"This is a new image");
        Call<RequestBody> call=APIClient.getInstance().getApi().upload(parts,description);
        call.enqueue(new Callback<RequestBody>() {
            @Override
            public void onResponse(Call<RequestBody> call, Response<RequestBody> response) {

            }

            @Override
            public void onFailure(Call<RequestBody> call, Throwable t) {

            }
        });
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
    private void UploadFile(){
        File file=new File (imagePath);
        RequestBody description=RequestBody.create(MediaType.parse("multipart/form-data"),file);
        MultipartBody.Part photo=MultipartBody.Part.createFormData("file",file.getName(),description);
        Call<RequestBody> call=APIClient.getInstance().getApi().upload(photo,description);
        call.enqueue(new Callback<RequestBody>() {
    @Override
    public void onResponse(Call<RequestBody> call, Response<RequestBody> response) {
        if(response.isSuccessful()){
            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFailure(Call<RequestBody> call, Throwable t) {
        Toast.makeText(getApplicationContext(), "Error" + t.getMessage(), Toast.LENGTH_LONG).show();
    }
});
}
    private void init()
    {
        if(gestureOverlayView==null)
        {
            gestureOverlayView = (GestureOverlayView)findViewById(R.id.signing_pad);
        }

        if(redrawButton==null)
        {
            redrawButton = (Button)findViewById(R.id.redraw_button);
        }

        if(saveButton==null)
        {
            saveButton = (Button)findViewById(R.id.save_button);
        }
        if(btn_choose == null){
            btn_choose = (Button)findViewById(R.id.choose_btn);
        }
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
                ActivityCompat.requestPermissions(Signature.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
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
        String name=new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
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
            File file = new File(filePath+"/"+name+"_"+".png");
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
            RequestBody description=RequestBody.create(MediaType.parse("text/plain"),"test");
            Call<RequestBody> call=APIClient.getInstance().getApi().upload(photo,description);
            call.enqueue(new Callback<RequestBody>() {
                @Override
                public void onResponse(Call<RequestBody> call, Response<RequestBody> response) {
                    Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();
                    if(response.isSuccessful()){
                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "Not Success"+response.body(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<RequestBody> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Error" + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e) {
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
}
