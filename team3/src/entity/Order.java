package entity;


import java.util.Calendar;
import java.util.UUID;

import org.json.JSONObject;


public class Order {
	public static final String STATUS_TEMPORARY = "temporary";
    public static final String STATUS_DELIVERED = "Delivered";
    public static final String STATUS_ONWAY = "On the way";
    public static final String STATUS_INITIAL = "Initialized";

    private String orderId;
//    private Integer pickPackageTime; //unit: minute
//    private Integer sendPackageTime; //unit: minute
    private String senderEmail;
    private String receiverEmail;
    private Address fromAddress;
    private Address toAddress;
    private Calendar appointmentTime;
    
    
    private Order(OrderBuilder builder) {
    	this.orderId = builder.orderId == null ? Order.randOrderId().toString() : builder.orderId;
    	this.senderEmail = builder.senderEmail;
    	this.receiverEmail = builder.receiverEmail;
    	this.fromAddress = builder.fromAddress;
    	this.toAddress = builder.toAddress;
    	this.appointmentTime = builder.appointmentTime;
    }
    
    
    public String getOrderId() {
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
    
    public Calendar getAppointmentTime() {
        return appointmentTime;
    }
    
    public void setAppointmentTime(Calendar appointmentTime) {
    	this.appointmentTime = appointmentTime;
    			
    }

    // TODO: apply this method to generate random order id for the newly fake order
    // e.g.
    //      this.setOrderId(randOrderId().toString());
    private static UUID randOrderId() {
        long currentTimeMillis = System.currentTimeMillis();
        return new UUID(currentTimeMillis, 1L + (long) (Math.random() * (10000L - 1L)));
    }
    
    public static class OrderBuilder {
    	private String orderId;
        private String senderEmail;
        private String receiverEmail;
        private Address fromAddress;
        private Address toAddress;
        private Calendar appointmentTime;
        
        public OrderBuilder setOrderId(String orderId) {
        	this.orderId = orderId;
        	return this;
        }
        
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
        public OrderBuilder setToAppointmentTime(Calendar appointmentTime) {
        	this.appointmentTime = appointmentTime;
        	return this;
        }
        public Order build() {
        	return new Order(this);
        }
    }
}
//    public void fakeOrder(Address fromAddress, Address toAddress) {
//        this.setOrderId(randOrderId().toString());
//        this.setFromAddress(fromAddress);
//        this.setToAddress(toAddress);
//    }

