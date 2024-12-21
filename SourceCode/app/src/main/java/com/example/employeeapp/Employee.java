package com.example.employeeapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Employee {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address; // WATCH FOR COMMA SEPARATION
    private String jobTitle;
    private String startDate;
    private String password;
    private int holidayAllowance;
    private String role;
    private UserSettings userSettings;

    public Employee( int id,
                    String firstName, String lastName, String email, String phone, String address,
                    String jobTitle, String startDate, String password, int holiday_allowance,
                    String role) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.jobTitle = jobTitle;
        this.startDate = startDate;
        this.password = password;
        this.role = role;

        if (holiday_allowance < 0) {
            this.holidayAllowance = 0;
        } else {
            this.holidayAllowance = holiday_allowance;
        }
    }

    public void repr()
    {
        Log.i("EmployeeRepr", firstName + " " + lastName);
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
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
        return jobTitle;
    }

    public String getStart_date() {
        return startDate;
    }

    public String getPassword() {
        return password;
    }

    public int getHolidayAllowance() {
        return holidayAllowance;
    }

    public String getRole() {
        return role;
    }

    public UserSettings getUserSettings() { return userSettings; }

    public void setFirstName(String first_name) {
        this.firstName = first_name;
    }

    public void setLastName(String last_name) {
        this.lastName = last_name;
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

    public void setJobTitle(String job_title) {
        this.jobTitle = job_title;
    }

    public void setStartDate(String start_date) {
        this.startDate = start_date;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
