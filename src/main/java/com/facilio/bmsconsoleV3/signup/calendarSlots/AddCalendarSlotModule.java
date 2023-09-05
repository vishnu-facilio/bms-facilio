package com.facilio.bmsconsoleV3.signup.calendarSlots;

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
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.NumberField;
import com.facilio.modules.fields.StringField;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddCalendarSlotModule extends SignUpData {

    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        long orgId = Objects.requireNonNull(AccountUtil.getCurrentOrg()).getId();
        constructCalendarSlotsModule(modBean,orgId);
    }
    private static  FacilioModule constructCalendarSlotsModule(ModuleBean moduleBean, long orgId) throws Exception{
        List<FacilioModule> modules = new ArrayList<>();
        FacilioModule calendarSlotsModule = new FacilioModule("calendarSlots","Calendar","Calendar_Slots",
                FacilioModule.ModuleType.BASE_ENTITY,true);
        calendarSlotsModule.setOrgId(orgId);

        List<FacilioField> fields = new ArrayList<>();
        FacilioModule calendarModule = moduleBean.getModule(FacilioConstants.Calendar.CALENDAR_MODULE_NAME);
        if(calendarModule == null){
            throw new IllegalArgumentException("Calendar Module Not Found for OrgId - #"+orgId);
        }
        FacilioModule eventModule = moduleBean.getModule(FacilioConstants.Calendar.EVENT_MODULE_NAME);
        if(eventModule == null){
            throw new IllegalArgumentException("Event Module Not Found for OrgId - #"+orgId);
        }
        LookupField calendarField = SignupUtil.getLookupField(calendarSlotsModule, calendarModule, "calendar", "Calendar",
                "CALENDAR_ID", null, FacilioField.FieldDisplayType.LOOKUP_SIMPLE,
                true, false, false, orgId, false);
        fields.add(calendarField);

        LookupField eventField = SignupUtil.getLookupField(calendarSlotsModule,eventModule,"event","Event",
                "EVENT_ID",null,FacilioField.FieldDisplayType.LOOKUP_SIMPLE,
                true,false,false,orgId,false);
        fields.add(eventField);

        NumberField calendarYear = SignupUtil.getNumberField(calendarSlotsModule,"calendarYear","Year",
                "CALENDAR_YEAR", FacilioField.FieldDisplayType.TEXTBOX,false,false,false,orgId);
        fields.add(calendarYear);

        NumberField calendarMonth = SignupUtil.getNumberField(calendarSlotsModule,"calendarMonth","Month",
                "CALENDAR_MONTH", FacilioField.FieldDisplayType.TEXTBOX,false,false,false,orgId);
        fields.add(calendarMonth);

        NumberField calendarDate = SignupUtil.getNumberField(calendarSlotsModule,"calendarDate","Date",
                "CALENDAR_DATE", FacilioField.FieldDisplayType.TEXTBOX,false,false,false,orgId);
        fields.add(calendarDate);

        NumberField calendarWeekNumber = SignupUtil.getNumberField(calendarSlotsModule,"calendarWeekNumber","Week",
                "CALENDAR_WEEK_NUMBER", FacilioField.FieldDisplayType.TEXTBOX,false,false,false,orgId);
        fields.add(calendarWeekNumber);

        NumberField calendarWeekDay = SignupUtil.getNumberField(calendarSlotsModule,"calendarWeekDay","Day",
                "CALENDAR_WEEK_DAY", FacilioField.FieldDisplayType.TEXTBOX,false,false,false,orgId);
        fields.add(calendarWeekDay);

        NumberField slotStartTime = SignupUtil.getNumberField(calendarSlotsModule,"slotStartTime","Start Time",
                "SLOT_START_TIME", FacilioField.FieldDisplayType.TEXTBOX,false,false,false,orgId);
        fields.add(slotStartTime);

        NumberField slotEndTime = SignupUtil.getNumberField(calendarSlotsModule,"slotEndTime","End Time",
                "SLOT_END_TIME", FacilioField.FieldDisplayType.TEXTBOX,false,false,false,orgId);
        fields.add(slotEndTime);

        LookupField createdByPeople = FieldFactory.getDefaultField("sysCreatedByPeople","Created By","SYS_CREATED_BY_PEOPLE", FieldType.LOOKUP);
        createdByPeople.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        fields.add(createdByPeople);

        LookupField modifiedByPeople = FieldFactory.getDefaultField("sysModifiedByPeople","Modified By","SYS_MODIFIED_BY_PEOPLE",FieldType.LOOKUP);
        modifiedByPeople.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        fields.add(modifiedByPeople);

        fields.add((FacilioField) FieldFactory.getDefaultField("sysCreatedTime", "Created Time", "SYS_CREATED_TIME", FieldType.DATE_TIME));
        fields.add((FacilioField) FieldFactory.getDefaultField("sysModifiedTime", "Modified Time", "SYS_MODIFIED_TIME", FieldType.DATE_TIME));


        calendarSlotsModule.setFields(fields);
        modules.add(calendarSlotsModule);
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST,modules );
        addModuleChain.execute();
        return calendarSlotsModule;
    }
}
