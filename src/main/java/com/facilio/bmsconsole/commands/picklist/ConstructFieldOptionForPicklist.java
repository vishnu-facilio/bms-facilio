package com.facilio.bmsconsole.commands.picklist;

import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.FieldOption;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

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
            boolean localSearch = true;
            JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
            if (pagination != null) {
                int page = (int) pagination.get("page");
                int perPage = (int) pagination.get("perPage");
                localSearch = page == 1 && pickListRecordCount < perPage;
            }
            context.put(FacilioConstants.PickList.LOCAL_SEARCH, localSearch);
        }
        context.put(FacilioConstants.ContextNames.PICKLIST, pickList);
        return false;
    }
}
