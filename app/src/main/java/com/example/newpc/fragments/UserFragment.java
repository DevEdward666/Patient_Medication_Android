package com.example.newpc.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.newpc.adapters.MedicationListAdapter;
import com.example.newpc.adapters.PatientListAdapter;
import com.example.newpc.model.LoginUserModel;
import com.example.newpc.model.MedsData;
import com.example.newpc.model.MedsResponse;
import com.example.newpc.model.PatientData;
import com.example.newpc.model.PatientDataResponse;
import com.example.newpc.qrcode.Login_Activity;
import com.example.newpc.qrcode.R;
import com.example.newpc.qrcode.Signature;
import com.example.newpc.services.APIClient;
import com.example.newpc.storage.SharedPrefManager;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserFragment extends Fragment {
private RecyclerView recyclerView,medicationsrecycler;
private PatientListAdapter adapter;
private MedicationListAdapter medicaitonsadapter;
private List<MedsData> medslist;
    private List<PatientData> patientList;
    public String patno=null,medpatno=null,medchargeslip=null,medstockcode=null,auth="";
    LoginUserModel loginUserModel= SharedPrefManager.getInstance(getContext()).getUser();

private Button scan_btn,btn_submit;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_user,container,false);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        IntentIntegrator integrator = new IntentIntegrator(getActivity());
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Scan");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.forFragment(UserFragment.this).initiateScan();
        auth = loginUserModel.getAccess_token();
        scan_btn = (Button) view.findViewById(R.id.scan_meds);
        btn_submit = (Button) view.findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getActivity(), Signature.class);
                startActivity(intent);
            }
        });
        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(getActivity());
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.forFragment(UserFragment.this).initiateScan();
            }
        });

        recyclerView=view.findViewById(R.id.patientlist_recycler);
        medicationsrecycler=view.findViewById(R.id.medications_recycler);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents()==null){
                Toast.makeText(getActivity(), "You cancelled the scanning", Toast.LENGTH_LONG).show();
            }
            else {

                if(result.getContents().contains(":")){
//                    String currentString = result.getContents();
//                    String[] separated = currentString.split(":");
//                    medpatno=separated[0].trim();
//                    medchargeslip=separated[1].trim();
//                    medstockcode=separated[2].trim();
//                    Toast.makeText(getContext(),medpatno, Toast.LENGTH_SHORT).show();
//                    medicationsrecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
//                    Call<MedsResponse> call = APIClient.getInstance().getApi().getallmedications(medpatno);
//                    call.enqueue(new Callback<MedsResponse>() {
//                        @Override
//                        public void onResponse(Call<MedsResponse> call, Response<MedsResponse> response) {
//                            medslist=response.body().getData();
//                            medicaitonsadapter=new MedicationListAdapter(getActivity(),medslist);
//                            medicationsrecycler.setAdapter(medicaitonsadapter);
//
//                        }
//                        @Override
//                        public void onFailure(Call<MedsResponse> call, Throwable t) {
//                            Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    });
                }else{
                    patno=result.getContents();
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    Call<PatientDataResponse> call = APIClient.getInstance().getApi().PatientList(auth,patno);
                    call.enqueue(new Callback<PatientDataResponse>() {
                        @Override
                        public void onResponse(Call<PatientDataResponse> call, Response<PatientDataResponse> response) {
                            patientList=response.body().getData();
                            adapter=new PatientListAdapter(getActivity(),patientList);
                            recyclerView.setAdapter(adapter);

                        }

                        @Override
                        public void onFailure(Call<PatientDataResponse> call, Throwable t) {
                            Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }



            }

        }
        else {
            Toast.makeText(getActivity(), result.getContents(), Toast.LENGTH_SHORT).show();
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
