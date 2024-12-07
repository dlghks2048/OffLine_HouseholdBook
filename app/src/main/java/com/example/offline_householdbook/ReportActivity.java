package com.example.offline_householdbook;

import static com.example.offline_householdbook.MainHome.getCurrentDate;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.offline_householdbook.Calendar.CalendarHome;
import com.example.offline_householdbook.db.DBHelper;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.chip.ChipGroup;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;

public class ReportActivity extends AppCompatActivity {
    ImageButton CalendarButton, ReportButton, SettingButton, mainHomeButton;
    private String TAG = "ReportActivity";
    private LineChart lineChart;
    private Button btnMonth, btnWeek;
    private DBHelper databaseHelper;
    private boolean isMonthlyView; // true = 월간 | false = 주간
    private TextView textweekmonth;
    //그래프 업데이트 변수
    Calendar calendar;
    int currentDay; // 오늘 날짜 (1일부터 31일까지)
    int currentMonth; // 현재 월 (0부터 시작하므로 1을 더해줌)
    int currentYear; // 현재 년도
    int currentWeekDay; // 오늘 요일 (일요일이 1, 월요일이 2, ... 토요일이 7)

    private boolean iscurrentday;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        calendar = Calendar.getInstance();
        currentDay = calendar.get(Calendar.DAY_OF_MONTH); // 오늘 날짜 (1일부터 31일까지)
        currentMonth = calendar.get(Calendar.MONTH) + 1; // 현재 월 (0부터 시작하므로 1을 더해줌)
        currentYear = calendar.get(Calendar.YEAR); // 현재 년도
        currentWeekDay = calendar.get(Calendar.DAY_OF_WEEK); // 오늘 요일 (일요일이 1, 월요일이 2, ... 토요일이 7)
        Log.d(TAG,String.format("초기 날자 : %04d-%02d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
        iscurrentday = true;
        lineChart = findViewById(R.id.lineChart);
        btnMonth = findViewById(R.id.btn_month);
        btnWeek = findViewById(R.id.btn_week);
        textweekmonth = findViewById(R.id.text_week_month);
        databaseHelper = new DBHelper(this);

        //라인 차트 택스트 색상 설정
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(ContextCompat.getColor(this, R.color.textColor));
        xAxis.enableGridDashedLine(8, 24, 0);
        YAxis yLAxis = lineChart.getAxisLeft();
        yLAxis.setTextColor(ContextCompat.getColor(this, R.color.textColor));

        Legend legend = lineChart.getLegend();
        legend.setEnabled(true);

        Description description = new Description();
        description.setText("");
        lineChart.setDescription(description);


        // ChipGroup
        ChipGroup chipGroup = findViewById(R.id.chipGroup);
        final int[] i = {0};// 0은 주간 1은 월간
        chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup chipGroup, int checkedId) {
                if (checkedId == -1) {
                    if(i[0] == 0)
                        chipGroup.check(R.id.btn_week);
                    else
                        chipGroup.check(R.id.btn_month);
                }

                if (checkedId == R.id.btn_week) {
                    // "지출" 선택 시 기존 spinner의 설정 유지
                    i[0] = 0;
                    initiallize();
                    showWeeklyGraph();isMonthlyView = false;
                } else if (checkedId == R.id.btn_month) {
                    // "수입" 선택 시 spinner 초기화 (아이템 1개만 포함)
                    i[0] = 1;
                    initiallize();
                    iscurrentday = true;
                    showMonthlyGraph();isMonthlyView = true;
                }
            }
        });

        // 시작 시 월간 그래프 표시
        showWeeklyGraph(); isMonthlyView = false;
        //toggleButtons(true);

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
//            Intent intent = new Intent(getApplicationContext(), MainHome.class);
//            startActivity(intent);
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

    private void showMonthlyGraph() {
        // 다크모드와 일반모드에 맞게 색상 설정
        int descriptionTextColor = ContextCompat.getColor(this, R.color.textColor); // 설명 텍스트 색상

        ArrayList<Entry> entries = new ArrayList<>();
        currentYear = calendar.get(Calendar.YEAR);
        int cumulativeAmount = 0;  // 누적 금액을 저장할 변수
        if(iscurrentday == false) // 현재 날짜가 아니라면 31을까지 초기화
            currentDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH); // 오늘 날짜 (1일부터 31(30)일까지)
        else
            currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        // 월간 데이터를 getAmountSumForDate를 통해 가져오기
        for (int i = 1; i <= currentDay; i++) {
            // 날짜를 "yyyy-mm-dd" 형식으로 생성
            String date = String.format("%d-%02d-%02d", currentYear, currentMonth, i);
            int dailyAmount = databaseHelper.getAmountSumForDate(date);
            cumulativeAmount += dailyAmount;
            entries.add(new Entry(i, cumulativeAmount));
            Log.d(TAG,String.format("월간 내역 : %d-%02d-%02d\n", currentYear, currentMonth, i));
        }

        // 데이터셋 생성 및 설정
        LineDataSet dataSet = new LineDataSet(entries, "월간 내역"); // 범례 텍스트 설정
        dataSet.setColor( ContextCompat.getColor(this, R.color.report_color_blue));
        dataSet.setCircleColor( ContextCompat.getColor(this, R.color.report_color_blue));
        dataSet.setValueTextColor(descriptionTextColor);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // 범례 텍스트 색상 설정
        Legend legend = lineChart.getLegend();
        legend.setTextColor(descriptionTextColor); // 범례 텍스트 색상을 descriptionTextColor로 변경

        //현재 날자까지 설정
        setXAxisForMonthly(currentDay);

        updateTextViewForMonth();  // 텍스트뷰 업데이트

        // 그래프 갱신
        lineChart.invalidate();
    }





    // 주간 그래프 표시
    private void showWeeklyGraph() {
        // 다크모드와 일반모드에 맞게 색상 설정
        int descriptionTextColor = ContextCompat.getColor(this, R.color.textColor); // 설명 텍스트 색상
        ArrayList<Entry> entries = new ArrayList<>();


        // 오늘이 속한 주의 월요일을 계산
        calendar.add(Calendar.DAY_OF_MONTH, -(currentWeekDay - 2));  // 월요일로 설정 (일요일이 1, 월요일이 2 ... 토요일이 7)
        currentDay = calendar.get(Calendar.DAY_OF_MONTH); // 오늘 날짜 (1일부터 31일까지)
        Log.d(TAG,String.format("속한 주의 월요일 게산 후 : %04d-%02d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
        Log.d(TAG,String.format("현재 날자 %02d", currentDay));
        int cumulativeAmount = 0;  // 누적 금액을 저장할 변수

        // 월요일부터 일요일까지 반복하여 각 날짜의 지출 합계를 누적하여 가져옵니다
        for (int i = 0; i < 7; i++) {
            // 해당 날짜를 가져오기
            String date = String.format("%04d-%02d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
            Log.d(TAG,String.format("주간 내역 : %04d-%02d-%02d\n", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
            int dailyAmount = databaseHelper.getAmountSumForDate(date);  // 해당 날짜의 지출 합계를 가져옴
            cumulativeAmount += dailyAmount;  // 이전까지의 누적 금액에 오늘의 금액을 더함
            entries.add(new Entry(i + 1, cumulativeAmount));  // 날짜를 x축, 누적 지출을 y축에 반영

            // 날짜를 하루씩 더해가며 반복
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        // 데이터셋 생성 및 설정
        LineDataSet dataSet = new LineDataSet(entries, "주간 내역");
        Log.d(TAG,String.format("주간 내역 반복분 후 : %04d-%02d-%02d\n", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));

        dataSet.setColor( ContextCompat.getColor(this, R.color.report_color_red));
        dataSet.setCircleColor( ContextCompat.getColor(this, R.color.report_color_red));
        dataSet.setValueTextColor(descriptionTextColor);


        // LineData에 데이터셋 추가
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);


        // 범례 텍스트 색상 설정
        Legend legend = lineChart.getLegend();
        legend.setTextColor(descriptionTextColor); // 범례 텍스트 색상을 descriptionTextColor로 변경

        // X축, Y축 설정
        setXAxisForWeekly();  // 주간 그래프용 X축 설정
        setYAxis();  // Y축 설정

        //주간 텍스트 갱신
        updateTextViewForWeek();
        // 그래프 갱신
        lineChart.invalidate();
    }



    // 월간 그래프 색상 변경
    private void styleDataSet(LineDataSet dataSet) {
        dataSet.setDrawValues(false);  // 값 라벨 숨기기
        dataSet.setDrawIcons(false);  // 아이콘 숨기기
        if (dataSet.getLabel().equals("월간 내역")) {
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

    // 왼쪽 화살표 클릭 이벤트
    public void onLeftArrowClick(View view) {
        if (isMonthlyView) {
            iscurrentday = false;
            Log.d(TAG,String.format("한달 빼기 전 : %04d-%02d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
            calendar.add(Calendar.MONTH, -1);
            currentMonth = calendar.get(Calendar.MONTH) + 1;
            Log.d(TAG,String.format("한달 빼기 후 : %04d-%02d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
            showMonthlyGraph();  // 월간 그래프 갱신
        } else {
            // 일주일 빼기
            currentDay -= 7;
            calendar.set(Calendar.DAY_OF_MONTH, currentDay); // 코드에서 한번 더해지기 때문에 2주 분량을 뺌
            Log.d(TAG,String.format("%04d-%02d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
            currentDay = calendar.get(Calendar.DAY_OF_MONTH); // 오늘 날짜 (1일부터 31일까지)
            currentMonth = calendar.get(Calendar.MONTH) + 1; // 현재 월 (0부터 시작하므로 1을 더해줌)
            currentYear = calendar.get(Calendar.YEAR); // 현재 년도
            currentWeekDay = calendar.get(Calendar.DAY_OF_WEEK); // 오늘 요일 (일요일이 1, 월요일이 2, ... 토요일이 7)
            Log.d(TAG,String.format("%04d-%02d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
            // 주간 데이터 갱신
            showWeeklyGraph();
        }
    }

    // 오른쪽 화살표 클릭 이벤트
    public void onRightArrowClick(View view) {
        if (isMonthlyView) {
            iscurrentday = false;
            // 한 달 후로 이동
            Log.d(TAG,String.format("한달 더하기 전 : %04d-%02d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
            calendar.add(Calendar.MONTH, 1);
            currentMonth = calendar.get(Calendar.MONTH) + 1;
            Log.d(TAG,String.format("한달 더한 후 : %04d-%02d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
            showMonthlyGraph();  // 월간 그래프 갱신
        } else {
            // 일주일 더하기
            Log.d(TAG,String.format("일주일 더하기 전 : %04d-%02d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
            calendar.add(Calendar.DAY_OF_YEAR, 7);
            currentDay = calendar.get(Calendar.DAY_OF_MONTH); // 오늘 날짜 (1일부터 31일까지)
            currentMonth = calendar.get(Calendar.MONTH) + 1; // 현재 월 (0부터 시작하므로 1을 더해줌)
            currentYear = calendar.get(Calendar.YEAR); // 현재 년도
            currentWeekDay = calendar.get(Calendar.DAY_OF_WEEK); // 오늘 요일 (일요일이 1, 월요일이 2, ... 토요일이 7)
            Log.d(TAG,String.format("일주일 더하기 : %04d-%02d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
            // 주간 데이터 갱신
            showWeeklyGraph();
        }
    }

    // 월간 TextView 업데이트
    private void updateTextViewForMonth() {
        String monthText = String.format("%04d.%02d", currentYear, currentMonth);  // "YYYY.MM" 형식
        textweekmonth.setText(monthText);
    }

    // 주간 TextView 업데이트
    private void updateTextViewForWeek() {
        Log.d(TAG,String.format("업데이트 전 현재 날짜 : %04d-%02d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
        Calendar startOfWeek = (Calendar) calendar.clone();  // 현재 날짜 복사
        startOfWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // 월요일로 설정
        Log.d(TAG,String.format("시작 날짜아앙 : %04d-%02d-%02d", startOfWeek.get(Calendar.YEAR), startOfWeek.get(Calendar.MONTH) + 1, startOfWeek.get(Calendar.DAY_OF_MONTH)));
        // 일주일 후 월요일 날짜 설정
        Calendar endOfWeek = (Calendar) startOfWeek.clone();
        endOfWeek.add(Calendar.WEEK_OF_YEAR, 1);  // 한 주 뒤로 설정
        Log.d(TAG,String.format("끝 날짜아앙 : %04d-%02d-%02d", endOfWeek.get(Calendar.YEAR), endOfWeek.get(Calendar.MONTH) + 1, endOfWeek.get(Calendar.DAY_OF_MONTH)));
        // 출력 형식: 월요일 ~ 월요일
        String weekText = String.format("%02d.%02d ~ %02d.%02d",
                startOfWeek.get(Calendar.MONTH) + 1, startOfWeek.get(Calendar.DAY_OF_MONTH),
                endOfWeek.get(Calendar.MONTH) + 1, endOfWeek.get(Calendar.DAY_OF_MONTH));
        Log.d(TAG, String.format("%02d.%02d ~ %02d.%02d",
                        startOfWeek.get(Calendar.MONTH) + 1, startOfWeek.get(Calendar.DAY_OF_MONTH),
                        endOfWeek.get(Calendar.MONTH) + 1, endOfWeek.get(Calendar.DAY_OF_MONTH)));
        textweekmonth.setText(weekText);
    }

    private void initiallize() {
        // getCurrentDate()로 현재 날짜를 yyyy-MM-dd 형식으로 받음
        String currentday = getCurrentDate(); // 예: "2024-12-07"

        // currentday를 yyyy-MM-dd 형식으로 파싱
        String[] dateParts = currentday.split("-"); // 날짜를 "-" 기준으로 분리
        currentYear = Integer.parseInt(dateParts[0]); // 년도 (2024)
        currentMonth = Integer.parseInt(dateParts[1]); // 월 (12)
        currentDay = Integer.parseInt(dateParts[2]); // 일 (07)

        // 현재 요일 계산 (일요일이 1, 월요일이 2, ... 토요일이 7)
        calendar = Calendar.getInstance();
        calendar.set(currentYear, currentMonth - 1, currentDay); // Calendar 객체에 날짜 설정
        currentWeekDay = calendar.get(Calendar.DAY_OF_WEEK); // 오늘 요일 (일요일이 1, 월요일이 2, ... 토요일이 7)
    }
}