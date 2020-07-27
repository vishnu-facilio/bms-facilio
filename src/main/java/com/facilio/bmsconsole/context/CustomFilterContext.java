package com.facilio.bmsconsole.context;

import com.facilio.db.criteria.Criteria;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class CustomFilterContext extends ModuleBaseWithCustomFields{

	private String name;
	
	private Long viewId;

	private Long criteriaId;
	
	private String filters;
	
	private Criteria criteria;
	

	public Criteria getCriteria() {
		return criteria;
	}

	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getViewId() {
		return viewId;
	}

	public void setViewId(Long viewId) {
		this.viewId = viewId;
	}

	public String getFilters() {
		return filters;
	}

	public void setFilters(String filters) {
		this.filters = filters;
	}
	
	public Long getCriteriaId() {
		return criteriaId;
	}

	public void setCriteriaId(Long criteriaId) {
		this.criteriaId = criteriaId;
	}
}
