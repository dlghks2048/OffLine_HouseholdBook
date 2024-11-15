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

        // 데이터 준비 (예시)
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0f, 1f));
        entries.add(new Entry(1f, 2f));
        entries.add(new Entry(2f, 0f));
        entries.add(new Entry(3f, 4f));

        // 데이터 세트 만들기
        LineDataSet dataSet = new LineDataSet(entries, "Example Data");
        LineData lineData = new LineData(dataSet);

        // 그래프에 데이터 적용
        lineChart.setData(lineData);

        // 그래프 업데이트
        lineChart.invalidate();
    }
}
