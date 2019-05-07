package entity;


import java.util.UUID;

import org.json.JSONObject;

import jnr.ffi.Struct.int16_t;

public class Order {
    public static final String STATUS_DELIVERED = "Delivered";
    public static final String STATUS_ONWAY = "On the way";
    public static final String STATUS_INITIAL = "Initialized";

    private UUID orderId;
//    private Integer pickPackageTime; //unit: minute
//    private Integer sendPackageTime; //unit: minute
    private String senderEmail;
    private String receiverEmail;
    private Address fromAddress;
    private Address toAddress;
    
    
    private Order(OrderBuilder builder) {
    	this.orderId = Order.randOrderId();
    	this.senderEmail = builder.senderEmail;
    	this.receiverEmail = builder.receiverEmail;
    	this.fromAddress = builder.fromAddress;
    	this.toAddress = builder.toAddress;
    }
    
    
    public UUID getOrderId() {
        return orderId;
    }


//    public Integer getPickPackageTime() {
//        return pickPackageTime;
//    }
//
//    public void setPickPackageTime(Integer pickPackageTime) {
//        this.pickPackageTime = pickPackageTime;
//    }
//
//    public Integer getSendPackageTime() {
//        return sendPackageTime;
//    }
//
//    public void setSendPackageTime(Integer sendPackageTime) {
//        this.sendPackageTime = sendPackageTime;
//    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public Address getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(Address fromAddress) {
        this.fromAddress = fromAddress;
    }

    public Address getToAddress() {
        return toAddress;
    }

    public void setToAddress(Address toAddress) {
        this.toAddress = toAddress;
    }

    // TODO: apply this method to generate random order id for the newly fake order
    // e.g.
    //      this.setOrderId(randOrderId().toString());
    private static UUID randOrderId() {
        long currentTimeMillis = System.currentTimeMillis();
        return new UUID(currentTimeMillis, 1L + (long) (Math.random() * (10000L - 1L)));
    }
    
    public static class OrderBuilder {
        private String senderEmail;
        private String receiverEmail;
        private Address fromAddress;
        private Address toAddress;
        
        public OrderBuilder setSenderEmail(String senderEmail) {
        	this.senderEmail = senderEmail;
        	return this;
        }
        public OrderBuilder setReceiverEmail(String receiverEmail) {
        	this.receiverEmail = receiverEmail;
        	return this;
        }
        public OrderBuilder setFromAddress(Address fromAddress) {
        	this.fromAddress = fromAddress;
        	return this;
        }
        public OrderBuilder setToAddress(Address toAddress) {
        	this.toAddress = toAddress;
        	return this;
        }
        public Order build() {
        	return new Order(this);
        }
    }
}
