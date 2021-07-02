package com.facilio.lang.i18n.translation;

import com.facilio.beans.RootBean;
import lombok.NonNull;
import org.json.simple.JSONArray;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public interface TranslationBean extends RootBean {

    void add (@NonNull String langCode ) throws Exception;

    JSONArray getTranslationList (@NonNull String langCode ) throws Exception;

    void save (@NonNull String langCode,List<Map<String, Object>> translations ) throws Exception;

    Properties getTranslationFile(@NonNull String langCode) throws Exception;

}
