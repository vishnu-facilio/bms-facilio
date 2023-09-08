package com.facilio.flows.blockconfigcommand;

import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.util.MailMessageUtil;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsoleV3.context.EmailFromAddress;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.flows.context.EmailFlowTransitionContext;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

public class BeforeSaveEmailBlockCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        EmailFlowTransitionContext emailFlowTransitionContext = (EmailFlowTransitionContext) context.get(FacilioConstants.ContextNames.FLOW_TRANSITION);
        long templateId = emailFlowTransitionContext.getTemplateId();
        long fromMailId = emailFlowTransitionContext.getFromMailId();
        String to = emailFlowTransitionContext.getTo();
        String cc = emailFlowTransitionContext.getCc();
        String bcc = emailFlowTransitionContext.getBcc();

        FacilioUtil.throwIllegalArgumentException(templateId==-1l,"templateId is can not be null");
        FacilioUtil.throwIllegalArgumentException(fromMailId==-1l,"fromMailId can not be empty");

        Template template = TemplateAPI.getTemplate(templateId);
        FacilioUtil.throwIllegalArgumentException(template==null,"templateId:"+templateId+ " does not exist");

        EmailFromAddress fromAddress = MailMessageUtil.getEmailFromAddress(fromMailId,EmailFromAddress.SourceType.NOTIFICATION,true);
        FacilioUtil.throwIllegalArgumentException(fromAddress==null,"fromMailId:"+fromMailId+ " does not exist");

        FacilioUtil.throwIllegalArgumentException(StringUtils.isEmpty(to),"To can not be empty");
        FacilioUtil.throwIllegalArgumentException(to.length()>1000,"TO address email id's length should be less than or equal to 1000");

        if (StringUtils.isNotEmpty(cc)){
            FacilioUtil.throwIllegalArgumentException(cc.length()>1000,"CC address email id's length should be less than or equal to 1000");
        }
        if (StringUtils.isNotEmpty(bcc)){
            FacilioUtil.throwIllegalArgumentException(bcc.length()>1000,"BCC address email id's length should be less than or equal to 1000");
        }

        return false;
    }
}
