package com.facilio.bmsconsoleV3.commands.quotation;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.context.quotation.QuotationSettingContext;
import com.facilio.bmsconsoleV3.context.readingimportapp.V3ReadingImportAppContext;
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

public class updateQuotationSettingCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        QuotationSettingContext quotationSetting = (QuotationSettingContext) context.get(FacilioConstants.ContextNames.QUOTATIONSETTING);
        FacilioUtil.throwIllegalArgumentException(quotationSetting == null, "Reading import context should not be null");


        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getQuoteSettingModule().getTableName())
                .fields(FieldFactory.getQuoteSettingFields())
                .andCondition(CriteriaAPI.getIdCondition(quotationSetting.getId(), ModuleFactory.getQuoteSettingModule()));

        Map<String, Object> props = FieldUtil.getAsProperties(quotationSetting);
        updateBuilder.update(props);

        return false;
    }
}
