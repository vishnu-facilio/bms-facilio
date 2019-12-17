package com.facilio.agentv2.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.fw.constants.Status;
import com.facilio.agentv2.AgentConstants;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

public class AckIotMessageCommand extends AgentV2Command{

    private static final Logger LOGGER = LogManager.getLogger(AckIotMessageCommand.class.getName());


    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", AccountUtil.getCurrentAccount().getOrg().getOrgId());
        if(containsCheck(AgentConstants.ID,context)){
            long msgId = (long) context.get(AgentConstants.ID);
            if(containsCheck(AgentConstants.DATA,context)){
                JSONObject jsonObject = (JSONObject) context.get(AgentConstants.DATA);
                if(containsCheck(AgentConstants.STATUS,context)){
                    Status status = (Status) context.get(AgentConstants.STATUS);
                    bean.acknowledgeNewPublishedMessage(msgId, status, jsonObject);
                }else {
                    throw new Exception(" error with key status ->"+context);
                }
            }else {
                throw new Exception(" error with key status ->"+context);
            }
        }else {
            throw new Exception(" error with key status ->"+context);
        }
        return false;
    }
}
