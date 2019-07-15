package com.monir.expensetracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.monir.expensetracker.model.Category;
import com.monir.expensetracker.model.Credit;
import com.monir.expensetracker.model.Debit;
import com.monir.expensetracker.util.Constant;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CreditDataSource {

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public CreditDataSource(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    // open the database for operation
    private void open() {
        database = databaseHelper.getWritableDatabase();
    }

    // close the database
    private void close() {
        database.close();
    }

    // insert a credit to Credit table
    public boolean insertCredit(Credit credit) {
        this.open();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Constant.COL_CREDIT_DATE, credit.getCreditDate());
        contentValues.put(Constant.COL_CREDIT_CATEGORY, credit.getCreditCategory());
        contentValues.put(Constant.COL_CREDIT_DESCRIPTION, credit.getCreditDescription());
        contentValues.put(Constant.COL_CREDIT_AMOUNT, credit.getCreditAmount());
        contentValues.put(Constant.COL_CREDIT_TIMESTAMP, credit.getCreditTimestamp());

        long inserted = database.insert(
                Constant.TABLE_CREDIT,
                null,
                contentValues);

        this.close();

        return inserted > 0;
    }

    // insert a deleted credit to DeletedCredit table
    public boolean insertDeletedCredit(Credit credit) {
        this.open();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Constant.COL_CREDIT_DATE, credit.getCreditDate());
        contentValues.put(Constant.COL_CREDIT_CATEGORY, credit.getCreditCategory());
        contentValues.put(Constant.COL_CREDIT_DESCRIPTION, credit.getCreditDescription());
        contentValues.put(Constant.COL_CREDIT_AMOUNT, credit.getCreditAmount());
        contentValues.put(Constant.COL_CREDIT_TIMESTAMP, credit.getCreditTimestamp());

        long inserted = database.insert(
                Constant.TABLE_DELETED_CREDIT,
                null,
                contentValues);

        this.close();

        return inserted > 0;
    }

    // get a single credit from the credit table by credit id
    public Credit getCredit(int id) {
        this.open();

        Cursor cursor = database.query(
                Constant.TABLE_CREDIT,
                new String[]{Constant.COL_ID,
                        Constant.COL_CREDIT_DATE,
                        Constant.COL_CREDIT_CATEGORY,
                        Constant.COL_CREDIT_DESCRIPTION,
                        Constant.COL_CREDIT_AMOUNT,
                        Constant.COL_CREDIT_TIMESTAMP},
                Constant.COL_ID + " = " + id,
                null,
                null,
                null,
                null);

        cursor.moveToFirst();
        Credit credit = createCredit(cursor);

        cursor.close();
        this.close();

        return credit;
    }

    // return all credits from credit table
    public List<Credit> getAllCredits() {
        ArrayList<Credit> credits = new ArrayList<>();
        this.open();

        Cursor cursor = database.rawQuery(
                "SELECT * FROM " + Constant.TABLE_CREDIT,
                null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                Credit credit = createCredit(cursor);
                credits.add(credit);
                cursor.moveToNext();
            }
        }

        if (cursor != null) {
            cursor.close();
        }

        database.close();

        return credits;
    }

    // return credit by category from credit table
    public List<Credit> getCreditsByCategoryAndMonth(String category, int month, int year) {
        ArrayList<Credit> credits = new ArrayList<>();
        this.open();

        Cursor cursor = database.rawQuery(
                "SELECT * FROM " + Constant.TABLE_CREDIT + " WHERE " +
                        Constant.COL_CREDIT_CATEGORY + " = ?",
                new String[]{category});

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                Credit credit = createCredit(cursor);
                credits.add(credit);
                cursor.moveToNext();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        if (database != null) {
            this.close();
        }

        List<Credit> foundCredits = new ArrayList<>(credits);

        for (Credit c : credits) {
            int firstIndex = c.getCreditDate().indexOf('-');
            int lastIndex = c.getCreditDate().lastIndexOf('-');
            int m = Integer.parseInt(c.getCreditDate().substring(firstIndex + 1, lastIndex));
            int y = Integer.parseInt(c.getCreditDate().substring(lastIndex + 1));

            if (m != month || y != year) {
                foundCredits.remove(c);
            }
        }

        return foundCredits;
    }

    // return credit by category from debit table
    public List<Credit> getCreditsByMonth(int month, int year) {
        List<Credit> credits = new LinkedList<>();
        this.open();

        Cursor cursor = database.rawQuery(
                "SELECT * FROM " + Constant.TABLE_CREDIT,
                null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                Credit credit = createCredit(cursor);
                credits.add(credit);
                cursor.moveToNext();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        if (database != null) {
            this.close();
        }

        List<Credit> foundCredits = new ArrayList<>(credits);

        for (Credit c : credits) {
            int firstIndex = c.getCreditDate().indexOf('-');
            int lastIndex = c.getCreditDate().lastIndexOf('-');
            int m = Integer.parseInt(c.getCreditDate().substring(firstIndex + 1, lastIndex));
            int y = Integer.parseInt(c.getCreditDate().substring(lastIndex + 1));

            if (m != month || y != year) {
                foundCredits.remove(c);
            }
        }

        return foundCredits;
    }

    // return all credits from credit table
    public ArrayList<Credit> getAllDeletedCredits() {
        ArrayList<Credit> credits = new ArrayList<>();
        this.open();

        Cursor cursor = database.rawQuery(
                "SELECT * FROM " + Constant.TABLE_DELETED_CREDIT,
                null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                Credit credit = createCredit(cursor);
                credits.add(credit);
                cursor.moveToNext();
            }
        }

        if (cursor != null) {
            cursor.close();
        }
        database.close();


        return credits;
    }

    // return all credit amounts from a specific date
    public ArrayList<Double> getCreditAmountsInThisDate(String date) {
        ArrayList<Double> creditAmounts = new ArrayList<>();
        this.open();

        Cursor cursor = database.rawQuery(
                "SELECT * FROM " + Constant.TABLE_CREDIT + " WHERE " +
                        Constant.COL_CREDIT_DATE + " = ?",
                new String[]{date});

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                Credit credit = createCredit(cursor);
                creditAmounts.add(credit.getCreditAmount());
                cursor.moveToNext();
            }
        }

        if (cursor != null) {
            cursor.close();
        }
        database.close();

        return creditAmounts;
    }

    // update a credit with a given value
    public boolean updateCredit(int id, Credit credit) {
        this.open();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Constant.COL_CREDIT_DATE, credit.getCreditDate());
        contentValues.put(Constant.COL_CREDIT_CATEGORY, credit.getCreditCategory());
        contentValues.put(Constant.COL_CREDIT_DESCRIPTION, credit.getCreditDescription());
        contentValues.put(Constant.COL_CREDIT_AMOUNT, credit.getCreditAmount());
        contentValues.put(Constant.COL_CREDIT_TIMESTAMP, credit.getCreditTimestamp());

        int updated = database.update(
                Constant.TABLE_CREDIT,
                contentValues,
                Constant.COL_ID + " = ?",
                new String[]{String.valueOf(id)});
        this.close();

        return updated > 0;
    }

    // delete a credit from credit table by credit id
    public boolean deleteCredit(int id) {
        this.open();

        int deleted = database.delete(
                Constant.TABLE_CREDIT,
                Constant.COL_ID + " = ?" + id,
                new String[]{String.valueOf(id)});
        this.close();

        return deleted > 0;
    }

    // return total amount of credits
    public double getTotalCreditAmount() {
        this.open();
        Cursor c = database.rawQuery(
                "SELECT SUM(" + Constant.COL_CREDIT_AMOUNT + ") FROM " + Constant.TABLE_CREDIT,
                null);
        c.moveToFirst();
        double amount = c.getDouble(0);
        c.close();
        return amount;
    }

    // return total credit amount by category
    public double getTotalCreditAmountByCategory(String category) {
        this.open();
        Cursor c = database.rawQuery(
                "SELECT TOTAL(" + Constant.COL_CREDIT_AMOUNT + ") FROM " +
                        Constant.TABLE_CREDIT + " WHERE " + Constant.COL_CREDIT_CATEGORY + " = ?",
                new String[]{category});
        c.moveToFirst();
        double total = c.getDouble(0);
        c.close();
        return total;
    }

    // return total credit amount by category
    public double getTotalCreditAmountByCategoryAndMonth(String category, int month, int year) {
        List<Credit> credits = new LinkedList<>();
        double totalCredit = 0;
        this.open();
        Cursor cursor = database.rawQuery(
                "SELECT * FROM " + Constant.TABLE_CREDIT + " WHERE " +
                        Constant.COL_CREDIT_CATEGORY + " = ?",
                new String[]{category});

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                Credit credit = createCredit(cursor);
                credits.add(credit);
                cursor.moveToNext();
            }
        }

        if (cursor != null) {
            cursor.close();
        }
        if (database != null) {
            database.close();
        }

        for (Credit c : credits) {
            String date = c.getCreditDate();
            int firstIndex = date.indexOf('-');
            int lastIndex = date.lastIndexOf('-');
            int m = Integer.parseInt(date.substring(firstIndex + 1, lastIndex));
            int y = Integer.parseInt(date.substring(lastIndex + 1));
            if (m != month || y != year) {
                continue;
            }
            totalCredit += c.getCreditAmount();
        }

        return totalCredit;
    }

    // return total credit amount by month
    public double getTotalCreditAmountByMonth(int month, int year) {
        List<Credit> credits = new LinkedList<>();
        double totalCredit = 0;
        this.open();

        Cursor cursor = database.rawQuery(
                "SELECT * FROM " + Constant.TABLE_CREDIT,
                null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                Credit credit = createCredit(cursor);
                credits.add(credit);
                cursor.moveToNext();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        if (database != null) {
            database.close();
        }

        for (Credit c : credits) {
            String date = c.getCreditDate();
            int firstIndex = date.indexOf('-');
            int lastIndex = date.lastIndexOf('-');
            int m = Integer.parseInt(date.substring(firstIndex + 1, lastIndex));
            int y = Integer.parseInt(date.substring(lastIndex + 1));

            if (m != month || y != year) {
                continue;
            }
            totalCredit += c.getCreditAmount();
        }

        return totalCredit;
    }

    // delete all credits
    public void deleteAllCredits() {
        this.open();

        database.delete(Constant.TABLE_CREDIT, null, null);

        this.close();
    }

    // create a credit from cursor data
    private Credit createCredit(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(Constant.COL_ID));
        String creditDate = cursor.getString(cursor.getColumnIndex(Constant.COL_CREDIT_DATE));
        String creditCategory = cursor.getString(cursor.getColumnIndex(Constant.COL_CREDIT_CATEGORY));
        String creditDescription = cursor.getString(cursor.getColumnIndex(Constant.COL_CREDIT_DESCRIPTION));
        Double creditAmount = cursor.getDouble(cursor.getColumnIndex(Constant.COL_CREDIT_AMOUNT));
        int creditTimestamp = cursor.getInt(cursor.getColumnIndex(Constant.COL_CREDIT_TIMESTAMP));

        return new Credit(id, creditDate, creditCategory, creditDescription, creditAmount, creditTimestamp);
    }
}
