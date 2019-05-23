package com.monir.expensetracker.model;

/**
 * Created by Invariant-PC on 26-Apr-17.
 */

public class Debit {

    private int debitId;
    private String debitDate;
    private String debitCategory;
    private String debitDescription;
    private Double debitAmount;

    // Constructor without debitID
    public Debit(String debitDate, String debitCategory, String debitDescription, Double debitAmount) {
        this.debitDate = debitDate;
        this.debitCategory = debitCategory;
        this.debitDescription = debitDescription;
        this.debitAmount = debitAmount;
    }

    // Constructor with debitID
    public Debit(int debitId, String debitDate, String debitCategory, String debitDescription, Double debitAmount) {
        this.debitId = debitId;
        this.debitDate = debitDate;
        this.debitCategory = debitCategory;
        this.debitDescription = debitDescription;
        this.debitAmount = debitAmount;
    }

    public int getDebitId() {
        return debitId;
    }

    public void setDebitId(int debitId) {
        this.debitId = debitId;
    }

    public String getDebitDate() {
        return debitDate;
    }

    public void setDebitDate(String debitDate) {
        this.debitDate = debitDate;
    }

    public String getDebitCategory() {
        return debitCategory;
    }

    public void setDebitCategory(String debitCategory) {
        this.debitCategory = debitCategory;
    }

    public String getDebitDescription() {
        return debitDescription;
    }

    public void setDebitDescription(String debitDescription) {
        this.debitDescription = debitDescription;
    }

    public Double getDebitAmount() {
        return debitAmount;
    }

    public void setDebitAmount(Double debitAmount) {
        this.debitAmount = debitAmount;
    }
}
