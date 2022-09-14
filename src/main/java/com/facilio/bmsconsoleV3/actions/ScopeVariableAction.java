package com.facilio.bmsconsoleV3.actions;

import com.facilio.beans.GlobalScopeBean;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.scoping.GlobalScopeVariableContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.v3.V3Action;

public class ScopeVariableAction extends V3Action {
    private GlobalScopeVariableContext scopeVariable;

    public GlobalScopeVariableContext getScopeVariable() {
        return scopeVariable;
    }

    public void setScopeVariable(GlobalScopeVariableContext scopeVariable) {
        this.scopeVariable = scopeVariable;
    }

    public String addOrUpdate() throws Exception {
        GlobalScopeBean scopeBean = (GlobalScopeBean) BeanFactory.lookup("ScopeBean");
        FacilioChain chain = TransactionChainFactoryV3.addOrUpdateScopeVariable();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.RECORD, scopeVariable);
        chain.execute();

        Long scopeVariableId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
        if(scopeVariableId > -1l){
            setData(FacilioConstants.ContextNames.SCOPE_VARIABLE,scopeBean.getScopeVariable(scopeVariableId));
        }
        return V3Action.SUCCESS;
    }

    public String delete() throws Exception {
        if(scopeVariable == null) {
            throw new IllegalArgumentException("Scope variable cannot be empty");
        }
        GlobalScopeBean scopeBean = (GlobalScopeBean) BeanFactory.lookup("ScopeBean");
        scopeBean.deleteScopeVariable(scopeVariable.getId());
        setMessage("Global Scope Varibale Deleted");
        return V3Action.SUCCESS;
    }

    public String list() throws Exception {
        GlobalScopeBean scopeBean = (GlobalScopeBean) BeanFactory.lookup("ScopeBean");
        setData(FacilioConstants.ContextNames.SCOPE_VARIABLE,scopeBean.getAllScopeVariable());
        scopeBean.getAllScopeVariable();
        return V3Action.SUCCESS;
    }
}
