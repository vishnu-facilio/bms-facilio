package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.db.criteria.CommonOperators;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportFilterContext;

public class FilterFieldCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
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
		return false;
	}

}
