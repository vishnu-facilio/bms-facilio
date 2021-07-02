package com.facilio.lang.i18n.translation;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.translation.TranslationsUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.util.DBConf;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONArray;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Log4j
public class TranslationBeanImpl implements TranslationBean {

    @Override
    public long getOrgId () {
        return DBConf.getInstance().getCurrentOrgId();
    }

    @Override
    public void add ( String langCode ) throws Exception {
        long fileId = DBConf.getInstance().addFile("",TranslationConstants.TRANSLATION_DATA + ".properties","text/plain");
        Map<String, Object> props = new HashMap<>();
        props.put("orgId",getOrgId());
        props.put(TranslationConstants.LANG_CODE,langCode);
        props.put(TranslationConstants.FILE_ID,fileId);
        addLanguage(props);
    }

    @Override
    public JSONArray getTranslationList ( String langCode) throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getTranslationListChain();
        FacilioContext context = chain.getContext();
        context.put(TranslationConstants.LANG_CODE,langCode);
        chain.execute();
        return (JSONArray)context.get(TranslationConstants.TRANSLATION_LIST);
    }

    @Override
    public void save ( String langCode,List<Map<String,Object>> translations) throws Exception {
        FacilioChain chain= TransactionChainFactory.addOrUpdateTranslationChain();
        FacilioContext context = chain.getContext();
        context.put(TranslationConstants.LANG_CODE,langCode);
        context.put("translations",translations);
        chain.execute();
    }

    @Override
    public Properties getTranslationFile ( String langCode ) throws Exception {
        long fileId = TranslationsUtil.getTranslationFileId(langCode);
        Properties properties = new Properties();
        if(fileId > -1L) {
        	 FileStore fs = FacilioFactory.getFileStore();
             String filePath = fs.getFileInfo(fileId).getFilePath();
             try (FileInputStream input = new FileInputStream(filePath)) {
                 properties.load(input);
                 properties.forEach(( k,v ) -> properties.put(k.toString().trim(),v.toString().trim()));
                 return properties;
             } catch (Exception e) {
                 LOGGER.error("Exception occurred while writing translation file",e);
             }
        }
        return properties;
    }

    private void addLanguage ( Map<String, Object> props ) throws Exception {
        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .fields(TranslationConstants.getTranslationFields())
                .table(TranslationConstants.getTranslationModule().getTableName());
        if(insertBuilder.insert(props) > 0L) {
            LOGGER.info("Successfully added new language in Translation");
        }
    }
}
