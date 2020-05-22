
<%@page import="com.facilio.accounts.dto.Organization"%>
<%@ page import="com.facilio.accounts.util.AccountUtil" %>
<%@ page import="com.facilio.beans.ModuleBean" %>
<%@ page import="com.facilio.bmsconsole.commands.FacilioCommand" %>
<%@page import="com.facilio.bmsconsole.util.StateFlowRulesAPI" %>
<%@ page import="com.facilio.bmsconsole.util.TicketAPI" %>
<%@ page import="com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext" %>
<%@ page import="com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext" %>
<%@ page import="com.facilio.chain.FacilioChain" %>
<%@ page import="com.facilio.constants.FacilioConstants" %>
<%@ page import="com.facilio.fs.FileInfo" %>
<%@ page import="com.facilio.fw.BeanFactory" %>
<%@ page import="com.facilio.modules.FacilioModule" %>
<%@ page import="com.facilio.modules.FacilioStatus" %>
<%@ page import="com.facilio.modules.FieldFactory" %>
<%@ page import="com.facilio.modules.FieldType" %>
<%@ page import="com.facilio.modules.fields.*" %>
<%@ page import="org.apache.commons.chain.Context" %>

<%@ page import="org.apache.log4j.LogManager" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="java.util.List" %>


<%--

  _____                      _          _                              _   _            _                       __                           _
 |  __ \                    | |        | |                            | | | |          | |                     / _|                         | |
 | |  | | ___    _ __   ___ | |_    ___| |__   __ _ _ __   __ _  ___  | |_| |__   ___  | |__   __ _ ___  ___  | |_ ___  _ __ _ __ ___   __ _| |_
 | |  | |/ _ \  | '_ \ / _ \| __|  / __| '_ \ / _` | '_ \ / _` |/ _ \ | __| '_ \ / _ \ | '_ \ / _` / __|/ _ \ |  _/ _ \| '__| '_ ` _ \ / _` | __|
 | |__| | (_) | | | | | (_) | |_  | (__| | | | (_| | | | | (_| |  __/ | |_| | | |  __/ | |_) | (_| \__ \  __/ | || (_) | |  | | | | | | (_| | |_
 |_____/ \___/  |_| |_|\___/ \__|  \___|_| |_|\__,_|_| |_|\__, |\___|  \__|_| |_|\___| |_.__/ \__,_|___/\___| |_| \___/|_|  |_| |_| |_|\__,_|\__|
                                                           __/ |
                                                          |___/

--%>

<%
    final class OrgLevelMigrationCommand extends FacilioCommand {
        private final Logger LOGGER = LogManager.getLogger(OrgLevelMigrationCommand.class.getName());
        @Override
        public boolean executeCommand(Context context) throws Exception {

            // Have migration commands for each org
            // Transaction is only org level. If failed, have to continue from the last failed org and not from first
            // Tax Module Addition
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule tM = modBean.getModule("tax");
            if (tM == null) {
                FacilioModule taxModule = new FacilioModule();
                taxModule.setName("tax");
                taxModule.setDisplayName("Tax");
                taxModule.setTableName("Tax");
                taxModule.setType(FacilioModule.ModuleType.BASE_ENTITY.getValue());
                long taxModuleId = modBean.addModule(taxModule);
                taxModule.setModuleId(taxModuleId);

                // Fields For Tax Module
                FacilioModule ticketStatusModule = modBean.getModule(FacilioConstants.ContextNames.TICKET_STATUS);
                FacilioModule locationModule = modBean.getModule(FacilioConstants.ContextNames.LOCATION);

                FacilioField taxNameSF = new FacilioField(taxModule, "name", "Tax Name", FacilioField.FieldDisplayType.TEXTBOX, "NAME", FieldType.STRING, true, false, true, true);
                NumberField taxRateNF = new NumberField(taxModule, "rate", "Rate", FacilioField.FieldDisplayType.NUMBER, "RATE", FieldType.NUMBER, false, false, true, false);
                SystemEnumField taxTypeField = (SystemEnumField) FieldFactory.getField("type", "Type", "TYPE", taxModule, FieldType.SYSTEM_ENUM);
                taxTypeField.setEnumName("Type");
                taxTypeField.setDefault(true);
                taxTypeField.setDisplayType(FacilioField.FieldDisplayType.SELECTBOX);
                BooleanField taxIsActiveBF = new BooleanField(taxModule, "isActive", "Is Active", FacilioField.FieldDisplayType.DECISION_BOX, "IS_ACTIVE", FieldType.BOOLEAN, false, false, true, false);

                modBean.addField(taxNameSF);
                modBean.addField(taxRateNF);
                modBean.addField(taxIsActiveBF);
                modBean.addField(taxTypeField);

                // Tax Group Module Addition
                FacilioModule taxGroupModule = new FacilioModule();
                taxGroupModule.setName("taxgroup");
                taxGroupModule.setDisplayName("Tax Group");
                taxGroupModule.setTableName("TaxGroups");
                taxGroupModule.setType(FacilioModule.ModuleType.SUB_ENTITY);

                long taxGroupModuleId = modBean.addModule(taxGroupModule);
                taxGroupModule.setModuleId(taxGroupModuleId);
                modBean.addSubModule(taxModuleId, taxGroupModuleId);

                // Tax Group Fields Addition

                LookupField tgParentTaxField = new LookupField(taxGroupModule, "parentTax", "Parent Tax", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "PARENT_TAX_ID", FieldType.LOOKUP, true, false, true, false, taxModule);
                modBean.addField(tgParentTaxField);
                LookupField tgChildTaxField = new LookupField(taxGroupModule, "childTax", "Child Tax", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "CHILD_TAX_ID", FieldType.LOOKUP, true, false, true, false, taxModule);
                modBean.addField(tgChildTaxField);

                // Quotation Module Addition

                FacilioModule quotationModule = new FacilioModule();
                quotationModule.setName("quotation");
                quotationModule.setDisplayName("Quotation");
                quotationModule.setTableName("Quotation");
                quotationModule.setType(FacilioModule.ModuleType.BASE_ENTITY);
                quotationModule.setTrashEnabled(true);
                quotationModule.setStateFlowEnabled(true);
                long quotationModuleId = modBean.addModule(quotationModule);
                quotationModule.setModuleId(quotationModuleId);

                // Quotation Module Fields

                NumberField qLocalIdNF = new NumberField(quotationModule, "localId", "Id", FacilioField.FieldDisplayType.NUMBER, "LOCAL_ID", FieldType.NUMBER, false, false, true, false);
                FacilioField qSubjectField = new FacilioField(quotationModule, "subject", "Subject", FacilioField.FieldDisplayType.TEXTBOX, "SUBJECT", FieldType.STRING, true, false, true, true);
                FacilioField qDescriptionField = new FacilioField(quotationModule, "description", "Description", FacilioField.FieldDisplayType.TEXTAREA, "DESCRIPTION", FieldType.STRING, false, false, true, false);
                LookupField qShipToAddressField = new LookupField(quotationModule, "shipToAddress", "Ship To Address", FacilioField.FieldDisplayType.ADDRESS, "SHIP_TO_ADDRESS_ID", FieldType.LOOKUP, false, false, true, false, locationModule);
                LookupField qBillToAddressField = new LookupField(quotationModule, "billToAddress", "Bill To Address", FacilioField.FieldDisplayType.ADDRESS, "BILL_TO_ADDRESS_ID", FieldType.LOOKUP, false, false, true, false, locationModule);
                FacilioField qBillDateField = new FacilioField(quotationModule, "billDate", "Bill Date", FacilioField.FieldDisplayType.DATETIME, "BILL_DATE", FieldType.DATE_TIME, true, false, true, false);
                FacilioField qExpiryDateField = new FacilioField(quotationModule, "expiryDate", "Expiry Date", FacilioField.FieldDisplayType.DATETIME, "EXPIRY_DATE", FieldType.DATE_TIME, true, false, true, false);
                FileField qSignatureField = (FileField) FieldFactory.getField("signature", "Signature", "SIGNATURE_ID", quotationModule, FieldType.FILE);
                qSignatureField.setDisplayType(FacilioField.FieldDisplayType.FILE);
                qSignatureField.setFormat(FileInfo.FileFormat.SIGNATURE);
                qSignatureField.setDefault(true);
                NumberField qTotalTaxAmountNF = new NumberField(quotationModule, "totalTaxAmount", "Total Tax Amount", FacilioField.FieldDisplayType.DECIMAL, "TOTAL_TAX_AMOUNT", FieldType.DECIMAL, false, false, true, false);
                NumberField qDiscountAmountNF = new NumberField(quotationModule, "discountAmount", "Discount Amount", FacilioField.FieldDisplayType.DECIMAL, "DISCOUNT_AMOUNT", FieldType.DECIMAL, false, false, true, false);
                NumberField qDiscountPerNF = new NumberField(quotationModule, "discountPercentage", "Discount Percentage", FacilioField.FieldDisplayType.DECIMAL, "DISCOUNT_PERCENTAGE", FieldType.DECIMAL, false, false, true, false);
                NumberField qShippingChargesNF = new NumberField(quotationModule, "shippingCharges", "Shipping Charges", FacilioField.FieldDisplayType.DECIMAL, "SHIPPING_CHARGES", FieldType.DECIMAL, false, false, true, false);
                NumberField qAdjustmentsCostNF = new NumberField(quotationModule, "adjustmentsCost", "Adjustments Cost", FacilioField.FieldDisplayType.DECIMAL, "ADJUSTMENTS_COST", FieldType.DECIMAL, false, false, true, false);
                FacilioField qAdjustmentsCostNameField = new FacilioField(quotationModule, "adjustmentsCostName", "Adjustments Cost Name", FacilioField.FieldDisplayType.TEXTBOX, "ADJUSTMENTS_COST_NAME", FieldType.STRING, false, false, true, false);
                NumberField qMiscellaneousChargesNF = new NumberField(quotationModule, "miscellaneousCharges", "Miscellaneous Charges", FacilioField.FieldDisplayType.DECIMAL, "MISCELLANEOUS_CHARGES", FieldType.DECIMAL, false, false, true, false);
                NumberField qTotalCostNF = new NumberField(quotationModule, "totalCost", "Total Cost", FacilioField.FieldDisplayType.DECIMAL, "TOTAL_COST", FieldType.DECIMAL, false, false, true, false);
                LookupField qApprovalStateField = new LookupField(quotationModule, "approvalStatus", "Approval State", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "APPROVAL_STATE", FieldType.LOOKUP, false, false, true, false, ticketStatusModule);
                NumberField qApprovalFlowIdNF = new NumberField(quotationModule, "approvalFlowId", "Approval Flow Id", FacilioField.FieldDisplayType.NUMBER, "APPROVAL_FLOW_ID", FieldType.NUMBER, false, false, true, false);
                LookupField qModuleStateField = new LookupField(quotationModule, "moduleState", "Module State", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "MODULE_STATE", FieldType.LOOKUP, false, false, true, false, ticketStatusModule);
                NumberField qStateFlowIdNF = new NumberField(quotationModule, "stateFlowId", "State Flow Id", FacilioField.FieldDisplayType.NUMBER, "STATE_FLOW_ID", FieldType.NUMBER, false, false, true, false);

                modBean.addField(qLocalIdNF);
                modBean.addField(qSubjectField);
                modBean.addField(qDescriptionField);
                modBean.addField(qShipToAddressField);
                modBean.addField(qBillToAddressField);
                modBean.addField(qBillDateField);
                modBean.addField(qExpiryDateField);
                modBean.addField(qSignatureField);
                modBean.addField(qTotalTaxAmountNF);
                modBean.addField(qDiscountAmountNF);
                modBean.addField(qDiscountPerNF);
                modBean.addField(qShippingChargesNF);
                modBean.addField(qAdjustmentsCostNF);
                modBean.addField(qAdjustmentsCostNameField);
                modBean.addField(qMiscellaneousChargesNF);
                modBean.addField(qTotalCostNF);
                modBean.addField(qApprovalStateField);
                modBean.addField(qApprovalFlowIdNF);
                modBean.addField(qModuleStateField);
                modBean.addField(qStateFlowIdNF);


                // Quotation Module Default Status

                FacilioStatus qRequestedState = new FacilioStatus();
                qRequestedState.setDisplayName("Requested");
                qRequestedState.setStatus("Requested");
                qRequestedState.setTypeCode(FacilioStatus.StatusType.REQUESTED.getIntVal());
                qRequestedState.setRequestedState(true);

                FacilioStatus qActiveState = new FacilioStatus();
                qActiveState.setDisplayName("Active");
                qActiveState.setStatus("Active");
                qActiveState.setTypeCode(FacilioStatus.StatusType.OPEN.getIntVal());

                FacilioStatus qRejectedState = new FacilioStatus();
                qRejectedState.setDisplayName("Rejected");
                qRejectedState.setStatus("Rejected");
                qRejectedState.setTypeCode(FacilioStatus.StatusType.REJECTED.getIntVal());
                qRejectedState.setRecordLocked(true);

                FacilioStatus qInactiveStatus = new FacilioStatus();
                qInactiveStatus.setDisplayName("InActive");
                qInactiveStatus.setStatus("InActive");
                qInactiveStatus.setTypeCode(FacilioStatus.StatusType.OPEN.getIntVal());
                qInactiveStatus.setRecordLocked(true);

                TicketAPI.addStatus(qRequestedState, quotationModule);
                TicketAPI.addStatus(qActiveState, quotationModule);
                TicketAPI.addStatus(qRejectedState, quotationModule);
                TicketAPI.addStatus(qInactiveStatus, quotationModule);

                // Default Transitions

                StateFlowRuleContext qDefaultWorkFlowRule = new StateFlowRuleContext();
                qDefaultWorkFlowRule.setName("Default Quotation Stateflow");
                qDefaultWorkFlowRule.setRuleType(29);
                qDefaultWorkFlowRule.setActivityType(1);
                qDefaultWorkFlowRule.setModule(quotationModule);
                qDefaultWorkFlowRule.setDefaltStateFlow(true);
                qDefaultWorkFlowRule.setDefaultStateId(qRequestedState.getId());
                long ruleId = StateFlowRulesAPI.addWorkflowRule(qDefaultWorkFlowRule);


                StateflowTransitionContext qApproveTransition = new StateflowTransitionContext();
                qApproveTransition.setName("Approve");
                qApproveTransition.setRuleType(28);
                qApproveTransition.setActivityType(4194304);
                qApproveTransition.setModule(quotationModule);
                qApproveTransition.setFromStateId(qRequestedState.getId());
                qApproveTransition.setToStateId(qActiveState.getId());
                qApproveTransition.setStateFlowId(ruleId);
                qApproveTransition.setType(1);
                qApproveTransition.setButtonType(1);
                StateFlowRulesAPI.addWorkflowRule(qApproveTransition);

                StateflowTransitionContext qRejectTransition = new StateflowTransitionContext();
                qRejectTransition.setName("Reject");
                qRejectTransition.setRuleType(28);
                qRejectTransition.setActivityType(4194304);
                qRejectTransition.setModule(quotationModule);
                qRejectTransition.setFromStateId(qRequestedState.getId());
                qRejectTransition.setToStateId(qRejectedState.getId());
                qRejectTransition.setStateFlowId(ruleId);
                qRejectTransition.setType(1);
                qRejectTransition.setButtonType(2);
                StateFlowRulesAPI.addWorkflowRule(qRejectTransition);

                StateflowTransitionContext qInactiveTransition = new StateflowTransitionContext();
                qInactiveTransition.setName("Mark Inactive");
                qInactiveTransition.setRuleType(28);
                qInactiveTransition.setActivityType(4194304);
                qInactiveTransition.setModule(quotationModule);
                qInactiveTransition.setFromStateId(qActiveState.getId());
                qInactiveTransition.setToStateId(qInactiveStatus.getId());
                qInactiveTransition.setStateFlowId(ruleId);
                qInactiveTransition.setType(1);
                qInactiveTransition.setButtonType(2);
                StateFlowRulesAPI.addWorkflowRule(qInactiveTransition);

                // Quotation Line Items Module Addition

                FacilioModule quotationLineItemModule = new FacilioModule();
                quotationLineItemModule.setName("quotationlineitems");
                quotationLineItemModule.setDisplayName("Quotation Line Items");
                quotationLineItemModule.setTableName("QuotationLineItems");
                quotationLineItemModule.setType(FacilioModule.ModuleType.SUB_ENTITY);
                long quotationLineItemModuleId = modBean.addModule(quotationLineItemModule);
                modBean.addSubModule(quotationModuleId, quotationLineItemModuleId);
                quotationLineItemModule.setModuleId(quotationLineItemModuleId);

                // Quotation Line Items Module Fields
                FacilioModule itemTypesModule = modBean.getModule(FacilioConstants.ContextNames.ITEM_TYPES);
                FacilioModule toolTypesModule = modBean.getModule(FacilioConstants.ContextNames.TOOL_TYPES);
                FacilioModule serviceModule = modBean.getModule(FacilioConstants.ContextNames.SERVICE);
                FacilioModule labourModule = modBean.getModule(FacilioConstants.ContextNames.LABOUR);


                LookupField lQuotationLF = new LookupField(quotationLineItemModule, "quotation", "Quotation", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "QUOTATION_ID", FieldType.LOOKUP, false, false, true, false, quotationModule);
                SystemEnumField lineItemTypeField = (SystemEnumField) FieldFactory.getField("type", "Type", "TYPE", quotationLineItemModule, FieldType.SYSTEM_ENUM);
                lineItemTypeField.setEnumName("Type");
                lineItemTypeField.setDefault(true);
                lineItemTypeField.setDisplayType(FacilioField.FieldDisplayType.SELECTBOX);
                LookupField lItemTypeLF = new LookupField(quotationLineItemModule, "itemType", "Item Type", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "ITEM_TYPE_ID", FieldType.LOOKUP, false, false, true, false, itemTypesModule);
                LookupField lToolTypeLF = new LookupField(quotationLineItemModule, "toolType", "Tool Type", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "TOOL_TYPE_ID", FieldType.LOOKUP, false, false, true, false, toolTypesModule);
                LookupField lServiceLF = new LookupField(quotationLineItemModule, "service", "Service", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "SERVICE_ID", FieldType.LOOKUP, false, false, true, false, serviceModule);
                LookupField lLabourLF = new LookupField(quotationLineItemModule, "labour", "Labour", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "LABOUR_ID", FieldType.LOOKUP, false, false, true, false, labourModule);
                LookupField lTaxLF = new LookupField(quotationLineItemModule, "tax", "Tax", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "TAX_ID", FieldType.LOOKUP, false, false, true, false, taxModule);
                NumberField lQuantityNF = new NumberField(quotationLineItemModule, "quantity", "Quantity", FacilioField.FieldDisplayType.DECIMAL, "QUANTITY", FieldType.DECIMAL, false, false, true, false);
                NumberField lUnitPriceNF = new NumberField(quotationLineItemModule, "unitPrice", "Unit Price", FacilioField.FieldDisplayType.DECIMAL, "UNIT_PRICE", FieldType.DECIMAL, false, false, true, false);
                NumberField lTaxAmountNF = new NumberField(quotationLineItemModule, "taxAmount", "Tax Amount", FacilioField.FieldDisplayType.DECIMAL, "TAX_AMOUNT", FieldType.DECIMAL, false, false, true, false);
                NumberField lCostNF = new NumberField(quotationLineItemModule, "cost", "Cost", FacilioField.FieldDisplayType.DECIMAL, "COST", FieldType.DECIMAL, false, false, true, false);
                FacilioField lDescriptionField = new FacilioField(quotationLineItemModule, "description", "Description", FacilioField.FieldDisplayType.TEXTAREA, "DESCRIPTION", FieldType.STRING, false, false, true, false);

                modBean.addField(lQuotationLF);
                modBean.addField(lineItemTypeField);
                modBean.addField(lItemTypeLF);
                modBean.addField(lToolTypeLF);
                modBean.addField(lServiceLF);
                modBean.addField(lLabourLF);
                modBean.addField(lTaxLF);
                modBean.addField(lQuantityNF);
                modBean.addField(lUnitPriceNF);
                modBean.addField(lTaxAmountNF);
                modBean.addField(lCostNF);
                modBean.addField(lDescriptionField);

                // Quotation Notes
                FacilioModule quotationNotes = new FacilioModule();
                quotationNotes.setName("quotationnotes");
                quotationNotes.setDisplayName("Quotation Notes");
                quotationNotes.setTableName("QuotationNotes");
                quotationNotes.setType(FacilioModule.ModuleType.NOTES);
                long quotationNotesModuleId = modBean.addModule(quotationNotes);
                quotationNotes.setModuleId(quotationNotesModuleId);
                modBean.addSubModule(quotationModuleId, quotationNotesModuleId);

                FacilioField qncreatedTime = new FacilioField(quotationNotes, "createdTime", "Created Time", FacilioField.FieldDisplayType.NUMBER, "CREATED_TIME", FieldType.DATE_TIME, true, false, true, false);
                modBean.addField(qncreatedTime);

                LookupField qncreatedByLF = new LookupField(quotationNotes, "createdBy", "Created By", FacilioField.FieldDisplayType.LOOKUP_POPUP, "CREATED_BY", FieldType.LOOKUP, false, false, true, false, "users");
                modBean.addField(qncreatedByLF);

                NumberField qnparentIdNF = new NumberField(quotationNotes, "parentId", "Parent", FacilioField.FieldDisplayType.NUMBER, "PARENT_ID", FieldType.NUMBER, false, false, true, false);
                modBean.addField(qnparentIdNF);

                FacilioField qntitle = new FacilioField(quotationNotes, "title", "Title", FacilioField.FieldDisplayType.TEXTBOX, "TITLE", FieldType.STRING, false, false, true, true);
                modBean.addField(qntitle);

                FacilioField qnbody = new FacilioField(quotationNotes, "body", "Body", FacilioField.FieldDisplayType.TEXTAREA, "BODY", FieldType.STRING, false, false, true, false);
                modBean.addField(qnbody);

                BooleanField qnnotifyRequesterBF = new BooleanField(quotationNotes, "notifyRequester", "Notify Requester", FacilioField.FieldDisplayType.DECISION_BOX, "NOTIFY_REQUESTER", FieldType.BOOLEAN, false, false, true, false);
                modBean.addField(qnnotifyRequesterBF);

                // Quotation Attachments

                FacilioModule quotationattachments = new FacilioModule();
                quotationattachments.setName("quotationattachments");
                quotationattachments.setDisplayName("Quotation Attachments");
                quotationattachments.setTableName("QuotationAttachments");
                quotationattachments.setType(FacilioModule.ModuleType.ATTACHMENTS);
                long quotationattachmentsModuleId = modBean.addModule(quotationattachments);
                quotationattachments.setModuleId(quotationattachmentsModuleId);
                modBean.addSubModule(quotationModuleId, quotationattachmentsModuleId);

                NumberField qafileIdNF = new NumberField(quotationattachments, "fileId", "File ID", FacilioField.FieldDisplayType.NUMBER, "FILE_ID", FieldType.NUMBER, true, false, true, true);
                modBean.addField(qafileIdNF);

                NumberField qaparentIdNF = new NumberField(quotationattachments, "parentId", "Parent", FacilioField.FieldDisplayType.NUMBER, "PARENT_ID", FieldType.NUMBER, true, false, true, false);
                modBean.addField(qaparentIdNF);

                NumberField qacreatedTimeNF = new NumberField(quotationattachments, "createdTime", "Created Time", FacilioField.FieldDisplayType.NUMBER, "CREATED_TIME", FieldType.NUMBER, true, false, true, false);
                modBean.addField(qacreatedTimeNF);

                NumberField qatypeNF = new NumberField(quotationattachments, "type", "Type", FacilioField.FieldDisplayType.NUMBER, "ATTACHMENT_TYPE", FieldType.NUMBER, true, false, true, false);
                modBean.addField(qatypeNF);

                // Quotation Terms Module Addition
                FacilioModule qTerms = new FacilioModule();
                qTerms.setName("quotationterms");
                qTerms.setDisplayName("Quotation Associated Terms");
                qTerms.setTableName("QuotationAssociatedTerms");
                qTerms.setType(FacilioModule.ModuleType.SUB_ENTITY);

                long qTermsModuleId = modBean.addModule(taxGroupModule);
                qTerms.setModuleId(qTermsModuleId);

                // Quoation Terms Fields Addition
                LookupField qTermsQuotationLF = new LookupField(qTerms, "quotation", "Quotation", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "QUOTATION_ID", FieldType.LOOKUP, true, false, true, false, quotationModule);
                modBean.addField(qTermsQuotationLF);
                LookupField qTermsLF = new LookupField(qTerms, "terms", "Terms", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "TERMS_AND_CONDITIONS_ID", FieldType.LOOKUP, true, false, true, false, quotationModule);
                modBean.addField(qTermsLF);

                LOGGER.info("Completed For -- " + AccountUtil.getCurrentOrg().getId());
            } else {
                LOGGER.info("Tax Module Already Present For -- " + AccountUtil.getCurrentOrg().getId());
            }


            return false;
        }
    }
%>

<%
    List<Organization> orgs = AccountUtil.getOrgBean().getOrgs();
    AccountUtil.cleanCurrentAccount();
    for (Organization org : orgs) {
        AccountUtil.setCurrentAccount(org.getOrgId());
        FacilioChain c = FacilioChain.getTransactionChain();
        c.addCommand(new OrgLevelMigrationCommand());
        c.execute();

        AccountUtil.cleanCurrentAccount();
    }
%>