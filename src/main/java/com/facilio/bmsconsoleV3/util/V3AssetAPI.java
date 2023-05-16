package com.facilio.bmsconsoleV3.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PMResourcePlanner;
import com.facilio.bmsconsole.context.PlannedMaintenance;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.asset.AssetSpareParts;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTemplateContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTriggerIncludeExcludeResourceContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemTypesContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.util.V3Util;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class V3AssetAPI {
    public static void updateAsset(V3AssetContext asset, long assetId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule assetModule = modBean.getModule(FacilioConstants.ContextNames.ASSET);
        List<FacilioField> assetFields = modBean.getAllFields(FacilioConstants.ContextNames.ASSET);

        UpdateRecordBuilder<V3AssetContext> updateBuilder = new UpdateRecordBuilder<V3AssetContext>()
                .module(assetModule)
                .fields(assetFields)
                .andCondition(CriteriaAPI.getIdCondition(assetId, assetModule));

        updateBuilder.update(asset);

    }
    public static void stockRotatingItem(V3ItemContext rotatingItem, V3AssetContext asset) throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.getAddOrUpdateItemStockTransactionChainV3();
        chain.getContext().put(FacilioConstants.ContextNames.ITEM, rotatingItem);
        chain.getContext().put(FacilioConstants.ContextNames.ROTATING_ASSET, asset);
        chain.getContext().put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, false);
        chain.execute();
    }
    public static void updateRotatingItem(V3ItemContext item,V3AssetContext asset) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> assetFields = modBean.getAllFields(FacilioConstants.ContextNames.ASSET);
        FacilioModule assetModule = modBean.getModule(FacilioConstants.ContextNames.ASSET);
        asset.setRotatingItem(item);
        V3RecordAPI.updateRecord(asset,assetModule,assetFields);
    }
    public static Boolean isAssetMaintainable(V3AssetContext asset) throws Exception {
        // asset filter
        JSONObject filter = new JSONObject();
        filter.put("operatorId",36);
        filter.put("value",Collections.singletonList(String.valueOf(asset.getId())));
        // filter object
        JSONObject filterObj = new JSONObject();
        filterObj.put("resource",filter);
        String listFilter = filterObj.toString();
        Map<String, List<Object>> queryParams = new HashMap<>();
        queryParams.put("filters", Collections.singletonList(listFilter));
        FacilioContext listContext = V3Util.fetchList(FacilioConstants.ContextNames.WORK_ORDER, true, "open", listFilter, false, null, null, null, null, 1, 5000, false, queryParams, null);
        if(listContext!=null && ((Map<String,List>) listContext.get(FacilioConstants.ContextNames.RECORD_MAP)).get("workorder").size()>0){
            return true;
        }
        return false;
    }
    public static Boolean isAssetInStoreRoom(V3AssetContext asset) throws Exception {
        return asset.getStoreRoom()!=null && asset.getStoreRoom().getId()>0;
    }
    public static List<InspectionTemplateContext> getInspectionTemplatesList(Long asset) throws Exception{
        List<InspectionTemplateContext> singleResourceInspectionTemplates  = getSingleResourceInspectionTemplates(asset);
        List<InspectionTemplateContext> multiResourceInspectionTemplates  = getMultiResourceInspectionTemplate(asset);
        List<InspectionTemplateContext> inspectionTemplates = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(singleResourceInspectionTemplates)){
            inspectionTemplates.addAll(singleResourceInspectionTemplates);
        }
        if(CollectionUtils.isNotEmpty(multiResourceInspectionTemplates)){
            inspectionTemplates.addAll(multiResourceInspectionTemplates);
        }
        return inspectionTemplates;

    }
    private static List<InspectionTemplateContext> getMultiResourceInspectionTemplate(Long asset) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule inspectionTriggerResourceModule = modBean.getModule(FacilioConstants.Inspection.INSPECTION_TRIGGER_INCL_EXCL);
        List<FacilioField> inspectionTriggerResourceFields = modBean.getAllFields(FacilioConstants.Inspection.INSPECTION_TRIGGER_INCL_EXCL);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(inspectionTriggerResourceFields);
        SelectRecordsBuilder<InspectionTriggerIncludeExcludeResourceContext> select  = new SelectRecordsBuilder<InspectionTriggerIncludeExcludeResourceContext>()
                .select(Collections.singletonList(fieldMap.get("inspectionTemplate")))
                .module(inspectionTriggerResourceModule)
                .beanClass(InspectionTriggerIncludeExcludeResourceContext.class)
                .fetchSupplement((LookupField) fieldMap.get("inspectionTemplate"))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("resource"), String.valueOf(asset), NumberOperators.EQUALS));
        List<InspectionTriggerIncludeExcludeResourceContext> inspectionTriggerIncludeExcludeResources = select.get();
        List<Long> inspectionTemplateIds = inspectionTriggerIncludeExcludeResources.stream().map(inspectionTriggerIncludeExcludeResource -> inspectionTriggerIncludeExcludeResource.getInspectionTemplate().getId()).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(inspectionTemplateIds)){
            return V3RecordAPI.getRecordsList(FacilioConstants.Inspection.INSPECTION_TEMPLATE,inspectionTemplateIds,InspectionTemplateContext.class);
        }
        return null;
    }
    private static List<InspectionTemplateContext> getSingleResourceInspectionTemplates(Long asset) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule inspectionTemplateModule = modBean.getModule(FacilioConstants.Inspection.INSPECTION_TEMPLATE);
        List<FacilioField> inspectionTemplateAllFields = modBean.getAllFields(FacilioConstants.Inspection.INSPECTION_TEMPLATE);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(inspectionTemplateAllFields);
        SelectRecordsBuilder<InspectionTemplateContext> select  = new SelectRecordsBuilder<InspectionTemplateContext>()
                .select(inspectionTemplateAllFields)
                .module(inspectionTemplateModule)
                .beanClass(InspectionTemplateContext.class)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("resource"), String.valueOf(asset), NumberOperators.EQUALS));
        List<InspectionTemplateContext> inspectionTemplates = select.get();
        return inspectionTemplates;
    }
    public static List<PlannedMaintenance> plannedMaintenanceList(Long asset) throws Exception{
        List<PMResourcePlanner> pmResourcePlanners = getPMResourcePlanners(asset);
        if(CollectionUtils.isNotEmpty(pmResourcePlanners)){
            List<Long> plannedMaintenanceIds = pmResourcePlanners.stream().map(pmResourcePlanner -> pmResourcePlanner.getPmId()).collect(Collectors.toList());
            return V3RecordAPI.getRecordsList("plannedmaintenance",plannedMaintenanceIds,PlannedMaintenance.class);
        }
        return null;
    }
    private static List<PMResourcePlanner> getPMResourcePlanners(Long asset) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule pmPlannerModule = modBean.getModule("pmResourcePlanner");
        List<FacilioField> pmPlannerFields = modBean.getAllFields("pmResourcePlanner");
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(pmPlannerFields);

        SelectRecordsBuilder<PMResourcePlanner> selectRecordsBuilder = new SelectRecordsBuilder<>();
        selectRecordsBuilder.select(Collections.singletonList(fieldMap.get("pmId")))
                .beanClass(PMResourcePlanner.class)
                .module(pmPlannerModule)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("resource"), String.valueOf(asset), NumberOperators.EQUALS));

        List<PMResourcePlanner> pmResourcePlanners = selectRecordsBuilder.get();
        return pmResourcePlanners;
    }
    public static Boolean isAssetInStoreRoom(ResourceContext resource) throws Exception {
        if(resource!=null && resource.getResourceTypeEnum()!=null && resource.getResourceTypeEnum().equals(ResourceContext.ResourceType.ASSET) && resource.getId()>0){
            V3AssetContext asset  = V3RecordAPI.getRecord(FacilioConstants.ContextNames.ASSET,resource.getId(),V3AssetContext.class);
            return asset!=null && asset.getStoreRoom()!=null && asset.getStoreRoom().getId()>0;
        }
        return false;
    }
        public static Boolean createSparePart (V3ItemTypesContext itemType, V3AssetContext asset) throws Exception {
        String ModName = FacilioConstants.ContextNames.ASSET_SPARE_PARTS;
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(ModName);
        List<FacilioField> fields = modBean.getAllFields(ModName);
        AssetSpareParts existingSparePart = getSparePart(itemType,asset);

        if (existingSparePart != null){
            return false; // returns if spare part already exists
        }
        AssetSpareParts newSparePart = new AssetSpareParts();

        newSparePart.setItemType(itemType);
        newSparePart.setAsset(asset);
        newSparePart.setRequiredCount(1);
        V3Util.createRecord(module, FieldUtil.getAsProperties(newSparePart));
        return true;
    }

    public static AssetSpareParts getSparePart(V3ItemTypesContext itemType, V3AssetContext asset) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String ModName = FacilioConstants.ContextNames.ASSET_SPARE_PARTS;
        FacilioModule module = modBean.getModule(ModName);
        List<FacilioField> fields = modBean.getAllFields(ModName);

        Long itemTypeId = itemType._getId();
        Long assetId = asset.getId();
        SelectRecordsBuilder<AssetSpareParts> selectRecordsBuilder = new SelectRecordsBuilder<AssetSpareParts>()
                .module(module)
                .beanClass(AssetSpareParts.class)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition("ITEM_TYPE", "itemType", String.valueOf(itemTypeId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("ASSET_ID", "asset", String.valueOf(assetId), NumberOperators.EQUALS));
        List<AssetSpareParts> sparePartsList = selectRecordsBuilder.get();
        if(sparePartsList.isEmpty()){
            return null;
        } else {
            AssetSpareParts sparePart = sparePartsList.get(0);
            return sparePart;
        }

    }

    public static void updateSparePartsIssuedCount (V3ItemTypesContext itemType, V3AssetContext asset, Integer latestIssuedCount) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String ModName = FacilioConstants.ContextNames.ASSET_SPARE_PARTS;
        FacilioModule module = modBean.getModule(ModName);
        AssetSpareParts sparePartRecord = getSparePart(itemType,asset);
        Integer existingCount =   sparePartRecord.getIssuedCount();
        if(existingCount != null){
            sparePartRecord.setIssuedCount(existingCount + latestIssuedCount);
        }else {
            sparePartRecord.setIssuedCount(latestIssuedCount);
        }
        FacilioField issuedCountField = modBean.getField("issuedCount", module.getName());
        List<FacilioField> updatedfields = new ArrayList<FacilioField>();
        updatedfields.add(issuedCountField);
        V3RecordAPI.updateRecord(sparePartRecord,module,updatedfields);
    }
}
