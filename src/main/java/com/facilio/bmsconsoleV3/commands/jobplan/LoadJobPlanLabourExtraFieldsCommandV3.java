package com.facilio.bmsconsoleV3.commands.jobplan;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoadJobPlanLabourExtraFieldsCommandV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        boolean fetchOnlyViewGroupColumn = (boolean) context.getOrDefault(FacilioConstants.ContextNames.FETCH_ONLY_VIEW_GROUP_COLUMN, false);

        if (fetchOnlyViewGroupColumn) {

            List<FacilioField> jobPlanLabourFields = new ArrayList<>();

            Map<String, FacilioField> allFieldsAsMap = FieldFactory.getAsMap(Constants.getModBean().getAllFields(FacilioConstants.ContextNames.JOB_PLAN_LABOURS));

            String[] extraFieldNames = new String[]{"sysCreatedBy","sysCreatedTime","sysModifiedBy","sysModifiedTime"};

            for (String fieldName : extraFieldNames) {
                if (allFieldsAsMap.containsKey(fieldName)) {
                    jobPlanLabourFields.add((allFieldsAsMap.get(fieldName)));
                }
            }

            context.put(FacilioConstants.ContextNames.EXTRA_SELECTABLE_FIELDS, jobPlanLabourFields);

        }

        return false;
    }
}
