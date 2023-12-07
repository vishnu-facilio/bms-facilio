package com.facilio.trigger.command;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.constants.FacilioConstants;

import java.util.*;

import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.trigger.context.TriggerType;
import org.apache.commons.chain.Context;

import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.ModuleFactory;
import com.facilio.trigger.context.BaseTriggerContext;
import com.facilio.trigger.util.TriggerUtil;
import org.apache.commons.collections4.CollectionUtils;

public class DeleteTriggerCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long triggerId = (long) context.get(FacilioConstants.ContextNames.ID);
		EventType eventType = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);
		
		BaseTriggerContext oldTrigger = TriggerUtil.getTrigger(triggerId,eventType);
		long moduleId = oldTrigger.getModuleId();
		TriggerUtil.fillTriggerExtras(Collections.singletonList(oldTrigger), false);

		if (CollectionUtils.isNotEmpty(oldTrigger.getTriggerActions())){
			throw new IllegalArgumentException("Invalid Delete action, Trigger is associated to rule");
		}
		
		GenericDeleteRecordBuilder delete = new GenericDeleteRecordBuilder()
				.table(ModuleFactory.getTriggerModule().getTableName())
				.andCondition(CriteriaAPI.getIdCondition(triggerId, ModuleFactory.getTriggerModule()));
		
		delete.delete();
		
		TriggerUtil.deleteTriggerChildLookups(oldTrigger, null);
		TriggerUtil.deleteTypeRefObj(oldTrigger.getTriggerActions(), oldTrigger.getTypeEnum());
		reOrderTrigger(moduleId);

		return false;
	}

	private static void reOrderTrigger(long moduleId) throws Exception{
		ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule triggerModule = moduleBean.getModule(moduleId);

		FacilioModule module = ModuleFactory.getTriggerModule();
		List<FacilioField> fields = FieldFactory.getTriggerFields();
		Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		List<BaseTriggerContext> triggers = TriggerUtil.getTriggers(triggerModule, null, null, true, false, null, true, TriggerType.MODULE_TRIGGER);

		if (CollectionUtils.isEmpty(triggers)) {
			return;
		}

		List<GenericUpdateRecordBuilder.BatchUpdateContext> batchUpdateList = new ArrayList<>();

		long executionOrder =  0;
		for (BaseTriggerContext trigger : triggers) {
				executionOrder += 1;
				GenericUpdateRecordBuilder.BatchUpdateContext updateVal = new GenericUpdateRecordBuilder.BatchUpdateContext();
				updateVal.addWhereValue("id",trigger.getId());
				updateVal.addUpdateValue("executionOrder", executionOrder);
				batchUpdateList.add(updateVal);
		}

		List<FacilioField> whereFields = new ArrayList<>();
		whereFields.add(fieldMap.get("id"));

		if (CollectionUtils.isNotEmpty(batchUpdateList)) {
			GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
					.table(module.getTableName())
					.fields(Collections.singletonList(fieldMap.get("executionOrder")));
			updateRecordBuilder.batchUpdate(whereFields, batchUpdateList);
		}
	}

}
