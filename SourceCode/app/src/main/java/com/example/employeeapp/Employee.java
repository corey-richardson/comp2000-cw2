package com.example.employeeapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class Employee {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String department;
    private float salary;
    private String startDate;
    private int holidayAllowance;
    private String password;
    private String role;
    private UserSettings userSettings;

    public Employee( int id,
                    String firstName, String lastName, String email, String department,
                    Float salary, String startDate, int holidayAllowance,
                    String password, String role) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.department = department;
        this.salary = salary;
        this.startDate = startDate;

        this.password = password;
        this.role = role;

        this.holidayAllowance = Math.max(holidayAllowance, 0); // Returns whichever is higher
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

    public String getDepartment() {
        return department;
    }

    public float getSalary() {
        return salary;
    }

    public String getStartDate() {
        return startDate;
    }
    public int getHolidayAllowance() {
        return holidayAllowance;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public UserSettings getUserSettings() {
        return userSettings;
    }

    public void setFirstName(String first_name) {
        this.firstName = first_name;
    }

    public void setLastName(String last_name) {
        this.lastName = last_name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setSalary(float salary) {
        this.salary = salary;
    }

    public void setHolidayAllowance(int holidayAllowance) {
        this.holidayAllowance = Math.max(holidayAllowance, 0);;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        String[] roles = {"Admin", "Employee"};
        if (Arrays.asList(roles).contains(role)) {
            this.role = role;
        }
    }
}
