package com.facilio.bmsconsole.localization;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.ApplicationLayoutContext;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.context.WebTabGroupContext;
import com.facilio.bmsconsole.localization.fetchtranslationfields.TranslationTypeEnum;
import com.facilio.bmsconsole.localization.translationbean.TranslationBean;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.TransactionBeanFactory;
import com.facilio.util.FacilioUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

@Log4j
@Getter
@Setter
public class TranslationAction extends FacilioAction {

    private String langCode;
    private List<Map<String, Object>> translations;
    private Long tabId;
    private long applicationId = -1L;
    private String translationType;

    public String addLanguage () throws Exception {
        addNewLanguage();
        return SUCCESS;
    }

    public String getTranslationFields () throws Exception {
        setResult("fieldList",getTranslationList());
        return SUCCESS;
    }

    private JSONArray getTranslationList () throws Exception {
        TranslationBean bean = (TranslationBean)TransactionBeanFactory.lookup("TranslationBean");
        Properties properties= bean.getTranslationFile(getLangCode());
        FacilioUtil.throwIllegalArgumentException(properties ==null,"Translation file is an empty");
        WebTabContext webTab= ApplicationApi.getWebTab(getTabId());
        List<Long> moduleIds = ApplicationApi.getModuleIdsForTab(getTabId());
        webTab.setModuleIds(moduleIds);
        TranslationTypeEnum type = TranslationTypeEnum.getTranslationTypeModule(getTranslationType());
        Objects.requireNonNull(type,"Invalid module type for Translation");
        return type.getHandler().constructTranslationObject(webTab,properties);
    }

    public String save () throws Exception {
        addOrUpdateTranslation();
        return SUCCESS;
    }

    public String fetchDetail () throws Exception {
        setResult("result",fetchTranslationDetails());
        return SUCCESS;
    }

    private JSONObject fetchTranslationDetails () throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getApplicationDetails();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.APPLICATION_ID,applicationId);
        context.put(FacilioConstants.ContextNames.FETCH_ALL_LAYOUTS,true);
        chain.execute();
        ApplicationContext props = (ApplicationContext)context.get(FacilioConstants.ContextNames.APPLICATION);
        JSONObject jsonObject = new JSONObject();
        if(props != null) {
            List<ApplicationLayoutContext> layout = props.getLayouts();
            if(CollectionUtils.isNotEmpty(layout)){
                for (ApplicationLayoutContext prop : layout) {
                    for (WebTabGroupContext webtabGroup : prop.getWebTabGroupList()) {
                        for (WebTabContext webTab : webtabGroup.getWebTabs()) {
                            List<String> columns = new ArrayList<>();
                            switch (webTab.getTypeEnum()) {
                                case MODULE:
                                    columns.add("VIEWS");
                                    columns.add("VIEW COLUMNS");
                                    columns.add("FORMS");
                                    columns.add("FIELDS");
                                    columns.add("FORM FIELDS");
                                    columns.add("DETAILS");
                                    columns.add("BUTTONS");
                                    columns.add("STATE FLOWS");
                                    webTab.setTranslationColumns(columns);
                                    break;
                                case DASHBOARD:
                                    columns.add("FOLDER&DASHBOARDS");
                                    webTab.setTranslationColumns(columns);
                                    break;
                            }

                        }
                    }
                }
            }
            jsonObject.put("appCategory",props.getAppCategory());
            jsonObject.put("appCategoryEnum",props.getAppCategoryEnum());
            jsonObject.put("id",props.getId());
            jsonObject.put("layouts",props.getLayouts());
            jsonObject.put("linkName",props.getLinkName());
        }
        return jsonObject;
    }

    private void addNewLanguage () throws Exception {
        TranslationBean bean = (TranslationBean)TransactionBeanFactory.lookup("TranslationBean");
        bean.add(getLangCode());
    }

    private JSONArray getTranslationsMeta () throws Exception {
        TranslationBean bean = (TranslationBean)BeanFactory.lookup("TranslationBean");
        return bean.getTranslationList(getLangCode());
    }

    private void addOrUpdateTranslation () throws Exception {
        TranslationBean bean = (TranslationBean)TransactionBeanFactory.lookup("TranslationBean");
        bean.save(getLangCode(),getTranslations());
    }
}