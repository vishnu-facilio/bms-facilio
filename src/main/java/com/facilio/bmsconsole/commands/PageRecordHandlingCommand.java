package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.mv.context.MVProjectWrapper;
import com.facilio.mv.util.MVUtil;

public class PageRecordHandlingCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		long recordId = (long) context.get(FacilioConstants.ContextNames.ID);
		
		switch(moduleName) {
			case ContextNames.MV_PROJECT_MODULE:
				MVProjectWrapper project = MVUtil.getMVProject(recordId);
				context.put(ContextNames.RECORD, project);
				break;
				
			case FacilioConstants.ContextNames.READING_RULE_MODULE:
				Chain fetchAlarmChain = ReadOnlyChainFactory.fetchAlarmRuleWithActionsChain();
				fetchAlarmChain.execute(context);
				context.put(ContextNames.RECORD, context.get(ContextNames.ALARM_RULE));
		}
		
		if (context.containsKey(ContextNames.RECORD)) {
			context.put(ContextNames.ID, -1);
		}
		
		return false;
	}

}
