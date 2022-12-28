package com.facilio.workflows.functions;

import java.util.ArrayList;
import com.facilio.scriptengine.systemfunctions.FacilioSystemFunctionNameSpace;
import com.facilio.scriptengine.systemfunctions.FacilioWorkflowFunctionInterface;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.BusinessHourContext;
import com.facilio.bmsconsole.context.BusinessHoursContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.BusinessHoursAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.controlaction.context.ControlActionCommandContext;
import com.facilio.controlaction.util.ControlActionUtil;
import com.facilio.time.DateTimeUtil;

public enum FacilioBusinessHourFunctions implements FacilioWorkflowFunctionInterface  {

	SET_VALUE(1,"get") {

		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			// TODO Auto-generated method stub
			
			Object businessObjectRef = objects[0];
			
			if(businessObjectRef instanceof Map) {
				Map<String,List<List<String>>> businessHourMap = (Map<String, List<List<String>>>) objects[0];
				
				BusinessHoursContext bussBusinessHoursContext = new BusinessHoursContext();
				
				List<BusinessHourContext> bussHours = new ArrayList<BusinessHourContext>();
				
				for(String dayOfWeek : businessHourMap.keySet()) {
					
					List<List<String>> timeList = businessHourMap.get(dayOfWeek);
					
					for(List<String> times : timeList) {
						
						BusinessHourContext singleDayBusHour = new BusinessHourContext();
						
						singleDayBusHour.setDayOfWeek(Integer.parseInt(dayOfWeek));
						
						singleDayBusHour.setStartTime(times.get(0));
						singleDayBusHour.setEndTime(times.get(1));
						
						bussHours.add(singleDayBusHour);
					}
					
				}
				
				bussBusinessHoursContext.setSingleDaybusinessHoursList(bussHours);
				
				return bussBusinessHoursContext;
			}
			else {
				return BusinessHoursAPI.getBusinessHours(Collections.singletonList((long) Double.parseDouble(objects[0].toString()))).get(0);
			}
		}
		
	},
	
	ADD_HOURS(2,"addHours") {

		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			// TODO Auto-generated method stub
			
			BusinessHoursContext bussBusinessHoursContext = (BusinessHoursContext)objects[0];
			int hourInt = (int) Double.parseDouble(objects[1].toString());
			long fromTime = DateTimeUtil.getCurrenTime();
			if(objects.length > 2) {
				fromTime = (long) Double.parseDouble(objects[2].toString());
			}
			return BusinessHoursAPI.addSecondsToBusinessHours(bussBusinessHoursContext, fromTime, hourInt*60*60);
		}
		
	},
	
	ADD_MINS(3,"addMins") {

		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			// TODO Auto-generated method stub
			
			BusinessHoursContext bussBusinessHoursContext = (BusinessHoursContext)objects[0];
			int minInt = (int) Double.parseDouble(objects[1].toString());
			long fromTime = DateTimeUtil.getCurrenTime();
			if(objects.length > 2) {
				fromTime = (long) Double.parseDouble(objects[2].toString());
			}
			return BusinessHoursAPI.addSecondsToBusinessHours(bussBusinessHoursContext, fromTime, minInt*60);
		}
		
	},
	
	GET_HOURS_BETWEEN(2,"getHoursBetween") {

		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			// TODO Auto-generated method stub
			
			BusinessHoursContext bussBusinessHoursContext = (BusinessHoursContext)objects[0];
			long fromTime = (long) Double.parseDouble(objects[1].toString());
			long toTime = (long) Double.parseDouble(objects[2].toString());
			return BusinessHoursAPI.getSecondsBetween(bussBusinessHoursContext, fromTime, toTime)/60/60;
		}
		
	},
	
	GET_MINS_BETWEEN(3,"getMinsBetween") {

		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			// TODO Auto-generated method stub
			
			BusinessHoursContext bussBusinessHoursContext = (BusinessHoursContext)objects[0];
			long fromTime = (long) Double.parseDouble(objects[1].toString());
			long toTime = (long) Double.parseDouble(objects[2].toString());
			return BusinessHoursAPI.getSecondsBetween(bussBusinessHoursContext, fromTime, toTime)/60;
		}
		
	},
	IS_TIME_BETWEEN(4,"isTimeBetween"){
		@Override
		public Object execute(Map<String, Object> map, Object... objects) throws Exception {
			BusinessHoursContext bussBusinessHoursContext = (BusinessHoursContext)objects[0];
			long time=(long) Double.parseDouble(objects[1].toString());
			return BusinessHoursAPI.isTimeBetweenBusinessHours(bussBusinessHoursContext,time);
		}
	},
	;
	
	private Integer value;
	private String functionName;
	private String namespace = "businessHours";
	private String params;
	private FacilioSystemFunctionNameSpace nameSpaceEnum = FacilioSystemFunctionNameSpace.BUSINESS_HOUR;
	
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}
	public String getFunctionName() {
		return functionName;
	}
	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	}
	FacilioBusinessHourFunctions(Integer value,String functionName) {
		this.value = value;
		this.functionName = functionName;
	}
	public static Map<String, FacilioBusinessHourFunctions> getAllFunctions() {
		return CONTROL_FUNCTIONS;
	}
	public static FacilioBusinessHourFunctions getFacilioBusinessHourFunctions(String functionName) {
		return CONTROL_FUNCTIONS.get(functionName);
	}
	static final Map<String, FacilioBusinessHourFunctions> CONTROL_FUNCTIONS = Collections.unmodifiableMap(initTypeMap());
	static Map<String, FacilioBusinessHourFunctions> initTypeMap() {
		Map<String, FacilioBusinessHourFunctions> typeMap = new HashMap<>();
		for(FacilioBusinessHourFunctions type : FacilioBusinessHourFunctions.values()) {
			typeMap.put(type.getFunctionName(), type);
		}
		return typeMap;
	}
}