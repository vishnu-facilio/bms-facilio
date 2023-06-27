package com.facilio.odataservice.data;
import com.facilio.odataservice.util.ODATAUtil;
import com.facilio.odataservice.util.ODataModuleViewsUtil;
import com.facilio.odataservice.util.ODataReadingsUtil;
import org.apache.olingo.commons.api.data.EntityCollection;

public class Storage {
    public EntityCollection getViewRecords(String moduleName) throws Exception {
        ODATAUtil.setModuleName(moduleName);
        ODataModuleViewsUtil.setModuleName(moduleName);
        EntityCollection entities =  ODataModuleViewsUtil.getModuleViewRecords();
        return entities;
    }

    public EntityCollection getReadingsData(String readingView) throws Exception{
        EntityCollection retEntitySet = ODATAUtil.getMapAsEntityCollection(ODataReadingsUtil.getReadingViewData(readingView));
        return retEntitySet;
    }
}