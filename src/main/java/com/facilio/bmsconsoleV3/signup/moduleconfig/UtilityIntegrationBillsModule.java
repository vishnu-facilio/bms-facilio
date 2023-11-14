package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.RelatedListWidgetUtil;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.*;
import com.facilio.v3.context.Constants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class UtilityIntegrationBillsModule extends BaseModuleConfig {
    public static List<String> supportedApps = Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP, FacilioConstants.ApplicationLinkNames.IWMS_APP,FacilioConstants.ApplicationLinkNames.ENERGY_APP);

    public UtilityIntegrationBillsModule(){
        setModuleName(FacilioConstants.UTILITY_INTEGRATION_BILLS);
    }

    @Override
    public void addData() throws Exception {
        ModuleBean bean = Constants.getModBean();

        List<FacilioModule> modules = new ArrayList<>();

        FacilioModule utilityIntegrationBills = addUtilityIntegrationBills();
        modules.add(utilityIntegrationBills);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.execute();
        addDefaultStateFlow(utilityIntegrationBills);
    }
    public FacilioModule addUtilityIntegrationBills() throws Exception{

        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean", AccountUtil.getCurrentOrg().getOrgId());

        FacilioModule module = new FacilioModule("utilityIntegrationBills", "Utility Bills", "Utility_Integration_Bills",FacilioModule.ModuleType.BASE_ENTITY,true);

        List<FacilioField> fields = new ArrayList<>();

        StringField billUid = (StringField) FieldFactory.getDefaultField("billUid", "Bill UID", "BILL_UID", FieldType.STRING,true);
        fields.add(billUid);

        StringField meterUid = (StringField) FieldFactory.getDefaultField("meterUid", "Meter UID", "METER_UID", FieldType.STRING);
        fields.add(meterUid);

        StringField customerUid = (StringField) FieldFactory.getDefaultField("customerUid", "Customer UID", "CUSTOMER_UID", FieldType.STRING);
        fields.add(customerUid);

        DateField createdTime =  FieldFactory.getDefaultField("createdTime", "Created Time", "CREATED_TIME", FieldType.DATE_TIME,FacilioField.FieldDisplayType.DATETIME);
        fields.add(createdTime);

        DateField updatedTime =  FieldFactory.getDefaultField("updatedTime", "Updated Time", "UPDATED_TIME", FieldType.DATE_TIME,FacilioField.FieldDisplayType.DATETIME);
        fields.add(updatedTime);


        StringField meta = (StringField) FieldFactory.getDefaultField("meta", "Meta", "META_JSON", FieldType.STRING);
        fields.add(meta);

        StringField utilityID = (StringField) FieldFactory.getDefaultField("utilityID", "Utility ID", "UTILITY_ID", FieldType.STRING);
        fields.add(utilityID);

        StringField serviceIdentifier = (StringField) FieldFactory.getDefaultField("serviceIdentifier", "Service Identifier", "SERVICE_IDENTIFIER", FieldType.STRING);
        fields.add(serviceIdentifier);

        StringField serviceTariff = (StringField) FieldFactory.getDefaultField("serviceTariff", "Service Tariff", "SERVICE_TARIFF", FieldType.STRING);
        fields.add(serviceTariff);

        StringField serviceAddress = (StringField) FieldFactory.getDefaultField("serviceAddress", "Service Address", "SERVICE_ADDRESS", FieldType.STRING);
        fields.add(serviceAddress);

        StringField meterNumber = (StringField)FieldFactory.getDefaultField("meterNumber","Meter Number","METER_NUMBER", FieldType.STRING);
        fields.add(meterNumber);

        StringField serviceClass = (StringField) FieldFactory.getDefaultField("serviceClass", "Utility Type", "SERVICE_CLASS", FieldType.STRING);
        fields.add(serviceClass);


        StringField billingContact = (StringField) FieldFactory.getDefaultField("billingContact", "Billing Contact", "BILLING_CONTACT", FieldType.STRING);
        fields.add(billingContact);

        StringField billingAddress = (StringField) FieldFactory.getDefaultField("billingAddress", "Billing Address", "BILLING_ADDRESS", FieldType.STRING);
        fields.add(billingAddress);

        StringField billingAccount = (StringField) FieldFactory.getDefaultField("billingAccount", "Billing Account", "BILLING_ACCOUNT", FieldType.STRING);
        fields.add(billingAccount);

        DateField billStatementDate =  FieldFactory.getDefaultField("billStatementDate", "Bill Statement Date", "BILL_STATEMENT_DATE", FieldType.DATE_TIME,FacilioField.FieldDisplayType.DATETIME);
        fields.add(billStatementDate);

        DateField billStartDate =  FieldFactory.getDefaultField("billStartDate", "Bill Start Date", "BILL_START_DATE", FieldType.DATE_TIME,FacilioField.FieldDisplayType.DATETIME);
        fields.add(billStartDate);

        DateField billEndDate =  FieldFactory.getDefaultField("billEndDate", "Bill End Date", "BILL_END_DATE", FieldType.DATE_TIME,FacilioField.FieldDisplayType.DATETIME);
        fields.add(billEndDate);

        StringField billTotalUnit = (StringField) FieldFactory.getDefaultField("billTotalUnit", "Bill Total Unit", "BILL_TOTAL_UNIT", FieldType.STRING);
        fields.add(billTotalUnit);

        NumberField billTotalCost = FieldFactory.getDefaultField("billTotalCost","Bill Total Cost","BILL_TOTAL_COST", FieldType.DECIMAL);
        fields.add(billTotalCost);

        NumberField billTotalVolume = FieldFactory.getDefaultField("billTotalVolume","Bill Total Volume","BILL_TOTAL_VOLUME", FieldType.DECIMAL);
        fields.add(billTotalVolume);

        StringField supplierType = (StringField) FieldFactory.getDefaultField("supplierType", "Supplier Type", "SUPPLIER_TYPE", FieldType.STRING);
        fields.add(supplierType);

        StringField supplierName = (StringField) FieldFactory.getDefaultField("supplierName", "Supplier Name", "SUPPLIER_NAME", FieldType.STRING);
        fields.add(supplierName);

        StringField supplierServiceId = (StringField) FieldFactory.getDefaultField("supplierServiceId", "supplierServiceId", "SUPPLIER_SERVICE_ID", FieldType.STRING);
        fields.add(supplierServiceId);

        StringField supplierTariff = (StringField) FieldFactory.getDefaultField("supplierTariff", "Supplier Tariff", "SUPPLIER_TARIFF", FieldType.STRING);
        fields.add(supplierTariff);

        StringField supplierTotalUnit = (StringField) FieldFactory.getDefaultField("supplierTotalUnit", "Supplier Total Unit", "SUPPLIER_TOTAL_UNIT", FieldType.STRING);
        fields.add(supplierTotalUnit);

        NumberField supplierTotalCost = FieldFactory.getDefaultField("supplierTotalCost","Supplier Total Cost","SUPPLIER_TOTAL_COST", FieldType.DECIMAL);
        fields.add(supplierTotalCost);

        NumberField supplierTotalVolume = FieldFactory.getDefaultField("supplierTotalVolume","Supplier Total Cost","SUPPLIER_TOTAL_VOLUME", FieldType.DECIMAL);
        fields.add(supplierTotalVolume);

        StringField sourceType = (StringField) FieldFactory.getDefaultField("sourceType", "Source Type", "SOURCE_TYPE", FieldType.STRING);
        fields.add(sourceType);

        StringField sourceUrl = (StringField) FieldFactory.getDefaultField("sourceUrl", "Source URL", "SOURCE_URL", FieldType.STRING);
        fields.add(sourceUrl);

        NumberField billFileId = FieldFactory.getDefaultField("billFileId","Bill File Id","BILL_FILE_ID", FieldType.DECIMAL);
        fields.add(billFileId);

        LookupField utilityIntegrationCustomer = FieldFactory.getDefaultField("utilityIntegrationCustomer", "Utility Integration Customer ", "UTILITY_INTEGRATION_CUSTOMER_ID", FieldType.LOOKUP);
        utilityIntegrationCustomer.setLookupModule(moduleBean.getModule(FacilioConstants.UTILITY_INTEGRATION_CUSTOMER));
        utilityIntegrationCustomer.setRelatedListDisplayName("Utility Bills");
        fields.add(utilityIntegrationCustomer);

        LookupField utilityIntegrationMeter = FieldFactory.getDefaultField("utilityIntegrationMeter", "Utility Integration Meter ", "UTILITY_INTEGRATION_METER_ID", FieldType.LOOKUP);
        utilityIntegrationMeter.setLookupModule(moduleBean.getModule(FacilioConstants.UTILITY_INTEGRATION_METER));
        fields.add(utilityIntegrationMeter);

        NumberField billType = (NumberField) FieldFactory.getDefaultField("billType", "Bill Type", "BILL_TYPE", FieldType.NUMBER);
        fields.add(billType);

        StringField sourceDownloadUrl = (StringField) FieldFactory.getDefaultField("sourceDownloadUrl", "Source Download URL", "SOURCE_DOWNLOAD_URL", FieldType.STRING);
        fields.add(sourceDownloadUrl);

        SystemEnumField billStatus = (SystemEnumField) FieldFactory.getDefaultField("utilityBillStatus", "UtilityBillStatus", "UTILITY_BILL_STATUS", FieldType.SYSTEM_ENUM);
        billStatus.setEnumName("UtilityBillStatus");
        fields.add(billStatus);

        LookupField moduleStateField = (LookupField) FieldFactory.getField("moduleState", "Status", "MODULE_STATE", module, FieldType.LOOKUP);
        moduleStateField.setDefault(true);
        moduleStateField.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        moduleStateField.setLookupModule(moduleBean.getModule("ticketstatus"));
        fields.add(moduleStateField);

        FacilioField stateFlowIdField = FieldFactory.getField("stateFlowId", "State Flow Id", "STATE_FLOW_ID", module, FieldType.NUMBER);
        stateFlowIdField.setDefault(true);
        stateFlowIdField.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
        fields.add(stateFlowIdField);

        LookupField approvalStateField = (LookupField) FieldFactory.getField("approvalStatus", "Approval Status", "APPROVAL_STATE", module, FieldType.LOOKUP);
        approvalStateField.setDefault(true);
        approvalStateField.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        approvalStateField.setLookupModule(moduleBean.getModule("ticketstatus"));
        fields.add(approvalStateField);

        FacilioField approvalFlowIdField = FieldFactory.getField("approvalFlowId", "Approval Flow Id", "APPROVAL_FLOW_ID", module, FieldType.NUMBER);
        approvalFlowIdField.setDefault(true);
        approvalFlowIdField.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
        fields.add(approvalFlowIdField);


        module.setFields(fields);
        return module;
    }
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> utilitybillModule = new ArrayList<FacilioView>();
        utilitybillModule.add(getUtilityBillViews().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.UTILITY_INTEGRATION_BILLS);
        groupDetails.put("views", utilitybillModule);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private FacilioView getUtilityBillViews() {

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "utilityIntegrationBills.ID", FieldType.NUMBER), true));

        FacilioView utilityBillView = new FacilioView();
        utilityBillView.setName("all");
        utilityBillView.setDisplayName("Utility Bills");
        utilityBillView.setAppLinkNames(UtilityIntegrationBillsModule.supportedApps);
        utilityBillView.setModuleName(FacilioConstants.UTILITY_INTEGRATION_BILLS);
        utilityBillView.setSortFields(sortFields);

        List<ViewField> utilityBillViewFields = new ArrayList<>();

        utilityBillViewFields.add(new ViewField("billUid","Bill UID"));
        utilityBillViewFields.add(new ViewField("meterUid","Meter UID"));
        utilityBillViewFields.add(new ViewField("customerUid","Customer ID"));
        utilityBillViewFields.add(new ViewField("utilityID","Utility ID"));
        utilityBillViewFields.add(new ViewField("serviceIdentifier","Service Identifier"));
        utilityBillViewFields.add(new ViewField("serviceTariff","Service Tariff"));
        utilityBillViewFields.add(new ViewField("billingContact","Billing Contact"));
        utilityBillViewFields.add(new ViewField("billStatementDate","Bill Statement Date"));
        utilityBillViewFields.add(new ViewField("billTotalCost","Bill Total Cost"));
        utilityBillViewFields.add(new ViewField("serviceClass","Utility Type"));


        utilityBillView.setFields(utilityBillViewFields);

        return utilityBillView;
    }
    public void addDefaultStateFlow(FacilioModule module) throws Exception {

        FacilioStatus clearStatus = getFacilioStatus(module, "undisputed", "Undisputed", FacilioStatus.StatusType.OPEN, Boolean.FALSE);
        FacilioStatus partialStatus = getFacilioStatus(module, "partiallyDisputed", "Partially Dispute", FacilioStatus.StatusType.OPEN, Boolean.TRUE);
        FacilioStatus disputedStatus = getFacilioStatus(module, "underDispute", "Under Dispute", FacilioStatus.StatusType.OPEN, Boolean.FALSE);

        StateFlowRuleContext stateFlowRuleContext = new StateFlowRuleContext();
        stateFlowRuleContext.setName("Default Stateflow");
        stateFlowRuleContext.setModuleId(module.getModuleId());
        stateFlowRuleContext.setModule(module);
        stateFlowRuleContext.setActivityType(EventType.CREATE);
        stateFlowRuleContext.setExecutionOrder(1);
        stateFlowRuleContext.setStatus(true);
        stateFlowRuleContext.setDefaltStateFlow(true);
        stateFlowRuleContext.setDefaultStateId(clearStatus.getId());
        stateFlowRuleContext.setRuleType(WorkflowRuleContext.RuleType.STATE_FLOW);

        WorkflowRuleAPI.addWorkflowRule(stateFlowRuleContext);



    }
    private FacilioStatus getFacilioStatus(FacilioModule module, String status, String displayName, FacilioStatus.StatusType status1, Boolean timerEnabled) throws Exception {

        FacilioStatus statusObj = new FacilioStatus();
        statusObj.setStatus(status);
        statusObj.setDisplayName(displayName);
        statusObj.setTypeCode(status1.getIntVal());
        statusObj.setTimerEnabled(timerEnabled);
        TicketAPI.addStatus(statusObj, module);

        return statusObj;
    }
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {
        Map<String,List<PagesContext>> appNameVsPage = new HashMap<>();
        List<String> appNames = new ArrayList<>();

        appNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.ENERGY_APP);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(getModuleName());

        for (String appName : appNames) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            appNameVsPage.put(appName,getSystemPage(app, module));
        }
        return appNameVsPage;
    }

    private static List<PagesContext> getSystemPage(ApplicationContext app,FacilioModule module) throws Exception {



        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.UTILITY_INTEGRATION_BILL_ACTIVITY);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule billModule = modBean.getModule(FacilioConstants.UTILITY_INTEGRATION_BILLS);
        String pageName, pageDisplayName;
        pageName = billModule.getName().toLowerCase().replaceAll("[^a-zA-Z0-9]+", "") + "defaultpage";
        pageDisplayName = "Default "+ billModule.getDisplayName()+" Page ";

        List<PagesContext> disputePages = new ArrayList<>();

        PagesContext billMissingTemplatePage = new PagesContext(pageName, pageDisplayName, "", null, false, true, true)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("utilityintegrationbillsummary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("", "", null)
                .addWidget("billSummaryWidget", "Bill", PageWidget.WidgetType.BILL_SUMMARY_WIDGET, "webBillSummaryWidget_13_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()


                .addTab("utilityintegrationbillnotes", "Notes & Information", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("utilityintegrationbillsummaryfields", null, null)
                .addWidget("utilityintegrationbillsummaryfieldswidget", "Bill  Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_3", 0, 0, null, getSummaryWidgetDetails((FacilioConstants.UTILITY_INTEGRATION_BILLS),app))
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getSummaryWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("utilityintegrationbillrelated", "Related", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("utilityintegrationbillrelationships", "Relationships", "List of relationships and types between records across modules")
                .addWidget("bulkrelationshipwidget", "Relationships", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET, "flexiblewebbulkrelationshipwidget_6", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("utilityintegrationbilllist", "Related List", "List of related records across modules")
                .addWidget("bulkrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_6", 0, 0, null, RelatedListWidgetUtil.fetchAllRelatedListForModule(billModule))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()


                .layoutDone();
        disputePages.add(billMissingTemplatePage);


        return disputePages;
    }
    private static JSONObject getSummaryWidgetDetails(String moduleName,ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField supplier = moduleBean.getField("utilityID",moduleName);
        FacilioField phoneField = moduleBean.getField("serviceTariff", moduleName);
        FacilioField emailField = moduleBean.getField("serviceClass", moduleName);
        FacilioField unitField = moduleBean.getField("billTotalUnit", moduleName);
        FacilioField volumeField = moduleBean.getField("billTotalVolume", moduleName);
        FacilioField costField = moduleBean.getField("billTotalCost", moduleName);
        FacilioField createdField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField createdByField = moduleBean.getField("sysCreatedBy",moduleName);
        FacilioField modifiedField = moduleBean.getField("sysModifiedBy",moduleName);
        FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime",moduleName);


        SummaryWidget pageWidget = new SummaryWidget();

        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(widgetGroup, supplier, 1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, phoneField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, emailField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, unitField, 1, 4, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, volumeField, 2, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, costField, 2, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, createdField, 2, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, createdByField, 2, 4, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, modifiedField, 3, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, sysModifiedTimeField, 3, 2, 1);

        widgetGroup.setName("moduleDetails");
        widgetGroup.setDisplayName("");
        widgetGroup.setColumns(4);



        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);

        pageWidget.setDisplayName("");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
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
        commentWidgetParam.put("notesModuleName", FacilioConstants.UTILITY_INTEGRATION_BILL_NOTES);

        JSONObject attachmentWidgetParam = new JSONObject();
        attachmentWidgetParam.put("attachmentsModuleName", FacilioConstants.UTILITY_INTEGRATION_BILL_ATTACHMENTS);

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentwidget", "Comment", PageWidget.WidgetType.COMMENT, isMobile ? "flexiblemobilecomment_8" : "flexiblewebcomment_5", 0, 0, commentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile ? "flexiblemobileattachment_8" : "flexiblewebattachment_5", 0, 0, attachmentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();

        return FieldUtil.getAsJSON(widgetGroup);
    }


}