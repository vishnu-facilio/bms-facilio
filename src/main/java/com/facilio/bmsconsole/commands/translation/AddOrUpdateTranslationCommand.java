package com.facilio.bmsconsole.commands.translation;

import com.facilio.command.FacilioCommand;
import com.facilio.db.util.DBConf;
import com.facilio.fw.BeanFactory;
import com.facilio.lang.i18n.translation.TranslationBean;
import com.facilio.lang.i18n.translation.TranslationConstants;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;
import java.util.Properties;

@Log4j
public class AddOrUpdateTranslationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand ( Context context ) throws Exception {
        String langCode = (String)context.get(TranslationConstants.LANG_CODE);
        List<Map<String, Object>> contents = (List<Map<String, Object>>)context.get("translations");
        Properties translationFile = DBConf.getInstance().getTranslationFile();
        try {
            for (Map<String, Object> prop : contents) {
                String prefix = (String)prop.get("prefix");
                String suffix = (String)prop.get("suffix");
                if(!TranslationsUtil.confFileValidation(prefix,suffix)) {
                    throw new IllegalArgumentException("Invalid prefix/ suffix set while adding translation props");
                }
                String key = prefix + "." + prop.get("key") + "." + suffix;
                String value = (String)prop.getOrDefault("value","");
                LOGGER.info("Translation kye "+key+" "+"value : "+value);
                translationFile.setProperty(key.trim(),value);
            }
            TranslationBean bean = (TranslationBean)BeanFactory.lookup("TranslationBean");
            bean.saveTranslationFile(langCode,translationFile);
        } catch (Exception e) {
            LOGGER.error("Exception occurred while writing translation file",e);
            throw e;
        }
        return false;
    }
}
