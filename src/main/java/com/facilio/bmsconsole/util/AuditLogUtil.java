package com.facilio.bmsconsole.util;

import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.ModuleFactory;
import com.facilio.wmsv2.handler.AuditLogHandler;

public class AuditLogUtil {

    public static void insertAuditLog(AuditLogHandler.AuditLog auditLog) throws Exception {
        if (auditLog == null) {
            return;
        }
        InsertRecordBuilder builder = new InsertRecordBuilder()
                .module(ModuleFactory.getAuditLogModule())
                .fields(FieldFactory.getAuditLogFields());
        builder.addRecordProp(FieldUtil.getAsProperties(auditLog));
        builder.save();
    }
}
