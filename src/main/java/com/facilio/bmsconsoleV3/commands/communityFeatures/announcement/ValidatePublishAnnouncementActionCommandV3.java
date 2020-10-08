package com.facilio.bmsconsoleV3.commands.communityFeatures.announcement;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.communityfeatures.announcement.AnnouncementContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;

public class ValidatePublishAnnouncementActionCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<AnnouncementContext> list = recordMap.get(moduleName);
        Map<String, Object> bodyParams = Constants.getBodyParams(context);

        if(CollectionUtils.isNotEmpty(list) && MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("publish")) {
            for(AnnouncementContext announcement : list) {
                AnnouncementContext existingContext = (AnnouncementContext) V3RecordAPI.getRecord(moduleName, announcement.getId(), AnnouncementContext.class);
                if(existingContext != null && existingContext.isPublished()) {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "The announcement - "+announcement.getId()+" is already published");
                }
                announcement.setIsPublished(true);
            }
        }

        return false;
    }
}
