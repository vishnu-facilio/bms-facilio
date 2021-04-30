package com.facilio.report.context;

import org.apache.struts2.json.annotations.JSON;

import com.facilio.modules.FacilioIntEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class ReportSettings  {
	/**
	 * 
	 */
	public static ReportSettings getDefaultReportSettings()
	{
		ReportSettings settings=new ReportSettings();
		settings.setClickAction(ClickAction.LIST);
		settings.setIsShowListAtDrillEnd(true);
		return settings;
		
	}
	private static final long serialVersionUID = 1L;

	private   Boolean isShowListAtDrillEnd;

	public Boolean getIsShowListAtDrillEnd() {
		return isShowListAtDrillEnd;
	}
	
	public void setIsShowListAtDrillEnd(Boolean isShowListAtDrillEnd) {
		this.isShowListAtDrillEnd = isShowListAtDrillEnd;
	}
	//isPrefixedGetter needed for boolean field in serialize to JSON with jackson obj mapper
	@JSON(serialize = false)
	
	public boolean isShowListAtDrillEnd()
	{
		if(this.isShowListAtDrillEnd!=null)
		{
			return this.isShowListAtDrillEnd.booleanValue();
		}
		else {
			return false;
			}
		
	}

	private ClickAction clickAction;
	
	
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

	

	public static enum ClickAction implements FacilioIntEnum {
		NONE("None"), LIST("List"),DRILLDOWN("Drilldown");

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
		public Integer getIndex() {
			return ordinal() + 1;
		}

		@Override
		public String getValue() {
			return this.name;
		}

	}
}
