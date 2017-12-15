package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.List;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class DashboardContext extends ModuleBaseWithCustomFields implements Comparable<DashboardContext> {

	private String dashboardName;

	public String getDashboardName() {
		return dashboardName;
	}
	public void setDashboardName(String dashboardName) {
		this.dashboardName = dashboardName;
	}
	@Override
	public int compareTo(DashboardContext o) {
		if(this.getId() < o.getId()) {
			return -1;
		}
		else if(this.getId() > o.getId()) {
			return 1;
		}
		return 0;
	}
}
