package com.facilio.report.context;

import java.util.Map;
import java.util.Set;

public interface TransformReportDataIfc {
	//TODO Change return type and remove vValues param when we move to new report format
	public Map<String, Map<String, Map<Object, Object>>> transformReportData (ReportContext report, Map<String, Map<String, Map<Object, Object>>> reportData, Set<Object> xValues);
}
