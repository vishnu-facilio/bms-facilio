package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportFilterContext;
import org.json.simple.JSONObject;

public class FilterFieldCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		Criteria criteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
		if(criteria != null) {
			report.setCriteria(criteria);
		}
		if (report.getFilters() != null && !report.getFilters().isEmpty()) {
			for (ReportFilterContext filter : report.getFilters()) {
				if (!filter.isDataFilter()) {
					SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
																						.select(Collections.singletonList(filter.getField()))
																						.module(filter.getField().getModule())
																						.andCriteria(filter.getCriteria())
																						.andCondition(CriteriaAPI.getCondition(filter.getField(), CommonOperators.IS_NOT_EMPTY))
																						;
					List<Map<String, Object>> props = selectBuilder.getAsProps();
					if (props != null && !props.isEmpty()) {
						StringJoiner joiner = new StringJoiner(",");
						for (Map<String, Object> prop : props) {
							joiner.add(prop.get(filter.getField().getName()).toString());
						}
						filter.setFilterValue(joiner.toString());
					}
				}
			}
		}
		JSONObject ttimeFilter = (JSONObject) context.get(FacilioConstants.ContextNames.REPORT_TTIME_FILTER);
		if(report.getReportTTimeFilter() == null && ttimeFilter != null) {
			report.setReportTTimeFilter(ttimeFilter);
		}
		return false;
	}

}
