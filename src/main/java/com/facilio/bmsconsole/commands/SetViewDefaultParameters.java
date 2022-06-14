package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import com.facilio.bmsconsole.context.SingleSharingContext;

public class SetViewDefaultParameters extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.NEW_CV);

        view.setOwnerId(AccountUtil.getCurrentUser().getId());
        if (view.getIsLocked() == null)
            view.setLocked(false);

        if (view.getViewSharing() == null || view.getViewSharing().size() == 0) {
            SharingContext<SingleSharingContext> viewSharing = new SharingContext<SingleSharingContext>();
            SingleSharingContext sharingContext = new SingleSharingContext();
            sharingContext.setType(SingleSharingContext.SharingType.USER);
            sharingContext.setUserId(view.getOwnerId());
            viewSharing.add(sharingContext);
            view.setViewSharing(viewSharing);
        }
        return false;
    }
}
