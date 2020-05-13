package com.facilio.energystar.context;

public class EnergyStarMeterPointContext {

	long id = -1;
	long orgId = -1;
	long meterId = -1;
	int pointId = -1;
	long fieldId = -1;
	int aggr = -1;
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	public long getMeterId() {
		return meterId;
	}
	public void setMeterId(long meterId) {
		this.meterId = meterId;
	}
	public int getPointId() {
		return pointId;
	}
	public void setPointId(int pointId) {
		this.pointId = pointId;
	}
	public long getFieldId() {
		return fieldId;
	}
	public void setFieldId(long fieldId) {
		this.fieldId = fieldId;
	}
	public int getAggr() {
		return aggr;
	}
	public void setAggr(int aggr) {
		this.aggr = aggr;
	}
}
