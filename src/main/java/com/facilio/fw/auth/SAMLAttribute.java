//$Id$
package com.facilio.fw.auth;

import org.json.simple.JSONObject;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

public class SAMLAttribute {

	private String issuer;
	private String intendedAudience;
	private String recipient;
	private String inResponseTo;
	private String email;
	private JSONObject customAttr;
	
	private PrivateKey privateKey;
	private X509Certificate x509Certificate;
	
	public SAMLAttribute() throws Exception
	{
		privateKey = SAMLUtil.loadPrivateKey();
		x509Certificate = SAMLUtil.loadCert();
	}
	
	public String getIssuer() {
		return issuer;
	}
	
	public SAMLAttribute setIssuer(String issuer) {
		this.issuer = issuer;
		return this;
	}
	
	public String getIntendedAudience() {
		return intendedAudience;
	}
	
	public SAMLAttribute setIntendedAudience(String intendedAudience) {
		this.intendedAudience = intendedAudience;
		return this;
	}
	
	public String getRecipient() {
		return recipient;
	}
	
	public SAMLAttribute setRecipient(String recipient) {
		this.recipient = recipient;
		return this;
	}

	public String getInResponseTo() {
		return inResponseTo;
	}
	
	public SAMLAttribute setInResponseTo(String inResponseTo) {
		this.inResponseTo = inResponseTo;
		return this;
	}
	
	public String getEmail() {
		return email;
	}
	
	public SAMLAttribute setEmail(String email) {
		this.email = email;
		return this;
	}
	
	public PrivateKey getPrivateKey() {
		return privateKey;
	}
	
	public SAMLAttribute setPrivateKey(PrivateKey privateKey) {
		this.privateKey = privateKey;
		return this;
	}
	
	public X509Certificate getX509Certificate() {
		return x509Certificate;
	}

	public SAMLAttribute setX509Certificate(X509Certificate x509Certificate) {
		this.x509Certificate = x509Certificate;
		return this;
	}
	
	public SAMLAttribute setCustomAttr(JSONObject attr) {
		this.customAttr = attr;
		return this;
	}
	
	public JSONObject getCustomAttr() {
		return this.customAttr;
	}
}

