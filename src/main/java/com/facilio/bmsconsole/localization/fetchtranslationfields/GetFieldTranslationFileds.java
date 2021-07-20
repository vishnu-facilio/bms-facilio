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

public class GetFieldTranslationFileds implements TranslationTypeInterface{
    @Override
    public JSONObject constructTranslationObject ( @NonNull WebTabContext webTabContext ) throws Exception {
        FacilioUtil.throwIllegalArgumentException(!WebTabContext.Type.MODULE.equals(WebTabContext.Type.valueOf(webTabContext.getType())),"Invalid webTab Type for fetch Module Fields");
        JSONObject fieldObject = new JSONObject();
        ModuleBean moduleBean = (ModuleBean)BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(webTabContext.getModuleIds().get(0));
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
        context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID,-1L);
        context.put(FacilioConstants.ContextNames.IS_FILTER, true);
        FacilioChain metaField = FacilioChainFactory.getAllFieldsChain();
        metaField.execute(context);
        fieldObject.put("fields",context.get(FacilioConstants.ContextNames.META));
        return fieldObject;
    }
}
