package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.chain.Chain;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class AlarmRuleAction extends FacilioAction {

	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = Logger.getLogger(AlarmRuleAction.class.getName());
	
	public String fetchAlarmRuleLibrary() throws Exception {
		
		List<AlarmRuleContext> alarmRules = TemplateAPI.getAllRuleLibraryContext();
		setResult("rule", alarmRules);
		return SUCCESS;
	}
}
