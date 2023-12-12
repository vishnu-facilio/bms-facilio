package com.facilio.bmsconsoleV3.commands.invoice;

import com.facilio.bmsconsoleV3.context.invoice.InvoiceSettingContext;
import com.facilio.bmsconsoleV3.context.quotation.QuotationSettingContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;

import java.util.Map;

public class AddOrUpdateInvoiceSettingCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        InvoiceSettingContext invoiceSetting = (InvoiceSettingContext) context.get(FacilioConstants.ContextNames.INVOICE_SETTING);
        FacilioUtil.throwIllegalArgumentException(invoiceSetting == null, "Reading import context should not be null");


        if(invoiceSetting.getId() !=null) {
            GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                    .table(ModuleFactory.getInvoiceSettingModule().getTableName())
                    .fields(FieldFactory.getInvoicSettingFields())
                    .andCondition(CriteriaAPI.getIdCondition(invoiceSetting.getId(), ModuleFactory.getInvoiceSettingModule()));

            Map<String, Object> props = FieldUtil.getAsProperties(invoiceSetting);
            updateBuilder.update(props);
        }
        else{
            GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getInvoiceSettingModule().getTableName())
                    .fields(FieldFactory.getInvoicSettingFields());

            Map<String, Object> props = FieldUtil.getAsProperties(invoiceSetting);

            insertBuilder.addRecord(props);
            insertBuilder.save();

            long invoiceSettingId = (Long) props.get("id");
            invoiceSetting.setId(invoiceSettingId);
            context.put(FacilioConstants.ContextNames.INVOICE_SETTING, invoiceSetting);
        }

        return false;
    }
}