package com.facilio.bmsconsole.context;

import java.io.Serializable;
import java.util.List;

import org.apache.struts2.json.annotations.JSON;

import com.facilio.bmsconsole.util.FacilioFrequency;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.workflows.context.WorkflowContext;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class FormulaFieldContext implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id = -1;
	private long startTime;
	private long endTime;
	private long kpiCategory = -1l;
	public long getKpiCategory() {
		return kpiCategory;
	}
	public void setKpiCategory(long kpiCategory) {
		this.kpiCategory = kpiCategory;
	}
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long siteId = -1;
	public long getSiteId() {
		return siteId;
	}
	public void setSiteId(long siteId) {
		this.siteId = siteId;
	}

	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private String description;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	private FormulaFieldType formulaFieldType;
	public FormulaFieldType getFormulaFieldTypeEnum() {
		return formulaFieldType;
	}
	public void setFormulaFieldType(FormulaFieldType formulaFieldType) {
		this.formulaFieldType = formulaFieldType;
	}
	public int getFormulaFieldType() {
		if (formulaFieldType != null) {
			return formulaFieldType.getValue();
		}
		return -1;
	}
	public void setFormulaFieldType(int formulaFieldType) {
		this.formulaFieldType = FormulaFieldType.valueOf(formulaFieldType);
	}

	private long workflowId = -1;
	public long getWorkflowId() {
		return workflowId;
	}
	public void setWorkflowId(long workflowId) {
		this.workflowId = workflowId;
	}
	
	private WorkflowContext workflow;
	@JsonIgnore
	public WorkflowContext getWorkflow() {
		return workflow;
	}
	public void setWorkflow(WorkflowContext workflow) {
		this.workflow = workflow;
	}
	
	private TriggerType triggerType;
	public TriggerType getTriggerTypeEnum() {
		return triggerType;
	}
	public void setTriggerType(TriggerType triggerType) {
		this.triggerType = triggerType;
	}
	public int getTriggerType() {
		if (triggerType != null) {
			return triggerType.getValue();
		}
		return -1;
	}
	public void setTriggerType(int triggerType) {
		this.triggerType = TriggerType.valueOf(triggerType);
	}

	private FacilioFrequency frequency;
	public int getFrequency() {
		if (frequency != null) {
			return frequency.getValue();
		}
		return -1;
	}
	public void setFrequency(int frequency) {
		this.frequency = FacilioFrequency.valueOf(frequency);
	}
	public void setFrequency(FacilioFrequency frequency) {
		this.frequency = frequency;
	}
	public FacilioFrequency getFrequencyEnum() {
		return frequency;
	}
	
	private int interval = -1; //In minutes
	public int getInterval() {
		return interval;
	}
	public void setInterval(int interval) {
		this.interval = interval;
	}
	
	private String moduleName;
	public String getModuleName() {
		if (moduleName == null && readingField != null && readingField.getModule() != null) {
			return readingField.getModule().getName();
		}
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	private long moduleId = -1;
	public long getModuleId() {
		return moduleId;
	}
	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}
	
	private FacilioModule module;
	@JSON(serialize = false)
	@JsonIgnore
	public FacilioModule getModule() {
		return module;
	}
	@JsonIgnore
	public void setModule(FacilioModule module) {
		this.module = module;
		setModuleName(module.getName());
	}

	private long readingFieldId = -1;
	public long getReadingFieldId() {
		return readingFieldId;
	}
	public void setReadingFieldId(long readingFieldId) {
		this.readingFieldId = readingFieldId;
	}
	
	private FacilioField readingField;
	public FacilioField getReadingField() {
		return readingField;
	}
	public void setReadingField(FacilioField readingField) {
		this.readingField = readingField;
	}
	
	private ResourceType resourceType;
	public ResourceType getResourceTypeEnum() {
		return resourceType;
	}
	public void setResourceType(ResourceType resourceType) {
		this.resourceType = resourceType;
	}
	public int getResourceType() {
		if (resourceType != null) {
			return resourceType.getValue();
		}
		return -1;
	}
	public void setResourceType(int resourceType) {
		this.resourceType = ResourceType.valueOf(resourceType);
	}

	private long resourceId = -1;
	public long getResourceId() {
		return resourceId;
	}
	public void setResourceId(long resourceId) {
		this.resourceId = resourceId;
	}
	
	private long assetCategoryId = -1;
	public long getAssetCategoryId() {
		return assetCategoryId;
	}
	public void setAssetCategoryId(long assetCategoryId) {
		this.assetCategoryId = assetCategoryId;
	}
	
	private long spaceCategoryId = -1;
	public long getSpaceCategoryId() {
		return spaceCategoryId;
	}
	public void setSpaceCategoryId(long spaceCategoryId) {
		this.spaceCategoryId = spaceCategoryId;
	}

	private List<Long> includedResources;
	public List<Long> getIncludedResources() {
		return includedResources;
	}
	public void setIncludedResources(List<Long> includedResources) {
		this.includedResources = includedResources;
	}
	
	private List<Long> matchedResourcesIds;
	public List<Long> getMatchedResourcesIds() {
		return matchedResourcesIds;
	}
	public void setMatchedResourcesIds(List<Long> matchedResourcesIds) {
		this.matchedResourcesIds = matchedResourcesIds;
	}
	
	private List<? extends ResourceContext> matchedResources;
	public List<? extends ResourceContext> getMatchedResources() {
		return matchedResources;
	}
	public void setMatchedResources(List<? extends ResourceContext> matchedResources) {
		this.matchedResources = matchedResources;
	}

	private FieldType resultDataType;
	public FieldType getResultDataTypeEnum() {
		return resultDataType;
	}
	public void setResultDataType(FieldType resultDataType) {
		this.resultDataType = resultDataType;
	}
	public int getResultDataType() {
		if (resultDataType != null) {
			return resultDataType.getTypeAsInt();
		}
		return -1;
	}
	public void setResultDataType(int resultDataType) {
		this.resultDataType = FieldType.getCFType(resultDataType);
	}
	
	private Boolean active;
	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public boolean isActive() {
		if (active != null) {
			return active.booleanValue();
		}
		return false;
	}
	
	private double minTarget = -1;
	public double getMinTarget() {
		return minTarget;
	}
	public void setMinTarget(double minTarget) {
		this.minTarget = minTarget;
	}

	private double target = -1;
	public double getTarget() {
		return target;
	}
	public void setTarget(double target) {
		this.target = target;
	}

	private Object currentValue = null;	// Only if single matched resource and target is there
	public Object getCurrentValue() {
		return currentValue;
	}
	public void setCurrentValue(Object currentValue) {
		this.currentValue = currentValue;
	}

	private long createdTime = -1;
	public long getCreatedTime() {
		return createdTime;
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
	
	private long violationRuleId = -1;
	public long getViolationRuleId() {
		return violationRuleId;
	}
	public void setViolationRuleId(long violationRuleId) {
		this.violationRuleId = violationRuleId;
	}
	
	private WorkflowRuleContext violationRule;
	@JSON(serialize=false)
	public WorkflowRuleContext getViolationRule() {
		return violationRule;
	}
	public void setViolationRule(WorkflowRuleContext violationRule) {
		this.violationRule = violationRule;
	}

	public enum TriggerType {
		SCHEDULE,
		POST_LIVE_READING,
		PRE_LIVE_READING
		;
		
		public int getValue() {
			return ordinal() + 1;
		}
		
		public static TriggerType valueOf (int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}

	public enum FormulaFieldType {
		ENPI,
		LIVE_FORMULA,
		M_AND_V_ENPI
		;
		
		public int getValue() {
			return ordinal() + 1;
		}
		
		public static FormulaFieldType valueOf (int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
	
	public enum ResourceType {
		ONE_RESOURCE,
		ALL_SITES,
		ALL_BUILDINGS,
		ALL_FLOORS,
		SPACE_CATEGORY,
		ASSET_CATEGORY
		;
		
		public int getValue() {
			return ordinal() + 1;
		}
		
		public static ResourceType valueOf (int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuilder builder = new StringBuilder("Formula Field::")
									.append(id)
									.append("::")
									.append(name)
									;
		return builder.toString();
	}
}
