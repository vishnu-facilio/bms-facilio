package com.facilio.fsm.commands.timeSheet;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.exception.FSMErrorCode;
import com.facilio.fsm.exception.FSMException;
import com.facilio.fsm.util.ServiceTaskUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class StopTimeSheetCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
        String transitionToState = (String) context.get(FacilioConstants.ContextNames.STATUS);
        boolean validate = (boolean) context.getOrDefault(FacilioConstants.ContextNames.DO_VALIDTION,false);
        if(!validate && transitionToState == null){
            transitionToState = FacilioConstants.ContextNames.ServiceTaskStatus.ON_HOLD;
        }
        ServiceTaskUtil.stopTimeSheet(recordIds,transitionToState);
        return false;
    }
}
