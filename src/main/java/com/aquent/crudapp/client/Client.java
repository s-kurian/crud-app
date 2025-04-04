package com.aquent.crudapp.client;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.aquent.crudapp.person.Person;

/**
 * The client entity corresponding to the "client" table in the database.
 */
public class Client {

    private Integer clientId;

    @NotNull
    @Size(min = 1, max = 100, message = "Client name is required with maximum length of 50")
    private String name;

    @NotNull
    @Size(min = 1, max = 400, message = "Website URL is required with maximum length of 400")
    private String websiteUrl;

    @NotNull
    @Size(min = 1, max = 50, message = "Phone number is required with maximum length of 10")
    private String phoneNumber;
    
    @NotNull
    @Size(min = 1, max = 50, message = "Street address is required with maximum length of 50")
    private String streetAddress;

    @NotNull
    @Size(min = 1, max = 50, message = "City is required with maximum length of 50")
    private String city;

    @NotNull
    @Size(min = 2, max = 2, message = "State is required with length 2")
    private String state;

    @NotNull
    @Size(min = 5, max = 5, message = "Zip code is required with length 5")
    private String zipCode;
    
    private List<Person> contacts = new ArrayList<Person>();

	public Integer getClientId() {
		return clientId;
	}

	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getWebsiteUrl() {
		return websiteUrl;
	}

	public void setWebsiteUrl(String websiteUrl) {
		this.websiteUrl = websiteUrl;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
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

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public List<Person> getContacts() {
		return contacts;
	}

	public void setContacts(List<Person> contacts) {
		this.contacts = contacts;
	}

	@Override
	public String toString() {
		return "Client [clientId=" + clientId + ", name=" + name + ", websiteUrl=" + websiteUrl + ", phoneNumber="
				+ phoneNumber + ", streetAddress=" + streetAddress + ", city=" + city + ", state=" + state
				+ ", zipCode=" + zipCode + ", persons=" + contacts + "]";
	}


}
