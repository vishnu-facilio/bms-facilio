package com.facilio.bmsconsole.localization.fetchtranslationfields;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.ViewGroups;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.localization.translationImpl.ViewColumnTranslationImpl;
import com.facilio.bmsconsole.localization.translationImpl.ViewTranslationImpl;
import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.bmsconsole.localization.util.TranslationsUtil;
import com.facilio.bmsconsole.util.ApplicationApi;
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

public class GetViewFolderTranslationFields implements TranslationTypeInterface {

    @Override
    public JSONObject constructTranslationObject ( @NonNull WebTabContext webTabContext,String queryString,Properties properties ) throws Exception {
        FacilioUtil.throwIllegalArgumentException(!WebTabContext.Type.MODULE.equals(WebTabContext.Type.valueOf(webTabContext.getType())),"Invalid webTab Type for fetch Module Fields");
        JSONArray viewFolderArray = new JSONArray();
        ModuleBean moduleBean = (ModuleBean)BeanFactory.lookup("ModuleBean");

        List<Long> moduleIds = webTabContext.getModuleIds();

        if(CollectionUtils.isNotEmpty(moduleIds)){
            for (long moduleId : moduleIds){
                FacilioModule module = moduleBean.getModule(moduleId);
                if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_ALARMS) && module.getName().equals("alarm")) {
                    module.setName(FacilioConstants.ContextNames.ALARM_OCCURRENCE);
                }

                FacilioChain chain = FacilioChainFactory.getViewListChain();
                FacilioContext context = chain.getContext();
                context.put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
                context.put(FacilioConstants.ContextNames.GROUP_STATUS, true);
                context.put(FacilioConstants.ContextNames.APP_ID,webTabContext.getApplicationId());
                context.put(FacilioConstants.ContextNames.RESTRICT_PERMISSIONS, false);
                chain.execute();

                List<ViewGroups> viewGroups = (List<ViewGroups>)context.get(FacilioConstants.ContextNames.GROUP_VIEWS);
                if(CollectionUtils.isNotEmpty(viewGroups)) {
                    viewGroups.forEach(field -> {
                        String key = ViewColumnTranslationImpl.getTranslationKey(field.getName());
                        viewFolderArray.add(TranslationsUtil.constructJSON(field.getDisplayName(),ViewTranslationImpl.VIEWS,TranslationConstants.DISPLAY_NAME,field.getName(),key,properties));
                    });
                }
            }
        }

        JSONObject fieldObject = new JSONObject();
        fieldObject.put("fields",viewFolderArray);
        fieldObject.put("label","");

        return fieldObject;
    }


}
