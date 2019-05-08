package backbencers.nub.dailycostcalc.custom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import backbencers.nub.dailycostcalc.R;
import backbencers.nub.dailycostcalc.activities.MainActivity;
import backbencers.nub.dailycostcalc.activities.ScanActivity;
import backbencers.nub.dailycostcalc.constant.Constant;
import backbencers.nub.dailycostcalc.model.Debit;
import backbencers.nub.dailycostcalc.objects.ObjectParser;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by dutchman on 4/21/17.
 */

public class SaveOption {


    private Context context;

    private String text;

    private Activity activity;

    private Debit debit = null;
    private String category;


    public SaveOption(Activity activity, String category, String text){

        this.activity = activity;
        this.category = category;
        this.context = activity.getApplicationContext();
        this.text = text;

    }


    public void showDetailData(){



        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);

        //Text Title
        TextView title = new TextView(context);
        title.setText("Save data");
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        //title.setTextColor(context.getResources().getColor(R.color.colorPrimaryText));
        title.setTextSize(25);

        //alertDialog.setTitle(cName);
        alertDialog.setCustomTitle(title);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        View convertView = (View) inflater.inflate(R.layout.custom_save_option, null);


//        ListView lvCustomDailog = (ListView) convertView.findViewById(R.id.lvCustomDailog);
//
        final EditText etNotiText = (EditText) convertView.findViewById(R.id.etCusNotiText);

        Button btnSave = (Button) convertView.findViewById(R.id.btnCusNotiSave);

        Button btnCancel = (Button) convertView.findViewById(R.id.btnCusNotiCancel);


        etNotiText.setText(text);

        alertDialog.setView(convertView);

        alertDialog.setCancelable(false);

        final AlertDialog dialog = alertDialog.create();
        final SharedPreferences.Editor editor = context.getApplicationContext().getSharedPreferences(Constant.PREF_DEBIT_NAME, MODE_PRIVATE).edit();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    if (etNotiText.getText().toString().trim().length() > 0) {

                        ObjectParser parser = new ObjectParser(category,etNotiText.getText().toString().trim());

                        debit = parser.parse();

                        editor.putBoolean(Constant.PREF_IS_DATA_AVAILABLE,true);

                        Gson gson = new Gson();
                        String json = gson.toJson(debit);
                        editor.putString(Constant.PREF_DEBIT_OBJECT, json);

                        editor.commit();

                        dialog.dismiss();

                        moveToMain();

                    } else {

                        Toast.makeText(context, "Write some text to save", Toast.LENGTH_SHORT).show();
                    }
            }
        });


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();


    }

    private void moveToMain(){

        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
        activity.finish();

    }


}
