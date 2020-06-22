package com.facilio.bmsconsole.jobs;

import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.HistoricalLoggerContext;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsole.util.FacilioFrequency;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.bmsconsole.util.HistoricalLoggerUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.energystar.context.EnergyStarMeterContext;
import com.facilio.energystar.util.EnergyStarUtil;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.tasker.ScheduleInfo;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.time.DateRange;

public class EnergyStarPushHistoricalData extends FacilioJob {
	
	private static final Logger LOGGER = LogManager.getLogger(EnergyStarPushHistoricalData.class.getName());
	@Override
	public void execute(JobContext jc) throws Exception {
		
		HistoricalLoggerContext logger = null;
		try {
			
			logger = HistoricalLoggerUtil.getHistoricalLoggerById(jc.getJobId());
			
			List<Map<String, Object>> meterProps = EnergyStarUtil.fetchEnergyStarRelated(ModuleFactory.getEnergyStarMeterModule(), FieldFactory.getEnergyStarMeterFields(),null,CriteriaAPI.getIdCondition(logger.getParentId(), ModuleFactory.getEnergyStarMeterModule()));
			EnergyStarMeterContext meter = FieldUtil.getAsBeanFromMap(meterProps.get(0), EnergyStarMeterContext.class);
			
			long startTime = logger.getStartTime();
			long endTime = logger.getEndTime();
			
			FacilioChain chain = TransactionChainFactory.getESPushMeterDataChain();
			
			FacilioContext context = chain.getContext();
			
			ScheduleInfo schedule = FormulaFieldAPI.getSchedule(FacilioFrequency.MONTHLY);
			List<DateRange> intervals = schedule.getTimeIntervals(startTime, endTime);
			
			context.put(EnergyStarUtil.ENERGY_STAR_METER_CONTEXT, meter);
			context.put(FacilioConstants.ContextNames.INTERVAL, intervals);
			
			chain.execute();
			
			logger.setStatus(HistoricalLoggerContext.Status.RESOLVED.getIntVal());
			
			HistoricalLoggerUtil.updateHistoricalLogger(logger);
		}
		catch(Exception e) {
			LOGGER.error("Energy Star Push data Failed", e);
			CommonCommandUtil.emailException(AssetActionJob.class.getName(), "Energy Star Push data Failed -- "+AccountUtil.getCurrentOrg().getId(), e);
			
			logger.setStatus(HistoricalLoggerContext.Status.FAILED.getIntVal());
			
			HistoricalLoggerUtil.updateHistoricalLogger(logger);
		}
	}

}