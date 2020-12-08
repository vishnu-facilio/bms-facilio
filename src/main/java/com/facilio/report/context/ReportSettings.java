package com.facilio.report.context;

import com.facilio.modules.FacilioEnum;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class ReportSettings  {
	/**
	 * 
	 */
	public static ReportSettings getDefaultReportSettings()
	{
		ReportSettings settings=new ReportSettings();
		settings.setClickAction(ClickAction.LIST);
		settings.setShowListAtDrillEnd(true);
		return settings;
		
	}
	private static final long serialVersionUID = 1L;

	private ClickAction clickAction;
	
	private Boolean showListAtDrillEnd;
	
	@JsonIgnore
	public ClickAction getClickActionEnum() {
		return clickAction;
	}
	public int getClickAction()
	{
		if(this.clickAction!=null)
		{
			return this.clickAction.getIndex();
		}
		return -1;
	}

	
	public void setClickAction(ClickAction clickAction) {
		this.clickAction = clickAction;
	}
	public void setClickAction(int value)
	{
		this.clickAction=ClickAction.valueOf(value);
	}

	public Boolean getShowListAtDrillEnd() {
		return showListAtDrillEnd;
	}

	public void setShowListAtDrillEnd(Boolean showListAtDrillEnd) {
		this.showListAtDrillEnd = showListAtDrillEnd;
	}

	public static enum ClickAction implements FacilioEnum {
		NONE("None"), LIST("Some"),DRILLDOWN("Drilldown");

		public static ClickAction valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}

		private String name;

		ClickAction(String name) {
			this.name = name;
		}

		@Override
		public int getIndex() {
			return ordinal() + 1;
		}

		@Override
		public String getValue() {
			return this.name;
		}

	}
}
