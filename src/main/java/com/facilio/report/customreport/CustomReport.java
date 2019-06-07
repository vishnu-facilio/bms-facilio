package com.facilio.report.customreport;

import com.facilio.bmsconsole.context.ReportContext;
import com.facilio.modules.FacilioModule;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public interface CustomReport {

	public JSONArray getData(ReportContext report, FacilioModule module, JSONArray dateFilter, JSONObject userFilterValues, long baseLineId, long criteriaId) throws Exception;
}
