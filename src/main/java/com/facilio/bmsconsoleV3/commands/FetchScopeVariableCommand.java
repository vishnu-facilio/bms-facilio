package com.facilio.bmsconsoleV3.commands;

import com.facilio.beans.GlobalScopeBean;
import com.facilio.bmsconsoleV3.context.scoping.GlobalScopeVariableContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Context;

public class FetchScopeVariableCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Long scopeVariableId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
        GlobalScopeBean scopeBean = (GlobalScopeBean) BeanFactory.lookup("ScopeBean");
        GlobalScopeVariableContext scopeVariableContext = scopeBean.getScopeVariable(scopeVariableId);
        context.put(FacilioConstants.ContextNames.SCOPE_VARIABLE,scopeVariableContext);

        return false;
    }
}
