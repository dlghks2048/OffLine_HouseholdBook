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
    class FinancialRecordTable implements BaseColumns {
        public static final String TABLE_NAME = "financial_record";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_CATEGORY_NAME = "category_name";
        public static final String COLUMN_NAME_AMOUNT = "amount";
        public static final String COLUMN_NAME_MEMO = "memo";
    }
    // 데이터베이스 이름과 버전
    private static final String DATABASE_NAME = "HouseholdBook.db";
    private static final int DATABASE_VERSION = 1;

    // Constructor
    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE if not exists " + FinancialRecordTable.TABLE_NAME +" (" +
                FinancialRecordTable._ID + " integer primary key autoincrement, " +
                FinancialRecordTable.COLUMN_NAME_DATE + " text," +
                FinancialRecordTable.COLUMN_NAME_CATEGORY_NAME + " text," +
                FinancialRecordTable.COLUMN_NAME_AMOUNT + " int," +
                FinancialRecordTable.COLUMN_NAME_MEMO + " text)");
        
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String sql = "DROP TABLE if exists " + FinancialRecordTable.TABLE_NAME;
        sqLiteDatabase.execSQL(sql);
        onCreate(sqLiteDatabase);
    }

    public long insertFinancialRecord(FinancialRecord rhs) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FinancialRecordTable.COLUMN_NAME_DATE, rhs.getDate());
        values.put(FinancialRecordTable.COLUMN_NAME_CATEGORY_NAME, rhs.getCategoryName());
        values.put(FinancialRecordTable.COLUMN_NAME_AMOUNT, rhs.getAmount());
        values.put(FinancialRecordTable.COLUMN_NAME_MEMO, rhs.getMemo());

        long newRowId = db.insert(FinancialRecordTable.TABLE_NAME, null, values);

        return newRowId;
    }

    public ArrayList<FinancialRecord> selectFinancialRecordbyCategoryName(String categoryName) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor;
        cursor = db.rawQuery("SELECT * FROM " + FinancialRecordTable.TABLE_NAME +
                "WHERE " + FinancialRecordTable.COLUMN_NAME_CATEGORY_NAME + "=" + categoryName, null);

        ArrayList<FinancialRecord> res = new ArrayList<>();
        FinancialRecord temp;
        while(cursor.moveToNext()) {
            res.add(new FinancialRecord(cursor.getString(0), cursor.getString(1), cursor.getInt(2), cursor.getString((3))));
        }
        cursor.close();
        return res;
    }

    public ArrayList<FinancialRecord> selectFinancilRecordbyDate(String Date) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor;
        cursor = db.rawQuery("SELECT * FROM " + FinancialRecordTable.TABLE_NAME +
                "WHERE " + FinancialRecordTable.COLUMN_NAME_DATE + "=" + Date, null);

        ArrayList<FinancialRecord> res = new ArrayList<>();
        FinancialRecord temp;
        while(cursor.moveToNext()) {
            res.add(new FinancialRecord(cursor.getString(0), cursor.getString(1), cursor.getInt(2), cursor.getString((3))));
        }
        cursor.close();
        return res;
    }

    public ArrayList<FinancialRecord> selectFinancilRecordbyDate(String startDate, String EndDate) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor;
        cursor = db.rawQuery("SELECT * FROM " + FinancialRecordTable.TABLE_NAME +
                "WHERE " + FinancialRecordTable.COLUMN_NAME_CATEGORY_NAME + "Between " + startDate + " and " + EndDate, null);

        ArrayList<FinancialRecord> res = new ArrayList<>();
        FinancialRecord temp;
        while(cursor.moveToNext()) {
            res.add(new FinancialRecord(cursor.getString(0), cursor.getString(1), cursor.getInt(2), cursor.getString((3))));
        }
        cursor.close();
        return res;
    }
}
