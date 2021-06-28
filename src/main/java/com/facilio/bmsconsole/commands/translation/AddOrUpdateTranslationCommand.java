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
import java.util.stream.Collectors;

@Log4j
public class AddOrUpdateTranslationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand ( Context context ) throws Exception {
        String langCode = (String)context.get(TranslationConstants.LANG_CODE);
        List<Map<String, Object>> contents = (List<Map<String, Object>>)context.get("translations");
        long fileId = TranslationUtils.getFileId(langCode);
        FileStore fs = FacilioFactory.getFileStore();
        String filePath = fs.getFileInfo(fileId).getFilePath();
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(filePath); FileOutputStream outputStream = new FileOutputStream(filePath);) {
            properties.load(input);
            properties.forEach(( k,v ) -> properties.put(k.toString().trim(),v.toString().trim()));
            Map<String, Object> props = contents.stream().flatMap(m -> m.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue));
            props.forEach((key,value)->{
                properties.setProperty(key.trim(),value.toString().trim());
            });
            properties.store(outputStream,"Translation Properties");
        } catch (Exception e) {
            LOGGER.error("Exception occurred while writing translation file",e);
        }
        return false;
    }
}
