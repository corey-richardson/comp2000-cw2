package com.example.employeeapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.util.List;

public class PtoAdapter extends BaseAdapter {

    private final DatabaseHelper databaseHelper;
    private final Context context;
    private final List<PtoRequest> ptoRequestList;

    public PtoAdapter(Context context, List<PtoRequest> ptoRequestList) {
        this.context = context;
        this.ptoRequestList = ptoRequestList;
        this.databaseHelper = DatabaseHelper.getInstance(context);
    }

    @Override
    public int getCount() {
        return ptoRequestList.size();
    }

    @Override
    public Object getItem(int position) {
        return ptoRequestList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return ptoRequestList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.pto_list_item, parent, false);
        }

        PtoRequest ptoRequest = ptoRequestList.get(position);

        String ptoDateRange = context.getString(R.string.pto_request_dates, ptoRequest.getStartDate(), ptoRequest.getEndDate());
        TextView ptoDateRangeTextField = convertView.findViewById(R.id.pto_start_end_date);
        TextView ptoStatusTextField = convertView.findViewById(R.id.pto_status);
        TextView ptoCommentTextField = convertView.findViewById(R.id.pto_comment);

        Button cancelButton = convertView.findViewById(R.id.cancel_button);
        Button editButton = convertView.findViewById(R.id.edit_button);
        Button resubmitButton = convertView.findViewById(R.id.resubmit_button);

        ptoDateRangeTextField.setText(ptoDateRange);
        ptoStatusTextField.setText(ptoRequest.getStatus());
        ptoCommentTextField.setText(ptoRequest.getRequestComment());

        switch (ptoRequest.getStatus()) {
            case "Approved":
                ptoStatusTextField.setTextColor(Color.parseColor("#06402B"));
                ptoStatusTextField.setTypeface(null, Typeface.BOLD);
                cancelButton.setVisibility(View.GONE);
                editButton.setVisibility(View.GONE);
                resubmitButton.setVisibility(View.GONE);
                break;
            case "Denied":
                ptoStatusTextField.setTextColor(Color.parseColor("#FF0000"));
                ptoStatusTextField.setTypeface(null, Typeface.BOLD);
                cancelButton.setVisibility(View.GONE);
                editButton.setVisibility(View.GONE);
                resubmitButton.setVisibility(View.VISIBLE);
                break;
            default:
                resubmitButton.setVisibility(View.GONE);
                break;
        }

        cancelButton.setOnClickListener(v -> {

            // https://developer.android.com/develop/ui/views/components/dialogs
            AlertDialog.Builder confirmDeletion = new AlertDialog.Builder(context);
            confirmDeletion.setMessage("Confirm cancellation?");
            confirmDeletion.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cancelPtoRequest(ptoRequest);
                    ptoRequestList.remove(position);
                    notifyDataSetChanged(); // Refreshes ListView
                    Toast.makeText(context, "Cancelled PTO Request.", Toast.LENGTH_SHORT).show();
                }
            });
            confirmDeletion.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            confirmDeletion.show();
        });

        editButton.setOnClickListener(v -> {
            Intent iLaunchEditPtoRequest = new Intent(context, EditPtoRequest.class);
            iLaunchEditPtoRequest.putExtra("ptoRequestId", ptoRequest.getId());
            iLaunchEditPtoRequest.putExtra("startDate", ptoRequest.getStartDate());
            iLaunchEditPtoRequest.putExtra("endDate", ptoRequest.getEndDate());
            iLaunchEditPtoRequest.putExtra("requestComment", ptoRequest.getRequestComment());
            context.startActivity(iLaunchEditPtoRequest);
        });

        return convertView;
    }

    private void cancelPtoRequest(PtoRequest ptoRequest) {
        try {
            databaseHelper.deletePtoRequest(ptoRequest.getId());
        } catch (SQLException e) { // Propagated from DatabaseHelper::deletePtoRequest to UI Layer
            Toast.makeText(context, "Failed to cancel PTO request,", Toast.LENGTH_SHORT).show();
        }
    }
}
