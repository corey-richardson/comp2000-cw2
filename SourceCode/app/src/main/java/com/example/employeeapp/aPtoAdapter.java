package com.example.employeeapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class aPtoAdapter extends BaseAdapter {
    private final DatabaseHelper databaseHelper;
    private final Context context;
    private final List<PtoRequest> aPtoRequestList;

    public aPtoAdapter(Context context, List<PtoRequest> aPtoRequestList) {
        this.context = context;
        this.aPtoRequestList = aPtoRequestList;
        this.databaseHelper = DatabaseHelper.getInstance(context);
    }

    @Override
    public int getCount() {
        return aPtoRequestList.size();
    }

    @Override
    public Object getItem(int position) {
        return aPtoRequestList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return aPtoRequestList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.admin_pto_list_item, parent, false);
        }

        PtoRequest ptoRequest = aPtoRequestList.get(position);
        if (ptoRequest.getStatus().equals("Approved") || ptoRequest.getStatus().equals("Denied")) {
            return new View(context);
        }

        Log.d("RequesterId", Integer.toString(ptoRequest.getRequesterId()));
        Employee requester = databaseHelper.getEmployeeById(ptoRequest.getRequesterId());

        TextView requesterTextView = convertView.findViewById(R.id.apto_employee_name);
        TextView dateRangeTextView = convertView.findViewById(R.id.apto_date_range);
        TextView statusTextView = convertView.findViewById(R.id.apto_status);
        TextView commentTextView = convertView.findViewById(R.id.apto_comment);

        requesterTextView.setText(requester.getFullName());
        dateRangeTextView.setText(context.getString(R.string.pto_request_dates, ptoRequest.getStartDate(), ptoRequest.getEndDate()));
        statusTextView.setText(ptoRequest.getStatus());
        commentTextView.setText(ptoRequest.getRequestComment());

        Button approveButton = convertView.findViewById(R.id.apto_approve_button);
        Button denyButton = convertView.findViewById(R.id.apto_deny_button);

        if (ptoRequest.getStatus().equals("Approved") || ptoRequest.getStatus().equals("Denied")) {
            approveButton.setVisibility(View.GONE);
            denyButton.setVisibility(View.GONE);
        }

        approveButton.setOnClickListener(v -> {
            updateRequestStatus(ptoRequest, "Approved");
        });

        denyButton.setOnClickListener(v -> {
            updateRequestStatus(ptoRequest, "Denied");
        });

        return convertView;
    }


    private void updateRequestStatus(PtoRequest ptoRequest, String updatedStatus) {
        ContentValues statusValue = new ContentValues();
        statusValue.put("status", updatedStatus);

        ptoRequest.setStatus(updatedStatus);
        try (SQLiteDatabase db = databaseHelper.getWritableDatabase()) {
            db.update("PtoRequest", statusValue, "id = ?", new String[]{ Integer.toString(ptoRequest.getId()) });
            Toast.makeText(context, updatedStatus + " PTO Request.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, "Failed to update PTO Request.", Toast.LENGTH_SHORT).show();
            Log.e("UpdateRequestStatus", "Error while updating PTO request status: ", e);
        }

        notifyDataSetChanged();
    }
}
