package com.facilio.trigger.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.trigger.context.TriggerType;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.trigger.context.BaseTriggerContext;
import com.facilio.trigger.util.TriggerUtil;
import org.apache.commons.lang3.StringUtils;

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
        if (module == null && trigger.getType() != TriggerType.TIMESERIES_COMPLETED_TRIGGER.getValue()) {
			throw new IllegalArgumentException("Invalid module name");
		}
        if (module != null) {
            trigger.setModuleId(module.getModuleId());
        }
		if (StringUtils.isEmpty(trigger.getName())) {
			throw new IllegalArgumentException("Trigger name cannot be empty");
		}
		if (trigger.getTypeEnum() == null) {
			throw new IllegalArgumentException("Trigger type is not given");
		}
		if (trigger.getEventTypeEnum() == null) {
			throw new IllegalArgumentException("Event type cannot be empty");
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

		Map<String, Object> props = FieldUtil.getAsProperties(trigger);
		if (trigger.getId() < 0) {
			for (FacilioModule mod : moduleOrder) {
				GenericInsertRecordBuilder insert = new GenericInsertRecordBuilder()
						.table(mod.getTableName())
						.fields(triggerFieldMap.get(mod))
						.addRecord(props);
				insert.save();
				trigger.setId((long) props.get("id"));
			}
		} else {
			for (FacilioModule mod : moduleOrder) {
				GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
						.table(mod.getTableName())
						.fields(triggerFieldMap.get(mod))
						.andCondition(CriteriaAPI.getIdCondition(trigger.getId(), ModuleFactory.getTriggerModule()));
				builder.update(props);
			}
		}
		
		return false;
	}

	private FacilioModule getTriggerModule(TriggerType triggerType) {
		switch (triggerType) {
			case MODULE_TRIGGER:
			case SLA_DUE_DATE_TRIGGER:
			case SCORING_RULE_TRIGGER:
            case TIMESERIES_COMPLETED_TRIGGER:
				return ModuleFactory.getTriggerModule();

			default:
				throw new IllegalArgumentException("Invalid trigger type");
		}
	}

	public List<FacilioField> getTriggerFields(TriggerType triggerType) {
		switch (triggerType) {
			case MODULE_TRIGGER:
			case SLA_DUE_DATE_TRIGGER:
			case SCORING_RULE_TRIGGER:
            case TIMESERIES_COMPLETED_TRIGGER:
				return FieldFactory.getTriggerFields();

			default:
				throw new IllegalArgumentException("Invalid trigger type");
		}
	}
}
