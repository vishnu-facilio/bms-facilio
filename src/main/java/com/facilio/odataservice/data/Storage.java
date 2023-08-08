package com.facilio.odataservice.data;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.odataservice.util.ODATAUtil;
import com.facilio.odataservice.util.ODataModuleViewsUtil;
import com.facilio.odataservice.util.ODataReadingsUtil;
import org.apache.olingo.commons.api.data.EntityCollection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Storage {
    public EntityCollection getViewRecords(String moduleName, int limit, String selectField, int skip, String orderBy, boolean isDescending, FacilioView view, List<ODataFilterContext> filterContext) throws Exception {
        Map<String,Object> builderParams = new HashMap<>();
        builderParams.put("limit",limit);
        builderParams.put("offset",skip);
        builderParams.put("orderBy",orderBy);
        builderParams.put("isDescending",isDescending);
        builderParams.put("selectFields",selectField);
        builderParams.put("filterContext",filterContext);
        FacilioView selectedView = new FacilioView();
        if(view != null){
            selectedView = view;
        }
        EntityCollection entities =  ODataModuleViewsUtil.getModuleViewRecords(moduleName,builderParams,selectedView);
        return entities;
    }

    public EntityCollection getReadingsData(String readingView) throws Exception{
        EntityCollection retEntitySet = ODATAUtil.getMapAsEntityCollection(ODataReadingsUtil.getReadingViewData(readingView), null,true);
        return retEntitySet;
    }
}