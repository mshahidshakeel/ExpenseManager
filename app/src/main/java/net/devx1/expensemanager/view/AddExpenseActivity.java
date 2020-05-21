package net.devx1.expensemanager.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.devx1.expensemanager.R;
import net.devx1.expensemanager.db.firebase.FirebaseOps;
import net.devx1.expensemanager.db.sqlite.SqliteOps;
import net.devx1.expensemanager.model.Expense;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddExpenseActivity extends AppCompatActivity {
	private MaterialButton btnDate;
	private Context context = this;

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
									buildDate(year, month, dayOfMonth);
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
					addExpense();
				}
			}
		);
	}

	private void buildDate(int year, int month, int day) {
		String date = "";

		if (day < 10)
			date += "0";
		date += day + "/";

		if (month < 10)
			date += "0";
		date += (month + 1) + "/" + year;

		MaterialTextView dateText = findViewById(R.id.dateText);
		dateText.setText(date);
		dateText.setVisibility(View.VISIBLE);

		btnDate.setVisibility(View.GONE);
	}

	private Expense validateData() {
		TextInputEditText amount = findViewById(R.id.amount);
		if (amount.getText().equals("")) {
			return null;
		}

		if (btnDate.getVisibility() == View.VISIBLE) {
			return null;
		}

		Spinner type = findViewById(R.id.type);
		MaterialTextView date = findViewById(R.id.dateText);

		return new Expense(
			amount.getText().toString(),
			type.getSelectedItem().toString(),
			date.getText().toString()
		);
	}


	private void addExpense() {
		final Expense expense = validateData();
		if (expense == null)
			return;

		final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

		reference.child("nextId").addListenerForSingleValueEvent(
			new ValueEventListener() {
				@Override
				public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
					Long id = dataSnapshot.getValue(Long.class);

					if (id != null) {
						reference.child(Long.toString(id)).child("amount").setValue(expense.getAmount());
						reference.child(Long.toString(id)).child("type").setValue(expense.getType());
						reference.child(Long.toString(id)).child("date").setValue(expense.getDate());
						reference.child("nextId").setValue(++id);
					}
				}

				@Override
				public void onCancelled(@NonNull DatabaseError databaseError) {
					Log.d("MYDATABASE", databaseError.getMessage());
				}
			}
		);

		finish();
	}

}
