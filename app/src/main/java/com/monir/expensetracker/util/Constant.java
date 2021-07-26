package com.monir.expensetracker.util;

public class Constant {

  public static final String DATABASE_NAME = "Expense.db";
  public static final int DATABASE_VERSION = 1;
  public static final String TABLE_DEBIT = "Debit";
  public static final String TABLE_CREDIT = "Credit";
  public static final String TABLE_CATEGORY = "Category";
  public static final String TABLE_DELETED_CREDIT = "DeletedCredit";

  public static final String COL_ID = "Id";

  public static final String COL_DEBIT_DATE = "DebitDate";
  public static final String COL_DEBIT_CATEGORY = "DebitCategory";
  public static final String COL_DEBIT_DESCRIPTION = "DebitDescription";
  public static final String COL_DEBIT_AMOUNT = "DebitAmount";

  public static final String COL_CREDIT_DATE = "CreditDate";
  public static final String COL_CREDIT_CATEGORY = "CreditCategory";
  public static final String COL_CREDIT_DESCRIPTION = "CreditDescription";
  public static final String COL_CREDIT_AMOUNT = "CreditAmount";
  public static final String COL_CREDIT_TIMESTAMP = "CreditTimestamp";

  public static final String COL_CATEGORY_NAME = "CategoryName";

  public static final String ACTIVITY_TYPE = "ActivityType";
  public static final String ACTIVITY_TYPE_ADD = "ActivityTypeAdd";
  public static final String ACTIVITY_TYPE_EDIT = "ActivityTypeEdit";
  public static final String CREDIT_ITEM_ID = "CreditItemPosition";
  public static final String DEBIT_ITEM_ID = "DebitItemPosition";

  // For sharedpreferences

  public static final String PREF_DEBIT_NAME = "DEBIT_INFO";
  public static final String PREF_IS_DATA_AVAILABLE = "IS_DATA_AVAILABLE";
  public static final String PREF_DEBIT_OBJECT = "DEBIT_OBJECT";

  public static final String CATEGORY_BUNDLE = "Category";


  // Intent Data

  public static final String INTENT_SCAN_DATA = "intent_scan_data";

}
