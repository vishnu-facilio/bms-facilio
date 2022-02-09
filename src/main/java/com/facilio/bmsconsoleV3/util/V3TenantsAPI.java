package com.facilio.bmsconsoleV3.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.ZoneContext;
import com.facilio.bmsconsole.tenant.UtilityAsset;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsoleV3.context.V3ContactsContext;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.context.V3TenantSpaceContext;
import com.facilio.bmsconsoleV3.context.V3TenantUnitSpaceContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.BuildingOperator;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;

public class V3TenantsAPI {

    public static List<BaseSpaceContext> fetchTenantSpaces(long tenantId, boolean fetchSpaceDetails) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TENANT_SPACES);
        Map<String, FacilioField> tenantSpaceFieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));
        SelectRecordsBuilder<V3TenantSpaceContext> builder = new SelectRecordsBuilder<V3TenantSpaceContext>()
                .module(module)
                .beanClass(V3TenantSpaceContext.class)
                .select(Collections.singletonList(tenantSpaceFieldMap.get("space")))
                .andCondition(CriteriaAPI.getCondition(tenantSpaceFieldMap.get("tenant"), String.valueOf(tenantId), NumberOperators.EQUALS))
                ;

        if (fetchSpaceDetails) {
            builder.fetchSupplement((LookupField)tenantSpaceFieldMap.get("space"));
        }

        List<V3TenantSpaceContext> tenantSpaces = builder.get();
        if (tenantSpaces != null && !tenantSpaces.isEmpty()) {
            return tenantSpaces.stream().map(V3TenantSpaceContext::getSpace).collect(Collectors.toList());
        }
        return null;
    }
    public static List<V3TenantSpaceContext> fetchTenantSpacesAssociation(long tenantId, boolean fetchSpaceDetails, String orderType) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TENANT_SPACES);
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        Map<String, FacilioField> tenantSpaceFieldMap = FieldFactory.getAsMap(fields);
        
        Collection<SupplementRecord> supplements = null;
        if (fetchSpaceDetails) {
        	supplements = Collections.singletonList((LookupField)tenantSpaceFieldMap.get("space"));
        }
        
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(tenantSpaceFieldMap.get("tenant"), String.valueOf(tenantId), NumberOperators.EQUALS));
        
        List<V3TenantSpaceContext> tenantUnits = V3RecordAPI.getRecordsListWithSupplements(module.getName(), null, V3TenantSpaceContext.class, criteria, supplements, tenantSpaceFieldMap.get("disassociatedTime").getColumnName(), orderType);

        if (CollectionUtils.isNotEmpty(tenantUnits)) {
            return tenantUnits;
        }
        return null;
    }
    public static void loadTenantLookups(Collection<? extends V3TenantContext> tenants) throws Exception {
        loadTenantZones(tenants);
        loadTenantProps(tenants);

    }

    private static void loadTenantZones(Collection<? extends V3TenantContext> tenants) throws Exception {
        if(tenants != null && !tenants.isEmpty()) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule zoneModule = modBean.getModule(FacilioConstants.ContextNames.ZONE);
            List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ZONE);

            SelectRecordsBuilder<ZoneContext> selectBuilder = new SelectRecordsBuilder<ZoneContext>()
                    .select(fields)
                    .table(zoneModule.getTableName())
                    .moduleName(zoneModule.getName())
                    .beanClass(ZoneContext.class)
//																				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(zoneModule))
                    ;
            Map<Long, ZoneContext> zones = selectBuilder.getAsMap();

            for(V3TenantContext tenant : tenants) {
                if (tenant != null) {
                    ZoneContext zone = tenant.getZone();
                    if(zone != null) {
                        tenant.setZone(zones.get(zone.getId()));
                    }
                }
            }

        }
    }

    private static void loadTenantProps(Collection<? extends V3TenantContext> tenants) throws Exception {
        try {
            List<Long> ids = new ArrayList<>();
            for (V3TenantContext tenant : tenants) {
                ids.add(tenant.getId());
                if (tenant.getLogoId()  != null) {
                    FileStore fs = FacilioFactory.getFileStore();
                    tenant.setLogoUrl(fs.getPrivateUrl(tenant.getLogoId()));
                }
            }
            Map<Long, List<UtilityAsset>> utilMap = getUtilityAssets(ids);
            if (utilMap != null && !utilMap.isEmpty()) {
                for (V3TenantContext tenant : tenants) {
                    tenant.setUtilityAssets(utilMap.get(tenant.getId()));
                }
            }
        }
        catch(Exception e)
        {
            throw e;
        }
    }

    private static Map<Long, List<UtilityAsset>> getUtilityAssets(Collection<Long> ids) throws Exception {
        if (CollectionUtils.isEmpty(ids)) {
            return null;
        }
        FacilioModule module = ModuleFactory.getTenantsUtilityMappingModule();
        List<FacilioField> fields = FieldFactory.getTenantsUtilityMappingFields();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        FacilioField tenantId = fieldMap.get("tenantId");

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(fields)
//													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
                .andCondition(CriteriaAPI.getCondition(tenantId, ids, PickListOperators.IS))
                ;
        List<Map<String,Object>> props = selectBuilder.get();

        if (props != null && !props.isEmpty()) {
            Map<Long, List<UtilityAsset>> utilityMap = new HashMap<>();
            for (Map<String, Object> prop : props) {
                UtilityAsset util = FieldUtil.getAsBeanFromMap(prop, UtilityAsset.class);
                AssetContext assetInfo = AssetsAPI.getAssetInfo(util.getAssetId());
                util.setAsset(assetInfo);
                List<UtilityAsset> utilList = utilityMap.get(util.getTenantId());
                if (utilList == null) {
                    utilList = new ArrayList<>();
                    utilityMap.put(util.getTenantId(), utilList);
                }
                utilList.add(util);
            }
            return utilityMap;
        }
        return null;
    }

    public static List<BaseSpaceContext> fetchTenantSpaces(long tenantId) throws Exception {
        return fetchTenantSpaces(tenantId, true);
    }

    public static List<V3ContactsContext> getTenantContacts(long id) throws Exception {
        List<Map<String,Object>> contactList = V3ContactsAPI.getTenantContacts(Collections.singletonList(id));
        return FieldUtil.getAsBeanListFromMapList(contactList, V3ContactsContext.class);
    }

    public static V3TenantContext getTenant(long id) throws Exception {

        if (id <= 0) {
            return null;
        }

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> tenantFields = modBean.getAllFields(FacilioConstants.ContextNames.TENANT);
        List<LookupField> lookupFields = new ArrayList<>();
        for (FacilioField f : tenantFields) {
            if (f instanceof LookupField) {
                lookupFields.add((LookupField) f);
            }
        }
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TENANT);
        SelectRecordsBuilder<V3TenantContext> builder = new SelectRecordsBuilder<V3TenantContext>()
                .module(module)
                .beanClass(V3TenantContext.class)
                .select(tenantFields)
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
                .andCondition(CriteriaAPI.getIdCondition(id, module));
        if (CollectionUtils.isNotEmpty(lookupFields)) {
            builder.fetchSupplements(lookupFields);
        }

        return builder.fetchFirst();
    }

    public static List<V3TenantContext> getAllTenantsForSpace(Collection<Long> spaceIds) throws Exception {
        List<V3TenantSpaceContext> tenantSpaces = getTenantSpaces(spaceIds);
        if (tenantSpaces != null && !tenantSpaces.isEmpty()) {
            return tenantSpaces.stream().map(tenantSpace -> tenantSpace.getTenant()).collect(Collectors.toList());
        }
        return null;
    }

    public static List<V3TenantContext> getAllTenantsForResource(long resourceId) throws Exception {
        ResourceContext resource = ResourceAPI.getResource(resourceId);
        long spaceId = resource.getResourceTypeEnum() == ResourceContext.ResourceType.ASSET ? resource.getSpaceId() : resource.getId();
        return getAllTenantsForSpace(Collections.singletonList(spaceId));
    }

    private static List<V3TenantSpaceContext> getTenantSpaces(Collection<Long> spaceIds) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TENANT_SPACES);
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        Map<String, FacilioField> tenantSpaceFieldMap = FieldFactory.getAsMap(fields);
        SelectRecordsBuilder<V3TenantSpaceContext> builder = new SelectRecordsBuilder<V3TenantSpaceContext>()
                .module(module)
                .beanClass(V3TenantSpaceContext.class)
                .select(fields)
                .fetchSupplement((LookupField)tenantSpaceFieldMap.get("tenant"))
                ;
        
        Set<Long> baseSpaceParentIds = SpaceAPI.getBaseSpaceParentIds(spaceIds);
		Criteria criteria = new Criteria();
		criteria.addOrCondition(CriteriaAPI.getCondition(tenantSpaceFieldMap.get("space"), spaceIds, BuildingOperator.BUILDING_IS));
		if (CollectionUtils.isNotEmpty(baseSpaceParentIds)) {
			criteria.addOrCondition(CriteriaAPI.getCondition(tenantSpaceFieldMap.get("space"), baseSpaceParentIds, NumberOperators.EQUALS));
		}
		builder.andCriteria(criteria);

        return builder.get();
    }

    public static V3TenantContext getTenantForResource(long resourceId) throws Exception{
        Map<Long, V3TenantContext> tenantMap = getTenantForResources(Collections.singletonList(resourceId));
        if (MapUtils.isNotEmpty(tenantMap)) {
            return tenantMap.get(resourceId);
        }
        return null;
    }

    public static Map<Long, V3TenantContext> getTenantForResources(List<Long> resourceIds) throws Exception{
        List<ResourceContext> resources = ResourceAPI.getResources(resourceIds, false);
        List<Long> spaceIds = new ArrayList<>();
        for(ResourceContext resource: resources) {
            long spaceId = resource.getResourceTypeEnum() == ResourceContext.ResourceType.ASSET ? resource.getSpaceId() : resource.getId();
            if (spaceId != -1) {
                spaceIds.add(spaceId);
            }
        }
        Map<Long, V3TenantContext> spaceTenants = V3TenantsAPI.getTenantForSpace(spaceIds);
        if (MapUtils.isNotEmpty(spaceTenants)) {
            Map<Long, V3TenantContext> tenants = new HashMap<>();
            for(ResourceContext resource: resources) {
                long spaceId = resource.getResourceTypeEnum() == ResourceContext.ResourceType.ASSET ? resource.getSpaceId() : resource.getId();
                if (spaceId != -1 && spaceTenants.containsKey(spaceId)) {
                    tenants.put(resource.getId(), spaceTenants.get(spaceId));
                }
            }
            return tenants;
        }
        return null;
    }

    public static Map<Long, V3TenantContext> getTenantForSpace(Collection<Long> spaceIds) throws Exception {
        List<Long> filteredSpaces = spaceIds.stream().filter(i -> i > 0).collect(Collectors.toList());
        Map<Long, V3TenantContext> empty = new HashMap<>();
        V3TenantContext emptyTenant = new V3TenantContext();
        emptyTenant.setId(-99);
        empty.put(-1L, emptyTenant);

        if (filteredSpaces.isEmpty()) {
            return empty;
        }
        List<V3TenantUnitSpaceContext> tenantSpaces = getOccupiedTenantUnits(spaceIds);
        if (tenantSpaces != null && !tenantSpaces.isEmpty()) {
            return tenantSpaces.stream().collect(Collectors.toMap(tenantSpace -> tenantSpace.getSpace().getId(), V3TenantUnitSpaceContext::getTenant));
        }
        return empty;
    }
    
    private static List<V3TenantUnitSpaceContext> getOccupiedTenantUnits(Collection<Long> spaceIds) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(ContextNames.TENANT_UNIT_SPACE);
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		SelectRecordsBuilder<V3TenantUnitSpaceContext> builder = new SelectRecordsBuilder<V3TenantUnitSpaceContext>()
				.module(module)
				.beanClass(V3TenantUnitSpaceContext.class)
				.select(fields)
				.andCondition(CriteriaAPI.getIdCondition(spaceIds, module))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("isOccupied"), "true", BooleanOperators.IS))
				;
		return builder.get();

	}

}
