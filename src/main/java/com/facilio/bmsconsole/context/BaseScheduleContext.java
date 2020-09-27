package com.facilio.bmsconsole.context;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.mail.MethodNotSupportedException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PMTriggerContext.TriggerExectionSource;
import com.facilio.bmsconsole.context.WorkflowRuleResourceLoggerContext.Status;
import com.facilio.bmsconsole.context.sensor.SensorRuleType;
import com.facilio.bmsconsole.context.sensor.SensorRuleTypeValidationInterface;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioEnum;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.tasker.ScheduleInfo;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.ChainUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class BaseScheduleContext implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Long startTime; 
	private Long endTime; 
	private Long moduleId;
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
	
	public void saveRecords(List<ModuleBaseWithCustomFields> childRecords) throws Exception{
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleId);

		InsertRecordBuilder<ModuleBaseWithCustomFields> insertBuilder = new InsertRecordBuilder<ModuleBaseWithCustomFields>()
				.moduleName(module.getName())
				.fields(modBean.getAllFields(module.getName()));
		
		insertBuilder.addRecords(childRecords);
		insertBuilder.save();
	}
	
	public void saveAsV3Records(List<ModuleBaseWithCustomFields> childRecords) throws Exception{
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleId);
		FacilioChain addChain = ChainUtil.getCreateRecordChain(module.getName());
        FacilioContext context = addChain.getContext();
        Constants.setModuleName(context, module.getName());
        Constants.setRawInput(context, FieldUtil.getAsJSON(childRecords));
        context.put(Constants.BEAN_CLASS, ModuleBaseWithCustomFields.class);
        context.put(FacilioConstants.ContextNames.EVENT_TYPE, com.facilio.bmsconsole.workflow.rule.EventType.CREATE);
        context.put(FacilioConstants.ContextNames.PERMISSION_TYPE, FieldPermissionContext.PermissionType.READ_WRITE);
        addChain.execute();
	}
	
	public enum ScheduleType implements FacilioEnum{
		
		RECURRING_VISITOR_INVITE(new InviteVisitorScheduler()),
		COMMUNITY_ENGAGEMENT_EVENT(),
		PM()
		;
		
		public int getIndex() {
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
	
}
