package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.constants.FacilioConstants;

public class GetV2AlarmsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		List<Long> ids = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if (CollectionUtils.isNotEmpty(ids)) {
			List<BaseAlarmContext> alarms = NewAlarmAPI.getAlarms(ids);
			context.put(FacilioConstants.ContextNames.RECORD_LIST, alarms);
		}
		return false;
	}

}
