package net.devx1.expensemanager.view;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.devx1.expensemanager.ExpDataAdapter;
import net.devx1.expensemanager.R;
import net.devx1.expensemanager.db.sqlite.SqliteOps;
import net.devx1.expensemanager.model.Expense;

import java.util.ArrayList;
import java.util.List;

public class ShowAllExpensesActivity extends AppCompatActivity {
	private Context context = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_all_expenses);

		final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

		reference.addListenerForSingleValueEvent(
			new ValueEventListener() {
				@Override
				public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
					List<Expense> expenses = new ArrayList<>();

					for (DataSnapshot ds : dataSnapshot.getChildren()) {
						if (!ds.getKey().equals("nextId")) {
							expenses.add(
								new Expense(
									Long.parseLong(ds.getKey()),
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

					ExpDataAdapter adapter = new ExpDataAdapter(context, R.layout.data_item, expenses);
					ListView dataList = findViewById(R.id.dataList);
					dataList.setAdapter(adapter);
				}

				@Override
				public void onCancelled(@NonNull DatabaseError databaseError) {
					Log.d("MYDATABASE", databaseError.getMessage());
				}
			}
		);
	}
}
