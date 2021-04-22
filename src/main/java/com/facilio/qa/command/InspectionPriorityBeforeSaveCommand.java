package com.facilio.qa.command;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.inspection.InspectionCategoryContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionPriorityContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.util.DisplayNameToLinkNameUtil;
import com.facilio.v3.context.Constants;

public class InspectionPriorityBeforeSaveCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		// TODO Auto-generated method stub
		
		List<InspectionPriorityContext> priorities = Constants.getRecordList((FacilioContext) context);
		
		
		for(InspectionPriorityContext priority : priorities) {
			
			String linkName = DisplayNameToLinkNameUtil.getLinkName(priority.getDisplayName(), FacilioConstants.Inspection.INSPECTION_PRIORITY, "priority");
			
			priority.setPriority(linkName);
		}
		return false;
	}

}
