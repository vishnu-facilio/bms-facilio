package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.V3Action;

public class V3VisitorSettingAction extends V3Action {
    private static final long serialVersionUID = 1L;
    @Override
    public String getModuleName() {
        return moduleName;
    }

    @Override
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String moduleName=null;
    public long appId;

    public long getAppId() {
        return appId;
    }

    public void setAppId(long appId) {
        this.appId = appId;
    }

    public long getVisitorTypeId() {
        return visitorTypeId;
    }

    public void setVisitorTypeId(long visitorTypeId) {
        this.visitorTypeId = visitorTypeId;
    }
    public String formName;

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getFormDescription() {
        return formDescription;
    }

    public void setFormDescription(String formDescription) {
        this.formDescription = formDescription;
    }

    public String formDescription;

    public long visitorTypeId;

    public String addNewVisitsAndInviteForm() throws Exception{
        FacilioChain chain= TransactionChainFactoryV3.addVisitsAndInvitesForms();
        FacilioContext context=chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
        context.put(FacilioConstants.ContextNames.APP_ID, getAppId());
        context.put(FacilioConstants.ContextNames.VISITOR_TYPE, getVisitorTypeId());
        context.put(FacilioConstants.ContextNames.FORM_NAME, getFormName());
        context.put("formDescription", getFormDescription());
        chain.execute();
        setData(FacilioConstants.ContextNames.VISITOR_SETTINGS, context.get(FacilioConstants.ContextNames.VISITOR_SETTINGS));
        return V3Action.SUCCESS;
    }
}
