package com.facilio.bmsconsole.localization.fetchtranslationfields;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.localization.translationImpl.FormTranslationImpl;
import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.bmsconsole.localization.util.TranslationsUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.util.FacilioUtil;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONArray;

import java.util.List;
import java.util.Properties;

public class GetFormTranslationFields implements TranslationTypeInterface{
    @Override
    public JSONArray constructTranslationObject ( @NonNull WebTabContext context,String queryString,Properties properties ) throws Exception {
        FacilioUtil.throwIllegalArgumentException(!WebTabContext.Type.MODULE.equals(WebTabContext.Type.valueOf(context.getType())),"Invalid webTab Type for fetch Module Fields");
        JSONArray jsonArray = new JSONArray();
        ModuleBean moduleBean = (ModuleBean)BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(context.getModuleIds().get(0));
        FacilioChain chain = ReadOnlyChainFactory.getFormList();
        chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
        chain.getContext().put(FacilioConstants.ContextNames.FETCH_EXTENDED_MODULE_FORMS, false);
        chain.getContext().put(FacilioConstants.ContextNames.FETCH_DISABLED_FORMS, true);
        chain.getContext().put(FacilioConstants.ContextNames.APP_ID, AccountUtil.getCurrentApp().getId());
        chain.execute();

        List<FacilioForm> formsList = (List<FacilioForm>)chain.getContext().get(FacilioConstants.ContextNames.FORMS);
        if(CollectionUtils.isNotEmpty(formsList)){
            formsList.forEach(form ->{
                String key = FormTranslationImpl.getTranslationKey(form.getName());
                jsonArray.add(TranslationsUtil.constructJSON(form.getDisplayName(),FormTranslationImpl.FORM,TranslationConstants.DISPLAY_NAME,form.getName(),key,properties));
            });
        }
        return jsonArray;
    }
}
