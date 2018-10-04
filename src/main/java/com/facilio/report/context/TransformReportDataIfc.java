package com.facilio.report.context;

import java.util.Map;
import java.util.Set;

public interface TransformReportDataIfc {
	//TODO Change return type and remove vValues param when we move to new report format
	public void transformReportData (ReportContext report, Map<String, Map<String, Map<Object, Object>>> reportData, Set<Object> xValues);
	
	public Map<String, Map<String, Map<Object, Object>>> getReportData();
	
	public Set<Object> getxValues();
}
