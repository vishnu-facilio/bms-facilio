package com.facilio.bmsconsole.localization.fetchtranslationfields;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.localization.translationImpl.FormFieldTranslationImpl;
import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.bmsconsole.localization.util.TranslationsUtil;
import com.facilio.bmsconsole.util.FormsAPI;
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

public class GetFormFieldTranslationFields implements TranslationTypeInterface {
    @Override
    public JSONArray constructTranslationObject ( @NonNull WebTabContext context,Map<String,String> filters,Properties properties ) throws Exception {

        FacilioUtil.throwIllegalArgumentException(!WebTabContext.Type.MODULE.equals(WebTabContext.Type.valueOf(context.getType())),"Invalid webTab Type for fetch Module Fields");

        JSONArray jsonArray = new JSONArray();
        ModuleBean moduleBean = (ModuleBean)BeanFactory.lookup("ModuleBean");
        List<Long> moduleIds = context.getModuleIds();

        if(CollectionUtils.isNotEmpty(moduleIds)){
            for (long moduleId : moduleIds){
                FacilioModule module = moduleBean.getModule(moduleId);
                List<FormField> fields = FormsAPI.getAllFormFields(module.getName(),null);
                if(CollectionUtils.isNotEmpty(fields)) {
                    fields.forEach(field -> {
                        String key = FormFieldTranslationImpl.getTranslationKey(field.getName());
                        jsonArray.add(TranslationsUtil.constructJSON(field.getDisplayName(),FormFieldTranslationImpl.FORM_FIELD,TranslationConstants.DISPLAY_NAME,field.getName(),key,properties));
                    });
                }
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
