package com.facilio.remotemonitoring.action;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.remotemonitoring.RemoteMonitorConstants;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RawAlarmAction extends V3Action {

    @Getter @Setter
    List<Map<String,Object>> rawAlarmList = new ArrayList<Map<String,Object>>();

    public String generateRawAlarms() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.generateRawAlarms();
        FacilioContext context = chain.getContext();
        context.put(RemoteMonitorConstants.RAW_ALARMS,rawAlarmList);
        chain.execute();
        return SUCCESS;
    }
}
