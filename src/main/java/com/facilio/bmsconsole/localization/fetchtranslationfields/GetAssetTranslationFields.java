package com.facilio.bmsconsole.localization.fetchtranslationfields;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.AssetTypeContext;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.bmsconsole.localization.util.TranslationsUtil;
import com.facilio.bmsconsoleV3.context.AssetDepartmentContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public class GetAssetTranslationFields implements TranslationTypeInterface{

    @Override
    public JSONArray constructTranslationObject(@NonNull WebTabContext context, Map<String, String> queryString, Properties properties) throws Exception {

        String moduleName = queryString.get("moduleName");

        FacilioChain dataList = ReadOnlyChainFactory.fetchModuleDataListChain();
        FacilioContext listContext = dataList.getContext();
        listContext.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);

        dataList.execute();

        JSONArray jsonArray = new JSONArray();

        switch (moduleName) {
            case FacilioConstants.ContextNames.ASSET_CATEGORY:
                fetchAssetCategory(properties, moduleName, listContext, jsonArray);
                break;
            case FacilioConstants.ContextNames.ASSET_DEPARTMENT:
                fetchAssetDepartment(properties, moduleName, listContext, jsonArray);
                break;
            case FacilioConstants.ContextNames.ASSET_TYPE:
                fetchAssetType(properties, moduleName, listContext, jsonArray);
                break;
        }

        JSONObject fieldObject = new JSONObject();
        fieldObject.put("fields", jsonArray);

        JSONArray sectionArray = new JSONArray();
        sectionArray.add(fieldObject);

        return sectionArray;
    }

    private void fetchAssetType(Properties properties, String prefix, FacilioContext listContext, JSONArray jsonArray) {
        List<AssetTypeContext> moduleDataList = (List<AssetTypeContext>) listContext.get(FacilioConstants.ContextNames.RECORD_LIST);
        if (CollectionUtils.isNotEmpty(moduleDataList)) {
            for (AssetTypeContext moduleData : moduleDataList) {
                String key = TranslationsUtil.getTranslationKey(prefix, String.valueOf(moduleData.getId()));
                jsonArray.add(TranslationsUtil.constructJSON(moduleData.getName(), prefix, TranslationConstants.DISPLAY_NAME, String.valueOf(moduleData.getId()), key, properties));
            }
        }
    }

    private void fetchAssetDepartment(Properties properties, String prefix, FacilioContext listContext, JSONArray workOrderArray) {
        List<AssetDepartmentContext> moduleDataList = (List<AssetDepartmentContext>) listContext.get(FacilioConstants.ContextNames.RECORD_LIST);
        if (CollectionUtils.isNotEmpty(moduleDataList)) {
            for (AssetDepartmentContext moduleData : moduleDataList) {
                String key = TranslationsUtil.getTranslationKey(prefix, String.valueOf(moduleData.getId()));
                workOrderArray.add(TranslationsUtil.constructJSON(moduleData.getName(), prefix, TranslationConstants.DISPLAY_NAME, String.valueOf(moduleData.getId()), key, properties));
            }
        }
    }

    private void fetchAssetCategory(Properties properties, String prefix, FacilioContext listContext, JSONArray workOrderArray) {
        List<AssetCategoryContext> moduleDataList = (List<AssetCategoryContext>) listContext.get(FacilioConstants.ContextNames.RECORD_LIST);
        if (CollectionUtils.isNotEmpty(moduleDataList)) {
            for (AssetCategoryContext moduleData : moduleDataList) {
                String key = TranslationsUtil.getTranslationKey(prefix, String.valueOf(moduleData.getId()));
                workOrderArray.add(TranslationsUtil.constructJSON(moduleData.getDisplayName(), prefix, TranslationConstants.DISPLAY_NAME, String.valueOf(moduleData.getId()), key, properties));
            }
        }
    }

}
