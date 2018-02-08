package com.facilio.bmsconsole.context;

public class ExpressionContext {

	Long id;
	Long orgId;
	String expressionString;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getOrgId() {
		return orgId;
	}
	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}
	public String getExpressionString() {
		return expressionString;
	}
	public void setExpressionString(String expressionString) {
		this.expressionString = expressionString;
	}
}
