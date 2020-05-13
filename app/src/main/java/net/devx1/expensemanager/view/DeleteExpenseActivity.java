package net.devx1.expensemanager.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

import net.devx1.expensemanager.ExpenseContract;
import net.devx1.expensemanager.ExpensesDbHelper;
import net.devx1.expensemanager.R;

import java.util.ArrayList;
import java.util.List;

public class DeleteExpenseActivity extends AppCompatActivity {
    Spinner deleteId;
    MaterialButton btnDelete;
    List<String> idList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_expense);

        deleteId = findViewById(R.id.deleteId);
        btnDelete = findViewById(R.id.btnDeleteExp);
        getData();

        if(idList.size() == 0){
            MaterialAlertDialogBuilder builder =
                    new MaterialAlertDialogBuilder(this)
                    .setTitle("Oh No..")
                    .setMessage("There no expense record available to delete.")
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

            deleteId.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);

            return;
        }

        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.spinner_item, idList);
        deleteId.setAdapter(adapter);

        btnDelete.setOnClickListener(
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
                ExpenseContract.ExpensesTable._ID
        };

        Cursor cursor = db.query(
                ExpenseContract.ExpensesTable.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        idList = new ArrayList<>();

        while(cursor.moveToNext()) {
            idList.add(
                    cursor.getString(cursor.getColumnIndexOrThrow(ExpenseContract.ExpensesTable._ID))
            );
        }

        cursor.close();
    }

    private void dbOperations(){
        ExpensesDbHelper helper = new ExpensesDbHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();


        String whereClause = ExpenseContract.ExpensesTable._ID + " = ? ";
        String[] whereArgs = { deleteId.getSelectedItem().toString() };

        long updateStatus = db.delete(ExpenseContract.ExpensesTable.TABLE_NAME, whereClause, whereArgs);

        if(updateStatus == -1){
            Toast.makeText(this, "Failed to insert", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}
