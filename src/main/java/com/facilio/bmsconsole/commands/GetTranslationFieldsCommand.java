package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.localization.fetchtranslationfields.TranslationTypeEnum;
import com.facilio.bmsconsole.localization.translationbean.TranslationBean;
import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.command.FacilioCommand;
import com.facilio.fw.TransactionBeanFactory;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Objects;
import java.util.Properties;

public class GetTranslationFieldsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand ( Context context ) throws Exception {

        long tabId = (long)context.get(TranslationConstants.TAB_ID);
        String langCode = (String)context.get(TranslationConstants.LANG_CODE);
        String translationType = (String)context.get(TranslationConstants.TRANSLATION_TYPE);

        TranslationBean bean = (TranslationBean)TransactionBeanFactory.lookup("TranslationBean");
        Properties properties = bean.getTranslationFile(langCode);
        FacilioUtil.throwIllegalArgumentException(properties == null,"Translation file is an empty");

        WebTabContext webTab = ApplicationApi.getWebTab(tabId);
        List<Long> moduleIds = ApplicationApi.getModuleIdsForTab(tabId);
        webTab.setModuleIds(moduleIds);

        TranslationTypeEnum type = TranslationTypeEnum.getTranslationTypeModule(translationType);
        Objects.requireNonNull(type,"Invalid module type for Translation");
        context.put(TranslationConstants.TRANSLATION_FIELDS,type.getHandler().constructTranslationObject(webTab,properties));

        return false;
    }
}
