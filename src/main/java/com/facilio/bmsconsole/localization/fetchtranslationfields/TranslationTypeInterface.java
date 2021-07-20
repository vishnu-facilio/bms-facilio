package com.facilio.bmsconsole.localization.fetchtranslationfields;

import com.facilio.bmsconsole.context.WebTabContext;
import lombok.NonNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Properties;

public interface TranslationTypeInterface {

    JSONArray constructTranslationObject ( @NonNull WebTabContext context,Properties properties ) throws Exception;
}
