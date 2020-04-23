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
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.energystar.context.EnergyStarCustomerContext;
import com.facilio.energystar.context.EnergyStarMeterContext;
import com.facilio.energystar.context.EnergyStarPropertyContext;
import com.facilio.energystar.context.EnergyStarProperyUseContext;
import com.facilio.services.FacilioHttpUtils;
import com.facilio.xml.builder.XMLBuilder;

public class EnergyStarSDK {
	
	
	public static String ENERGY_STAR_SANDBOX_ENDPOINT = "https://portfoliomanager.energystar.gov/wstest/";
	
	public static String ENERGY_STAR_FACILIO_USERNAME = "Krishnan123ek";
	public static String ENERGY_STAR_FACILIO_PASSWORD = "Facilio@123";
	
	public static String createCustomer(EnergyStarCustomerContext customerContext) throws Exception {
		
		Organization org = AccountUtil.getCurrentOrg();
		
		User user = AccountUtil.getCurrentUser();
		
		if(user.getPhone() == null) {
			throw new Exception("Phone number is mandatory to create energy star account");
		}
		
		XMLBuilder builder = XMLBuilder.create("account")
								.element("username").text(customerContext.getUserName()).p()
								.element("password").text(customerContext.getPassword()).p()
								.element("webserviceUser").text(Boolean.TRUE.toString()).p()
								.element("searchable").text(Boolean.FALSE.toString()).p()
								.element("contact")
									.element("firstName").text(user.getName()).p()
									.element("lastName").text(user.getName()).p()
									.element("email").text(user.getEmail()).p()
									.element("jobTitle").text("Building Administrator Data Exchange User").p()
									.element("phone").text(user.getPhone()).p()
									.element("address").a("address1", org.getStreet()).a("city", org.getCity()).a("state", org.getState()).a("postalCode", org.getZip()).a("country", org.getCountry()).p().p()
								.element("organization").a("name", org.getName())
									.element("primaryBusiness").text("Other").p()
									.element("otherBusinessDescription").text("other").p()
									.element("energyStarPartner").text(Boolean.FALSE.toString()).p().p()
								.element("emailPreferenceCanadianAccount").t(Boolean.FALSE.toString());
								;
								
		String xmlString = builder.getAsXMLString();	
		
		System.out.println(xmlString);
		String response = sendAPI("customer", HttpMethod.POST, xmlString);
		
		System.out.println(response);
		
		return XMLBuilder.parse(response).getElement("id").text();
	}
	
	public static String addProperty(EnergyStarCustomerContext customerContext,EnergyStarPropertyContext propertyContext) throws Exception {
		
		Organization org = AccountUtil.getCurrentOrg();
		
		User user = AccountUtil.getCurrentUser();
		
		BuildingContext building = propertyContext.getBuildingContext();
		
		if(user.getPhone() == null) {
			throw new Exception("Phone number is mandatory to create energy star account");
		}
		
		XMLBuilder builder = XMLBuilder.create("property")
								.element("name").text(building.getName()).p()
								.element("primaryFunction").text(propertyContext.getBuildingTypeEnum().getName()).p()
								.element("constructionStatus").text("Existing").p()
								.element("grossFloorArea").a("temporary", Boolean.FALSE.toString()).a("units", "Square Feet")
									.element("value").text((int)building.getGrossFloorArea()+"").p().p()
								.element("yearBuilt").text(propertyContext.getYearBuild()).p()
								.element("occupancyPercentage").text(propertyContext.getOccupancyPercentage()).p()
								.element("isFederalProperty").text(Boolean.FALSE.toString()).p()
								.element("address").a("address1", org.getStreet()).a("city", org.getCity()).a("state", org.getState()).a("postalCode", org.getZip()).a("country", org.getCountry())
								;
								
		String xmlString = builder.getAsXMLString();	
		
		System.out.println(xmlString);
		String response = sendAPI("account/"+customerContext.getEnergyStarCustomerId()+"/property", HttpMethod.POST, xmlString);
		
		System.out.println(response);
		
		return XMLBuilder.parse(response).getElement("id").text();
	}
	
	public static String addPropertyUse(EnergyStarCustomerContext customerContext,EnergyStarPropertyContext propertyContext) throws Exception {
		
		BuildingContext building = propertyContext.getBuildingContext();
		
		XMLBuilder builder = XMLBuilder.create(propertyContext.getBuildingTypeEnum().getLinkName())
								.element("name").text(building.getName()).p()
								.element("useDetails")
								;
		
		for(EnergyStarProperyUseContext propertyUse : propertyContext.getPropertyUseContexts()) {
			
			XMLBuilder element = builder.constructElement(propertyUse.getProperyUseTypeEnum().getFieldName()).a("temporary", Boolean.FALSE.toString());
			
			if(propertyUse.getProperyUseTypeEnum().getFieldName().equals("totalGrossFloorArea")) {
				element.a("units", "Square Feet");
			}
			element.element("value").text(propertyUse.getValue());
			
			builder.addElement(element);
		}
								
		String xmlString = builder.getAsXMLString();	
		
		System.out.println(xmlString);
		
		String response = sendAPI("property/"+propertyContext.getEnergyStarPropertyId()+"/propertyUse", HttpMethod.POST, xmlString);
		
		System.out.println(response);
		
		return XMLBuilder.parse(response).getElement("id").text();
	}
	
	public static String addPropertyMeter(EnergyStarCustomerContext customerContext,EnergyStarPropertyContext propertyContext,EnergyStarMeterContext meter) throws Exception {
		
		XMLBuilder builder = XMLBuilder.create("meter")
								.element("type").t(meter.getTypeEnum().getName()).p()
								.element("name").t(meter.getMeterContext().getName()).p()
								.element("unitOfMeasure").t(meter.getTypeEnum().getUnitOfMessure()).p()
								.element("metered").t(Boolean.TRUE.toString()).p()
								.element("inUse").t(Boolean.TRUE.toString()).p()
								.element("firstBillDate").t(meter.getFirstBillDate())
								;
		
		String xmlString = builder.getAsXMLString();	
		
		System.out.println(xmlString);
		String response = sendAPI("property/"+propertyContext.getEnergyStarPropertyId()+"/meter", HttpMethod.POST, xmlString);
		
		System.out.println(response);

		String id = XMLBuilder.parse(response).getElement("id").text();
		
		return id;
			
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
