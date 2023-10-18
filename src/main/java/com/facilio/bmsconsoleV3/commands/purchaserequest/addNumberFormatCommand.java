package com.facilio.bmsconsoleV3.commands.purchaserequest;

import com.facilio.bmsconsoleV3.context.quotation.NumberFormatContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;

import java.util.Map;

public class addNumberFormatCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        NumberFormatContext numberformat = (NumberFormatContext) context.get(FacilioConstants.ContextNames.NUMBER_FORMAT);

        FacilioUtil.throwIllegalArgumentException(numberformat == null, "Number format context should not be null");


        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getNumberformatModule().getTableName())
                .fields(FieldFactory.getNumberFormatFields());

        Map<String, Object> props = FieldUtil.getAsProperties(numberformat);

        insertBuilder.addRecord(props);
        insertBuilder.save();

        long id = (Long) props.get("id");
        numberformat.setId(id);
        context.put(FacilioConstants.ContextNames.NUMBER_FORMAT, numberformat);

        return false;
    }
}