package net.devx1.expensemanager.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import net.devx1.expensemanager.ExpenseContract.ExpensesTable;
import net.devx1.expensemanager.ExpensesDbHelper;
import net.devx1.expensemanager.R;
import net.devx1.expensemanager.model.Expense;

public class EditExpenseActivity extends AppCompatActivity {
    Spinner selectId;
    TextInputEditText amount;
    Spinner type;
    MaterialTextView dateText;
    MaterialButton changeDate;
    List<Expense> expenses;
    MaterialButton btnChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_expense);

        selectId = findViewById(R.id.selectId);
        amount = findViewById(R.id.editAmount);
        type = findViewById(R.id.editType);
        dateText = findViewById(R.id.editDateText);
        changeDate = findViewById(R.id.editBtnDate);
        btnChange = findViewById(R.id.btnEditExp);
        getData();

        if(expenses.size() == 0){
            MaterialAlertDialogBuilder builder =
                    new MaterialAlertDialogBuilder(this)
                    .setTitle("Oh No..")
                    .setMessage("There is no expense record available to edit.")
                    .setPositiveButton("Return to Home",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                    .setOnCancelListener(
                            new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    finish();
                                }
                            }
                    );
            builder.create().show();

            selectId.setVisibility(View.GONE);
            amount.setVisibility(View.GONE);
            type.setVisibility(View.GONE);
            dateText.setVisibility(View.GONE);
            changeDate.setVisibility(View.GONE);
            btnChange.setVisibility(View.GONE);

            return;
        }

        List<String> ids = new ArrayList<>();
        for(int i=0; i<expenses.size(); ++i){
            ids.add(Long.toString(expenses.get(i).getId()));
        }
        ArrayAdapter<String> idAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, ids);
        selectId.setAdapter(idAdapter);

        List<String> types = new ArrayList<>();
        types.add("Fuel");
        types.add("Food");
        types.add("Education");
        types.add("Misc.");
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, types);
        type.setAdapter(typeAdapter);

        if (expenses.size() > 0){
            initializeAt(0);
        }

        selectId.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        initializeAt(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );

        changeDate.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatePickerDialog dialog =
                                new DatePickerDialog(
                                        EditExpenseActivity.this,
                                        new DatePickerDialog.OnDateSetListener() {
                                            @Override
                                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                                operation(year, month, dayOfMonth);
                                            }
                                        },
                                        Calendar.getInstance().get(Calendar.YEAR),
                                        Calendar.getInstance().get(Calendar.MONTH),
                                        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                                );
                        dialog.show();
                    }
                }
        );

        btnChange.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dbOperations();
                    }
                }
        );
    }

    private void getData(){
        ExpensesDbHelper helper = new ExpensesDbHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();

        String[] projection = {
                ExpensesTable._ID,
                ExpensesTable.COLUMN_AMOUNT,
                ExpensesTable.COLUMN_TYPE,
                ExpensesTable.COLUMN_DATE
        };

        Cursor cursor = db.query(
                ExpensesTable.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        expenses = new ArrayList<>();

        while(cursor.moveToNext()) {
            expenses.add(new Expense (
                    Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(ExpensesTable._ID))),
                    cursor.getString(cursor.getColumnIndexOrThrow(ExpensesTable.COLUMN_AMOUNT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(ExpensesTable.COLUMN_TYPE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(ExpensesTable.COLUMN_DATE))
            ));
        }

        cursor.close();
    }

    private void initializeAt(int index){
        amount.setText(expenses.get(index).getAmount());

        if(expenses.get(index).getType().equals("Fuel"))
            type.setSelection(0);
        else if (expenses.get(index).getType().equals("Food"))
            type.setSelection(1);
        else if (expenses.get(index).getType().equals("Education"))
            type.setSelection(2);
        else if (expenses.get(index).getType().equals("Misc."))
            type.setSelection(3);

        dateText.setText(expenses.get(index).getDate());
    }

    private void operation(int year, int month, int day){
        String date = "";

        if(day < 10)
            date += "0";
        date += day + "/";

        if (month < 10)
            date += "0";
        date += (month+1) + "/" + year;

        dateText.setText(date);
    }

    private void dbOperations(){
        if(amount.getText().equals("")){
            return;
        }

        ExpensesDbHelper helper = new ExpensesDbHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ExpensesTable.COLUMN_AMOUNT, Integer.parseInt(amount.getText().toString()));
        values.put(ExpensesTable.COLUMN_TYPE, type.getSelectedItem().toString());
        values.put(ExpensesTable.COLUMN_DATE, dateText.getText().toString());

        String whereClause = ExpensesTable._ID + " = ? ";
        String[] whereArgs = { selectId.getSelectedItem().toString() };

        long updateStatus = db.update(ExpensesTable.TABLE_NAME, values, whereClause, whereArgs);

        if(updateStatus == -1){
            Toast.makeText(this, "Failed to insert", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}
