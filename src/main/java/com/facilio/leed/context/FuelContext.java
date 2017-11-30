package com.facilio.leed.context;

public class FuelContext {

	private long fuelId;
	private String fuelType;
	private String subType;
	private String kind;
	private String resource;

	public long getFuelId() {
		return fuelId;
	}

	public void setFuelId(long fuelId) {
		this.fuelId = fuelId;
	}

	public String getFuelType() {
		return fuelType;
	}

	public void setFuelType(String fuelType) {
		this.fuelType = fuelType;
	}

	public String getSubType() {
		return subType;
	}

	public void setSubType(String subType) {
		this.subType = subType;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

}
