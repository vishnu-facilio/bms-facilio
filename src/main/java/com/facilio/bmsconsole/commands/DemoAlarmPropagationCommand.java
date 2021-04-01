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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.yaml.snakeyaml.Yaml;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.DemoRollUpYearlyCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.facilio.bmsconsole.context.ReadingAlarm;
import com.facilio.bmsconsole.enums.RuleJobType;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.bmsconsole.util.NewEventAPI;
import com.facilio.bmsconsole.util.ReadingRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.tasker.job.JobContext;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;

public class DemoAlarmPropagationCommand extends FacilioCommand{
	
	private static final Logger LOGGER = LogManager.getLogger(DemoAlarmPropagationCommand.class.getName());	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		Long orgId=(Long) context.get(ContextNames.DEMO_ROLLUP_JOB_ORG);
		Long jobId=(Long) context.get(ContextNames.JOB);
		ZonedDateTime currentTimeZdt = (ZonedDateTime) context.get(ContextNames.START_TIME);
		long currentTime = currentTimeZdt.toInstant().toEpochMilli();
		
		Map<String, String> orgInfoMap = CommonCommandUtil.getOrgInfo(FacilioConstants.OrgInfoKeys.EXECUTE_READING_RULE_THROUGH_AUTOMATED_HISTORY);
    	if (orgInfoMap != null && MapUtils.isNotEmpty(orgInfoMap)) {
    		String executeReadingRuleThroughAutomatedHistoryProp = orgInfoMap.get(FacilioConstants.OrgInfoKeys.EXECUTE_READING_RULE_THROUGH_AUTOMATED_HISTORY);
			if (executeReadingRuleThroughAutomatedHistoryProp != null && !executeReadingRuleThroughAutomatedHistoryProp.isEmpty() && StringUtils.isNotEmpty(executeReadingRuleThroughAutomatedHistoryProp) && Boolean.valueOf(executeReadingRuleThroughAutomatedHistoryProp) == Boolean.FALSE) {
        		return false;
			}
    	}
		
		FacilioContext runThroughRuleChainContext = new FacilioContext();
		runThroughRuleChainContext.put(FacilioConstants.ContextNames.RULE_JOB_TYPE, RuleJobType.READING_ALARM.getIndex());
		
		if (AccountUtil.getCurrentOrg() != null && (AccountUtil.getCurrentOrg().getOrgId() == 339 || AccountUtil.getCurrentOrg().getOrgId() == 1 || AccountUtil.getCurrentOrg().getOrgId() == 405)) {
			JSONObject jobProps = BmsJobUtil.getJobProps(jobId, "DemoAlarmPropagationJob");
			if(jobProps == null) {
				LOGGER.error("DemoAlarmPropagationJob empty jobProps");
				return false;
			}
			Integer jobInterval =Integer.valueOf(String.valueOf((Long)jobProps.get("jobInterval")));	
			if(jobInterval == null && jobInterval <= 0) {
				LOGGER.error("DemoAlarmPropagationJob empty interval");
				return false;
			}			
//			currentTime = DateTimeUtil.addMinutes(currentTime, -15);
			
			long dataInterval=15*60*1000; //15minutes
			long endTime = (currentTime/dataInterval) * dataInterval;

			long startTime = DateTimeUtil.addMinutes(endTime, -jobInterval);
			startTime = (startTime/dataInterval) * dataInterval;
			endTime = endTime-1000;
			
			LOGGER.info("DemoAlarmPropagationJob jobProps: "+jobProps+ " startTime: "+startTime+" endTime: "+endTime);	

			runThroughRuleChainContext.put(FacilioConstants.ContextNames.DATE_RANGE, new DateRange(startTime, endTime));
		}
		else {
			long yesterdayTime = DateTimeUtil.addDays(currentTime, -1);
			runThroughRuleChainContext.put(FacilioConstants.ContextNames.DATE_RANGE, new DateRange(DateTimeUtil.getDayStartTimeOf(yesterdayTime), DateTimeUtil.getDayEndTimeOf(yesterdayTime)));
		}
		
		HashMap<Long, List<Long>> ruleIdVsResourceIds = getAllRulesFromReadingAlarms(jobId);
		
		if(ruleIdVsResourceIds != null && MapUtils.isNotEmpty(ruleIdVsResourceIds)) 
		{
			LOGGER.info("Daily DemoAlarmPropagationJob Started for the rules and resourceIds " +ruleIdVsResourceIds+ " with rules size: "+ruleIdVsResourceIds.size());
			
			for(long ruleId :ruleIdVsResourceIds.keySet())
			{
				List<Long> resourceIds = ruleIdVsResourceIds.get(ruleId);
				JSONObject loggerInfo = new JSONObject();
				loggerInfo.put("rule", ruleId);
				loggerInfo.put("resource", resourceIds);
				loggerInfo.put("skipLoggerUpdate", true);
				runThroughRuleChainContext.put(FacilioConstants.ContextNames.HISTORICAL_RULE_LOGGER_PROPS, loggerInfo);
				FacilioChain runThroughRuleChain = TransactionChainFactory.runThroughHistoricalRuleChain();
				runThroughRuleChain.execute(runThroughRuleChainContext);
				LOGGER.info("Daily Demo Historical rule evaluation has been started for the given rule " +ruleId+ " with resourceIds: "+resourceIds);
			}	
		} 	 
		return false;
	}

   private HashMap<Long,List<Long>> getAllRulesFromReadingAlarms(Long jobId) throws Exception
   {
	   if(jobId == null) {
		   return null;
	   }
	   
	   	HashMap<Long,List<Long>> ruleIdVsResourceIds = new HashMap<Long,List<Long>>();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		if (AccountUtil.getCurrentOrg() != null && (AccountUtil.getCurrentOrg().getOrgId() == 339 || AccountUtil.getCurrentOrg().getOrgId() == 1 || AccountUtil.getCurrentOrg().getOrgId() == 405)) {
			Criteria criteria = new Criteria();
			criteria.addAndCondition(CriteriaAPI.getCondition("RULE_TYPE", "ruleType", ""+RuleType.READING_RULE.getIntVal(), NumberOperators.EQUALS));
			criteria.addAndCondition(CriteriaAPI.getCondition("STATUS", "status", ""+true, BooleanOperators.IS));
			
			AssetCategoryContext assetCategory = AssetsAPI.getCategory("AHU");
			if(AccountUtil.getCurrentOrg().getOrgId() == 339) {
				if(jobId == 339l && assetCategory != null && assetCategory.getId() > 0) {	
					criteria.addAndCondition(CriteriaAPI.getCondition("ASSET_CATEGORY_ID", "assetCategoryId", ""+assetCategory.getId(), NumberOperators.EQUALS));
				}
				else {
					criteria.addAndCondition(CriteriaAPI.getCondition("ASSET_CATEGORY_ID", "assetCategoryId", ""+jobId, NumberOperators.EQUALS));
				}
			}
			
			List<ReadingRuleContext> rules = ReadingRuleAPI.getReadingRules(criteria);
			
			if(rules != null && !rules.isEmpty()) {
				for(ReadingRuleContext rule:rules) {
					ReadingRuleAPI.setMatchedResources(rule);
					if(rule.getMatchedResources() != null && MapUtils.isNotEmpty(rule.getMatchedResources()) && rule.getMatchedResources().keySet() != null) {
						ruleIdVsResourceIds.put(rule.getId(), new ArrayList<Long>(rule.getMatchedResources().keySet()));
					}	
				}	
			}			
			return ruleIdVsResourceIds;
		}
		else 
		{
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.NEW_READING_ALARM);
	    	List<FacilioField> allFields = modBean.getAllFields(module.getName());	

	    	List<LookupField> lookupFields = NewAlarmAPI.getLookupFields(BaseAlarmContext.Type.READING_ALARM);
			SelectRecordsBuilder<ReadingAlarm> selectbuilder = new SelectRecordsBuilder<ReadingAlarm>()
					.module(module)
					.select(allFields)
					.beanClass(ReadingAlarm.class);
			
			if (CollectionUtils.isNotEmpty(lookupFields)) {
				selectbuilder.fetchSupplements(lookupFields);
			}
			
			List<ReadingAlarm> readingAlarmList = selectbuilder.get();
			if(readingAlarmList != null && !readingAlarmList.isEmpty()) 
			{
				for(ReadingAlarm readingAlarm:readingAlarmList) 
				{
					if(readingAlarm.getRule() != null && readingAlarm.getResource() != null && readingAlarm.getRule().isActive()) 
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


}
