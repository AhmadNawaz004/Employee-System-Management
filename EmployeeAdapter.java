package com.example.toheed.myopenhelpercrud;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class EmployeeAdapter extends ArrayAdapter<Employee> {
    private Context context;
    int layoutFile;
    List<Employee> employeeList;
    DatabaseManager databaseManager;

    public EmployeeAdapter(Context context, int layoutFile, List<Employee> employeeList, DatabaseManager databaseManager) {
        super(context, layoutFile, employeeList);

        this.context = context;
        this.layoutFile = layoutFile;
        this.employeeList = employeeList;
        this.databaseManager = databaseManager;
    }


    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutFile, null);

        final Employee employee = employeeList.get(position);

        //getting views
        TextView textViewName = view.findViewById(R.id.textViewName);
        TextView textViewDepartment = view.findViewById(R.id.textViewDepartment);
        TextView textViewSalary = view.findViewById(R.id.textViewSalary);
        TextView textViewJoiningDate = view.findViewById(R.id.textViewJoiningDate);

        //setting valus to views
        textViewName.setText(employee.getName());
        textViewDepartment.setText(employee.getDept());
        textViewSalary.setText(String.valueOf(employee.getSalary()));
        textViewJoiningDate.setText(employee.getJoiningDate());

        Button buttonUpdate = view.findViewById(R.id.buttonUpdate);
        Button buttonDelete = view.findViewById(R.id.buttonDelete);

        //on button click
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEmployee(employee);
            }
        });

        //on button click
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEmployee(employee);
            }
        });

        return view;
    }

    private void updateEmployee(final Employee employee) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.update_employee, null);
        builder.setView(view);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        final EditText editTextName = view.findViewById(R.id.editTextName);
        final Spinner spinnerDepartment = view.findViewById(R.id.spinnerDepartment);
        final EditText editTextSalary = view.findViewById(R.id.editTextSalary);
        final Button buttonUpdateEmployee = view.findViewById(R.id.buttonUpdateEmployee);

        //setting values to views
        editTextName.setText(employee.getName());
        editTextSalary.setText(String.valueOf(employee.getSalary()));

        //on button click
        buttonUpdateEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString().trim();
                String department = spinnerDepartment.getSelectedItem().toString().trim();
                String salary = editTextSalary.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    String message;
                    editTextName.setError("please enter name");
                    return;
                }
                if (TextUtils.isEmpty(salary)) {
                    String message;
                    editTextSalary.setError("please enter salary");
                    return;
                }

                if (databaseManager.updateEmployee(employee.getId(), name, department, Double.valueOf(salary))) {
                    Toast.makeText(context, "employee updated", Toast.LENGTH_SHORT).show();
                    loadEmployee();
                }

                alertDialog.dismiss();

            }

        });
    }

    //delete method
    private void deleteEmployee(final Employee employee){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Are you sure?");

        //builtin method of builder
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (databaseManager.deleteEmployee(employee.getId())){
                    loadEmployee();
                    Toast.makeText(context, "employee deleted", Toast.LENGTH_SHORT).show();
                }
            }
        });


        //builtin method of builder
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //fetching data from database
    private void loadEmployee() {
        Cursor cursor = databaseManager.getAllEmployee();
        employeeList.clear();
        if (cursor.moveToFirst()) {
            do {
                employeeList.add(new Employee(cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getDouble(4)));
            } while (cursor.moveToNext());
        }
        notifyDataSetChanged();
    }
}
