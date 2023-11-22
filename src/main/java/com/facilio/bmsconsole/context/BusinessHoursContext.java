package com.facilio.bmsconsole.context;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;

import com.facilio.bmsconsole.util.BusinessHoursAPI;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BusinessHoursContext implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private int businessHourTypeId = -1;

	public void setBusinessHourTypeId(int businessHourTypeId) {
		this.businessHourTypeId = businessHourTypeId;
	}

	public int getBusinessHourTypeId() {
		if (businessHourType != null) {
			return businessHourType.getValue();
		} else {
			return businessHourTypeId;
		}
	}

	private BusinessHourType businessHourType;

	public void setBusinessHourType(BusinessHourType businessHourType) {
		this.businessHourType = businessHourType;
	}

	public void setBusinessHourType() {
		this.businessHourType = BusinessHourType.valueOf(businessHourTypeId);
	}

	public String getBusinessHourTypeVal() {
		if (businessHourType != null) {
			return businessHourType.toString();
		}else if(businessHourTypeId!=-1){
			return BusinessHourType.valueOf(businessHourTypeId).toString();
		}
		return null;
	}

	public BusinessHourType getBusinessHourTypeEnum() {
		if (businessHourType != null) {
			return businessHourType;
		}else if(businessHourTypeId!=-1){
			return BusinessHourType.valueOf(businessHourTypeId);
		} 
		return null;
	}
	private int customHourTypeId = -1;

	public void setCustomHourTypeId(int customHourTypeId) {
		this.customHourTypeId = customHourTypeId;
	}

	public int getCustomHourTypeId() {
		if (customHourType != null) {
			return customHourType.getValue();
		} else {
			return customHourTypeId;
		}
	}
	private CustomHourType customHourType;

	public void setCustomHourType(CustomHourType customHourType) {
		this.customHourType = customHourType;
	}

	public void setCustomHourType() {
		this.customHourType = CustomHourType.valueOf(customHourTypeId);
	}


	public String getCustomHourTypeVal() {
		if (customHourType != null) {
			return customHourType.toString();
		}else if(customHourTypeId!=-1){
			return CustomHourType.valueOf(customHourTypeId).toString();
		}
		return null;
		
	}

	public CustomHourType getCustomHourTypeEnum() {
		if (customHourType != null) {
			return customHourType;
		}else if(customHourTypeId!=-1){
			return CustomHourType.valueOf(customHourTypeId);
		} 
		return null;
	}

	private long orgId = -1;

	public long getOrgId() {
		return orgId;
	}

	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}

	private long id = -1;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	private List<BusinessHourContext> singleDaybusinessHoursList;

	public List<BusinessHourContext> getSingleDaybusinessHoursList() {
		return singleDaybusinessHoursList;
	}
	
	public List<BusinessHourContext> getSingleDaybusinessHoursListOrDefault() {
		if(singleDaybusinessHoursList == null || singleDaybusinessHoursList.isEmpty()) {
			return BusinessHoursAPI.fetchDefault24_7Days();
		}
		return singleDaybusinessHoursList;
	}

	public void setSingleDaybusinessHoursList(List<BusinessHourContext> singleDaybusinessHoursList) {
		this.singleDaybusinessHoursList = singleDaybusinessHoursList;
	}
	
	@JsonIgnore 
	public Map<DayOfWeek, List<Pair<LocalTime, LocalTime>>> getAsMapBusinessHours() {
		
		Map<DayOfWeek,List<Pair<LocalTime,LocalTime>>> businessHourMap = new HashMap<>();
		
		for(BusinessHourContext singleDaybusinessHour :getSingleDaybusinessHoursListOrDefault()) {
			
			DayOfWeek dayOfWeek = singleDaybusinessHour.getDayOfWeekEnum();
			
			List<Pair<LocalTime, LocalTime>> timeList = businessHourMap.getOrDefault(dayOfWeek, new ArrayList<Pair<LocalTime,LocalTime>>());
			
			Pair<LocalTime, LocalTime> timePair = Pair.of(singleDaybusinessHour.getStartTimeOrDefault(), singleDaybusinessHour.getEndTimeOrDefault());
			
			timeList.add(timePair);
			
			businessHourMap.put(dayOfWeek, timeList);
		}
		
		for(DayOfWeek dayOfWeek : businessHourMap.keySet()) {
			
			Collections.sort(businessHourMap.get(dayOfWeek));
		}
		
		return businessHourMap;
 	}

	public static enum BusinessHourType {
		DAYS_24_7, 
		DAYS_24_5, 
		CUSTOM;

		public int getValue() {
			return ordinal() + 1;
		}

		public static BusinessHourType valueOf(int val) {
			if (val > 0 && val <= values().length) {
				return values()[val - 1];
			}
			return null;
		}
	}

	public static enum CustomHourType {
		SAME_TIMING_ALLDAY, 
		DIFFERENT_TIMING_ALLDAY;
		public int getValue() {
			return ordinal() + 1;
		}

		public static CustomHourType valueOf(int val) {
			if (val > 0 && val <= values().length) {
				return values()[val - 1];
			}
			return null;
		}
	}

	public static JSONObject getAsJsonObject(BusinessHoursContext value)throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		String jsonInString = mapper.writeValueAsString(value);
		return new JSONObject(jsonInString);
	}
	@Override
	public String toString() {
		return "BusinessHoursContext [name=" + name + ", businessHourTypeId=" + businessHourTypeId
				+ ", businessHourType=" + businessHourType + ", customHourTypeId=" + customHourTypeId
				+ ", customHourType=" + customHourType + ", orgId=" + orgId + ", id=" + id
				+ ", singleDaybusinessHoursList=" + singleDaybusinessHoursList + "]";
	}

}
