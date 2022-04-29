package com.facilio.qa.command;

import com.facilio.bmsconsoleV3.context.workordersurvey.WorkOrderSurveyTemplateContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.List;

public class WorkOrderSurveyTemplateBeforeSaveCommand extends FacilioCommand {
	@Override
	public boolean executeCommand (Context context) throws Exception {

		List< WorkOrderSurveyTemplateContext > surveys = Constants.getRecordList(( FacilioContext ) context);

		for(WorkOrderSurveyTemplateContext survey : surveys) {
			survey.setStatus(Boolean.TRUE);
		}
		return false;
	}
}
