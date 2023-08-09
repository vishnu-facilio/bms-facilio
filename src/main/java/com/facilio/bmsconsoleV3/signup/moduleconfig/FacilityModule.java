package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.json.simple.JSONObject;

import java.util.*;

public class FacilityModule extends BaseModuleConfig{
    public FacilityModule(){
        setModuleName(FacilioConstants.ContextNames.FacilityBooking.FACILITY);
    }


    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> facility = new ArrayList<FacilioView>();
        facility.add(getAllFacilityView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.FacilityBooking.FACILITY);
        groupDetails.put("views", facility);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllFacilityView() {

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));
        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Facility");
        allView.setModuleName(FacilioConstants.ContextNames.FacilityBooking.FACILITY);
        allView.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule facilityModule = modBean.getModule(FacilioConstants.ContextNames.FacilityBooking.FACILITY);

        FacilioForm facilityCreationForm = new FacilioForm();
        facilityCreationForm.setDisplayName("Facility");
        facilityCreationForm.setName("default_"+ FacilioConstants.ContextNames.FacilityBooking.FACILITY +"_web");
        facilityCreationForm.setModule(facilityModule);
        facilityCreationForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        facilityCreationForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP));

        List<FormField> facilityCreationFormDefaultFields = new ArrayList<>();
        facilityCreationFormDefaultFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        facilityCreationFormDefaultFields.add(new FormField("facilityType", FacilioField.FieldDisplayType.SELECTBOX, "Facility Type", FormField.Required.OPTIONAL,2, 2));
        facilityCreationFormDefaultFields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED, "site",2, 3));
        facilityCreationFormDefaultFields.add(new FormField("location", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Location", FormField.Required.OPTIONAL, "basespace", 3, 2));
        FormField category = new FormField("category", FacilioField.FieldDisplayType.SELECTBOX, "Category", FormField.Required.OPTIONAL,3, 3);
        category.setAllowCreate(true);
        facilityCreationFormDefaultFields.add(category);
        facilityCreationFormDefaultFields.add(new FormField("manager", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Facility Manager", FormField.Required.OPTIONAL, "people",4, 1));
        facilityCreationFormDefaultFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL,5, 1));
        facilityCreationFormDefaultFields.add(new FormField("userGuidance", FacilioField.FieldDisplayType.TEXTAREA, "User Guidance", FormField.Required.OPTIONAL,6, 1));
        facilityCreationFormDefaultFields.add(new FormField("usageCapacity", FacilioField.FieldDisplayType.NUMBER, "Usage Capacity", FormField.Required.REQUIRED,7, 2));
        facilityCreationFormDefaultFields.add(new FormField("bookingAdvancePeriodInDays", FacilioField.FieldDisplayType.NUMBER, "Booking Advance In Days", FormField.Required.REQUIRED,7, 3));
        facilityCreationFormDefaultFields.add(new FormField("maxSlotBookingAllowed", FacilioField.FieldDisplayType.NUMBER, "Max Slot Bookings Allowed Per Booking", FormField.Required.REQUIRED,8, 2));
        facilityCreationFormDefaultFields.add(new FormField("maxAttendeeCountPerBooking", FacilioField.FieldDisplayType.NUMBER, "Maximum attendee Count Per Booking", FormField.Required.OPTIONAL,8, 3));
        facilityCreationFormDefaultFields.add(new FormField("isAttendeeListNeeded", FacilioField.FieldDisplayType.DECISION_BOX, "Attendee List Needed", FormField.Required.OPTIONAL,9, 2));
        facilityCreationFormDefaultFields.add(new FormField("isMultiBookingPerSlotAllowed", FacilioField.FieldDisplayType.DECISION_BOX, "Is Multi Booking Allowed Per slot", FormField.Required.OPTIONAL,9, 3));
        FormField slotGeneratedUptoField = new FormField("slotGeneratedUpto", FacilioField.FieldDisplayType.DATETIME, "Slot Generated Upto", FormField.Required.OPTIONAL,9, 1);
        slotGeneratedUptoField.setHideField(true);
        facilityCreationFormDefaultFields.add(slotGeneratedUptoField);
        facilityCreationFormDefaultFields.add(new FormField("allowCancellationBefore", FacilioField.FieldDisplayType.NUMBER, "Allow Cancellation Before (Days)", FormField.Required.REQUIRED,10, 1));
        facilityCreationFormDefaultFields.add(new FormField("isChargeable", FacilioField.FieldDisplayType.DECISION_BOX, "Is Chargeable", FormField.Required.OPTIONAL,11, 1));
        facilityCreationFormDefaultFields.add(new FormField("pricePerSlot", FacilioField.FieldDisplayType.NUMBER, "Price Per slot", FormField.Required.OPTIONAL,12, 2));
        facilityCreationFormDefaultFields.add(new FormField("securityDeposit", FacilioField.FieldDisplayType.NUMBER, "Security Deposit", FormField.Required.OPTIONAL,12, 3));
        facilityCreationFormDefaultFields.add(new FormField("slotDuration", FacilioField.FieldDisplayType.DURATION, "Slot Duration", FormField.Required.REQUIRED,13, 1));
        facilityCreationFormDefaultFields.add(new FormField("amenities", FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE, "Features / Amenties", FormField.Required.OPTIONAL,14, 1));

        List<FormField> slotAvailabilityFields = new ArrayList<>();
        slotAvailabilityFields.add(new FormField("facilityWeekdayAvailability", FacilioField.FieldDisplayType.FACILITY_AVAILABILITY, "Availability", FormField.Required.REQUIRED,15, 1));

        List<FormField> facilityCreationFormFields = new ArrayList<>();
        facilityCreationFormFields.addAll(facilityCreationFormDefaultFields);
        facilityCreationFormFields.addAll(slotAvailabilityFields);

//        facilityCreationForm.setFields(facilityCreationFormFields);

        FormSection defaultSection = new FormSection("DETAILS", 1, facilityCreationFormDefaultFields, false);
        defaultSection.setSectionType(FormSection.SectionType.FIELDS);

        FormSection checklistSection = new FormSection("AVAILABILITY", 2, slotAvailabilityFields, true);
        checklistSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormSection> sections = new ArrayList<>();
        sections.add(defaultSection);
        sections.add(checklistSection);

        facilityCreationForm.setSections(sections);
        facilityCreationForm.setIsSystemForm(true);
        facilityCreationForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(facilityCreationForm);
    }
    @Override
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {

        FacilioModule module= ModuleFactory.getFacilityModule();
        List<String> appNames = new ArrayList<>();
        appNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.IWMS_APP);
        Map<String,List<PagesContext>> appNameVsPage = new HashMap<>();
        for (String appName : appNames) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            appNameVsPage.put(appName,facilityModuleTemplatePage(app, module, false,true));
        }
        return appNameVsPage;
    }

    private List<PagesContext> facilityModuleTemplatePage(ApplicationContext app, FacilioModule module, boolean isTemplate, boolean isDefault) throws Exception {
        String pageName, pageDisplayName;
        pageName = module.getName() + "defaultpage";
        pageDisplayName = "Default " + module.getDisplayName() + " Page ";


        return new ModulePages()
                .addPage(pageName, pageDisplayName, "", null, isTemplate, isDefault, true)
                .addWebLayout()
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryfields", null, null)
                .addWidget("summaryfieldswidget", "Facility Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_24", 0, 0, null, getSummaryWidgetDetails(module.getName(), app))
                .widgetDone()
                .sectionDone()
                .addSection("facilityphotos", null, null)
                .addWidget("facilityphotoswidget", "Photoes", PageWidget.WidgetType.FACILITY_PHOTOS, "webfacilityphotos_15_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_20", 0, 0, null, getWidgetGroup())
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("availability", "Availability", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("featureavailability", null, null)
                .addWidget("featureavailability", "Features available", PageWidget.WidgetType.FACILITY_FEATURES, "flexiblewebfacilityfeatures_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("slotinformation", null, null)
                .addWidget("slotinformation", "Slot Information", PageWidget.WidgetType.FACILITY_SLOT_INFORMATION, "flexiblewebfacilityslotinformation_49", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("specialavailability", null, null)
                .addWidget("specialavailability", "Special Availabilities", PageWidget.WidgetType.FACILITY_SPECIAL_AVAILABILITY, "flexiblewebfacilityspecialavalability_28", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone()
                .pageDone().getCustomPages();
    }

    private static JSONObject getSummaryWidgetDetails(String moduleName, ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField descriptionField = moduleBean.getField("description", moduleName);
        FacilioField facilityType = moduleBean.getField("facilityType", moduleName);
        FacilioField siteId = moduleBean.getField("siteId", moduleName);
        FacilioField location = moduleBean.getField("location", moduleName);
        FacilioField category = moduleBean.getField("category", moduleName);
        FacilioField manager = moduleBean.getField("manager", moduleName);
        FacilioField usageCapacity = moduleBean.getField("usageCapacity", moduleName);
        FacilioField maxAttendeeCountPerBooking = moduleBean.getField("maxAttendeeCountPerBooking", moduleName);
        FacilioField slotGeneratedUpto = moduleBean.getField("slotGeneratedUpto", moduleName);
        FacilioField allowCancellationBefore = moduleBean.getField("allowCancellationBefore", moduleName);
        FacilioField pricePerSlot = moduleBean.getField("pricePerSlot", moduleName);
        FacilioField securityDeposit = moduleBean.getField("securityDeposit", moduleName);


        FacilioField sysCreatedByField = moduleBean.getField("sysCreatedBy", moduleName);
        FacilioField sysCreatedTimeField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField sysModifiedByField = moduleBean.getField("sysModifiedBy", moduleName);
        FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime", moduleName);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup generalInformationWidgetGroup = new SummaryWidgetGroup();


        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, descriptionField, 1, 1, 4);
        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, facilityType, 2, 1, 1);
        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, siteId, 2, 2, 1);
        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, location, 2, 3, 1);
        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, category, 2, 4, 1);
        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, manager, 3, 1, 1);
        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, usageCapacity, 3, 2, 1);
        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, maxAttendeeCountPerBooking, 3, 3, 1);
        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, slotGeneratedUpto, 3, 4, 1);
        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, allowCancellationBefore, 4, 1, 1);
        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, pricePerSlot, 4, 2, 1);
        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, securityDeposit, 4, 3, 1);

        generalInformationWidgetGroup.setName("generalInformation");
        generalInformationWidgetGroup.setDisplayName("Primary Details");
        generalInformationWidgetGroup.setColumns(4);


        SummaryWidgetGroup systemInformationWidgetGroup = new SummaryWidgetGroup();
        systemInformationWidgetGroup.setName("systemInformation");
        systemInformationWidgetGroup.setDisplayName("System Information");
        systemInformationWidgetGroup.setColumns(4);

        addSummaryFieldInWidgetGroup(systemInformationWidgetGroup, sysCreatedByField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(systemInformationWidgetGroup, sysCreatedTimeField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(systemInformationWidgetGroup, sysModifiedByField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(systemInformationWidgetGroup, sysModifiedTimeField, 1, 4, 1);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(generalInformationWidgetGroup);
        widgetGroupList.add(systemInformationWidgetGroup);

        pageWidget.setDisplayName("");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);

    }

    private static void addSummaryFieldInWidgetGroup(SummaryWidgetGroup widgetGroup, FacilioField field, int rowIndex, int colIndex, int colSpan) {
        addSummaryFieldInWidgetGroup(widgetGroup, field, rowIndex, colIndex, colSpan, null);
    }

    private static void addSummaryFieldInWidgetGroup(SummaryWidgetGroup widgetGroup, FacilioField field, int rowIndex, int colIndex, int colSpan, FacilioField lookupField) {
        if (field != null) {
            SummaryWidgetGroupFields summaryField = new SummaryWidgetGroupFields();
            summaryField.setName(field.getName());
            summaryField.setDisplayName(field.getDisplayName());
            summaryField.setFieldId(field.getFieldId());
            summaryField.setRowIndex(rowIndex);
            summaryField.setColIndex(colIndex);
            summaryField.setColSpan(colSpan);

            if (lookupField != null) {
                summaryField.setParentLookupFieldId(lookupField.getFieldId());
            }

            if (widgetGroup.getFields() == null) {
                widgetGroup.setFields(new ArrayList<>(Arrays.asList(summaryField)));
            } else {
                widgetGroup.getFields().add(summaryField);
            }
        }
    }

    private static JSONObject getWidgetGroup() throws Exception {
        JSONObject commentWidgetParam = new JSONObject();
        commentWidgetParam.put("notesModuleName", FacilioConstants.ContextNames.FacilityBooking.FACILITY_NOTES);
        JSONObject attachmentWidgetParam = new JSONObject();
        attachmentWidgetParam.put("attachmentsModuleName", FacilioConstants.ContextNames.FacilityBooking.FACILITY_ATTACHMENTS);
        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("comments", "Comments", "")
                .addWidget("commentwidget", "Comments", PageWidget.WidgetType.COMMENT, "flexiblewebcomment_27", 0, 0, commentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Attachments", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT, "flexiblewebattachment_27", 0, 0, attachmentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();
        return FieldUtil.getAsJSON(widgetGroup);
    }

}
