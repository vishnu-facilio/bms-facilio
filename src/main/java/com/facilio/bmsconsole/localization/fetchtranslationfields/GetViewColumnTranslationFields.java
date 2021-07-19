package com.facilio.bmsconsole.localization.fetchtranslationfields;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import lombok.NonNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;

public class GetViewColumnTranslationFields implements TranslationTypeInterface {

    @Override
    public JSONObject constructTranslationObject (@NonNull WebTabContext context ) throws Exception {
        FacilioUtil.throwIllegalArgumentException(!WebTabContext.Type.MODULE.equals(WebTabContext.Type.valueOf(context.getType())),"Invalid webTab Type for fetch Module Fields");
        JSONObject viewFieldObject = new JSONObject();
        ModuleBean moduleBean = (ModuleBean)BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(context.getModuleIds().get(0));
        List<FacilioField> fields = moduleBean.getAllFields(module.getName());
        fields.add(FieldFactory.getSiteField(module));
        viewFieldObject.put("fields",fields);
        return viewFieldObject;
    }
}
