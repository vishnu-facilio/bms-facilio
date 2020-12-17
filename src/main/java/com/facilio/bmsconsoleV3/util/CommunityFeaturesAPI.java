package com.facilio.bmsconsoleV3.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.FieldPermissionContext;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.context.TenantUnitSpaceContext;
import com.facilio.bmsconsoleV3.context.CommunitySharingInfoContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.AudienceContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.ContactDirectoryContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.announcement.AnnouncementContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.announcement.AnnouncementSharingInfoContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.announcement.PeopleAnnouncementContext;
import com.facilio.bmsconsoleV3.interfaces.BuildingTenantContacts;
import com.facilio.bmsconsoleV3.interfaces.SiteTenantContacts;
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
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.V3Context;
import com.facilio.v3.util.ChainUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class CommunityFeaturesAPI {

    public static List<Long> getBuildingTenants(Long buildingId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TENANT_UNIT_SPACE);
        Map<String, FacilioField> tenantUnitFieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));
        SelectRecordsBuilder<TenantUnitSpaceContext> builder = new SelectRecordsBuilder<TenantUnitSpaceContext>()
                .module(module)
                .beanClass(TenantUnitSpaceContext.class)
                .select(modBean.getAllFields(module.getName()))
                .andCondition(CriteriaAPI.getCondition(tenantUnitFieldMap.get("building"), String.valueOf(buildingId), PickListOperators.IS))
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
                peopleAnnouncement.setModuleState(parentAnnouncement.getModuleState());
                peopleAnnouncement.setApprovalStatus(parentAnnouncement.getApprovalStatus());
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

    public static void addAnnouncementPeople(AnnouncementContext announcement) throws Exception{

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.Tenant.PEOPLE_ANNOUNCEMENTS);
        //only if is needed..so condition can be removed once the client supports audience
        if(announcement.getAudience() != null) {
           announcement.setAnnouncementsharing(setAudienceSharingInfo(announcement.getAudience()));
        }
        else {
            announcement.setAnnouncementsharing(getSharingInfo(announcement.getId()));
        }


        if(CollectionUtils.isNotEmpty(announcement.getAnnouncementsharing())) {
            Map<Long, PeopleAnnouncementContext> pplMap = new HashMap<>();
            for(CommunitySharingInfoContext sharingInfo : announcement.getAnnouncementsharing()){
                List<V3PeopleContext> ppl = new ArrayList<>();
                //handling only for building sharing type for now.. can be supported for others as well
                if(sharingInfo.getSharingTypeEnum() == AnnouncementSharingInfoContext.SharingType.BUILDING) {
                     ppl = new BuildingTenantContacts().getPeople(sharingInfo.getSharedToSpace() != null ? sharingInfo.getSharedToSpace().getId() : null);
                }
                if(sharingInfo.getSharingTypeEnum() == AnnouncementSharingInfoContext.SharingType.ALL_SITES || sharingInfo.getSharingTypeEnum() == AnnouncementSharingInfoContext.SharingType.SITE) {
                    ppl = new SiteTenantContacts().getPeople(sharingInfo.getSharedToSpace() != null ? sharingInfo.getSharedToSpace().getId() : null);
                }

                if(CollectionUtils.isNotEmpty(ppl)){
                    for(V3PeopleContext person : ppl) {
                        PeopleAnnouncementContext pplAnnouncement = FieldUtil.cloneBean(announcement, PeopleAnnouncementContext.class);
                        pplAnnouncement.setAudience(null);
                        pplAnnouncement.setIsRead(false);
                        pplAnnouncement.setPeople(person);
                        pplAnnouncement.setParentId(announcement.getId());
                        pplMap.put(person.getId(), pplAnnouncement);
                    }
                }
            }
            if(MapUtils.isNotEmpty(pplMap)) {
                V3RecordAPI.addRecord(true, new ArrayList<PeopleAnnouncementContext>(pplMap.values()), module, modBean.getAllFields(FacilioConstants.ContextNames.Tenant.PEOPLE_ANNOUNCEMENTS));
            }
        }

    }

    public static List<? extends CommunitySharingInfoContext> getSharingInfo(V3Context record, String sharingModuleName, String parentFieldName) throws Exception {

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
                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get(parentFieldName), String.valueOf(record.getId()), NumberOperators.EQUALS));
        List<? extends CommunitySharingInfoContext> list = builder.get();
        return list;

    }

    public static List<CommunitySharingInfoContext> setAudienceSharingInfo(AudienceContext audience) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.Tenant.AUDIENCE_SHARING);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        List<LookupField> fetchSupplementsList = Arrays.asList((LookupField) fieldsAsMap.get("sharedToSpace"));

        SelectRecordsBuilder<CommunitySharingInfoContext> builder = new SelectRecordsBuilder<CommunitySharingInfoContext>()
                .moduleName(FacilioConstants.ContextNames.Tenant.AUDIENCE_SHARING)
                .select(fields)
                .beanClass(CommunitySharingInfoContext.class)
                .fetchSupplements(fetchSupplementsList)
                .andCondition(CriteriaAPI.getCondition("AUDIENCE_ID", "audienceId", String.valueOf(audience.getId()), NumberOperators.EQUALS));
        List<CommunitySharingInfoContext> list = builder.get();
        return list;

    }


    public static void addAudience(AudienceContext audience) throws Exception {

        audience.setId(-1);
        if(AccountUtil.getCurrentSiteId() != -1){
            audience.setSiteId(AccountUtil.getCurrentSiteId());
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.Tenant.AUDIENCE);
        V3RecordAPI.addRecord(false, Collections.singletonList(audience), module, modBean.getAllFields(FacilioConstants.ContextNames.Tenant.AUDIENCE));

        for(CommunitySharingInfoContext sharing : audience.getAudienceSharing()){
            sharing.setAudienceId(audience.getId());
        }

        FacilioModule sharingInfoModule = modBean.getModule(FacilioConstants.ContextNames.Tenant.AUDIENCE_SHARING);
        V3RecordAPI.addRecord(false, audience.getAudienceSharing(), sharingInfoModule, modBean.getAllFields(FacilioConstants.ContextNames.Tenant.AUDIENCE_SHARING));

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

}
