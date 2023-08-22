package com.facilio.fsm.signup;


import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.ModuleLocalIdUtil;
import com.facilio.bmsconsole.util.RelatedListWidgetUtil;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceAppointmentTicketStatusContext;
import com.facilio.fsm.context.TimeOffTypeContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.*;
import com.facilio.v3.context.Constants;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class TimeOffModule extends BaseModuleConfig {
    public static List<String> timeOffSupportedApps = Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP, FacilioConstants.ApplicationLinkNames.FSM_APP);

    public TimeOffModule(){setModuleName(FacilioConstants.TimeOff.TIME_OFF);}

    @Override
    public void addData() throws Exception {
        addTimeOffTypeModule();
        addTimeOffModule();
        addActivityModuleForTimeOff();
        SignupUtil.addNotesAndAttachmentModule(Constants.getModBean().getModule(FacilioConstants.TimeOff.TIME_OFF));
    }

    public void addTimeOffTypeModule() throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        List<FacilioModule> modules = new ArrayList<>();
        FacilioModule timeOffTypeModule = new FacilioModule(FacilioConstants.TimeOff.TIME_OFF_TYPE,"Time Off Type","TIME_OFF_TYPE", FacilioModule.ModuleType.PICK_LIST);
        List<FacilioField> timeOffTypeFields = new ArrayList<>();

        StringField name = new StringField(timeOffTypeModule,"name","Name", FacilioField.FieldDisplayType.TEXTBOX,"NAME",FieldType.STRING,true,false,true,true);
        timeOffTypeFields.add(name);
        StringField displayName = new StringField(timeOffTypeModule,"displayName","Display Name", FacilioField.FieldDisplayType.TEXTBOX,"DISPLAY_NAME",FieldType.STRING,true,false,true,false);
        timeOffTypeFields.add(displayName);
        StringField description = new StringField(timeOffTypeModule,"description","Description", FacilioField.FieldDisplayType.TEXTAREA,"DESCRIPTION",FieldType.STRING,false,false,true,false);
        timeOffTypeFields.add(description);
        StringField color = new StringField(timeOffTypeModule,"color","Color", FacilioField.FieldDisplayType.TEXTBOX,"COLOR",FieldType.STRING,true,false,true,false);
        timeOffTypeFields.add(color);
        StringField textColor = new StringField(timeOffTypeModule,"textColor","Text Color", FacilioField.FieldDisplayType.TEXTBOX,"TEXT_COLOR",FieldType.STRING,true,false,true,false);
        timeOffTypeFields.add(textColor);

        timeOffTypeModule.setFields(timeOffTypeFields);
        modules.add(timeOffTypeModule);


        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();

        InsertRecordBuilder<TimeOffTypeContext> insertRecordBuilder = new InsertRecordBuilder<TimeOffTypeContext>()
                .moduleName(FacilioConstants.TimeOff.TIME_OFF_TYPE)
                .fields(moduleBean.getAllFields(FacilioConstants.TimeOff.TIME_OFF_TYPE));

        List<TimeOffTypeContext> defaultTypes = new ArrayList<>();

        TimeOffTypeContext standBy = new TimeOffTypeContext();
        standBy.setName("standBy");
        standBy.setDisplayName("Stand By");
        standBy.setColor("#FDE49D");
        standBy.setTextColor("#000000");
        defaultTypes.add(standBy);

        TimeOffTypeContext holiday = new TimeOffTypeContext();
        holiday.setName("holiday");
        holiday.setDisplayName("Holiday");
        holiday.setColor("#F4C8E7");
        holiday.setTextColor("#000000");
        defaultTypes.add(holiday);

        TimeOffTypeContext sick = new TimeOffTypeContext();
        sick.setName("sick");
        sick.setDisplayName("Sick");
        sick.setColor("#E5F4FF");
        sick.setTextColor("#000000");
        defaultTypes.add(sick);

        TimeOffTypeContext vacation = new TimeOffTypeContext();
        vacation.setName("vacation");
        vacation.setDisplayName("Vacation");
        vacation.setColor("#D7CCF0");
        vacation.setTextColor("#000000");
        defaultTypes.add(vacation);

        TimeOffTypeContext truckBreakdown = new TimeOffTypeContext();
        truckBreakdown.setName("truckBreakdown");
        truckBreakdown.setDisplayName("Truck Breakdown");
        truckBreakdown.setColor("#F6D1C8");
        truckBreakdown.setTextColor("#000000");
        defaultTypes.add(truckBreakdown);

        TimeOffTypeContext training = new TimeOffTypeContext();
        training.setName("training");
        training.setDisplayName("Training");
        training.setColor("#FCE0C3");
        training.setTextColor("#000000");
        defaultTypes.add(training);

        insertRecordBuilder.addRecords(defaultTypes);
        insertRecordBuilder.save();
    }

    public void addTimeOffModule() throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        List<FacilioModule> modules = new ArrayList<>();
        FacilioModule timeOffModule = new FacilioModule(FacilioConstants.TimeOff.TIME_OFF,"Time Off","Time_Off", FacilioModule.ModuleType.BASE_ENTITY,true);

        List<FacilioField> timeOffFields = new ArrayList<>();

        NumberField localId = new NumberField(timeOffModule,"localId","Id", FacilioField.FieldDisplayType.NUMBER,"LOCAL_ID",FieldType.NUMBER,false,false,true,false);
        timeOffFields.add(localId);
        ModuleLocalIdUtil.insertModuleLocalId(FacilioConstants.TimeOff.TIME_OFF);

        timeOffFields.add(new StringField(timeOffModule,"code","Code",FacilioField.FieldDisplayType.TEXTBOX,"CODE", FieldType.STRING,true,false,true,false));

        DateField startTime = new DateField(timeOffModule,"startTime","Start Time", FacilioField.FieldDisplayType.DATETIME,"START_TIME",FieldType.DATE_TIME,true,false,true,false);
        timeOffFields.add(startTime);

        DateField endTime = new DateField(timeOffModule,"endTime","End Time", FacilioField.FieldDisplayType.DATETIME,"END_TIME",FieldType.DATE_TIME,true,false,true,false);
        timeOffFields.add(endTime);

        LookupField peopleField = new LookupField(timeOffModule,"people","Field Agent Name", FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"PEOPLE_ID",FieldType.LOOKUP,true,false,true,true,"Related Field Agents", Constants.getModBean().getModule(FacilioConstants.ContextNames.PEOPLE));
        timeOffFields.add(peopleField);

        LookupField typeField = new LookupField(timeOffModule,"type","Type", FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"TYPE",FieldType.LOOKUP,true,false,true,false,"Type",moduleBean.getModule(FacilioConstants.TimeOff.TIME_OFF_TYPE));
        timeOffFields.add(typeField);

        LookupField moduleStateField = FieldFactory.getDefaultField("moduleState", "Status", "MODULE_STATE", FieldType.LOOKUP);
        moduleStateField.setDefault(true);
        moduleStateField.setLookupModule(moduleBean.getModule("ticketstatus"));
        timeOffFields.add(moduleStateField);

        NumberField stateFlowIdField = FieldFactory.getDefaultField("stateFlowId", "State Flow Id", "STATE_FLOW_ID", FieldType.NUMBER);
        stateFlowIdField.setDefault(true);
        timeOffFields.add(stateFlowIdField);

        LookupField approvalStateField = FieldFactory.getDefaultField("approvalStatus", "Approval Status", "APPROVAL_STATE", FieldType.LOOKUP);
        approvalStateField.setDefault(true);
        approvalStateField.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        approvalStateField.setLookupModule(moduleBean.getModule("ticketstatus"));
        timeOffFields.add(approvalStateField);

        NumberField approvalFlowIdField = FieldFactory.getDefaultField("approvalFlowId", "Approval Flow Id", "APPROVAL_FLOW_ID", FieldType.NUMBER);
        approvalFlowIdField.setDefault(true);
        timeOffFields.add(approvalFlowIdField);

        StringField comments=FieldFactory.getDefaultField("comments","Comments","COMMENTS",FieldType.STRING);
        comments.setDefault(true);
        timeOffFields.add(comments);

        timeOffModule.setFields(timeOffFields);
        modules.add(timeOffModule);


        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.execute();
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule timeOffModule = modBean.getModule(FacilioConstants.TimeOff.TIME_OFF);

        FacilioForm timeOffForm =new FacilioForm();
        timeOffForm.setDisplayName("Standard");
        timeOffForm.setName("default_asset_web");
        timeOffForm.setModule(timeOffModule);
        timeOffForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        timeOffForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP));

        List<FormField> timeOffFormFields = new ArrayList<>();
        timeOffFormFields.add(new FormField("people",FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"Field Agent",FormField.Required.REQUIRED,1,2));
        timeOffFormFields.add(new FormField("type", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Time-Off Type", FormField.Required.REQUIRED,2,2));
        timeOffFormFields.add(new FormField("startTime", FacilioField.FieldDisplayType.DATETIME, "Start Time", FormField.Required.REQUIRED, 3, 2));
        timeOffFormFields.add(new FormField("endTime", FacilioField.FieldDisplayType.DATETIME, "End Time", FormField.Required.REQUIRED, 4, 2));
        timeOffFormFields.add(new FormField("comments",FacilioField.FieldDisplayType.TEXTAREA,"Comments",FormField.Required.OPTIONAL,3,1));

        FormSection section = new FormSection("Default", 1, timeOffFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        timeOffForm.setSections(Collections.singletonList(section));
        timeOffForm.setIsSystemForm(true);
        timeOffForm.setType(FacilioForm.Type.FORM);

        List<FacilioForm> timeOffModuleForms = new ArrayList<>();
        timeOffModuleForms.add(timeOffForm);

        return timeOffModuleForms;
    }

    public void addActivityModuleForTimeOff() throws Exception {
        // TODO Auto-generated method stub

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule timeOff = modBean.getModule(FacilioConstants.TimeOff.TIME_OFF);

        FacilioModule module = new FacilioModule(FacilioConstants.TimeOff.TIME_OFF_ACTIVITY,
                "Time Off Activity",
                "Time_Off_Activity",
                FacilioModule.ModuleType.ACTIVITY
        );


        List<FacilioField> fields = new ArrayList<>();

        NumberField parentId = FieldFactory.getDefaultField("parentId", "Parent", "PARENT_ID", FieldType.NUMBER);
        fields.add(parentId);

        FacilioField timefield = FieldFactory.getDefaultField("ttime", "Timestamp", "TTIME", FieldType.DATE_TIME);

        fields.add(timefield);

        NumberField type = FieldFactory.getDefaultField("type", "Type", "ACTIVITY_TYPE", FieldType.NUMBER);
        fields.add(type);

        LookupField doneBy = FieldFactory.getDefaultField("doneBy", "Done By", "DONE_BY_ID", FieldType.LOOKUP);
        doneBy.setSpecialType("users");
        fields.add(doneBy);

        FacilioField info = FieldFactory.getDefaultField("infoJsonStr", "Info", "INFO", FieldType.STRING);

        fields.add(info);


        module.setFields(fields);

        FacilioChain addModuleChain1 = TransactionChainFactory.addSystemModuleChain();
        addModuleChain1.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(module));
        addModuleChain1.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain1.execute();

        modBean.addSubModule(timeOff.getModuleId(), module.getModuleId());
    }

    @Override
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.TimeOff.TIME_OFF);

        List<String> appNames = new ArrayList<>();
        appNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);

        Map<String, List<PagesContext>> appNameVsPage = new HashMap<>();
        for (String appName : appNames) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            appNameVsPage.put(appName, createTimeOffPage(app, module, false, true));
        }
        return appNameVsPage;
    }
    private static List<PagesContext> createTimeOffPage(ApplicationContext app, FacilioModule module, boolean isTemplate, boolean isDefault) throws Exception {

        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.TimeOff.TIME_OFF_ACTIVITY);

        return new ModulePages()
                .addPage("timeOff", "Time Off", "", null, isTemplate, isDefault, false)
                .addWebTab("timeoffsummary", "Summary", true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("timeoffsummaryfields", null, null)
                .addWidget("timeoffsummaryfieldswidget", "Time Off Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_24", 0, 0, null, getSummaryWidgetDetails(FacilioConstants.TimeOff.TIME_OFF))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()


                .addWebTab("timeoffhistory", "History", true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("history", null, null)
                .addWidget("historyWidget", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_60", 0, 0, historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .pageDone().getCustomPages();


    }

    private static JSONObject getSummaryWidgetDetails(String moduleName) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField people = moduleBean.getField("people", moduleName);
        FacilioField type = moduleBean.getField("type", moduleName);
        FacilioField startTime = moduleBean.getField("startTime", moduleName);
        FacilioField endTime = moduleBean.getField("endTime", moduleName);
        FacilioField comments=moduleBean.getField("comments",moduleName);

        FacilioField sysCreatedBy = moduleBean.getField("sysCreatedBy", moduleName);
        FacilioField sysCreatedTime = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField sysModifiedBy = moduleBean.getField("sysModifiedBy", moduleName);
        FacilioField sysModifiedTime = moduleBean.getField("sysModifiedTime", moduleName);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(widgetGroup, people, 1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, type, 1, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, startTime, 1, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, endTime, 1, 4, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, comments, 2, 1, 4);

        SummaryWidgetGroup sysDetailsWidgetGroup=new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(sysDetailsWidgetGroup, sysCreatedBy, 1, 1, 1);
        addSummaryFieldInWidgetGroup(sysDetailsWidgetGroup, sysCreatedTime, 1, 2, 1);
        addSummaryFieldInWidgetGroup(sysDetailsWidgetGroup, sysModifiedBy, 1, 3, 1);
        addSummaryFieldInWidgetGroup(sysDetailsWidgetGroup, sysModifiedTime, 1, 4, 1);

        widgetGroup.setName("moduleDetails");
        widgetGroup.setDisplayName("General Information");
        widgetGroup.setColumns(4);

        sysDetailsWidgetGroup.setName("systemDetails");
        sysDetailsWidgetGroup.setDisplayName("System Details");
        sysDetailsWidgetGroup.setColumns(4);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);
        widgetGroupList.add(sysDetailsWidgetGroup);

        pageWidget.setDisplayName("");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP).getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);

    }

    private static void addSummaryFieldInWidgetGroup(SummaryWidgetGroup widgetGroup, FacilioField field, int rowIndex, int colIndex, int colSpan) {
        if (field != null) {
            SummaryWidgetGroupFields summaryField = new SummaryWidgetGroupFields();
            summaryField.setName(field.getName());
            summaryField.setDisplayName(field.getDisplayName());
            summaryField.setFieldId(field.getFieldId());
            summaryField.setRowIndex(rowIndex);
            summaryField.setColIndex(colIndex);
            summaryField.setColSpan(colSpan);

            if (widgetGroup.getFields() == null) {
                widgetGroup.setFields(new ArrayList<>(Arrays.asList(summaryField)));
            } else {
                widgetGroup.getFields().add(summaryField);
            }
        }
    }

    private static JSONObject getSummaryWidgetGroup(boolean isMobile) throws Exception {
        JSONObject commentWidgetParam = new JSONObject();
        commentWidgetParam.put("notesModuleName", FacilioConstants.TimeOff.TIME_OFF_NOTES);

        JSONObject attachmentWidgetParam = new JSONObject();
        attachmentWidgetParam.put("attachmentsModuleName", FacilioConstants.TimeOff.TIME_OFF_ATTACHMENTS);

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentwidget", "Comment", PageWidget.WidgetType.COMMENT, isMobile ? "flexiblemobilecomment_8" : "flexiblewebcomment_27", 0, 0, commentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile ? "flexiblemobileattachment_8" : "flexiblewebattachment_27", 0, 0, attachmentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();

        return FieldUtil.getAsJSON(widgetGroup);
    }
    @Override
    public List<Map<String, Object>> getViewsAndGroups() throws Exception{
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> timeOffViews = new ArrayList<FacilioView>();
        timeOffViews.add(getAllTimeOffViews().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.TimeOff.TIME_OFF);
        groupDetails.put("views", timeOffViews);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private FacilioView getAllTimeOffViews() throws Exception {

        FacilioModule timeOffModule = Constants.getModBean().getModule(FacilioConstants.TimeOff.TIME_OFF);
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getIdField(timeOffModule), true));

        FacilioView allView = new FacilioView();
        allView.setName("alltimeoff");
        allView.setDisplayName("All Time Off");
        allView.setModuleName(FacilioConstants.TimeOff.TIME_OFF);
        allView.setSortFields(sortFields);
        allView.setAppLinkNames(TimeOffModule.timeOffSupportedApps);


        List<ViewField> timeOffViewFields = new ArrayList<>();

        timeOffViewFields.add(new ViewField("people","Field Agent"));
        timeOffViewFields.add(new ViewField("type","Time-Off Type"));
        timeOffViewFields.add(new ViewField("startTime","Start Time"));
        timeOffViewFields.add(new ViewField("endTime","End Time"));
        timeOffViewFields.add(new ViewField("comments","Comments"));

        allView.setFields(timeOffViewFields);

        return allView;
    }

}
