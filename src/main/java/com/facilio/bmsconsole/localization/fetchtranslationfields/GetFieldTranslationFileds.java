package com.facilio.bmsconsole.localization.fetchtranslationfields;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.localization.translation.ModuleTranslationUtils;
import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.bmsconsole.localization.util.TranslationsUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.EnumField;
import com.facilio.modules.fields.EnumFieldValue;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public class GetFieldTranslationFileds implements TranslationTypeInterface {
    @Override
    public JSONArray constructTranslationObject ( @NonNull WebTabContext webTabContext,Map<String,String> filters,Properties properties ) throws Exception {

        FacilioUtil.throwIllegalArgumentException(!WebTabContext.Type.MODULE.equals(WebTabContext.Type.valueOf(webTabContext.getType())),"Invalid webTab Type for fetch Module Fields");

        JSONArray jsonArray = new JSONArray();
        ModuleBean moduleBean = (ModuleBean)BeanFactory.lookup("ModuleBean");
        List<Long> moduleIds = webTabContext.getModuleIds();

        if(CollectionUtils.isNotEmpty(moduleIds)) {
            for (long moduleId : moduleIds) {
                FacilioModule module = moduleBean.getModule(moduleId);
                FacilioChain chain = FacilioChainFactory.getAllFieldsChain();
                chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME,module.getName());
                chain.getContext().put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID,-1L);
                chain.getContext().put(FacilioConstants.ContextNames.IS_FILTER,true);
                chain.execute();
                JSONObject meta = (JSONObject)chain.getContext().get(FacilioConstants.ContextNames.META);
                List<FacilioField> fields = (List<FacilioField>)meta.get("fields");
                if(CollectionUtils.isNotEmpty(fields)) {
                    for (FacilioField field : fields) {

                        String key = ModuleTranslationUtils.getFieldTranslationKey(field.getName());
                        JSONObject fieldJson = TranslationsUtil.constructJSON(field.getDisplayName(),ModuleTranslationUtils.PREFIX_FIELD,TranslationConstants.DISPLAY_NAME,field.getName(),key,properties);
                        if(field instanceof EnumField) {
                            List<EnumFieldValue<Integer>> enumFieldValues = ((EnumField)field).getValues();
                            JSONArray fieldOptions = new JSONArray();
                            for (EnumFieldValue enumFieldValue : enumFieldValues) {
                                String id = String.valueOf(enumFieldValue.getId());
                                String optionKey = ModuleTranslationUtils.getFieldOptionsTranslationKey(id);
                                fieldOptions.add(TranslationsUtil.constructJSON(enumFieldValue.getValue(),"fieldOption",TranslationConstants.DISPLAY_NAME,id,optionKey,properties));
                            }
                            fieldJson.put("fields",fieldOptions);
                        }
                        jsonArray.add(fieldJson);
                    }
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
