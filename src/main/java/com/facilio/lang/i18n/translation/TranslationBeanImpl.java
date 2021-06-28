package com.facilio.lang.i18n.translation;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.util.DBConf;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONArray;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        props.put(TranslationConstants.ORGID,getOrgId());
        props.put(TranslationConstants.LANG_CODE,langCode);
        props.put(TranslationConstants.FILE_ID,fileId);
        addLanguage(props);
    }

    @Override
    public JSONArray getModulesWithFields ( String langCode) throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getTranslationListChain();
        FacilioContext context = chain.getContext();
        context.put(TranslationConstants.LANG_CODE,langCode);
        chain.execute();
        return (JSONArray)context.get("translationList");
    }

    @Override
    public void save ( String langCode,List<Map<String,Object>> translations) throws Exception {
        FacilioChain chain= TransactionChainFactory.addOrUpdateTranslationChain();
        FacilioContext context = chain.getContext();
        context.put(TranslationConstants.LANG_CODE,langCode);
        context.put("translations",translations);
        chain.execute();
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
