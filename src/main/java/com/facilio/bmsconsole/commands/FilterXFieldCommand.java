package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.criteria.CommonOperators;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.report.context.ReportContext;

public class FilterXFieldCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		if (report.getxCriteria() != null) {
			SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
																				.select(Collections.singletonList(report.getxCriteria().getxField()))
																				.module(report.getxCriteria().getxField().getModule())
																				.andCriteria(report.getxCriteria().getCriteria())
																				.andCondition(CriteriaAPI.getCondition(report.getxCriteria().getxField(), CommonOperators.IS_NOT_EMPTY))
																				;
			List<Map<String, Object>> props = selectBuilder.getAsProps();
			if (props != null && !props.isEmpty()) {
				StringJoiner joiner = new StringJoiner(",");
				for (Map<String, Object> prop : props) {
					joiner.add(prop.get(report.getxCriteria().getxField().getName()).toString());
				}
				context.put(FacilioConstants.ContextNames.REPORT_X_VALUES, joiner.toString());
			}
		}
		return false;
	}

}
