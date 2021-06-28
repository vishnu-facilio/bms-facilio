package com.facilio.lang.i18n.translation;

import com.facilio.beans.RootBean;
import com.facilio.chain.FacilioContext;
import org.json.simple.JSONArray;

import java.util.List;
import java.util.Map;

public interface TranslationBean extends RootBean {

    void add ( String langCode ) throws Exception;

    JSONArray getModulesWithFields ( String langCode ) throws Exception;

    void save ( String langCode,List<Map<String, Object>> translations ) throws Exception;
}
