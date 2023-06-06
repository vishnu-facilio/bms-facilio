package com.facilio.bmsconsole.forms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.context.SingleSharingContext;
import org.apache.struts2.json.annotations.JSON;

import com.facilio.modules.FacilioModule;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class FacilioForm implements Serializable {
	public FacilioForm () {}

	public FacilioForm(long id, long orgId, String name, String displayName, long moduleId, List<FormField> fields, List<Long> ruleFieldIds, FacilioModule module, LabelPosition labelPosition, long appId, String appLinkName) {
		this.id = id;
		this.orgId = orgId;
		this.name = name;
		this.displayName = displayName;
		this.moduleId = moduleId;
		this.fields = fields;
		this.ruleFieldIds = ruleFieldIds;
		this.module = module;
		this.labelPosition = labelPosition;
		this.showInMobile = true;
		this.appLinkName = appLinkName;
		this.showInWeb = true;
		this.appId = appId;	
	}

	public FacilioForm(FacilioForm form) {
		this(form.id, form.orgId, form.name, form.displayName, form.moduleId, new ArrayList<>(form.fields), form.ruleFieldIds, form.module, form.labelPosition, form.appId, form.appLinkName);
		this.showInMobile = form.showInMobile;
		this.showInWeb = form.showInWeb;
		this.hideInList = form.hideInList;
		this.ignoreCustomFields = form.ignoreCustomFields;
	}

	public FacilioForm(String name, String displayName, String description, long moduleId, FacilioModule module, boolean showInWeb, boolean showInMobile, boolean hideInList, boolean isSystemForm, boolean primaryForm, LabelPosition labelPosition, FacilioForm.Type type, long appId, String appLinkName) {
		this.name = name;
		this.type = type;
		this.appId = appId;
		this.module = module;
		this.moduleId = moduleId;
		this.showInWeb = showInWeb;
		this.hideInList = hideInList;
		this.primaryForm = primaryForm;
		this.displayName = displayName;
		this.appLinkName = appLinkName;
		this.description = description;
		this.showInMobile = showInMobile;
		this.isSystemForm = isSystemForm;
		this.labelPosition = labelPosition;
	}

	private static final long serialVersionUID = 1L;
	
	private long id = -1;
	public long getId() {
		return this.id;
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
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private String displayName;
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	
	private List<Long> siteIds;
	
	
	public List<Long> getSiteIds() {
		return siteIds;
	}

	public void setSiteIds(List<Long> siteIds) {
		this.siteIds = siteIds;
	}


	private long moduleId = -1;

	public long getModuleId() {
		if (this.module != null) {
			return this.module.getModuleId();
		}
		return this.moduleId;
	}
	
	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}
	
	private List<FormField> fields;
	
	public List<FormField> getFields() {
		return fields;
	}
	public void setFields(List<FormField> fields) {
		this.fields = fields;
	}
	
	private List<Long> ruleFieldIds;
	
	public List<Long> getRuleFieldIds() {
		return ruleFieldIds;
	}

	public void setRuleFieldIds(List<Long> ruleFieldIds) {
		this.ruleFieldIds = ruleFieldIds;
	}

	@JsonIgnore
	@JSON(serialize=false)
	public Map<String, FormField> getFieldsMap() {
		if (fields == null) {
			return null;
		}
		return fields.stream().collect(Collectors.toMap(FormField::getName, Function.identity()));
	}
	
	private List<FormSection> sections;
	public List<FormSection> getSections() {
		return sections;
	}
	public void setSections(List<FormSection> sections) {
		this.sections = sections;
	}

	private FacilioModule module;
	
	public void setModule(FacilioModule module) {
		this.module = module;
	}
	
	public FacilioModule getModule() {
		return this.module;
	}
	
	
	
	
	
	
	public int getLabelPosition() {
		if (labelPosition != null) {
			return labelPosition.getIntVal();
		}
		return -1;
	}

	public void setLabelPosition(int val) {
		this.labelPosition = LabelPosition.getLabelPosition(val);
	}
	
	public void setLabelPosition(LabelPosition val) {
		this.labelPosition = val;
	}
	
	public LabelPosition getLabelPositionEnum() {
		return this.labelPosition;
	}
	
	public String getLabelPositionVal() {
		if (this.labelPosition != null) {
			return this.labelPosition.getStringVal();
		}
		return null;
	}

	private String description;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	private Boolean showInMobile;
	public Boolean getShowInMobile() {
		return showInMobile;
	}
	public void setShowInMobile(Boolean showInMobile) {
		this.showInMobile = showInMobile;
	}
	
	private Boolean showInWeb;
	
	public Boolean getShowInWeb() {
		return showInWeb;
	}

	public void setShowInWeb(Boolean showInWeb) {
		this.showInWeb = showInWeb;
	}

	private Boolean hideInList;
	public Boolean getHideInList() {
		return hideInList;
	}
	public void setHideInList(Boolean hideInList) {
		this.hideInList = hideInList;
	}
	public Boolean isHideInList() {
		if (this.hideInList == null) {
			return false;
		}
		return this.hideInList;
	}

	private long stateFlowId = -1;
	public long getStateFlowId() {
		return stateFlowId;
	}
	public void setStateFlowId(long stateFlowId) {
		this.stateFlowId = stateFlowId;
	}

	private boolean locked;
	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	private Boolean ignoreCustomFields;
	@JsonIgnore
	@JSON(serialize=false)
	public boolean isIgnoreCustomFields() {
		if (ignoreCustomFields == null) {
			return false;
		}
		return ignoreCustomFields;
	}
	@JsonIgnore
	@JSON(serialize=false)
	public void setIgnoreCustomFields(boolean ignoreCustomFields) {
		this.ignoreCustomFields = ignoreCustomFields;
	}

	public long getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(long sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	private long sequenceNumber = -1;

	public long getSysModifiedTime() {
		return sysModifiedTime;
	}

	public void setSysModifiedTime(long sysModifiedTime) {
		this.sysModifiedTime = sysModifiedTime;
	}

	private long sysModifiedTime;

	public long getSysCreatedTime() {
		return sysCreatedTime;
	}

	public void setSysCreatedTime(long sysCreatedTime) {
		this.sysCreatedTime = sysCreatedTime;
	}

	private long sysCreatedTime;

	public long getSysModifiedBy() {
		return sysModifiedBy;
	}

	public void setSysModifiedBy(long sysModifiedBy) {
		this.sysModifiedBy = sysModifiedBy;
	}

	private long sysModifiedBy;

	public long getSysCreatedBy() {
		return sysCreatedBy;
	}

	public void setSysCreatedBy(long sysCreatedBy) {
		this.sysCreatedBy = sysCreatedBy;
	}

	private long sysCreatedBy;
	public int getType() {
		if (type != null) {
			return type.getIntVal();
		}
		return -1;
	}
	public void setType(int val) {
		this.type = Type.getType(val);
	}
	public void setType(Type val) {
		this.type = val;
	}
	public Type getTypeEnum() {
		return this.type;
	}
	private Type type;
	public enum Type{
		FORM(1),
		SUB_FORM(2),
		APPROVAL_FORM(3),
		REJECTION_FORM(4),
		STATEFLOW_FORM(5),
		CUSTOM_BUTTON_FORM(6);
		private int intVal;
		public int getIntVal() {
			return intVal;
		}
		public void setIntVal(int intVal) {
			this.intVal = intVal;
		}
		private Type(int intVal) {
			this.intVal = intVal;
		}
		public static Type getType(int intVal) {
			return typeMap.get(intVal);
		}
		private static final Map<Integer, Type> typeMap = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, Type> initTypeMap() {
			Map<Integer, Type> typeMap = new HashMap<>();
			for (Type type: values()) {
				typeMap.put(type.intVal, type);
			}
			return typeMap;
		}
	}

	public SharingContext<SingleSharingContext> getFormSharing() {
		return formSharing;
	}

	public void setFormSharing(SharingContext<SingleSharingContext> formSharing) {
		this.formSharing = formSharing;
	}

	private SharingContext<SingleSharingContext> formSharing;
	private LabelPosition labelPosition;
	
	public enum LabelPosition {
		TOP(1, "top"),
		LEFT(2, "left"),
		RIGHT(3, "right");
		
		private int intVal;
		public int getIntVal() {
			return intVal;
		}

		public void setIntVal(int intVal) {
			this.intVal = intVal;
		}

		private String stringVal;
		
		private LabelPosition(int intVal, String stringVal) {
			this.intVal = intVal;
			this.setStringVal(stringVal);
		}
		
		public static LabelPosition getLabelPosition(int intVal) {
			return typeMap.get(intVal);
		}
		
		private static final Map<Integer, LabelPosition> typeMap = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, LabelPosition> initTypeMap() {
			Map<Integer, LabelPosition> typeMap = new HashMap<>();
			for (LabelPosition type: values()) {
				typeMap.put(type.intVal, type);
			}
			return typeMap;
		}

		public String getStringVal() {
			return stringVal;
		}

		public void setStringVal(String stringVal) {
			this.stringVal = stringVal;
		}
	} 
	
	private String appLinkName;
	private long appId = -1;
	public String getAppLinkName() {
		return appLinkName;
	}

	public void setAppLinkName(String appLinkName) {
		this.appLinkName = appLinkName;
	}
	private List<String> appLinkNamesForForm;
	public List<String> getAppLinkNamesForForm() {
		return appLinkNamesForForm;
	}
	public void setAppLinkNamesForForm(List<String> appLinkNamesForForm) {
		this.appLinkNamesForForm = appLinkNamesForForm;
	}

	private List<FormRuleContext> defaultFormRules;
	public List<FormRuleContext> getDefaultFormRules() {
		return defaultFormRules;
	}
	public void setDefaultFormRules(List<FormRuleContext> defaultFormRules) {
		this.defaultFormRules = defaultFormRules;
	}

	public long getAppId() {
		return appId;
	}

	public void setAppId(long appId) {
		this.appId = appId;
	}

	private Boolean primaryForm;

	private Boolean isSystemForm;
	public Boolean getPrimaryForm() {
		return primaryForm;
	}

	public void setPrimaryForm(Boolean primaryForm) {
		this.primaryForm = primaryForm;
	}

	public Boolean getIsSystemForm() {
		return isSystemForm;
	}

	public void setIsSystemForm(Boolean isSystemForm) {
		this.isSystemForm = isSystemForm;
	}

	public enum FormSourceType {
		FROM_BUILDER,
		FROM_FORM,
		FROM_BULK_FORM,
		;

		public Integer getIndex() {
			return ordinal() + 1;
		}

		public String getValue() {
			return name();
		}

		public static FormSourceType valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
	
	
	
}
