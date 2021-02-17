package com.facilio.report.context;

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
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return getField()+"";
	}
}
