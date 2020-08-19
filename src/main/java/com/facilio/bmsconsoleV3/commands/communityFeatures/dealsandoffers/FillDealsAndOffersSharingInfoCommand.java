package com.facilio.bmsconsoleV3.commands.communityFeatures.dealsandoffers;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.tenantEngagement.DealsAndOffersContext;
import com.facilio.bmsconsoleV3.context.tenantEngagement.DealsAndOffersSharingContext;
import com.facilio.bmsconsoleV3.util.AnnouncementAPI;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.CommandUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class FillDealsAndOffersSharingInfoCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> recordIds  = Constants.getRecordIds(context);
        String moduleName = Constants.getModuleName(context);
        if(CollectionUtils.isNotEmpty(recordIds)) {
            for(Long recId : recordIds) {
                DealsAndOffersContext record = (DealsAndOffersContext) CommandUtil.getModuleData(context, moduleName,recId);
                if (record != null) {
                    List<DealsAndOffersSharingContext> list = (List<DealsAndOffersSharingContext>) AnnouncementAPI.getSharingInfo(record, "dealsandofferssharing", "deals");
                    if (CollectionUtils.isNotEmpty(list)) {
                        record.setDealsandofferssharing(list);
                    }
                }
            }
        }

        return false;
    }
}