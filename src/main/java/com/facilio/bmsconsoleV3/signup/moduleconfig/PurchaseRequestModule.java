package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.ModuleSettingConfig.context.GlimpseContext;
import com.facilio.bmsconsole.ModuleSettingConfig.context.GlimpseFieldContext;
import com.facilio.bmsconsole.ModuleSettingConfig.util.GlimpseUtil;
import com.facilio.bmsconsole.TemplatePages.PurchaseOrderTemplatePage;
import com.facilio.bmsconsole.TemplatePages.PurchaseRequestTemplatePage;
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
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.relation.util.RelationshipWidgetUtil;
import org.json.simple.JSONObject;

import java.util.*;

public class PurchaseRequestModule extends BaseModuleConfig{
    public PurchaseRequestModule(){
        setModuleName(FacilioConstants.ContextNames.PURCHASE_REQUEST);
    }
    public void addData() throws Exception {
        addSystemButtons();
    }

    public static void addSystemButtons() throws Exception {

        SystemButtonRuleContext convertToRfq = new SystemButtonRuleContext();
        convertToRfq.setName("Convert to RFQ");
        convertToRfq.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        convertToRfq.setIdentifier("convertToRfq");
        convertToRfq.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.PURCHASE_REQUEST,convertToRfq);

        SystemButtonRuleContext downloadPdf = new SystemButtonRuleContext();
        downloadPdf.setName("Download Pdf");
        downloadPdf.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        downloadPdf.setIdentifier("downloadPdf");
        downloadPdf.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.PURCHASE_REQUEST,downloadPdf);

        SystemButtonRuleContext printPdf = new SystemButtonRuleContext();
        printPdf.setName("Print Pdf");
        printPdf.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        printPdf.setIdentifier("printPdf");
        printPdf.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.PURCHASE_REQUEST,printPdf);

        SystemButtonRuleContext associateTerms = new SystemButtonRuleContext();
        associateTerms.setName("Associate Terms");
        associateTerms.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        associateTerms.setIdentifier("associateTerms");
        associateTerms.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.PURCHASE_REQUEST,associateTerms);

        SystemButtonRuleContext edit = new SystemButtonRuleContext();
        edit.setName("Edit");
        edit.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        edit.setIdentifier("edit");
        edit.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        edit.setPermission(AccountConstants.ModulePermission.UPDATE.name());
        edit.setPermissionRequired(true);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.PURCHASE_REQUEST,edit);

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> purchaseRequest = new ArrayList<FacilioView>();
        purchaseRequest.add(getAllPurchaseRequestView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.PURCHASE_REQUEST);
        groupDetails.put("views", purchaseRequest);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllPurchaseRequestView() {
        FacilioField localId = new FacilioField();
        localId.setName("id");
        localId.setColumnName("ID");
        localId.setDataType(FieldType.NUMBER);
        localId.setModule(ModuleFactory.getPurchaseRequestModule());

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
        FacilioModule purchaseRequestModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_REQUEST);

        FacilioForm purchaseRequestForm = new FacilioForm();
        purchaseRequestForm.setDisplayName("PURCHASE REQUEST");
        purchaseRequestForm.setName("default_purchaserequest_web");
        purchaseRequestForm.setModule(purchaseRequestModule);
        purchaseRequestForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        purchaseRequestForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP));

        List<FormField> purchaseRequestFormFields = new ArrayList<>();
        purchaseRequestFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        purchaseRequestFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        purchaseRequestFormFields.add(new FormField("vendor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Vendor", FormField.Required.OPTIONAL, "vendors", 3, 2).setAllowCreateOptions(true).setCreateFormName("vendors_form"));
        purchaseRequestFormFields.add(new FormField("storeRoom", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Storeroom", FormField.Required.OPTIONAL, "storeRoom", 3, 3));
        purchaseRequestFormFields.add(new FormField("requestedTime", FacilioField.FieldDisplayType.DATE, "Requested Date", FormField.Required.OPTIONAL, 4, 2));
        purchaseRequestFormFields.add(new FormField("requiredTime", FacilioField.FieldDisplayType.DATE, "Required Date", FormField.Required.OPTIONAL, 4, 3));
        purchaseRequestFormFields.add(new FormField("requestedBy", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Requested By", FormField.Required.OPTIONAL, "user", 5, 1));

        List<FormField> billingAddressFields = new ArrayList<>();
        billingAddressFields.add(new FormField("billToAddress", FacilioField.FieldDisplayType.SADDRESS, "BILLING ADDRESS", FormField.Required.OPTIONAL, 6, 1));

        List<FormField> shippingAddressFields = new ArrayList<>();
        shippingAddressFields.add(new FormField("shipToAddress", FacilioField.FieldDisplayType.SADDRESS, "SHIPPING ADDRESS", FormField.Required.OPTIONAL, 7, 1));

        List<FormField> lineItemFields = new ArrayList<>();
        FormField lineItemField = new FormField("lineItems", FacilioField.FieldDisplayType.LINEITEMS, "LINE ITEMS", FormField.Required.REQUIRED, 8, 1);
        lineItemField.addToConfig("hideTaxField",false);
        lineItemFields.add(lineItemField);

        List<FormField> requestForQuotationModuleFormFields = new ArrayList<>();
        requestForQuotationModuleFormFields.addAll(purchaseRequestFormFields);
        requestForQuotationModuleFormFields.addAll(billingAddressFields);
        requestForQuotationModuleFormFields.addAll(shippingAddressFields);
        requestForQuotationModuleFormFields.addAll(lineItemFields);
//        purchaseRequestForm.setFields(requestForQuotationModuleFormFields);

        FormSection defaultSection = new FormSection("Purchase Order", 1, purchaseRequestFormFields, true);
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

        purchaseRequestForm.setSections(sections);
        purchaseRequestForm.setIsSystemForm(true);
        purchaseRequestForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(purchaseRequestForm);
    }

    @Override
    public List<GlimpseContext> getModuleGlimpse() throws Exception{

        List<String> fieldNames = new ArrayList<>();
        fieldNames.add("requestedBy");
        fieldNames.add("storeRoom");
        fieldNames.add("requestedTime");
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
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(getModuleName());
        for (String appName : appNames) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            appNameVsPage.put(appName,getPurchaseRequestViewPage(app, module));
        }
        return appNameVsPage;
    }
    private List<PagesContext> getPurchaseRequestViewPage(ApplicationContext app, FacilioModule module) throws Exception {
        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.ContextNames.PURCHASE_REQUEST_ACTIVITY);
        return new ModulePages()
                .addPage("purchaseRequestViewPage", "Default Purchase Request View Page", "", null, false, true, true)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("purchaseRequestPdfViewer", null, null)
                .addWidget("purchaseRequestPdfViewerWidget", "Summary", PageWidget.WidgetType.PDF_VIEWER, "flexiblewebpdfviewer_19", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("noteandinformation","Notes & Information",PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("purchaseRequestSummaryFields", null, null)
                .addWidget("purchaseRequestSummaryFieldsWidget", "Purchase Request Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, PurchaseRequestTemplatePage.getSummaryWidgetDetails(module.getName(), app))
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null,  null)
                .addWidget("purchaserequestcommentandattachmentwidgetgroupwidget", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0,  null, PurchaseRequestTemplatePage.getWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("related", "Related", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("purchaserequestrelationships", "Relationships", "List of relationships and types between records across modules")
                .addWidget("purchaserequestbulkrelationshipwidget", "Relationships", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET,"flexiblewebbulkrelationshipwidget_6", 0, 0, null, RelationshipWidgetUtil.fetchRelationshipsOfModule(module))
                .widgetDone()
                .sectionDone()
                .addSection("purchaserequestrelatedlist", "Related List", "List of related records across modules")
                .addWidget("purchaserequestbulkrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST,"flexiblewebbulkrelatedlist_6", 0, 0,  null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("history","History",PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("purchaseRequestHistory",null,null)
                .addWidget("purchaseRequestHistoryWidget", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_4", 0, 0,  historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone()
                .pageDone()
                .getCustomPages();
    }

}
