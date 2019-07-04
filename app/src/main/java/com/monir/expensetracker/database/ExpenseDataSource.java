package com.monir.expensetracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.monir.expensetracker.util.Constant;
import com.monir.expensetracker.model.Category;
import com.monir.expensetracker.model.Credit;
import com.monir.expensetracker.model.Debit;

import java.util.ArrayList;
import java.util.List;

public class ExpenseDataSource {

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public ExpenseDataSource(Context context) {
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

        long inserted = database.insert(Constant.TABLE_DEBIT, null, contentValues);

        this.close();

        return inserted > 0;
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

        long inserted = database.insert(Constant.TABLE_CREDIT, null, contentValues);

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

        long inserted = database.insert(Constant.TABLE_DELETED_CREDIT, null, contentValues);

        this.close();

        return inserted > 0;
    }

    // insert a category to Category table
    public boolean insertCategory(Category category) {
        this.open();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Constant.COL_CATEGORY_NAME, category.getCategoryName());

        long inserted = database.insert(Constant.TABLE_CATEGORY, null, contentValues);

        this.close();

        return inserted > 0;
    }

    // get a single debit from the table by debit id
    public Debit getDebit(int id) {
        this.open();

        Cursor cursor = database.query(Constant.TABLE_DEBIT, new String[]{Constant.COL_ID,
                Constant.COL_DEBIT_DATE, Constant.COL_DEBIT_CATEGORY, Constant.COL_DEBIT_DESCRIPTION,
                Constant.COL_DEBIT_AMOUNT}, Constant.COL_ID + " = " + id, null, null, null, null);

        cursor.moveToFirst();
        Debit debit = createDebit(cursor);
        cursor.close();
        this.close();

        return debit;
    }

    // get a single credit from the table by credit id
    public Credit getCredit(int id) {
        this.open();

        Cursor cursor = database.query(Constant.TABLE_CREDIT, new String[]{Constant.COL_ID,
                Constant.COL_CREDIT_DATE, Constant.COL_CREDIT_CATEGORY, Constant.COL_CREDIT_DESCRIPTION,
                Constant.COL_CREDIT_AMOUNT, Constant.COL_CREDIT_TIMESTAMP}, Constant.COL_ID + " = " + id, null, null, null, null);

        cursor.moveToFirst();
        Credit credit = createCredit(cursor);

        cursor.close();
        this.close();

        return credit;
    }

    // return all debits from debit table
    public ArrayList<Debit> getAllDebits() {
        ArrayList<Debit> debits = new ArrayList<>();
        this.open();

        Cursor cursor = database.rawQuery("SELECT * FROM " + Constant.TABLE_DEBIT, null);

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

        Cursor cursor = database.rawQuery("SELECT * FROM " + Constant.TABLE_DEBIT +
                " WHERE " + Constant.COL_DEBIT_CATEGORY + " = ?", new String[]{category});

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

    // return all debit amounts from a specific date
    public List<Debit> getDebitsInThisDate(String date) {
        List<Debit> debits = new ArrayList<>();
        this.open();

        Cursor cursor = database.rawQuery("SELECT * FROM " + Constant.TABLE_DEBIT + " WHERE " +
                Constant.COL_DEBIT_DATE + " = ?", new String[]{date});

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

    // return all credits from credit table
    public ArrayList<Credit> getAllCredits() {
        ArrayList<Credit> credits = new ArrayList<>();
        this.open();

        Cursor cursor = database.rawQuery("SELECT * FROM " + Constant.TABLE_CREDIT, null);

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

    // return all credits from credit table
    public ArrayList<Credit> getAllDeletedCredits() {
        ArrayList<Credit> credits = new ArrayList<>();
        this.open();

        Cursor cursor = database.rawQuery("SELECT * FROM " + Constant.TABLE_DELETED_CREDIT, null);

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

    // return all categories from Category table
    public ArrayList<Category> getAllCategories() {
        ArrayList<Category> categories = new ArrayList<>();
        this.open();

        Cursor cursor = database.rawQuery("SELECT * FROM " + Constant.TABLE_CATEGORY, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                Category category = createCategory(cursor);
                categories.add(category);
                cursor.moveToNext();
            }
        }

        if (cursor != null) {
            cursor.close();
        }
        database.close();

        return categories;
    }

    // return all credit amounts from a specific date
    public ArrayList<Double> getCreditAmountsInThisDate(String date) {
        ArrayList<Double> creditAmounts = new ArrayList<>();
        this.open();

        Cursor cursor = database.rawQuery("SELECT * FROM " + Constant.TABLE_CREDIT + " WHERE " +
                Constant.COL_CREDIT_DATE + " = ?", new String[]{date});

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

    // update a debit with a given value
    public boolean updateDebit(int id, Debit debit) {
        this.open();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Constant.COL_DEBIT_DATE, debit.getDebitDate());
        contentValues.put(Constant.COL_DEBIT_CATEGORY, debit.getDebitCategory());
        contentValues.put(Constant.COL_DEBIT_DESCRIPTION, debit.getDebitDescription());
        contentValues.put(Constant.COL_DEBIT_AMOUNT, debit.getDebitAmount());

        int updated = database.update(Constant.TABLE_DEBIT, contentValues,
                Constant.COL_ID + " = " + id, null);
        this.close();

        return updated > 0;
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

        int updated = database.update(Constant.TABLE_CREDIT, contentValues,
                Constant.COL_ID + " = " + id, null);
        this.close();

        return updated > 0;
    }

    // delete a debit from debit table by debit id
    public boolean deleteDebit(int id) {
        this.open();

        int deleted = database.delete(Constant.TABLE_DEBIT, Constant.COL_ID + " = " + id, null);
        this.close();

        return deleted > 0;
    }

    // delete a credit from credit table by credit id
    public boolean deleteCredit(int id) {
        this.open();

        int deleted = database.delete(Constant.TABLE_CREDIT, Constant.COL_ID + " = " + id, null);
        this.close();

        return deleted > 0;
    }

    // return total amount of debits
    public double getTotalDebitAmount() {
        this.open();
        Cursor c = database.rawQuery("SELECT SUM(" + Constant.COL_DEBIT_AMOUNT + ") FROM " + Constant.TABLE_DEBIT, null);
        c.moveToFirst();
        double amount = c.getDouble(0);
        c.close();
        return amount;
    }

    // return total amount of debits
    public double getTotalDebitAmountOnThisYear(int year) {
        this.open();
        Cursor c = database.rawQuery("SELECT SUM(" + Constant.COL_DEBIT_AMOUNT + ") FROM " + Constant.TABLE_DEBIT, null);
        c.moveToFirst();
        double amount = c.getDouble(0);
        c.close();
        return amount;
    }


    // return total amount of credits
    public double getTotalCreditAmount() {
        this.open();
        Cursor c = database.rawQuery("SELECT SUM(" + Constant.COL_CREDIT_AMOUNT + ") FROM " +
                Constant.TABLE_CREDIT, null);
        c.moveToFirst();
        double amount = c.getDouble(0);
        c.close();
        return amount;
    }

    // return total credit amount by category
    public double getTotalCreditByCategory(String category) {
        this.open();
        Cursor c = database.rawQuery("SELECT TOTAL(" + Constant.COL_DEBIT_AMOUNT + ") FROM " +
                        Constant.TABLE_DEBIT + " WHERE " + Constant.COL_DEBIT_CATEGORY + " = ?",
                new String[]{category});
        c.moveToFirst();
        double total = c.getDouble(0);
        c.close();
        return total;
    }

    // delete all credits
    public void deleteAllCredits() {
        this.open();

        database.delete(Constant.TABLE_CREDIT, null, null);

        this.close();
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

    // Create a category from cursor data
    private Category createCategory(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(Constant.COL_ID));
        String categoryName = cursor.getString(cursor.getColumnIndex(Constant.COL_CATEGORY_NAME));

        return new Category(id, categoryName);
    }
}
