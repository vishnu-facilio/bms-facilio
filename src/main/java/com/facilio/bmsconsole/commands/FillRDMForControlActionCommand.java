package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.controlaction.context.ControlActionCommandContext;
import com.facilio.controlaction.util.ControlActionUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;

public class FillRDMForControlActionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		List<ControlActionCommandContext> commands = (List<ControlActionCommandContext>)context.get(ControlActionUtil.CONTROL_ACTION_COMMANDS);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<ControlActionCommandContext> removableCommands = new ArrayList<ControlActionCommandContext>();
		for(ControlActionCommandContext command :commands) {
			FacilioField field = modBean.getField(command.getFieldId());
			ReadingDataMeta rdm = ReadingsAPI.getReadingDataMeta(command.getResource().getId(), field);
			if(rdm == null || !rdm.isControllable() || rdm.getReadingTypeEnum() != ReadingDataMeta.ReadingType.WRITE) {
				removableCommands.add(command);
			}
			else {
				command.setRdm(rdm);
			}
		}
		commands.removeAll(removableCommands);
		
		return false;
	}

}
