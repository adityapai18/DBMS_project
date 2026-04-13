package model;

import java.sql.Date;

public class Order {
    private int orderId;
    private int distributorId;
    private Integer editionId;
    private Integer issueId;
    private int quantity;
    private double price;
    private double shippingCost;
    private Date requiredDate;
    private Double billedAmount;

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public int getDistributorId() { return distributorId; }
    public void setDistributorId(int distributorId) { this.distributorId = distributorId; }
    public Integer getEditionId() { return editionId; }
    public void setEditionId(Integer editionId) { this.editionId = editionId; }
    public Integer getIssueId() { return issueId; }
    public void setIssueId(Integer issueId) { this.issueId = issueId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public double getShippingCost() { return shippingCost; }
    public void setShippingCost(double shippingCost) { this.shippingCost = shippingCost; }
    public Date getRequiredDate() { return requiredDate; }
    public void setRequiredDate(Date requiredDate) { this.requiredDate = requiredDate; }
    public Double getBilledAmount() { return billedAmount; }
    public void setBilledAmount(Double billedAmount) { this.billedAmount = billedAmount; }
}
