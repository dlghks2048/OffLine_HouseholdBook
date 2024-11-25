package com.example.offline_householdbook;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.offline_householdbook.Calendar.CalendarHome;
import com.example.offline_householdbook.db.DBHelper;
import com.example.offline_householdbook.db.FinancialRecord;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import java.util.Calendar;


import java.util.ArrayList;

public class ReportActivity extends AppCompatActivity {
    ImageButton CalendarButton, ReportButton, SettingButton, mainHomeButton;
    private LineChart lineChart;
    private Button btnMonth, btnWeek;
    private DBHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        lineChart = findViewById(R.id.lineChart);
        btnMonth = findViewById(R.id.btn_month);
        btnWeek = findViewById(R.id.btn_week);

        databaseHelper = new DBHelper(this);

        btnMonth.setOnClickListener(v -> {
            showMonthlyGraph();  // 월간 데이터 표시
            toggleButtons(true);  // 월간 버튼 활성화, 주간 비활성화
        });

        btnWeek.setOnClickListener(v -> {
            showWeeklyGraph();  // 주간 데이터 표시
            toggleButtons(false);  // 주간 버튼 활성화, 월간 비활성화
        });

        // 시작 시 월간 그래프 표시
        showMonthlyGraph();
        toggleButtons(true);

        // 버튼 클릭 이벤트 설정
        setupNavigationButtons();
    }

    // 버튼 클릭 시 네비게이션 처리
    private void setupNavigationButtons() {
        CalendarButton = findViewById(R.id.btn_calendar);
        mainHomeButton = findViewById(R.id.btn_home);
        SettingButton = findViewById(R.id.btn_settings);
        ReportButton = findViewById(R.id.btn_graph);

        CalendarButton.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), CalendarHome.class);
            startActivity(intent);
        });

        mainHomeButton.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MainHome.class);
            startActivity(intent);
            finish();
        });

        SettingButton.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
            startActivity(intent);
            finish();
        });

        // 현재 페이지는 리포트 페이지이므로 버튼을 강조함
        ReportButton.setBackgroundColor(ContextCompat.getColor(this, R.color.bright_background_color));
        Drawable drawable = ReportButton.getDrawable();
        ColorFilter filter = new LightingColorFilter(0xFFFFFFFF, 0x00333333);  // 밝기 효과 추가
        drawable.setColorFilter(filter);
        ReportButton.setImageDrawable(drawable);
    }

    // 월간 그래프 표시
    private void showMonthlyGraph() {
        ArrayList<Entry> entries = new ArrayList<>();

        // 현재 날짜를 얻기 위해 Calendar 사용
        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH); // 오늘 날짜 (1일부터 31일까지)

        // 시작 날짜는 매월 1일로 설정
        String startDate = "2024-11-01";  // 월간 시작 날짜 (11월 1일로 고정)
        String endDate = String.format("2024-11-%02d", currentDay);  // 종료 날짜는 오늘 날짜 (11월 XX일 형식)

        // 월간 데이터를 getAmountSumForDate를 통해 가져오기
        int totalAmount = 0;
        for (int i = 1; i <= currentDay; i++) {
            String date = String.format("2024-11-%02d", i);  // 날짜를 "2024-11-01", "2024-11-02" 형식으로 생성
            totalAmount = databaseHelper.getAmountSumForDate(date);  // 해당 날짜의 지출 합계를 가져옴
            entries.add(new Entry(i, totalAmount));  // 날짜를 x축, 지출 합계를 y축에 반영
        }

        // 데이터셋 생성 및 설정
        LineDataSet dataSet = new LineDataSet(entries, "월간 지출");
        styleDataSet(dataSet);

        // LineData에 데이터셋 추가
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // X축, Y축 설정
        setXAxisForMonthly(currentDay); // 오늘 날짜를 기준으로 x축 설정
        setYAxis();

        // 그래프 갱신
        lineChart.invalidate();
    }

    // 주간 그래프 표시
    private void showWeeklyGraph() {
        ArrayList<Entry> entries = new ArrayList<>();

        // 주간 날짜 범위 설정 (예: 2024-11-01부터 2024-11-07까지)
        String startDate = "2024-11-01";  // 주간 시작 날짜
        String endDate = "2024-11-07";    // 주간 끝 날짜

        // 주간 데이터를 getAmountSumForDate를 통해 가져오기
        int totalAmount = 0;
        for (int i = 1; i <= 7; i++) {
            String date = String.format("2024-11-%02d", i);  // 날짜를 "2024-11-01", "2024-11-02" 형식으로 생성
            totalAmount = databaseHelper.getAmountSumForDate(date);  // 해당 날짜의 지출 합계를 가져옴
            entries.add(new Entry(i, totalAmount));  // 날짜를 x축, 지출 합계를 y축에 반영
        }

        // 데이터셋 생성 및 설정
        LineDataSet dataSet = new LineDataSet(entries, "주간 지출");
        styleDataSet(dataSet);

        // LineData에 데이터셋 추가
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // X축, Y축 설정
        setXAxisForWeekly();
        setYAxis();

        // 그래프 갱신
        lineChart.invalidate();
    }


    // 월간 그래프 색상 변경
    private void styleDataSet(LineDataSet dataSet) {
        dataSet.setDrawValues(false);  // 값 라벨 숨기기
        dataSet.setDrawIcons(false);  // 아이콘 숨기기
        if (dataSet.getLabel().equals("월간 지출")) {
            dataSet.setColor(Color.rgb(0, 0, 255));  // 월간 그래프는 파란색으로 설정
            dataSet.setCircleColor(Color.rgb(0, 0, 255));
        } else {
            dataSet.setColor(Color.rgb(255, 0, 0));  // 주간 그래프는 빨간색으로 설정
            dataSet.setCircleColor(Color.rgb(255,0,0));
        }
        //dataSet.setCircleColor(Color.rgb(0, 0, 255));  // 점 색상도 파란색으로 설정
    }

    // X축 설정 (월간 그래프용)
    private void setXAxisForMonthly(int currentDay) {
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);  // x축 간격 설정 (날짜별로 표시)
        xAxis.setLabelCount(currentDay);  // 최대 오늘 날짜까지 표시
        xAxis.setAxisMinimum(1);  // 1일부터 시작
        xAxis.setAxisMaximum(currentDay);  // 오늘 날짜까지 표시 제한
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);  // 날짜만 표시 (1일부터 오늘 날짜까지)
            }
        });
    }

    // X축 설정 (주간 그래프용)
    private void setXAxisForWeekly() {
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);  // x축 간격 설정 (날짜별로 표시)
        xAxis.setLabelCount(7);  // 7일간 표시
        xAxis.setAxisMinimum(1);  // 1일부터 시작
        xAxis.setAxisMaximum(7);  // 7일까지 표시
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                // 요일 계산 (예: 1 -> 월, 2 -> 화, ... )
                String[] daysOfWeek = {"일", "월", "화", "수", "목", "금", "토"};
                return daysOfWeek[(int) value % 7];  // 주간 라벨로 요일 표시
            }
        });
    }

    // Y축 설정
    private void setYAxis() {
        YAxis leftYAxis = lineChart.getAxisLeft();
        leftYAxis.setGranularity(100f);  // 왼쪽 y축 간격 설정 (금액 단위)
        leftYAxis.setEnabled(true);  // 왼쪽 y축 활성화

        YAxis rightYAxis = lineChart.getAxisRight();
        rightYAxis.setGranularity(100f);  // 오른쪽 y축 간격 설정
        rightYAxis.setEnabled(false);  // 오른쪽 y축 비활성화 (안 보이게)
    }

    // 월간/주간 버튼 활성화 처리
    private void toggleButtons(boolean isMonth) {
        if (isMonth) {
            btnMonth.setEnabled(false);
            btnWeek.setEnabled(true);
        } else {
            btnMonth.setEnabled(true);
            btnWeek.setEnabled(false);
        }
    }
}
