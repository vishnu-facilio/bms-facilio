package com.facilio.bmsconsoleV3.commands.itemtypes;

import com.facilio.bmsconsoleV3.context.inventory.V3ItemTypesContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class ValidateItemTypeCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3ItemTypesContext> itemTypes = recordMap.get(moduleName);
        Map<Long, V3ItemTypesContext> oldItemTypesRecordMap = (Map<Long,V3ItemTypesContext>) (((Map<String,Object>)context.get(FacilioConstants.ContextNames.OLD_RECORD_MAP)).get(FacilioConstants.ContextNames.ITEM_TYPES));
        if(CollectionUtils.isNotEmpty(itemTypes)){
            for(V3ItemTypesContext itemType: itemTypes){
                V3ItemTypesContext oldItemType = oldItemTypesRecordMap.get(itemType.getId());
                if(oldItemType.isRotating() !=itemType.isRotating()){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Cannot update Is Rotating field");
                }
            }
        }
        return false;
    }
}
