package ch.florianluescher.salary.bank;

public interface Bank {
    void doTransaction(String targetIBAN, int amount);
}
