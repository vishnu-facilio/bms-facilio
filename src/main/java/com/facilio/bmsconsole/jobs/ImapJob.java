package com.facilio.bmsconsole.jobs;

import com.facilio.bmsconsole.context.SupportEmailContext;
import com.facilio.bmsconsole.util.MailMessageUtil;
import com.facilio.bmsconsole.util.SupportEmailAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.transaction.FTransactionManager;
import com.facilio.db.transaction.FacilioTransactionManager;
import com.facilio.service.FacilioService;
import com.facilio.services.email.ImapsClient;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class ImapJob extends FacilioJob {
    @Override
    public void execute(JobContext jc) throws Exception {
        List<SupportEmailContext> imapEmails = FacilioService.runAsServiceWihReturn(FacilioConstants.Services.DEFAULT_SERVICE,() -> SupportEmailAPI.getImapsEmailsOfOrg(jc.getOrgId()));
        if (CollectionUtils.isNotEmpty(imapEmails)) {
            for (SupportEmailContext imapMail : imapEmails) {
                long latestUID = imapMail.getLatestMessageUID();
                try ( ImapsClient mailService = new ImapsClient(imapMail)){
                    if (latestUID > 0) {
                        // fetch mail which is greater than messageUID
                        mailService.getMessageGtUID(latestUID);
                    } else {
                        // fetch today's Mail n days
                        mailService.getNDaysMails(1);
                    }
                }

            }
        }

    }
}
