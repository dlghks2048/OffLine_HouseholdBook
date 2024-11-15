package com.example.offline_householdbook;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.offline_householdbook.db.DBHelper;
import com.example.offline_householdbook.db.FinancialRecord;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.ArrayList;


public class CalendarHome extends AppCompatActivity {

    private MaterialCalendarView calendarView;
    private RecyclerView recyclerView;
    private FinancialRecordAdapter adapter;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_home);

        // DBHelper 초기화
        dbHelper = new DBHelper(this);

        // CalendarView 초기화 및 날짜 선택 리스너 설정
        calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            String selectedDate = date.getDate().toString();  // 날짜를 "yyyy-MM-dd" 형식으로 변환
            loadRecordsForSelectedDate(selectedDate);
        });

        // RecyclerView 초기화 및 어댑터 설정
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FinancialRecordAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
    }

    // 선택한 날짜의 데이터를 로드하여 RecyclerView에 표시하는 메서드
    private void loadRecordsForSelectedDate(String date) {
        ArrayList<FinancialRecord> records = dbHelper.selectFinancialRecordsByDate(date);
        adapter.updateData(records); // 어댑터 데이터 업데이트
    }
}
