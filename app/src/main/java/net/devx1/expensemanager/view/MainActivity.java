package net.devx1.expensemanager.view;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.devx1.expensemanager.R;

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

    private void updatePercentages(){
	    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

	    reference.addListenerForSingleValueEvent(
		    new ValueEventListener() {
			    @SuppressLint("SetTextI18n")
			    @Override
			    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				    int[] food = {0, 0};
				    int[] fuel = {0, 0};
				    int[] edu = {0, 0};
				    int[] misc = {0, 0};

				    for (DataSnapshot ds: dataSnapshot.getChildren()){
				    	if (!"nextId".equals(ds.getKey())){
						    if ("Food".equals(ds.child("type").getValue())) {
						    	food[0] += Integer.parseInt(String.valueOf(ds.child("amount").getValue()));
						    	food[1]++;
						    }
						    else if("Fuel".equals(ds.child("type").getValue())){
							    fuel[0] += Integer.parseInt(String.valueOf(ds.child("amount").getValue()));
							    fuel[1]++;
						    }
						    else if ("Education".equals(ds.child("type").getValue())) {
							    edu[0] += Integer.parseInt(String.valueOf(ds.child("amount").getValue()));
							    edu[1]++;
						    }
						    else {
							    misc[0] += Integer.parseInt(String.valueOf(ds.child("amount").getValue()));
							    misc[1]++;
						    }
					    }
				    }

				    int total = fuel[0] + food[0] + edu[0] + misc[0];

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
				        txtFuel.setText(Math.round(fuel[0] * 100.0 / total) + " %");
				        txtFood.setText(Math.round(food[0] * 100.0 / total) + " %");
				        txtEdu.setText(Math.round(edu[0] * 100.0 / total) + " %");
				        txtMisc.setText(Math.round((total - fuel[0] - food[0] - edu[0]) * 100 / total) +
					        " %");
			        }
			    }

			    @Override
			    public void onCancelled(@NonNull DatabaseError databaseError) {
					new AlertDialog.Builder(getBaseContext())
						.setTitle("Oops.. Database Failure")
						.setMessage("Please restart app!")
						.setPositiveButton("Okay",
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
			    }
		    }
	    );
    }
}
