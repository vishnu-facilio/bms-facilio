package com.facilio.bmsconsoleV3.commands;

import com.facilio.beans.GlobalScopeBean;
import com.facilio.bmsconsoleV3.context.scoping.GlobalScopeVariableContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Context;

public class DeleteGlobalScopeVariableCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
        GlobalScopeBean scopeBean = (GlobalScopeBean) BeanFactory.lookup("ScopeBean");

        GlobalScopeVariableContext scopeVariable = scopeBean.getScopeVariable(id);
        if (scopeVariable == null) {
            throw new IllegalArgumentException("Scope variable does not exists");
        }
        if(scopeVariable.getAppId() == null){
            throw new IllegalArgumentException("Appid cannot be null");
        }
        scopeBean.deleteScopeVariable(id,scopeVariable.getAppId());
        return false;
    }
}
