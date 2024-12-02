package com.example.offline_householdbook;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.offline_householdbook.db.DBHelper;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Android 12 이상에서 SplashScreen API 호출
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            getSplashScreen();
        }

        // DBHelper 초기화 및 비밀번호 확인
        DBHelper dbHelper = new DBHelper(this);
        String password = dbHelper.selectSettingPassword();

        if (password == null || password.isEmpty()) {
            // 비밀번호가 없을 경우 바로 MainHome으로 이동
            startActivity(new Intent(this, MainHome.class));
            finish(); // SplashActivity를 종료하여 백스택 제거
        } else {
            // 비밀번호가 설정되어 있으면 다이얼로그 표시
            showPasswordDialog(password);
        }
    }

    private void showPasswordDialog(String correctPassword) {
        // 비밀번호 입력 Dialog 생성
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("비밀번호 입력");

        // 입력 필드 추가
        final EditText input = new EditText(this);
        builder.setView(input);

        // 확인 버튼 처리
        builder.setPositiveButton("확인", (dialog, which) -> {
            String enteredPassword = input.getText().toString();
            if (correctPassword.equals(enteredPassword)) {
                finish();// 비밀번호가 일치하면 MainHome으로 이동
            } else {
                Toast.makeText(this, "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                showPasswordDialog(correctPassword); // 재입력 요청
            }
        });

        // 취소 버튼 처리
        builder.setNegativeButton("취소", (dialog, which) -> {
            dialog.cancel();

            finishAffinity();
            System.exit(0); // 앱 종료
        });

        builder.setCancelable(false); // 다이얼로그 외부 클릭으로 닫히지 않도록 설정
        builder.show();
    }
}
