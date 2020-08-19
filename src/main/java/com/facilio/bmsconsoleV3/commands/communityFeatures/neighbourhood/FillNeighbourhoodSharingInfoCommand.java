package com.facilio.bmsconsoleV3.commands.communityFeatures.neighbourhood;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.tenantEngagement.NeighbourhoodContext;
import com.facilio.bmsconsoleV3.context.tenantEngagement.NeighbourhoodSharingContext;
import com.facilio.bmsconsoleV3.util.AnnouncementAPI;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.CommandUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class FillNeighbourhoodSharingInfoCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> recordIds  = Constants.getRecordIds(context);
        String moduleName = Constants.getModuleName(context);
        if(CollectionUtils.isNotEmpty(recordIds)) {
            for(Long recId : recordIds) {
                NeighbourhoodContext record = (NeighbourhoodContext) CommandUtil.getModuleData(context, moduleName,recId);
                if (record != null) {
                    List<NeighbourhoodSharingContext> list = (List<NeighbourhoodSharingContext>) AnnouncementAPI.getSharingInfo(record, "neighbourhoodsharing", "neighbourhood");
                    if (CollectionUtils.isNotEmpty(list)) {
                        record.setNeighbourhoodsharing(list);
                    }
                }
            }
        }

        return false;
    }
}
