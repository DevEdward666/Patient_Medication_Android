package com.example.newpc.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newpc.model.LogsModel;
import com.example.newpc.model.MedsData;
import com.example.newpc.qrcode.R;

import java.util.List;

public class LogsAdapter extends RecyclerView.Adapter<LogsAdapter.LogsViewHolder>{

    private Context mcTx;
    private List<LogsModel> logsModels;

    public LogsAdapter(Context mcTx, List<LogsModel> logsModels) {
        this.mcTx = mcTx;
        this.logsModels = logsModels;
    }

    @NonNull
    @Override
    public LogsAdapter.LogsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mcTx).inflate(R.layout.recycler_logs,parent,false);
        return new LogsAdapter.LogsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogsViewHolder holder, int position) {
        LogsModel logsModel = logsModels.get(position);
        holder.patientname.setText(logsModel.patientname);
        holder.stockdesc.setText(logsModel.stockdesc);
        holder.qty.setText(logsModel.qty);
        holder.urgency.setText(logsModel.urgency);
        holder.givendate.setText(logsModel.givendate);
    }

    @Override
    public int getItemCount() {
        return logsModels.size();
    }
    class LogsViewHolder extends RecyclerView.ViewHolder{
        TextView patientname,stockdesc,qty,urgency,givendate;

        public LogsViewHolder(@NonNull View itemView) {
            super(itemView);
            patientname=itemView.findViewById(R.id.txt_patname_logs);
            stockdesc=itemView.findViewById(R.id.txt_stockdesc_logs);
            qty=itemView.findViewById(R.id.txt_qty_logs);
            urgency=itemView.findViewById(R.id.txt_urgency_logs);
            givendate=itemView.findViewById(R.id.txt_encoded_logs);
        }
    }
}
