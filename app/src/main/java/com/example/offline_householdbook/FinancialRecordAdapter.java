package com.example.offline_householdbook;

// FinancialRecordAdapter.java
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.offline_householdbook.db.FinancialRecord;

import java.util.ArrayList;
import java.util.List;

public class FinancialRecordAdapter extends RecyclerView.Adapter<FinancialRecordAdapter.ViewHolder> {

    // OnItemClickListener 인터페이스 정의
    public interface OnItemClickListener {
        void onItemClick(FinancialRecord record);
        void onDeleteClick(FinancialRecord record);
    }

    private ArrayList<FinancialRecord> records;
    private OnItemClickListener listener;

    public FinancialRecordAdapter(ArrayList<FinancialRecord> records) {
        this.records = records;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_financial_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FinancialRecord record = records.get(position);
        holder.tvDate.setText(record.getDate());
        holder.tvCategory.setText(record.getCategoryName());
        holder.tvAmount.setText("₩ " + record.getAmount());
        holder.tvMemo.setText(record.getMemo());

        holder.minusButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(record);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(record);
            }
        });
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    public void updateData(ArrayList<FinancialRecord> newRecords) {
        this.records = newRecords;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvCategory, tvAmount, tvMemo;
        ImageButton minusButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvMemo = itemView.findViewById(R.id.tv_memo);
            minusButton = itemView.findViewById(R.id.minusButton);
        }
    }
}