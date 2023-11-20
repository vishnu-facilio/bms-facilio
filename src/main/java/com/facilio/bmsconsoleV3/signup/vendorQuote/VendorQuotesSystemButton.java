package com.facilio.bmsconsoleV3.signup.vendorQuote;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.SystemButtonAppRelContext;
import com.facilio.bmsconsole.workflow.rule.SystemButtonRuleContext;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationContext;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.*;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VendorQuotesSystemButton extends SignUpData {
    @Override
    public void addData() throws Exception {
        addSystemButtons();
    }
    public static void addSystemButtons() throws Exception {
        for (SystemButtonRuleContext btn:getSystemButtons()){
            SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.VENDOR_QUOTES, btn);
        }
    }
    public static List<SystemButtonRuleContext> getSystemButtons() throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VENDOR_QUOTES);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(Constants.getModBean().getAllFields(module.getName()));

        List<SystemButtonRuleContext> btnList = new ArrayList<>();

        SystemButtonRuleContext addQuote = new SystemButtonRuleContext();
        addQuote.setName("Add quote");
        addQuote.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        addQuote.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        addQuote.setIdentifier("addQuote");
        addQuote.setPermissionRequired(true);
        addQuote.setPermission(AccountConstants.ModulePermission.UPDATE.name());
        addQuote.setCriteria(checkAddQuotePermission(module,fieldMap));

        SystemButtonRuleContext updateQuote = new SystemButtonRuleContext();
        updateQuote.setName("Update quote");
        updateQuote.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        updateQuote.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        updateQuote.setIdentifier("updateQuote");
        updateQuote.setPermissionRequired(true);
        updateQuote.setPermission(AccountConstants.ModulePermission.UPDATE.name());
        updateQuote.setCriteria(checkAddQuotePermission(module,fieldMap));


        SystemButtonRuleContext submitButton = new SystemButtonRuleContext();
        submitButton.setName("Submit");
        submitButton.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        submitButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        submitButton.setIdentifier("submitQuote");
        submitButton.setPermissionRequired(true);
        submitButton.setPermission(AccountConstants.ModulePermission.UPDATE.name());
        submitButton.setCriteria(checkAddQuotePermission(module,fieldMap));


        SystemButtonRuleContext negotiateButton = new SystemButtonRuleContext();
        negotiateButton.setName("Negotiate");
        negotiateButton.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        negotiateButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        negotiateButton.setIdentifier("negotiateQuote");
        negotiateButton.setPermissionRequired(true);
        negotiateButton.setPermission(AccountConstants.ModulePermission.UPDATE.name());

        List<SystemButtonAppRelContext> systemButtonAppRels = new ArrayList<>();

        SystemButtonAppRelContext mainAppNegotiateBtn = new SystemButtonAppRelContext();
        SystemButtonAppRelContext maintenanceNegotiateBtn = new SystemButtonAppRelContext();

        mainAppNegotiateBtn.setAppId(ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));
        maintenanceNegotiateBtn.setAppId(ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        systemButtonAppRels.add(mainAppNegotiateBtn);
        systemButtonAppRels.add(maintenanceNegotiateBtn);
        negotiateButton.setSystemButtonAppRels(systemButtonAppRels);

        negotiateButton.setCriteria(checkPermissionForNegotiation(fieldMap));


        SystemButtonRuleContext downloadRFQButton = new SystemButtonRuleContext();
        downloadRFQButton.setName("Download RFQ");
        downloadRFQButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        downloadRFQButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        downloadRFQButton.setIdentifier("downloadRFQ");
        downloadRFQButton.setPermissionRequired(true);
        downloadRFQButton.setPermission(AccountConstants.ModulePermission.UPDATE.name());


        SystemButtonRuleContext goToRFQButton = new SystemButtonRuleContext();
        goToRFQButton.setName("Go To RFQ");
        goToRFQButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        goToRFQButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        goToRFQButton.setIdentifier("goToRFQ");
        goToRFQButton.setPermissionRequired(true);
        goToRFQButton.setPermission(AccountConstants.ModulePermission.READ.name());


        btnList.add(addQuote);
        btnList.add(updateQuote);
        btnList.add(submitButton);
        btnList.add(negotiateButton);
        btnList.add(downloadRFQButton);
        btnList.add(goToRFQButton);


        //LIST BUTTONS
        SystemButtonRuleContext exportAsCSVButton = new SystemButtonRuleContext();
        exportAsCSVButton.setName("Export As CSV");
        exportAsCSVButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        exportAsCSVButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_TOP.getIndex());
        exportAsCSVButton.setIdentifier("export_as_csv");
        exportAsCSVButton.setPermissionRequired(true);
        exportAsCSVButton.setPermission("EXPORT");
        btnList.add(exportAsCSVButton);



        SystemButtonRuleContext exportAsExcelButton = new SystemButtonRuleContext();
        exportAsExcelButton.setName("Export As Excel");
        exportAsExcelButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        exportAsExcelButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_TOP.getIndex());
        exportAsExcelButton.setIdentifier("export_as_excel");
        exportAsExcelButton.setPermissionRequired(true);
        exportAsExcelButton.setPermission("EXPORT");
        btnList.add(exportAsExcelButton);

        return btnList;
    }
    private static Criteria checkRfqAndApprovalPermission(Map<String,FacilioField> fieldMap) throws Exception{
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("approvalFlowId"),"", CommonOperators.IS_EMPTY));
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("approvalStatus"),"", CommonOperators.IS_EMPTY));


        Map<String,FacilioField> oneLevelFieldMap = FieldFactory.getAsMap(Constants.getModBean().getAllFields(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION));
        Criteria oneLevelCriteria=new Criteria();
        oneLevelCriteria.addAndCondition(CriteriaAPI.getCondition(oneLevelFieldMap.get("isRfqFinalized"),String.valueOf(true), BooleanOperators.IS));
        oneLevelCriteria.addAndCondition(CriteriaAPI.getCondition(oneLevelFieldMap.get("isQuoteReceived"),String.valueOf(false), BooleanOperators.IS));

        Condition oneLevelCondition=new Condition();
        oneLevelCondition.setOperator(LookupOperator.LOOKUP);
        oneLevelCondition.setModuleName(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION);
        oneLevelCondition.setFieldName("requestForQuotation");
        oneLevelCondition.setCriteriaValue(oneLevelCriteria);

        criteria.addAndCondition(oneLevelCondition);

        return criteria;
    }
    private static Criteria checkPermissionForNegotiation(Map<String,FacilioField> fieldMap) throws Exception{
        Criteria criteria = new Criteria();

        criteria.andCriteria(checkRfqAndApprovalPermission(fieldMap));
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("isFinalized"),String.valueOf(true), BooleanOperators.IS));
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("approvalFlowId"),"", CommonOperators.IS_EMPTY));
        criteria.andCriteria(isNotClosedBid());

        return criteria;
    }
    private static Criteria isNotClosedBid() throws Exception{
        Criteria criteria = new Criteria();

        Criteria oneLevelCriteria=new Criteria();
        Map<String,FacilioField> oneLevelFieldMap = FieldFactory.getAsMap(Constants.getModBean().getAllFields(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION));
        oneLevelCriteria.addAndCondition(CriteriaAPI.getCondition(oneLevelFieldMap.get("rfqType"), V3RequestForQuotationContext.RfqTypes.CLOSED_BID.name(), StringSystemEnumOperators.ISN_T));

        Condition oneLevelCondition=new Condition();
        oneLevelCondition.setOperator(LookupOperator.LOOKUP);
        oneLevelCondition.setModuleName(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION);
        oneLevelCondition.setFieldName("requestForQuotation");
        oneLevelCondition.setCriteriaValue(oneLevelCriteria);

        criteria.addAndCondition(oneLevelCondition);
        return criteria;
    }
    private static Criteria checkAddQuotePermission(FacilioModule module,Map<String,FacilioField> fieldMap) throws Exception {
        Criteria criteria = new Criteria();

        FacilioStatus underNegotiation = TicketAPI.getStatus(module,"undernegotiation");
        FacilioStatus awaitingVendorQuote =  TicketAPI.getStatus(module,"awaitingvendorquote");
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("moduleState"),String.valueOf(underNegotiation.getId()), PickListOperators.IS));
        criteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("moduleState"),String.valueOf(awaitingVendorQuote.getId()),PickListOperators.IS));
        criteria.andCriteria(checkRfqAndApprovalPermission(fieldMap));
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("isFinalized"),String.valueOf(false), BooleanOperators.IS));
        return criteria;
    }

}
