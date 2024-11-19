package com.example.offline_householdbook.Calendar;

import android.graphics.Color;
import android.text.style.ForegroundColorSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Calendar;

public class CurrentCalendarDecorator implements DayViewDecorator {

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        return day.getMonth() == currentMonth;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new ForegroundColorSpan(Color.WHITE)); // 현재 달 날짜는 순수 흰색
        view.addSpan(new CustomTextSpan("Holiday")); //추가 텍스트 추가
    }

    private static class CustomTextSpan extends android.text.style.CharacterStyle {
        private String text;

        public CustomTextSpan(String text) {
            this.text = text;
        }

        @Override
        public void updateDrawState(android.text.TextPaint paint) {
            // 텍스트 색상, 크기 설정
            paint.setColor(Color.GREEN); // 텍스트 색상
            paint.setTextSize(20); // 텍스트 크기
        }
    }
}
