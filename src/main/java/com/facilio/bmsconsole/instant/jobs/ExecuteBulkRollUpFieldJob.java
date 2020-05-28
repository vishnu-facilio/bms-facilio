package com.facilio.bmsconsole.instant.jobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.RollUpField;
import com.facilio.bmsconsole.util.RollUpFieldUtil;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.tasker.job.InstantJob;

public class ExecuteBulkRollUpFieldJob extends InstantJob{
		
	private static final Logger LOGGER = Logger.getLogger(ExecuteBulkRollUpFieldJob.class.getName());
	
	public static final int offsetLimit = 5000; 
	private boolean timedOut = false;
	
	@Override
	public void execute(FacilioContext context) throws Exception {
		
		List<ReadingDataMeta> rollUpFieldData = new ArrayList<ReadingDataMeta>();
		List<RollUpField> triggeringChildFields = new ArrayList<RollUpField>();;
			
		try {
			long processStartTime = System.currentTimeMillis();
			Map<String, Criteria> moduleCriteriaMap = (Map<String, Criteria>) context.get(FacilioConstants.ContextNames.MODULE_CRITERIA_MAP);
			List<Long> rollUpFieldRuleIds = (List<Long>) context.get(FacilioConstants.ContextNames.ROLL_UP_FIELD_IDS);
			if(moduleCriteriaMap == null || moduleCriteriaMap.isEmpty()) {
				String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
				if (moduleName == null || moduleName.isEmpty()) {
					LOGGER.log(Level.WARNING, "Module Name/CriteriaMap is null/empty while bulk updating records in ExecuteBulkRollUpFieldJob ==> "+moduleName);
				}else {
					moduleCriteriaMap = Collections.singletonMap(moduleName, null);
				}
			}
			
			if(moduleCriteriaMap != null && !moduleCriteriaMap.isEmpty()) 
			{		
				for(Entry<String, Criteria> entry : moduleCriteriaMap.entrySet()) 
				{				
					String moduleName = entry.getKey();
					Criteria criteria = entry.getValue();
					if (moduleName == null || moduleName.isEmpty()) {
						LOGGER.log(Level.WARNING, "Module Name / Records is null/ empty in ModuleCriteriaMap in bulk rollUpField Job ==> "+moduleName+" Criteria ==>"+entry.getValue());
						continue;
					}
					
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					FacilioModule module = modBean.getModule(moduleName);
				
					triggeringChildFields = RollUpFieldUtil.getRollUpFieldsByChildModuleId(module,true);	
					executeBulkUpdateForRollUpFieldsInBatch(triggeringChildFields, rollUpFieldData, criteria);
				}						
			}
			else if(rollUpFieldRuleIds != null && !rollUpFieldRuleIds.isEmpty())
			{
				executeBulkUpdateForRollUpFieldsInBatch(RollUpFieldUtil.getRollUpFieldsByIds(rollUpFieldRuleIds, true), rollUpFieldData, null);
			}
			
			if(rollUpFieldData != null && !rollUpFieldData.isEmpty()) 
			{		
				RollUpFieldUtil.updateRollUpFieldParentDataFromRDM(rollUpFieldData);
				LOGGER.info("Time taken for ExecuteBulkRollUpFieldJob Run for moduleCriteriaMap: " +moduleCriteriaMap+ " RollUpFieldRuleIds: " +rollUpFieldRuleIds+ " rollUpFieldData: " +rollUpFieldData+ " with process time as: "+(System.currentTimeMillis() - processStartTime));
				LOGGER.info("Update done OrgId -- " + AccountUtil.getCurrentOrg().getOrgId() + " rollUpFieldData : " + rollUpFieldData);
			}
		}
		catch(Exception e) {
			LOGGER.log(Level.SEVERE, "Error in ExecuteBulkRollUpFieldJob -- ChildFields: "+ triggeringChildFields + " rollUpFieldData: " + rollUpFieldData + 
					" Exception: " + e.getMessage() , e);
		}		
	}
		
	private static String constructChildFieldOffsetKey(long childFieldId, int offsetCount) {
		return (childFieldId + "_" + offsetCount);			
	}
	
	@Override
	public void handleTimeOut() {
		LOGGER.info("ExecuteBulkRollUpFieldJob timed out!!");
		timedOut = true;
		super.handleTimeOut();
	}
	
	private void executeBulkUpdateForRollUpFields(List<RollUpField> triggeringChildFields, List<ReadingDataMeta> rollUpFieldData, Criteria criteria) throws Exception 
	{
		HashMap<String, List<Long>> distinctChildGroupedIdsMap = new HashMap<String, List<Long>>();
		if(triggeringChildFields != null && !triggeringChildFields.isEmpty()) 
		{	
			LOGGER.info("Started OrgId -- " + AccountUtil.getCurrentOrg().getOrgId() + " triggeringChildFieldsSize : " + triggeringChildFields.size());
			for(RollUpField triggeringChildField:triggeringChildFields) 
			{
				int offsetCount = 0;	
				while(!timedOut) 
				{	
					String key = constructChildFieldOffsetKey(triggeringChildField.getChildField().getId(), offsetCount);
					if(offsetCount >= 10000) {
						LOGGER.info(" FieldOffsetKey : "+ key + " triggeringChildFieldContext : " + triggeringChildField + " OrgId -- " +AccountUtil.getCurrentOrg().getOrgId());
					}
					
					List<Long> triggerChildGroupedIds = distinctChildGroupedIdsMap.get(key);
					if(triggerChildGroupedIds == null) {
						triggerChildGroupedIds = RollUpFieldUtil.getDistinctChildModuleRecordIds(triggeringChildField, criteria, offsetCount);
						distinctChildGroupedIdsMap.put(key, triggerChildGroupedIds);
					}
					if(triggerChildGroupedIds == null || triggerChildGroupedIds.isEmpty()) {
						break;
					}
					
					LOGGER.info(" TriggerChildGroupedIds OrgId -- " + AccountUtil.getCurrentOrg().getOrgId() + " triggeringChildField : " + triggeringChildField + " offset : "  +offsetCount+ " triggerChildGroupedIds size " + triggerChildGroupedIds.size());
					RollUpFieldUtil.aggregateFieldAndAddRollUpFieldData(triggeringChildField, triggerChildGroupedIds, rollUpFieldData);	
					offsetCount+=offsetLimit;							
				}
			}
		}			
	}
	
	private void executeBulkUpdateForRollUpFieldsInBatch(List<RollUpField> triggeringChildFields, List<ReadingDataMeta> rollUpFieldData, Criteria criteria) throws Exception 
	{
		HashMap<Long, List<List<Long>>> distinctChildGroupedIdsMap = new HashMap<Long, List<List<Long>>>();
		if(triggeringChildFields != null && !triggeringChildFields.isEmpty()) 
		{	
			LOGGER.info("Started OrgId -- " + AccountUtil.getCurrentOrg().getOrgId() + " triggeringChildFieldsSize : " + triggeringChildFields.size());
			for(RollUpField triggeringChildField:triggeringChildFields) 
			{
				List<List<Long>> triggerChildGroupedIdsList = distinctChildGroupedIdsMap.get(triggeringChildField.getChildField().getId());
				if(triggerChildGroupedIdsList == null) {
					triggerChildGroupedIdsList = RollUpFieldUtil.getDistinctChildModuleRecordIdsInBatch(triggeringChildField, criteria, offsetLimit);
					distinctChildGroupedIdsMap.put(triggeringChildField.getChildField().getId(), triggerChildGroupedIdsList);
				}
				for(List<Long> triggerChildGroupedIds:triggerChildGroupedIdsList) {
					RollUpFieldUtil.aggregateFieldAndAddRollUpFieldData(triggeringChildField, triggerChildGroupedIds, rollUpFieldData);	
					LOGGER.info(" TriggerChildGroupedIds OrgId -- " + AccountUtil.getCurrentOrg().getOrgId() + " triggeringChildField : " + triggeringChildField + " triggerChildGroupedIds size " + triggerChildGroupedIds.size());
				}	
			}
		}			
	}

}
