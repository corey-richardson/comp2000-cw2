package com.example.employeeapp;

import android.content.Context;
import android.os.AsyncTask;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ApiService {
    private static final String API_URL = "http://10.224.41.11/comp2000";
    private static RequestQueue queue;

    // Singleton
    public static RequestQueue getRequestQueue(Context context) {
        if (queue == null) {
            queue = Volley.newRequestQueue(context);
        }
        return queue;
    }

    // API Methods
    public static void apiHealthCheck(final Context context) {
        new HealthCheck(context).execute();
    }

    public static void apiInsertUser(final Context context) {
        new InsertUserTask(context).execute();
    }

    // AsyncTask Classes
    // API HealthCheck Task
    private static class HealthCheck extends AsyncTask<Void, Void, Void> {
        private final Context context;

        public HealthCheck(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String healthCheckUrl = API_URL + "/health";

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

            return null;
        }
    }

    private static class InsertUserTask extends AsyncTask<Employee, Void, Void> {
        private final Context context;

        public InsertUserTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Employee... employees) {
            Employee employee = employees[0];
            String insertEmployeeUrl = API_URL + "/employees/add";

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
                    Request.Method.POST, insertEmployeeUrl, employeeJson,
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

            return null;
        }
    }

}
