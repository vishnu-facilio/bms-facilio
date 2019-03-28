package com.facilio.accounts.dto;

import java.io.Serializable;

public class Organization implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long orgId;
	private String name;
	private String domain;
	private long logoId;
	private String phone;
	private String mobile;
	private String fax;
	private String street;
	private String city;
	private String state;
	private String zip;
	private String country;
	private String timezone;
	private String currency;
	private long createdTime;
	private long portalId;
	private String customDomain;
	
	
	
	public String getCustomDomain() {
		return customDomain;
	}
	public void setCustomDomain(String customDomain) {
		this.customDomain = customDomain;
	}
	public long getOrgId() {
		return orgId;
	}
	public long getId() {
		return orgId;
	}
	public void setId(long id) {
		this.orgId = id;
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
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public long getLogoId() {
		return logoId;
	}
	public void setLogoId(long logoId) {
		this.logoId = logoId;
	}
	
	private String logoUrl;
	public String getLogoUrl() {
		return logoUrl;
	}
	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}
	
	private String originalUrl;
	public String getOriginalUrl() {
		return originalUrl;
	}
	public void setOriginalUrl(String originalUrl) {
		this.originalUrl = originalUrl;
	}
	
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
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
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getTimezone() {
		return timezone;
	}
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}
	public long getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}

	public long getPortalId() {
		return portalId;
	}

	public void setPortalId(long portalId) {
		this.portalId = portalId;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuilder orgInfo = new StringBuilder();
		orgInfo.append("ORG ID : ").append(orgId)
				.append("\nName : ").append(name)
				.append("\nDomain : ").append(domain)
				.append("\nLogo ID : ").append(logoId)
				.append("\nPhone : ").append(phone)
				.append("\nMobile : ").append(mobile)
				.append("\nFax : ").append(fax)
				.append("\nStreet : ").append(street)
				.append("\nCity : ").append(city)
				.append("\nState : ").append(state)
				.append("\nZip : ").append(zip)
				.append("\nCountry : ").append(country)
				.append("\ntimezone : ").append(timezone)
				.append("\nCreated Time : ").append(createdTime)
				.append("\nPortal ID : ").append(portalId);
		return orgInfo.toString();
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
}
