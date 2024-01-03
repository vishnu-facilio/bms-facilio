package com.facilio.remotemonitoring.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;

public class AlarmTimeLogModule  extends SignUpData {

    public static final String MODULE_NAME = "alarmTimeLog";
    public static final String MODULE_DISPLAY_NAME = "Alarm Time Log";

    @Override
    public void addData() throws Exception {


        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = new FacilioModule();
        module.setName(MODULE_NAME);
        module.setDisplayName(MODULE_DISPLAY_NAME);
        module.setDescription("Alarm Time Log");
        module.setTableName("Alarm_Time_Log");
        module.setCustom(false);
        module.setType(FacilioModule.ModuleType.SUB_ENTITY);
        module.setTrashEnabled(false);
        modBean.addModule(module);
        FacilioModule mod = modBean.getModule(MODULE_NAME);


        StringSystemEnumField eventStatus = new StringSystemEnumField();
        eventStatus.setDefault(true);
        eventStatus.setName("eventStatus");
        eventStatus.setDisplayName("Event Status");
        eventStatus.setModule(mod);
        eventStatus.setEnumName("FlaggedEventStatus");
        eventStatus.setDataType(FieldType.STRING_SYSTEM_ENUM);
        eventStatus.setDisplayType(FacilioField.FieldDisplayType.SELECTBOX);
        eventStatus.setColumnName("EVENT_STATUS");
        modBean.addField(eventStatus);

        StringSystemEnumField assessmentStatus = new StringSystemEnumField();
        assessmentStatus.setDefault(true);
        assessmentStatus.setName("assessmentStatus");
        assessmentStatus.setDisplayName("Assessment Status");
        assessmentStatus.setModule(mod);
        assessmentStatus.setEnumName("FlaggedEventBureauActionStatus");
        assessmentStatus.setDataType(FieldType.STRING_SYSTEM_ENUM);
        assessmentStatus.setDisplayType(FacilioField.FieldDisplayType.SELECTBOX);
        assessmentStatus.setColumnName("ASSESSMENT_STATUS");
        modBean.addField(assessmentStatus);

        DateField startTime = new DateField();
        startTime.setName("startTime");
        startTime.setDisplayName("Start Time");
        startTime.setModule(mod);
        startTime.setColumnName("START_TIME");
        startTime.setDataType(FieldType.DATE_TIME);
        startTime.setDisplayType(FacilioField.FieldDisplayType.DATETIME);
        startTime.setDefault(true);
        startTime.setMainField(true);
        modBean.addField(startTime);

        DateField endTime = new DateField();
        endTime.setName("endTime");
        endTime.setDisplayName("End Time");
        endTime.setModule(mod);
        endTime.setColumnName("END_TIME");
        endTime.setDataType(FieldType.DATE_TIME);
        endTime.setDisplayType(FacilioField.FieldDisplayType.DATETIME);
        endTime.setDefault(true);
        endTime.setMainField(true);
        modBean.addField(endTime);

        LookupField parentTicket = new LookupField();
        parentTicket.setName("parentTicket");
        parentTicket.setDisplayName("Parent Ticket");
        parentTicket.setModule(mod);
        parentTicket.setColumnName("PARENT_TICKET");
        parentTicket.setDataType(FieldType.LOOKUP);
        parentTicket.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        parentTicket.setDefault(true);
        parentTicket.setMainField(true);
        parentTicket.setLookupModule(modBean.getModule(FlaggedEventModule.MODULE_NAME));
        modBean.addField(parentTicket);

        LookupField performedBy = new LookupField();
        performedBy.setName("performedBy");
        performedBy.setDisplayName("Performed By");
        performedBy.setModule(mod);
        performedBy.setColumnName("PERFORMED_BY");
        performedBy.setDataType(FieldType.LOOKUP);
        performedBy.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        performedBy.setDefault(true);
        performedBy.setMainField(true);
        performedBy.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.PEOPLE));
        modBean.addField(performedBy);

        LookupField performedTeam = new LookupField();
        performedTeam.setName("performedTeam");
        performedTeam.setDisplayName("Performed Team");
        performedTeam.setModule(mod);
        performedTeam.setColumnName("PERFORMED_TEAM");
        performedTeam.setDataType(FieldType.LOOKUP);
        performedTeam.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        performedTeam.setDefault(true);
        performedTeam.setMainField(true);
        performedTeam.setLookupModule(modBean.getModule(FacilioConstants.PeopleGroup.PEOPLE_GROUP));
        modBean.addField(performedTeam);

        NumberField duration = new NumberField();
        duration.setName("duration");
        duration.setDisplayName("Duration");
        duration.setModule(mod);
        duration.setColumnName("DURATION");
        duration.setDataType(FieldType.NUMBER);
        duration.setDisplayType(FacilioField.FieldDisplayType.DURATION);
        duration.setDefault(true);
        duration.setMainField(true);
        modBean.addField(duration);
    }
}