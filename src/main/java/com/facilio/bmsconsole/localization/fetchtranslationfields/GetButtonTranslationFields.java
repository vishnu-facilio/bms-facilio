package com.facilio.bmsconsole.localization.fetchtranslationfields;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.localization.translationImpl.ButtonTranslationImpl;
import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.bmsconsole.localization.util.TranslationsUtil;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.util.FacilioUtil;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONArray;

import java.util.List;
import java.util.Properties;

public class GetButtonTranslationFields implements TranslationTypeInterface {
    @Override
    public JSONArray constructTranslationObject ( @NonNull WebTabContext context,String queryString,Properties properties ) throws Exception {

        FacilioUtil.throwIllegalArgumentException(!WebTabContext.Type.MODULE.equals(WebTabContext.Type.valueOf(context.getType())),"Invalid webTab Type for fetch Module Fields");

        JSONArray buttonArray = new JSONArray();
        ModuleBean moduleBean = (ModuleBean)BeanFactory.lookup("ModuleBean");
        List<Long> moduleIds = context.getModuleIds();

        if(CollectionUtils.isNotEmpty(moduleIds)){
            for (long moduleId : moduleIds){
                FacilioModule module = moduleBean.getModule(moduleId);
                FacilioChain chain = ReadOnlyChainFactory.getAllCustomButtonChain();
                chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
                chain.execute();
                List<WorkflowRuleContext> customButtons = (List<WorkflowRuleContext>)chain.getContext().get(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST);
                if(CollectionUtils.isNotEmpty(customButtons)) {
                    customButtons.forEach(button -> {
                        String key = ButtonTranslationImpl.getTranslationKey(button.getId());
                        buttonArray.add(TranslationsUtil.constructJSON(button.getName(),ButtonTranslationImpl.BUTTON,TranslationConstants.DISPLAY_NAME,String.valueOf(button.getId()),key,properties));
                    });
                }
            }
        }
        return buttonArray;
    }
}
