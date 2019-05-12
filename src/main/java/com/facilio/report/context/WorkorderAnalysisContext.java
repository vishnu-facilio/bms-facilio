package com.facilio.report.context;

import com.facilio.modules.AggregateOperator;
import com.facilio.db.criteria.Criteria;

import java.util.List;

public class WorkorderAnalysisContext {
	private long xFieldId = -1;
	public long getxFieldId() {
		return xFieldId;
	}
	public void setxFieldId(long xFieldId) {
		this.xFieldId = xFieldId;
	}
	
	private long yFieldId = -1;
	public long getyFieldId() {
		return yFieldId;
	}
	public void setyFieldId(long yFieldId) {
		this.yFieldId = yFieldId;
	}
	
	private AggregateOperator yAggr;
	public AggregateOperator getyAggrEnum() {
		return yAggr;
	}
	public void setyAggr(AggregateOperator yAggr) {
		this.yAggr = yAggr;
	}
	public int getyAggr() {
		if (yAggr != null) {
			return yAggr.getValue();
		}
		return -1;
	}
	public void setyAggr(int yAggr) {
		this.yAggr = AggregateOperator.getAggregateOperator(yAggr);
	}
	
	private List<Long> groupBy;
	public List<Long> getGroupBy() {
		return groupBy;
	}
	public void setGroupBy(List<Long> groupBy) {
		this.groupBy = groupBy;
	}
	
	private long dateFieldId = -1;
	public long getDateFieldId() {
		return dateFieldId;
	}
	public void setDateFieldId(long dateFieldId) {
		this.dateFieldId = dateFieldId;
	}
	
	private Criteria criteria;
	public Criteria getCriteria() {
		return criteria;
	}
	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}
}
