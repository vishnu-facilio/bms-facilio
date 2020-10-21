package com.facilio.bmsconsole.context;

import java.util.List;
import java.util.Map;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class DashboardCustomScriptFilter extends ModuleBaseWithCustomFields{
	
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


public List<DashboardUserFilterContext> getFilterMeta() {
	return filterMeta;
}
public void setFilterMeta(List<DashboardUserFilterContext> filterMeta) {
	this.filterMeta = filterMeta;
}
public Map<Long, List<String>> getFilterValues() {
	return filterValues;
}
public void setFilterValues(Map<Long, List<String>> filterValues) {
	this.filterValues = filterValues;
}
private List<DashboardUserFilterContext> filterMeta;
private Map<Long, List<String>> filterValues;
}
