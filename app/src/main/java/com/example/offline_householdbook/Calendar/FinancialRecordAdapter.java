package com.example.offline_householdbook.Calendar;

// FinancialRecordAdapter.java
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.offline_householdbook.R;
import com.example.offline_householdbook.db.FinancialRecord;

import java.util.ArrayList;
import java.util.List;

public class FinancialRecordAdapter extends RecyclerView.Adapter<FinancialRecordAdapter.ViewHolder> {
    private List<FinancialRecord> records;

    // OnItemClickListener 인터페이스 정의
    public interface OnItemClickListener {
        void onItemClick(FinancialRecord record);
        void onDeleteClick(FinancialRecord record);
    }

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

        // 금액 설정 및 색상 변경
        int amount = record.getAmount();
        holder.tvAmount.setText("₩ " + amount);

        // amount가 양수면 파란색, 음수면 빨간색으로 설정
        if (amount > 0) {
            holder.tvAmount.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_blue_dark));
        } else if(amount == 0){
            holder.tvAmount.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.textColor));
        } else {
            holder.tvAmount.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
        }

        holder.tvMemo.setText(record.getMemo());

        // 삭제 버튼 클릭 리스너
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


    public List<FinancialRecord> getData() {
        return records;
    }

    public void updateData(List<FinancialRecord> newData) {
        this.records = newData;
        notifyDataSetChanged();
    }
}