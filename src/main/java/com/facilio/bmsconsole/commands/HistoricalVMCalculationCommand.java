package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.command.PostTransactionCommand;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.HistoricalLoggerContext;
import com.facilio.bmsconsole.jobs.HistoricalVMEnergyDataCalculatorJob;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.bmsconsole.util.HistoricalLoggerUtil;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;

public class HistoricalVMCalculationCommand extends FacilioCommand implements PostTransactionCommand {
	
	private static final Logger LOGGER = Logger.getLogger(HistoricalVMCalculationCommand.class.getName());
	
	private HistoricalLoggerContext historicalLogger = null;
	private Long jobId;
	private String exceptionMessage = null;
	private StackTraceElement[] stack = null;

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		try {
				jobId = (long) context.get(FacilioConstants.ContextNames.HISTORICAL_VM_JOB_ID);
				String jobName = (String) context.get(FacilioConstants.ContextNames.HISTORICAL_VM_JOB);
				JSONObject jobProps = BmsJobUtil.getJobProps(jobId, jobName);
				
				historicalLogger = HistoricalLoggerUtil.getHistoricalLoggerById(jobId);
				historicalLogger.setCalculationStartTime(DateTimeUtil.getCurrenTime());
				
				Long meterId=(Long)jobProps.get("meterId");
				Long startTime = (Long)jobProps.get("startTime");
				Long endTime = (Long)jobProps.get("endTime");
				Boolean updateReading= (Boolean)jobProps.get("updateReading");
				long processStartTime = System.currentTimeMillis();
				
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioField energyField=modBean.getField("totalEnergyConsumptionDelta", FacilioConstants.ContextNames.ENERGY_DATA_READING);
				
				if(historicalLogger != null) 
				{					
					List<HistoricalLoggerContext> historicalLoggers = new ArrayList<HistoricalLoggerContext>();
					historicalLoggers.add(historicalLogger);
					
					while(historicalLoggers != null && !historicalLoggers.isEmpty())
					{
						
						List<Long> historicalLoggerIds = new ArrayList<Long>();
						
						for (HistoricalLoggerContext historicalLoggerContext : historicalLoggers) {
							EnergyMeterContext meter = DeviceAPI.getEnergyMeter(historicalLoggerContext.getParentId());
							List<Long> childMeterIds = DeviceAPI.getChildrenMeters(meter);
							if (childMeterIds == null) {
								continue;
							}
							
							int interval = ReadingsAPI.getDataInterval(meter.getId(), energyField);
							DeviceAPI.insertVirtualMeterReadings(meter, childMeterIds, startTime, endTime, interval,updateReading, true);
							
							historicalLoggerIds.add(historicalLoggerContext.getId());
						}
						historicalLoggers = HistoricalLoggerUtil.getActiveHistoricalLogger(historicalLoggerIds);	
					}
				}				
			}
		
			catch (Exception VMException) {
				exceptionMessage = VMException.getMessage();
				stack = VMException.getStackTrace();
				throw VMException;
			}
			
			return false;
	}
	
	public boolean postExecute() throws Exception {
		if(historicalLogger != null )
		{
			historicalLogger.setStatus(HistoricalLoggerContext.Status.RESOLVED.getIntVal());
			historicalLogger.setCalculationEndTime(DateTimeUtil.getCurrenTime());
			HistoricalLoggerUtil.updateHistoricalLogger(historicalLogger);	
			
			List<HistoricalLoggerContext> historicalLoggers = HistoricalLoggerUtil.getGroupedHistoricalLogger(historicalLogger.getId());
			
			if(historicalLoggers != null && !historicalLoggers.isEmpty())
			{
				
				for(HistoricalLoggerContext historicalLoggerContext:historicalLoggers)
				{
					historicalLoggerContext.setStatus(HistoricalLoggerContext.Status.RESOLVED.getIntVal());
					historicalLoggerContext.setCalculationEndTime(DateTimeUtil.getCurrenTime());
					HistoricalLoggerUtil.updateHistoricalLogger(historicalLoggerContext);	
				}	
			}
		}
		else
		{
			LOGGER.severe("NO MAIN LOGGER DATA IN VM HISTORICAL JOB POST EXECUTE -- " + jobId);
		}			
		return false;
	}
	public void onError() throws Exception {
		constructErrorMessage();
	}
	public void constructErrorMessage() throws Exception 
	{
		try {
			
			Exception mailExp = new Exception(exceptionMessage);
			if (stack != null) 
			{
				mailExp.setStackTrace(stack);
			}
			
			CommonCommandUtil.emailException("Historical VM Calculation failed",
					"Historical VM Calculation failed - orgid -- " + AccountUtil.getCurrentOrg().getId()+ ", VM Logger Id -- " +jobId, mailExp);
			LOGGER.log(Level.SEVERE, exceptionMessage);
			
			LOGGER.severe("HISTORICAL VM JOB COMMAND FAILED, LOGGER ID -- " + jobId);
			
			if(historicalLogger != null )
			{
				historicalLogger.setStatus(HistoricalLoggerContext.Status.FAILED.getIntVal());
				historicalLogger.setCalculationEndTime(DateTimeUtil.getCurrenTime());
				HistoricalLoggerUtil.updateHistoricalLogger(historicalLogger);	
				
				List<HistoricalLoggerContext> historicalLoggers = HistoricalLoggerUtil.getGroupedHistoricalLogger(historicalLogger.getId());
				
				for(HistoricalLoggerContext historicalLoggerContext:historicalLoggers)
				{
					historicalLoggerContext.setStatus(HistoricalLoggerContext.Status.FAILED.getIntVal());
					historicalLoggerContext.setCalculationEndTime(DateTimeUtil.getCurrenTime());
					HistoricalLoggerUtil.updateHistoricalLogger(historicalLoggerContext);	
				}
			}
			else
			{
				LOGGER.severe("NO MAIN LOGGER DATA IN VM HISTORICAL JOB IN CONSTRUCT ERROR MESSAGE -- " + jobId);
			}	
			
		}
		catch (Exception e) 
		{
			CommonCommandUtil.emailException("Historical VM Exception Handling failed",
					"Historical VM Exception Handling failed - orgid -- " + AccountUtil.getCurrentOrg().getId()+ ", VM Logger Id -- " +jobId, e);
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
}
