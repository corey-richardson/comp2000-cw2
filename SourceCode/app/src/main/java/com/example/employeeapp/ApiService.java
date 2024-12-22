package com.example.employeeapp;

import android.content.Context;
import android.database.SQLException;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApiService {
    private static final String API_URL = "http://10.224.41.11/comp2000";
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static RequestQueue queue;
    private static final Gson gson = new Gson();

    // Singleton
    public static RequestQueue getRequestQueue(Context context) {
        if (queue == null) {
            queue = Volley.newRequestQueue(context);
        }
        return queue;
    }

    // API Methods
    public static void apiHealthCheck(final Context context) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                String healthCheckUrl = API_URL + "/health";
                Log.d("HealthCheckUrl", healthCheckUrl);

                StringRequest stringRequest = new StringRequest(Request.Method.GET, healthCheckUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("VolleyResponse", "Response is: " + response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("VolleyError", "No response!", error);
                                Toast.makeText(context, "No response from API.", Toast.LENGTH_LONG).show();
                            }
                        });

                queue = Volley.newRequestQueue(context);
                queue.add(stringRequest);
            }
        });
    }

    public static void apiInsertUser(Context context, Employee employee) {
        String insertUserUrl = API_URL + "/employees/add";

        // Convert Employee to JSON
        Map<String, Object> employeeMap = new HashMap<>();
        employeeMap.put("firstname", employee.getFirstName());
        employeeMap.put("lastname", employee.getLastName());
        employeeMap.put("email", employee.getEmail());
        employeeMap.put("department", employee.getDepartment());
        employeeMap.put("salary", employee.getSalary());
        employeeMap.put("joiningdate", employee.getStartDate());
        JSONObject employeeJson = new JSONObject(employeeMap);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, insertUserUrl, employeeJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(context, "Employee added successfully!", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("InsertUser", "Error inserting employee", error);
                        Toast.makeText(context, "Failed to add employee.", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        queue = getRequestQueue(context);
        queue.add(request);
    }

    public static void fetchAndStoreEmployees(Context context) {
        String fetchEmployeesUrl = API_URL + "/employees";
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, fetchEmployeesUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Use worker thread
                        executor.execute(() -> {
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject jsonObject = response.getJSONObject(i);

                                    // Skip if employee already exists
                                    if (databaseHelper.getEmployeeById(jsonObject.getInt("id")) != null) {
                                        continue;
                                    }

                                    // Create Employee object
                                    Employee employee = new Employee(
                                            jsonObject.getInt("id"),
                                            jsonObject.getString("firstname"),
                                            jsonObject.getString("lastname"),
                                            jsonObject.getString("email"),
                                            jsonObject.getString("department"),
                                            (float) jsonObject.getDouble("salary"),
                                            jsonObject.getString("joiningdate"),
                                            jsonObject.getInt("leaves"),
                                            "employee_password",
                                            "Employee"
                                    );

                                    // Insert employee into database
                                    databaseHelper.insertUser(employee);

                                } catch (Exception e) {
                                    Log.e("FetchAndStore", "Error processing employee", e);
                                }
                            }
                        });
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("FetchEmployees", "Error fetching employees: " + error.getMessage());
                        Toast.makeText(context, "Failed to fetch employees!", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        queue = getRequestQueue(context);
        queue.add(jsonArrayRequest);
    }
}
