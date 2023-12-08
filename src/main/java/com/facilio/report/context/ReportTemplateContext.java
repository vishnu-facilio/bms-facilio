package com.facilio.report.context;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.beans.ModuleBean;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;

@Getter @Setter
public class ReportTemplateContext {
	private Long templateType;
	private Boolean show;
	private Long categoryId;
	private Long siteId;
	private Long buildingId;
	private Long defaultValue;
	private List<Object> chooseValues;
	private Boolean isVisibleInDashBoard;
	private String categoryFillter;
	private List<ReportTemplateCategoryFilterContext> categoryFillter1;
	private Map<String, ReportTemplateCategoryFilterContext> categoryTemplate;
	public String getCategoryFillter() {
		return categoryFillter;
	}
	public void setCategoryFillter(String categoryFillter) {
		this.categoryFillter = categoryFillter;
	}
	public Long getTemplateType() {
		return templateType;
	}
	public void setTemplateType(Long templateType) {
		this.templateType = templateType;
	}
	public Boolean getIsVisibleInDashBoard() {
		return isVisibleInDashBoard;
	}
	public void setIsVisibleInDashBoard(Boolean isVisibleInDashBoard) {
		this.isVisibleInDashBoard = isVisibleInDashBoard;
	}
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
	public Criteria getCriteria(ReportContext report, ReportDataPointContext dp) throws Exception{
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
//	public List<ReportTemplateCategoryFilterContext> getCategoryFillter() {
//		return categoryFillter;
//	}
//	public void setCategoryFillter(List<ReportTemplateCategoryFilterContext> categoryFillter) {
//		this.categoryFillter = categoryFillter;
//	}
	public Map<String, ReportTemplateCategoryFilterContext> getCategoryTemplate() {
		return categoryTemplate;
	}
	public void setCategoryTemplate(Map<String, ReportTemplateCategoryFilterContext> categoryTemplate) {
		this.categoryTemplate = categoryTemplate;
	}
	public List<ReportTemplateCategoryFilterContext> getCategoryFillter1() {
		return categoryFillter1;
	}
	public void setCategoryFillter1(List<ReportTemplateCategoryFilterContext> categoryFillter1) {
		this.categoryFillter1 = categoryFillter1;
	}
	private String categoryName;
}
