package com.facilio.bmsconsoleV3.commands;

import com.facilio.beans.GlobalScopeBean;
import com.facilio.bmsconsoleV3.context.scoping.GlobalScopeVariableContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Context;

public class FetchScopeVariableListCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Long appId = (Long) context.get(FacilioConstants.ContextNames.APP_ID);
        String search = (String) context.get(FacilioConstants.ContextNames.SEARCH);
        int page = (int) context.get(FacilioConstants.ContextNames.PAGE);
        int perPage = (int) context.get(FacilioConstants.ContextNames.PER_PAGE);

        GlobalScopeBean scopeBean = (GlobalScopeBean) BeanFactory.lookup("ScopeBean");
        context.put(FacilioConstants.ContextNames.COUNT,scopeBean.getScopeVariableCount(appId,search));
        context.put(FacilioConstants.ContextNames.SCOPE_VARIABLE_LIST,scopeBean.getAllScopeVariable(appId,page,perPage,search,false));


        return false;
    }
}
