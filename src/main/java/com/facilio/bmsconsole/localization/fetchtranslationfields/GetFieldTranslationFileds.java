package com.facilio.bmsconsole.localization.fetchtranslationfields;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.localization.translation.ModuleTranslationUtils;
import com.facilio.bmsconsole.localization.translationImpl.ModuleTranslationImpl;
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

public class GetFieldTranslationFileds implements TranslationTypeInterface{
    @Override
    public JSONArray constructTranslationObject ( @NonNull WebTabContext webTabContext,Properties properties ) throws Exception {
        FacilioUtil.throwIllegalArgumentException(!WebTabContext.Type.MODULE.equals(WebTabContext.Type.valueOf(webTabContext.getType())),"Invalid webTab Type for fetch Module Fields");
        JSONArray jsonArray = new JSONArray();
        ModuleBean moduleBean = (ModuleBean)BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(webTabContext.getModuleIds().get(0));
        FacilioChain chain = FacilioChainFactory.getAllFieldsChain();
        chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
        chain.getContext().put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID,-1L);
        chain.getContext().put(FacilioConstants.ContextNames.IS_FILTER, true);
        chain.execute();
        JSONObject meta = (JSONObject)chain.getContext().get(FacilioConstants.ContextNames.META);
        List<FacilioField> fields = (List<FacilioField>)meta.get("fields");
        if(CollectionUtils.isNotEmpty(fields)) {
            fields.forEach(field -> {
                String key = ModuleTranslationUtils.getFieldTranslationKey(field.getName());
                jsonArray.add(TranslationsUtil.constructJSON(field.getDisplayName(),ModuleTranslationImpl.FIELDS,TranslationConstants.DISPLAY_NAME,key,properties));
            });
        }
        return jsonArray;
    }
}
