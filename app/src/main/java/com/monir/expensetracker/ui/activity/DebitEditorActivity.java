package com.monir.expensetracker.ui.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.monir.expensetracker.R;
import com.monir.expensetracker.database.DebitDataSource;
import com.monir.expensetracker.database.ExpenseDataSource;
import com.monir.expensetracker.model.Category;
import com.monir.expensetracker.model.Debit;
import com.monir.expensetracker.util.Constant;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DebitEditorActivity extends AppCompatActivity {

    private String TAG = DebitEditorActivity.class.getSimpleName();
    private static final int RC_CHOOSE_IMAGE = 200;

    private Toolbar toolbar;
    private ProgressBar progressBar;
    private static EditText etDebitDate;
    //private AutoCompleteTextView actvDebitCategory;
    private Spinner categorySpinner;
    private EditText etDebitDescription;
    private EditText etDebitAmount;
    private FloatingActionButton fabScanDebit;
    private DebitDataSource debitDataSource;
    private ArrayList<String> categoriesString = new ArrayList<>();
    private List<String> categoryList;
    private String activityType;
    private int debitId;
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

        //String scanData = getIntent().getStringExtra(Constant.INTENT_SCAN_DATA);

        debitDataSource = new DebitDataSource(this);
        initializeViews();

        /*if (scanData != null && scanData.trim().length() > 0) {
            String category = getIntent().getStringExtra(Constant.CATEGORY_BUNDLE);

            if (category != null) {
                //actvDebitCategory.setText(category);
            }

            //ObjectParser objectParser = new ObjectParser(actvDebitCategory.getText().toString(), scanData);
            //debit = objectParser.parse();

            ///actvDebitCategory.setText(debit.getDebitCategory());
            etDebitDate.setText(debit.getDebitDate());
            etDebitAmount.setText(String.valueOf(debit.getDebitAmount()));
            etDebitDescription.setText(debit.getDebitDescription());
        }*/

        //getCategoriesFromDatabase();
        //ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, categoriesString);
        //actvDebitCategory.setAdapter(adapter);
        //actvDebitCategory.setThreshold(1);
        Intent debitIntent = getIntent();
        activityType = debitIntent.getStringExtra(Constant.ACTIVITY_TYPE);
        Log.e(TAG, "Activity type: " + activityType);

        if (activityType.equals(Constant.ACTIVITY_TYPE_ADD)) {
            toolbar.setTitle(R.string.add_debit);
            invalidateOptionsMenu();
            setInitialDate();
        } else if (activityType.equals(Constant.ACTIVITY_TYPE_EDIT)) {
            toolbar.setTitle(R.string.edit_debit);
            debitId = debitIntent.getIntExtra(Constant.DEBIT_ITEM_ID, -1);
            Log.e(TAG, "debit list item position: " + debitId);
            if (debitId > -1) {
                Debit debit = debitDataSource.getDebit(debitId);
                etDebitDate.setText(debit.getDebitDate());
                //actvDebitCategory.setText(debit.getDebitCategory());
                categorySpinner.setSelection(categoryList.indexOf(debit.getDebitCategory()));
                etDebitDescription.setText(debit.getDebitDescription());
                etDebitAmount.setText(String.valueOf(debit.getDebitAmount()));
            } else {
                Toast.makeText(this, "Error loading debit!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initializeViews() {
        initToolbar();
        progressBar = findViewById(R.id.progressBar);
        etDebitDate = (EditText) findViewById(R.id.edit_text_debit_date);
        //actvDebitCategory = (AutoCompleteTextView) findViewById(R.id.auto_complete_debit_category);
        categorySpinner = findViewById(R.id.categorySpinner);
        etDebitDescription = (EditText) findViewById(R.id.edit_text_debit_description);
        etDebitAmount = (EditText) findViewById(R.id.edit_text_debit_amount);
        fabScanDebit = findViewById(R.id.btn_scan_debit);

        initializeCategorySpinner();

        etDebitDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        fabScanDebit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasPermissions())
                    moveToScan();
                else
                    requestPerms();
            }
        });

        etDebitDate.setOnTouchListener(touchListener);
        //actvDebitCategory.setOnTouchListener(touchListener);
        categorySpinner.setOnTouchListener(touchListener);
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

    private void initializeCategorySpinner() {
        String[] categories = getResources().getStringArray(R.array.debit_categories);
        categoryList = Arrays.asList(categories);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, categoryList);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
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
            boolean deleted = debitDataSource.deleteDebit(debitId);
            // Show a toast message depending on whether or not the delete was successful.
            if (deleted) {
                Toast.makeText(this, getString(R.string.editor_delete_debit_successful),
                        Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_debit_failed),
                        Toast.LENGTH_SHORT).show();
            }
        }
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
        String category = categorySpinner.getSelectedItem().toString();
        String description = etDebitDescription.getText().toString().trim();
        String amount = etDebitAmount.getText().toString().trim();

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

            Debit debit = new Debit(date, category, description, Double.valueOf(amount));
            //Log.e(TAG, "system currentTimeMillis: " + System.currentTimeMillis() % 100000000);

            if (activityType.equals(Constant.ACTIVITY_TYPE_ADD)) {
                boolean inserted = debitDataSource.insertDebit(debit);
                if (inserted) {
                    Toast.makeText(this, "Debit saved!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(this, "Failed to save debit!", Toast.LENGTH_SHORT).show();
                }
            } else if (activityType.equals(Constant.ACTIVITY_TYPE_EDIT)) {
                boolean updated = debitDataSource.updateDebit(debitId, debit);
                if (updated) {
                    Toast.makeText(this, "Debit updated!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
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
        startActivityForResult(getPickImageChooserIntent(), RC_CHOOSE_IMAGE);
    }

    /*  Create a chooser intent to select the source to get image from.<br/>
      The source can be camera's (ACTION_IMAGE_CAPTURE) or gallery's (ACTION_GET_CONTENT).<br/>
      All possible sources are added to the intent chooser.*/
    public Intent getPickImageChooserIntent() {
        // Determine Uri of camera image to save.
        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        // collect all camera intents
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        // collect all gallery intents
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        // the main intent is the last in the list (fucking android) so pickup the useless one
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        // Create a chooser from the main intent
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    // Get URI to image received from capture by camera.
    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalCacheDir();

        //File file = FileCreator.createFile();

        if (getImage != null) {

            //mCropImageView.setClickable(true);
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "pickImageResult.jpeg"));
            //outputFileUri = Uri.fromFile(file);

        }
        return outputFileUri;
    }

    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null && data.getData() != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }

        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == RC_CHOOSE_IMAGE) {
                progressBar.setVisibility(View.VISIBLE);
                Uri imageUri = getPickImageResultUri(data);
                Log.d(TAG, "onActivityResult: imageUri = " + imageUri.toString());
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    runTextRecognition(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                }
            }
        }
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

    private void runTextRecognition(Bitmap bitmap) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionTextRecognizer recognizer = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();
        fabScanDebit.setEnabled(false);
        recognizer.processImage(image)
                .addOnSuccessListener(
                        new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText texts) {
                                fabScanDebit.setEnabled(true);
                                progressBar.setVisibility(View.GONE);
                                processTextRecognitionResult(texts);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                fabScanDebit.setEnabled(true);
                                progressBar.setVisibility(View.GONE);
                                e.printStackTrace();
                            }
                        });
    }

    private void processTextRecognitionResult(FirebaseVisionText texts) {
        List<FirebaseVisionText.TextBlock> blocks = texts.getTextBlocks();
        if (blocks.size() == 0) {
            Toast.makeText(this, "No text found", Toast.LENGTH_SHORT).show();
            return;
        }

        etDebitDescription.setText(texts.getText());

        String total = "";

        //mGraphicOverlay.clear();
        for (int i = 0; i < blocks.size(); i++) {
            List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
            for (int j = 0; j < lines.size(); j++) {
                List<FirebaseVisionText.Element> elements = lines.get(j).getElements();
                for (int k = 0; k < elements.size(); k++) {
                    //Graphic textGraphic = new TextGraphic(mGraphicOverlay, elements.get(k));
                    //mGraphicOverlay.add(textGraphic);
                    String elementText = elements.get(k).getText();
                    Log.d(TAG, "processTextRecognitionResult: elementText = " + elementText);

                    if (elementText.matches("-?\\d+(\\.\\d+)?")) {
                        total = elementText;
                    }
                }
            }
        }

        etDebitAmount.setText(total);
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

        if (requestCode == PERMS_REQUEST_CODE) {
            for (int res : grantResults) {
                // if user granted permissions
                allowed = allowed && (res == PackageManager.PERMISSION_GRANTED);
            }
        } else {// if user not granted permissions
            allowed = false;
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
