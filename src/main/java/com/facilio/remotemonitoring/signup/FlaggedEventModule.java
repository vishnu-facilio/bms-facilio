package com.facilio.remotemonitoring.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;

public class FlaggedEventModule extends SignUpData {

    public static final String MODULE_NAME = "flaggedEvent";
    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule rawAlarmModule = new FacilioModule();
        rawAlarmModule.setName(MODULE_NAME);
        rawAlarmModule.setDisplayName("Flagged Event");
        rawAlarmModule.setDescription("Flagged Event");
        rawAlarmModule.setTableName("Flagged_Event");
        rawAlarmModule.setCustom(false);
        rawAlarmModule.setType(FacilioModule.ModuleType.BASE_ENTITY);
        rawAlarmModule.setTrashEnabled(true);
        modBean.addModule(rawAlarmModule);

        FacilioModule mod = modBean.getModule(MODULE_NAME);
        StringField nameField = new StringField();
        nameField.setName("name");
        nameField.setModule(mod);
        nameField.setDisplayName("Name");
        nameField.setColumnName("NAME");
        nameField.setDataType(FieldType.BIG_STRING);
        nameField.setDisplayType(FacilioField.FieldDisplayType.TEXTAREA);
        nameField.setDefault(true);
        nameField.setMainField(true);
        modBean.addField(nameField);

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

        LookupField flaggedEventRuleField = new LookupField();
        flaggedEventRuleField.setDefault(true);
        flaggedEventRuleField.setName("flaggedEventRule");
        flaggedEventRuleField.setDisplayName("Flagged Event Rule");
        flaggedEventRuleField.setModule(mod);
        flaggedEventRuleField.setDataType(FieldType.LOOKUP);
        flaggedEventRuleField.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        flaggedEventRuleField.setColumnName("FLAGGED_EVENT_RULE");
        flaggedEventRuleField.setLookupModule(modBean.getModule(FlaggedEventRuleModule.MODULE_NAME));
        modBean.addField(flaggedEventRuleField);

        StringSystemEnumField status = new StringSystemEnumField();
        status.setDefault(true);
        status.setName("status");
        status.setDisplayName("Status");
        status.setModule(mod);
        status.setEnumName("FlaggedEventStatus");
        status.setDataType(FieldType.STRING_SYSTEM_ENUM);
        status.setDisplayType(FacilioField.FieldDisplayType.SELECTBOX);
        status.setColumnName("STATUS");
        modBean.addField(status);

        LookupField controllerField = new LookupField();
        controllerField.setDefault(true);
        controllerField.setName("controller");
        controllerField.setDisplayName("Controller");
        controllerField.setModule(mod);
        controllerField.setDataType(FieldType.LOOKUP);
        controllerField.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        controllerField.setColumnName("CONTROLLER");
        controllerField.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.CONTROLLER));
        modBean.addField(controllerField);

        LookupField workorder = new LookupField();
        workorder.setDefault(true);
        workorder.setName("workorder");
        workorder.setDisplayName("Workorder");
        workorder.setModule(mod);
        workorder.setDataType(FieldType.LOOKUP);
        workorder.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        workorder.setColumnName("WORKORDER");
        workorder.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER));
        modBean.addField(workorder);

        LookupField assignedPeople = new LookupField();
        assignedPeople.setDefault(true);
        assignedPeople.setName("assignedPeople");
        assignedPeople.setDisplayName("Assigned People");
        assignedPeople.setModule(mod);
        assignedPeople.setDataType(FieldType.LOOKUP);
        assignedPeople.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        assignedPeople.setColumnName("ASSIGNED_PEOPLE");
        assignedPeople.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.PEOPLE));
        modBean.addField(assignedPeople);

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


        LookupField flaggedEventLookupInWorkorder = new LookupField();
        flaggedEventLookupInWorkorder.setDefault(true);
        flaggedEventLookupInWorkorder.setName("flaggedEvent");
        flaggedEventLookupInWorkorder.setDisplayName("Flagged Event");
        flaggedEventLookupInWorkorder.setModule(modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER));
        flaggedEventLookupInWorkorder.setDataType(FieldType.LOOKUP);
        flaggedEventLookupInWorkorder.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        flaggedEventLookupInWorkorder.setColumnName("FLAGGED_EVENT");
        flaggedEventLookupInWorkorder.setLookupModule(modBean.getModule(FlaggedEventModule.MODULE_NAME));
        modBean.addField(flaggedEventLookupInWorkorder);

        modBean.addField(FieldFactory.getSystemField("sysCreatedTime", mod));
        modBean.addField(FieldFactory.getSystemField("sysCreatedByPeople", mod));
        modBean.addField(FieldFactory.getSystemField("sysModifiedTime", mod));
        modBean.addField(FieldFactory.getSystemField("sysModifiedByPeople", mod));

    }
}
