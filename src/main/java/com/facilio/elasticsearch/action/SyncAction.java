package com.facilio.elasticsearch.action;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.elasticsearch.SyncChainFactory;

public class SyncAction extends FacilioAction {

    private String moduleName;
    public String getModuleName() {
        return moduleName;
    }
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String invokeSync() throws Exception {
        FacilioChain chain = SyncChainFactory.getInvokeSyncChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        chain.execute();
        return SUCCESS;
    }

    public String removeSync() throws Exception {
        FacilioChain chain = SyncChainFactory.getRemoveSyncChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        chain.execute();

        return SUCCESS;
    }

    public String listModules() throws Exception {
        FacilioChain chain = SyncChainFactory.getListModuleChain();
        FacilioContext context = chain.getContext();
        chain.execute();

        setResult(FacilioConstants.ContextNames.MODULE_LIST, context.get(FacilioConstants.ContextNames.MODULE_LIST));
        return SUCCESS;
    }

    public String search() throws Exception {
        FacilioChain chain = SyncChainFactory.getSearchChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.SEARCH, getSearch());
        chain.execute();

        setResult(FacilioConstants.ContextNames.SEARCH_RESULT, context.get(FacilioConstants.ContextNames.SEARCH_RESULT));
        return SUCCESS;
    }
}
