package com.facilio.agentv2.commands;

import com.facilio.agent.AgentType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.point.PointEnum;
import com.facilio.agentv2.point.PointsAPI;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PointsConfigurationCompleteCommand extends AgentV2Command{
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<String>pointNames = (List<String>) context.get(AgentConstants.POINT_NAMES);
        Controller controller = (Controller) context.get(AgentConstants.CONTROLLER);
        FacilioAgent agent = controller.getAgent();
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule pointModule = moduleBean.getModule(AgentConstants.POINT);
        if (pointModule == null) {
             pointModule = ModuleFactory.getPointModule();
        }
        if ((pointNames != null) && (!pointNames.isEmpty())) {
            FacilioChain editChain = TransactionChainFactory.getEditPointChain();
            FacilioContext editChainContext = editChain.getContext();
            Criteria criteria = new Criteria();
            criteria.addAndCondition(PointsAPI.getNameCondition(pointNames));
            criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getControllerIdField(pointModule), String.valueOf(controller.getId()), NumberOperators.EQUALS));
            editChainContext.put(FacilioConstants.ContextNames.CRITERIA, criteria);
            editChainContext.put(FacilioConstants.ContextNames.TO_UPDATE_MAP, Collections.singletonMap(AgentConstants.CONFIGURE_STATUS, PointEnum.ConfigureStatus.CONFIGURED.getIndex()));
            editChain.execute();
            if (agent.getAgentType() == AgentType.NIAGARA.getKey()){
                context.put(AgentConstants.POINT_NAMES,mlPointsToTag(pointNames,controller));
            }
            if (editChainContext.containsKey(FacilioConstants.ContextNames.ROWS_UPDATED) && ((Integer) editChainContext.get(FacilioConstants.ContextNames.ROWS_UPDATED) > 0)) {
                return false;
            }
        }
        return false;
    }

    public List<String> mlPointsToTag(List<String>pointNames,Controller controller) throws Exception {
        List<String>pointToTag = new ArrayList<>();
        List<Map<String,Object>> pointData = PointsAPI.getPointsFromDb(pointNames,controller);
        for (Map<String,Object> point:pointData) {
            pointToTag.add((String) point.get(AgentConstants.DISPLAY_NAME));
        }
        return pointToTag;
    }
}
