package com.facilio.remotemonitoring.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;
import org.apache.xpath.operations.Bool;

public class FlaggedEventBureauActionModule extends SignUpData {

    public static final String MODULE_NAME = "flaggedEventBureauActions";
    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = new FacilioModule();
        module.setName(MODULE_NAME);
        module.setDisplayName("Flagged Event Bureau Actions");
        module.setDescription("Flagged Event Bureau Actions");
        module.setTableName("Flagged_Event_Bureau_Actions");
        module.setCustom(false);
        module.setExtendModule(modBean.getModule(FlaggedEventBureauEvaluationModule.MODULE_NAME));
        module.setType(FacilioModule.ModuleType.SUB_ENTITY);
        module.setTrashEnabled(false);
        module.setHideFromParents(true);
        modBean.addModule(module);

        FacilioModule mod = modBean.getModule(MODULE_NAME);

        LookupField flaggedEvent = new LookupField();
        flaggedEvent.setName("flaggedEvent");
        flaggedEvent.setModule(mod);
        flaggedEvent.setDisplayName("Flagged Event");
        flaggedEvent.setColumnName("FLAGGED_EVENT");
        flaggedEvent.setDataType(FieldType.LOOKUP);
        flaggedEvent.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        flaggedEvent.setLookupModule(modBean.getModule(FlaggedEventModule.MODULE_NAME));
        flaggedEvent.setDefault(true);
        flaggedEvent.setMainField(true);
        modBean.addField(flaggedEvent);

        DateField takeCustodyTimestamp = new DateField();
        takeCustodyTimestamp.setName("takeCustodyTimestamp");
        takeCustodyTimestamp.setModule(mod);
        takeCustodyTimestamp.setDisplayName("Take Custody Time");
        takeCustodyTimestamp.setColumnName("TAKE_CUSTODY_TIMESTAMP");
        takeCustodyTimestamp.setDataType(FieldType.DATE_TIME);
        takeCustodyTimestamp.setDisplayType(FacilioField.FieldDisplayType.DATETIME);
        takeCustodyTimestamp.setDefault(true);
        modBean.addField(takeCustodyTimestamp);

        DateField takeActionTimestamp = new DateField();
        takeActionTimestamp.setName("takeActionTimestamp");
        takeActionTimestamp.setModule(mod);
        takeActionTimestamp.setDisplayName("Take Action Time");
        takeActionTimestamp.setColumnName("TAKE_ACTION_TIMESTAMP");
        takeActionTimestamp.setDataType(FieldType.DATE_TIME);
        takeActionTimestamp.setDisplayType(FacilioField.FieldDisplayType.DATETIME);
        takeActionTimestamp.setDefault(true);
        modBean.addField(takeActionTimestamp);

        DateField inhibitTimestamp = new DateField();
        inhibitTimestamp.setName("inhibitTimeStamp");
        inhibitTimestamp.setModule(mod);
        inhibitTimestamp.setDisplayName("Inhibit Time");
        inhibitTimestamp.setColumnName("INHIBIT_TIMESTAMP");
        inhibitTimestamp.setDataType(FieldType.DATE_TIME);
        inhibitTimestamp.setDisplayType(FacilioField.FieldDisplayType.DATETIME);
        inhibitTimestamp.setDefault(true);
        modBean.addField(inhibitTimestamp);

        DateField evaluationOpenTimestamp = new DateField();
        evaluationOpenTimestamp.setName("evaluationOpenTimestamp");
        evaluationOpenTimestamp.setModule(mod);
        evaluationOpenTimestamp.setDisplayName("Evaluation Open Time");
        evaluationOpenTimestamp.setColumnName("EVALUATION_OPEN_TIME");
        evaluationOpenTimestamp.setDataType(FieldType.DATE_TIME);
        evaluationOpenTimestamp.setDisplayType(FacilioField.FieldDisplayType.DATETIME);
        evaluationOpenTimestamp.setDefault(true);
        modBean.addField(evaluationOpenTimestamp);

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


        StringSystemEnumField eventStatus = new StringSystemEnumField();
        eventStatus.setModule(mod);
        eventStatus.setDefault(true);
        eventStatus.setName("eventStatus");
        eventStatus.setColumnName("EVENT_STATUS");
        eventStatus.setDataType(FieldType.STRING_SYSTEM_ENUM);
        eventStatus.setDisplayType(FacilioField.FieldDisplayType.SELECTBOX);
        eventStatus.setEnumName("FlaggedEventBureauActionStatus");
        eventStatus.setDisplayName("Event Status");
        modBean.addField(eventStatus);

        BooleanField isSLABreached = new BooleanField();
        isSLABreached.setName("isSLABreached");
        isSLABreached.setModule(mod);
        isSLABreached.setDisplayName("Is SLA Breached");
        isSLABreached.setColumnName("IS_SLA_BREACHED");
        isSLABreached.setDataType(FieldType.BOOLEAN);
        isSLABreached.setDisplayType(FacilioField.FieldDisplayType.DECISION_BOX);
        isSLABreached.setDefault(true);
        modBean.addField(isSLABreached);


        //Flagged event action in
        LookupField currentBureauActionDetail = new LookupField();
        currentBureauActionDetail.setDefault(true);
        currentBureauActionDetail.setName("currentBureauActionDetail");
        currentBureauActionDetail.setDisplayName("Working Team");
        currentBureauActionDetail.setModule(modBean.getModule(FlaggedEventModule.MODULE_NAME));
        currentBureauActionDetail.setDataType(FieldType.LOOKUP);
        currentBureauActionDetail.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        currentBureauActionDetail.setColumnName("CURRENT_BUREAU_ACTION");
        currentBureauActionDetail.setLookupModule(mod);
        modBean.addField(currentBureauActionDetail);
    }
}