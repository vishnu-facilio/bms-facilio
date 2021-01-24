package com.facilio.trigger.command;

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
import com.facilio.trigger.context.TriggerInclExclContext;
import com.facilio.trigger.util.TriggerUtil;

public class AddOrUpdateTriggerInclExclCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		BaseTriggerContext trigger = (BaseTriggerContext)context.get(TriggerUtil.TRIGGER_CONTEXT);
		
		Map<String, FacilioField> FieldMAp = FieldFactory.getAsMap(FieldFactory.getTriggerInclExclFields());
		
		GenericDeleteRecordBuilder delete = new GenericDeleteRecordBuilder()
				.table(ModuleFactory.getTriggerInclExclModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(FieldMAp.get("triggerId"), trigger.getId()+"", NumberOperators.EQUALS));
		
		delete.delete();
		
//		if(trigger.getInclExclResources() != null && !trigger.getInclExclResources().isEmpty()) {
//
//			GenericInsertRecordBuilder insert = new GenericInsertRecordBuilder()
//					.table(ModuleFactory.getTriggerInclExclModule().getTableName())
//					.fields(FieldFactory.getTriggerInclExclFields());
//
//			for(TriggerInclExclContext inclExcl : trigger.getInclExclResources()) {
//				inclExcl.setTriggerId(trigger.getId());
//
//				insert.addRecord(FieldUtil.getAsProperties(inclExcl));
//			}
//			insert.save();
//		}
		return false;
	}

}
