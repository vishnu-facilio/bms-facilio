package com.facilio.bmsconsole.context;

import com.facilio.beans.ModuleBean;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class ReportFormulaFieldContext extends ModuleBaseWithCustomFields {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String name;
	Integer dataType;
	String formula;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getDataType() {
		return dataType;
	}
	public void setDataType(Integer dataType) {
		this.dataType = dataType;
	}
	public String getFormula() {
		return formula;
	}
	public void setFormula(String formula) {
		this.formula = formula;
	}
	public FacilioModule getModule() throws Exception {
		if(super.getModuleId() > 0 ) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			return modBean.getModule(super.getModuleId());
		}
		return null;
	}
}
