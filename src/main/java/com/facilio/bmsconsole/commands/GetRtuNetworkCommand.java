package com.facilio.bmsconsole.commands;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.modbusrtu.RtuNetworkContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetRtuNetworkCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        if (context.containsKey(AgentConstants.CONTROLLER_TYPE) && context.get(AgentConstants.CONTROLLER_TYPE) == FacilioControllerType.MODBUS_RTU) {
            RtuNetworkContext network =
                    RtuNetworkContext.getRtuNetworkContext((long) context.get(AgentConstants.AGENT_ID),
                            context.get(AgentConstants.COM_PORT).toString());
            if (network != null) {
                Map<String, FacilioField> fieldsMap = Controller.getFieldsMap(FacilioConstants.ContextNames.MODBUS_RTU_CONTROLLER_MODULE_NAME);
                Criteria criteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
                List<Condition> conditions = new ArrayList<>();
                conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.NETWORK_ID), String.valueOf(network.getId()), NumberOperators.EQUALS));
                criteria.addAndConditions(conditions);
            }
        }
        return false;
    }
}
