package com.facilio.flows.command;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.flows.context.FlowContext;
import com.facilio.flows.util.FlowUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.List;

public class GetFlowListCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        int flowType = (context.get(FacilioConstants.ContextNames.TYPE)) == null ? -1 : (int) context.get(FacilioConstants.ContextNames.TYPE);
        String moduleName = null;
        if (flowType <= 0){
            return false;
        }
        if (flowType == 2){
            moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule  module = modBean.getModule(moduleName);
        JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("TYPE","flowType", String.valueOf(flowType), NumberOperators.EQUALS));
        if (module != null){
            criteria.addAndCondition(CriteriaAPI.getCondition("MODULE_ID","moduleId", String.valueOf(module.getModuleId()),NumberOperators.EQUALS));
        }

       List<FlowContext> flowList =  FlowUtil.getFlowList(criteria,pagination,"ID ASC");

        context.put(FacilioConstants.ContextNames.FLOWS,flowList);
        return false;
    }
}
