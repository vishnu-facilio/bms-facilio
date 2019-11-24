package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioStatus;

public class AddVisitorLogStatesCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		int logType = (Integer)context.getOrDefault("logType", 3);
		
		FacilioStatus checkedIn = VisitorManagementAPI.getLogStatus("CheckedIn");
		FacilioStatus checkedOut = VisitorManagementAPI.getLogStatus("CheckedOut");
		FacilioStatus requested = VisitorManagementAPI.getLogStatus("Requested");
		FacilioStatus approved = VisitorManagementAPI.getLogStatus("Approved");
		FacilioStatus rejected = VisitorManagementAPI.getLogStatus("Rejected");
		FacilioStatus waiting = VisitorManagementAPI.getLogStatus("Waiting");
		FacilioStatus blocked = VisitorManagementAPI.getLogStatus("Blocked");
		
		FacilioStatus upcoming = VisitorManagementAPI.getLogStatus("Upcoming");
		
		Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
		
		if(logType == 1) { // checkedin log list
			Criteria statusCriteria = new Criteria();
			statusCriteria.addAndCondition(CriteriaAPI.getCondition("MODULE_STATE", "moduleState", String.valueOf(checkedIn.getId()) + ","  + String.valueOf(checkedOut.getId()) + "," +  String.valueOf(requested.getId()) + "," +  String.valueOf(approved.getId()) + "," +  String.valueOf(rejected.getId()) + "," +  String.valueOf(blocked.getId()) + "," +  String.valueOf(waiting.getId()), NumberOperators.EQUALS));
			filterCriteria.andCriteria(statusCriteria);
		}
		else if(logType == 2) { // invite with requested
			Criteria statusCriteria = new Criteria();
			statusCriteria.addAndCondition(CriteriaAPI.getCondition("MODULE_STATE", "moduleState", String.valueOf(checkedIn.getId()) + ","  + String.valueOf(checkedOut.getId()) + "," +  String.valueOf(requested.getId()) + "," +  String.valueOf(approved.getId()) + "," +  String.valueOf(rejected.getId()) + "," +  String.valueOf(blocked.getId()) + "," +  String.valueOf(waiting.getId()) + "," +  String.valueOf(upcoming.getId()), NumberOperators.NOT_EQUALS));
			filterCriteria.andCriteria(statusCriteria);
		}
		//3 - upcoming
		
		return false;
	}

}
