package com.example.newpc.model;

public class LogsModel {

    public String patientname;
    public String stockdesc;
    public  String qty;
    public String urgency;
    public String givendate;

    public LogsModel(String patientname, String stockdesc, String qty, String urgency, String givendate) {
        this.patientname = patientname;
        this.stockdesc = stockdesc;
        this.qty = qty;
        this.urgency = urgency;
        this.givendate = givendate;
    }

    public void setPatientname(String patientname) {
        this.patientname = patientname;
    }

    public void setStockdesc(String stockdesc) {
        this.stockdesc = stockdesc;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    public void setGivendate(String givendate) {
        this.givendate = givendate;
    }

    public String getPatientname() {
        return patientname;
    }

    public String getStockdesc() {
        return stockdesc;
    }

    public String getQty() {
        return qty;
    }

    public String getUrgency() {
        return urgency;
    }

    public String getGivendate() {
        return givendate;
    }
}
