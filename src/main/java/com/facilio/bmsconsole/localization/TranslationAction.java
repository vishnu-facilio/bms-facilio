package com.facilio.bmsconsole.localization;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.ApplicationLayoutContext;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.context.WebTabGroupContext;
import com.facilio.bmsconsole.localization.translationbean.TranslationBean;
import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.bmsconsole.localization.util.TranslationsUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.TransactionBeanFactory;
import com.facilio.util.FacilioUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j
@Getter
@Setter
public class TranslationAction extends FacilioAction {

    private String langCode;
    private List<Map<String, Object>> translations;
    private long tabId = -1L;
    private long applicationId = -1L;
    private String translationType;
    private Map<String,String> filter;



    public String addLanguage () throws Exception {
        addNewLanguage();
        return SUCCESS;
    }

    public String getTranslationFields () throws Exception {

        FacilioChain chain = ReadOnlyChainFactory.getTranslationFields();
        FacilioContext context = chain.getContext();

        context.put(TranslationConstants.LANG_CODE,getLangCode());
        context.put(TranslationConstants.TAB_ID,getTabId());
        context.put(TranslationConstants.TRANSLATION_TYPE,getTranslationType());
        context.put(TranslationConstants.FILTERS,getFilter());
        chain.execute();

        setResult("sections",context.get(TranslationConstants.TRANSLATION_FIELDS));

        return SUCCESS;
    }

    public String getAllWebTabFields () throws Exception {

        String linkName = ServletActionContext.getRequest().getParameter("linkName");

        FacilioUtil.throwIllegalArgumentException(StringUtils.isEmpty(linkName),"Application linkName shouldn't be null");

        FacilioChain chain = ReadOnlyChainFactory.getAllWebTabTranlsationFields();
        FacilioContext context = chain.getContext();
        context.put(TranslationConstants.LANG_CODE,getLangCode());
        context.put("linkName",linkName);
        chain.execute();

        setResult("fields",context.get(TranslationConstants.TRANSLATION_FIELDS));
        return SUCCESS;
    }

    public String save () throws Exception {
        addOrUpdateTranslation();
        return SUCCESS;
    }

    public String fetchDetail () throws Exception {
        setResult("webTabDetails",fetchTranslationDetails());
        return SUCCESS;
    }

    public String getTranslatedLang()throws Exception {
        setResult("translatedLang",getLang());
        return SUCCESS;
    }

    private List<String> getLang () throws Exception {
        GenericSelectRecordBuilder select = new GenericSelectRecordBuilder().select(TranslationConstants.getTranslationFields())
                .table(TranslationConstants.getTranslationModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("STATUS","status","1",StringOperators.IS));
        List<Map<String, Object>> props = select.get();
        return props.stream().map(p -> (String)p.get("langCode")).collect(Collectors.toList());
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
                            switch (webTab.getTypeEnum()) {
                                case MODULE:
                                    webTab.setTypeVsColumns(TranslationsUtil.COLUMN_VS_TRANSLATION_TYPE.get("moduleTab"));
                                    break;
                                case DASHBOARD:
                                    webTab.setTypeVsColumns(TranslationsUtil.COLUMN_VS_TRANSLATION_TYPE.get("dashboardTab"));
                                    break;
                                case REPORT:
                                    webTab.setTypeVsColumns(TranslationsUtil.COLUMN_VS_TRANSLATION_TYPE.get("reportTab"));
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
