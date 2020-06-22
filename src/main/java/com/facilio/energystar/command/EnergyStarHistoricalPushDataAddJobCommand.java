package com.facilio.energystar.command;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.HistoricalLoggerContext;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsole.util.HistoricalLoggerUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.energystar.util.EnergyStarUtil;

public class EnergyStarHistoricalPushDataAddJobCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		long meterId = (long)context.get(EnergyStarUtil.ENERGY_STAR_METER_ID);
		long startTime = (long)context.get(FacilioConstants.ContextNames.START_TIME);
		long endTime = (long)context.get(FacilioConstants.ContextNames.END_TIME);
		
		HistoricalLoggerContext syncLogger = new HistoricalLoggerContext();
		
		syncLogger.setOrgId(AccountUtil.getCurrentOrg().getId());
		syncLogger.setStartTime(startTime);
		syncLogger.setEndTime(endTime);
		syncLogger.setType(HistoricalLoggerContext.Type.ENERGY_STAR_PUSH_HISTORICAL_DATA.getIntVal());
		syncLogger.setStatus(HistoricalLoggerContext.Status.IN_PROGRESS.getIntVal());
		syncLogger.setParentId(meterId);
		
		HistoricalLoggerUtil.addHistoricalLogger(syncLogger);
		
		BmsJobUtil.scheduleOneTimeJobWithProps(syncLogger.getId(), "EnergyStarPushHistoricalData", 5, "history", null);
		
		return false;
	}

}
