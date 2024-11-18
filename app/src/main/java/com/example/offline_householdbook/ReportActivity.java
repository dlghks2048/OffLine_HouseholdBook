package com.example.offline_householdbook;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
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

import java.util.ArrayList;

public class ReportActivity extends AppCompatActivity {

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

        showMonthlyGraph();  // 처음에는 월간 그래프 표시
        toggleButtons(true);  // 초기 상태로 월간 버튼 활성화
    }

    // 월간 그래프 표시
    private void showMonthlyGraph() {
        ArrayList<Entry> entries = new ArrayList<>();

        // 날짜 범위를 지정하여 DB에서 월간 데이터 가져오기
        String startDate = "2024-11-01";  // 월간 시작 날짜
        String endDate = "2024-11-30";    // 월간 끝 날짜
        ArrayList<FinancialRecord> monthlyExpenses = databaseHelper.selectFinancialRecordsByDate(startDate, endDate);

        // FinancialRecord 객체에서 금액만 사용하여 Entry로 변환
        for (int i = 0; i < monthlyExpenses.size(); i++) {
            entries.add(new Entry(i + 1, monthlyExpenses.get(i).getAmount()));  // 날짜를 x축, 금액을 y축에 반영
        }

        // 데이터셋 생성
        LineDataSet dataSet = new LineDataSet(entries, "월간 지출");
        dataSet.setDrawValues(false);  // 값 라벨 숨기기
        dataSet.setDrawIcons(false);  // 아이콘 숨기기

        // LineData에 데이터셋 추가
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);  // LineChart에 데이터 반영

        // X축, Y축 설정
        setXAxisForMonthly();
        setYAxis();

        lineChart.invalidate();
    }

    // 주간 그래프 표시
    private void showWeeklyGraph() {
        ArrayList<Entry> entries = new ArrayList<>();

        // 날짜 범위를 지정하여 DB에서 주간 데이터 가져오기
        String startDate = "2024-11-01";  // 주간 시작 날짜
        String endDate = "2024-11-07";    // 주간 끝 날짜
        ArrayList<FinancialRecord> weeklyExpenses = databaseHelper.selectFinancialRecordsByDate(startDate, endDate);

        // FinancialRecord 객체에서 금액만 사용하여 Entry로 변환
        for (int i = 0; i < weeklyExpenses.size(); i++) {
            entries.add(new Entry(i + 1, weeklyExpenses.get(i).getAmount()));  // 날짜를 x축, 금액을 y축에 반영
        }

        // 데이터셋 생성
        LineDataSet dataSet = new LineDataSet(entries, "주간 지출");
        dataSet.setDrawValues(false);  // 값 라벨 숨기기
        dataSet.setDrawIcons(false);  // 아이콘 숨기기

        dataSet.setColor(Color.rgb(255, 0, 0));
        dataSet.setCircleColor(Color.rgb(255, 0, 0));

        // LineData에 데이터셋 추가
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);  // LineChart에 데이터 반영

        // X축, Y축 설정
        setXAxisForWeekly();
        setYAxis();


        lineChart.invalidate();
    }

    // X축 설정 (월간 그래프용)
    private void setXAxisForMonthly() {
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);  // x축 간격 설정 (날짜별로 표시)
        xAxis.setLabelCount(10);  // x축 라벨 수
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);  // 날짜만 표시 (월/일 형식)
            }
        });
    }

    // X축 설정 (주간 그래프용)
    private void setXAxisForWeekly() {
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);  // x축 간격 설정 (날짜별로 표시)
        xAxis.setLabelCount(7);  // x축 라벨 수
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                // 요일 계산 (예: 0 -> 월, 1 -> 화, ... )
                String[] daysOfWeek = {"일", "월", "화", "수", "목", "금", "토"};
                return daysOfWeek[(int) value % 7];  // 주간 라벨로 요일 표시
            }
        });
    }

    // Y축 설정
    private void setYAxis() {
        YAxis leftYAxis = lineChart.getAxisLeft();
        leftYAxis.setGranularity(1000f);  // 왼쪽 y축 간격 설정 (금액 단위)
        leftYAxis.setEnabled(true);  // 왼쪽 y축 활성화

        YAxis rightYAxis = lineChart.getAxisRight();
        rightYAxis.setGranularity(1000f);  // 오른쪽 y축 간격 설정 (금액 단위)
        rightYAxis.setEnabled(true);  // 오른쪽 y축 활성화

        // 가로선만 보이도록 설정
        lineChart.getXAxis().setDrawGridLines(false);  // 세로선 제거
        lineChart.getAxisLeft().setDrawGridLines(true);  // 가로선만 표시
        lineChart.getAxisRight().setDrawGridLines(true);  // 오른쪽 y축 가로선만 표시
    }

    // 버튼 상태 토글
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