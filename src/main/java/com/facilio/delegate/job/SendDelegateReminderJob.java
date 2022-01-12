package com.facilio.delegate.job;

import com.facilio.bmsconsole.templates.DefaultTemplate;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.delegate.context.DelegationContext;
import com.facilio.delegate.util.DelegationUtil;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;

public class SendDelegateReminderJob extends FacilioJob {

    @Override
    public void execute(JobContext jobContext) throws Exception {
        long jobId = jobContext.getJobId();
        DelegationContext delegationContext = DelegationUtil.getDelegationContext(jobId);
        if (delegationContext == null) {
            return;
        }

        DefaultTemplate defaultTemplate = TemplateAPI.getDefaultTemplate(DefaultTemplate.DefaultTemplateType.ACTION, 121);
        DelegationUtil.sendMail(delegationContext, defaultTemplate);
    }
}
