package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.context.HistoricalLoggerContext;
import com.facilio.bmsconsole.context.LoggerContext;
import com.facilio.bmsconsole.jobs.HistoricalVMEnergyDataCalculatorJob;
import com.facilio.bmsconsole.jobs.SingleResourceHistoricalFormulaCalculatorJob;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.bmsconsole.util.HistoricalLoggerUtil;
import com.facilio.bmsconsole.util.LoggerAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;

public class HistoricalFormulaFieldCalculationCommand extends FacilioCommand implements PostTransactionCommand{

private static final Logger LOGGER = Logger.getLogger(SingleResourceHistoricalFormulaCalculatorJob.class.getName());
	
	private LoggerContext historicalFormulaFieldLogger = null;
	private Long jobId;
	private String exceptionMessage = null;
	private StackTraceElement[] stack = null;
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
	
		try {
			
			long jobStartTime = System.currentTimeMillis();
			jobId = (long) context.get(FacilioConstants.ContextNames.HISTORICAL_FORMULA_FIELD_JOB_ID);
			List<FacilioField> loggerfields = FieldFactory.getFormulaFieldHistoricalLoggerFields();
			
			historicalFormulaFieldLogger = LoggerAPI.getLoggerById(ModuleFactory.getFormulaFieldHistoricalLoggerModule(),loggerfields,jobId);
			historicalFormulaFieldLogger.setCalculationStartTime(DateTimeUtil.getCurrenTime());	
			
			long startTime = (long) historicalFormulaFieldLogger.getStartTime();
			long endTime = (long) historicalFormulaFieldLogger.getEndTime();
			long resourceId = (long) historicalFormulaFieldLogger.getResourceId();
			long formulaId = (long) historicalFormulaFieldLogger.getParentId();
			DateRange range = new DateRange(startTime, endTime);
			
			JSONObject prop = BmsJobUtil.getJobProps(jobId, "SingleResourceHistoricalFormulaFieldCalculator");
			
			boolean skipOptimisedWorkflow = false;
			FormulaFieldContext formula = FormulaFieldAPI.getFormulaField(formulaId);
			if(formula.getWorkflow().isV2Script()){
				skipOptimisedWorkflow = true;
			}
			
			if (prop != null && prop.get("skipOptimisedWorkflow") != null) {
				skipOptimisedWorkflow = (boolean) prop.get("skipOptimisedWorkflow");
			}
			
			LOGGER.info("Historical formula Job started with Job Logger Id --" +jobId);
			
			switch (formula.getTriggerTypeEnum()) {
				case POST_LIVE_READING:
					if(skipOptimisedWorkflow) {						
						FormulaFieldAPI.historicalCalculation(formula, range, resourceId, false, false);				
					}
					else {
						FormulaFieldAPI.optimisedHistoricalCalculation(formula, range, resourceId, false, false);
					}
					break;
				default:				
					FormulaFieldAPI.historicalCalculation(formula, range, resourceId, false, false);			
			}
			
			String msg = "Time taken for Historical Formula calculation of formula : "+formulaId+" for resource : "+resourceId+" between "+startTime+" and "+endTime+" is "+(System.currentTimeMillis() - jobStartTime);
			LOGGER.info(msg);
			
		}
		
		catch (Exception formulaFieldException) {
			exceptionMessage = formulaFieldException.getMessage();
			stack = formulaFieldException.getStackTrace();
			throw formulaFieldException;
		}
		
		return false;
	}	
	

	public boolean postExecute() throws Exception {
		if(historicalFormulaFieldLogger != null )
		{
			List<FacilioField> loggerfields = FieldFactory.getFormulaFieldHistoricalLoggerFields();
			historicalFormulaFieldLogger.setStatus(LoggerContext.Status.RESOLVED.getIntVal());
			historicalFormulaFieldLogger.setCalculationEndTime(DateTimeUtil.getCurrenTime());
			LoggerAPI.updateLogger(ModuleFactory.getFormulaFieldHistoricalLoggerModule(), loggerfields, historicalFormulaFieldLogger);	

			List<LoggerContext> dependentFormulaLoggers = LoggerAPI.getActiveDependentParentAndResourceLogger(ModuleFactory.getFormulaFieldHistoricalLoggerModule(), loggerfields, historicalFormulaFieldLogger.getId(), historicalFormulaFieldLogger.getResourceId());

			if(dependentFormulaLoggers != null && !dependentFormulaLoggers.isEmpty())
			{
				for (LoggerContext dependentFormulaLogger : dependentFormulaLoggers) {	
					FormulaFieldAPI.calculateHistoricalDataForSingleResource(dependentFormulaLogger.getId());
				}			
			}				
		}
		else
		{
			LOGGER.severe(" FORMULAFIELDLOGGER IS NULL IN POSTEXECUTE FOR JOB -- " + jobId);
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
			
			CommonCommandUtil.emailException(SingleResourceHistoricalFormulaCalculatorJob.class.getName(), "Error occurred during formula calculation of single resource for job id : "+jobId, mailExp); 
			LOGGER.severe("HISTORICAL FORMULA FIELD JOB COMMAND FAILED, JOB ID -- : "+jobId);
			LOGGER.log(Level.SEVERE, exceptionMessage);

			if(historicalFormulaFieldLogger != null )
			{

				List<FacilioField> loggerfields = FieldFactory.getFormulaFieldHistoricalLoggerFields();				

				historicalFormulaFieldLogger.setStatus(LoggerContext.Status.FAILED.getIntVal());
				historicalFormulaFieldLogger.setCalculationEndTime(DateTimeUtil.getCurrenTime());
				LoggerAPI.updateLogger(ModuleFactory.getFormulaFieldHistoricalLoggerModule(), loggerfields, historicalFormulaFieldLogger);

				List<LoggerContext> dependentFormulaLoggers = new ArrayList<>();	
				dependentFormulaLoggers.add(historicalFormulaFieldLogger);
				
				while(dependentFormulaLoggers != null && !dependentFormulaLoggers.isEmpty())
				{
					List<Long> nextDependentLoggerIds = new ArrayList<Long>(); 
					
					for (LoggerContext dependentFormulaLogger : dependentFormulaLoggers) {	
						dependentFormulaLogger.setStatus(LoggerContext.Status.FAILED.getIntVal());
						dependentFormulaLogger.setCalculationEndTime(DateTimeUtil.getCurrenTime());
						LoggerAPI.updateLogger(ModuleFactory.getFormulaFieldHistoricalLoggerModule(), loggerfields, dependentFormulaLogger);
						nextDependentLoggerIds.add(dependentFormulaLogger.getId());
					}
					
					dependentFormulaLoggers =  LoggerAPI.getActiveDependentParentAndResourceLogger(ModuleFactory.getFormulaFieldHistoricalLoggerModule(), loggerfields, nextDependentLoggerIds, historicalFormulaFieldLogger.getResourceId());	
				}

			}
			else
			{
				LOGGER.severe(" FORMULAFIELDLOGGER IS NULL IN ONERROR FOR JOB -- " + jobId);
			}	
			
		}
		catch (Exception e) 
		{
			CommonCommandUtil.emailException("Historical Formula Field Exception Handling failed",
					"Historical Formula Field  Exception Handling failed - orgid -- " + AccountUtil.getCurrentOrg().getId()+ ", JOB ID -- " +jobId, e);
			LOGGER.severe("Historical Formula Field Exception Handling failed  --"+jobId);
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
}


	
