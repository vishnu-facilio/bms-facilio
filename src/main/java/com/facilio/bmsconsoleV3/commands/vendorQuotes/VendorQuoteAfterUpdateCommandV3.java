package com.facilio.bmsconsoleV3.commands.vendorQuotes;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationContext;
import com.facilio.bmsconsoleV3.context.vendorquotes.V3VendorQuotesContext;
import com.facilio.bmsconsoleV3.context.vendorquotes.V3VendorQuotesLineItemsContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class VendorQuoteAfterUpdateCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        Map<String, Object> bodyParams = Constants.getBodyParams(context);
        List<V3VendorQuotesContext> vendorQuotes = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(vendorQuotes)) {
            for (V3VendorQuotesContext vendorQuote : vendorQuotes) {
                if (MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("isPoCreated") && (boolean) bodyParams.get("isPoCreated") && bodyParams.get("purchaseOrderId") != null) {
                    checkPoCreatedForAllAwardedVendorQuotes(vendorQuote);
                }
            }
        }
        return false;
    }
    private void checkPoCreatedForAllAwardedVendorQuotes(V3VendorQuotesContext vendorQuote) throws Exception {
        V3RequestForQuotationContext requestForQuotation = vendorQuote.getRequestForQuotation();
        if(requestForQuotation != null){
            Long rfqId = requestForQuotation.getId();
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.VENDOR_QUOTES);
            FacilioField purchaseOrderField = modBean.getField("purchaseOrder", FacilioConstants.ContextNames.VENDOR_QUOTES);
            Map<String, List<Object>> queryParams = new HashMap<>();
            queryParams.put("getWithLineItems", Collections.singletonList(true));
            Criteria serverCriteria = new Criteria();
            serverCriteria.addAndCondition(CriteriaAPI.getCondition("RFQ_ID","requestForQuotation",String.valueOf(rfqId), NumberOperators.EQUALS));
            FacilioContext listContext = V3Util.fetchList(FacilioConstants.ContextNames.VENDOR_QUOTES, true,
                    null,null,false, null, null,null,null,
                    1, 5000, false, queryParams, serverCriteria,null);
            Map<String, Map> recordMap = (Map<String, Map>) listContext.get(FacilioConstants.ContextNames.RECORD_MAP);
            List<V3VendorQuotesContext> vendorQuotes = (List<V3VendorQuotesContext>) recordMap.get(FacilioConstants.ContextNames.VENDOR_QUOTES);
            if(CollectionUtils.isNotEmpty(vendorQuotes)){
                List<V3VendorQuotesContext> awardedVendorQuotes = new ArrayList<>();
                for(V3VendorQuotesContext record : vendorQuotes) {
                    List<V3VendorQuotesLineItemsContext> vendorQuotesLineItems = record.getVendorQuotesLineItems();
                    List<V3VendorQuotesLineItemsContext> awardedVendorQuotesLineItems = vendorQuotesLineItems.stream().filter(V3VendorQuotesLineItemsContext::getIsLineItemAwarded).collect(Collectors.toList());
                    if(CollectionUtils.isNotEmpty(awardedVendorQuotesLineItems)) {
                        awardedVendorQuotes.add(record);
                    }
                }
                SelectRecordsBuilder<V3VendorQuotesContext> selectRecordsBuilder = new SelectRecordsBuilder<V3VendorQuotesContext>()
                        .beanClass(V3VendorQuotesContext.class)
                        .module(modBean.getModule(FacilioConstants.ContextNames.VENDOR_QUOTES))
                        .select(fields)
                        .andCondition(CriteriaAPI.getCondition("RFQ_ID","requestForQuotation",String.valueOf(rfqId), NumberOperators.EQUALS))
                        .andCondition(CriteriaAPI.getCondition(purchaseOrderField, CommonOperators.IS_NOT_EMPTY));
                List<V3VendorQuotesContext> vendorQuotesWithPoCreated = selectRecordsBuilder.get();
                if(CollectionUtils.isNotEmpty(vendorQuotesWithPoCreated)){
                    if(awardedVendorQuotes.size() == vendorQuotesWithPoCreated.size()){
                        JSONObject bodyParams = new JSONObject();
                        bodyParams.put("createPo",true);
                        Map<String, Object> rawRecord = new HashMap<>();
                        rawRecord.put("isPoCreated", true);
                        V3Util.updateBulkRecords(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION, rawRecord, Collections.singletonList(rfqId),bodyParams,null,false);
                    }
                }
            }
        }
    }
}
