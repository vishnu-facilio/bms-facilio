package com.facilio.backgroundactivity.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.backgroundactivity.context.BackgroundActivity;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.transaction.NewTransactionService;
import com.facilio.function.Unchecked;
import com.facilio.fw.cache.LRUCache;
import com.facilio.iam.accounts.util.IAMUtil;
import com.facilio.service.FacilioService;
import com.facilio.time.DateTimeUtil;
import lombok.Getter;
import lombok.extern.log4j.Log4j;

import java.util.concurrent.Callable;

import static com.facilio.backgroundactivity.util.BackgroundActivityAPI.isActivityLicenseEnabled;

@Log4j
public class BackgroundActivityService implements BackgroundActivityInterface {

    @Getter
    private Long activityId;
    public BackgroundActivityService(Long recordId,String recordType,String name,String message) {
        Long activityId = ignoreExceptionWithReturn(() -> addActivity(recordId,recordType,name,message,null));
        this.activityId = activityId;
    }

    public BackgroundActivityService(Long activityId) {
        this.activityId = activityId;
    }

    private Long addActivity(Long recordId,String recordType,String name,String message,Long initiatedBy) {
        try {
            return ignoreExceptionWithReturn(() -> BackgroundActivityAPI.addBackgroundActivity(recordId, recordType, name, message, initiatedBy));
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

    public ChildActivityService addChildActivity(Long recordId,String recordType,String name,String message) {
        return ignoreExceptionWithReturn(() -> new ChildActivityService(activityId,recordId,recordType,name,message));
    }

    public static <T> T ignoreExceptionWithReturn(Callable<T> callable) {
        try {
            if(isActivityLicenseEnabled()) {
                return callable.call();
            }
        } catch (Exception e) {
            LOGGER.error("Background Activity Ignore Exception With Return => ", e);
        }
        return null;
    }

    public static void ignoreException(Unchecked.Runnable runnable) {
        try {
            if(isActivityLicenseEnabled()) {
                runnable.run();
            }
        } catch (Exception e) {
            LOGGER.error("Background Activity Ignore Exception => ", e);
        }
    }
}