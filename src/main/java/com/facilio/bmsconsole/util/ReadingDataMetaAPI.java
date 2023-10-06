package com.facilio.bmsconsole.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsoleV3.context.V3BaseSpaceContext;
import com.facilio.bmsconsoleV3.context.V3ResourceContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetCategoryContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j
public class ReadingDataMetaAPI {

    public static void updateReadingDataMeta(Long assetCategoryId) throws Exception {
        LOGGER.info("RDM assetCategoryId -- " + assetCategoryId);
        List<AssetContext> assetListOfCategory = AssetsAPI.getAssetListOfCategory(assetCategoryId);
        List<V3AssetContext> newAssetsList = new ArrayList<>();
        for (AssetContext ac : assetListOfCategory) {
            newAssetsList.add(FieldUtil.cloneBean(ac, V3AssetContext.class));
        }
        updateReadingDataMetaForAssets(newAssetsList);
    }

    public static <E extends V3ResourceContext> void updateReadingDataMeta(List<E> resources, V3ResourceContext.ResourceType type) throws Exception {
        switch (type) {
            case ASSET:
                updateReadingDataMetaForAssets((List<V3AssetContext>) resources);
                break;
            default:
                updateReadingDataMetaForSpace((List<V3ResourceContext>) resources);
                break;
        }
    }

    public static void updateReadingDataMetaForAssets(List<V3AssetContext> resourcesList) throws Exception {

        if (CollectionUtils.isEmpty(resourcesList)) {
            LOGGER.info("Resource list is empty!!");
            return;
        }

        long startTime = System.currentTimeMillis();
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        Map<Long, List<FacilioModule>> finalModuleListMap = new HashMap<>();

        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getReadingDataMetaModule().getTableName())
                .fields(FieldFactory.getReadingDataMetaFields())
                .ignore()
                .recordsPerBatch(20_000);

        for (V3AssetContext asset : resourcesList) {

            Long resourceId = asset.getId();

            V3AssetCategoryContext category = asset.getCategory();

            if (category == null) {
                LOGGER.info("Category is null for asset " + asset);
                continue;
            }

            List<FacilioModule> facilioModules = finalModuleListMap.get(category.getId());
            if (facilioModules == null) {
                FacilioContext context = new FacilioContext();
                context.put(FacilioConstants.ContextNames.PARENT_ID, asset.getId());

                FacilioChain assetCategoryChain = FacilioChainFactory.getAssetReadingsChain();
                assetCategoryChain.execute(context);
                facilioModules = (List<FacilioModule>) context.getOrDefault(FacilioConstants.ContextNames.MODULE_LIST, new ArrayList<>());
                finalModuleListMap.put(category.getId(), facilioModules);
            }

            for (FacilioModule module : facilioModules) {
                List<Map<String, Object>> rdmListProps = computeRDMListProps(module, orgId, resourceId);
                builder.addRecords(rdmListProps);
            }
        }

        builder.save();

        LOGGER.info("asset readings' rdm updated. resource list size : " + resourcesList.size() + ", time taken : " + (System.currentTimeMillis() - startTime) + "ms");
    }

    public static void updateReadingDataMetaForSpace(List<V3ResourceContext> resourcesList) throws Exception {

        if (CollectionUtils.isEmpty(resourcesList)) {
            LOGGER.info("Resource list is empty!!");
            return;
        }

        long startTime = System.currentTimeMillis();
        long orgId = AccountUtil.getCurrentOrg().getOrgId();

        Map<V3BaseSpaceContext.SpaceType, List<FacilioModule>> moduleListMap = new HashMap<>();

        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getReadingDataMetaModule().getTableName())
                .fields(FieldFactory.getReadingDataMetaFields())
                .ignore();

        LOGGER.info("resourcesList size -- " + resourcesList.size());
        for (int i = 0; i < resourcesList.size(); i++) {
            V3ResourceContext resource = resourcesList.get(i);
            V3BaseSpaceContext.SpaceType spaceTypeEnum = resource.getSpace().getSpaceTypeEnum();

            List<FacilioModule> facilioModules = moduleListMap.get(spaceTypeEnum);
            if (facilioModules == null) {
                FacilioContext context = new FacilioContext();
                context.put(FacilioConstants.ContextNames.PARENT_ID, resource.getId());

                FacilioChain assetCategoryChain = FacilioChainFactory.getAssetReadingsChain();
                assetCategoryChain.execute(context);

                facilioModules = (List<FacilioModule>) context.getOrDefault(FacilioConstants.ContextNames.MODULE_LIST, new ArrayList<>());
                moduleListMap.put(spaceTypeEnum, facilioModules);
            }

            for (FacilioModule module : facilioModules) {
                List<Map<String, Object>> rdmListProps = computeRDMListProps(module, orgId, resource.getId());
                builder.addRecords(rdmListProps);
            }
        }
        builder.save();
        LOGGER.info("space readings' rdm updated. resource list size : " + resourcesList.size() + ", time taken : " + (System.currentTimeMillis() - startTime) + "ms");
    }

    private static List<Map<String, Object>> computeRDMListProps(FacilioModule mod, long orgId, long resourceId) throws Exception {
        List<Map<String, Object>> computedList = new ArrayList<>();
        List<FacilioField> fieldList = mod.getFields();
        ReadingDataMeta.ReadingInputType inputType = ReadingsAPI.getRDMInputTypeFromModuleType(mod.getTypeEnum());
        for (FacilioField field : fieldList) {
            ReadingDataMeta rdm = new ReadingDataMeta();
            rdm.setOrgId(orgId);
            rdm.setFieldId(field.getFieldId());
            rdm.setField(field);
            rdm.setValue("-1");
            rdm.setResourceId(resourceId);
            rdm.setTtime(System.currentTimeMillis());
            rdm.setInputType(inputType);
            rdm.setCustom(!field.isDefault());
            computedList.add(FieldUtil.getAsProperties(rdm));
        }
        return computedList;
    }
}
