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

public class DebitDataSource {

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public DebitDataSource(Context context) {
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

    // add a debit to Debit table
    public boolean insertDebit(Debit debit) {
        this.open();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Constant.COL_DEBIT_DATE, debit.getDebitDate());
        contentValues.put(Constant.COL_DEBIT_CATEGORY, debit.getDebitCategory());
        contentValues.put(Constant.COL_DEBIT_DESCRIPTION, debit.getDebitDescription());
        contentValues.put(Constant.COL_DEBIT_AMOUNT, debit.getDebitAmount());

        long inserted = database.insert(
                Constant.TABLE_DEBIT,
                null,
                contentValues);

        this.close();

        return inserted > 0;
    }

    // get a single debit from the debit table by debit id
    public Debit getDebit(int id) {
        this.open();

        Cursor cursor = database.query(
                Constant.TABLE_DEBIT,
                new String[]{Constant.COL_ID,
                        Constant.COL_DEBIT_DATE,
                        Constant.COL_DEBIT_CATEGORY,
                        Constant.COL_DEBIT_DESCRIPTION,
                        Constant.COL_DEBIT_AMOUNT},
                Constant.COL_ID + " = " + id,
                null,
                null,
                null,
                null);

        cursor.moveToFirst();
        Debit debit = createDebit(cursor);
        cursor.close();
        this.close();

        return debit;
    }

    // return all debits from debit table
    public ArrayList<Debit> getAllDebits() {
        ArrayList<Debit> debits = new ArrayList<>();
        this.open();

        Cursor cursor = database.rawQuery(
                "SELECT * FROM " + Constant.TABLE_DEBIT,
                null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                Debit debit = createDebit(cursor);
                debits.add(debit);
                cursor.moveToNext();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        if (database != null) {
            database.close();
        }

        return debits;
    }

    // return debits by category from debit table
    public ArrayList<Debit> getDebitsByCategory(String category) {
        ArrayList<Debit> debits = new ArrayList<>();
        this.open();

        Cursor cursor = database.rawQuery(
                "SELECT * FROM " + Constant.TABLE_DEBIT + " WHERE " +
                        Constant.COL_DEBIT_CATEGORY + " = ?",
                new String[]{category});

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                Debit debit = createDebit(cursor);
                debits.add(debit);
                cursor.moveToNext();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        if (database != null) {
            database.close();
        }

        return debits;
    }

    // return credit by category from debit table
    public List<Debit> getDebitsByMonth(int month, int year) {
        List<Debit> debits = new LinkedList<>();
        this.open();

        Cursor cursor = database.rawQuery(
                "SELECT * FROM " + Constant.TABLE_DEBIT,
                null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                Debit debit = createDebit(cursor);
                debits.add(debit);
                cursor.moveToNext();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        if (database != null) {
            database.close();
        }

        List<Debit> foundDebits = new LinkedList<>(debits);

        for (Debit d : debits) {
            String date = d.getDebitDate();
            int firstIndex = date.indexOf('-');
            int lastIndex = date.lastIndexOf('-');
            int m = Integer.parseInt(date.substring(firstIndex + 1, lastIndex));
            int y = Integer.parseInt(date.substring(lastIndex + 1));

            if (m != month || y != year) {
                foundDebits.remove(d);
            }
        }

        return foundDebits;
    }

    // return all debit amounts from a specific date
    public List<Debit> getDebitsInThisDate(String date) {
        List<Debit> debits = new ArrayList<>();
        this.open();

        Cursor cursor = database.rawQuery(
                "SELECT * FROM " + Constant.TABLE_DEBIT + " WHERE " +
                        Constant.COL_DEBIT_DATE + " = ?",
                new String[]{date});

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                Debit debit = createDebit(cursor);
                debits.add(debit);
                cursor.moveToNext();
            }
        }

        if (cursor != null)
            cursor.close();
        if (database != null)
            database.close();

        return debits;
    }

    // update a debit with a given value
    public boolean updateDebit(int id, Debit debit) {
        this.open();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Constant.COL_DEBIT_DATE, debit.getDebitDate());
        contentValues.put(Constant.COL_DEBIT_CATEGORY, debit.getDebitCategory());
        contentValues.put(Constant.COL_DEBIT_DESCRIPTION, debit.getDebitDescription());
        contentValues.put(Constant.COL_DEBIT_AMOUNT, debit.getDebitAmount());

        int updated = database.update(
                Constant.TABLE_DEBIT,
                contentValues,
                Constant.COL_ID + " = ?",
                new String[]{String.valueOf(id)});
        this.close();

        return updated > 0;
    }

    // delete a debit from debit table by debit id
    public boolean deleteDebit(int id) {
        this.open();

        int deleted = database.delete(
                Constant.TABLE_DEBIT, Constant.COL_ID + " = ?",
                new String[]{String.valueOf(id)});
        this.close();

        return deleted > 0;
    }

    // return total amount of debits
    public double getTotalDebitAmount() {
        this.open();
        Cursor c = database.rawQuery(
                "SELECT SUM(" + Constant.COL_DEBIT_AMOUNT + ") FROM " + Constant.TABLE_DEBIT,
                null);
        c.moveToFirst();
        double amount = c.getDouble(0);
        c.close();
        return amount;
    }

    public double getTotalDebitAmountByCategory(String category) {
        this.open();
        Cursor c = database.rawQuery(
                "SELECT SUM(" + Constant.COL_DEBIT_AMOUNT + ") FROM " + Constant.TABLE_DEBIT +
                        " WHERE " + Constant.COL_DEBIT_CATEGORY + " = ?",
                new String[]{category});
        c.moveToFirst();
        double amount = c.getDouble(0);
        c.close();
        return amount;
    }

    // return total debit amount by month
    public double getTotalDebitAmountByMonth(int month, int year) {
        List<Debit> debits = new LinkedList<>();
        double totalDebit = 0;
        this.open();

        Cursor cursor = database.rawQuery(
                "SELECT * FROM " + Constant.TABLE_DEBIT,
                null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                Debit debit = createDebit(cursor);
                debits.add(debit);
                cursor.moveToNext();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        if (database != null) {
            database.close();
        }

        for (Debit d : debits) {
            String date = d.getDebitDate();
            int firstIndex = date.indexOf('-');
            int lastIndex = date.lastIndexOf('-');
            int m = Integer.parseInt(date.substring(firstIndex + 1, lastIndex));
            int y = Integer.parseInt(date.substring(lastIndex + 1));

            if (m != month || y != year) {
                continue;
            }
            totalDebit += d.getDebitAmount();
        }

        return totalDebit;
    }

    // return total amount of debits
    public double getTotalDebitAmountOnThisYear(int year) {
        this.open();
        Cursor c = database.rawQuery(
                "SELECT SUM(" + Constant.COL_DEBIT_AMOUNT + ") FROM " + Constant.TABLE_DEBIT,
                null);
        c.moveToFirst();
        double amount = c.getDouble(0);
        c.close();
        return amount;
    }

    // create a debit from cursor data
    private Debit createDebit(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(Constant.COL_ID));
        String debitDate = cursor.getString(cursor.getColumnIndex(Constant.COL_DEBIT_DATE));
        String debitCategory = cursor.getString(cursor.getColumnIndex(Constant.COL_DEBIT_CATEGORY));
        String debitDescription = cursor.getString(cursor.getColumnIndex(Constant.COL_DEBIT_DESCRIPTION));
        Double debitAmount = cursor.getDouble(cursor.getColumnIndex(Constant.COL_DEBIT_AMOUNT));

        return new Debit(id, debitDate, debitCategory, debitDescription, debitAmount);
    }
}
