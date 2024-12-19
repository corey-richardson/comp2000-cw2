package com.example.employeeapp;

import android.util.Log;

public class Employee {
    private int id;
    private String first_name;
    private String last_name;
    private String email;
    private String phone;
    private String address; // WATCH FOR COMMA SEPARATION
    private String job_title;
    private String start_date;
    private String password;
    private int holiday_allowance;
    private String role;

    public Employee(int id,
                    String first_name, String last_name, String email, String phone, String address,
                    String job_title, String start_date, String password, int holiday_allowance, String role) {
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.job_title = job_title;
        this.start_date = start_date;
        this.password = password;
        this.role = role;

        if (holiday_allowance < 0) {
            this.holiday_allowance = 0;
        } else {
            this.holiday_allowance = holiday_allowance;
        }
    }

    public void repr()
    {
        Log.i("EmployeeRepr", first_name + " " + last_name);
    }

    public int getId() {
        return id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getFull_name() {
        return first_name + " " + last_name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone () {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getJob_title() {
        return job_title;
    }

    public String getStart_date() {
        return start_date;
    }

    public String getPassword() {
        return password;
    }

    public int getHoliday_allowance() {
        return holiday_allowance;
    }

    public String getRole() {
        return role;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone (String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        address = address.replaceAll(",", "");
        this.address = address;
    }

    public void setJob_title(String job_title) {
        this.job_title = job_title;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
