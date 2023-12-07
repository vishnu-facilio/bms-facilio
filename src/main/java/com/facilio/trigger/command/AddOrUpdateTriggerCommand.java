package com.facilio.trigger.command;

import java.util.*;

import com.facilio.agentv2.triggers.PostTimeseriesTriggerContext;
import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.trigger.context.TriggerType;
import org.apache.commons.chain.Context;

import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.trigger.context.BaseTriggerContext;
import com.facilio.trigger.util.TriggerUtil;
import org.apache.commons.collections4.MapUtils;


public class AddOrUpdateTriggerCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		BaseTriggerContext trigger = (BaseTriggerContext)context.get(TriggerUtil.TRIGGER_CONTEXT);
		if (trigger == null) {
			throw new IllegalArgumentException("Trigger not given");
		}

		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);

		if (module != null) {
			trigger.setModuleId(module.getModuleId());
		}

		if (trigger.getId() <= 0 && trigger.getStatus() == null) {
			// set the trigger active when creating
			trigger.setStatus(true);
		}

		if (trigger.getInternal() == null) {
			trigger.setInternal(false);
		}

		if (trigger.getIsDefault() == null) {
			trigger.setIsDefault(false);
		}

		trigger.validateTrigger();
		if (!trigger.isValidated()) {
			throw new IllegalArgumentException("Trigger should be validated. If you extended the BaseTriggerContext, make " +
					"sure you call super.validateTrigger() method.");
		}
		
		addChildLookups(trigger.getTypeEnum(), trigger);

		FacilioModule triggerModule = getTriggerModule(trigger.getTypeEnum());
		List<FacilioModule> moduleOrder = new ArrayList<>();
		while (triggerModule != null) {
			moduleOrder.add(0, triggerModule);
			triggerModule = triggerModule.getExtendModule();
		}
		List<FacilioField> triggerFields = getTriggerFields(trigger.getTypeEnum());
		Map<FacilioModule, List<FacilioField>> triggerFieldMap = new HashMap<>();
		for (FacilioField field : triggerFields) {
			FacilioModule fieldModule = field.getModule();
			List<FacilioField> list = triggerFieldMap.get(fieldModule);
			if (list == null) {
				list = new ArrayList<>();
				triggerFieldMap.put(fieldModule, list);
			}
			list.add(field);
		}

		Map<String, Object> props = new HashMap<>();
		if (trigger.getId() < 0) {
			trigger.setExecutionOrder(getTriggerMaxExecutionOrder(trigger.getModuleId()) + 1);
			props = FieldUtil.getAsProperties(trigger);
			for (FacilioModule mod : moduleOrder) {
				GenericInsertRecordBuilder insert = new GenericInsertRecordBuilder()
						.table(mod.getTableName())
						.fields(triggerFieldMap.get(mod))
						.addRecord(props);
				insert.save();
				trigger.setId((long) props.get("id"));
			}
		} else {
			
			BaseTriggerContext oldTrigger = TriggerUtil.getTrigger(trigger.getId(),trigger.getEventTypeEnum());
			if (oldTrigger.getTypeEnum() != trigger.getTypeEnum()) {
				throw new IllegalArgumentException("Trigger type cannot be changed");
			}
			props = FieldUtil.getAsProperties(trigger);
			for (FacilioModule mod : moduleOrder) {
				GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
						.table(mod.getTableName())
						.fields(triggerFieldMap.get(mod))
						.andCondition(CriteriaAPI.getIdCondition(trigger.getId(), mod));
				builder.update(props);
			}
			
			TriggerUtil.deleteTriggerChildLookups(oldTrigger, trigger);
		}
		
		return false;
	}

	public static FacilioModule getTriggerModule(TriggerType triggerType) {
		switch (triggerType) {
			case MODULE_TRIGGER:
			case SLA_DUE_DATE_TRIGGER:
			case SCORING_RULE_TRIGGER:
				return ModuleFactory.getTriggerModule();
            case AGENT_TRIGGER:
                return ModuleFactory.getAgentTriggerModule();
			default:
				throw new IllegalArgumentException("Invalid trigger type");
		}
	}

	public static Class getTriggerClass(TriggerType triggerType) {
		switch (triggerType) {
			case MODULE_TRIGGER:
			case SLA_DUE_DATE_TRIGGER:
			case SCORING_RULE_TRIGGER:
				return BaseTriggerContext.class;
            case AGENT_TRIGGER:
                return PostTimeseriesTriggerContext.class;
			default:
				throw new IllegalArgumentException("Invalid trigger type");
		}
	}

	public static List<FacilioField> getTriggerFields(TriggerType triggerType) {
		switch (triggerType) {
			case MODULE_TRIGGER:
			case SLA_DUE_DATE_TRIGGER:
			case SCORING_RULE_TRIGGER:
                return FieldFactory.getTriggerFields();
            case AGENT_TRIGGER:
				return FieldFactory.getAgentTriggerFields();
			default:
				throw new IllegalArgumentException("Invalid trigger type");
		}
	}
	
	
	private void addChildLookups(TriggerType triggerType, BaseTriggerContext trigger) throws Exception {
		switch(triggerType) {
			case  AGENT_TRIGGER:
				addAgentTriggerLookups((PostTimeseriesTriggerContext) trigger);
				break;
		}
	}
	private void addAgentTriggerLookups(PostTimeseriesTriggerContext trigger) throws Exception {
		if (trigger.getCriteria() != null && !trigger.getCriteria().isEmpty()) {
			long criteriaId = CriteriaAPI.addCriteria(trigger.getCriteria());
			trigger.setCriteriaId(criteriaId);
		}
	}


	public static Integer getTriggerMaxExecutionOrder(long moduleId) throws Exception {
		 Integer triggerMaxExecutionOrder = null;

		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getTriggerFields());
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getTriggerModule().getTableName())
				.select(Arrays.asList(fieldMap.get("moduleId")))
				.aggregate(BmsAggregateOperators.NumberAggregateOperator.MAX, fieldMap.get("executionOrder"))
				.andCondition(CriteriaAPI.getCondition("MODULE_ID","moduleId", String.valueOf(moduleId),NumberOperators.EQUALS));

		Map<String, Object> props = builder.fetchFirst();

		triggerMaxExecutionOrder = (MapUtils.isNotEmpty(props) && props.get("executionOrder") != null) ? (Integer) props.get("executionOrder") : 0;

		return triggerMaxExecutionOrder;
	}
	
	
}
