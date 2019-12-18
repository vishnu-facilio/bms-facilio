package com.facilio.report.context;

import java.util.Collections;
import java.util.List;

import com.facilio.beans.ModuleBean;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

public class ReportTemplateCategoryFilterContext {
	private Long categoryId;
	private Long parentId;
	private List<Object> applyTo;
	public Long getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}
	public Long getParentId() {
		return parentId;
	}
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	public List<Object> getApplyTo() {
		return applyTo;
	}
	public void setApplyTo(List<Object> applyTo) {
		this.applyTo = applyTo;
	}
	
	public Criteria getCriteria(ReportDataPointContext dp) throws Exception{
		Criteria c = new Criteria ();
		FacilioModule facilioModule = dp.getxAxis().getModule();
		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		// Assumption that x Axis module has parentId
		
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
