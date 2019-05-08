package backbencers.nub.dailycostcalc.model;

/**
 * Created by Invariant-PC on 26-Apr-17.
 */

public class Credit {

    private int creditId;
    private String creditDate;
    private String creditCategory;
    private String creditDescription;
    private Double creditAmount;
    private int creditTimestamp;

    public Credit() {
    }

    // Constructor without creditId
    public Credit(String creditDate, String creditCategory, String creditDescription, Double creditAmount, int creditTimestamp) {
        this.creditDate = creditDate;
        this.creditCategory = creditCategory;
        this.creditDescription = creditDescription;
        this.creditAmount = creditAmount;
        this.creditTimestamp = creditTimestamp;
    }

    // Constructor with creditId
    public Credit(int creditId, String creditDate, String creditCategory, String creditDescription, Double creditAmount, int creditTimestamp) {
        this.creditId = creditId;
        this.creditDate = creditDate;
        this.creditCategory = creditCategory;
        this.creditDescription = creditDescription;
        this.creditAmount = creditAmount;
        this.creditTimestamp = creditTimestamp;
    }

    public int getCreditId() {
        return creditId;
    }

    public void setCreditId(int creditId) {
        this.creditId = creditId;
    }

    public String getCreditDate() {
        return creditDate;
    }

    public void setCreditDate(String creditDate) {
        this.creditDate = creditDate;
    }

    public String getCreditCategory() {
        return creditCategory;
    }

    public void setCreditCategory(String creditCategory) {
        this.creditCategory = creditCategory;
    }

    public String getCreditDescription() {
        return creditDescription;
    }

    public void setCreditDescription(String creditDescription) {
        this.creditDescription = creditDescription;
    }

    public Double getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(Double creditAmount) {
        this.creditAmount = creditAmount;
    }

    public int getCreditTimestamp() {
        return creditTimestamp;
    }

    public void setCreditTimestamp(int creditTimestamp) {
        this.creditTimestamp = creditTimestamp;
    }
}
