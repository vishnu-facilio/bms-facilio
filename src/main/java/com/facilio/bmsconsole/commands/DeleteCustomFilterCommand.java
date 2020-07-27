package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.CustomFilterContext;
import com.facilio.bmsconsole.util.FiltersAPI;
import com.facilio.constants.FacilioConstants;


public class DeleteCustomFilterCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		CustomFilterContext customFilter = (CustomFilterContext) context.get(FacilioConstants.ContextNames.CUSTOM_FILTER);
		FiltersAPI.deleteCustomFilter(customFilter);
		return false;
	}

}