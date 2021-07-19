package com.facilio.bmsconsole.localization;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.ApplicationContext;
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
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    private JSONObject getTranslationList () throws Exception {
        WebTabContext webTab= ApplicationApi.getWebTab(getTabId());
        List<Long> moduleIds = ApplicationApi.getModuleIdsForTab(getTabId());
        webTab.setModuleIds(moduleIds);
        TranslationTypeEnum type = TranslationTypeEnum.getTranslationTypeModule(getTranslationType());
        Objects.requireNonNull(type,"Invalid module type for Translation");
        return type.getHandler().constructTranslationObject(webTab);
    }

    public String save () throws Exception {
        addOrUpdateTranslation();
        return SUCCESS;
    }

    public String fetchDetail () throws Exception {
        setResult("result",fetchTranslationDetails());
        return SUCCESS;
    }

    private ApplicationContext fetchTranslationDetails () throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getApplicationDetails();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.APPLICATION_ID,applicationId);
        context.put(FacilioConstants.ContextNames.FETCH_ALL_LAYOUTS,true);
        chain.execute();
        ApplicationContext props = (ApplicationContext)context.get(FacilioConstants.ContextNames.APPLICATION);
        List<WebTabGroupContext> webTabGroups = props.getWebTabGroups();
        for (WebTabGroupContext prop : webTabGroups) {
            for (WebTabContext webtab : prop.getWebTabs()) {
                List<String> columns = new ArrayList<>();
                switch (webtab.getTypeEnum()) {
                    case MODULE:
                        columns.add("VIEWS");
                        columns.add("VIEW COLUMNS");
                        columns.add("FORMS");
                        columns.add("FIELDS");
                        columns.add("FORM FIELDS");
                        columns.add("DETAILS");
                        columns.add("BUTTONS");
                        columns.add("STATE FLOWS");
                        webtab.setTranslationColumns(columns);
                        break;
                    case DASHBOARD:
                        columns.add("FOLDER&DASHBOARDS");
                }
            }
        }
        return props;
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
