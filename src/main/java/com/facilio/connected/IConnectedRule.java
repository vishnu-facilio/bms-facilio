package com.facilio.connected;

import com.facilio.ns.context.NameSpaceContext;
import com.facilio.v3.context.V3Context;

// Any class that works on namespace must implement this interface
public interface IConnectedRule {
    long getId();

    String getName();

    Long getReadingFieldId();

    NameSpaceContext getNs();

    ResourceCategory<? extends V3Context> getCategory();

    long insertLog(Long startTime, Long endTime, Integer resourceCount, boolean isSysCreated) throws Exception;
}
