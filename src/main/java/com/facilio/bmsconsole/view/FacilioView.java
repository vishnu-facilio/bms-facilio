package com.facilio.bmsconsole.view;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.criteria.Criteria;

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
	
	private String moduleName = "";
	public String getModuleName() {
		return moduleName;
	}
	
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
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
	
	private Boolean isDefault;
	public Boolean getIsDefault() {
		return isDefault;
	}
	public void setDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}
	public boolean isDefault() {
		if(isDefault != null) {
			return isDefault.booleanValue();
		}
		return false;
	}
	
	private List<ViewField> fields;
	public List<ViewField> getFields() {
		return fields;
	}
	public void setFields(List<ViewField> fields) {
		this.fields = fields;
	}
	
	private long viewId;

	public long getViewId() {
		return viewId;
	}
	public void setViewId(long viewId) {
		this.viewId = viewId;
	}

	private List<SortField> sortFields;
	public List<SortField> getSortFields() {
		return sortFields;
	}
	public void setSortFields(List<SortField> sortFields) {
		this.sortFields = sortFields;
	}
	
	private Map<String, ViewField> defaultModulefields;
	public void setDefaultModuleFields(String moduleName, String parentViewName) {
		List<ViewField> fields = null;
		if(parentViewName != null && !parentViewName.isEmpty()) {
			fields = ColumnFactory.getColumns(moduleName, parentViewName);
		}
		else {
			fields = ColumnFactory.getColumns(moduleName, "default");
		}
		
 		if (fields != null && !fields.isEmpty()) {
			defaultModulefields = new HashMap<>();
			for(ViewField vf : fields) {
				defaultModulefields.put(vf.getName(),vf);
			}
		}
	}
	public Map<String, ViewField> getDefaultModuleFields(){
		return defaultModulefields;
	}
	
	private Map<String, Object> lookupFields;
	public void setLookupFields( Map<String, Object> lookupFields) {
		this.lookupFields = lookupFields;
	}
	public Map<String, Object> getLookupFields(){
		return lookupFields;
	}
	
	private JSONObject filters;
	public JSONObject getFilters() {
		return filters;
	}
	public void setFilters(JSONObject filters) {
		this.filters = filters;
	}
	public void setFiltersJson(String json) throws ParseException {
		this.filters = (JSONObject) new JSONParser().parse(json);
	}
	
	private String viewName;
	public String getViewName() {
		return viewName;
	}
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	private int sequenceNumber = -1;
	public int getSequenceNumber() {
		return sequenceNumber;
	}
	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
	public FacilioView setOrder(int order) {
		this.sequenceNumber = order;
		return this;
	}
	
	private Boolean isHidden;
	public Boolean getIsHidden() {
		return isHidden;
	}
	public void setHidden(Boolean isHidden) {
		this.isHidden = isHidden;
	}
	public boolean isHidden() {
		if(isHidden != null) {
			return isHidden.booleanValue();
		}
		return false;
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
