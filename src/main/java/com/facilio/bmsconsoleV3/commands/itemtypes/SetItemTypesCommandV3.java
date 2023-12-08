package com.facilio.bmsconsoleV3.commands.itemtypes;

import com.facilio.bmsconsoleV3.context.inventory.V3ItemTypesContext;
import com.facilio.bmsconsoleV3.enums.CostType;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class SetItemTypesCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3ItemTypesContext> itemTypes = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(itemTypes)) {
            for (V3ItemTypesContext itemType : itemTypes) {
                if(itemType.getCostType()==null){
                    itemType.setCostType(CostType.FIFO.getIndex());
                }
            }
        }
        return false;
    }
}
