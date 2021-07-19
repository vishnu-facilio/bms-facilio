package com.facilio.bmsconsole.localization.translationbean;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.bmsconsole.localization.util.TranslationsUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.util.DBConf;
import com.facilio.util.FacilioUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.*;

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
        props.put("status",true);
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
             try (InputStream input = DBConf.getInstance().getFileContent(fileId)) {
                 String msgBody = IOUtils.toString(input, StandardCharsets.UTF_8);
                 try(StringReader reader = new StringReader(msgBody);){
                     properties.load(reader);
                 }
                 return properties;
             } catch (Exception e) {
                 LOGGER.error("Exception occurred while writing translation file",e);
                 throw e;
             }
        }
        return null;
    }

    @Override
    public void saveTranslationFile ( String langCode,Properties translationFile ) throws Exception {
        long existingFileId = TranslationsUtil.getTranslationFileId(langCode);
        DBConf.getInstance().deleteFileContent(Collections.singletonList(existingFileId));
        String fileContent = convertObjectToString(translationFile);
        FacilioUtil.throwIllegalArgumentException(StringUtils.isEmpty(fileContent),"Invalid content to store in Property file");
        long newFileId = DBConf.getInstance().addFile(fileContent,TranslationConstants.TRANSLATION_DATA + ".properties","text/plain");
        Map<String, Object> props = new HashMap<>();
        props.put(TranslationConstants.FILE_ID,newFileId);
        updateFileId(existingFileId,props);
    }

    private String convertObjectToString ( Properties prop )throws Exception {
        StringWriter writer = new StringWriter();
        try {
            if(prop != null && !prop.isEmpty()){
                prop.store(writer, "Translation Properties");
            }
        } catch (Exception e) {
            throw e;
        }
        return writer.getBuffer().toString();
    }

    private void updateFileId ( long existingFileId,Map<String, Object> props ) throws SQLException {
        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .fields(TranslationConstants.getTranslationFields())
                .table(TranslationConstants.getTranslationModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("FILE_ID",TranslationConstants.FILE_ID,String.valueOf(existingFileId),NumberOperators.EQUALS));
                builder.update(props);
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
