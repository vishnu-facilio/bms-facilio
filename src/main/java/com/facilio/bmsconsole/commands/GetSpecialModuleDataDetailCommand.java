package com.facilio.bmsconsole.commands;


import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

public class GetSpecialModuleDataDetailCommand implements Command  {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		long recordId = (long) context.get(FacilioConstants.ContextNames.ID);
		
		if(recordId > 0) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			switch (moduleName) {
				case FacilioConstants.ContextNames.READING_RULE_MODULE:
					Chain fetchAlarmChain = ReadOnlyChainFactory.fetchAlarmRuleWithActionsChain();
					fetchAlarmChain.execute(context);
					context.put(FacilioConstants.ContextNames.RECORD, (AlarmRuleContext) context.get(FacilioConstants.ContextNames.ALARM_RULE));
				default:
					break;
			}
		}
		return false;
	}
}
