package com.facilio.bmsconsole.localization.fetchtranslationfields;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.util.FacilioUtil;
import lombok.NonNull;
import org.json.simple.JSONArray;

import java.util.Properties;

public class GetStateFlowTranslationFields implements TranslationTypeInterface{
    @Override
    public JSONArray constructTranslationObject ( @NonNull WebTabContext context,Properties properties ) throws Exception {
        FacilioUtil.throwIllegalArgumentException(!WebTabContext.Type.MODULE.equals(WebTabContext.Type.valueOf(context.getType())),"Invalid webTab Type for fetch Module Fields");
        ModuleBean moduleBean = (ModuleBean)BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(context.getModuleIds().get(0));
        FacilioContext stateFlowContext = new FacilioContext();
        JSONArray jsonArray = new JSONArray();
        stateFlowContext.put(FacilioConstants.ContextNames.PARENT_MODULE, module.getName());
        stateFlowContext.put(FacilioConstants.ContextNames.APPROVAL_STATUS, false);
        FacilioChain statusListChain = FacilioChainFactory.getTicketStatusListChain();
        statusListChain.execute(stateFlowContext);
        return jsonArray;
    }
}
