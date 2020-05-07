package com.facilio.bmsconsole.commands;

import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.DemoRollUpYearlyCommand;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.ReadingAlarm;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.bmsconsole.util.NewEventAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.job.JobContext;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;

public class DemoAlarmPropagationCommand extends FacilioCommand{
	
	private static final Logger LOGGER = LogManager.getLogger(DemoAlarmPropagationCommand.class.getName());	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		Long orgId=(Long) context.get(ContextNames.DEMO_ROLLUP_JOB_ORG);
		ZonedDateTime currentTimeZdt = (ZonedDateTime) context.get(ContextNames.START_TIME);
		long currentTime = currentTimeZdt.toInstant().toEpochMilli();
		long yesterdayTime = DateTimeUtil.addDays(currentTime, -1);

		FacilioContext runThroughRuleChainContext = new FacilioContext();
		runThroughRuleChainContext.put(FacilioConstants.ContextNames.DATE_RANGE, new DateRange(DateTimeUtil.getDayStartTimeOf(yesterdayTime), DateTimeUtil.getDayEndTimeOf(yesterdayTime)));
		runThroughRuleChainContext.put(FacilioConstants.ContextNames.IS_SCALED_FLOW,true);
		
		HashMap<Long, List<Long>> ruleIdVsResourceIds = getAllRulesFromReadingAlarms();
		
		if(ruleIdVsResourceIds != null && MapUtils.isNotEmpty(ruleIdVsResourceIds)) 
		{
			for(long ruleId :ruleIdVsResourceIds.keySet())
			{
				List<Long> resourceIds = ruleIdVsResourceIds.get(ruleId);
				runThroughRuleChainContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE, ruleId);
				runThroughRuleChainContext.put(FacilioConstants.ContextNames.RESOURCE_LIST, resourceIds);
				
				FacilioChain runThroughRuleChain = TransactionChainFactory.runThroughReadingRuleChain();
				runThroughRuleChain.execute(runThroughRuleChainContext);
				LOGGER.info("Daily Demo Historical rule evaluation has been started for the given rule " +ruleId+ " with resourceIds: "+resourceIds);
			}	
		} 	 
		return false;
	}

   private HashMap<Long,List<Long>> getAllRulesFromReadingAlarms() throws Exception
   {
	   	HashMap<Long,List<Long>> ruleIdVsResourceIds = new HashMap<Long,List<Long>>();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
    	FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.NEW_READING_ALARM);
    	List<FacilioField> allFields = modBean.getAllFields(module.getName());	

		SelectRecordsBuilder<ReadingAlarm> selectbuilder = new SelectRecordsBuilder<ReadingAlarm>()
				.module(module)
				.select(allFields)
				.beanClass(ReadingAlarm.class);
		
		List<ReadingAlarm> readingAlarmList = selectbuilder.get();
		if(readingAlarmList != null && !readingAlarmList.isEmpty()) 
		{
			for(ReadingAlarm readingAlarm:readingAlarmList) 
			{
				if(readingAlarm.getRule() != null && readingAlarm.getResource() != null) 
				{
					if(ruleIdVsResourceIds.containsKey(readingAlarm.getRule().getId())) 
					{
						List<Long> resourceIds = ruleIdVsResourceIds.get(readingAlarm.getRule().getId());
						resourceIds.add(readingAlarm.getResource().getId());				
					}
					else
					{
						List<Long> resourceIds = new ArrayList<Long>();
						resourceIds.add(readingAlarm.getResource().getId());
						ruleIdVsResourceIds.put(readingAlarm.getRule().getId(), resourceIds);
					}					
				}
			}
			
		}		
		return ruleIdVsResourceIds;
   }


}
