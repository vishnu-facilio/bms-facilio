package com.facilio.bmsconsoleV3.signup.jobs;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.tasker.FacilioTimer;
import com.facilio.taskengine.ScheduleInfo;

import java.time.LocalTime;

public class DefaultJobsOnSignUp extends SignUpData {
    private ScheduleInfo constructDailyScheduleInfoWithOneTime(String time) {
        ScheduleInfo scheduleInfo = new ScheduleInfo();
        scheduleInfo.setFrequencyType(ScheduleInfo.FrequencyType.DAILY);
        scheduleInfo.addTime(time);

        return scheduleInfo;
    }

    private ScheduleInfo constructDailyScheduleInfoWithHourlyTime(int minuteVal) {
        ScheduleInfo scheduleInfo = new ScheduleInfo();
        scheduleInfo.setFrequencyType(ScheduleInfo.FrequencyType.DAILY);
        for (int i = 0; i < 24; i++) {
            scheduleInfo.addTime(LocalTime.of(i, minuteVal));
        }

        return scheduleInfo;
    }

    private ScheduleInfo constructDailyScheduleInfoWithQuarterPastHourlyTime() {
        ScheduleInfo scheduleInfo = new ScheduleInfo();
        scheduleInfo.setFrequencyType(ScheduleInfo.FrequencyType.DAILY);
        for (int i = 0; i < 24; i++) {
            scheduleInfo.addTime(LocalTime.of(i, 15));
        }

        return scheduleInfo;
    }

    private ScheduleInfo constructDailyScheduleInfoWithHalfHourlyTime() {
        ScheduleInfo scheduleInfo = new ScheduleInfo();
        scheduleInfo.setFrequencyType(ScheduleInfo.FrequencyType.DAILY);
        for (int i = 0; i < 24; i++) {
            scheduleInfo.addTime(LocalTime.of(i, 0));
            scheduleInfo.addTime(LocalTime.of(i, 30));
        }

        return scheduleInfo;
    }

    @Override
    public void addData() throws Exception {
        long orgId = AccountUtil.getCurrentOrg().getId();
        // INSERT INTO ${publicDb}.Jobs (JOBID, ORGID, JOBNAME, IS_ACTIVE, IS_PERIODIC, PERIOD, NEXT_EXECUTION_TIME, EXECUTOR_NAME, STATUS, JOB_SERVER_ID, TRANSACTION_TIMEOUT, CURRENT_EXECUTION_TIME) 
        // VALUES (@ASSET_SCHEDULE_EXPIRY_RULEID, ${orgId}, 'ScheduledRuleExecution', true, true, 1800, UNIX_TIMESTAMP()+300,'facilio', 3, 0, 900000, UNIX_TIMESTAMP()*1000);
        FacilioTimer.schedulePeriodicJob(AssetsAPI.getAssetExpiryRuleId(), "ScheduledRuleExecution", 1800, 1800, "facilio");

        // INSERT INTO ${publicDb}.Jobs (JOBID, ORGID, JOBNAME, IS_ACTIVE, IS_PERIODIC, PERIOD, NEXT_EXECUTION_TIME, EXECUTOR_NAME, STATUS, JOB_SERVER_ID, TRANSACTION_TIMEOUT, CURRENT_EXECUTION_TIME) VALUES (@DAILY_ENERGY_AGGREGATION_ID, ${orgId}, 'AggregationJob', 1, 1, 1800, UNIX_TIMESTAMP()+30,'facilio', 3, 0, 7200000, UNIX_TIMESTAMP()*1000);
        // Boopathy

        // INSERT INTO ${publicDb}.Jobs (JOBID, ORGID, JOBNAME, IS_ACTIVE, IS_PERIODIC, SCHEDULE_INFO, NEXT_EXECUTION_TIME, EXECUTOR_NAME, STATUS, JOB_SERVER_ID, TRANSACTION_TIMEOUT, CURRENT_EXECUTION_TIME)
        // VALUES (${orgId}, ${orgId}, 'PMNewScheduler', true, true, '{"times":["00:01"],"frequencyType":1,"frequencyTypeEnum":"DAILY"}', UNIX_TIMESTAMP()+30,'facilio', 3, 0, 300000, UNIX_TIMESTAMP()*1000);
        FacilioTimer.scheduleCalendarJob(orgId, "PMNewScheduler", System.currentTimeMillis(), constructDailyScheduleInfoWithOneTime("00:01"), "facilio");

        // INSERT INTO ${publicDb}.Jobs (JOBID, ORGID, JOBNAME, IS_ACTIVE, IS_PERIODIC, SCHEDULE_INFO, NEXT_EXECUTION_TIME, EXECUTOR_NAME, STATUS, JOB_SERVER_ID, TRANSACTION_TIMEOUT, CURRENT_EXECUTION_TIME)
        // VALUES (${orgId}, ${orgId}, 'ScheduledFormulaCalculator', true, true, '{"times":["00:05","01:05","02:05","03:05","04:05","05:05","06:05","07:05","08:05","09:05","10:05","11:05","12:05","13:05","14:05","15:05","16:05","17:05","18:05","19:05","20:05","21:05","22:05","23:05"],"frequencyType":1,"frequencyTypeEnum":"DAILY"}', UNIX_TIMESTAMP()+30,'facilio', 3, 0, 300000, UNIX_TIMESTAMP()*1000);
        FacilioTimer.scheduleCalendarJob(orgId, "ScheduledFormulaCalculator", System.currentTimeMillis(), constructDailyScheduleInfoWithHourlyTime(5), "facilio");

        FacilioTimer.scheduleCalendarJob(orgId, "ScheduledKpiExecInitiator", System.currentTimeMillis(), constructDailyScheduleInfoWithOneTime("00:01"), "facilio");

        // INSERT INTO ${publicDb}.Jobs (JOBID, ORGID, JOBNAME, IS_ACTIVE, IS_PERIODIC, PERIOD, NEXT_EXECUTION_TIME, EXECUTOR_NAME, STATUS, JOB_SERVER_ID, TRANSACTION_TIMEOUT, CURRENT_EXECUTION_TIME)
        // VALUES (${orgId}, ${orgId}, 'VirtualMeterCalculation', true, true, 900, UNIX_TIMESTAMP()+900,'facilio', 3, 0, 300000, UNIX_TIMESTAMP()*1000);
        FacilioTimer.schedulePeriodicJob(orgId, "VirtualMeterCalculation", 900, 900, "facilio");

        // INSERT INTO ${publicDb}.Jobs (JOBID, ORGID, JOBNAME, IS_ACTIVE, IS_PERIODIC, SCHEDULE_INFO, NEXT_EXECUTION_TIME, EXECUTOR_NAME, STATUS, JOB_SERVER_ID, TRANSACTION_TIMEOUT, CURRENT_EXECUTION_TIME)
        // VALUES (${orgId}, ${orgId}, 'WeatherData', true, true, '{"times":["00:00","00:30","01:00","01:30","02:00","02:30","03:00","03:30","04:00","04:30","05:00","05:30","06:00","06:30","07:00","07:30","08:00","08:30","09:00","09:30","10:00","10:30","11:00","11:30","12:00","12:30","13:00","13:30","14:00","14:30","15:00","15:30","16:00","16:30","17:00","17:30","18:00","18:30","19:00","19:30","20:00","20:30","21:00","21:30","22:00","22:30","23:00","23:30"],"frequencyType":1,"frequencyTypeEnum":"DAILY"}', UNIX_TIMESTAMP()+30,'facilio', 3, 0, 300000, UNIX_TIMESTAMP()*1000);
        FacilioTimer.scheduleCalendarJob(orgId, "WeatherData", System.currentTimeMillis(), constructDailyScheduleInfoWithHalfHourlyTime(), "facilio");

        // INSERT INTO ${publicDb}.Jobs (JOBID, ORGID, JOBNAME, IS_ACTIVE, IS_PERIODIC, PERIOD, NEXT_EXECUTION_TIME, EXECUTOR_NAME, STATUS, JOB_SERVER_ID, TRANSACTION_TIMEOUT, CURRENT_EXECUTION_TIME)
        // VALUES (${orgId}, ${orgId}, 'ScheduleWOStatusChange', true, true, 1800, UNIX_TIMESTAMP()+1800,'facilio', 3, 0, 300000, UNIX_TIMESTAMP()*1000);
        FacilioTimer.schedulePeriodicJob(orgId, "ScheduleWOStatusChange", 1800, 1800, "facilio");

        // INSERT INTO ${publicDb}.Jobs (JOBID, ORGID, JOBNAME, IS_ACTIVE, IS_PERIODIC, SCHEDULE_INFO, NEXT_EXECUTION_TIME, EXECUTOR_NAME, STATUS, JOB_SERVER_ID, TRANSACTION_TIMEOUT, CURRENT_EXECUTION_TIME)
        // VALUES (${orgId}, ${orgId}, 'DailyWeatherData', true, true, '{"times":["00:00"],"frequencyType":1,"frequencyTypeEnum":"DAILY"}', UNIX_TIMESTAMP()+30,'facilio', 3, 0, 300000, UNIX_TIMESTAMP()*1000);
        FacilioTimer.scheduleCalendarJob(orgId, "DailyWeatherData", System.currentTimeMillis(), constructDailyScheduleInfoWithOneTime("00:00"), "facilio");

        // INSERT INTO ${publicDb}.Jobs (JOBID, ORGID, JOBNAME, IS_ACTIVE, IS_PERIODIC, SCHEDULE_INFO, NEXT_EXECUTION_TIME, EXECUTOR_NAME, STATUS, JOB_SERVER_ID, TRANSACTION_TIMEOUT, CURRENT_EXECUTION_TIME)
        // VALUES (${orgId}, ${orgId}, 'DegreeDaysCalculator', true, true, '{"times":["00:00"],"frequencyType":1,"frequencyTypeEnum":"DAILY"}', UNIX_TIMESTAMP()+30,'facilio', 3, 0, 300000, UNIX_TIMESTAMP()*1000);
        FacilioTimer.scheduleCalendarJob(orgId, "DegreeDaysCalculator", System.currentTimeMillis(), constructDailyScheduleInfoWithOneTime("00:00"), "facilio");

        // INSERT INTO ${publicDb}.Jobs (JOBID, ORGID, JOBNAME, IS_ACTIVE, IS_PERIODIC, SCHEDULE_INFO, NEXT_EXECUTION_TIME, EXECUTOR_NAME, STATUS, JOB_SERVER_ID, TRANSACTION_TIMEOUT, CURRENT_EXECUTION_TIME)
        // VALUES (${orgId}, ${orgId}, 'DeleteControllerActivityRecords', true, true, '{"times":["00:00"],"frequencyType":1,"frequencyTypeEnum":"DAILY"}', UNIX_TIMESTAMP()+30,'facilio', 3, 0, 300000, UNIX_TIMESTAMP()*1000);
        FacilioTimer.scheduleCalendarJob(orgId, "DeleteControllerActivityRecords", System.currentTimeMillis(), constructDailyScheduleInfoWithOneTime("00:00"), "facilio");

        // INSERT INTO ${publicDb}.Jobs (JOBID, ORGID, JOBNAME, IS_ACTIVE, IS_PERIODIC, SCHEDULE_INFO, NEXT_EXECUTION_TIME, EXECUTOR_NAME, STATUS, JOB_SERVER_ID, TRANSACTION_TIMEOUT, CURRENT_EXECUTION_TIME)
        // VALUES (${orgId}, ${orgId}, 'AssetDepreciationJob', true, true, '{"times":["00:00"],"frequencyType":1,"frequencyTypeEnum":"DAILY"}', UNIX_TIMESTAMP()+30,'facilio', 3, 0, 7200000, UNIX_TIMESTAMP()*1000);
        FacilioTimer.scheduleCalendarJob(orgId, "AssetDepreciationJob", System.currentTimeMillis(), constructDailyScheduleInfoWithOneTime("00:00"), "facilio");

        // INSERT INTO ${publicDb}.Jobs (JOBID, ORGID, JOBNAME, IS_ACTIVE, IS_PERIODIC, SCHEDULE_INFO, NEXT_EXECUTION_TIME, EXECUTOR_NAME, STATUS, JOB_SERVER_ID, TRANSACTION_TIMEOUT, CURRENT_EXECUTION_TIME)
        // VALUES (${orgId}, ${orgId}, 'DeleteFileRecordsJob', true, true, '{"times":["00:00"],"frequencyType":1,"frequencyTypeEnum":"DAILY"}', UNIX_TIMESTAMP()+30,'facilio', 3, 0, 1200000, UNIX_TIMESTAMP()*1000);
        FacilioTimer.scheduleCalendarJob(orgId, "DeleteFileRecordsJob", System.currentTimeMillis(), constructDailyScheduleInfoWithOneTime("00:00"), "facilio");

        // INSERT INTO ${publicDb}.Jobs (JOBID, ORGID, JOBNAME, IS_ACTIVE, IS_PERIODIC, SCHEDULE_INFO, NEXT_EXECUTION_TIME, EXECUTOR_NAME, STATUS, JOB_SERVER_ID, TRANSACTION_TIMEOUT, CURRENT_EXECUTION_TIME)
        // VALUES (${orgId}, ${orgId}, 'controlCommandExecutionCreateScheduleJob', true, true, '{"times":["00:00","00:30","01:00","01:30","02:00","02:30","03:00","03:30","04:00","04:30","05:00","05:30","06:00","06:30","07:00","07:30","08:00","08:30","09:00","09:30","10:00","10:30","11:00","11:30","12:00","12:30","13:00","13:30","14:00","14:30","15:00","15:30","16:00","16:30","17:00","17:30","18:00","18:30","19:00","19:30","20:00","20:30","21:00","21:30","22:00","22:30","23:00","23:30"],"frequencyType":1,"frequencyTypeEnum":"DAILY"}', UNIX_TIMESTAMP()+30,'facilio', 3, 0, 600000, UNIX_TIMESTAMP()*1000);
        FacilioTimer.scheduleCalendarJob(orgId, "controlCommandExecutionCreateScheduleJob", System.currentTimeMillis(), constructDailyScheduleInfoWithHalfHourlyTime(), "facilio");

        // INSERT INTO ${publicDb}.Jobs (JOBID, ORGID, JOBNAME, IS_ACTIVE, IS_PERIODIC, SCHEDULE_INFO, NEXT_EXECUTION_TIME, EXECUTOR_NAME, STATUS, JOB_SERVER_ID, TRANSACTION_TIMEOUT, CURRENT_EXECUTION_TIME)
        // VALUES (${orgId}, ${orgId}, 'ControlScheduleSlotCreationDailyJob', true, true, '{"times":["00:00"],"frequencyType":1,"frequencyTypeEnum":"DAILY"}', UNIX_TIMESTAMP()+30,'facilio', 3, 0, 600000, UNIX_TIMESTAMP()*1000);
        FacilioTimer.scheduleCalendarJob(orgId, "ControlScheduleSlotCreationDailyJob", System.currentTimeMillis(), constructDailyScheduleInfoWithOneTime("00:00"), "facilio");

        // INSERT INTO ${publicDb}.Jobs (JOBID, ORGID, JOBNAME, IS_ACTIVE, IS_PERIODIC, SCHEDULE_INFO, NEXT_EXECUTION_TIME, EXECUTOR_NAME, STATUS, JOB_SERVER_ID, TRANSACTION_TIMEOUT, CURRENT_EXECUTION_TIME)
        // VALUES (${orgId}, ${orgId}, 'ImapJob', true, true, '{"times":["00:15","01:15","02:15","03:15","04:15","05:15","06:15","07:15","08:15","08:30","09:15","10:15","11:15","12:15","13:15","14:15","15:15","16:15","17:15","18:15","19:15","20:15","21:15","22:15","23:15"],"frequencyType":1,"frequencyTypeEnum":"DAILY"}', UNIX_TIMESTAMP()+30,'facilio', 3, 0, 300000, UNIX_TIMESTAMP()*1000);
        FacilioTimer.scheduleCalendarJob(orgId, "ImapJob", System.currentTimeMillis(), constructDailyScheduleInfoWithHourlyTime(0), "facilio");

        // INSERT INTO ${publicDb}.Jobs (JOBID, ORGID, JOBNAME, IS_ACTIVE, IS_PERIODIC, SCHEDULE_INFO, NEXT_EXECUTION_TIME, EXECUTOR_NAME, STATUS, JOB_SERVER_ID, TRANSACTION_TIMEOUT, CURRENT_EXECUTION_TIME)
        // VALUES (${orgId}, ${orgId}, 'ScheduleSlotCreation', true, true, '{"times":["00:00"],"frequencyType":1,"frequencyTypeEnum":"DAILY"}', UNIX_TIMESTAMP()+30,'facilio', 3, 0, 1200000, UNIX_TIMESTAMP()*1000);
        FacilioTimer.scheduleCalendarJob(orgId, "ScheduleSlotCreation", System.currentTimeMillis(), constructDailyScheduleInfoWithOneTime("00:00"), "facilio");

        // INSERT INTO Jobs (JOBID, ORGID, JOBNAME, IS_ACTIVE, IS_PERIODIC, SCHEDULE_INFO, NEXT_EXECUTION_TIME, EXECUTOR_NAME, STATUS, JOB_SERVER_ID, TRANSACTION_TIMEOUT, CURRENT_EXECUTION_TIME)
        // VALUES (1, 1, 'PreOpenWoObservation', true, true, '{"times":["00:00","00:30","01:00","01:30","02:00","02:30","03:00","03:30","04:00","04:30","05:00","05:30","06:00","06:30","07:00","07:30","08:00","08:30","09:00","09:30","10:00","10:30","11:00","11:30","12:00","12:30","13:00","13:30","14:00","14:30","15:00","15:30","16:00","16:30","17:00","17:30","18:00","18:30","19:00","19:30","20:00","20:30","21:00","21:30","22:00","22:30","23:00","23:30"],"frequencyType":1,"frequencyTypeEnum":"DAILY"}', UNIX_TIMESTAMP()+30,'facilio', 3, 0, 900000, UNIX_TIMESTAMP()*1000);
        FacilioTimer.scheduleCalendarJob(orgId, "PreOpenWoObservation", System.currentTimeMillis(), constructDailyScheduleInfoWithQuarterPastHourlyTime(), "facilio");

		//Delete Unanswered Expired Survey
		FacilioTimer.scheduleCalendarJob(orgId, "DeleteUnAnsweredSurvey", System.currentTimeMillis(), constructDailyScheduleInfoWithOneTime("00:00"), "facilio");

        // JOB TO DELETE "DELETED PRE-OPEN WORKORDERS"
        FacilioTimer.scheduleCalendarJob(orgId, "RemoveDeletedPreOpenWorkOrdersJob", System.currentTimeMillis(), constructDailyScheduleInfoWithOneTime("03:00"), "facilio");
    }
}
