package com.facilio.qa.command;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsoleV3.context.induction.InductionTemplateContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTemplateContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTriggerContext;
import com.facilio.chain.FacilioContext;
import com.facilio.v3.context.Constants;

public class InductionTemplateBeforeSaveCommand extends FacilioCommand {
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<InductionTemplateContext> inductions = Constants.getRecordList((FacilioContext) context);
		
		for(InductionTemplateContext induction : inductions) {
			induction.setStatus(Boolean.TRUE);

			if(induction.getIsPublished() == null){
				induction.setIsPublished(false);
			}
		}
		return false;
	}

}
