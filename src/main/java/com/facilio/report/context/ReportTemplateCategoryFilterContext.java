package com.facilio.report.context;

import java.util.List;

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
}
