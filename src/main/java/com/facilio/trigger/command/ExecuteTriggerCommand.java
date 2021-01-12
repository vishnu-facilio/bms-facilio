package com.facilio.trigger.command;

import java.util.Collections;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.chain.FacilioContext;
import com.facilio.trigger.context.BaseTriggerContext;
import com.facilio.trigger.util.TriggerUtil;

public class ExecuteTriggerCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		long triggerId = (long)context.get(FacilioConstants.ContextNames.ID);
		BaseTriggerContext trigger = TriggerUtil.getTrigger(triggerId);
		if (trigger == null) {
			throw new IllegalArgumentException("Invalid trigger");
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(trigger.getModuleId());
		long recordId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);

		ModuleBaseWithCustomFields record = RecordAPI.getRecord(module.getName(), recordId);
		if (record != null) {
			TriggerUtil.executeTriggerActions(Collections.singletonList(trigger), (FacilioContext) context, module.getName(), record, null);
		}

		return false;
	}

}
