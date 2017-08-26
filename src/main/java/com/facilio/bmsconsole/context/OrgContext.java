package com.facilio.bmsconsole.context;

import java.io.File;

import com.facilio.bmsconsole.util.OrgApi;

public class OrgContext{
	private String name;
	private long orgId;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	private long logoId;
	
	public long getLogoId() {
		return logoId;
	}
	public void setLogoId(long logoId) {
		this.logoId = logoId;
	}
	
	private long phone;
	private long mobile;
	private long timezone;
	private long zipcode;
	private long dateformat;
	
	private String street;
	private String city;
	private String state;
	private String language;
	private String fax;
	private String country;
	public long getPhone() {
		return phone;
	}
	public void setPhone(long phone) {
		this.phone = phone;
	}
	public long getMobile() {
		return mobile;
	}
	public void setMobile(long mobile) {
		this.mobile = mobile;
	}
	public long getTimezone() {
		return timezone;
	}
	public void setTimezone(long timezone) {
		this.timezone = timezone;
	}
	public long getZipcode() {
		return zipcode;
	}
	public void setZipcode(long zipcode) {
		this.zipcode = zipcode;
	}
	public long getDateformat() {
		return dateformat;
	}
	public void setDateformat(long dateformat) {
		this.dateformat = dateformat;
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
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	
	
	
}