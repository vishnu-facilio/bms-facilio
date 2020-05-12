package com.facilio.energystar.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.http.HttpHeaders;
import org.json.simple.JSONObject;

import com.amazonaws.HttpMethod;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.energystar.context.EnergyStarCustomerContext;
import com.facilio.energystar.context.EnergyStarMeterContext;
import com.facilio.energystar.context.EnergyStarMeterDataContext;
import com.facilio.energystar.context.EnergyStarPropertyContext;
import com.facilio.energystar.context.EnergyStarProperyUseContext;
import com.facilio.energystar.context.Meter_Category;
import com.facilio.energystar.context.Meter_Category_Points;
import com.facilio.services.FacilioHttpUtils;
import com.facilio.time.DateTimeUtil;
import com.facilio.xml.builder.XMLBuilder;

public class EnergyStarSDK {
	
	private static final Logger LOGGER = Logger.getLogger(EnergyStarSDK.class.getName());
	
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
		
		String resp = sendAPI("association/property/"+propertyContext.getEnergyStarPropertyId()+"/meter/"+id, HttpMethod.POST, null);
		
		System.out.println("meter associate response "+ resp);
		
		return id;
			
	}
	
	public static void addConsumptionData(EnergyStarMeterContext meter,List<EnergyStarMeterDataContext> datas,boolean isEstimated) throws Exception {
		
		XMLBuilder builder = XMLBuilder.create("meterData");
		
		Map<String,EnergyStarMeterDataContext> dataStartTimeMap = new HashMap<>();
		for(EnergyStarMeterDataContext data :datas) {
			
			dataStartTimeMap.put(data.getFormatedStartTime(), data);
			
			builder = builder.e("meterConsumption");
			if(isEstimated) {
				builder.a("estimatedValue", isEstimated+"");
			}
			builder = builder.element("startDate")
						.t(data.getFormatedStartTime())
						.p()
					.element("endDate")
						.t(data.getFormatedEndTime())
						.p()
						;
			
			List<Meter_Category_Points> points = Meter_Category_Points.getPointList(meter.getType());
			
			for(Meter_Category_Points point :points) {
				
				if(data.getDatum(point.getName()) != null) {
					builder = builder.element(point.getName())
								.t(data.getDatum(point.getName()).toString())
								.p();
				}
			}
			builder = builder.p();
		}
		
		String xmlString = builder.getAsXMLString();	
		
		System.out.println(xmlString);
		String response = sendAPI("meter/"+meter.getEnergyStarMeterId()+"/consumptionData", HttpMethod.POST, xmlString);
		
		System.out.println(response);

		List<XMLBuilder> respDataList = XMLBuilder.parse(response).getElementList("meterConsumption");
		
		for(XMLBuilder respData : respDataList) {
			EnergyStarMeterDataContext data = dataStartTimeMap.get(respData.getElement("startDate").text());
			data.setEnergyStarId(respData.getElement("id").text());
		}
	}
	
	public static String confirmAccountShare(String shareKey) throws Exception {
		
		String response = sendAPI("connect/account/pending/list", HttpMethod.GET, null);
		
		List<XMLBuilder> accounts = XMLBuilder.parse(response).getElementList("account");
		
		String id = null;
		for(XMLBuilder account :accounts) {
			String key = account.getElement("customFieldList").getElement("customField").getText();
			LOGGER.info("parsed key -- "+key);
			if(key.equals(shareKey)) {
				id = account.getElement("accountId").getText();
				sendConfirmAccountShare(id);
				break;
			}
		}
		
		return id;
	}
	
	public static List<EnergyStarPropertyContext> confirmPropertyShare(String accountId) throws Exception {
		
		String response = sendAPI("share/property/pending/list", HttpMethod.GET, null);
		
		List<XMLBuilder> properties = XMLBuilder.parse(response).getElementList("property");
		
		List<EnergyStarPropertyContext> propertyList = new ArrayList<EnergyStarPropertyContext>();
		for(XMLBuilder property :properties) {
			String localAccountID = property.getElement("accountId").getText();
			if(localAccountID.equals(accountId)) {
			
				String propertyId = property.getElement("propertyId").getText();
				
				sendConfirmPropertyShare(propertyId);
				
				String propertyName = property.getElement("propertyInfo").getElement("name").getText();
				
				JSONObject json = new JSONObject();
				
				json.put("name", propertyName);
				
				EnergyStarPropertyContext propertyContext = new EnergyStarPropertyContext();
				
				propertyContext.setMeta(json.toJSONString());
				
				propertyContext.setEnergyStarPropertyId(propertyId);
				
				propertyList.add(propertyContext);
			}
		}
		
		return propertyList;
	}
	
	public static List<EnergyStarMeterContext> confirmMeterShare(String accountId) throws Exception {
		
		String response = sendAPI("share/meter/pending/list", HttpMethod.GET, null);
		
		List<XMLBuilder> meters = XMLBuilder.parse(response).getElementList("meter");
		
		List<EnergyStarMeterContext> meterList = new ArrayList<EnergyStarMeterContext>();
		for(XMLBuilder meter :meters) {
			String localAccountID = meter.getElement("accountId").getText();
			if(localAccountID.equals(accountId)) {
				
				String meterId = meter.getElement("meterId").getText();
				
				sendConfirmMeterShare(meterId);
			
				String propertyId = meter.getElement("propertyId").getText();
				
				String meterName = meter.getElement("meterInfo").getElement("name").getText();
				String type = meter.getElement("meterInfo").getElement("type").getText();
				String firstBillDate = meter.getElement("meterInfo").getElement("firstBillDate").getText();
				
				
				JSONObject json = new JSONObject();
				
				json.put("name", meterName);
				
				EnergyStarMeterContext meterContext = new EnergyStarMeterContext();
				meterContext.setFirstBillDate(firstBillDate);
				meterContext.setType(Meter_Category.getAllTypes().get(type).getIntVal());
				
				meterContext.setMeta(json.toJSONString());
				meterContext.setEnergyStarMeterId(meterId);
				
				meterContext.setEnergyStarPropertyId(propertyId);
				
				meterList.add(meterContext);
			}
		}
		
		return meterList;
	}
	
	private static String sendConfirmAccountShare(String accountId) throws Exception {
		
		String payload = XMLBuilder.create("sharingResponse").element("action").text("Accept").getAsXMLString();
		
		String response = sendAPI("connect/account/"+accountId, HttpMethod.POST, payload);
		
		LOGGER.info("CONFIRM ACCOUNT RESP - "+response);
		
		return response;
	}
	
	private static String sendConfirmPropertyShare(String propertyId) throws Exception {
		
		String payload = XMLBuilder.create("sharingResponse").element("action").text("Accept").getAsXMLString();
		
		String response = sendAPI("share/property/"+propertyId, HttpMethod.POST, payload);
		
		LOGGER.info("CONFIRM PROPERTY RESP - "+response);
		
		return response;
	}
	
	private static String sendConfirmMeterShare(String meterId) throws Exception {
		
		String payload = XMLBuilder.create("sharingResponse").element("action").text("Accept").getAsXMLString();
		
		String response = sendAPI("share/meter/"+meterId, HttpMethod.POST, payload);
		
		LOGGER.info("CONFIRM METER RESP - "+response);
		
		return response;
	}
	
	public static String fetchMetrics(EnergyStarPropertyContext property,String year,String month) throws Exception {
		
		String response = sendAPI("/property/"+property.getEnergyStarPropertyId()+"/metrics?year="+year+"&month="+month+"&measurementSystem=EPA", HttpMethod.GET, null);
		
		System.out.println(response);

		return response;
	}
	
	
	private static String sendAPI(String action,HttpMethod method, String payload) throws IOException {
		
		Map<String,String> headers = new HashMap<>();
		
		headers.put(HttpHeaders.AUTHORIZATION, getAuthentication());
		
		switch (method) {
			case POST :
				headers.put(HttpHeaders.CONTENT_TYPE, "application/xml");
				return FacilioHttpUtils.doHttpPost(ENERGY_STAR_SANDBOX_ENDPOINT+action, headers, null, payload);
			case GET:
				return FacilioHttpUtils.doHttpGet(ENERGY_STAR_SANDBOX_ENDPOINT+action, headers, null);
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
