package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;

public class HandleOccurrenceListLookupCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		boolean fetchLookups = (boolean) context.getOrDefault(ContextNames.FETCH_LOOKUPS, false);
		List<AlarmOccurrenceContext> occurrences =  (List<AlarmOccurrenceContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if (fetchLookups && CollectionUtils.isNotEmpty(occurrences)) {
			NewAlarmAPI.loadOccurrenceLookups(occurrences);
		}
		return false;
	}

}
