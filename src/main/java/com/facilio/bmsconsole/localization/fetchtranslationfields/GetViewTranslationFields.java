package com.facilio.bmsconsole.localization.fetchtranslationfields;

import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.localization.translationImpl.ViewColumnTranslationImpl;
import com.facilio.bmsconsole.localization.translationImpl.ViewTranslationImpl;
import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.bmsconsole.localization.util.TranslationsUtil;
import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.util.FacilioUtil;
import lombok.NonNull;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Properties;

@Log4j
public class GetViewTranslationFields implements TranslationTypeInterface {

    private static final String VIEWS = "views";


    @Override
    public JSONArray constructTranslationObject ( @NonNull WebTabContext context,Map<String,String> filters,Properties properties ) throws Exception {

        FacilioUtil.throwIllegalArgumentException(!WebTabContext.Type.MODULE.equals(WebTabContext.Type.valueOf(context.getType())),"Invalid webTab Type for fetch Module Fields");
        FacilioUtil.throwIllegalArgumentException(StringUtils.isEmpty(filters.get("viewId")),"view id is mandatory param for fetching view  fields");

        JSONArray fieldList = new JSONArray();
        long viewId = Long.parseLong(filters.get("viewId"));

        FacilioView view = ViewAPI.getView(viewId);
        List<ViewField> viewFields = view.getFields();

        if(CollectionUtils.isNotEmpty(viewFields)) {

            String viewKey = ViewTranslationImpl.getTranslationKey(view.getName());
            fieldList.add(TranslationsUtil.constructJSON(view.getDisplayName(),VIEWS,TranslationConstants.DISPLAY_NAME,view.getName(),viewKey,properties));

            for (ViewField viewField : viewFields) {
                String viewColumnId = String.valueOf(viewField.getId());
                String viewColumnKey = ViewColumnTranslationImpl.getTranslationKey(viewColumnId);
                String displayName = viewField.getColumnDisplayName();
                if(StringUtils.isNotEmpty(displayName)){
                    fieldList.add(TranslationsUtil.constructJSON(viewField.getColumnDisplayName(),ViewColumnTranslationImpl.VIEWS_COLUMNS,TranslationConstants.DISPLAY_NAME,viewColumnId,viewColumnKey,properties));
                }
            }
        }

        JSONObject fieldObject = new JSONObject();
        fieldObject.put("fields",fieldList);
        fieldObject.put("label","");

        JSONArray sectionArray = new JSONArray();
        sectionArray.add(fieldObject);

        return sectionArray;
    }
}
