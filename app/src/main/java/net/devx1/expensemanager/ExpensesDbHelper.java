package net.devx1.expensemanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.Nullable;
import net.devx1.expensemanager.ExpenseContract.ExpensesTable;

public class ExpensesDbHelper extends SQLiteOpenHelper {
    private final static int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ExpensesRec.db";

    private static final String CREATE_TABLE_EXPENSES =
            "CREATE TABLE " + ExpensesTable.TABLE_NAME + " (" +
                    ExpensesTable._ID + " INTEGER PRIMARY KEY," +
                    ExpensesTable.COLUMN_AMOUNT + " INTEGER," +
                    ExpensesTable.COLUMN_TYPE + " TEXT," +
                    ExpensesTable.COLUMN_DATE + " DATETIME)";

    private final String DROP_TABLE_EXPENSES =
            "DROP TABLE IF EXISTS " + ExpensesTable.TABLE_NAME;

    public ExpensesDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_EXPENSES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_EXPENSES);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (Build.VERSION.SDK_INT >= 28){
            db.disableWriteAheadLogging();
        }
    }
}
