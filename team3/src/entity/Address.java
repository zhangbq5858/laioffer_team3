package entity;

<<<<<<< HEAD
=======
import org.json.JSONObject;

>>>>>>> ef89b10b3436908de9ade7cc0f1417865186f5a0
public class Address {
	private String street;
	private String city;
	private String state;
	private String zipcode;
	
<<<<<<< HEAD
=======
	public Address(JSONObject address) {
		
	}
	
>>>>>>> ef89b10b3436908de9ade7cc0f1417865186f5a0
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
	
<<<<<<< HEAD
=======
	public static Address parse(JSONObject obj) {
		return new AddressBuilder()
				.setStreet(obj.getString("street"))
				.setCity(obj.getString("city"))
				.setState(obj.getString("state"))
				.setZipcode(obj.getString("zipcode"))
				.build();
	}
	
>>>>>>> ef89b10b3436908de9ade7cc0f1417865186f5a0
	private Address(AddressBuilder builder) {
		this.street = builder.street;
		this.city = builder.city;
		this.state = builder.state;
		this.zipcode = builder.zipcode;
	}
<<<<<<< HEAD
	
=======

>>>>>>> ef89b10b3436908de9ade7cc0f1417865186f5a0
	public static class AddressBuilder{
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
	
}
