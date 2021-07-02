package com.facilio.bmsconsole.commands.translation;

import com.facilio.command.FacilioCommand;
import com.facilio.db.util.DBConf;
import com.facilio.lang.i18n.translation.TranslationConstants;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;

import java.util.Properties;

public class CreateTranslationJSONCommand extends FacilioCommand {
    @Override
    public boolean executeCommand ( Context context ) throws Exception {
        Properties properties = DBConf.getInstance().getTranslationFile();
        if(properties ==null){
            throw new IllegalArgumentException("Translation file is empty");
        }
        JSONArray translationList = new JSONArray();
        context.put("propertyFile",properties);
        context.put(TranslationConstants.TRANSLATION_LIST,translationList);
        return false;
    }
}
