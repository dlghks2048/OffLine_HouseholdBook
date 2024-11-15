package com.example.offline_householdbook;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;

public class ReportActivity extends AppCompatActivity {

    private LineChart lineChart;  // LineChart 객체
    private Button btnMonth, btnWeek;  // 버튼 객체

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        // 버튼과 그래프 참조
        lineChart = findViewById(R.id.lineChart);
        btnMonth = findViewById(R.id.btn_month);
        btnWeek = findViewById(R.id.btn_week);

        // 월간 그래프 버튼 클릭 시 월간 그래프 표시
        btnMonth.setOnClickListener(v -> {
            showMonthlyGraph();
            toggleButtons(true);  // 월간 버튼 비활성화
        });

        // 주간 그래프 버튼 클릭 시 주간 그래프 표시
        btnWeek.setOnClickListener(v -> {
            showWeeklyGraph();
            toggleButtons(false);  // 주간 버튼 비활성화
        });

        // 기본적으로 월간 그래프를 먼저 표시
        showMonthlyGraph();
        toggleButtons(true);  // 처음에는 월간 그래프가 표시되므로 월간 버튼 비활성화
    }

    // 월간 그래프 표시
    private void showMonthlyGraph() {
        // 월간 지출 내역 (고정된 값)
        ArrayList<Entry> entries = new ArrayList<>();
        float[] monthlyExpenses = {5000, 12000, 8000, 15000, 10000, 20000, 18000, 22000, 9000, 14000,
                16000, 7000, 13000, 19000, 17000, 11000, 25000, 23000, 12000, 10000,
                9000, 8000, 14000, 16000, 11000, 18000, 21000, 19000, 22000, 24000};

        for (int i = 1; i <= monthlyExpenses.length; i++) {
            entries.add(new Entry(i, monthlyExpenses[i - 1]));  // 고정된 지출 값 추가
        }

        LineDataSet dataSet = new LineDataSet(entries, "월간 지출");
        dataSet.setDrawValues(false);  // 데이터 값 표시 안함
        dataSet.setDrawIcons(false);  // 아이콘 표시 안함

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // X축 날짜 대신 1일부터 30일까지 숫자 표시
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);  // X축 간격을 1로 설정
        xAxis.setDrawGridLines(false);  // X축 그리드 선 숨김
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);  // 숫자 표시
            }
        });

        // Y축 그리드 선 유지 및 왼쪽/오른쪽 Y축 가격 표시
        YAxis leftAxis = lineChart.getAxisLeft();
        YAxis rightAxis = lineChart.getAxisRight();
        leftAxis.setDrawGridLines(true);  // Y축 왼쪽 그리드 선 숨김
        rightAxis.setDrawGridLines(true);  // Y축 오른쪽 그리드 선 숨김
        rightAxis.setEnabled(true);  // 오른쪽 Y축 활성화

        // 그래프 제목을 상단 중앙에 표시
        lineChart.getDescription().setText("월간 지출");
        lineChart.getDescription().setTextSize(12f);
        lineChart.getDescription().setPosition(lineChart.getWidth() / 2f, 50);  // 상단 중앙에 제목 배치

        // 범례 설정 비활성화
        Legend legend = lineChart.getLegend();
        legend.setEnabled(false);

        // 그래프 업데이트
        lineChart.invalidate();
    }

    // 주간 그래프 표시
    private void showWeeklyGraph() {
        // 주간 지출 내역 (고정된 값)
        ArrayList<Entry> entries = new ArrayList<>();
        float[] weeklyExpenses = {10000, 15000, 20000, 25000, 12000, 18000, 22000};

        for (int i = 0; i < weeklyExpenses.length; i++) {
            entries.add(new Entry(i, weeklyExpenses[i]));  // 고정된 지출 값 추가
        }

        LineDataSet dataSet = new LineDataSet(entries, "주간 지출");
        dataSet.setDrawValues(false);  // 데이터 값 표시 안함
        dataSet.setDrawIcons(false);  // 아이콘 표시 안함

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // X축에 요일 표시
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);  // X축 간격을 1로 설정
        xAxis.setDrawGridLines(false);  // X축 그리드 선 숨김
        xAxis.setValueFormatter(new ValueFormatter() {
            private final String[] daysOfWeek = {"월", "화", "수", "목", "금", "토", "일"};

            @Override
            public String getFormattedValue(float value) {
                return daysOfWeek[(int) value % daysOfWeek.length];  // 요일 표시
            }
        });

        // Y축 그리드 선 유지 및 왼쪽/오른쪽 Y축 가격 표시
        YAxis leftAxis = lineChart.getAxisLeft();
        YAxis rightAxis = lineChart.getAxisRight();
        leftAxis.setDrawGridLines(true);  // Y축 왼쪽 그리드 선 숨김
        rightAxis.setDrawGridLines(true);  // Y축 오른쪽 그리드 선 숨김
        rightAxis.setEnabled(true);  // 오른쪽 Y축 활성화

        // 그래프 제목을 상단 중앙에 표시
        lineChart.getDescription().setText("주간 지출");
        lineChart.getDescription().setTextSize(12f);
        lineChart.getDescription().setPosition(lineChart.getWidth() / 2f, 50);  // 상단 중앙에 제목 배치

        // 범례 설정 비활성화
        Legend legend = lineChart.getLegend();
        legend.setEnabled(false);

        // 그래프 업데이트
        lineChart.invalidate();
    }

    // 버튼 활성화/비활성화 설정
    private void toggleButtons(boolean isMonthly) {
        if (isMonthly) {
            btnMonth.setEnabled(false);  // 월간 버튼 비활성화
            btnWeek.setEnabled(true);    // 주간 버튼 활성화
        } else {
            btnMonth.setEnabled(true);   // 월간 버튼 활성화
            btnWeek.setEnabled(false);   // 주간 버튼 비활성화
        }
    }
}
