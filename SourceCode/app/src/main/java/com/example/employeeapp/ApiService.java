package com.example.employeeapp;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

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
                RequestQueue queue = Volley.newRequestQueue(context);
                String healthCheckUrl = API_URL + "/health";
                Log.d("HealthCheckUrl", healthCheckUrl);

                StringRequest stringRequest = new StringRequest(Request.Method.GET, healthCheckUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("VolleyResponse", "Response is: " + response);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VolleyError", "No response!", error);
                        Toast.makeText(context, "No response from API.", Toast.LENGTH_LONG).show();
                    }
                });

                queue.add(stringRequest);
            }
        });
    }

    public static void apiInsertUser(Context context, Employee employee) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Map<String, Object> employeeMap = new HashMap<>();
                JSONObject employeeJson;

                try {
                    employeeMap.put("firstname", employee.getFirstName());
                    employeeMap.put("lastname", employee.getLastName());
                    employeeMap.put("email", employee.getEmail());
                    employeeMap.put("department", employee.getDepartment());
                    employeeMap.put("salary", employee.getSalary());
                    employeeMap.put("joiningdate", employee.getStartDate());
                    JsonElement employeeJsonElement = gson.toJsonTree(employeeMap).getAsJsonObject();
                    employeeJson = new JSONObject(employeeJsonElement.toString());
                } catch (Exception e) {
                    Log.e("ApiInsertUser", "Couldn't write Employee Object to JSONObject.");
                    return;
                }

                Log.d("EmployeeJson", employeeJson.toString());

                String insertUserUrl = API_URL + "/employees/add";
                Log.d("InsertUserUrl", insertUserUrl);

                JsonObjectRequest request = new JsonObjectRequest
                        (Request.Method.POST, insertUserUrl, employeeJson,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Toast.makeText(context, "Success!", Toast.LENGTH_SHORT).show();
                                Log.d("Response", response.toString());
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(context, "Fail!", Toast.LENGTH_SHORT).show();
                            }
                        });

                queue = getRequestQueue(context);
                queue.add(request);
            }
        });
    }
}
