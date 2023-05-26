package com.facilio.bmsconsoleV3.commands.requestForQuotation;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationContext;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationLineItemsContext;
import com.facilio.bmsconsoleV3.context.vendorquotes.V3VendorQuotesContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateAwardedStatusCommandV3  extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        V3RequestForQuotationContext requestForQuotation = (V3RequestForQuotationContext) context.get(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION);
        List<V3RequestForQuotationLineItemsContext> requestForQuotationLineItems = (List<V3RequestForQuotationLineItemsContext>) context.get(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION_LINE_ITEMS);

        if(CollectionUtils.isNotEmpty(requestForQuotationLineItems) && requestForQuotation!=null) {
            List<Long> awardedVendors = new ArrayList<>();
            for(V3RequestForQuotationLineItemsContext requestForQuotationLineItem : requestForQuotationLineItems){
                if(requestForQuotationLineItem.getAwardedTo()!=null && !awardedVendors.contains(requestForQuotationLineItem.getAwardedTo().getId())){
                    awardedVendors.add(requestForQuotationLineItem.getAwardedTo().getId());
                }
            }
            getVendorQuotes(requestForQuotation.getId(),context);
            Map<String, List> vendorQuotesMap = (Map<String, List>) context.get(FacilioConstants.ContextNames.VENDOR_QUOTES);
            List<V3VendorQuotesContext> vendorQuotesList =  (List<V3VendorQuotesContext>) vendorQuotesMap.get(FacilioConstants.ContextNames.VENDOR_QUOTES);
            List<V3VendorQuotesContext> clonedVendorQuotesList = vendorQuotesList;
                    List<V3VendorQuotesContext>  updatedVendorQuotesList = new ArrayList<>();
            if(CollectionUtils.isNotEmpty(clonedVendorQuotesList)){
                for(V3VendorQuotesContext vendorQuote: clonedVendorQuotesList){
                    if(vendorQuote.getVendor()!=null && awardedVendors.contains(vendorQuote.getVendor().getId())){
                        vendorQuote.setAwardStatus(V3VendorQuotesContext.AwardStatus.AWARDED.getIndex());
                    }else{
                        vendorQuote.setAwardStatus(V3VendorQuotesContext.AwardStatus.NOT_AWARDED.getIndex());
                    }
                    updatedVendorQuotesList.add(vendorQuote);
                }
                FacilioModule vendorQuoteModule = modBean.getModule(FacilioConstants.ContextNames.VENDOR_QUOTES);
                V3Util.processAndUpdateBulkRecords(vendorQuoteModule, vendorQuotesMap.get(FacilioConstants.ContextNames.VENDOR_QUOTES),  FieldUtil.getAsMapList(updatedVendorQuotesList,V3VendorQuotesContext.class)  ,null,null,null,null,null,null,null,null,false,false);
            }
        }
        return false;
    }
    public  void  getVendorQuotes(Long rfqId,Context context) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String vendorQuotesModuleName = FacilioConstants.ContextNames.VENDOR_QUOTES;
        List<FacilioField> vendorQuotesFields = modBean.getAllFields(FacilioConstants.ContextNames.VENDOR_QUOTES);
        Class beanClass = V3VendorQuotesContext.class;
        SelectRecordsBuilder<ModuleBaseWithCustomFields> recordsBuilder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
                .moduleName(vendorQuotesModuleName)
                .select(vendorQuotesFields)
                .beanClass(beanClass)
                .andCondition(CriteriaAPI.getCondition("RFQ_ID", "requestForQuotation", String.valueOf(rfqId), NumberOperators.EQUALS));

        List<ModuleBaseWithCustomFields>  vendorQuotesList = recordsBuilder.get();
        Map<String, List<ModuleBaseWithCustomFields>> vendorQuotesMap = new HashMap<>();
        vendorQuotesMap.put(vendorQuotesModuleName, vendorQuotesList);
        context.put(FacilioConstants.ContextNames.VENDOR_QUOTES, vendorQuotesMap);

    }
}
