package net.devx1.expensemanager.model;

public class Expense {
    private long id;
    private String amount;
    private String type;
    private String date;

    public Expense(long id, String amount, String type, String date) {
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
