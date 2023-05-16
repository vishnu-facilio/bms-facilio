package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsole.context.PMResourcePlanner;
import com.facilio.bmsconsoleV3.context.V3ResourceContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.bmsconsoleV3.util.V3ResourceAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

import static com.facilio.bmsconsoleV3.util.V3AssetAPI.isAssetInStoreRoom;

public class ValidateRotatingAssetPMResource extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<PMResourcePlanner> pmResourcePlanners = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(pmResourcePlanners)){
            for(PMResourcePlanner pmResourcePlanner : pmResourcePlanners){
                if(pmResourcePlanner.getResource()!=null){
                    V3ResourceContext resource = V3ResourceAPI.getResource(pmResourcePlanner.getResource().getId());
                    if(resource.getResourceTypeEnum().equals(V3ResourceContext.ResourceType.ASSET)){
                        V3AssetContext asset = V3RecordAPI.getRecord(FacilioConstants.ContextNames.ASSET,resource.getId(), V3AssetContext.class);
                        if(isAssetInStoreRoom(asset)){
                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Cannot create PM for the Rotating Asset - " + asset.getName());
                        }
                    }
                }
            }
        }
        return false;
    }
}
