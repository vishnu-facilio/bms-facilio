package com.facilio.bmsconsoleV3.commands.invoice;

import com.facilio.bmsconsole.context.TermsAndConditionContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.invoice.InvoiceAssociatedTermsContext;
import com.facilio.bmsconsoleV3.context.invoice.InvoiceContextV3;
import com.facilio.bmsconsoleV3.context.invoice.InvoiceLineItemsContext;

import com.facilio.bmsconsoleV3.util.InvoiceAPI;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvoiceContextCloneCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Map<String,Object> recordMap = (Map<String, Object>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        List<InvoiceContextV3> invoiceContextList = new ArrayList<>();
       if(recordMap.containsKey(FacilioConstants.INVOICE.INVOICE_LIST)) {
           invoiceContextList.addAll((List<InvoiceContextV3>) recordMap.get(FacilioConstants.INVOICE.INVOICE_LIST));
        }
        if(invoiceContextList == null || invoiceContextList.isEmpty()){
            throw new IllegalArgumentException("No Invoice Available");
        }
        Boolean isCloning = (Boolean) context.getOrDefault(FacilioConstants.INVOICE.IS_CLONING,false);
        Boolean isConversion = (Boolean) context.getOrDefault(FacilioConstants.INVOICE.IS_CONVERSION,false);
        for(InvoiceContextV3 invoiceContext : invoiceContextList){
            Long groupId = invoiceContext.getGroup().getId();
            List<InvoiceContextV3> invoiceList = InvoiceAPI.getInvoiceFromGroupId(groupId);
            if(invoiceList == null || invoiceList.isEmpty()){
                throw new IllegalArgumentException("This Group has No Invoice (Group Id - #"+groupId);
            }
            //Approvers List change
            InvoiceAPI.setInvoiceApprovers(invoiceContext);
            InvoiceAPI.setInvoiceAssociatedTerms(invoiceContext);

            //Default values to null on clone
            Double version = getInvoiceVersionNumber(invoiceList);
            invoiceContext.setInvoiceVersion(version);
            invoiceContext.setIsInvoiceRevised(true);
            invoiceContext.setId(-1);
            invoiceContext.setApprovalFlowId(-1);
            invoiceContext.setApprovalStatus(null);
            invoiceContext.setInvoiceNumber(null);
            invoiceContext.setInvoiceStatusEnum(InvoiceContextV3.InvoiceStatus.DRAFT);

           //Default LineItems Clone
            List<Map<String,Object>> lineItemContextMapList = new ArrayList<>();
            List<InvoiceLineItemsContext> invoiceLineItemsContextList = invoiceContext.getLineItems();
            for(InvoiceLineItemsContext invoiceLineItemsContext : invoiceLineItemsContextList){
                Map<String,Object> lineItemContextMap = FieldUtil.getAsProperties(invoiceLineItemsContext);
                lineItemContextMap.put("id",null);
                lineItemContextMap.put("invoice",null);
                lineItemContextMapList.add(lineItemContextMap);
            }
            invoiceLineItemsContextList = FieldUtil.getAsBeanListFromMapList(lineItemContextMapList,InvoiceLineItemsContext.class);
            invoiceContext.setLineItems(invoiceLineItemsContextList);

            //Cloning Value Set
            if(isCloning){
                invoiceContext.setInvoiceVersion(0.0);
                invoiceContext.setGroup(null);
                invoiceContext.setIsInvoiceRevised(false);
                if(!isConversion) {
                    invoiceContext.setSubject("Copy Of " + invoiceContext.getSubject());
                }
            }
        }
        if(recordMap.containsKey(FacilioConstants.ContextNames.INVOICE)){
            recordMap.put(FacilioConstants.ContextNames.INVOICE,invoiceContextList);
        }
        else if(recordMap.containsKey(FacilioConstants.INVOICE.INVOICE_LIST)){
            recordMap.put(FacilioConstants.INVOICE.INVOICE_LIST,invoiceContextList);
        }
        context.put(FacilioConstants.ContextNames.RECORD_MAP,recordMap);
        return false;
    }
    public Double getInvoiceVersionNumber(List<InvoiceContextV3> invoiceContextList) {
        Double version = 1.0;
        if(invoiceContextList == null || invoiceContextList.isEmpty()){
            return version;
        }
        for(InvoiceContextV3 invoiceContext : invoiceContextList){
            if(invoiceContext.getInvoiceVersion() > version){
                version = invoiceContext.getInvoiceVersion();
            }
        }
        return version + 1;
    }
}
