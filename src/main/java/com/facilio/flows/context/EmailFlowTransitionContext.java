package com.facilio.flows.context;

import com.facilio.flowengine.context.Constants;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class EmailFlowTransitionContext extends FlowTransitionContext{
    private Long fromMailId=-1l; //Email_From_Address table ID
    private String to;
    private String cc;
    private String bcc;
    private Long templateId=-1l;
    private Boolean sendAsSeparateMail;
    @Override
    public void updateConfig() throws Exception{
        super.updateConfig();
        addConfigData(Constants.EmailBlockConstants.FROM_MAIL_ID,fromMailId);
        addConfigData(Constants.EmailBlockConstants.TO,to);
        addConfigData(Constants.EmailBlockConstants.CC,cc);
        addConfigData(Constants.EmailBlockConstants.BCC,bcc);
        addConfigData(Constants.EmailBlockConstants.TEMPLATE_ID,templateId);
        addConfigData(Constants.EmailBlockConstants.SEND_AS_SEPARATE_MAIL,sendAsSeparateMail);
    }
}
