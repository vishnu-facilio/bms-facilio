package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.controlaction.context.ControlActionCommandContext;
import com.facilio.controlaction.util.ControlActionUtil;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.UpdateRecordBuilder;

public class MarkPublishedCommandStatusCommand extends FacilioCommand {

	private static final Logger LOGGER = LogManager.getLogger(MarkPublishedCommandStatusCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<ControlActionCommandContext> commands = (List<ControlActionCommandContext>)context.get(ControlActionUtil.CONTROL_ACTION_COMMANDS);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		for(ControlActionCommandContext command : commands) {
			command.setStatus(ControlActionCommandContext.Status.SUCCESS.getIntVal());
			
			UpdateRecordBuilder<ControlActionCommandContext> update = new UpdateRecordBuilder<ControlActionCommandContext>()
					.moduleName(FacilioConstants.ContextNames.CONTROL_ACTION_COMMAND_MODULE)
					.fields(modBean.getAllFields(FacilioConstants.ContextNames.CONTROL_ACTION_COMMAND_MODULE))
					.andCondition(CriteriaAPI.getIdCondition(command.getId(), modBean.getModule(FacilioConstants.ContextNames.CONTROL_ACTION_COMMAND_MODULE)));
			
			update.update(command);
		}
		
		return false;
	}

}
