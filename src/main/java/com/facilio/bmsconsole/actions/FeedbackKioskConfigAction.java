package com.facilio.bmsconsole.actions;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ConnectedDeviceContext;
import com.facilio.bmsconsole.context.FeedbackKioskContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;

public class FeedbackKioskConfigAction extends FacilioAction {

    private FeedbackKioskContext feedbackKiosk;
    public FeedbackKioskContext getFeedbackKiosk() {
        return feedbackKiosk;
    }
    public void setFeedbackKiosk(FeedbackKioskContext feedbackKiosk) {
        this.feedbackKiosk = feedbackKiosk;
    }

    public String addOrUpdate() throws Exception
    {
        if(feedbackKiosk.getPrinter()!=null)
        {
            feedbackKiosk.setPrinterId(feedbackKiosk.getPrinter().getId());

        }

        FacilioChain chain = TransactionChainFactory.addOrUpdateFeedbackKioskConfigChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.RECORD, getFeedbackKiosk());
        context.put(FacilioConstants.ContextNames.MODULE, ModuleFactory.getFeedbackKioskConfigModule());
        context.put(FacilioConstants.ContextNames.FIELDS, FieldFactory.getFeedbackKioskConfigFields());

        chain.execute();

        return SUCCESS;
    }

    public String getServiceCatalog() throws Exception {
        ConnectedDeviceContext connectedDevice = AccountUtil.getCurrentAccount().getConnectedDevice();
        if (connectedDevice == null) {
            throw new IllegalArgumentException("Invalid device");
        }

        FacilioChain chain = ReadOnlyChainFactory.getDeviceServiceCatalogChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.DEVICEID, connectedDevice.getDeviceId());
        chain.execute();

        setResult(FacilioConstants.ContextNames.SERVICE_CATALOGS, context.get(FacilioConstants.ContextNames.SERVICE_CATALOGS));

        return SUCCESS;
    }
}
