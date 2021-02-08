package com.facilio.apiv3;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.controlaction.context.ControlActionCommandContext;
import com.facilio.fw.BeanFactory;

public class ControlActionCommandAfterFetchCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
        List<ControlActionCommandContext> commands = (List<ControlActionCommandContext>) (((Map<String,Object>)context.get(FacilioConstants.ContextNames.RECORD_MAP)).get(moduleName));
        
        
        for(ControlActionCommandContext command :commands) {
        	command.setField(modBean.getField(command.getFieldId()));
        }
        return false;
    }

}
