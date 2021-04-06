package com.facilio.bmsconsoleV3.signup.scoping;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.constants.FacilioConstants;
import com.facilio.service.FacilioService;
import com.facilio.tasker.FacilioTimer;
import com.facilio.tasker.ScheduleInfo;
import lombok.extern.log4j.Log4j;

import java.time.LocalTime;

@Log4j
public class DeleteJobConfig extends SignUpData {
    @Override
    public void addData () throws Exception {
       addDeleteJobEntry();
    }

    private void addDeleteJobEntry() throws Exception {
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        if(orgId < 1){
            throw new IllegalArgumentException("Invalid orgId while adding DeleteJobRecord entry in Org signup.");
        }
        ScheduleInfo schedule = new ScheduleInfo();
        schedule.addTime(LocalTime.of(01, 0));
        schedule.setFrequencyType(ScheduleInfo.FrequencyType.DAILY);
        schedule.setFrequency(1);
        FacilioTimer.scheduleCalendarJob(orgId,"DeleteJobRecords",System.currentTimeMillis(),schedule,"facilio");
    }
}
