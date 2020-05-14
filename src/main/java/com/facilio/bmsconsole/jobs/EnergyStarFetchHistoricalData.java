package com.facilio.bmsconsole.jobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsole.util.FacilioFrequency;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.energystar.context.EnergyStarMeterContext;
import com.facilio.energystar.context.EnergyStarPropertyContext;
import com.facilio.energystar.util.EnergyStarUtil;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.tasker.ScheduleInfo;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.time.DateRange;

public class EnergyStarFetchHistoricalData extends FacilioJob {
	
	private static final Logger LOGGER = LogManager.getLogger(AssetActionJob.class.getName());
	@Override
	public void execute(JobContext jc) {
		try {
			
			JSONObject props= BmsJobUtil.getJobProps(jc.getJobId(), jc.getJobName());
			
			List<Map<String, Object>> propertyProps = EnergyStarUtil.fetchEnergyStarRelated(ModuleFactory.getEnergyStarPropertyModule(), FieldFactory.getEnergyStarPropertyFields(),null,CriteriaAPI.getIdCondition(jc.getJobId(), ModuleFactory.getEnergyStarPropertyModule()));
			EnergyStarPropertyContext property = FieldUtil.getAsBeanFromMap(propertyProps.get(0), EnergyStarPropertyContext.class);
			
			long startTime = (long) props.get(FacilioConstants.ContextNames.START_TIME);
			long endTime = (long) props.get(FacilioConstants.ContextNames.END_TIME);
			
			FacilioChain chain = TransactionChainFactory.getEnergyStarFetchDataChain();
			
			FacilioContext context = chain.getContext();
			
			ScheduleInfo schedule = FormulaFieldAPI.getSchedule(FacilioFrequency.MONTHLY);
			List<DateRange> intervals = schedule.getTimeIntervals(startTime, endTime);
			
			List<Long> fetchTimeList = new ArrayList<Long>();
			for(DateRange interval :intervals) {
				fetchTimeList.add(interval.getStartTime());
			}
			
			context.put(EnergyStarUtil.ENERGY_STAR_PROPERTY_CONTEXT, property);
			context.put(EnergyStarUtil.ENERGY_STAR_FETCH_TIME_LIST, fetchTimeList);
			
			chain.execute();
		}
		catch(Exception e) {
			LOGGER.error("Energy Star Push data Failed", e);
			CommonCommandUtil.emailException(AssetActionJob.class.getName(), "Energy Star Push data Failed -- "+AccountUtil.getCurrentOrg().getId(), e);
		}
	}

}