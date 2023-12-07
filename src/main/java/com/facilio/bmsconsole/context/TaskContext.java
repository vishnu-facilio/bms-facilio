package com.facilio.bmsconsole.context;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.facilio.bmsconsoleV3.context.V3TaskContext;
import com.facilio.bmsconsoleV3.context.meter.V3MeterContext;
import com.facilio.modules.FacilioIntEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts2.json.annotations.JSON;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.unitconversion.Unit;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.opensymphony.xwork2.conversion.annotations.TypeConversion;

public class TaskContext extends ModuleBaseWithCustomFields {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = LogManager.getLogger(TaskContext.class.getName());
	private boolean preRequest;

	public boolean isPreRequest() {
		return preRequest;
	}

	public void setPreRequest(boolean preRequest) {
		this.preRequest = preRequest;
	}
	private String subject;
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	private String description;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	private ResourceContext resource;
	public ResourceContext getResource() {
		return resource;
	}
	public void setResource(ResourceContext resource) {
		this.resource = resource;
	}
	
	private AssetContext asset;
	public AssetContext getAsset() {
		return asset;
	}
	public void setAsset(AssetContext asset) {
		this.asset = asset;
	}
	
	private SpaceContext space;
	public SpaceContext getSpace() {
		return space;
	}
	public void setSpace(SpaceContext space) {
		this.space = space;
	}

	private V3MeterContext meter;

	public V3MeterContext getMeter() {
		return meter;
	}

	public void setMeter(V3MeterContext meter) {
		this.meter = meter;
	}

	public enum TaskStatus {
		OPEN,
		CLOSED
		;
		
		public int getValue() {
			return ordinal() + 1;
		}
		
		public static TaskStatus valueOf (int value) {
			if (value > 0 && value <= values().length) {
				return values() [value -1];
			}
			return null;
		}
		
	}
	private FacilioStatus status;
	public FacilioStatus getStatus() {
		return status;
	}
	public void setStatus(FacilioStatus status) {
		this.status = status;
	}
	
	private TaskStatus statusNew;
	public TaskStatus getStatusNewEnum() {
		return statusNew;
	}
	public void setStatusNew(TaskStatus statusNew) {
		this.statusNew = statusNew;
	}
	public int getStatusNew() {
		if (statusNew != null) {
			return statusNew.getValue();
		}
		return -1;
	}
	public void setStatusNew(int statusNew) {
		this.statusNew = TaskStatus.valueOf(statusNew);
	}

	private long parentTicketId = -1;
	public long getParentTicketId() {
		return parentTicketId;
	}
	public void setParentTicketId(long parentTicketId) {
		this.parentTicketId = parentTicketId;
	}
	
	private int uniqueId = -1;
	public int getUniqueId() {
		return uniqueId;
	}
	public void setUniqueId(int uniqueId) {
		this.uniqueId = uniqueId;
	}

	private long createdTime = -1;
	public long getCreatedTime() {
		return createdTime;
	}
	@TypeConversion(converter = "java.lang.String", value = "java.lang.String")
	public void setCreatedTime(String createdTime) {
		if(createdTime != null && !createdTime.isEmpty()) {
			try {
				this.createdTime = FacilioConstants.HTML5_DATE_FORMAT.parse(createdTime).getTime();
			}
			catch (ParseException e) {
				try {
					this.createdTime = FacilioConstants.HTML5_DATE_FORMAT_1.parse(createdTime).getTime();
				} catch (ParseException e1) {
					log.info("Exception occurred ", e1);
				}
			}
		}
	}
	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}
	
	private long modifiedTime = -1;
	public long getModifiedTime() {
		return modifiedTime;
	}
	public void setModifiedTime(long modifiedTime) {
		this.modifiedTime = modifiedTime;
	}
	
	private long readingFieldId = -1;
	public long getReadingFieldId() {
		return readingFieldId;
	}
	public void setReadingFieldId(long readingId) {
		this.readingFieldId = readingId;
	}
	
	private FacilioField readingField;
	public FacilioField getReadingField() {
		return readingField;
	}
	public void setReadingField(FacilioField readingField) {
		this.readingField = readingField;
	}
	
	private Unit readingFieldUnit;
	
	public Unit getReadingFieldUnitEnum() {
		return readingFieldUnit;
	}
	
	public int getReadingFieldUnit() {
		if (readingFieldUnit != null) {
			return readingFieldUnit.getUnitId();
		}
		return -1;
	}

	public void setReadingFieldUnit(int readingFieldUnit) {
		this.readingFieldUnit = Unit.valueOf(readingFieldUnit);
	}
	

	private long readingDataId = -1;
	public long getReadingDataId() {
		return readingDataId;
	}
	public void setReadingDataId(long readingDataId) {
		this.readingDataId = readingDataId;
	}
	
	private Object lastReading;
	public Object getLastReading() {
		return lastReading;
	}
	public void setLastReading(Object lastReading) {
		this.lastReading = lastReading;
	}
	
	private long sectionId = -1;
	public long getSectionId() {
		return sectionId;
	}
	public void setSectionId(long sectionId) {
		this.sectionId = sectionId;
	}
	
	private int sequence = -1;
	public int getSequence() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	private InputType inputType;
	public int getInputType() {
		if(inputType != null) {
			return inputType.getVal();
		}
		return -1;
	}
	public void setInputType(int inputType) {
		this.inputType = InputType.valueOf(inputType);
	}
	public void setInputType(InputType inputType) {
		this.inputType = inputType;
	}
	public InputType getInputTypeEnum() {
		return inputType;
	}
	
	private String inputValue;
	public String getInputValue() {
		return inputValue;
	}
	public void setInputValue(String inputValue) {
		this.inputValue = inputValue;
	}
	
	private long inputTime = -1;
	public long getInputTime() {
		return inputTime;
	}
	public void setInputTime(long inputTime) {
		this.inputTime = inputTime;
	}

	private List<String> inputValues;
	public List<String> getInputValues() {
		return inputValues;
	}
	public void setInputValues(List<String> inputValues) {
		this.inputValues = inputValues;
	}

	private Boolean attachmentRequired;
	public Boolean getAttachmentRequired() {
		return attachmentRequired;
	}
	public void setAttachmentRequired(boolean attachmentRequired) {
		this.attachmentRequired = attachmentRequired;
	}
	public boolean isAttachmentRequired() {
		if(attachmentRequired != null) {
			return attachmentRequired.booleanValue();
		}
		return false;
	}
	
	private int noOfAttachments = -1;
	public int getNoOfAttachments() {
		return noOfAttachments;
	}
	public void setNoOfAttachments(int noOfAttachments) {
		this.noOfAttachments = noOfAttachments;
	}
	
	private String remarks;
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	private List<String> options;
	public List<String> getOptions() {
		return options;
	}
	public void setOptions(List<String> options) {
		this.options = options;
	}
	
	private List<ReadingRuleContext> readingRules;
	public List<ReadingRuleContext> getReadingRules() {
		return readingRules;
	}
	public void setReadingRules(List<ReadingRuleContext> readingRules) {
		this.readingRules = readingRules;
	}
	
	private List<List<ActionContext>> actionsList;
	public List<List<ActionContext>> getActionsList() {
		return this.actionsList;
	}
	
	public void setActionsList(List<List<ActionContext>> actionsList) {
		this.actionsList = actionsList;
	}

	public enum InputType {
		NONE,
		READING,
		TEXT,
		NUMBER,
		RADIO,
//		CHECKBOX,
		BOOLEAN
		;
		
		public int getVal() {
			return ordinal()+1;
		}
		
		public static InputType valueOf(int val) {
			if(val > 0 && val <= values().length) {
				return values()[val - 1];
			}
			return null;
		}
	}
	
	private long siteId = -1;
	public void setSiteId(long siteId) {
		this.siteId = siteId;
	}
	
	public long getSiteId() {
		return this.siteId;
	}
	
	private User createdBy;
	public User getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}
	
	private String defaultValue;
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	private long actionId = -1;
	public long getActionId() {
		return actionId;
	}
	public void setActionId(long actionId) {
		this.actionId = actionId;
	}
	
	private long woCreateTemplateId = -1;
	public long getWoCreateTemplateId() {
		return woCreateTemplateId;
	}
	public void setWoCreateTemplateId(long woCreateTemplateId) {
		this.woCreateTemplateId = woCreateTemplateId;
	}
	
	private long woCreateFormId = -1;
	public long getWoCreateFormId() {
		return woCreateFormId;
	}
	public void setWoCreateFormId(long woCreateFormId) {
		this.woCreateFormId = woCreateFormId;
	}
	private ActionContext action;
	public ActionContext getAction() {
		return action;
	}
	public void setAction(ActionContext action) {
		this.action = action;
	}

	private Boolean failed;
	public Boolean getFailed() {
		return failed;
	}
	public void setFailed(boolean failed) {
		this.failed = failed;
	}
	public boolean isFailed() {
		if(failed != null) {
			return failed.booleanValue();
		}
		return false;
	}

	private String truevalue;

	public String getTruevalue() {
		return truevalue;
	}

	public void setTruevalue(String truevalue) {
		this.truevalue = truevalue;
	}

	private String falsevalue;

	public String getFalsevalue() {
		return falsevalue;
	}

	public void setFalsevalue(String falsevalue) {
		this.falsevalue = falsevalue;
	}
	private String failureValue;
	public String getFailureValue() {
		return failureValue;
	}
	public void setFailureValue(String failureValue) {
		this.failureValue = failureValue;
	}
	
	private int deviationOperatorId = -1;
	public int getDeviationOperatorId() {
		if(deviationOperator != null) {
			return deviationOperator.getOperatorId();
		}
		return deviationOperatorId;
	}
	public void setDeviationOperatorId(int operatorId) {
		this.deviationOperatorId = operatorId;
		this.deviationOperator = operatorId > 0 ? Operator.getOperator(operatorId) : null;
	}

	private Operator deviationOperator;
	@JsonIgnore
	public Operator getDeviationOperator() {
		return deviationOperator;
	}
	public void setDeviationOperator(Operator operator) {
		this.deviationOperatorId = operator.getOperatorId();
		this.deviationOperator = operator;
	}
	
	private long offlineModifiedTime = -1;
	public long getOfflineModifiedTime() {
		return offlineModifiedTime;
	}
	public void setOfflineModifiedTime(long offlineModifiedTime) {
		this.offlineModifiedTime = offlineModifiedTime;
	}
	
	private long syncTime = -1;
	public long getSyncTime() {
		return syncTime;
	}
	public void setSyncTime(long syncTime) {
		this.syncTime = syncTime;
	}

	private Boolean remarksRequired;

	public Boolean getRemarksRequired() {
		return remarksRequired;
	}
	public void setRemarksRequired(boolean remarksRequired) {
		this.remarksRequired = remarksRequired;
	}

	public boolean isRemarksRequired() {
		if(remarksRequired != null) {
			return remarksRequired.booleanValue();
		}
		return false;
	}

	private List<String> remarkOptionValues;

	public List<String> getRemarkOptionValues() throws Exception {

		if (remarkOptionValuesString != null && remarkOptionValuesString.length() > 0) {
			List<String> valueList = new ArrayList<String>(Arrays.asList(remarkOptionValuesString.split(",")));
			return valueList;
		}
		return remarkOptionValues;
	}
	public void setRemarkOptionValues(List<String> remarkOptionValues) {
		this.remarkOptionValues = remarkOptionValues;
	}

	private String remarkOptionValuesString;

	public String getRemarkOptionValuesString() {
		if (remarkOptionValues != null && remarkOptionValues.size() != 0) {
			String valueString = StringUtils.join(remarkOptionValues, ",");
			return valueString;
		}
		return remarkOptionValuesString;
	}

	public void setRemarkOptionValuesString(String remarkOptionValuesString) {
		this.remarkOptionValuesString = remarkOptionValuesString;
	}

	private List<String> attachmentOptionValues;
	public List<String> getAttachmentOptionValues() throws Exception {
		return attachmentOptionValues;
	}
	public void setAttachmentOptionValues(List<String> attachmentOptionValues) {
		this.attachmentOptionValues = attachmentOptionValues;
	}
	public String getAttachmentOptionValuesString() {
		if (CollectionUtils.isNotEmpty(attachmentOptionValues)) {
			return StringUtils.join(attachmentOptionValues, ",");
		}
		return null;
	}
	public void setAttachmentOptionValuesString(String attachmentOptionValuesString) {
		if (StringUtils.isNotEmpty(attachmentOptionValuesString)) {
			this.attachmentOptionValues = new ArrayList<String>(Arrays.asList(attachmentOptionValuesString.split(",")));
		}
	}
	
	private JSONObject additionalInfo;
	
	public JSONObject getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(JSONObject additionalInfo) {
		this.additionalInfo = additionalInfo;
	}
	
	public void addAdditionalInfo(String key, Object value) {
		if(this.additionalInfo == null) {
			this.additionalInfo =  new JSONObject();
		}
		this.additionalInfo.put(key,value);
	}
	
	private String additionalInfoJsonStr;
	public String getAdditionalInfoJsonStr() {
		if(additionalInfo != null) {
			return additionalInfo.toJSONString();
		}
		return additionalInfoJsonStr;
	}
	public void setAdditionalInfoJsonStr(String additionalInfoJsonStr) throws org.json.simple.parser.ParseException{
		this.additionalInfoJsonStr = additionalInfoJsonStr;
		JSONParser parser = new JSONParser();
		this.additionalInfo = (JSONObject) parser.parse(additionalInfoJsonStr);
	}

	@JsonIgnore
	private WorkOrderContext parentWo;
	
	@JSON(serialize = false)
	public WorkOrderContext getParentWo() {
		return parentWo;
	}
	@JSON(serialize = false)
	public void setParentWo(WorkOrderContext parentWo) {
		this.parentWo = parentWo;
	}


	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	private String notes;

	public String getActionContent() {
		return actionContent;
	}

	public void setActionContent(String actionContent) {
		this.actionContent = actionContent;
	}

	private String actionContent;


	private V3TaskContext.TaskFrequency taskFrequency;

	public Integer getTaskFrequency() {
		if (taskFrequency == null) {
			return null;
		}
		return taskFrequency.getIndex();
	}

	public void setTaskFrequency(Integer taskFrequency) {
		if (taskFrequency != null) {
			this.taskFrequency = V3TaskContext.TaskFrequency.valueOf(taskFrequency);
		} else {
			this.taskFrequency = null;
		}
	}
	public void setTaskFrequencyEnum(V3TaskContext.TaskFrequency taskFrequency) {
		this.taskFrequency = taskFrequency;
	}

	public V3TaskContext.TaskFrequency getTaskFrequencyEnum() {
		return taskFrequency;
	}

	public String getTaskFrequencyName() {
		if(taskFrequency !=null)
		{
			return taskFrequency.getValue();
		}
		return null;
	}
	@AllArgsConstructor
	@Getter
	public static enum TaskFrequency implements FacilioIntEnum {

		DO_NOT_REPEAT ("Do Not Repeat"),
		DAILY("Daily"),
		WEEKLY( "Weekly"),
		MONTHLY("Monthly"),
		QUARTERLY("Quarterly"),
		HALF_YEARLY("Half Yearly"),
		ANNUALLY("Annually"),
		CUSTOM("Custom"),
		HOURLY("Hourly")
		;

		public int getVal() {
			return ordinal() + 1;
		}
		String name;
		@Override
		public String getValue() {
			// TODO Auto-generated method stub
			return this.name;
		}
		private static final V3TaskContext.TaskFrequency[] CREATION_TYPES = V3TaskContext.TaskFrequency.values();
		public static V3TaskContext.TaskFrequency valueOf(int type) {
			if (type > 0 && type <= CREATION_TYPES.length) {
				return CREATION_TYPES[type - 1];
			}
			return null;
		}
	}


	private V3TaskContext.TaskCriticality taskCriticality;


	public Integer getTaskCriticality() {
		if (taskCriticality == null) {
			return null;
		}
		return taskCriticality.getIndex();
	}

	public void setTaskCriticality(Integer taskCriticality) {
		if (taskCriticality !=null) {
			this.taskCriticality = V3TaskContext.TaskCriticality.valueOf(taskCriticality);
		} else {
			this.taskCriticality = null;
		}
	}
	public void setTaskCriticalityEnum(V3TaskContext.TaskCriticality taskCriticality) {
		this.taskCriticality = taskCriticality;
	}

	public V3TaskContext.TaskCriticality getTaskCriticalityEnum() {
		return taskCriticality;
	}

	public String getTaskCriticalityName() {
		if(taskCriticality !=null) {
			return taskCriticality.getName();
		}
		return null;
	}

	public String getTaskCriticalityColor() {
		if(taskCriticality !=null) {
			return taskCriticality.getColor();
		}
		return null;
	}

	public String getTaskCriticalitySeverity() {
		if(taskCriticality !=null) {
			return taskCriticality.getSeverity();
		}
		return null;
	}

	@AllArgsConstructor
	@Getter
	public static enum TaskCriticality implements FacilioIntEnum {

		STATUTORY("Statutory","red","L1"),
		MANDATORY("Mandatory", "pink","L2"),
		OPTIMAL("Optimal", "orange","L3"),
		DISCRETIONARY("Discretionary", "green","L3")
		;

		public int getVal() {
			return ordinal() + 1;
		}
		String name;
		String color;
		String severity;
		@Override
		public String getValue() {
			// TODO Auto-generated method stub
			return this.name;
		}

		public String getColor()
		{
			return this.color;
		}
		public String getSeverity()
		{
			return this.severity;
		}

		private static final V3TaskContext.TaskCriticality[] CREATION_TYPES = V3TaskContext.TaskCriticality.values();
		public static V3TaskContext.TaskCriticality valueOf(int type) {
			if (type > 0 && type <= CREATION_TYPES.length) {
				return CREATION_TYPES[type - 1];
			}
			return null;
		}
	}


	public Long getSfgScheduleId() {
		return sfgScheduleId;
	}

	public void setSfgScheduleId(Long sfgScheduleId) {
		this.sfgScheduleId = sfgScheduleId;
	}

	private Long sfgScheduleId;


	public V3TaskContext.SkillSet getSkillSetEnum() {
		return skillSet;
	}

	public void setSkillSetEnum(V3TaskContext.SkillSet skillSet) {
		this.skillSet = skillSet;
	}

	public String getSkillSetName() {
		if (skillSet != null) {
			return skillSet.getValue();
		}
		return null;
	}


	public Integer getSkillSet() {
		if (skillSet == null) {
			return null;
		}
		return skillSet.getIndex();
	}

	public void setSkillSet(Integer skillSet) {
		if (skillSet !=null) {
			this.skillSet = V3TaskContext.SkillSet.valueOf(skillSet);
		} else {
			this.skillSet = null;
		}
	}

	private V3TaskContext.SkillSet skillSet;


	@AllArgsConstructor
	@Getter
	public static enum SkillSet implements FacilioIntEnum {
		NOT_SPECIFIED("Not-specified"),
		APPOINTED_PERSON("Appointed Person"),
		AUTHORIZING_ENGINEER("Authorising Engineer"),
		AUTHORIZING_ENGINEER_FIRE("Authorising Engineer (Fire)"),
		AUTHORIZING_ENGINEER_LIFTS("Authorising Engineer (Lifts)"),
		AUTHORIZED_PERSON_HV("Authorised Person (HV)"),
		AUTHORIZED_PERSON_LV("Authorised Person (LV)"),
		AUTHORIZED_PERSON_LIFTS("Authorised Person (Lifts)"),
		AUTHORIZED_PERSON("Authorised Person"),
		BUILDING_TRADE("Building Trade"),
		CONTROLS_ENGINEER("Controls Engineer"),
		COMPETENT_PERSON_FIRE("Competent Person (Fire)"),
		COMPETENT_PERSON_HV("Competent Person (HV)"),
		COMPETENT_PERSON_LV("Competent Person (LV)"),
		COMPETENT_PERSON_LIFTS("Competent Person (Lifts)"),
		CONTRACTOR("Contractor"),
		COMPETENT_PERSON("Competent Person"),
		DUTY_HOLDER("Duty Holder"),
		DESIGNATED_PERSON("Designated Person"),
		DESIGNATED_PERSONS_LIFTS("Designated Persons (Lifts)"),
		ELECTRICAL("Electrical"),
		FACILITIES_COORDINATOR("Facilities Coordinator"),
		FACILITIES_MANAGER("Facilities Manager"),
		FACILITIES_OPERATIVE("Facilities Operative"),
		FIRE_OFFICER("Fire Officer"),
		FIRE_SAFETY_MANAGER("Fire Safety Manager"),
		GAS_SAFE("Gas Safe"),
		INFECTION_CONTROL_OFFICER("Infection Control Officer"),
		LOCKSMITH("Locksmith"),
		LIFT_STEWARD("Lift Steward"),
		MECHANICAL("Mechanical"),
		M_AND_E("M&E"),
		MANAGEMENT("Management"),
		MANAGER("Manager"),
		MULTI_SKILLED("Multi-skilled"),
		OPERATOR("Operator"),
		PLANT_ATTENDANT("Plant Attendant"),
		PAINTER("Painter"),
		PRISON_OFFICER("Prison Officer"),
		PLUMBER("Plumber"),
		POOL_ATTENDANT("Pool Attendant"),
		REFRIGERATION_ENGINEER("Refrigeration Engineer"),
		SPECIALIST("Specialist"),
		TECHNICIAN("Technician"),
		USER("User");

		public int getVal() {
			return ordinal() + 1;
		}
		String name;
		@Override
		public String getValue() {
			// TODO Auto-generated method stub
			return this.name;
		}
		private static final V3TaskContext.SkillSet[] CREATION_TYPES = V3TaskContext.SkillSet.values();
		public static V3TaskContext.SkillSet valueOf(int type) {
			if (type > 0 && type <= CREATION_TYPES.length) {
				return CREATION_TYPES[type - 1];
			}
			return null;
		}

		public static Integer fromString(String name) {
			for (V3TaskContext.SkillSet skillingKey : V3TaskContext.SkillSet.values()) {
				if (skillingKey.getName().equalsIgnoreCase(name.trim())) {
					return skillingKey.getVal();
				}
			}
			return NOT_SPECIFIED.getVal();
		}
	}
}
