package com.facilio.bmsconsole.localization.fetchtranslationfields;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.localization.translationImpl.ViewColumnTranslationImpl;
import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.bmsconsole.localization.util.TranslationsUtil;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.chain.FacilioChain;
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
    public JSONArray constructTranslationObject ( @NonNull WebTabContext context,String queryString,Properties properties ) throws Exception {
        FacilioUtil.throwIllegalArgumentException(!WebTabContext.Type.MODULE.equals(WebTabContext.Type.valueOf(context.getType())),"Invalid webTab Type for fetch Module Fields");
        JSONArray viewColumnsArray = new JSONArray();
        ModuleBean moduleBean = (ModuleBean)BeanFactory.lookup("ModuleBean");

        List<Long> moduleIds = context.getModuleIds();

        if(CollectionUtils.isNotEmpty(moduleIds)){
            for (long moduleId : moduleIds){
                FacilioModule module = moduleBean.getModule(moduleId);
                FacilioChain chain = FacilioChainFactory.getAllFieldsChain();
                chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
                chain.getContext().put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID,-1L);
                chain.getContext().put(FacilioConstants.ContextNames.IS_FILTER, true);
                chain.execute();
                JSONObject meta = (JSONObject)chain.getContext().get(FacilioConstants.ContextNames.META);
                List<FacilioField> fields = (List<FacilioField>)meta.get("fields");
                if(CollectionUtils.isNotEmpty(fields)) {
                    fields.forEach(field -> {
                        String key = ViewColumnTranslationImpl.getTranslationKey(String.valueOf(field.getId()));
                        viewColumnsArray.add(TranslationsUtil.constructJSON(field.getDisplayName(),ViewColumnTranslationImpl.VIEWS_COLUMNS,TranslationConstants.DISPLAY_NAME,field.getName(),key,properties));
                    });
                }
            }
        }

        return viewColumnsArray;
    }
}
