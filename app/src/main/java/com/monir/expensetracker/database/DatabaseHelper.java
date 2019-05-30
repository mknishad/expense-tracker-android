package com.monir.expensetracker.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.monir.expensetracker.util.Constant;

public class DatabaseHelper extends SQLiteOpenHelper {

    // query to create debit table in the database
    private static String CREATE_DEBIT_TABLE = "CREATE TABLE " + Constant.TABLE_DEBIT + " (" + Constant.COL_ID + " INTEGER PRIMARY KEY, " + Constant.COL_DEBIT_DATE + " TEXT, " + Constant.COL_DEBIT_CATEGORY + " TEXT, " + Constant.COL_DEBIT_DESCRIPTION + " TEXT, " + Constant.COL_DEBIT_AMOUNT + " DOUBLE);";

    // query to create credit table in the database
    private static String CREATE_CREDIT_TABLE = "CREATE TABLE " + Constant.TABLE_CREDIT + " (" + Constant.COL_ID + " INTEGER PRIMARY KEY, " + Constant.COL_CREDIT_DATE + " TEXT, " + Constant.COL_CREDIT_CATEGORY + " TEXT, " + Constant.COL_CREDIT_DESCRIPTION + " TEXT, " + Constant.COL_CREDIT_AMOUNT + " DOUBLE, " + Constant.COL_CREDIT_TIMESTAMP + " INTEGER);";

    // query to create deleted credit table in the database
    private static String CREATE_DELETED_CREDIT_TABLE = "CREATE TABLE " + Constant.TABLE_DELETED_CREDIT + " (" + Constant.COL_ID + " INTEGER PRIMARY KEY, " + Constant.COL_CREDIT_DATE + " TEXT, " + Constant.COL_CREDIT_CATEGORY + " TEXT, " + Constant.COL_CREDIT_DESCRIPTION + " TEXT, " + Constant.COL_CREDIT_AMOUNT + " DOUBLE, " + Constant.COL_CREDIT_TIMESTAMP + " INTEGER);";

    // query to create category table
    private static String CREATE_CATEGORY_TABLE = "CREATE TABLE " + Constant.TABLE_CATEGORY + " (" + Constant.COL_ID + " INTEGER PRIMARY KEY, " + Constant.COL_CATEGORY_NAME + " TEXT);";

    // constructor
    public DatabaseHelper(Context context) {
        super(context, Constant.DATABASE_NAME, null, Constant.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DEBIT_TABLE);
        db.execSQL(CREATE_CREDIT_TABLE);
        db.execSQL(CREATE_CATEGORY_TABLE);
        db.execSQL(CREATE_DELETED_CREDIT_TABLE);
        insertInitialCategories(db);
    }

    private void insertInitialCategories(SQLiteDatabase db) {
        db.execSQL("INSERT INTO " + Constant.TABLE_CATEGORY + " VALUES (" + 1 + ", \"Clothing\");");
        db.execSQL("INSERT INTO " + Constant.TABLE_CATEGORY + " VALUES (" + 2 + ", \"Charitable Giving\");");
        db.execSQL("INSERT INTO " + Constant.TABLE_CATEGORY + " VALUES (" + 3 + ", \"Education\");");
        db.execSQL("INSERT INTO " + Constant.TABLE_CATEGORY + " VALUES (" + 4 + ", \"Electronics\");");
        db.execSQL("INSERT INTO " + Constant.TABLE_CATEGORY + " VALUES (" + 5 + ", \"Entertainment\");");
        db.execSQL("INSERT INTO " + Constant.TABLE_CATEGORY + " VALUES (" + 6 + ", \"Fitness\");");
        db.execSQL("INSERT INTO " + Constant.TABLE_CATEGORY + " VALUES (" + 7 + ", \"Food\");");
        db.execSQL("INSERT INTO " + Constant.TABLE_CATEGORY + " VALUES (" + 8 + ", \"Gifts\");");
        db.execSQL("INSERT INTO " + Constant.TABLE_CATEGORY + " VALUES (" + 9 + ", \"Health\");");
        db.execSQL("INSERT INTO " + Constant.TABLE_CATEGORY + " VALUES (" + 10 + ", \"Household\");");
        db.execSQL("INSERT INTO " + Constant.TABLE_CATEGORY + " VALUES (" + 11 + ", \"Investment\");");
        db.execSQL("INSERT INTO " + Constant.TABLE_CATEGORY + " VALUES (" + 12 + ", \"Loan Payment\");");
        db.execSQL("INSERT INTO " + Constant.TABLE_CATEGORY + " VALUES (" + 13 + ", \"Miscellaneous\");");
        db.execSQL("INSERT INTO " + Constant.TABLE_CATEGORY + " VALUES (" + 14 + ", \"Rent\");");
        db.execSQL("INSERT INTO " + Constant.TABLE_CATEGORY + " VALUES (" + 15 + ", \"Transportation\");");
        db.execSQL("INSERT INTO " + Constant.TABLE_CATEGORY + " VALUES (" + 16 + ", \"Utility Bills\");");
        db.execSQL("INSERT INTO " + Constant.TABLE_CATEGORY + " VALUES (" + 17 + ", \"Vacation\");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Constant.TABLE_DEBIT);
        db.execSQL("DROP TABLE IF EXISTS " + Constant.TABLE_CREDIT);
        db.execSQL("DROP TABLE IF EXISTS " + Constant.TABLE_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + Constant.TABLE_DELETED_CREDIT);
        onCreate(db);
    }

}
