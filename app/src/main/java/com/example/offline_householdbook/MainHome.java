package com.example.offline_householdbook;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.offline_householdbook.db.DBHelper;
import com.example.offline_householdbook.db.FinancialRecord;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.*;

public class MainHome extends AppCompatActivity {
    ImageButton CalendarButton, ReportButton, SettingButton;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        CalendarButton = findViewById(R.id.btn_calendar);
        ReportButton = findViewById(R.id.btn_graph);
        SettingButton = findViewById(R.id.btn_settings);

        CalendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CalendarHome.class);
                startActivity(intent);
            }
        });

        ReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ReportActivity.class);
                startActivity(intent);
            }
        });

        SettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(intent);
            }
        });

    }

    public void showBottomSheetDialog(View view) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = LayoutInflater.from(this).inflate(R.layout.calendar_bottom_sheet, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        // 스피너 (카테고리)
        Spinner spinner = bottomSheetView.findViewById(R.id.CategorySpin);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_items, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // 캘린더 뷰 (홈 화면에서는 현재 날짜를 사용)
        String selectedDate = getCurrentDate();  // 현재 날짜를 가져옴

        // 금액과 메모 입력을 위한 EditText
        EditText moneyEdit = bottomSheetView.findViewById(R.id.moneyEdit);
        EditText textMemo = bottomSheetView.findViewById(R.id.memoEdit);

        // 버튼 클릭 리스너
        Button btnAction1 = bottomSheetView.findViewById(R.id.bottomBtn);
        btnAction1.setOnClickListener(v -> {
            // 사용자 입력 값 가져오기
            String date = selectedDate;
            String category = spinner.getSelectedItem().toString();
            String moneyString = moneyEdit.getText().toString();
            String memo = textMemo.getText().toString();

            // 날짜가 선택되었는지
            if (selectedDate == null){
                Toast.makeText(this, "날자를 선택해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 금액과 메모가 비어있는지 확인
            if (moneyString.isEmpty() || memo.isEmpty()) {
                Toast.makeText(this, "금액과 메모를 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 금액 입력이 숫자인지 확인
            int money;
            try {
                money = Integer.parseInt(moneyString);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "유효한 금액을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // DBHelper 객체 생성
            DBHelper db = new DBHelper(getApplicationContext());

            // FinancialRecord 객체 생성 및 DB에 추가
            FinancialRecord record = new FinancialRecord(date, category, money, memo);
            db.insertFinancialRecord(record);

            // 바텀 시트 닫기
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    // 현재 날짜를 "yyyy-MM-dd" 형식으로 얻는 메서드
    private String getCurrentDate() {
        // 현재 날짜를 가져오기 위한 Calendar 객체
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH); // 월은 0부터 시작
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // 날짜를 "yyyy-MM-dd" 형식으로 포맷팅
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date(year - 1900, month, day)); // Date 객체로 변환 후 포맷
    }
}