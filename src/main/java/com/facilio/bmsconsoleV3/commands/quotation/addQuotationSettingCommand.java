package com.facilio.bmsconsoleV3.commands.quotation;

import com.facilio.bmsconsoleV3.context.quotation.QuotationSettingContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;

import java.util.Map;

public class addQuotationSettingCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        QuotationSettingContext quotationSetting = (QuotationSettingContext) context.get(FacilioConstants.ContextNames.QUOTATIONSETTING);

        FacilioUtil.throwIllegalArgumentException(quotationSetting == null, "Quotation Setting context should not be null");


        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getQuoteSettingModule().getTableName())
                .fields(FieldFactory.getQuoteSettingFields());

        Map<String, Object> props = FieldUtil.getAsProperties(quotationSetting);

        insertBuilder.addRecord(props);
        insertBuilder.save();

        long quotationsettingId = (Long) props.get("id");
        quotationSetting.setId(quotationsettingId);
        context.put(FacilioConstants.ContextNames.QUOTATIONSETTING, quotationSetting);

        return false;
    }
}
