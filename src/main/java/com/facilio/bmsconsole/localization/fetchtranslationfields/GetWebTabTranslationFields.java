package com.facilio.bmsconsole.localization.fetchtranslationfields;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.localization.translation.ModuleTranslationUtils;
import com.facilio.bmsconsole.localization.translationImpl.WebTabTranslationImpl;
import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.bmsconsole.localization.util.TranslationsUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.util.FacilioUtil;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public class GetWebTabTranslationFields implements TranslationTypeInterface{
    @Override
    public JSONArray  constructTranslationObject ( @NonNull WebTabContext context,Map<String,String> filters,Properties properties ) throws Exception {

        FacilioUtil.throwIllegalArgumentException(!WebTabContext.Type.MODULE.equals(WebTabContext.Type.valueOf(context.getType())),"Invalid webTab Type for fetch Module Fields");

        JSONArray jsonArray = new JSONArray();

        List<Long> moduleIds = context.getModuleIds();
        String webTabKey = WebTabTranslationImpl.getTranslationKey(WebTabTranslationImpl.WEB_TAB,context.getRoute());
        jsonArray.add(TranslationsUtil.constructJSON(context.getName(),WebTabTranslationImpl.WEB_TAB,TranslationConstants.DISPLAY_NAME,context.getRoute(),webTabKey,properties));

        if(CollectionUtils.isNotEmpty(moduleIds)){
            ModuleBean moduleBean = (ModuleBean)BeanFactory.lookup("ModuleBean");
            for (long moduleId : moduleIds){
                FacilioModule module = moduleBean.getModule(moduleId);
                String key = ModuleTranslationUtils.getTranslationKey(context.getRoute());
                jsonArray.add(TranslationsUtil.constructJSON(module.getDisplayName(),ModuleTranslationUtils.PREFIX_MODULE,TranslationConstants.DISPLAY_NAME,module.getName(),key,properties));
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
