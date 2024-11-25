package com.example.offline_householdbook;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

import com.example.offline_householdbook.db.DBHelper;
import com.example.offline_householdbook.db.FinancialRecord;

import java.util.ArrayList;

/**
 * Implementation of App Widget functionality.
 */
public class ReportWidget extends AppWidgetProvider {
    private static DBHelper dbHelper;
    static public void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.report_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        // DBHelper 인스턴스 생성
        dbHelper = new DBHelper(context.getApplicationContext());

        try {
            // 현재 자산 계산
            ArrayList<FinancialRecord> allRecords = dbHelper.selectFinancialRecordsByDate("1900-01-01", "2100-12-31");
            int currentAssets = 0;
            for (FinancialRecord record : allRecords) {
                currentAssets += record.getAmount();
            }
            views.setTextViewText(R.id.appwidget_text, String.format("총 자산:%,d원", currentAssets));
        }
        catch (Exception e){
            views.setTextViewText(R.id.appwidget_text, "초기화 오류");
        }
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}