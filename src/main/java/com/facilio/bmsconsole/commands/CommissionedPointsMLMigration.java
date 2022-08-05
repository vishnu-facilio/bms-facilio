package com.facilio.bmsconsole.commands;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.commands.AgentV2Command;
import com.facilio.agentv2.controller.ControllerApiV2;
import com.facilio.agentv2.point.PointEnum.ConfigureStatus;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.*;

public class CommissionedPointsMLMigration extends AgentV2Command {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long agentId = (Long) context.get(AgentConstants.AGENT_ID);
        Set<Long> controllerIds = (Set<Long>) ControllerApiV2.getControllerIds(Collections.singletonList(agentId));
        context.put(AgentConstants.CONTROLLER_ID,new ArrayList<>(controllerIds));


        FacilioModule pointModule = ModuleFactory.getPointModule();
        List<FacilioField>fields = FieldFactory.getPointFields();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getControllerIdField(pointModule), new ArrayList<>(controllerIds), NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.CONFIGURE_STATUS), String.valueOf(ConfigureStatus.CONFIGURED.getIndex()),NumberOperators.EQUALS));
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(pointModule.getTableName())
                .select(fields)
                .andCriteria(criteria);


        List<Map<String, Object>> data = builder.get();
        context.put(AgentConstants.POINTS,data);
        return false;
    }
}
