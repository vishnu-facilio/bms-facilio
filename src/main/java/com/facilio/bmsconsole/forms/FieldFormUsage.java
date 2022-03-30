package com.facilio.bmsconsole.forms;

import java.io.Serializable;

public class FieldFormUsage implements Serializable {
	public FieldFormUsage() {}

	private long componentId;
	public long getComponentId() {
		return componentId;
	}
	public void setComponentId(long componentId) {
		this.componentId = componentId;
	}

	private FormType formType;
	public FormType getFormType() {
		return formType;
	}
	public void setFormType(FormType formType) {
		this.formType = formType;
	}

	private String componentName;
	public String getComponentName() {
		return componentName;
	}
	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}

	private String parentFeature;
	public String getParentFeature() {
		return parentFeature;
	}
	public void setParentFeature(String parentFeature) {
		this.parentFeature = parentFeature;
	}

	private String parentFeatureName;
	public String getParentFeatureName() {
		return parentFeatureName;
	}
	public void setParentFeatureName(String parentFeatureName) {
		this.parentFeatureName = parentFeatureName;
	}

	public enum FormType {
		TEMPLATE,
		CUSTOM_BUTTON,
		STATEFLOW_TRANSITION,
		APPROVAL_PROCESS
		;

		public Integer getIndex() {
			return ordinal() + 1;
		}

		public String getValue() {
			return name();
		}

		public static FormType valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
}
