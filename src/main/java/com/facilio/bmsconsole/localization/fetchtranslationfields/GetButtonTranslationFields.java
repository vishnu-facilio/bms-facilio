package com.facilio.bmsconsole.localization.fetchtranslationfields;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.util.FacilioUtil;
import lombok.NonNull;
import org.json.simple.JSONObject;

public class GetButtonTranslationFields implements TranslationTypeInterface{
    @Override
    public JSONObject constructTranslationObject ( @NonNull WebTabContext context ) throws Exception {
        FacilioUtil.throwIllegalArgumentException(!WebTabContext.Type.MODULE.equals(context.getTypeEnum()),"Invalid webTab Type for fetch Module Fields");
        ModuleBean moduleBean = (ModuleBean)BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(context.getModuleIds().get(0));
        JSONObject buttonObject = new JSONObject();
        FacilioChain chain = ReadOnlyChainFactory.getAllCustomButtonChain();
        FacilioContext context1 = chain.getContext();
        context1.put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
        chain.execute();
        buttonObject.put("fields", context1.get(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST));
        return buttonObject;
    }
}
