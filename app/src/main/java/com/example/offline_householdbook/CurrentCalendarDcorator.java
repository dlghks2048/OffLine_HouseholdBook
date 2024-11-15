package com.example.offline_householdbook;

import android.graphics.Color;
import android.text.style.ForegroundColorSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Calendar;

public class CurrentCalendarDcorator implements DayViewDecorator {

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        return day.getMonth() == currentMonth;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new ForegroundColorSpan(Color.WHITE)); // 현재 달 날짜는 순수 흰색
    }
}
