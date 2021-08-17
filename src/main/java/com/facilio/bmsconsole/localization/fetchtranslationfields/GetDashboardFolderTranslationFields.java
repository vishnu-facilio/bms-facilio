package com.facilio.bmsconsole.localization.fetchtranslationfields;

import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.DashboardFolderContext;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.localization.translationImpl.DashboardTranslationImpl;
import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.bmsconsole.localization.util.TranslationsUtil;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.util.FacilioUtil;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Properties;

public class GetDashboardFolderTranslationFields implements TranslationTypeInterface {
    @Override
    public JSONArray constructTranslationObject ( @NonNull WebTabContext context,String queryString,Properties properties ) throws Exception {

        FacilioUtil.throwIllegalArgumentException(!WebTabContext.Type.DASHBOARD.equals(WebTabContext.Type.valueOf(context.getType())),"Invalid webTab Type for fetching Dashboard Fields");

        JSONArray jsonArray = new JSONArray();
        List<DashboardFolderContext> dashboardFolders;

        dashboardFolders = DashboardUtil.getDashboardList(null,false,context.getApplicationId());

        if(CollectionUtils.isNotEmpty(dashboardFolders)) {

            for (DashboardFolderContext dashboard : dashboardFolders) {
                String id = String.valueOf(dashboard.getId());
                String key = DashboardTranslationImpl.getDashboardFolderKey(id);
                jsonArray.add(TranslationsUtil.constructJSON(dashboard.getName(),DashboardTranslationImpl.DASHBOARD_FOLDER,TranslationConstants.DISPLAY_NAME,id,key,properties));
            }
        }

        JSONObject fieldObject = new JSONObject();
        fieldObject.put("fields",jsonArray);
        fieldObject.put("label","");

        JSONArray sectionArray = new JSONArray();
        sectionArray.add(fieldObject);

        return sectionArray;
    }
}
