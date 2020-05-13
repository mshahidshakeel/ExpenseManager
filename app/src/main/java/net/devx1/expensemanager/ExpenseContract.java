package net.devx1.expensemanager;

import android.provider.BaseColumns;

public class ExpenseContract {
    private ExpenseContract(){}

    public class ExpensesTable implements BaseColumns{
        public static final String TABLE_NAME = "expenses";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_AMOUNT = "amount";
        public static final String COLUMN_DATE = "date";
    }
}
