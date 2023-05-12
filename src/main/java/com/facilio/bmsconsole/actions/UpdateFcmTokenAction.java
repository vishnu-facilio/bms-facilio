package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class UpdateFcmTokenAction extends FacilioAction{

    private String oldFcmToken;
    public String getOldFcmToken() {
        return oldFcmToken;
    }
    public void setOldFcmToken(String oldFcmToken) {
        this.oldFcmToken = oldFcmToken;
    }

    private String newFcmToken;
    public String getNewFcmToken() {
        return newFcmToken;
    }
    public void setNewFcmToken(String newFcmToken) {
        this.newFcmToken = newFcmToken;
    }

    public String updateFcmToken() throws Exception{
        FacilioChain chain = TransactionChainFactory.getUpdateFCMTokenChain();
        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.OLD_FCM_TOKEN, oldFcmToken);
        context.put(FacilioConstants.ContextNames.NEW_FCM_TOKEN, newFcmToken);

        chain.execute();
        setResult(FacilioConstants.ContextNames.USER_MOBILE_SETTING,context.get(FacilioConstants.ContextNames.USER_MOBILE_SETTING));

        return SUCCESS;
    }
}
