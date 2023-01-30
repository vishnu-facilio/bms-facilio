package com.facilio.bmsconsole.localization.fetchtranslationfields;

import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.context.WebTabGroupContext;
import com.facilio.bmsconsole.localization.translationImpl.WebTabTranslationImpl;
import com.facilio.bmsconsole.localization.translationbean.TranslationBean;
import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.bmsconsole.localization.util.TranslationsUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.TransactionBeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.util.FacilioUtil;
import lombok.NonNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public class GetWebTabGroupTranslationFields {

    public static JSONArray constructTranslationObject (Map<String, String> queryString) throws Exception {

        TranslationBean bean = (TranslationBean)TransactionBeanFactory.lookup("TranslationBean");
        Properties properties = bean.getTranslationFile(queryString.get("langCode"));
        FacilioUtil.throwIllegalArgumentException(properties == null,"Translation file is an empty");

        JSONArray jsonArray = new JSONArray();
        WebTabGroupContext webTabGroupContext = getWebTabGroups(queryString);
        String webTabGroupKey = WebTabTranslationImpl.getTranslationKey(WebTabTranslationImpl.WEB_TAB_GROUP,webTabGroupContext.getRoute());
        jsonArray.add(TranslationsUtil.constructJSON(webTabGroupContext.getName(),WebTabTranslationImpl.WEB_TAB_GROUP,TranslationConstants.DISPLAY_NAME,webTabGroupContext.getRoute(),webTabGroupKey,properties));

        JSONObject fieldObject = new JSONObject();
        fieldObject.put("fields",jsonArray);

        JSONArray sectionArray = new JSONArray();
        sectionArray.add(fieldObject);

        return sectionArray;
    }

    private static WebTabGroupContext getWebTabGroups ( Map<String, String> queryString ) throws Exception {
        Long webTabGroupId = Long.parseLong(queryString.get(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID));

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getWebTabGroupModule().getTableName())
                .select(FieldFactory.getWebTabGroupFields());

        if(webTabGroupId != null && webTabGroupId > 0){
            builder.andCondition(CriteriaAPI.getIdCondition(webTabGroupId,ModuleFactory.getWebTabGroupModule()));
        }
        List<WebTabGroupContext> webTabGroups = FieldUtil.getAsBeanListFromMapList(builder.get(), WebTabGroupContext.class);
        return webTabGroups.get(0);
    }
}
