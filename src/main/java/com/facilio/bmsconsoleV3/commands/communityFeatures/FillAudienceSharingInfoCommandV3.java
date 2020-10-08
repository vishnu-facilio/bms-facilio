package com.facilio.bmsconsoleV3.commands.communityFeatures;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.communityfeatures.AudienceContext;
import com.facilio.bmsconsoleV3.util.CommunityFeaturesAPI;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class FillAudienceSharingInfoCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<AudienceContext> list = recordMap.get(moduleName);

        //temp handling.this command can be removed once fetch related records are fetched in v3
        if(CollectionUtils.isNotEmpty(list)) {
            for(AudienceContext audience : list) {
                CommunityFeaturesAPI.setAudienceSharingInfo(audience);
            }
        }
        return false;
    }
}
