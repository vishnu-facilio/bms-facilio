package com.facilio.backgroundactivity.util;

import com.facilio.db.transaction.NewTransactionService;
import com.facilio.function.Unchecked;
import lombok.Getter;
import lombok.extern.log4j.Log4j;

import java.util.concurrent.Callable;

import static com.facilio.backgroundactivity.util.BackgroundActivityAPI.isActivityLicenseEnabled;

@Log4j
public class ChildActivityService implements BackgroundActivityInterface {

    @Getter
    private Long activityId;
    public ChildActivityService(Long parentActivityId, Long recordId,String recordType,String name,String message) {
        Long activityId = ignoreExceptionWithReturn(() -> addActivity(parentActivityId,recordId,recordType,name,message,null));
        this.activityId = activityId;
    }

    public ChildActivityService(Long activityId) {
        this.activityId = activityId;
    }

    private Long addActivity(Long parentActivityId, Long recordId,String recordType,String name,String message,Long initiatedBy) {
        try {
            return ignoreExceptionWithReturn(() -> BackgroundActivityAPI.addChildBackgroundActivity(recordId, recordType, name, message, initiatedBy, parentActivityId));
        } catch (Exception e) {
            LOGGER.error("Background Activity addActivity => ", e);
        }
        return null;
    }

    @Override
    public void updateActivity(Integer percentage,String message) {
        ignoreException(() -> BackgroundActivityAPI.updateBackgroundActivity(activityId,percentage,message));
    }

    @Override
    public void completeActivity(String message) {
        ignoreException(() -> BackgroundActivityAPI.completeBackgroundActivity(activityId,message));
    }

    @Override
    public void failActivity(String message) {
        ignoreException(() -> BackgroundActivityAPI.failBackgroundActivity(activityId,message));
    }

    @Override
    public void updateMessage(String activityMessage) {
        ignoreException(() -> BackgroundActivityAPI.updateBackgroundActivityMessage(activityId,activityMessage));
    }

    public static <T> T ignoreExceptionWithReturn(Callable<T> callable) {
        try {
            if(isActivityLicenseEnabled()) {
                return callable.call();
            }
        } catch (Exception e) {
            LOGGER.error("Child Background Activity Ignore Exception With Return => ", e);
        }
        return null;
    }

    public static void ignoreException(Unchecked.Runnable runnable) {
        try {
            if(isActivityLicenseEnabled()) {
                runnable.run();
            }
        } catch (Exception e) {
            LOGGER.error("Child Background Activity Ignore Exception => ", e);
        }
    }
}