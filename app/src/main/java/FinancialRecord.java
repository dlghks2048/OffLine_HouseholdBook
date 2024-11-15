public class FinancialRecord {
    // Construct
    public FinancialRecord(String date, String categoryName, int amount, String memo) {
        this.date = date;
        this.categoryName = categoryName;
        this.amount = amount;
        this.memo = memo;
    }

    // Field
    private String date;    // 날짜
    private String categoryName;
    private int amount;
    private String memo;

    // Getter
    public String getDate() {
        return date;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public int getAmount() {
        return amount;
    }

    public String getMemo() {
        return memo;
    }
}


