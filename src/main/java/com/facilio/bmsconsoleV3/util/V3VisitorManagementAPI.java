package com.facilio.bmsconsoleV3.util;

import java.io.File;
import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.*;
import com.facilio.bmsconsoleV3.context.V3VisitorContext;
import com.facilio.bmsconsoleV3.context.V3VisitorLoggingContext;
import com.facilio.tasker.FacilioTimer;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import com.chargebee.internal.StringJoiner;
import com.facilio.accounts.bean.UserBean;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FacilioForm.LabelPosition;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormField.Required;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.templates.DefaultTemplate.DefaultTemplateType;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.bmsconsole.templates.SMSTemplate;
import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fs.FileInfo.FileFormat;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.LookupFieldMeta;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.FacilioField.FieldDisplayType;
import com.facilio.modules.fields.LookupField;
import com.facilio.tasker.ScheduleInfo;
import com.facilio.tasker.ScheduleInfo.FrequencyType;
import com.facilio.time.DateTimeUtil;
import com.facilio.workflows.context.WorkflowContext;

public class V3VisitorManagementAPI {

    public static List<InviteVisitorRelContext> getEventInvitees(long eventId) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_INVITE_REL);
        List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.VISITOR_INVITE_REL);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        SelectRecordsBuilder<InviteVisitorRelContext> builder = new SelectRecordsBuilder<InviteVisitorRelContext>()
                .module(module)
                .beanClass(InviteVisitorRelContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition("INVITE_ID", "inviteId", String.valueOf(eventId), NumberOperators.EQUALS))
                .fetchSupplement((LookupField) fieldsAsMap.get("visitorId"))
                ;


        List<InviteVisitorRelContext> records = builder.get();
        return records;

    }

    public static VisitorInviteContext getVisitorEvent(long eventId) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_INVITE);
        List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.VISITOR_INVITE);
        SelectRecordsBuilder<VisitorInviteContext> builder = new SelectRecordsBuilder<VisitorInviteContext>()
                .module(module)
                .beanClass(VisitorInviteContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(eventId), NumberOperators.EQUALS))
                ;


        VisitorInviteContext records = builder.fetchFirst();
        return records;

    }

    public static V3VisitorLoggingContext getVisitorLogging(long visitorId, boolean fetchActiveLog, long currentLogId) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING);
        List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.VISITOR_LOGGING);
        SelectRecordsBuilder<V3VisitorLoggingContext> builder = new SelectRecordsBuilder<V3VisitorLoggingContext>()
                .module(module)
                .beanClass(V3VisitorLoggingContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition("VISITOR", "visitorId", String.valueOf(visitorId), NumberOperators.EQUALS))
                ;
        if(fetchActiveLog) {
            FacilioStatus checkedInStatus = TicketAPI.getStatus(module, "CheckedIn");
            builder.andCondition(CriteriaAPI.getCondition("MODULE_STATE", "moduleState", String.valueOf(checkedInStatus.getId()), NumberOperators.EQUALS));

        }
        if(currentLogId > 0) {
            builder.andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(currentLogId), NumberOperators.NOT_EQUALS));
        }

        V3VisitorLoggingContext records = builder.fetchFirst();
        return records;

    }

    public static Boolean checkExistingVisitorLogging(long logId) throws Exception {
        long currenttime = System.currentTimeMillis();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING);
        List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.VISITOR_LOGGING);
        SelectRecordsBuilder<V3VisitorLoggingContext> builder = new SelectRecordsBuilder<V3VisitorLoggingContext>()
                .module(module)
                .beanClass(V3VisitorLoggingContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getIdCondition(logId, module))

                ;
        FacilioStatus checkedOutStatus = TicketAPI.getStatus(module, "CheckedOut");

        V3VisitorLoggingContext records = builder.fetchFirst();
        if(records != null && records.getModuleState().getId() == checkedOutStatus.getId()) {
            if(records.getCheckInTime() !=  null && currenttime >= records.getCheckInTime()) {
                if(records.getExpectedCheckOutTime() == null || records.getExpectedCheckOutTime() <= 0) {
                    return true;
                }
                if(records.getExpectedCheckOutTime() != null && records.getExpectedCheckOutTime() > 0 && currenttime <= records.getExpectedCheckOutTime()) {
                    return true;
                }
                return false;
            }
            return false;
        }
        return null;

    }

    public static List<V3VisitorLoggingContext> getRecurringVisitorLogs() throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING);
        List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.VISITOR_LOGGING);
        Map<String, FacilioField> map = FieldFactory.getAsMap(fields);
        SelectRecordsBuilder<V3VisitorLoggingContext> builder = new SelectRecordsBuilder<V3VisitorLoggingContext>()
                .module(module)
                .beanClass(V3VisitorLoggingContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition("IS_RECURRING", "isRecurring", "true", BooleanOperators.IS))
                .andCondition(CriteriaAPI.getCondition(map.get("parentLogId"),CommonOperators.IS_EMPTY))
                ;

        List<V3VisitorLoggingContext> records = builder.get();
        return records;

    }

    public static V3VisitorLoggingContext getVisitorLoggingTriggers(long logId, String passCode, boolean fetchTriggers) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING);
        List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.VISITOR_LOGGING);
        SelectRecordsBuilder<V3VisitorLoggingContext> builder = new SelectRecordsBuilder<V3VisitorLoggingContext>()
                .module(module)
                .beanClass(V3VisitorLoggingContext.class)
                .select(fields)

                ;
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        List<LookupField> additionaLookups = new ArrayList<LookupField>();

        if(logId > 0) {
            builder.andCondition(CriteriaAPI.getIdCondition(logId, module));
        }

        if(StringUtils.isNotEmpty(passCode)) {
            builder.andCondition(CriteriaAPI.getCondition("PASSCODE", "passcode", passCode, StringOperators.IS));
        }

        LookupField contactField = (LookupField) fieldsAsMap.get("visitor");
        LookupField hostField = (LookupField) fieldsAsMap.get("host");
        LookupField visitedSpacefield = (LookupField) fieldsAsMap.get("visitedSpace");
        LookupField moduleStateField = (LookupField)fieldsAsMap.get("moduleState");
        LookupField visitorTypefield = (LookupField)fieldsAsMap.get("visitorType");
        LookupField vendorfield = (LookupField)fieldsAsMap.get("vendor");
        if(AccountUtil.isFeatureEnabled(FeatureLicense.TENANTS)) {
            LookupField tenant = (LookupField)fieldsAsMap.get("tenant");
            additionaLookups.add(tenant);
        }
        LookupField requestedBy = (LookupField)fieldsAsMap.get("requestedBy");

        additionaLookups.add(contactField);
        additionaLookups.add(hostField);
        additionaLookups.add(visitedSpacefield);
        additionaLookups.add(moduleStateField);
        additionaLookups.add(visitorTypefield);
        additionaLookups.add(requestedBy);
        additionaLookups.add(vendorfield);
        builder.fetchSupplements(additionaLookups);

        V3VisitorLoggingContext records = builder.fetchFirst();
        if(records != null && records.isRecurring() && fetchTriggers) {
            GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                    .select(FieldFactory.getVisitorLogTriggerFields())
                    .table(ModuleFactory.getVisitorLogTriggersModule().getTableName())
                    .andCondition(CriteriaAPI.getCondition("VISITOR_LOG_ID", "pmId", String.valueOf(records.getId()), NumberOperators.EQUALS));
            List<Map<String, Object>> map = selectBuilder.get();
            if(CollectionUtils.isNotEmpty(map)) {
                records.setTrigger(FieldUtil.getAsBeanFromMap(map.get(0), PMTriggerContext.class));
            }

        }
        return records;

    }

    public static V3VisitorLoggingContext getValidChildLogForToday(Long parentLogId, Long currentTime, boolean fetchUpcoming, Long visitorId) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING);
        FacilioStatus status = TicketAPI.getStatus(module, "Upcoming");
        List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.VISITOR_LOGGING);
        SelectRecordsBuilder<V3VisitorLoggingContext> builder = new SelectRecordsBuilder<V3VisitorLoggingContext>()
                .module(module)
                .beanClass(V3VisitorLoggingContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition("EXPECTED_CHECKIN_TIME", "expectedCheckInTime", String.valueOf(currentTime) , DateOperators.TODAY))
                .andCondition(CriteriaAPI.getCondition("EXPECTED_CHECKOUT_TIME", "expectedCheckOutTime", String.valueOf(currentTime) , DateOperators.IS_AFTER))

                ;
        if(parentLogId != null && parentLogId > 0) {
            builder.andCondition(CriteriaAPI.getCondition("PARENT_LOG_ID", "parentLogId", String.valueOf(parentLogId), NumberOperators.EQUALS));
        }
        if(visitorId != null && visitorId > 0) {
            builder.andCondition(CriteriaAPI.getCondition("VISITOR", "visitor", String.valueOf(visitorId), NumberOperators.EQUALS));
        }
        if(fetchUpcoming) {
            builder.andCondition(CriteriaAPI.getCondition("MODULE_STATE", "moduleState", String.valueOf(status.getId()), NumberOperators.EQUALS));
        }

        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        LookupFieldMeta visitorField = new LookupFieldMeta((LookupField) fieldsAsMap.get("visitor"));
        List<LookupField> additionaLookups = new ArrayList<LookupField>();
        additionaLookups.add(visitorField);
        builder.fetchSupplements(additionaLookups);


        V3VisitorLoggingContext records = builder.fetchFirst();
        return records;

    }

    public static List<V3VisitorLoggingContext> getAllVisitorLogging(long visitorId) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING);
        List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.VISITOR_LOGGING);
        SelectRecordsBuilder<V3VisitorLoggingContext> builder = new SelectRecordsBuilder<V3VisitorLoggingContext>()
                .module(module)
                .beanClass(V3VisitorLoggingContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition("VISITOR", "visitor", String.valueOf(visitorId), NumberOperators.EQUALS))
                ;

        builder.orderBy("Visitor_Logging.SYS_CREATED_TIME DESC");
        List<V3VisitorLoggingContext> records = builder.get();
        return records;

    }

    public static InviteVisitorRelContext getInviteVisitorRel(long visitorId, long inviteId) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_INVITE_REL);
        List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.VISITOR_INVITE_REL);
        SelectRecordsBuilder<InviteVisitorRelContext> builder = new SelectRecordsBuilder<InviteVisitorRelContext>()
                .module(module)
                .beanClass(InviteVisitorRelContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition("VISITOR_ID", "visitorId", String.valueOf(visitorId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("INVITE_ID", "inviteId", String.valueOf(inviteId), NumberOperators.EQUALS))

                ;
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        LookupFieldMeta inviteField = new LookupFieldMeta((LookupField) fieldsAsMap.get("inviteId"));
        LookupField inviteHost = (LookupField) modBean.getField("inviteHost", FacilioConstants.ContextNames.VISITOR_INVITE);

        LookupFieldMeta visitorField = new LookupFieldMeta((LookupField) fieldsAsMap.get("visitorId"));
        LookupField visitorLocation = (LookupField) modBean.getField("location", FacilioConstants.ContextNames.VISITOR);
        inviteField.addChildLookupField(inviteHost);
        visitorField.addChildLookupField(visitorLocation);

        List<LookupField> additionaLookups = new ArrayList<LookupField>();
        additionaLookups.add(inviteField);
        additionaLookups.add(visitorField);
        builder.fetchSupplements(additionaLookups);
        InviteVisitorRelContext records = builder.fetchFirst();
        return records;

    }

    public static InviteVisitorRelContext getInviteVisitorRel(long id) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_INVITE_REL);
        List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.VISITOR_INVITE_REL);
        SelectRecordsBuilder<InviteVisitorRelContext> builder = new SelectRecordsBuilder<InviteVisitorRelContext>()
                .module(module)
                .beanClass(InviteVisitorRelContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getIdCondition(id, module))
                ;
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        LookupFieldMeta inviteField = new LookupFieldMeta((LookupField) fieldsAsMap.get("inviteId"));
        LookupField inviteHost = (LookupField) modBean.getField("inviteHost", FacilioConstants.ContextNames.VISITOR_INVITE);

        LookupFieldMeta visitorField = new LookupFieldMeta((LookupField) fieldsAsMap.get("visitorId"));
        LookupField visitorLocation = (LookupField) modBean.getField("location", FacilioConstants.ContextNames.VISITOR);
        inviteField.addChildLookupField(inviteHost);
        visitorField.addChildLookupField(visitorLocation);

        List<LookupField> additionaLookups = new ArrayList<LookupField>();
        additionaLookups.add(inviteField);
        additionaLookups.add(visitorField);
        builder.fetchSupplements(additionaLookups);
        InviteVisitorRelContext records = builder.fetchFirst();
        return records;

    }

    public static VisitorInviteContext getVisitorInvite(long inviteId) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_INVITE);
        List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.VISITOR_INVITE);
        SelectRecordsBuilder<VisitorInviteContext> builder = new SelectRecordsBuilder<VisitorInviteContext>()
                .module(module)
                .beanClass(VisitorInviteContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getIdCondition(inviteId, module))
                ;

        VisitorInviteContext record = builder.fetchFirst();
        if(record != null) {
            List<BusinessHoursContext> businessHoursList = BusinessHoursAPI.getBusinessHours(Collections.singletonList(record.getVisitingHoursId()));
            if(CollectionUtils.isNotEmpty(businessHoursList)) {
                record.setRecurringVisitTime(businessHoursList.get(0));
            }
        }
        return record;

    }

    public static InviteVisitorRelContext getVisitorInviteRel(long inviteId, long visitorId) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_INVITE_REL);
        List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.VISITOR_INVITE_REL);
        SelectRecordsBuilder<InviteVisitorRelContext> builder = new SelectRecordsBuilder<InviteVisitorRelContext>()
                .module(module)
                .beanClass(InviteVisitorRelContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition("INVITE_ID", "inviteId", String.valueOf(inviteId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("VISITOR_ID", "visitorId", String.valueOf(visitorId), NumberOperators.EQUALS))
                ;
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        List<LookupField> additionaLookups = new ArrayList<LookupField>();
        LookupField inviteField = (LookupField) fieldsAsMap.get("inviteId");
        LookupField visitorField = (LookupField) fieldsAsMap.get("visitorId");

        additionaLookups.add(inviteField);
        additionaLookups.add(visitorField);

        builder.fetchSupplements(additionaLookups);


        InviteVisitorRelContext record = builder.fetchFirst();
        return record;

    }
    public static V3VisitorContext getVisitor(Long id, String phoneNumber) throws Exception {

        if(id <= 0 && StringUtils.isEmpty(phoneNumber)) {
            return null;
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR);
        List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.VISITOR);
        SelectRecordsBuilder<V3VisitorContext> builder = new SelectRecordsBuilder<V3VisitorContext>()
                .module(module)
                .beanClass(V3VisitorContext.class)
                .select(fields)
                ;

        if(StringUtils.isNotEmpty(phoneNumber)) {
            builder.andCondition(CriteriaAPI.getCondition("PHONE", "phone", String.valueOf(phoneNumber), StringOperators.IS));
        }
        if(id != null && id > 0) {
            builder.andCondition(CriteriaAPI.getIdCondition(id, module));
        }

        V3VisitorContext records = builder.fetchFirst();
        return records;

    }



    public static void updateVisitorLogCheckInCheckoutTime(V3VisitorLoggingContext vLog, boolean isCheckIn, long time) throws Exception {

        if(vLog.getId() > 0) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING);
            List<FacilioField> updatedfields = new ArrayList<FacilioField>();

            Map<String, Object> updateMap = new HashMap<>();
            if(!isCheckIn) {
                FacilioField checkOutTimeField = modBean.getField("checkOutTime", module.getName());
                updateMap.put("checkOutTime", time);
                //FacilioField actualVisitdurationField = modBean.getField("actualVisitDuration", module.getName());

                FacilioField overStayField = modBean.getField("isOverStay", module.getName());
                boolean isOverStay = false;
                if(vLog.getExpectedCheckOutTime() != null && time - vLog.getExpectedCheckOutTime() > 0) {
                    isOverStay = true;
                }
                else {
                    isOverStay = false;
                }
                updateMap.put("isOverStay", isOverStay);

                updatedfields.add(checkOutTimeField);
                updatedfields.add(overStayField);
                //	updatedfields.add(actualVisitdurationField);

                vLog.setCheckOutTime(time);
                //	vLog.setActualVisitDuration(actualVisitDuration);
                vLog.setIsOverStay(isOverStay);
            }
            else {
                FacilioField checkInTimeField = modBean.getField("checkInTime", module.getName());
                updatedfields.add(checkInTimeField);
                updateMap.put("checkInTime", time);
                vLog.setCheckInTime(time);
                if(vLog.getExpectedVisitDuration() != null && vLog.getExpectedVisitDuration() > 0) {
                    FacilioField expectedCheckOutTimeField = modBean.getField("expectedCheckOutTime", module.getName());
                    updateMap.put("expectedCheckOutTime", time + vLog.getExpectedVisitDuration());
                    updatedfields.add(expectedCheckOutTimeField);
                }

            }

            if(StringUtils.isEmpty(vLog.getVisitorPhone()) && vLog.getVisitor() != null) {
                if(StringUtils.isEmpty(vLog.getVisitor().getName())) {
                    vLog.setVisitor(getVisitor(vLog.getVisitor().getId(), null));
                }
                FacilioField visitorNameField = modBean.getField("visitorName", module.getName());
                FacilioField visitorEmailField = modBean.getField("visitorEmail", module.getName());
                FacilioField visitorPhoneField = modBean.getField("visitorPhone", module.getName());

                updateMap.put("visitorName", vLog.getVisitor().getName());
                updateMap.put("visitorEmail", vLog.getVisitor().getEmail());
                updateMap.put("visitorPhone", vLog.getVisitor().getPhone());

                updatedfields.add(visitorNameField);
                updatedfields.add(visitorEmailField);
                updatedfields.add(visitorPhoneField);
            }

            UpdateRecordBuilder<V3VisitorLoggingContext> updateBuilder = new UpdateRecordBuilder<V3VisitorLoggingContext>()
                    .module(module)
                    .fields(updatedfields)
                    .andCondition(CriteriaAPI.getIdCondition(vLog.getId(), module))
                    ;
            updateBuilder.ignoreSplNullHandling();
            updateBuilder.updateViaMap(updateMap);
        }

    }

    public static void updateVisitorLogInvitationStatus(V3VisitorLoggingContext vLog, boolean isInvitationSent) throws Exception {

        if(vLog.getId() > 0) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING);
            List<FacilioField> updatedfields = new ArrayList<FacilioField>();

            Map<String, Object> updateMap = new HashMap<>();
            FacilioField invitationSentField = modBean.getField("isInvitationSent", module.getName());
            updateMap.put("isInvitationSent", isInvitationSent);
            updatedfields.add(invitationSentField);

            UpdateRecordBuilder<V3VisitorLoggingContext> updateBuilder = new UpdateRecordBuilder<V3VisitorLoggingContext>()
                    .module(module)
                    .fields(updatedfields)
                    .andCondition(CriteriaAPI.getIdCondition(vLog.getId(), module))
                    ;
            updateBuilder.ignoreSplNullHandling();
            updateBuilder.updateViaMap(updateMap);
        }

    }

    public static void checkOutVisitorLogging(String visitorPhoneNumber, FacilioContext context) throws Exception {

        if(StringUtils.isNotEmpty(visitorPhoneNumber)) {
            V3VisitorContext visitor = getVisitor(-1L, visitorPhoneNumber);
            if(visitor == null) {
                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid phone number");
            }
            V3VisitorLoggingContext activeLog = getVisitorLogging(visitor.getId(), true, -1);
            if(activeLog == null) {
                throw new RESTException(ErrorCode.VALIDATION_ERROR, "No active CheckIn Log found");
            }
            List<WorkflowRuleContext> nextStateRule = StateFlowRulesAPI.getAvailableState(activeLog.getStateFlowId(), activeLog.getModuleState().getId(), FacilioConstants.ContextNames.VISITOR_LOGGING, activeLog, context);
            activeLog.setCheckOutTime(System.currentTimeMillis());
            long nextTransitionId = nextStateRule.get(0).getId();
            context.put("nextTransitionId", nextTransitionId);
            context.put("visitorLogging", activeLog);
        }

    }

    public static void getActiveLogExcludingCurrentLog(V3VisitorLoggingContext record, FacilioContext context) throws Exception {

        if(record.getVisitor() != null) {
            V3VisitorContext visitor = getVisitor(record.getVisitor().getId(), null);
            if(visitor != null) {
                V3VisitorLoggingContext activeLog = getVisitorLogging(visitor.getId(), true, record.getId());
                if(activeLog != null) {
                    List<WorkflowRuleContext> nextStateRule = StateFlowRulesAPI.getAvailableState(activeLog.getStateFlowId(), activeLog.getModuleState().getId(), FacilioConstants.ContextNames.VISITOR_LOGGING, activeLog, context);
                    activeLog.setCheckOutTime(System.currentTimeMillis());
                    long nextTransitionId = nextStateRule.get(0).getId();
                    context.put("nextTransitionId", nextTransitionId);
                    context.put("visitorLogging", activeLog);
                }
            }
        }

    }



    public static void updateVisitorLastVisitRollUps(V3VisitorLoggingContext visitorLog) throws Exception {

        if(visitorLog != null) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR);
            Map<String, Object> updateMap = new HashMap<>();
            FacilioField lastVisitedTime = modBean.getField("lastVisitedTime", module.getName());
            FacilioField lastVisitedSpace = modBean.getField("lastVisitedSpace", module.getName());
            FacilioField lastVisitedHost = modBean.getField("lastVisitedHost", module.getName());
            FacilioField firstVisitedTime = modBean.getField("firstVisitedTime", module.getName());

            updateMap.put("lastVisitedTime", visitorLog.getCheckInTime());
            updateMap.put("lastVisitedHost", FieldUtil.getAsProperties(visitorLog.getHost()));

            List<FacilioField> updatedfields = new ArrayList<FacilioField>();
            updatedfields.add(lastVisitedTime);
            updatedfields.add(lastVisitedHost);

            if(visitorLog.getVisitedSpace() != null) {
                updateMap.put("lastVisitedSpace", FieldUtil.getAsProperties(visitorLog.getVisitedSpace()));
                updatedfields.add(lastVisitedSpace);
            }
            V3VisitorContext visitor = getVisitor(visitorLog.getVisitor().getId(), null);
            if(visitor.getFirstVisitedTime() == null || visitor.getFirstVisitedTime() <= 0) {
                updateMap.put("firstVisitedTime", visitorLog.getCheckInTime());
                updatedfields.add(firstVisitedTime);
            }
            updatevisitor(visitorLog.getVisitor().getId(),updatedfields, updateMap);
        }


    }

    public static void updateVisitorLastVisitDurationRollUp(V3VisitorLoggingContext visitorLog) throws Exception {

        if(visitorLog != null) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR);
            Map<String, Object> updateMap = new HashMap<>();
            FacilioField lastVisitDuration = modBean.getField("lastVisitDuration", module.getName());

            long workDuration = visitorLog.getCheckOutTime() - visitorLog.getCheckInTime();
            updateMap.put("lastVisitDuration", workDuration);

            List<FacilioField> updatedfields = new ArrayList<FacilioField>();
            updatedfields.add(lastVisitDuration);

            updatevisitor(visitorLog.getVisitor().getId(),updatedfields, updateMap);
        }


    }

    public static void updateVisitorRollUps(V3VisitorLoggingContext visitorLog, V3VisitorLoggingContext oldRecord) throws Exception {

        if(visitorLog != null) {
            V3VisitorLoggingContext updatedVisitorLog = getVisitorLoggingTriggers(visitorLog.getId(), null, false);
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR);
            Map<String, Object> updateMap = new HashMap<>();
            FacilioField avatarId = modBean.getField("avatar", module.getName());
            FacilioField visitorType = modBean.getField("visitorType", module.getName());

            updateMap.put("visitorType", FieldUtil.getAsProperties(updatedVisitorLog.getVisitorType()));

            List<FacilioField> updatedfields = new ArrayList<FacilioField>();
            if(updatedVisitorLog.getAvatarId() != null && updatedVisitorLog.getAvatarId() > 0) {
                updatedfields.add(avatarId);
                updateMap.put("avatar", oldRecord.getAvatar());
            }

            updatedfields.add(visitorType);

            updatevisitor(visitorLog.getVisitor().getId(),updatedfields, updateMap);
        }


    }

    public static void updatevisitor(long visitorId, List<FacilioField> fields, Map<String, Object> updateMap) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR);

        UpdateRecordBuilder<V3VisitorContext> updateBuilder = new UpdateRecordBuilder<V3VisitorContext>()
                .module(module)
                .fields(fields)
                .andCondition(CriteriaAPI.getIdCondition(visitorId, module))
                ;
        updateBuilder.ignoreSplNullHandling();
        updateBuilder.updateViaMap(updateMap);

    }

    public static void updateVisitorLogNDA(long logId, File file) throws Exception {

        if(logId > 0) {
//			AttachmentContext attachment = new AttachmentContext();
//			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
//			FacilioModule attachmentsModule = modBean.getModule("visitorloggingattachments");
//			attachment.setParentId(logId);
//			attachment.setModuleId(attachmentsModule.getModuleId());
//			attachment.setFileId(fileId);
//			attachment.setCreatedTime(System.currentTimeMillis());
//			AttachmentsAPI.addAttachments(Collections.singletonList(attachment), "visitorloggingattachments");

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING);
            List<FacilioField> updatedfields = new ArrayList<FacilioField>();

            Map<String, Object> updateMap = new HashMap<>();
            FacilioField ndaIdField = modBean.getField("nda", module.getName());
            updateMap.put("nda", file);
            updateMap.put("ndaContentType", FileFormat.PDF.getContentType());
            updateMap.put("ndaFileName", "NDA_" + logId);


            updatedfields.add(ndaIdField);

            UpdateRecordBuilder<V3VisitorLoggingContext> updateBuilder = new UpdateRecordBuilder<V3VisitorLoggingContext>()
                    .module(module)
                    .fields(updatedfields)
                    .andCondition(CriteriaAPI.getIdCondition(logId, module))
                    ;
            updateBuilder.ignoreSplNullHandling();
            updateBuilder.updateViaMap(updateMap);

        }

    }

    public static Preference getWelcomeMailNotificationsPref() {

        FacilioForm form = new FacilioForm();
        return new Preference("welcomeVisitor_MailNotification", "Welcome Visitor_Email", form, "Notify Visitors when they check-in") {
            @Override
            public void subsituteAndEnable(Map<String, Object> map, Long recordId, Long moduleId) throws Exception {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(moduleId);
                Long ruleId = saveWelcomeMailNotifications(map, module.getName());
                List<Long> ruleIdList = new ArrayList<>();
                ruleIdList.add(ruleId);
                PreferenceRuleUtil.addPreferenceRule(moduleId, recordId, ruleIdList, getName());
            }

            @Override
            public void disable(Long recordId, Long moduleId) throws Exception {
                // TODO Auto-generated method stub
                PreferenceRuleUtil.disablePreferenceRule(moduleId, recordId, getName());
            }

        };

    }

    public static Preference getWelcomeSmsNotificationsPref() {

        FacilioForm form = new FacilioForm();
        return new Preference("welcomeVisitor_SmsNotification", "Welcome Visitor_SMS", form, "Notify Visitors when they check-in") {
            @Override
            public void subsituteAndEnable(Map<String, Object> map, Long recordId, Long moduleId) throws Exception {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(moduleId);
                Long ruleId = saveWelcomeSmsNotifications(map, module.getName());
                List<Long> ruleIdList = new ArrayList<>();
                ruleIdList.add(ruleId);
                PreferenceRuleUtil.addPreferenceRule(moduleId, recordId, ruleIdList, getName());
            }

            @Override
            public void disable(Long recordId, Long moduleId) throws Exception {
                // TODO Auto-generated method stub
                PreferenceRuleUtil.disablePreferenceRule(moduleId, recordId, getName());
            }

        };

    }

    public static Preference getWelcomeWhatsappNotificationsPref() {

        FacilioForm form = new FacilioForm();
        return new Preference("welcomeVisitor_WhatsappNotification", "Welcome Visitor_Whatsapp", form, "Notify Visitors when they check-in") {
            @Override
            public void subsituteAndEnable(Map<String, Object> map, Long recordId, Long moduleId) throws Exception {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(moduleId);
                Long ruleId = saveWelcomeWhatsappNotifications(map, module.getName());
                List<Long> ruleIdList = new ArrayList<>();
                ruleIdList.add(ruleId);
                PreferenceRuleUtil.addPreferenceRule(moduleId, recordId, ruleIdList, getName());
            }

            @Override
            public void disable(Long recordId, Long moduleId) throws Exception {
                // TODO Auto-generated method stub
                PreferenceRuleUtil.disablePreferenceRule(moduleId, recordId, getName());
            }

        };

    }


    public static Preference getThanksMailNotificationsPref() {

        FacilioForm form = new FacilioForm();
        return new Preference("thankVisitor_MailNotification", "Thank Visitor_Email", form, "Notify Visitor when they check-out") {
            @Override
            public void subsituteAndEnable(Map<String, Object> map, Long recordId, Long moduleId) throws Exception {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(moduleId);
                Long ruleId = saveThanksEmailNotificationPrefs(map, module.getName());
                List<Long> ruleIdList = new ArrayList<>();
                ruleIdList.add(ruleId);
                PreferenceRuleUtil.addPreferenceRule(moduleId, recordId, ruleIdList, getName());
            }

            @Override
            public void disable(Long recordId, Long moduleId) throws Exception {
                // TODO Auto-generated method stub
                PreferenceRuleUtil.disablePreferenceRule(moduleId, recordId, getName());
            }

        };

    }

    public static Preference getThanksSmsNotificationsPref() {

        FacilioForm form = new FacilioForm();
        return new Preference("thankVisitor_SmsNotification", "Thank Visitor_SMS", form, "Notify Visitor when they check-out") {
            @Override
            public void subsituteAndEnable(Map<String, Object> map, Long recordId, Long moduleId) throws Exception {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(moduleId);
                Long ruleId = saveThanksSmsNotificationPrefs(map, module.getName());
                List<Long> ruleIdList = new ArrayList<>();
                ruleIdList.add(ruleId);
                PreferenceRuleUtil.addPreferenceRule(moduleId, recordId, ruleIdList, getName());
            }

            @Override
            public void disable(Long recordId, Long moduleId) throws Exception {
                // TODO Auto-generated method stub
                PreferenceRuleUtil.disablePreferenceRule(moduleId, recordId, getName());
            }

        };

    }

    public static Preference getThanksWhatsappNotificationsPref() {

        FacilioForm form = new FacilioForm();
        return new Preference("thankVisitor_WhatsappNotification", "Thank Visitor_Whatsapp", form, "Notify Visitor when they check-out") {
            @Override
            public void subsituteAndEnable(Map<String, Object> map, Long recordId, Long moduleId) throws Exception {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(moduleId);
                Long ruleId = saveThanksWhatsappNotificationPrefs(map, module.getName());
                List<Long> ruleIdList = new ArrayList<>();
                ruleIdList.add(ruleId);
                PreferenceRuleUtil.addPreferenceRule(moduleId, recordId, ruleIdList, getName());
            }

            @Override
            public void disable(Long recordId, Long moduleId) throws Exception {
                // TODO Auto-generated method stub
                PreferenceRuleUtil.disablePreferenceRule(moduleId, recordId, getName());
            }

        };

    }

    public static Preference getInviteMailNotificationsPref() {

        FacilioForm form = new FacilioForm();
        return new Preference("inviteVisitor_MailNotification", "Invite Visitor_Email", form, "Notify invited visitors before the day of Visit") {
            @Override
            public void subsituteAndEnable(Map<String, Object> map, Long recordId, Long moduleId) throws Exception {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING);
                Long ruleId = saveInviteEmailNotificationPrefs(map, module.getName());
                List<Long> ruleIdList = new ArrayList<>();
                ruleIdList.add(ruleId);
                PreferenceRuleUtil.addPreferenceRule(moduleId, recordId, ruleIdList, getName());
            }

            @Override
            public void disable(Long recordId, Long moduleId) throws Exception {
                // TODO Auto-generated method stub
                PreferenceRuleUtil.disablePreferenceRule(moduleId, recordId, getName());
            }

        };

    }

    public static Preference getInviteSmsNotificationsPref() {

        FacilioForm form = new FacilioForm();
        return new Preference("inviteVisitor_SmsNotification", "Invite Visitor_SMS", form, "Notify invited visitors before the day of Visit") {
            @Override
            public void subsituteAndEnable(Map<String, Object> map, Long recordId, Long moduleId) throws Exception {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING);
                Long ruleId = saveInviteSmsNotificationPrefs(map, module.getName());
                List<Long> ruleIdList = new ArrayList<>();
                ruleIdList.add(ruleId);
                PreferenceRuleUtil.addPreferenceRule(moduleId, recordId, ruleIdList, getName());
            }

            @Override
            public void disable(Long recordId, Long moduleId) throws Exception {
                // TODO Auto-generated method stub
                PreferenceRuleUtil.disablePreferenceRule(moduleId, recordId, getName());
            }

        };

    }

    public static Preference getInviteWhatsappNotificationsPref() {
        FacilioForm form = new FacilioForm();
        return new Preference("inviteVisitor_WhatsappNotification", "Invite Visitor_Whatsapp", form, "Notify invited visitors before the day of Visit") {
            @Override
            public void subsituteAndEnable(Map<String, Object> map, Long recordId, Long moduleId) throws Exception {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING);
                Long ruleId = saveInviteWhatsappNotificationPrefs(map, module.getName());
                List<Long> ruleIdList = new ArrayList<>();
                ruleIdList.add(ruleId);
                PreferenceRuleUtil.addPreferenceRule(moduleId, recordId, ruleIdList, getName());
            }

            @Override
            public void disable(Long recordId, Long moduleId) throws Exception {
                // TODO Auto-generated method stub
                PreferenceRuleUtil.disablePreferenceRule(moduleId, recordId, getName());
            }

        };
    }

    public static Preference getApprovalMailNotificationsPref() {

        FacilioForm form = new FacilioForm();
        return new Preference("approveVisitor_MailNotification", "Approve Visitor_Email", form, "Notify Hosts requesting approval for their Visitors") {
            @Override
            public void subsituteAndEnable(Map<String, Object> map, Long recordId, Long moduleId) throws Exception {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(moduleId);
                Long ruleId = saveApprovalNotificationPrefs(map, module.getName());
                List<Long> ruleIdList = new ArrayList<>();
                ruleIdList.add(ruleId);
                PreferenceRuleUtil.addPreferenceRule(moduleId, recordId, ruleIdList, getName());
            }

            @Override
            public void disable(Long recordId, Long moduleId) throws Exception {
                // TODO Auto-generated method stub
                PreferenceRuleUtil.disablePreferenceRule(moduleId, recordId, getName());
            }

        };

    }

    public static Preference getApprovalSmsNotificationsPref() {

        FacilioForm form = new FacilioForm();
        return new Preference("approveVisitor_SmsNotification", "Approve Visitor_SMS", form, "Notify Hosts requesting approval for their Visitors") {
            @Override
            public void subsituteAndEnable(Map<String, Object> map, Long recordId, Long moduleId) throws Exception {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(moduleId);
                Long ruleId = saveApprovalSmsNotificationPrefs(map, module.getName());
                List<Long> ruleIdList = new ArrayList<>();
                ruleIdList.add(ruleId);
                PreferenceRuleUtil.addPreferenceRule(moduleId, recordId, ruleIdList, getName());
            }

            @Override
            public void disable(Long recordId, Long moduleId) throws Exception {
                // TODO Auto-generated method stub
                PreferenceRuleUtil.disablePreferenceRule(moduleId, recordId, getName());
            }

        };

    }

    public static Preference getApprovalWhatsappNotificationsPref() {

        FacilioForm form = new FacilioForm();
        return new Preference("approveVisitor_WhatsappNotification", "Approve Visitor_Whatsapp", form, "Notify Hosts requesting approval for their Visitors") {
            @Override
            public void subsituteAndEnable(Map<String, Object> map, Long recordId, Long moduleId) throws Exception {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(moduleId);
                Long ruleId = saveApprovalWhatsappNotificationPrefs(map, module.getName());
                List<Long> ruleIdList = new ArrayList<>();
                ruleIdList.add(ruleId);
                PreferenceRuleUtil.addPreferenceRule(moduleId, recordId, ruleIdList, getName());
            }

            @Override
            public void disable(Long recordId, Long moduleId) throws Exception {
                // TODO Auto-generated method stub
                PreferenceRuleUtil.disablePreferenceRule(moduleId, recordId, getName());
            }

        };

    }


    public static Preference getHostMailNotificationsPref() {

        FacilioForm form = new FacilioForm();
        return new Preference("notifyHost_MailNotification", "Notify Host_Email", form, "Automatically notify hosts when their visitors arrive") {
            @Override
            public void subsituteAndEnable(Map<String, Object> map, Long recordId, Long moduleId) throws Exception {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(moduleId);
                Long ruleId = saveHostMailNotificationPrefs(map, module.getName());
                List<Long> ruleIdList = new ArrayList<>();
                ruleIdList.add(ruleId);
                PreferenceRuleUtil.addPreferenceRule(moduleId, recordId, ruleIdList, getName());
            }

            @Override
            public void disable(Long recordId, Long moduleId) throws Exception {
                // TODO Auto-generated method stub
                PreferenceRuleUtil.disablePreferenceRule(moduleId, recordId, getName());
            }

        };

    }

    public static Preference getHostSmsNotificationsPref() {

        FacilioForm form = new FacilioForm();
        return new Preference("notifyHost_SmsNotification", "Notify Host_SMS", form, "Automatically notify hosts when their visitors arrive") {
            @Override
            public void subsituteAndEnable(Map<String, Object> map, Long recordId, Long moduleId) throws Exception {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(moduleId);
                Long ruleId = saveHostSmsNotificationPrefs(map, module.getName());
                List<Long> ruleIdList = new ArrayList<>();
                ruleIdList.add(ruleId);
                PreferenceRuleUtil.addPreferenceRule(moduleId, recordId, ruleIdList, getName());
            }

            @Override
            public void disable(Long recordId, Long moduleId) throws Exception {
                // TODO Auto-generated method stub
                PreferenceRuleUtil.disablePreferenceRule(moduleId, recordId, getName());
            }

        };

    }

    public static Preference getHostWhatsappNotificationsPref() {

        FacilioForm form = new FacilioForm();
        return new Preference("notifyHost_WhatsappNotification", "Notify Host_Whatsapp", form, "Automatically notify hosts when their visitors arrive") {
            @Override
            public void subsituteAndEnable(Map<String, Object> map, Long recordId, Long moduleId) throws Exception {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(moduleId);
                Long ruleId = saveHostWhatsappNotificationPrefs(map, module.getName());
                List<Long> ruleIdList = new ArrayList<>();
                ruleIdList.add(ruleId);
                PreferenceRuleUtil.addPreferenceRule(moduleId, recordId, ruleIdList, getName());
            }

            @Override
            public void disable(Long recordId, Long moduleId) throws Exception {
                // TODO Auto-generated method stub
                PreferenceRuleUtil.disablePreferenceRule(moduleId, recordId, getName());
            }

        };

    }

    public static Long saveHostMailNotificationPrefs (Map<String, Object> map, String moduleName) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);


        WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
        workflowRuleContext.setName("Host Notification");
        workflowRuleContext.setRuleType(RuleType.MODULE_RULE_NOTIFICATION);

        workflowRuleContext.setModuleName(module.getName());
        workflowRuleContext.setActivityType(EventType.CREATE_OR_EDIT);

        Condition condition = new Condition();
        condition.setFieldName("host");
        condition.setOperator(CommonOperators.IS_NOT_EMPTY);
        condition.setColumnName("VisitorLogging.HOST");

        Condition isApprovalNeeded = new Condition();
        isApprovalNeeded.setFieldName("isApprovalNeeded");
        isApprovalNeeded.setOperator(BooleanOperators.IS);
        isApprovalNeeded.setValue("false");
        isApprovalNeeded.setColumnName("VisitorLogging.IS_APPROVAL_NEEDED");


        Criteria criteria = new Criteria();
        criteria.addConditionMap(condition);
        criteria.addConditionMap(isApprovalNeeded);
        criteria.addConditionMap(ViewFactory.getVisitorLogStatusCriteria("CheckedIn"));

        criteria.setPattern("(1 and 2 and 3)");

        workflowRuleContext.setCriteria(criteria);


        ActionContext emailAction = new ActionContext();
        emailAction.setActionType(ActionType.EMAIL_NOTIFICATION);

        emailAction.setDefaultTemplateId(102);
        //add rule,action and job
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRuleContext);
        context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, Collections.singletonList(emailAction));

        FacilioChain chain = TransactionChainFactory.addWorkflowRuleChain();
        chain.execute(context);

        return (Long)context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
    }


    public static Long saveHostSmsNotificationPrefs (Map<String, Object> map, String moduleName) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);


        WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
        workflowRuleContext.setName("Host SMS Notification");
        workflowRuleContext.setRuleType(RuleType.MODULE_RULE_NOTIFICATION);

        workflowRuleContext.setModuleName(module.getName());
        workflowRuleContext.setActivityType(EventType.CREATE_OR_EDIT);

        Condition condition = new Condition();
        condition.setFieldName("host");
        condition.setOperator(CommonOperators.IS_NOT_EMPTY);
        condition.setColumnName("VisitorLogging.HOST");

        Condition isApprovalNeeded = new Condition();
        isApprovalNeeded.setFieldName("isApprovalNeeded");
        isApprovalNeeded.setOperator(BooleanOperators.IS);
        isApprovalNeeded.setValue("false");
        isApprovalNeeded.setColumnName("VisitorLogging.IS_APPROVAL_NEEDED");



        Criteria criteria = new Criteria();
        criteria.addConditionMap(condition);
        criteria.addConditionMap(isApprovalNeeded);
        criteria.addConditionMap(ViewFactory.getVisitorLogStatusCriteria("CheckedIn"));
        criteria.setPattern("(1 and 2 and 3)");

        workflowRuleContext.setCriteria(criteria);


        ActionContext smsAction = new ActionContext();
        smsAction.setActionType(ActionType.SMS_NOTIFICATION);

        smsAction.setDefaultTemplateId(107);
        //add rule,action and job
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRuleContext);
        context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, Collections.singletonList(smsAction));

        FacilioChain chain = TransactionChainFactory.addWorkflowRuleChain();
        chain.execute(context);

        return (Long)context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
    }


    public static Long saveHostWhatsappNotificationPrefs (Map<String, Object> map, String moduleName) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);


        WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
        workflowRuleContext.setName("Host Whatsapp Notification");
        workflowRuleContext.setRuleType(RuleType.MODULE_RULE_NOTIFICATION);

        workflowRuleContext.setModuleName(module.getName());
        workflowRuleContext.setActivityType(EventType.CREATE_OR_EDIT);

        Condition condition = new Condition();
        condition.setFieldName("host");
        condition.setOperator(CommonOperators.IS_NOT_EMPTY);
        condition.setColumnName("VisitorLogging.HOST");

        Condition isApprovalNeeded = new Condition();
        isApprovalNeeded.setFieldName("isApprovalNeeded");
        isApprovalNeeded.setOperator(BooleanOperators.IS);
        isApprovalNeeded.setValue("false");
        isApprovalNeeded.setColumnName("VisitorLogging.IS_APPROVAL_NEEDED");

        Criteria criteria = new Criteria();
        criteria.addConditionMap(condition);
        criteria.addConditionMap(isApprovalNeeded);
        criteria.addConditionMap(ViewFactory.getVisitorLogStatusCriteria("CheckedIn"));
        criteria.setPattern("(1 and 2 and 3)");

        workflowRuleContext.setCriteria(criteria);


        ActionContext whatsappAction = new ActionContext();
        whatsappAction.setActionType(ActionType.WHATSAPP_MESSAGE);

        whatsappAction.setDefaultTemplateId(110);
        //add rule,action and job
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRuleContext);
        context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, Collections.singletonList(whatsappAction));

        FacilioChain chain = TransactionChainFactory.addWorkflowRuleChain();
        chain.execute(context);

        return (Long)context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
    }

    private static Long saveWelcomeMailNotifications(Map<String, Object> map, String moduleName) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);


        WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
        workflowRuleContext.setName("Welcome MAIL Notification");
        workflowRuleContext.setRuleType(RuleType.MODULE_RULE_NOTIFICATION);

        workflowRuleContext.setModuleName(module.getName());
        workflowRuleContext.setActivityType(EventType.CREATE_OR_EDIT);

        Condition condition = new Condition();
        condition.setFieldName("visitor");
        condition.setOperator(CommonOperators.IS_NOT_EMPTY);
        condition.setColumnName("VisitorLogging.VISITOR");

        Criteria criteria = new Criteria();
        criteria.addConditionMap(condition);

        criteria.setPattern("(1 and 2)");
        criteria.addConditionMap(ViewFactory.getVisitorLogStatusCriteria("CheckedIn"));

        workflowRuleContext.setCriteria(criteria);


        ActionContext emailAction = new ActionContext();
        emailAction.setActionType(ActionType.EMAIL_NOTIFICATION);

        emailAction.setDefaultTemplateId(103);
        //add rule,action and job
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRuleContext);
        context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, Collections.singletonList(emailAction));

        FacilioChain chain = TransactionChainFactory.addWorkflowRuleChain();
        chain.execute(context);

        return (Long)context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
    }

    private static Long saveWelcomeSmsNotifications(Map<String, Object> map, String moduleName) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);


        WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
        workflowRuleContext.setName("Welcome SMS Notification");
        workflowRuleContext.setRuleType(RuleType.MODULE_RULE_NOTIFICATION);

        workflowRuleContext.setModuleName(module.getName());
        workflowRuleContext.setActivityType(EventType.CREATE_OR_EDIT);

        Condition condition = new Condition();
        condition.setFieldName("visitor");
        condition.setOperator(CommonOperators.IS_NOT_EMPTY);
        condition.setColumnName("VisitorLogging.VISITOR");

        Criteria criteria = new Criteria();
        criteria.addConditionMap(condition);

        criteria.setPattern("(1 and 2)");
        criteria.addConditionMap(ViewFactory.getVisitorLogStatusCriteria("CheckedIn"));

        workflowRuleContext.setCriteria(criteria);


        ActionContext emailAction = new ActionContext();
        emailAction.setActionType(ActionType.SMS_NOTIFICATION);

        emailAction.setDefaultTemplateId(119);
        //add rule,action and job
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRuleContext);
        context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, Collections.singletonList(emailAction));

        FacilioChain chain = TransactionChainFactory.addWorkflowRuleChain();
        chain.execute(context);

        return (Long)context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
    }

    private static Long saveWelcomeWhatsappNotifications(Map<String, Object> map, String moduleName) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);


        WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
        workflowRuleContext.setName("Welcome Whatsapp Notification");
        workflowRuleContext.setRuleType(RuleType.MODULE_RULE_NOTIFICATION);

        workflowRuleContext.setModuleName(module.getName());
        workflowRuleContext.setActivityType(EventType.CREATE_OR_EDIT);

        Condition condition = new Condition();
        condition.setFieldName("visitor");
        condition.setOperator(CommonOperators.IS_NOT_EMPTY);
        condition.setColumnName("VisitorLogging.VISITOR");

        Criteria criteria = new Criteria();
        criteria.addConditionMap(condition);

        criteria.setPattern("(1 and 2)");
        criteria.addConditionMap(ViewFactory.getVisitorLogStatusCriteria("CheckedIn"));

        workflowRuleContext.setCriteria(criteria);

        ActionContext whatsappAction = new ActionContext();
        whatsappAction.setActionType(ActionType.WHATSAPP_MESSAGE);
        whatsappAction.setDefaultTemplateId(109);

        //add rule,action and job
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRuleContext);
        context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, Collections.singletonList(whatsappAction));

        FacilioChain chain = TransactionChainFactory.addWorkflowRuleChain();
        chain.execute(context);

        return (Long)context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
    }

    public static Long saveThanksEmailNotificationPrefs (Map<String, Object> map, String moduleName) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);


        WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
        workflowRuleContext.setName("Thanks Notification");
        workflowRuleContext.setRuleType(RuleType.MODULE_RULE_NOTIFICATION);

        workflowRuleContext.setModuleName(module.getName());
        workflowRuleContext.setActivityType(EventType.EDIT);

        Condition condition = new Condition();
        condition.setFieldName("visitor");
        condition.setOperator(CommonOperators.IS_NOT_EMPTY);
        condition.setColumnName("VisitorLogging.VISITOR");

        Criteria criteria = new Criteria();
        criteria.addConditionMap(condition);

        criteria.setPattern("(1 and 2)");
        criteria.addConditionMap(ViewFactory.getVisitorLogStatusCriteria("CheckedOut"));

        workflowRuleContext.setCriteria(criteria);


        ActionContext emailAction = new ActionContext();
        emailAction.setActionType(ActionType.EMAIL_NOTIFICATION);

        emailAction.setDefaultTemplateId(104);
        //add rule,action and job
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRuleContext);
        context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, Collections.singletonList(emailAction));

        FacilioChain chain = TransactionChainFactory.addWorkflowRuleChain();
        chain.execute(context);

        return (Long)context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
    }

    public static Long saveThanksSmsNotificationPrefs (Map<String, Object> map, String moduleName) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);


        WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
        workflowRuleContext.setName("Thanks SMS Notification");
        workflowRuleContext.setRuleType(RuleType.MODULE_RULE_NOTIFICATION);

        workflowRuleContext.setModuleName(module.getName());
        workflowRuleContext.setActivityType(EventType.EDIT);

        Condition condition = new Condition();
        condition.setFieldName("visitor");
        condition.setOperator(CommonOperators.IS_NOT_EMPTY);
        condition.setColumnName("VisitorLogging.VISITOR");

        Criteria criteria = new Criteria();
        criteria.addConditionMap(condition);

        criteria.setPattern("(1 and 2)");
        criteria.addConditionMap(ViewFactory.getVisitorLogStatusCriteria("CheckedOut"));

        workflowRuleContext.setCriteria(criteria);


        ActionContext emailAction = new ActionContext();
        emailAction.setActionType(ActionType.SMS_NOTIFICATION);

        emailAction.setDefaultTemplateId(101);
        //add rule,action and job
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRuleContext);
        context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, Collections.singletonList(emailAction));

        FacilioChain chain = TransactionChainFactory.addWorkflowRuleChain();
        chain.execute(context);

        return (Long)context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
    }

    public static Long saveThanksWhatsappNotificationPrefs (Map<String, Object> map, String moduleName) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);


        WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
        workflowRuleContext.setName("Thanks Whatsapp Notification");
        workflowRuleContext.setRuleType(RuleType.MODULE_RULE_NOTIFICATION);

        workflowRuleContext.setModuleName(module.getName());
        workflowRuleContext.setActivityType(EventType.EDIT);

        Condition condition = new Condition();
        condition.setFieldName("visitor");
        condition.setOperator(CommonOperators.IS_NOT_EMPTY);
        condition.setColumnName("VisitorLogging.VISITOR");

        Criteria criteria = new Criteria();
        criteria.addConditionMap(condition);

        criteria.setPattern("(1 and 2)");
        criteria.addConditionMap(ViewFactory.getVisitorLogStatusCriteria("CheckedOut"));

        workflowRuleContext.setCriteria(criteria);


        ActionContext whatsappAction = new ActionContext();
        whatsappAction.setActionType(ActionType.WHATSAPP_MESSAGE);

        whatsappAction.setDefaultTemplateId(111);
        //add rule,action and job
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRuleContext);
        context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, Collections.singletonList(whatsappAction));

        FacilioChain chain = TransactionChainFactory.addWorkflowRuleChain();
        chain.execute(context);

        return (Long)context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
    }

    public static Long saveInviteEmailNotificationPrefs (Map<String, Object> map, String moduleName) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);


        WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
        workflowRuleContext.setName("Invite Notification");
        workflowRuleContext.setRuleType(RuleType.MODULE_RULE_NOTIFICATION);

        workflowRuleContext.setModuleName(module.getName());
        workflowRuleContext.setActivityType(EventType.CREATE_OR_EDIT);

        Condition condition = new Condition();
        condition.setFieldName("visitor");
        condition.setOperator(CommonOperators.IS_NOT_EMPTY);
        condition.setColumnName("Visitor_Logging.VISITOR");

        Condition preregisteredCondition = new Condition();
        preregisteredCondition.setFieldName("isPreregistered");
        preregisteredCondition.setOperator(BooleanOperators.IS);
        preregisteredCondition.setValue("true");
        preregisteredCondition.setColumnName("Visitor_Logging.IS_PREREGISTERED");

        Criteria criteria = new Criteria();
        criteria.addConditionMap(condition);
        criteria.addConditionMap(preregisteredCondition);
        criteria.addConditionMap(ViewFactory.getVisitorLogStatusCriteria("Upcoming"));


        criteria.setPattern("(1 and 2 and 3)");

        workflowRuleContext.setCriteria(criteria);


        ActionContext emailAction = new ActionContext();
        emailAction.setActionType(ActionType.EMAIL_NOTIFICATION);

        emailAction.setDefaultTemplateId(105);
        //add rule,action and job
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRuleContext);
        context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, Collections.singletonList(emailAction));

        FacilioChain chain = TransactionChainFactory.addWorkflowRuleChain();
        chain.execute(context);

        return (Long)context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
    }


    public static Long saveInviteSmsNotificationPrefs (Map<String, Object> map, String moduleName) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);


        WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
        workflowRuleContext.setName("Invite SMS Notification");
        workflowRuleContext.setRuleType(RuleType.MODULE_RULE_NOTIFICATION);

        workflowRuleContext.setModuleName(module.getName());
        workflowRuleContext.setActivityType(EventType.CREATE_OR_EDIT);

        Condition condition = new Condition();
        condition.setFieldName("visitor");
        condition.setOperator(CommonOperators.IS_NOT_EMPTY);
        condition.setColumnName("Visitor_Logging.VISITOR");

        Condition preregisteredCondition = new Condition();
        preregisteredCondition.setFieldName("isPreregistered");
        preregisteredCondition.setOperator(BooleanOperators.IS);
        preregisteredCondition.setValue("true");
        preregisteredCondition.setColumnName("Visitor_Logging.IS_PREREGISTERED");

        Criteria criteria = new Criteria();
        criteria.addConditionMap(condition);
        criteria.addConditionMap(preregisteredCondition);
        criteria.addConditionMap(ViewFactory.getVisitorLogStatusCriteria("Upcoming"));


        criteria.setPattern("(1 and 2 and 3)");

        workflowRuleContext.setCriteria(criteria);


        ActionContext emailAction = new ActionContext();
        emailAction.setActionType(ActionType.SMS_NOTIFICATION);

        emailAction.setDefaultTemplateId(108);
        //add rule,action and job
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRuleContext);
        context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, Collections.singletonList(emailAction));

        FacilioChain chain = TransactionChainFactory.addWorkflowRuleChain();
        chain.execute(context);

        return (Long)context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
    }

    public static Long saveInviteWhatsappNotificationPrefs(Map<String, Object> map, String moduleName) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);

        WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
        workflowRuleContext.setName("Invite Whatsapp Notification");
        workflowRuleContext.setRuleType(RuleType.MODULE_RULE_NOTIFICATION);

        workflowRuleContext.setModuleName(module.getName());
        workflowRuleContext.setActivityType(EventType.CREATE_OR_EDIT);

        Condition condition = new Condition();
        condition.setFieldName("visitor");
        condition.setOperator(CommonOperators.IS_NOT_EMPTY);
        condition.setColumnName("Visitor_Logging.VISITOR");

        Condition preregisteredCondition = new Condition();
        preregisteredCondition.setFieldName("isPreregistered");
        preregisteredCondition.setOperator(BooleanOperators.IS);
        preregisteredCondition.setValue("true");
        preregisteredCondition.setColumnName("Visitor_Logging.IS_PREREGISTERED");

        Criteria criteria = new Criteria();
        criteria.addConditionMap(condition);
        criteria.addConditionMap(preregisteredCondition);
        criteria.addConditionMap(ViewFactory.getVisitorLogStatusCriteria("Upcoming"));


        criteria.setPattern("(1 and 2 and 3)");

        workflowRuleContext.setCriteria(criteria);

        ActionContext whatsappAction = new ActionContext();
        whatsappAction.setActionType(ActionType.WHATSAPP_MESSAGE);
        whatsappAction.setDefaultTemplateId(112);

        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRuleContext);
        context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, Collections.singletonList(whatsappAction));

        FacilioChain chain = TransactionChainFactory.addWorkflowRuleChain();
        chain.execute(context);

        return (Long)context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
    }

    public static Long saveApprovalNotificationPrefs (Map<String, Object> map, String moduleName) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);


        WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
        workflowRuleContext.setName("Approval Notification");
        workflowRuleContext.setRuleType(RuleType.MODULE_RULE_NOTIFICATION);

        workflowRuleContext.setModuleName(module.getName());
        workflowRuleContext.setActivityType(EventType.CREATE);

        Condition condition = new Condition();
        condition.setFieldName("host");
        condition.setOperator(CommonOperators.IS_NOT_EMPTY);
        condition.setColumnName("VisitorLogging.HOST");

        Condition isApprovalNeeded = new Condition();
        isApprovalNeeded.setFieldName("isApprovalNeeded");
        isApprovalNeeded.setOperator(BooleanOperators.IS);
        isApprovalNeeded.setValue("true");
        isApprovalNeeded.setColumnName("VisitorLogging.IS_APPROVAL_NEEDED");

        Condition isPreregistered = new Condition();
        isPreregistered.setFieldName("isPreregistered");
        isPreregistered.setOperator(BooleanOperators.IS);
        isPreregistered.setValue("false");
        isPreregistered.setColumnName("VisitorLogging.IS_PREREGISTERED");

        Criteria criteria = new Criteria();
        criteria.addConditionMap(condition);
        criteria.addConditionMap(isApprovalNeeded);
        criteria.addConditionMap(isPreregistered);

        criteria.setPattern("(1 and 2 and 3 and 4)");
        criteria.addConditionMap(ViewFactory.getVisitorLogStatusCriteria("Requested"));

        workflowRuleContext.setCriteria(criteria);


        ActionContext emailAction = new ActionContext();
        emailAction.setActionType(ActionType.EMAIL_NOTIFICATION);

        emailAction.setDefaultTemplateId(106);
        //add rule,action and job
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRuleContext);
        context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, Collections.singletonList(emailAction));

        FacilioChain chain = TransactionChainFactory.addWorkflowRuleChain();
        chain.execute(context);

        return (Long)context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
    }

    public static Long saveApprovalSmsNotificationPrefs (Map<String, Object> map, String moduleName) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);


        WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
        workflowRuleContext.setName("Approval SMS Notification");
        workflowRuleContext.setRuleType(RuleType.MODULE_RULE_NOTIFICATION);

        workflowRuleContext.setModuleName(module.getName());
        workflowRuleContext.setActivityType(EventType.CREATE);

        Condition condition = new Condition();
        condition.setFieldName("host");
        condition.setOperator(CommonOperators.IS_NOT_EMPTY);
        condition.setColumnName("VisitorLogging.HOST");

        Condition isApprovalNeeded = new Condition();
        isApprovalNeeded.setFieldName("isApprovalNeeded");
        isApprovalNeeded.setOperator(BooleanOperators.IS);
        isApprovalNeeded.setValue("true");
        isApprovalNeeded.setColumnName("VisitorLogging.IS_APPROVAL_NEEDED");

        Condition isPreregistered = new Condition();
        isPreregistered.setFieldName("isPreregistered");
        isPreregistered.setOperator(BooleanOperators.IS);
        isPreregistered.setValue("false");
        isPreregistered.setColumnName("VisitorLogging.IS_PREREGISTERED");


        Criteria criteria = new Criteria();
        criteria.addConditionMap(condition);
        criteria.addConditionMap(isApprovalNeeded);
        criteria.addConditionMap(isPreregistered);

        criteria.setPattern("(1 and 2 and 3 and 4)");
        criteria.addConditionMap(ViewFactory.getVisitorLogStatusCriteria("Requested"));

        workflowRuleContext.setCriteria(criteria);


        ActionContext emailAction = new ActionContext();
        emailAction.setActionType(ActionType.SMS_NOTIFICATION);

        emailAction.setDefaultTemplateId(99);
        //add rule,action and job
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRuleContext);
        context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, Collections.singletonList(emailAction));

        FacilioChain chain = TransactionChainFactory.addWorkflowRuleChain();
        chain.execute(context);

        return (Long)context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
    }
    public static Long saveApprovalWhatsappNotificationPrefs (Map<String, Object> map, String moduleName) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);


        WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
        workflowRuleContext.setName("Approval Whatsapp Notification");
        workflowRuleContext.setRuleType(RuleType.MODULE_RULE_NOTIFICATION);

        workflowRuleContext.setModuleName(module.getName());
        workflowRuleContext.setActivityType(EventType.CREATE);

        Condition condition = new Condition();
        condition.setFieldName("host");
        condition.setOperator(CommonOperators.IS_NOT_EMPTY);
        condition.setColumnName("VisitorLogging.HOST");

        Condition isApprovalNeeded = new Condition();
        isApprovalNeeded.setFieldName("isApprovalNeeded");
        isApprovalNeeded.setOperator(BooleanOperators.IS);
        isApprovalNeeded.setValue("true");
        isApprovalNeeded.setColumnName("VisitorLogging.IS_APPROVAL_NEEDED");

        Condition isPreregistered = new Condition();
        isPreregistered.setFieldName("isPreregistered");
        isPreregistered.setOperator(BooleanOperators.IS);
        isPreregistered.setValue("false");
        isPreregistered.setColumnName("VisitorLogging.IS_PREREGISTERED");

        Criteria criteria = new Criteria();
        criteria.addConditionMap(condition);
        criteria.addConditionMap(isApprovalNeeded);
        criteria.addConditionMap(isPreregistered);

        criteria.setPattern("(1 and 2 and 3 and 4)");
        criteria.addConditionMap(ViewFactory.getVisitorLogStatusCriteria("Requested"));

        workflowRuleContext.setCriteria(criteria);


        ActionContext whatsappAction = new ActionContext();
        whatsappAction.setActionType(ActionType.WHATSAPP_MESSAGE);

        whatsappAction.setDefaultTemplateId(113);
        //add rule,action and job
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRuleContext);
        context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, Collections.singletonList(whatsappAction));

        FacilioChain chain = TransactionChainFactory.addWorkflowRuleChain();
        chain.execute(context);

        return (Long)context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
    }

    public static void scheduleVisitorLog(long visitorLogId, ScheduleActions action, long endTime) throws Exception {
        BmsJobUtil.deleteJobsWithProps(Collections.singletonList(visitorLogId), "ScheduleNewVisitorLogs");
    }

    public static long getEndTime(long startTime, List<PMTriggerContext> triggers) {
        Optional<PMTriggerContext> minTrigger = triggers.stream().min(Comparator.comparingInt(PMTriggerContext::getFrequency));

        int maxSchedulingDays = minTrigger.get().getFrequencyEnum().getMaxSchedulingDays();
        if (startTime == -1) {
            return DateTimeUtil.getDayStartTime(maxSchedulingDays, true) - 1;
        }

        return startTime + (maxSchedulingDays * 24 * 60 * 60);
    }

    public static long getStartTime(int action, V3VisitorLoggingContext log, PMTriggerContext trigger) {
        long startTime = -1;
        if (action == 1 && log.getExpectedCheckInTime() != null) {
            startTime = getStartTimeInSecond(log.getExpectedCheckInTime());
        } else if (action == 2) {
            startTime = log.getLogGeneratedUpto();
        }
        return startTime;
    }

    public static long getStartTimeInSecond(long startTime) {

        long startTimeInSecond = startTime / 1000;
        startTimeInSecond = startTimeInSecond - 300; //for calculating next execution time

        return startTimeInSecond;
    }

    public static void updateGeneratedUptoInLogAndAddChildren(PMTriggerContext trigger, V3VisitorLoggingContext parentLog, List<V3VisitorLoggingContext> children) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.VISITOR_LOGGING);
        V3RecordAPI.addRecord(true, children, module, fields);
        V3RecordAPI.updateRecord(parentLog, module, fields);
        updateTrigger(trigger);
    }

    public static void updateTrigger(PMTriggerContext trigger) throws Exception {
        GenericUpdateRecordBuilder update = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getVisitorLogTriggersModule().getTableName())
                .fields(FieldFactory.getVisitorLogTriggerFields())
                .andCondition(CriteriaAPI.getIdCondition(trigger.getId(), ModuleFactory.getVisitorLogTriggersModule()));
        update.update(FieldUtil.getAsProperties(trigger));
    }

    public enum ScheduleActions {
        GENERATION,
        NIGHTLY;

        public int getVal() {
            return ordinal() + 1;
        }

        public static ScheduleActions getEnum(int val) {
            if (val > 0 && val <= values().length) {
                return values()[val - 1];
            }
            return null;
        }
    }

    public static void deleteUpcomingChildLogs(long parentLogId, long currentTime) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING);
        DeleteRecordBuilder<V3VisitorLoggingContext> deleteBuilder = new DeleteRecordBuilder<V3VisitorLoggingContext>()
                .module(module)
                .andCondition(CriteriaAPI.getCondition("PARENT_LOG_ID", "parentLogId", String.valueOf(parentLogId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("EXPECTED_CHECKIN_TIME", "expectedCheckInTime", String.valueOf(currentTime), DateOperators.IS_AFTER));

        deleteBuilder.markAsDelete();

    }

    public static FacilioStatus getLogStatus(String statusString) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING);
        FacilioStatus status = TicketAPI.getStatus(module, statusString);
        return status;

    }

    public static Map<Long, VisitorSettingsContext> getVisitorSettingsForType() throws Exception {
        FacilioModule module = ModuleFactory.getVisitorSettingsModule();
        List<FacilioField> fields = FieldFactory.getVisitorSettingsFields();
        Map<Long, VisitorSettingsContext> typeSettingsMap = new HashMap<Long, VisitorSettingsContext>();

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(fields)
                .table(module.getTableName());
        List<Map<String, Object>> map = selectBuilder.get();
        if(CollectionUtils.isNotEmpty(map)) {
            for(Map<String, Object> visitorSetting : map) {
                VisitorSettingsContext settingBean = FieldUtil.getAsBeanFromMap(visitorSetting, VisitorSettingsContext.class);
                typeSettingsMap.put(settingBean.getVisitorTypeId(), settingBean);
            }
        }
        return typeSettingsMap;

    }

    private static boolean passcodeExists(String passCode) throws Exception{

        V3VisitorLoggingContext vLog = getVisitorLoggingTriggers(-1, passCode, false);
        return !(vLog == null);
    }

    public static String generatePassCode() throws Exception {
        String passCode = null;
        Random random = new Random();
        while(true) {
            passCode = String.format("%04d", random.nextInt(10000));
            if(!passcodeExists(passCode)) {
                break;
            }
        }
        return passCode;
    }

    public static Long saveBlockedVisitorMailNotificationPrefs (Map<String, Object> map, String moduleName) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING);


        WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
        workflowRuleContext.setName("Blocked Visitor Notification");
        workflowRuleContext.setRuleType(RuleType.MODULE_RULE_NOTIFICATION);

        workflowRuleContext.setModuleName(module.getName());
        workflowRuleContext.setActivityType(EventType.CREATE);

        Condition isBlocked = new Condition();
        isBlocked.setFieldName("isBlocked");
        isBlocked.setOperator(BooleanOperators.IS);
        isBlocked.setValue("true");
        isBlocked.setColumnName("VisitorLogging.IS_BLOCKED");


        Criteria criteria = new Criteria();
        criteria.addConditionMap(isBlocked);

        criteria.setPattern("(1)");

        workflowRuleContext.setCriteria(criteria);

        ActionContext emailAction = new ActionContext();
        emailAction.setActionType(ActionType.EMAIL_NOTIFICATION);

        List<String> ouIdList = (List<String>)map.get("to");
        UserBean userBean = (UserBean) BeanFactory.lookup("UserBean");

        StringJoiner userEmailStr = new StringJoiner(",");
        for(String ouId : ouIdList) {
            User user = userBean.getUser(Long.parseLong(ouId), false);
            if(user != null) {
                userEmailStr.add(user.getEmail());
            }
        }

        EMailTemplate temp = addTemplate("Blocked Email Template", userEmailStr.toString(), 114);
        emailAction.setTemplateId(temp.getId());
        //add rule,action and job
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRuleContext);
        context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, Collections.singletonList(emailAction));

        FacilioChain chain = TransactionChainFactory.addWorkflowRuleChain();
        chain.execute(context);

        return (Long)context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
    }


    public static void saveAutoCheckOutVisitorsPrefs (Map<String, Object> map) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR);
//		JobContext job = new JobContext();
//		job.setJobName(FacilioConstants.Job.AUTO_CHECKOUT_JOB_NAME);
//		job.setActive(true);
//		job.setExecutorName("facilio");
//		job.setIsPeriodic(true);
//		job.setOrgId(AccountUtil.getCurrentOrg().getId());

        ScheduleInfo info = new ScheduleInfo();
        info.setFrequencyType(FrequencyType.DAILY);
        List<String> timeList = new ArrayList<String>();
        if(MapUtils.isNotEmpty(map)) {

            ZonedDateTime date = DateTimeUtil.getDateTime((Long)map.get("autoCheckOutTime"), false);
            DecimalFormat formatter = new DecimalFormat("00");
            String dateFormatted = formatter.format(date.getHour()) + ":" + formatter.format(date.getMinute());
            timeList.add(dateFormatted);
            info.setTimes(timeList);
//			job.setJobId(module.getModuleId());
//			job.setSchedule(info);
//			job.setExecutionTime(info.nextExecutionTime(System.currentTimeMillis() / 1000));
            FacilioTimer.scheduleCalendarJob(module.getModuleId(), FacilioConstants.Job.AUTO_CHECKOUT_JOB_NAME, System.currentTimeMillis(), info, "facilio");
//			FacilioService.runAsService(() -> JobStore.addJob(job));
        }
    }

    public static Long saveVipVisitorMailNotificationPrefs (Map<String, Object> map, String moduleName) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING);


        WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
        workflowRuleContext.setName("VIP Visitor Notification");
        workflowRuleContext.setRuleType(RuleType.MODULE_RULE_NOTIFICATION);

        workflowRuleContext.setModuleName(module.getName());
        workflowRuleContext.setActivityType(EventType.CREATE);

        Condition isVip = new Condition();
        isVip.setFieldName("isVip");
        isVip.setOperator(BooleanOperators.IS);
        isVip.setValue("true");
        isVip.setColumnName("VisitorLogging.IS_VIP");


        Criteria criteria = new Criteria();
        criteria.addConditionMap(isVip);

        criteria.setPattern("(1)");

        workflowRuleContext.setCriteria(criteria);

        ActionContext emailAction = new ActionContext();
        emailAction.setActionType(ActionType.EMAIL_NOTIFICATION);

        List<String> ouIdList = (List<String>)map.get("to");
        UserBean userBean = (UserBean) BeanFactory.lookup("UserBean");

        StringJoiner userEmailStr = new StringJoiner(",");
        for(String ouId : ouIdList) {
            User user = userBean.getUser(Long.parseLong(ouId), false);
            if(user != null) {
                userEmailStr.add(user.getEmail());
            }
        }

        EMailTemplate temp = addTemplate("Vip Email Template", userEmailStr.toString(), 118);
        emailAction.setTemplateId(temp.getId());
        //add rule,action and job
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRuleContext);
        context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, Collections.singletonList(emailAction));

        FacilioChain chain = TransactionChainFactory.addWorkflowRuleChain();
        chain.execute(context);

        return (Long)context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
    }


    public static Long saveBlockedVisitorSmsNotificationPrefs (Map<String, Object> map, String moduleName) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING);


        WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
        workflowRuleContext.setName("Blocked Visitor Notification");
        workflowRuleContext.setRuleType(RuleType.MODULE_RULE_NOTIFICATION);

        workflowRuleContext.setModuleName(module.getName());
        workflowRuleContext.setActivityType(EventType.CREATE);

        Condition isBlocked = new Condition();
        isBlocked.setFieldName("isBlocked");
        isBlocked.setOperator(BooleanOperators.IS);
        isBlocked.setValue("true");
        isBlocked.setColumnName("VisitorLogging.IS_BLOCKED");


        Criteria criteria = new Criteria();
        criteria.addConditionMap(isBlocked);

        criteria.setPattern("(1)");

        workflowRuleContext.setCriteria(criteria);

        ActionContext emailAction = new ActionContext();
        emailAction.setActionType(ActionType.SMS_NOTIFICATION);

        List<String> ouIdList = (List<String>)map.get("to");
        UserBean userBean = (UserBean) BeanFactory.lookup("UserBean");

        StringJoiner userEmailStr = new StringJoiner(",");
        for(String ouId : ouIdList) {
            User user = userBean.getUser(Long.parseLong(ouId), false);
            if(user != null && StringUtils.isNotEmpty(user.getMobile())) {
                userEmailStr.add(user.getMobile());
            }
        }
        if(!userEmailStr.isEmpty()) {

            SMSTemplate temp = addSmsTemplate("Blocked SMS Template", userEmailStr.toString(), 115);
            emailAction.setTemplateId(temp.getId());
            //add rule,action and job
            FacilioContext context = new FacilioContext();
            context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRuleContext);
            context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, Collections.singletonList(emailAction));

            FacilioChain chain = TransactionChainFactory.addWorkflowRuleChain();
            chain.execute(context);

            return (Long)context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
        }
        return null;
    }


    public static Preference getBlockedMailNotificationPref() {
        FacilioForm form = new FacilioForm();
        List<FormSection> sections = new ArrayList<FormSection>();
        FormSection formSection = new FormSection();
        formSection.setName("Blocked Visitor Notification Preference");
        List<FormField> fields = new ArrayList<FormField>();
        fields.add(new FormField("to", FieldDisplayType.MULTI_USER_LIST, "Select User(s)", Required.REQUIRED,"users", 1, 1));

        formSection.setFields(fields);
        sections.add(formSection);
        form.setSections(sections);
        form.setFields(fields);
        form.setLabelPosition(LabelPosition.TOP);
        return new Preference("notifyBlocked_MailNotification", "Notify on Blocked visitor checkin_Email", form, "Define who needs to be notified when a visitor matches a blocked record in watchlist") {
            @Override
            public void subsituteAndEnable(Map<String, Object> map, Long recordId, Long moduleId) throws Exception {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(moduleId);
                Long ruleId = saveBlockedVisitorMailNotificationPrefs(map, module.getName());
                List<Long> ruleIdList = new ArrayList<>();
                ruleIdList.add(ruleId);
                PreferenceRuleUtil.addPreferenceRule(moduleId, recordId, ruleIdList, getName());
            }

            @Override
            public void disable(Long recordId, Long moduleId) throws Exception {
                // TODO Auto-generated method stub
                PreferenceRuleUtil.disablePreferenceRule(moduleId, recordId, getName());
            }

        };

    }

    public static Preference getVipVisitorMailNotificationPref() {
        FacilioForm form = new FacilioForm();
        List<FormSection> sections = new ArrayList<FormSection>();
        FormSection formSection = new FormSection();
        formSection.setName("VIP Visitor Notification Preference");
        List<FormField> fields = new ArrayList<FormField>();
        fields.add(new FormField("to", FieldDisplayType.MULTI_USER_LIST, "Select User(s)", Required.REQUIRED,"users", 1, 1));

        formSection.setFields(fields);
        sections.add(formSection);
        form.setSections(sections);
        form.setFields(fields);
        form.setLabelPosition(LabelPosition.TOP);
        return new Preference("notifyVip_MailNotification", "Notify on VIP visitor checkin_Email", form, "Define who needs to be notified when a visitor matches a VIP record in watchlist") {
            @Override
            public void subsituteAndEnable(Map<String, Object> map, Long recordId, Long moduleId) throws Exception {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(moduleId);
                Long ruleId = saveVipVisitorMailNotificationPrefs(map, module.getName());
                List<Long> ruleIdList = new ArrayList<>();
                ruleIdList.add(ruleId);
                PreferenceRuleUtil.addPreferenceRule(moduleId, recordId, ruleIdList, getName());
            }

            @Override
            public void disable(Long recordId, Long moduleId) throws Exception {
                // TODO Auto-generated method stub
                PreferenceRuleUtil.disablePreferenceRule(moduleId, recordId, getName());
            }

        };

    }
    private static EMailTemplate addTemplate(String name, String email, int defaultTempId) throws Exception {
        EMailTemplate emailTemplate = new EMailTemplate();
        JSONObject blockedVisitorMailJson = TemplateAPI.getDefaultTemplate(DefaultTemplateType.ACTION, defaultTempId).getOriginalTemplate();
        WorkflowContext wf = TemplateAPI.getDefaultTemplate(DefaultTemplateType.ACTION, defaultTempId).getWorkflow();
        emailTemplate.setName("Name");
        emailTemplate.setType(Template.Type.EMAIL);
        emailTemplate.setFrom((String) blockedVisitorMailJson.get("sender"));
        emailTemplate.setTo(email);
        emailTemplate.setSubject((String) blockedVisitorMailJson.get("subject"));
        emailTemplate.setMessage((String) blockedVisitorMailJson.get("message"));
        emailTemplate.setFtl(true);
        emailTemplate.setWorkflow(wf);

        long id = TemplateAPI.addEmailTemplate(AccountUtil.getCurrentOrg().getOrgId(), emailTemplate);
        emailTemplate.setId(id);

        return emailTemplate;
    }

    private static SMSTemplate addSmsTemplate(String name, String phoneNumbers, int defaultTempId) throws Exception {
        SMSTemplate smsTemplate = new SMSTemplate();
        JSONObject blockedVisitorMailJson = TemplateAPI.getDefaultTemplate(DefaultTemplateType.ACTION, defaultTempId).getOriginalTemplate();
        WorkflowContext wf = TemplateAPI.getDefaultTemplate(DefaultTemplateType.ACTION, defaultTempId).getWorkflow();
        smsTemplate.setName("Name");
        smsTemplate.setType(Template.Type.SMS);
        smsTemplate.setFrom((String) blockedVisitorMailJson.get("sender"));
        smsTemplate.setTo(phoneNumbers);
        smsTemplate.setMessage((String) blockedVisitorMailJson.get("message"));
        smsTemplate.setFtl(true);
        smsTemplate.setWorkflow(wf);

        long id = TemplateAPI.addSMSTemplate(AccountUtil.getCurrentOrg().getOrgId(), smsTemplate);
        smsTemplate.setId(id);

        return smsTemplate;
    }

    public static Preference getBlockedSmsNotificationPref() {
        FacilioForm form = new FacilioForm();
        List<FormSection> sections = new ArrayList<FormSection>();
        FormSection formSection = new FormSection();
        formSection.setName("Blocked Visitor SMS Notification Preference");
        List<FormField> fields = new ArrayList<FormField>();
        fields.add(new FormField("to", FieldDisplayType.MULTI_USER_LIST, "Select User(s)", Required.REQUIRED,"users", 1, 1));

        formSection.setFields(fields);
        sections.add(formSection);
        form.setSections(sections);
        form.setFields(fields);
        form.setLabelPosition(LabelPosition.TOP);
        return new Preference("notifyBlocked_SmsNotification", "Notify on Blocked visitor checkin_SMS", form, "Define who needs to be notified when a visitor matches a blocked record in watchlist") {
            @Override
            public void subsituteAndEnable(Map<String, Object> map, Long recordId, Long moduleId) throws Exception {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(moduleId);
                Long ruleId = saveBlockedVisitorSmsNotificationPrefs(map, module.getName());
                if(ruleId != null) {
                    List<Long> ruleIdList = new ArrayList<>();
                    ruleIdList.add(ruleId);
                    PreferenceRuleUtil.addPreferenceRule(moduleId, recordId, ruleIdList, getName());
                }
            }

            @Override
            public void disable(Long recordId, Long moduleId) throws Exception {
                // TODO Auto-generated method stub
                PreferenceRuleUtil.disablePreferenceRule(moduleId, recordId, getName());
            }

        };

    }

    public static Preference getVipSmsNotificationPref() {
        FacilioForm form = new FacilioForm();
        List<FormSection> sections = new ArrayList<FormSection>();
        FormSection formSection = new FormSection();
        formSection.setName("VIP Visitor SMS Notification Preference");
        List<FormField> fields = new ArrayList<FormField>();
        fields.add(new FormField("to", FieldDisplayType.MULTI_USER_LIST, "Select User(s)", Required.REQUIRED,"users", 1, 1));

        formSection.setFields(fields);
        sections.add(formSection);
        form.setSections(sections);
        form.setFields(fields);
        form.setLabelPosition(LabelPosition.TOP);
        return new Preference("notifyVip_SmsNotification", "Notify on VIP visitor checkin_SMS", form, "Define who needs to be notified when a visitor matches a VIP record in watchlist") {
            @Override
            public void subsituteAndEnable(Map<String, Object> map, Long recordId, Long moduleId) throws Exception {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(moduleId);
                Long ruleId = saveVipVisitorSmsNotificationPrefs(map, module.getName());
                if(ruleId != null) {
                    List<Long> ruleIdList = new ArrayList<>();
                    ruleIdList.add(ruleId);
                    PreferenceRuleUtil.addPreferenceRule(moduleId, recordId, ruleIdList, getName());
                }
            }

            @Override
            public void disable(Long recordId, Long moduleId) throws Exception {
                // TODO Auto-generated method stub
                PreferenceRuleUtil.disablePreferenceRule(moduleId, recordId, getName());
            }

        };

    }


    public static Long saveVipVisitorSmsNotificationPrefs (Map<String, Object> map, String moduleName) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING);


        WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
        workflowRuleContext.setName("VIP Visitor Notification");
        workflowRuleContext.setRuleType(RuleType.MODULE_RULE_NOTIFICATION);

        workflowRuleContext.setModuleName(module.getName());
        workflowRuleContext.setActivityType(EventType.CREATE);

        Condition isVip = new Condition();
        isVip.setFieldName("isVip");
        isVip.setOperator(BooleanOperators.IS);
        isVip.setValue("true");
        isVip.setColumnName("VisitorLogging.IS_VIP");


        Criteria criteria = new Criteria();
        criteria.addConditionMap(isVip);

        criteria.setPattern("(1)");

        workflowRuleContext.setCriteria(criteria);

        ActionContext emailAction = new ActionContext();
        emailAction.setActionType(ActionType.SMS_NOTIFICATION);

        List<String> ouIdList = (List<String>)map.get("to");
        UserBean userBean = (UserBean) BeanFactory.lookup("UserBean");

        StringJoiner userEmailStr = new StringJoiner(",");
        for(String ouId : ouIdList) {
            User user = userBean.getUser(Long.parseLong(ouId), false);
            if(user != null && StringUtils.isNotEmpty(user.getMobile())) {
                userEmailStr.add(user.getMobile());
            }
        }
        if(!userEmailStr.isEmpty()) {
            SMSTemplate temp = addSmsTemplate("VIP SMS Template", userEmailStr.toString(), 117);
            emailAction.setTemplateId(temp.getId());
            //add rule,action and job
            FacilioContext context = new FacilioContext();
            context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRuleContext);
            context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, Collections.singletonList(emailAction));

            FacilioChain chain = TransactionChainFactory.addWorkflowRuleChain();
            chain.execute(context);

            return (Long)context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
        }
        return null;
    }

    public static boolean checkForDuplicateVisitor(V3VisitorContext visitor) throws Exception {
        V3VisitorContext visitorExisiting = getVisitor(-1L, visitor.getPhone());
        if(visitorExisiting != null && visitor.getId() != visitorExisiting.getId()) {
            return true;
        }
        return false;
    }

    public static Preference getVisitorCheckOutPref() {
        FacilioForm form = new FacilioForm();
        List<FormSection> sections = new ArrayList<FormSection>();
        FormSection formSection = new FormSection();
        formSection.setName("Visitor CheckOut Preference");
        List<FormField> fields = new ArrayList<FormField>();
        fields.add(new FormField("autoCheckOutTime", FieldDisplayType.TIME, "Sign visitors out at", Required.REQUIRED, 1, 1));

        formSection.setFields(fields);
        sections.add(formSection);
        form.setSections(sections);
        form.setFields(fields);
        form.setLabelPosition(LabelPosition.TOP);
        return new Preference("autoCheckOut", "Automatic Check Out", form, "Choose when to automatically check out all visitors.") {
            @Override
            public void subsituteAndEnable(Map<String, Object> map, Long recordId, Long moduleId) throws Exception {
                saveAutoCheckOutVisitorsPrefs(map);
            }

            @Override
            public void disable(Long recordId, Long moduleId) throws Exception {
                // TODO Auto-generated method stub
                FacilioTimer.deleteJob(moduleId, FacilioConstants.Job.AUTO_CHECKOUT_JOB_NAME);
            }

        };

    }

    public static void autoCheckOutVisitors() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING);
        List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.VISITOR_LOGGING);

        FacilioStatus checkedInStatus = TicketAPI.getStatus(module, "CheckedIn");
        FacilioStatus checkedOutStatus = TicketAPI.getStatus(module, "CheckedOut");

        List<FacilioField> updatedfields = new ArrayList<FacilioField>();

        UpdateRecordBuilder<V3VisitorLoggingContext> updateBuilder = new UpdateRecordBuilder<V3VisitorLoggingContext>()
                .module(module)
                .fields(updatedfields)
                .andCondition(CriteriaAPI.getCondition("MODULE_STATE", "moduleState", String.valueOf(checkedInStatus.getId()), NumberOperators.EQUALS))
                ;
        Map<String, Object> updateMap = new HashMap<>();
        FacilioField statusField = modBean.getField("moduleState", module.getName());
        FacilioField checkOutField = modBean.getField("checkOutTime", module.getName());

        updateMap.put("moduleState", FieldUtil.getAsProperties(checkedOutStatus));
        updateMap.put("checkOutTime", System.currentTimeMillis());

        updatedfields.add(statusField);
        updatedfields.add(checkOutField);

        updateBuilder.updateViaMap(updateMap);
    }

}
