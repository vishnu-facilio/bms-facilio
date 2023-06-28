package com.facilio.bmsconsoleV3.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agentv2.AgentConstants;
import com.facilio.beans.ModuleBean;
import com.facilio.beans.ModuleBeanImpl;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsoleV3.context.*;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import com.facilio.wmsv2.endpoint.SessionManager;
import com.facilio.wmsv2.handler.CommissioningLogHandler;
import com.facilio.wmsv2.handler.ResourceDecommissioningHandler;
import com.facilio.wmsv2.message.Message;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;
@Getter@Setter
public class DecommissionUtil {

    public static Map<String, Object> fetchDependentModulesDataForResource(Long currentResourceId, List<Long> dependentResourceIds, String currentResourceModuleName) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        Map<String, Object> dependentResourcesModuleList = new LinkedHashMap<>();


        //ERROR MODULES

        FacilioModule tenantUnitSpaceModule = ModuleFactory.getTenantUnitSpaceModule();
        Long tenantUnitSpaceCount = getTenantsCountOccupiedUnits(dependentResourceIds);
        if (tenantUnitSpaceCount > 0) {
            dependentResourcesModuleList.put(tenantUnitSpaceModule.getName(), constructResultJSON(tenantUnitSpaceModule, tenantUnitSpaceCount, FacilioConstants.DecommissionModuleType.ERROR));
        }

        if(currentResourceModuleName.equals(FacilioConstants.ContextNames.SITE)) {
            FacilioModule clientPortalModule = modBean.getModule(FacilioConstants.ContextNames.CLIENT);
            Long clientPortalClientCount = getClientCountOfDependentUnits(currentResourceId);
            if (clientPortalClientCount > 0) {
                dependentResourcesModuleList.put(clientPortalModule.getName(), constructResultJSON(clientPortalModule, clientPortalClientCount, FacilioConstants.DecommissionModuleType.ERROR));
            }
        }

        //WARNING MODULES
        FacilioModule pmPlannerModule = modBean.getModule(FacilioConstants.PM_V2.PM_V2_RESOURCE_PLANNER);
        Long pmPlannerCount = getDependentResourcePMCount(dependentResourceIds);
        if (pmPlannerCount > 0) {
            dependentResourcesModuleList.put(pmPlannerModule.getName(), constructResultJSON(pmPlannerModule, pmPlannerCount, FacilioConstants.DecommissionModuleType.WARNING));
        }

//        FacilioModule inspectionTemplateModule = modBean.getModule(FacilioConstants.Inspection.INSPECTION_TEMPLATE);
//        Long inspectionTemplateCount = getDependentResourceInspectionTemplateCount(dependentResourceIds , currentResourceModuleName);
//        if(inspectionTemplateCount > 0){
//            dependentResourcesModuleList.put(inspectionTemplateModule.getName() , constructResultJSON(inspectionTemplateModule , inspectionTemplateCount , false));
//        }

        if (currentResourceModuleName.equals(FacilioConstants.ContextNames.SITE)) {

            FacilioModule inductionTemplateModule = modBean.getModule(FacilioConstants.Induction.INDUCTION_TEMPLATE);
            Long inductionTemplateCount = getDependentResourceInductionTemplateCount(Collections.singletonList(currentResourceId));
            if(inductionTemplateCount > 0) {
                dependentResourcesModuleList.put(inductionTemplateModule.getName() , constructResultJSON(inductionTemplateModule , inductionTemplateCount , FacilioConstants.DecommissionModuleType.WARNING));
            }


        }

        Long workorderCount = getDependentResourceWorkorderCount(dependentResourceIds, currentResourceModuleName);
        if (workorderCount > 0) {
            dependentResourcesModuleList.put(ModuleFactory.getWorkOrdersModule().getName(), constructResultJSON(ModuleFactory.getWorkOrdersModule(), workorderCount, FacilioConstants.DecommissionModuleType.INFO));
        }


        FacilioModule inspectionModule = modBean.getModule(FacilioConstants.Inspection.INSPECTION_RESPONSE);
        Long inspectionCount = getDependentResourceInspectionCount(dependentResourceIds);
        if (inspectionCount > 0) {
            dependentResourcesModuleList.put(inspectionModule.getName(), constructResultJSON(inspectionModule, inspectionCount, FacilioConstants.DecommissionModuleType.INFO));
        }
        if(currentResourceModuleName.equals(FacilioConstants.ContextNames.SITE)){
            FacilioModule inductionModule = modBean.getModule(FacilioConstants.Induction.INDUCTION_RESPONSE);
            Long inductionCount = getDependentResourceInductionCount(Collections.singletonList(currentResourceId), currentResourceModuleName);
            if (inductionCount > 0) {
                dependentResourcesModuleList.put(inductionModule.getName(), constructResultJSON(inductionModule, inductionCount, FacilioConstants.DecommissionModuleType.INFO));
            }

        }

        FacilioModule serviceRequestModule = modBean.getModule(FacilioConstants.ContextNames.SERVICE_REQUEST);
        Long serviceRequestCount = getDependentResourceServiceRequestCount(dependentResourceIds, currentResourceModuleName);
        if (serviceRequestCount > 0) {
            dependentResourcesModuleList.put(serviceRequestModule.getName(), constructResultJSON(serviceRequestModule, serviceRequestCount, FacilioConstants.DecommissionModuleType.INFO));
        }

        if(currentResourceModuleName.equals(FacilioConstants.ContextNames.SITE)){
            FacilioModule inventoryModule = modBean.getModule(FacilioConstants.ContextNames.STORE_ROOM);
            Long storeRoomCount = getDependentResourceStoreRoomCount(Collections.singletonList(currentResourceId), currentResourceModuleName);
            if (storeRoomCount > 0) {
                dependentResourcesModuleList.put(inventoryModule.getName(), constructResultJSON(inventoryModule, storeRoomCount, FacilioConstants.DecommissionModuleType.INFO));
            }

            FacilioModule safetyPlanModule = modBean.getModule(FacilioConstants.ContextNames.SAFETY_PLAN);
            Long safetyPlanCount = getDependentSafetyPlansCount(currentResourceId);
            if (safetyPlanCount > 0) {
                dependentResourcesModuleList.put(safetyPlanModule.getName(), constructResultJSON(safetyPlanModule, safetyPlanCount, FacilioConstants.DecommissionModuleType.INFO));
            }
        }

        return dependentResourcesModuleList;
    }

    public static JSONObject constructResultJSON(FacilioModule module, Long count, String type) {
        JSONObject concernedModuleObject = new JSONObject();
        concernedModuleObject.put(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME, module.getDisplayName());
        concernedModuleObject.put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
        concernedModuleObject.put(FacilioConstants.ContextNames.TYPE, type);
        concernedModuleObject.put(FacilioConstants.ContextNames.COUNT, count);
        return concernedModuleObject;
    }

    public static String getResourceModuleNameFromType(V3ResourceContext resource) {

        if (resource.getResourceType() == V3ResourceContext.ResourceType.ASSET.getValue()) {
            return FacilioConstants.ContextNames.ASSET;
        } else if (resource.getResourceType() == V3ResourceContext.ResourceType.SPACE.getValue()) {
            if ((int) resource.getData().get(FacilioConstants.ContextNames.SPACE_TYPE) == BaseSpaceContext.SpaceType.SPACE.getIntVal()) {
                return FacilioConstants.ContextNames.SPACE;
            } else if ((int) resource.getData().get(FacilioConstants.ContextNames.SPACE_TYPE) == BaseSpaceContext.SpaceType.FLOOR.getIntVal()) {
                return FacilioConstants.ContextNames.FLOOR;
            } else if ((int) resource.getData().get(FacilioConstants.ContextNames.SPACE_TYPE) == BaseSpaceContext.SpaceType.BUILDING.getIntVal()) {
                return FacilioConstants.ContextNames.BUILDING;
            } else if ((int) resource.getData().get(FacilioConstants.ContextNames.SPACE_TYPE) == BaseSpaceContext.SpaceType.SITE.getIntVal()) {
                return FacilioConstants.ContextNames.SITE;
            }
        }
        return null;
    }

    public static List<V3ResourceContext> getChildResources(Long currentResourceId, String currentResourceModuleName, Boolean isDecommission, boolean isFetchToUpdateLog, Long decommissionTime) throws Exception {

        ModuleBean bean = Constants.getModBean();
        FacilioModule resourceModule = bean.getModule(FacilioConstants.ContextNames.RESOURCE);
        FacilioModule baseSpaceModule = bean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
        List<FacilioField> resourceFields = new ArrayList<>();
        resourceFields.addAll(bean.getModuleFields(FacilioConstants.ContextNames.RESOURCE));

        SelectRecordsBuilder<V3ResourceContext> selectBuilder = new SelectRecordsBuilder<V3ResourceContext>()
                .module(resourceModule)
                .select(resourceFields)
                .table(resourceModule.getTableName())
                .beanClass(V3ResourceContext.class);
        if(isFetchToUpdateLog) {
            selectBuilder.andCondition(CriteriaAPI.getCondition("Resources.DECOMMISSIONED_TIME", FacilioConstants.ContextNames.COMMISSION_TIME, decommissionTime + "", DateOperators.IS));
        } else {
            if (isDecommission != null) {
                selectBuilder.andCondition(CriteriaAPI.getCondition("Resources.IS_DECOMMISSIONED", FacilioConstants.ContextNames.DECOMMISSION, isDecommission ? "false" : "true", BooleanOperators.IS));
            }
        }
        if (currentResourceModuleName.equals(FacilioConstants.ContextNames.ASSET)) {
            selectBuilder.andCondition(CriteriaAPI.getIdCondition(currentResourceId, resourceModule));
        } else {
            selectBuilder.innerJoin(baseSpaceModule.getTableName())
                    .on("BaseSpace.ID= Resources.SPACE_ID");
            resourceFields.add(bean.getField(FacilioConstants.ContextNames.SPACE_TYPE, baseSpaceModule.getName()));

            if (currentResourceModuleName.equals(FacilioConstants.ContextNames.SITE)) {
                selectBuilder.andCondition(CriteriaAPI.getCondition("BaseSpace.SITE_ID", FacilioConstants.ContextNames.SITE, String.valueOf(currentResourceId), NumberOperators.EQUALS));
            } else if (currentResourceModuleName.equals(FacilioConstants.ContextNames.BUILDING)) {
                selectBuilder.andCondition(CriteriaAPI.getCondition("BaseSpace.BUILDING_ID", FacilioConstants.ContextNames.BUILDING, String.valueOf(currentResourceId), NumberOperators.EQUALS));
            } else if (currentResourceModuleName.equals(FacilioConstants.ContextNames.FLOOR)) {
                selectBuilder.andCondition(CriteriaAPI.getCondition("BaseSpace.FLOOR_ID", FacilioConstants.ContextNames.FLOOR, String.valueOf(currentResourceId), NumberOperators.EQUALS));
            } else if (currentResourceModuleName.equals(FacilioConstants.ContextNames.SPACE)) {
                selectBuilder.andCondition(CriteriaAPI.getCondition("BaseSpace.SPACE_ID1", FacilioConstants.ContextNames.SPACE_ID1, String.valueOf(currentResourceId), NumberOperators.EQUALS));
                selectBuilder.orCondition(CriteriaAPI.getCondition("BaseSpace.SPACE_ID2", FacilioConstants.ContextNames.SPACE_ID2, String.valueOf(currentResourceId), NumberOperators.EQUALS));
                selectBuilder.orCondition(CriteriaAPI.getCondition("BaseSpace.SPACE_ID3", FacilioConstants.ContextNames.SPACE_ID3, String.valueOf(currentResourceId), NumberOperators.EQUALS));
                selectBuilder.orCondition(CriteriaAPI.getCondition("BaseSpace.SPACE_ID4", FacilioConstants.ContextNames.SPACE_ID4, String.valueOf(currentResourceId), NumberOperators.EQUALS));
                selectBuilder.orCondition(CriteriaAPI.getCondition("BaseSpace.SPACE_ID5", FacilioConstants.ContextNames.SPACE_ID5, String.valueOf(currentResourceId), NumberOperators.EQUALS));
                selectBuilder.orCondition(CriteriaAPI.getCondition("BaseSpace.ID", FacilioConstants.ContextNames.SPACE_ID, String.valueOf(currentResourceId), NumberOperators.EQUALS));
            }
        }

        List<V3ResourceContext> childResourcesData = selectBuilder.get();

        return childResourcesData;
    }

    public static Map<String, Long> getPortfolioDependency(List<V3ResourceContext> dependentResourcesData, String currentResourceModuleName) {
        long assetCount = 0;
        long spaceCount = 0;
        long floorCount = 0;
        long buildingCount = 0;
        long siteCount = 0;
        for (V3ResourceContext resource : dependentResourcesData) {
            if (resource.getResourceType() == V3ResourceContext.ResourceType.ASSET.getValue()) {
                assetCount++;
            } else if (resource.getResourceType() == V3ResourceContext.ResourceType.SPACE.getValue()) {
                if ((int) resource.getData().get(FacilioConstants.ContextNames.SPACE_TYPE) == BaseSpaceContext.SpaceType.SPACE.getIntVal()) {
                    spaceCount++;
                } else if ((int) resource.getData().get(FacilioConstants.ContextNames.SPACE_TYPE) == BaseSpaceContext.SpaceType.FLOOR.getIntVal()) {
                    floorCount++;
                } else if ((int) resource.getData().get(FacilioConstants.ContextNames.SPACE_TYPE) == BaseSpaceContext.SpaceType.BUILDING.getIntVal()) {
                    buildingCount++;
                } else if ((int) resource.getData().get(FacilioConstants.ContextNames.SPACE_TYPE) == BaseSpaceContext.SpaceType.SITE.getIntVal()) {
                    siteCount++;
                }
            }
        }

        Map<String, Long> portfolioDependencies = new LinkedHashMap<>();
        if (siteCount > 0 && !currentResourceModuleName.equals(FacilioConstants.ContextNames.SITE)) {
            portfolioDependencies.put(FacilioConstants.ContextNames.SITE, siteCount);
        }
        if (buildingCount > 0 && !currentResourceModuleName.equals(FacilioConstants.ContextNames.BUILDING)) {
            portfolioDependencies.put(FacilioConstants.ContextNames.BUILDING, buildingCount);
        }
        if (floorCount > 0 && !currentResourceModuleName.equals(FacilioConstants.ContextNames.FLOOR)) {
            portfolioDependencies.put(FacilioConstants.ContextNames.FLOOR, floorCount);
        }
        if (spaceCount > 0) {
            portfolioDependencies.put(FacilioConstants.ContextNames.SPACE, spaceCount);
        }
        if (assetCount > 0 && !currentResourceModuleName.equals(FacilioConstants.ContextNames.ASSET)) {
            portfolioDependencies.put(FacilioConstants.ContextNames.ASSET, assetCount);
        }
        return portfolioDependencies;
    }


    public static Long getTenantsCountOccupiedUnits(List<Long> resourceIds) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule tenantUnitSpaceModule = modBean.getModule(FacilioConstants.ContextNames.TENANT_UNIT_SPACE);
        FacilioModule tenantModule = modBean.getModule(FacilioConstants.ContextNames.TENANT);
        Map<String, FacilioField> tenantFieldMap = new HashMap<>();
        tenantFieldMap.put(FacilioConstants.ContextNames.TENANT_ID, FieldFactory.getCountOfDistinctField(FieldFactory.getIdField(tenantModule), FacilioConstants.ContextNames.TENANT_ID));

        SelectRecordsBuilder builder = new SelectRecordsBuilder()
                .module(tenantModule)
                .select(tenantFieldMap.values())
                .innerJoin(tenantUnitSpaceModule.getTableName())
                .on("Tenants.ID = Tenant_Unit_Space.TENANT_ID")
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(tenantUnitSpaceModule), resourceIds, NumberOperators.EQUALS));
        List<Map<String, Object>> props = builder.getAsProps();
        return CollectionUtils.isNotEmpty(props) ? (Long) props.get(0).get(FacilioConstants.ContextNames.TENANT_ID) : 0;

    }
    public static Long getClientCountOfDependentUnits(Long currentResourceId) throws Exception{
        FacilioModule siteModule = Constants.getModBean().getModule(FacilioConstants.ContextNames.SITE);
        FacilioModule clientPortalModule = Constants.getModBean().getModule(FacilioConstants.ContextNames.CLIENT);
        Map<String, FacilioField> clientFieldMap = new HashMap<>();
        clientFieldMap.put(FacilioConstants.ContextNames.CLIENT_IDS, FieldFactory.getCountOfDistinctField(FieldFactory.getIdField(clientPortalModule), FacilioConstants.ContextNames.CLIENT_IDS));

        SelectRecordsBuilder builder = new SelectRecordsBuilder()
                .module(clientPortalModule)
                .select(clientFieldMap.values())
                .innerJoin(siteModule.getTableName())
                .on("Site.CLIENT_ID = Clients.ID")
                .andCondition(CriteriaAPI.getIdCondition(currentResourceId , siteModule));
        List<Map<String, Object>> props = builder.getAsProps();
        return CollectionUtils.isNotEmpty(props) ? (Long) props.get(0).get(FacilioConstants.ContextNames.CLIENT_IDS) : 0;

    }
    public static List<Long> getClientIdOfDependentUnits(Long currentResourceId) throws Exception{
        FacilioModule siteModule = Constants.getModBean().getModule(FacilioConstants.ContextNames.SITE);
        FacilioModule clientPortalModule = Constants.getModBean().getModule(FacilioConstants.ContextNames.CLIENT);
        List<FacilioField> clientFields =  new ArrayList<>();
        clientFields.add(FieldFactory.getIdField(clientPortalModule));

        SelectRecordsBuilder<V3ClientContext> builder = new SelectRecordsBuilder<V3ClientContext>()
                .module(clientPortalModule)
                .select(clientFields)
                .beanClass(V3ClientContext.class)
                .innerJoin(siteModule.getTableName())
                .on("Site.CLIENT_ID = Clients.ID")
                .andCondition(CriteriaAPI.getIdCondition(currentResourceId , siteModule));
        List<V3ClientContext> props = builder.get();
        List<Long> clientIds = props.stream().map(V3ClientContext::getId).collect(Collectors.toList());
        return clientIds;
    }

    public static Long getDependentResourceWorkorderCount(List<Long> dependentResourceIds, String currentResourceModuleName) throws Exception {
        FacilioModule ticketModule = Constants.getModBean().getModule(FacilioConstants.ContextNames.TICKET);
        SelectRecordsBuilder builder = new SelectRecordsBuilder()
                .module(ticketModule)
                .select(new HashSet<>())
                .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FieldFactory.getIdField(ticketModule))
                .andCondition(CriteriaAPI.getCondition("MODULE_STATE", FacilioConstants.ContextNames.MODULE_STATE, null, CommonOperators.IS_NOT_EMPTY));
        if (currentResourceModuleName.equals(FacilioConstants.ContextNames.SITE)) {
            builder.andCondition(CriteriaAPI.getCondition("SITE_ID", FacilioConstants.ContextNames.SITE, StringUtils.join(dependentResourceIds, ','), NumberOperators.EQUALS));
        } else {
            builder.andCondition(CriteriaAPI.getCondition("RESOURCE_ID", FacilioConstants.ContextNames.ID, StringUtils.join(dependentResourceIds, ','), NumberOperators.EQUALS));
        }
        List<Map<String, Object>> props = builder.getAsProps();
        return CollectionUtils.isNotEmpty(props) ? (Long) props.get(0).get(AgentConstants.ID) : 0;
    }

    public static Long getDependentResourceStoreRoomCount(List<Long> dependentResourceIds, String currentResourceModuleName) throws Exception {
        FacilioModule inventoryModule = Constants.getModBean().getModule(FacilioConstants.ContextNames.STORE_ROOM);
        SelectRecordsBuilder builder = new SelectRecordsBuilder()
                .module(inventoryModule)
                .select(new HashSet<>())
                .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FieldFactory.getIdField(inventoryModule));
        if (currentResourceModuleName.equals(FacilioConstants.ContextNames.SITE)) {
            builder.andCondition(CriteriaAPI.getCondition("SITE_ID", FacilioConstants.ContextNames.SITE, StringUtils.join(dependentResourceIds, ','), NumberOperators.EQUALS));
        } else {
            builder.andCondition(CriteriaAPI.getCondition("RESOURCE_ID", FacilioConstants.ContextNames.ID, StringUtils.join(dependentResourceIds, ','), NumberOperators.EQUALS));
        }
        List<Map<String, Object>> props = builder.getAsProps();
        return CollectionUtils.isNotEmpty(props) ? (Long) props.get(0).get(AgentConstants.ID) : 0;
    }

    public static Long getDependentResourcePMCount(List<Long> dependentResourceIds) throws Exception {
        FacilioModule pmModule = Constants.getModBean().getModule(FacilioConstants.PM_V2.PM_V2_RESOURCE_PLANNER);
        Map<String, FacilioField> pmFieldMap = new HashMap<>();
        pmFieldMap.put(FacilioConstants.ContextNames.PM_ID, FieldFactory.getCountOfDistinctField(FieldFactory.getField(FacilioConstants.ContextNames.PM_ID, "PM_ID", pmModule, FieldType.NUMBER), FacilioConstants.ContextNames.PM_ID));

        SelectRecordsBuilder builder = new SelectRecordsBuilder()
                .module(pmModule)
                .select(pmFieldMap.values())
                .andCondition(CriteriaAPI.getCondition("RESOURCE_ID", FacilioConstants.ContextNames.ID, StringUtils.join(dependentResourceIds, ','), NumberOperators.EQUALS));

        List<Map<String, Object>> props = builder.getAsProps();
        return CollectionUtils.isNotEmpty(props) ? (Long) props.get(0).get(FacilioConstants.ContextNames.PM_ID) : 0;
    }
    public static Long getDependentResourceInductionTemplateCount(List<Long> resourceIds ) throws Exception{
        FacilioModule inductionTemplateModule = Constants.getModBean().getModule(FacilioConstants.Induction.INDUCTION_TEMPLATE);
        SelectRecordsBuilder allSiteInductionsBuilder = new SelectRecordsBuilder()
                .module(inductionTemplateModule)
                .select(new HashSet<>())
                .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FieldFactory.getIdField(inductionTemplateModule))
                .andCondition(CriteriaAPI.getCondition("SITE_APPLY_TO","siteAppliedTo" , "true" ,  BooleanOperators.IS));
        List<Map<String, Object>> props = allSiteInductionsBuilder.getAsProps();
        Long inductionTemplateCount = CollectionUtils.isNotEmpty(props) ? (Long) props.get(0).get(AgentConstants.ID) : 0;

        Map<String, FacilioField> inspFieldMap = new HashMap<>();
        inspFieldMap.put("inductionId", FieldFactory.getCountOfDistinctField(FieldFactory.getIdField(inductionTemplateModule), "inductionId"));


        GenericSelectRecordBuilder specificInductionBuilder = new GenericSelectRecordBuilder()
                .table(inductionTemplateModule.getTableName())
                .select(inspFieldMap.values())
                .innerJoin("Custom_Rel_Records")
                .on("Induction_Templates.ID = Custom_Rel_Records.LEFT_ID")
                .andCondition(CriteriaAPI.getCondition("Custom_Rel_Records.RIGHT_ID",FacilioConstants.ContextNames.RESOURCE,StringUtils.join(resourceIds, ','),NumberOperators.EQUALS));

        props = specificInductionBuilder.get();
        inductionTemplateCount += CollectionUtils.isNotEmpty(props) ? (Long) props.get(0).get("inductionId") : 0;

        return inductionTemplateCount;
    }
    public static Long getDependentResourceInductionCount(List<Long> dependentResourceIds, String currentResourceModuleName) throws Exception {
        FacilioModule inductionModule = Constants.getModBean().getModule(FacilioConstants.Induction.INDUCTION_RESPONSE);
        SelectRecordsBuilder builder = new SelectRecordsBuilder()
                .module(inductionModule)
                .select(new HashSet<>())
                .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FieldFactory.getIdField(inductionModule));
        if (currentResourceModuleName.equals(FacilioConstants.ContextNames.SITE)) {
            builder.andCondition(CriteriaAPI.getCondition("SITE_ID", FacilioConstants.ContextNames.SITE, StringUtils.join(dependentResourceIds, ','), NumberOperators.EQUALS));
        } else {
            builder.andCondition(CriteriaAPI.getCondition("RESOURCE_ID", FacilioConstants.ContextNames.ID, StringUtils.join(dependentResourceIds, ','), NumberOperators.EQUALS));
        }
        List<Map<String, Object>> props = builder.getAsProps();
        return CollectionUtils.isNotEmpty(props) ? (Long) props.get(0).get(AgentConstants.ID) : 0;
    }
//    public static Long getDependentResourceInspectionTemplateCount(List<Long> resourceIds , String moduleName) throws Exception{
//        FacilioModule inspectionTemplateModule = Constants.getModBean().getModule(FacilioConstants.Inspection.INSPECTION_TEMPLATE);
//        SelectRecordsBuilder singleInspectionBuilder = new SelectRecordsBuilder()
//                .module(inspectionTemplateModule)
//                .select(new HashSet<>())
//                .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FieldFactory.getIdField(inspectionTemplateModule))
//                .andCondition(CriteriaAPI.getCondition("CREATION_TYPE","creationType","1",NumberOperators.EQUALS))
//                .andCondition(CriteriaAPI.getCondition("RESOURCE" , FacilioConstants.ContextNames.RESOURCE, StringUtils.join(resourceIds, ','), NumberOperators.EQUALS));
//
//        List<Map<String, Object>> props = singleInspectionBuilder.getAsProps();
//        Long inspectionTemplateCount = CollectionUtils.isNotEmpty(props) ? (Long) props.get(0).get(AgentConstants.ID) : 0;
//
//        Map<String, FacilioField> inspFieldMap = new HashMap<>();
//        inspFieldMap.put("inspectionId", FieldFactory.getCountOfDistinctField(FieldFactory.getIdField(inspectionTemplateModule), "inspectionId"));
//
//        GenericSelectRecordBuilder multiInspectionBuilder = new GenericSelectRecordBuilder()
//                .table(inspectionTemplateModule.getTableName())
//                .select(inspFieldMap.values())
//                .innerJoin("Custom_Rel_Records")
//                .on("Inspection_Templates.ID = Custom_Rel_Records.LEFT_ID")
//                .andCondition(CriteriaAPI.getCondition("Inspection_Templates.CREATION_TYPE","creationType","2",NumberOperators.EQUALS))
//                .andCondition(CriteriaAPI.getCondition("Custom_Rel_Records.RIGHT_ID",FacilioConstants.ContextNames.RESOURCE,StringUtils.join(resourceIds, ','),NumberOperators.EQUALS));
//
//        props = multiInspectionBuilder.get();
//        inspectionTemplateCount += CollectionUtils.isNotEmpty(props) ? (Long) props.get(0).get("inspectionId") : 0;
//
//        return inspectionTemplateCount;
//    }
    public static Long getDependentResourceInspectionCount(List<Long> dependentResourceIds) throws Exception {
        FacilioModule inspectionModule = Constants.getModBean().getModule(FacilioConstants.Inspection.INSPECTION_RESPONSE);
        SelectRecordsBuilder builder = new SelectRecordsBuilder()
                .module(inspectionModule)
                .select(new HashSet<>())
                .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FieldFactory.getIdField(inspectionModule))
                .andCondition(CriteriaAPI.getCondition("RESOURCE", FacilioConstants.ContextNames.RESOURCE, StringUtils.join(dependentResourceIds, ','), NumberOperators.EQUALS));
        List<Map<String, Object>> props = builder.getAsProps();
        return CollectionUtils.isNotEmpty(props) ? (Long) props.get(0).get(AgentConstants.ID) : 0;
    }

    public static Long getDependentResourceServiceRequestCount(List<Long> dependentResourceIds, String currentResourceModuleName) throws Exception {
        FacilioModule srModule = Constants.getModBean().getModule(FacilioConstants.ContextNames.SERVICE_REQUEST);
        SelectRecordsBuilder builder = new SelectRecordsBuilder()
                .module(srModule)
                .select(new HashSet<>())
                .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FieldFactory.getIdField(srModule));
        if (currentResourceModuleName.equals(FacilioConstants.ContextNames.SITE)) {
            builder.andCondition(CriteriaAPI.getCondition("SITE_ID", FacilioConstants.ContextNames.SITE, StringUtils.join(dependentResourceIds, ','), NumberOperators.EQUALS));
        } else {
            builder.andCondition(CriteriaAPI.getCondition("RESOURCE_ID", FacilioConstants.ContextNames.ID, StringUtils.join(dependentResourceIds, ','), NumberOperators.EQUALS));
        }
        List<Map<String, Object>> props = builder.getAsProps();
        return CollectionUtils.isNotEmpty(props) ? (Long) props.get(0).get(AgentConstants.ID) : 0;
    }

    public static Long getDependentSafetyPlansCount(Long siteId) throws Exception {
        FacilioModule safetyPlanModule = Constants.getModBean().getModule(FacilioConstants.ContextNames.SAFETY_PLAN);
        SelectRecordsBuilder builder = new SelectRecordsBuilder()
                .module(safetyPlanModule)
                .select(new HashSet<>())
                .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FieldFactory.getIdField(safetyPlanModule))
                .andCondition(CriteriaAPI.getCondition("SITE_ID", FacilioConstants.ContextNames.SITE, StringUtils.join(Collections.singletonList(siteId), ','), NumberOperators.EQUALS));
        List<Map<String, Object>> props = builder.getAsProps();
        return CollectionUtils.isNotEmpty(props) ? (Long) props.get(0).get(AgentConstants.ID) : 0;
    }

    public static Long getDependentResourceEmailCount(List<Long> dependentResourceIds, long orgId) throws Exception {
        FacilioModule supportEmailsModule = ModuleFactory.getSupportEmailsModule();
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(supportEmailsModule.getTableName())
                .select(new HashSet<>())
                .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FieldFactory.getIdField(supportEmailsModule))
                .andCondition(CriteriaAPI.getCondition("SITE_ID", FacilioConstants.ContextNames.SITE, StringUtils.join(dependentResourceIds, ','), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("ORGID", FacilioConstants.ContextNames.ORGID, String.valueOf(orgId), NumberOperators.EQUALS));
        List<Map<String, Object>> props = builder.get();
        return CollectionUtils.isNotEmpty(props) ? (Long) props.get(0).get(AgentConstants.ID) : 0;
    }

    public static boolean checkParentResourcesIsDecommissioned(long currentResourceId, String currentResourceModuleName) throws Exception {
        ModuleBean bean = Constants.getModBean();
        FacilioModule baseSpaceModule = bean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
        FacilioModule resourceModule = bean.getModule(FacilioConstants.ContextNames.RESOURCE);

        List<FacilioField> resourceFields = new ArrayList<>();
        if (currentResourceModuleName.equals(FacilioConstants.ContextNames.ASSET)) {
            resourceFields.add(FieldFactory.getField(FacilioConstants.ContextNames.SPACE_ID, "SPACE_ID", resourceModule, FieldType.NUMBER));
        } else {
            resourceFields.add(FieldFactory.getField(FacilioConstants.ContextNames.WORK_ORDER_SITE_ID, "SITE_ID", baseSpaceModule, FieldType.NUMBER));
            resourceFields.add(FieldFactory.getField(FacilioConstants.ContextNames.BUILDING_ID, "BUILDING_ID", baseSpaceModule, FieldType.NUMBER));
            resourceFields.add(FieldFactory.getField(FacilioConstants.ContextNames.FLOOR_ID, "FLOOR_ID", baseSpaceModule, FieldType.NUMBER));
            resourceFields.add(FieldFactory.getField(FacilioConstants.ContextNames.SPACE_ID1, "SPACE_ID1", baseSpaceModule, FieldType.NUMBER));
            resourceFields.add(FieldFactory.getField(FacilioConstants.ContextNames.SPACE_ID2, "SPACE_ID2", baseSpaceModule, FieldType.NUMBER));
            resourceFields.add(FieldFactory.getField(FacilioConstants.ContextNames.SPACE_ID3, "SPACE_ID3", baseSpaceModule, FieldType.NUMBER));
            resourceFields.add(FieldFactory.getField(FacilioConstants.ContextNames.SPACE_ID4, "SPACE_ID4", baseSpaceModule, FieldType.NUMBER));
            resourceFields.add(FieldFactory.getField(FacilioConstants.ContextNames.SPACE_ID5, "SPACE_ID5", baseSpaceModule, FieldType.NUMBER));
        }

        SelectRecordsBuilder<V3ResourceContext> selectBuilder = new SelectRecordsBuilder<V3ResourceContext>()
                .select(resourceFields)
                .beanClass(V3ResourceContext.class);

        if (currentResourceModuleName.equals(FacilioConstants.ContextNames.ASSET)) {
            selectBuilder.module(resourceModule)
                    .andCondition(CriteriaAPI.getIdCondition(currentResourceId, resourceModule));
        } else {
            selectBuilder.module(baseSpaceModule)
                    .andCondition(CriteriaAPI.getIdCondition(currentResourceId, baseSpaceModule));
        }

        List<V3ResourceContext> props = selectBuilder.get();
        FacilioUtil.throwIllegalArgumentException(CollectionUtils.isEmpty(props), "No Data found for this Resource");

        List<Long> parentIds = new ArrayList<>();
        if (currentResourceModuleName.equals(FacilioConstants.ContextNames.ASSET)) {
            parentIds.add(props.get(0).getSpaceId());
        } else {
            parentIds = props.get(0).getData().values().stream().map(obj -> (Long) obj).collect(Collectors.toList());
            parentIds.add(props.get(0).getSiteId());
        }
        parentIds.removeIf(parentId -> parentId == currentResourceId);
        if (CollectionUtils.isEmpty(parentIds)) {
            return false;
        }
        resourceFields = new ArrayList<>();
        resourceFields.add(FieldFactory.getField(FacilioConstants.ContextNames.DECOMMISSION, "IS_DECOMMISSIONED", resourceModule, FieldType.BOOLEAN));

        selectBuilder = new SelectRecordsBuilder<V3ResourceContext>()
                .select(resourceFields)
                .module(resourceModule)
                .beanClass(V3ResourceContext.class)
                .andCondition(CriteriaAPI.getIdCondition(parentIds, resourceModule));

        props = selectBuilder.get();
        FacilioUtil.throwIllegalArgumentException(CollectionUtils.isEmpty(props), "No Data found for the parents of this resource");
        for (V3ResourceContext resource : props) {
            if (resource.getDecommission()) {
                return true;
            }
        }

        return false;
    }

    public static DecommissionLogContext getDecommissionLogForResource(String resourceModuleName, Long resourceId, boolean isDecommission, Long decommissionTime, String remarks) throws Exception {
        ModuleBean modBean = Constants.getModBean();

        List<V3ResourceContext> childResourceData = DecommissionUtil.getChildResources(resourceId, resourceModuleName, isDecommission, true, decommissionTime);

        List<Long> childResourceDataIds = childResourceData.stream().map(V3ResourceContext::getId).collect(Collectors.toList());
        ;

        Map<String, Long> portfolioDependency = DecommissionUtil.getPortfolioDependency(childResourceData, resourceModuleName);

        List<Object> dependentModuleList = new ArrayList<>();


        if (resourceModuleName.equals(FacilioConstants.ContextNames.SITE)) {
            FacilioModule inductionTemplateModule = modBean.getModule(FacilioConstants.Induction.INDUCTION_TEMPLATE);
            Long inductionTemplateCount = getDependentResourceInductionTemplateCount(Collections.singletonList(resourceId));
            if(inductionTemplateCount > 0) {
                dependentModuleList.add(DecommissionUtil.constructResultJSON(inductionTemplateModule , inductionTemplateCount , FacilioConstants.DecommissionModuleType.WARNING));
            }
        }

        FacilioModule pmPlannerModule = modBean.getModule(FacilioConstants.PM_V2.PM_V2_RESOURCE_PLANNER);
        Long pmPlannerCount = DecommissionUtil.getDependentResourcePMCount(childResourceDataIds);
        if (pmPlannerCount > 0) {
            dependentModuleList.add(DecommissionUtil.constructResultJSON(pmPlannerModule, pmPlannerCount, FacilioConstants.DecommissionModuleType.WARNING));
        }


        JSONObject dependentResourceDataObject = new JSONObject();
        dependentResourceDataObject.put(FacilioConstants.ContextNames.MODULE_LIST, dependentModuleList);
        dependentResourceDataObject.put(FacilioConstants.ContextNames.RESOURCE, portfolioDependency);
        String logValue = dependentResourceDataObject.toJSONString();

        DecommissionLogContext decommissionLog = new DecommissionLogContext();
        decommissionLog.setDecommission(isDecommission);
        decommissionLog.setResourceId(resourceId);
        decommissionLog.setCommissionedBy(AccountUtil.getCurrentUser());
        decommissionLog.setLogValue(logValue);
        decommissionLog.setRemarks(remarks);
        decommissionLog.setCommissionedTime(decommissionTime);
        return decommissionLog;
    }

    public static void sendDecommissionLogsToWms(List<V3ResourceContext> resources, DecommissionContext decommissionContext, long decommissionTime) {
        JSONObject json = new JSONObject();
        long orgId = AccountUtil.getCurrentOrg().getOrgId();

        json.put(FacilioConstants.ContextNames.DECOMMISSION, decommissionContext.getDecommission());
        json.put(FacilioConstants.ContextNames.COMMISSION_TIME, decommissionTime);
        json.put(FacilioConstants.ContextNames.REMARKS, decommissionContext.getRemarkValue());
        for (V3ResourceContext resource : resources) {
            String resourceModuleName = DecommissionUtil.getResourceModuleNameFromType(resource);
            Long resourceId = resource.getId();
            json.put("resourceId", resourceId);
            json.put(FacilioConstants.ContextNames.RESOURCE_MODULENAME, resourceModuleName);
            SessionManager.getInstance().sendMessage(new Message()
                    .setTopic(CommissioningLogHandler.TOPIC)
                    .setOrgId(orgId)
                    .setContent(json)
            );
        }
    }

    public static void sendResourcesToWms(List<Long> resourceIds, boolean decommission) {

        JSONObject json = new JSONObject();
        long orgId = AccountUtil.getCurrentOrg().getOrgId();

        json.put(FacilioConstants.ContextNames.DECOMMISSION, decommission);
        for (Long id : resourceIds) {
            json.put("resourceId", id);
            SessionManager.getInstance().sendMessage(new Message()
                    .setTopic(ResourceDecommissioningHandler.TOPIC)
                    .setOrgId(orgId)
                    .setContent(json)
            );
        }
    }

    public static void updateRecommissionButtonVisibility(List<Long> resourceIds, boolean isToEnableButton) throws Exception {
        FacilioModule resourceModule = Constants.getModBean().getModule(FacilioConstants.ContextNames.RESOURCE);
        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getField(FacilioConstants.ContextNames.VIEW_RECOMMISSION_BTN, "VIEW_RECOMMISSION_BTN", ModuleFactory.getResourceModule(), FieldType.BOOLEAN));

        UpdateRecordBuilder<V3ResourceContext> builder = new UpdateRecordBuilder<V3ResourceContext>()
                .module(resourceModule)
                .fields(fields)
                .table(resourceModule.getTableName())
                .andCondition(CriteriaAPI.getCondition("Resources.ID", "id", StringUtils.join(resourceIds, ','), NumberOperators.EQUALS));
        Map<String, Object> props = new HashMap<>();
        props.put(FacilioConstants.ContextNames.VIEW_RECOMMISSION_BTN, isToEnableButton ? true : null);
        builder.update(FieldUtil.getAsBeanFromMap(props, V3ResourceContext.class));

    }

    public static List<Long> getTenantsIdsOccupiedUnits(List<Long> resourceIds) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule tenantModule = modBean.getModule(FacilioConstants.ContextNames.TENANT);
        FacilioModule tenantUnitSpaceModule = modBean.getModule(FacilioConstants.ContextNames.TENANT_UNIT_SPACE);
        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getIdField(tenantModule));
        SelectRecordsBuilder<V3TenantContext> builder = new SelectRecordsBuilder<V3TenantContext>()
                .module(tenantModule)
                .beanClass(V3TenantContext.class)
                .select(fields)
                .innerJoin(tenantUnitSpaceModule.getTableName())
                .on("Tenants.ID = Tenant_Unit_Space.TENANT_ID")
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(tenantUnitSpaceModule), resourceIds, NumberOperators.EQUALS))
                .groupBy("Tenants.ID");
        List<V3TenantContext> props = builder.get();
        List<Long> tenantsIds = props.stream().map(V3TenantContext::getId).collect(Collectors.toList());
        return tenantsIds;
    }
}
