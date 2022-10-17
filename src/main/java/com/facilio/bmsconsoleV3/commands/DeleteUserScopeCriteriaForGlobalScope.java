package com.facilio.bmsconsoleV3.commands;

import com.facilio.beans.GlobalScopeBean;
import com.facilio.bmsconsoleV3.context.scoping.GlobalScopeVariableContext;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Context;

public class DeleteUserScopeCriteriaForGlobalScope extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        GlobalScopeVariableContext scopeVariable = (GlobalScopeVariableContext) context.get(FacilioConstants.ContextNames.RECORD);
        GlobalScopeBean scopeBean = (GlobalScopeBean) BeanFactory.lookup("ScopeBean");
        if(scopeVariable == null) {
            Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
            if(id != null) {
                scopeVariable = scopeBean.getScopeVariable(id);
            }
        }
        if(scopeVariable != null) {
            ScopingUtil.deleteUserscopeConfigForGlobalScope(scopeVariable.getId());
        }
        return false;
    }
}
