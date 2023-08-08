package com.facilio.odataservice.action;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public  class ODataViewAction extends V3Action {
    public static FacilioView getViewDetail(String moduleName, String viewName) throws Exception
    {
        FacilioView viewObj;
        if (moduleName == null || moduleName.equals("approval")) {
            if (moduleName.equals("approval")) {
                viewName = "approval_" + viewName;
            }
            moduleName = "workorder";
        }
        FacilioChain getViewChain = FacilioChainFactory.getViewDetailsChain();
        FacilioContext context = getViewChain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.CV_NAME,viewName);
        context.put(FacilioConstants.ContextNames.APP_ID, -1l);
        context.put(FacilioConstants.ContextNames.PARENT_VIEW, null);
        context.put(FacilioConstants.ContextNames.FETCH_FIELD_DISPLAY_NAMES, true);
        context.put(FacilioConstants.ContextNames.IS_FETCH_CALL, true);
        getViewChain.execute();
        viewObj = (FacilioView)context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
        return viewObj;
    }
}
