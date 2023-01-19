package com.facilio.bmsconsoleV3.actions;

import com.facilio.beans.GlobalScopeBean;
import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.scoping.GlobalScopeVariableContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.v3.V3Action;

public class ScopeVariableAction extends V3Action {
    private GlobalScopeVariableContext scopeVariable;
    private Long appId;
    private Boolean status;
    private String lookupModuleName;
    private String applicableModuleName;

    public String getLookupModuleName() {
        return lookupModuleName;
    }

    public void setLookupModuleName(String lookupModuleName) {
        this.lookupModuleName = lookupModuleName;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public GlobalScopeVariableContext getScopeVariable() {
        return scopeVariable;
    }

    public void setScopeVariable(GlobalScopeVariableContext scopeVariable) {
        this.scopeVariable = scopeVariable;
    }

    public String addOrUpdate() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.addOrUpdateScopeVariable();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.RECORD, scopeVariable);
        chain.execute();
        setData(FacilioConstants.ContextNames.SCOPE_VARIABLE, context.get(FacilioConstants.ContextNames.SCOPE_VARIABLE));
        return V3Action.SUCCESS;
    }

    public String delete() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.deleteGlobalScopeVariable();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, scopeVariable.getId());
        chain.execute();
        setMessage("Global Scope Varibale Deleted");
        return V3Action.SUCCESS;
    }

    public String list() throws Exception {
        FacilioChain chain = ReadOnlyChainFactoryV3.getScopeVariableList();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.APP_ID, appId);
        context.put(FacilioConstants.ContextNames.SEARCH, getSearch());
        context.put(FacilioConstants.ContextNames.PAGE, getPage());
        context.put(FacilioConstants.ContextNames.PER_PAGE, getPerPage());
        chain.execute();

        if(appId == null) {
            throw new IllegalArgumentException("Application id cannot be empty");
        }
        setMeta("totalCount",context.get(FacilioConstants.ContextNames.COUNT));
        setData(FacilioConstants.ContextNames.SCOPE_VARIABLE,context.get(FacilioConstants.ContextNames.SCOPE_VARIABLE_LIST));
        return V3Action.SUCCESS;
    }

    public String getSwitchVariable() throws Exception {
        GlobalScopeBean scopeBean = (GlobalScopeBean) BeanFactory.lookup("ScopeBean");
        setData(FacilioConstants.ContextNames.SWITCH_VARIABLE,scopeBean.getSwitchVariable());
        scopeBean.getSwitchVariable();
        return V3Action.SUCCESS;

    }
    public String detail() throws Exception {
        FacilioChain chain = ReadOnlyChainFactoryV3.getScopeVariable();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.RECORD_ID,getId());
        chain.execute();
        setData(FacilioConstants.ContextNames.SCOPE_VARIABLE,context.get(FacilioConstants.ContextNames.SCOPE_VARIABLE));
        return V3Action.SUCCESS;
    }

    public String setStatus() throws Exception {

        FacilioChain chain = TransactionChainFactoryV3.setGlobalScopeVariableStatus();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID,getId());
        context.put(FacilioConstants.ContextNames.STATUS,getStatus());
        chain.execute();

        return SUCCESS;
    }

    public String setSwitchStatus() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.setSwitchStatus();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, getId());
        context.put(FacilioConstants.ContextNames.STATUS, getStatus());
        chain.execute();
        return SUCCESS;
    }

    public String getScopeMetaData() throws Exception {
        FacilioChain chain = ReadOnlyChainFactoryV3.getGlobalScopeVariableMeta();
        FacilioContext context = chain.getContext();
        chain.execute();
        setData(FacilioConstants.ContextNames.MODULE_LIST, context.get(FacilioConstants.ContextNames.MODULE_LIST));
        return SUCCESS;
    }

    public String getApplicableFieldsMap() throws Exception {
        FacilioChain chain = ReadOnlyChainFactoryV3.getGlobalScopeVariableFields();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME,getLookupModuleName());
        chain.execute();
        setData(FacilioConstants.ContextNames.RECORD_LIST, context.get(FacilioConstants.ContextNames.MODULE_LIST));        return SUCCESS;
    }

    public String getApplicableModuleName() {
        return applicableModuleName;
    }

    public void setApplicableModuleName(String applicableModuleName) {
        this.applicableModuleName = applicableModuleName;
    }

    public String getApplicationValueGenerators() throws Exception {
        FacilioChain chain = ReadOnlyChainFactoryV3.getValueGeneratorsForGlobalScopeVariable();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME,getApplicableModuleName());
        chain.execute();
        setData(FacilioConstants.ContextNames.VALUE_GENERATORS,context.get(FacilioConstants.ContextNames.VALUE_GENERATORS));
        return SUCCESS;
    }
}
