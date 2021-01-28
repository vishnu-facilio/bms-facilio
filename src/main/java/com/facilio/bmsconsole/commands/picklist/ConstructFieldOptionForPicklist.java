package com.facilio.bmsconsole.commands.picklist;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.FieldOption;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class ConstructFieldOptionForPicklist extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Map<String, Object>> records = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
        List<FieldOption<Long>> pickList = null;
        if(CollectionUtils.isNotEmpty(records)) {
            FacilioField defaultField = (FacilioField) context.get(FacilioConstants.ContextNames.DEFAULT_FIELD);
            FacilioField secondaryField = (FacilioField) context.get(FacilioConstants.PickList.SECONDARY_FIELD);
            String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
            boolean isResource = moduleName.equals(FacilioConstants.ContextNames.RESOURCE);
            pickList = RecordAPI.constructFieldOptionsFromRecords(records, defaultField, secondaryField, isResource);
            int pickListRecordCount = pickList == null ? 0 : pickList.size();

            JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
            // No null check because we have default values in V3PickListAction
            // If null check is added, default value for page and perPage should be handled here
            int page = (int) pagination.get("page");
            int perPage = (int) pagination.get("perPage");
            boolean localSearch = page == 1 && pickListRecordCount < perPage;
            context.put(FacilioConstants.PickList.LOCAL_SEARCH, localSearch);
        }
        context.put(FacilioConstants.ContextNames.PICKLIST, pickList);
        return false;
    }
}
