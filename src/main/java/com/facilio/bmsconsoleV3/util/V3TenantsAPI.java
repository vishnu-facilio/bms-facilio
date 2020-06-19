package com.facilio.bmsconsoleV3.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.tenant.TenantSpaceContext;
import com.facilio.bmsconsole.tenant.UtilityAsset;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.ContactsAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.bmsconsoleV3.context.V3ContactsContext;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.context.V3TenantSpaceContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BuildingOperator;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

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
        List<V3TenantSpaceContext> tenantSpaces = getTenantSpaces(spaceIds, true);
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

    private static List<V3TenantSpaceContext> getTenantSpaces(Collection<Long> spaceIds, boolean fetchChildTenants) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TENANT_SPACES);
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        Map<String, FacilioField> tenantSpaceFieldMap = FieldFactory.getAsMap(fields);
        Operator operator = fetchChildTenants ? BuildingOperator.BUILDING_IS : NumberOperators.EQUALS;
        SelectRecordsBuilder<V3TenantSpaceContext> builder = new SelectRecordsBuilder<V3TenantSpaceContext>()
                .module(module)
                .beanClass(V3TenantSpaceContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition(tenantSpaceFieldMap.get("space"), spaceIds, operator))
                .fetchSupplement((LookupField)tenantSpaceFieldMap.get("tenant"))
                ;

        return builder.get();
    }

    public static V3TenantContext getTenantForResource(long resourceId) throws Exception{
        Map<Long, V3TenantContext> tenantMap = getTenantForResources(Collections.singletonList(resourceId));
        if (MapUtils.isNotEmpty(tenantMap)) {
            return tenantMap.get(resourceId);
        }
        return null;
    }

    public static V3TenantContext getTenantForSpace(long spaceId) throws Exception{
        Map<Long, V3TenantContext> spaceTenant = getTenantForSpace(Collections.singletonList(spaceId));
        if (spaceTenant != null) {
            return spaceTenant.get(spaceId);
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
        emptyTenant.setId(-99L);
        empty.put(-1L, emptyTenant);

        if (filteredSpaces.isEmpty()) {
            return empty;
        }
        List<V3TenantSpaceContext> tenantSpaces = getTenantSpaces(spaceIds, false);
        if (tenantSpaces != null && !tenantSpaces.isEmpty()) {
            return tenantSpaces.stream().collect(Collectors.toMap(tenantSpace -> tenantSpace.getSpace().getId(), V3TenantSpaceContext::getTenant));
        }
        return empty;
    }

}
