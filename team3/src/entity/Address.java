package entity;

import org.json.JSONException;
import org.json.JSONObject;

public class Address {
    private String street;
    private String city;
    private String state;
    private String zipcode;

    public Address(JSONObject address) {

    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }


    public static Address parse(JSONObject obj) throws JSONException {
        return new AddressBuilder()
                .setStreet(obj.getString("street"))
                .setCity(obj.getString("city"))
                .setState(obj.getString("state"))
                .setZipcode(obj.getString("zipcode"))
                .build();
    }

    private Address(AddressBuilder builder) {
        this.street = builder.street;
        this.city = builder.city;
        this.state = builder.state;
        this.zipcode = builder.zipcode;
    }

    public static class AddressBuilder {
        private String street;
        private String city;
        private String state;
        private String zipcode;

        public AddressBuilder setStreet(String street) {
            this.street = street;
            return this;
        }

        public AddressBuilder setCity(String city) {
            this.city = city;
            return this;
        }

        public AddressBuilder setState(String state) {
            this.state = state;
            return this;
        }

        public AddressBuilder setZipcode(String zipcode) {
            this.zipcode = zipcode;
            return this;
        }

        public Address build() {
            return new Address(this);
        }
    }

    @Override
    public String toString() {
        return "Street: " + street + ", City: " + city + ", State: " + state + ", Zipcode: " + zipcode;
    }


    public String encode() {
        String code = "";
        if (street != null && !street.isEmpty()) {
            code += street.trim().replaceAll("\\s+", "");
        }
        code += ",";

        if (city != null && !city.isEmpty()) {
            code += city.trim().replaceAll("\\s+", "+");
        }
        code += ",";

        if (state != null && !state.isEmpty()) {
//            code += state.trim().replaceAll("\\s+", "+");
            code += state;
        }

        return code;
    }

//    public static void main(String[] args) {
//        Address address = new AddressBuilder().setStreet("e  \t t").setCity("ho").setState("dd").build();
//        System.out.println(address.encode());
//    }
}
