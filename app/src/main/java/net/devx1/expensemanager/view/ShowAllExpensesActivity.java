package net.devx1.expensemanager.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

import net.devx1.expensemanager.ExpDataAdapter;
import net.devx1.expensemanager.ExpenseContract.ExpensesTable;
import net.devx1.expensemanager.ExpensesDbHelper;
import net.devx1.expensemanager.R;
import net.devx1.expensemanager.model.Expense;

public class ShowAllExpensesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_expenses);

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

        List<Expense> expenses = new ArrayList<>();

        while(cursor.moveToNext()) {
            expenses.add(new Expense (
                    Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(ExpensesTable._ID))),
                    cursor.getString(cursor.getColumnIndexOrThrow(ExpensesTable.COLUMN_AMOUNT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(ExpensesTable.COLUMN_TYPE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(ExpensesTable.COLUMN_DATE))
            ));
        }

        cursor.close();

        if(expenses.size() == 0){
            MaterialAlertDialogBuilder builder =
                    new MaterialAlertDialogBuilder(this)
                    .setTitle("Oh Noo..")
                    .setMessage("There is now expense record available to show.")
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

            RelativeLayout body = findViewById(R.id.body);
            body.setVisibility(View.GONE);
        }

        ExpDataAdapter adapter = new ExpDataAdapter(this, R.layout.data_item, expenses);
        ListView dataList = findViewById(R.id.dataList);
        dataList.setAdapter(adapter);
    }
}
