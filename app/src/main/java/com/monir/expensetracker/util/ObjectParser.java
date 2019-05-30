package com.monir.expensetracker.util;

import android.util.Log;

import com.monir.expensetracker.model.Debit;

import java.text.SimpleDateFormat;
import java.util.Date;


public class ObjectParser {

    private String text;
    private String category;

    public ObjectParser(String category, String text) {

        this.setText(text.trim());
        this.category = category;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Debit parse() {

        Debit debit = null;

        //"Description        Amount\nBiriani         125.00\nBurhani         30.00\nTotal        155.00"

        String[] tokens = this.text.toUpperCase().split("\n");

        String description = "";
        String total = "0.00";

        int len = tokens.length - 1;

        String tempTotalToken = "";
        boolean isTotal = false;

        double amount = 0.0;

        int index = 0;

        for (String token : tokens) {

            if (token.equals("TOTAL") || index == len) {

                Log.d("ObjectParser", "parse Total: " + token);
                tempTotalToken = token;
                String[] tTokens = token.trim().split(" ");
                total = tTokens[tTokens.length - 1];

                isTotal = Double.parseDouble(total) == amount;

            } else {

                Log.d("ObjectParser", "parse: " + token);
                description += token + "\n";
                String[] tTokens = token.trim().split(" ");
                String tempAmount = tTokens[tTokens.length - 1];
                amount += Double.parseDouble(tempAmount);

            }

            index++;

        }
        String date = new SimpleDateFormat("MMM dd, yyyy").format(new Date());

        if (isTotal) {
            debit = new Debit(date, category, description, Double.parseDouble(total));

        } else {
            //description += tempTotalToken +"\n";
            Log.d("ObjectParser", "parse: " + description);
            double tt = amount + Double.parseDouble(total);
            debit = new Debit(date, category, text.toUpperCase(), tt);
        }


        return debit;
    }
}
