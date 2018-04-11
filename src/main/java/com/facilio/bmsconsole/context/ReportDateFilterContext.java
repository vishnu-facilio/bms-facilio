package com.facilio.bmsconsole.context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.Operator;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.fw.BeanFactory;

public class ReportDateFilterContext {

	private Long id;
	private Long reportId;
	private Long fieldId;
	private FacilioField field;
	private Integer operatorId;
	private String value;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getReportId() {
		return reportId;
	}
	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}
	public Long getFieldId() {
		if (this.fieldId == null && this.field != null) {
			try {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				this.fieldId = modBean.getField(this.field.getName(), this.field.getModule().getName()).getId();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return fieldId;
	}
	public void setFieldId(Long fieldId) {
		this.fieldId = fieldId;
	}
	public FacilioField getField() throws Exception {
		if(field == null && fieldId != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			this.field = modBean.getField(fieldId);
		}
		return field;
	}
	public void setField(FacilioField field) {
		this.field = field;
	}
	public DateOperators getOperator() {
		return (DateOperators) Operator.OPERATOR_MAP.get(operatorId);
	}
	public Integer getOperatorId() {
		return operatorId;
	}
	public void setOperatorId(Integer operatorId) {
		this.operatorId = operatorId;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
