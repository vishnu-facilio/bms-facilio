package com.facilio.bmsconsole.commands;


import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;

public class GetSpecialModuleDataDetailCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		long recordId = (long) context.get(FacilioConstants.ContextNames.ID);
		
		if(recordId > 0) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			switch (moduleName) {
				case FacilioConstants.ContextNames.READING_RULE_MODULE:
					FacilioChain fetchAlarmChain = ReadOnlyChainFactory.fetchAlarmRuleWithActionsChain();
					fetchAlarmChain.execute(context);
					context.put(FacilioConstants.ContextNames.RECORD, context.get(FacilioConstants.ContextNames.ALARM_RULE));
				default:
					break;
			}
		}
		return false;
	}
}
