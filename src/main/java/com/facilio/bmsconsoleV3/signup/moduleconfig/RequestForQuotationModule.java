package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.ModuleSettingConfig.context.GlimpseContext;
import com.facilio.bmsconsole.ModuleSettingConfig.context.GlimpseFieldContext;
import com.facilio.bmsconsole.ModuleSettingConfig.util.GlimpseUtil;
import com.facilio.bmsconsole.TemplatePages.PurchaseOrderTemplatePage;
import com.facilio.bmsconsole.TemplatePages.RequestForQuotationTemplatePage;
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
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
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

public class RequestForQuotationModule extends BaseModuleConfig{
    public RequestForQuotationModule(){
        setModuleName(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION);
    }
    public void addData() throws Exception {
        addSystemButtons();
    }
    public static void addSystemButtons() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> rfqFields = modBean.getAllFields(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION);
        Map<String,FacilioField> rfqFieldMap = FieldFactory.getAsMap(rfqFields);

        SystemButtonRuleContext downloadPdf = new SystemButtonRuleContext();
        downloadPdf.setName("Download Pdf");
        downloadPdf.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        downloadPdf.setIdentifier("downloadPdf");
        downloadPdf.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION,downloadPdf);

        SystemButtonRuleContext printPdf = new SystemButtonRuleContext();
        printPdf.setName("Print Pdf");
        printPdf.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        printPdf.setIdentifier("printPdf");
        printPdf.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION,printPdf);

        SystemButtonRuleContext publishRfq = new SystemButtonRuleContext();
        publishRfq.setName("Publish RFQ");
        publishRfq.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        publishRfq.setIdentifier("publishRfq");
        publishRfq.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        Criteria publishBtnCriteria = new Criteria();
        publishBtnCriteria.addAndCondition(CriteriaAPI.getCondition(rfqFieldMap.get("isRfqFinalized"),"false", BooleanOperators.IS));
        publishBtnCriteria.addAndCondition(CriteriaAPI.getCondition(rfqFieldMap.get("approvalStatus"), CommonOperators.IS_EMPTY));
        publishBtnCriteria.addAndCondition(CriteriaAPI.getCondition(rfqFieldMap.get("approvalFlowId"), CommonOperators.IS_EMPTY));
        publishRfq.setCriteria(publishBtnCriteria);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION,publishRfq);

        SystemButtonRuleContext closeSubmissionBtn = new SystemButtonRuleContext();
        closeSubmissionBtn.setName("Close Submission");
        closeSubmissionBtn.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        closeSubmissionBtn.setIdentifier("closeSubmission");
        closeSubmissionBtn.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        Criteria closeSubmissionBtnCriteria = new Criteria();
        closeSubmissionBtnCriteria.addAndCondition(CriteriaAPI.getCondition(rfqFieldMap.get("isRfqFinalized"),"true", BooleanOperators.IS));
        closeSubmissionBtnCriteria.addAndCondition(CriteriaAPI.getCondition(rfqFieldMap.get("isQuoteReceived"),"false", BooleanOperators.IS));
        closeSubmissionBtnCriteria.addAndCondition(CriteriaAPI.getCondition(rfqFieldMap.get("approvalStatus"), CommonOperators.IS_EMPTY));
        closeSubmissionBtnCriteria.addAndCondition(CriteriaAPI.getCondition(rfqFieldMap.get("approvalFlowId"), CommonOperators.IS_EMPTY));
        closeSubmissionBtn.setCriteria(closeSubmissionBtnCriteria);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION,closeSubmissionBtn);

        SystemButtonRuleContext assessAndAwardBtn = new SystemButtonRuleContext();
        assessAndAwardBtn.setName("Assess & Award");
        assessAndAwardBtn.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        assessAndAwardBtn.setIdentifier("assessAndAward");
        assessAndAwardBtn.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        Criteria assessAndAwardBtnCriteria = new Criteria();
        assessAndAwardBtnCriteria.addAndCondition(CriteriaAPI.getCondition(rfqFieldMap.get("isQuoteReceived"),"true", BooleanOperators.IS));
        assessAndAwardBtnCriteria.addAndCondition(CriteriaAPI.getCondition(rfqFieldMap.get("isAwarded"),"false", BooleanOperators.IS));
        assessAndAwardBtn.setCriteria(assessAndAwardBtnCriteria);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION,assessAndAwardBtn);

        SystemButtonRuleContext viewAwardSummaryBtn = new SystemButtonRuleContext();
        viewAwardSummaryBtn.setName("View Award Summary");
        viewAwardSummaryBtn.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        viewAwardSummaryBtn.setIdentifier("viewAwardSummary");
        viewAwardSummaryBtn.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        Criteria viewAwardSummaryBtnCriteria = new Criteria();
        viewAwardSummaryBtnCriteria.addAndCondition(CriteriaAPI.getCondition(rfqFieldMap.get("isAwarded"),"true", BooleanOperators.IS));
        viewAwardSummaryBtn.setCriteria(viewAwardSummaryBtnCriteria);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION,viewAwardSummaryBtn);//

        SystemButtonRuleContext discardRfqBtn = new SystemButtonRuleContext();
        discardRfqBtn.setName("Discard RFQ");
        discardRfqBtn.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        discardRfqBtn.setIdentifier("discardRfq");
        discardRfqBtn.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        Criteria discardRfqBtnCriteria = new Criteria();
        discardRfqBtnCriteria.addAndCondition(CriteriaAPI.getCondition(rfqFieldMap.get("isAwarded"),"false", BooleanOperators.IS));
        discardRfqBtn.setCriteria(discardRfqBtnCriteria);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION,discardRfqBtn);

        SystemButtonRuleContext edit = SystemButtonApi.getSummaryEditButton("Edit Rfq");
        Criteria editBtnCriteria = new Criteria();
        editBtnCriteria.addAndCondition(CriteriaAPI.getCondition(rfqFieldMap.get("isRfqFinalized"),"false", BooleanOperators.IS));
        edit.setCriteria(editBtnCriteria);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION,edit);

        SystemButtonApi.addCreateButtonWithModuleDisplayName(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION );
        SystemButtonApi.addExportAsCSV(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION);
        SystemButtonApi.addExportAsExcel(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION);
        SystemButtonApi.addListEditButton(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION);
        SystemButtonApi.addListDeleteButton(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION);

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> requestForQuotation = new ArrayList<FacilioView>();
        requestForQuotation.add(getAllRequestForQuotationView().setOrder(order++));
        requestForQuotation.add(getQuotesReceivedRfqView("quotesReceived", "Quotes Received" ,true).setOrder(order++));
        requestForQuotation.add(getAwardedRfqView("awarded", "Awarded" ,true).setOrder(order++));
        requestForQuotation.add(getPoCreatedRfqView("poCreated", "PO Created" ,true).setOrder(order++));
        requestForQuotation.add(getDiscardedRfqView("discarded", "Discarded" ,true).setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION);
        groupDetails.put("views", requestForQuotation);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllRequestForQuotationView() {

        FacilioModule module = ModuleFactory.getRequestForQuotationModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), false));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Request For Quotations");
        allView.setModuleName(module.getName());
        allView.setSortFields(sortFields);

        return allView;
    }

    private static FacilioView getQuotesReceivedRfqView(String viewName, String viewDisplayName, boolean quotesReceived) {
        FacilioModule rfqModule = ModuleFactory.getRequestForQuotationModule();

        FacilioField createdTime = new FacilioField();
        createdTime.setName("localId");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("LOCAL_ID");
        createdTime.setModule(rfqModule);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

        Criteria criteria = getQuotesReceivedRfqCondition(quotesReceived);
        FacilioView statusView = new FacilioView();
        statusView.setName(viewName);
        statusView.setDisplayName(viewDisplayName);
        statusView.setSortFields(sortFields);
        statusView.setCriteria(criteria);

        return statusView;
    }

    private static Criteria getQuotesReceivedRfqCondition(boolean quotesReceived) {
        FacilioField quotesReceivedField = new FacilioField();
        quotesReceivedField.setName("isQuoteReceived");
        quotesReceivedField.setColumnName("QUOTE_RECEIVED");
        quotesReceivedField.setDataType(FieldType.BOOLEAN);
        quotesReceivedField.setModule(ModuleFactory.getRequestForQuotationModule());

        Condition quotesReceivedCondition = new Condition();
        quotesReceivedCondition.setField(quotesReceivedField);
        quotesReceivedCondition.setOperator(BooleanOperators.IS);
        quotesReceivedCondition.setValue(String.valueOf(quotesReceived));

        Criteria rfqStatusCriteria = new Criteria();
        rfqStatusCriteria.addAndCondition(quotesReceivedCondition);
        rfqStatusCriteria.addAndCondition(CriteriaAPI.getCondition("RFQ_FINALIZED","isRfqFinalized",String.valueOf(true),BooleanOperators.IS));
        rfqStatusCriteria.addAndCondition(CriteriaAPI.getCondition("AWARDED","isAwarded",String.valueOf(false),BooleanOperators.IS));
        rfqStatusCriteria.addAndCondition(CriteriaAPI.getCondition("PO_CREATED","isPoCreated",String.valueOf(false),BooleanOperators.IS));
        rfqStatusCriteria.addAndCondition(CriteriaAPI.getCondition("DISCARDED","isDiscarded",String.valueOf(false),BooleanOperators.IS));

        return rfqStatusCriteria;
    }

    private static FacilioView getAwardedRfqView(String viewName, String viewDisplayName, boolean awarded) {
        FacilioModule rfqModule = ModuleFactory.getRequestForQuotationModule();

        FacilioField createdTime = new FacilioField();
        createdTime.setName("localId");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("LOCAL_ID");
        createdTime.setModule(rfqModule);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

        Criteria criteria = getAwardedRfqCondition(awarded);
        FacilioView statusView = new FacilioView();
        statusView.setName(viewName);
        statusView.setDisplayName(viewDisplayName);
        statusView.setSortFields(sortFields);
        statusView.setCriteria(criteria);

        return statusView;
    }

    private static Criteria getAwardedRfqCondition(boolean awarded) {
        FacilioField awardedField = new FacilioField();
        awardedField.setName("isAwarded");
        awardedField.setColumnName("AWARDED");
        awardedField.setDataType(FieldType.BOOLEAN);
        awardedField.setModule(ModuleFactory.getRequestForQuotationModule());

        Condition awardedCondition = new Condition();
        awardedCondition.setField(awardedField);
        awardedCondition.setOperator(BooleanOperators.IS);
        awardedCondition.setValue(String.valueOf(awarded));

        Criteria rfqStatusCriteria = new Criteria();
        rfqStatusCriteria.addAndCondition(awardedCondition);
        rfqStatusCriteria.addAndCondition(CriteriaAPI.getCondition("RFQ_FINALIZED","isRfqFinalized",String.valueOf(true),BooleanOperators.IS));
        rfqStatusCriteria.addAndCondition(CriteriaAPI.getCondition("QUOTE_RECEIVED","isQuoteReceived",String.valueOf(true),BooleanOperators.IS));
        rfqStatusCriteria.addAndCondition(CriteriaAPI.getCondition("PO_CREATED","isPoCreated",String.valueOf(false),BooleanOperators.IS));
        rfqStatusCriteria.addAndCondition(CriteriaAPI.getCondition("DISCARDED","isDiscarded",String.valueOf(false),BooleanOperators.IS));

        return rfqStatusCriteria;
    }
    private static FacilioView getPoCreatedRfqView(String viewName, String viewDisplayName, boolean poCreated) {
        FacilioModule rfqModule = ModuleFactory.getRequestForQuotationModule();

        FacilioField createdTime = new FacilioField();
        createdTime.setName("localId");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("LOCAL_ID");
        createdTime.setModule(rfqModule);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

        Criteria criteria = getPoCreatedRfqCondition(poCreated);
        FacilioView statusView = new FacilioView();
        statusView.setName(viewName);
        statusView.setDisplayName(viewDisplayName);
        statusView.setSortFields(sortFields);
        statusView.setCriteria(criteria);

        return statusView;
    }

    private static Criteria getPoCreatedRfqCondition(boolean poCreated) {
        FacilioField poCreatedField = new FacilioField();
        poCreatedField.setName("isPoCreated");
        poCreatedField.setColumnName("PO_CREATED");
        poCreatedField.setDataType(FieldType.BOOLEAN);
        poCreatedField.setModule(ModuleFactory.getRequestForQuotationModule());

        Condition poCreatedCondition = new Condition();
        poCreatedCondition.setField(poCreatedField);
        poCreatedCondition.setOperator(BooleanOperators.IS);
        poCreatedCondition.setValue(String.valueOf(poCreated));

        Criteria rfqStatusCriteria = new Criteria();
        rfqStatusCriteria.addAndCondition(poCreatedCondition);
        rfqStatusCriteria.addAndCondition(CriteriaAPI.getCondition("RFQ_FINALIZED","isRfqFinalized",String.valueOf(true),BooleanOperators.IS));
        rfqStatusCriteria.addAndCondition(CriteriaAPI.getCondition("QUOTE_RECEIVED","isQuoteReceived",String.valueOf(true),BooleanOperators.IS));
        rfqStatusCriteria.addAndCondition(CriteriaAPI.getCondition("AWARDED","isAwarded",String.valueOf(true),BooleanOperators.IS));
        rfqStatusCriteria.addAndCondition(CriteriaAPI.getCondition("DISCARDED","isDiscarded",String.valueOf(false),BooleanOperators.IS));
        return rfqStatusCriteria;
    }

    private static FacilioView getDiscardedRfqView(String viewName, String viewDisplayName, boolean discarded) {
        FacilioModule rfqModule = ModuleFactory.getRequestForQuotationModule();

        FacilioField createdTime = new FacilioField();
        createdTime.setName("localId");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("LOCAL_ID");
        createdTime.setModule(rfqModule);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

        Criteria criteria = getDiscardedRfqCondition(discarded);
        FacilioView statusView = new FacilioView();
        statusView.setName(viewName);
        statusView.setDisplayName(viewDisplayName);
        statusView.setSortFields(sortFields);
        statusView.setCriteria(criteria);

        return statusView;
    }

    private static Criteria getDiscardedRfqCondition(boolean discarded) {
        FacilioField discardedField = new FacilioField();
        discardedField.setName("isDiscarded");
        discardedField.setColumnName("DISCARDED");
        discardedField.setDataType(FieldType.BOOLEAN);
        discardedField.setModule(ModuleFactory.getRequestForQuotationModule());

        Condition discardedCondition = new Condition();
        discardedCondition.setField(discardedField);
        discardedCondition.setOperator(BooleanOperators.IS);
        discardedCondition.setValue(String.valueOf(discarded));

        Criteria rfqStatusCriteria = new Criteria();
        rfqStatusCriteria.addAndCondition(discardedCondition);
        return rfqStatusCriteria;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule requestForQuotationModule = modBean.getModule(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION);

        FacilioForm requestForQuotationModuleForm = new FacilioForm();
        requestForQuotationModuleForm.setDisplayName("REQUEST FOR QUOTATION");
        requestForQuotationModuleForm.setName("default_requestForQuotation_web");
        requestForQuotationModuleForm.setModule(requestForQuotationModule);
        requestForQuotationModuleForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        requestForQuotationModuleForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP));

        List<FormField> requestForQuotationModuleFormDefaultFields = new ArrayList<>();
        requestForQuotationModuleFormDefaultFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        requestForQuotationModuleFormDefaultFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        requestForQuotationModuleFormDefaultFields.add(new FormField("storeRoom", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Storeroom", FormField.Required.OPTIONAL, "storeRoom", 3, 2));
        FormField field = new FormField("vendor", FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE, "Vendors", FormField.Required.OPTIONAL,3, 3);
        field.setAllowCreateOptions(true);
        requestForQuotationModuleFormDefaultFields.add(field);
        requestForQuotationModuleFormDefaultFields.add(new FormField("requestedDate", FacilioField.FieldDisplayType.DATE, "Requested Date", FormField.Required.OPTIONAL, 4, 2));
        requestForQuotationModuleFormDefaultFields.add(new FormField("requiredDate", FacilioField.FieldDisplayType.DATE, "Required Date", FormField.Required.OPTIONAL, 4, 3));
        requestForQuotationModuleFormDefaultFields.add(new FormField("expectedReplyDate", FacilioField.FieldDisplayType.DATE, "Expected Reply Date", FormField.Required.OPTIONAL, 5, 2));
        requestForQuotationModuleFormDefaultFields.add(new FormField("requestedBy", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Requested By", FormField.Required.OPTIONAL, "people", 5, 3));
        requestForQuotationModuleFormDefaultFields.add(new FormField("rfqType", FacilioField.FieldDisplayType.SELECTBOX, "RFQ Type", FormField.Required.REQUIRED, 6, 1));

        //fields.add(new FormField("billToAddress", FieldDisplayType.SADDRESS, "BILLING ADDRESS", Required.OPTIONAL, 6, 1));

        List<FormField> shippingAddressFields = new ArrayList<>();
        shippingAddressFields.add(new FormField("shipToAddress", FacilioField.FieldDisplayType.SADDRESS, "SHIPPING ADDRESS", FormField.Required.OPTIONAL, 7, 1));

        List<FormField> lineItemFields = new ArrayList<>();
        lineItemFields.add(new FormField("requestForQuotationLineItems", FacilioField.FieldDisplayType.LINEITEMS, "LINE ITEMS", FormField.Required.REQUIRED, 8, 1));

        List<FormField> requestForQuotationModuleFormFields = new ArrayList<>();
        requestForQuotationModuleFormFields.addAll(requestForQuotationModuleFormDefaultFields);
        requestForQuotationModuleFormFields.addAll(shippingAddressFields);
        requestForQuotationModuleFormFields.addAll(lineItemFields);
//        requestForQuotationModuleForm.setFields(requestForQuotationModuleFormFields);

        FormSection defaultSection = new FormSection("Request For Quotation", 1, requestForQuotationModuleFormDefaultFields, true);
        defaultSection.setSectionType(FormSection.SectionType.FIELDS);

        FormSection shippingSection = new FormSection("Shipping Address", 2, shippingAddressFields, true);
        shippingSection.setSectionType(FormSection.SectionType.FIELDS);

        FormSection lineItemSection = new FormSection("Line Items", 3, lineItemFields, true);
        shippingSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormSection> sections = new ArrayList<>();
        sections.add(defaultSection);
        sections.add(shippingSection);
        sections.add(lineItemSection);

        requestForQuotationModuleForm.setSections(sections);
        requestForQuotationModuleForm.setIsSystemForm(true);
        requestForQuotationModuleForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(requestForQuotationModuleForm);
    }

    @Override
    public List<GlimpseContext> getModuleGlimpse() throws Exception{

        List<String> fieldNames = new ArrayList<>();
        fieldNames.add("requiredDate");
        fieldNames.add("expectedReplyDate");

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
            appNameVsPage.put(appName,getRequestForQuotationViewPage(app, module));
        }
        return appNameVsPage;
    }
    private List<PagesContext> getRequestForQuotationViewPage(ApplicationContext app, FacilioModule module) throws Exception {
        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION_ACTIVITY);
        return new ModulePages()
                .addPage("requestForQuotationViewPage", "Default Request For Quotation View Page", "", null, false, true, true)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("requestForQuotationPdfViewer", null, null)
                .addWidget("requestForQuotationPdfViewerWidget", null, PageWidget.WidgetType.PDF_VIEWER, "flexiblewebpdfviewer_19", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("vendors", "Vendors", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("vendorsfields", null, null)
                .addWidget("vendorsFieldsWidget", "Vendors", PageWidget.WidgetType.REQUEST_FOR_QUOTATION_VENDOR_LIST, "flexiblewebrequestforquotationvendorlist_9", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("noteandinformation","Notes & Information", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("requestForQuotationSummaryFields", null, null)
                .addWidget("requestForQuotationSummaryFieldsWidget", "Request For Quotation Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, RequestForQuotationTemplatePage.getSummaryWidgetDetails(module.getName(), app))
                .widgetDone()
                .sectionDone()
                .addSection("requestForQuotationwidgetGroup", null,  null)
                .addWidget("requestForQuotationcommentandattachmentwidgetgroupwidget", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0,  null, RequestForQuotationTemplatePage.getWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("related", "Related", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("requestForQuotationrelatedlist", "Related List", "List of related records across modules")
                .addWidget("requestForQuotationbulkrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST,"flexiblewebbulkrelatedlist_6", 0, 0,  null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module))
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
