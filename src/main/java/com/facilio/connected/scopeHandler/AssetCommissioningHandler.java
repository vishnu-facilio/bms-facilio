package com.facilio.connected.scopeHandler;

import com.facilio.agentv2.AgentConstants;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.CommissioningApi;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.V3Context;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

@Log4j
public class AssetCommissioningHandler implements ScopeCommissioningHandler {

    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.ASSET;
    }

    @Override
    public String getSubModuleName() {
        return FacilioConstants.ContextNames.ASSET_CATEGORY;
    }

    @Override
    public String getDisplayName() {
        return "Asset";
    }

    @Override
    public String getTypeDisplayName() {
        return "Category";
    }

    @Override
    public void updateConnectionStatus(Set<Long> assetIds, boolean isConnected) throws Exception {
        AssetsAPI.updateAssetConnectionStatus(assetIds, isConnected);
    }

    @Override
    public String getResourceName(Long resourceId) throws Exception {
        ResourceContext resource = ResourceAPI.getResource(resourceId);
        return resource.getName();
    }

    @Override
    public Map<Long, String> getParent(Set<Long> assetIds) throws Exception {
        return CommissioningApi.getParent(assetIds, FacilioConstants.ContextNames.RESOURCE);
    }

    @Override
    public Map<Long, String> getChildTypes(Set<Long> categoryIds) throws Exception {
        return CommissioningApi.getParent(categoryIds, getSubModuleName());
    }

    @Override
    public Map<String, FacilioField> getReadings(Long categoryId, Long parentId, AgentConstants.AutoMappingReadingFieldName autoMappingReadingFieldNameEnum) throws Exception {
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE, ModuleFactory.getAssetCategoryReadingRelModule());
        context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, categoryId);
        context.put(FacilioConstants.ContextNames.FILTER, "available");
        context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_ID, true);
        context.put(FacilioConstants.ContextNames.PARENT_ID, parentId);

        FacilioChain getCategoryReadingChain = FacilioChainFactory.getCategoryReadingsChain();
        getCategoryReadingChain.execute(context);

        Map<String, FacilioField> fieldsMap = new HashMap<>();
        List<FacilioModule> facilioModules = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
        for (FacilioModule module : facilioModules) {
            List<Long> fieldIds = new ArrayList<>();
            List<FacilioField> fields = module.getFields();
            for (FacilioField field : fields) {
                String fieldName = (autoMappingReadingFieldNameEnum == AgentConstants.AutoMappingReadingFieldName.NAME) ?
                        field.getName() : field.getDisplayName();
                fieldsMap.put(fieldName, field);
                fieldIds.add(field.getFieldId());
            }
            LOGGER.info("Module name: " + module.getName() + ", ID : " + module.getModuleId() + ", Field Ids : " + fieldIds);
        }
        return fieldsMap;
    }

    @Override
    public Pair<Long, Long> getParentIdAndCategoryId(V3Context parent) {
        V3AssetContext assetContext = (V3AssetContext) parent;
        return Pair.of(assetContext.getCategory().getId(), assetContext.getId());
    }

    @Override
    public FacilioField getTypeField() throws Exception {
        return FieldFactory.getField("category", "CATEGORY", Constants.getModBean().getModule(getModuleName()), FieldType.LOOKUP);
    }

}
