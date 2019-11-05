package com.facilio.agentnew;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import org.apache.commons.chain.Context;

public class AddAgent extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", AccountUtil.getCurrentOrg().getOrgId());
        FacilioAgent agent = (FacilioAgent) context.get(AgentConstants.AGENT);
        if(agent == null){
            throw new Exception(" Agent Can't be null");
        }
        context.put(FacilioConstants.ContextNames.TABLE_NAME,AgentConstants.AGENT_TABLE);
        context.put(FacilioConstants.ContextNames.FIELDS, FieldFactory.getNewAgentDataFields());
        context.put(FacilioConstants.ContextNames.TO_INSERT_MAP, FieldUtil.getAsProperties(agent));
        bean.genericInsert(context);
        if(context.containsKey(FacilioConstants.ContextNames.ID) && ( ((Number)context.get(FacilioConstants.ContextNames.ID)).intValue() > 0) ){
            return true;
        }
        return false;
    }
}
