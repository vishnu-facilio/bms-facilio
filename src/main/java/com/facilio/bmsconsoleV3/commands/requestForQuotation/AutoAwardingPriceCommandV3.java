package com.facilio.bmsconsoleV3.commands.requestForQuotation;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationContext;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationLineItemsContext;
import com.facilio.bmsconsoleV3.context.vendorquotes.V3VendorQuotesLineItemsContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.LookupFieldMeta;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.CommandUtil;
import org.apache.commons.chain.Context;
import com.facilio.command.FacilioCommand;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AutoAwardingPriceCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        long id = Constants.getRecordIds(context).get(0);
        V3RequestForQuotationContext requestForQuotation = (V3RequestForQuotationContext) CommandUtil.getModuleData(context, FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION, id);
        List<V3RequestForQuotationLineItemsContext> requestForQuotationLineItems = requestForQuotation.getRequestForQuotationLineItems();

        Object awardQuote = Constants.getQueryParam(context, "awardQuote");

        if(awardQuote!=null && FacilioUtil.parseBoolean(Constants.getQueryParam(context, "awardQuote")) && requestForQuotation.getIsQuoteReceived()){

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            String lineItemModuleName = FacilioConstants.ContextNames.VENDOR_QUOTES_LINE_ITEMS;
            List<FacilioField> fields = new ArrayList<>();
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(lineItemModuleName));

            FacilioField counterPrice = fieldMap.get("counterPrice");
            FacilioField vendorQuotes = fieldMap.get("vendorQuotes");
            fields.add(vendorQuotes);

            LookupFieldMeta vendorQuotesLookup = new LookupFieldMeta((LookupField) fieldMap.get("vendorQuotes"));
            LookupField vendorLookup =(LookupField) modBean.getField("vendor", FacilioConstants.ContextNames.VENDOR_QUOTES);
            vendorQuotesLookup.addChildSupplement(vendorLookup);

            for(V3RequestForQuotationLineItemsContext requestForQuotationLineItem : requestForQuotationLineItems){
                SelectRecordsBuilder<V3VendorQuotesLineItemsContext> recordsBuilder = new SelectRecordsBuilder<V3VendorQuotesLineItemsContext>()
                        .moduleName(lineItemModuleName)
                        .beanClass(V3VendorQuotesLineItemsContext.class)
                        .andCondition(CriteriaAPI.getCondition("RFQ_LINE_ITEM_ID", "requestForQuotationLineItem", String.valueOf(requestForQuotationLineItem.getId()), NumberOperators.EQUALS));

                recordsBuilder.aggregate(BmsAggregateOperators.NumberAggregateOperator.MIN,  counterPrice);

                List<V3VendorQuotesLineItemsContext> recordList = recordsBuilder.get();
                if(CollectionUtils.isNotEmpty(recordList)){
                    double counterPriceValue = recordList.get(0).getCounterPrice();
                    SelectRecordsBuilder<V3VendorQuotesLineItemsContext> builder = new SelectRecordsBuilder<V3VendorQuotesLineItemsContext>()
                            .moduleName(lineItemModuleName)
                            .select(fields)
                            .beanClass(V3VendorQuotesLineItemsContext.class)
                            .andCondition(CriteriaAPI.getCondition("RFQ_LINE_ITEM_ID", "requestForQuotationLineItem", String.valueOf(requestForQuotationLineItem.getId()), NumberOperators.EQUALS))
                            .andCondition(CriteriaAPI.getCondition("COUNTER_PRICE", "counterPrice", String.valueOf(counterPriceValue), NumberOperators.EQUALS))
                            .fetchSupplements(Arrays.asList((LookupField) vendorQuotesLookup));

                    List<V3VendorQuotesLineItemsContext> list = builder.get();
                    requestForQuotationLineItem.setAwardedPrice(counterPriceValue);
                    requestForQuotationLineItem.setAwardedTo(list.get(0).getVendorQuotes().getVendor());
                }
                else{
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "No Vendors have given their counter prices");
                }
            }
        }

        return false;
    }
}
