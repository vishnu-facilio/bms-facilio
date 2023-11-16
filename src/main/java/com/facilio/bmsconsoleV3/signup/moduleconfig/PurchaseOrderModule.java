package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.ModuleSettingConfig.context.GlimpseContext;
import com.facilio.bmsconsole.ModuleSettingConfig.util.GlimpseUtil;
import com.facilio.bmsconsole.TemplatePages.PurchaseOrderTemplatePage;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.RelatedListWidgetUtil;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.SystemButtonRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.db.criteria.operators.LookupOperator;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.relation.util.RelationshipWidgetUtil;
import org.json.simple.JSONObject;

import java.util.*;

public class PurchaseOrderModule extends BaseModuleConfig{
    public PurchaseOrderModule(){
        setModuleName(FacilioConstants.ContextNames.PURCHASE_ORDER);
    }
    public void addData() throws Exception {
        addSystemButtons();
    }

    public static void addSystemButtons() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> poFields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASE_ORDER);
        Map<String,FacilioField> poFieldMap = FieldFactory.getAsMap(poFields);

        SystemButtonRuleContext downloadPdf = new SystemButtonRuleContext();
        downloadPdf.setName("Download Pdf");
        downloadPdf.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        downloadPdf.setIdentifier("downloadPdf");
        downloadPdf.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.PURCHASE_ORDER,downloadPdf);

        SystemButtonRuleContext printPdf = new SystemButtonRuleContext();
        printPdf.setName("Print Pdf");
        printPdf.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        printPdf.setIdentifier("printPdf");
        printPdf.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.PURCHASE_ORDER,printPdf);

        SystemButtonRuleContext associateTerms = new SystemButtonRuleContext();
        associateTerms.setName("Associate Terms");
        associateTerms.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        associateTerms.setIdentifier("associateTerms");
        associateTerms.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.PURCHASE_ORDER,associateTerms);

        SystemButtonRuleContext gotoReceivables = new SystemButtonRuleContext();
        gotoReceivables.setName("Go to Receivables");
        gotoReceivables.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        gotoReceivables.setIdentifier("gotoReceivables");
        gotoReceivables.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.PURCHASE_ORDER,gotoReceivables);

        SystemButtonRuleContext completePo = new SystemButtonRuleContext();
        completePo.setName("Complete PO");
        completePo.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        completePo.setIdentifier("completePo");
        completePo.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        completePo.setPermission(AccountConstants.ModulePermission.UPDATE.name());
        completePo.setPermissionRequired(true);
        Criteria poBtnCriteria = new Criteria();
        poBtnCriteria.addAndCondition(CriteriaAPI.getCondition(poFieldMap.get("receivableStatus"),String.valueOf(ReceivableContext.Status.RECEIVED.getValue()), EnumOperators.ISN_T));
        poBtnCriteria.addAndCondition(CriteriaAPI.getCondition(poFieldMap.get("approvalStatus"), CommonOperators.IS_EMPTY));
        poBtnCriteria.addAndCondition(CriteriaAPI.getCondition(poFieldMap.get("approvalFlowId"), CommonOperators.IS_EMPTY));
        poBtnCriteria.addAndCondition(CriteriaAPI.getCondition(poFieldMap.get("completedTime"), CommonOperators.IS_EMPTY));
        completePo.setCriteria(poBtnCriteria);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.PURCHASE_ORDER,completePo);

        SystemButtonRuleContext edit = new SystemButtonRuleContext();
        edit.setName("Edit");
        edit.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        edit.setIdentifier("edit_summary");
        edit.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        edit.setPermission(AccountConstants.ModulePermission.UPDATE.name());
        edit.setPermissionRequired(true);
        Criteria poEditBtnCriteria = new Criteria();
        poEditBtnCriteria.addAndCondition(CriteriaAPI.getCondition(poFieldMap.get("receivableStatus"),String.valueOf(ReceivableContext.Status.YET_TO_RECEIVE.getValue()), EnumOperators.IS));
        poEditBtnCriteria.addAndCondition(CriteriaAPI.getCondition(poFieldMap.get("completedTime"),CommonOperators.IS_EMPTY));
        edit.setCriteria(poEditBtnCriteria);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.PURCHASE_ORDER,edit);

        SystemButtonApi.addCreateButtonWithModuleDisplayName(FacilioConstants.ContextNames.PURCHASE_ORDER);
        SystemButtonApi.addExportAsCSV(FacilioConstants.ContextNames.PURCHASE_ORDER);
        SystemButtonApi.addExportAsExcel(FacilioConstants.ContextNames.PURCHASE_ORDER);
        SystemButtonApi.addListEditButton(FacilioConstants.ContextNames.PURCHASE_ORDER);
        SystemButtonApi.addListDeleteButton(FacilioConstants.ContextNames.PURCHASE_ORDER);


    }
    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> purchaseOrder = new ArrayList<FacilioView>();
        purchaseOrder.add(getAllPurchaseOrderView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.PURCHASE_ORDER);
        groupDetails.put("views", purchaseOrder);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllPurchaseOrderView() {
        FacilioField localId = new FacilioField();
        localId.setName("id");
        localId.setColumnName("ID");
        localId.setDataType(FieldType.NUMBER);
        localId.setModule(ModuleFactory.getPurchaseOrderModule());

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All");
        allView.setSortFields(Arrays.asList(new SortField(localId, false)));

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
        FacilioModule purchaseOrderModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_ORDER);

        FacilioForm purchaseOrderForm = new FacilioForm();
        purchaseOrderForm.setDisplayName("PURCHASE ORDER");
        purchaseOrderForm.setName("default_purchaseorder_web");
        purchaseOrderForm.setModule(purchaseOrderModule);
        purchaseOrderForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        purchaseOrderForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP));

        List<FormField> purchaseOrderFormFields = new ArrayList<>();
        purchaseOrderFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        purchaseOrderFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        purchaseOrderFormFields.add(new FormField("vendor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Vendor", FormField.Required.REQUIRED, "vendors", 3, 2).setAllowCreateOptions(true).setCreateFormName("vendors_form"));
        purchaseOrderFormFields.add(new FormField("storeRoom", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Storeroom", FormField.Required.OPTIONAL, "storeRoom", 3, 3));
        purchaseOrderFormFields.add(new FormField("orderedTime", FacilioField.FieldDisplayType.DATE, "Ordered Date", FormField.Required.OPTIONAL, 4, 2));
        purchaseOrderFormFields.add(new FormField("requiredTime", FacilioField.FieldDisplayType.DATE, "Expected Delivery Date", FormField.Required.OPTIONAL, 4, 3));
        purchaseOrderFormFields.add(new FormField("requestedBy", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Requested By", FormField.Required.OPTIONAL, "user", 5, 1));
//        purchaseOrderForm.setFields(purchaseOrderFormFields);

        List<FormField> billingAddressFields = new ArrayList<>();
        billingAddressFields.add(new FormField("billToAddress", FacilioField.FieldDisplayType.SADDRESS, "BILLING ADDRESS", FormField.Required.OPTIONAL, 6, 1));

        List<FormField> shippingAddressFields = new ArrayList<>();
        shippingAddressFields.add(new FormField("shipToAddress", FacilioField.FieldDisplayType.SADDRESS, "SHIPPING ADDRESS", FormField.Required.OPTIONAL, 7, 1));

        List<FormField> lineItemFields = new ArrayList<>();
        FormField lineItemField = new FormField("lineItems", FacilioField.FieldDisplayType.LINEITEMS, "LINE ITEMS", FormField.Required.REQUIRED, 8, 1);
        lineItemField.addToConfig("hideTaxField",false);
        lineItemFields.add(lineItemField);

        List<FormField> requestForQuotationModuleFormFields = new ArrayList<>();
        requestForQuotationModuleFormFields.addAll(purchaseOrderFormFields);
        requestForQuotationModuleFormFields.addAll(billingAddressFields);
        requestForQuotationModuleFormFields.addAll(shippingAddressFields);
        requestForQuotationModuleFormFields.addAll(lineItemFields);
//        purchaseOrderForm.setFields(requestForQuotationModuleFormFields);

        FormSection defaultSection = new FormSection("Purchase Order", 1, purchaseOrderFormFields, true);
        defaultSection.setSectionType(FormSection.SectionType.FIELDS);

        FormSection billingSection = new FormSection("Billing Address", 2, billingAddressFields, true);
        billingSection.setSectionType(FormSection.SectionType.FIELDS);

        FormSection shippingSection = new FormSection("Shipping Address", 3, shippingAddressFields, true);
        shippingSection.setSectionType(FormSection.SectionType.FIELDS);

        FormSection lineItemSection = new FormSection("Line Items", 4, lineItemFields, true);
        shippingSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormSection> sections = new ArrayList<>();
        sections.add(defaultSection);
        sections.add(billingSection);
        sections.add(shippingSection);
        sections.add(lineItemSection);

        purchaseOrderForm.setSections(sections);
        purchaseOrderForm.setIsSystemForm(true);
        purchaseOrderForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(purchaseOrderForm);
    }

    @Override
    public List<GlimpseContext> getModuleGlimpse() throws Exception{

        List<String> fieldNames = new ArrayList<>();
        fieldNames.add("storeRoom");
        fieldNames.add("vendor");
        fieldNames.add("orderedTime");
        fieldNames.add("requiredTime");

        GlimpseContext glimpse = GlimpseUtil.getNewGlimpse(fieldNames,getModuleName());

        List<GlimpseContext> glimpseList = new ArrayList<>();
        glimpseList.add(glimpse);

        return glimpseList;

    }
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {
        Map<String,List<PagesContext>> appNameVsPage = new HashMap<>();
        List<String> appNames = new ArrayList<>();
        appNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(getModuleName());
        for (String appName : appNames) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            appNameVsPage.put(appName,getPurchaseOrderViewPage(app, module));
        }
        return appNameVsPage;
    }
    private List<PagesContext> getPurchaseOrderViewPage(ApplicationContext app, FacilioModule module) throws Exception {
        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.ContextNames.PURCHASE_ORDER_ACTIVITY);
        return new ModulePages()
                .addPage("purchaseOrderViewPage", "Default Purchase Order View Page", "", null, false, true, true)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("purchaseOrderPdfViewer", null, null)
                .addWidget("purchaseOrderPdfViewerWidget", "", PageWidget.WidgetType.PDF_VIEWER, "flexiblewebpdfviewer_19", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("noteandinformation","Notes & Information",PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("purchaseOrderSummaryfields", null, null)
                .addWidget("purchaseOrderSummaryFieldsWidget", "Purchase Order Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, PurchaseOrderTemplatePage.getSummaryWidgetDetails(module.getName(), app))
                .widgetDone()
                .sectionDone()
                .addSection("purchaseorderwidgetGroup", null,  null)
                .addWidget("purchaseordercommentandattachmentwidgetgroupwidget", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0,  null, PurchaseOrderTemplatePage.getWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("related", "Related", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("purchaseorderrelationships", "Relationships", "List of relationships and types between records across modules")
                .addWidget("purchaseorderbulkrelationshipwidget", "Relationships", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET,"flexiblewebbulkrelationshipwidget_6", 0, 0, null, RelationshipWidgetUtil.fetchRelationshipsOfModule(module))
                .widgetDone()
                .sectionDone()
                .addSection("purchaseorderrelatedlist", "Related List", "List of related records across modules")
                .addWidget("purchaseorderbulkrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST,"flexiblewebbulkrelatedlist_6", 0, 0,  null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("history","History",PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("purchaseOrderHistory",null,null)
                .addWidget("purchaseOrderHistoryWidget", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_4", 0, 0,  historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone()
                .pageDone()
                .getCustomPages();
    }

}
