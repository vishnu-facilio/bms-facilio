package com.facilio.bmsconsoleV3.commands.invoice;

import com.facilio.bmsconsoleV3.context.V3VendorContactContext;
import com.facilio.bmsconsoleV3.context.V3VendorContext;
import com.facilio.bmsconsoleV3.context.invoice.InvoiceContextV3;
import com.facilio.bmsconsoleV3.util.InvoiceAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsoleV3.context.V3ClientContactContext;
import com.facilio.bmsconsoleV3.context.V3TenantContactContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;

public class InvoiceValidationAndCostCalculationCommand extends FacilioCommand {
    private static final Logger LOGGER = LogManager.getLogger(InvoiceValidationAndCostCalculationCommand.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<InvoiceContextV3> list = recordMap.get(moduleName);
        Map<Long, List<String>> patchFieldNamesMap = (Map<Long, List<String>>) context.getOrDefault(FacilioConstants.ContextNames.PATCH_FIELD_NAMES, new HashMap<>());

        if (CollectionUtils.isNotEmpty(list)) {
            for (InvoiceContextV3 invoice : list) {
                InvoiceAPI.lineItemsCostCalculations(invoice, invoice.getLineItems());
                // fields which are updated internally to be added in this List so recalculation of currency fields can be avoided
                List<String> fieldNames = patchFieldNamesMap.getOrDefault(patchFieldNamesMap.get(invoice.getId()), new ArrayList<>());
                fieldNames.add("totalTaxAmount");
                fieldNames.add("subTotal");
                fieldNames.add("discountAmount");
                fieldNames.add("totalCost");
                patchFieldNamesMap.put(invoice.getId(), fieldNames);

                InvoiceAPI.lineItemsCostCalculationsForInvoice(invoice, invoice.getLineItems(), context);
                Double invoiceTotalCost = invoice.getTotalCost();
                if(invoice.getInvoiceType() == null)
                {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invoice type is Mandatory");
                }
                if(Objects.equals(invoice.getInvoiceType(), InvoiceContextV3.InvoiceType.CLIENT.getIndex()) && invoice.getClient() == null)
                {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Client is Mandatory for Client Invoice Type");
                }
                if(Objects.equals(invoice.getInvoiceType(), InvoiceContextV3.InvoiceType.TENANT.getIndex()) && invoice.getTenant() == null)
                {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Tenant is Mandatory for Tenant Invoice Type");
                }
                if(Objects.equals(invoice.getInvoiceType(), InvoiceContextV3.InvoiceType.VENDOR.getIndex()) && invoice.getVendor() == null)
                {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Vendor is Mandatory for Vendor Invoice Type");
                }
                if (invoice.getShippingCharges() != null) {
                    invoiceTotalCost += invoice.getShippingCharges();
                }
                if (invoice.getMiscellaneousCharges() != null) {
                    invoiceTotalCost += invoice.getMiscellaneousCharges();
                }
                if (invoice.getAdjustmentsCost() != null) {
                    invoiceTotalCost += invoice.getAdjustmentsCost();
                }
                invoice.setTotalCost(invoiceTotalCost);

                if(invoice.getRevisionNumber() == null){
                    invoice.setRevisionNumber(0L);
                }
                if(invoice.getInvoiceStatus() == null){
                    invoice.setInvoiceStatusEnum(InvoiceContextV3.InvoiceStatus.DRAFT);
                }
                if (invoice.getBillToAddress() != null) {
                    LocationContext billToAddressLocation = invoice.getBillToAddress();
                    InvoiceAPI.addLocation(invoice, billToAddressLocation);
                }
                if (invoice.getShipToAddress() != null) {
                    LocationContext shipToAddressLocation = invoice.getShipToAddress();
                    InvoiceAPI.addLocation(invoice, shipToAddressLocation);
                }
                if(InvoiceAPI.lookupValueIsNotEmpty(invoice.getVendor())) {
                    if(invoice.getContact() == null) {
                        List<V3VendorContactContext> vendorContacts =  V3PeopleAPI.getVendorContacts(invoice.getVendor().getId(), true, false);
                        if(CollectionUtils.isNotEmpty(vendorContacts)) {
                            invoice.setContact(vendorContacts.get(0));
                        }
                    }
                }
                else if (InvoiceAPI.lookupValueIsNotEmpty(invoice.getClient())){
                    if(invoice.getContact() == null) {
                        List<V3ClientContactContext> clientContacts =  V3PeopleAPI.getClientContacts(invoice.getClient().getId(), true);
                        if(CollectionUtils.isNotEmpty(clientContacts)) {
                            invoice.setContact(clientContacts.get(0));
                        }
                    }
                }
                else if(InvoiceAPI.lookupValueIsNotEmpty(invoice.getTenant())) {
                    if(invoice.getContact() == null) {
                        List<V3TenantContactContext> tenantContacts =  V3PeopleAPI.getTenantContacts(invoice.getTenant().getId(), true, false);
                        if(CollectionUtils.isNotEmpty(tenantContacts)) {
                            invoice.setContact(tenantContacts.get(0));
                        }
                    }
                }
            }
        } else {
            LOGGER.error("Invoice List is empty");
        }

        context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);

        return false;
    }
}
