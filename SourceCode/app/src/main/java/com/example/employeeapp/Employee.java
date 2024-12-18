package com.example.employeeapp;

import java.util.Date;
import android.util.Log;

public class Employee {
    private int id;
    private String first_name;
    private String last_name;
    private String email;
    private String phone;
    private String address; // WATCH FOR COMMA SEPARATION
    private String job_title;
    private Date start_date;
    private String password;
    private int holiday_allowance;
    private String role;

    public Employee(int id,
                    String first_name, String last_name, String email, String phone, String address,
                    String job_title, Date start_date, String password, int holiday_allowance, String role) {
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

    public Date getStart_date() {
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
}
