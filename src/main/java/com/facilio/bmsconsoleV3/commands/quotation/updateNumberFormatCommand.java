package com.facilio.bmsconsoleV3.commands.quotation;

import com.facilio.bmsconsoleV3.context.quotation.NumberFormatContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;

import java.util.Map;

public class updateNumberFormatCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        NumberFormatContext numberformat = (NumberFormatContext) context.get(FacilioConstants.ContextNames.NUMBER_FORMAT);
        FacilioUtil.throwIllegalArgumentException(numberformat == null, "Number format context should not be null");


        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getNumberformatModule().getTableName())
                .fields(FieldFactory.getNumberFormatFields())
                .andCondition(CriteriaAPI.getIdCondition(numberformat.getId(), ModuleFactory.getNumberformatModule()));

        Map<String, Object> props = FieldUtil.getAsProperties(numberformat);
        updateBuilder.update(props);

        return false;
    }
}
