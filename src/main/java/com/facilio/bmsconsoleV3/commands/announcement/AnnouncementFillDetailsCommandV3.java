package com.facilio.bmsconsoleV3.commands.announcement;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.announcement.AnnouncementContext;
import com.facilio.bmsconsoleV3.util.AnnouncementAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.CommandUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class AnnouncementFillDetailsCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> recordIds  = Constants.getRecordIds(context);

        if(CollectionUtils.isNotEmpty(recordIds)) {
            for(Long recId : recordIds) {
                AnnouncementContext announcement = (AnnouncementContext) CommandUtil.getModuleData(context, FacilioConstants.ContextNames.ANNOUNCEMENT, recId);
                if (announcement != null) {
                    AnnouncementAPI.setSharingInfo(announcement);
                }
            }
        }
        return false;
    }
}
