package com.facilio.delegate.command;

import com.facilio.bmsconsole.templates.DefaultTemplate;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.delegate.context.DelegationContext;
import com.facilio.delegate.util.DelegationUtil;
import org.apache.commons.chain.Context;

import java.io.Serializable;

public class SendDelegationMailCommand extends FacilioCommand implements Serializable {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        DelegationContext delegationContext = (DelegationContext) context.get(FacilioConstants.ContextNames.DELEGATION_CONTEXT);

        sendMail(delegationContext);
        return false;
    }

    private void sendMail(DelegationContext delegationContext) {
        if (System.currentTimeMillis() > delegationContext.getToTime()) {
            // don't want to send the mails
            return;
        }

        try {
            DefaultTemplate defaultTemplate = TemplateAPI.getDefaultTemplate(DefaultTemplate.DefaultTemplateType.ACTION, 120);
            DelegationUtil.sendMail(delegationContext, defaultTemplate);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
