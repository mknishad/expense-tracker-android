package com.monir.expensetracker.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.monir.expensetracker.util.Constant;

public class DatabaseHelper extends SQLiteOpenHelper {

    // query to create debit table in the database
    private static String CREATE_DEBIT_TABLE = "CREATE TABLE " + Constant.TABLE_DEBIT + " (" +
            Constant.COL_ID + " INTEGER PRIMARY KEY, " +
            Constant.COL_DEBIT_DATE + " TEXT, " +
            Constant.COL_DEBIT_CATEGORY + " TEXT, " +
            Constant.COL_DEBIT_DESCRIPTION + " TEXT, " +
            Constant.COL_DEBIT_AMOUNT + " DOUBLE);";

    // query to create credit table in the database
    private static String CREATE_CREDIT_TABLE = "CREATE TABLE " + Constant.TABLE_CREDIT + " (" +
            Constant.COL_ID + " INTEGER PRIMARY KEY, " +
            Constant.COL_CREDIT_DATE + " TEXT, " +
            Constant.COL_CREDIT_CATEGORY + " TEXT, " +
            Constant.COL_CREDIT_DESCRIPTION + " TEXT, " +
            Constant.COL_CREDIT_AMOUNT + " DOUBLE, " +
            Constant.COL_CREDIT_TIMESTAMP + " INTEGER);";

    // query to create deleted credit table in the database
    private static String CREATE_DELETED_CREDIT_TABLE = "CREATE TABLE " + Constant.TABLE_DELETED_CREDIT + " (" +
            Constant.COL_ID + " INTEGER PRIMARY KEY, " +
            Constant.COL_CREDIT_DATE + " TEXT, " +
            Constant.COL_CREDIT_CATEGORY + " TEXT, " +
            Constant.COL_CREDIT_DESCRIPTION + " TEXT, " +
            Constant.COL_CREDIT_AMOUNT + " DOUBLE, " +
            Constant.COL_CREDIT_TIMESTAMP + " INTEGER);";

    // query to create category table
    private static String CREATE_CATEGORY_TABLE = "CREATE TABLE " + Constant.TABLE_CATEGORY + " (" +
            Constant.COL_ID + " INTEGER PRIMARY KEY, " +
            Constant.COL_CATEGORY_NAME + " TEXT);";

    // constructor
    DatabaseHelper(Context context) {
        super(context, Constant.DATABASE_NAME, null, Constant.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DEBIT_TABLE);
        db.execSQL(CREATE_CREDIT_TABLE);
        db.execSQL(CREATE_CATEGORY_TABLE);
        db.execSQL(CREATE_DELETED_CREDIT_TABLE);
        //insertInitialDebitCategories(db);
    }

    private void insertInitialDebitCategories(SQLiteDatabase db) {
        db.execSQL("INSERT INTO " + Constant.TABLE_CATEGORY + " VALUES (" + 1 + ", \"Car\");");
        db.execSQL("INSERT INTO " + Constant.TABLE_CATEGORY + " VALUES (" + 2 + ", \"Charity\");");
        db.execSQL("INSERT INTO " + Constant.TABLE_CATEGORY + " VALUES (" + 3 + ", \"Clothes\");");
        db.execSQL("INSERT INTO " + Constant.TABLE_CATEGORY + " VALUES (" + 4 + ", \"Communication\");");
        db.execSQL("INSERT INTO " + Constant.TABLE_CATEGORY + " VALUES (" + 5 + ", \"Education\");");
        db.execSQL("INSERT INTO " + Constant.TABLE_CATEGORY + " VALUES (" + 6 + ", \"Eating Out\");");
        db.execSQL("INSERT INTO " + Constant.TABLE_CATEGORY + " VALUES (" + 7 + ", \"Electronics\");");
        db.execSQL("INSERT INTO " + Constant.TABLE_CATEGORY + " VALUES (" + 8 + ", \"Entertainment\");");
        db.execSQL("INSERT INTO " + Constant.TABLE_CATEGORY + " VALUES (" + 9 + ", \"Fitness\");");
        db.execSQL("INSERT INTO " + Constant.TABLE_CATEGORY + " VALUES (" + 10 + ", \"Food\");");
        db.execSQL("INSERT INTO " + Constant.TABLE_CATEGORY + " VALUES (" + 11 + ", \"Gifts\");");
        db.execSQL("INSERT INTO " + Constant.TABLE_CATEGORY + " VALUES (" + 12 + ", \"Health\");");
        db.execSQL("INSERT INTO " + Constant.TABLE_CATEGORY + " VALUES (" + 13 + ", \"House\");");
        db.execSQL("INSERT INTO " + Constant.TABLE_CATEGORY + " VALUES (" + 14 + ", \"Investment\");");
        db.execSQL("INSERT INTO " + Constant.TABLE_CATEGORY + " VALUES (" + 15 + ", \"Loan Payment\");");
        db.execSQL("INSERT INTO " + Constant.TABLE_CATEGORY + " VALUES (" + 16 + ", \"Miscellaneous\");");
        db.execSQL("INSERT INTO " + Constant.TABLE_CATEGORY + " VALUES (" + 17 + ", \"Sports\");");
        db.execSQL("INSERT INTO " + Constant.TABLE_CATEGORY + " VALUES (" + 18 + ", \"Taxy\");");
        db.execSQL("INSERT INTO " + Constant.TABLE_CATEGORY + " VALUES (" + 19 + ", \"Toiletry\");");
        db.execSQL("INSERT INTO " + Constant.TABLE_CATEGORY + " VALUES (" + 20 + ", \"Transport\");");
        db.execSQL("INSERT INTO " + Constant.TABLE_CATEGORY + " VALUES (" + 21 + ", \"Utility Bills\");");
        db.execSQL("INSERT INTO " + Constant.TABLE_CATEGORY + " VALUES (" + 22 + ", \"Vacation\");");
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
