package com.example.newpc.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.newpc.entities.Patients;
import com.example.newpc.model.LoginUserModel;
import com.example.newpc.model.PatientData;
import com.example.newpc.qrcode.R;

import org.w3c.dom.Text;

import java.util.List;

public class PatientListAdapter extends RecyclerView.Adapter<PatientListAdapter.PatientViewHolder> {

    private Context mcTx;
    private List<PatientData> patientlist;

    public PatientListAdapter(Context mcTx, List<PatientData> patientlist) {
        this.mcTx = mcTx;
        this.patientlist = patientlist;
    }

    @NonNull
    @Override
    public PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view=LayoutInflater.from(mcTx).inflate(R.layout.recyclerview_patients,parent,false);
        return new PatientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientViewHolder holder, int position) {
        PatientData patientData = patientlist.get(position);
        holder.patno.setText(patientData.patno);
        holder.patname.setText(patientData.patname);
        holder.sex.setText(patientData.sex);
        holder.roombedno.setText(patientData.roombedno);
        holder.roomno.setText(patientData.roomno);
    }

    @Override
    public int getItemCount() {
        Log.d("size_patient",patientlist.size()+" ");
        return patientlist.size();
    }

    class PatientViewHolder extends RecyclerView.ViewHolder{
        TextView patno,patname,sex,roombedno,roomno;

        public PatientViewHolder(@NonNull View itemView) {
            super(itemView);
            patno=itemView.findViewById(R.id.txt_patno);
            patname=itemView.findViewById(R.id.txt_patname);
            sex=itemView.findViewById(R.id.txt_sex);
            roombedno=itemView.findViewById(R.id.txt_roombedno);
            roomno=itemView.findViewById(R.id.txt_roomno);
        }
    }

}
