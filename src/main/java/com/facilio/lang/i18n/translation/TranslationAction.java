package com.facilio.lang.i18n.translation;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.chain.FacilioContext;
import com.facilio.fw.BeanFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONArray;

import java.util.List;
import java.util.Map;

@Log4j
@Getter
@Setter
public class TranslationAction extends FacilioAction {

    private String langCode;
    private List<Map<String, Object>> translations;

    public String addLanguage () {
        try {
            addNewLanguage();
            setResponseCode(200);
        } catch (Exception e) {
            setResponseCode(500);
            setResult("exception",e.getMessage());
            LOGGER.error("Exception occurred while adding new language in Translation",e);
        }
        return SUCCESS;
    }

    public String getTranslationList () {
        try {
            setResult("translationList",constructModulesWithFields());
            setResponseCode(200);
        } catch (Exception e) {
            setResponseCode(500);
            setResult("exception",e.getMessage());
            LOGGER.error("Exception occurred while fetching modules in Translation",e);
        }
        return SUCCESS;
    }

    public String save () {
        try {
            addOrUpdateTranslation();
            setResponseCode(200);
        } catch (Exception e) {
            setResponseCode(500);
            setResult("exception",e.getMessage());
            LOGGER.error("Exception occurred while fetching modules in Translation",e);
        }
        return SUCCESS;
    }

    private void addNewLanguage () throws Exception {
        TranslationBean bean = (TranslationBean)BeanFactory.lookup("TranslationBean");
        bean.add(getLangCode());
    }

    private JSONArray constructModulesWithFields () throws Exception {
        TranslationBean bean = (TranslationBean)BeanFactory.lookup("TranslationBean");
        return bean.getModulesWithFields(getLangCode());
    }

    private void addOrUpdateTranslation () throws Exception {
        TranslationBean bean = (TranslationBean)BeanFactory.lookup("TranslationBean");
        bean.save(getLangCode(),getTranslations());
    }
}
