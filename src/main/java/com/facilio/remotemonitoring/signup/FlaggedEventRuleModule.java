package com.facilio.remotemonitoring.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;

public class FlaggedEventRuleModule extends SignUpData {

    public static final String MODULE_NAME = "flaggedAlarmProcess";
    public static final String MODULE_DISPLAY_NAME = "Flagged Alarm Process";

    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = new FacilioModule();
        module.setName(MODULE_NAME);
        module.setDisplayName(MODULE_DISPLAY_NAME);
        module.setDescription("Flagged Alarm Process");
        module.setTableName("Flagged_Event_Rule");
        module.setCustom(false);
        module.setType(FacilioModule.ModuleType.BASE_ENTITY);
        module.setTrashEnabled(true);
        modBean.addModule(module);

        FacilioModule mod = modBean.getModule(MODULE_NAME);

        StringField nameField = new StringField();
        nameField.setName("name");
        nameField.setModule(mod);
        nameField.setDisplayName("Name");
        nameField.setColumnName("NAME");
        nameField.setDataType(FieldType.STRING);
        nameField.setDisplayType(FacilioField.FieldDisplayType.TEXTBOX);
        nameField.setDefault(true);
        nameField.setMainField(true);
        modBean.addField(nameField);

        LargeTextField descriptionField = new LargeTextField();
        descriptionField.setName("description");
        descriptionField.setDisplayName("Description");
        descriptionField.setModule(mod);
        descriptionField.setDataType(FieldType.LARGE_TEXT);
        descriptionField.setDisplayType(FacilioField.FieldDisplayType.TEXTAREA);
        descriptionField.setDefault(true);
        modBean.addField(descriptionField);

        BooleanField statusField = new BooleanField();
        statusField.setName("status");
        statusField.setModule(mod);
        statusField.setDisplayName("Status");
        statusField.setColumnName("STATUS");
        statusField.setDataType(FieldType.BOOLEAN);
        statusField.setDisplayType(FacilioField.FieldDisplayType.DECISION_BOX);
        statusField.setDefault(true);
        modBean.addField(statusField);

        LookupField clientField = new LookupField();
        clientField.setDefault(true);
        clientField.setName("client");
        clientField.setDisplayName("Client");
        clientField.setModule(mod);
        clientField.setDataType(FieldType.LOOKUP);
        clientField.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        clientField.setColumnName("CLIENT_ID");
        clientField.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.CLIENT));
        modBean.addField(clientField);

        NumberField controllerCriteriaId = new NumberField();
        controllerCriteriaId.setDefault(true);
        controllerCriteriaId.setName("controllerCriteriaId");
        controllerCriteriaId.setDisplayName("Controller Criteria ID");
        controllerCriteriaId.setModule(mod);
        controllerCriteriaId.setDataType(FieldType.NUMBER);
        controllerCriteriaId.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
        controllerCriteriaId.setColumnName("CONTROLLER_CRITERIA_ID");
        modBean.addField(controllerCriteriaId);

        NumberField siteCriteriaId = new NumberField();
        siteCriteriaId.setDefault(true);
        siteCriteriaId.setName("siteCriteriaId");
        siteCriteriaId.setDisplayName("Site Criteria ID");
        siteCriteriaId.setModule(mod);
        siteCriteriaId.setDataType(FieldType.NUMBER);
        siteCriteriaId.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
        siteCriteriaId.setColumnName("SITE_CRITERIA_ID");
        modBean.addField(siteCriteriaId);

        NumberField priorityField = new NumberField();
        priorityField.setName("priority");
        priorityField.setDisplayName("Priority");
        priorityField.setColumnName("PRIORITY");
        priorityField.setModule(mod);
        priorityField.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
        priorityField.setDataType(FieldType.NUMBER);
        modBean.addField(priorityField);

        StringSystemEnumField executionType = new StringSystemEnumField();
        executionType.setDefault(true);
        executionType.setName("executionType");
        executionType.setDisplayName("Execution Type");
        executionType.setModule(mod);
        executionType.setEnumName("FlaggedEventExecutionType");
        executionType.setDataType(FieldType.STRING_SYSTEM_ENUM);
        executionType.setDisplayType(FacilioField.FieldDisplayType.SELECTBOX);
        executionType.setColumnName("EXECUTION_TYPE");
        modBean.addField(executionType);

        BooleanField createWorkorderField = new BooleanField();
        createWorkorderField.setDefault(true);
        createWorkorderField.setName("createWorkorder");
        createWorkorderField.setDisplayName("Create Workorder");
        createWorkorderField.setModule(mod);
        createWorkorderField.setDataType(FieldType.BOOLEAN);
        createWorkorderField.setDisplayType(FacilioField.FieldDisplayType.DECISION_BOX);
        createWorkorderField.setColumnName("CREATE_WORKORDER");
        modBean.addField(createWorkorderField);

        NumberField workorderTemplateIdField = new NumberField();
        workorderTemplateIdField.setDefault(true);
        workorderTemplateIdField.setName("workorderTemplateId");
        workorderTemplateIdField.setDisplayName("Workorder Template ID");
        workorderTemplateIdField.setModule(mod);
        workorderTemplateIdField.setDataType(FieldType.NUMBER);
        workorderTemplateIdField.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
        workorderTemplateIdField.setColumnName("WORKORDER_TEMPLATE_ID");
        modBean.addField(workorderTemplateIdField);

        BooleanField sendEmailNotification = new BooleanField();
        sendEmailNotification.setDefault(true);
        sendEmailNotification.setName("sendEmailNotification");
        sendEmailNotification.setDisplayName("Send Email Notification");
        sendEmailNotification.setModule(mod);
        sendEmailNotification.setDataType(FieldType.BOOLEAN);
        sendEmailNotification.setDisplayType(FacilioField.FieldDisplayType.DECISION_BOX);
        sendEmailNotification.setColumnName("SEND_EMAIL_NOTIFICATION");
        modBean.addField(sendEmailNotification);

        NumberField emailNotificationRuleId = new NumberField();
        emailNotificationRuleId.setDefault(true);
        emailNotificationRuleId.setName("emailNotificationRuleId");
        emailNotificationRuleId.setDisplayName("Email Notification Rule ID");
        emailNotificationRuleId.setModule(mod);
        emailNotificationRuleId.setDataType(FieldType.NUMBER);
        emailNotificationRuleId.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
        emailNotificationRuleId.setColumnName("EMAIL_NOTIFICATION_RULE_ID");
        modBean.addField(emailNotificationRuleId);

        NumberField delayedEmailRuleOneId = new NumberField();
        delayedEmailRuleOneId.setDefault(true);
        delayedEmailRuleOneId.setName("delayedEmailRuleOneId");
        delayedEmailRuleOneId.setDisplayName("Delayed Email Notification Rule One ID");
        delayedEmailRuleOneId.setModule(mod);
        delayedEmailRuleOneId.setDataType(FieldType.NUMBER);
        delayedEmailRuleOneId.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
        delayedEmailRuleOneId.setColumnName("DELAYED_EMAIL_NOTIFICATION_RULE_ONE_ID");
        modBean.addField(delayedEmailRuleOneId);

        NumberField delayedEmailRuleTwoId = new NumberField();
        delayedEmailRuleTwoId.setDefault(true);
        delayedEmailRuleTwoId.setName("delayedEmailRuleTwoId");
        delayedEmailRuleTwoId.setDisplayName("Delayed Email Notification Rule Two ID");
        delayedEmailRuleTwoId.setModule(mod);
        delayedEmailRuleTwoId.setDataType(FieldType.NUMBER);
        delayedEmailRuleTwoId.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
        delayedEmailRuleTwoId.setColumnName("DELAYED_EMAIL_NOTIFICATION_RULE_TWO_ID");
        modBean.addField(delayedEmailRuleTwoId);

        NumberField followUpEmailDelayTimeOne = new NumberField();
        followUpEmailDelayTimeOne.setName("followUpEmailDelayTimeOne");
        followUpEmailDelayTimeOne.setModule(mod);
        followUpEmailDelayTimeOne.setDisplayName("Follow Up Email Delay Time One");
        followUpEmailDelayTimeOne.setColumnName("FOLLOW_UP_EMAIL_DELAY_TIME_ONE");
        followUpEmailDelayTimeOne.setDataType(FieldType.NUMBER);
        followUpEmailDelayTimeOne.setDisplayType(FacilioField.FieldDisplayType.DURATION);
        followUpEmailDelayTimeOne.setDefault(true);
        modBean.addField(followUpEmailDelayTimeOne);

        NumberField followUpEmailDelayTimeTwo = new NumberField();
        followUpEmailDelayTimeTwo.setName("followUpEmailDelayTimeTwo");
        followUpEmailDelayTimeTwo.setModule(mod);
        followUpEmailDelayTimeTwo.setDisplayName("Follow Up Email Delay Time Two");
        followUpEmailDelayTimeTwo.setColumnName("FOLLOW_UP_EMAIL_DELAY_TIME_TWO");
        followUpEmailDelayTimeTwo.setDataType(FieldType.NUMBER);
        followUpEmailDelayTimeTwo.setDisplayType(FacilioField.FieldDisplayType.DURATION);
        followUpEmailDelayTimeTwo.setDefault(true);
        modBean.addField(followUpEmailDelayTimeTwo);

        NumberField workflowId = new NumberField();
        workflowId.setDefault(true);
        workflowId.setName("workflowId");
        workflowId.setDisplayName("Workflow ID");
        workflowId.setModule(mod);
        workflowId.setDataType(FieldType.NUMBER);
        workflowId.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
        workflowId.setColumnName("WORKFLOW_ID");
        modBean.addField(workflowId);

        modBean.addField(FieldFactory.getSystemField("sysCreatedTime", mod));
        modBean.addField(FieldFactory.getSystemField("sysCreatedByPeople", mod));
        modBean.addField(FieldFactory.getSystemField("sysModifiedTime", mod));
        modBean.addField(FieldFactory.getSystemField("sysModifiedByPeople", mod));

        addSystemButtons();
    }

    private static void addSystemButtons() throws Exception{
        SystemButtonApi.addCreateButtonWithModuleDisplayName(MODULE_NAME);
        SystemButtonApi.addExportAsCSV(MODULE_NAME);
        SystemButtonApi.addExportAsExcel(MODULE_NAME);
        SystemButtonApi.addListEditButton(MODULE_NAME);
        SystemButtonApi.addListDeleteButton(MODULE_NAME);
        SystemButtonApi.addBulkDeleteButton(MODULE_NAME);
    }
}