package com.facilio.fsm.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.RelatedListWidgetUtil;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.DateField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.NumberField;
import com.facilio.v3.context.Constants;
import org.json.simple.JSONObject;

import java.util.*;

public class TripModule extends BaseModuleConfig {
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

        LookupField peopleId = FieldFactory.getDefaultField("people", "Field Agent", "PEOPLE_ID", FieldType.LOOKUP,true);
        peopleId.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.PEOPLE));
        fields.add(peopleId);

        LookupField serviceAppointmentId = new LookupField(module,"serviceAppointment","Service Appointment",FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"SERVICE_APPOINTMENT_ID", FieldType.LOOKUP,true,false,true,true,null,modBean.getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT));
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
        fields.add(tripDuration);

        fields.add(FieldFactory.getDefaultField("tripDistance","Trip Distance","TRIP_DISTANCE",FieldType.DECIMAL));


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
                .addWebTab("tripsummary", "SUMMARY", true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("tripsummaryfields", null, null)
                .addWidget("tripsummaryfieldswidget", "Trip Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_24", 0, 0, null, getSummaryWidgetDetails(FacilioConstants.Trip.TRIP))
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_20", 0, 0, null, getSummaryWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()


                .addWebTab("triprelated", "RELATED", true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("triprelationships", "Relationships", "List of relationships and types between records across modules")
                .addWidget("tripbulkrelationshipwidget", "Relationships", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET, "flexiblewebbulkrelationshipwidget_29", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("triprelatedlist", "Related List", "List of related records across modules")
                .addWidget("tripbulkrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_29", 0, 4, null, RelatedListWidgetUtil.addAllRelatedModuleToWidget(FacilioConstants.Trip.TRIP))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addWebTab("triphistory", "HISTORY", true, null)
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
        FacilioField moduleState = moduleBean.getField("moduleState", moduleName);
        FacilioField serviceAppointment=moduleBean.getField("serviceAppointment",moduleName);
        FacilioField approvalStatus = moduleBean.getField("approvalStatus", moduleName);
        FacilioField startLocation=moduleBean.getField("startLocation",moduleName);
        FacilioField endLocation=moduleBean.getField("endLocation",moduleName);
        FacilioField startTime = moduleBean.getField("startTime", moduleName);
        FacilioField endTime = moduleBean.getField("endTime", moduleName);
        FacilioField sysCreatedBy = moduleBean.getField("sysCreatedBy", moduleName);
        FacilioField sysCreatedTime = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField sysModifiedBy = moduleBean.getField("sysModifiedBy", moduleName);
        FacilioField sysModifiedTime = moduleBean.getField("sysModifiedTime", moduleName);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(widgetGroup, people, 1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, moduleState, 1, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, serviceAppointment, 1, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, approvalStatus, 1, 4, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, startLocation, 2, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, endLocation, 2, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, startTime, 2, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, endTime, 2, 4, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, sysCreatedBy, 3, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, sysCreatedTime, 3, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, sysModifiedBy, 3, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, sysModifiedTime, 3, 4, 1);

        widgetGroup.setName("moduleDetails");
        widgetGroup.setDisplayName("General Information");
        widgetGroup.setColumns(4);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);

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
}