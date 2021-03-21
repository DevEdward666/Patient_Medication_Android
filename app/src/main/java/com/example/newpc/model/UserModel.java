package com.example.newpc.model;

public class UserModel {
    public String username;
    public String empname;
    public String moddesc;

    public UserModel(String username, String empname, String moddesc) {
        this.username = username;
        this.empname = empname;
        this.moddesc = moddesc;
    }

    public String getUsername() {
        return username;
    }

    public String getEmpname() {
        return empname;
    }

    public String getModdesc() {
        return moddesc;
    }
}
