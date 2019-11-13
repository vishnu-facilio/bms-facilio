package com.facilio.bmsconsole.forms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.struts2.json.annotations.JSON;

import com.facilio.modules.FacilioModule;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class FacilioForm implements Serializable {
	public FacilioForm () {}

	public FacilioForm(long id, long orgId, String name, String displayName, long moduleId, List<FormField> fields, FacilioModule module, FormType formType, LabelPosition labelPosition) {
		this.id = id;
		this.orgId = orgId;
		this.name = name;
		this.displayName = displayName;
		this.moduleId = moduleId;
		this.fields = fields;
		this.module = module;
		this.formType = formType;
		this.labelPosition = labelPosition;
		this.showInMobile = true;
	}

	public FacilioForm(FacilioForm form) {
		this(form.id, form.orgId, form.name, form.displayName, form.moduleId, new ArrayList<>(form.fields), form.module, form.formType, form.labelPosition);
		this.showInMobile = form.showInMobile;
		this.hideInList = form.hideInList;
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
	
	private FormType formType;
	
	public void setFormType(FormType formType) {
		this.formType = formType;
	}
	
	public int getFormType() {
		if (this.formType != null) {
			return this.formType.getIntVal();
		}
		return -1;
	}
	
	public void setFormType(int val) {
		this.formType = FormType.getFormType(val);
	}
	
	public String getFormTypeVal() {
		if (this.formType != null) {
			return this.formType.stringVal;
		}
		return null;
	}
	
	public FormType getFormTypeEnum() {
		return this.formType;
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
		if (showInMobile == null) {
			return true;
		}
		return showInMobile;
	}
	public void setShowInMobile(Boolean showInMobile) {
		this.showInMobile = showInMobile;
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

	public enum FormType {
		WEB(1, "web"),
		MOBILE(2, "mobile"), // Will be removed
		PORTAL(3, "portal"),
		SERVICE_CATALOG(4, "portal"),
		;
		
		private int intVal;
		private String stringVal;
		
		public int getIntVal() {
			return this.intVal;
		}
		
		public String getStringVal() {
			return this.stringVal;
		}
		
		private FormType(int intVal, String stringVal) {
			this.intVal = intVal;
			this.stringVal = stringVal;
		}
		
		public static FormType getFormType(int intVal) {
			return typeMap.get(intVal);
		}
		
		private static final Map<Integer, FormType> typeMap = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, FormType> initTypeMap() {
			Map<Integer, FormType> typeMap = new HashMap<>();
			
			for(FormType type: values()) {
				typeMap.put(type.intVal, type);
			}
			
			return typeMap;
		}
	}
	
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
}
