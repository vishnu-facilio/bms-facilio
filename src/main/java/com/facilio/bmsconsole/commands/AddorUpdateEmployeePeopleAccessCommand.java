package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

public class AddorUpdateEmployeePeopleAccessCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
//		List<EmployeeContext> employees = (List<EmployeeContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
//		if(CollectionUtils.isNotEmpty(employees)) {
//			for(EmployeeContext emp : employees) {
//				PeopleAPI.updateAppAccess(emp, false);
//			}
//		}
		return false;
	}

}
