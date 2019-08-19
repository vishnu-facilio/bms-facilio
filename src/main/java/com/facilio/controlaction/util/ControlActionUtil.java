package com.facilio.controlaction.util;

import java.util.List;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.controlaction.context.ControlActionCommandContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class ControlActionUtil {

	public static final String CONTROL_ACTION_COMMANDS = "controlActionCommands";
	public static final String CONTROL_ACTION_COMMANDS_COUNT = "controlActionCommandscount";
	public static final String CONTROL_ACTION_COMMAND_EXECUTED_FROM = "controlActionCommandExecutedFrom"; 
	public static final String CONTROL_ACTION_CONTROLLABLE_POINTS = "controllablePoints"; 
	
	
	public static List<ControlActionCommandContext> getCommands() throws Exception {
		
		ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule controlActionModule = modbean.getModule(FacilioConstants.ContextNames.CONTROL_ACTION_COMMAND_MODULE);
		List<FacilioField> controlActionFields = modbean.getAllFields(FacilioConstants.ContextNames.CONTROL_ACTION_COMMAND_MODULE);
		
		SelectRecordsBuilder<ControlActionCommandContext> selectProject = new SelectRecordsBuilder<ControlActionCommandContext>()
				.module(controlActionModule)
				.select(controlActionFields)
				.beanClass(ControlActionCommandContext.class);
		
		List<ControlActionCommandContext> props = selectProject.get();
		
		for(ControlActionCommandContext prop :props) {
			FacilioField field = modbean.getField(prop.getFieldId());
			prop.setField(field);
			prop.setResource(ResourceAPI.getResource(prop.getResource().getId()));
		}
		return props;
	}
}
