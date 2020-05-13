package com.facilio.bmsconsole.jobs;

import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.energystar.context.EnergyStarMeterContext;
import com.facilio.energystar.util.EnergyStarUtil;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class EnergyStarPushHistoricalData extends FacilioJob {
	
	private static final Logger LOGGER = LogManager.getLogger(AssetActionJob.class.getName());
	@Override
	public void execute(JobContext jc) {
		try {
			
			JSONObject props= BmsJobUtil.getJobProps(jc.getJobId(), jc.getJobName());
			
			List<Map<String, Object>> meterProps = EnergyStarUtil.fetchEnergyStarRelated(ModuleFactory.getEnergyStarMeterModule(), FieldFactory.getEnergyStarMeterFields(),null,CriteriaAPI.getIdCondition(jc.getJobId(), ModuleFactory.getEnergyStarMeterModule()));
			EnergyStarMeterContext meter = FieldUtil.getAsBeanFromMap(meterProps.get(0), EnergyStarMeterContext.class);
			
			long startTime = (long) props.get(FacilioConstants.ContextNames.START_TIME);
			long endTime = (long) props.get(FacilioConstants.ContextNames.END_TIME);
			
			FacilioChain chain = TransactionChainFactory.getESPushMeterDataChain();
			
			FacilioContext context = chain.getContext();
			
			context.put(EnergyStarUtil.ENERGY_STAR_METER_CONTEXT, meter);
			context.put(FacilioConstants.ContextNames.START_TIME, startTime);
			context.put(FacilioConstants.ContextNames.END_TIME, endTime);
			
			chain.execute();
		}
		catch(Exception e) {
			LOGGER.error("Energy Star Push data Failed", e);
			CommonCommandUtil.emailException(AssetActionJob.class.getName(), "Energy Star Push data Failed -- "+AccountUtil.getCurrentOrg().getId(), e);
		}
	}

}