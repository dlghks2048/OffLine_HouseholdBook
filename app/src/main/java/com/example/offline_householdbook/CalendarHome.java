package com.example.offline_householdbook;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.offline_householdbook.db.DBHelper;
import com.example.offline_householdbook.db.FinancialRecord;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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

        // 이미지 버튼 클릭 리스너 설정
        findViewById(R.id.imageView_Insert).setOnClickListener(v -> showBottomSheetDialog());
    }

    private void loadRecordsForSelectedDate(String date) {
        ArrayList<FinancialRecord> records = dbHelper.selectFinancialRecordsByDate(date);
        dbHelper.updateSettingPassword("");
        dbHelper.updateSettingBalance(0);
        String pass = dbHelper.selectSettingPassword();
        Toast.makeText(getApplicationContext(), pass, Toast.LENGTH_SHORT).show();
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

    // BottomSheetDialog 띄우는 메서드
    private void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = LayoutInflater.from(this).inflate(R.layout.calendar_bottom_sheet, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        Spinner spinner = bottomSheetView.findViewById(R.id.CategorySpin);

        // ArrayAdapter 생성 및 Spinner에 연결
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_items, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // 버튼 클릭 리스너 설정 (옵션)
        Button btnAction1 = bottomSheetView.findViewById(R.id.bottomBtn);
        btnAction1.setOnClickListener(v -> {
            // 액션 1 처리
            bottomSheetDialog.dismiss(); // 바텀 시트 닫기
        });
        bottomSheetDialog.show();
    }

    public void saveRecordForSelectedDate(String date, String categoryName,int amount, String memo){
        FinancialRecord record= new FinancialRecord(date, categoryName,amount,memo);
        dbHelper.insertFinancialRecord(record);
    }

    public void updateRecordForSelectedDate(int id,  String date, String categoryName,int amount, String memo) {
        FinancialRecord record= new FinancialRecord(date, categoryName,amount,memo);
        dbHelper.updateFinancialRecord(id, record);
    }

    public void deleteRecordForSelectedDate(int id){
        dbHelper.deleteFinancialRecord(id);
    }
}
