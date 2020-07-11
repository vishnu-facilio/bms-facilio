package com.facilio.bmsconsole.context;

import java.util.List;

import org.apache.struts2.json.annotations.JSON;

import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class DashboardUserFilterContext extends ModuleBaseWithCustomFields {

	public static class DashboardUserFilter{ 
		 public enum ComponentType {
			SINGLE_SELECT,
			MULTI_SELECT;

			public static ComponentType valueOf(int value) {
				if (value > 0 && value <= values().length) {
					return values()[value - 1];
				}
				return null;
			}

			public int getValue() {
				return ordinal() + 1;
			}
		}
		 String label;

		long fieldId;

		ComponentType componentType;

		Boolean isOthersOptionEnabled;

		List<Long> selectedOptions;

		public ComponentType getComponentType() {
			return componentType;
		}

		public long getFieldId() {
			return fieldId;
		}

		public Boolean getIsOthersOptionEnabled() {
			return isOthersOptionEnabled;
		}

		public String getLabel() {
			return label;
		}

		public List<Long> getSelectedOptions() {
			return selectedOptions;
		}

		 public void setComponentType(ComponentType componentType) {
			this.componentType = componentType;
		}
		 public void setFieldId(long fieldId) {
			this.fieldId = fieldId;
		}
		 public void setIsOthersOptionEnabled(Boolean isOthersOptionEnabled) {
			this.isOthersOptionEnabled = isOthersOptionEnabled;
		}
		 public void setLabel(String label) {
			this.label = label;
		}
		 
			public void setSelectedOptions(List<Long> selectedOptions) {
				this.selectedOptions = selectedOptions;
			} 
		 
	 }
	
	
	private static final long serialVersionUID = 1L;	
	
	
	private long dashboardFilterId;


	private String dashboardUserFilterJson;
	
	private DashboardUserFilter dashboardUserFilter; 

	public long getDashboardFilterId() {
		return dashboardFilterId;
	}

	public DashboardUserFilter getDashboardUserFilter() {
		return dashboardUserFilter;
	}

	@JSON(serialize = false)
	public String getDashboardUserFilterJson() throws Exception {
	
		if(dashboardUserFilter!=null)
		{
		//	FieldUtil.getAsBeanFromJson(this.dashboardUserFilterJson, DashboardUserFilterContext.DashboardUserFilter.class);
			return FieldUtil.getAsJSON(dashboardUserFilter).toJSONString();
		}
		else {
		return null;
		}
	}

	public void setDashboardFilterId(long dashboardFilterId) {
		this.dashboardFilterId = dashboardFilterId;
	}


	public void setDashboardUserFilter(DashboardUserFilter dashboardUserFilter) {
		this.dashboardUserFilter = dashboardUserFilter;
	}
	

	 public void setDashboardUserFilterJson(String dashboardUserFilterJson) throws Exception{
		this.dashboardUserFilterJson = dashboardUserFilterJson;
		if(dashboardUserFilterJson!=null)
		{
			this.dashboardUserFilter=FieldUtil.getAsBeanFromJson(FieldUtil.getAsJSON(dashboardUserFilterJson),DashboardUserFilterContext.DashboardUserFilter.class);
		}
	}
	
	 
}
