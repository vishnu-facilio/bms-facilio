package com.facilio.bmsconsole.commands.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.actions.ColorPaletteContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class AddColorPaletteCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
       ColorPaletteContext colorPalette= (ColorPaletteContext) context.get(FacilioConstants.ColourPalette.COLOR_PALETTE);

       if(colorPalette==null){
           throw new IllegalArgumentException("ColorPalette cannot be empty");
       }
        if(StringUtils.isEmpty(colorPalette.getColorCode())){
            throw new IllegalArgumentException("Colorcode cannot be empty");
        }
        if(StringUtils.isEmpty(colorPalette.getKeyName())){
            throw new IllegalArgumentException("KeyName cannot be empty");
        }
       long userId= AccountUtil.getCurrentUser().getId();
        colorPalette.setUserId(userId);

        Map<String, Object> props = FieldUtil.getAsProperties(colorPalette);
        GenericInsertRecordBuilder insertRecordBuilder=new GenericInsertRecordBuilder()
                .table(ModuleFactory.getColorPaletteModule().getTableName())
                .fields(FieldFactory.getColorPaletteFields())
                .addRecord(props);
        insertRecordBuilder.save();

        context.put(FacilioConstants.ColourPalette.COLOR_PALETTE,props);
        return false;
    }
}
