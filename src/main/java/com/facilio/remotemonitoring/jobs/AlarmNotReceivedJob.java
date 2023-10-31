package com.facilio.remotemonitoring.jobs;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.fms.message.Message;
import com.facilio.ims.endpoint.Messenger;
import com.facilio.modules.FieldUtil;
import com.facilio.remotemonitoring.wms.ControllerOfflineJobInfoContext;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import com.facilio.wmsv2.constants.Topics;
import lombok.extern.log4j.Log4j;

@Log4j
public class AlarmNotReceivedJob extends FacilioJob {
    @Override
    public void execute(JobContext jc) throws Exception {
        // TODO Auto-generated method stub
        Long currentTimestamp = System.currentTimeMillis();
        ControllerOfflineJobInfoContext controllerOfflineJobInfo = new ControllerOfflineJobInfoContext();
        controllerOfflineJobInfo.setTimeStamp(currentTimestamp);
        if (AccountUtil.getCurrentOrg() != null) {
            Messenger.getMessenger().sendMessage(new Message()
                    .setKey(Topics.ControllerOffline.controllerOfflineTopic + "/" + AccountUtil.getCurrentOrg().getId() + System.currentTimeMillis())
                    .setOrgId(AccountUtil.getCurrentOrg().getId())
                    .setContent(FieldUtil.getAsJSON(controllerOfflineJobInfo)));
        }
    }
}