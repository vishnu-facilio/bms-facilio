package com.facilio.bmsconsoleV3.commands.jobplan;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FetchExtraFieldsForJobPlanCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        boolean fetchOnlyViewGroupColumn = (boolean) context.getOrDefault(FacilioConstants.ContextNames.FETCH_ONLY_VIEW_GROUP_COLUMN, false);
        if (fetchOnlyViewGroupColumn && StringUtils.isNotEmpty(moduleName)) {
            List<FacilioField> allFields = Constants.getModBean().getAllFields(moduleName);
            Map<String, FacilioField> allFieldsAsMap = FieldFactory.getAsMap(allFields);
            List<FacilioField> extraFields = new ArrayList<>();
            FacilioField jpStatus = allFieldsAsMap.get("jpStatus");
            extraFields.add(jpStatus);
            context.put(FacilioConstants.ContextNames.EXTRA_SELECTABLE_FIELDS, extraFields);
        }
        return false;
    }
}
