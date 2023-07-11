package com.facilio.remotemonitoring.jobs;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.remotemonitoring.compute.RawAlarmUtil;
import com.facilio.remotemonitoring.context.AlarmFilterCriteriaType;
import com.facilio.remotemonitoring.context.FilterRuleCriteriaContext;
import com.facilio.remotemonitoring.context.RawAlarmContext;
import com.facilio.remotemonitoring.handlers.AlarmCriteriaHandler;
import com.facilio.remotemonitoring.signup.AlarmFilterRuleCriteriaModule;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import lombok.extern.log4j.Log4j;

@Log4j
public class AlarmOpenForDurationOfTimeJob extends FacilioJob {
    @Override
    public void execute(JobContext jc) throws Exception {
        // TODO Auto-generated method stub

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        long rawAlarmId = jc.getJobId();
        RawAlarmContext rawAlarm = RawAlarmUtil.fetchRawAlarm(rawAlarmId);
        if(rawAlarm != null) {
            FilterRuleCriteriaContext filterRuleCriteria = V3RecordAPI.getRecord(AlarmFilterRuleCriteriaModule.MODULE_NAME,rawAlarm.getFilterRuleCriteriaId());
            if(filterRuleCriteria != null) {
                AlarmCriteriaHandler handler = AlarmFilterCriteriaType.ALARM_OPEN_FOR_SPECIFIED_DURATION.getHandler(rawAlarm);
                if(handler != null) {
                    handler.createFilteredAlarm(rawAlarm,filterRuleCriteria);
                }
            }
        }
    }
}