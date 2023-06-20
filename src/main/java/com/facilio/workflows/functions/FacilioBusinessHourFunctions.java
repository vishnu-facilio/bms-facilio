package com.facilio.workflows.functions;

import com.facilio.bmsconsole.context.BusinessHourContext;
import com.facilio.bmsconsole.context.BusinessHoursContext;
import com.facilio.bmsconsole.util.BusinessHoursAPI;
import com.facilio.scriptengine.annotation.ScriptNameSpace;
import com.facilio.scriptengine.context.ScriptContext;
import com.facilio.scriptengine.systemfunctions.FacilioNameSpaceConstants;
import com.facilio.time.DateTimeUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@ScriptNameSpace(nameSpace = FacilioNameSpaceConstants.BUSINESS_HOUR_FUNCTION)
public class FacilioBusinessHourFunctions {
	public Object get(ScriptContext scriptContext,  Map<String, Object> globalParam, Object... objects) throws Exception {
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

	public Object addHours(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {
		// TODO Auto-generated method stub

		BusinessHoursContext bussBusinessHoursContext = (BusinessHoursContext)objects[0];
		int hourInt = (int) Double.parseDouble(objects[1].toString());
		long fromTime = DateTimeUtil.getCurrenTime();
		if(objects.length > 2) {
			fromTime = (long) Double.parseDouble(objects[2].toString());
		}
		return BusinessHoursAPI.addSecondsToBusinessHours(bussBusinessHoursContext, fromTime, hourInt*60*60);
	}

	public Object addMins(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {
		// TODO Auto-generated method stub

		BusinessHoursContext bussBusinessHoursContext = (BusinessHoursContext)objects[0];
		int minInt = (int) Double.parseDouble(objects[1].toString());
		long fromTime = DateTimeUtil.getCurrenTime();
		if(objects.length > 2) {
			fromTime = (long) Double.parseDouble(objects[2].toString());
		}
		return BusinessHoursAPI.addSecondsToBusinessHours(bussBusinessHoursContext, fromTime, minInt*60);
	}

	public Object getHoursBetween(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {
		// TODO Auto-generated method stub

		BusinessHoursContext bussBusinessHoursContext = (BusinessHoursContext)objects[0];
		long fromTime = (long) Double.parseDouble(objects[1].toString());
		long toTime = (long) Double.parseDouble(objects[2].toString());
		return BusinessHoursAPI.getSecondsBetween(bussBusinessHoursContext, fromTime, toTime)/60/60;
	}

	public Object getMinsBetween(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {
		// TODO Auto-generated method stub

		BusinessHoursContext bussBusinessHoursContext = (BusinessHoursContext)objects[0];
		long fromTime = (long) Double.parseDouble(objects[1].toString());
		long toTime = (long) Double.parseDouble(objects[2].toString());
		return BusinessHoursAPI.getSecondsBetween(bussBusinessHoursContext, fromTime, toTime)/60;
	}

	public Object isTimeBetween(ScriptContext scriptContext, Map<String, Object> map, Object... objects) throws Exception {
		BusinessHoursContext bussBusinessHoursContext = (BusinessHoursContext)objects[0];
		long time=(long) Double.parseDouble(objects[1].toString());
		return BusinessHoursAPI.isTimeBetweenBusinessHours(bussBusinessHoursContext,time);
	}
}