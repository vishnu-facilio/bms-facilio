package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ResetCounterMetaContext;
import com.facilio.constants.FacilioConstants;

public class ValidateResetReadingsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ResetCounterMetaContext> resetCounterMetaList = (List<ResetCounterMetaContext>) context.get(FacilioConstants.ContextNames.RESET_COUNTER_META_LIST);
		for(ResetCounterMetaContext reset :resetCounterMetaList){
			if(reset.getEndvalue() < 0){
				throw new IllegalArgumentException("Please enter end value");
			}
		}
		return false;
	}

}
