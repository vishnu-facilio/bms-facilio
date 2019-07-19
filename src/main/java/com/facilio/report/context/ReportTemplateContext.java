package com.facilio.report.context;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;

public class ReportTemplateContext {

	private Boolean show;
	private Long categoryId;
	private Long siteId;
	private Long buildingId;
	private Long defaultValue;
	private List<Object> chooseValues;
	private Long parentId;
	
	private Criteria criteria;
	
	public Criteria getCriteria() {
		return criteria;
	}
	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}
	public Boolean getShow() {
		return show;
	}
	public void setShow(Boolean show) {
		this.show = show;
	}
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
	
	public List<Object> getChooseValues() {
		return chooseValues;
	}
	public void setChooseValues(List<Object> chooseValues) {
		this.chooseValues = chooseValues;
	}
	public Long getParentId() {
		return parentId;
	}
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	
	public Long getSiteId() {
		return siteId;
	}
	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}
	public Long getBuildingId() {
		return buildingId;
	}
	public void setBuildingId(Long buildingId) {
		this.buildingId = buildingId;
	}
	public Criteria getCriteria(ReportContext report) throws Exception{
		Criteria c = new Criteria ();
		
		FacilioModule facilioModule = report.getModule();
		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		// Assumption that x Axis module has parentId
		if(facilioModule == null) {
			facilioModule = report.getDataPoints().get(0).getxAxis().getModule();
		}
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
