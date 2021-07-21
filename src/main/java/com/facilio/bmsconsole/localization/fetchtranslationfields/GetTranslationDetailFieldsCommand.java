package com.facilio.bmsconsole.localization.fetchtranslationfields;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.context.WebTabGroupContext;
import com.facilio.bmsconsole.localization.translationbean.TranslationBean;
import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.bmsconsole.localization.util.TranslationsUtil;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.TransactionBeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.util.FacilioUtil;
import lombok.NonNull;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONArray;

import java.util.List;
import java.util.Objects;
import java.util.Properties;

public class GetTranslationDetailFieldsCommand extends FacilioCommand implements TranslationTypeInterface {

    private static final String WEB_TAB_GROUP = "webTabGroup";
    private static final String WEB_TAB = "webTab";
    private static final String MODULE = "module";

    @Override
    public boolean executeCommand ( Context context ) throws Exception {

        long webTabGroupId = (long)context.get(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID);
        String langCode = (String)context.get(TranslationConstants.LANG_CODE);
        String translationType = (String)context.get(TranslationConstants.TRANSLATION_TYPE);

        TranslationBean bean = (TranslationBean)TransactionBeanFactory.lookup("TranslationBean");
        Properties properties = bean.getTranslationFile(langCode);
        FacilioUtil.throwIllegalArgumentException(properties == null,"Translation file is an empty");

        WebTabGroupContext webTabGroup = ApplicationApi.getWebTabGroup(webTabGroupId);
        String webTabGroupKey = getTranslationKey(WEB_TAB_GROUP,webTabGroup.getRoute());

        List<WebTabContext> webTabs = ApplicationApi.getWebTabsForWebGroup(webTabGroupId);

        TranslationTypeEnum type = TranslationTypeEnum.getTranslationTypeModule(translationType);
        Objects.requireNonNull(type,"Invalid module type for Translation");

        JSONArray result = new JSONArray();

        for (WebTabContext webTab : webTabs) {

            List<Long> moduleIds = ApplicationApi.getModuleIdsForTab(webTab.getId());
            if(CollectionUtils.isEmpty(moduleIds)) {
                continue;
            }
            webTab.setModuleIds(moduleIds);

            JSONArray webTabArray = type.getHandler().constructTranslationObject(webTab,properties);
            for (int i=0;i<webTabArray.size();i++){
                result.add(webTabArray.get(i));
            }
        }

        result.add(TranslationsUtil.constructJSON(webTabGroup.getName(),WEB_TAB_GROUP,TranslationConstants.DISPLAY_NAME,webTabGroup.getRoute(),webTabGroupKey,properties));
        context.put(TranslationConstants.TRANSLATION_FIELDS,result);

        return false;
    }

    @Override
    public JSONArray constructTranslationObject ( @NonNull WebTabContext context,Properties properties ) throws Exception {

        FacilioUtil.throwIllegalArgumentException(!WebTabContext.Type.MODULE.equals(WebTabContext.Type.valueOf(context.getType())),"Invalid webTab Type for fetch Module Fields");

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

        return jsonArray;
    }

    private String getTranslationKey ( String prefix,String key ) {
        return prefix + "." + key + "." + TranslationConstants.DISPLAY_NAME;
    }
}
