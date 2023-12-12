package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.bean.OrgBean;
import com.facilio.accounts.bean.RoleBean;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.*;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.context.invoice.InvoiceContextV3;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.*;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.*;
import com.facilio.relation.util.RelationshipWidgetUtil;
import com.facilio.remotemonitoring.signup.AlarmFilterRuleCriteriaModule;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class InvoiceModule extends BaseModuleConfig {
    public InvoiceModule() throws Exception {
        setModuleName(FacilioConstants.ContextNames.INVOICE);
    }
    @Override
    public void addData() throws Exception {
        FacilioModule invoiceModule =  addInvoiceModule(Constants.getModBean());
        FacilioModule invoiceLineItemModule = addInvoiceLineItemModule(Constants.getModBean(),invoiceModule);
        addInvoiceAttachmentsModule(Constants.getModBean(), AccountUtil.getCurrentOrg().getId(), invoiceModule);
        constructInvoiceNotesModule(Constants.getModBean(), AccountUtil.getCurrentOrg().getId(), invoiceModule);
        addActivityModuleForInvoice(invoiceModule);
        addTermsAndCondModuleForInvoice(invoiceModule);
        addInvoiceApproversRelModule();
        addInvoiceApproversField();
        addSystemButtons();
        addDefaultApprovals();

        ModuleLocalIdUtil.insertModuleLocalId(FacilioConstants.ContextNames.INVOICE);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() throws Exception {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> allInvoices = new ArrayList<FacilioView>();
        allInvoices.add(getAllInvoices().setOrder(order++));
        allInvoices.add(getAllVendorInvoices("allVendorInvoices",false).setOrder(order++));
        allInvoices.add(getAllCustomerInvoices("allCustomerInvoices",false).setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "allInvoices");
        groupDetails.put("displayName", "All Invoices");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.INVOICE);
        groupDetails.put("views", allInvoices);
        groupDetails.put("appLinkNames", Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        ArrayList<FacilioView> allVendorInvoices = new ArrayList<FacilioView>();
        order = 1;
        allVendorInvoices.add(getAllVendorInvoices("vendorInvoice",true).setOrder(order++));
        allVendorInvoices.add(getVendorInvoicesForApprovalView("vendorInvoiceForApproval",false).setOrder(order++));
        allVendorInvoices.add(getVendorInvoicesApprovedView("vendorInvoiceApproved",false).setOrder(order++));
        allVendorInvoices.add(getVendorInvoicesRejectedView("vendorInvoiceRejected",false).setOrder(order++));
        allVendorInvoices.add(getVendorInvoicesCanceledView("vendorInvoiceCanceled",false).setOrder(order++));

        ArrayList<FacilioView> allClientInvoices = new ArrayList<FacilioView>();
        order = 1;
        allClientInvoices.add(getAllCustomerInvoices("customerInvoices",true).setOrder(order++));
        allClientInvoices.add(getClientInvoicesForApprovalView("clientInvoicesForApproval",false).setOrder(order++));
        allClientInvoices.add(getClientInvoicesApprovedView("clientInvoiceApproved",false).setOrder(order++));
        allClientInvoices.add(getClientInvoicesRejectedView("clientInvoiceRejected",false).setOrder(order++));
        allClientInvoices.add(getClientInvoicesCanceledView("clientInvoiceCanceled",false).setOrder(order++));

        ArrayList<FacilioView> allPortalVendorInvoices = new ArrayList<FacilioView>();
        order = 1;
        allPortalVendorInvoices.add(getAllVendorInvoices("vendorInvoice_portal",true).setOrder(order++));
        allPortalVendorInvoices.add(getVendorInvoicesForApprovalView("vendorInvoiceForApproval_portal",true).setOrder(order++));
        allPortalVendorInvoices.add(getVendorInvoicesApprovedView("vendorInvoiceApproved_portal",true).setOrder(order++));
        allPortalVendorInvoices.add(getVendorInvoicesRejectedView("vendorInvoiceRejected_portal",true).setOrder(order++));
        allPortalVendorInvoices.add(getVendorInvoicesCanceledView("vendorInvoiceCanceled_portal",true).setOrder(order++));

        ArrayList<FacilioView> allPortalClientInvoices = new ArrayList<FacilioView>();
        order = 1;
        allPortalClientInvoices.add(getAllCustomerInvoices("customerInvoices_portal",true).setOrder(order++));
        allPortalClientInvoices.add(getClientInvoicesForApprovalView("clientInvoicesForApproval_portal_portal",true).setOrder(order++));
        allPortalClientInvoices.add(getClientInvoicesApprovedView("clientInvoiceApproved_portal",true).setOrder(order++));
        allPortalClientInvoices.add(getClientInvoicesRejectedView("clientInvoiceRejected_portal",true).setOrder(order++));
        allPortalClientInvoices.add(getClientInvoicesCanceledView("clientInvoiceCanceled_portal",true).setOrder(order++));

        Map<String, Object> allVendorInvoicesDetails = new HashMap<>();
        allVendorInvoicesDetails.put("name", "vendorInvoices");
        allVendorInvoicesDetails.put("displayName", "Vendor Invoices");
        allVendorInvoicesDetails.put("moduleName", FacilioConstants.ContextNames.INVOICE);
        allVendorInvoicesDetails.put("views", allVendorInvoices);
        allVendorInvoicesDetails.put("appLinkNames", Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        Map<String, Object> allCustomerInvoicesDetails = new HashMap<>();
        allCustomerInvoicesDetails.put("name", "clientInvoices");
        allCustomerInvoicesDetails.put("displayName", "Client Invoices");
        allCustomerInvoicesDetails.put("moduleName", FacilioConstants.ContextNames.INVOICE);
        allCustomerInvoicesDetails.put("views", allClientInvoices);
        allCustomerInvoicesDetails.put("appLinkNames", Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));


        Map<String, Object> allVendorPortalInvoicesDetails = new HashMap<>();
        allVendorPortalInvoicesDetails.put("name", "allInvoices");
        allVendorPortalInvoicesDetails.put("displayName", "All Invoices");
        allVendorPortalInvoicesDetails.put("moduleName", FacilioConstants.ContextNames.INVOICE);
        allVendorPortalInvoicesDetails.put("views", allPortalVendorInvoices);
        allVendorPortalInvoicesDetails.put("appLinkNames", Arrays.asList(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP));

        Map<String, Object> allClientPortalInvoicesDetails = new HashMap<>();
        allClientPortalInvoicesDetails.put("name", "allInvoices");
        allClientPortalInvoicesDetails.put("displayName", "All Invoices");
        allClientPortalInvoicesDetails.put("moduleName", FacilioConstants.ContextNames.INVOICE);
        allClientPortalInvoicesDetails.put("views", allPortalClientInvoices);
        allClientPortalInvoicesDetails.put("appLinkNames", Arrays.asList(FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP,FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP,FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP));


        groupVsViews.add(groupDetails);
        groupVsViews.add(allVendorInvoicesDetails);
        groupVsViews.add(allVendorPortalInvoicesDetails);
        groupVsViews.add(allCustomerInvoicesDetails);
        groupVsViews.add(allClientPortalInvoicesDetails);

        return groupVsViews;
    }

    @Override
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {

        Map<String,List<PagesContext>> appNameVsPage = new HashMap<>();
        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INVOICE);
        for (String appName : appLinkNames) {
            appNameVsPage.put(appName, createInvoiceDefaultPage(ApplicationApi.getApplicationForLinkName(appName), module, false, true));
        }
        return appNameVsPage;
    }

    public static List<PagesContext> createInvoiceDefaultPage(ApplicationContext app, FacilioModule module, boolean isTemplate, boolean isDefault) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule invoiceModule = modBean.getModule(FacilioConstants.ContextNames.INVOICE);

        JSONObject historyWidgetParam = new org.json.simple.JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.ContextNames.INVOICE_ACTIVITY);
        List<PagesContext> pagesContexts = new ArrayList<>();
        
          pagesContexts.add(new PagesContext("invoiceSummaryPage", "Default Invoice Summary","", null, false, true, true)
                .addWebLayout()
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("invoicePdfViewer", null, null)
                .addWidget("invoicePdfViewerWidget", "Summary", PageWidget.WidgetType.PDF_VIEWER, "flexiblewebpdfviewer_19", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("noteandinformation","Notes & Information",PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("invoiceSummaryFields", null, null)
                .addWidget("invoiceSummaryFieldsWidget", "Invoice Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_7", 0, 0, null, getSummaryWidgetDetails(module.getName(), app))
                .widgetDone()
                .sectionDone()
                .addSection("invoicewidgetGroup", null,  null)
                .addWidget("invoicecommentandattachmentwidgetgroupwidget", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0,  null, getWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("related", "Related", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("invoicerelationships", "Relationships", "List of relationships and types between records across modules")
                .addWidget("invoicebulkrelationshipwidget", "Relationships", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET,"flexiblewebbulkrelationshipwidget_6", 0, 0, null, RelationshipWidgetUtil.fetchRelationshipsOfModule(module))
                .widgetDone()
                .sectionDone()
                .addSection("invoicerelatedlist", "Related List", "List of related records across modules")
                .addWidget("invoicebulkrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST,"flexiblewebbulkrelatedlist_6", 0, 0,  null, getFieldRelatedListForModule(module))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("history","History",PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("invoiceHistory",null,null)
                .addWidget("invoiceHistoryWidget", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_4", 0, 0,  historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone());
        return pagesContexts;
    
    }

    private static JSONObject getFieldRelatedListForModule(FacilioModule module) throws Exception {
        List<String>  fieldNames = new ArrayList<>();
        fieldNames.add("childInvoice");
        fieldNames.add("group");
        List<RelatedListWidgetContext> relatedLists = RelatedListWidgetUtil.fetchAllRelatedList(module, false, null, null);
        if(CollectionUtils.isNotEmpty(relatedLists)) {
            for( String fieldName : fieldNames) {
                relatedLists.removeIf(relList -> (fieldName.equalsIgnoreCase(relList.getFieldName())));
            }
            BulkRelatedListContext bulkRelatedListWidget = new BulkRelatedListContext();
            List<RelatedListWidgetContext> relLists = CollectionUtils.isNotEmpty(relatedLists) ? relatedLists : null;
            bulkRelatedListWidget.setRelatedList(relLists);
            return FieldUtil.getAsJSON(bulkRelatedListWidget);
        }
        return null;
    }

    public static JSONObject getWidgetGroup(boolean isMobile) throws Exception {
        JSONObject notesWidgetParam = new JSONObject();
        notesWidgetParam.put("notesModuleName", FacilioConstants.ContextNames.INVOICE_NOTES);
        JSONObject attachmentsWidgetParam = new JSONObject();
        notesWidgetParam.put("attachmentsModuleName", FacilioConstants.ContextNames.INVOICE_ATTACHMENTS);
        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentwidget", "Comment", PageWidget.WidgetType.COMMENT, isMobile?"flexiblemobilecomment_8":"flexiblewebcomment_5", 0, 0, notesWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile?"flexiblemobileattachment_8":"flexiblewebattachment_5", 0, 0, attachmentsWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();
        return FieldUtil.getAsJSON(widgetGroup);
    }

    public static JSONObject getSummaryWidgetDetails(String moduleName, ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);
        FacilioField sysCreatedByField = moduleBean.getField("sysCreatedByPeople", moduleName);
        FacilioField sysCreatedTimeField = moduleBean.getField("sysCreatedTime", moduleName);

        FacilioField sysModifiedByField = moduleBean.getField("sysModifiedByPeople", moduleName);
        FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime", moduleName);
        FacilioField client = moduleBean.getField("client", moduleName);
        FacilioField vendor = moduleBean.getField("vendor", moduleName);

        FacilioField workorder = moduleBean.getField("workorder", moduleName);
        FacilioField description = moduleBean.getField("description", moduleName);
        FacilioField siteId = moduleBean.getField("siteId", moduleName);
        FacilioField invoiceType = moduleBean.getField("invoiceType", moduleName);
        FacilioField tenant = moduleBean.getField("tenant", moduleName);
        FacilioField invoiceNumber = moduleBean.getField("invoiceNumber", moduleName);
        FacilioField quote = moduleBean.getField("quote", moduleName);
        FacilioField purchaseOrder = moduleBean.getField("purchaseOrder", moduleName);
        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();
        addSummaryFieldInWidgetGroup(widgetGroup, invoiceNumber,1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, invoiceType, 1, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, quote,1, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, workorder,1, 4, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, purchaseOrder, 2, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, description, 3, 1, 4);

        SummaryWidgetGroup widgetGroup2 = new SummaryWidgetGroup();
        addSummaryFieldInWidgetGroup(widgetGroup2, vendor, 2, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup2, client, 2, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup2, tenant, 2, 4, 1);

        SummaryWidgetGroup widgetGroup3 = new SummaryWidgetGroup();
        addSummaryFieldInWidgetGroup(widgetGroup3, sysCreatedByField,1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup3, sysCreatedTimeField, 1 , 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup3, sysModifiedByField,1, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup3, sysModifiedTimeField, 1, 4, 1);

        widgetGroup.setName("generalInformation");
        widgetGroup.setDisplayName("General Information");
        widgetGroup.setColumns(4);
        widgetGroup2.setName("stakeholders");
        widgetGroup2.setDisplayName("Stakeholders");
        widgetGroup2.setColumns(4);
        widgetGroup3.setName("systemInformation");
        widgetGroup3.setDisplayName("System Information");
        widgetGroup3.setColumns(4);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);
        widgetGroupList.add(widgetGroup2);
        widgetGroupList.add(widgetGroup3);
        pageWidget.setDisplayName("");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);
        return FieldUtil.getAsJSON(pageWidget);
    }
    private static void addSummaryFieldInWidgetGroup(SummaryWidgetGroup widgetGroup, FacilioField field, int rowIndex, int colIndex, int colSpan) {
        if(field != null) {
            SummaryWidgetGroupFields summaryField = new SummaryWidgetGroupFields();
            summaryField.setName(field.getName());
            summaryField.setDisplayName(field.getDisplayName());
            summaryField.setFieldId(field.getFieldId());
            summaryField.setRowIndex(rowIndex);
            summaryField.setColIndex(colIndex);
            summaryField.setColSpan(colSpan);
            if(widgetGroup.getFields() == null) {
                widgetGroup.setFields(new ArrayList<>(Arrays.asList(summaryField)));
            }
            else {
                widgetGroup.getFields().add(summaryField);
            }
        }
    }

    private static FacilioView getAllInvoices() throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INVOICE);
        FacilioField invoiceStatusField = modBean.getField("invoiceStatus",module.getName());
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(invoiceStatusField.getColumnName(),invoiceStatusField.getName(),String.valueOf(InvoiceContextV3.InvoiceStatus.REVISED.getVal()), NumberOperators.NOT_EQUALS));
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("localId", "LOCAL_ID", FieldType.NUMBER), false));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Invoices");
        allView.setModuleName(module.getName());
        allView.setCriteria(criteria);
        allView.setSortFields(sortFields);

        List<ViewField> viewFields = new ArrayList<>();
        viewFields.add(new ViewField("subject","Subject"));
        viewFields.add(new ViewField("invoiceType","Invoice Type"));
        viewFields.add(new ViewField("invoiceStatus","Invoice Status"));
        viewFields.add(new ViewField("vendor","Vendor"));
        viewFields.add(new ViewField("client","Client"));
        viewFields.add(new ViewField("tenant","Tenant"));
        viewFields.add(new ViewField("billDate","Bill Date"));
        viewFields.add(new ViewField("expiryDate","Expiry Date"));
        viewFields.add(new ViewField("sysCreatedByPeople"," Created By"));
        viewFields.add(new ViewField("sysCreatedTime","Created Time"));
        allView.setFields(viewFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    private static FacilioView getAllVendorInvoices(String name,Boolean forPortal) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INVOICE);
        FacilioField invoiceTypeField = modBean.getField("invoiceType",module.getName());
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(invoiceTypeField.getColumnName(),invoiceTypeField.getName(),String.valueOf(InvoiceContextV3.InvoiceType.VENDOR.getIndex()), NumberOperators.EQUALS));
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("localId", "LOCAL_ID", FieldType.NUMBER), false));

        FacilioView allView = new FacilioView();
        allView.setName(name);
        allView.setDisplayName("Vendor Invoices");
        allView.setModuleName(module.getName());
        allView.setCriteria(criteria);
        allView.setSortFields(sortFields);

        List<ViewField> viewFields = new ArrayList<>();
        viewFields.add(new ViewField("subject","Subject"));
        viewFields.add(new ViewField("invoiceType","Invoice Type"));
        viewFields.add(new ViewField("invoiceStatus","Invoice Status"));
        viewFields.add(new ViewField("vendor","Vendor"));
        viewFields.add(new ViewField("billDate","Bill Date"));
        viewFields.add(new ViewField("expiryDate","Expiry Date"));
        viewFields.add(new ViewField("sysCreatedByPeople"," Created By"));
        viewFields.add(new ViewField("sysCreatedTime","Created Time"));
        allView.setFields(viewFields);

        List<String> appLinkNames = new ArrayList<>();
        if(!forPortal) {
            appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        }
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    private static FacilioView getAllCustomerInvoices(String name,Boolean forPortal) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INVOICE);
        FacilioField invoiceTypeField = modBean.getField("invoiceType",module.getName());
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(invoiceTypeField.getColumnName(),invoiceTypeField.getName(),String.valueOf(InvoiceContextV3.InvoiceType.VENDOR.getIndex()), NumberOperators.NOT_EQUALS));
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("localId", "LOCAL_ID", FieldType.NUMBER), false));

        FacilioView allView = new FacilioView();
        allView.setName(name);
        allView.setDisplayName("Customer Invoices");
        allView.setModuleName(module.getName());
        allView.setCriteria(criteria);
        allView.setSortFields(sortFields);

        List<ViewField> viewFields = new ArrayList<>();
        viewFields.add(new ViewField("subject","Subject"));
        viewFields.add(new ViewField("invoiceType","Invoice Type"));
        viewFields.add(new ViewField("invoiceStatus","Invoice Status"));
        viewFields.add(new ViewField("client","Client"));
        viewFields.add(new ViewField("tenant","Tenant"));
        viewFields.add(new ViewField("billDate","Bill Date"));
        viewFields.add(new ViewField("expiryDate","Expiry Date"));
        viewFields.add(new ViewField("sysCreatedByPeople"," Created By"));
        viewFields.add(new ViewField("sysCreatedTime","Created Time"));
        allView.setFields(viewFields);

        List<String> appLinkNames = new ArrayList<>();
        if(!forPortal) {
            appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        }
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    private static FacilioView getVendorInvoicesForApprovalView(String viewName,Boolean forPortal) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INVOICE);
        FacilioField invoiceTypeField = modBean.getField("invoiceType",module.getName());
        FacilioField invoiceStatusField = modBean.getField("invoiceStatus",module.getName());
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("localId", "LOCAL_ID", FieldType.NUMBER), false));
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(invoiceTypeField.getColumnName(),invoiceTypeField.getName(),String.valueOf(InvoiceContextV3.InvoiceType.VENDOR.getIndex()), NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition(invoiceStatusField.getColumnName(),invoiceStatusField.getName(),String.valueOf(InvoiceContextV3.InvoiceStatus.DELIVERED.getIndex()), NumberOperators.EQUALS));

        FacilioView allView = new FacilioView();
        allView.setName(viewName);
        allView.setDisplayName("Invoices Waiting for Approval");
        allView.setModuleName(module.getName());
        allView.setCriteria(criteria);
        allView.setSortFields(sortFields);

        List<ViewField> viewFields = new ArrayList<>();
        viewFields.add(new ViewField("subject","Subject"));
        viewFields.add(new ViewField("invoiceType","Invoice Type"));
        viewFields.add(new ViewField("invoiceStatus","Invoice Status"));
        viewFields.add(new ViewField("client","Client"));
        viewFields.add(new ViewField("tenant","Tenant"));
        viewFields.add(new ViewField("vendor","Vendor"));
        viewFields.add(new ViewField("billDate","Bill Date"));
        viewFields.add(new ViewField("expiryDate","Expiry Date"));
        viewFields.add(new ViewField("sysCreatedByPeople"," Created By"));
        viewFields.add(new ViewField("sysCreatedTime","Created Time"));
        allView.setFields(viewFields);

        List<String> appLinkNames = new ArrayList<>();
        if(!forPortal) {
            appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        }
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    private static FacilioView getClientInvoicesForApprovalView(String viewName,Boolean forPortal) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INVOICE);
        FacilioField invoiceTypeField = modBean.getField("invoiceType",module.getName());
        FacilioField invoiceStatusField = modBean.getField("invoiceStatus",module.getName());
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("localId", "LOCAL_ID", FieldType.NUMBER), false));
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(invoiceTypeField.getColumnName(),invoiceTypeField.getName(),String.valueOf(InvoiceContextV3.InvoiceType.VENDOR.getIndex()), NumberOperators.NOT_EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition(invoiceStatusField.getColumnName(),invoiceStatusField.getName(),String.valueOf(InvoiceContextV3.InvoiceStatus.DELIVERED.getIndex()), NumberOperators.EQUALS));

        FacilioView allView = new FacilioView();
        allView.setName(viewName);
        allView.setDisplayName("Invoices Waiting for Approval");
        allView.setModuleName(module.getName());
        allView.setCriteria(criteria);
        allView.setSortFields(sortFields);

        List<ViewField> viewFields = new ArrayList<>();
        viewFields.add(new ViewField("subject","Subject"));
        viewFields.add(new ViewField("invoiceType","Invoice Type"));
        viewFields.add(new ViewField("invoiceStatus","Invoice Status"));
        viewFields.add(new ViewField("client","Client"));
        viewFields.add(new ViewField("tenant","Tenant"));
        viewFields.add(new ViewField("billDate","Bill Date"));
        viewFields.add(new ViewField("sysCreatedByPeople"," Created By"));
        viewFields.add(new ViewField("sysCreatedTime","Created Time"));
        allView.setFields(viewFields);

        List<String> appLinkNames = new ArrayList<>();
        if(!forPortal) {
            appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        }
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    private static FacilioView getVendorInvoicesApprovedView(String viewName,Boolean forPortal) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INVOICE);
        FacilioField invoiceTypeField = modBean.getField("invoiceType",module.getName());
        FacilioField invoiceStatusField = modBean.getField("invoiceStatus",module.getName());
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("localId", "LOCAL_ID", FieldType.NUMBER), false));
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(invoiceTypeField.getColumnName(),invoiceTypeField.getName(),String.valueOf(InvoiceContextV3.InvoiceType.VENDOR.getIndex()), NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition(invoiceStatusField.getColumnName(),invoiceStatusField.getName(),String.valueOf(InvoiceContextV3.InvoiceStatus.APPROVED.getIndex()), NumberOperators.EQUALS));

        FacilioView allView = new FacilioView();
        allView.setName(viewName);
        allView.setDisplayName("Invoices Approved");
        allView.setModuleName(module.getName());
        allView.setCriteria(criteria);
        allView.setSortFields(sortFields);

        List<ViewField> viewFields = new ArrayList<>();
        viewFields.add(new ViewField("subject","Subject"));
        viewFields.add(new ViewField("invoiceType","Invoice Type"));
        viewFields.add(new ViewField("invoiceStatus","Invoice Status"));
        viewFields.add(new ViewField("vendor","Vendor"));
        viewFields.add(new ViewField("billDate","Bill Date"));
        viewFields.add(new ViewField("expiryDate","Expiry Date"));
        viewFields.add(new ViewField("sysCreatedByPeople"," Created By"));
        viewFields.add(new ViewField("sysCreatedTime","Created Time"));
        allView.setFields(viewFields);

        List<String> appLinkNames = new ArrayList<>();
        if(!forPortal) {
            appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        }
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    private static FacilioView getClientInvoicesApprovedView(String viewName,Boolean forPortal) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INVOICE);
        FacilioField invoiceTypeField = modBean.getField("invoiceType",module.getName());
        FacilioField invoiceStatusField = modBean.getField("invoiceStatus",module.getName());
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("localId", "LOCAL_ID", FieldType.NUMBER), false));
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(invoiceTypeField.getColumnName(),invoiceTypeField.getName(),String.valueOf(InvoiceContextV3.InvoiceType.VENDOR.getIndex()), NumberOperators.NOT_EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition(invoiceStatusField.getColumnName(),invoiceStatusField.getName(),String.valueOf(InvoiceContextV3.InvoiceStatus.APPROVED.getIndex()), NumberOperators.EQUALS));

        FacilioView allView = new FacilioView();
        allView.setName(viewName);
        allView.setDisplayName("Invoices Approved");
        allView.setModuleName(module.getName());
        allView.setCriteria(criteria);
        allView.setSortFields(sortFields);

        List<ViewField> viewFields = new ArrayList<>();
        viewFields.add(new ViewField("subject","Subject"));
        viewFields.add(new ViewField("invoiceType","Invoice Type"));
        viewFields.add(new ViewField("invoiceStatus","Invoice Status"));
        viewFields.add(new ViewField("client","Client"));
        viewFields.add(new ViewField("tenant","Tenant"));
        viewFields.add(new ViewField("billDate","Bill Date"));
        viewFields.add(new ViewField("sysCreatedByPeople"," Created By"));
        viewFields.add(new ViewField("sysCreatedTime","Created Time"));
        allView.setFields(viewFields);

        List<String> appLinkNames = new ArrayList<>();
        if(!forPortal) {
            appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        }
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    private static FacilioView getVendorInvoicesRejectedView(String viewName,Boolean forPortal) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INVOICE);
        FacilioField invoiceTypeField = modBean.getField("invoiceType",module.getName());
        FacilioField invoiceStatusField = modBean.getField("invoiceStatus",module.getName());
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("localId", "LOCAL_ID", FieldType.NUMBER), false));
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(invoiceTypeField.getColumnName(),invoiceTypeField.getName(),String.valueOf(InvoiceContextV3.InvoiceType.VENDOR.getIndex()), NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition(invoiceStatusField.getColumnName(),invoiceStatusField.getName(),String.valueOf(InvoiceContextV3.InvoiceStatus.REJECTED.getIndex()), NumberOperators.EQUALS));

        FacilioView allView = new FacilioView();
        allView.setName(viewName);
        allView.setDisplayName("Invoices Rejected");
        allView.setModuleName(module.getName());
        allView.setCriteria(criteria);
        allView.setSortFields(sortFields);

        List<ViewField> viewFields = new ArrayList<>();
        viewFields.add(new ViewField("subject","Subject"));
        viewFields.add(new ViewField("invoiceType","Invoice Type"));
        viewFields.add(new ViewField("invoiceStatus","Invoice Status"));
        viewFields.add(new ViewField("vendor","Vendor"));
        viewFields.add(new ViewField("billDate","Bill Date"));
        viewFields.add(new ViewField("sysCreatedByPeople"," Created By"));
        viewFields.add(new ViewField("sysCreatedTime","Created Time"));
        viewFields.add(new ViewField("sysModifiedByPeople","Modified By"));
        viewFields.add(new ViewField("sysModifiedTime","Modified Time"));

        allView.setFields(viewFields);

        List<String> appLinkNames = new ArrayList<>();
        if(!forPortal) {
            appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        }
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    private static FacilioView getClientInvoicesRejectedView(String viewName,Boolean forPortal) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INVOICE);
        FacilioField invoiceTypeField = modBean.getField("invoiceType",module.getName());
        FacilioField invoiceStatusField = modBean.getField("invoiceStatus",module.getName());
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("localId", "LOCAL_ID", FieldType.NUMBER), false));
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(invoiceTypeField.getColumnName(),invoiceTypeField.getName(),String.valueOf(InvoiceContextV3.InvoiceType.VENDOR.getIndex()), NumberOperators.NOT_EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition(invoiceStatusField.getColumnName(),invoiceStatusField.getName(),String.valueOf(InvoiceContextV3.InvoiceStatus.REJECTED.getIndex()), NumberOperators.EQUALS));

        FacilioView allView = new FacilioView();
        allView.setName(viewName);
        allView.setDisplayName("Invoices Rejected");
        allView.setModuleName(module.getName());
        allView.setCriteria(criteria);
        allView.setSortFields(sortFields);

        List<ViewField> viewFields = new ArrayList<>();
        viewFields.add(new ViewField("subject","Subject"));
        viewFields.add(new ViewField("invoiceType","Invoice Type"));
        viewFields.add(new ViewField("invoiceStatus","Invoice Status"));
        viewFields.add(new ViewField("client","Client"));
        viewFields.add(new ViewField("tenant","Tenant"));
        viewFields.add(new ViewField("billDate","Bill Date"));
        viewFields.add(new ViewField("sysCreatedByPeople"," Created By"));
        viewFields.add(new ViewField("sysCreatedTime","Created Time"));
        viewFields.add(new ViewField("sysModifiedByPeople","Modified By"));
        viewFields.add(new ViewField("sysModifiedTime","Modified Time"));
        allView.setFields(viewFields);

        List<String> appLinkNames = new ArrayList<>();
        if(!forPortal) {
            appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        }
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    private static FacilioView getVendorInvoicesCanceledView(String viewName,Boolean forPortal) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INVOICE);
        FacilioField invoiceTypeField = modBean.getField("invoiceType",module.getName());
        FacilioField invoiceStatusField = modBean.getField("invoiceStatus",module.getName());
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("localId", "LOCAL_ID", FieldType.NUMBER), false));
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(invoiceTypeField.getColumnName(),invoiceTypeField.getName(),String.valueOf(InvoiceContextV3.InvoiceType.VENDOR.getIndex()), NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition(invoiceStatusField.getColumnName(),invoiceStatusField.getName(),String.valueOf(InvoiceContextV3.InvoiceStatus.CANCELLED.getIndex()), NumberOperators.EQUALS));

        FacilioView allView = new FacilioView();
        allView.setName(viewName);
        allView.setDisplayName("Invoices Canceled");
        allView.setModuleName(module.getName());
        allView.setCriteria(criteria);
        allView.setSortFields(sortFields);

        List<ViewField> viewFields = new ArrayList<>();
        viewFields.add(new ViewField("subject","Subject"));
        viewFields.add(new ViewField("invoiceType","Invoice Type"));
        viewFields.add(new ViewField("invoiceStatus","Invoice Status"));
        viewFields.add(new ViewField("vendor","Vendor"));
        viewFields.add(new ViewField("billDate","Bill Date"));
        viewFields.add(new ViewField("sysCreatedByPeople"," Created By"));
        viewFields.add(new ViewField("sysCreatedTime","Created Time"));
        viewFields.add(new ViewField("sysModifiedByPeople","Modified By"));
        viewFields.add(new ViewField("sysModifiedTime","Modified Time"));
        allView.setFields(viewFields);

        List<String> appLinkNames = new ArrayList<>();
        if(!forPortal) {
            appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        }
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    private static FacilioView getClientInvoicesCanceledView(String viewName,Boolean forPortal) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INVOICE);
        FacilioField invoiceTypeField = modBean.getField("invoiceType",module.getName());
        FacilioField invoiceStatusField = modBean.getField("invoiceStatus",module.getName());
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("localId", "LOCAL_ID", FieldType.NUMBER), false));
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(invoiceTypeField.getColumnName(),invoiceTypeField.getName(),String.valueOf(InvoiceContextV3.InvoiceType.VENDOR.getIndex()), NumberOperators.NOT_EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition(invoiceStatusField.getColumnName(),invoiceStatusField.getName(),String.valueOf(InvoiceContextV3.InvoiceStatus.CANCELLED.getIndex()), NumberOperators.EQUALS));

        FacilioView allView = new FacilioView();
        allView.setName(viewName);
        allView.setDisplayName("Invoices Canceled");
        allView.setModuleName(module.getName());
        allView.setCriteria(criteria);
        allView.setSortFields(sortFields);

        List<ViewField> viewFields = new ArrayList<>();
        viewFields.add(new ViewField("subject","Subject"));
        viewFields.add(new ViewField("invoiceType","Invoice Type"));
        viewFields.add(new ViewField("invoiceStatus","Invoice Status"));
        viewFields.add(new ViewField("client","Client"));
        viewFields.add(new ViewField("tenant","Tenant"));
        viewFields.add(new ViewField("billDate","Bill Date"));
        viewFields.add(new ViewField("sysCreatedByPeople","Created By"));
        viewFields.add(new ViewField("sysCreatedTime","Created Time"));
        viewFields.add(new ViewField("sysModifiedByPeople","Modified By"));
        viewFields.add(new ViewField("sysModifiedTime","Modified Time"));
        allView.setFields(viewFields);

        List<String> appLinkNames = new ArrayList<>();
        if(!forPortal) {
            appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        }
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule invoiceModule = modBean.getModule(FacilioConstants.ContextNames.INVOICE);

        List<FormField> invoiceFormFields = new ArrayList<>();
        invoiceFormFields.add(new FormField("subject", FacilioField.FieldDisplayType.TEXTBOX, "Subject", FormField.Required.REQUIRED, 1, 1));
        invoiceFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        invoiceFormFields.add(new FormField("billDate", FacilioField.FieldDisplayType.DATE, "Bill Date", FormField.Required.OPTIONAL, 3, 2));
        invoiceFormFields.add(new FormField("expiryDate", FacilioField.FieldDisplayType.DATE, "Expiry Date", FormField.Required.OPTIONAL, 3, 3));
        invoiceFormFields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.OPTIONAL,"site", 4, 2));
        invoiceFormFields.add(new FormField("invoiceType", FacilioField.FieldDisplayType.SELECTBOX, "Invoice Type", FormField.Required.REQUIRED, 5, 3));
        FormField vendorField = new FormField("vendor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Vendor", FormField.Required.OPTIONAL,"vendor", 6, 3);
        vendorField.setHideField(true);
        invoiceFormFields.add(vendorField);

        FormField tenantField = new FormField("tenant", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Tenant", FormField.Required.OPTIONAL,"tenant", 7, 3);
        tenantField.setHideField(true);
        invoiceFormFields.add(tenantField);

        FormField clientField = new FormField("client", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Client", FormField.Required.OPTIONAL,"client", 8, 3);
        clientField.setHideField(true);
        invoiceFormFields.add(clientField);

        FormField workOrderField = new FormField("workorder", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Workorder", FormField.Required.OPTIONAL,"workorder", 9, 1);
        workOrderField.setHideField(true);
        invoiceFormFields.add(workOrderField);


        FormField purchaseOrderField = new FormField("purchaseOrder", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Purchase Order", FormField.Required.OPTIONAL,"purchaseOrder", 11, 1);
        purchaseOrderField.setHideField(true);
        invoiceFormFields.add(purchaseOrderField);

        FormField quoteField = new FormField("quote", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Quote", FormField.Required.OPTIONAL,"quote", 12, 1);
        quoteField.setHideField(true);
        invoiceFormFields.add(quoteField);

        FormField approversField = new FormField("approvers", FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE, "Approvers", FormField.Required.OPTIONAL, 13, 1);
        invoiceFormFields.add(approversField);

        invoiceFormFields.add(new FormField("signature", FacilioField.FieldDisplayType.SIGNATURE, "Signature", FormField.Required.OPTIONAL, 14, 1));

        List<FormField> billingAddressFields = new ArrayList<>();
        billingAddressFields.add(new FormField("billToAddress", FacilioField.FieldDisplayType.SADDRESS, "Bill To Address", FormField.Required.OPTIONAL, 1, 1));

        List<FormField> shippingAddressFields = new ArrayList<>();
        shippingAddressFields.add(new FormField("shipToAddress", FacilioField.FieldDisplayType.SADDRESS, "Shipping Address", FormField.Required.OPTIONAL, 1, 1));

        List<FormField> lineItemFields = new ArrayList<>();
        FormField lineItemField  =new FormField("lineItems", FacilioField.FieldDisplayType.QUOTE_LINE_ITEMS, "Line Items", FormField.Required.REQUIRED, 1, 1);
        lineItemField.addToConfig("hideTaxField",false);
        lineItemFields.add(lineItemField);

        List<FormField> signatureFields = new ArrayList<>();
        signatureFields.add(new FormField("notes", FacilioField.FieldDisplayType.TEXTAREA, "Customer Notes", FormField.Required.OPTIONAL, 15, 1));


        FormSection defaultSection = new FormSection("Invoice Information", 1, invoiceFormFields, true);
        defaultSection.setSectionType(FormSection.SectionType.FIELDS);

        FormSection createDefaultSection = new FormSection("Information", 1, getCreateFormFieldSection(), true);
        createDefaultSection.setSectionType(FormSection.SectionType.FIELDS);


        FormSection billingSection = new FormSection("Billing Address", 2, billingAddressFields, true);
        billingSection.setSectionType(FormSection.SectionType.FIELDS);

        FormSection shippingAddressSection = new FormSection("Shipping Address", 3, shippingAddressFields, true);
        shippingAddressSection.setSectionType(FormSection.SectionType.FIELDS);

        FormSection lineItemSection = new FormSection("Invoice Items", 4, lineItemFields, true);
        lineItemSection.setSectionType(FormSection.SectionType.FIELDS);

        FormSection notesSection = new FormSection("Notes", 5, signatureFields, false);
        notesSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormSection> sections = new ArrayList<>();
        sections.add(defaultSection);
        sections.add(billingSection);
        sections.add(shippingAddressSection);
        sections.add(lineItemSection);
        sections.add(notesSection);

        List<FormSection> createSections = new ArrayList<>();
        createSections.add(createDefaultSection);
        createSections.add(billingSection);
        createSections.add(shippingAddressSection);
        createSections.add(lineItemSection);
        createSections.add(notesSection);


        FacilioForm conversionInvoiceForm = new FacilioForm();
        conversionInvoiceForm.setDisplayName("System Invoice Form");
        conversionInvoiceForm.setName("default_system_invoice_web");
        conversionInvoiceForm.setModule(invoiceModule);
        conversionInvoiceForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        conversionInvoiceForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP,FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP,FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP,FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP));
        conversionInvoiceForm.setSections(sections);
        conversionInvoiceForm.setIsSystemForm(true);
        conversionInvoiceForm.setHideInList(true);
        conversionInvoiceForm.setPrimaryForm(true);
        conversionInvoiceForm.setType(FacilioForm.Type.FORM);

        FacilioForm createInvoiceForm = new FacilioForm();
        createInvoiceForm.setDisplayName("Invoice Form");
        createInvoiceForm.setName("default_create_invoice_web");
        createInvoiceForm.setModule(invoiceModule);
        createInvoiceForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        createInvoiceForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP,FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP,FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP,FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP));
        createInvoiceForm.setSections(createSections);
        createInvoiceForm.setIsSystemForm(false);
        createInvoiceForm.setType(FacilioForm.Type.FORM);

        List<FormRuleContext> formRuleContextList = new ArrayList<>();
        formRuleContextList.add(getconversionInvoiceFormRule());
        formRuleContextList.add(getTenantInvoiceFormRule());
        formRuleContextList.add(getClientInvoiceFormRule());
        formRuleContextList.add(getWorkOrderInvoiceFormRule());
        formRuleContextList.add(getPOInvoiceFormRule());
        formRuleContextList.add(getQuoteInvoiceFormRule());

        conversionInvoiceForm.setDefaultFormRules(formRuleContextList);

        List<FacilioForm> invoiceForms = new ArrayList<>();
        invoiceForms.add(conversionInvoiceForm);
        invoiceForms.add(createInvoiceForm);

        return invoiceForms;
    }

    private List<FormField> getCreateFormFieldSection()
    {
        List<FormField> systemCreateFormFields = new ArrayList<>();
        systemCreateFormFields.add(new FormField("subject", FacilioField.FieldDisplayType.TEXTBOX, "Subject", FormField.Required.REQUIRED, 1, 1));
        systemCreateFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        systemCreateFormFields.add(new FormField("billDate", FacilioField.FieldDisplayType.DATE, "Bill Date", FormField.Required.OPTIONAL, 3, 2));
        systemCreateFormFields.add(new FormField("expiryDate", FacilioField.FieldDisplayType.DATE, "Expiry Date", FormField.Required.OPTIONAL, 3, 3));
        systemCreateFormFields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.OPTIONAL,"site", 4, 2));
        systemCreateFormFields.add(new FormField("invoiceType", FacilioField.FieldDisplayType.SELECTBOX, "Invoice Type", FormField.Required.REQUIRED, 5, 3));
        FormField vendorField = new FormField("vendor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Vendor", FormField.Required.OPTIONAL,"vendor", 6, 3);
        systemCreateFormFields.add(vendorField);


        FormField tenantField = new FormField("tenant", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Tenant", FormField.Required.OPTIONAL,"tenant", 7, 3);
        systemCreateFormFields.add(tenantField);


        FormField clientField = new FormField("client", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Client", FormField.Required.OPTIONAL,"client", 8, 3);
        systemCreateFormFields.add(clientField);

        FormField workOrderField = new FormField("workorder", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Workorder", FormField.Required.OPTIONAL,"workorder", 9, 1);
        systemCreateFormFields.add(workOrderField);


        FormField purchaseOrderField = new FormField("purchaseOrder", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Purchase Order", FormField.Required.OPTIONAL,"purchaseOrder", 11, 1);
        systemCreateFormFields.add(purchaseOrderField);

        FormField quoteField = new FormField("quote", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Quote", FormField.Required.OPTIONAL,"quote", 12, 1);
        systemCreateFormFields.add(quoteField);

        FormField approversField = new FormField("approvers", FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE, "Approvers", FormField.Required.OPTIONAL, 13, 1);
        systemCreateFormFields.add(approversField);

        systemCreateFormFields.add(new FormField("signature", FacilioField.FieldDisplayType.SIGNATURE, "Signature", FormField.Required.OPTIONAL, 14, 1));
         return systemCreateFormFields;
    }

    private FormRuleContext getconversionInvoiceFormRule()
    {
        FormRuleContext invoiceRule = new FormRuleContext();
        invoiceRule.setName("System Invoice Vendor Field Form Rule");
        invoiceRule.setRuleType(FormRuleContext.RuleType.ACTION.getIntVal());
        invoiceRule.setTriggerType(FormRuleContext.TriggerType.FIELD_UPDATE.getIntVal());
        invoiceRule.setType(FormRuleContext.FormRuleType.FROM_RULE.getIntVal());
        invoiceRule.setAppLinkNamesForRule(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP,FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP,FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP,FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP));

        FormRuleTriggerFieldContext triggerField = new FormRuleTriggerFieldContext();
        triggerField.setFieldName("Invoice Type");
        invoiceRule.setTriggerFields(Collections.singletonList(triggerField));


        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("Invoice.INVOICE_TYPE","invoiceType", String.valueOf(InvoiceContextV3.InvoiceType.VENDOR.getIndex()), EnumOperators.IS));
        invoiceRule.setCriteria(criteria);


        List<FormRuleActionContext> actions = new ArrayList<FormRuleActionContext>();

        FormRuleActionContext disableAction = new FormRuleActionContext();
        disableAction.setActionType(FormActionType.DISABLE_FIELD.getVal());
        List<FormRuleActionFieldsContext> disableActionFields = new ArrayList<>();
        FormRuleActionFieldsContext disableActionField = new FormRuleActionFieldsContext();
        disableActionField.setFormFieldName("Invoice Type");
        disableActionFields.add(disableActionField);
        disableAction.setFormRuleActionFieldsContext(disableActionFields);

        FormRuleActionContext showVendorAction = new FormRuleActionContext();
        showVendorAction.setActionType(FormActionType.SHOW_FIELD.getVal());
        List<FormRuleActionFieldsContext> showVendorActionFields = new ArrayList<>();
        FormRuleActionFieldsContext showVendorActionField = new FormRuleActionFieldsContext();
        showVendorActionField.setFormFieldName("Vendor");
        showVendorActionFields.add(showVendorActionField);
        showVendorAction.setFormRuleActionFieldsContext(showVendorActionFields);


        FormRuleActionContext mandatoryVendorAction = new FormRuleActionContext();
        mandatoryVendorAction.setActionType(FormActionType.SET_MANDATORY.getVal());
        List<FormRuleActionFieldsContext> mandatoryVendorActionFields = new ArrayList<>();
        FormRuleActionFieldsContext mandatoryVendorActionField = new FormRuleActionFieldsContext();
        mandatoryVendorActionField.setFormFieldName("Vendor");
        mandatoryVendorActionFields.add(mandatoryVendorActionField);
        mandatoryVendorAction.setFormRuleActionFieldsContext(mandatoryVendorActionFields);

        actions.add(disableAction);
        actions.add(showVendorAction);
        actions.add(mandatoryVendorAction);

        invoiceRule.setActions(actions);

        return invoiceRule;
    }

    private FormRuleContext getTenantInvoiceFormRule()
    {
        FormRuleContext invoiceRule = new FormRuleContext();
        invoiceRule.setName("System Invoice Tenant Field Form Rule");
        invoiceRule.setRuleType(FormRuleContext.RuleType.ACTION.getIntVal());
        invoiceRule.setTriggerType(FormRuleContext.TriggerType.FIELD_UPDATE.getIntVal());
        invoiceRule.setType(FormRuleContext.FormRuleType.FROM_RULE.getIntVal());
        invoiceRule.setAppLinkNamesForRule(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP,FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP,FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP,FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP));

        FormRuleTriggerFieldContext triggerField = new FormRuleTriggerFieldContext();
        triggerField.setFieldName("Invoice Type");
        invoiceRule.setTriggerFields(Collections.singletonList(triggerField));


        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("Invoice.INVOICE_TYPE","invoiceType", String.valueOf(InvoiceContextV3.InvoiceType.TENANT.getIndex()), EnumOperators.IS));
        invoiceRule.setCriteria(criteria);


        List<FormRuleActionContext> actions = new ArrayList<FormRuleActionContext>();

        FormRuleActionContext disableAction = new FormRuleActionContext();
        disableAction.setActionType(FormActionType.DISABLE_FIELD.getVal());
        List<FormRuleActionFieldsContext> disableActionFields = new ArrayList<>();
        FormRuleActionFieldsContext disableActionField = new FormRuleActionFieldsContext();
        disableActionField.setFormFieldName("Invoice Type");
        disableActionFields.add(disableActionField);
        disableAction.setFormRuleActionFieldsContext(disableActionFields);

        FormRuleActionContext showTenantAction = new FormRuleActionContext();
        showTenantAction.setActionType(FormActionType.SHOW_FIELD.getVal());
        List<FormRuleActionFieldsContext> showTenantActionFields = new ArrayList<>();
        FormRuleActionFieldsContext showTenantActionField = new FormRuleActionFieldsContext();
        showTenantActionField.setFormFieldName("Tenant");
        showTenantActionFields.add(showTenantActionField);
        showTenantAction.setFormRuleActionFieldsContext(showTenantActionFields);

        FormRuleActionContext mandatoryTenantAction = new FormRuleActionContext();
        mandatoryTenantAction.setActionType(FormActionType.SET_MANDATORY.getVal());
        List<FormRuleActionFieldsContext> mandatoryTenantActionFields = new ArrayList<>();
        FormRuleActionFieldsContext mandatoryTenantActionField = new FormRuleActionFieldsContext();
        mandatoryTenantActionField.setFormFieldName("Tenant");
        mandatoryTenantActionFields.add(mandatoryTenantActionField);
        mandatoryTenantAction.setFormRuleActionFieldsContext(mandatoryTenantActionFields);

        actions.add(disableAction);
        actions.add(showTenantAction);
        actions.add(mandatoryTenantAction);

        invoiceRule.setActions(actions);

        return invoiceRule;
    }

    private FormRuleContext getClientInvoiceFormRule()
    {
        FormRuleContext invoiceRule = new FormRuleContext();
        invoiceRule.setName("System Invoice Client Field Form Rule");
        invoiceRule.setRuleType(FormRuleContext.RuleType.ACTION.getIntVal());
        invoiceRule.setTriggerType(FormRuleContext.TriggerType.FIELD_UPDATE.getIntVal());
        invoiceRule.setType(FormRuleContext.FormRuleType.FROM_RULE.getIntVal());
        invoiceRule.setAppLinkNamesForRule(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP,FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP,FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP,FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP));

        FormRuleTriggerFieldContext triggerField = new FormRuleTriggerFieldContext();
        triggerField.setFieldName("Invoice Type");
        invoiceRule.setTriggerFields(Collections.singletonList(triggerField));


        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("Invoice.INVOICE_TYPE","invoiceType", String.valueOf(InvoiceContextV3.InvoiceType.CLIENT.getIndex()), EnumOperators.IS));
        invoiceRule.setCriteria(criteria);


        List<FormRuleActionContext> actions = new ArrayList<FormRuleActionContext>();

        FormRuleActionContext disableAction = new FormRuleActionContext();
        disableAction.setActionType(FormActionType.DISABLE_FIELD.getVal());
        List<FormRuleActionFieldsContext> disableActionFields = new ArrayList<>();
        FormRuleActionFieldsContext disableActionField = new FormRuleActionFieldsContext();
        disableActionField.setFormFieldName("Invoice Type");
        disableActionFields.add(disableActionField);
        disableAction.setFormRuleActionFieldsContext(disableActionFields);

        FormRuleActionContext showClientAction = new FormRuleActionContext();
        showClientAction.setActionType(FormActionType.SHOW_FIELD.getVal());
        List<FormRuleActionFieldsContext> showClientActionFields = new ArrayList<>();
        FormRuleActionFieldsContext showClientActionField = new FormRuleActionFieldsContext();
        showClientActionField.setFormFieldName("Client");
        showClientActionFields.add(showClientActionField);
        showClientAction.setFormRuleActionFieldsContext(showClientActionFields);

        FormRuleActionContext mandatoryClientAction = new FormRuleActionContext();
        mandatoryClientAction.setActionType(FormActionType.SET_MANDATORY.getVal());
        List<FormRuleActionFieldsContext> mandatoryClientActionFields = new ArrayList<>();
        FormRuleActionFieldsContext mandatoryClientActionField = new FormRuleActionFieldsContext();
        mandatoryClientActionField.setFormFieldName("Client");
        mandatoryClientActionFields.add(mandatoryClientActionField);
        mandatoryClientAction.setFormRuleActionFieldsContext(mandatoryClientActionFields);

        actions.add(disableAction);
        actions.add(showClientAction);
        actions.add(mandatoryClientAction);

        invoiceRule.setActions(actions);

        return invoiceRule;
    }

    private FormRuleContext getWorkOrderInvoiceFormRule()
    {
        FormRuleContext invoiceRule = new FormRuleContext();
        invoiceRule.setName("System Invoice WorkOrder Field Form Rule");
        invoiceRule.setRuleType(FormRuleContext.RuleType.ACTION.getIntVal());
        invoiceRule.setTriggerType(FormRuleContext.TriggerType.FORM_ON_LOAD.getIntVal());
        invoiceRule.setType(FormRuleContext.FormRuleType.FROM_RULE.getIntVal());
        invoiceRule.setAppLinkNamesForRule(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP,FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP,FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP,FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP));

        FormRuleTriggerFieldContext triggerField = new FormRuleTriggerFieldContext();
        triggerField.setFieldName("Workorder");
        invoiceRule.setTriggerFields(Collections.singletonList(triggerField));


        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("Invoice.WORKORDER_ID","workorder",null,CommonOperators.IS_NOT_EMPTY));
        invoiceRule.setCriteria(criteria);


        List<FormRuleActionContext> actions = new ArrayList<FormRuleActionContext>();


        FormRuleActionContext showWorkorderAction = new FormRuleActionContext();
        showWorkorderAction.setActionType(FormActionType.SHOW_FIELD.getVal());
        List<FormRuleActionFieldsContext> showWorkorderActionFields = new ArrayList<>();
        FormRuleActionFieldsContext showWorkorderActionField = new FormRuleActionFieldsContext();
        showWorkorderActionField.setFormFieldName("Workorder");
        showWorkorderActionFields.add(showWorkorderActionField);
        showWorkorderAction.setFormRuleActionFieldsContext(showWorkorderActionFields);

        FormRuleActionContext mandatoryWorkorderAction = new FormRuleActionContext();
        mandatoryWorkorderAction.setActionType(FormActionType.SET_MANDATORY.getVal());
        List<FormRuleActionFieldsContext> mandatoryWorkorderActionFields = new ArrayList<>();
        FormRuleActionFieldsContext mandatoryWorkorderActionField = new FormRuleActionFieldsContext();
        mandatoryWorkorderActionField.setFormFieldName("Workorder");
        mandatoryWorkorderActionFields.add(mandatoryWorkorderActionField);
        mandatoryWorkorderAction.setFormRuleActionFieldsContext(mandatoryWorkorderActionFields);

        FormRuleActionContext disableAction = new FormRuleActionContext();
        disableAction.setActionType(FormActionType.DISABLE_FIELD.getVal());
        List<FormRuleActionFieldsContext> disableActionFields = new ArrayList<>();
        FormRuleActionFieldsContext disableActionField = new FormRuleActionFieldsContext();
        disableActionField.setFormFieldName("Workorder");
        disableActionFields.add(disableActionField);
        disableAction.setFormRuleActionFieldsContext(disableActionFields);


        actions.add(showWorkorderAction);
        actions.add(mandatoryWorkorderAction);
        actions.add(disableAction);

        invoiceRule.setActions(actions);

        return invoiceRule;
    }

    private FormRuleContext getServiceRequestInvoiceFormRule()
    {
        FormRuleContext invoiceRule = new FormRuleContext();
        invoiceRule.setName("System Invoice Service Request Field Form Rule");
        invoiceRule.setRuleType(FormRuleContext.RuleType.ACTION.getIntVal());
        invoiceRule.setTriggerType(FormRuleContext.TriggerType.FORM_ON_LOAD.getIntVal());
        invoiceRule.setType(FormRuleContext.FormRuleType.FROM_RULE.getIntVal());
        invoiceRule.setAppLinkNamesForRule(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP,FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP,FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP,FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP));

        FormRuleTriggerFieldContext triggerField = new FormRuleTriggerFieldContext();
        triggerField.setFieldName("Service Request");
        invoiceRule.setTriggerFields(Collections.singletonList(triggerField));


        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("Invoice.SERVICE_REQUEST_ID","serviceRequest",null,CommonOperators.IS_NOT_EMPTY));
        invoiceRule.setCriteria(criteria);


        List<FormRuleActionContext> actions = new ArrayList<FormRuleActionContext>();


        FormRuleActionContext showServiceRequestAction = new FormRuleActionContext();
        showServiceRequestAction.setActionType(FormActionType.SHOW_FIELD.getVal());
        List<FormRuleActionFieldsContext> showServiceRequestActionFields = new ArrayList<>();
        FormRuleActionFieldsContext showServiceRequestActionField = new FormRuleActionFieldsContext();
        showServiceRequestActionField.setFormFieldName("Service Request");
        showServiceRequestActionFields.add(showServiceRequestActionField);
        showServiceRequestAction.setFormRuleActionFieldsContext(showServiceRequestActionFields);

        FormRuleActionContext mandatoryServiceRequestAction = new FormRuleActionContext();
        mandatoryServiceRequestAction.setActionType(FormActionType.SET_MANDATORY.getVal());
        List<FormRuleActionFieldsContext> mandatoryServiceRequestActionFields = new ArrayList<>();
        FormRuleActionFieldsContext mandatoryServiceRequestActionField = new FormRuleActionFieldsContext();
        mandatoryServiceRequestActionField.setFormFieldName("Service Request");
        mandatoryServiceRequestActionFields.add(mandatoryServiceRequestActionField);
        mandatoryServiceRequestAction.setFormRuleActionFieldsContext(mandatoryServiceRequestActionFields);

        FormRuleActionContext disableAction = new FormRuleActionContext();
        disableAction.setActionType(FormActionType.DISABLE_FIELD.getVal());
        List<FormRuleActionFieldsContext> disableActionFields = new ArrayList<>();
        FormRuleActionFieldsContext disableActionField = new FormRuleActionFieldsContext();
        disableActionField.setFormFieldName("Service Request");
        disableActionFields.add(disableActionField);
        disableAction.setFormRuleActionFieldsContext(disableActionFields);


        actions.add(showServiceRequestAction);
        actions.add(mandatoryServiceRequestAction);
        actions.add(disableAction);

        invoiceRule.setActions(actions);

        return invoiceRule;
    }

    private FormRuleContext getPOInvoiceFormRule()
    {
        FormRuleContext invoiceRule = new FormRuleContext();
        invoiceRule.setName("System Invoice PO Field Form Rule");
        invoiceRule.setRuleType(FormRuleContext.RuleType.ACTION.getIntVal());
        invoiceRule.setTriggerType(FormRuleContext.TriggerType.FORM_ON_LOAD.getIntVal());
        invoiceRule.setType(FormRuleContext.FormRuleType.FROM_RULE.getIntVal());
        invoiceRule.setAppLinkNamesForRule(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP,FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP,FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP,FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP));

        FormRuleTriggerFieldContext triggerField = new FormRuleTriggerFieldContext();
        triggerField.setFieldName("Purchase Order");
        invoiceRule.setTriggerFields(Collections.singletonList(triggerField));


        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("Invoice.PURCHASE_ORDER_ID","purchaseOrder",null,CommonOperators.IS_NOT_EMPTY));
        invoiceRule.setCriteria(criteria);


        List<FormRuleActionContext> actions = new ArrayList<FormRuleActionContext>();


        FormRuleActionContext showPOAction = new FormRuleActionContext();
        showPOAction.setActionType(FormActionType.SHOW_FIELD.getVal());
        List<FormRuleActionFieldsContext> showPOActionFields = new ArrayList<>();
        FormRuleActionFieldsContext showPOActionField = new FormRuleActionFieldsContext();
        showPOActionField.setFormFieldName("Purchase Order");
        showPOActionFields.add(showPOActionField);
        showPOAction.setFormRuleActionFieldsContext(showPOActionFields);

        FormRuleActionContext mandatoryPOAction = new FormRuleActionContext();
        mandatoryPOAction.setActionType(FormActionType.SET_MANDATORY.getVal());
        List<FormRuleActionFieldsContext> mandatoryPOActionFields = new ArrayList<>();
        FormRuleActionFieldsContext mandatoryPOActionField = new FormRuleActionFieldsContext();
        mandatoryPOActionField.setFormFieldName("Purchase Order");
        mandatoryPOActionFields.add(mandatoryPOActionField);
        mandatoryPOAction.setFormRuleActionFieldsContext(mandatoryPOActionFields);

        FormRuleActionContext disableAction = new FormRuleActionContext();
        disableAction.setActionType(FormActionType.DISABLE_FIELD.getVal());
        List<FormRuleActionFieldsContext> disableActionFields = new ArrayList<>();
        FormRuleActionFieldsContext disableActionField = new FormRuleActionFieldsContext();
        disableActionField.setFormFieldName("Purchase Order");
        disableActionFields.add(disableActionField);
        disableAction.setFormRuleActionFieldsContext(disableActionFields);

        actions.add(showPOAction);
        actions.add(mandatoryPOAction);
        actions.add(disableAction);

        invoiceRule.setActions(actions);

        return invoiceRule;
    }

    private FormRuleContext getQuoteInvoiceFormRule()
    {
        FormRuleContext invoiceRule = new FormRuleContext();
        invoiceRule.setName("System Invoice Quote Field Form Rule");
        invoiceRule.setRuleType(FormRuleContext.RuleType.ACTION.getIntVal());
        invoiceRule.setTriggerType(FormRuleContext.TriggerType.FORM_ON_LOAD.getIntVal());
        invoiceRule.setType(FormRuleContext.FormRuleType.FROM_RULE.getIntVal());
        invoiceRule.setAppLinkNamesForRule(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP,FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP,FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP,FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP));

        FormRuleTriggerFieldContext triggerField = new FormRuleTriggerFieldContext();
        triggerField.setFieldName("Quote");
        invoiceRule.setTriggerFields(Collections.singletonList(triggerField));


        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("Invoice.QUOTE_ID","quote",null,CommonOperators.IS_NOT_EMPTY));
        invoiceRule.setCriteria(criteria);


        List<FormRuleActionContext> actions = new ArrayList<FormRuleActionContext>();


        FormRuleActionContext showQuoteAction = new FormRuleActionContext();
        showQuoteAction.setActionType(FormActionType.SHOW_FIELD.getVal());
        List<FormRuleActionFieldsContext> showQuoteActionFields = new ArrayList<>();
        FormRuleActionFieldsContext showQuoteActionField = new FormRuleActionFieldsContext();
        showQuoteActionField.setFormFieldName("Quote");
        showQuoteActionFields.add(showQuoteActionField);
        showQuoteAction.setFormRuleActionFieldsContext(showQuoteActionFields);

        FormRuleActionContext mandatoryQuoteAction = new FormRuleActionContext();
        mandatoryQuoteAction.setActionType(FormActionType.SET_MANDATORY.getVal());
        List<FormRuleActionFieldsContext> mandatoryQuoteActionFields = new ArrayList<>();
        FormRuleActionFieldsContext mandatoryQuoteActionField = new FormRuleActionFieldsContext();
        mandatoryQuoteActionField.setFormFieldName("Quote");
        mandatoryQuoteActionFields.add(mandatoryQuoteActionField);
        mandatoryQuoteAction.setFormRuleActionFieldsContext(mandatoryQuoteActionFields);

        FormRuleActionContext disableAction = new FormRuleActionContext();
        disableAction.setActionType(FormActionType.DISABLE_FIELD.getVal());
        List<FormRuleActionFieldsContext> disableActionFields = new ArrayList<>();
        FormRuleActionFieldsContext disableActionField = new FormRuleActionFieldsContext();
        disableActionField.setFormFieldName("Quote");
        disableActionFields.add(disableActionField);
        disableAction.setFormRuleActionFieldsContext(disableActionFields);

        actions.add(showQuoteAction);
        actions.add(mandatoryQuoteAction);
        actions.add(disableAction);

        invoiceRule.setActions(actions);

        return invoiceRule;
    }

    private void addDefaultApprovals() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INVOICE);

        FacilioField approversField = modBean.getField("approvers",module.getName());
        FacilioField invoiceStatusField = modBean.getField("invoiceStatus",module.getName());

        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        RoleBean roleBean = AccountUtil.getRoleBean();
        long adminRoleId = roleBean.getRole(orgId,"CAFM Administrator").getRoleId();
        long superAdminRoleId = roleBean.getRole(orgId,"CAFM Super Administrator").getRoleId();


        List<FacilioField> fieldList = new ArrayList<>();
        fieldList.add(invoiceStatusField);

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(invoiceStatusField.getColumnName(),invoiceStatusField.getName(),String.valueOf(InvoiceContextV3.InvoiceStatus.DELIVERED.getVal()), EnumOperators.IS));


        //Approve Actions
        List<ActionContext> approveActions = new ArrayList<>();
        ActionContext approveActionContext = new ActionContext();
        approveActionContext.setActionType(ActionType.FIELD_CHANGE);
        JSONObject jsonObject = new JSONObject();

        JSONArray fieldMatcherArray = new JSONArray();

        JSONObject fieldMatcherObject = new JSONObject();
        fieldMatcherObject.put("columnName", "Invoice.INVOICE_STATUS");
        fieldMatcherObject.put("field", "invoiceStatus");
        fieldMatcherObject.put("isSpacePicker", false);
        fieldMatcherObject.put("value", InvoiceContextV3.InvoiceStatus.APPROVED.getIndex().toString());
        fieldMatcherObject.put("fieldObj","[" + FacilioUtil.getAsJSON(invoiceStatusField).toJSONString() + "]");

        fieldMatcherArray.add(fieldMatcherObject);

        jsonObject.put("fieldMatcher", fieldMatcherArray);
        approveActionContext.setTemplateJson(jsonObject);
        approveActions.add(approveActionContext);

        //Reject Actions
        List<ActionContext> rejectActions = new ArrayList<>();
        ActionContext rejectActionContext = new ActionContext();
        rejectActionContext.setActionType(ActionType.FIELD_CHANGE);
        JSONObject rejectJsonObject = new JSONObject();

        JSONArray rejectFieldMatcherArray = new JSONArray();

        JSONObject rejectFieldMatcherObject = new JSONObject();
        rejectFieldMatcherObject.put("columnName", "Invoice.INVOICE_STATUS");
        rejectFieldMatcherObject.put("field", "invoiceStatus");
        rejectFieldMatcherObject.put("isSpacePicker", false);
        rejectFieldMatcherObject.put("value", InvoiceContextV3.InvoiceStatus.REJECTED.getIndex().toString());
        rejectFieldMatcherObject.put("fieldObj", "[" + FacilioUtil.getAsJSON(invoiceStatusField).toJSONString() + "]");

        rejectFieldMatcherArray.add(rejectFieldMatcherObject);

        rejectJsonObject.put("fieldMatcher", rejectFieldMatcherArray);
        rejectActionContext.setTemplateJson(rejectJsonObject);
        rejectActions.add(rejectActionContext);





        ApprovalRuleMetaContext approvalMeta = new ApprovalRuleMetaContext();
        approvalMeta.setName("Approval");
        approvalMeta.setAllApprovalRequired(false);
        approvalMeta.setApprovalDialogType(AbstractStateTransitionRuleContext.DialogType.MODULE);
        approvalMeta.setRejectDialogType(AbstractStateTransitionRuleContext.DialogType.MODULE);
        approvalMeta.setEventType(EventType.FIELD_CHANGE);
        approvalMeta.setFieldIds(Collections.singletonList(invoiceStatusField.getFieldId()));

        //Approver Field Approvals
        SharingContext<ApproverContext> approvers = new SharingContext<>();
        ApproverContext approverContext = new ApproverContext();
        approverContext.setType(SingleSharingContext.SharingType.FIELD);
        approverContext.setFieldId(approversField.getId());
        approvers.add(approverContext);

        //CAFM Super Admin Role Approval
        ApproverContext roleApproverContext = new ApproverContext();
        roleApproverContext.setType(SingleSharingContext.SharingType.ROLE);
        roleApproverContext.setRoleId(superAdminRoleId);
        approvers.add(roleApproverContext);

        //CAFM  Admin Role Approval
        ApproverContext adminRoleApproverContext = new ApproverContext();
        adminRoleApproverContext.setType(SingleSharingContext.SharingType.ROLE);
        adminRoleApproverContext.setRoleId(adminRoleId);
        approvers.add(adminRoleApproverContext);

        approvalMeta.setApprovers(approvers);
//        approvalMeta.setResendApprovers(approvers);
        approvalMeta.setCriteria(criteria);
        approvalMeta.setApproveActions(approveActions);
        approvalMeta.setRejectActions(rejectActions);

        FacilioChain chain = TransactionChainFactory.getAddOrUpdateApprovalRuleChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.APPROVAL_RULE,approvalMeta);
        context.put(FacilioConstants.ContextNames.MODULE_NAME,FacilioConstants.ContextNames.INVOICE);
        chain.setContext(context);
        chain.execute();
    }
    private FacilioModule addInvoiceModule(ModuleBean modBean) throws Exception {

        FacilioModule module = new FacilioModule(FacilioConstants.ContextNames.INVOICE,
                "Invoice",
                "Invoice",
                FacilioModule.ModuleType.BASE_ENTITY
        );


        List<FacilioField> fields = new ArrayList<>();

        FacilioField localIdField = FieldFactory.getDefaultField("localId","Id","LOCAL_ID",FieldType.NUMBER);
        fields.add(localIdField);

        FacilioField invoiceNumber = FieldFactory.getDefaultField("invoiceNumber","Invoice Number","INVOICE_NUMBER",FieldType.STRING);
        fields.add(invoiceNumber);

        FacilioField subjectField = FieldFactory.getDefaultField("subject","Subject","SUBJECT",FieldType.STRING,true);
        fields.add(subjectField);

        FacilioField descriptionField = FieldFactory.getDefaultField("description","Description","DESCRIPTION",FieldType.STRING);
        fields.add(descriptionField);

        FacilioField notesField = FieldFactory.getDefaultField("notes","NOTES","NOTES",FieldType.STRING);
        fields.add(notesField);


        LookupField shipToAddress = (LookupField) FieldFactory.getDefaultField("shipToAddress", "Ship To Address", "SHIP_TO_ADDRESS_ID", FieldType.LOOKUP);
        shipToAddress.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.LOCATION));
        shipToAddress.setDisplayType(FacilioField.FieldDisplayType.ADDRESS);
        fields.add(shipToAddress);

        LookupField billToAddress = (LookupField) FieldFactory.getDefaultField("billToAddress", "Bill To Address", "BILL_TO_ADDRESS_ID", FieldType.LOOKUP);
        billToAddress.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.LOCATION));
        billToAddress.setDisplayType(FacilioField.FieldDisplayType.ADDRESS);
        fields.add(billToAddress);


        SystemEnumField customerType = (SystemEnumField) FieldFactory.getDefaultField("invoiceType", "Invoice Type", "INVOICE_TYPE", FieldType.SYSTEM_ENUM);
        customerType.setEnumName("InvoiceType");
        fields.add(customerType); // need to check if to be added as new

        SystemEnumField InvoiceStatus = (SystemEnumField) FieldFactory.getDefaultField("invoiceStatus", "Invoice Status", "INVOICE_STATUS", FieldType.SYSTEM_ENUM);
        InvoiceStatus.setEnumName("InvoiceStatus");
        fields.add(InvoiceStatus); // need to check if to be added as new

        LookupField tenantField = (LookupField) FieldFactory.getDefaultField("tenant", "Tenant", "TENANT_ID", FieldType.LOOKUP);
        tenantField.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.TENANT));
        fields.add(tenantField);

        LookupField clientField = (LookupField) FieldFactory.getDefaultField("client", "Client", "CLIENT_ID", FieldType.LOOKUP);
        clientField.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.CLIENT));
        fields.add(clientField);


        LookupField workorderField = (LookupField) FieldFactory.getDefaultField("workorder", "Workorder", "WORKORDER_ID", FieldType.LOOKUP);
        workorderField.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER));
        fields.add(workorderField);

        DateField billDateField = (DateField) FieldFactory.getDefaultField("billDate","Bill Date","BILL_DATE",FieldType.DATE);
        fields.add(billDateField);

        DateField expiryDateField = (DateField) FieldFactory.getDefaultField("expiryDate","Expiry Date","EXPIRY_DATE",FieldType.DATE);
        fields.add(expiryDateField);

        FileField signatureField= (FileField) FieldFactory.getDefaultField("signature","Signature","SIGNATURE_ID",FieldType.FILE);
        signatureField.setDisplayType(FacilioField.FieldDisplayType.SIGNATURE);
        fields.add(signatureField); //need to verify for type

        FacilioField subTotalField = FieldFactory.getDefaultField("subTotal","Sub Total","SUB_TOTAL",FieldType.NUMBER);
        fields.add(subTotalField);

        FacilioField totalTaxAmountField = FieldFactory.getDefaultField("totalTaxAmount","Total Tax Amount","TOTAL_TAX_AMOUNT",FieldType.DECIMAL);
        fields.add(totalTaxAmountField);

        FacilioField discountAmountField = FieldFactory.getDefaultField("discountAmount","Discount Amount","DISCOUNT_AMOUNT",FieldType.DECIMAL);
        fields.add(discountAmountField);

        FacilioField discountPercentageField = FieldFactory.getDefaultField("discountPercentage","Discount Percentage","DISCOUNT_PERCENTAGE",FieldType.DECIMAL);
        fields.add(discountPercentageField);

        FacilioField shippingChargesField = FieldFactory.getDefaultField("shippingCharges","Shipping Charges","SHIPPING_CHARGES",FieldType.DECIMAL);
        fields.add(shippingChargesField);

        FacilioField adjustmentsCostField = FieldFactory.getDefaultField("adjustmentsCost","Adjustments Cost","ADJUSTMENTS_COST",FieldType.DECIMAL);
        fields.add(adjustmentsCostField);

        FacilioField adjustmentsCostNameField = FieldFactory.getDefaultField("adjustmentsCostName","Adjustments Cost Name","ADJUSTMENTS_COST_NAME",FieldType.STRING);
        fields.add(adjustmentsCostNameField);

        FacilioField miscellaneousChargesField = FieldFactory.getDefaultField("miscellaneousCharges","Miscellaneous Charges","MISCELLANEOUS_CHARGES",FieldType.NUMBER);
        fields.add(miscellaneousChargesField);

        FacilioField totalCostField = FieldFactory.getDefaultField("totalCost","Total Cost","TOTAL_COST",FieldType.DECIMAL);
        fields.add(totalCostField);


        LookupField approvalStatusField= (LookupField) FieldFactory.getDefaultField("approvalStatus", "Approval State", "APPROVAL_STATE", FieldType.LOOKUP);
        approvalStatusField.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.TICKET_STATUS));
        fields.add(approvalStatusField);

        FacilioField approvalFlowIdField = FieldFactory.getDefaultField("approvalFlowId","Approval Flow Id","APPROVAL_FLOW_ID",FieldType.NUMBER);
        fields.add(approvalFlowIdField);


        LookupField moduleStateField= (LookupField) FieldFactory.getDefaultField("moduleState", "Module State", "MODULE_STATE", FieldType.LOOKUP);
        moduleStateField.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.TICKET_STATUS));
        fields.add(moduleStateField);

        FacilioField stateFlowIdField = FieldFactory.getDefaultField("stateFlowId","State Flow Id","STATE_FLOW_ID",FieldType.NUMBER);
        fields.add(stateFlowIdField);

        FacilioField revisionNumberField = FieldFactory.getDefaultField("revisionNumber","Revision Number","REVISION_NUMBER",FieldType.NUMBER);
        fields.add(revisionNumberField);

        FacilioField parentIdField = FieldFactory.getDefaultField("parentId","Parent Id","PARENT_ID",FieldType.NUMBER);
        fields.add(parentIdField);


        FacilioField isInvoiceRevisedField = FieldFactory.getDefaultField("isInvoiceRevised","Is Invoice Revised","IS_INVOICE_REVISED",FieldType.BOOLEAN);
        fields.add(isInvoiceRevisedField);


        FacilioField revisionHistoryAvailableField = FieldFactory.getDefaultField("revisionHistoryAvailable","Is Revision History Available","REVISION_HISTORY_AVAILABLE",FieldType.BOOLEAN);
        fields.add(revisionHistoryAvailableField);


        LookupField taxField = (LookupField) FieldFactory.getDefaultField("tax", "Tax", "TAX_ID", FieldType.LOOKUP);
        taxField.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.TAX));
        fields.add(taxField);


        NumberField markup = (NumberField) FieldFactory.getDefaultField("markup", "Markup", "MARKUP", FieldType.DECIMAL);
        fields.add(markup);

        NumberField totalmarkup = (NumberField) FieldFactory.getDefaultField("totalMarkup", "Total Markup", "TOTAL_MARKUP_AMOUNT", FieldType.DECIMAL);
        fields.add(totalmarkup);

        NumberField version = (NumberField) FieldFactory.getDefaultField("invoiceVersion", "Invoice Version", "VERSION", FieldType.DECIMAL);
        fields.add(version);

        LookupField vendor = FieldFactory.getDefaultField("vendor", "Vendor", "VENDOR_ID", FieldType.LOOKUP);
        vendor.setLookupModule(Constants.getModBean().getModule(FacilioConstants.ContextNames.VENDORS));
        fields.add(vendor);


        BooleanField showMarkupValue = (BooleanField) FieldFactory.getDefaultField("showMarkupValue", "Show Markup value", "SHOW_MARKUP_VALUE", FieldType.BOOLEAN);
        fields.add(showMarkupValue);

        BooleanField isGlobalMarkup = (BooleanField) FieldFactory.getDefaultField("isGlobalMarkup", "IS Global Markup", "IS_GLOBAL_MARKUP", FieldType.BOOLEAN);
        fields.add(isGlobalMarkup);

        LookupField createdByPeople = FieldFactory.getDefaultField("sysCreatedByPeople","Created By","SYS_CREATED_BY_PEOPLE",FieldType.LOOKUP);
        createdByPeople.setLookupModule(Objects.requireNonNull(Constants.getModBean().getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        createdByPeople.setDefault(true);
        createdByPeople.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        fields.add(createdByPeople);

        LookupField modifiedByPeople = FieldFactory.getDefaultField("sysModifiedByPeople","Modified By","SYS_MODIFIED_BY_PEOPLE",FieldType.LOOKUP);
        modifiedByPeople.setLookupModule(Objects.requireNonNull(Constants.getModBean().getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        modifiedByPeople.setDefault(true);
        modifiedByPeople.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        fields.add(modifiedByPeople);

        DateField sysCreatedTime = FieldFactory.getDefaultField("sysCreatedTime","Created Time","SYS_CREATED_TIME",FieldType.DATE_TIME);
        fields.add(sysCreatedTime);

        DateField sysModifiedTime = FieldFactory.getDefaultField("sysModifiedTime","Modified Time","SYS_MODIFIED_TIME",FieldType.DATE_TIME);
        fields.add(sysModifiedTime);


        module.setFields(fields);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(module));
        addModuleChain.execute();

        LookupField parentQuotationField = FieldFactory.getDefaultField("parentInvoiceId", "Parent Invoice Id", "PARENT_INVOICE_ID", FieldType.LOOKUP);
        parentQuotationField.setLookupModule(Constants.getModBean().getModule(FacilioConstants.ContextNames.INVOICE));
        parentQuotationField.setModule(Constants.getModBean().getModule(FacilioConstants.ContextNames.INVOICE));
        modBean.addField(parentQuotationField);

        LookupField groupField = FieldFactory.getDefaultField("group", "Group Id", "GROUP_ID", FieldType.LOOKUP);
        groupField.setLookupModule(Constants.getModBean().getModule(FacilioConstants.ContextNames.INVOICE));
        groupField.setModule(Constants.getModBean().getModule(FacilioConstants.ContextNames.INVOICE));
        modBean.addField(groupField);


        LookupField childInvoice = FieldFactory.getDefaultField("childInvoice", "Child Invoice", "CHILD_INVOICE_ID", FieldType.LOOKUP);
        childInvoice.setLookupModule(Constants.getModBean().getModule(FacilioConstants.ContextNames.INVOICE));
        childInvoice.setModule(Constants.getModBean().getModule(FacilioConstants.ContextNames.INVOICE));
        modBean.addField(childInvoice);

        LookupField purchaseOrderField = FieldFactory.getDefaultField("purchaseOrder", "Purchase Order", "PURCHASE_ORDER_ID", FieldType.LOOKUP);
        purchaseOrderField.setLookupModule(Constants.getModBean().getModule(FacilioConstants.ContextNames.PURCHASE_ORDER));
        purchaseOrderField.setModule(Constants.getModBean().getModule(FacilioConstants.ContextNames.INVOICE));
        modBean.addField(purchaseOrderField);

        LookupField serviceRequestField = FieldFactory.getDefaultField("serviceRequest", "Service Request", "SERVICE_REQUEST_ID", FieldType.LOOKUP);
        serviceRequestField.setLookupModule(Constants.getModBean().getModule(FacilioConstants.ContextNames.SERVICE_REQUEST));
        serviceRequestField.setModule(Constants.getModBean().getModule(FacilioConstants.ContextNames.INVOICE));
        modBean.addField(serviceRequestField);



        LookupField quoteField = FieldFactory.getDefaultField("quote", "Quote", "QUOTE_ID", FieldType.LOOKUP);
        quoteField.setLookupModule(Constants.getModBean().getModule(FacilioConstants.ContextNames.QUOTE));
        quoteField.setModule(Constants.getModBean().getModule(FacilioConstants.ContextNames.INVOICE));
        modBean.addField(quoteField);


        return module;
    }

    private FacilioModule addInvoiceLineItemModule(ModuleBean modBean,FacilioModule invoiceModule) throws Exception {
        FacilioModule module = new FacilioModule(FacilioConstants.ContextNames.INVOICE_LINE_ITEMS,
                "Invoice Line Items",
                "InvoiceLineItems",
                FacilioModule.ModuleType.SUB_ENTITY
        );
        List<FacilioField> fields = new ArrayList<>();

        LookupField invoiceIdField= (LookupField)FieldFactory.getDefaultField("invoice","Invoice","INVOICE_ID",FieldType.LOOKUP);
        invoiceIdField.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.INVOICE));
        fields.add(invoiceIdField);

        SystemEnumField typeField = (SystemEnumField) FieldFactory.getDefaultField("type", "Type", "TYPE", FieldType.SYSTEM_ENUM);
        typeField.setEnumName("Type");
        fields.add(typeField);

        LookupField itemTypeField= (LookupField) FieldFactory.getDefaultField("itemType", "Item Type", "ITEM_TYPE_ID", FieldType.LOOKUP);
        itemTypeField.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.ITEM_TYPES));
        fields.add(itemTypeField);

        LookupField toolTypeField= (LookupField) FieldFactory.getDefaultField("toolType", "Tool Type", "TOOL_TYPE_ID", FieldType.LOOKUP);
        toolTypeField.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.TOOL_TYPES));
        fields.add(toolTypeField);

        LookupField serviceField= (LookupField) FieldFactory.getDefaultField("service", "Service", "SERVICE_ID", FieldType.LOOKUP);
        serviceField.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.SERVICE));
        fields.add(serviceField);

        LookupField labourField= (LookupField) FieldFactory.getDefaultField("labour", "Labour", "LABOUR_ID", FieldType.LOOKUP);
        labourField.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.LABOUR));
        fields.add(labourField);

        LookupField taxField = (LookupField) FieldFactory.getDefaultField("tax", "Tax", "TAX_ID", FieldType.LOOKUP);
        taxField.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.TAX));
        fields.add(taxField);


        FacilioField quantityField = FieldFactory.getDefaultField("quantity","Quantity","QUANTITY",FieldType.DECIMAL);
        fields.add(quantityField);


        FacilioField unitPriceField = FieldFactory.getDefaultField("unitPrice","Unit Price","UNIT_PRICE",FieldType.DECIMAL);
        fields.add(unitPriceField);


        FacilioField totalCostField = FieldFactory.getDefaultField("taxAmount","Tax Amount","TAX_AMOUNT",FieldType.DECIMAL);
        fields.add(totalCostField);

        FacilioField costField = FieldFactory.getDefaultField("cost","Cost","COST",FieldType.DECIMAL);
        fields.add(costField);

        FacilioField descriptionField = FieldFactory.getDefaultField("description","Description","DESCRIPTION",FieldType.STRING);
        fields.add(descriptionField);

        EnumField unitOfMeasure = (EnumField) FieldFactory.getDefaultField("unitOfMeasure","Unit of Measure","UNIT_OF_MEASURE",FieldType.ENUM);
        List<String> values = Arrays.asList("No", "kg");
        List<EnumFieldValue<Integer>> enumFieldValues = values.stream().map( val -> {
            int index = values.indexOf(val)+1;
            return new EnumFieldValue<>(index, val, index, true);
        }).collect(Collectors.toList());
        unitOfMeasure.setValues(enumFieldValues);
        fields.add(unitOfMeasure);




        module.setFields(fields);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(module));
        addModuleChain.execute();

        modBean.addSubModule(module.getModuleId(), invoiceModule.getModuleId());
        return module;
    }

    private FacilioModule constructInvoiceNotesModule(ModuleBean modBean, long orgId, FacilioModule invoiceModule) throws Exception {
        FacilioModule invoiceNotesModule = new FacilioModule("invoiceNotes", "Invoice Notes",
                "Invoice_Notes", FacilioModule.ModuleType.NOTES, null,
                null);

        List<FacilioField> fields = new ArrayList<>();

        FacilioField createdTimeField = new FacilioField(invoiceNotesModule, "createdTime", "Created Time",
                FacilioField.FieldDisplayType.DATETIME, "CREATED_TIME", FieldType.DATE_TIME,
                true, false, true, false);
        fields.add(createdTimeField);

        LookupField createdByField = SignupUtil.getLookupField(invoiceNotesModule, null, "createdBy",
                "Created By", "CREATED_BY", "users", FacilioField.FieldDisplayType.LOOKUP_POPUP,
                false, false, true, orgId);
        fields.add(createdByField);

        NumberField parentIdField = SignupUtil.getNumberField(invoiceNotesModule,
                "parentId", "Parent", "PARENT_ID", FacilioField.FieldDisplayType.NUMBER,
                true, false, true, orgId);
        fields.add(parentIdField);

        StringField titleField = SignupUtil.getStringField(invoiceNotesModule,
                "title", "Title",  "TITLE", FacilioField.FieldDisplayType.TEXTBOX,
                false, false, true, false,orgId);
        fields.add(titleField);

        StringField bodyField = SignupUtil.getStringField(invoiceNotesModule,
                "body", "Body", "BODY", FacilioField.FieldDisplayType.TEXTAREA,
                false, false, true, false,orgId);
        fields.add(bodyField);

        StringField bodyHtmlField = SignupUtil.getStringField(invoiceNotesModule,
                "bodyHTML", "Body HTML", "BODY_HTML", FacilioField.FieldDisplayType.TEXTAREA,
                false, false, true, false,orgId);
        fields.add(bodyHtmlField);

        LookupField parentNote = SignupUtil.getLookupField(invoiceNotesModule, invoiceNotesModule, "parentNote", "Parent Note",
                "PARENT_NOTE", null, FacilioField.FieldDisplayType.LOOKUP_POPUP,
                false, false, true, orgId);
        fields.add(parentNote);


        invoiceNotesModule.setFields(fields);

        SignupUtil.addModules(invoiceNotesModule);

        modBean.addSubModule(invoiceModule.getModuleId(), invoiceNotesModule.getModuleId());

        return invoiceNotesModule;
    }

    private FacilioModule addInvoiceAttachmentsModule(ModuleBean modBean, long orgId, FacilioModule invoiceModule) throws Exception {
        FacilioModule invoiceAttachmentsModule = new FacilioModule("invoiceAttachments", "Invoice Attachments",
                "Invoice_Attachments", FacilioModule.ModuleType.ATTACHMENTS, null,
                null);

        List<FacilioField> fields = new ArrayList<>();

        NumberField fieldIdField = SignupUtil.getNumberField(invoiceAttachmentsModule, "fileId", "File ID",
                "FILE_ID", FacilioField.FieldDisplayType.NUMBER,
                true, false, true, orgId);
        fieldIdField.setMainField(true);
        fields.add(fieldIdField);

        NumberField parentIdField = SignupUtil.getNumberField(invoiceAttachmentsModule,
                "parentId", "Parent", "PARENT_ID", FacilioField.FieldDisplayType.NUMBER,
                true, false, true, orgId);
        fields.add(parentIdField);

        FacilioField createdTimeField = SignupUtil.getNumberField(invoiceAttachmentsModule,
                "createdTime", "Created Time","CREATED_TIME",
                FacilioField.FieldDisplayType.NUMBER,
                true, false, true, orgId);
        fields.add(createdTimeField);

        FacilioField attachmentTypeField = SignupUtil.getNumberField(invoiceAttachmentsModule,
                "type", "Type", "ATTACHMENT_TYPE",
                FacilioField.FieldDisplayType.NUMBER,
                true, false, true, orgId);
        fields.add(attachmentTypeField);


        invoiceAttachmentsModule.setFields(fields);

        SignupUtil.addModules(invoiceAttachmentsModule);

        modBean.addSubModule(invoiceModule.getModuleId(), invoiceAttachmentsModule.getModuleId());

        return invoiceAttachmentsModule;
    }

    public void addActivityModuleForInvoice(FacilioModule invoiceModule) throws Exception {
        // TODO Auto-generated method stub

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule module = new FacilioModule(FacilioConstants.ContextNames.INVOICE_ACTIVITY,
                "Invoice Activity",
                "InvoiceActivity",
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

        module.setFields(fields);

        FacilioChain addModuleChain1 = TransactionChainFactory.addSystemModuleChain();
        addModuleChain1.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(module));
        addModuleChain1.execute();

        modBean.addSubModule(invoiceModule.getModuleId(), module.getModuleId());
    }

    public void addTermsAndCondModuleForInvoice(FacilioModule invoiceModule) throws Exception {
        // TODO Auto-generated method stub

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule module = new FacilioModule(FacilioConstants.ContextNames.INVOICE_ASSOCIATED_TERMS,
                "Invoice Associated Terms",
                "InvoiceAssociatedTerms",
                FacilioModule.ModuleType.SUB_ENTITY
        );


        List<FacilioField> fields = new ArrayList<>();

        LookupField invoiceIdField= (LookupField)FieldFactory.getDefaultField("invoice","Invoice","INVOICE_ID",FieldType.LOOKUP);
        invoiceIdField.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.INVOICE));
        fields.add(invoiceIdField);

        LookupField termsField= (LookupField)FieldFactory.getDefaultField("terms","Terms","TERMS_AND_CONDITIONS_ID",FieldType.LOOKUP);
        termsField.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.TERMS_AND_CONDITIONS));
        fields.add(termsField);

        module.setFields(fields);

        FacilioChain addModuleChain1 = TransactionChainFactory.addSystemModuleChain();
        addModuleChain1.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(module));
        addModuleChain1.execute();

        modBean.addSubModule(invoiceModule.getModuleId(), module.getModuleId());
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
    private StateflowTransitionContext addStateflowTransitionContext(FacilioModule module, StateFlowRuleContext parentStateFlow, String name, FacilioStatus fromStatus, FacilioStatus toStatus, AbstractStateTransitionRuleContext.TransitionType transitionType, Criteria criteria, List<ActionContext> actions) throws Exception {

        StateflowTransitionContext stateFlowTransitionContext = new StateflowTransitionContext();
        stateFlowTransitionContext.setName(name);
        stateFlowTransitionContext.setModule(module);
        stateFlowTransitionContext.setModuleId(module.getModuleId());
        stateFlowTransitionContext.setActivityType(EventType.STATE_TRANSITION);
        stateFlowTransitionContext.setExecutionOrder(1);
        stateFlowTransitionContext.setButtonType(1);
        stateFlowTransitionContext.setFromStateId(fromStatus.getId());
        stateFlowTransitionContext.setToStateId(toStatus.getId());
        stateFlowTransitionContext.setRuleType(WorkflowRuleContext.RuleType.STATE_RULE);
        stateFlowTransitionContext.setType(transitionType);
        stateFlowTransitionContext.setStateFlowId(parentStateFlow.getId());
        stateFlowTransitionContext.setCriteria(criteria);

        WorkflowRuleAPI.addWorkflowRule(stateFlowTransitionContext);

        if (actions != null && !actions.isEmpty()) {
            actions = ActionAPI.addActions(actions, stateFlowTransitionContext);
            if(stateFlowTransitionContext != null) {
                ActionAPI.addWorkflowRuleActionRel(stateFlowTransitionContext.getId(), actions);
                stateFlowTransitionContext.setActions(actions);
            }
        }

        return stateFlowTransitionContext;
    }

    private void addInvoiceApproversRelModule() throws Exception {
        List<FacilioModule> modules = new ArrayList<>();
        ModuleBean modBean = Constants.getModBean();
        FacilioModule peopleModule = modBean.getModule(FacilioConstants.ContextNames.PEOPLE);
        FacilioModule invoiceModule = modBean.getModule(FacilioConstants.ContextNames.INVOICE);
        FacilioModule invoiceApproversModule = new FacilioModule(FacilioConstants.ContextNames.INVOICE_APPROVERS_REL,"Invoive Approvers","INVOICE_APPROVERS_REL",FacilioModule.ModuleType.SUB_ENTITY,true);
        List<FacilioField> fields = new ArrayList<>();
        LookupField approversField = new LookupField(invoiceApproversModule,"right","Approver",FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"PEOPLE_ID",FieldType.LOOKUP,true,false,true,false,"Approvers",peopleModule);
        fields.add(approversField);
        LookupField invoiceField = new LookupField(invoiceApproversModule,"left","Invoice", FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"INVOICE_ID",FieldType.LOOKUP,true,false,true,true,"Invoice",invoiceModule);
        fields.add(invoiceField);
        invoiceApproversModule.setFields(fields);
        modules.add(invoiceApproversModule);
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.execute();
    }

    private void addInvoiceApproversField() throws Exception{
        ModuleBean modBean = Constants.getModBean();
        List<FacilioField>fields = new ArrayList<>();

        MultiLookupField approversField = (MultiLookupField) FieldFactory.getDefaultField("approvers", "Approvers", null, FieldType.MULTI_LOOKUP);
        approversField.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.PEOPLE));
        approversField.setDisplayType(FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE);
        approversField.setParentFieldPositionEnum(MultiLookupField.ParentFieldPosition.LEFT);
        approversField.setRelModule(modBean.getModule(FacilioConstants.ContextNames.INVOICE_APPROVERS_REL));
        approversField.setRelModuleId(modBean.getModule(FacilioConstants.ContextNames.INVOICE_APPROVERS_REL).getModuleId());
        fields.add(approversField);
        FacilioChain chain = TransactionChainFactory.getAddFieldsChain();
        chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.INVOICE);
        chain.getContext().put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, fields);
        chain.execute();
    }

    private void addSystemButtons() throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INVOICE);
        SystemButtonRuleContext editInvoice = new SystemButtonRuleContext();
        Criteria editInvoiceCriteria = new Criteria();
        editInvoiceCriteria.addAndCondition(CriteriaAPI.getCondition("Invoice.INVOICE_STATUS","invoiceStatus", String.valueOf(InvoiceContextV3.InvoiceStatus.DRAFT.getIndex()), EnumOperators.IS));

        editInvoice.setName("Edit");
        editInvoice.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        editInvoice.setIdentifier("edit_summary");
        editInvoice.setCriteria(editInvoiceCriteria);
        editInvoice.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        editInvoice.setPermission(AccountConstants.ModulePermission.UPDATE.name());
        editInvoice.setPermissionRequired(false);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.INVOICE,editInvoice);

//        SystemButtonRuleContext invoicePrint = new SystemButtonRuleContext();
//        invoicePrint.setName("Print");
//        invoicePrint.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
//        invoicePrint.setIdentifier("invoicePrint");
//        invoicePrint.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
//        invoicePrint.setPermission(AccountConstants.ModulePermission.READ.name());
//        invoicePrint.setPermissionRequired(false);
//        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.INVOICE,invoicePrint);
//
//        SystemButtonRuleContext invoiceDownload = new SystemButtonRuleContext();
//        invoiceDownload.setName("Download");
//        invoiceDownload.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
//        invoiceDownload.setIdentifier("invoiceDownload");
//        invoiceDownload.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
//        invoiceDownload.setPermission(AccountConstants.ModulePermission.READ.name());
//        invoiceDownload.setPermissionRequired(false);
//        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.INVOICE,invoiceDownload);
//
//        SystemButtonRuleContext invoiceEmail = new SystemButtonRuleContext();
//        invoiceEmail.setName("Send Email");
//        invoiceEmail.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
//        invoiceEmail.setIdentifier("invoiceEmail");
//        invoiceEmail.setPermission(AccountConstants.ModulePermission.READ.name());
//        invoiceEmail.setPermissionRequired(false);
//        invoiceEmail.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
//        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.INVOICE,invoiceEmail);

        SystemButtonRuleContext reviseInvoice = new SystemButtonRuleContext();
        Criteria reviseInvoiceCriteria = new Criteria();
        ArrayList<Integer> list = new ArrayList();
        list.add(InvoiceContextV3.InvoiceStatus.CANCELLED.getIndex());
        list.add(InvoiceContextV3.InvoiceStatus.DRAFT.getIndex());
        reviseInvoiceCriteria.addOrCondition(CriteriaAPI.getCondition("Invoice.INVOICE_STATUS","invoiceStatus", String.valueOf(InvoiceContextV3.InvoiceStatus.REJECTED.getIndex()), EnumOperators.IS));

        reviseInvoice.setName("Revise Invoice");
        reviseInvoice.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        reviseInvoice.setIdentifier("reviseInvoice");
        reviseInvoice.setCriteria(reviseInvoiceCriteria);
        reviseInvoice.setPermission(AccountConstants.ModulePermission.UPDATE.name());
        reviseInvoice.setPermissionRequired(false);
        reviseInvoice.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.INVOICE,reviseInvoice);

        SystemButtonRuleContext cloneInvoice = new SystemButtonRuleContext();
        Criteria cloneInvoiceCriteria = new Criteria();
        cloneInvoiceCriteria.addAndCondition(CriteriaAPI.getCondition("Invoice.INVOICE_STATUS","invoiceStatus", String.valueOf(InvoiceContextV3.InvoiceStatus.DRAFT.getIndex()), EnumOperators.ISN_T));

        cloneInvoice.setName("Clone Invoice");
        cloneInvoice.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        cloneInvoice.setIdentifier("cloneInvoice");
        cloneInvoice.setCriteria(cloneInvoiceCriteria);
        cloneInvoice.setPermission(AccountConstants.ModulePermission.CREATE.name());
        cloneInvoice.setPermissionRequired(false);
        cloneInvoice.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.INVOICE,cloneInvoice);

        SystemButtonRuleContext cancelInvoice = new SystemButtonRuleContext();
        Criteria cancelInvoiceCriteria = new Criteria();
        cancelInvoiceCriteria.addAndCondition(CriteriaAPI.getCondition("Invoice.INVOICE_STATUS","invoiceStatus", String.valueOf(InvoiceContextV3.InvoiceStatus.CANCELLED.getIndex()), EnumOperators.ISN_T));

        cancelInvoice.setName("Cancel Invoice");
        cancelInvoice.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        cancelInvoice.setIdentifier("cancelInvoice");
        cancelInvoice.setCriteria(cancelInvoiceCriteria);
        cancelInvoice.setPermission(AccountConstants.ModulePermission.UPDATE.name());
        cancelInvoice.setPermissionRequired(false);
        cancelInvoice.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.INVOICE,cancelInvoice);


        Criteria viewHistroryInvoiceCriteria = new Criteria();
        viewHistroryInvoiceCriteria.addAndCondition(CriteriaAPI.getCondition("Invoice.IS_INVOICE_REVISED","isInvoiceRevised", String.valueOf(true), BooleanOperators.IS));

        SystemButtonRuleContext viewHistroryInvoice = new SystemButtonRuleContext();
        viewHistroryInvoice.setName("View History");
        viewHistroryInvoice.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        viewHistroryInvoice.setIdentifier("invoiceHistory");
        viewHistroryInvoice.setPermission(AccountConstants.ModulePermission.READ.name());
        viewHistroryInvoice.setPermissionRequired(false);
        viewHistroryInvoice.setCriteria(viewHistroryInvoiceCriteria);
        viewHistroryInvoice.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.INVOICE,viewHistroryInvoice);

        SystemButtonRuleContext vendorToClientInvoice = new SystemButtonRuleContext();
        Criteria vendorToClientCriteria = new Criteria();
        vendorToClientCriteria.addAndCondition(CriteriaAPI.getCondition("Invoice.INVOICE_STATUS","invoiceStatus", String.valueOf(InvoiceContextV3.InvoiceStatus.PAYMENT_ACKNOWLEDGED.getIndex()), EnumOperators.IS));
        vendorToClientCriteria.addAndCondition(CriteriaAPI.getCondition("Invoice.INVOICE_TYPE","invoiceType", String.valueOf(InvoiceContextV3.InvoiceType.VENDOR.getIndex()), EnumOperators.IS));

        vendorToClientInvoice.setName("Invoice the Customer");
        vendorToClientInvoice.setIdentifier("invoiceCustomer");
        vendorToClientInvoice.setCriteria(vendorToClientCriteria);
        vendorToClientInvoice.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        vendorToClientInvoice.setPermission(AccountConstants.ModulePermission.CREATE.name());
        vendorToClientInvoice.setPermissionRequired(false);
        vendorToClientInvoice.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.INVOICE,vendorToClientInvoice);

        SystemButtonRuleContext associateTermsInvoice = new SystemButtonRuleContext();
        associateTermsInvoice.setName("Associate Terms");
        associateTermsInvoice.setIdentifier("associateTermsInvoice");
        associateTermsInvoice.setCriteria(cancelInvoiceCriteria);
        associateTermsInvoice.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        associateTermsInvoice.setPermission(AccountConstants.ModulePermission.UPDATE.name());
        associateTermsInvoice.setPermissionRequired(false);
        associateTermsInvoice.setCriteria(editInvoiceCriteria);
        associateTermsInvoice.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.INVOICE,associateTermsInvoice);

        SystemButtonRuleContext recordPayment = new SystemButtonRuleContext();
        Criteria recordPaymentCriteria = new Criteria();
        recordPaymentCriteria.addAndCondition(CriteriaAPI.getCondition("Invoice.INVOICE_STATUS","invoiceStatus", String.valueOf(InvoiceContextV3.InvoiceStatus.APPROVED.getIndex()), EnumOperators.IS));

        recordPayment.setName("Acknowledge Payment");
        recordPayment.setIdentifier("acknowledgePayment");
        recordPayment.setCriteria(recordPaymentCriteria);
        recordPayment.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        recordPayment.setPermission(AccountConstants.ModulePermission.UPDATE.name());
        recordPayment.setPermissionRequired(false);
        recordPayment.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.INVOICE,recordPayment);

        SystemButtonRuleContext dispatchInvoice = new SystemButtonRuleContext();

        Criteria dispatchButtonCriteria = new Criteria();
        dispatchButtonCriteria.addAndCondition(CriteriaAPI.getCondition("Invoice.INVOICE_STATUS","invoiceStatus", String.valueOf(InvoiceContextV3.InvoiceStatus.DRAFT.getIndex()), EnumOperators.IS));

        dispatchInvoice.setName("Dispatch Invoice");
        dispatchInvoice.setIdentifier("dispatchInvoice");
        dispatchInvoice.setCriteria(dispatchButtonCriteria);
        dispatchInvoice.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        dispatchInvoice.setPermission(AccountConstants.ModulePermission.UPDATE.name());
        dispatchInvoice.setPermissionRequired(false);
        dispatchInvoice.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.INVOICE,dispatchInvoice);

        SystemButtonApi.addExportAsCSV(FacilioConstants.ContextNames.INVOICE);
        SystemButtonApi.addExportAsExcel(FacilioConstants.ContextNames.INVOICE);

        SystemButtonRuleContext createButton = new SystemButtonRuleContext();
        createButton.setName("Create");
        createButton.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        createButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_TOP.getIndex());
        createButton.setIdentifier("create");
        createButton.setPermissionRequired(true);
        createButton.setStatus(false);
        createButton.setPermission("CREATE");
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.INVOICE, createButton);

        FacilioField invoiceStatusField = modBean.getField("invoiceStatus",module.getName());
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(invoiceStatusField.getColumnName(),invoiceStatusField.getName(),String.valueOf(InvoiceContextV3.InvoiceStatus.DRAFT.getIndex()), EnumOperators.IS));
        SystemButtonRuleContext listDeleteButton = new SystemButtonRuleContext();
        listDeleteButton.setName("Delete");
        listDeleteButton.setButtonType(SystemButtonRuleContext.ButtonType.DELETE.getIndex());
        listDeleteButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        listDeleteButton.setIdentifier("delete_list");
        listDeleteButton.setPermissionRequired(true);
        listDeleteButton.setPermission("DELETE");
        listDeleteButton.setCriteria(criteria);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.INVOICE, listDeleteButton);
        SystemButtonRuleContext listEditButton = new SystemButtonRuleContext();
        listEditButton.setName("Edit");
        listEditButton.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        listEditButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        listEditButton.setIdentifier("edit_list");
        listEditButton.setPermissionRequired(true);
        listEditButton.setPermission("UPDATE");
        listEditButton.setCriteria(criteria);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.INVOICE, listEditButton);



    }

    @Override
    public List<ScopeVariableModulesFields> getGlobalScopeConfig() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INVOICE);
        List<ScopeVariableModulesFields> scopeConfigList;

        ScopeVariableModulesFields tenantApp = new ScopeVariableModulesFields();
        tenantApp.setScopeVariableId(ScopingUtil.getScopeVariableId("default_tenant_user"));
        tenantApp.setModuleId(module.getModuleId());
        tenantApp.setFieldName("tenant");

        ScopeVariableModulesFields vendorApp = new ScopeVariableModulesFields();
        vendorApp.setScopeVariableId(ScopingUtil.getScopeVariableId("default_vendor_user"));
        vendorApp.setModuleId(module.getModuleId());
        vendorApp.setFieldName("vendor");

        ScopeVariableModulesFields clientApp = new ScopeVariableModulesFields();
        clientApp.setScopeVariableId(ScopingUtil.getScopeVariableId("default_client_user"));
        clientApp.setModuleId(module.getModuleId());
        clientApp.setFieldName("client");

        scopeConfigList = Arrays.asList(tenantApp,vendorApp,clientApp);
        return scopeConfigList;
    }


}

