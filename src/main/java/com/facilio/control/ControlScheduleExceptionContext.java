package com.facilio.control;

import java.util.Collections;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.facilio.modules.FacilioEnum;
import com.facilio.modules.FieldUtil;
import com.facilio.tasker.ScheduleInfo;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.V3Context;

public class ControlScheduleExceptionContext extends V3Context {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String name;
	Type type;
	JSONObject startSchedule;
	JSONObject endSchedule;
	String endScheduleTime;
	Long startTime;
	Long endTime;
	boolean offSchedule;
	boolean excludeSchedule;
	
	ControlScheduleContext schedule;		// only for client use case dont use it in code
	
	public static enum Type implements FacilioEnum {
        RECURING, 
        ONETIME;

        @Override
        public int getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name();
        }

        public static Type valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		if(type != null) {
			return type.getIndex();
		}
		return -1;
	}
	
	public void build() throws Exception {
		if(startSchedule != null && endScheduleTime != null) {
			ScheduleInfo startScheduleInfo = FieldUtil.getAsBeanFromJson(startSchedule, ScheduleInfo.class);
			startScheduleInfo.setTimes(Collections.singletonList(endScheduleTime));
			
			endSchedule = FieldUtil.getAsJSON(startScheduleInfo);
		}
	}
	
	public Type getTypeEnum() {
		return type;
	}

	public void setType(int type) {
		
		this.type = Type.valueOf(type);
	}

	public String getStartSchedule() {
		if(startSchedule != null) {
			return startSchedule.toJSONString();
		}
		return null;
	}
	
	public ScheduleInfo startScheduleAsObj() throws Exception {
		if(getStartScheduleJSON() != null) {
			return FieldUtil.getAsBeanFromJson(getStartScheduleJSON(), ScheduleInfo.class);
		}
		return null;
	}
	
	public ScheduleInfo endScheduleAsObj() throws Exception {
		if(getEndScheduleJson() != null) {
			return FieldUtil.getAsBeanFromJson(getEndScheduleJson(), ScheduleInfo.class);
		}
		return null;
	}
	
	public JSONObject getStartScheduleJSON() {
		return startSchedule;
	}

	public void setStartSchedule(String startSchedule) throws ParseException {
		this.startSchedule = FacilioUtil.parseJson(startSchedule);
	}
	
	public void setStartScheduleJson(JSONObject startSchedule) throws ParseException {
		this.startSchedule = startSchedule;
	}

	public String getEndSchedule() {
		if(endSchedule != null) {
			return endSchedule.toJSONString();
		}
		return null;
	}
	
	public JSONObject getEndScheduleJson() {
		return endSchedule;
	}
	
	public void setEndSchedule(String endSchedule) throws ParseException {
		this.endSchedule = FacilioUtil.parseJson(endSchedule);
	}

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	public boolean isOffSchedule() {
		return offSchedule;
	}
	
	public boolean getOffSchedule() {
		return offSchedule;
	}

	public void setOffSchedule(boolean offSchedule) {
		this.offSchedule = offSchedule;
	}

	public boolean isExcludeSchedule() {
		return excludeSchedule;
	}
	
	public boolean getExcludeSchedule() {
		return excludeSchedule;
	}

	public void setExcludeSchedule(boolean excludeSchedule) {
		this.excludeSchedule = excludeSchedule;
	}

	public String getEndScheduleTime() {
		return endScheduleTime;
	}

	public void setEndScheduleTime(String endScheduleTime) {
		this.endScheduleTime = endScheduleTime;
	}
	@Deprecated
	public ControlScheduleContext getSchedule() {
		return schedule;
	}
	@Deprecated
	public void setSchedule(ControlScheduleContext schedule) {
		this.schedule = schedule;
	}
}
