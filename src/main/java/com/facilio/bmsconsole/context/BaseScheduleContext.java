package com.facilio.bmsconsole.context;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsoleV3.context.inspection.InspectionScheduler;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.tasker.ScheduleInfo;
import com.facilio.v3.util.V3Util;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class BaseScheduleContext implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Long startTime; 
	private Long endTime; 
	private Long moduleId;
	private Long dataModuleId;
	private Long recordId;
	private ScheduleInfo scheduleInfo;
	private Long generatedUptoTime;
	private ScheduleType scheduleType;
	private Long id;
	private Long orgId;
	
	public List<Map<String, Object>> fetchParent() throws Exception{
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleId);
		SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
				.module(module)
				.select(modBean.getAllFields(module.getName()))
				.beanClass(ModuleBaseWithCustomFields.class)
				.andCondition(CriteriaAPI.getIdCondition(recordId, module));
		
		List<Map<String, Object>> parentRecord = selectBuilder.getAsProps();
		return parentRecord;
	}
	
	public void saveRecords(List<? extends ModuleBaseWithCustomFields> childRecords) throws Exception{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(getDataModuleId());	
		RecordAPI.addRecord(true, childRecords, module.getExtendModule(), modBean.getAllFields(module.getExtendModule().getName()));
	}
	
	public void saveAsV3Records(List<? extends ModuleBaseWithCustomFields> childRecords) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(getDataModuleId());
		
		for(ModuleBaseWithCustomFields childRecord :childRecords) {
			
			V3Util.createRecord(module, FieldUtil.getAsJSON(childRecord));
		}
	}
	
	public enum ScheduleType implements FacilioIntEnum {
		
		RECURRING_VISITOR_INVITE(new InviteVisitorScheduler()),
		COMMUNITY_ENGAGEMENT_EVENT(),
		PM(),
		INSPECTION(new InspectionScheduler()),
		INDUCTION(new InspectionScheduler()),			// should  change
		;
		
		public Integer getIndex() {
			return ordinal()+1;
		}
		
		public String getValue() {
	        return name();
	    }

		public static ScheduleType valuOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
		
		private ScheduleTypeInterface schedulerType;
	

		private ScheduleType(){
		}
		
		private ScheduleType(ScheduleTypeInterface schedulerInvokingType){
			this.schedulerType = schedulerInvokingType;
		}
		
	    public ScheduleTypeInterface getSchedulerTypeHandler() {
	        return schedulerType;
	    }
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

	public Long getModuleId() {
		return moduleId;
	}

	public void setModuleId(Long moduleId) {
		this.moduleId = moduleId;
	}

	public Long getRecordId() {
		return recordId;
	}

	public void setRecordId(Long recordId) {
		this.recordId = recordId;
	}

	public Long getGeneratedUptoTime() {
		return generatedUptoTime;
	}

	public void setGeneratedUptoTime(Long generatedUptoTime) {
		this.generatedUptoTime = generatedUptoTime;
	}

	public ScheduleType getScheduleTypeEnum() {
		return scheduleType;
	}

	public int getScheduleType() {
		if (scheduleType != null) {
			return scheduleType.getIndex();
		}
		return -1;
	}
	
	public void setScheduleType(ScheduleType scheduleType) {
		this.scheduleType = scheduleType;
	}
	
	public void setScheduleType(int scheduleType) {
		this.scheduleType = ScheduleType.valuOf(scheduleType);
	}
	
	public ScheduleInfo getScheduleInfo() {
		return scheduleInfo;
	}

	public void setScheduleInfo(ScheduleInfo scheduleInfo) {
		this.scheduleInfo = scheduleInfo;
	}
	
	public String getScheduleInfoJson() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if(scheduleInfo != null) {
			return FieldUtil.getAsJSON(scheduleInfo).toJSONString();
		}
		return null;
	}
	public void setScheduleInfoJson(String jsonString) throws JsonParseException, JsonMappingException, IOException, ParseException {
		JSONParser parser = new JSONParser();
		this.scheduleInfo = FieldUtil.getAsBeanFromJson((JSONObject)parser.parse(jsonString), ScheduleInfo.class);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOrgId() {
		return orgId;
	}

	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}

	@Override
	public String toString() {
		return "BaseScheduleContext [startTime=" + startTime + ", endTime=" + endTime + ", moduleId=" + moduleId
				+ ", recordId=" + recordId + ", generatedUptoTime=" + generatedUptoTime + ", id=" + id + "]";
	}

	public Long getDataModuleId() {
		if(dataModuleId != null) {
			return dataModuleId;
		}
		return getModuleId();
	}

	public void setDataModuleId(Long dataModuleId) {
		this.dataModuleId = dataModuleId;
	}
	
}
