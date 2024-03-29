package com.facilio.bmsconsoleV3.commands.accessibleSpaces;

import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsoleV3.util.AccessibleSpacesUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.List;

public class FetchAccessibleSpacesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long ouId = (Long) context.get(FacilioConstants.ContextNames.ORG_USER_ID);
        Long peopleId = (Long) context.get(FacilioConstants.ContextNames.PEOPLE_ID);
        JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
        String searchQuery = (String) context.get(FacilioConstants.ContextNames.SEARCH);
        Boolean fetchCount = (Boolean) context.get(FacilioConstants.ContextNames.FETCH_COUNT);
        int page, perPage = -1, offset = -1;
        if (pagination != null) {
            page = (int) pagination.get("page");
            perPage = (int) pagination.get("perPage");
            offset = (page - 1) * perPage;

            if (offset < 0) {
                offset = 0;
            }
        }
        List<BaseSpaceContext> baseSpaces = AccessibleSpacesUtil.getAccessibleSpaceList(ouId,peopleId, perPage, offset, searchQuery);
        if (fetchCount != null) {
            Long baseSpacesCount = AccessibleSpacesUtil.getAccessibleSpaceCount(ouId,peopleId,searchQuery);
            context.put(FacilioConstants.ContextNames.COUNT, baseSpacesCount);
        }
        context.put(FacilioConstants.ContextNames.ACCESSIBLE_SPACE, baseSpaces);
        return false;
    }
}

