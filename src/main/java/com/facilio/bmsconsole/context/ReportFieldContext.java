package com.facilio.bmsconsole.context;

import com.facilio.beans.ModuleBean;
import com.facilio.modules.fields.FacilioField;
import com.facilio.fw.BeanFactory;

public class ReportFieldContext {

	Long id;
	String fieldLabel;
	Long moduleFieldId;
	Long formulaFieldId;
	ReportFormulaFieldContext reportFormulaContext;
	FacilioField moduleField;
	Boolean isFormulaField;
	String unit;
	
	public Boolean getIsFormulaField() {
		if(isFormulaField != null) {
			return isFormulaField;
		}
		return formulaFieldId != null;
	}
	public FacilioField getField() throws Exception {
		if(getIsFormulaField()) {
			FacilioField facilioField = new FacilioField();
			facilioField.setName(reportFormulaContext.getName());
			facilioField.setColumnName(reportFormulaContext.getFormula());
//			facilioField.setModule(reportFormulaContext.getModule());
			if(reportFormulaContext.getDataType() != null) {
				facilioField.setDataType(reportFormulaContext.getDataType());
			}
			
			return facilioField;
		}
		return getModuleField();
	}
	
	public void setFieldLabel(String fieldLabel) {
		this.fieldLabel = fieldLabel;
	}
	
	public String getFieldLabel() throws Exception {
		if (this.fieldLabel == null && this.getField() != null) {
			return this.getField().getDisplayName();
		}
		return this.fieldLabel;
	}
	
	public void setUnit(String unit) {
		this.unit = unit;
	}
	
	public String getUnit() {
		return this.unit;
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
			moduleField = modBean.getFieldFromDB(moduleFieldId);
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
}
