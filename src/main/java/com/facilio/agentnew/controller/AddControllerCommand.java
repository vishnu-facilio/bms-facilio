package com.facilio.agentnew.controller;

import com.facilio.agentnew.AgentConstants;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class AddControllerCommand extends FacilioCommand {

    private static final Logger LOGGER = LogManager.getLogger(AddControllerCommand.class.getName());


    @Override
    public boolean executeCommand(Context context) throws Exception {
        Controller controller = (Controller) context.get(AgentConstants.CONTROLLERS);
        LOGGER.info(" controller from context inside command "+controller);
        ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", controller.getOrgId());
        if( bean.addAgentController(controller) > 0){
            LOGGER.info(" returning true for parent ");
            return true;
        }
        return false;
    }
}
