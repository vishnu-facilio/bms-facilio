package com.facilio.bmsconsoleV3.commands.quotation;

import com.facilio.bmsconsoleV3.context.quotation.NumberFormatContext;
import com.facilio.bmsconsoleV3.util.QuotationAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

public class DeleteNumberFormatCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        NumberFormatContext numberFormat = QuotationAPI.fetchNumberFormat();
        if (numberFormat != null) {

            FacilioModule module = ModuleFactory.getNumberformatModule();
            GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                    .table(ModuleFactory.getNumberformatModule().getTableName())
                    .andCondition(CriteriaAPI.getIdCondition(numberFormat.getId(),module));
            builder.delete();

            return builder.delete()==1;
        }

        return false;

    }
}
