package net.devx1.expensemanager.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.devx1.expensemanager.db.sqlite.ExpenseContract;
import net.devx1.expensemanager.db.sqlite.ExpensesDbHelper;
import net.devx1.expensemanager.R;
import net.devx1.expensemanager.db.sqlite.SqliteOps;

import java.util.ArrayList;
import java.util.List;

public class DeleteExpenseActivity extends AppCompatActivity {
	private Context context = this;
	Spinner deleteId;
	MaterialButton btnDelete;
	List<String> idList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_delete_expense);

		deleteId = findViewById(R.id.deleteId);
		btnDelete = findViewById(R.id.btnDeleteExp);
		idList = new ArrayList<>();
		setup();
	}

	private void setup() {
		DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

		reference.addListenerForSingleValueEvent(
			new ValueEventListener() {
				@Override
				public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
					for (DataSnapshot ds : dataSnapshot.getChildren()) {
						if (!"nextId".equals(ds.getKey())) {
							idList.add(String.valueOf(ds.getKey()));
						}
					}

					if (idList.size() == 0) {
						MaterialAlertDialogBuilder builder =
							new MaterialAlertDialogBuilder(context)
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

					// String why?
					ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.spinner_item, idList);
					deleteId.setAdapter(adapter);

					btnDelete.setOnClickListener(
						new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								delete(String.valueOf(deleteId.getSelectedItem()));
							}
						}
					);
				}

				@Override
				public void onCancelled(@NonNull DatabaseError databaseError) {
					Log.d("MYDATABASE", databaseError.getMessage());
				}
			}
		);
	}

	private void delete(String id) {
		DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

		reference.child(id).getRef().removeValue();
		finish();
	}
}
