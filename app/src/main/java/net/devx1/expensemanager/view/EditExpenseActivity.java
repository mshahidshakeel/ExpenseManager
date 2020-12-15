package net.devx1.expensemanager.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import net.devx1.expensemanager.R;
import net.devx1.expensemanager.model.Expense;

public class EditExpenseActivity extends AppCompatActivity {
	private Context context = this;
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
		expenses = new ArrayList<>();

		setup();

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

		btnChange.setOnClickListener(
			new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Expense e = validateData();
					if (e != null)
						change(e);
				}
			}
		);
	}


	private void initializeAt(int index) {
		amount.setText(expenses.get(index).getAmount());

		switch (expenses.get(index).getType()) {
			case "Fuel":
				type.setSelection(0);
				break;
			case "Food":
				type.setSelection(1);
				break;
			case "Education":
				type.setSelection(2);
				break;
			case "Misc.":
				type.setSelection(3);
				break;
		}

		dateText.setText(expenses.get(index).getDate());
	}

	private void buildDate(int year, int month, int day) {
		String date = "";

		if (day < 10)
			date += "0";
		date += day + "/";

		if (month < 10)
			date += "0";
		date += (month + 1) + "/" + year;

		dateText.setText(date);
	}

	private Expense validateData(){
		if ("".equals(String.valueOf(amount.getText()))){
			return null;
		}

		return new Expense(
			Long.parseLong(String.valueOf(selectId.getSelectedItem())),
			String.valueOf(amount.getText()),
			type.getSelectedItem().toString(),
			dateText.getText().toString()
		);
	}



	private void setup() {
		DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

		reference.addListenerForSingleValueEvent(
			new ValueEventListener() {
				@Override
				public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
					for (DataSnapshot ds: dataSnapshot.getChildren()){
						if (!"nextId".equals(ds.getKey())){
							expenses.add(
								new Expense(
									Long.parseLong(String.valueOf(ds.getKey())),
									String.valueOf(ds.child("amount").getValue()),
									String.valueOf(ds.child("type").getValue()),
									String.valueOf(ds.child("date").getValue())
								)
							);
						}
					}

					if (expenses.size() == 0) {
						MaterialAlertDialogBuilder builder =
							new MaterialAlertDialogBuilder(context)
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
					for (int i = 0; i < expenses.size(); ++i) {
						ids.add(Long.toString(expenses.get(i).getId()));
					}
					ArrayAdapter<String> idAdapter = new ArrayAdapter<>(context,
						R.layout.spinner_item,	ids);
					selectId.setAdapter(idAdapter);

					List<String> types = new ArrayList<>();
					types.add("Fuel");
					types.add("Food");
					types.add("Education");
					types.add("Misc.");
					ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(context,
						R.layout.spinner_item, types);
					type.setAdapter(typeAdapter);

					if (expenses.size() > 0) {
						initializeAt(0);
					}
				}

				@Override
				public void onCancelled(@NonNull DatabaseError databaseError) {
					Log.d("MYDATABASE", databaseError.getMessage());
				}
			}
		);
	}


	private void change(Expense e) {
		DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
		reference.child(String.valueOf(e.getId())).child("amount").setValue(e.getAmount());
		reference.child(String.valueOf(e.getId())).child("type").setValue(e.getType());
		reference.child(String.valueOf(e.getId())).child("date").setValue(e.getDate());
		finish();
	}

}
