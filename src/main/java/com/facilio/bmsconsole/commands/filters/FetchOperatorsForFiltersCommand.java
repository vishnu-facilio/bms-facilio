package com.facilio.bmsconsole.commands.filters;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.modules.FieldType;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.lang.StringUtils;

import java.util.Collections;
import java.util.Map;

public class FetchOperatorsForFiltersCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String dataTypeStr = (String) context.get(FacilioConstants.Filters.FILTER_DATA_TYPE);
        FacilioUtil.throwIllegalArgumentException(StringUtils.isEmpty(dataTypeStr), "Invalid Field Data Type to fetch operators");

        FieldType fieldType = FieldType.valueOf(dataTypeStr);
        Map<String, Operator> operators = fieldType.getOperators();
        context.put(FacilioConstants.Filters.FILTER_OPERATORS, operators.values());

        return false;
    }
}
