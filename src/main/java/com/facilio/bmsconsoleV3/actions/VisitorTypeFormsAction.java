package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsole.context.V3VisitorTypeContext;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.V3Action;

public class VisitorTypeFormsAction extends V3Action {
    private V3VisitorTypeContext visitorType;
    private Long appId;

    public V3VisitorTypeContext getVisitorType() {
        return visitorType;
    }

    public void setVisitorType(V3VisitorTypeContext visitorType) {
        this.visitorType = visitorType;
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public String getParentModuleName() {
        return parentModuleName;
    }

    public void setParentModuleName(String parentModuleName) {
        this.parentModuleName = parentModuleName;
    }

    private String parentModuleName;
    public String updateVisitorTypeForm() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.addVisitsAndInvitesForms();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.VISITOR_TYPE, getVisitorType());
        context.put(FacilioConstants.ContextNames.APP_ID, getAppId());
        context.put(FacilioConstants.ContextNames.PARENT_MODULE_NAME,getParentModuleName());
        chain.execute();
        setData("message", "Added Successfully!");
        return SUCCESS;
    }

}
