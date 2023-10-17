package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.context.inventory.V3InventoryRequestContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.json.simple.JSONObject;

import java.util.*;

public class InventoryRequestModule extends BaseModuleConfig{
    public InventoryRequestModule(){
        setModuleName(FacilioConstants.ContextNames.INVENTORY_REQUEST);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() throws Exception {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> inventoryRequest = new ArrayList<FacilioView>();
        inventoryRequest.add(getAllInventoryRequestView().setOrder(order++));
        inventoryRequest.add(getPendingInventoryRequestView().setOrder(order++));
        inventoryRequest.add(getPartiallyReservedInventoryRequestView().setOrder(order++));
        inventoryRequest.add(getFullyReservedInventoryRequestView().setOrder(order++));


        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.INVENTORY_REQUEST);
        groupDetails.put("views", inventoryRequest);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllInventoryRequestView() {
        FacilioField createdTime = new FacilioField();
        createdTime.setName("sysCreatedTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("SYS_CREATED_TIME");
        createdTime.setModule(ModuleFactory.getInventoryRequestModule());

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All");
        allView.setSortFields(Arrays.asList(new SortField(createdTime, false)));
        allView.setFields(getAllViewColumns());

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    private static FacilioView getPendingInventoryRequestView() throws Exception {
        FacilioView pendingInvReqView = new FacilioView();
        FacilioField createdTime = new FacilioField();
        createdTime.setName("sysCreatedTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("SYS_CREATED_TIME");
        createdTime.setModule(ModuleFactory.getInventoryRequestModule());

        pendingInvReqView.setName("pending");
        pendingInvReqView.setDisplayName("Pending");
        pendingInvReqView.setSortFields(Arrays.asList(new SortField(createdTime, false)));
        pendingInvReqView.setFields(getAllViewColumns());
        pendingInvReqView.setCriteria(getPendingInventoryRequestViewCriteria());

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        pendingInvReqView.setAppLinkNames(appLinkNames);

        return pendingInvReqView;
    }

    private static Criteria getPendingInventoryRequestViewCriteria() throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Criteria pendingInventoryRequestViewCriteria = new Criteria();
        FacilioField inventoryRequestReservationStatusField = modBean.getField("inventoryRequestReservationStatus", FacilioConstants.ContextNames.INVENTORY_REQUEST);
        if(inventoryRequestReservationStatusField != null) {
            Condition pendingInventoryRequestViewCondition = new Condition();
            pendingInventoryRequestViewCondition.setField(inventoryRequestReservationStatusField);
            pendingInventoryRequestViewCondition.setOperator(NumberOperators.EQUALS);
            pendingInventoryRequestViewCondition.setValue(String.valueOf(V3InventoryRequestContext.InventoryRequestReservationStatus.PENDING.getIndex()));
            pendingInventoryRequestViewCriteria.addAndCondition(pendingInventoryRequestViewCondition);
        }
        return pendingInventoryRequestViewCriteria;
    }

    private static FacilioView getPartiallyReservedInventoryRequestView() throws Exception {
        FacilioView partiallyReservedInvReqView = new FacilioView();
        FacilioField createdTime = new FacilioField();
        createdTime.setName("sysCreatedTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("SYS_CREATED_TIME");
        createdTime.setModule(ModuleFactory.getInventoryRequestModule());

        partiallyReservedInvReqView.setName("partiallyReserved");
        partiallyReservedInvReqView.setDisplayName("Partially Reserved");
        partiallyReservedInvReqView.setSortFields(Arrays.asList(new SortField(createdTime, false)));
        partiallyReservedInvReqView.setFields(getAllViewColumns());
        partiallyReservedInvReqView.setCriteria(getPartiallyReservedInventoryRequestViewCriteria());

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        partiallyReservedInvReqView.setAppLinkNames(appLinkNames);

        return partiallyReservedInvReqView;
    }

    private static Criteria getPartiallyReservedInventoryRequestViewCriteria() throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Criteria partiallyReservedInventoryRequestViewCriteria = new Criteria();
        FacilioField inventoryRequestReservationStatusField = modBean.getField("inventoryRequestReservationStatus", FacilioConstants.ContextNames.INVENTORY_REQUEST);
        if(inventoryRequestReservationStatusField != null) {
            Condition partiallyReservedInventoryRequestViewCondition = new Condition();
            partiallyReservedInventoryRequestViewCondition.setField(inventoryRequestReservationStatusField);
            partiallyReservedInventoryRequestViewCondition.setOperator(NumberOperators.EQUALS);
            partiallyReservedInventoryRequestViewCondition.setValue(String.valueOf(V3InventoryRequestContext.InventoryRequestReservationStatus.PARTIALLY_RESERVED.getIndex()));
            partiallyReservedInventoryRequestViewCriteria.addAndCondition(partiallyReservedInventoryRequestViewCondition);
        }
        return partiallyReservedInventoryRequestViewCriteria;
    }

    private static FacilioView getFullyReservedInventoryRequestView() throws Exception {
        FacilioView fullyReservedInvReqView = new FacilioView();

        FacilioField createdTime = new FacilioField();
        createdTime.setName("sysCreatedTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("SYS_CREATED_TIME");
        createdTime.setModule(ModuleFactory.getInventoryRequestModule());

        fullyReservedInvReqView.setName("fullyReserved");
        fullyReservedInvReqView.setDisplayName("Fully Reserved");
        fullyReservedInvReqView.setSortFields(Arrays.asList(new SortField(createdTime, false)));
        fullyReservedInvReqView.setFields(getAllViewColumns());
        fullyReservedInvReqView.setCriteria(getFullyReservedInventoryRequestViewCriteria());

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        fullyReservedInvReqView.setAppLinkNames(appLinkNames);

        return fullyReservedInvReqView;
    }

    private static Criteria getFullyReservedInventoryRequestViewCriteria() throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Criteria fullyReservedInventoryRequestViewCriteria = new Criteria();
        FacilioField inventoryRequestReservationStatusField = modBean.getField("inventoryRequestReservationStatus", FacilioConstants.ContextNames.INVENTORY_REQUEST);
        if(inventoryRequestReservationStatusField != null) {
            Condition fullyReservedInventoryRequestViewCondition = new Condition();
            fullyReservedInventoryRequestViewCondition.setField(inventoryRequestReservationStatusField);
            fullyReservedInventoryRequestViewCondition.setOperator(NumberOperators.EQUALS);
            fullyReservedInventoryRequestViewCondition.setValue(String.valueOf(V3InventoryRequestContext.InventoryRequestReservationStatus.FULLY_RESERVED.getIndex()));
            fullyReservedInventoryRequestViewCriteria.addAndCondition(fullyReservedInventoryRequestViewCondition);
        }
        return fullyReservedInventoryRequestViewCriteria;
    }

    private static List<ViewField> getAllViewColumns() {
        List<ViewField> columns = new ArrayList<ViewField>();
        columns.add(new ViewField("name","Name"));
        columns.add(new ViewField("inventoryRequestReservationStatus","Reservation Status"));
        columns.add(new ViewField("storeRoom","Store Room"));
        columns.add(new ViewField("workorder","Work Order"));
        columns.add(new ViewField("sysCreatedBy","Created By"));
        columns.add(new ViewField("sysCreatedTime","Created Time"));
        columns.add(new ViewField("sysModifiedBy","Modified By"));
        columns.add(new ViewField("sysModifiedTime","Modified Time"));
        return columns;
    }


    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule inventoryRequestModule = modBean.getModule(FacilioConstants.ContextNames.INVENTORY_REQUEST);

        FacilioForm inventoryRequestModuleForm = new FacilioForm();
        inventoryRequestModuleForm.setDisplayName("INVENTORY REQUEST");
        inventoryRequestModuleForm.setName("default_inventoryrequest_web");
        inventoryRequestModuleForm.setModule(inventoryRequestModule);
        inventoryRequestModuleForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        inventoryRequestModuleForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP));

        List<FormField> inventoryRequestModuleFormDefaultFields = new ArrayList<>();
        int seqNum = 0;
        inventoryRequestModuleFormDefaultFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, ++seqNum, 1));
        inventoryRequestModuleFormDefaultFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, ++seqNum, 1));
        inventoryRequestModuleFormDefaultFields.add(new FormField("requestedTime", FacilioField.FieldDisplayType.DATE, "Requested Date", FormField.Required.OPTIONAL, ++seqNum, 2));
        inventoryRequestModuleFormDefaultFields.add(new FormField("requiredTime", FacilioField.FieldDisplayType.DATE, "Required Date", FormField.Required.OPTIONAL, ++seqNum, 2));
        inventoryRequestModuleFormDefaultFields.add(new FormField("requestedBy", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Requested By", FormField.Required.OPTIONAL, "user", ++seqNum, 2));
        inventoryRequestModuleFormDefaultFields.add(new FormField("requestedFor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Requested For", FormField.Required.REQUIRED, "user", ++seqNum, 2));
        inventoryRequestModuleFormDefaultFields.add(new FormField("storeRoom", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Storeroom", FormField.Required.REQUIRED, "storeRoom", ++seqNum, 2));
//        inventoryRequestModuleFormDefaultFields.add(new FormField("transactionType", FacilioField.FieldDisplayType.SELECTBOX, "Transaction Type", FormField.Required.REQUIRED, ++seqNum, 2));
        inventoryRequestModuleFormDefaultFields.add(new FormField("workorder", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Work Order", FormField.Required.REQUIRED, ++seqNum, 2));

        List<FormField> lineItemFields = new ArrayList<>();
        lineItemFields.add(new FormField("inventoryrequestlineitems", FacilioField.FieldDisplayType.INVREQUEST_LINE_ITEMS, "LINE ITEMS", FormField.Required.REQUIRED, ++seqNum, 1));

        List<FormField> inventoryRequestModuleFormFields = new ArrayList<>();
        inventoryRequestModuleFormFields.addAll(inventoryRequestModuleFormDefaultFields);
        inventoryRequestModuleFormFields.addAll(lineItemFields);

        FormSection defaultSection = new FormSection("Inventory Request", 1, inventoryRequestModuleFormDefaultFields, true);
        defaultSection.setSectionType(FormSection.SectionType.FIELDS);

        FormSection lineItemSection = new FormSection("Line Items", 2, lineItemFields, true);
        lineItemSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormSection> sections = new ArrayList<>();
        sections.add(defaultSection);
        sections.add(lineItemSection);

        inventoryRequestModuleForm.setSections(sections);
        inventoryRequestModuleForm.setIsSystemForm(true);
        inventoryRequestModuleForm.setType(FacilioForm.Type.FORM);
        return Collections.singletonList(inventoryRequestModuleForm);
    }

    @Override
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {
        FacilioModule module=ModuleFactory.getInventoryRequestModule();
        List<String> appNames = new ArrayList<>();
        appNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        Map<String,List<PagesContext>> appNameVsPage = new HashMap<>();
        for (String appName : appNames) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            appNameVsPage.put(appName,createInventoryRequestPage(app, module, false,true));
        }
        return appNameVsPage;
    }
    public static List<PagesContext> createInventoryRequestPage(ApplicationContext app, FacilioModule module, boolean isTemplate, boolean isDefault) throws Exception {

        return new ModulePages()
                .addPage("inventoryrequestdefaultpage", "Default Inventory Request Page", "", null, isTemplate, isDefault, true)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("inventoryrequestsummary", null, null)
                .addWidget("inventoryrequestsummary", "Inventory Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, getSummaryWidgetDetails(module.getName(),app))
                .widgetDone()
                .sectionDone()
                .addSection("inventoryrequestlineitems", null, null)
                .addWidget("inventoryrequestlineitems", "Line Items", PageWidget.WidgetType.INVENTORY_REQUEST_LINE_ITEMS, "flexiblewebinventoryrequestlineitems_6", 0, 0, null,null)
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getWidgetGroup())
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone()
                .pageDone().getCustomPages();
    }
    private static JSONObject getWidgetGroup() throws Exception {
        JSONObject commentWidgetParam = new JSONObject();
        commentWidgetParam.put("notesModuleName","inventoryrequestnotes");
        JSONObject attachmentWidgetParam = new JSONObject();
        attachmentWidgetParam.put("attachmentsModuleName", "inventoryrequestattachments");
        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentwidget", "Comments", PageWidget.WidgetType.COMMENT,  "flexiblewebcomment_5", 0, 0, commentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Attachments", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT,  "flexiblewebattachment_5", 0, 0, attachmentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();
        return FieldUtil.getAsJSON(widgetGroup);
    }
    private static JSONObject getSummaryWidgetDetails(String moduleName,ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        //general information

        FacilioField description = moduleBean.getField("description", moduleName);
        FacilioField requestedTime = moduleBean.getField("requestedTime", moduleName);
        FacilioField requiredTime = moduleBean.getField("requiredTime", moduleName);
        FacilioField requestedBy = moduleBean.getField("requestedBy", moduleName);
        FacilioField requestedFor = moduleBean.getField("requestedFor", moduleName);
        FacilioField storeRoom = moduleBean.getField("duestoreRoomDate", moduleName);
        FacilioField workorder=moduleBean.getField("workorder",moduleName);

        SummaryWidgetGroup generalInformationwidgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, description, 1, 1, 4);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, requestedTime, 2, 1, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, requiredTime, 2, 2, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, requestedBy, 2, 3, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, requestedFor, 2, 4, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, storeRoom, 3, 1, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, workorder, 3, 2, 1);

        // System Details
        FacilioField sysCreatedByField = moduleBean.getField("sysCreatedBy", moduleName);
        FacilioField sysCreatedTimeField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField sysModifiedByField = moduleBean.getField("sysModifiedBy", moduleName);
        FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime", moduleName);

        SummaryWidgetGroup systemDetailswidgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(systemDetailswidgetGroup, sysCreatedByField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(systemDetailswidgetGroup, sysCreatedTimeField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(systemDetailswidgetGroup, sysModifiedByField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(systemDetailswidgetGroup, sysModifiedTimeField, 1, 4, 1);

        generalInformationwidgetGroup.setName("primaryDetails");
        generalInformationwidgetGroup.setDisplayName("Primary Details");
        generalInformationwidgetGroup.setColumns(4);


        systemDetailswidgetGroup.setName("systemDetails");
        systemDetailswidgetGroup.setDisplayName("System Details");
        systemDetailswidgetGroup.setColumns(4);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(generalInformationwidgetGroup);
        widgetGroupList.add(systemDetailswidgetGroup);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();
        widgetGroup.setName("primaryDetails");
        widgetGroup.setDisplayName("Primary Details");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);
    }
    private static void addSummaryFieldInWidgetGroup(SummaryWidgetGroup widgetGroup, FacilioField field, int rowIndex, int colIndex, int colSpan){
        addSummaryFieldInWidgetGroup(widgetGroup,field,rowIndex,colIndex,colSpan,null);
    }
    private static void addSummaryFieldInWidgetGroup(SummaryWidgetGroup widgetGroup, FacilioField field, int rowIndex, int colIndex, int colSpan,FacilioField lookupField) {
        if (field != null) {
            SummaryWidgetGroupFields summaryField = new SummaryWidgetGroupFields();
            summaryField.setName(field.getName());
            summaryField.setDisplayName(field.getDisplayName());
            summaryField.setFieldId(field.getFieldId());
            summaryField.setRowIndex(rowIndex);
            summaryField.setColIndex(colIndex);
            summaryField.setColSpan(colSpan);

            if(lookupField!=null){
                summaryField.setParentLookupFieldId(lookupField.getFieldId());
            }

            if (widgetGroup.getFields() == null) {
                widgetGroup.setFields(new ArrayList<>(Arrays.asList(summaryField)));
            } else {
                widgetGroup.getFields().add(summaryField);
            }
        }
    }
}

