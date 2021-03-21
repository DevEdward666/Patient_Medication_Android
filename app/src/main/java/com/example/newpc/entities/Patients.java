package com.example.newpc.entities;
import com.example.newpc.model.PatientData;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Patients {
public boolean success;
public String message;
public List<PatientData> data;

    public Patients(boolean success, String message, List<PatientData> data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<PatientData> getData() {
        return data;
    }

    public void setData(List<PatientData> data) {
        this.data = data;
    }
}
