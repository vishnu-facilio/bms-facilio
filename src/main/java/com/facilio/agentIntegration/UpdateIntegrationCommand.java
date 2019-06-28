package com.facilio.agentIntegration;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.chain.FacilioContext;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class UpdateIntegrationCommand implements Command {
    @Override
    public boolean execute(Context context) throws Exception {
        ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", AccountUtil.getCurrentOrg().getOrgId());
        Integer updationcount =  bean.updateTable((FacilioContext) context);
        if(updationcount == 1 ){
            return true;
        }
        return false;
    }
}
