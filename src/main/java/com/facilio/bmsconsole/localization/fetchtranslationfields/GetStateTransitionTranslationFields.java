package com.facilio.bmsconsole.localization.fetchtranslationfields;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.localization.translationImpl.StateFlowTransImpl;
import com.facilio.bmsconsole.localization.translationImpl.StateFlowTranslationImpl;
import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.bmsconsole.localization.util.TranslationsUtil;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.util.FacilioUtil;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public class GetStateTransitionTranslationFields implements TranslationTypeInterface {
    @Override
    public JSONArray constructTranslationObject ( @NonNull WebTabContext context,Map<String,String> filters,Properties properties ) throws Exception {

        FacilioUtil.throwIllegalArgumentException(!WebTabContext.Type.MODULE.equals(WebTabContext.Type.valueOf(context.getType())),"Invalid webTab Type for fetch Module Fields");
        FacilioUtil.throwIllegalArgumentException(StringUtils.isEmpty(filters.get("stateFlowId")),"stateFlow ID is mandatory");

        JSONArray jsonArray = new JSONArray();

        long stateFlowId = Long.parseLong(filters.get("stateFlowId"));

        FacilioChain stateTransitionChain = ReadOnlyChainFactory.getStateTransitionList();
        FacilioContext stateTransitionChainContext = stateTransitionChain.getContext();
        stateTransitionChainContext.put(FacilioConstants.ContextNames.STATE_FLOW_ID,stateFlowId);
        stateTransitionChain.execute();

        List<StateflowTransitionContext> stateTransitions = (List<StateflowTransitionContext>)stateTransitionChainContext.get(FacilioConstants.ContextNames.STATE_TRANSITION_LIST);

        if(CollectionUtils.isNotEmpty(stateTransitions)) {

            for (StateflowTransitionContext stateTransition : stateTransitions) {
                String id = String.valueOf(stateTransition.getId());
                String stateTransitionKey = StateFlowTranslationImpl.getTranslationKey(StateFlowTranslationImpl.STATE_TRANSITION,id);
                jsonArray.add(TranslationsUtil.constructJSON(stateTransition.getName(),StateFlowTranslationImpl.STATE_TRANSITION,TranslationConstants.DISPLAY_NAME,id,stateTransitionKey,properties));
            }
        }
        StateFlowRuleContext stateFlowRuleContext = (StateFlowRuleContext) WorkflowRuleAPI.getWorkflowRule(stateFlowId);

        if(stateFlowRuleContext != null){
            String stateFlow = String.valueOf(stateFlowRuleContext.getId());
            String stateFlowKey = StateFlowTranslationImpl.getTranslationKey(StateFlowTransImpl.STATE_FLOW,stateFlow);
            jsonArray.add(TranslationsUtil.constructJSON(stateFlowRuleContext.getName(),StateFlowTransImpl.STATE_FLOW,TranslationConstants.DISPLAY_NAME,stateFlow,stateFlowKey,properties));
        }

        JSONObject fieldObject = new JSONObject();
        fieldObject.put("fields",jsonArray);
        fieldObject.put("label","");

        JSONArray sectionArray = new JSONArray();
        sectionArray.add(fieldObject);

        return sectionArray;
    }
}
