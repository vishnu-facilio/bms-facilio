package com.facilio.bmsconsole.localization.fetchtranslationfields;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.localization.translationImpl.StateFlowTranslationImpl;
import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.bmsconsole.localization.util.TranslationsUtil;
import com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.util.FacilioUtil;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Properties;

public class GetStateFlowTranslationFields implements TranslationTypeInterface {
    @Override
    public JSONArray constructTranslationObject ( @NonNull WebTabContext context,String queryString,Properties properties ) throws Exception {

        FacilioUtil.throwIllegalArgumentException(!WebTabContext.Type.MODULE.equals(WebTabContext.Type.valueOf(context.getType())),"Invalid webTab Type for fetch Module Fields");

        ModuleBean moduleBean = (ModuleBean)BeanFactory.lookup("ModuleBean");
        JSONArray jsonArray = new JSONArray();
        List<Long> moduleIds = context.getModuleIds();

        if(CollectionUtils.isNotEmpty(moduleIds)) {

            for (long moduleId : moduleIds) {

                FacilioModule module = moduleBean.getModule(moduleId);
                FacilioChain statusListChain = FacilioChainFactory.getTicketStatusListChain();
                FacilioContext stateContext = statusListChain.getContext();
                stateContext.put(FacilioConstants.ContextNames.PARENT_MODULE,module.getName());
                stateContext.put(FacilioConstants.ContextNames.APPROVAL_STATUS,false);
                statusListChain.execute();

                List<FacilioStatus> statues = (List<FacilioStatus>)stateContext.get(FacilioConstants.ContextNames.TICKET_STATUS_LIST);

                if(CollectionUtils.isNotEmpty(statues)) {

                    for (FacilioStatus status : statues) {
                        String key = StateFlowTranslationImpl.getTranslationKey(StateFlowTranslationImpl.STATE,String.valueOf(status.getId()));
                        jsonArray.add(TranslationsUtil.constructJSON(status.getDisplayName(),StateFlowTranslationImpl.STATE,TranslationConstants.DISPLAY_NAME,String.valueOf(status.getId()),key,properties));
                    }

                }

                FacilioChain chain = ReadOnlyChainFactory.getStateFlowList();
                FacilioContext stateFlowContext = chain.getContext();
                stateFlowContext.put(FacilioConstants.ContextNames.MODULE_NAME,module.getName());
                chain.execute();

                List<WorkflowRuleContext> stateFlowList = (List<WorkflowRuleContext>)stateFlowContext.get(FacilioConstants.ContextNames.STATE_FLOW_LIST);

                if(CollectionUtils.isNotEmpty(stateFlowList)) {

                    for (WorkflowRuleContext ruleContext : stateFlowList) {

                        long stateFlowId = ruleContext.getId();

                        FacilioChain stateTransitionChain = ReadOnlyChainFactory.getStateTransitionList();
                        FacilioContext stateTransitionChainContext = stateTransitionChain.getContext();
                        stateTransitionChainContext.put(FacilioConstants.ContextNames.STATE_FLOW_ID,stateFlowId);
                        stateTransitionChain.execute();

                        List<StateflowTransitionContext> stateTransitions = (List<StateflowTransitionContext>)stateFlowContext.get(FacilioConstants.ContextNames.STATE_TRANSITION_LIST);

                        if(CollectionUtils.isNotEmpty(stateTransitions)){

                            for (StateflowTransitionContext stateTransition : stateTransitions){
                                String id = String.valueOf(stateTransition.getId());
                                String stateTransitionKey = StateFlowTranslationImpl.getTranslationKey(StateFlowTranslationImpl.STATE_TRANSITION,id);
                                jsonArray.add(TranslationsUtil.constructJSON(stateTransition.getName(),StateFlowTranslationImpl.STATE_TRANSITION,TranslationConstants.DISPLAY_NAME,id,stateTransitionKey,properties));
                            }
                        }
                    }
                }
            }
        }

        JSONObject fieldObject = new JSONObject();
        fieldObject.put("fields",jsonArray);
        fieldObject.put("label","");

        JSONArray sectionArray = new JSONArray();
        sectionArray.add(fieldObject);

        return sectionArray;
    }
}
