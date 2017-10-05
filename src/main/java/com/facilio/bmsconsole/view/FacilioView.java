package com.facilio.bmsconsole.view;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.modules.FacilioField;

public class FacilioView {
	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
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
	
	private ViewType type;
	public int getType() {
		if(type != null) {
			return type.getIntVal();
		}
		return -1;
	}
	public ViewType getTypeEnum() {
		return type;
	}
	public void setType(int type) {
		this.type = ViewType.TYPE_MAP.get(type);
	}
	public void setType(ViewType type) {
		this.type = type;
	}
	
	private long moduleId = -1;
	public long getModuleId() {
		return moduleId;
	}
	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}
	
	private long criteriaId = -1;
	public long getCriteriaId() {
		return criteriaId;
	}
	public void setCriteriaId(long criteriaId) {
		this.criteriaId = criteriaId;
	}
	
	private Criteria criteria = null;
	public Criteria getCriteria() {
		return criteria;
	}
	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}
	
	private List<FacilioField> fields;
	public List<FacilioField> getFields() {
		return fields;
	}
	public void setFields(List<FacilioField> fields) {
		this.fields = fields;
	}
	
	public static enum ViewType {
		TABLE_LIST(1);
		
		private int intVal;
		
		private ViewType(int val) {
			// TODO Auto-generated constructor stub
			this.intVal = val;
		}
		
		public int getIntVal() {
			return intVal;
		}
		
		private static final Map<Integer, ViewType> TYPE_MAP = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, ViewType> initTypeMap() {
			Map<Integer, ViewType> typeMap = new HashMap<>();
			for(ViewType type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}
	}
}
