package com.facilio.report.context;

import com.facilio.db.criteria.Criteria;
import org.json.simple.JSONObject;

public class ReportPivotTableRowsContext {
	
	private long lookupFieldId = -1;
	public long getLookupFieldId() {
		return lookupFieldId;
	}
	public void setLookupFieldId(long lookupFieldId) {
		this.lookupFieldId = lookupFieldId;
	}
	
	private ReportPivotFieldContext field;

	public ReportPivotFieldContext getField() {
		return field;
	}
	public void setField(ReportPivotFieldContext field) {
		this.field = field;
	}
	
	private JSONObject formatting;

	public JSONObject getFormatting() {
		return formatting;
	}
	public void setFormatting(JSONObject formatting) {
		this.formatting = formatting;
	}
	
	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	private String alias; 
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}

	private long subModuleFieldId = -1;
	public long getSubModuleFieldId() {
		return subModuleFieldId;
	}
	public void setSubModuleFieldId(long subModuleFieldId) {
		this.subModuleFieldId = subModuleFieldId;
	}

	private Criteria criteria;
	public Criteria getCriteria() {
		return criteria;
	}
	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}

	private int sortOrder;
	public int getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	private ReportPivotFieldContext sortField;
	public ReportPivotFieldContext getSortField() {
		return sortField;
	}

	public void setSortField(ReportPivotFieldContext sortField) {
		this.sortField = sortField;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return getField()+"";
	}
}
