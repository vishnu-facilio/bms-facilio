package com.facilio.bmsconsole.localization.fetchtranslationfields;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.util.FacilioUtil;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;

import java.util.List;
import java.util.Properties;

public class GetStateFlowTranslationFields implements TranslationTypeInterface{
    @Override
    public JSONArray constructTranslationObject ( @NonNull WebTabContext context,String queryString,Properties properties ) throws Exception {
        FacilioUtil.throwIllegalArgumentException(!WebTabContext.Type.MODULE.equals(WebTabContext.Type.valueOf(context.getType())),"Invalid webTab Type for fetch Module Fields");
        ModuleBean moduleBean = (ModuleBean)BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(context.getModuleIds().get(0));
        long stateFlowId=-1L;
        if(StringUtils.isNotEmpty(queryString)){
            stateFlowId = Long.parseLong(queryString);
        }
        JSONArray jsonArray = new JSONArray();
        FacilioChain chain = ReadOnlyChainFactory.getStateTransitionList();
        FacilioContext stateFlowContext =chain.getContext();
        stateFlowContext.put(FacilioConstants.ContextNames.STATE_FLOW_ID, stateFlowId);
        chain.execute();

        List<StateflowTransitionContext> stateTransitions = (List<StateflowTransitionContext>) stateFlowContext.get(FacilioConstants.ContextNames.STATE_TRANSITION_LIST);
        return jsonArray;
    }
}
