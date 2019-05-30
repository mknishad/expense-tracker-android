package com.monir.expensetracker.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.system.ErrnoException;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.monir.expensetracker.R;
import com.monir.expensetracker.util.Constant;
import com.monir.expensetracker.util.FileCreator;
import com.monir.expensetracker.ocr.TessOCR;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


public class ScanActivity extends Activity {

    private static final String TAG = "ScanActivity";
    private CropImageView mCropImageView;
    Bitmap converted;
    private EditText etScanResultData;
    private TessOCR mTessOCR;
    private Uri mCropImageUri;
    public static final String lang = "eng";
    public static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/DemoOCR/";
    private ProgressDialog mProgressDialog;

    private File imgFile;

    private String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        category = getIntent().getStringExtra(Constant.CATEGORY_BUNDLE);

        etScanResultData = (EditText) findViewById(R.id.et_scan_result_data);

        mCropImageView = (CropImageView) findViewById(R.id.crop_image_view);
        String[] paths = new String[]{DATA_PATH, DATA_PATH + "tessdata/"};

        for (String path : paths) {
            File dir = new File(path);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Log.v("Main", "ERROR: Creation of directory " + path + " on sdcard failed");
                    break;
                } else {
                    Log.v("Main", "Created directory " + path + " on sdcard");
                }
            }

        }
        if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata")).exists()) {
            try {

                AssetManager assetManager = getAssets();

                InputStream in = assetManager.open(lang + ".traineddata");
                //GZIPInputStream gin = new GZIPInputStream(in);
                OutputStream out = new FileOutputStream(DATA_PATH
                        + "tessdata/" + lang + ".traineddata");

                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                //while ((lenf = gin.read(buff)) > 0) {
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                //gin.close();
                out.close();

                // Log.v(TAG, "Copied " + lang + " traineddata");
            } catch (IOException e) {
                // Log.e(TAG, "Was unable to copy " + lang + " traineddata " + e.toString());
            }


        }
        mTessOCR = new TessOCR();
    }

    /**
     * On load image button click, start pick image chooser activity.
     */
    public void onLoadImageClick(View view) {
        startActivityForResult(getPickImageChooserIntent(), 200);
    }


    /**
     * On save image data button click, start debit editor activity.
     */

    public void onSaveImageData(View view) {

        if (etScanResultData.getText().toString().trim().length() > 0) {

            //Log.d(TAG, "onSaveImageData: "+etScanResultData.getText().toString());

            Intent intent = new Intent(this, DebitEditorActivity.class);

            intent.putExtra(Constant.INTENT_SCAN_DATA, etScanResultData.getText().toString());
            intent.putExtra(Constant.CATEGORY_BUNDLE, category);

            intent.putExtra(Constant.ACTIVITY_TYPE, Constant.ACTIVITY_TYPE_ADD);

            startActivity(intent);
            finish();

        }

    }


    /**
     * On open camera button click, start capture image.
     */

    public void onLaunchCamera(View view) {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        imgFile = FileCreator.createFile();

        Uri imgUri = Uri.fromFile(imgFile);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);

        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);         // 1 for high and 0 for low

        startActivityForResult(intent, 111);

    }

    /**
     * Crop the image and set it back to the cropping view.
     */

    public void onCropImageClick(View view) {
        if (mCropImageView != null) {
            Bitmap cropped = mCropImageView.getCroppedImage(500, 500);
            if (cropped != null) {
                mCropImageView.setImageBitmap(cropped);
                Bitmap bnwBitmap = convertColorIntoBlackAndWhiteImage(cropped);
                mCropImageView.setImageBitmap(bnwBitmap);
                doOCR(bnwBitmap);
            }
        }
    }

    public void doOCR(final Bitmap bitmap) {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(this, "Processing",
                    "Please wait...", true);
            // mResult.setVisibility(V.ViewISIBLE);


        } else {
            mProgressDialog.show();
        }

        new Thread(new Runnable() {
            public void run() {

                final String result = mTessOCR.getOCRResult(bitmap).toLowerCase();
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        if (result != null && !result.equals("")) {
                            String s = result.trim();
                            etScanResultData.setText(result);
                        }

                        mProgressDialog.dismiss();
                    }

                });

            }

            ;
        }).start();

    }

    private Bitmap convertColorIntoBlackAndWhiteImage(Bitmap originalBitmap) {
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);

        ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(
                colorMatrix);

        Bitmap blackAndWhiteBitmap = originalBitmap.copy(
                Bitmap.Config.ARGB_8888, true);

        Paint paint = new Paint();
        paint.setColorFilter(colorMatrixFilter);

        Canvas canvas = new Canvas(blackAndWhiteBitmap);
        canvas.drawBitmap(blackAndWhiteBitmap, 0, 0, paint);

        return blackAndWhiteBitmap;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == 200) {                        // for open image request
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = getPickImageResultUri(data);

                // For API >= 23 we need to check specifically that we have permissions to read external storage,
                // but we don't know if we need to for the URI so the simplest is to try open the stream and see if we get error.
                boolean requirePermissions = false;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                        checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                        isUriRequiresPermissions(imageUri)) {

                    // request permissions and handle the result in onRequestPermissionsResult()
                    requirePermissions = true;
                    mCropImageUri = imageUri;
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                }

                if (!requirePermissions) {
                    mCropImageView.setImageUriAsync(imageUri);
                    mCropImageView.setClickable(true);
                }
            }

        }
//        else if(requestCode == 111){                      // For capture image request
//
//            switch (resultCode){
//
//                case Activity.RESULT_OK:
//
//                    if(imgFile != null && imgFile.exists()){
//
//                        Log.d(TAG, "onActivityResult: File on "+imgFile.getAbsolutePath());
//
//                        Bundle bundle = data.getExtras();
//
//                        Bitmap bitmap = (Bitmap) bundle.get("data");
//
//                        mCropImageView.setImageBitmap(bitmap);
//
//                    } else{
//                        Log.d(TAG, "onActivityResult: File not created");
//                    }
//
//                    break;
//                case Activity.RESULT_CANCELED:
//
//                    break;
//
//                default:
//
//                    break;
//
//            }
//
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            Log.d(TAG, "onRequestPermissionsResult: " + mCropImageUri.getPath());
            mCropImageView.setImageUriAsync(mCropImageUri);
            mCropImageView.setClickable(true);
        } else {
            Toast.makeText(this, "Required permissions are not granted", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Create a chooser intent to select the source to get image from.<br/>
     * The source can be camera's (ACTION_IMAGE_CAPTURE) or gallery's (ACTION_GET_CONTENT).<br/>
     * All possible sources are added to the intent chooser.
     */
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

    /**
     * Get URI to image received from capture by camera.
     */
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

    /**
     * Get the URI of the selected image from {@link #getPickImageChooserIntent()}.<br/>
     * Will return the correct URI for camera and gallery image.
     *
     * @param data the returned data of the activity result
     */
    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null && data.getData() != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }

        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    /**
     * Test if we can open the given Android URI to test if permission required error is thrown.<br>
     */
    public boolean isUriRequiresPermissions(Uri uri) {
        try {
            ContentResolver resolver = getContentResolver();
            InputStream stream = resolver.openInputStream(uri);
            stream.close();

            return false;
        } catch (FileNotFoundException e) {
            if (e.getCause() instanceof ErrnoException) {

                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }
}