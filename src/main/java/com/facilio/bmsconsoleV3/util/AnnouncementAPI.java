package com.facilio.bmsconsoleV3.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.FieldPermissionContext;
import com.facilio.bmsconsoleV3.context.*;
import com.facilio.bmsconsoleV3.context.announcement.AnnouncementContext;
import com.facilio.bmsconsoleV3.context.announcement.AnnouncementSharingInfoContext;
import com.facilio.bmsconsoleV3.context.announcement.PeopleAnnouncementContext;
import com.facilio.bmsconsoleV3.context.quotation.QuotationContext;
import com.facilio.bmsconsoleV3.context.quotation.QuotationLineItemsContext;
import com.facilio.bmsconsoleV3.interfaces.BuildingTenantContacts;
import com.facilio.bmsconsoleV3.interfaces.SiteTenantContacts;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.tasker.FacilioTimer;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.V3Context;
import com.facilio.v3.util.ChainUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.lang.reflect.Field;
import java.util.*;

public class AnnouncementAPI {

    public static List<V3TenantContext> getBuildingTenants(Long buildingId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TENANT);
        Map<String, FacilioField> tenantFieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));
        SelectRecordsBuilder<V3TenantContext> builder = new SelectRecordsBuilder<V3TenantContext>()
                .module(module)
                .beanClass(V3TenantContext.class)
                .select(modBean.getAllFields(module.getName()))
                .andCondition(CriteriaAPI.getCondition(tenantFieldMap.get("building"), String.valueOf(buildingId), PickListOperators.IS))
                ;

        return builder.get();
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

    public static List<AnnouncementSharingInfoContext> getSharingInfo(Long id) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ANNOUNCEMENTS_SHARING_INFO);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));

        SelectRecordsBuilder<AnnouncementSharingInfoContext> builder = new SelectRecordsBuilder<AnnouncementSharingInfoContext>()
                .module(module)
                .beanClass(AnnouncementSharingInfoContext.class)
                .select(modBean.getAllFields(module.getName()))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("announcement"), String.valueOf(id), NumberOperators.EQUALS))
                ;

        return builder.get();
    }

    private static List<PeopleAnnouncementContext> getChildAnnouncementList(Long parentId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PEOPLE_ANNOUNCEMENTS);
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
                FacilioChain updateChain = ChainUtil.getUpdateChain(FacilioConstants.ContextNames.PEOPLE_ANNOUNCEMENTS);
                FacilioContext context = updateChain.getContext();
                context.put(Constants.RECORD_ID, peopleAnnouncement.getId());
                Constants.setModuleName(context, FacilioConstants.ContextNames.PEOPLE_ANNOUNCEMENTS);
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

    public static void addAnnouncementPeople(AnnouncementContext announcement) throws Exception{

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PEOPLE_ANNOUNCEMENTS);
        announcement.setSharingInfo(getSharingInfo(announcement.getId()));

        if(CollectionUtils.isNotEmpty(announcement.getSharingInfo())) {
            Map<Long, PeopleAnnouncementContext> pplMap = new HashMap<>();
            for(AnnouncementSharingInfoContext sharingInfo : announcement.getSharingInfo()){
                List<V3PeopleContext> ppl = new ArrayList<>();
                //handling only for building sharing type for now.. can be supported for others as well
                if(sharingInfo.getSharingTypeEnum() == AnnouncementSharingInfoContext.SharingType.BUILDING) {
                     ppl = new BuildingTenantContacts().getPeople(sharingInfo.getSharedToSpace() != null ? sharingInfo.getSharedToSpace().getId() : null);
                }
                if(sharingInfo.getSharingTypeEnum() == AnnouncementSharingInfoContext.SharingType.SITE) {
                    ppl = new SiteTenantContacts().getPeople(sharingInfo.getSharedToSpace() != null ? sharingInfo.getSharedToSpace().getId() : null);
                }

                if(CollectionUtils.isNotEmpty(ppl)){
                    for(V3PeopleContext person : ppl) {
                        PeopleAnnouncementContext pplAnnouncement = FieldUtil.cloneBean(announcement, PeopleAnnouncementContext.class);
                        pplAnnouncement.setIsRead(false);
                        pplAnnouncement.setPeople(person);
                        pplAnnouncement.setParentId(announcement.getId());
                        pplMap.put(person.getId(), pplAnnouncement);
                    }
                }
            }
            if(MapUtils.isNotEmpty(pplMap)) {
                V3RecordAPI.addRecord(true, new ArrayList<PeopleAnnouncementContext>(pplMap.values()), module, modBean.getAllFields(FacilioConstants.ContextNames.PEOPLE_ANNOUNCEMENTS));
            }
        }

    }

    public static void setSharingInfo(AnnouncementContext announcement) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String sharingModName = FacilioConstants.ContextNames.ANNOUNCEMENTS_SHARING_INFO;
        List<FacilioField> fields = modBean.getAllFields(sharingModName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        List<LookupField> fetchSupplementsList = Arrays.asList((LookupField) fieldsAsMap.get("sharedToSpace"));

        SelectRecordsBuilder<AnnouncementSharingInfoContext> builder = new SelectRecordsBuilder<AnnouncementSharingInfoContext>()
                .moduleName(sharingModName)
                .select(fields)
                .beanClass(AnnouncementSharingInfoContext.class)
                .fetchSupplements(fetchSupplementsList)
                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("announcement"), String.valueOf(announcement.getId()), NumberOperators.EQUALS));
        List<AnnouncementSharingInfoContext> list = builder.get();
        if (CollectionUtils.isNotEmpty(list)) {
            announcement.setSharingInfo(list);
        }
    }
}
