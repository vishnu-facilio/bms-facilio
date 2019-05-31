package com.facilio.bmsconsole.context;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.opensymphony.xwork2.conversion.annotations.TypeConversion;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.text.ParseException;
import java.util.List;

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
}
