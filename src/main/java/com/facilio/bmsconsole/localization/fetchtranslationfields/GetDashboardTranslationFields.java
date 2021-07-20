package com.facilio.bmsconsole.localization.fetchtranslationfields;

import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.util.FacilioUtil;
import lombok.NonNull;
import org.json.simple.JSONObject;

import java.util.Properties;

public class GetDashboardTranslationFields implements TranslationTypeInterface{
    @Override
    public JSONObject constructTranslationObject ( @NonNull WebTabContext context,Properties properties ) throws Exception {
        FacilioUtil.throwIllegalArgumentException(!WebTabContext.Type.DASHBOARD.equals(context.getType()),"Invalid webTab Type for fetch Dashboard Fields");
        context.getModules();
        return null;
    }
}
