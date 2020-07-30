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

    public String search() throws Exception {

        return SUCCESS;
    }
}
