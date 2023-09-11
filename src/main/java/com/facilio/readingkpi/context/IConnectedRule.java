package com.facilio.readingkpi.context;

import com.facilio.ns.context.NameSpaceContext;

// Any class that works on namespace must implement this interface
public interface IConnectedRule {
    long getId();
    String getName();
    Long getReadingFieldId();
    NameSpaceContext getNs();
    long insertLog(Long startTime, Long endTime, Integer resourceCount, boolean isSysCreated) throws Exception;
}
