package com.facilio.report.context;

import org.json.simple.JSONObject;

import com.facilio.db.criteria.Criteria;
import com.facilio.modules.AggregateOperator;
import com.facilio.modules.fields.FacilioField;

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
	
	private FacilioField field;

	public FacilioField getField() {
		return field;
	}
	public void setField(FacilioField field) {
		this.field = field;
	}

    // public static enum DataModuleType {
	// 	MODULE,
	// 	READING
	// 	;
		
	// 	public int getValue() {
	// 		return ordinal() + 1;
	// 	}
		
	// 	public static DataModuleType valueOf (int value) {
	// 		if (value > 0 && value <= values().length) {
	// 			return values() [value - 1];
	// 		}
	// 		return null;
	// 	}
	// }

    // private DataModuleType moduleType = DataModuleType.MODULE;
	// public DataModuleType getModuleTypeEnum() {
	// 	return moduleType;
	// }
	// public void setModuleType(DataModuleType moduleType) {
	// 	this.moduleType = moduleType;
	// }
	// public int getModuleType() {
	// 	if (moduleType != null) {
	// 		return moduleType.getValue();
	// 	}
	// 	return -1;
	// }
	// public void setModuleType(int moduleType) {
	// 	this.moduleType = DataPointType.valueOf(moduleType);
	// }

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
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return getField()+"";
	}
}