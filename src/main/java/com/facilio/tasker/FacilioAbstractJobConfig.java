package com.facilio.tasker;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.modules.FieldUtil;
import com.facilio.server.ServerInfo;
import com.facilio.taskengine.config.JobConfig;
import com.facilio.taskengine.job.JobContext;
import com.facilio.util.SentryUtil;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public abstract class FacilioAbstractJobConfig implements JobConfig {

    @Override
    public abstract String getJobFilePath();

    @Override
    public abstract String getExecFilePath();

    @Override
    public abstract boolean isEnabledService();

    @Override
    public void handleException(String className, String msg, Throwable t) {

    }

    @Override
    public void handleSchedulerExceptions(JobContext jc, Exception e) {
        SentryUtil.handleSchedulerExceptions(jc, e);
    }

    @Override
    public String getAsJSON(Object bean) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return FieldUtil.getAsJSON(bean).toJSONString();
    }

    @Override
    public <E> E getAsBeanFromJson(JSONObject content, Class<E> classObj) throws IOException {
        return FieldUtil.getAsBeanFromJson(content, classObj);
    }

    @Override
    public void emailException(String fromClass, String msg, Throwable t) {
        CommonCommandUtil.emailException(fromClass, msg, t);
    }

    @Override
    public void emailException(String fromClass, String msg, String reason) {
        CommonCommandUtil.emailException(fromClass, msg, reason);
    }

    @Override
    public void log(JobContext jobContext, long timeTaken, int status) {
        JobLogger.log(jobContext, timeTaken, status);
    }

    @Override
    public Long getServerId() {
        return ServerInfo.getServerId();
    }
}



