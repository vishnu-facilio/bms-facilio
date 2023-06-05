package com.facilio.bmsconsoleV3.commands.workorder.workorderFeature;

import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioStatus;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.V3Util;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;

/**
 * ValidateAndFetchRecordCommand
 * - This command has validation checks on the details that're mandatory to proceed.
 */
@Log4j
public class ValidateAndFetchRecordCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        if(recordId == null || recordId < 0L){
            LOGGER.error("Invalid record ID.");
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid record ID.");
        }

        if(moduleName == null || moduleName.isEmpty()){
            LOGGER.error("Module name cannot be empty for the record with ID " + recordId);
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Module name cannot be empty.");
        }

        V3WorkOrderContext workOrderContext = (V3WorkOrderContext) V3Util.getRecord(moduleName, recordId, null);

        if(workOrderContext == null){
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "WorkOrder not available.");
        }

        context.put(FacilioConstants.ContextNames.RECORD, workOrderContext);

        // Add current module state in context
        FacilioStatus currentModuleState;
        if(workOrderContext.getModuleState() != null){
            currentModuleState = workOrderContext.getModuleState();
        }else {
            currentModuleState = workOrderContext.getStatus(); // this fetches moduleState when it's PRE_OPEN
        }

        if(currentModuleState == null){
            LOGGER.error("Module State cannot be empty for the record with ID " + recordId + ", module name " + moduleName);
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Module State of the record cannot be empty.");
        }

        context.put(FacilioConstants.ContextNames.CURRENT_MODULE_STATE, currentModuleState);

        return false;
    }
}
