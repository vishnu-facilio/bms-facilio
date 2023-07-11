package com.facilio.remotemonitoring.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;

import java.util.Arrays;

public class FlaggedEventBureauEvaluationModule extends SignUpData {

    public static final String MODULE_NAME = "flaggedEventBureauEvaluation";
    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = new FacilioModule();
        module.setName(MODULE_NAME);
        module.setDisplayName("Flagged Event Bureau Evaluation");
        module.setDescription("Flagged Event Bureau Evaluation");
        module.setTableName("Flagged_Event_Bureau_Evaluation");
        module.setCustom(false);
        module.setType(FacilioModule.ModuleType.SUB_ENTITY);
        module.setTrashEnabled(false);
        modBean.addModule(module);

        FacilioModule mod = modBean.getModule(MODULE_NAME);
        LookupField flaggedEventRule = new LookupField();
        flaggedEventRule.setName("flaggedEventRule");
        flaggedEventRule.setModule(mod);
        flaggedEventRule.setDisplayName("Flagged Event Rule");
        flaggedEventRule.setColumnName("FLAGGED_EVENT_RULE");
        flaggedEventRule.setDataType(FieldType.LOOKUP);
        flaggedEventRule.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        flaggedEventRule.setLookupModule(modBean.getModule(FlaggedEventRuleModule.MODULE_NAME));
        flaggedEventRule.setDefault(true);
        modBean.addField(flaggedEventRule);

        LargeTextField descriptionField = new LargeTextField();
        descriptionField.setName("description");
        descriptionField.setDisplayName("Description");
        descriptionField.setModule(mod);
        descriptionField.setDataType(FieldType.LARGE_TEXT);
        descriptionField.setDisplayType(FacilioField.FieldDisplayType.TEXTAREA);
        descriptionField.setDefault(true);
        modBean.addField(descriptionField);

        LargeTextField troubleShootingTips = new LargeTextField();
        troubleShootingTips.setDisplayName("Trouble Shooting Tips");
        troubleShootingTips.setModule(mod);
        troubleShootingTips.setName("troubleShootingTips");
        troubleShootingTips.setDataType(FieldType.LARGE_TEXT);
        troubleShootingTips.setDisplayType(FacilioField.FieldDisplayType.RICH_TEXT);
        troubleShootingTips.setDefault(true);
        modBean.addField(troubleShootingTips);

        NumberField emailRuleId = new NumberField();
        emailRuleId.setName("emailRuleId");
        emailRuleId.setModule(mod);
        emailRuleId.setDisplayName("Email Rule Id");
        emailRuleId.setColumnName("EMAIL_RULE_ID");
        emailRuleId.setDataType(FieldType.NUMBER);
        emailRuleId.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
        emailRuleId.setDefault(true);
        modBean.addField(emailRuleId);

        NumberField takeCustodyPeriod = new NumberField();
        takeCustodyPeriod.setName("takeCustodyPeriod");
        takeCustodyPeriod.setModule(mod);
        takeCustodyPeriod.setDisplayName("Take Custody Period");
        takeCustodyPeriod.setColumnName("TAKE_CUSTODY_PERIOD");
        takeCustodyPeriod.setDataType(FieldType.NUMBER);
        takeCustodyPeriod.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
        takeCustodyPeriod.setDefault(true);
        modBean.addField(takeCustodyPeriod);


        NumberField takeActionPeriod = new NumberField();
        takeActionPeriod.setName("takeActionPeriod");
        takeActionPeriod.setModule(mod);
        takeActionPeriod.setDisplayName("Take Action Period");
        takeActionPeriod.setColumnName("TAKE_ACTION_PERIOD");
        takeActionPeriod.setDataType(FieldType.NUMBER);
        takeActionPeriod.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
        takeActionPeriod.setDefault(true);
        modBean.addField(takeActionPeriod);

        BooleanField addUnusedEvalTime = new BooleanField();
        addUnusedEvalTime.setDisplayName("Add Unused Eval Time");
        addUnusedEvalTime.setName("addUnusedEvalTime");
        addUnusedEvalTime.setModule(mod);
        addUnusedEvalTime.setColumnName("ADD_UNUSED_EVAL_TIME");
        addUnusedEvalTime.setDataType(FieldType.BOOLEAN);
        addUnusedEvalTime.setDisplayType(FacilioField.FieldDisplayType.RADIO);
        addUnusedEvalTime.setDefault(true);
        modBean.addField(addUnusedEvalTime);

        BooleanField allowFlaggedEventClose = new BooleanField();
        allowFlaggedEventClose.setDisplayName("Allow Flagged Event Close");
        allowFlaggedEventClose.setName("allowFlaggedEventClose");
        allowFlaggedEventClose.setModule(mod);
        allowFlaggedEventClose.setColumnName("ALLOW_FLAGGED_EVENT_CLOSE");
        allowFlaggedEventClose.setDataType(FieldType.BOOLEAN);
        allowFlaggedEventClose.setDisplayType(FacilioField.FieldDisplayType.RADIO);
        allowFlaggedEventClose.setDefault(true);
        modBean.addField(allowFlaggedEventClose);

        LookupField teamField = new LookupField();
        teamField.setDefault(true);
        teamField.setName("team");
        teamField.setDisplayName("Team");
        teamField.setModule(mod);
        teamField.setDataType(FieldType.LOOKUP);
        teamField.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        teamField.setColumnName("TEAM_ID");
        teamField.setLookupModule(modBean.getModule(FacilioConstants.PeopleGroup.PEOPLE_GROUP));
        modBean.addField(teamField);

        NumberField amberUrgencyTime = new NumberField();
        amberUrgencyTime.setName("amberUrgencyTime");
        amberUrgencyTime.setModule(mod);
        amberUrgencyTime.setDisplayName("Amber Urgency Time");
        amberUrgencyTime.setColumnName("AMBER_URGENCY_TIME");
        amberUrgencyTime.setDataType(FieldType.NUMBER);
        amberUrgencyTime.setDisplayType(FacilioField.FieldDisplayType.DURATION);
        amberUrgencyTime.setDefault(true);
        modBean.addField(amberUrgencyTime);

        BooleanField isFinalTeam = new BooleanField();
        isFinalTeam.setName("isFinalTeam");
        isFinalTeam.setModule(mod);
        isFinalTeam.setDisplayName("Is Final Team");
        isFinalTeam.setColumnName("IS_FINAL_TEAM");
        isFinalTeam.setDataType(FieldType.BOOLEAN);
        isFinalTeam.setDisplayType(FacilioField.FieldDisplayType.RADIO);
        isFinalTeam.setDefault(true);
        modBean.addField(isFinalTeam);

        NumberField redUrgencyTime = new NumberField();
        redUrgencyTime.setName("redUrgencyTime");
        redUrgencyTime.setModule(mod);
        redUrgencyTime.setDisplayName("Red Urgency Time");
        redUrgencyTime.setColumnName("RED_URGENCY_TIME");
        redUrgencyTime.setDataType(FieldType.NUMBER);
        redUrgencyTime.setDisplayType(FacilioField.FieldDisplayType.DURATION);
        redUrgencyTime.setDefault(true);
        modBean.addField(redUrgencyTime);


        NumberField order = new NumberField();
        order.setName("order");
        order.setModule(mod);
        order.setDisplayName("Order");
        order.setColumnName("EXECUTION_ORDER");
        order.setDataType(FieldType.NUMBER);
        order.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
        order.setDefault(true);
        modBean.addField(order);
    }
}