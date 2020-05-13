package net.devx1.expensemanager.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import net.devx1.expensemanager.ExpenseContract;
import net.devx1.expensemanager.ExpensesDbHelper;
import net.devx1.expensemanager.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddExpenseActivity extends AppCompatActivity {
    private MaterialButton btnDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        btnDate = findViewById(R.id.btnDate);

        Spinner spinner = findViewById(R.id.type);
        List<String> catagories = new ArrayList<>();
        catagories.add("Fuel");
        catagories.add("Food");
        catagories.add("Education");
        catagories.add("Misc.");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, catagories);

        spinner.setAdapter(adapter);


        btnDate.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatePickerDialog dialog =
                                new DatePickerDialog(
                                        AddExpenseActivity.this,
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
                        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                        dialog.show();

                    }
                }
        );

        MaterialButton btnAddToDb = findViewById(R.id.btnAddToDb);
        btnAddToDb.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dbOperations();
                    }
                }
        );
    }

    private void operation(int year, int month, int day){
        String date = "";

        if(day < 10)
            date += "0";
        date += day + "/";

        if (month < 10)
            date += "0";
        date += (month+1) + "/" + year;

        MaterialTextView dateText = findViewById(R.id.dateText);
        dateText.setText(date);
        dateText.setVisibility(View.VISIBLE);

        btnDate.setVisibility(View.GONE);
    }

    private void dbOperations(){
        TextInputEditText amount = findViewById(R.id.amount);
        if(amount.getText().equals("")){
            return;
        }

        if(btnDate.getVisibility() == View.VISIBLE){
            return;
        }

        Spinner type = findViewById(R.id.type);
        MaterialTextView date = findViewById(R.id.dateText);


        ExpensesDbHelper helper = new ExpensesDbHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ExpenseContract.ExpensesTable.COLUMN_AMOUNT, Integer.parseInt(amount.getText().toString()));
        values.put(ExpenseContract.ExpensesTable.COLUMN_TYPE, type.getSelectedItem().toString());
        values.put(ExpenseContract.ExpensesTable.COLUMN_DATE, date.getText().toString());

        long newRowId = db.insert(ExpenseContract.ExpensesTable.TABLE_NAME, null, values);

        if(newRowId == -1){
            Toast.makeText(this, "Failed to insert", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}
