package com.facilio.bmsconsole.jobs;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.tasker.job.JobContext;
import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

public class JobLogger {

    private static final Logger LOGGER = LogManager.getLogger(JobLogger.class.getName());
    private static final String JOB_NAME = "job";
    private static final String EXECUTOR_NAME = "executor";
    private static final String PERIODIC = "periodic";
    private static final String TIME_TAKEN = "timetaken";
    private static final String TIME_TAKEN_IN_MILLIS = "timeInMillis";
    private static final String DUMMY_MSG = "joblog";
    private static final String APPENDER_NAME = "graylog2";
    private static final String DEFAULT_ORG_USER_ID = "-1";
    private static final String JOB_STATUS = "status";
    private static final Appender APPENDER = LOGGER.getAppender(APPENDER_NAME);;


    public static void log(JobContext jobContext, long timeTaken, int status) {

        String message = DUMMY_MSG;
        if(AccountUtil.getCurrentAccount() != null) {
            Account account = AccountUtil.getCurrentAccount();
            message = "select: " + account.getSelectQueries() + " time: " + account.getSelectQueriesTime() +
                    " update: " + account.getUpdateQueries() + " time: " + account.getUpdateQueriesTime() +
                    " insert: " + account.getInsertQueries() + " time: " + account.getInsertQueriesTime() +
                    " delete: " + account.getDeleteQueries() + " time: " + account.getDeleteQueriesTime() +
                    " rget: " + account.getRedisGetCount() + " time: " + account.getRedisGetTime() +
                    " rput: " + account.getRedisPutCount() + " time: " + account.getRedisPutTime() +
                    " rdel: " + account.getRedisDeleteCount() + " time: " + account.getRedisDeleteTime();
        }
        LoggingEvent event = new LoggingEvent(LOGGER.getName(), LOGGER, Level.INFO, message, null);

        Organization org = AccountUtil.getCurrentOrg();
        if(org != null) {
            event.setProperty("orgId", String.valueOf(org.getOrgId()));
        } else {
            event.setProperty("orgId", DEFAULT_ORG_USER_ID);
        }

        User user = AccountUtil.getCurrentUser();
        if (user != null) {
            event.setProperty("userId", String.valueOf(user.getOuid()));
        } else {
            event.setProperty("userId", DEFAULT_ORG_USER_ID);
        }

        event.setProperty(JOB_NAME, jobContext.getJobName());
        event.setProperty(PERIODIC, jobContext.getIsPeriodic().toString());
        event.setProperty(EXECUTOR_NAME, jobContext.getExecutorName());
        event.setProperty(JOB_STATUS, String.valueOf(status));

        event.setProperty(TIME_TAKEN, String.valueOf(timeTaken/1000));
        event.setProperty(TIME_TAKEN_IN_MILLIS, String.valueOf(timeTaken));
        if(APPENDER != null) {
            APPENDER.doAppend(event);
        } else {
            LOGGER.callAppenders(event);
        }
    }

}
