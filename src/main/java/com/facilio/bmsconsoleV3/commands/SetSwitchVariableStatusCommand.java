package com.facilio.bmsconsoleV3.commands;

import com.facilio.beans.GlobalScopeBean;
import com.facilio.bmsconsoleV3.context.scoping.GlobalScopeVariableContext;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Context;
import org.apache.xpath.operations.Bool;

public class SetSwitchVariableStatusCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Boolean status = (Boolean) context.get(FacilioConstants.ContextNames.STATUS);
        Long scopeVariableId = (Long) context.get(FacilioConstants.ContextNames.ID);
        GlobalScopeBean scopeBean = (GlobalScopeBean) BeanFactory.lookup("ScopeBean");
        GlobalScopeVariableContext scopeVariable = scopeBean.getScopeVariable(scopeVariableId);
        if (scopeVariable == null) {
            throw new IllegalArgumentException("Scope variable does not exists");
        }
        if(scopeVariable.getAppId() == null){
            throw new IllegalArgumentException("Appid cannot be null");
        }
        if(status == null){
            throw new IllegalArgumentException("Status cannot be null");
        }
        scopeVariable.setShowSwitch(status);
        ScopingUtil.checkUserSwitchAndThrowError(scopeVariable);
        scopeBean.setSwitchStatus(scopeVariable.getAppId(),scopeVariableId,status);
        return false;
    }
}
