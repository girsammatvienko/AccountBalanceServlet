package example.home.work.web.model;

import java.util.Objects;

public class AccountDetails {

    private String userId;
    private int balance;

    public AccountDetails(String userId, int balance) {
        this.userId = userId;
        this.balance = balance;
    }

    public AccountDetails() {
    }

    public String getUserId() {
        return userId;
    }

    public int getBalance() {
        return balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountDetails that = (AccountDetails) o;
        return balance == that.balance && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, balance);
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
}