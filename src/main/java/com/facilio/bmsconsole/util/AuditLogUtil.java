package com.facilio.bmsconsole.util;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.wmsv2.handler.AuditLogHandler;

import java.util.List;

public class AuditLogUtil {

    public static void insertAuditLog(AuditLogHandler.AuditLogContext auditLog) throws Exception {
        if (auditLog == null) {
            return;
        }

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.AUDIT_LOGS);
        InsertRecordBuilder builder = new InsertRecordBuilder()
                .module(module)
                .fields(modBean.getAllFields(module.getName()));
        builder.addRecordProp(FieldUtil.getAsProperties(auditLog));
        builder.save();
    }

    public static List<AuditLogHandler.AuditLogContext> getAuditLogs() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.AUDIT_LOGS);
        SelectRecordsBuilder<AuditLogHandler.AuditLogContext> builder = new SelectRecordsBuilder<AuditLogHandler.AuditLogContext>()
                .module(module)
                .select(modBean.getAllFields(module.getName()))
                .beanClass(AuditLogHandler.AuditLogContext.class);
        return builder.get();
    }
}
