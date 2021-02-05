package com.facilio.trigger.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.trigger.context.BaseTriggerContext;
import com.facilio.trigger.context.TriggerAction;
import com.facilio.trigger.util.TriggerUtil;

public class AddOrUpdateTriggerActionAndRelCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		BaseTriggerContext trigger = (BaseTriggerContext)context.get(TriggerUtil.TRIGGER_CONTEXT);
		
		if(trigger.getTriggerActions() != null) {
			
//			Map<String, FacilioField> triggerActionRelFieldMap = FieldFactory.getAsMap(FieldFactory.getTriggerActionRelFields());
			
//			GenericDeleteRecordBuilder delete = new GenericDeleteRecordBuilder()
//					.table(ModuleFactory.getTriggerActionRelModule().getTableName())
//					.andCondition(CriteriaAPI.getCondition(triggerActionRelFieldMap.get("triggerId"), trigger.getId()+"", NumberOperators.EQUALS));
			
//			delete.delete();
			
//			List<TriggerActionRel> rels = new ArrayList<TriggerActionRel>();
			
			for(TriggerAction action : trigger.getTriggerActions()) {
				if(action.getId() < 0) {
					
					Map<String, Object> props = FieldUtil.getAsProperties(action);
					
					GenericInsertRecordBuilder insert = new GenericInsertRecordBuilder()
							.table(ModuleFactory.getTriggerActionModule().getTableName())
							.fields(FieldFactory.getTriggerActionFields())
							.addRecord(props);
					
					insert.save();
					
					action.setId((long)props.get("id"));
				}
				
//				TriggerActionRel rel = new TriggerActionRel(trigger.getId(),action.getId());
//				rels.add(rel);
			}
			
//			GenericInsertRecordBuilder insert = new GenericInsertRecordBuilder()
//					.table(ModuleFactory.getTriggerActionRelModule().getTableName())
//					.fields(FieldFactory.getTriggerActionRelFields())
//					.addRecords(FieldUtil.getAsMapList(rels, TriggerActionRel.class));
//
//			insert.save();
		}
		
		return false;
	}

}
