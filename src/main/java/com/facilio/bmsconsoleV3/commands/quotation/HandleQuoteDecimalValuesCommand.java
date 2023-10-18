package com.facilio.bmsconsoleV3.commands.quotation;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsoleV3.context.quotation.NumberFormatContext;
import com.facilio.bmsconsoleV3.context.quotation.QuotationContext;
import com.facilio.bmsconsoleV3.util.QuotationAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;


public class HandleQuoteDecimalValuesCommand extends FacilioCommand {
    private static final Logger LOGGER = LogManager.getLogger(HandleQuoteDecimalValuesCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        ApplicationContext app = AccountUtil.getCurrentApp();

            String moduleName = Constants.getModuleName(context);
            Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
            List<QuotationContext> list = recordMap.get(moduleName);

            NumberFormatContext numberFormat = QuotationAPI.fetchNumberFormat();


            int numberOfDecimalPoint = 2;
            Boolean canTruncate = false;

            if(numberFormat != null && numberFormat.getEnableNumberFormat()) {
                numberOfDecimalPoint = numberFormat.getNumberOfDecimal();
                canTruncate = numberFormat.getCanTruncateValue();
            }

            if (CollectionUtils.isNotEmpty(list)) {
                for (QuotationContext quotation : list) {
                    if (quotation != null) {
                        if (isGreaterDecimalValue(quotation.getTotalCost(), numberOfDecimalPoint)) {
                            Double value = quotation.getTotalCost();
                            quotation.setTotalCost(formatToNDecimalPlaces(value, numberOfDecimalPoint, canTruncate));
                        }

                        if (isGreaterDecimalValue(quotation.getSubTotal(), numberOfDecimalPoint)) {
                            Double value = quotation.getSubTotal();
                            quotation.setSubTotal(formatToNDecimalPlaces(value, numberOfDecimalPoint, canTruncate));
                        }

                        if (isGreaterDecimalValue(quotation.getTotalTaxAmount(), numberOfDecimalPoint)) {
                            Double value = quotation.getTotalTaxAmount();
                            quotation.setTotalTaxAmount(formatToNDecimalPlaces(value, numberOfDecimalPoint, canTruncate));
                        }

                        if(isGreaterDecimalValue(quotation.getAdjustmentsCost(), numberOfDecimalPoint)) {
                            Double value = quotation.getAdjustmentsCost();
                            quotation.setAdjustmentsCost(formatToNDecimalPlaces(value, numberOfDecimalPoint, canTruncate));
                        }

                        if(isGreaterDecimalValue(quotation.getMiscellaneousCharges(), numberOfDecimalPoint)) {
                            Double value = quotation.getMiscellaneousCharges();
                            quotation.setMiscellaneousCharges(formatToNDecimalPlaces(value, numberOfDecimalPoint, canTruncate));
                        }

                        if(isGreaterDecimalValue(quotation.getShippingCharges(), numberOfDecimalPoint)) {
                            Double value = quotation.getShippingCharges();
                            quotation.setShippingCharges(formatToNDecimalPlaces(value, numberOfDecimalPoint, canTruncate));
                        }


                    }
                }
            } else {
                LOGGER.error("Quotation List is empty");
            }


        return false;
    }

    public static boolean isGreaterDecimalValue(Double value, int noOfDecimal) throws Exception {
        if(value == null) {
            return false;
        }
        double factor = Math.pow(10, noOfDecimal);
        double roundedNum = Math.round(value * factor) / factor;
        return value != roundedNum;
    }

    public static double truncateToNDecimalPlaces(double num, int n) throws Exception {
        double factor = Math.pow(10, n);
        return Math.floor(num * factor) / factor;
    }

    public static double roundToNDecimalPlaces(double num, int n) throws Exception{
        double factor = Math.pow(10, n);
        return Math.round(num * factor) / factor;
    }


    public static double formatToNDecimalPlaces(double num, int n, Boolean canTruncate) throws Exception {
        if(canTruncate) {
            return truncateToNDecimalPlaces(num, n);
        }
        else {
            return roundToNDecimalPlaces(num, n);
        }
    }
}
