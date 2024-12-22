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

    public static void apiInsertUser(final Context context, Employee employee) {
        new InsertUserTask(context).execute(employee);
    }

    public static void fetchAndStoreEmployees(Context context) {
        new FetchEmployeesTask(context).execute();
    }

    public static void apiUpdateUser(Context context, Employee employee) {
        new UpdateUserTask(context).execute(employee);
    }

    public static void apiDeleteUser(Context context, int userId) {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        Employee employee = databaseHelper.getEmployeeById(userId);
        new DeleteUserTask(context).execute(employee);
    }

    public static void apiDeleteUser(Context context, Employee employee) {
        new DeleteUserTask(context).execute(employee);
    }

    // Helper Method
    private static JSONObject employeeToJson(Employee employee) {
        // Convert Employee to JSON
        Map<String, Object> employeeMap = new HashMap<>();
        employeeMap.put("firstname", employee.getFirstName());
        employeeMap.put("lastname", employee.getLastName());
        employeeMap.put("email", employee.getEmail());
        employeeMap.put("department", employee.getDepartment());
        employeeMap.put("salary", employee.getSalary());
        employeeMap.put("joiningdate", employee.getStartDate());
        employeeMap.put("leaves", employee.getHolidayAllowance());

        return new JSONObject(employeeMap);
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

            JSONObject employeeJson = employeeToJson(employee);

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

    private static class FetchEmployeesTask extends AsyncTask<Void, Void, Void> {
        private final Context context;

        public FetchEmployeesTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String fetchEmployeesUrl = API_URL + "/employees";
            DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET, fetchEmployeesUrl, null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            for (int i = 0; i < response.length(); i++) {

                                try {
                                    JSONObject jsonObject = response.getJSONObject(i);

                                    // Skip if employee already exists
                                    if (databaseHelper.getEmployeeById(jsonObject.getInt("id")) != null) {
                                        databaseHelper.deleteEmployee(jsonObject.getInt("id"));
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

                                    try {
                                        // Insert employee into database
                                        databaseHelper.insertUser(context, employee);
                                        Log.d("AddedEmployee", employee.getFullName());
                                    } catch (Exception e) {
                                        Log.d("FailedEmployeeInsert", employee.getFullName());
                                    }

                                } catch (Exception e) {
                                    Log.e("FetchAndStore", "Error processing employee");
                                }
                            }
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

            return null;
        }
    }

    private static class UpdateUserTask extends AsyncTask<Employee, Void, Void> {
        private final Context context;

        public UpdateUserTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Employee... employees) {
            Employee employee = employees[0];
            String updateEmployeeUrl = API_URL + "/employees/edit/" + employee.getId();

            JSONObject employeeJson = employeeToJson(employee);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.PUT, updateEmployeeUrl, employeeJson,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(context, "Employee updated successfully!", Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("UpdateUser", "Error updating employee", error);
                            Toast.makeText(context, "Failed to update employee.", Toast.LENGTH_SHORT).show();
                        }
                    }
            );

            queue = getRequestQueue(context);
            queue.add(request);
            return null;
        }
    }

    private static class DeleteUserTask extends AsyncTask<Employee, Void, Void> {
        private final Context context;

        public DeleteUserTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Employee... employees) {
            Employee employee = employees[0];
            String deleteEmployeeUrl = API_URL + "/employees/delete/" + employee.getId();

            StringRequest request = new StringRequest(Request.Method.DELETE, deleteEmployeeUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Handle success response
                            // Toast.makeText(context, "Employee deleted successfully!", Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Handle error response
                            Log.e("DeleteUser", "Error deleting employee", error);
                            // Toast.makeText(context, "Failed to delete employee.", Toast.LENGTH_SHORT).show();
                        }
                    }
            );

            queue = getRequestQueue(context);
            queue.add(request);

            return null;
        }
    }
}
