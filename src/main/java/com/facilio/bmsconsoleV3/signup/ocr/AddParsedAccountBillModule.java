package com.facilio.bmsconsoleV3.signup.ocr;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.context.ocr.ParsedAccountBillContext;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;

import java.util.*;

public class AddParsedAccountBillModule extends BaseModuleConfig {

    public AddParsedAccountBillModule(){
        setModuleName(FacilioConstants.Ocr.PARSED_ACCOUNT_BILL);
    }
    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        long orgId = Objects.requireNonNull(AccountUtil.getCurrentOrg()).getId();

        addParsedAccountBillModule(modBean,orgId);
//        addSystemButton();

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() throws Exception {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;


        List<FacilioView> parsedAccountBill =  getAllParsedAccountBillView();

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.Ocr.PARSED_ACCOUNT_BILL);
        groupDetails.put("views", parsedAccountBill);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static List<FacilioView> getAllParsedAccountBillView() throws Exception {

        FacilioModule module = new FacilioModule(FacilioConstants.Ocr.PARSED_ACCOUNT_BILL,"Parsed Account Bill","Parsed_Integration_Account_bill",
                FacilioModule.ModuleType.BASE_ENTITY,true);

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

        ArrayList<FacilioView> parsedBill = new ArrayList<FacilioView>();
        int order = 1;

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Bills");
        allView.setModuleName(module.getName());
        allView.setSortFields(sortFields);
        allView.setFields(getAllViewColumns());

        FacilioView pendingView = new FacilioView();
        pendingView.setName("pending");
        pendingView.setDisplayName("Pending");
        pendingView.setModuleName(module.getName());
        pendingView.setSortFields(sortFields);
        pendingView.setFields(getAllViewColumns());
        pendingView.setCriteria(getParsedAccountBillViewCriteria(ParsedAccountBillContext.ParsedAccountBillStatus.PENDING));

        FacilioView approvedBillView = new FacilioView();
        approvedBillView.setName("approved");
        approvedBillView.setDisplayName("Approved");
        approvedBillView.setModuleName(module.getName());
        approvedBillView.setSortFields(sortFields);
        approvedBillView.setFields(getAllViewColumns());
        approvedBillView.setCriteria(getParsedAccountBillViewCriteria(ParsedAccountBillContext.ParsedAccountBillStatus.APPROVED));

        FacilioView rejectedBillView = new FacilioView();
        rejectedBillView.setName("rejected");
        rejectedBillView.setDisplayName("Rejected");
        rejectedBillView.setModuleName(module.getName());
        rejectedBillView.setSortFields(sortFields);
        rejectedBillView.setFields(getAllViewColumns());
        rejectedBillView.setCriteria(getParsedAccountBillViewCriteria(ParsedAccountBillContext.ParsedAccountBillStatus.REJECTED));

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.ENERGY_APP);
        allView.setAppLinkNames(appLinkNames);
        pendingView.setAppLinkNames(appLinkNames);
        approvedBillView.setAppLinkNames(appLinkNames);
        rejectedBillView.setAppLinkNames(appLinkNames);

        parsedBill.add(allView.setOrder(order++));
        parsedBill.add(pendingView.setOrder(order++));
        parsedBill.add(approvedBillView.setOrder(order++));
        parsedBill.add(rejectedBillView.setOrder(order++));

        return parsedBill;
    }

    private static List<ViewField> getAllViewColumns() {
        List<ViewField> columns = new ArrayList<ViewField>();
        columns.add(new ViewField("billingAccount","Billing Account"));
        columns.add(new ViewField("billedTo","Billed To"));
        columns.add(new ViewField("billStatementDate","Bill Statement Date"));
        columns.add(new ViewField("billTotalCost","Bill Total Cost"));

        return columns;
    }

    private static Criteria getParsedAccountBillViewCriteria(ParsedAccountBillContext.ParsedAccountBillStatus status) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Criteria billUploadedViewCriteria = new Criteria();
        FacilioField statusField = modBean.getField("status", FacilioConstants.Ocr.PARSED_ACCOUNT_BILL);
        if(statusField != null) {
            Condition billUploadedViewCondition = new Condition();
            billUploadedViewCondition.setField(statusField);
            billUploadedViewCondition.setOperator(NumberOperators.EQUALS);
            billUploadedViewCondition.setValue(String.valueOf(status.getIndex()));
            billUploadedViewCriteria.addAndCondition(billUploadedViewCondition);
        }
        return billUploadedViewCriteria;
    }

    private FacilioModule addParsedAccountBillModule(ModuleBean moduleBean, long orgId) throws Exception{
        List<FacilioModule> modules = new ArrayList<>();
        FacilioModule parsedAccountBillModule = new FacilioModule(FacilioConstants.Ocr.PARSED_ACCOUNT_BILL,"Parsed Account Bill","Parsed_Account_bill",
                FacilioModule.ModuleType.BASE_ENTITY,true);
        parsedAccountBillModule.setOrgId(orgId);

        List<FacilioField> fields = new ArrayList<>();

        SystemEnumField utilityProvider = (SystemEnumField) FieldFactory.getDefaultField("utilityProvider", "Utility Provider", "UTILITY_PROVIDER", FieldType.SYSTEM_ENUM);
        utilityProvider.setEnumName("UtilityProviderEnum");
        fields.add(utilityProvider);
        fields.add(FieldFactory.getDefaultField("billMonth","Bill Month","BILL_MONTH",FieldType.DATE));

        NumberField invoiceNo = (NumberField) FieldFactory.getDefaultField("invoiceNo", "Invoice No", "INVOICE_NUMBER", FieldType.NUMBER);
        fields.add(invoiceNo);

        NumberField vatNo = (NumberField) FieldFactory.getDefaultField("vatNo", "VAT No", "VAT_NUMBER", FieldType.NUMBER);
        fields.add(vatNo);

        DateField billStartDate =  FieldFactory.getDefaultField("billStartDate", "Bill Period Start Date", "BILL_START_DATE", FieldType.DATE,FacilioField.FieldDisplayType.DATE);
        fields.add(billStartDate);

        DateField billEndDate =  FieldFactory.getDefaultField("billEndDate", "Bill Period End Date", "BILL_END_DATE", FieldType.DATE,FacilioField.FieldDisplayType.DATE);
        fields.add(billEndDate);

        DateField dueDate =  FieldFactory.getDefaultField("dueDate", "Due Date", "DUE_DATE", FieldType.DATE,FacilioField.FieldDisplayType.DATE);
        fields.add(dueDate);

        LookupField billTemplate =  FieldFactory.getDefaultField("billTemplate","Bill Template","TEMPLATE_ID",FieldType.LOOKUP);
        billTemplate.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.Ocr.BILL_TEMPLATE),"Bill Template module doesn't exists."));
        fields.add(billTemplate);
        
        LookupField parsedDocId =  FieldFactory.getDefaultField("parsedDocId","Parsed Doc Id","OCR_PARSED_DOC_ID",FieldType.LOOKUP);
        parsedDocId.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.Ocr.OCR_TEMPLATE_PARSED_RESULT),"Bill Template module doesn't exists."));
        fields.add(parsedDocId);
        
        LookupField originalDocId =  FieldFactory.getDefaultField("originalDocId","Original Doc Id","OCR_ORIGINAL_DOC_ID",FieldType.LOOKUP);
        originalDocId.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.Ocr.ACTUAL_BILL),"Bill Template module doesn't exists."));
        fields.add(originalDocId);

        LookupField meter =  FieldFactory.getDefaultField("meter","Meter","METER_ID",FieldType.LOOKUP);
        meter.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.METER),"Meter module doesn't exists."));
        fields.add(meter);

        SystemEnumField billStatus = (SystemEnumField) FieldFactory.getDefaultField("status", "Status", "STATUS", FieldType.SYSTEM_ENUM);
        billStatus.setEnumName("ParsedAccountBillStatus");
        fields.add(billStatus);

        StringField billedTo = (StringField) FieldFactory.getDefaultField("billedTo", "Billed To", "BILLED_TO", FieldType.STRING);
        fields.add(billedTo);

        StringField billingAddress = (StringField) FieldFactory.getDefaultField("billingAddress", "Bill Address", "BILLING_ADDRESS", FieldType.STRING);
        fields.add(billingAddress);

        StringField billingAccount = (StringField) FieldFactory.getDefaultField("billingAccount", "Account Number", "BILLING_ACCOUNT", FieldType.STRING, true);
        fields.add(billingAccount);

        SystemEnumField accountType = (SystemEnumField) FieldFactory.getDefaultField("accountType", "Account Type", "ACCOUNT_TYPE", FieldType.SYSTEM_ENUM);
        accountType.setEnumName("OcrAccountType");
        fields.add(accountType);

        DateField billStatementDate =  FieldFactory.getDefaultField("billStatementDate", "Bill Statement Date", "BILL_STATEMENT_DATE", FieldType.DATE,FacilioField.FieldDisplayType.DATE);
        fields.add(billStatementDate);

        NumberField billTotalCost = FieldFactory.getDefaultField("billTotalCost","Charges/Cost","BILL_TOTAL_COST", FieldType.DECIMAL);
        fields.add(billTotalCost);

        NumberField billDiscount = FieldFactory.getDefaultField("discount","Discount","DISCOUNT", FieldType.DECIMAL);
        fields.add(billDiscount);

        NumberField tax = FieldFactory.getDefaultField("tax","Tax (%)","Tax", FieldType.DECIMAL);
        fields.add(tax);

        NumberField currentMonthTotal = FieldFactory.getDefaultField("currentMonthTotal","Current Month Total","CURRENT_MONTH_TOTAL", FieldType.DECIMAL);
        fields.add(currentMonthTotal);

        NumberField adjustments = FieldFactory.getDefaultField("adjustments","Adjustments","ADJUSTMENTS", FieldType.DECIMAL);
        fields.add(adjustments);

        NumberField previousMonthBalance = FieldFactory.getDefaultField("previousMonthBalance","Previous Month Balance","PREVIOUS_MONTH_BALANCE", FieldType.DECIMAL);
        fields.add(previousMonthBalance);

        NumberField additionalCharges = FieldFactory.getDefaultField("additionalCharges","Additional Charges","ADDITIONAL_CHARGES", FieldType.DECIMAL);
        fields.add(additionalCharges);

        NumberField currentDue = FieldFactory.getDefaultField("currentDue","Total/Current Due","CURRENT_DUE", FieldType.DECIMAL);
        fields.add(currentDue);

        LookupField moduleStateField = (LookupField) FieldFactory.getField("moduleState", "Status", "MODULE_STATE", parsedAccountBillModule, FieldType.LOOKUP);
        moduleStateField.setDefault(true);
        moduleStateField.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        moduleStateField.setLookupModule(moduleBean.getModule("ticketstatus"));
        fields.add(moduleStateField);

        FacilioField stateFlowIdField = FieldFactory.getField("stateFlowId", "State Flow Id", "STATE_FLOW_ID", parsedAccountBillModule, FieldType.NUMBER);
        stateFlowIdField.setDefault(true);
        stateFlowIdField.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
        fields.add(stateFlowIdField);

        LookupField approvalStateField = (LookupField) FieldFactory.getField("approvalStatus", "Approval Status", "APPROVAL_STATE", parsedAccountBillModule, FieldType.LOOKUP);
        approvalStateField.setDefault(true);
        approvalStateField.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        approvalStateField.setLookupModule(moduleBean.getModule("ticketstatus"));
        fields.add(approvalStateField);

        FacilioField approvalFlowIdField = FieldFactory.getField("approvalFlowId", "Approval Flow Id", "APPROVAL_FLOW_ID", parsedAccountBillModule, FieldType.NUMBER);
        approvalFlowIdField.setDefault(true);
        approvalFlowIdField.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
        fields.add(approvalFlowIdField);

        LookupField createdByPeople = FieldFactory.getDefaultField("sysCreatedByPeople","Created By","SYS_CREATED_BY",FieldType.LOOKUP);
        createdByPeople.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        fields.add(createdByPeople);

        LookupField modifiedByPeople = FieldFactory.getDefaultField("sysModifiedByPeople","Modified By","SYS_MODIFIED_BY",FieldType.LOOKUP);
        modifiedByPeople.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        fields.add(modifiedByPeople);

        fields.add(FieldFactory.getDefaultField("sysCreatedTime", "Created Time", "SYS_CREATED_TIME", FieldType.DATE_TIME));
        fields.add(FieldFactory.getDefaultField("sysModifiedTime", "Modified Time", "SYS_MODIFIED_TIME", FieldType.DATE_TIME));

        parsedAccountBillModule.setFields(fields);
        modules.add(parsedAccountBillModule);
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();

        return parsedAccountBillModule;
    }

//    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {
//        Map<String,List<PagesContext>> appNameVsPage = new HashMap<>();
//        List<String> appNames = new ArrayList<>();
//        appNames.add(FacilioConstants.ApplicationLinkNames.ENERGY_APP);
//
//        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
//        FacilioModule module = modBean.getModule(getModuleName());
//
//        for (String appName : appNames) {
//            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
//            appNameVsPage.put(appName,getSystemPage(app, module));
//        }
//        return appNameVsPage;
//    }
//
//    private static List<PagesContext> getSystemPage(ApplicationContext app,FacilioModule module) throws Exception {
//
//        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
//        FacilioModule billModule = modBean.getModule(FacilioConstants.Ocr.PARSED_BILL);
//        String pageName, pageDisplayName;
//        pageName = billModule.getName().toLowerCase().replaceAll("[^a-zA-Z0-9]+", "") + "defaultpage";
//        pageDisplayName = "Default "+ billModule.getDisplayName()+" Page ";
//
//        List<PagesContext> parsedBillPages = new ArrayList<>();
//
//        PagesContext parsedBillTemplatePage = new PagesContext(pageName, pageDisplayName, "", null, false, true, true)
//                .addLayout(PagesContext.PageLayoutType.WEB)
//                .addTab("parsedbillsummary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
//                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
//                .addSection("", "", null)
//                .addWidget("parsedbillsummaryfieldswidget", "Parsed  Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_3", 0, 0, null, getSummaryWidgetDetails((FacilioConstants.Ocr.PARSED_BILL),app))
//                .widgetDone()
//                .addWidget("parsedBillLineItem", "Bill", PageWidget.WidgetType.OCR_LINE_ITEM_WIDGET, "flexiblewebparsedbilllineitem_8", 0, 0, null, null)
//                .widgetDone()
//                .sectionDone()
//                .columnDone()
//                .tabDone()
//
//                .addTab("parsedbilllog", "Logs", PageTabContext.TabType.SIMPLE, true, null)
//                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
//                .addSection("parsedbilllogsection", null, null)
//                .addWidget("parsedbilllogwidget", null, PageWidget.WidgetType.OCR_RULE_LOGS, "flexiblewebocrrulelogs_9", 0, 0, null,null)
//                .widgetDone()
//                .sectionDone()
//                .columnDone()
//                .tabDone()
//                .layoutDone();
//
//        parsedBillPages.add(parsedBillTemplatePage);
//
//        return parsedBillPages;
//    }
//    private static JSONObject getSummaryWidgetDetails(String moduleName, ApplicationContext app) throws Exception {
//        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
//        FacilioModule module = moduleBean.getModule(moduleName);
//
//        FacilioField supplier = moduleBean.getField("utilityID",moduleName);
//        FacilioField phoneField = moduleBean.getField("serviceTariff", moduleName);
//        FacilioField emailField = moduleBean.getField("serviceClass", moduleName);
//        FacilioField unitField = moduleBean.getField("billTotalUnit", moduleName);
//        FacilioField volumeField = moduleBean.getField("billTotalVolume", moduleName);
//        FacilioField costField = moduleBean.getField("billTotalCost", moduleName);
//        FacilioField createdField = moduleBean.getField("sysCreatedTime", moduleName);
//        FacilioField createdByField = moduleBean.getField("sysCreatedByPeople",moduleName);
//        FacilioField modifiedField = moduleBean.getField("sysModifiedByPeople",moduleName);
//        FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime",moduleName);
//
//
//        SummaryWidget pageWidget = new SummaryWidget();
//
//        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();
//
//        addSummaryFieldInWidgetGroup(widgetGroup, supplier, 1, 1, 1);
//        addSummaryFieldInWidgetGroup(widgetGroup, phoneField, 1, 2, 1);
//        addSummaryFieldInWidgetGroup(widgetGroup, emailField, 1, 3, 1);
//        addSummaryFieldInWidgetGroup(widgetGroup, unitField, 1, 4, 1);
//        addSummaryFieldInWidgetGroup(widgetGroup, volumeField, 2, 1, 1);
//        addSummaryFieldInWidgetGroup(widgetGroup, costField, 2, 2, 1);
//        addSummaryFieldInWidgetGroup(widgetGroup, createdField, 2, 3, 1);
//        addSummaryFieldInWidgetGroup(widgetGroup, createdByField, 2, 4, 1);
//        addSummaryFieldInWidgetGroup(widgetGroup, modifiedField, 3, 1, 1);
//        addSummaryFieldInWidgetGroup(widgetGroup, sysModifiedTimeField, 3, 2, 1);
//
//        widgetGroup.setName("moduleDetails");
//        widgetGroup.setDisplayName("");
//        widgetGroup.setColumns(4);
//
//
//
//        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
//        widgetGroupList.add(widgetGroup);
//
//        pageWidget.setDisplayName("");
//        pageWidget.setModuleId(module.getModuleId());
//        pageWidget.setAppId(app.getId());
//        pageWidget.setGroups(widgetGroupList);
//
//        return FieldUtil.getAsJSON(pageWidget);
//
//    }
//
//    private static void addSummaryFieldInWidgetGroup(SummaryWidgetGroup widgetGroup, FacilioField field, int rowIndex, int colIndex, int colSpan) {
//        if (field != null) {
//            SummaryWidgetGroupFields summaryField = new SummaryWidgetGroupFields();
//            summaryField.setName(field.getName());
//            summaryField.setDisplayName(field.getDisplayName());
//            summaryField.setFieldId(field.getFieldId());
//            summaryField.setRowIndex(rowIndex);
//            summaryField.setColIndex(colIndex);
//            summaryField.setColSpan(colSpan);
//
//            if (widgetGroup.getFields() == null) {
//                widgetGroup.setFields(new ArrayList<>(Arrays.asList(summaryField)));
//            } else {
//                widgetGroup.getFields().add(summaryField);
//            }
//        }
//    }
//
//    public static void addSystemButton() throws Exception {
//        String moduleName = FacilioConstants.Ocr.PARSED_BILL;
//
//        List<Integer> statusList = new ArrayList<>();
//        statusList.add(ParsedBillContext.ParsedBillStatus.REJECTED.getVal());
//        statusList.add(ParsedBillContext.ParsedBillStatus.APPROVED.getVal());
//
//        SystemButtonRuleContext listApproveButton = new SystemButtonRuleContext();
//        listApproveButton.setName("Approve Bill");
//        listApproveButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
//        listApproveButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
//        listApproveButton.setIdentifier("ocr_approve_list");
//        listApproveButton.setPermissionRequired(true);
//        listApproveButton.setPermission("UPDATE");
//
//        Criteria listApproveCriteria = new Criteria();
//        listApproveCriteria.addAndCondition(CriteriaAPI.getCondition("STATUS","status", String.valueOf(ParsedBillContext.ParsedBillStatus.APPROVED.getVal()), EnumOperators.ISN_T));
//
//        listApproveButton.setCriteria(listApproveCriteria);
//        SystemButtonApi.addSystemButton(moduleName, listApproveButton);
//
//        SystemButtonRuleContext listRejectButton = new SystemButtonRuleContext();
//        listRejectButton.setName("Reject Bill");
//        listRejectButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
//        listRejectButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
//        listRejectButton.setIdentifier("ocr_reject_list");
//        listRejectButton.setPermissionRequired(true);
//        listRejectButton.setPermission("UPDATE");
//
//        Criteria listRejectCriteria = new Criteria();
//        listRejectCriteria.addAndCondition(CriteriaAPI.getCondition("STATUS","status",  StringUtils.join(statusList, ","), EnumOperators.ISN_T));
//
//        listRejectButton.setCriteria(listRejectCriteria);
//        SystemButtonApi.addSystemButton(moduleName, listRejectButton);
//
//        SystemButtonRuleContext listOpenSummaryButton = new SystemButtonRuleContext();
//        listOpenSummaryButton.setName("Open Summary");
//        listOpenSummaryButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
//        listOpenSummaryButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
//        listOpenSummaryButton.setIdentifier("ocr_open_summary_list");
//        listOpenSummaryButton.setPermissionRequired(true);
//        listOpenSummaryButton.setPermission("UPDATE");
//        SystemButtonApi.addSystemButton(moduleName, listOpenSummaryButton);
//
//        SystemButtonRuleContext summaryApproveButton = new SystemButtonRuleContext();
//        summaryApproveButton.setName("Approve Bill");
//        summaryApproveButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
//        summaryApproveButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
//        summaryApproveButton.setIdentifier("ocr_approve_summary");
//        summaryApproveButton.setPermissionRequired(true);
//        summaryApproveButton.setPermission("UPDATE");
//
//        Criteria approveCriteria = new Criteria();
//        approveCriteria.addAndCondition(CriteriaAPI.getCondition("STATUS","status", String.valueOf(ParsedBillContext.ParsedBillStatus.APPROVED.getVal()), EnumOperators.ISN_T));
//
//        summaryApproveButton.setCriteria(approveCriteria);
//        SystemButtonApi.addSystemButton(moduleName, summaryApproveButton);
//
//        SystemButtonRuleContext summaryRejectButton = new SystemButtonRuleContext();
//        summaryRejectButton.setName("Reject Bill");
//        summaryRejectButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
//        summaryRejectButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
//        summaryRejectButton.setIdentifier("ocr_reject_summary");
//        summaryRejectButton.setPermissionRequired(true);
//        summaryRejectButton.setPermission("UPDATE");
//
//        Criteria rejectCriteria = new Criteria();
//        rejectCriteria.addAndCondition(CriteriaAPI.getCondition("STATUS","status", StringUtils.join(statusList, ","), EnumOperators.ISN_T));
//
//        summaryRejectButton.setCriteria(rejectCriteria);
//        SystemButtonApi.addSystemButton(moduleName, summaryRejectButton);
//
//        SystemButtonRuleContext bulkApproveBtn = new SystemButtonRuleContext();
//        bulkApproveBtn.setName("Approve All");
//        bulkApproveBtn.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
//        bulkApproveBtn.setPositionType(CustomButtonRuleContext.PositionType.LIST_BAR.getIndex());
//        bulkApproveBtn.setIdentifier("ocr_approve_bulk");
//        bulkApproveBtn.setPermissionRequired(true);
//        bulkApproveBtn.setPermission("PATCH");
//
//        Criteria bulkApproveCriteria = new Criteria();
//        bulkApproveCriteria.addAndCondition(CriteriaAPI.getCondition("STATUS","status", String.valueOf(ParsedBillContext.ParsedBillStatus.APPROVED.getVal()), EnumOperators.ISN_T));
//
//        bulkApproveBtn.setCriteria(bulkApproveCriteria);
//        SystemButtonApi.addSystemButton(moduleName, bulkApproveBtn);
//
//        SystemButtonRuleContext bulkRejectBtn = new SystemButtonRuleContext();
//        bulkRejectBtn.setName("Reject All");
//        bulkRejectBtn.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
//        bulkRejectBtn.setPositionType(CustomButtonRuleContext.PositionType.LIST_BAR.getIndex());
//        bulkRejectBtn.setIdentifier("ocr_reject_bulk");
//        bulkRejectBtn.setPermissionRequired(true);
//        bulkRejectBtn.setPermission("PATCH");
//
//        Criteria bulkRejectCriteria = new Criteria();
//        bulkRejectCriteria.addAndCondition(CriteriaAPI.getCondition("STATUS","status", StringUtils.join(statusList, ","), EnumOperators.ISN_T));
//
//        bulkRejectBtn.setCriteria(bulkRejectCriteria);
//        SystemButtonApi.addSystemButton(moduleName, bulkRejectBtn);
//
//    }
}
