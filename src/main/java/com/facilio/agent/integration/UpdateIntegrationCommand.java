package com.facilio.agent.integration;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.command.FacilioCommand;
import com.facilio.chain.FacilioContext;
import com.facilio.fw.BeanFactory;

public class UpdateIntegrationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", AccountUtil.getCurrentOrg().getOrgId());
        Integer updationcount =  bean.updateTable((FacilioContext) context);
        if(updationcount == 1 ){
            return true;
        }
        return false;
    }
}
