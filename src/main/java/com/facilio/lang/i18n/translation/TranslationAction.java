package com.facilio.lang.i18n.translation;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.TransactionBeanFactory;
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

    public String addLanguage () throws Exception {
        addNewLanguage();
        return SUCCESS;
    }

    public String getTranslationList () throws Exception {
        setResult("translationList",getTranslationsMeta());
        return SUCCESS;
    }

    public String save () throws Exception {
        addOrUpdateTranslation();
        return SUCCESS;
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
