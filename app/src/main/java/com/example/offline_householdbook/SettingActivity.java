package com.example.offline_householdbook;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.offline_householdbook.Calendar.CalendarHome;
import com.example.offline_householdbook.db.DBHelper;
import com.example.offline_householdbook.db.FinancialRecord;

public class SettingActivity extends AppCompatActivity {
    ImageButton CalendarButton, ReportButton, SettingButton, mainHomeButton;
    DBHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        CalendarButton = findViewById(R.id.btn_calendar);
        mainHomeButton = findViewById(R.id.btn_home);
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
                finish();
            }
        });

        mainHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainHome.class);
                startActivity(intent);
                finish();
            }
        });

        // 배경색 변경
        SettingButton.setBackgroundColor(ContextCompat.getColor(this, R.color.bright_background_color));

        // 이미지 밝게 만들기
        Drawable drawable = SettingButton.getDrawable();
        ColorFilter filter = new LightingColorFilter(0xFFFFFFFF, 0x00333333);  // 밝기 효과 추가
        drawable.setColorFilter(filter);

        SettingButton.setImageDrawable(drawable);

        dbHelper = new DBHelper(getApplicationContext());
  }


    public void setPassword(View view) {
        // 비밀번호 입력 Dialog 생성
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("비밀번호 입력");

        // 입력 필드 추가
        final EditText input = new EditText(this);
        builder.setView(input);

        // 확인 버튼 처리
        builder.setPositiveButton("확인", (dialog, which) -> {
            String enteredPassword = input.getText().toString();
            dbHelper.updateSettingPassword(enteredPassword);
        });

        // 취소 버튼 처리
        builder.setNegativeButton("취소", (dialog, which) -> {
            dialog.cancel();
        });

        builder.setCancelable(false); // 다이얼로그 외부 클릭으로 닫히지 않도록 설정
        builder.show();
    }
}