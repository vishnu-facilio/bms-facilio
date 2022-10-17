package com.facilio.bmsconsoleV3.commands;

import com.facilio.beans.GlobalScopeBean;
import com.facilio.bmsconsoleV3.context.scoping.GlobalScopeVariableContext;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class AddUserScopeForGlobalScopeCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        GlobalScopeBean scopeBean = (GlobalScopeBean) BeanFactory.lookup("ScopeBean");
        List<GlobalScopeVariableContext> scopeVariableList = scopeBean.getAllScopeVariable();
        if(CollectionUtils.isNotEmpty(scopeVariableList)) {
            for(GlobalScopeVariableContext globalScopeVariable : scopeVariableList) {
                ScopingUtil.deleteUserscopeConfigForGlobalScope(globalScopeVariable.getId());
                ScopingUtil.addUserscopeConfigForGlobalScope(globalScopeVariable.getId());
            }
        }
        return false;
    }
}
