
package com.facilio.bmsconsoleV3.commands.quotation;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsoleV3.context.BaseLineItemContext;
import com.facilio.bmsconsoleV3.context.V3VendorContactContext;
import com.facilio.bmsconsoleV3.context.quotation.QuotationLineItemsContext;
import com.facilio.bmsconsoleV3.context.quotation.TaxContext;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsoleV3.context.V3ClientContactContext;
import com.facilio.bmsconsoleV3.context.V3TenantContactContext;
import com.facilio.bmsconsoleV3.context.quotation.QuotationContext;
import com.facilio.bmsconsoleV3.util.QuotationAPI;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HandlePortalSummaryMarkupDataCommand extends FacilioCommand {
    private static final Logger LOGGER = LogManager.getLogger(QuotationValidationAndCostCalculationCommand.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception {

        ApplicationContext app = AccountUtil.getCurrentApp();


        if(app != null && (app.getLinkName().equals(FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP)) || (app.getLinkName().equals(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP)) || (app.getLinkName().equals(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP))) {

            String moduleName = Constants.getModuleName(context);
            Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
            List<QuotationContext> list = recordMap.get(moduleName);
            if (CollectionUtils.isNotEmpty(list)) {
                for (QuotationContext quotation : list) {


                    if(quotation != null) {

                        Boolean showMarkup = QuotationAPI.showMarkupValue(context, quotation);
                        Boolean isGlobalMarkup = quotation.getIsGlobalMarkup();
                        Double globalMarkupValue = quotation.getMarkup();

                        if(showMarkup == null) {
                            showMarkup = false;
                        }
                        if(isGlobalMarkup == null) {
                            isGlobalMarkup = false;
                        }

                        List<QuotationLineItemsContext> lineItems = quotation.getLineItems();
                        if (CollectionUtils.isNotEmpty(lineItems)) {

                            for (BaseLineItemContext lineItem : (List<QuotationLineItemsContext>) lineItems) {

                                if(isGlobalMarkup && showMarkup == false) {
                                    Double markup = getMarkupValue(lineItem, globalMarkupValue);
                                    Double quantity = lineItem.getQuantity();
                                    Double formatedUnitPrice = (lineItem.getUnitPrice() + markup);
                                    lineItem.setUnitPrice(formatedUnitPrice);
                                    Double lineItemCost = quantity * (lineItem.getUnitPrice() );
                                    lineItem.setCost(lineItemCost);
                                }
                                else if(isGlobalMarkup == false && showMarkup == false) {
                                    Double lineItemMarkupValue = lineItem.getMarkup();
                                    Double markup = getMarkupValue(lineItem, lineItemMarkupValue);
                                    Double quantity = lineItem.getQuantity();
                                    Double formatedUnitPrice = (lineItem.getUnitPrice() + markup);
                                    lineItem.setUnitPrice(formatedUnitPrice);
//                                    Double totalCost = quotation.getTotalCost() + (markup * quantity);
//                                    lineItem.setCost(totalCost);
                                }

                            }
                        }
                    }

                }
            } else {
                LOGGER.error("Quotation List is empty");
            }

            context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
        }

        return false;
    }


    public static  Double getMarkupValue(BaseLineItemContext lineItem, Double markupValue) throws Exception {

        if(markupValue == null) {
            markupValue = 0.0;
        }

        Double unitPrice = lineItem.getUnitPrice();

        Double percentageValue = 0.0;
        if(markupValue != null && unitPrice != null) {
            Double perValue = ((markupValue / 100) * unitPrice);
            BigDecimal bd = new BigDecimal(perValue).setScale(2, RoundingMode.HALF_UP);
            percentageValue = bd.doubleValue();
        }
        return  percentageValue;
    }
}
