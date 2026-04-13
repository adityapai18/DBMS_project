package model;

import java.sql.Date;

/** Distributor-side payment ({@code DISTRIBUTOR_PAYMENT}). */
public class Payment {
    private int paymentId;
    private int distributorId;
    private double amount;
    private Date paymentDate;

    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }
    public int getDistributorId() { return distributorId; }
    public void setDistributorId(int distributorId) { this.distributorId = distributorId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public Date getPaymentDate() { return paymentDate; }
    public void setPaymentDate(Date paymentDate) { this.paymentDate = paymentDate; }
}
