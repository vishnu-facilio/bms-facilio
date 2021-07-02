package com.facilio.bmsconsole.commands.translation;

import com.facilio.command.FacilioCommand;
import com.facilio.lang.i18n.translation.TranslationConstants;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Log4j
public class AddOrUpdateTranslationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand ( Context context ) throws Exception {
        String langCode = (String)context.get(TranslationConstants.LANG_CODE);
        List<Map<String, Object>> contents = (List<Map<String, Object>>)context.get("translations");
        long fileId = TranslationsUtil.getTranslationFileId(langCode);
        FileStore fs = FacilioFactory.getFileStore();
        String filePath = fs.getFileInfo(fileId).getFilePath();
        Properties properties = new Properties();
        try {
            try (FileInputStream inputStream = new FileInputStream(filePath);) {
                properties.load(inputStream);
            }
            try (FileOutputStream outputStream = new FileOutputStream(filePath);) {
                for (Map<String, Object> prop : contents) {
                    String prefix = (String)prop.get("prefix");
                    String suffix = (String)prop.get("suffix");
                    if(!TranslationsUtil.prefixSuffixValidation(prefix,suffix)) {
                        throw new IllegalArgumentException("Exception occurred while validating prefix/suffix in Add or Update Translation ");
                    }
                    String key = prefix + "." + prop.get("key") + "." + suffix;
                    String value = (String)prop.getOrDefault("value","");
                    properties.setProperty(key.trim(),value);
                }
                properties.store(outputStream,"Translation Properties");
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred while writing translation file",e);
        }
        return false;
    }
}
