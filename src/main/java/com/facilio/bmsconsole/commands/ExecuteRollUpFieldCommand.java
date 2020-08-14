package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.RollUpField;
import com.facilio.bmsconsole.util.RollUpFieldUtil;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.chain.FacilioContext;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.AggregateOperator;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class ExecuteRollUpFieldCommand extends FacilioCommand implements PostTransactionCommand{
	
	private static final Logger LOGGER = Logger.getLogger(ExecuteRollUpFieldCommand.class.getName());
	
	private LinkedHashMap<RollUpField,List<Long>> triggeringChildFieldVsChildGroupedIds = new LinkedHashMap<RollUpField,List<Long>>();

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		try {
			long processStartTime = System.currentTimeMillis();
			Map<String, List> recordMap = CommonCommandUtil.getRecordMap((FacilioContext) context);
			Map<String, Map<Long, List<UpdateChangeSet>>> changeSetMap = CommonCommandUtil.getChangeSetMap((FacilioContext) context);
				
			if(recordMap != null && !recordMap.isEmpty()) 
			{		
				for(Map.Entry<String, List> entry : recordMap.entrySet()) 
				{				
					String moduleName = entry.getKey();
					List records = new LinkedList<>(entry.getValue());
					if (moduleName == null || moduleName.isEmpty() || records == null || records.isEmpty()) {
						LOGGER.log(Level.WARNING, "Module Name / Records is null/ empty while upating records in rollUpField ==> "+moduleName+"==>"+entry.getValue());
						continue;
					}
					
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					FacilioModule module = modBean.getModule(moduleName);
					List<RollUpField> triggeringChildFields = new ArrayList<RollUpField>();
					triggeringChildFields = RollUpFieldUtil.getRollUpFieldsByChildModuleId(module,true);
					
					if(triggeringChildFields != null && !triggeringChildFields.isEmpty()) 
					{	
						for(RollUpField triggeringChildField:triggeringChildFields) 
						{
							List<Long> triggerChildGroupedIds = new ArrayList<Long>();
							for(Object record:records) 
							{
								Map<String, Object> recordData = FieldUtil.getAsProperties(record);
								if(recordData.get(triggeringChildField.getChildField().getName()) != null) {	
									if(triggeringChildField.getChildField() instanceof LookupField) {
										Map<String, Object> lookUpObject = (Map<String, Object>) recordData.get(triggeringChildField.getChildField().getName());
										if(lookUpObject.get("id") != null) {
											triggerChildGroupedIds.add((long)lookUpObject.get("id"));
										}
									}
									else {
										triggerChildGroupedIds.add((long)recordData.get(triggeringChildField.getChildField().getName()));
									}
								}
							}
							triggeringChildFieldVsChildGroupedIds.put(triggeringChildField, triggerChildGroupedIds);								
						}
					}			
				}	
			}	
			LOGGER.info("Time taken for ExecuteRollUpFieldCommand for triggeringChildFieldVsChildGroupedIds: " +triggeringChildFieldVsChildGroupedIds+  " is: "+(System.currentTimeMillis() - processStartTime));

		}
		catch(Exception e) {
			LOGGER.log(Level.SEVERE, "Error in executeRollUpFieldCommand -- triggeringChildFieldVsChildGroupedIds: " +triggeringChildFieldVsChildGroupedIds+ 
					" Exception: " + e.getMessage() , e);
		}
			
		return false;
	}
		
	@Override
	public boolean postExecute() throws Exception {
		
		if(triggeringChildFieldVsChildGroupedIds != null && MapUtils.isNotEmpty(triggeringChildFieldVsChildGroupedIds)) 
		{
			try {
				List<ReadingDataMeta> rollUpFieldData = new ArrayList<ReadingDataMeta>();
				for(RollUpField triggeringChildField :triggeringChildFieldVsChildGroupedIds.keySet()) 
				{
					List<Long> triggerChildGroupedIds = triggeringChildFieldVsChildGroupedIds.get(triggeringChildField);
					if(triggerChildGroupedIds != null && !triggerChildGroupedIds.isEmpty())
					{						
						RollUpFieldUtil.aggregateFieldAndAddRollUpFieldData(triggeringChildField, triggerChildGroupedIds, rollUpFieldData);	
					}
				}
				if(rollUpFieldData != null && !rollUpFieldData.isEmpty()) 
				{	
					RollUpFieldUtil.updateRollUpFieldParentDataFromRDM(rollUpFieldData);
				}
			}
			catch(Exception e) {
				LOGGER.log(Level.SEVERE, "Error while updating ExecuteRollUpFieldCommand in Post Execute -- triggeringChildFieldVsChildGroupedIds: " +triggeringChildFieldVsChildGroupedIds+ 
						" Exception: " + e.getMessage() , e);
			}	
		}	
		
		return false;
	}

}
