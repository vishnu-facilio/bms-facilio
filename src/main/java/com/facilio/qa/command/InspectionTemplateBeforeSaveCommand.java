package com.facilio.qa.command;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTemplateContext;
import com.facilio.chain.FacilioContext;
import com.facilio.v3.context.Constants;

public class InspectionTemplateBeforeSaveCommand extends FacilioCommand {
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<InspectionTemplateContext> inspections = Constants.getRecordList((FacilioContext) context);
		
		for(InspectionTemplateContext inspection : inspections) {
			inspection.setStatus(Boolean.TRUE);
		}
		return false;
	}

}
