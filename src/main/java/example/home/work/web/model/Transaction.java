package example.home.work.web.model;

import java.util.Objects;

public class Transaction {

    private String userId;
    private int amount;

    public Transaction(String userId, int amount) {
        this.userId = userId;
        this.amount = amount;
    }

    public Transaction() {
    }

    public String getUserId() {
        return userId;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return amount == that.amount && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, amount);
    }

}