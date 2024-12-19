package com.example.employeeapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

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
            convertView = inflater.inflate(android.R.layout.simple_list_item_2, null);
        }

        PtoRequest ptoRequest = ptoRequestList.get(position);

        TextView text1 = convertView.findViewById(android.R.id.text1);
        TextView text2 = convertView.findViewById(android.R.id.text2);

        text1.setText(ptoRequest.getStart_date() + " | " + ptoRequest.getEnd_date());
        text2.setText(ptoRequest.getStatus());

        return convertView;
    }
}
