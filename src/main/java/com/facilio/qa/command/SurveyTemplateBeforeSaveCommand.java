package com.facilio.qa.command;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsoleV3.context.survey.SurveyTemplateContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;

public class SurveyTemplateBeforeSaveCommand extends FacilioCommand {
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<SurveyTemplateContext> surveys = Constants.getRecordList((FacilioContext) context);
		
		for(SurveyTemplateContext survey : surveys) {
			survey.setStatus(Boolean.TRUE);
		}
		return false;
	}

}
