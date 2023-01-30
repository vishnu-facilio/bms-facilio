package com.facilio.bmsconsole.localization.fetchtranslationfields;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WebTabContext;
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

public class GetTranslationDetailFields implements TranslationTypeInterface {

    private static final String WEB_TAB = "webTab";
    private static final String MODULE = "module";

    @Override
    public JSONArray constructTranslationObject ( @NonNull WebTabContext context,Map<String,String> filters,Properties properties ) throws Exception {

//        FacilioUtil.throwIllegalArgumentException(!WebTabContext.Type.MODULE.equals(WebTabContext.Type.valueOf(context.getType())),"Invalid webTab Type for fetch Module Fields");

        List<Long> moduleIds = context.getModuleIds();
        JSONArray jsonArray = new JSONArray();

        if(CollectionUtils.isNotEmpty(moduleIds)) {
            ModuleBean moduleBean = (ModuleBean)BeanFactory.lookup("ModuleBean");
            for (long moduleId : moduleIds) {
                FacilioModule module = moduleBean.getModule(moduleId);
                String moduleKey = getTranslationKey(MODULE,module.getName());
                jsonArray.add(TranslationsUtil.constructJSON(module.getDisplayName(),MODULE,TranslationConstants.DISPLAY_NAME,module.getName(),moduleKey,properties));
            }
        }
        String webTabKey = getTranslationKey(WEB_TAB,context.getRoute());
        jsonArray.add(TranslationsUtil.constructJSON(context.getName(),WEB_TAB,TranslationConstants.DISPLAY_NAME,context.getRoute(),webTabKey,properties));

        JSONObject fieldObject = new JSONObject();
        fieldObject.put("fields",jsonArray);
        fieldObject.put("label","");

        JSONArray sectionArray = new JSONArray();
        sectionArray.add(fieldObject);

        return sectionArray;
    }

    private String getTranslationKey ( String prefix,String key ) {
        return prefix + "." + key + "." + TranslationConstants.DISPLAY_NAME;
    }
}
