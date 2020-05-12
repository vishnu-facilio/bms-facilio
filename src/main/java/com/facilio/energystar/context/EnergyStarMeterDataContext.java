package com.facilio.energystar.context;

import org.apache.struts2.json.annotations.JSON;

import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.time.DateTimeUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class EnergyStarMeterDataContext extends ReadingContext {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String energyStarId;

	public String getEnergyStarId() {
		return energyStarId;
	}

	public void setEnergyStarId(String energyStarId) {
		this.energyStarId = energyStarId;
	}
	
	@JsonIgnore
	@JSON(serialize=false)
	public String getFormatedStartTime() {
		return DateTimeUtil.getFormattedTime((long)this.getDatum("fromDate"), "yyyy-MM-dd");
	}
	
	@JsonIgnore
	@JSON(serialize=false)
	public String getFormatedEndTime() {
		return DateTimeUtil.getFormattedTime((long)this.getDatum("toDate"), "yyyy-MM-dd");
	}
}
