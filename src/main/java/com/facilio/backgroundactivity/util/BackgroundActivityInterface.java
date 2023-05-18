package com.facilio.backgroundactivity.util;

public interface BackgroundActivityInterface {

    void updateActivity(Integer percentage,String message) throws Exception;

    void completeActivity(String message) throws Exception;

    void failActivity(String message) throws Exception;

    Long getActivityId();

    void updateMessage(String message);
}