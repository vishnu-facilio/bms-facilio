package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.context.LoggerContext;
import com.facilio.bmsconsole.context.FormulaFieldContext.TriggerType;
import com.facilio.bmsconsole.jobs.VirtualMeterEnergyDataCalculator;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.bmsconsole.util.LoggerAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;

public class HistoricalFormulaRunCalculationCommand extends FacilioCommand {

	private static final Logger LOGGER = LogManager.getLogger(HistoricalFormulaRunCalculationCommand.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		long formulaId = (long) context.get(FacilioConstants.ContextNames.FORMULA_FIELD);
		DateRange range = (DateRange) context.get(FacilioConstants.ContextNames.DATE_RANGE);
		List<Long> resourceIds = (List<Long>) context.get(FacilioConstants.ContextNames.RESOURCE_LIST);
		Boolean isInclude = (Boolean) context.get(FacilioConstants.ContextNames.IS_INCLUDE);
		
		if (formulaId == -1 || range == null || range.getStartTime() == -1 || range.getEndTime() == -1) {
			throw new IllegalArgumentException("In Sufficient paramaters for Historical formula calculation");
		}
		
		FormulaFieldContext formula = FormulaFieldAPI.getFormulaField(formulaId);
		if (formula == null) {
			throw new IllegalArgumentException("Invalid formula ID for historical formula calculation");
		}
		
		Boolean historicalAlarm = (Boolean) context.get(FacilioConstants.ContextNames.HISTORY_ALARM);
		if (historicalAlarm == null) {
			historicalAlarm = false;
		}
		Boolean skipOptimisedWorkflow = (Boolean) context.get(FacilioConstants.ContextNames.SKIP_OPTIMISED_WF);
		if (skipOptimisedWorkflow == null) {
			skipOptimisedWorkflow = false;
		}
		
		if(isInclude == null)
		{
			isInclude = true;
		}

		List<Long> finalResourceIds = new ArrayList<Long>();
		if(resourceIds == null || resourceIds.isEmpty())
		{
			finalResourceIds = formula.getMatchedResourcesIds();
		}
		else if (resourceIds!=null && !resourceIds.isEmpty() && isInclude)
		{
			List<Long> matchedResources = formula.getMatchedResourcesIds();
			for(Long resourceId: resourceIds)
			{
				if(matchedResources.contains(resourceId)) {
					finalResourceIds.add(resourceId);
				}
			}
		}
		else if (resourceIds!=null && !resourceIds.isEmpty() && !isInclude)
		{
			List<Long> matchedResources = formula.getMatchedResourcesIds();
			for(Long matchedResourceId: matchedResources)
			{
				if(!resourceIds.contains(matchedResourceId)) {
					finalResourceIds.add(matchedResourceId);
				}
			}
		}
		else
		{
			throw new Exception("Not a valid Inclusion/Exclusion of Resources for the given Formula");
		}
		
		if(finalResourceIds == null || finalResourceIds.isEmpty())
		{
			throw new IllegalArgumentException("The given formula : "+formulaId+" is not defined for the given resource");
		}
		
		List<FacilioField> loggerfields = FieldFactory.getFormulaFieldHistoricalLoggerFields();
		List<LoggerContext> activeDependentFormulaLoggerList = LoggerAPI.getActiveParentAndResourceLoggers(ModuleFactory.getFormulaFieldHistoricalLoggerModule(), loggerfields, formulaId, finalResourceIds);
		if(activeDependentFormulaLoggerList != null && !activeDependentFormulaLoggerList.isEmpty())
		{
			throw new Exception("Formula Field Historical already In-Progress for the Current Resource Logger with formulaId "+ formulaId);
		}
				
		long loggerGroupId = -1l;
		boolean isFirst = true;
		Map<Long,LoggerContext> formulaLoggerIdvsHistoricalLoggerMap = new HashMap<Long,LoggerContext>();
			
		for(Long finalResourceId:finalResourceIds)
		{
			Map<Long,LoggerContext> uniqueFormulaIdvsHistoricalLoggerMap = new HashMap<Long,LoggerContext>();
			LoggerContext formulaFieldHistoricalLoggerContext = new LoggerContext();
			if(isFirst) {
				formulaFieldHistoricalLoggerContext = setLoggerContext(formulaId, range, finalResourceId, -1);
				LoggerAPI.addLogger(ModuleFactory.getFormulaFieldHistoricalLoggerModule(), loggerfields, formulaFieldHistoricalLoggerContext);
				
				loggerGroupId = formulaFieldHistoricalLoggerContext.getId();
				formulaFieldHistoricalLoggerContext.setLoggerGroupId(loggerGroupId);
				LoggerAPI.updateLogger(ModuleFactory.getFormulaFieldHistoricalLoggerModule(), loggerfields, formulaFieldHistoricalLoggerContext);
				isFirst = false;
			}
			else {
				formulaFieldHistoricalLoggerContext = setLoggerContext(formulaId, range, finalResourceId, loggerGroupId);
				LoggerAPI.addLogger(ModuleFactory.getFormulaFieldHistoricalLoggerModule(), loggerfields, formulaFieldHistoricalLoggerContext);
			}

			formulaLoggerIdvsHistoricalLoggerMap.put(formulaFieldHistoricalLoggerContext.getId(), formulaFieldHistoricalLoggerContext);
			uniqueFormulaIdvsHistoricalLoggerMap.put(formulaFieldHistoricalLoggerContext.getParentId(), formulaFieldHistoricalLoggerContext);
			checkDependentFormulae (formula, formulaFieldHistoricalLoggerContext.getId(), finalResourceId, range, loggerGroupId, formulaLoggerIdvsHistoricalLoggerMap, uniqueFormulaIdvsHistoricalLoggerMap);
			
			JSONObject props = new JSONObject();
			props.put("skipOptimisedWorkflow", skipOptimisedWorkflow);
			BmsJobUtil.addJobProps(formulaFieldHistoricalLoggerContext.getId(), "SingleResourceHistoricalFormulaFieldCalculator", props);
			FormulaFieldAPI.calculateHistoricalDataForSingleResource(formulaFieldHistoricalLoggerContext.getId());

			LOGGER.info("Historical formula Job uniqueFormulaIdvsHistoricalLoggerMap --" + uniqueFormulaIdvsHistoricalLoggerMap + "--formulaId--"+formulaId+ " --formulaLoggerJobId--"+formulaFieldHistoricalLoggerContext.getId());
			System.out.println("uniqueFormulaIdvsHistoricalLoggerMap"+ uniqueFormulaIdvsHistoricalLoggerMap);
		}
		

		LOGGER.info("Historical formula Job formulaLoggerIdvsHistoricalLoggerMap --" + formulaLoggerIdvsHistoricalLoggerMap + "--formulaId--"+formulaId);
		System.out.println("formulaLoggerIdvsHistoricalLoggerMap"+ formulaLoggerIdvsHistoricalLoggerMap);
		
		return false;
	}
	
	public static void checkDependentFormulae (FormulaFieldContext formula, long formulaLoggerId, long singleResourceId, DateRange range, long loggerGroupId, 
			 Map<Long,LoggerContext> formulaLoggerIdvsHistoricalLoggerMap, Map<Long,LoggerContext> uniqueFormulaIdvsHistoricalLoggerMap) throws Exception {
		
		
		List<FacilioField> loggerfields = FieldFactory.getFormulaFieldHistoricalLoggerFields();
		List<FormulaFieldContext> dependentFormulae = FormulaFieldAPI.getActiveFormulasDependingOnFields(formula.getTriggerTypeEnum(), Collections.singletonList(formula.getReadingField().getId()));
		if (dependentFormulae == null || dependentFormulae.isEmpty()) 
		{
			return;
		}
		
		for (FormulaFieldContext dependentFormula : dependentFormulae) 
		{
			if (singleResourceId != -1 && dependentFormula.getMatchedResourcesIds().contains(singleResourceId)) 
			{
				List<Long> dependentFieldIds = dependentFormula.getWorkflow().getDependentFieldIds();
				if (dependentFieldIds.contains(formula.getReadingField().getFieldId())) 
				{	
					LoggerContext dependentFormulaLoggerContext = setLoggerContext(dependentFormula.getId(), range, singleResourceId, loggerGroupId);
					LoggerContext parentFormulaLoggerContext = formulaLoggerIdvsHistoricalLoggerMap.get(formulaLoggerId);
										
					if(!uniqueFormulaIdvsHistoricalLoggerMap.containsKey(dependentFormula.getId()))
					{
						dependentFormulaLoggerContext.setDependentId(parentFormulaLoggerContext.getId());
						uniqueFormulaIdvsHistoricalLoggerMap.put(dependentFormula.getId(), dependentFormulaLoggerContext);	
						LoggerAPI.addLogger(ModuleFactory.getFormulaFieldHistoricalLoggerModule(), loggerfields, dependentFormulaLoggerContext);	
					}
					else
					{
						dependentFormulaLoggerContext = uniqueFormulaIdvsHistoricalLoggerMap.get(dependentFormula.getId());
						dependentFormulaLoggerContext.setDependentId(parentFormulaLoggerContext.getId());
						uniqueFormulaIdvsHistoricalLoggerMap.put(dependentFormula.getId(), dependentFormulaLoggerContext);	
						LoggerAPI.updateLogger(ModuleFactory.getFormulaFieldHistoricalLoggerModule(), loggerfields, dependentFormulaLoggerContext);;
					}
					
					formulaLoggerIdvsHistoricalLoggerMap.put(dependentFormulaLoggerContext.getId(), dependentFormulaLoggerContext);	
					checkDependentFormulae(dependentFormula, dependentFormulaLoggerContext.getId(), singleResourceId, range, loggerGroupId, formulaLoggerIdvsHistoricalLoggerMap, uniqueFormulaIdvsHistoricalLoggerMap);

				}
			}				
		}
	}


	public static LoggerContext setLoggerContext(long formulaId, DateRange range, long resourceId, long loggerGroupId)
	{
		LoggerContext loggerContext = new LoggerContext();
		loggerContext.setParentId(formulaId);;
		loggerContext.setResourceId(resourceId);
		loggerContext.setLoggerGroupId(loggerGroupId);;
		loggerContext.setStatus(LoggerContext.Status.IN_PROGRESS.getIntVal());
		loggerContext.setStartTime(range.getStartTime());
		loggerContext.setEndTime(range.getEndTime());
		loggerContext.setCreatedBy(AccountUtil.getCurrentUser().getId());
		loggerContext.setCreatedTime(DateTimeUtil.getCurrenTime());
		return loggerContext;	
	}
	
	
	
}
