package entity;

public class Address {
	private String street;
	private String city;
	private String state;
	private String zipcode;
	
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
	
	private Address(AddressBuilder builder) {
		this.street = builder.street;
		this.city = builder.city;
		this.state = builder.state;
		this.zipcode = builder.zipcode;
	}
	
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
