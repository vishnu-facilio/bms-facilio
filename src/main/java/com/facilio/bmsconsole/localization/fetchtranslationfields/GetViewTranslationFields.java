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
import org.json.simple.JSONObject;

public class GetViewTranslationFields implements TranslationTypeInterface {

    @Override
    public JSONObject constructTranslationObject (@NonNull WebTabContext context) throws Exception {
        FacilioUtil.throwIllegalArgumentException(!WebTabContext.Type.MODULE.equals(WebTabContext.Type.valueOf(context.getType())),"Invalid webTab Type for fetch Module Fields");
        ModuleBean moduleBean = (ModuleBean)BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(context.getModuleIds().get(0));
        FacilioContext viewContext = new FacilioContext();
        JSONObject viewObject = new JSONObject();
        viewContext.put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
        viewContext.put(FacilioConstants.ContextNames.GROUP_STATUS, true);
        FacilioChain getViewListsChain = FacilioChainFactory.getViewListChain();
        getViewListsChain.execute(viewContext);
        viewObject.put("viewFields",viewContext.get(FacilioConstants.ContextNames.GROUP_VIEWS));
        return viewObject;
    }
}
