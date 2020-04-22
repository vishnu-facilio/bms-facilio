package com.facilio.energystar.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpHeaders;

import com.amazonaws.HttpMethod;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.energystar.context.EnergyStarCustomerContext;
import com.facilio.services.FacilioHttpUtils;
import com.facilio.xml.builder.XMLBuilder;

public class EnergyStarSDK {
	
	
	public static String ENERGY_STAR_SANDBOX_ENDPOINT = "https://portfoliomanager.energystar.gov/wstest/";
	
	public static String ENERGY_STAR_FACILIO_USERNAME = "Krishnan123ek";
	public static String ENERGY_STAR_FACILIO_PASSWORD = "Facilio@123";
	
	public static String createCustomer(EnergyStarCustomerContext customerContext) throws Exception {
		
		Organization org = AccountUtil.getCurrentOrg();
		
		User user = AccountUtil.getCurrentUser();
		
		if(user.getPhone() != null) {
			throw new Exception("Phone number is mandatory to create energy star account");
		}
		
		XMLBuilder builder = XMLBuilder.create("account")
								.element("username").text(customerContext.getUserName())
								.element("password").text(customerContext.getPassword())
								.element("webserviceUser").text(Boolean.TRUE.toString())
								.element("searchable").text(Boolean.FALSE.toString())
								.element("contact")
									.element("firstName").text(user.getName())
									.element("lastName").text(user.getName())
									.element("email").text(user.getEmail())
									.element("jobTitle").text("Building Administrator Data Exchange User")
									.element("phone").text(user.getPhone())
									.element("address").a("address1", org.getStreet()).a("city", org.getCity()).a("state", org.getState()).a("postalCode", org.getState()).a("country", org.getCountry())
								.element("organization").a("name", org.getName())
									.element("primaryBusiness").text("Other")
									.element("otherBusinessDescription").text("other")
									.element("energyStarPartner").text(Boolean.FALSE.toString())
								.element("emailPreferenceCanadianAccount").t(Boolean.FALSE.toString());
								;
								
		String xmlString = builder.getAsXMLString();	
		
		String response = sendAPI("customer", HttpMethod.POST, xmlString);
		
		return XMLBuilder.parse(response).getElement("id").text();
	}
	
	private static String sendAPI(String action,HttpMethod method, String payload) throws IOException {
		
		Map<String,String> headers = new HashMap<>();
		
		headers.put(HttpHeaders.AUTHORIZATION, getAuthentication());
		headers.put(HttpHeaders.CONTENT_TYPE, "application/xml");
		
		switch (method) {
			case POST :
					return FacilioHttpUtils.doHttpPost(ENERGY_STAR_SANDBOX_ENDPOINT+action, headers, null, payload);
			case GET:
				break;
			case PUT:
				break;
			
		}
		return null;
	}
	
	private static String getAuthentication() {
		String auth = ENERGY_STAR_FACILIO_USERNAME + ":" + ENERGY_STAR_FACILIO_PASSWORD;
		byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.ISO_8859_1));
		String authHeader = "Basic " + new String(encodedAuth);
		
		return authHeader;
	}

}
