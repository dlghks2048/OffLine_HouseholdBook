package com.example.offline_householdbook;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.offline_householdbook.db.DBHelper;
import com.example.offline_householdbook.db.FinancialRecord;

public class SettingActivity extends AppCompatActivity {

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
        // DBHelper 객체 생성, 생성자 인수는 현재 컨텍스트
        DBHelper db = new DBHelper(getApplicationContext());
        // 메서드 이름은 sql문+테이블이름(+~)
        // insertFinancialRecord는 FinancialRecord객체를 생성하여 전달하면 됨
        db.insertFinancialRecord(new FinancialRecord("2024-11-15", "외식", 10000, "메모"));
        db.insertFinancialRecord(new FinancialRecord("2024-11-15", "문구", 10000, "메모"));
        db.insertFinancialRecord(new FinancialRecord("2024-11-15", "교통", 10000, "메모"));
    }


}