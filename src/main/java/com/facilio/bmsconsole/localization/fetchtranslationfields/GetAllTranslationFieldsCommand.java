package com.facilio.bmsconsole.localization.fetchtranslationfields;

import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.localization.translationbean.TranslationBean;
import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.command.FacilioCommand;
import com.facilio.fw.TransactionBeanFactory;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Properties;

public class GetAllTranslationFieldsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand ( Context context ) throws Exception {

        String langCode = (String)context.get(TranslationConstants.LANG_CODE);
        String linkName = (String)context.get("linkName");

        TranslationBean bean = (TranslationBean)TransactionBeanFactory.lookup("TranslationBean");
        Properties properties = bean.getTranslationFile(langCode);
        FacilioUtil.throwIllegalArgumentException(properties == null,"Translation file is an empty");

        long appId = ApplicationApi.getApplicationIdForLinkName(linkName);
        FacilioUtil.throwIllegalArgumentException(appId < 0,"Application doesn't exists");

        List<WebTabContext> webTabs = ApplicationApi.getWebTabsForApplication(appId);
        FacilioUtil.throwIllegalArgumentException(CollectionUtils.isEmpty(webTabs),"WebTabs doesn't exists");

        JSONArray jsonArray = new JSONArray();
        JSONObject webTabObject = new JSONObject();

        for (WebTabContext webTab : webTabs) {

            List<Long> moduleIds = ApplicationApi.getModuleIdsForTab(webTab.getId());
            webTab.setModuleIds(moduleIds);

            TranslationTypeEnum viewType = TranslationTypeEnum.getTranslationTypeModule(TranslationTypeEnum.VIEWS.name());
            jsonArray.add(viewType.getHandler().constructTranslationObject(webTab,linkName,properties));

            TranslationTypeEnum viewColumnType = TranslationTypeEnum.getTranslationTypeModule(TranslationTypeEnum.VIEW_COLUMNS.name());
            jsonArray.add(viewColumnType.getHandler().constructTranslationObject(webTab,linkName,properties));

            TranslationTypeEnum formType = TranslationTypeEnum.getTranslationTypeModule(TranslationTypeEnum.FORMS.name());
            jsonArray.add(formType.getHandler().constructTranslationObject(webTab,linkName,properties));

            TranslationTypeEnum fieldType = TranslationTypeEnum.getTranslationTypeModule(TranslationTypeEnum.FIELDS.name());
            jsonArray.add(fieldType.getHandler().constructTranslationObject(webTab,linkName,properties));

            TranslationTypeEnum formFieldType = TranslationTypeEnum.getTranslationTypeModule(TranslationTypeEnum.FORM_FIELDS.name());
            jsonArray.add(formFieldType.getHandler().constructTranslationObject(webTab,linkName,properties));

            TranslationTypeEnum buttonType = TranslationTypeEnum.getTranslationTypeModule(TranslationTypeEnum.BUTTONS.name());
            jsonArray.add(buttonType.getHandler().constructTranslationObject(webTab,linkName,properties));

            TranslationTypeEnum webTabType = TranslationTypeEnum.getTranslationTypeModule(TranslationTypeEnum.WEB_TAB.name());
            jsonArray.add(webTabType.getHandler().constructTranslationObject(webTab,linkName,properties));

            TranslationTypeEnum stateFlowType = TranslationTypeEnum.getTranslationTypeModule(TranslationTypeEnum.STATE_FLOWS.name());
            jsonArray.add(stateFlowType.getHandler().constructTranslationObject(webTab,linkName,properties));

        }

        context.put(TranslationConstants.TRANSLATION_FIELDS,jsonArray);

        return false;
    }
}
