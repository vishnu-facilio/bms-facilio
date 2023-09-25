package com.facilio.fsm.signup;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.*;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fsm.context.ServiceAppointmentTicketStatusContext;
import com.facilio.fsm.context.TripStatusContext;
import com.facilio.fsm.util.ServiceAppointmentUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.*;
import com.facilio.unitconversion.Metric;
import com.facilio.unitconversion.Unit;
import com.facilio.v3.context.Constants;
import org.json.simple.JSONObject;

import java.util.*;

public class TripModule extends BaseModuleConfig {
    public static List<String> tripSupportedApps = Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP, FacilioConstants.ApplicationLinkNames.FSM_APP);
    public TripModule(){setModuleName(FacilioConstants.Trip.TRIP);}

    @Override
    public void addData() throws Exception {
        try {
            FacilioModule trip = addTripModule();

            FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
            addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(trip));
            addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
            addModuleChain.execute();
            addActivityModuleForTrip();
            SignupUtil.addNotesAndAttachmentModule(trip);
            addSystemButtons();
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    private FacilioModule addTripModule() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = new FacilioModule(FacilioConstants.Trip.TRIP,
                "Trip",
                "TRIP",
                FacilioModule.ModuleType.BASE_ENTITY,
                true
        );

        List<FacilioField> fields = new ArrayList<>();

        NumberField localId = FieldFactory.getDefaultField("localId", "Id", "LOCAL_ID", FieldType.NUMBER);
        fields.add(localId);
        ModuleLocalIdUtil.insertModuleLocalId(FacilioConstants.Trip.TRIP);

        fields.add(new StringField(module,"code","Code",FacilioField.FieldDisplayType.TEXTBOX,"CODE", FieldType.STRING,true,false,true,true));

        LookupField peopleId = FieldFactory.getDefaultField("people", "Field Agent", "PEOPLE_ID", FieldType.LOOKUP,false);
        peopleId.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.PEOPLE));
        fields.add(peopleId);

        LookupField serviceAppointmentId = new LookupField(module,"serviceAppointment","Service Appointment",FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"SERVICE_APPOINTMENT_ID", FieldType.LOOKUP,true,false,true,false,null,modBean.getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT));
        fields.add(serviceAppointmentId);

        LookupField startLocation = FieldFactory.getDefaultField("startLocation","Start Location","START_LOCATION",FieldType.LOOKUP);
        startLocation.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        startLocation.setLookupModule(modBean.getModule("location"));
        fields.add(startLocation);

        LookupField endLocation = FieldFactory.getDefaultField("endLocation","End Location","END_LOCATION",FieldType.LOOKUP);
        endLocation.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        endLocation.setLookupModule(modBean.getModule("location"));
        fields.add(endLocation);

        DateField startTime = FieldFactory.getDefaultField("startTime", "Start Time", "START_TIME", FieldType.DATE_TIME);
        fields.add(startTime);

        DateField endTime = FieldFactory.getDefaultField("endTime", "End Time", "END_TIME", FieldType.DATE_TIME);
        fields.add(endTime);

        NumberField tripDuration = FieldFactory.getDefaultField("tripDuration", "Trip Duration", "TRIP_DURATION", FieldType.NUMBER);
        tripDuration.setDisplayType(FacilioField.FieldDisplayType.DURATION);
        fields.add(tripDuration);

        NumberField tripDistance = FieldFactory.getDefaultField("tripDistance","Trip Distance","TRIP_DISTANCE",FieldType.DECIMAL);
        tripDistance.setMetric(Metric.LENGTH.getMetricId());
        tripDistance.setUnit(Unit.MILE.getSymbol());
        tripDistance.setUnitId(Unit.MILE.getUnitId());
        fields.add(tripDistance);

        NumberField estimatedDuration = FieldFactory.getDefaultField("estimatedDuration", "Estimated Duration", "ESTIMATED_DURATION", FieldType.NUMBER);
        fields.add(estimatedDuration);

        fields.add(FieldFactory.getDefaultField("estimatedDistance","Estimated Distance","ESTIMATED_DISTANCE",FieldType.DECIMAL));

        FileField journey = FieldFactory.getDefaultField("journey","Journey","JOURNEY_ID",FieldType.FILE);
        fields.add(journey);

        LookupField serviceOrder = FieldFactory.getDefaultField("serviceOrder","Service Order","SERVICE_ORDER_ID",FieldType.LOOKUP);
        serviceOrder.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.SERVICE_ORDER));
        fields.add(serviceOrder);

        LookupField moduleStateField = FieldFactory.getDefaultField("moduleState", "Status", "MODULE_STATE", FieldType.LOOKUP);
        moduleStateField.setDefault(true);
        moduleStateField.setLookupModule(modBean.getModule("ticketstatus"));
        fields.add(moduleStateField);

        NumberField stateFlowIdField = FieldFactory.getDefaultField("stateFlowId", "State Flow Id", "STATE_FLOW_ID", FieldType.NUMBER);
        stateFlowIdField.setDefault(true);
        fields.add(stateFlowIdField);

        LookupField approvalStateField = FieldFactory.getDefaultField("approvalStatus", "Approval Status", "APPROVAL_STATE", FieldType.LOOKUP);
        approvalStateField.setDefault(true);
        approvalStateField.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        approvalStateField.setLookupModule(modBean.getModule("ticketstatus"));
        fields.add(approvalStateField);

        NumberField approvalFlowIdField = FieldFactory.getDefaultField("approvalFlowId", "Approval Flow Id", "APPROVAL_FLOW_ID", FieldType.NUMBER);
        approvalFlowIdField.setDefault(true);
        fields.add(approvalFlowIdField);

        LookupField status = FieldFactory.getDefaultField("status","Trip Status","STATUS",FieldType.LOOKUP);
        status.setRequired(true);
        status.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        status.setLookupModule(modBean.getModule(FacilioConstants.Trip.TRIP_STATUS));
        fields.add(status);


        module.setFields(fields);
        return module;

    }

    public void addActivityModuleForTrip() throws Exception {
        // TODO Auto-generated method stub

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule trip = modBean.getModule(FacilioConstants.Trip.TRIP);

        FacilioModule module = new FacilioModule(FacilioConstants.Trip.TRIP_ACTIVITY,
                "Trip Activity",
                "Trip_Activity",
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

        modBean.addSubModule(trip.getModuleId(), module.getModuleId());
    }

    @Override
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.Trip.TRIP);

        List<String> appNames = new ArrayList<>();
        appNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);

        Map<String, List<PagesContext>> appNameVsPage = new HashMap<>();
        for (String appName : appNames) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            appNameVsPage.put(appName, createTripPage(app, module, false, true));
        }
        return appNameVsPage;
    }
    private static List<PagesContext> createTripPage(ApplicationContext app, FacilioModule module, boolean isTemplate, boolean isDefault) throws Exception {

        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.Trip.TRIP_ACTIVITY);

        return new ModulePages()
                .addPage("trip", "Trip", "", null, isTemplate, isDefault, false)
                .addWebLayout()
                .addTab("tripsummary", "Summary",PageTabContext.TabType.SIMPLE,  true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("tripsummaryfields", null, null)
                .addWidget("tripsummaryfieldswidget", "Trip Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_24", 0, 0, null, getSummaryWidgetDetails(FacilioConstants.Trip.TRIP))
                .widgetDone()
                .sectionDone()
//                .addSection("tripjourney",null,null)
//                .addWidget("tripjourneywidget","Journey",PageWidget.WidgetType.TRIP_JOURNEY,"tripJourney_22_6",0,0,null,null)
//                .widgetDone()
//                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("triphistory", "History",PageTabContext.TabType.SIMPLE,  true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("history", null, null)
                .addWidget("historyWidget", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_50", 0, 0, historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone()
                .pageDone().getCustomPages();


    }

    private static JSONObject getSummaryWidgetDetails(String moduleName) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField people = moduleBean.getField("people", moduleName);
        FacilioField serviceAppointment=moduleBean.getField("serviceAppointment",moduleName);
        FacilioField startTime = moduleBean.getField("startTime", moduleName);
        FacilioField endTime = moduleBean.getField("endTime", moduleName);
        FacilioField tripDuration=moduleBean.getField("tripDuration",moduleName);
        FacilioField tripDistance=moduleBean.getField("tripDistance",moduleName);


        FacilioField sysCreatedBy = moduleBean.getField("sysCreatedBy", moduleName);
        FacilioField sysCreatedTime = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField sysModifiedBy = moduleBean.getField("sysModifiedBy", moduleName);
        FacilioField sysModifiedTime = moduleBean.getField("sysModifiedTime", moduleName);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(widgetGroup, people, 1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, serviceAppointment, 1, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, startTime, 1, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, endTime, 1, 4, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, tripDuration, 2, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, tripDistance, 2, 2, 1);

        SummaryWidgetGroup systemDetailsWidgetGroup=new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(systemDetailsWidgetGroup, sysCreatedBy, 1, 1, 1);
        addSummaryFieldInWidgetGroup(systemDetailsWidgetGroup, sysCreatedTime, 1, 2, 1);
        addSummaryFieldInWidgetGroup(systemDetailsWidgetGroup, sysModifiedBy, 1, 3, 1);
        addSummaryFieldInWidgetGroup(systemDetailsWidgetGroup, sysModifiedTime, 1, 4, 1);

        widgetGroup.setName("moduleDetails");
        widgetGroup.setDisplayName("General Information");
        widgetGroup.setColumns(4);

        systemDetailsWidgetGroup.setName("systemDetails");
        systemDetailsWidgetGroup.setDisplayName("System Details");
        systemDetailsWidgetGroup.setColumns(4);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);
        widgetGroupList.add(systemDetailsWidgetGroup);

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
        commentWidgetParam.put("notesModuleName", FacilioConstants.Trip.TRIP_NOTES);

        JSONObject attachmentsWidgetParam = new JSONObject();
        attachmentsWidgetParam.put("attachmentsModuleName", FacilioConstants.Trip.TRIP_ATTACHMENTS);

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentwidget", "Comment", PageWidget.WidgetType.COMMENT, isMobile ? "flexiblemobilecomment_8" : "flexiblewebcomment_27", 0, 0, commentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile ? "flexiblemobileattachment_8" : "flexiblewebattachment_27", 0, 0, attachmentsWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();

        return FieldUtil.getAsJSON(widgetGroup);
    }

    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule tripModule = modBean.getModule(FacilioConstants.Trip.TRIP);
        FacilioForm tripForm =new FacilioForm();
        tripForm.setDisplayName("Standard");
        tripForm.setName("default_trip_web");
        tripForm.setModule(tripModule);
        tripForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        tripForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP));
        List<FormField> generalInformationFields = new ArrayList<>();

        generalInformationFields.add(new FormField("people", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Field Agent", FormField.Required.REQUIRED,1,2));
        generalInformationFields.add(new FormField("serviceAppointment", FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"Service Appointment",FormField.Required.REQUIRED,2,2));
        generalInformationFields.add(new FormField("startTime", FacilioField.FieldDisplayType.DATETIME, "Start Time", FormField.Required.REQUIRED, 3, 2));
        generalInformationFields.add(new FormField("endTime", FacilioField.FieldDisplayType.DATETIME, "End Time", FormField.Required.REQUIRED, 4, 2));
        generalInformationFields.add(new FormField("tripDistance", FacilioField.FieldDisplayType.NUMBER,"Distance",FormField.Required.OPTIONAL,5,2));

        FormSection generalSection = new FormSection("General Information", 1, generalInformationFields, true);
        generalSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormSection> webTripFormSection = new ArrayList<>();
        webTripFormSection.add(generalSection);

        tripForm.setSections(webTripFormSection);
        tripForm.setIsSystemForm(true);
        tripForm.setType(FacilioForm.Type.FORM);
        List<FacilioForm> tripModuleForms = new ArrayList<>();
        tripModuleForms.add(tripForm);
        return tripModuleForms;
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() throws Exception{
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> tripViews = new ArrayList<FacilioView>();
        tripViews.add(getAllTripViews().setOrder(order++));
        tripViews.add(getHiddenAllTripViews().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "default");
        groupDetails.put("displayName", "Default");
        groupDetails.put("moduleName", FacilioConstants.Trip.TRIP);
        groupDetails.put("views", tripViews);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private FacilioView getHiddenAllTripViews() throws Exception {

        FacilioModule tripModule = Constants.getModBean().getModule(FacilioConstants.Trip.TRIP);
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getIdField(tripModule), true));

        FacilioView allView = new FacilioView();
        allView.setName("hidden-all");
        allView.setDisplayName("All Trips");
        allView.setModuleName(FacilioConstants.Trip.TRIP);
        allView.setSortFields(sortFields);
        allView.setAppLinkNames(TripModule.tripSupportedApps);
        allView.setHidden(true);


        List<ViewField> tripViewFields = new ArrayList<>();
        tripViewFields.add(new ViewField("code","Code"));
        tripViewFields.add(new ViewField("people","Field Agent"));
        tripViewFields.add(new ViewField("startTime","Start Time"));
        tripViewFields.add(new ViewField("endTime","End Time"));
        tripViewFields.add(new ViewField("tripDuration","Duration"));
        tripViewFields.add(new ViewField("tripDistance","Distance"));

        allView.setFields(tripViewFields);

        return allView;
    }

    private FacilioView getAllTripViews() throws Exception {

        FacilioModule tripModule = Constants.getModBean().getModule(FacilioConstants.Trip.TRIP);
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getIdField(tripModule), true));

        FacilioView allView = new FacilioView();
        allView.setName("alltrips");
        allView.setDisplayName("All Trips");
        allView.setModuleName(FacilioConstants.Trip.TRIP);
        allView.setSortFields(sortFields);
        allView.setAppLinkNames(TripModule.tripSupportedApps);


        List<ViewField> tripViewFields = new ArrayList<>();
        tripViewFields.add(new ViewField("code","Code"));
        tripViewFields.add(new ViewField("people","Field Agent"));
        tripViewFields.add(new ViewField("startTime","Start Time"));
        tripViewFields.add(new ViewField("endTime","End Time"));
        tripViewFields.add(new ViewField("tripDuration","Duration"));
        tripViewFields.add(new ViewField("tripDistance","Distance"));

        allView.setFields(tripViewFields);

        return allView;
    }
    private static void addSystemButtons() throws Exception {

        TripStatusContext status = ServiceAppointmentUtil.getTripStatus(FacilioConstants.Trip.IN_PROGRESS);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.Trip.TRIP);
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        if(status != null) {
            SystemButtonRuleContext endTrip = new SystemButtonRuleContext();
            endTrip.setName("End Trip");
            endTrip.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
            endTrip.setIdentifier(FacilioConstants.ServiceAppointment.END_TRIP);
            endTrip.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
            endTrip.setPermission(AccountConstants.ModulePermission.UPDATE.name());

            Criteria endTripCriteria = new Criteria();
            endTripCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(FacilioConstants.ContextNames.STATUS),Collections.singletonList(status.getId()), PickListOperators.IS));
            endTripCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(FacilioConstants.ContextNames.PEOPLE), FacilioConstants.Criteria.LOGGED_IN_PEOPLE,PickListOperators.IS));
            endTripCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(FacilioConstants.Trip.START_TIME), CommonOperators.IS_NOT_EMPTY));
            endTripCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT), CommonOperators.IS_NOT_EMPTY));
            endTrip.setCriteria(endTripCriteria);
            SystemButtonApi.addSystemButton(FacilioConstants.Trip.TRIP,endTrip);

            SystemButtonRuleContext endTripButton = new SystemButtonRuleContext();
            endTripButton.setName("End Trip");
            endTripButton.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
            endTripButton.setIdentifier(FacilioConstants.ServiceAppointment.END_TRIP);
            endTripButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
            endTripButton.setPermission(AccountConstants.ModulePermission.UPDATE.name());
            endTripButton.setCriteria(endTripCriteria);
            SystemButtonApi.addSystemButton(FacilioConstants.Trip.TRIP,endTripButton);
        }
    }

}