package com.facilio.bmsconsole.localization.fetchtranslationfields;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.localization.translationImpl.ViewColumnTranslationImpl;
import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.bmsconsole.localization.util.TranslationsUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Properties;

public class GetViewColumnTranslationFields implements TranslationTypeInterface {

    @Override
    public JSONArray constructTranslationObject ( @NonNull WebTabContext context,Properties properties ) throws Exception {
        FacilioUtil.throwIllegalArgumentException(!WebTabContext.Type.MODULE.equals(WebTabContext.Type.valueOf(context.getType())),"Invalid webTab Type for fetch Module Fields");
        JSONArray viewColumnsArray = new JSONArray();
        ModuleBean moduleBean = (ModuleBean)BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(context.getModuleIds().get(0));
        FacilioChain chain = FacilioChainFactory.getAllFieldsChain();
        chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
        chain.getContext().put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID,-1L);
        chain.getContext().put(FacilioConstants.ContextNames.IS_FILTER, true);
        chain.execute();
        JSONObject meta = (JSONObject)chain.getContext().get(FacilioConstants.ContextNames.META);
        List<FacilioField> fields = (List<FacilioField>)meta.get("fields");
        if(CollectionUtils.isNotEmpty(fields)) {
            fields.forEach(field -> {
                String key = ViewColumnTranslationImpl.getTranslationKey(field.getName());
                TranslationsUtil.constructJSON(field.getDisplayName(),ViewColumnTranslationImpl.VIEWS_COLUMNS,TranslationConstants.DISPLAY_NAME,key,properties);
            });
        }
        return viewColumnsArray;
    }
}
