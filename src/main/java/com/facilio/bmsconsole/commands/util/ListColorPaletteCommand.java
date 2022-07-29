package com.facilio.bmsconsole.commands.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

public class ListColorPaletteCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<String> keys= (List<String>) context.get(FacilioConstants.ColourPalette.COLOR_KEYS);

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getColorPaletteModule().getTableName())
                .select(FieldFactory.getColorPaletteFields())
                .andCondition(CriteriaAPI.getCondition("KEY_NAME","keyName", StringUtils.join(keys,","), StringOperators.IS))
                .andCondition(CriteriaAPI.getCondition("USERID","userId",String.valueOf(AccountUtil.getCurrentUser().getId()),NumberOperators.EQUALS));

        List<Map<String,Object>>props=selectRecordBuilder.get();
        JSONObject colorPaletteList=new JSONObject();
        for(String key:keys){
            JSONArray colorPaletteArray=new JSONArray();
            for (Map<String,Object> prop:props) {
                if(prop.get("keyName")!=null) {
                    if (prop.get("keyName").equals(key)) {
                        prop.remove("keyName");
                        prop.remove("userId");
                        prop.remove("orgId");
                        colorPaletteArray.add(prop);
                    }
                }
            }
                colorPaletteList.put(key, colorPaletteArray);
        }
        context.put(FacilioConstants.ColourPalette.COLOR_PALETTE,colorPaletteList);

        return false;
    }
}
