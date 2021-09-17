package com.facilio.bmsconsoleV3.commands.asset;

import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class AssetAfterSaveCommandV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<V3AssetContext> v3assetList = (List<V3AssetContext>) (((Map<String,Object>)context.get(FacilioConstants.ContextNames.RECORD_MAP)).get(FacilioConstants.ContextNames.ASSET));
        for(V3AssetContext asset : v3assetList){
            context.put(FacilioConstants.ContextNames.RECORD_ID,asset.getId());
            context.put(FacilioConstants.ContextNames.PARENT_ID,asset.getId());
        }

        return false;
    }
}
