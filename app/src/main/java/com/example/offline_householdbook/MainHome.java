package com.example.offline_householdbook;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.offline_householdbook.Calendar.CalendarHome;
import com.example.offline_householdbook.db.DBHelper;
import com.example.offline_householdbook.db.FinancialRecord;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.ChipGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainHome extends AppCompatActivity {
    ImageButton CalendarButton, ReportButton, SettingButton, mainHomeButton;
    private DBHelper dbHelper;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, SplashActivity.class);
        startActivity(intent);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // TextView 변수 선언
        TextView wonTextView = findViewById(R.id.won);
        TextView vsTextView = findViewById(R.id.vs);
        TextView useTextView = findViewById(R.id.use);

        CalendarButton = findViewById(R.id.btn_calendar);
        ReportButton = findViewById(R.id.btn_graph);
        SettingButton = findViewById(R.id.btn_settings);
        mainHomeButton = findViewById(R.id.btn_home);

        // DBHelper 인스턴스 생성
        dbHelper = new DBHelper(getApplicationContext());

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

        // 배경색 변경
        mainHomeButton.setBackgroundColor(ContextCompat.getColor(this, R.color.bright_background_color));

        // 이미지 밝게 만들기
        Drawable drawable = mainHomeButton.getDrawable();
        ColorFilter filter = new LightingColorFilter(0xFFFFFFFF, 0x00333333);  // 밝기 효과 추가
        drawable.setColorFilter(filter);

        mainHomeButton.setImageDrawable(drawable);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 텍스트뷰를 업데이트하는 메서드 호출
        updateTextViews();
    }

    // 텍스트뷰 갱신 메서드 정의
    private void updateTextViews() {
        TextView wonTextView = findViewById(R.id.won);
        TextView vsTextView = findViewById(R.id.vs);
        TextView useTextView = findViewById(R.id.use);

        // 현재 자산 계산
        ArrayList<FinancialRecord> allRecords = dbHelper.selectFinancialRecordsByDate("1900-01-01", "2100-12-31");
        int currentAssets = 0;
        for (FinancialRecord record : allRecords) {
            currentAssets += record.getAmount();
        }
        wonTextView.setText(String.format("%,d원", currentAssets));

        // 이번 달과 지난 달 지출 비교
        String currentMonth = getCurrentMonth(); // "YYYY-MM"
        String lastMonth = getLastMonth();       // "YYYY-MM"

        ArrayList<FinancialRecord> currentMonthRecords = dbHelper.selectFinancialRecordsByDate(currentMonth + "-01", currentMonth + "-31");
        int currentMonthExpense = 0;
        for (FinancialRecord record : currentMonthRecords) {
            if (record.getAmount() < 0) {
                currentMonthExpense += Math.abs(record.getAmount());
            }
        }

        ArrayList<FinancialRecord> lastMonthRecords = dbHelper.selectFinancialRecordsByDate(lastMonth + "-01", lastMonth + "-31");
        int lastMonthExpense = 0;
        for (FinancialRecord record : lastMonthRecords) {
            if (record.getAmount() < 0) {
                lastMonthExpense += Math.abs(record.getAmount());
            }
        }

        int expenseDifference = currentMonthExpense - lastMonthExpense;
        if (expenseDifference > 0) {
            vsTextView.setText(String.format("지난 달에 비해 \n%,d원 더 썼습니다.", expenseDifference));
        } else {
            vsTextView.setText(String.format("지난 달에 비해 \n%,d원 덜 썼습니다.", Math.abs(expenseDifference)));
        }

        useTextView.setText(String.format("%,d원", currentMonthExpense));
    }

    public void showBottomSheetDialog(View view) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = LayoutInflater.from(this).inflate(R.layout.calendar_bottom_sheet, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        // ChipGroup
        ChipGroup chipGroup = bottomSheetView.findViewById(R.id.chipGroup);
        Spinner spinner = bottomSheetView.findViewById(R.id.CategorySpin);
        //처음에 초기화가 필요, 지출 상태로 초기화
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.expense_spinner_items, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        final int[] i = {0};// 0은 지출 1은 수입
        // 선택된 chip의 id를 가져와서 스피너 초기화
        chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup chipGroup, int checkedId) {
                if (checkedId == -1) {
                    if(i[0] == 0)
                        chipGroup.check(R.id.chipExpense);
                    else
                        chipGroup.check(R.id.chipIncome);
                }

                if (checkedId == R.id.chipExpense) {
                    // "지출" 선택 시 기존 spinner의 설정 유지
                    i[0] = 0;
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                            R.array.expense_spinner_items, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                } else if (checkedId == R.id.chipIncome) {
                    // "수입" 선택 시 spinner 초기화 (아이템 1개만 포함)
                    i[0] = 1;
                    chipGroup.check(R.id.chipIncome);
                    ArrayAdapter<CharSequence> incomeAdapter = ArrayAdapter.createFromResource(getApplicationContext(),
                            R.array.income_spinner_items, android.R.layout.simple_spinner_item);
                    incomeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(incomeAdapter);
                }
            }
        });


        // 캘린더 뷰 (홈 화면에서는 현재 날짜를 사용)
        String selectedDate = getCurrentDate();  // 현재 날짜를 가져옴

        //날짜 표시 덱스트 뷰
        TextView textView = bottomSheetView.findViewById(R.id.select_dayText);
        textView.setText(selectedDate);

        // 금액과 메모 입력을 위한 EditText
        EditText moneyEdit = bottomSheetView.findViewById(R.id.moneyEdit);
        EditText textMemo = bottomSheetView.findViewById(R.id.memoEdit);

        // 버튼 클릭 리스너
        Button btnAction1 = bottomSheetView.findViewById(R.id.bottomBtn);
        btnAction1.setOnClickListener(v -> {
            // 사용자 입력 값 가져오기
            String date = textView.getText().toString();
            String category = spinner.getSelectedItem().toString();
            String moneyString = moneyEdit.getText().toString();
            String memo = textMemo.getText().toString();

            // 날짜가 선택되었는지
            if (selectedDate == null){
                Toast.makeText(this, "날자를 선택해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 금액과 메모가 비어있는지 확인
            if (moneyString.isEmpty()) {
                Toast.makeText(this, "금액을 입력해주세요.", Toast.LENGTH_SHORT).show();
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
            if(money <= 0 ){
                Toast.makeText(this, "0 혹은 그 이하의 금액은 입력할 수 없습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            if(chipGroup.getCheckedChipId() == R.id.chipExpense)
                money = -money;

            // FinancialRecord 객체 생성 및 DB에 추가
            FinancialRecord record = new FinancialRecord(date, category, money, memo);
            dbHelper.insertFinancialRecord(record);

            // 텍스트뷰를 업데이트하는 메서드 호출
            updateTextViews();

            //위젯 업데이트 함수 호출
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());

            // 특정 위젯 프로바이더의 위젯 ID 가져오기
            ComponentName widgetComponent = new ComponentName(getApplicationContext(), ReportWidget.class);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(widgetComponent);

            // 각 위젯 ID를 이용해 updateAppWidget 호출
            for (int appWidgetId : appWidgetIds) {
                ReportWidget.updateAppWidget(getApplicationContext(), appWidgetManager, appWidgetId);
            }
            // 바텀 시트 닫기
            bottomSheetDialog.dismiss();
        });

        //date Textview ClickListener
        TextView dateText = bottomSheetView.findViewById(R.id.select_dayText);
        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // BottomSheetDialog 생성
                Dialog dialog = new BottomSheetDialog(bottomSheetDialog.getContext());
                View centerSheetView = LayoutInflater.from(bottomSheetDialog.getContext()).inflate(R.layout.data_picker_dialog, null);
                dialog.setContentView(centerSheetView);

                // BottomSheetDialog의 크기와 위치 설정
                if (dialog.getWindow() != null) {
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }

                // NumberPicker 참조
                NumberPicker numberPickerY = centerSheetView.findViewById(R.id.np_year);
                NumberPicker numberPickerM = centerSheetView.findViewById(R.id.np_month);
                NumberPicker numberPickerD = centerSheetView.findViewById(R.id.np_day);

                // NumberPicker 최소값 및 최대값 설정
                // Year 설정
                numberPickerY.setMinValue(1900);
                numberPickerY.setMaxValue(2100);
                numberPickerY.setWrapSelectorWheel(true);

                // Month 설정
                numberPickerM.setMinValue(1);
                numberPickerM.setMaxValue(12);
                numberPickerM.setWrapSelectorWheel(true);

                // Day 설정
                numberPickerD.setMinValue(1);
                numberPickerD.setMaxValue(31);
                numberPickerD.setWrapSelectorWheel(true);

                // TextView에서 초기값 가져오기
                String dateTextValue = dateText.getText().toString(); // ex: "2024-11-22"
                String[] dateParts = dateTextValue.split("-"); // 분리: [년도, 월, 일]
                if (dateParts.length == 3) {
                    try {
                        numberPickerY.setValue(Integer.parseInt(dateParts[0]));
                        numberPickerM.setValue(Integer.parseInt(dateParts[1]));
                        numberPickerD.setValue(Integer.parseInt(dateParts[2]));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }

                // 확인 버튼 클릭 리스너 설정
                Button confirmButton = centerSheetView.findViewById(R.id.dataBtn);
                confirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // NumberPicker의 값 가져오기
                        int year = numberPickerY.getValue();
                        int month = numberPickerM.getValue();
                        int day = numberPickerD.getValue();

                        // TextView 값 업데이트
                        dateText.setText(String.format("%04d-%02d-%02d", year, month, day));

                        // Dialog 닫기
                        dialog.dismiss();
                    }
                });

                // Dialog 표시
                dialog.show();
            }
        });

        bottomSheetDialog.show();
    }

    // 현재 날짜를 "yyyy-MM-dd" 형식으로 얻는 메서드
    static public String getCurrentDate() {
        // 현재 날짜를 가져오기 위한 Calendar 객체
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH); // 월은 0부터 시작
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // 날짜를 "yyyy-MM-dd" 형식으로 포맷팅
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date(year - 1900, month, day)); // Date 객체로 변환 후 포맷
    }

    public String getCurrentMonth() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        return sdf.format(new Date()); // 현재 월 (YYYY-MM)
    }

    public String getLastMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1); // 지난 달로 이동
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        return sdf.format(calendar.getTime()); // 지난 월 (YYYY-MM)
    }
}