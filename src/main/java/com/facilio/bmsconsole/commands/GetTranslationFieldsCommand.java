package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.localization.fetchtranslationfields.TranslationTypeEnum;
import com.facilio.bmsconsole.localization.translationbean.TranslationBean;
import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.TransactionBeanFactory;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class GetTranslationFieldsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand ( Context context ) throws Exception {

        long tabId = (long)context.get(TranslationConstants.TAB_ID);
        String langCode = (String)context.get(TranslationConstants.LANG_CODE);
        String translationType = (String)context.get(TranslationConstants.TRANSLATION_TYPE);
        Map<String,String> filters = (Map<String, String>)context.getOrDefault(TranslationConstants.FILTERS,null);

        TranslationBean bean = (TranslationBean)TransactionBeanFactory.lookup("TranslationBean");
        Properties properties = bean.getTranslationFile(langCode);
        FacilioUtil.throwIllegalArgumentException(properties == null,"Translation file is an empty");

        WebTabContext webTab = ApplicationApi.getWebTab(tabId);

        FacilioUtil.throwIllegalArgumentException(webTab == null, "Invalid WebTab Id");

        switch (webTab.getTypeEnum()) {
            case MODULE:
                webTab.setModuleIds(ApplicationApi.getModuleIdsForTab(tabId));
                break;
            case CUSTOM:
                if (MapUtils.isNotEmpty(filters) && webTab.getRoute().equals("portfolio")) {
                    String moduleName = filters.get("moduleName");
                    FacilioUtil.throwIllegalArgumentException(StringUtils.isEmpty(moduleName), "ModuleName should not be null while fetching Custom type");
                    long moduleId = Constants.getModBean().getModule(moduleName).getModuleId();
                    webTab.setModuleIds(Collections.singletonList(moduleId));
                    webTab.setType(WebTabContext.Type.MODULE);
                }
                break;
        }

        TranslationTypeEnum type = TranslationTypeEnum.getTranslationTypeModule(translationType);
        Objects.requireNonNull(type,"Invalid enum type for Translation");
        context.put(TranslationConstants.TRANSLATION_FIELDS,type.getHandler().constructTranslationObject(webTab,filters,properties));

        return false;
    }
}
