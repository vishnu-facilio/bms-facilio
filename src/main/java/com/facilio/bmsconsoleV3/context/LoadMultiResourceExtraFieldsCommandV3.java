package com.facilio.bmsconsoleV3.context;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoadMultiResourceExtraFieldsCommandV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        boolean fetchOnlyViewGroupColumn = (boolean) context.getOrDefault(FacilioConstants.ContextNames.FETCH_ONLY_VIEW_GROUP_COLUMN, false);

        if (fetchOnlyViewGroupColumn) {

            List<FacilioField> multiResourceFields = new ArrayList<>();

            Map<String, FacilioField> allFieldsAsMap = FieldFactory.getAsMap(Constants.getModBean().getAllFields(FacilioConstants.MultiResource.NAME));

            String[] extraFieldNames = new String[]{"sysCreatedBy","sysCreatedTime","sysModifiedBy","sysModifiedTime"};

            for (String fieldName : extraFieldNames) {
                if (allFieldsAsMap.containsKey(fieldName)) {
                    multiResourceFields.add((allFieldsAsMap.get(fieldName)));
                }
            }

            context.put(FacilioConstants.ContextNames.EXTRA_SELECTABLE_FIELDS, multiResourceFields);

        }

        return false;
    }
}
