package com.facilio.bmsconsoleV3.commands.invoice;

import com.facilio.bmsconsoleV3.context.invoice.InvoiceSettingContext;
import com.facilio.bmsconsoleV3.context.quotation.QuotationSettingContext;
import com.facilio.bmsconsoleV3.util.InvoiceAPI;
import com.facilio.bmsconsoleV3.util.QuotationAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

public class DeleteInvoiceSettingCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        InvoiceSettingContext invoiceSetting = InvoiceAPI.getInvoiceSetting();
        if (invoiceSetting != null) {

            FacilioModule module = ModuleFactory.getInvoiceSettingModule();
            GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                    .table(ModuleFactory.getInvoiceSettingModule().getTableName())
                    .andCondition(CriteriaAPI.getIdCondition(invoiceSetting.getId(),module));
            builder.delete();

            return builder.delete()==1;
        }

        return false;

    }
}

