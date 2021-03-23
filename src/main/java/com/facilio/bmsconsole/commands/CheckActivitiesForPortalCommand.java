package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.activity.ActivityContext;
import com.facilio.bmsconsole.activity.WorkOrderActivityType;
import com.facilio.constants.FacilioConstants;

public class CheckActivitiesForPortalCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ActivityContext> activity =  (List<ActivityContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		List<ActivityContext> activities = new ArrayList<>();
		for(ActivityContext prop : activity) {	
			ActivityContext checkIsNotify = prop;
			if (AccountUtil.getCurrentUser().isPortalUser()) {
					if (checkIsNotify.getType() == WorkOrderActivityType.ADD_COMMENT.getValue()) {
						if (checkIsNotify.getInfo().get("notifyRequester") != null) {
							if ((boolean) checkIsNotify.getInfo().get("notifyRequester")) {
								activities.add(checkIsNotify);
							}
						}
						else if(checkIsNotify.getInfo().get("addedBy")!=null) {
							if((long) checkIsNotify.getInfo().get("addedBy") == AccountUtil.getCurrentUser().getOuid()) {
								activities.add(checkIsNotify);
							}
						}
					}
					else {
						activities.add(checkIsNotify);
					}
			}
			else {
				activities.add(checkIsNotify);
			}
		}
		context.put(FacilioConstants.ContextNames.RECORD_LIST, activities);
		return false;
	}

	

}
