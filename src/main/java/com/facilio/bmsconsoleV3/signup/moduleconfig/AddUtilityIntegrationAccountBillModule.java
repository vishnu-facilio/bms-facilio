package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;

import java.util.*;

public class AddUtilityIntegrationAccountBillModule extends BaseModuleConfig {
    public AddUtilityIntegrationAccountBillModule(){
        setModuleName(FacilioConstants.Ocr.UTILITY_ACCOUNT_BILL);
    }
    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        long orgId = Objects.requireNonNull(AccountUtil.getCurrentOrg()).getId();

        addUtilityAccountBillModule(modBean,orgId);
//        addSystemButton();

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() throws Exception {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        List<FacilioView> utilityAccountBill =  getAllUtilityAccountBillView();

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.Ocr.UTILITY_ACCOUNT_BILL);
        groupDetails.put("views", utilityAccountBill);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static List<FacilioView> getAllUtilityAccountBillView() throws Exception {

        FacilioModule module = new FacilioModule(FacilioConstants.Ocr.UTILITY_ACCOUNT_BILL,"Utility Account Bill","Utility_Integration_Account_Bill",
                FacilioModule.ModuleType.BASE_ENTITY,true);

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

        ArrayList<FacilioView> utilityAccountBill = new ArrayList<FacilioView>();
        int order = 1;

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Bills");
        allView.setModuleName(module.getName());
        allView.setSortFields(sortFields);
        allView.setFields(getAllViewColumns());

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.ENERGY_APP);
        allView.setAppLinkNames(appLinkNames);

        utilityAccountBill.add(allView.setOrder(order++));

        return utilityAccountBill;
    }

    private static List<ViewField> getAllViewColumns() {
        List<ViewField> columns = new ArrayList<ViewField>();
        columns.add(new ViewField("billingAccount","Billing Account"));
        columns.add(new ViewField("billedTo","Billed To"));
        columns.add(new ViewField("billStatementDate","Bill Statement Date"));
        columns.add(new ViewField("billTotalCost","Bill Total Cost"));

        return columns;
    }

    private FacilioModule addUtilityAccountBillModule(ModuleBean moduleBean, long orgId) throws Exception{
        List<FacilioModule> modules = new ArrayList<>();
        FacilioModule utilityAccountBillModule = new FacilioModule(FacilioConstants.Ocr.UTILITY_ACCOUNT_BILL,"Utility Account Bill","Utility_Integration_Account_Bill",
                FacilioModule.ModuleType.BASE_ENTITY,true);
        utilityAccountBillModule.setOrgId(orgId);

        List<FacilioField> fields = new ArrayList<>();

        SystemEnumField utilityProvider = (SystemEnumField) FieldFactory.getDefaultField("utilityProvider", "Utility Provider", "UTILITY_PROVIDER", FieldType.SYSTEM_ENUM);
        utilityProvider.setEnumName("UtilityProviderEnum");
        fields.add(utilityProvider);

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

        LookupField meter =  FieldFactory.getDefaultField("meter","Meter","METER_ID",FieldType.LOOKUP);
        meter.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.METER),"Meter module doesn't exists."));
        fields.add(meter);

        StringField billedTo = (StringField) FieldFactory.getDefaultField("billedTo", "Billed To", "BILLED_TO", FieldType.STRING);
        fields.add(billedTo);

        StringField billingAddress = (StringField) FieldFactory.getDefaultField("billingAddress", "Bill Address", "BILLING_ADDRESS", FieldType.STRING);
        fields.add(billingAddress);

        StringField billingAccount = (StringField) FieldFactory.getDefaultField("billingAccount", "Account Number", "BILLING_ACCOUNT", FieldType.STRING, true);
        fields.add(billingAccount);

        SystemEnumField accountType = (SystemEnumField) FieldFactory.getDefaultField("accountType", "Account Type", "ACCOUNT_TYPE", FieldType.SYSTEM_ENUM);
        accountType.setEnumName("OcrUtilityAccountType");
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

        LookupField moduleStateField = (LookupField) FieldFactory.getField("moduleState", "Status", "MODULE_STATE", utilityAccountBillModule, FieldType.LOOKUP);
        moduleStateField.setDefault(true);
        moduleStateField.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        moduleStateField.setLookupModule(moduleBean.getModule("ticketstatus"));
        fields.add(moduleStateField);

        FacilioField stateFlowIdField = FieldFactory.getField("stateFlowId", "State Flow Id", "STATE_FLOW_ID", utilityAccountBillModule, FieldType.NUMBER);
        stateFlowIdField.setDefault(true);
        stateFlowIdField.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
        fields.add(stateFlowIdField);

        LookupField approvalStateField = (LookupField) FieldFactory.getField("approvalStatus", "Approval Status", "APPROVAL_STATE", utilityAccountBillModule, FieldType.LOOKUP);
        approvalStateField.setDefault(true);
        approvalStateField.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        approvalStateField.setLookupModule(moduleBean.getModule("ticketstatus"));
        fields.add(approvalStateField);

        FacilioField approvalFlowIdField = FieldFactory.getField("approvalFlowId", "Approval Flow Id", "APPROVAL_FLOW_ID", utilityAccountBillModule, FieldType.NUMBER);
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

        utilityAccountBillModule.setFields(fields);
        modules.add(utilityAccountBillModule);
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();

        return utilityAccountBillModule;
    }
}
