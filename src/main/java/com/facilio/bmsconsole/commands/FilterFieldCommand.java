package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.criteria.CommonOperators;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.StringOperators;
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
						
						filter.setFilterOperator(StringOperators.IS); //This is a hack for now
						filter.setFilterValue(joiner.toString());
					}
				}
			}
		}
		return false;
	}

}
