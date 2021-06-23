package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.controlaction.context.ControlActionCommandContext;
import com.facilio.controlaction.util.ControlActionUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class AddControlActionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		List<ControlActionCommandContext> commands = (List<ControlActionCommandContext>)context.get(ControlActionUtil.CONTROL_ACTION_COMMANDS);
		
		ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modbean.getModule(FacilioConstants.ContextNames.CONTROL_ACTION_COMMAND_MODULE);
		List<FacilioField> fields = modbean.getAllFields(FacilioConstants.ContextNames.CONTROL_ACTION_COMMAND_MODULE);
		
		InsertRecordBuilder<ControlActionCommandContext> insert = new InsertRecordBuilder<ControlActionCommandContext>()
				.module(module)
				.fields(fields)
				.addRecords(commands);
		insert.save();
		
		return false;
	}

}
