package com.example.offline_householdbook;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

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
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_home);

        // DBHelper 초기화
        dbHelper = new DBHelper(getApplicationContext());

        // CalendarView 초기화 및 날짜 선택 리스너 설정
        calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            String selectedDate = date.getDate().toString();  // 날짜를 "yyyy-MM-dd" 형식으로 변환
            loadRecordsForSelectedDate(selectedDate);
        });

        // DBHelper 객체 생성, 생성자 인수는 현재 컨텍스트
        DBHelper db = new DBHelper(getApplicationContext());
        // 메서드 이름은 sql문+테이블이름(+~)
        // insertFinancialRecord는 FinancialRecord객체를 생성하여 전달하면 됨
        db.insertFinancialRecord(new FinancialRecord("2024-11-15", "문구", 10000, "메모"));
        db.insertFinancialRecord(new FinancialRecord("2024-11-15", "교통", 10000, "메모"));
        db.insertFinancialRecord(new FinancialRecord("2024-11-15", "취미", 10000, "메모"));
        db.insertFinancialRecord(new FinancialRecord("2024-11-15", "외식", 10000, "메모"));
        db.insertFinancialRecord(new FinancialRecord("2024-11-15", "게임", 10000, "메모"));
        db.insertFinancialRecord(new FinancialRecord("2024-11-15", "휴가", 10000, "메모"));

        // RecyclerView 초기화 및 어댑터 설정
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FinancialRecordAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        textView = findViewById(R.id.res_txt); // TextView 초기화
    }

    private void loadRecordsForSelectedDate(String date) {
        ArrayList<FinancialRecord> records = dbHelper.selectFinancialRecordsByDate(date);

        Log.d("CalendarHome", "Selected Date: " + date + " -> Records: " + records.size());

        int totalAmount = 0;
        for (FinancialRecord record : records) {
            totalAmount += record.getAmount();
        }

        if (totalAmount>=0)
            textView.setText("수입 : " + totalAmount);
        else
            textView.setText("지출 : "+totalAmount);

        // 합계가 양수이면 파란색, 음수이면 빨간색
        if (totalAmount >= 0) {
            textView.setTextColor(getResources().getColor(android.R.color.holo_blue_light));  // 파란색
        } else {
            textView.setTextColor(getResources().getColor(android.R.color.holo_red_light));  // 빨간색
        }

        // 어댑터 데이터 업데이트
        adapter.updateData(records);
    }
}
