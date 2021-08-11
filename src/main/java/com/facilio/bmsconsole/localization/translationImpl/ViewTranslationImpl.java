package com.facilio.bmsconsole.localization.translationImpl;

import com.facilio.bmsconsole.localization.translation.ModuleTranslationUtils;
import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.translation.TranslationIfc;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Properties;

public class ViewTranslationImpl implements TranslationIfc {
    public static final String VIEWS ="views";
    @Override
    public JSONObject translate ( JSONObject json,Properties translationFile ) throws Exception {

        JSONObject jsonObject = (JSONObject)json.get("result");
        JSONArray groups = (JSONArray)jsonObject.get("groupViews");
        JSONArray views = (JSONArray)jsonObject.get("views");
        if(groups != null && !groups.isEmpty()) {
            for (int i=0;i< groups.size();i++){
                JSONObject groupViews = (JSONObject)groups.get(i);
                String outerKey = getTranslationKey((String)groupViews.get(TranslationConstants.NAME));
                groupViews.put(TranslationConstants.DISPLAY_NAME,getTranslation(translationFile,outerKey,(String)groupViews.get(TranslationConstants.DISPLAY_NAME)));
                JSONArray jsonArray = (JSONArray)groupViews.get("views");
                for (int j=0;j< jsonArray.size();j++){
                    JSONObject viewObject = (JSONObject)jsonArray.get(j);
                    String innerKey = getTranslationKey((String)viewObject.get(TranslationConstants.NAME));
                    viewObject.put(TranslationConstants.DISPLAY_NAME,getTranslation(translationFile,innerKey,(String)viewObject.get(TranslationConstants.DISPLAY_NAME)));
                }
            }
        }else if(views != null && !views.isEmpty()){
            for (int i=0;i< views.size();i++){
                JSONObject groupViews = (JSONObject)views.get(i);
                String outerKey = getTranslationKey((String)groupViews.get(TranslationConstants.NAME));
                groupViews.put(TranslationConstants.DISPLAY_NAME,getTranslation(translationFile,outerKey,(String)groupViews.get(TranslationConstants.DISPLAY_NAME)));
            }
        }

        return json;
    }

    public static String getTranslationKey ( String key ) {
        return VIEWS+"."+key+"."+TranslationConstants.DISPLAY_NAME;
    }
}
