// DBHelper.java
package com.example.offline_householdbook.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    // 테이블 정보를 저장하는 Inner class
    // financial_record 테이블
    final static class FinancialRecordTable implements BaseColumns {
        public static final String TABLE_NAME = "financial_record";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_CATEGORY_NAME = "category_name";
        public static final String COLUMN_NAME_AMOUNT = "amount";
        public static final String COLUMN_NAME_MEMO = "memo";
    }
    // setting 테이블
    final static class SettingTable implements BaseColumns {
        public static final String TABLE_NAME = "setting";
        public static final String COLUMN_NAME_PASSWORD = "password";
        public static final String COLUMN_NAME_BALANCE = "balance";
        public static final String COLUMN_NAME_DARK_MODE = "dark_mode";
    }
    // DB 이름과 버전
    private static final String DATABASE_NAME = "HouseholdBook.db";
    private static final int DATABASE_VERSION = 1;
    // Field
    private SQLiteDatabase writeDb;
    private SQLiteDatabase readDb;
    // Constructor
    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        writeDb = getWritableDatabase();
        readDb = getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // financial_record 테이블 생성
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + FinancialRecordTable.TABLE_NAME + " (" +
                FinancialRecordTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FinancialRecordTable.COLUMN_NAME_DATE + " TEXT, " +
                FinancialRecordTable.COLUMN_NAME_CATEGORY_NAME + " TEXT, " +
                FinancialRecordTable.COLUMN_NAME_AMOUNT + " INTEGER, " +
                FinancialRecordTable.COLUMN_NAME_MEMO + " TEXT)");

        // setting 테이블 생성
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + SettingTable.TABLE_NAME +" (" +
                SettingTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SettingTable.COLUMN_NAME_PASSWORD + " TEXT," +
                SettingTable.COLUMN_NAME_BALANCE + " INTEGER," +
                SettingTable.COLUMN_NAME_DARK_MODE + " INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + FinancialRecordTable.TABLE_NAME;
        sqLiteDatabase.execSQL(sql);
        sql = "DROP TABLE IF EXISTS " + SettingTable.TABLE_NAME;
        sqLiteDatabase.execSQL(sql);
        onCreate(sqLiteDatabase);
    }
    
    // financial_record 테이블에 대한 인터페이스
    // 삽입
    public void insertFinancialRecord(FinancialRecord record) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FinancialRecordTable.COLUMN_NAME_DATE, record.getDate());
        values.put(FinancialRecordTable.COLUMN_NAME_CATEGORY_NAME, record.getCategoryName());
        values.put(FinancialRecordTable.COLUMN_NAME_AMOUNT, record.getAmount());
        values.put(FinancialRecordTable.COLUMN_NAME_MEMO, record.getMemo());

        long result = writeDb.insert(FinancialRecordTable.TABLE_NAME, null, values);
        if (result == -1) {
            Log.e("DBHelper", "Insert failed");
        } else {
            Log.d("DBHelper", "Insert successful");
        }

    }

    // 카테고리 이름으로 조회
    public ArrayList<FinancialRecord> selectFinancialRecordsByCategoryName(String categoryName) {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<FinancialRecord> records = new ArrayList<>();

        String query = "SELECT * FROM " + FinancialRecordTable.TABLE_NAME +
                " WHERE " + FinancialRecordTable.COLUMN_NAME_CATEGORY_NAME + " = ?";
        Cursor cursor = readDb.rawQuery(query, new String[]{categoryName});

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
    
    // 단일 Date로 조회
    public ArrayList<FinancialRecord> selectFinancialRecordsByDate(String date) {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<FinancialRecord> records = new ArrayList<>();

        // 날짜를 쿼리할 때 작은따옴표를 제대로 넣어야 함
        String query = "SELECT * FROM " + FinancialRecordTable.TABLE_NAME +
                " WHERE " + FinancialRecordTable.COLUMN_NAME_DATE + " = ?";
        Cursor cursor = readDb.rawQuery(query, new String[]{date});  // 날짜 파라미터로 전달

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
    
    // 범위 Date로 조회
    public ArrayList<FinancialRecord> selectFinancialRecordsByDate(String startDate, String endDate) {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<FinancialRecord> records = new ArrayList<>();

        String query = "SELECT * FROM " + FinancialRecordTable.TABLE_NAME +
                " WHERE " + FinancialRecordTable.COLUMN_NAME_DATE + " BETWEEN ? and ?";
        Cursor cursor = readDb.rawQuery(query, new String[]{startDate, endDate});

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
    
    // 업데이트
    public void updateFinancialRecord(FinancialRecord before, FinancialRecord after) {



    }


}
