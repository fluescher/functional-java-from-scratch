package ch.florianluescher.salary;

public class Salary {
    private final int amount;
    private final String transferredToIBAN;

    public Salary(String transferredToIBAN, int amount) {
        this.transferredToIBAN = transferredToIBAN;
        this.amount = amount;
    }

    public String getTransferredToIBAN() {
        return transferredToIBAN;
    }

    public int getAmount() {
        return amount;
    }
}
