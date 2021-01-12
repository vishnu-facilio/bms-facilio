package com.facilio.trigger.command;

import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.trigger.context.BaseTriggerContext;
import com.facilio.trigger.util.TriggerUtil;
import org.apache.commons.lang3.StringUtils;

public class AddTriggerCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		BaseTriggerContext trigger = (BaseTriggerContext)context.get(TriggerUtil.TRIGGER_CONTEXT);
		if (trigger == null) {
			throw new IllegalArgumentException("Trigger not given");
		}

		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		if (module == null) {
			throw new IllegalArgumentException("Invalid module name");
		}

		trigger.setModuleId(module.getModuleId());

		if (StringUtils.isEmpty(trigger.getName())) {
			throw new IllegalArgumentException("Trigger name cannot be empty");
		}
		if (trigger.getTypeEnum() == null) {
			throw new IllegalArgumentException("Trigger type is not given");
		}
		if (trigger.getEventTypeEnum() == null) {
			throw new IllegalArgumentException("Event type cannot be empty");
		}

		if (trigger.getId() <= 0) {
			// set the trigger active when creating
			trigger.setStatus(true);
		}

		Map<String, Object> props = FieldUtil.getAsProperties(trigger);
		GenericInsertRecordBuilder insert = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getTriggerModule().getTableName())
				.fields(FieldFactory.getTriggerFields())
				.addRecord(props);
		insert.save();
		
		trigger.setId((long)props.get("id"));
		
		return false;
	}

}
