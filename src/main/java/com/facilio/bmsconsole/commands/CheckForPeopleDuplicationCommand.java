package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.constants.FacilioConstants;

public class CheckForPeopleDuplicationCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<PeopleContext> peopleList = (List<PeopleContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if (CollectionUtils.isNotEmpty(peopleList)) {
			for (PeopleContext people : peopleList) {
				if(StringUtils.isNotEmpty(people.getEmail()) && PeopleAPI.checkForDuplicatePeople(people)) {
					throw new IllegalArgumentException("People with the same email id already exists");
				}
			}
		}
		return false;
	}
}