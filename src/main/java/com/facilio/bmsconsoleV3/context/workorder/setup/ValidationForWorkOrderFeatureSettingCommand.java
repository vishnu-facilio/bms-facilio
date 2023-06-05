package com.facilio.bmsconsoleV3.context.workorder.setup;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Log4j
public class ValidationForWorkOrderFeatureSettingCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        LOGGER.info("ValidationForWorkOrderFeatureSettingCommand");

        List<V3WorkOrderFeatureSettingsContext> workOrderFeatureSettings = (List<V3WorkOrderFeatureSettingsContext>) context.getOrDefault(FacilioConstants.ContextNames.WORK_ORDER_FEATURE_SETTINGS_LIST, new ArrayList<>());

//        if (CollectionUtils.isEmpty(workOrderFeatureSettings)){
//            throw new RESTException(ErrorCode.VALIDATION_ERROR,"Settings record cannot be empty.", null);
//        }

        return false;
    }
}
