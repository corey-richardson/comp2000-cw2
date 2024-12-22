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

    public static void apiHealthCheck(final Context context) {
        new HealthCheck(context).execute();
    }

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

}
