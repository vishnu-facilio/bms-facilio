package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
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
		
		if(commands != null) {
			for(ControlActionCommandContext command :commands) {
				FacilioField field = modBean.getField(command.getFieldId());
				if(command.getRdm() == null) {
					ReadingDataMeta rdm = ReadingsAPI.getReadingDataMeta(command.getResource().getId(), field);
					command.setRdm(rdm);
				}
				
			}
		}
		else {
			return true;
		}
		
		return false;
	}

}
