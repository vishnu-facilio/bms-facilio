package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.ReadingAlarm;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.mv.context.MVProjectWrapper;
import com.facilio.mv.util.MVUtil;

public class PageRecordHandlingCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		long recordId = (long) context.get(FacilioConstants.ContextNames.ID);
		
		switch(moduleName) {
			case ContextNames.MV_PROJECT_MODULE:
				MVProjectWrapper project = MVUtil.getMVProject(recordId);
				context.put(ContextNames.RECORD, project);
				break;
				
			case FacilioConstants.ContextNames.READING_RULE_MODULE:
				FacilioChain fetchAlarmChain = ReadOnlyChainFactory.fetchAlarmRuleWithActionsChain();
				fetchAlarmChain.execute(context);
				context.put(ContextNames.RECORD, context.get(ContextNames.ALARM_RULE));
				break;
				
			case ContextNames.FORMULA_FIELD:
				FormulaFieldContext formula = FormulaFieldAPI.getFormulaField(recordId);
				context.put(ContextNames.RECORD, formula);
				break;
			case ContextNames.READING_ALARM:
			case ContextNames.NEW_READING_ALARM:	
				BaseAlarmContext readingAlarm = NewAlarmAPI.getAlarm(recordId);
				AlarmOccurrenceContext latestAlarmOccurance = NewAlarmAPI.getLatestAlarmOccurance(readingAlarm);

				readingAlarm.setLastOccurrence(latestAlarmOccurance);
				context.put(ContextNames.RECORD, readingAlarm);
				break;
			case ContextNames.WORKPERMIT:
				FacilioChain facilioChain = ReadOnlyChainFactory.getWorkPermitDetailsChain();
				facilioChain.execute(context);
				context.put(ContextNames.RECORD, context.get(ContextNames.RECORD));
				break;
				
		}

		if (context.containsKey(ContextNames.RECORD)) {
			context.put(ContextNames.ID, -1l);
		}
		
		return false;
	}

}
