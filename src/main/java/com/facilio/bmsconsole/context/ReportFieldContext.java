package com.facilio.bmsconsole.context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.FormulaContext.AggregateOperator;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.fw.BeanFactory;

public class ReportFieldContext {

	Long id;
	Long moduleFieldId;
	Long formulaFieldId;
	Integer aggregateFunction;
	ReportFormulaFieldContext reportFormulaContext;
	FacilioField moduleField;
	Boolean isFormulaField;
	
	public Boolean getIsFormulaField() {
		if(isFormulaField != null) {
			return isFormulaField;
		}
		return formulaFieldId != null;
	}
	public FacilioField getField() throws Exception {
		if(getIsFormulaField()) {
			FacilioField facilioField = new FacilioField();
			facilioField.setName("value");
			facilioField.setColumnName(reportFormulaContext.getFormula());
			facilioField.setDataType(FieldType.NUMBER);
			
			return facilioField;
		}
		return getModuleField();
	}
	public void setIsFormulaField(Boolean isFormulaField) {
		this.isFormulaField = isFormulaField;
	}
	public ReportFormulaFieldContext getReportFormulaContext() {
		return reportFormulaContext;
	}
	public void setReportFormulaContext(ReportFormulaFieldContext reportFormulaContext) {
		this.reportFormulaContext = reportFormulaContext;
	}
	public FacilioField getModuleField() throws Exception {
		
		if(moduleField == null) {
			if(moduleFieldId == null) {
				return null;
			}
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			moduleField = modBean.getField(moduleFieldId);
		}
		return moduleField;
	}
	public void setModuleField(FacilioField moduleField) {
		this.moduleField = moduleField;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getModuleFieldId() {
		return moduleFieldId;
	}
	public void setModuleFieldId(Long moduleFieldId) {
		this.moduleFieldId = moduleFieldId;
	}
	public Long getFormulaFieldId() {
		return formulaFieldId;
	}
	public void setFormulaFieldId(Long formulaFieldId) {
		this.formulaFieldId = formulaFieldId;
	}
	public Integer getAggregateFunction() {
		return aggregateFunction;
	}
	public void setAggregateFunction(Integer aggregateFunction) {
		this.aggregateFunction = aggregateFunction;
	}
	public AggregateOperator getAggregateOpperator() {
		if (getAggregateFunction() != null) {
			return AggregateOperator.getAggregateOperator(getAggregateFunction());
		}
		else {
			return AggregateOperator.COUNT;
		}
	}
}
