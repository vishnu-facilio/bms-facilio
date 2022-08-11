package com.facilio.mailtracking;

import com.facilio.mailtracking.context.V3OutgoingMailLogContext;

public interface OutgoingMailData {
    public void loadMailData(V3OutgoingMailLogContext record) throws Exception;
}
