package com.facilio.trigger.command;

import java.util.Collections;
import java.util.List;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import org.apache.commons.chain.Context;

import com.facilio.command.FacilioCommand;
import com.facilio.chain.FacilioContext;
import com.facilio.trigger.context.BaseTriggerContext;
import com.facilio.trigger.util.TriggerUtil;
import org.apache.commons.collections.CollectionUtils;

public class ExecuteTriggerCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		long triggerId = (long)context.get(FacilioConstants.ContextNames.ID);
		EventType eventType = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);

		BaseTriggerContext trigger = TriggerUtil.getTrigger(triggerId,eventType);
		if (trigger == null) {
			throw new IllegalArgumentException("Invalid trigger");
		}
		if (!trigger.isActive()) {
			// don't trigger..
			return false;
		}

		if (eventType != null) {
			if (trigger.getEventTypeEnum() != eventType) {
				return false;
			}
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(trigger.getModuleId());
		List<ModuleBaseWithCustomFields> recordList = (List<ModuleBaseWithCustomFields>) context.get(FacilioConstants.ContextNames.RECORD_LIST);

		if (CollectionUtils.isNotEmpty(recordList)) {
			for (ModuleBaseWithCustomFields record : recordList) {
				if (record != null) {
					TriggerUtil.executeTriggerActions(Collections.singletonList(trigger), (FacilioContext) context, module.getName(), record, null);
				}
			}
		}

		return false;
	}

}
