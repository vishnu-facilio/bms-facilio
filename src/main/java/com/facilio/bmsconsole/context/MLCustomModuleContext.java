package com.facilio.bmsconsole.context;

import java.util.List;
import java.util.Map;

import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;

public class MLCustomModuleContext extends ModuleBaseWithCustomFields {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	private String modelPath;
	private Boolean moduleNeeded;
	private String moduleName;
	private Map<String, List<String>> fields;
	private String parentModule;
	private List<String> parentFields;
	private String type;
	
	/**
	 * Internal usage variables
	 */
	private List<FacilioField> requestFields;
	private long mlReadingModuleId;
	private long mlId;
	
	
	public String getModelPath() {
		return modelPath;
	}
	
	public void setModelPath(String modelPath) {
		this.modelPath = modelPath;
	}
	
	public Boolean getModuleNeeded() {
		return moduleNeeded;
	}
	
	public void setModuleNeeded(Boolean moduleNeeded) {
		this.moduleNeeded = moduleNeeded;
	}
	
	public String getModuleName() {
		return moduleName;
	}
	
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	public Map<String, List<String>> getFields() {
		return fields;
	}
	
	public void setFields(Map<String, List<String>> fields) {
		this.fields = fields;
	}

	public String getParentModule() {
		return parentModule;
	}

	public void setParentModule(String parentModule) {
		this.parentModule = parentModule;
	}

	public List<FacilioField> getRequestFields() {
		return requestFields;
	}

	public void setRequestFields(List<FacilioField> requestFields) {
		this.requestFields = requestFields;
	}

	public long getMlReadingModuleId() {
		return mlReadingModuleId;
	}

	public void setMlReadingModuleId(long mlReadingModuleId) {
		this.mlReadingModuleId = mlReadingModuleId;
	}

	public long getMlId() {
		return mlId;
	}

	public void setMlId(long mlId) {
		this.mlId = mlId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<String> getParentFields() {
		return parentFields;
	}

	public void setParentFields(List<String> parentFields) {
		this.parentFields = parentFields;
	}

	@Override
	public String toString() {
		return "MLCustomModuleContext [modelPath=" + modelPath + ", moduleNeeded=" + moduleNeeded + ", moduleName="
				+ moduleName + ", fields=" + fields + ", parentModule=" + parentModule + ", parentFields="
				+ parentFields + ", type=" + type + ", requestFields=" + requestFields + ", mlReadingModuleId="
				+ mlReadingModuleId + ", mlId=" + mlId + "]";
	}
	
}