package com.facilio.agentv2.controller.commands;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.Controller;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class AddControllerChildCommand extends FacilioCommand {

    private static final Logger LOGGER = LogManager.getLogger(AddControllerChildCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Controller controller = (Controller) context.get(AgentConstants.CONTROLLERS);
        LOGGER.info("child controller from context inside command "+controller);
        ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", controller.getOrgId());
        if( bean.addChildController(controller) > 0){
            return true;
        }
        return false;
    }
}
