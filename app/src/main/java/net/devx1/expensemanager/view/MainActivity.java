package net.devx1.expensemanager.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import net.devx1.expensemanager.ExpenseContract.ExpensesTable;
import net.devx1.expensemanager.ExpensesDbHelper;
import net.devx1.expensemanager.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialButton btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(
                                MainActivity.this, AddExpenseActivity.class
                        ));
                    }
                }
        );

        MaterialButton btnShowAll = findViewById(R.id.btnShowAll);
        btnShowAll.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(
                                MainActivity.this, ShowAllExpensesActivity.class
                        ));
                    }
                }
        );

        MaterialButton btnEdit = findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(
                                MainActivity.this, EditExpenseActivity.class
                        ));
                    }
                }
        );

        MaterialButton btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(
                                MainActivity.this, DeleteExpenseActivity.class
                        ));
                    }
                }
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
        updatePercentages();
    }

    private int getAmount(String type){
        ExpensesDbHelper helper = new ExpensesDbHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();

        String[] projection = {
                ExpensesTable.COLUMN_AMOUNT
        };

        String selection = ExpensesTable.COLUMN_TYPE + " = ?";
        String[] selectionArgs = { type };

        Cursor cursor = db.query(
                ExpensesTable.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        int sum = 0;

        List itemIds = new ArrayList<>();
        while(cursor.moveToNext()) {
            sum += (int) Long.parseLong(cursor.getString(0));
        }

        cursor.close();

        return sum;
    }

    private void updatePercentages(){
        int fuel = getAmount("Fuel");
        int food = getAmount("Food");
        int edu = getAmount("Education");
        int misc = getAmount("Misc.");
        int total = fuel + food + edu + misc;

        TextView txtFuel = findViewById(R.id.txtFuel);
        TextView txtFood = findViewById(R.id.txtFood);
        TextView txtEdu = findViewById(R.id.txtEdu);
        TextView txtMisc = findViewById(R.id.txtMisc);

        if (total == 0){
            txtFuel.setText("0 %");
            txtFood.setText("0 %");
            txtEdu.setText("0 %");
            txtMisc.setText("0 %");
        }
        else {
            txtFuel.setText((fuel*100/total) + " %");
            txtFood.setText((food*100/total) + " %");
            txtEdu.setText((edu*100/total) + " %");
            txtMisc.setText((total-fuel-food-edu)*100/total + " %");
        }
    }
}
