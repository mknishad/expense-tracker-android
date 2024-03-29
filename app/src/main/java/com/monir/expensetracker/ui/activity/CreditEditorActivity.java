package com.monir.expensetracker.ui.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.core.app.NavUtils;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.monir.expensetracker.R;
import com.monir.expensetracker.database.CreditDataSource;
import com.monir.expensetracker.model.Credit;
import com.monir.expensetracker.util.Constant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreditEditorActivity extends AppCompatActivity {

  private static EditText etCreditDate;
  private String TAG = CreditEditorActivity.class.getSimpleName();
  private Toolbar toolbar;
  private Spinner categorySpinner;
  private EditText etCreditDescription;
  private EditText etCreditAmount;
  private CreditDataSource creditDataSource;
  private ArrayList<String> categoriesString = new ArrayList<>();
  private List<String> categoryList;
  private String activityType;
  private int creditId;
  private Credit credit;
  private boolean creditHasChanged = false;
  private View.OnTouchListener touchListener = new View.OnTouchListener() {
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
      creditHasChanged = true;
      return false;
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_credit_editor);

    creditDataSource = new CreditDataSource(this);
    initializeViews();

    Intent creditIntent = getIntent();
    activityType = creditIntent.getStringExtra(Constant.ACTIVITY_TYPE);
    Log.e(TAG, "Activity type: " + activityType);

    if (activityType.equals(Constant.ACTIVITY_TYPE_ADD)) {
      toolbar.setTitle(R.string.add_credit);
      invalidateOptionsMenu();
      setInitialDate();
    } else if (activityType.equals(Constant.ACTIVITY_TYPE_EDIT)) {
      toolbar.setTitle(R.string.edit_credit);
      creditId = creditIntent.getIntExtra(Constant.CREDIT_ITEM_ID, -1);
      Log.e(TAG, "credit list item position: " + creditId);
      if (creditId > -1) {
        credit = creditDataSource.getCredit(creditId);
        etCreditDate.setText(credit.getCreditDate());
        categorySpinner.setSelection(categoryList.indexOf(credit.getCreditCategory()));
        etCreditDescription.setText(credit.getCreditDescription());
        etCreditAmount.setText(String.valueOf(credit.getCreditAmount()));
      } else {
        Toast.makeText(this, "Error loading credit!", Toast.LENGTH_SHORT).show();
      }
    }
  }

  private void initializeViews() {
    initToolbar();

    etCreditDate = findViewById(R.id.edit_text_credit_date);
    categorySpinner = findViewById(R.id.categorySpinner);
    etCreditDescription = findViewById(R.id.edit_text_credit_description);
    etCreditAmount = findViewById(R.id.edit_text_credit_amount);

    initializeCategorySpinner();

    etCreditDate.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
      }
    });

    etCreditDate.setOnTouchListener(touchListener);
    //actvCreditCategory.setOnTouchListener(touchListener);
    categorySpinner.setOnTouchListener(touchListener);
    etCreditDescription.setOnTouchListener(touchListener);
    etCreditAmount.setOnTouchListener(touchListener);
  }

  private void initToolbar() {
    toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setDisplayShowTitleEnabled(false);
    toolbar.setTitleTextColor(getResources().getColor(R.color.colorText));
    toolbar.setTitle(R.string.add_credit);
    toolbar.setTitleTextColor(Color.WHITE);
    toolbar.getNavigationIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onBackPressed();
      }
    });
  }

  private void setInitialDate() {
    String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
    etCreditDate.setText(date);
  }

  private void initializeCategorySpinner() {
    String[] categories = getResources().getStringArray(R.array.credit_categories);
    categoryList = Arrays.asList(categories);
    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, categoryList);
    adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
    categorySpinner.setAdapter(adapter);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_editor, menu);
    return true;
  }

  /**
   * This method is called after invalidateOptionsMenu(), so that the
   * menu can be updated (some menu items can be hidden or made visible).
   */
  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    super.onPrepareOptionsMenu(menu);
    // If this is a new credit, hide the "Delete" menu item.
    if (activityType.equals(Constant.ACTIVITY_TYPE_ADD)) {
      MenuItem menuItem = menu.findItem(R.id.action_delete);
      menuItem.setVisible(false);
    }
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      // Respond to the action bar's Up/Home button
      case android.R.id.home:
        // If the credit hasn't changed, continue with navigating up to parent activity
        // which is the {@link MainActivity}.
        if (!creditHasChanged) {
          NavUtils.navigateUpFromSameTask(CreditEditorActivity.this);
          return true;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that
        // changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked "Discard" button, navigate to parent activity.
                NavUtils.navigateUpFromSameTask(CreditEditorActivity.this);
              }
            };

        // Show a dialog that notifies the user they have unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
        return true;

      case R.id.action_save:
        saveCredit();
        return true;

      // Respond to a click on the "Delete" menu option
      case R.id.action_delete:
        // Pop up confirmation dialog for deletion
        showDeleteConfirmationDialog();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void showDeleteConfirmationDialog() {
    // Create an AlertDialog.Builder and set the message, and click listeners
    // for the postivie and negative buttons on the dialog.
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage(R.string.delete_credit_dialog_msg);
    builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) {
        // User clicked the "Delete" button, so delete the pet.
        deleteCredit();
      }
    });
    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) {
        // User clicked the "Cancel" button, so dismiss the dialog
        // and continue editing the pet.
        if (dialog != null) {
          dialog.dismiss();
        }
      }
    });

    // Create and show the AlertDialog
    AlertDialog alertDialog = builder.create();
    alertDialog.show();
  }

  /**
   * Perform the deletion of the credit in the database.
   */
  private void deleteCredit() {
    // Only perform the delete if this is an existing pet.
    if (activityType.equals(Constant.ACTIVITY_TYPE_EDIT)) {
      boolean deleted = creditDataSource.deleteCredit(creditId);
      creditDataSource.insertDeletedCredit(credit);
      // Show a toast message depending on whether or not the delete was successful.
      if (deleted) {
        // If no rows were deleted, then there was an error with the delete.
        Toast.makeText(this, getString(R.string.editor_delete_credit_successful),
            Toast.LENGTH_SHORT).show();
      } else {
        // Otherwise, the delete was successful and we can display a toast.
        Toast.makeText(this, getString(R.string.editor_delete_credit_failed),
            Toast.LENGTH_SHORT).show();
      }
    }

    // Close the activity
    finish();
  }

  @Override
  public void onBackPressed() {
    // If the credit hasn't changed, continue with handling back button press
    if (!creditHasChanged) {
      super.onBackPressed();
      return;
    }

    // Otherwise if there are unsaved changes, setup a dialog to warn the user.
    // Create a click listener to handle the user confirming that changes should be discarded.
    DialogInterface.OnClickListener discardButtonClickListener =
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            // User clicked "Discard" button, close the current activity.
            finish();
          }
        };

    // Show dialog that there are unsaved changes
    showUnsavedChangesDialog(discardButtonClickListener);
  }

  private void saveCredit() {
    String date = etCreditDate.getText().toString().trim();
    //String category = actvCreditCategory.getText().toString().trim();
    String category = categorySpinner.getSelectedItem().toString();
    String description = etCreditDescription.getText().toString().trim();
    String amount = etCreditAmount.getText().toString().trim();

    if (TextUtils.isEmpty(date)) {
      Toast.makeText(this, "Please enter or select a date!", Toast.LENGTH_SHORT).show();
    } else if (category.equals(categoryList.get(0))) {
      Toast.makeText(this, "Please select a category!", Toast.LENGTH_SHORT).show();
    } else if (TextUtils.isEmpty(description)) {
      Toast.makeText(this, "Please enter description!", Toast.LENGTH_SHORT).show();
    } else if (TextUtils.isEmpty(amount)) {
      Toast.makeText(this, "Please enter amount!", Toast.LENGTH_SHORT).show();
    } else {
            /*if (!isCategoryExisted(category)) {
                expenseDataSource.insertCategory(new Category(category));
            }*/

      Credit credit = new Credit(date, category, description, new Double(amount), (int) (System.currentTimeMillis() % 100000000));
      Log.e(TAG, "system currentTimeMillis: " + System.currentTimeMillis() % 100000000);

      if (activityType.equals(Constant.ACTIVITY_TYPE_ADD)) {
        boolean inserted = creditDataSource.insertCredit(credit);
        if (inserted) {
          Toast.makeText(this, "Credit saved!", Toast.LENGTH_SHORT).show();
          setResult(RESULT_OK);
          finish();
        } else {
          Toast.makeText(this, "Failed to save credit!", Toast.LENGTH_SHORT).show();
        }
      } else if (activityType.equals(Constant.ACTIVITY_TYPE_EDIT)) {
        boolean updated = creditDataSource.updateCredit(creditId, credit);
        if (updated) {
          Toast.makeText(this, "Credit updated!", Toast.LENGTH_SHORT).show();
          setResult(RESULT_OK);
          finish();
        } else {
          Toast.makeText(this, "Failed to update credit!", Toast.LENGTH_SHORT).show();
        }
      }
    }
  }

  private boolean isCategoryExisted(String category) {
    for (String s : categoriesString) {
      if (s.equalsIgnoreCase(category)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Show a dialog that warns the user there are unsaved changes that will be lost
   * if they continue leaving the editor.
   *
   * @param discardButtonClickListener is the click listener for what to do when
   *                                   the user confirms they want to discard their changes
   */
  private void showUnsavedChangesDialog(
      DialogInterface.OnClickListener discardButtonClickListener) {
    // Create an AlertDialog.Builder and set the message, and click listeners
    // for the postivie and negative buttons on the dialog.
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage(R.string.unsaved_changes_dialog_msg);
    builder.setPositiveButton(R.string.discard, discardButtonClickListener);
    builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) {
        // User clicked the "Keep editing" button, so dismiss the dialog
        // and continue editing the pet.
        if (dialog != null) {
          dialog.dismiss();
        }
      }
    });

    // Create and show the AlertDialog
    AlertDialog alertDialog = builder.create();
    alertDialog.show();
  }

  public static class DatePickerFragment extends DialogFragment
      implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
      // Use the current date as the default date in the picker
      final Calendar c = Calendar.getInstance();
      int year = c.get(Calendar.YEAR);
      int month = c.get(Calendar.MONTH);
      int day = c.get(Calendar.DAY_OF_MONTH);

      // Create a new instance of DatePickerDialog and return it
      return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
      // Do something with the date chosen by the user
      month += 1;
      String finalDay = day > 9 ? ("" + day) : ("0" + day);
      String finalMonth = month > 9 ? ("" + month) : ("0" + month);
      String finalYear = "" + year;
      etCreditDate.setText("");
      etCreditDate.setText(finalDay + "-" + finalMonth + "-" + finalYear);
    }
  }
}
