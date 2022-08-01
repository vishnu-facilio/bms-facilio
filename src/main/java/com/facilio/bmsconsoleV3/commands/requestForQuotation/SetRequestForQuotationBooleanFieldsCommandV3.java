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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SetRequestForQuotationBooleanFieldsCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, Object> bodyParams = Constants.getBodyParams(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3RequestForQuotationContext> requestForQuotations = recordMap.get(moduleName);

        if (CollectionUtils.isNotEmpty(requestForQuotations) && MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("rfqFinalized") && (boolean) bodyParams.get("rfqFinalized")){
            checkIsRfqDiscarded(requestForQuotations.get(0));
            if (requestForQuotations.get(0).getVendor().size()==0) {
                throw new RESTException(ErrorCode.VALIDATION_ERROR, "RFQ cannot be finalized without selecting vendors");
            }
            requestForQuotations.get(0).setIsRfqFinalized(true);
        }
        else if (CollectionUtils.isNotEmpty(requestForQuotations) && MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("receiveQuote") && (boolean) bodyParams.get("receiveQuote")) {
            checkIsRfqDiscarded(requestForQuotations.get(0));
            checkVendorsQuotedPrice(requestForQuotations.get(0));
            if(!requestForQuotations.get(0).getIsRfqFinalized()){
                throw new RESTException(ErrorCode.VALIDATION_ERROR, "cannot receive quote without finalizing RFQ");
            }
            requestForQuotations.get(0).setIsQuoteReceived(true);
        }
        else if (CollectionUtils.isNotEmpty(requestForQuotations) && MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("awardQuotes") && (boolean) bodyParams.get("awardQuotes")){
            checkIsRfqDiscarded(requestForQuotations.get(0));
            if(!requestForQuotations.get(0).getIsQuoteReceived() && !requestForQuotations.get(0).getIsRfqFinalized()){
                throw new RESTException(ErrorCode.VALIDATION_ERROR, "vendors cannot awarded without receiving quotes or Finalizing quotes");
            }
            requestForQuotations.get(0).setIsAwarded(true);
        }
        else if (CollectionUtils.isNotEmpty(requestForQuotations) && MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("createPo") && (boolean) bodyParams.get("createPo")){
            checkIsRfqDiscarded(requestForQuotations.get(0));
            if(!requestForQuotations.get(0).getIsRfqFinalized() && !requestForQuotations.get(0).getIsQuoteReceived() && !requestForQuotations.get(0).getIsAwarded()){
                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Purchase order cannot be created without Finalizing or Receiving or awarding vendors");
            }
            requestForQuotations.get(0).setIsPoCreated(true);
        }
        else if (CollectionUtils.isNotEmpty(requestForQuotations) && MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("discardQuote") && (boolean) bodyParams.get("discardQuote")) {
            requestForQuotations.get(0).setIsDiscarded(true);
            setVendorQuotesDiscarded(requestForQuotations.get(0));
        }

        recordMap.put(moduleName, requestForQuotations);
        return false;
    }

    public void checkIsRfqDiscarded(V3RequestForQuotationContext requestForQuotation) throws RESTException {
        if(requestForQuotation.getIsDiscarded()){
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Rfq Has Been Discarded");
        }
    }
    public void checkVendorsQuotedPrice(V3RequestForQuotationContext requestForQuotation) throws Exception {
        List<Long> rfqLineItemIds = requestForQuotation.getRequestForQuotationLineItems().stream().map(ModuleBaseWithCustomFields::getId).collect(Collectors.toList());
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("RFQ_LINE_ITEM_ID", "requestForQuotationLineItem", String.valueOf(StringUtils.join(rfqLineItemIds,',')), NumberOperators.EQUALS));
        List<V3VendorQuotesLineItemsContext> vendorQuotesLineItems = V3RecordAPI.getRecordsListWithSupplements(FacilioConstants.ContextNames.VENDOR_QUOTES_LINE_ITEMS,null,V3VendorQuotesLineItemsContext.class,criteria,null);
        boolean noVendorsFinalized = vendorQuotesLineItems.stream().allMatch(lineItem -> lineItem.getCounterPrice()==null);
        if(noVendorsFinalized){
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Vendor(s) not quoted their counter price");
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
    }

}
