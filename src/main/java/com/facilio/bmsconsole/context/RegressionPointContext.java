package com.facilio.bmsconsole.context;

public class RegressionPointContext {

	String axis;
	String alias;
	long aggr;
	long parentId;
	long readingId;
	
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getAxis() {
		return axis;
	}
	public void setAxis(String axis) {
		this.axis = axis;
	}
	public long getAggr() {
		return aggr;
	}
	public void setAggr(long aggr) {
		this.aggr = aggr;
	}
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	public long getReadingId() {
		return readingId;
	}
	public void setReadingId(long readingId) {
		this.readingId = readingId;
	}
}
