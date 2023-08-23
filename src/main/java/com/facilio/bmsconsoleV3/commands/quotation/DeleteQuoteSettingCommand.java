package com.facilio.bmsconsoleV3.commands.quotation;

import com.facilio.bmsconsoleV3.context.quotation.QuotationSettingContext;
import com.facilio.bmsconsoleV3.util.QuotationAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

public class DeleteQuoteSettingCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        QuotationSettingContext quotationSetting = QuotationAPI.fetchQuotationSetting();
        if (quotationSetting != null) {

            FacilioModule module = ModuleFactory.getQuoteSettingModule();
            GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                    .table(ModuleFactory.getQuoteSettingModule().getTableName())
                    .andCondition(CriteriaAPI.getIdCondition(quotationSetting.getId(),module));
            builder.delete();

            return builder.delete()==1;
        }

        return false;

    }
}
