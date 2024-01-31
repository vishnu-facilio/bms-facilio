package com.facilio.bmsconsole.modulemapping.util;

import com.facilio.bmsconsole.context.ModuleMappingContext;
import com.facilio.bmsconsole.context.ModuleMappings;
import com.facilio.bmsconsole.context.MultiFieldMapping;
import com.facilio.constants.FacilioConstants;

public class ParsedBillModuleMapping {

    public static ModuleMappingContext parsedBillToUtilityBillTemplate() {
        return new ModuleMappings().addModuleMapping("parsedBillToUtilityBillMapping", "Parsed Bill To Utility Bill", FacilioConstants.UTILITY_INTEGRATION_BILLS, true)
                .addFieldMapping("billUid", "billUid")
                .addFieldMapping("meterUid", "meterUid")
                .addFieldMapping("customerUid", "customerUid")
                .addFieldMapping("utilityID", "utilityID")
                .addFieldMapping("serviceIdentifier", "serviceIdentifier")
                .addFieldMapping("serviceTariff", "serviceTariff")
                .addFieldMapping("serviceAddress", "serviceAddress")
                .addFieldMapping("meterNumber", "meterNumber") // List<String>
                .addFieldMapping("billingContact", "billingContact")
                .addFieldMapping("billingAddress", "billingAddress")
                .addFieldMapping("billingAccount", "billingAccount")
                .addFieldMapping("serviceClass", "serviceClass")
                .addFieldMapping("billStatementDate", "billStatementDate")
                .addFieldMapping("billEndDate", "billEndDate")
                .addFieldMapping("billTotalUnit", "billTotalUnit")
                .addFieldMapping("billTotalCost", "billTotalCost")
                .addFieldMapping("billTotalVolume", "billTotalVolume")
                .addFieldMapping("supplierType", "supplierType")
                .addFieldMapping("supplierName", "supplierName")
                .addFieldMapping("supplierServiceId", "supplierServiceId")
                .addFieldMapping("supplierTariff", "supplierTariff")
                .addFieldMapping("supplierTotalUnit", "supplierTotalUnit")
                .addFieldMapping("supplierTotalCost", "supplierTotalCost")
                .addFieldMapping("supplierTotalVolume", "supplierTotalVolume")
                .addFieldMapping("sourceType", "sourceType")
                .addFieldMapping("sourceUrl", "sourceUrl")
                .addFieldMapping("sourceDownloadUrl", "sourceDownloadUrl")
                .addFieldMapping("billFileId", "billFileId")
                .addFieldMapping("billType", "billType",billTypeEnumMap())
                .addFieldMapping("billStartDate","billStartDate")
                .addSubModuleMapping(FacilioConstants.Ocr.PRE_UTILITY_LINE_ITEMS,FacilioConstants.UTILITY_INTEGRATION_LINE_ITEMS,"utilityIntegrationLineItemContexts","utilityIntegrationLineItemContexts")
                .addFieldMapping("name", "name")
                .addFieldMapping("start", "start")
                .addFieldMapping("end", "end")
                .addFieldMapping("unit", "unit")
                .addFieldMapping("cost", "cost")
                .addFieldMapping("volume", "volume")
                .addFieldMapping("rate", "rate")
                .addFieldMapping("chargeKind", "chargeKind")
                .subModuleMappingDone()
                ;
    }

    private static MultiFieldMapping billTypeEnumMap() {
        MultiFieldMapping mapping = new MultiFieldMapping();
        mapping.add("Manually Generated", "Manually Generated");
        mapping.add("Auto Generated", "Auto Generated");
        return mapping;
    }
}
