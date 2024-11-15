package com.example.offline_householdbook;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;

public class ReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_report);

        // 시스템 바 여백 처리
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // LineChart 그래프 설정
        LineChart lineChart = findViewById(R.id.lineChart);

        // 1일부터 25일까지의 지출 내역 (임시 데이터)
        ArrayList<Entry> entries = new ArrayList<>();

        // 예시: 1일부터 25일까지의 지출 내역 (각각 날짜에 지출 금액을 추가)
        entries.add(new Entry(1f, 5000f));  // 1일 차 지출 5000원
        entries.add(new Entry(2f, 2000f));  // 2일 차 지출 2000원
        entries.add(new Entry(3f, 3000f));  // 3일 차 지출 3000원
        entries.add(new Entry(4f, 4000f));  // 4일 차 지출 4000원
        entries.add(new Entry(5f, 2500f));  // 5일 차 지출 2500원
        entries.add(new Entry(6f, 4500f));  // 6일 차 지출 4500원
        entries.add(new Entry(7f, 1500f));  // 7일 차 지출 1500원
        entries.add(new Entry(8f, 3500f));  // 8일 차 지출 3500원
        entries.add(new Entry(9f, 4200f));  // 9일 차 지출 4200원
        entries.add(new Entry(10f, 2200f)); // 10일 차 지출 2200원
        entries.add(new Entry(11f, 3000f)); // 11일 차 지출 3000원
        entries.add(new Entry(12f, 2800f)); // 12일 차 지출 2800원
        entries.add(new Entry(13f, 5000f)); // 13일 차 지출 5000원
        entries.add(new Entry(14f, 1000f)); // 14일 차 지출 1000원
        entries.add(new Entry(15f, 6000f)); // 15일 차 지출 6000원
        entries.add(new Entry(16f, 3200f)); // 16일 차 지출 3200원
        entries.add(new Entry(17f, 5300f)); // 17일 차 지출 5300원
        entries.add(new Entry(18f, 2300f)); // 18일 차 지출 2300원
        entries.add(new Entry(19f, 2900f)); // 19일 차 지출 2900원
        entries.add(new Entry(20f, 4800f)); // 20일 차 지출 4800원
        entries.add(new Entry(21f, 3600f)); // 21일 차 지출 3600원
        entries.add(new Entry(22f, 2400f)); // 22일 차 지출 2400원
        entries.add(new Entry(23f, 5500f)); // 23일 차 지출 5500원
        entries.add(new Entry(24f, 4300f)); // 24일 차 지출 4300원
        entries.add(new Entry(25f, 6000f)); // 25일 차 지출 6000원

        // LineDataSet 만들기 (데이터 세트) - 라벨을 빈 문자열로 설정
        LineDataSet dataSet = new LineDataSet(entries, "");

        // 값 표시 안 하도록 설정
        dataSet.setDrawValues(false);

        // 그래프의 라벨 표시 제거
        dataSet.setDrawIcons(false);   // 아이콘 (그래프 색상) 제거


        // LineData 생성
        LineData lineData = new LineData(dataSet);

        // 그래프에 데이터 적용
        lineChart.setData(lineData);

        // X축 날짜 설정
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);  // X축을 아래에 표시
        xAxis.setGranularity(1f);  // X축 간격을 1로 설정 (1일 단위)
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                // 날짜를 정수로 처리하여 X축에 표시
                if (value >= 1 && value <= 25) {
                    return String.valueOf((int) value);  // 1일부터 25일까지의 날짜 출력
                }
                return "";
            }
        });

        // Y축 그리드 선 숨기기
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);  // Y축 그리드 선 숨기기

        // X축 그리드 선 숨기기
        xAxis.setDrawGridLines(false);  // X축 그리드 선 숨기기

        // 범례 설정을 직접 없애기
        Legend legend = lineChart.getLegend();
        legend.setEnabled(false);  // 범례 전체를 비활성화

        // 그래프 업데이트
        lineChart.invalidate();
    }
}
