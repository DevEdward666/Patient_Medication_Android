package com.example.newpc.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.newpc.entities.Patients;
import com.example.newpc.model.LoginUserModel;
import com.example.newpc.model.MedsData;
import com.example.newpc.model.MedsSpecificData;
import com.example.newpc.model.PatientData;
import com.example.newpc.qrcode.R;

import org.w3c.dom.Text;

import java.util.List;

public class MedicationListAdapter extends RecyclerView.Adapter<MedicationListAdapter.MedicationSpecificViewHolder> {

    private Context mcTx;
    private List<MedsSpecificData> medspecificlist;

    public MedicationListAdapter(Context mcTx, List<MedsSpecificData> medspecificlist) {
        this.mcTx = mcTx;
        this.medspecificlist = medspecificlist;
    }

    @NonNull
    @Override
    public MedicationSpecificViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view=LayoutInflater.from(mcTx).inflate(R.layout.recylerview_medications,parent,false);
        return new MedicationSpecificViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicationSpecificViewHolder holder, int position) {
        MedsSpecificData medspecificData = medspecificlist.get(position);
        holder.medspatno.setText(medspecificData.patno);
        holder.medsstockdesc.setText(medspecificData.stockdesc);
        holder.medsfreqdesc.setText(medspecificData.freqdesc);
    }

    @Override
    public int getItemCount() {
        return medspecificlist.size();
    }

    class MedicationSpecificViewHolder extends RecyclerView.ViewHolder{
        TextView medspatno,medsstockdesc,medsfreqdesc;

        public MedicationSpecificViewHolder(@NonNull View itemView) {
            super(itemView);
            medspatno=itemView.findViewById(R.id.txt_medpatno);
            medsstockdesc=itemView.findViewById(R.id.txt_medstockdesc);
            medsfreqdesc=itemView.findViewById(R.id.txt_medfreqdesc);
        }
    }

}
