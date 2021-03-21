package com.example.newpc.model;

public class PatientData {
    public String patno;
    public String patname;
    public String sex;
    public String roombedno;
    public String roomno;
    public String nursestation;
    public String birthdate;
    public String civilstatus;
    public String complaint;
    public String admdiagnosis;
    public String medtype;
    public String admdate;
    public String careteam;

    public PatientData(String patno, String patname, String sex, String roombedno, String roomno, String nursestation, String birthdate, String civilstatus, String complaint, String admdiagnosis, String medtype, String admdate, String careteam) {
        this.patno = patno;
        this.patname = patname;
        this.sex = sex;
        this.roombedno = roombedno;
        this.roomno = roomno;
        this.nursestation = nursestation;
        this.birthdate = birthdate;
        this.civilstatus = civilstatus;
        this.complaint = complaint;
        this.admdiagnosis = admdiagnosis;
        this.medtype = medtype;
        this.admdate = admdate;
        this.careteam = careteam;
    }

    public String getPatno() {
        return patno;
    }

    public String getPatname() {
        return patname;
    }

    public String getSex() {
        return sex;
    }

    public String getRoombedno() {
        return roombedno;
    }

    public String getRoomno() {
        return roomno;
    }

    public String getNursestation() {
        return nursestation;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public String getCivilstatus() {
        return civilstatus;
    }

    public String getComplaint() {
        return complaint;
    }

    public String getAdmdiagnosis() {
        return admdiagnosis;
    }

    public String getMedtype() {
        return medtype;
    }

    public String getAdmdate() {
        return admdate;
    }

    public String getCareteam() {
        return careteam;
    }
}
