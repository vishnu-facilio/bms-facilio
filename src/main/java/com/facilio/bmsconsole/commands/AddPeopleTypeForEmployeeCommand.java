package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.EmployeeContext;
import com.facilio.bmsconsole.context.PeopleContext.PeopleType;
import com.facilio.constants.FacilioConstants;

public class AddPeopleTypeForEmployeeCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<EmployeeContext> employees = (List<EmployeeContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if(CollectionUtils.isNotEmpty(employees)) {
			for(EmployeeContext emp : employees) {
				emp.setPeopleType(PeopleType.EMPLOYEE);
			}
		}
		return false;
	}

}
