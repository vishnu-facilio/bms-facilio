package com.facilio.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.control.ControlGroupSection.Section_Type;
import com.facilio.v3.context.V3Context;

public class ControlGroupAssetContext extends V3Context{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String name;
	ControlGroupContext controlGroup;
	ControlGroupAssetCategory controlGroupAssetCategory;
	ResourceContext asset;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ControlGroupContext getControlGroup() {
		return controlGroup;
	}
	public void setControlGroup(ControlGroupContext controlGroup) {
		this.controlGroup = controlGroup;
	}
	public ControlGroupAssetCategory getControlGroupAssetCategory() {
		return controlGroupAssetCategory;
	}
	public void setControlGroupAssetCategory(ControlGroupAssetCategory controlGroupAssetCategory) {
		this.controlGroupAssetCategory = controlGroupAssetCategory;
	}
	public ResourceContext getAsset() {
		return asset;
	}
	public void setAsset(ResourceContext asset) {
		this.asset = asset;
	}
	
	List<ControlGroupFieldContext> controlFields;

	public List<ControlGroupFieldContext> getControlFields() {
		return controlFields;
	}
	public void setControlFields(List<ControlGroupFieldContext> controlFields) {
		this.controlFields = controlFields;
	}
	public void addField(ControlGroupFieldContext field) {
		this.controlFields = this.controlFields == null ? new ArrayList<ControlGroupFieldContext>() : this.controlFields; 
		
		this.controlFields.add(field);
	}
	
	Status status;
	
	public Status getStatusEnum() {
		return status;
	}
	public void setStatusEnum(Status status) {
		this.status = status;
	}
	
	public int getStatus() {
		if(status != null) {
			status.getIntVal();
		}
		return -1;
	}
	public void setStatus(int status) {
		this.status = Status.getAllOptions().get(status);
	}
	
	
	public enum Status {
		
		ACTIVE(1, "Active"),
		CONTROL_PASSED_TO_CHILD(2, "ControlPassedToChild"),
		
		;

		int intVal;
		String name;

		public int getIntVal() {
			return intVal;
		}

		public String getName() {
			return name;
		}

		private Status(int intVal, String name) {
			this.intVal = intVal;
			this.name = name;
		}
		
		private static final Map<Integer, Status> optionMap = Collections.unmodifiableMap(initTypeMap());

		private static Map<Integer, Status> initTypeMap() {
			Map<Integer, Status> typeMap = new HashMap<>();

			for (Status type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}

		public static Map<Integer, Status> getAllOptions() {
			return optionMap;
		}
	}


}
