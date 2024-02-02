package com.facilio.bmsconsoleV3.commands.invoice;

import com.facilio.bmsconsoleV3.commands.quotation.HandleQuoteDecimalValuesCommand;
import com.facilio.bmsconsoleV3.context.invoice.InvoiceContextV3;
import com.facilio.bmsconsoleV3.context.invoice.InvoiceLineItemsContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.EnumField;
import com.facilio.modules.fields.EnumFieldValue;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

public class HandleInvoiceDataFormatting extends FacilioCommand {
    private static final Logger LOGGER = LogManager.getLogger(HandleQuoteDecimalValuesCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<InvoiceContextV3> list = recordMap.get(moduleName);

        int numberOfDecimalPoint = 2;
        Boolean canTruncate = false;

        FacilioField field = Constants.getModBean().getField("unitOfMeasure", FacilioConstants.ContextNames.INVOICE_LINE_ITEMS);
        EnumField enumField = (EnumField) field;
        List<EnumFieldValue<Integer>> enumValues = enumField.getValues();

        //Invoice Decimal Field Formatting
        if (CollectionUtils.isNotEmpty(list)) {
            for (InvoiceContextV3 invoice : list) {
                if (invoice != null) {
                    if (isGreaterDecimalValue(invoice.getTotalCost(), numberOfDecimalPoint)) {
                        Double value = invoice.getTotalCost();
                        invoice.setTotalCost(formatToNDecimalPlaces(value, numberOfDecimalPoint, canTruncate));
                    }

                    if (isGreaterDecimalValue(invoice.getSubTotal(), numberOfDecimalPoint)) {
                        Double value = invoice.getSubTotal();
                        invoice.setSubTotal(formatToNDecimalPlaces(value, numberOfDecimalPoint, canTruncate));
                    }

                    if (isGreaterDecimalValue(invoice.getTotalTaxAmount(), numberOfDecimalPoint)) {
                        Double value = invoice.getTotalTaxAmount();
                        invoice.setTotalTaxAmount(formatToNDecimalPlaces(value, numberOfDecimalPoint, canTruncate));
                    }

                    if(isGreaterDecimalValue(invoice.getAdjustmentsCost(), numberOfDecimalPoint)) {
                        Double value = invoice.getAdjustmentsCost();
                        invoice.setAdjustmentsCost(formatToNDecimalPlaces(value, numberOfDecimalPoint, canTruncate));
                    }

                    if(isGreaterDecimalValue(invoice.getMiscellaneousCharges(), numberOfDecimalPoint)) {
                        Double value = invoice.getMiscellaneousCharges();
                        invoice.setMiscellaneousCharges(formatToNDecimalPlaces(value, numberOfDecimalPoint, canTruncate));
                    }

                    if(isGreaterDecimalValue(invoice.getShippingCharges(), numberOfDecimalPoint)) {
                        Double value = invoice.getShippingCharges();
                        invoice.setShippingCharges(formatToNDecimalPlaces(value, numberOfDecimalPoint, canTruncate));
                    }
                }
                //LineItems Name fill for PDF Template
                List<InvoiceLineItemsContext> lineItemsContextList = invoice.getLineItems();
                if(lineItemsContextList != null) {
                    for (InvoiceLineItemsContext lineItem : lineItemsContextList) {
                        if (lineItem.getTypeEnum().equals(InvoiceLineItemsContext.Type.LABOUR) && lineItem.getLabour() != null) {
                            lineItem.setDescription(lineItem.getLabour().getName());
                        } else if (lineItem.getTypeEnum().equals(InvoiceLineItemsContext.Type.SERVICE) && lineItem.getService() != null) {
                            lineItem.setDescription(lineItem.getService().getName());
                        } else if (lineItem.getTypeEnum().equals(InvoiceLineItemsContext.Type.TOOL_TYPE) && lineItem.getToolType() != null) {
                            lineItem.setDescription(lineItem.getToolType().getName());
                        } else if (lineItem.getTypeEnum().equals(InvoiceLineItemsContext.Type.ITEM_TYPE) && lineItem.getItemType() != null) {
                            lineItem.setDescription(lineItem.getItemType().getName());
                        }
                        //Unit Of Measure Enum Fill
                        Long unitOfMeasure = lineItem.getUnitOfMeasure();
                        if (unitOfMeasure == null || unitOfMeasure <= 0) {
                            continue;
                        }
                        EnumFieldValue<Integer> value = enumValues.stream().filter(e -> e.getIndex() == unitOfMeasure.intValue()).findFirst().orElse(null);

                        if (value == null) {
                            continue;
                        }
                        lineItem.setUnitOfMeasureEnum(value.getValue());
                    }
                }
            }
        } else {
            LOGGER.error("Invoice List is empty");
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
