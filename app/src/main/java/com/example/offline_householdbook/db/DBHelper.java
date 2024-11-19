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
                SettingTable.COLUMN_NAME_PASSWORD + " TEXT, " +
                SettingTable.COLUMN_NAME_BALANCE + " INTEGER, " +
                SettingTable.COLUMN_NAME_DARK_MODE + " INTEGER)");

        String query = "SELECT * FROM " + SettingTable.TABLE_NAME;
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        if(cursor.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put(SettingTable.COLUMN_NAME_PASSWORD, "");
            values.put(SettingTable.COLUMN_NAME_BALANCE, 0);
            values.put(SettingTable.COLUMN_NAME_DARK_MODE, 0);

            long result = sqLiteDatabase.insert(SettingTable.TABLE_NAME, null, values);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + FinancialRecordTable.TABLE_NAME;
        sqLiteDatabase.execSQL(sql);
        sql = "DROP TABLE IF EXISTS " + SettingTable.TABLE_NAME;
        sqLiteDatabase.execSQL(sql);
        onCreate(sqLiteDatabase);
    }

    @Override
    protected void finalize() throws Throwable {
        readDb.close();
        writeDb.close();
        super.finalize();
    }

    /* -------------- financial_record 테이블에 대한 인터페이스 -------------- */
    // 삽입
    public void insertFinancialRecord(FinancialRecord record) {
        ContentValues values = new ContentValues();
        values.put(FinancialRecordTable.COLUMN_NAME_DATE, record.getDate());
        values.put(FinancialRecordTable.COLUMN_NAME_CATEGORY_NAME, record.getCategoryName());
        values.put(FinancialRecordTable.COLUMN_NAME_AMOUNT, record.getAmount());
        values.put(FinancialRecordTable.COLUMN_NAME_MEMO, record.getMemo());

        long result = writeDb.insert(FinancialRecordTable.TABLE_NAME, null, values);
        int curBalance = selectSettingBalance();
        updateSettingBalance(curBalance + record.getAmount());
    }
    // _id로 조회
    private FinancialRecord selectFinancialRecordsById(int id) {
        String query = "SELECT * FROM " + FinancialRecordTable.TABLE_NAME +
                " WHERE " + FinancialRecordTable._ID + " = ?";

        Cursor cursor = readDb.rawQuery(query, new String[]{Integer.toString(id)});
        FinancialRecord fr;
        fr = new FinancialRecord(
                cursor.getInt(cursor.getColumnIndexOrThrow(FinancialRecordTable._ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(FinancialRecordTable.COLUMN_NAME_DATE)),
                cursor.getString(cursor.getColumnIndexOrThrow(FinancialRecordTable.COLUMN_NAME_CATEGORY_NAME)),
                cursor.getInt(cursor.getColumnIndexOrThrow(FinancialRecordTable.COLUMN_NAME_AMOUNT)),
                cursor.getString(cursor.getColumnIndexOrThrow(FinancialRecordTable.COLUMN_NAME_MEMO))
        );

        return fr;
    }
    // 카테고리 이름으로 조회
    public ArrayList<FinancialRecord> selectFinancialRecordsByCategoryName(String categoryName) {
        ArrayList<FinancialRecord> records = new ArrayList<>();

        String query = "SELECT * FROM " + FinancialRecordTable.TABLE_NAME +
                " WHERE " + FinancialRecordTable.COLUMN_NAME_CATEGORY_NAME + " = ?";
        Cursor cursor = readDb.rawQuery(query, new String[]{categoryName});

        while (cursor.moveToNext()) {
            records.add(new FinancialRecord(
                    cursor.getInt(cursor.getColumnIndexOrThrow(FinancialRecordTable._ID)),
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
        ArrayList<FinancialRecord> records = new ArrayList<>();

        // 날짜를 쿼리할 때 작은따옴표를 제대로 넣어야 함
        String query = "SELECT * FROM " + FinancialRecordTable.TABLE_NAME +
                " WHERE " + FinancialRecordTable.COLUMN_NAME_DATE + " = ?";
        Cursor cursor = readDb.rawQuery(query, new String[]{date});  // 날짜 파라미터로 전달

        while (cursor.moveToNext()) {
            records.add(new FinancialRecord(
                    cursor.getInt(cursor.getColumnIndexOrThrow(FinancialRecordTable._ID)),
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
        ArrayList<FinancialRecord> records = new ArrayList<>();

        String query = "SELECT * FROM " + FinancialRecordTable.TABLE_NAME +
                " WHERE " + FinancialRecordTable.COLUMN_NAME_DATE + " BETWEEN ? and ?";
        Cursor cursor = readDb.rawQuery(query, new String[]{startDate, endDate});

        while (cursor.moveToNext()) {
            records.add(new FinancialRecord(
                    cursor.getInt(cursor.getColumnIndexOrThrow(FinancialRecordTable._ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(FinancialRecordTable.COLUMN_NAME_DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(FinancialRecordTable.COLUMN_NAME_CATEGORY_NAME)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(FinancialRecordTable.COLUMN_NAME_AMOUNT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(FinancialRecordTable.COLUMN_NAME_MEMO))
            ));
        }
        cursor.close();

        return records;
    }
    
    // 업데이트, _id 검사하지 않음
    public void updateFinancialRecord(FinancialRecord before, FinancialRecord after) {
        // 새로운 values
        ContentValues values = new ContentValues();
        values.put(FinancialRecordTable.COLUMN_NAME_DATE, after.getDate());
        values.put(FinancialRecordTable.COLUMN_NAME_CATEGORY_NAME, after.getCategoryName());
        values.put(FinancialRecordTable.COLUMN_NAME_AMOUNT, after.getAmount());
        values.put(FinancialRecordTable.COLUMN_NAME_MEMO, after.getMemo());

        // 업데이트 쿼리문의 where절
        String selection = FinancialRecordTable.COLUMN_NAME_DATE + "=? and " +
                FinancialRecordTable.COLUMN_NAME_CATEGORY_NAME + "=? and "+
                FinancialRecordTable.COLUMN_NAME_AMOUNT + "=? and " +
                FinancialRecordTable.COLUMN_NAME_MEMO + "=?";
        // where절의 인수
        String[] selectionArgs = {before.getDate(), before.getCategoryName(), Integer.toString(before.getAmount()), before.getMemo()};

        int count = writeDb.update(
                FinancialRecordTable.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );

        // 차이 계산
        int differ = after.getAmount() - before.getAmount();
        // 잔액 업데이트
        int curBalance = selectSettingBalance();
        updateSettingBalance(curBalance + differ);
    }
    // _id로 조회하여 업데이트
    public void updateFinancialRecord(int beforeId, FinancialRecord after) {
        // 새로운 values
        ContentValues values = new ContentValues();
        values.put(FinancialRecordTable.COLUMN_NAME_DATE, after.getDate());
        values.put(FinancialRecordTable.COLUMN_NAME_CATEGORY_NAME, after.getCategoryName());
        values.put(FinancialRecordTable.COLUMN_NAME_AMOUNT, after.getAmount());
        values.put(FinancialRecordTable.COLUMN_NAME_MEMO, after.getMemo());

        // 업데이트 쿼리문의 where절
        String selection = FinancialRecordTable._ID + "=?";
        // where절의 인수
        String[] selectionArgs = {Integer.toString(beforeId)};

        int count = writeDb.update(
                FinancialRecordTable.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );

        FinancialRecord before = selectFinancialRecordsById(beforeId);
        // 차이 계산
        int differ = after.getAmount() - before.getAmount();
        // 잔액 업데이트
        int curBalance = selectSettingBalance();
        updateSettingBalance(curBalance + differ);
    }

    // 삭제, _id로 조회하지 않음
    public void deleteFinancialRecord(FinancialRecord rhs) {
        // 삭제 쿼리문의 where절
        String selection = FinancialRecordTable.COLUMN_NAME_DATE + "=? and " +
                FinancialRecordTable.COLUMN_NAME_CATEGORY_NAME + "=? and "+
                FinancialRecordTable.COLUMN_NAME_AMOUNT + "=? and " +
                FinancialRecordTable.COLUMN_NAME_MEMO + "=?";
        // where절의 인수
        String[] selectionArgs = {rhs.getDate(), rhs.getCategoryName(), Integer.toString(rhs.getAmount()), rhs.getMemo()};

        int deleteRows = writeDb.delete(FinancialRecordTable.TABLE_NAME, selection, selectionArgs);
    }
    // _id 값으로 삭제
    public void deleteFinancialRecord(int _id) {
        // 삭제 쿼리문의 where절
        String selection = FinancialRecordTable._ID + "=?";
        // where절의 인수
        String[] selectionArgs = {Integer.toString(_id)};

        int deleteRows = writeDb.delete(FinancialRecordTable.TABLE_NAME, selection, selectionArgs);
    }

    /* -------------- settings 테이블에 대한 인터페이스 -------------- */
    // 비밀번호 조회
    public String selectSettingPassword() {
        String password = "";
        String query = "SELECT * FROM " + SettingTable.TABLE_NAME;
        Cursor cursor = readDb.rawQuery(query, null);

        while (cursor.moveToNext())
            password = cursor.getString(cursor.getColumnIndexOrThrow(SettingTable.COLUMN_NAME_PASSWORD));

        cursor.close();

        return password;
    }
    // 잔액 조회
    public int selectSettingBalance() {
        int balance = 0;
        String query = "SELECT * FROM " + SettingTable.TABLE_NAME;
        Cursor cursor = readDb.rawQuery(query, null);

        while (cursor.moveToNext())
            balance = cursor.getInt(cursor.getColumnIndexOrThrow(SettingTable.COLUMN_NAME_BALANCE));

        cursor.close();

        return balance;
    }
    // 다크 모드 조회
    public int selectSettingDarkMode() {
        int isdarkMode = 0;
        String query = "SELECT * FROM " + SettingTable.TABLE_NAME;
        Cursor cursor = readDb.rawQuery(query, null);

        while (cursor.moveToNext())
            isdarkMode = cursor.getInt(cursor.getColumnIndexOrThrow(SettingTable.COLUMN_NAME_DARK_MODE));

        cursor.close();

        return isdarkMode;
    }
    // 비밀번호 수정
    public void updateSettingPassword(String password) {
        ContentValues values = new ContentValues();
        values.put(SettingTable.COLUMN_NAME_PASSWORD, password);

        // 업데이트 쿼리문의 where절
        String selection = SettingTable._ID + "=1";

        int count = writeDb.update(
                SettingTable.TABLE_NAME,
                values,
                selection,
                null
        );
    }

    // 잔액 수정
    public void updateSettingBalance(int balance) {
        ContentValues values = new ContentValues();
        values.put(SettingTable.COLUMN_NAME_BALANCE, balance);

        // 업데이트 쿼리문의 where절
        String selection = SettingTable._ID + "=1";

        int count = writeDb.update(
                SettingTable.TABLE_NAME,
                values,
                selection,
                null
        );
    }
    // 테마(다크모드) 수정
    public void updateSettingDarkMode(int isDarkMode) {
        ContentValues values = new ContentValues();
        values.put(SettingTable.COLUMN_NAME_DARK_MODE, isDarkMode);

        // 업데이트 쿼리문의 where절
        String selection = SettingTable ._ID + "=1";

        int count = writeDb.update(
                SettingTable.TABLE_NAME,
                values,
                selection,
                null
        );
    }

}
