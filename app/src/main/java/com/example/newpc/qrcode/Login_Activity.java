package com.example.newpc.qrcode;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.newpc.model.LoginResponse;
import com.example.newpc.services.APIClient;
import com.example.newpc.services.PatientService;
import com.example.newpc.storage.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login_Activity extends AppCompatActivity {
    EditText username,password;
    Button btnlogin;
    PatientService patientService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//
//        btnlogin=findViewById(R.id.btn_login);
//        AnimationDrawable animationDrawable=(AnimationDrawable) btnlogin.getBackground();
//        animationDrawable.setEnterFadeDuration(2000);
//        animationDrawable.setExitFadeDuration(4000);
//        animationDrawable.start();

        username=(EditText)findViewById(R.id.username);
        password=(EditText)findViewById(R.id.password);
        btnlogin=(Button)findViewById(R.id.btn_login);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Username=username.getText().toString();
                String Password=password.getText().toString();
                if(validateLogin(Username,Password)){
                    doLogin(Username,Password);
                }
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        if(SharedPrefManager.getInstance(this).isLoggedIn()){
            Intent intent=new Intent(this,Dashboard.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private boolean validateLogin(String Username, String Password){
        if(Username==null || Username.trim().length()==0){
            Toast.makeText(this,"Username is Required",Toast.LENGTH_LONG).show();
            return false;
        }
        if(Password==null || Password.trim().length()==0){
            Toast.makeText(this,"Password is Required",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
    private void doLogin(final String Username, final String Password){
     Call<LoginResponse> call=APIClient.getInstance().getApi().userLogin(Username,Password,"password");
     call.enqueue(new Callback<LoginResponse>() {
         @Override
         public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

             try {
                 LoginResponse loginResponse=response.body();
                 SharedPrefManager.getInstance(Login_Activity.this).saveUser(loginResponse.getAccess_token(),loginResponse.getRefresh_token());
                 Intent intent=new Intent(Login_Activity.this,Dashboard.class);
                 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                 startActivity(intent);
             }catch (Exception e){
                 Toast.makeText(Login_Activity.this,"Your username/password is incorrect",Toast.LENGTH_LONG).show();
             }
         }

         @Override
         public void onFailure(Call<LoginResponse> call, Throwable t) {
             Toast.makeText(Login_Activity.this,t.getMessage(),Toast.LENGTH_LONG).show();
         }
     });
    }
}
