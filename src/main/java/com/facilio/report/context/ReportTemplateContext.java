package com.facilio.report.context;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;

public class ReportTemplateContext {

	private Long categoryId;
	private Long defaultValue;
	private List<String> chooseValues;
	private Long parentId;
	
	public Long getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}
	public Long getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(Long defaultValue) {
		this.defaultValue = defaultValue;
	}
	public List<String> getChooseValues() {
		return chooseValues;
	}
	public void setChooseValues(List<String> chooseValues) {
		this.chooseValues = chooseValues;
	}
	public Long getParentId() {
		return parentId;
	}
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	
	public Criteria getCriteria(ReportContext report) throws Exception{
		Criteria c = new Criteria ();
		
		FacilioModule facilioModule = report.getModule();
		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioField parentField = null;
		
		List<FacilioField> fields = bean.getAllFields(facilioModule.getName());
		
		for(FacilioField field : fields) {
			if(field.getName().equals("parentId")) {
				parentField = field;
				break;
			}
		}
		
		if(parentId != null) {
			Condition condition = CriteriaAPI.getCondition(parentField, Collections.singletonList(parentId), NumberOperators.EQUALS);
			c.addAndCondition(condition);
			return c;
		}
		
		return null;
	}
}
