package net.devx1.expensemanager.model;

public class FireExpense {
	private int id, amount;
	private String type, date;

	public FireExpense(int amount, String type, String date) {
		this.id = -1;
		this.amount = amount;
		this.type = type;
		this.date = date;
	}

	public FireExpense(int id, int amount, String type, String date) {
		this.id = id;
		this.amount = amount;
		this.type = type;
		this.date = date;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
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
