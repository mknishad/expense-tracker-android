package backbencers.nub.dailycostcalc.model;

/**
 * Created by Nishad on 4/28/2017.
 */

public class TempCredit {

    private String date;
    private double amount;

    public TempCredit(String date, double amount) {
        this.date = date;
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
