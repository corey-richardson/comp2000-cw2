package com.example.employeeapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.graphics.Color;
import android.graphics.Typeface;
import java.util.List;

public class PtoAdapter extends BaseAdapter {
    private Context context;
    private List<PtoRequest> ptoRequestList;

    public PtoAdapter(Context context, List<PtoRequest> ptoRequestList) {
        this.context = context;
        this.ptoRequestList = ptoRequestList;
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
            convertView = inflater.inflate(R.layout.pto_list_item, null);
        }

        PtoRequest ptoRequest = ptoRequestList.get(position);

        String ptoDateRange = context.getString(R.string.pto_request_dates, ptoRequest.getStart_date(), ptoRequest.getEnd_date());
        TextView ptoDateRangeTextField = convertView.findViewById(R.id.pto_start_end_date);
        TextView ptoStatusTextField = convertView.findViewById(R.id.pto_status);

        Button cancelButton = convertView.findViewById(R.id.cancel_button);
        Button editButton = convertView.findViewById(R.id.edit_button);
        Button resubmitButton = convertView.findViewById(R.id.resubmit_button);

        ptoDateRangeTextField.setText(ptoDateRange);
        ptoStatusTextField.setText(ptoRequest.getStatus());

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

        return convertView;
    }
}
