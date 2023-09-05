package com.facilio.bmsconsoleV3.signup.calendar;

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
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.tasker.FacilioTimer;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.context.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class AddCalendarModule extends SignUpData {

    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        long orgId = Objects.requireNonNull(AccountUtil.getCurrentOrg()).getId();
        FacilioModule calendar = constructCalendarModule(modBean,orgId);
        constructCalendarActivityModule(calendar,modBean);
        addNightlyJob();
        addCalendarLookUpToSiteModule(calendar);
        addCalendarLookUpToAssetModule(calendar);

    }
    private FacilioModule constructCalendarModule(ModuleBean moduleBean, long orgId) throws Exception{
        List<FacilioModule> modules = new ArrayList<>();
        FacilioModule calendar = new FacilioModule("calendar","Calendar","Calendar",
                FacilioModule.ModuleType.BASE_ENTITY,true);
        calendar.setOrgId(orgId);

        List<FacilioField> fields = new ArrayList<>();

         StringField name = SignupUtil.getStringField(calendar,"name","Name","NAME",
                 FacilioField.FieldDisplayType.TEXTBOX,true,false,true,true,orgId);
         fields.add(name);

         StringField description = SignupUtil.getStringField(calendar,"description","Description","DESCRIPTION",
                 FacilioField.FieldDisplayType.TEXTBOX,false,false,false,false,orgId);
         fields.add(description);

        SystemEnumField calendarType = SignupUtil.getSystemEnumField(calendar,"calendarType","Type","CALENDAR_TYPE","CalendarTypeEnum",
                FacilioField.FieldDisplayType.SELECTBOX,true,false,false,orgId);
        fields.add(calendarType);

        FacilioModule clientModule = moduleBean.getModule(FacilioConstants.ContextNames.CLIENT);
        if(clientModule == null){
            throw new IllegalArgumentException("Client Module is Null");
        }
        LookupField clientField = SignupUtil.getLookupField(calendar, clientModule, "client", "Client",
                "CLIENT_ID", null, FacilioField.FieldDisplayType.LOOKUP_SIMPLE,
                false, false, true, orgId);
        fields.add(clientField);

        LookupField createdByPeople = FieldFactory.getDefaultField("sysCreatedByPeople","Created By","SYS_CREATED_BY_PEOPLE",FieldType.LOOKUP);
        createdByPeople.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        fields.add(createdByPeople);

        LookupField modifiedByPeople = FieldFactory.getDefaultField("sysModifiedByPeople","Modified By","SYS_MODIFIED_BY_PEOPLE",FieldType.LOOKUP);
        modifiedByPeople.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        fields.add(modifiedByPeople);

        fields.add((FacilioField) FieldFactory.getDefaultField("sysCreatedTime", "Created Time", "SYS_CREATED_TIME", FieldType.DATE_TIME));
        fields.add((FacilioField) FieldFactory.getDefaultField("sysModifiedTime", "Modified Time", "SYS_MODIFIED_TIME", FieldType.DATE_TIME));


        calendar.setFields(fields);
        modules.add(calendar);
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();

        return calendar;
    }
    private void constructCalendarActivityModule(FacilioModule calendarModule, ModuleBean moduleBean) throws Exception{

        FacilioModule module = new FacilioModule(FacilioConstants.Calendar.CALENDAR_ACTIVITY_MODULE,
                "CalendarActivity",
                "Calendar_Activity",
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

       moduleBean.addSubModule(calendarModule.getModuleId(), module.getModuleId());
    }
    public void addNightlyJob() throws Exception {
        ScheduleInfo si = new ScheduleInfo();
        si.setFrequencyType(ScheduleInfo.FrequencyType.DAILY);
        si.setTimes(Collections.singletonList("00:01"));
        FacilioTimer.scheduleCalendarJob(AccountUtil.getCurrentOrg().getId(), "calendarNightlyJob", DateTimeUtil.getCurrenTime(), si, "facilio");
        //FacilioTimer.schedulePeriodicJob(AccountUtil.getCurrentOrg().getId(), "calendarNightlyJob", 60, 60, "facilio");
    }
    private void addCalendarLookUpToSiteModule(FacilioModule calendarModule) throws Exception {

        LookupField calendarField = (LookupField) FieldFactory.getField("calendar", "Calendar", "CALENDAR_ID", Constants.getModBean().getModule(FacilioConstants.ContextNames.SITE), FieldType.LOOKUP);
        calendarField.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        calendarField.setLookupModule(calendarModule);
        calendarField.setDefault(false);
        Constants.getModBean().addField(calendarField);
    }
    private void addCalendarLookUpToAssetModule(FacilioModule calendarModule) throws Exception {

        LookupField calendarField = (LookupField) FieldFactory.getField("calendar", "Calendar", "CALENDAR_ID", Constants.getModBean().getModule(FacilioConstants.ContextNames.ASSET), FieldType.LOOKUP);
        calendarField.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        calendarField.setLookupModule(calendarModule);
        calendarField.setDefault(false);
        Constants.getModBean().addField(calendarField);
    }
}
