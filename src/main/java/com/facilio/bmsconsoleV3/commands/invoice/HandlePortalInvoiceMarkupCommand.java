package com.facilio.bmsconsoleV3.commands.invoice;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsoleV3.context.BaseLineItemContext;
import com.facilio.bmsconsoleV3.context.invoice.InvoiceContextV3;
import com.facilio.bmsconsoleV3.context.invoice.InvoiceLineItemsContext;

import com.facilio.bmsconsoleV3.util.InvoiceAPI;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

public class HandlePortalInvoiceMarkupCommand extends FacilioCommand {
    private static final Logger LOGGER = LogManager.getLogger(HandlePortalInvoiceMarkupCommand.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception {

        ApplicationContext app = AccountUtil.getCurrentApp();


        if(app != null && app.getLinkName() != null && ((app.getLinkName().equals(FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP)) || (app.getLinkName().equals(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP)) || (app.getLinkName().equals(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP)))) {

            String moduleName = Constants.getModuleName(context);
            Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
            List<InvoiceContextV3> list = recordMap.get(moduleName);
            if (CollectionUtils.isNotEmpty(list)) {
                for (InvoiceContextV3 invoice : list) {


                    if(invoice != null) {

                        Boolean showMarkup = InvoiceAPI.showMarkupValue(context, invoice);
                        Boolean isGlobalMarkup = invoice.getIsGlobalMarkup();
                        Double globalMarkupValue = invoice.getMarkup();

                        if(showMarkup == null) {
                            showMarkup = false;
                        }
                        if(isGlobalMarkup == null) {
                            isGlobalMarkup = false;
                        }

                        List<InvoiceLineItemsContext> lineItems = invoice.getLineItems();
                        if (CollectionUtils.isNotEmpty(lineItems)) {

                            for (BaseLineItemContext lineItem : (List<InvoiceLineItemsContext>) lineItems) {

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
                                }

                            }
                        }
                    }

                }
            } else {
                LOGGER.error("Invoice List is empty");
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
