package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.FormulaContext.DateAggregateOperator;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

public class ValidateCalendarWOCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String dateFieldName = (String) context.get(FacilioConstants.ContextNames.DATE_FIELD);
		if (StringUtils.isEmpty(dateFieldName)) {
			throw new IllegalArgumentException("Date Field cannot be empty");
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField dateField = modBean.getField(dateFieldName, FacilioConstants.ContextNames.WORK_ORDER);
		if (dateField == null) {
			throw new IllegalArgumentException("Invalid date field");
		}
		context.put(FacilioConstants.ContextNames.DATE_FIELD, dateField);
		
		long startTime = (long) context.get(FacilioConstants.ContextNames.START_TIME);
		if (startTime == -1) {
			throw new IllegalArgumentException("Start Time cannot be empty");
		}
		long endTime = (long) context.get(FacilioConstants.ContextNames.END_TIME);
		if (endTime == -1) {
			throw new IllegalArgumentException("End time cannot be empty");
		}
		if (endTime < startTime) {
			throw new IllegalArgumentException("End time cannot be behind start time");
		}
		
		String viewName = (String) context.get(FacilioConstants.ContextNames.CV_NAME);
		if (StringUtils.isEmpty(viewName)) {
			throw new IllegalArgumentException("View name cannot be empty");
		}
		
		
		Boolean isCount = (Boolean) context.get(FacilioConstants.ContextNames.COUNT);
		if (isCount != null && isCount) {
			DateAggregateOperator aggr = (DateAggregateOperator) context.get(FacilioConstants.ContextNames.AGGR_KEY);
			if (aggr == null) {
				context.put(FacilioConstants.ContextNames.AGGR_KEY, DateAggregateOperator.FULLDATE);
			}
		}
		else {
			context.put(FacilioConstants.ContextNames.COUNT, false);
		}
		
		return false;
	}

}
