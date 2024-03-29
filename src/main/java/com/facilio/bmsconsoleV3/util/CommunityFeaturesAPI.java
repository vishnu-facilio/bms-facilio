package com.facilio.bmsconsoleV3.util;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.FieldPermissionContext;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.context.TenantUnitSpaceContext;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.bmsconsoleV3.context.CommunitySharingInfoContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.V3TenantContactContext;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.AudienceContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.AudienceSharingInfoContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.ContactDirectoryContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.announcement.AnnouncementContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.announcement.AnnouncementSharingInfoContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.announcement.PeopleAnnouncementContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.V3FacilityBookingContext;
import com.facilio.bmsconsoleV3.interfaces.BuildingTenantContacts;
import com.facilio.bmsconsoleV3.interfaces.SiteTenantContacts;
import com.facilio.bmsconsoleV3.interfaces.TenantUnitTenantContacts;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.*;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.V3Context;
import com.facilio.v3.util.ChainUtil;
import com.facilio.v3.util.V3Util;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.logging.log4j.util.Strings;

import java.util.*;
import java.util.stream.Collectors;

public class CommunityFeaturesAPI {

    private static final Logger LOGGER = org.apache.log4j.Logger.getLogger(CommunityFeaturesAPI.class);

    public static List<Long> getBuildingTenants(List<Long> buildingId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TENANT_UNIT_SPACE);
        Map<String, FacilioField> tenantUnitFieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));
        SelectRecordsBuilder<TenantUnitSpaceContext> builder = new SelectRecordsBuilder<TenantUnitSpaceContext>()
                .module(module)
                .beanClass(TenantUnitSpaceContext.class)
                .select(modBean.getAllFields(module.getName()))
                .andCondition(CriteriaAPI.getCondition(tenantUnitFieldMap.get("building"), Strings.join(buildingId, ','), PickListOperators.IS))
                .andCondition(CriteriaAPI.getCondition(tenantUnitFieldMap.get("isOccupied"), "true", BooleanOperators.IS))
                ;

        List<TenantUnitSpaceContext> tenantUnits = builder.get();
        if(CollectionUtils.isNotEmpty(tenantUnits)){
            List<Long> tenantIds = new ArrayList<>();
            for(TenantUnitSpaceContext tu : tenantUnits){
                if(tu.getTenant() != null) {
                    tenantIds.add(tu.getTenant().getId());
                }
            }
            return tenantIds;
        }
        return null;
    }

    public static List<Long> getBuildingTenants(Long buildingId) throws Exception {
       return getBuildingTenants(Collections.singletonList(buildingId));
    }

    public static List<V3TenantContext> getSiteTenants(Long siteId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TENANT);
        Map<String, FacilioField> tenantFieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));
        SelectRecordsBuilder<V3TenantContext> builder = new SelectRecordsBuilder<V3TenantContext>()
                .module(module)
                .beanClass(V3TenantContext.class)
                .select(modBean.getAllFields(module.getName()))
                .andCondition(CriteriaAPI.getCondition(tenantFieldMap.get("siteId"), String.valueOf(siteId), NumberOperators.EQUALS))
                ;

        return builder.get();
    }

    public static List<AudienceContext> getAudienceListForAnnouncement(Long id) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule announcementModule = modBean.getModule(FacilioConstants.ContextNames.Tenant.ANNOUNCEMENT);
        if(id != null) {
            Collection<SupplementRecord> lookUpfields = new ArrayList<>();
            List<FacilioField> announcementFields = modBean.getAllFields(announcementModule.getName());
            Map<String, FacilioField> announcementFieldMap = FieldFactory.getAsMap(announcementFields);
            lookUpfields.add((LookupField) announcementFieldMap.get("audience"));
            List<AnnouncementContext> announcements = V3RecordAPI.getRecordsListWithSupplements(announcementModule.getName(), Collections.singletonList(id), AnnouncementContext.class, lookUpfields);
            if(CollectionUtils.isNotEmpty(announcements)){
                return announcements.get(0).getAudience();
            }
        }
        return null;
    }

    public static List<AudienceContext> getAudienceForAnnouncement(Long id) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule announcementModule = modBean.getModule(FacilioConstants.ContextNames.Tenant.ANNOUNCEMENT);
        if(id != null) {
            Collection<SupplementRecord> lookUpfields = new ArrayList<>();
            List<FacilioField> announcementFields = modBean.getAllFields(announcementModule.getName());
            Map<String, FacilioField> announcementFieldMap = FieldFactory.getAsMap(announcementFields);
            lookUpfields.add((MultiLookupField) announcementFieldMap.get("audience"));
            List<AnnouncementContext> announcements = V3RecordAPI.getRecordsListWithSupplements(announcementModule.getName(), Collections.singletonList(id), AnnouncementContext.class, lookUpfields);
            List<Long> audienceIds = new ArrayList<>();
            if(CollectionUtils.isNotEmpty(announcements)){
                if(announcements.get(0) != null){
                    List<AudienceContext> audiences = announcements.get(0).getAudience();
                    if(CollectionUtils.isNotEmpty(audiences)){
                        for(AudienceContext audience : audiences){
                            audienceIds.add(audience.getId());
                        }
                        audiences = V3RecordAPI.getRecordsList(FacilioConstants.ContextNames.AUDIENCE,audienceIds,AudienceContext.class);
                        return audiences;
                    }
                }
            }
        }
        return null;
    }

    public static List<CommunitySharingInfoContext> getSharingInfo(Long id) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.Tenant.ANNOUNCEMENTS_SHARING_INFO);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));

        SelectRecordsBuilder<CommunitySharingInfoContext> builder = new SelectRecordsBuilder<CommunitySharingInfoContext>()
                .module(module)
                .beanClass(CommunitySharingInfoContext.class)
                .select(modBean.getAllFields(module.getName()))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("announcement"), String.valueOf(id), NumberOperators.EQUALS))
                ;

        return builder.get();
    }

    private static List<PeopleAnnouncementContext> getChildAnnouncementList(Long parentId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.Tenant.PEOPLE_ANNOUNCEMENTS);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));

        SelectRecordsBuilder<PeopleAnnouncementContext> builder = new SelectRecordsBuilder<PeopleAnnouncementContext>()
                .module(module)
                .beanClass(PeopleAnnouncementContext.class)
                .select(modBean.getAllFields(module.getName()))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), String.valueOf(parentId), NumberOperators.EQUALS))
                ;

        return builder.get();
    }
    public static Long getParentAnnouncementId(long peopleAnnouncementId) throws Exception {
        PeopleAnnouncementContext peopleAnnouncement=(PeopleAnnouncementContext)V3RecordAPI.getRecord(FacilioConstants.ContextNames.PEOPLE_ANNOUNCEMENTS,peopleAnnouncementId,PeopleAnnouncementContext.class);
        Long parentAnnouncementId = peopleAnnouncement.getParentId();
        return parentAnnouncementId;
    }

    public static void cancelChildAnnouncements(AnnouncementContext parentAnnouncement) throws Exception {
        List<PeopleAnnouncementContext> childAnnouncements = getChildAnnouncementList(parentAnnouncement.getId());
        if(CollectionUtils.isNotEmpty(childAnnouncements)) {
            //can be changed to batch update once the support comes in v3
            for(PeopleAnnouncementContext peopleAnnouncement : childAnnouncements) {
                FacilioChain updateChain = ChainUtil.getUpdateChain(FacilioConstants.ContextNames.Tenant.PEOPLE_ANNOUNCEMENTS);
                FacilioContext context = updateChain.getContext();
                context.put(Constants.RECORD_ID, peopleAnnouncement.getId());
                Constants.setModuleName(context, FacilioConstants.ContextNames.Tenant.PEOPLE_ANNOUNCEMENTS);
                peopleAnnouncement.setIsCancelled(true);
                //peopleAnnouncement.setModuleState(parentAnnouncement.getModuleState());
                //peopleAnnouncement.setApprovalStatus(parentAnnouncement.getApprovalStatus());
                Constants.setRawInput(context, FieldUtil.getAsJSON(peopleAnnouncement));
                context.put(Constants.BEAN_CLASS, PeopleAnnouncementContext.class);
                context.put(FacilioConstants.ContextNames.PERMISSION_TYPE, FieldPermissionContext.PermissionType.READ_WRITE);
                updateChain.execute();
            }
        }
    }

    public static void deleteChildAnnouncements(List<Long> recordIds) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PEOPLE_ANNOUNCEMENTS);
        DeleteRecordBuilder<PeopleAnnouncementContext> deleteBuilder = new DeleteRecordBuilder<PeopleAnnouncementContext>()
                .module(module)
                .andCondition(CriteriaAPI.getCondition("PARENT_ANNOUNCEMENT_ID", "parentId", StringUtils.join(recordIds, ","), NumberOperators.EQUALS));
        deleteBuilder.markAsDelete();

    }

    public static List<V3PeopleContext> getAudienceSharing(List<Long> audienceIds,Long page,Long perPage) throws Exception {
        long skipCount = perPage * (page - 1);
        long count = 0,recCount = 0;
        List<AudienceContext> audiences = V3RecordAPI.getRecordsList(FacilioConstants.ContextNames.AUDIENCE,audienceIds,AudienceContext.class);
        Long filterSharingType = null;
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<V3PeopleContext> ppl = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(audiences)){
            for(AudienceContext audience : audiences){
                audience.setAudienceSharing(setAudienceSharingInfo(audience));
                filterSharingType = audience.getFilterSharingType();
                if(CollectionUtils.isNotEmpty(audience.getAudienceSharing())) {
                    List<CommunitySharingInfoContext> sharingInfoList = new ArrayList<>();
                    List<Long> sharedToRoleIds = new ArrayList<>();
                    //special handling for including roles filter to spaces (tenant units and buildings)
                    if (filterSharingType != null && filterSharingType.equals(2l)) {
                        sharedToRoleIds = getSharedToRoleIds(audience.getAudienceSharing());
                        sharingInfoList = getSharedToSpaceInfo(audience.getAudienceSharing());
                    } else {
                        sharingInfoList = audience.getAudienceSharing();
                    }
                    if (CollectionUtils.isNotEmpty(sharingInfoList)) {
                        for (CommunitySharingInfoContext sharingInfo : sharingInfoList) {
                            //Tenant Unit
                            if (sharingInfo.getSharingTypeEnum() == AnnouncementSharingInfoContext.SharingType.TENANT_UNIT) {
                                List<V3PeopleContext> people = new TenantUnitTenantContacts().getPeople(sharingInfo.getSharedToSpace() != null ? sharingInfo.getSharedToSpace().getId() : null, filterSharingType, CollectionUtils.isNotEmpty(sharedToRoleIds) ? sharedToRoleIds : null);
                                if (CollectionUtils.isNotEmpty(people)) {
                                    ppl.addAll(people);
                                }
                            }

                            //Building
                            if (sharingInfo.getSharingTypeEnum() == AnnouncementSharingInfoContext.SharingType.BUILDING) {
                                List<V3PeopleContext> people = new BuildingTenantContacts().getPeople(sharingInfo.getSharedToSpace() != null ? sharingInfo.getSharedToSpace().getId() : null, filterSharingType, CollectionUtils.isNotEmpty(sharedToRoleIds) ? sharedToRoleIds : null);
                                if (CollectionUtils.isNotEmpty(people)) {
                                    ppl.addAll(people);
                                }
                            }

                            //Role
                            if (sharingInfo.getSharingTypeEnum() == AnnouncementSharingInfoContext.SharingType.ROLE) {
                                List<Map<String, Object>> pplForRoleMap = new ArrayList<>();
                                if (sharingInfo.getSharedToRole() != null) {
                                    pplForRoleMap = V3PeopleAPI.getPeopleForRoles(Collections.singletonList(sharingInfo.getSharedToRole().getRoleId()));
                                } else {
                                    List<Long> portalAppIds = new ArrayList<Long>();
                                    portalAppIds.add(ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP));
                                    portalAppIds.add(ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP));
                                    portalAppIds.add(ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP));
                                    List<Role> roles = AccountUtil.getRoleBean().getRolesForApps(portalAppIds);
                                    if (CollectionUtils.isNotEmpty(roles)) {
                                        pplForRoleMap = V3PeopleAPI.getPeopleForRoles(roles.stream().map(role -> role.getId()).collect(Collectors.toList()));
                                    }
                                }
                                if (CollectionUtils.isNotEmpty(pplForRoleMap)) {
                                    ppl.addAll(FieldUtil.getAsBeanListFromMapList(pplForRoleMap, V3PeopleContext.class));
                                }
                            }

                            //People
                            if (sharingInfo.getSharingTypeEnum() == AnnouncementSharingInfoContext.SharingType.PEOPLE) {
                                if (sharingInfo.getSharedToPeople() != null) {
                                    ppl.add(V3PeopleAPI.getPeopleById(sharingInfo.getSharedToPeople().getId()));
                                } else {
                                    List<V3PeopleContext.PeopleType> peopleTypeIds = new ArrayList<V3PeopleContext.PeopleType>();
                                    peopleTypeIds.add(V3PeopleContext.PeopleType.TENANT_CONTACT);
                                    peopleTypeIds.add(V3PeopleContext.PeopleType.VENDOR_CONTACT);
                                    ppl.addAll(V3PeopleAPI.getPeopleByPeopleTypes(peopleTypeIds));
                                }
                            }
                        }
                    }
                }
            }
        }

        List<Long> pplIds = new ArrayList<>();
        List<V3PeopleContext> people = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(ppl)) {
            for (V3PeopleContext person : ppl) {
                if(person == null){
                    continue;
                }
                count++;
                if(count > skipCount && recCount < perPage) {
                    recCount++;
                    if (!pplIds.contains(person.getId())) {
                        pplIds.add(person.getId());
                        V3PeopleContext fetchedPeople = V3RecordAPI.getRecord(FacilioConstants.ContextNames.PEOPLE, person.getId(), V3PeopleContext.class);
                        people.add(fetchedPeople);
                    }
                }
            }
        }
        if (CollectionUtils.isNotEmpty(people)) {
            return people;
        }
        return null;
    }

    public static Map<Long, PeopleAnnouncementContext> getAnnouncementPeople(AnnouncementContext announcement) throws Exception {
        Long filterSharingType = null;
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.Tenant.PEOPLE_ANNOUNCEMENTS);
        //only if is needed..so condition can be removed once the client supports audience
        List<AudienceContext> audiences = getAudienceForAnnouncement(announcement.getId());
        List<V3PeopleContext> ppl = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(audiences)){
            for(AudienceContext audience : audiences){
                announcement.setAnnouncementsharing(setAudienceSharingInfo(audience));
                filterSharingType = audience.getFilterSharingType();
                if(CollectionUtils.isNotEmpty(announcement.getAnnouncementsharing())) {
                    List<CommunitySharingInfoContext> sharingInfoList = new ArrayList<>();
                    List<Long> sharedToRoleIds = new ArrayList<>();
                    //special handling for including roles filter to spaces (tenant units and buildings)
                    if (filterSharingType != null && filterSharingType.equals(2l)) {
                        sharedToRoleIds = getSharedToRoleIds(announcement.getAnnouncementsharing());
                        sharingInfoList = getSharedToSpaceInfo(announcement.getAnnouncementsharing());
                    } else {
                        sharingInfoList = announcement.getAnnouncementsharing();
                    }
                    if (CollectionUtils.isNotEmpty(sharingInfoList)) {
                        for (CommunitySharingInfoContext sharingInfo : sharingInfoList) {
                            //Tenant Unit
                            if (sharingInfo.getSharingTypeEnum() == AnnouncementSharingInfoContext.SharingType.TENANT_UNIT) {
                                List<V3PeopleContext> people = new TenantUnitTenantContacts().getPeople(sharingInfo.getSharedToSpace() != null ? sharingInfo.getSharedToSpace().getId() : null, filterSharingType, CollectionUtils.isNotEmpty(sharedToRoleIds) ? sharedToRoleIds : null);
                                if (CollectionUtils.isNotEmpty(people)) {
                                    ppl.addAll(people);
                                }
                            }

                            //Building
                            if (sharingInfo.getSharingTypeEnum() == AnnouncementSharingInfoContext.SharingType.BUILDING) {
                                List<V3PeopleContext> people = new BuildingTenantContacts().getPeople(sharingInfo.getSharedToSpace() != null ? sharingInfo.getSharedToSpace().getId() : null, filterSharingType, CollectionUtils.isNotEmpty(sharedToRoleIds) ? sharedToRoleIds : null);
                                if (CollectionUtils.isNotEmpty(people)) {
                                    ppl.addAll(people);
                                }
                            }

                            //Role
                            if (sharingInfo.getSharingTypeEnum() == AnnouncementSharingInfoContext.SharingType.ROLE) {
                                List<Map<String, Object>> pplForRoleMap = new ArrayList<>();
                                if (sharingInfo.getSharedToRole() != null) {
                                    pplForRoleMap = V3PeopleAPI.getPeopleForRoles(Collections.singletonList(sharingInfo.getSharedToRole().getRoleId()));
                                } else {
                                    List<Long> portalAppIds = new ArrayList<Long>();
                                    portalAppIds.add(ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP));
                                    portalAppIds.add(ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP));
                                    portalAppIds.add(ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP));
                                    List<Role> roles = AccountUtil.getRoleBean().getRolesForApps(portalAppIds);
                                    if (CollectionUtils.isNotEmpty(roles)) {
                                        pplForRoleMap = V3PeopleAPI.getPeopleForRoles(roles.stream().map(role -> role.getId()).collect(Collectors.toList()));
                                    }
                                }
                                if (CollectionUtils.isNotEmpty(pplForRoleMap)) {
                                    ppl.addAll(FieldUtil.getAsBeanListFromMapList(pplForRoleMap, V3PeopleContext.class));
                                }
                            }

                            //People
                            if (sharingInfo.getSharingTypeEnum() == AnnouncementSharingInfoContext.SharingType.PEOPLE) {
                                if (sharingInfo.getSharedToPeople() != null) {
                                    ppl.add(V3PeopleAPI.getPeopleById(sharingInfo.getSharedToPeople().getId()));
                                } else {
                                    List<V3PeopleContext.PeopleType> peopleTypeIds = new ArrayList<V3PeopleContext.PeopleType>();
                                    peopleTypeIds.add(V3PeopleContext.PeopleType.TENANT_CONTACT);
                                    peopleTypeIds.add(V3PeopleContext.PeopleType.VENDOR_CONTACT);
                                    ppl.addAll(V3PeopleAPI.getPeopleByPeopleTypes(peopleTypeIds));
                                }
                            }
                        }
                    }
                }
            }
        }

        Map<Long, PeopleAnnouncementContext> pplMap = new HashMap<>();
        List<Long> pplIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(ppl)) {
            announcement = getAnnouncementById(announcement.getId());
            for (V3PeopleContext person : ppl) {
                if(person == null){
                    continue;
                }
                if(!pplIds.contains(person.getId())) {
                    pplIds.add(person.getId());
                    PeopleAnnouncementContext pplAnnouncement = FieldUtil.cloneBean(announcement, PeopleAnnouncementContext.class);
                    pplAnnouncement.setAudience(null);
                    pplAnnouncement.setIsRead(false);
                    pplAnnouncement.setPeople(person);
                    pplAnnouncement.setParentId(announcement.getId());
                    pplAnnouncement.setCreatedBy(announcement.getSysModifiedBy());
                    pplAnnouncement.setCreatedTime(announcement.getSysModifiedTime());
                    pplAnnouncement.setLongDescription(announcement.getLongDescription());
                    pplAnnouncement.setStateFlowId(-1);
                    pplAnnouncement.setModuleState(null);
                    pplMap.put(person.getId(), pplAnnouncement);
                }
            }
        }
        if (MapUtils.isNotEmpty(pplMap)) {
            return pplMap;
        }
        return null;
    }
    public static void addAnnouncementPeople(AnnouncementContext announcement) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Map<Long, PeopleAnnouncementContext> pplMap = getAnnouncementPeople(announcement);
        if (MapUtils.isNotEmpty(pplMap)) {
            V3Util.createRecordList(modBean.getModule(FacilioConstants.ContextNames.PEOPLE_ANNOUNCEMENTS), FieldUtil.getAsJSONArray(new ArrayList<PeopleAnnouncementContext>(pplMap.values()),PeopleAnnouncementContext.class),null,null);
        }
    }

    public static List<? extends CommunitySharingInfoContext> getSharingInfo(V3Context record, String sharingModuleName, String parentFieldName) throws Exception {
        return getSharingInfo(Collections.singletonList(record),sharingModuleName,parentFieldName);
    }

    public static List<String> getAudiencePeopleEmails(List<Long> audienceIds) throws Exception {
        if(CollectionUtils.isNotEmpty(audienceIds)){
            List<V3PeopleContext> peopleList = getAudienceSharing(audienceIds,1l,Long.MAX_VALUE);
            if(CollectionUtils.isNotEmpty(peopleList)) {
                return peopleList.stream().map(V3PeopleContext::getEmail).collect(Collectors.toList());
            }
        }
        return null;
    }
    public static List<? extends CommunitySharingInfoContext> getSharingInfo(List<V3Context> records, String sharingModuleName, String parentFieldName) throws Exception {
        if(CollectionUtils.isNotEmpty(records)) {
            List<Long> ids = records.stream().map(V3Context::getId).collect(Collectors.toList());
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(sharingModuleName);
            List<FacilioField> fields = modBean.getAllFields(sharingModuleName);
            Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
            List<LookupField> fetchSupplementsList = Arrays.asList((LookupField) fieldsAsMap.get("sharedToSpace"));

            V3Config config = ChainUtil.getV3Config(sharingModuleName);
            Class beanClass = ChainUtil.getBeanClass(config, module);


            SelectRecordsBuilder<? extends CommunitySharingInfoContext> builder = new SelectRecordsBuilder<AnnouncementSharingInfoContext>()
                    .moduleName(sharingModuleName)
                    .select(fields)
                    .beanClass(beanClass)
                    .fetchSupplements(fetchSupplementsList)
                    .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get(parentFieldName), StringUtils.join(ids,","), NumberOperators.EQUALS));
            List<? extends CommunitySharingInfoContext> list = builder.get();
            return list;
        }
        return null;
    }

    public static List<CommunitySharingInfoContext> setAudienceSharingInfo(AudienceContext audience) throws Exception {
        return setAudienceSharingInfo(Collections.singletonList(audience));
    }
        public static List<CommunitySharingInfoContext> setAudienceSharingInfo(List<AudienceContext> audienceList) throws Exception {

        if(CollectionUtils.isNotEmpty(audienceList)) {
            List<Long> ids = audienceList.stream().map(AudienceContext::getId).collect(Collectors.toList());
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.Tenant.AUDIENCE_SHARING);
            Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

            LookupFieldMeta sharedToSpaceField = new LookupFieldMeta((LookupField) fieldsAsMap.get("sharedToSpace"));

            LookupField siteField = (LookupField) modBean.getField("site", FacilioConstants.ContextNames.SITE);
            LookupField buildingField = (LookupField) modBean.getField("building", FacilioConstants.ContextNames.BUILDING);
            LookupField floorField = (LookupField) modBean.getField("floor", FacilioConstants.ContextNames.FLOOR);

            sharedToSpaceField.addChildSupplement(siteField);
            sharedToSpaceField.addChildSupplement(buildingField);
            sharedToSpaceField.addChildSupplement(floorField);

            List<LookupField> fetchSupplementsList = Arrays.asList(sharedToSpaceField, (LookupField) fieldsAsMap.get("sharedToPeople"));

            SelectRecordsBuilder<CommunitySharingInfoContext> builder = new SelectRecordsBuilder<CommunitySharingInfoContext>()
                    .moduleName(FacilioConstants.ContextNames.Tenant.AUDIENCE_SHARING)
                    .select(fields)
                    .beanClass(CommunitySharingInfoContext.class)
                    .fetchSupplements(fetchSupplementsList)
                    .andCondition(CriteriaAPI.getCondition("AUDIENCE_ID", "audienceId", StringUtils.join(ids,","), NumberOperators.EQUALS));
            List<CommunitySharingInfoContext> list = builder.get();

            for (CommunitySharingInfoContext sharing : list) {
                if (sharing.getSharedToRoleId() != null && sharing.getSharedToRoleId() > 0) {
                    sharing.setSharedToRole(AccountUtil.getRoleBean().getRole(sharing.getSharedToRoleId()));
                }
            }

            return list;
        }
        return null;
    }


    public static void addAudience(AudienceContext audience) throws Exception {

        audience.setId(-1);
        if(AccountUtil.getCurrentSiteId() != -1){
            audience.setSiteId(AccountUtil.getCurrentSiteId());
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.Tenant.AUDIENCE);
        V3RecordAPI.addRecord(false, Collections.singletonList(audience), module, modBean.getAllFields(FacilioConstants.ContextNames.Tenant.AUDIENCE));

        List<AudienceSharingInfoContext> audienceSharingList = new ArrayList<>();
        for(CommunitySharingInfoContext sharing : audience.getAudienceSharing()){
            AudienceSharingInfoContext audienceSharing = new AudienceSharingInfoContext();
            audienceSharing.setAudienceId(audience);
            audienceSharing.setSharingType(sharing.getSharingType());
            audienceSharing.setSharedToSpace(sharing.getSharedToSpace());
            audienceSharing.setSharedToRoleId(sharing.getSharedToRoleId());
            audienceSharing.setSharedToRole(sharing.getSharedToRole());
            audienceSharing.setSharedToPeople(sharing.getSharedToPeople());
            audienceSharingList.add(audienceSharing);
        }

        FacilioModule sharingInfoModule = modBean.getModule(FacilioConstants.ContextNames.Tenant.AUDIENCE_SHARING);
        V3RecordAPI.addRecord(false, audienceSharingList, sharingInfoModule, modBean.getAllFields(FacilioConstants.ContextNames.Tenant.AUDIENCE_SHARING));

    }

    public static List<ContactDirectoryContext> getContacts(Long pplId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.Tenant.CONTACT_DIRECTORY);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));
        SelectRecordsBuilder<ContactDirectoryContext> builder = new SelectRecordsBuilder<ContactDirectoryContext>()
                .module(module)
                .beanClass(ContactDirectoryContext.class)
                .select(modBean.getAllFields(module.getName()))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("people"), String.valueOf(pplId), PickListOperators.IS))
                ;

        return builder.get();
    }

    public static void updateContactDirectoryList(List<Long> ids, PeopleContext people) throws Exception {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.Tenant.CONTACT_DIRECTORY);
            List<FacilioField> fields = modBean.getAllFields(module.getName());
            Map<String, FacilioField> contactFieldMap = FieldFactory.getAsMap(fields);
            List<FacilioField> updatedfields = new ArrayList<FacilioField>();

            FacilioField primaryEmailField = contactFieldMap.get("contactEmail");
            FacilioField primaryPhoneField = contactFieldMap.get("contactPhone");
            FacilioField primaryNameField = contactFieldMap.get("contactName");

            updatedfields.add(primaryEmailField);
            updatedfields.add(primaryPhoneField);
            updatedfields.add(primaryNameField);

            GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                    .table(module.getTableName())
                    .fields(updatedfields)
                    .andCondition(CriteriaAPI.getIdCondition(ids, module));

            Map<String, Object> value = new HashMap<>();
            value.put("contactEmail", people.getEmail());
            value.put("contactPhone", people.getPhone());
            value.put("contactName", people.getName());
            updateBuilder.update(value);

    }

    public static void updateContactDirectoryList(List<Long> ids, V3PeopleContext people) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.Tenant.CONTACT_DIRECTORY);
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        Map<String, FacilioField> contactFieldMap = FieldFactory.getAsMap(fields);
        List<FacilioField> updatedfields = new ArrayList<FacilioField>();

        FacilioField primaryEmailField = contactFieldMap.get("contactEmail");
        FacilioField primaryPhoneField = contactFieldMap.get("contactPhone");
        FacilioField primaryNameField = contactFieldMap.get("contactName");

        updatedfields.add(primaryEmailField);
        updatedfields.add(primaryPhoneField);
        updatedfields.add(primaryNameField);

        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                .table(module.getTableName())
                .fields(updatedfields)
                .andCondition(CriteriaAPI.getIdCondition(ids, module));

        Map<String, Object> value = new HashMap<>();
        value.put("contactEmail", people.getEmail());
        value.put("contactPhone", people.getPhone());
        value.put("contactName", people.getName());
        updateBuilder.update(value);

    }

    public static AnnouncementContext getAnnouncementById(long id) throws  Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ANNOUNCEMENT);

        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        List<SupplementRecord> supplements = new ArrayList<SupplementRecord>();
        supplements.add((LargeTextField)fieldsAsMap.get("longDescription"));

        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ANNOUNCEMENT);
        SelectRecordsBuilder<AnnouncementContext> builder = new SelectRecordsBuilder<AnnouncementContext>()
                .module(module)
                .beanClass(AnnouncementContext.class)
                .select(modBean.getAllFields(module.getName()))
                .fetchSupplements(supplements)
                .andCondition(CriteriaAPI.getIdCondition(id,module));

        return builder.fetchFirst();
    }

    private static List<Long> getSharedToRoleIds(List<CommunitySharingInfoContext> communitySharingInfo){
        List<Long> ids = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(communitySharingInfo)) {
            for (CommunitySharingInfoContext sharingInfo : communitySharingInfo) {
                if(sharingInfo.getSharedToRole() != null){
                    ids.add(sharingInfo.getSharedToRole().getId());
                }
                else if(sharingInfo.getSharedToRoleId() != null) {
                    ids.add(sharingInfo.getSharedToRoleId());
                }
            }
        }
        if(CollectionUtils.isNotEmpty(ids)){
            return ids;
        }
        return null;
    }

    private static List<CommunitySharingInfoContext> getSharedToSpaceInfo(List<CommunitySharingInfoContext> communitySharingInfo){
        List<CommunitySharingInfoContext> spaceSharingInfo = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(communitySharingInfo)) {
            for (CommunitySharingInfoContext sharingInfo : communitySharingInfo) {
                if(sharingInfo.getSharedToSpace() != null){
                    spaceSharingInfo.add(sharingInfo);
                }
            }
        }
        if(CollectionUtils.isNotEmpty(spaceSharingInfo)){
            return spaceSharingInfo;
        }
        return null;
    }
}
