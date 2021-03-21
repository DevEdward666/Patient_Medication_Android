package com.example.newpc.qrcode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.newpc.adapters.PatientListAdapter;
import com.example.newpc.entities.Patients;
import com.example.newpc.model.PatientData;
import com.example.newpc.services.APIClient;
import com.example.newpc.services.PatientService;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReaderActivity extends AppCompatActivity {
    private Button scan_btn;
    private ListView listViewPatients;
    private TextView patno;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        scan_btn = (Button) findViewById(R.id.scan_btn);
        patno = (TextView) findViewById(R.id.patno);
        final Activity activity = this;
        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });

        listViewPatients= (ListView) findViewById(R.id.list_view_patient);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents()==null){
                Toast.makeText(this, "You cancelled the scanning", Toast.LENGTH_LONG).show();
            }
            else {
                patno.setText(result.getContents());
//                PatientService patientService= APIClient.getClient().create(PatientService.class);
//                Call call = patientService.patientinformationwithoutmeds("IP19-000002");
//                call.enqueue(new Callback() {
//                    @Override
//                    public void onResponse(Call call, Response response) {
//                        List<PatientData> patient=(List<PatientData>) response.body();
//                        listViewPatients.setAdapter(new PatientListAdapter(patient,getApplicationContext()));
//                        Toast.makeText(getApplicationContext(), "Inside", Toast.LENGTH_LONG).show();
//                    }
//                    @Override
//                    public void onFailure(Call call, Throwable t) {
//                        Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
//                    }
//                });
//                Toast.makeText(this, result.getContents(),Toast.LENGTH_LONG).show();
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
