package com.facilio.report.context;

import org.json.simple.JSONObject;

import com.facilio.db.criteria.Criteria;
import com.facilio.modules.AggregateOperator;

public class ReportPivotTableDataContext {
	private AggregateOperator aggr;
	public AggregateOperator getAggrEnum() {
		return aggr;
	}
	public void setAggr(AggregateOperator aggr) {
		this.aggr = aggr;
	}
	public int getAggr() {
		if (aggr != null) {
			return aggr.getValue();
		}
		return -1;
	}
	public void setAggr(int aggr) {
		this.aggr = AggregateOperator.getAggregateOperator(aggr);
	}
	
	private ReportPivotFieldContext field;

	public ReportPivotFieldContext getField() {
		return field;
	}
	public void setField(ReportPivotFieldContext field) {
		this.field = field;
	}
	
	private ReportPivotFieldContext readingField;
	public ReportPivotFieldContext getReadingField(){
		return readingField;
	}
	public void setReadingField(ReportPivotFieldContext readingField) {
		this.readingField = readingField;
		
	}
	
	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

    private String moduleType;
    public String getModuleType(){
        return moduleType;
    }
    public void setModuleType(String moduleType){
        this.moduleType = moduleType;
    }
    
    private Criteria criteria;
	public Criteria getCriteria() {
		return criteria;
	}
	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}
	
	private Integer datePeriod; 
	public Integer getDatePeriod() {
		return datePeriod;
	}
	public void setDatePeriod(Integer datePeriod) {
		this.datePeriod = datePeriod;
	}
	
	private long dateFieldId = -1;

	public long getDateFieldId() {
		return dateFieldId;
	}

	public void setDateFieldId(long dateFieldId) {
		this.dateFieldId = dateFieldId;
	}
	
	private JSONObject formatting;

	public JSONObject getFormatting() {
		return formatting;
	}
	public void setFormatting(JSONObject formatting) {
		this.formatting = formatting;
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


	private long parentModuleId = -1;
	public long getParentModuleId() {
		return parentModuleId;
	}

	public void setParentModuleId(long parentModuleId) {
		this.parentModuleId = parentModuleId;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return getField()+"";
	}
}