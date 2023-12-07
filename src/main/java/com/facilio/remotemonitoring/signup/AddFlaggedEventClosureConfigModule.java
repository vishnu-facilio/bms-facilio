package com.facilio.remotemonitoring.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;
import com.facilio.telemetry.signup.AddTelemetryCriteriaModule;

public class AddFlaggedEventClosureConfigModule extends SignUpData {
    public static final String MODULE_NAME = "flaggedEventClosureConfig";
    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = new FacilioModule();
        module.setName(MODULE_NAME);
        module.setDisplayName("Flagged Event Closure Config");
        module.setDescription("Flagged Event Closure Config");
        module.setTableName("Flagged_Event_Closure_Config");
        module.setCustom(false);
        module.setType(FacilioModule.ModuleType.SUB_ENTITY);
        module.setTrashEnabled(false);
        modBean.addModule(module);

        FacilioModule mod = modBean.getModule(MODULE_NAME);

        LookupField flaggedEventRule = new LookupField();
        flaggedEventRule.setName("flaggedEventRule");
        flaggedEventRule.setModule(mod);
        flaggedEventRule.setDisplayName("Flagged Event Rule");
        flaggedEventRule.setDataType(FieldType.LOOKUP);
        flaggedEventRule.setColumnName("FLAGGED_EVENT_RULE_ID");
        flaggedEventRule.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        flaggedEventRule.setLookupModule(modBean.getModule(FlaggedEventRuleModule.MODULE_NAME));
        flaggedEventRule.setDefault(true);
        modBean.addField(flaggedEventRule);

        StringSystemEnumField closureType = new StringSystemEnumField();
        closureType.setName("closureType");
        closureType.setModule(mod);
        closureType.setDisplayName("Closure Type");
        closureType.setColumnName("CLOSURE_TYPE");
        closureType.setEnumName("ClosureType");
        closureType.setDataType(FieldType.STRING_SYSTEM_ENUM);
        closureType.setDisplayType(FacilioField.FieldDisplayType.SELECTBOX);
        closureType.setDefault(true);
        modBean.addField(closureType);

        MultiLookupField workorderStatuses = new MultiLookupField();
        workorderStatuses.setName("workorderStatuses");
        workorderStatuses.setModule(mod);
        workorderStatuses.setDisplayName("Workorder Statuses");
        workorderStatuses.setDataType(FieldType.MULTI_LOOKUP);
        workorderStatuses.setDisplayType(FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE);
        workorderStatuses.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.TICKET_STATUS));
        workorderStatuses.setDefault(true);
        modBean.addField(workorderStatuses);

        NumberField autoClosureTime = new NumberField();
        autoClosureTime.setName("autoClosureDuration");
        autoClosureTime.setModule(mod);
        autoClosureTime.setDisplayName("Auto Closure Duration");
        autoClosureTime.setDataType(FieldType.NUMBER);
        autoClosureTime.setColumnName("AUTO_CLOSURE_DURATION");
        autoClosureTime.setDisplayType(FacilioField.FieldDisplayType.DURATION);
        autoClosureTime.setDefault(true);
        modBean.addField(autoClosureTime);

        NumberField closeEmailNotificationRuleId = new NumberField();
        closeEmailNotificationRuleId.setName("closeEmailNotificationRuleId");
        closeEmailNotificationRuleId.setModule(mod);
        closeEmailNotificationRuleId.setDisplayName("Close Email Notification Rule Id");
        closeEmailNotificationRuleId.setDataType(FieldType.NUMBER);
        closeEmailNotificationRuleId.setColumnName("CLOSE_EMAIL_NOTIFICATION_RULE_ID");
        closeEmailNotificationRuleId.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
        closeEmailNotificationRuleId.setDefault(true);
        modBean.addField(closeEmailNotificationRuleId);

        StringSystemEnumField closureRestriction = new StringSystemEnumField();
        closureRestriction.setName("closureRestriction");
        closureRestriction.setModule(mod);
        closureRestriction.setDisplayName("Closure Restriction");
        closureRestriction.setDataType(FieldType.STRING_SYSTEM_ENUM);
        closureRestriction.setColumnName("CLOSURE_RESTRICTION");
        closureRestriction.setEnumName("ClosureRestriction");
        closureRestriction.setDisplayType(FacilioField.FieldDisplayType.SELECTBOX);
        closureRestriction.setDefault(true);
        modBean.addField(closureRestriction);

        LargeTextField warningMessage = new LargeTextField();
        warningMessage.setName("warningMessage");
        warningMessage.setModule(mod);
        warningMessage.setDisplayName("Warning Message");
        warningMessage.setDataType(FieldType.LARGE_TEXT);
        warningMessage.setDisplayType(FacilioField.FieldDisplayType.RICH_TEXT);
        warningMessage.setDefault(true);
        modBean.addField(warningMessage);

        BooleanField sendWorkorderCloseCommand = new BooleanField();
        sendWorkorderCloseCommand.setName("sendWorkorderCloseCommand");
        sendWorkorderCloseCommand.setModule(mod);
        sendWorkorderCloseCommand.setDisplayName("Send Workorder Close Command");
        sendWorkorderCloseCommand.setColumnName("SEND_WO_CLOSE_COMMAND");
        sendWorkorderCloseCommand.setDataType(FieldType.BOOLEAN);
        sendWorkorderCloseCommand.setDisplayType(FacilioField.FieldDisplayType.RADIO);
        sendWorkorderCloseCommand.setDefault(true);
        modBean.addField(sendWorkorderCloseCommand);

        BooleanField autoCloseOnClear = new BooleanField();
        autoCloseOnClear.setName("autoCloseOnClear");
        autoCloseOnClear.setDisplayName("Auto Close On Clear");
        autoCloseOnClear.setColumnName("AUTO_CLOSE_ON_CLEAR");
        autoCloseOnClear.setDataType(FieldType.BOOLEAN);
        autoCloseOnClear.setDisplayType(FacilioField.FieldDisplayType.RADIO);
        autoCloseOnClear.setModule(mod);
        autoCloseOnClear.setDefault(true);
        modBean.addField(autoCloseOnClear);

        MultiLookupField workorderCloseCommandCriteria = new MultiLookupField();
        workorderCloseCommandCriteria.setName("workorderCloseCommandCriteria");
        workorderCloseCommandCriteria.setModule(mod);
        workorderCloseCommandCriteria.setDisplayName("Workorder Close Command Criteria");
        workorderCloseCommandCriteria.setDataType(FieldType.MULTI_LOOKUP);
        workorderCloseCommandCriteria.setDisplayType(FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE);
        workorderCloseCommandCriteria.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.TICKET_STATUS));
        workorderCloseCommandCriteria.setDefault(true);
        modBean.addField(workorderCloseCommandCriteria);

        LookupField workorderCloseStatus = new LookupField();
        workorderCloseStatus.setName("workorderCloseStatus");
        workorderCloseStatus.setModule(mod);
        workorderCloseStatus.setDisplayName("Workorder Close Status");
        workorderCloseStatus.setDataType(FieldType.LOOKUP);
        workorderCloseStatus.setColumnName("WORKORDER_CLOSE_STATUS");
        workorderCloseStatus.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        workorderCloseStatus.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.TICKET_STATUS));
        workorderCloseStatus.setDefault(true);
        modBean.addField(workorderCloseStatus);

        NumberField flaggedEventTriggerCriteriaReevaluationTime = new NumberField();
        flaggedEventTriggerCriteriaReevaluationTime.setName("flaggedEventTriggerCriteriaReevaluationTime");
        flaggedEventTriggerCriteriaReevaluationTime.setModule(mod);
        flaggedEventTriggerCriteriaReevaluationTime.setDisplayName("Flagged Event Trigger Reevaluation Time");
        flaggedEventTriggerCriteriaReevaluationTime.setDataType(FieldType.NUMBER);
        flaggedEventTriggerCriteriaReevaluationTime.setColumnName("TRIGGER_REEVALUATION_TIME");
        flaggedEventTriggerCriteriaReevaluationTime.setDisplayType(FacilioField.FieldDisplayType.DURATION);
        flaggedEventTriggerCriteriaReevaluationTime.setDefault(true);
        modBean.addField(flaggedEventTriggerCriteriaReevaluationTime);

        MultiLookupField onCloseTelemetryCriteria = new MultiLookupField();
        onCloseTelemetryCriteria.setName("onCloseTelemetryCriteria");
        onCloseTelemetryCriteria.setDisplayName("On Close Telemetry Criteria");
        onCloseTelemetryCriteria.setModule(mod);
        onCloseTelemetryCriteria.setLookupModule(modBean.getModule(AddTelemetryCriteriaModule.MODULE_NAME));
        onCloseTelemetryCriteria.setDataType(FieldType.MULTI_LOOKUP);
        onCloseTelemetryCriteria.setDefault(true);
        onCloseTelemetryCriteria.setDisplayType(FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE);
        modBean.addField(onCloseTelemetryCriteria);
    }
}