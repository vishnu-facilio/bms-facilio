package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.VisitorContext;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.constants.FacilioConstants;

public class CheckForVisitorDuplicationCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<VisitorContext> visitors = (List<VisitorContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if(CollectionUtils.isNotEmpty(visitors)) {
			for(VisitorContext visitor : visitors) {
				boolean visitorExisiting = VisitorManagementAPI.checkForDuplicateVisitor(visitor);
				if(visitorExisiting) {
					throw new IllegalArgumentException("A visitor already exists with this phone number");
				}
			}
		}
		
		return false;
	}

}
