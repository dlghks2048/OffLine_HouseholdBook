// DBHelper.java
package com.example.offline_householdbook.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    // 테이블 정보를 저장하는 Inner class
    public static class FinancialRecordTable implements BaseColumns {
        public static final String TABLE_NAME = "financial_record";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_CATEGORY_NAME = "category_name";
        public static final String COLUMN_NAME_AMOUNT = "amount";
        public static final String COLUMN_NAME_MEMO = "memo";
    }

    private static final String DATABASE_NAME = "HouseholdBook.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + FinancialRecordTable.TABLE_NAME + " (" +
                FinancialRecordTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FinancialRecordTable.COLUMN_NAME_DATE + " TEXT, " +
                FinancialRecordTable.COLUMN_NAME_CATEGORY_NAME + " TEXT, " +
                FinancialRecordTable.COLUMN_NAME_AMOUNT + " INTEGER, " +
                FinancialRecordTable.COLUMN_NAME_MEMO + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + FinancialRecordTable.TABLE_NAME;
        sqLiteDatabase.execSQL(sql);
        onCreate(sqLiteDatabase);
    }

    public long insertFinancialRecord(FinancialRecord record) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FinancialRecordTable.COLUMN_NAME_DATE, record.getDate());
        values.put(FinancialRecordTable.COLUMN_NAME_CATEGORY_NAME, record.getCategoryName());
        values.put(FinancialRecordTable.COLUMN_NAME_AMOUNT, record.getAmount());
        values.put(FinancialRecordTable.COLUMN_NAME_MEMO, record.getMemo());

        return db.insert(FinancialRecordTable.TABLE_NAME, null, values);
    }

    public ArrayList<FinancialRecord> selectFinancialRecordsByDate(String date) {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<FinancialRecord> records = new ArrayList<>();

        String query = "SELECT * FROM " + FinancialRecordTable.TABLE_NAME +
                " WHERE " + FinancialRecordTable.COLUMN_NAME_DATE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{date});

        while (cursor.moveToNext()) {
            records.add(new FinancialRecord(
                    cursor.getString(cursor.getColumnIndexOrThrow(FinancialRecordTable.COLUMN_NAME_DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(FinancialRecordTable.COLUMN_NAME_CATEGORY_NAME)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(FinancialRecordTable.COLUMN_NAME_AMOUNT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(FinancialRecordTable.COLUMN_NAME_MEMO))
            ));
        }
        cursor.close();
        return records;
    }
}
