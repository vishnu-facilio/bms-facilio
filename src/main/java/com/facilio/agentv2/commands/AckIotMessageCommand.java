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

public class AckIotMessageCommand extends AgentV2Command {

    private static final Logger LOGGER = LogManager.getLogger(AckIotMessageCommand.class.getName());


    public static void main(String[] args) {
        try {
            System.out.println(" Hello ");
            String str = null;
            System.out.println(str.toLowerCase());
        } catch (Exception e) {
            System.out.println("exception ");
        }
    }

    @Override
    public boolean executeCommand(Context context) throws Exception {
        //NEW TRY
        long orgId;
        if (context.containsKey(AgentConstants.ORGID)) {
            orgId = (long) context.get(AgentConstants.ORGID);
        } else {
            orgId = AccountUtil.getCurrentAccount().getOrg().getOrgId();
        }
        if (orgId == 0) {
            throw new Exception("orgId set failed ->" + AccountUtil.getCurrentAccount().getOrg().getOrgId());
        }
        ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
        if (containsCheck(AgentConstants.ID, context)) {
            long msgId = (long) context.get(AgentConstants.ID);
            if (containsCheck(AgentConstants.DATA, context)) {
                JSONObject payload = (JSONObject) context.get(AgentConstants.DATA);

                if (containsCheck(AgentConstants.STATUS, context)) {
                    Status status = (Status) context.get(AgentConstants.STATUS);
                    bean.acknowledgeNewPublishedMessage(msgId, status);
                } else {
                    throw new Exception(" error with key status ->" + context);
                }
            } else {
                throw new Exception(" error with key status ->" + context);
            }
        }else {
            throw new Exception(" error with key status ->"+context);
        }
        return false;
    }
}
