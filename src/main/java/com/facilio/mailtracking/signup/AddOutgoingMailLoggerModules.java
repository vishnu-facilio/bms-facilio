package com.facilio.mailtracking.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.RollUpField;
import com.facilio.bmsconsole.util.RollUpFieldUtil;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.mailtracking.MailConstants;
import com.facilio.mailtracking.context.MailStatus;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.StringSystemEnumField;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import lombok.extern.log4j.Log4j;

import java.util.ArrayList;
import java.util.List;

@Log4j
public class AddOutgoingMailLoggerModules extends SignUpData {
    @Override
    public void addData() throws Exception {
        FacilioModule mailModule = constructOutgoingMailModule();
        FacilioModule recipientModule = constructOutgoingRecipientModule(mailModule);

        List<FacilioModule> modules = new ArrayList<>();
        modules.add(mailModule);
        modules.add(recipientModule);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();

        addRecipientStatusRollUpFields(mailModule, recipientModule);

        LOGGER.info("OutgoingMail related modules added successfully");
    }

    private void addRecipientStatusRollUpFields(FacilioModule mailModule, FacilioModule recipientModule) throws Exception {
        ModuleBean modBean = Constants.getModBean();

        FacilioField logger = modBean.getField("logger", recipientModule.getName());
        FacilioUtil.throwIllegalArgumentException(logger == null, "logger::outgoingMailLogger cannot be null");

        FacilioField statusField = modBean.getField("status", recipientModule.getName());
        FacilioUtil.throwIllegalArgumentException(statusField == null, "status::outgoingMailLogger cannot be null");

        FacilioField recipientCount = modBean.getField("recipientCount", mailModule.getName());
        FacilioUtil.throwIllegalArgumentException(recipientCount == null, "recipientCount::outgoingRecipientLogger cannot be null");

        FacilioField inProgressCount = modBean.getField("inProgressCount", mailModule.getName());
        FacilioUtil.throwIllegalArgumentException(inProgressCount == null, "inProgressCount::outgoingRecipientLogger cannot be null");

        FacilioField sentCount = modBean.getField("sentCount", mailModule.getName());
        FacilioUtil.throwIllegalArgumentException(sentCount == null, "sentCount::outgoingRecipientLogger cannot be null");

        FacilioField deliveredCount = modBean.getField("deliveredCount", mailModule.getName());
        FacilioUtil.throwIllegalArgumentException(deliveredCount == null, "deliveredCount::outgoingRecipientLogger cannot be null");

        FacilioField bouncedCount = modBean.getField("bouncedCount", mailModule.getName());
        FacilioUtil.throwIllegalArgumentException(bouncedCount == null, "bouncedCount::outgoingRecipientLogger cannot be null");


        List<RollUpField> rollUpFields = new ArrayList<>();
        rollUpFields.add(constructRollUpField("Total Recipient Counts", recipientModule, logger, mailModule, recipientCount, null));
        rollUpFields.add(constructRollUpField("In progress Recipient Counts", recipientModule, logger, mailModule, inProgressCount,
                CriteriaAPI.getCondition(statusField, String.valueOf(MailStatus.IN_PROGRESS.getValue()), NumberOperators.EQUALS)));
        rollUpFields.add(constructRollUpField("Sent Recipient Counts", recipientModule, logger, mailModule, sentCount,
                CriteriaAPI.getCondition(statusField, String.valueOf(MailStatus.SENT.getValue()), NumberOperators.EQUALS)));
        rollUpFields.add(constructRollUpField("Delivered Recipient Counts", recipientModule, logger, mailModule, deliveredCount,
                CriteriaAPI.getCondition(statusField, String.valueOf(MailStatus.DELIVERED.getValue()), NumberOperators.EQUALS)));
        rollUpFields.add(constructRollUpField("Bounced Recipient Counts", recipientModule, logger, mailModule, bouncedCount,
                CriteriaAPI.getCondition(statusField, String.valueOf(MailStatus.BOUNCED.getValue()), NumberOperators.EQUALS)));
        RollUpFieldUtil.addRollUpField(rollUpFields);
    }

    public RollUpField constructRollUpField(String desc, FacilioModule childModule, FacilioField childLookupField, FacilioModule parentModule, FacilioField parentRollupField, Condition condition) throws Exception {
        RollUpField rollUp = new RollUpField();
        rollUp.setDescription(desc);
        rollUp.setAggregateFunctionId(BmsAggregateOperators.CommonAggregateOperator.COUNT.getValue());
        rollUp.setChildModuleId(childModule.getModuleId());
        rollUp.setChildFieldId(childLookupField.getFieldId());
        rollUp.setParentModuleId(parentModule.getModuleId());
        rollUp.setParentRollUpFieldId(parentRollupField.getFieldId());
        rollUp.setIsSystemRollUpField(true);
        if (condition != null) {
            Criteria criteria = new Criteria();
            criteria.addAndCondition(condition);
            rollUp.setChildCriteriaId(CriteriaAPI.addCriteria(criteria));
        }
        return rollUp;
    }

    private FacilioModule constructOutgoingMailModule() {
        FacilioModule module = new FacilioModule(MailConstants.ModuleNames.OUTGOING_MAIL_LOGGER,
                "Outgoing Mail Logger", "Outgoing_Mail_Logger", FacilioModule.ModuleType.BASE_ENTITY);

        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getDefaultField("mapperId", "Mapper ID", "MAPPER_ID", FieldType.NUMBER, true));
        fields.add(FieldFactory.getDefaultField("recordId", "Record ID", "RECORD_ID", FieldType.NUMBER));
        fields.add(FieldFactory.getDefaultField("recordsModuleId", "Records Module ID", "RECORDS_MODULE_ID", FieldType.NUMBER));
        fields.add(FieldFactory.getDefaultField("messageId", "Message ID", "MESSAGE_ID", FieldType.STRING));
//        fields.add(FieldFactory.getDefaultField("sourceType", "Source Type", "SOURCE_TYPE", FieldType.STRING));

        StringSystemEnumField sourceTypeField =  FieldFactory.getDefaultField("sourceType", "Source Type", "SOURCE_TYPE", FieldType.STRING_SYSTEM_ENUM);
        sourceTypeField.setEnumName("MailSourceType");
        fields.add(sourceTypeField);


        fields.add(FieldFactory.getDefaultField("sender", "From Address", "SENDER", FieldType.STRING));
        fields.add(FieldFactory.getDefaultField("to", "To Addresses", "TO_ADDR", FieldType.STRING));
        fields.add(FieldFactory.getDefaultField("cc", "CC Addresses", "CC_ADDR", FieldType.STRING));
        fields.add(FieldFactory.getDefaultField("bcc", "BCC Addresses", "BCC_ADDR", FieldType.STRING));
        fields.add(FieldFactory.getDefaultField("subject", "Subject", "SUBJECT", FieldType.STRING));
        fields.add(FieldFactory.getDefaultField("message", "Message", "MESSAGE", FieldType.STRING));
        fields.add(FieldFactory.getDefaultField("recipientCount", "Recipient Count", "RECIPIENT_COUNT", FieldType.NUMBER));
        fields.add(FieldFactory.getDefaultField("inProgressCount", "InProgress Count", "IN_PROGRESS_COUNT", FieldType.NUMBER));
        fields.add(FieldFactory.getDefaultField("sentCount", "Sent Count", "SENT_COUNT", FieldType.NUMBER));
        fields.add(FieldFactory.getDefaultField("deliveredCount", "Delivered Count", "DELIVERED_COUNT", FieldType.NUMBER));
        fields.add(FieldFactory.getDefaultField("bouncedCount", "Bounced Count", "BOUNCED_COUNT", FieldType.NUMBER));

        module.setFields(fields);
        return module;
    }

    private FacilioModule constructOutgoingRecipientModule(FacilioModule mailModule) {
        FacilioModule module = new FacilioModule(MailConstants.ModuleNames.OUTGOING_RECIPIENT_LOGGER,
                "Outgoing Recipient Logger", "Outgoing_Recipient_Logger", FacilioModule.ModuleType.BASE_ENTITY);
        List<FacilioField> fields = new ArrayList<>();

        LookupField logger = FieldFactory.getDefaultField("logger", "Logger", "LOGGER", FieldType.LOOKUP);
        logger.setMainField(true);
        logger.setLookupModule(mailModule);
        fields.add(logger);

        fields.add(FieldFactory.getDefaultField("name", "Name", "NAME", FieldType.STRING));
        fields.add(FieldFactory.getDefaultField("recipient", "Recipient", "RECIPIENT", FieldType.STRING));
        fields.add(FieldFactory.getDefaultField("status", "Status", "STATUS", FieldType.NUMBER));
        fields.add(FieldFactory.getDefaultField("bounceType", "Bounce Type", "BOUNCE_TYPE", FieldType.NUMBER));
        fields.add(FieldFactory.getDefaultField("bounceReason", "Bounce Reason", "BOUNCE_REASON", FieldType.STRING));
        fields.add(FieldFactory.getDefaultField("diagnosticCode", "Diagnostic Code", "DIAGNOSTIC_CODE", FieldType.STRING));
        fields.add(FieldFactory.getSystemField("sysCreatedTime", module));
        fields.add(FieldFactory.getSystemField("sysModifiedTime", module));

        module.setFields(fields);
        return module;
    }
}
