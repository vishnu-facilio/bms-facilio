package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.TemplatePages.QuoteTemplatePage;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.RelatedListWidgetUtil;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.SystemButtonRuleContext;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.relation.util.RelationshipWidgetUtil;
import org.json.simple.JSONObject;

import java.util.*;

public class QuoteModule extends BaseModuleConfig{
    public QuoteModule(){
        setModuleName(FacilioConstants.ContextNames.QUOTE);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> quote = new ArrayList<FacilioView>();
        quote.add(getAllQuotations().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.QUOTE);
        groupDetails.put("views", quote);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }
    public void addData() throws Exception {
        addSystemButtons();
    }

    private static void addSystemButtons() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.QUOTE);
        SystemButtonRuleContext downloadPdf = new SystemButtonRuleContext();
        downloadPdf.setName("Download Pdf");
        downloadPdf.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        downloadPdf.setIdentifier("downloadPdf");
        downloadPdf.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.QUOTE,downloadPdf);

        SystemButtonRuleContext printPdf = new SystemButtonRuleContext();
        printPdf.setName("Print Pdf");
        printPdf.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        printPdf.setIdentifier("printPdf");
        printPdf.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.QUOTE,printPdf);

        SystemButtonRuleContext associateTerms = new SystemButtonRuleContext();
        associateTerms.setName("Associate Terms");
        associateTerms.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        associateTerms.setIdentifier("associateTerms");
        associateTerms.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.QUOTE,associateTerms);

        SystemButtonRuleContext sendMail = new SystemButtonRuleContext();
        sendMail.setName("Send Mail");
        sendMail.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        sendMail.setIdentifier("sendMail");
        sendMail.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.QUOTE,sendMail);

        SystemButtonRuleContext reviseQuoute = new SystemButtonRuleContext();
        reviseQuoute.setName("Revise");
        reviseQuoute.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        reviseQuoute.setIdentifier("reviseQuoute");
        reviseQuoute.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.QUOTE,reviseQuoute);

        SystemButtonRuleContext viewCustomerQuote = new SystemButtonRuleContext();
        viewCustomerQuote.setName("View Customer Quote");
        viewCustomerQuote.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        viewCustomerQuote.setIdentifier("viewCustomerQuote");
        viewCustomerQuote.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.QUOTE,viewCustomerQuote);

        SystemButtonRuleContext convertUserQuote = new SystemButtonRuleContext();
        convertUserQuote.setName("Convert to User Quote");
        convertUserQuote.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        convertUserQuote.setIdentifier("convertUserQuote");
        convertUserQuote.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.QUOTE,convertUserQuote);


        SystemButtonRuleContext edit = new SystemButtonRuleContext();
        edit.setName("Edit");
        edit.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        edit.setIdentifier("edit");
        edit.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        edit.setPermission(AccountConstants.ModulePermission.UPDATE.name());
        edit.setPermissionRequired(true);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.QUOTE,edit);

        SystemButtonApi.addCreateButton(FacilioConstants.ContextNames.QUOTE);
        SystemButtonApi.addExportAsCSV(FacilioConstants.ContextNames.QUOTE);
        SystemButtonApi.addExportAsExcel(FacilioConstants.ContextNames.QUOTE);

        FacilioStatus status = TicketAPI.getStatus(module, "Draft");
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField("moduleState",module.getName()),String.valueOf(status.getId()) , NumberOperators.EQUALS));

        SystemButtonRuleContext listDeleteButton = new SystemButtonRuleContext();
        listDeleteButton.setName("Delete");
        listDeleteButton.setButtonType(SystemButtonRuleContext.ButtonType.DELETE.getIndex());
        listDeleteButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        listDeleteButton.setIdentifier("delete_list");
        listDeleteButton.setPermissionRequired(true);
        listDeleteButton.setPermission("DELETE");
        listDeleteButton.setCriteria(criteria);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.QUOTE, listDeleteButton);

        SystemButtonRuleContext listEditButton = new SystemButtonRuleContext();
        listEditButton.setName("Edit");
        listEditButton.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        listEditButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        listEditButton.setIdentifier("edit_list");
        listEditButton.setPermissionRequired(true);
        listEditButton.setPermission("UPDATE");
        listEditButton.setCriteria(criteria);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.QUOTE, listEditButton);
        SystemButtonRuleContext invoiceButton = new SystemButtonRuleContext();
        invoiceButton.setName("Invoice the Customer");
        invoiceButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        invoiceButton.setIdentifier("invoiceCustomer");
        invoiceButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.QUOTE,invoiceButton);


    }

    private static FacilioView getAllQuotations() {

        FacilioModule module = ModuleFactory.getQuotationModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("parentId", "PARENT_ID", FieldType.NUMBER), false));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Quotes");
        allView.setModuleName(module.getName());
        allView.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {
        Map<String,List<PagesContext>> appNameVsPage = new HashMap<>();
        List<String> appNames = new ArrayList<>();
        appNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(getModuleName());
        for (String appName : appNames) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            appNameVsPage.put(appName,getQuotePage(app, module));
        }
        return appNameVsPage;
    }


    private List<PagesContext> getQuotePage(ApplicationContext app, FacilioModule module) throws Exception {
        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.ContextNames.QUOTE_ACTIVITY);
        return new ModulePages()
                .addPage("QuoteViewPage", "Default Quote Page", "", null, false, true, true)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("quotePdfViewer", null, null)
                .addWidget("quotePdfViewerWidget", "Summary", PageWidget.WidgetType.PDF_VIEWER, "flexiblewebpdfviewer_19", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("noteandinformation","Notes & Information",PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("quoteSummaryFields", null, null)
                .addWidget("quoteSummaryFieldsWidget", "Quote Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, QuoteTemplatePage.getSummaryWidgetDetails(module.getName(), app))
                .widgetDone()
                .sectionDone()
                .addSection("quotewidgetGroup", null,  null)
                .addWidget("quotecommentandattachmentwidget", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0,  null, QuoteTemplatePage.getWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("related", "Related", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("quoterelationships", "Relationships", "List of relationships and types between records across modules")
                .addWidget("quotebulkrelationshipwidget", "Relationships", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET,"flexiblewebbulkrelationshipwidget_6", 0, 0, null, RelationshipWidgetUtil.fetchRelationshipsOfModule(module))
                .widgetDone()
                .sectionDone()
                .addSection("quoterelatedlist", "Related List", "List of related records across modules")
                .addWidget("quotebulkrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST,"flexiblewebbulkrelatedlist_6", 0, 0,  null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("history","History",PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("quoteHistory",null,null)
                .addWidget("quoteHistoryWidget", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_4", 0, 0,  historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone()
                .pageDone()
                .getCustomPages();
    }


    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule quoteModule = modBean.getModule(FacilioConstants.ContextNames.QUOTE);

        FacilioForm quotationForm = new FacilioForm();
        quotationForm.setDisplayName("Quote");
        quotationForm.setName("default_quote_web");
        quotationForm.setModule(quoteModule);
        quotationForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        quotationForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP));

        List<FormField> quotationFormFields = new ArrayList<>();
        quotationFormFields.add(new FormField("subject", FacilioField.FieldDisplayType.TEXTBOX, "Subject", FormField.Required.REQUIRED, 1, 1));
        quotationFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        quotationFormFields.add(new FormField("billDate", FacilioField.FieldDisplayType.DATE, "Bill Date", FormField.Required.OPTIONAL, 3, 2));
        quotationFormFields.add(new FormField("expiryDate", FacilioField.FieldDisplayType.DATE, "Expiry Date", FormField.Required.REQUIRED, 3, 3));
        quotationFormFields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED,"site", 4, 2));
        quotationFormFields.add(new FormField("tenant", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Tenant", FormField.Required.REQUIRED,"tenant", 4, 3));
        quotationFormFields.add(new FormField("contact", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Contact", FormField.Required.OPTIONAL,"people", 5, 3));
        quotationFormFields.add(new FormField("workorder", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Workorder", FormField.Required.OPTIONAL,"workorder", 6, 1));
        quotationFormFields.add(new FormField("signature", FacilioField.FieldDisplayType.SIGNATURE, "Signature", FormField.Required.OPTIONAL, 14, 1));

        List<FormField> billingAddressFields = new ArrayList<>();
        billingAddressFields.add(new FormField("billToAddress", FacilioField.FieldDisplayType.QUOTE_ADDRESS, "Bill To Address", FormField.Required.OPTIONAL, 7, 1));

        List<FormField> lineItemFields = new ArrayList<>();
        FormField lineItemField  =new FormField("lineItems", FacilioField.FieldDisplayType.QUOTE_LINE_ITEMS, "Line Items", FormField.Required.REQUIRED, 9, 1);
        lineItemField.addToConfig("hideTaxField",false);
        lineItemFields.add(lineItemField);

        List<FormField> signatureFields = new ArrayList<>();
        signatureFields.add(new FormField("notes", FacilioField.FieldDisplayType.TEXTAREA, "Customer Notes", FormField.Required.OPTIONAL, 13, 1));

        List<FormField> requestForQuotationModuleFormFields = new ArrayList<>();
        requestForQuotationModuleFormFields.addAll(quotationFormFields);
        requestForQuotationModuleFormFields.addAll(billingAddressFields);
        requestForQuotationModuleFormFields.addAll(lineItemFields);
        requestForQuotationModuleFormFields.addAll(signatureFields);
//        quotationForm.setFields(requestForQuotationModuleFormFields);

        FormSection defaultSection = new FormSection("QUOTE INFORMATION", 1, quotationFormFields, true);
        defaultSection.setSectionType(FormSection.SectionType.FIELDS);

        FormSection billingSection = new FormSection("Billing Address", 2, billingAddressFields, false);
        billingSection.setSectionType(FormSection.SectionType.FIELDS);

        FormSection lineItemSection = new FormSection("QUOTE ITEMS", 3, lineItemFields, true);
        lineItemSection.setSectionType(FormSection.SectionType.FIELDS);

        FormSection notesSection = new FormSection("NOTES", 4, signatureFields, false);
        notesSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormSection> sections = new ArrayList<>();
        sections.add(defaultSection);
        sections.add(billingSection);
        sections.add(lineItemSection);
        sections.add(notesSection);

        quotationForm.setSections(sections);
        quotationForm.setIsSystemForm(true);
        quotationForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(quotationForm);
    }
}
