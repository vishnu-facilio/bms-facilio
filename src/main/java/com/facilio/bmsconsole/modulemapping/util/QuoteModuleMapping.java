package com.facilio.bmsconsole.modulemapping.util;

import com.facilio.bmsconsole.context.ModuleMappingContext;
import com.facilio.bmsconsole.context.ModuleMappings;
import com.facilio.bmsconsole.context.MultiFieldMapping;

public class QuoteModuleMapping {
    public static ModuleMappingContext quoteToInvoiceConversion() {
        return new ModuleMappings().addModuleMapping("QuoteToInvoiceConversion", "Quote To Invoice Conversion", "invoice", true)
                .addFieldMapping("customerType", "invoiceType",enummap())
                .addFieldMapping("subject","subject")
                .addFieldMapping("vendor","vendor")
                .addFieldMapping("tenant","tenant")
                .addFieldMapping("client","client")
                .addFieldMapping("workorder","workorder")
                .addFieldMapping("shipToAddress","shipToAddress")
                .addFieldMapping("expiryDate","expiryDate")
                .addFieldMapping("expiryDate","single_line_invoice")
                .addFieldMapping("datetime_quote","single_line_invoice_1")
                .addFieldMapping("billDate","billDate")
                .addSubModuleMapping("quotelineitems","invoicelineitems","lineItems","lineItems")
                .addFieldMapping("type","type",itemtypeenummap())
                .addFieldMapping("unitPrice","unitPrice")
                .addFieldMapping("quantity","quantity")
                .subModuleMappingDone()
                ;
    }

    private static MultiFieldMapping enummap() {
        MultiFieldMapping mapping = new MultiFieldMapping();
        mapping.add("Tenant", "Tenant");
        mapping.add("Client", "Client");
        mapping.add("Vendor", "Vendor");
        return mapping;
    }

    private static MultiFieldMapping itemtypeenummap() {
        MultiFieldMapping mapping = new MultiFieldMapping();
        mapping.add("Item Type", "Item Type");
        mapping.add("Tool Type", "Tool Type");
        mapping.add("Service", "Service");
        mapping.add("Labour", "Labour");
        mapping.add("Others", "Others");
        return mapping;
    }

}
