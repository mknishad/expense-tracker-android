package com.monir.expensetracker.custom;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.monir.expensetracker.R;

/**
 * Created by dutchman on 4/12/17.
 */

public class CalculatorDialog implements View.OnClickListener {

    private Context context;

    private Button btnSeven, btnEight, btnNine, btnDiv;
    private Button btnFour, btnFive, btnSix, btnMul;
    private Button btnOne, btnTwo, btnThree, btnSub;
    private Button btnPoint, btnZero, btnPlus, btnEqual;

    private TextView tvCalDisplay;

    private String number = "";
    private char operator = ' ';

    private double fNum, sNum, result;

    public CalculatorDialog(){

    }


    public void showCalculator(Activity activity) {

        context = activity.getApplicationContext();

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);

        //Text Title
        TextView title = new TextView(context);
        title.setText("Calculator");
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
//        title.setBackgroundColor(context.getResources().getColor(android.R.color.white));
//        title.setTextColor(context.getResources().getColor(android.R.color.black));
        title.setTextSize(25);

        //alertDialog.setTitle(cName);
        alertDialog.setCustomTitle(title);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View convertView = (View) inflater.inflate(R.layout.layout_calculator, null);


        initViews(convertView);

        alertDialog.setView(convertView);

        alertDialog.setCancelable(true);

        final AlertDialog dialog = alertDialog.create();

        setActions();


        dialog.show();

    }

    private void initViews(View view){

        tvCalDisplay = (TextView) view.findViewById(R.id.tvCalDisplay);

        tvCalDisplay.setText("0");

        btnSeven = (Button) view.findViewById(R.id.btnSeven);
        btnEight = (Button) view.findViewById(R.id.btnEight);
        btnNine = (Button) view.findViewById(R.id.btnNine);
        btnDiv = (Button) view.findViewById(R.id.btnDiv);

        btnFour = (Button) view.findViewById(R.id.btnFour);
        btnFive = (Button) view.findViewById(R.id.btnFive);
        btnSix = (Button) view.findViewById(R.id.btnSix);
        btnMul = (Button) view.findViewById(R.id.btnMul);

        btnOne = (Button) view.findViewById(R.id.btnOne);
        btnTwo = (Button) view.findViewById(R.id.btnTwo);
        btnThree = (Button) view.findViewById(R.id.btnThree);
        btnSub = (Button) view.findViewById(R.id.btnSub);

        btnPoint = (Button) view.findViewById(R.id.btnPoint);
        btnZero = (Button) view.findViewById(R.id.btnZero);
        btnPlus = (Button) view.findViewById(R.id.btnPlus);
        btnEqual = (Button) view.findViewById(R.id.btnEqual);

    }

    private void setActions(){

        btnSeven.setOnClickListener(this);
        btnEight.setOnClickListener(this);
        btnNine.setOnClickListener(this);
        btnDiv.setOnClickListener(this);

        btnFour.setOnClickListener(this);
        btnFive.setOnClickListener(this);
        btnSix.setOnClickListener(this);
        btnMul.setOnClickListener(this);

        btnOne.setOnClickListener(this);
        btnTwo.setOnClickListener(this);
        btnThree.setOnClickListener(this);
        btnSub.setOnClickListener(this);

        btnPoint.setOnClickListener(this);
        btnZero.setOnClickListener(this);
        btnPlus.setOnClickListener(this);
        btnEqual.setOnClickListener(this);

    }



    @Override
    public void onClick(View v) {


        int id = v.getId();

//        if (id != R.id.btnDiv && id != R.id.btnMul && id != R.id.btnSub && id != R.id.btnPlus && id != R.id.btnEqual) {
//
//
//            Button btnDigit = (Button) v.findViewById(id);
//
//            number += btnDigit.getText().toString();
//
//            tvCalDisplay.setText(number);
//
//        }
//        if(number.trim().length() > 0){
//            if (id == R.id.btnDiv || id == R.id.btnMul || id == R.id.btnSub || id == R.id.btnPlus) {
//
//                Button btnOper = (Button) v.findViewById(id);
//
//                operator = btnOper.getText().toString().toLowerCase().charAt(0);
//                //tvCalDisplay.setText(operator+"");
//                fNum = Double.parseDouble(number.trim());
//                number = "";
//            }
//
//            if(id == R.id.btnEqual){
//
//                sNum = Double.parseDouble(number);
//                calculate();
//            }
//        }

        switch (id){

            case R.id.btnDiv:
            case R.id.btnMul:
            case R.id.btnSub:
            case R.id.btnPlus:

                if(tvCalDisplay.getText().toString().trim().length() > 0) {

                    Button btnOper = (Button) v.findViewById(id);
                    operator = btnOper.getText().toString().toLowerCase().charAt(0);
                    //tvCalDisplay.setText(operator+"");
                    fNum = Double.parseDouble(tvCalDisplay.getText().toString().trim());
                    number = "";

                }
                break;

            case R.id.btnEqual:

                sNum = Double.parseDouble(tvCalDisplay.getText().toString().trim());
                calculate();

                break;

            default:
                Button btnDigit = (Button) v.findViewById(id);
                number += btnDigit.getText().toString();
                tvCalDisplay.setText(number);
                break;
        }


    }

    private void calculate(){

        switch (operator){

            case '+':

                result = fNum + sNum;
                break;
            case '-':
                result = fNum - sNum;
                break;

            case 'x':
                result = fNum * sNum;
                break;

            case '÷':
                result = fNum / sNum;
                break;

        }
        tvCalDisplay.setText(""+result);

    }
}