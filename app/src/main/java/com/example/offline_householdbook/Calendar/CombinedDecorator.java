package com.example.offline_householdbook.Calendar;


import android.content.Context;
import android.graphics.Color;
import android.text.style.ForegroundColorSpan;

import androidx.core.content.ContextCompat;

import com.example.offline_householdbook.R;
import com.example.offline_householdbook.db.DBHelper;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import org.threeten.bp.LocalDate;

import java.util.Calendar;

public class CombinedDecorator implements DayViewDecorator {
    private Context context;
    private final CalendarDay today;
    private final DBHelper dbHelper;
    private final String date;
    private final int totalAmount;

    public CombinedDecorator(DBHelper dbHelper, String date, Context context) {
        this.dbHelper = dbHelper;
        this.date = date;
        this.context = context;
        // 오늘 날짜 계산
        Calendar calendar = Calendar.getInstance();
        this.today = CalendarDay.from(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));

        // 해당 날짜의 금액 합계 계산
        this.totalAmount = dbHelper.getAmountSumForDate(date);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        // 특정 날짜에만 데코레이터 적용
        return day.getDate().toString().equals(date);
    }

    @Override
    public void decorate(DayViewFacade view) {
        int color;
        // 금액에 따라 색상 변경
        if(totalAmount == 0 )  color = ContextCompat.getColor(context, R.color.textColor);
        else color = totalAmount >= 0 ? Color.BLUE : Color.RED;
        view.addSpan(new ForegroundColorSpan(color));
    }
}
