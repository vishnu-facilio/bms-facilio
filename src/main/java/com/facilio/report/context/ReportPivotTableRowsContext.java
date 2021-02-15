package com.facilio.report.context;

import java.util.HashMap;

import org.json.simple.JSONObject;

import com.facilio.modules.AggregateOperator;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.modules.fields.BooleanField;
import com.facilio.modules.fields.EnumField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class ReportPivotTableRowsContext {
	
	private long lookupFieldId = -1;
	public long getLookupFieldId() {
		return lookupFieldId;
	}
	public void setLookupFieldId(long lookupFieldId) {
		this.lookupFieldId = lookupFieldId;
	}
	
	private FacilioField field;

	public FacilioField getField() {
		return field;
	}
	public void setField(FacilioField field) {
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
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return getField()+"";
	}
}
