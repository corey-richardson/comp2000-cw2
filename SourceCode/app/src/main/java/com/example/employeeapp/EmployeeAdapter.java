package com.example.employeeapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class EmployeeAdapter extends BaseAdapter {
    private final DatabaseHelper databaseHelper;
    private final Context context;
    private final List<Employee> employeeList;

    public EmployeeAdapter(Context context, List<Employee> employeeList) {
        this.context = context;
        this.employeeList = employeeList;
        this.databaseHelper = DatabaseHelper.getInstance(context);
    }

    @Override
    public int getCount() {
        return employeeList.size();
    }

    @Override
    public Object getItem(int position) {
        return employeeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return employeeList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.employee_list_item, parent, false);
        }

        Employee employee = employeeList.get(position);

        String salaryString = context.getString(R.string.salary_string, employee.getSalary());
        String additionalInfo = context.getString(R.string.additional_info, salaryString, employee.getStartDate());

        TextView fullNameTextView = convertView.findViewById(R.id.listItemEmployeeFullName);
        TextView emailTextView = convertView.findViewById(R.id.listItemEmployeeEmail);
        TextView departmentTextView = convertView.findViewById(R.id.listItemEmployeeDepartment);
        TextView additionalInfoTextView = convertView.findViewById(R.id.listItemAdditionalInfo);

        Button deleteButton = convertView.findViewById(R.id.delete_employee_button);
        Button editButton = convertView.findViewById(R.id.edit_employee_button);

        fullNameTextView.setText(employee.getFullName() + " " + employee.getId());
        emailTextView.setText(employee.getEmail());
        departmentTextView.setText(employee.getDepartment());
        additionalInfoTextView.setText(additionalInfo);

        deleteButton.setOnClickListener(v -> {
            AlertDialog.Builder confirmDeletion = new AlertDialog.Builder(context);
            confirmDeletion.setMessage("Confirm deletion?");
            confirmDeletion.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        databaseHelper.deleteEmployee(employee.getId());
                    } catch (Exception e) {
                        Toast.makeText(context, "Failed to delete Employee", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    employeeList.remove(position);
                    notifyDataSetChanged(); // Refreshes ListView
                    Toast.makeText(context, "Deleted Employee", Toast.LENGTH_SHORT).show();
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

        // TO ADD: editButton OnClickListener

        return convertView;
    }
}
