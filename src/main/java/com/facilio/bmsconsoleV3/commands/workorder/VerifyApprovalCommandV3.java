package com.facilio.bmsconsoleV3.commands.workorder;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

@Log4j
public class VerifyApprovalCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        boolean skipApproval = (boolean) context.getOrDefault(FacilioConstants.ContextNames.SKIP_APPROVAL,false);
        if(skipApproval){
            return false;
        }
        List<ModuleBaseWithCustomFields> recordList = Constants.getRecordList((FacilioContext) context);
        String moduleName = Constants.getModuleName(context);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        Map<String,Object> bodyParam = Constants.getBodyParams(context);

        skipApproval = bodyParam != null ? (boolean) bodyParam.getOrDefault(FacilioConstants.ContextNames.SKIP_APPROVAL,false) : false;

        if (skipApproval){
            return false;
        }

        if (module == null) {
            throw new IllegalArgumentException("Invalid module, " + moduleName);
        }
        Long stateTransitionId = (Long) context.get(FacilioConstants.ContextNames.TRANSITION_ID);
        boolean skipChecking =  (boolean) context.getOrDefault(FacilioConstants.ContextNames.SKIP_APPROVAL_CHECK, false);

        if (recordList != null && !recordList.isEmpty()) {
            for (ModuleBaseWithCustomFields record : recordList) {
                if (record.getModuleState() != null) {
                    FacilioStatus stateContext = StateFlowRulesAPI.getStateContext(record.getModuleState().getId());

                    boolean cannotEdit = false;
                    if (stateContext.isRecordLocked()) {
                        if ((stateTransitionId == null || stateTransitionId == -1)) {
                            cannotEdit = true;
                        }
                    }

                    // -99 will be set when he approved the module record, so should go to the if-block
                    if (record.getApprovalFlowId() == -99 || (record.getApprovalFlowId() > 0 && record.getApprovalStatus() != null)) {
                        if (!skipChecking) {
                            FacilioStatus status = TicketAPI.getStatus(record.getApprovalStatus().getId());
                            if (status.isRequestedState()) {
                                logRecordIdAndParentModuleId(record.getId(), stateContext.getParentModuleId());
//                                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Record " + record.getId() + " of Module " + module.getDisplayName() + " is in Approval process, cannot edit meanwhile");
                                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Record is in Approval process, cannot edit meanwhile");
                            }
                        }
                    }
                    else if ((stateContext.isRecordLocked())) {
                        if (cannotEdit) {
                            logRecordIdAndParentModuleId(record.getId(), stateContext.getParentModuleId());
//                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Record " + record.getId() + " of Module " + module.getDisplayName() + " is locked, cannot be updated");
                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Record is locked, cannot be updated");
                        }
                    }
                }
            }
        }

        return false;
    }
    private void logRecordIdAndParentModuleId(long recordId, long parentModuleId){
        LOGGER.info("Record ID: " + recordId);
        LOGGER.info("Parent Module ID: " + parentModuleId);
    }
}
