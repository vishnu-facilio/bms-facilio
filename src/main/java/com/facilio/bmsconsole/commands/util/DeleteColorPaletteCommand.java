package com.facilio.bmsconsole.commands.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

public class DeleteColorPaletteCommand extends FacilioCommand {

    public boolean executeCommand(Context context) throws Exception {

        long paletteId = (long) context.get(FacilioConstants.ContextNames.ID);

        GenericSelectRecordBuilder selectRecordBuilder=new GenericSelectRecordBuilder()
                .table(ModuleFactory.getColorPaletteModule().getTableName())
                .select(FieldFactory.getColorPaletteFields())
                .andCondition(CriteriaAPI.getIdCondition(paletteId, ModuleFactory.getColorPaletteModule()))
                .andCondition(CriteriaAPI.getCondition("USERID","userId",String.valueOf(AccountUtil.getCurrentUser().getId()), NumberOperators.EQUALS));

        if(selectRecordBuilder.get().isEmpty()){
            throw new IllegalArgumentException("Invalid ColorPalette");
        }

        GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getColorPaletteModule().getTableName())
                .andCondition(CriteriaAPI.getIdCondition(paletteId, ModuleFactory.getColorPaletteModule()))
                .andCondition(CriteriaAPI.getCondition("USERID","userId",String.valueOf(AccountUtil.getCurrentUser().getId()), NumberOperators.EQUALS));

        deleteBuilder.delete();

        return false;
    }
}
