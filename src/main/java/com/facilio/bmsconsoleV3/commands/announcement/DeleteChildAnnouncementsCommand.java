package com.facilio.bmsconsoleV3.commands.announcement;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.announcement.AnnouncementContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.FacilioTimer;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class DeleteChildAnnouncementsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<AnnouncementContext> announcements = recordMap.get(moduleName);

        if (CollectionUtils.isNotEmpty(announcements)) {
            FacilioContext jobContext =  new FacilioContext();
            jobContext.put(FacilioConstants.ContextNames.Tenant.ANNOUNCEMENTS, announcements);
            jobContext.put(FacilioConstants.ContextNames.Tenant.ANNOUNCEMENT_ACTION, 3);

            FacilioTimer.scheduleInstantJob("AddOrUpdateChildAnnouncementsJob", jobContext);
        }
        return false;
    }
}
