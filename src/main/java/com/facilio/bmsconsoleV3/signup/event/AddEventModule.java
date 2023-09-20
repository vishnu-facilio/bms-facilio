package com.facilio.bmsconsoleV3.signup.event;

import com.amazonaws.services.dynamodbv2.xspec.S;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;
import lombok.extern.log4j.Log4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
@Log4j
public class AddEventModule extends SignUpData {
    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        long orgId = Objects.requireNonNull(AccountUtil.getCurrentOrg()).getId();
        FacilioModule eventModule = constructEventModule(modBean,orgId);
        FacilioModule eventTimeSlot = constructEventTimeSlotModule(modBean,orgId,eventModule);
        constructCalendarTimeSlotModule(modBean,orgId,eventModule);
        constructCalendarEventMappingModule(modBean,orgId,eventModule);
        constructEventActivityModule(eventModule,modBean);
    }
    private static FacilioModule constructEventModule(ModuleBean moduleBean, long orgId) throws Exception{
        List<FacilioModule> modules = new ArrayList<>();
        FacilioModule eventModule = new FacilioModule("calendarEvent","Event","Events",
                FacilioModule.ModuleType.BASE_ENTITY,true);
        eventModule.setOrgId(orgId);

        List<FacilioField> fields = new ArrayList<>();

        StringField name = SignupUtil.getStringField(eventModule,"name","Name","NAME",
                FacilioField.FieldDisplayType.TEXTBOX,true,false,true,true,orgId);
        fields.add(name);

        StringField description = SignupUtil.getStringField(eventModule,"description","Description","DESCRIPTION",
                FacilioField.FieldDisplayType.TEXTBOX,false,false,true,false,orgId);
        fields.add(description);

        DateField startTimeField = SignupUtil.getDateField(eventModule, "validityStartTime", "Start Time", "VALID_FROM",
                FacilioField.FieldDisplayType.TEXTBOX, false, false, true, orgId);
        fields.add(startTimeField);

        DateField endTimeField = SignupUtil.getDateField(eventModule, "validityEndTime", "End Time", "VALID_TILL",
                FacilioField.FieldDisplayType.TEXTBOX, false, false, true, orgId); // check for display name
        fields.add(endTimeField);

        SystemEnumField eventType = SignupUtil.getSystemEnumField(eventModule,"eventType","Event Type","EVENT_TYPE","EventTypeEnum",
                FacilioField.FieldDisplayType.TEXTBOX,true,false,true,orgId);
        fields.add(eventType);

        NumberField eventSequence = SignupUtil.getNumberField(eventModule,"eventSequence","Event Sequence","EVENT_SEQUENCE",
                FacilioField.FieldDisplayType.TEXTBOX,true,false,true,orgId);
        fields.add(eventSequence);

        NumberField scheduledYear = SignupUtil.getNumberField(eventModule,"scheduledYear","Year","SCHEDULED_YEAR",
        FacilioField.FieldDisplayType.SELECTBOX,false,false,true,orgId);
        fields.add(scheduledYear);

        SystemEnumField scheduledMonth = SignupUtil.getSystemEnumField(eventModule,"scheduledMonth","Month","SCHEDULED_MONTH",
                "MonthValueEnum", FacilioField.FieldDisplayType.TEXTBOX,false,false,true,orgId);
        fields.add(scheduledMonth);

        NumberField scheduledDate = SignupUtil.getNumberField(eventModule,"scheduledDate","Date","SCHEDULED_DATE",
                FacilioField.FieldDisplayType.SELECTBOX,false,false,true,orgId);
        fields.add(scheduledDate);

        NumberField scheduledWeekNumber = SignupUtil.getNumberField(eventModule,"scheduledWeekNumber","Week","SCHEDULED_WEEK_NUMBER",
                FacilioField.FieldDisplayType.SELECTBOX,false,false,true,orgId);
        fields.add(scheduledWeekNumber);

        SystemEnumField scheduledDay = SignupUtil.getSystemEnumField(eventModule,"scheduledDay","Day","SCHEDULED_DAY",
                "WeekDayEnum", FacilioField.FieldDisplayType.TEXTBOX,false,false,true,orgId);
        fields.add(scheduledDay);

        SystemEnumField frequencyField = SignupUtil.getSystemEnumField(eventModule, "eventFrequency", "Frequency", "EVENT_FREQUENCY",
                "EventFrequencyEnum", FacilioField.FieldDisplayType.TEXTBOX, false, false, true, orgId);
        fields.add(frequencyField);

        BooleanField isSpecific = SignupUtil.getBooleanField(eventModule,"isSpecific","Specific","IS_SPECIFIC",
                FacilioField.FieldDisplayType.DECISION_BOX,null,false,false,true,orgId);
        isSpecific.setTrueVal("Specific");
        isSpecific.setFalseVal("Relative");
        fields.add(isSpecific);

        NumberField seasonStartMonth = SignupUtil.getNumberField(eventModule,"seasonStartMonth","Season Start Month","SEASON_START_MONTH",
                FacilioField.FieldDisplayType.SELECTBOX,false,false,true,orgId);
        fields.add(seasonStartMonth);

        NumberField seasonStartDate = SignupUtil.getNumberField(eventModule,"seasonStartDate","Season Start Date","SEASON_START_DATE",
                FacilioField.FieldDisplayType.SELECTBOX,false,false,true,orgId);
        fields.add(seasonStartDate);

        NumberField seasonEndMonth = SignupUtil.getNumberField(eventModule,"seasonEndMonth","Season End Month","SEASON_END_MONTH",
                FacilioField.FieldDisplayType.SELECTBOX,false,false,true,orgId);
        fields.add(seasonEndMonth);

        NumberField seasonEndDate = SignupUtil.getNumberField(eventModule,"seasonEndDate","Season End Date","SEASON_END_DATE",
                FacilioField.FieldDisplayType.SELECTBOX,false,false,true,orgId);
        fields.add(seasonEndDate);

        LookupField createdByPeople = FieldFactory.getDefaultField("sysCreatedByPeople","Created By","SYS_CREATED_BY_PEOPLE",FieldType.LOOKUP);
        createdByPeople.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        fields.add(createdByPeople);

        LookupField modifiedByPeople = FieldFactory.getDefaultField("sysModifiedByPeople","Modified By","SYS_MODIFIED_BY_PEOPLE",FieldType.LOOKUP);
        modifiedByPeople.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        fields.add(modifiedByPeople);

        fields.add((FacilioField) FieldFactory.getDefaultField("sysCreatedTime", "Created Time", "SYS_CREATED_TIME", FieldType.DATE_TIME));
        fields.add((FacilioField) FieldFactory.getDefaultField("sysModifiedTime", "Modified Time", "SYS_MODIFIED_TIME", FieldType.DATE_TIME));

        eventModule.setFields(fields);
        modules.add(eventModule);
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();
       return eventModule;
    }
    private static FacilioModule constructEventTimeSlotModule(ModuleBean moduleBean,long orgId,FacilioModule eventModule) throws Exception{
        List<FacilioModule> modules = new ArrayList<>();
        FacilioModule timeSlotModule = new FacilioModule("eventTimeSlot","Time Slot","Event_TimeSlot",
                FacilioModule.ModuleType.BASE_ENTITY,true);
        timeSlotModule.setOrgId(orgId);

        List<FacilioField> fields = getFieldsForTimeSlotModule(timeSlotModule,orgId,eventModule);

        LookupField createdByPeople = FieldFactory.getDefaultField("sysCreatedByPeople","Created By","SYS_CREATED_BY_PEOPLE",FieldType.LOOKUP);
        createdByPeople.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        fields.add(createdByPeople);

        LookupField modifiedByPeople = FieldFactory.getDefaultField("sysModifiedByPeople","Modified By","SYS_MODIFIED_BY_PEOPLE",FieldType.LOOKUP);
        modifiedByPeople.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        fields.add(modifiedByPeople);

        fields.add((FacilioField) FieldFactory.getDefaultField("sysCreatedTime", "Created Time", "SYS_CREATED_TIME", FieldType.DATE_TIME));
        fields.add((FacilioField) FieldFactory.getDefaultField("sysModifiedTime", "Modified Time", "SYS_MODIFIED_TIME", FieldType.DATE_TIME));

        timeSlotModule.setFields(fields);
        modules.add(timeSlotModule);
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();
        return timeSlotModule;
    }
    private static FacilioModule constructCalendarTimeSlotModule(ModuleBean moduleBean,long orgId,FacilioModule eventModule) throws Exception{
        List<FacilioModule> modules = new ArrayList<>();
        FacilioModule timeSlotModule = new FacilioModule("calendarTimeSlot","Time Slot","Event_TimeSlot",
                FacilioModule.ModuleType.BASE_ENTITY,true);
        timeSlotModule.setOrgId(orgId);

        List<FacilioField> fields = getFieldsForTimeSlotModule(timeSlotModule,orgId,eventModule);
        FacilioModule calendar = moduleBean.getModule(FacilioConstants.Calendar.CALENDAR_MODULE_NAME);
        if(calendar == null){
            LOGGER.error("calendar Module Not Found for Org - #"+orgId);
            throw new IllegalArgumentException("Calendar Module Not Found for OrgId - #"+orgId);
        }

        LookupField calendarField = SignupUtil.getLookupField(timeSlotModule, calendar, "calendar", "Calendar",
                "CALENDAR_ID", null, FacilioField.FieldDisplayType.LOOKUP_POPUP, false,
                true, true, orgId);
        fields.add(calendarField);

        LookupField createdByPeople = FieldFactory.getDefaultField("sysCreatedByPeople","Created By","SYS_CREATED_BY_PEOPLE",FieldType.LOOKUP);
        createdByPeople.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        fields.add(createdByPeople);

        LookupField modifiedByPeople = FieldFactory.getDefaultField("sysModifiedByPeople","Modified By","SYS_MODIFIED_BY_PEOPLE",FieldType.LOOKUP);
        modifiedByPeople.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        fields.add(modifiedByPeople);

        fields.add((FacilioField) FieldFactory.getDefaultField("sysCreatedTime", "Created Time", "SYS_CREATED_TIME", FieldType.DATE_TIME));
        fields.add((FacilioField) FieldFactory.getDefaultField("sysModifiedTime", "Modified Time", "SYS_MODIFIED_TIME", FieldType.DATE_TIME));

        timeSlotModule.setFields(fields);
        modules.add(timeSlotModule);
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();
        return timeSlotModule;
    }

    public static List<FacilioField> getFieldsForTimeSlotModule(FacilioModule timeSlotModule, long orgId,FacilioModule eventModule) throws Exception{
        List<FacilioField> fields = new ArrayList<>();

        LookupField eventField = SignupUtil.getLookupField(timeSlotModule,eventModule,"event","Event",
                "EVENT_ID",null, FacilioField.FieldDisplayType.LOOKUP_POPUP,true,
                true,true,orgId);
        fields.add(eventField);

        NumberField startMin = SignupUtil.getNumberField(timeSlotModule, "startMin", "Start Time", "START_TIME_MIN",
                FacilioField.FieldDisplayType.TEXTBOX, false, false, true, orgId);
        fields.add(startMin);

        NumberField endMin = SignupUtil.getNumberField(timeSlotModule, "endMin", "End Time", "END_TIME_MIN",
                FacilioField.FieldDisplayType.TEXTBOX, false, false, true, orgId);
        fields.add(endMin);
        return fields;
    }
    public static FacilioModule constructCalendarEventMappingModule(ModuleBean moduleBean,long orgId, FacilioModule eventModule) throws Exception{
        List<FacilioModule> modules = new ArrayList<>();
        FacilioModule calendarEventMappingModule = new FacilioModule("calendarEventMapping","Calendar Event Mapping","Calender_Event_Mapping",
                FacilioModule.ModuleType.BASE_ENTITY,true);
        calendarEventMappingModule.setOrgId(orgId);

        List<FacilioField> fields = new ArrayList<>();

        FacilioModule calendar = moduleBean.getModule(FacilioConstants.Calendar.CALENDAR_MODULE_NAME);
        if(calendar == null){
            LOGGER.error("calendar Module Not Found for Org - #"+orgId);
            throw new IllegalArgumentException("Calendar Module Not Found for OrgId - #"+orgId);
        }
        LookupField calendarField = SignupUtil.getLookupField(calendarEventMappingModule, calendar, "calendar", "Calendar",
                "CALENDAR_ID", null, FacilioField.FieldDisplayType.LOOKUP_SIMPLE,
                false, false, true, orgId);
        fields.add(calendarField);

        LookupField eventField = SignupUtil.getLookupField(calendarEventMappingModule, eventModule, "event", "Event",
                "EVENT_ID", null, FacilioField.FieldDisplayType.LOOKUP_SIMPLE,
                false, false, true, orgId);
        fields.add(eventField);

        BooleanField isEventEdited = SignupUtil.getBooleanField(calendarEventMappingModule,"isEventEdited","Event Edited",
                "EVENT_EDITED", FacilioField.FieldDisplayType.DECISION_BOX,null,false,false,true,orgId);
        fields.add(isEventEdited);

        LookupField createdByPeople = FieldFactory.getDefaultField("sysCreatedByPeople","Created By","SYS_CREATED_BY_PEOPLE",FieldType.LOOKUP);
        createdByPeople.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        fields.add(createdByPeople);

        LookupField modifiedByPeople = FieldFactory.getDefaultField("sysModifiedByPeople","Modified By","SYS_MODIFIED_BY_PEOPLE",FieldType.LOOKUP);
        modifiedByPeople.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        fields.add(modifiedByPeople);

        fields.add((FacilioField) FieldFactory.getDefaultField("sysCreatedTime", "Created Time", "SYS_CREATED_TIME", FieldType.DATE_TIME));
        fields.add((FacilioField) FieldFactory.getDefaultField("sysModifiedTime", "Modified Time", "SYS_MODIFIED_TIME", FieldType.DATE_TIME));

        calendarEventMappingModule.setFields(fields);
        modules.add(calendarEventMappingModule);
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();
        return calendarEventMappingModule;
    }
    public static void constructEventActivityModule(FacilioModule eventModule, ModuleBean moduleBean) throws Exception{
        FacilioModule module = new FacilioModule(FacilioConstants.Calendar.EVENT_ACTIVITY_MODULE,
                "Event Activity",
                "Event_Activity",
                FacilioModule.ModuleType.ACTIVITY
        );

        List<FacilioField> fields = new ArrayList<>();

        NumberField parentId = (NumberField) FieldFactory.getDefaultField("parentId", "Parent", "PARENT_ID", FieldType.NUMBER);
        fields.add(parentId);

        FacilioField timefield = FieldFactory.getDefaultField("ttime", "Timestamp", "TTIME", FieldType.DATE_TIME);

        fields.add(timefield);

        NumberField type = (NumberField) FieldFactory.getDefaultField("type", "Type", "ACTIVITY_TYPE", FieldType.NUMBER);
        fields.add(type);

        LookupField doneBy = (LookupField) FieldFactory.getDefaultField("doneBy", "Done By", "DONE_BY_ID", FieldType.LOOKUP, FacilioField.FieldDisplayType.LOOKUP_POPUP);
        doneBy.setSpecialType("users");
        fields.add(doneBy);

        FacilioField info = FieldFactory.getDefaultField("infoJsonStr", "Info", "INFO", FieldType.STRING, FacilioField.FieldDisplayType.TEXTAREA);

        fields.add(info);

        LookupField createdByPeople = FieldFactory.getDefaultField("sysCreatedByPeople","Created By","SYS_CREATED_BY_PEOPLE",FieldType.LOOKUP);
        createdByPeople.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        fields.add(createdByPeople);

        LookupField modifiedByPeople = FieldFactory.getDefaultField("sysModifiedByPeople","Modified By","SYS_MODIFIED_BY_PEOPLE",FieldType.LOOKUP);
        modifiedByPeople.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        fields.add(modifiedByPeople);

        fields.add((FacilioField) FieldFactory.getDefaultField("sysCreatedTime", "Created Time", "SYS_CREATED_TIME", FieldType.DATE_TIME));
        fields.add((FacilioField) FieldFactory.getDefaultField("sysModifiedTime", "Modified Time", "SYS_MODIFIED_TIME", FieldType.DATE_TIME));


        module.setFields(fields);

        FacilioChain addModuleChain1 = TransactionChainFactory.addSystemModuleChain();
        addModuleChain1.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(module));
        addModuleChain1.execute();

        moduleBean.addSubModule(eventModule.getModuleId(), module.getModuleId());
    }
}
