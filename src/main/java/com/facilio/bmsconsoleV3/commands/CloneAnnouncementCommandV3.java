package com.facilio.bmsconsoleV3.commands;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import com.facilio.bmsconsoleV3.context.communityfeatures.announcement.AnnouncementContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;

public class CloneAnnouncementCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map < String, List > recordMap = (Map < String, List > ) context.get(Constants.RECORD_MAP);
        Map < String, Object > bodyParams = Constants.getBodyParams(context);
        List < AnnouncementContext > announcements = (List < AnnouncementContext > ) recordMap.get(moduleName);

        if (!MapUtils.isEmpty(bodyParams) && bodyParams.containsKey("clone")) {
            if ((boolean) bodyParams.get("clone")) {
                if (CollectionUtils.isNotEmpty(announcements)) {
                    List < AnnouncementContext > cloneRecordList = new ArrayList < > ();
                    for (AnnouncementContext announcement: announcements) {
                    	announcement.setIsPublished(false);
                    	announcement.setIsCancelled(false);
                    	announcement.setModuleState(null);
                    	announcement.setApprovalStatus(null);
                        cloneRecordList.add(announcement);
                    }
                    recordMap.put(moduleName, cloneRecordList);
                }
            }
        }
        return false;
    }
}