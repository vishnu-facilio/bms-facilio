package com.facilio.audit;

import java.util.List;

public interface FacilioAudit {

    long add(AuditData data);
    long update(AuditData data);
    long delete(AuditData data);
    List<AuditData> get();
}
