package com.facilio.fields.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RemoveSortableInternalFields extends FacilioCommand {
    private static final List<String> fieldNames = new ArrayList<>(Arrays.asList(
            FacilioConstants.ContextNames.SITE_ID, FacilioConstants.ContextNames.STATE_FLOW_ID,
            FacilioConstants.ContextNames.SLA_POLICY_ID, FacilioConstants.ContextNames.FORM_ID,
            FacilioConstants.ContextNames.APPROVAL_STATUS, FacilioConstants.ContextNames.APPROVAL_FLOW_ID));
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<FacilioField> fieldsList = (List<FacilioField>) context.get(FacilioConstants.ContextNames.FIELDS);
        if(CollectionUtils.isNotEmpty(fieldsList)) {
            fieldsList.removeIf(field -> field != null && fieldNames.contains(field.getName()));
        }
        context.put(FacilioConstants.ContextNames.FIELDS, fieldsList);
        return false;
    }
}
