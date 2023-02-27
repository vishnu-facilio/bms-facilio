package com.facilio.bmsconsoleV3.commands.requestForQuotation;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationContext;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationLineItemsContext;
import com.facilio.bmsconsoleV3.context.vendorquotes.V3VendorQuotesContext;
import com.facilio.bmsconsoleV3.context.vendorquotes.V3VendorQuotesLineItemsContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class SetRequestForQuotationBooleanFieldsCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, Object> bodyParams = Constants.getBodyParams(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3RequestForQuotationContext> requestForQuotations = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(requestForQuotations)) {
            for(V3RequestForQuotationContext requestForQuotation : requestForQuotations){
                if (MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("rfqFinalized") && (boolean) bodyParams.get("rfqFinalized")){
                    checkIsRfqDiscarded(requestForQuotation);
                    if (CollectionUtils.isEmpty(requestForQuotation.getVendor())) {
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "RFQ cannot be Published without Selecting Vendor(s)");
                    }
                    requestForQuotation.setIsRfqFinalized(true);
                }
                else if (MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("receiveQuote") && (boolean) bodyParams.get("receiveQuote")) {
                    checkIsRfqDiscarded(requestForQuotation);
                    checkAtLeastOneVendorFinalizedAndQuotedPrice(requestForQuotation);
                    if(!requestForQuotation.getIsRfqFinalized()){
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Cannot Close Submission without Finalizing RFQ");
                    }
                    requestForQuotation.setIsQuoteReceived(true);
                }
                else if (MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("awardQuotes") && (boolean) bodyParams.get("awardQuotes")){
                    checkIsRfqDiscarded(requestForQuotation);
                    if(!requestForQuotation.getIsQuoteReceived() && !requestForQuotation.getIsRfqFinalized()){
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Vendors cannot awarded without Receiving Quotes");
                    }
                    requestForQuotation.setIsAwarded(true);
                }
                else if (MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("createPo") && (boolean) bodyParams.get("createPo")){
                    checkIsRfqDiscarded(requestForQuotation);
                    if(!requestForQuotation.getIsRfqFinalized() && !requestForQuotation.getIsQuoteReceived() && !requestForQuotation.getIsAwarded()){
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Purchase order cannot be created without Awarding Vendors");
                    }
                    requestForQuotation.setIsPoCreated(true);
                }
                else if (MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("discardQuote") && (boolean) bodyParams.get("discardQuote")){
                    requestForQuotation.setIsDiscarded(true);
                    setVendorQuotesDiscarded(requestForQuotation);
                }
            }
        }
        recordMap.put(moduleName, requestForQuotations);
        return false;
    }

    public void checkIsRfqDiscarded(V3RequestForQuotationContext requestForQuotation) throws RESTException {
        if(requestForQuotation.getIsDiscarded()){
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Rfq Has Been Discarded");
        }
    }
    public void checkAtLeastOneVendorFinalizedAndQuotedPrice(V3RequestForQuotationContext requestForQuotation) throws Exception {
        List<Long> vendorIds = requestForQuotation.getVendor().stream().map(ModuleBaseWithCustomFields::getId).collect(Collectors.toList());
        Long rfqId = requestForQuotation.getId();
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("VENDOR","vendor",String.valueOf(StringUtils.join(vendorIds,',')),NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition("RFQ_ID","requestForQuotation",String.valueOf(rfqId),NumberOperators.EQUALS));
        List<V3VendorQuotesContext> vendorQuotes = V3RecordAPI.getRecordsListWithSupplements(FacilioConstants.ContextNames.VENDOR_QUOTES,null,V3VendorQuotesContext.class,criteria,null);
        if(CollectionUtils.isNotEmpty(vendorQuotes)) {
            List<V3VendorQuotesContext> finalizedVendorQuotes = vendorQuotes.stream().filter(V3VendorQuotesContext::getIsFinalized).collect(Collectors.toList());
            if (finalizedVendorQuotes.isEmpty()) {
                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Unable to close quote submission for this RFQ. Please ensure that all line items have a response from at least one vendor before closing the submission!");
            }
            Criteria rfqLineItemCriteria = new Criteria();
            rfqLineItemCriteria.addAndCondition(CriteriaAPI.getCondition("RFQ_ID", "requestForQuotation", String.valueOf(rfqId), NumberOperators.EQUALS));
            List<V3RequestForQuotationLineItemsContext> rfqLineItems = V3RecordAPI.getRecordsListWithSupplements(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION_LINE_ITEMS, null, V3RequestForQuotationLineItemsContext.class, rfqLineItemCriteria, null);
            if(CollectionUtils.isNotEmpty(rfqLineItems)) {
                List<Long> rfqLineItemsIds = rfqLineItems.stream().map(ModuleBaseWithCustomFields::getId).collect(Collectors.toList());
                List<V3VendorQuotesLineItemsContext> allVendorQuotesLineItemsWithCounterPrice = new ArrayList<>();
                for (V3VendorQuotesContext vendorQuote : finalizedVendorQuotes) {
                    Long vendorQuoteId = vendorQuote.getId();
                    Criteria vendorQuoteLineItemCriteria = new Criteria();
                    vendorQuoteLineItemCriteria.addAndCondition(CriteriaAPI.getCondition("VENDOR_QUOTE_ID", "vendorQuotes", String.valueOf(vendorQuoteId), NumberOperators.EQUALS));
                    List<V3VendorQuotesLineItemsContext> vendorQuotesLineItems = V3RecordAPI.getRecordsListWithSupplements(FacilioConstants.ContextNames.VENDOR_QUOTES_LINE_ITEMS, null, V3VendorQuotesLineItemsContext.class, vendorQuoteLineItemCriteria, null);
                    if(CollectionUtils.isNotEmpty(vendorQuotesLineItems)) {
                        List<V3VendorQuotesLineItemsContext> vendorQuotesLineItemsWithCounterPrice = vendorQuotesLineItems.stream().filter(lineItem -> lineItem.getCounterPrice() != null).collect(Collectors.toList());
                        allVendorQuotesLineItemsWithCounterPrice.addAll(vendorQuotesLineItemsWithCounterPrice);
                    }
                }
                List<Long> rfqLineItemsIdsFromVQLineItems = allVendorQuotesLineItemsWithCounterPrice.stream().map(lineItem -> lineItem.getRequestForQuotationLineItem().getId()).collect(Collectors.toList());
                boolean isAllRfqLineItemsHaveCounterPrice = new HashSet<>(rfqLineItemsIdsFromVQLineItems).containsAll(rfqLineItemsIds);
                if (!isAllRfqLineItemsHaveCounterPrice) {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Vendor(s) Not Quoted Unit Price For Requested LineItem(s)");
                }
            }
        }
        else {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Vendor Quotes Not Created For RFQ");
        }
    }
    public void setVendorQuotesDiscarded(V3RequestForQuotationContext requestForQuotation) throws Exception {
        if (requestForQuotation.getIsRfqFinalized()) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            String vendorQuotesModuleName = FacilioConstants.ContextNames.VENDOR_QUOTES;
            FacilioModule vendorQuotesModule = modBean.getModule(vendorQuotesModuleName);
            List<FacilioField> vendorQuotesFields = modBean.getAllFields(vendorQuotesModuleName);

            List<FacilioField> updatedFields = new ArrayList<>();
            Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(vendorQuotesFields);
            updatedFields.add(fieldsMap.get("isDiscarded"));
            Map<String, Object> map = new HashMap<>();
            map.put("isDiscarded", true);

            UpdateRecordBuilder<V3VendorQuotesContext> updateBuilder = new UpdateRecordBuilder<V3VendorQuotesContext>()
                    .module(vendorQuotesModule)
                    .fields(updatedFields)
                    .andCondition(CriteriaAPI.getCondition("RFQ_ID","requestForQuotation",String.valueOf(requestForQuotation.getId()),NumberOperators.EQUALS));
            updateBuilder.updateViaMap(map);
        }
        else {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Cannot Discard RFQ without Publishing");
        }
    }

}
