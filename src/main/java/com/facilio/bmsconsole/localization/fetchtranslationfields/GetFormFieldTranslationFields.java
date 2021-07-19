package com.facilio.bmsconsole.localization.fetchtranslationfields;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.util.FacilioUtil;
import lombok.NonNull;
import org.json.simple.JSONObject;

import java.util.List;

public class GetFormFieldTranslationFields implements TranslationTypeInterface{
    @Override
    public JSONObject constructTranslationObject ( @NonNull WebTabContext context ) throws Exception {

        FacilioUtil.throwIllegalArgumentException(!WebTabContext.Type.MODULE.equals(context.getTypeEnum()),"Invalid webTab Type for fetch Module Fields");
        JSONObject formFieldObject = new JSONObject();
        ModuleBean moduleBean = (ModuleBean)BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(context.getModuleIds().get(0));
        List<FormField> fields = FormsAPI.getAllFormFields(module.getName(), null);
        formFieldObject.put("fields",fields);
        return formFieldObject;
    }
}
