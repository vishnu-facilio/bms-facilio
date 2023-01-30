package com.facilio.bmsconsole.localization;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.ApplicationLayoutContext;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.context.WebTabGroupContext;
import com.facilio.bmsconsole.localization.fetchtranslationfields.GetWebTabGroupTranslationFields;
import com.facilio.bmsconsole.localization.fetchtranslationfields.TranslationTypeEnum;
import com.facilio.bmsconsole.localization.translationbean.TranslationBean;
import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.bmsconsole.localization.util.TranslationsUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
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

import java.util.ArrayList;
import java.util.HashMap;
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
    private boolean status = true;


    public String addLanguage () throws Exception {
        FacilioUtil.throwIllegalArgumentException(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.MULTI_LANGUAGE_TRANSLATION) == false,"Multi-Lang feature license is not enabled for this Org");
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

    private List<Map<String, Object>> getLang () throws Exception {
        GenericSelectRecordBuilder select = new GenericSelectRecordBuilder().select(TranslationConstants.getTranslationFields())
                .table(TranslationConstants.getTranslationModule().getTableName());
        List<Map<String, Object>> props = select.get();
        List<Map<String,Object>> langList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(props)){
            for (Map<String,Object> prop : props){
                Map<String,Object> map = new HashMap<>();
                map.put("langCode",prop.get("langCode"));
                map.put("status",prop.get("status"));
                langList.add(map);
            }
        }
        return langList;
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
                    if (CollectionUtils.isNotEmpty(prop.getWebTabGroupList())) {
                        for (WebTabGroupContext webtabGroup : prop.getWebTabGroupList()) {
                            if (CollectionUtils.isNotEmpty( webtabGroup.getWebTabs())) {
                                for (WebTabContext webTab : webtabGroup.getWebTabs()) {
                                    List<TranslationTypeEnum.ClientColumnTypeEnum> typeEnums = TranslationTypeEnum.CLIENT_TRANSLATION_TYPE_ENUM.get(webTab.getTypeEnum());
                                    if (CollectionUtils.isEmpty(typeEnums)){
                                        continue;
                                    }
                                    List<TranslationTypeEnum.ClientColumnTypeEnum> columnTypeEnums = new ArrayList<>(typeEnums);
                                    if (CollectionUtils.isNotEmpty(webTab.getModules())){
                                        List<String> moduleNames = webTab.getModules().stream().map(p->p.getName()).collect(Collectors.toList());
                                        if (webTab.getTypeEnum().equals(WebTabContext.Type.MODULE)){
                                            if (!moduleNames.contains(FacilioConstants.ContextNames.WORK_ORDER) ){
                                                columnTypeEnums.removeIf(clientColumnTypeEnum -> clientColumnTypeEnum.getType().equals(TranslationTypeEnum.WORKORDER_FIELDS.getClientColumnName()));
                                            }
                                            if (!moduleNames.contains(FacilioConstants.ContextNames.ASSET)){
                                                columnTypeEnums.removeIf(clientColumnTypeEnum -> clientColumnTypeEnum.getType().equals(TranslationTypeEnum.ASSET_FIELDS.getClientColumnName()));
                                            }
                                        }
                                    }
                                    if (webTab.getRoute().equals("portfolio")) {
                                        List<TranslationTypeEnum.ClientColumnTypeEnum> columnTypeEnums1 = new ArrayList<>(TranslationTypeEnum.CLIENT_TRANSLATION_TYPE_ENUM.get(WebTabContext.Type.MODULE));
                                        columnTypeEnums1.removeIf(clientColumnTypeEnum ->
                                                clientColumnTypeEnum.getLabel().equals(TranslationTypeEnum.WORKORDER_FIELDS.getClientColumnName())
                                                        || clientColumnTypeEnum.getLabel().equals(TranslationTypeEnum.ASSET_FIELDS.getClientColumnName())
                                                        || clientColumnTypeEnum.getLabel().equals(TranslationTypeEnum.STATES.getClientColumnName())
                                                        || clientColumnTypeEnum.getLabel().equals(TranslationTypeEnum.STATE_TRANSITION.getClientColumnName())
                                                        || clientColumnTypeEnum.getLabel().equals(TranslationTypeEnum.STATE_TRANSITION_FORM.getClientColumnName()));


                                        webTab.setTypeVsColumns(columnTypeEnums1);
                                    } else {
                                        webTab.setTypeVsColumns(columnTypeEnums);
                                    }
                                }
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

    private JSONArray constructJson ( Map<String, String> webTabs ) {
        JSONArray jsonArray = new JSONArray();
        int i=0;
        for (Map.Entry<String, String> entry : webTabs.entrySet()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("label",entry.getKey());
            jsonObject.put("value",entry.getValue());
            jsonObject.put("id",++i);
            jsonArray.add(jsonObject);
        }
        return jsonArray;
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

    public String updateStatus() throws Exception{
        TranslationsUtil.updateStatus(getLangCode(),isStatus());
        return SUCCESS;
    }

    public String getWebTabGroupFields() throws Exception {
        setResult("sections",GetWebTabGroupTranslationFields.constructTranslationObject(getFilter()));
        return SUCCESS;
    }

    private String moduleName;
    public String getColumnFields() throws Exception {
        if (StringUtils.isNotEmpty(moduleName)){
            if (moduleName.equals(FacilioConstants.ContextNames.WORK_ORDER)){
                setResult("fields",constructJson(TranslationsUtil.WORKORDER_FIELDS_MAP));
            }else if (moduleName.equals(FacilioConstants.ContextNames.ASSET)){
                setResult("fields",constructJson(TranslationsUtil.ASSET_FIELDS_MAP));
            }
        }
        return SUCCESS;
    }
}
