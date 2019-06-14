package com.monir.expensetracker.ui.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.monir.expensetracker.R;
import com.monir.expensetracker.database.ExpenseDataSource;
import com.monir.expensetracker.model.Category;
import com.monir.expensetracker.model.Debit;
import com.monir.expensetracker.util.Constant;
import com.monir.expensetracker.util.ObjectParser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DebitEditorActivity extends AppCompatActivity {

    private String TAG = DebitEditorActivity.class.getSimpleName();

    private Toolbar toolbar;
    private static EditText etDebitDate;
    private AutoCompleteTextView actvDebitCategory;
    private EditText etDebitDescription;
    private EditText etDebitAmount;
    private Button btnScanDebit;
    private ExpenseDataSource expenseDataSource;
    private ArrayList<String> categoriesString = new ArrayList<>();
    private Intent debitIntent;
    private String activityType;
    private int debitId;
    private Debit debit;
    private boolean debitHasChanged = false;

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            debitHasChanged = true;
            return false;
        }
    };

    private String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private final int PERMS_REQUEST_CODE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debit_editor);

        String scanData = getIntent().getStringExtra(Constant.INTENT_SCAN_DATA);

        expenseDataSource = new ExpenseDataSource(this);
        initializeViews();

        if (scanData != null && scanData.trim().length() > 0) {
            String category = getIntent().getStringExtra(Constant.CATEGORY_BUNDLE);

            if (category != null) {
                actvDebitCategory.setText(category);
            }

            ObjectParser objectParser = new ObjectParser(actvDebitCategory.getText().toString(), scanData);
            debit = objectParser.parse();

            actvDebitCategory.setText(debit.getDebitCategory());
            etDebitDate.setText(debit.getDebitDate());
            etDebitAmount.setText(String.valueOf(debit.getDebitAmount()));
            etDebitDescription.setText(debit.getDebitDescription());
        }

        etDebitDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        btnScanDebit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasPermissions())
                    moveToScan();
                else
                    requestPerms();
            }
        });

        getCategoriesFromDatabase();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, categoriesString);
        actvDebitCategory.setAdapter(adapter);
        actvDebitCategory.setThreshold(1);
        debitIntent = getIntent();
        activityType = debitIntent.getStringExtra(Constant.ACTIVITY_TYPE);
        Log.e(TAG, "Activity type: " + activityType);

        if (activityType.equals(Constant.ACTIVITY_TYPE_ADD)) {
            setTitle("Add Debit");
            invalidateOptionsMenu();
            setInitialDate();
        } else if (activityType.equals(Constant.ACTIVITY_TYPE_EDIT)) {
            setTitle("Edit Debit");
            debitId = debitIntent.getIntExtra(Constant.DEBIT_ITEM_ID, -1);
            Log.e(TAG, "debit list item position: " + debitId);
            if (debitId > -1) {
                debit = expenseDataSource.getDebit(debitId);
                etDebitDate.setText(debit.getDebitDate());
                actvDebitCategory.setText(debit.getDebitCategory());
                etDebitDescription.setText(debit.getDebitDescription());
                etDebitAmount.setText("" + debit.getDebitAmount());
            } else {
                Toast.makeText(this, "Error loading debit!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initializeViews() {
        initToolbar();
        etDebitDate = (EditText) findViewById(R.id.edit_text_debit_date);
        actvDebitCategory = (AutoCompleteTextView) findViewById(R.id.auto_complete_debit_category);
        etDebitDescription = (EditText) findViewById(R.id.edit_text_debit_description);
        etDebitAmount = (EditText) findViewById(R.id.edit_text_debit_amount);
        btnScanDebit = (Button) findViewById(R.id.btn_scan_debit);

        etDebitDate.setOnTouchListener(touchListener);
        actvDebitCategory.setOnTouchListener(touchListener);
        etDebitDescription.setOnTouchListener(touchListener);
        etDebitAmount.setOnTouchListener(touchListener);
    }


    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorText));
        toolbar.setTitle(R.string.add_debit);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.getNavigationIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void getCategoriesFromDatabase() {
        ArrayList<Category> categories = expenseDataSource.getAllCategories();
        for (int i = 0; i < categories.size(); i++) {
            String c = categories.get(i).getCategoryName();
            categoriesString.add(c);
        }
    }

    private void setInitialDate() {
        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        etDebitDate.setText(date);
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @NonNull
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
            etDebitDate.setText("");
            etDebitDate.setText(finalDay + "-" + finalMonth + "-" + finalYear);
        }
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
                if (!debitHasChanged) {
                    NavUtils.navigateUpFromSameTask(DebitEditorActivity.this);
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
                                NavUtils.navigateUpFromSameTask(DebitEditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;

            case R.id.action_save:
                saveDebit();
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
                // User clicked the "Delete" button, so delete the debit.
                deleteDebit();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the debit.
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
    private void deleteDebit() {
        // Only perform the delete if this is an existing debit.
        if (activityType.equals(Constant.ACTIVITY_TYPE_EDIT)) {
            boolean deleted = expenseDataSource.deleteDebit(debitId);
            // Show a toast message depending on whether or not the delete was successful.
            if (deleted) {
                Toast.makeText(this, getString(R.string.editor_delete_debit_successful),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_debit_failed),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }

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

    private void saveDebit() {
        String date = etDebitDate.getText().toString().trim();
        String category = actvDebitCategory.getText().toString().trim();
        String description = etDebitDescription.getText().toString().trim();
        String amount = etDebitAmount.getText().toString().trim();

        if (TextUtils.isEmpty(date)) {
            Toast.makeText(this, "Please enter or select a date!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(category)) {
            Toast.makeText(this, "Please enter a category!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Please enter description!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(amount)) {
            Toast.makeText(this, "Please enter amount!", Toast.LENGTH_SHORT).show();
        } else {
            if (!isCategoryExisted(category)) {
                expenseDataSource.insertCategory(new Category(category));
            }

            Debit debit = new Debit(date, category, description, Double.valueOf(amount));
            //Log.e(TAG, "system currentTimeMillis: " + System.currentTimeMillis() % 100000000);

            if (activityType.equals(Constant.ACTIVITY_TYPE_ADD)) {
                boolean inserted = expenseDataSource.insertDebit(debit);
                if (inserted) {
                    Toast.makeText(this, "Debit saved!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to save debit!", Toast.LENGTH_SHORT).show();
                }
            } else if (activityType.equals(Constant.ACTIVITY_TYPE_EDIT)) {
                boolean updated = expenseDataSource.updateDebit(debitId, debit);
                if (updated) {
                    Toast.makeText(this, "Debit updated!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to update debit!", Toast.LENGTH_SHORT).show();
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

    //Go to scan activity
    private void moveToScan() {
        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra(Constant.CATEGORY_BUNDLE, actvDebitCategory.getText().toString());
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    //Get the camera permission on runtime
    private boolean hasPermissions() {
        int res;
        //String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        for (String permission : permissions) {
            res = checkCallingOrSelfPermission(permission);
            if (!(res == PackageManager.PERMISSION_GRANTED)) {
                return false;
            }
        }
        return true;
    }

    private void requestPerms() {
        //String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, PERMS_REQUEST_CODE);
        }
    }

    @Override
    public void onBackPressed() {
        // If the credit hasn't changed, continue with handling back button press
        if (!debitHasChanged) {
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean allowed = true;

        switch (requestCode) {
            case PERMS_REQUEST_CODE:
                for (int res : grantResults) {
                    // if user granted permissions
                    allowed = allowed && (res == PackageManager.PERMISSION_GRANTED);
                }
                break;

            default:
                // if user not granted permissions
                allowed = false;
                break;
        }

        if (allowed) {
            //user granted all permissions we can perform out task
            moveToScan();
        } else {
            // we will give warning to user that they haven't granted permissions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(permissions[0])) {
                    Toast.makeText(this, "Camera permission denied!", Toast.LENGTH_SHORT).show();
                }
                if (shouldShowRequestPermissionRationale(permissions[1])) {
                    Toast.makeText(this, "Storage permission denied!", Toast.LENGTH_SHORT).show();
                }
                if (shouldShowRequestPermissionRationale(permissions[2])) {
                    Toast.makeText(this, "Reader permission denied!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
