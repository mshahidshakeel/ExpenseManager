package net.devx1.expensemanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.devx1.expensemanager.model.Expense;

import java.util.List;

public class ExpDataAdapter extends ArrayAdapter<Expense> {
    public ExpDataAdapter(@NonNull Context context, int resource, @NonNull List<Expense> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if(view == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.data_item, null);
        }

        Expense exp = getItem(position);

        TextView txtId = view.findViewById(R.id.txtId);
        TextView txtAmount = view.findViewById(R.id.txtAmount);
        TextView txtType = view.findViewById(R.id.txtType);
        TextView txtDate = view.findViewById(R.id.txtDate);

        txtId.setText(Long.toString(exp.getId()));
        txtAmount.setText(exp.getAmount());
        txtType.setText(exp.getType());
        txtDate.setText(exp.getDate());

        return view;
    }
}
