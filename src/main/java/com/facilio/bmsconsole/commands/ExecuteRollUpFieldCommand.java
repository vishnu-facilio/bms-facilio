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
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.RollUpField;
import com.facilio.bmsconsole.util.RollUpFieldUtil;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
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
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class ExecuteRollUpFieldCommand extends FacilioCommand implements PostTransactionCommand{
	
	private static final Logger LOGGER = LogManager.getLogger(ExecuteRollUpFieldCommand.class.getName());
	
	private List<ReadingDataMeta> rollUpFieldData = new ArrayList<ReadingDataMeta>();
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		Map<String, List> recordMap = CommonCommandUtil.getRecordMap((FacilioContext) context);
		
		if(recordMap != null && !recordMap.isEmpty()) 
		{		
			for(Map.Entry<String, List> entry : recordMap.entrySet()) 
			{				
				String moduleName = entry.getKey();
				List records = new LinkedList<>(entry.getValue());
				if (moduleName == null || moduleName.isEmpty() || records == null || records.isEmpty()) {
					LOGGER.log(Level.WARN, "Module Name / Records is null/ empty while upating records in rollUpField ==> "+moduleName+"==>"+entry.getValue());
					continue;
				}
				
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(moduleName);
			
				List<RollUpField> triggeringChildFields = RollUpFieldUtil.getRollUpFieldsByChildModuleId(module,true);
				if(triggeringChildFields != null && !triggeringChildFields.isEmpty()) {		
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
						if(triggerChildGroupedIds != null && !triggerChildGroupedIds.isEmpty()){						
							RollUpFieldUtil.aggregateFieldAndAddRollUpFieldData(triggeringChildField, triggerChildGroupedIds, rollUpFieldData);	
						}	
					}
				}			
			}	
		}	
		return false;
	}
		
	@Override
	public boolean postExecute() throws Exception {
		
		if(rollUpFieldData != null && !rollUpFieldData.isEmpty()) 
		{
			for(ReadingDataMeta rollUpData:rollUpFieldData) 
			{			
				FacilioField parentRollUpField = rollUpData.getField();
				Map<String,Object> prop = new HashMap<String,Object>();
				prop.put(parentRollUpField.getName(), rollUpData.getValue());
				
				GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
						.table(parentRollUpField.getModule().getTableName())
						.fields(Collections.singletonList(parentRollUpField))
						.andCondition(CriteriaAPI.getIdCondition(rollUpData.getReadingDataId(), parentRollUpField.getModule()));
				
				updateBuilder.update(prop);
			}	
		}	
		
		return false;
	}

}
