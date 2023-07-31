package com.facilio.bmsconsoleV3.commands.quotation;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuotationValidationAndCostCalculationCommand extends FacilioCommand {
    private static final Logger LOGGER = LogManager.getLogger(QuotationValidationAndCostCalculationCommand.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<QuotationContext> list = recordMap.get(moduleName);
        Map<Long, List<String>> patchFieldNamesMap = (Map<Long, List<String>>) context.getOrDefault(FacilioConstants.ContextNames.PATCH_FIELD_NAMES, new HashMap<>());

        if (CollectionUtils.isNotEmpty(list)) {
            for (QuotationContext quotation : list) {
                QuotationAPI.lineItemsCostCalculations(quotation, quotation.getLineItems());
                // fields which are updated internally to be added in this List so recalculation of currency fields can be avoided
                List<String> fieldNames = patchFieldNamesMap.getOrDefault(patchFieldNamesMap.get(quotation.getId()), new ArrayList<>());
                fieldNames.add("totalTaxAmount");
                fieldNames.add("subTotal");
                fieldNames.add("discountAmount");
                fieldNames.add("totalCost");
                patchFieldNamesMap.put(quotation.getId(), fieldNames);

                Double quotationTotalCost = quotation.getTotalCost();
                if (quotation.getShippingCharges() != null) {
                    quotationTotalCost += quotation.getShippingCharges();
                }
                if (quotation.getMiscellaneousCharges() != null) {
                    quotationTotalCost += quotation.getMiscellaneousCharges();
                }
                if (quotation.getAdjustmentsCost() != null) {
                    quotationTotalCost += quotation.getAdjustmentsCost();
                }
                quotation.setTotalCost(quotationTotalCost);

                if(quotation.getRevisionNumber() == null){
                    quotation.setRevisionNumber(0L);
                }
                if (quotation.getBillToAddress() != null) {
                    LocationContext billToAddressLocation = quotation.getBillToAddress();
                    QuotationAPI.addLocation(quotation, billToAddressLocation);
                }
                if (quotation.getShipToAddress() != null) {
                    LocationContext shipToAddressLocation = quotation.getShipToAddress();
                    QuotationAPI.addLocation(quotation, shipToAddressLocation);
                }
                if (QuotationAPI.lookupValueIsNotEmpty(quotation.getClient())){
                    quotation.setCustomerType(QuotationContext.CustomerType.CLIENT.getIndex());
                    if(quotation.getContact() == null) {
                        List<V3ClientContactContext> clientContacts =  V3PeopleAPI.getClientContacts(quotation.getClient().getId(), true);
                        if(CollectionUtils.isNotEmpty(clientContacts)) {
                            quotation.setContact(clientContacts.get(0));
                        }
                    }
                }
                else if(QuotationAPI.lookupValueIsNotEmpty(quotation.getTenant())) {
                    quotation.setCustomerType(QuotationContext.CustomerType.TENANT.getIndex());
                    if(quotation.getContact() == null) {
                        List<V3TenantContactContext> tenantContacts =  V3PeopleAPI.getTenantContacts(quotation.getTenant().getId(), true, false);
                        if(CollectionUtils.isNotEmpty(tenantContacts)) {
                            quotation.setContact(tenantContacts.get(0));
                        }
                    }
                }
                else {
                    quotation.setCustomerType(QuotationContext.CustomerType.OTHERS.getIndex());
                }
//                QuotationAPI.validateForWorkorder(quotation);
            }
        } else {
            LOGGER.error("Quotation List is empty");
        }
        
        context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);

        return false;
    }
}
