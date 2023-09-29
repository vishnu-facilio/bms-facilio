package com.facilio.remotemonitoring.compute;

import com.facilio.agentv2.controller.Controller;
import com.facilio.bmsconsole.context.ControllerType;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.List;

public class ControllerUtil {
    public static Controller getSystemController() throws Exception {
        ControllerType systemControllerType = ControllerType.SYSTEM;
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("CONTROLLER_TYPE","controllerType",String.valueOf(systemControllerType.getIndex()), NumberOperators.EQUALS));
        List<Controller> controllerList = V3RecordAPI.getRecordsListWithSupplements(FacilioConstants.ContextNames.CONTROLLER, null, Controller.class, criteria, null);
        if(CollectionUtils.isNotEmpty(controllerList)) {
            return controllerList.get(0);
        }
        return null;
    }
}