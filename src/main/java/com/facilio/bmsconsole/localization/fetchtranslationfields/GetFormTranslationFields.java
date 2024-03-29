package com.facilio.bmsconsole.localization.fetchtranslationfields;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.localization.translation.ModuleTranslationUtils;
import com.facilio.bmsconsole.localization.translationImpl.FormFieldTranslationImpl;
import com.facilio.bmsconsole.localization.translationImpl.FormTranslationImpl;
import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.bmsconsole.localization.util.TranslationsUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.EnumField;
import com.facilio.modules.fields.EnumFieldValue;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public class GetFormTranslationFields implements TranslationTypeInterface {
    @Override
    public JSONArray constructTranslationObject ( @NonNull WebTabContext webTabContext,Map<String,String> filters,Properties properties ) throws Exception {

        FacilioUtil.throwIllegalArgumentException(!WebTabContext.Type.MODULE.equals(WebTabContext.Type.valueOf(webTabContext.getType())),"Invalid webTab Type for fetch Module Fields");
        FacilioUtil.throwIllegalArgumentException(StringUtils.isEmpty(filters.get("formId")),"Form id is mandatory param for fetching form fields");

        JSONArray jsonArray = new JSONArray();
        ModuleBean moduleBean = (ModuleBean)BeanFactory.lookup("ModuleBean");
        List<Long> moduleIds = webTabContext.getModuleIds();
        long formId = Long.parseLong(filters.get("formId"));

        if(CollectionUtils.isNotEmpty(moduleIds)) {
            for (long moduleId : moduleIds) {

                FacilioModule module = moduleBean.getModule(moduleId);

                FacilioChain chain = FacilioChainFactory.getFormMetaChain();
                FacilioContext context = chain.getContext();
                context.put(FacilioConstants.ContextNames.FORM_ID,formId);
                context.put(FacilioConstants.ContextNames.MODULE_NAME,module.getName());
                context.put(FacilioConstants.ContextNames.FETCH_FORM_RULE_FIELDS,true);
                chain.execute();

                FacilioForm form = (FacilioForm)context.get(FacilioConstants.ContextNames.FORM);
                FacilioUtil.throwIllegalArgumentException(form==null,"Form doesn't exist please check.");

                String formKey = FormTranslationImpl.getTranslationKey(form.getName());
                jsonArray.add(TranslationsUtil.constructJSON(form.getDisplayName(),FormTranslationImpl.FORM,TranslationConstants.DISPLAY_NAME,form.getName(),formKey,properties));

                List<FormSection> sections = form.getSections();

                if(CollectionUtils.isNotEmpty(sections)) {
                    for (FormSection section : sections) {
                        List<FormField> formFields = section.getFields();
                        if(CollectionUtils.isNotEmpty(formFields)) {
                            for (FormField formField : formFields) {
                                FacilioField field  = formField.getField();
                                String key = FormFieldTranslationImpl.getTranslationKey(formField.getName());
                                JSONObject fieldJson = TranslationsUtil.constructJSON(formField.getDisplayName(),FormFieldTranslationImpl.FORM_FIELD,TranslationConstants.DISPLAY_NAME,formField.getName(),key,properties);
                                if(field != null && field instanceof EnumField) {
                                    List<EnumFieldValue<Integer>> enumFieldValues = ((EnumField)field).getValues();
                                    JSONArray fieldOptions = new JSONArray();
                                    for (EnumFieldValue enumFieldValue : enumFieldValues) {
                                        String idx = field.getName()+"."+enumFieldValue.getIndex();
                                        String optionKey = ModuleTranslationUtils.getFieldOptionsTranslationKey(idx);
                                        fieldOptions.add(TranslationsUtil.constructJSON(enumFieldValue.getValue(),"fieldOption",TranslationConstants.DISPLAY_NAME,idx,optionKey,properties));
                                    }
                                    fieldJson.put("fields",fieldOptions);
                                }
                                jsonArray.add(fieldJson);
                            }
                        }
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
