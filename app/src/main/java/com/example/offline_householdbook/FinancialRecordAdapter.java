package com.example.offline_householdbook;

// FinancialRecordAdapter.java
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.offline_householdbook.db.FinancialRecord;

import java.util.ArrayList;
import java.util.List;

public class FinancialRecordAdapter extends RecyclerView.Adapter<FinancialRecordAdapter.ViewHolder> {

    private ArrayList<FinancialRecord> records;

    public FinancialRecordAdapter(ArrayList<FinancialRecord> records) {
        this.records = records;
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
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    // 새로운 데이터를 설정하고 RecyclerView를 갱신하는 메서드
    public void updateData(ArrayList<FinancialRecord> newRecords) {
        this.records = newRecords;
        notifyDataSetChanged(); // 데이터 변경 알림
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvCategory, tvAmount, tvMemo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvMemo = itemView.findViewById(R.id.tv_memo);
        }
    }
}