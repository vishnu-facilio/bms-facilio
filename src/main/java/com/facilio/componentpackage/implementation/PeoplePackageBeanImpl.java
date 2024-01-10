package com.facilio.componentpackage.implementation;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.componentpackage.constants.ComponentType;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.componentpackage.utils.PackageBeanUtil;
import com.facilio.componentpackage.utils.PackageUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.v3.util.ChainUtil;
import org.apache.commons.collections4.CollectionUtils;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import com.facilio.bmsconsoleV3.context.*;
import com.facilio.accounts.bean.RoleBean;
import com.facilio.xml.builder.XMLBuilder;
import com.facilio.v3.context.Constants;
import com.facilio.chain.FacilioContext;
import com.facilio.chain.FacilioChain;
import com.facilio.accounts.dto.Role;
import com.facilio.beans.ModuleBean;
import com.facilio.v3.util.V3Util;
import lombok.extern.log4j.Log4j;
import com.facilio.modules.*;

import java.util.*;
import java.util.stream.Collectors;
import com.facilio.fw.BeanFactory;

@Log4j
public class PeoplePackageBeanImpl implements PackageBean<V3PeopleContextExtendedProps> {
    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        Map<Long, Long> peopleIdMap = new HashMap<>();
        Map<Long, String> peopleIdVsPeopleMail = new HashMap<>();

        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule peopleModule = moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE);

        List<V3PeopleContext> activePeople = (List<V3PeopleContext>) getActivePeople(peopleModule, V3PeopleContext.class);
        List<V3PeopleContext> inActivePeople = (List<V3PeopleContext>) getDeletedPeople(peopleModule, V3PeopleContext.class);

        addPeopleConfigForXML(peopleIdMap, peopleIdVsPeopleMail, activePeople);
        addPeopleConfigForXML(peopleIdMap, peopleIdVsPeopleMail, inActivePeople);

        return peopleIdMap;
    }

    @Override
    public Map<Long, V3PeopleContextExtendedProps> fetchComponents(List<Long> ids) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule peopleModule = moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE);
        List<V3PeopleContext> peopleContexts = (List<V3PeopleContext>) PackageBeanUtil.getModuleDataListsForIds(ids, peopleModule, V3PeopleContext.class, true);

        Map<Long, V3TenantContext> tenantContexts = getRecordProps(FacilioConstants.ContextNames.TENANT, V3TenantContext.class);
        Map<Long, V3VendorContext> vendorContexts = getRecordProps(FacilioConstants.ContextNames.VENDORS, V3VendorContext.class);
        Map<Long, V3ClientContext> clientContexts = getRecordProps(FacilioConstants.ContextNames.CLIENT, V3ClientContext.class);
        Map<Long, V3TenantContactContext> tenantContactContexts = getRecordProps(FacilioConstants.ContextNames.TENANT_CONTACT, V3TenantContactContext.class);
        Map<Long, V3VendorContactContext> vendorContactContexts = getRecordProps(FacilioConstants.ContextNames.VENDOR_CONTACT, V3VendorContactContext.class);
        Map<Long, V3ClientContactContext> clientContactContexts = getRecordProps(FacilioConstants.ContextNames.CLIENT_CONTACT, V3ClientContactContext.class);

        Map<Long, V3PeopleContextExtendedProps> peopleIdVsPeople = new HashMap<>();
        if (CollectionUtils.isNotEmpty(peopleContexts)) {
            List<Map<String, Object>> mapList = FieldUtil.getAsMapList(peopleContexts, V3PeopleContext.class);
            List<V3PeopleContextExtendedProps> peopleContextExtendedProps = FieldUtil.getAsBeanListFromMapList(mapList, V3PeopleContextExtendedProps.class);
            for (V3PeopleContextExtendedProps people : peopleContextExtendedProps) {
                V3PeopleContext.PeopleType peopleTypeEnum = people.getPeopleTypeEnum();
                if (peopleTypeEnum != null) {
                    switch (peopleTypeEnum) {
                        case TENANT_CONTACT:
                            V3TenantContactContext tenantContactContext = tenantContactContexts.get(people.getId());
                            if (tenantContactContext != null) {
                                V3TenantContext tenant = tenantContactContext.getTenant();
                                if (tenant != null) {
                                    V3TenantContext tenantContext = tenantContexts.get(tenant.getId());
                                    people.setSubProp(tenantContactContext);
                                    people.setBaseProp(tenantContext);
                                }
                            }
                            break;

                        case VENDOR_CONTACT:
                            V3VendorContactContext vendorContactContext = vendorContactContexts.get(people.getId());
                            if (vendorContactContext != null) {
                                V3VendorContext vendor = vendorContactContext.getVendor();
                                if (vendor != null) {
                                    V3VendorContext vendorContext = vendorContexts.get(vendor.getId());
                                    people.setSubProp(vendorContactContext);
                                    people.setBaseProp(vendorContext);
                                }
                            }
                            break;

                        case CLIENT_CONTACT:
                            V3ClientContactContext clientContactContext = clientContactContexts.get(people.getId());
                            if (clientContactContext != null) {
                                V3ClientContext client = clientContactContext.getClient();
                                if (client != null) {
                                    V3ClientContext clientContext = clientContexts.get(client.getId());
                                    people.setSubProp(clientContactContext);
                                    people.setBaseProp(clientContext);
                                }
                            }
                            break;

                        default:
                            break;
                    }
                }

                if (peopleTypeEnum != null && PEOPLE_TYPE_LIST.contains(peopleTypeEnum)
                        && (people.getBaseProp() == null || people.getSubProp() == null)) {
                    LOGGER.info("####Sandbox Tracking - Improper People Info - " + people.getId() + " Name - " + people.getName());
                    continue;
                }

                peopleIdVsPeople.put(people.getId(), people);
            }
        }
        return peopleIdVsPeople;
    }

    @Override
    public void convertToXMLComponent(V3PeopleContextExtendedProps people, XMLBuilder element) throws Exception {
        V3PeopleContext.PeopleType peopleTypeEnum = people.getPeopleTypeEnum();
        peopleTypeEnum = peopleTypeEnum != null ? peopleTypeEnum : V3PeopleContext.PeopleType.OTHERS;
        peopleTypeEnum = (people.getSysDeletedTime() < 0) ? peopleTypeEnum : V3PeopleContext.PeopleType.OTHERS;

        element.element(PackageConstants.UserConstants.USER_NAME).text(people.getName());
        element.element(PackageConstants.UserConstants.EMAIL).text(people.getEmail());
        element.element(PackageConstants.UserConstants.PHONE).text(people.getPhone());
        element.element(PackageConstants.UserConstants.MOBILE).text(people.getMobile());
        element.element(PackageConstants.UserConstants.LANGUAGE).text(people.getLanguage());
        element.element(PackageConstants.UserConstants.TIMEZONE).text(people.getTimezone());
        element.element(PackageConstants.UserConstants.PEOPLE_TYPE).text(peopleTypeEnum.name());
        element.element(PackageConstants.UserConstants.IS_USER).text(String.valueOf(people.isUser()));
        element.element(PackageConstants.UserConstants.OCCUPANT_PORTAL_ACCESS).text(String.valueOf(people.isOccupantPortalAccess()));
        element.element(PackageConstants.UserConstants.EMPLOYEE_PORTAL_ACCESS).text(String.valueOf(people.isEmployeePortalAccess()));
        element.element(PackageConstants.UserConstants.DELETED_TIME).text(String.valueOf(people.getSysDeletedTime()));

        if (people.getRoleId() != null && people.getRoleId() > 0) {
            RoleBean roleBean = (RoleBean) BeanFactory.lookup("RoleBean");
            Role role = people.getRoleId() > 0 ? roleBean.getRole(people.getRoleId()) : null;
            String roleName = role != null ? role.getName() : null;
            element.element(PackageConstants.UserConstants.ROLE).text(roleName);
        }

        if (PEOPLE_TYPE_LIST.contains(peopleTypeEnum)) {
            String primaryContactName = null, primaryContactEmail = null, primaryContactPhone = null;
            String name = null, description = null;
            ModuleBaseWithCustomFields baseProp = people.getBaseProp();
            V3PeopleContext subProp = people.getSubProp();
            boolean primaryContact = false;
            long parentId = baseProp.getId();

            switch (peopleTypeEnum) {
                case TENANT_CONTACT:
                    name = ((V3TenantContext) baseProp).getName();
                    description = ((V3TenantContext) baseProp).getDescription();
                    primaryContact = ((V3TenantContactContext) subProp).isPrimaryContact();
                    primaryContactName = ((V3TenantContext) baseProp).getPrimaryContactName();
                    primaryContactEmail = ((V3TenantContext) baseProp).getPrimaryContactEmail();
                    primaryContactPhone = ((V3TenantContext) baseProp).getPrimaryContactPhone();
                    break;

                case VENDOR_CONTACT:
                    name = ((V3VendorContext) baseProp).getName();
                    description = ((V3VendorContext) baseProp).getDescription();
                    primaryContact = ((V3VendorContactContext) subProp).isPrimaryContact();
                    primaryContactName = ((V3VendorContext) baseProp).getPrimaryContactName();
                    primaryContactEmail = ((V3VendorContext) baseProp).getPrimaryContactEmail();
                    primaryContactPhone = ((V3VendorContext) baseProp).getPrimaryContactPhone();
                    break;

                case CLIENT_CONTACT:
                    name = ((V3ClientContext) baseProp).getName();
                    description = ((V3ClientContext) baseProp).getDescription();
                    primaryContact = ((V3ClientContactContext) subProp).isPrimaryContact();
                    primaryContactName = ((V3ClientContext) baseProp).getPrimaryContactName();
                    primaryContactEmail = ((V3ClientContext) baseProp).getPrimaryContactEmail();
                    primaryContactPhone = ((V3ClientContext) baseProp).getPrimaryContactPhone();
                    break;

                default:
                    break;
            }

            XMLBuilder additionalProp = element.element(PackageConstants.UserConstants.ADDITIONAL_PROPS);
            additionalProp.element(PackageConstants.UserConstants.IS_PRIMARY_CONTACT).text(String.valueOf(primaryContact));
            if (primaryContact) {
                additionalProp.element(PackageConstants.NAME).text(name);
                additionalProp.element(PackageConstants.DESCRIPTION).text(description);
            }
            additionalProp.element(PackageConstants.UserConstants.PARENT_UID).text(String.valueOf(parentId));
            additionalProp.element(PackageConstants.UserConstants.PRIMARY_CONTACT_NAME).text(primaryContactName);
            additionalProp.element(PackageConstants.UserConstants.PRIMARY_CONTACT_PHONE).text(primaryContactPhone);
            additionalProp.element(PackageConstants.UserConstants.PRIMARY_CONTACT_EMAIL).text(primaryContactEmail);
        }
    }

    @Override
    public Map<String, String> validateComponentToCreate(List<XMLBuilder> components) throws Exception {
        return null;
    }

    @Override
    public List<Long> getDeletedComponentIds(List<Long> componentIds) throws Exception {
        return null;
    }

    @Override
    public Map<String, Long> getExistingIdsByXMLData(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        Map<String, Long> roleNameVsRoleId = getRoleNameVsId();
        Map<String, Long> uniqueIdentifierVsComponentId = new HashMap<>();
        Map<String, String> peopleMailVsUniqueIdentifier = new HashMap<>();

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            String uniqueId = idVsData.getKey();
            XMLBuilder element = idVsData.getValue();
            V3PeopleContext peopleContext = constructPeopleContextFromBuilder(element, roleNameVsRoleId);

            peopleMailVsUniqueIdentifier.put(peopleContext.getEmail(), uniqueId);
        }

        if (MapUtils.isEmpty(peopleMailVsUniqueIdentifier)) {
            Map<String, PeopleContext> peopleForEmails = PeopleAPI.getPeopleForEmails(peopleMailVsUniqueIdentifier.keySet(), false);
            if (MapUtils.isNotEmpty(peopleForEmails)) {
                for (String email : peopleForEmails.keySet()) {
                    long peopleId = peopleForEmails.get(email).getId();
                    String uniqueId = peopleMailVsUniqueIdentifier.get(email);
                    uniqueIdentifierVsComponentId.put(uniqueId, peopleId);
                }
            }
        }
        return uniqueIdentifierVsComponentId;
    }

    @Override
    public Map<String, Long> createComponentFromXML(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        // PeopleMail & PeopleName are nullable columns - So handled everything using UniqueId
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        com.facilio.accounts.dto.User superAdminUser = AccountUtil.getOrgBean().getSuperAdmin(orgId);

        Map<String, Long> roleNameVsRoleId = getRoleNameVsId();
        String PEOPLE = "people", TENANTS = "tenants", VENDORS = "vendors";
        String CLIENTS = "clients", DELETED = "deleted", OTHERS = "others";
        String EMPLOYEES = "employees", OCCUPANTS = "occupants";

        // peopleTypeVsUIDVsRecordMap - contains records of OTHERS, DELETED, TENANTS, VENDORS, CLIENTS, EMPLOYEE, OCCUPANTS types
        Map<String, Map<String, Map<String, Object>>> peopleTypeVsUIDVsRecordMap = new HashMap<>();
        // peopleTypeVsPrimaryUIDVsContactUID - contains conf for TENANTS, VENDORS, CLIENTS ({"tenant" : {"tenantUId" : ["contact1UId", "contact2UId"]}})
        Map<String, Map<String, List<String>>> peopleTypeVsPrimaryUIDVsContactUID = new HashMap<>();
        // for PackageChangeSet Mapping - contains UniqueIdentifierVsComponentId for components PEOPLE, TENANTS, VENDORS, CLIENTS
        Map<String, Map<String, Long>> peopleTypeVsUniqueIdentifierVsComponentId = new HashMap<>();

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder element = idVsData.getValue();
            V3PeopleContext peopleContext = constructPeopleContextFromBuilder(element, roleNameVsRoleId);

            V3PeopleContext.PeopleType peopleTypeEnum = peopleContext.getPeopleTypeEnum();
            peopleTypeEnum = peopleTypeEnum != null ? peopleTypeEnum : V3PeopleContext.PeopleType.OTHERS;

            if (superAdminUser.getEmail().equals(peopleContext.getEmail())) {
                V3PeopleContext people = V3PeopleAPI.getPeople(peopleContext.getEmail());
                getUIDVsCompId(peopleTypeVsUniqueIdentifierVsComponentId, PEOPLE).put(idVsData.getKey(), people != null ? people.getId() : -1);
            } else if (peopleContext.getSysDeletedTime() > 0){
                addUIDVsDataProp(peopleTypeVsUIDVsRecordMap, DELETED, idVsData.getKey(), FieldUtil.getAsProperties(peopleContext));
            } else if (peopleTypeEnum.equals(V3PeopleContext.PeopleType.OTHERS)) {
                addUIDVsDataProp(peopleTypeVsUIDVsRecordMap, OTHERS, idVsData.getKey(), FieldUtil.getAsProperties(peopleContext));
            } else if (peopleTypeEnum.equals(V3PeopleContext.PeopleType.EMPLOYEE)){
                peopleContext.setPeopleType(V3PeopleContext.PeopleType.OTHERS.getIndex());
                addUIDVsDataProp(peopleTypeVsUIDVsRecordMap, EMPLOYEES, idVsData.getKey(), FieldUtil.getAsProperties(peopleContext));
            } else if (peopleTypeEnum.equals(V3PeopleContext.PeopleType.OCCUPANT)) {
                peopleContext.setPeopleType(V3PeopleContext.PeopleType.OTHERS.getIndex());
                addUIDVsDataProp(peopleTypeVsUIDVsRecordMap, OCCUPANTS, idVsData.getKey(), FieldUtil.getAsProperties(peopleContext));
            } else if (PEOPLE_TYPE_LIST.contains(peopleTypeEnum)){
                XMLBuilder additionalProp = element.getElement(PackageConstants.UserConstants.ADDITIONAL_PROPS);
                String primaryContactName = additionalProp.getElement(PackageConstants.UserConstants.PRIMARY_CONTACT_NAME).getText();
                String primaryContactEmail = additionalProp.getElement(PackageConstants.UserConstants.PRIMARY_CONTACT_EMAIL).getText();
                boolean primaryContact = Boolean.parseBoolean(additionalProp.getElement(PackageConstants.UserConstants.IS_PRIMARY_CONTACT).getText());
                String parentUid = additionalProp.getElement(PackageConstants.UserConstants.PARENT_UID).getText();

                Map<String, Object> dataProp = new HashMap<>();
                dataProp.put("primaryContactEmail", primaryContactEmail);
                dataProp.put("primaryContactName", primaryContactName);
                dataProp.put("primaryContactPhone", additionalProp.getElement(PackageConstants.UserConstants.PRIMARY_CONTACT_PHONE).getText());

                if (primaryContact) {
                    dataProp.put("name", additionalProp.getElement(PackageConstants.NAME).getText());
                    dataProp.put("description", additionalProp.getElement(PackageConstants.DESCRIPTION).getText());
                } else {
                    peopleContext.setPeopleType(V3PeopleContext.PeopleType.OTHERS.getIndex());
                }
                addUIDVsDataProp(peopleTypeVsUIDVsRecordMap, OTHERS, idVsData.getKey(), FieldUtil.getAsProperties(peopleContext));

                switch (peopleTypeEnum) {
                    case TENANT_CONTACT:
                        if (primaryContact) {
                            addUIDVsDataProp(peopleTypeVsUIDVsRecordMap, TENANTS, parentUid, dataProp);
                        }
                        addPrimaryUIDVSContactUID(peopleTypeVsPrimaryUIDVsContactUID, TENANTS, parentUid, idVsData.getKey());
                        break;

                    case VENDOR_CONTACT:
                        if (primaryContact) {
                            addUIDVsDataProp(peopleTypeVsUIDVsRecordMap, VENDORS, parentUid, dataProp);
                        }
                        addPrimaryUIDVSContactUID(peopleTypeVsPrimaryUIDVsContactUID, VENDORS, parentUid, idVsData.getKey());
                        break;

                    case CLIENT_CONTACT:
                        if (primaryContact) {
                            addUIDVsDataProp(peopleTypeVsUIDVsRecordMap, CLIENTS, parentUid, dataProp);
                        }
                        addPrimaryUIDVSContactUID(peopleTypeVsPrimaryUIDVsContactUID, CLIENTS, parentUid, idVsData.getKey());
                        break;

                    default:
                        break;
                }
            }
        }

        // Add People in OTHERS type & Mark as deleted
        List<Map<String, Object>> deletedList = bulkAddModuleRecords(FacilioConstants.ContextNames.PEOPLE, peopleTypeVsUIDVsRecordMap.get(DELETED), getUIDVsCompId(peopleTypeVsUniqueIdentifierVsComponentId, PEOPLE));
        List<Long> deletedPeopleIds = CollectionUtils.isNotEmpty(deletedList) ? deletedList.stream().map(map -> (long) map.get("id")).collect(Collectors.toList()) : null;
        PackageBeanUtil.bulkDeleteV3Records(FacilioConstants.ContextNames.PEOPLE, deletedPeopleIds);

        // Add all people with OTHERS type & later convert PEOPLE_TYPE
        bulkAddModuleRecords(FacilioConstants.ContextNames.PEOPLE, peopleTypeVsUIDVsRecordMap.get(OTHERS), getUIDVsCompId(peopleTypeVsUniqueIdentifierVsComponentId, PEOPLE));
        bulkAddModuleRecords(FacilioConstants.ContextNames.PEOPLE, peopleTypeVsUIDVsRecordMap.get(EMPLOYEES), getUIDVsCompId(peopleTypeVsUniqueIdentifierVsComponentId, PEOPLE));
        bulkAddModuleRecords(FacilioConstants.ContextNames.PEOPLE, peopleTypeVsUIDVsRecordMap.get(OCCUPANTS), getUIDVsCompId(peopleTypeVsUniqueIdentifierVsComponentId, PEOPLE));

        // Add Tenants, Vendors, Clients
        bulkAddModuleRecords(FacilioConstants.ContextNames.TENANT, peopleTypeVsUIDVsRecordMap.get(TENANTS), getUIDVsCompId(peopleTypeVsUniqueIdentifierVsComponentId, TENANTS));
        bulkAddModuleRecords(FacilioConstants.ContextNames.VENDORS, peopleTypeVsUIDVsRecordMap.get(VENDORS), getUIDVsCompId(peopleTypeVsUniqueIdentifierVsComponentId, VENDORS));
        bulkAddModuleRecords(FacilioConstants.ContextNames.CLIENT, peopleTypeVsUIDVsRecordMap.get(CLIENTS), getUIDVsCompId(peopleTypeVsUniqueIdentifierVsComponentId, CLIENTS));

        // Convert People to respective types
        convertPeopleToEmployeeOrOccupant(V3PeopleContext.PeopleType.EMPLOYEE, peopleTypeVsUniqueIdentifierVsComponentId.get(PEOPLE), peopleTypeVsUIDVsRecordMap.get(EMPLOYEES));
        convertPeopleToEmployeeOrOccupant(V3PeopleContext.PeopleType.OCCUPANT, peopleTypeVsUniqueIdentifierVsComponentId.get(PEOPLE), peopleTypeVsUIDVsRecordMap.get(OCCUPANTS));
        convertPeopleType(V3PeopleContext.PeopleType.TENANT_CONTACT, peopleTypeVsPrimaryUIDVsContactUID.get(TENANTS), peopleTypeVsUniqueIdentifierVsComponentId.get(TENANTS), peopleTypeVsUniqueIdentifierVsComponentId.get(PEOPLE));
        convertPeopleType(V3PeopleContext.PeopleType.VENDOR_CONTACT, peopleTypeVsPrimaryUIDVsContactUID.get(VENDORS), peopleTypeVsUniqueIdentifierVsComponentId.get(VENDORS), peopleTypeVsUniqueIdentifierVsComponentId.get(PEOPLE));
        convertPeopleType(V3PeopleContext.PeopleType.CLIENT_CONTACT, peopleTypeVsPrimaryUIDVsContactUID.get(CLIENTS), peopleTypeVsUniqueIdentifierVsComponentId.get(CLIENTS), peopleTypeVsUniqueIdentifierVsComponentId.get(PEOPLE));

        PackageUtil.addPackageChangeset(ComponentType.TENANT, peopleTypeVsUniqueIdentifierVsComponentId.get(TENANTS));
        PackageUtil.addPackageChangeset(ComponentType.VENDOR, peopleTypeVsUniqueIdentifierVsComponentId.get(VENDORS));
        PackageUtil.addPackageChangeset(ComponentType.CLIENT, peopleTypeVsUniqueIdentifierVsComponentId.get(CLIENTS));

        return peopleTypeVsUniqueIdentifierVsComponentId.get(PEOPLE);
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        // TODO - Handle whether to update Tenant/ Vendor/ Client contact, For now handled PeopleType Others only
        if (MapUtils.isEmpty(idVsXMLComponents)) {
            return;
        }

        Map<String, Long> roleNameVsRoleId = getRoleNameVsId();
        Map<Long, V3PeopleContext> peopleForSimpleUpdate = new HashMap<>();                             // PeopleType is unchanged (perform normal update)
        Map<String, Map<Long, V3PeopleContext>> peopleTypeVsIdVsPeople = new HashMap<>();               // PeopleType is changed
        Map<Long, PeopleContext> dbPeopleForIds = PeopleAPI.getPeopleForIds(idVsXMLComponents.keySet(), false);

        for (Map.Entry<Long, XMLBuilder> idVsData : idVsXMLComponents.entrySet()) {
            Long peopleId = idVsData.getKey();
            XMLBuilder element = idVsData.getValue();

            V3PeopleContext peopleContext = constructPeopleContextFromBuilder(element, roleNameVsRoleId);
            peopleContext.setId(peopleId);

            if (MapUtils.isNotEmpty(dbPeopleForIds) && dbPeopleForIds.containsKey(peopleId)) {
                PeopleContext dbPeopleContext = dbPeopleForIds.get(peopleId);
                // PeopleType is changed
                if (!peopleContext.getPeopleType().equals(dbPeopleContext.getPeopleType())) {
                    peopleTypeVsIdVsPeople.computeIfAbsent(peopleContext.getPeopleTypeEnum().name(), k -> new HashMap<>());
                    peopleTypeVsIdVsPeople.get(peopleContext.getPeopleTypeEnum().name()).put(peopleId, peopleContext);
                } else {
                    // PeopleType is unchanged
                    peopleForSimpleUpdate.put(peopleId, peopleContext);
                }
            }
        }

        // Simple bulk update (PeopleType not updated)
        bulkUpdatePeople(FacilioConstants.ContextNames.PEOPLE, new ArrayList<>(peopleForSimpleUpdate.keySet()), new ArrayList<>(peopleForSimpleUpdate.values()));
    }

    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {
        PackageBeanUtil.bulkDeleteV3Records(FacilioConstants.ContextNames.PEOPLE, ids);
    }

    @Override
    public void addPickListConf() throws Exception {
        Map<String, Long> peopleMailVsPeopleId = new HashMap<>();

        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule peopleModule = moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE);
        List<V3PeopleContext> activePeople = (List<V3PeopleContext>) getActivePeople(peopleModule, V3PeopleContext.class);
        List<V3PeopleContext> inActivePeople = (List<V3PeopleContext>) getDeletedPeople(peopleModule, V3PeopleContext.class);

        addPeopleConfigForContext(activePeople, peopleMailVsPeopleId);
        addPeopleConfigForContext(inActivePeople, peopleMailVsPeopleId);
    }

    private static final List<V3PeopleContext.PeopleType> PEOPLE_TYPE_LIST = new ArrayList<V3PeopleContext.PeopleType>() {{
        add(V3PeopleContext.PeopleType.TENANT_CONTACT);
        add(V3PeopleContext.PeopleType.VENDOR_CONTACT);
        add(V3PeopleContext.PeopleType.CLIENT_CONTACT);
    }};

    private static V3PeopleContext constructPeopleContextFromBuilder(XMLBuilder element, Map<String, Long> roleNameVsRoleId) {
        String email = element.getElement(PackageConstants.UserConstants.EMAIL).getText();
        String language = element.getElement(PackageConstants.UserConstants.LANGUAGE).getText();
        String timeZone = element.getElement(PackageConstants.UserConstants.TIMEZONE).getText();
        String userName = element.getElement(PackageConstants.UserConstants.USER_NAME).getText();
        String phone = element.getElement(PackageConstants.UserConstants.PHONE).getText();
        String mobile = element.getElement(PackageConstants.UserConstants.MOBILE).getText();
        String peopleTypeStr = element.getElement(PackageConstants.UserConstants.PEOPLE_TYPE).getText();
        boolean isUser = Boolean.parseBoolean(element.getElement(PackageConstants.UserConstants.IS_USER).getText());
        long deletedTime = Long.parseLong(element.getElement(PackageConstants.UserConstants.DELETED_TIME).getText());
        boolean hasOccupantAccess = Boolean.parseBoolean(element.getElement(PackageConstants.UserConstants.OCCUPANT_PORTAL_ACCESS).getText());
        boolean hasEmployeePortalAccess = Boolean.parseBoolean(element.getElement(PackageConstants.UserConstants.EMPLOYEE_PORTAL_ACCESS).getText());

        V3PeopleContext.PeopleType peopleType = StringUtils.isNotEmpty(peopleTypeStr) ? V3PeopleContext.PeopleType.valueOf(peopleTypeStr) : V3PeopleContext.PeopleType.OTHERS;
        int peopleTypeInt = peopleType.getIndex();
        long roleId = -1;

        if (element.getElement(PackageConstants.UserConstants.ROLE) != null) {
            String roleName = element.getElement(PackageConstants.UserConstants.ROLE).getText();
            roleId = (StringUtils.isNotEmpty(roleName) && roleNameVsRoleId.containsKey(roleName)) ? roleNameVsRoleId.get(roleName) : -1;
        }

        V3PeopleContext peopleContext = new V3PeopleContext();
        peopleContext.setEmail(email);
        peopleContext.setPhone(phone);
        peopleContext.setUser(isUser);
        peopleContext.setMobile(mobile);
        peopleContext.setName(userName);
        peopleContext.setRoleId(roleId);
        peopleContext.setLanguage(language);
        peopleContext.setTimezone(timeZone);
        peopleContext.setPeopleType(peopleTypeInt);
        peopleContext.setSysDeletedTime(deletedTime);
        peopleContext.setIsOccupantPortalAccess(hasOccupantAccess);
        peopleContext.setEmployeePortalAccess(hasEmployeePortalAccess);

        return peopleContext;
    }

    private static <T extends ModuleBaseWithCustomFields> Map<Long, T> getRecordProps(String moduleName, Class<?> clazz) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule module = moduleBean.getModule(moduleName);

        List<T> moduleRecords = (List<T>) PackageBeanUtil.getModuleData(null, module, clazz, true);

        Map<Long, T> recordMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(moduleRecords)) {
            for (T record : moduleRecords) {
                recordMap.put(record.getId(), record);
            }
        }
        return recordMap;
    }

    private static List<Map<String, Object>> bulkAddModuleRecords(String moduleName, Map<String, Map<String, Object>> uIDVsRecordMap, Map<String, Long> uniqueIdentifierVsComponentId) throws Exception {
        if (MapUtils.isEmpty(uIDVsRecordMap)) {
            return new ArrayList<>();
        }

        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule module = moduleBean.getModule(moduleName);
        Class beanClass = ChainUtil.getV3Config(module).getBeanClass();

        Map<String, List<Object>> queryParams = new HashMap<>();
        queryParams.put(FacilioConstants.ContextNames.SKIP_PEOPLE_CONTACTS, new ArrayList<Object>(){{
            add(Boolean.TRUE);
        }});

        FacilioContext context = V3Util.createRecordList(module, new ArrayList<>(uIDVsRecordMap.values()), null, queryParams);
        List<ModuleBaseWithCustomFields> addedRecords = Constants.getRecordList(context);
        if (CollectionUtils.isNotEmpty(addedRecords)) {
            List<Map<String, Object>> addedRecordsList = FieldUtil.getAsMapList(addedRecords, beanClass);
            addUIDVsCompId(uniqueIdentifierVsComponentId, new ArrayList<>(uIDVsRecordMap.keySet()), addedRecordsList);

            return addedRecordsList;
        }

        return new ArrayList<>();
    }

    private static void addUIDVsDataProp(Map<String, Map<String, Map<String, Object>>> peopleTypeVsUIDVsRecordMap, String peopleType, String uid, Map<String, Object> dataProp) {
        getUIDVsDataProp(peopleTypeVsUIDVsRecordMap, peopleType).put(uid, dataProp);
    }

    private static Map<String, Map<String, Object>> getUIDVsDataProp(Map<String, Map<String, Map<String, Object>>> peopleTypeVsUIDVsRecordMap, String peopleType) {
        return peopleTypeVsUIDVsRecordMap.computeIfAbsent(peopleType, k -> new LinkedHashMap<>());
    }

    private static void addPrimaryUIDVSContactUID(Map<String, Map<String, List<String>>> peopleTypeVsPrimaryUIDVsContactUID, String peopleType, String primaryUID, String contactUID) {
        getPrimaryUIDVSContactUID(peopleTypeVsPrimaryUIDVsContactUID, peopleType).computeIfAbsent(primaryUID, k -> new ArrayList<>());
        peopleTypeVsPrimaryUIDVsContactUID.get(peopleType).get(primaryUID).add(contactUID);
    }

    private static Map<String, List<String>> getPrimaryUIDVSContactUID(Map<String, Map<String, List<String>>> peopleTypeVsPrimaryUIDVsContactUID, String peopleType) {
        return peopleTypeVsPrimaryUIDVsContactUID.computeIfAbsent(peopleType, k -> new LinkedHashMap<>());
    }

    private static Map<String, Long> getUIDVsCompId(Map<String, Map<String, Long>> peopleTypeVsUniqueIdentifierVsComponentId, String peopleType) {
        return peopleTypeVsUniqueIdentifierVsComponentId.computeIfAbsent(peopleType, k -> new HashMap<>());
    }

    private static void addUIDVsCompId(Map<String, Long> uniqueIdentifierVsComponentId, List<String> othersUID, List<Map<String, Object>> othersMapList) {
        // Construct UniqueIdentifier Vs ComponentId
        for (int i = 0; i < othersMapList.size() ; i++) {
            String uId = othersUID.get(i);
            long id = (long) othersMapList.get(i).get("id");
            uniqueIdentifierVsComponentId.put(uId, id);
        }
    }

    private static void bulkUpdatePeople(String moduleName, List<Long> ids, List<V3PeopleContext> peopleContextList) throws Exception {
        if (CollectionUtils.isEmpty(peopleContextList)) {
            return;
        }

        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule module = moduleBean.getModule(moduleName);
        FacilioContext summaryContext = V3Util.getSummary(moduleName, ids);
        List<ModuleBaseWithCustomFields> moduleBaseWithCustomFields = Constants.getRecordListFromContext(summaryContext, moduleName);

        List<Map<String, Object>> peopleContextMap = FieldUtil.getAsMapList(peopleContextList, V3PeopleContext.class);
        V3Util.processAndUpdateBulkRecords(module, moduleBaseWithCustomFields, peopleContextMap, null, null, null, null, null, null, null, null, true,false, null);
    }

    private static void convertPeopleType(V3PeopleContext.PeopleType peopleType, Map<String, List<String>> primaryUIDVSContactUID, Map<String, Long> primaryUIDVsCompId, Map<String, Long> contactUIDVsCompId) throws Exception {
        if (MapUtils.isEmpty(primaryUIDVSContactUID) || MapUtils.isEmpty(primaryUIDVsCompId) || MapUtils.isEmpty(contactUIDVsCompId)) {
            return;
        }

        for (Map.Entry<String, List<String>> entry : primaryUIDVSContactUID.entrySet()) {
            String primaryUId = entry.getKey();
            List<String> contactUIds = entry.getValue();

            long lookupId = primaryUIDVsCompId.getOrDefault(primaryUId, -1L);
            // tenant (or) vendor (or) client not added
            if (lookupId < 0) {
                continue;
            }

            List<Long> contactPeopleIds = contactUIds.stream().filter(contactUIDVsCompId::containsKey).map(contactUIDVsCompId::get).collect(Collectors.toList());
            convertPeopleTypeChain(peopleType, contactPeopleIds, lookupId);
        }

    }

      private static void convertPeopleToEmployeeOrOccupant(V3PeopleContext.PeopleType peopleType, Map<String, Long> uIDVsCompId, Map<String, Map<String, Object>> uIDVsRecordMap) throws Exception {
        if (MapUtils.isEmpty(uIDVsCompId) || MapUtils.isEmpty(uIDVsRecordMap)) {
            return;
        }

        List<Long> employeePeopleIds = uIDVsRecordMap.keySet().stream().filter(uIDVsCompId::containsKey).map(uIDVsCompId::get).collect(Collectors.toList());
        convertPeopleTypeChain(peopleType, employeePeopleIds, -1);
    }

    private static void convertPeopleTypeChain(V3PeopleContext.PeopleType peopleType, List<Long> peopleIds, long lookupId) throws Exception {
        if (CollectionUtils.isEmpty(peopleIds)) {
            return;
        }

        FacilioChain chain = TransactionChainFactoryV3.convertPeopleTypeChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.PEOPLE_TYPE, peopleType.getIndex());
        context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, peopleIds);
        context.put("lookupId", lookupId);
        chain.execute();
    }

    private static Map<String, Long> getRoleNameVsId() throws Exception {
        List<Role> allRoles = PackageBeanUtil.getAllRoles();
        Map<String, Long> roleNameVsRoleId = CollectionUtils.isEmpty(allRoles) ? new HashMap<>()
                : allRoles.stream().collect(Collectors.toMap(Role::getName, Role::getRoleId, (a, b) -> b));

        return roleNameVsRoleId;
    }

    private void addPeopleConfigForContext(List<V3PeopleContext> props, Map<String, Long> peopleMailVsPeopleId) {
        if (CollectionUtils.isNotEmpty(props)) {
            for (V3PeopleContext peopleContext : props) {
                // PeopleMail & PeopleName are nullable columns
                if (StringUtils.isNotEmpty(peopleContext.getEmail())) {
                    peopleMailVsPeopleId.put(peopleContext.getEmail(), peopleContext.getId());
                } else if (StringUtils.isNotEmpty(peopleContext.getName())) {
                    peopleMailVsPeopleId.put(peopleContext.getName(), peopleContext.getId());
                }
            }
            PackageUtil.addPeopleConfigForContext(peopleMailVsPeopleId);
        }
    }

    private void addPeopleConfigForXML(Map<Long, Long> peopleIdMap, Map<Long, String> peopleIdVsPeopleMail, List<V3PeopleContext> props) {
        if (CollectionUtils.isNotEmpty(props)) {
            for (V3PeopleContext peopleContext : props) {
                if (StringUtils.isNotEmpty(peopleContext.getEmail()) && !V3PeopleAPI.VALID_EMAIL_ADDRESS_REGEX.matcher(peopleContext.getEmail()).find()) {
                    LOGGER.info("####Sandbox Tracking - Improper People Info - " + peopleContext.getId() + " Name - " + peopleContext.getName());
                    continue;
                }
                peopleIdMap.put(peopleContext.getId(), -1L);
                // PeopleMail & PeopleName are nullable columns
                if (StringUtils.isNotEmpty(peopleContext.getEmail())) {
                    peopleIdVsPeopleMail.put(peopleContext.getId(), peopleContext.getEmail());
                } else if (StringUtils.isNotEmpty(peopleContext.getName())) {
                    peopleIdVsPeopleMail.put(peopleContext.getId(), peopleContext.getName());
                }
            }
            PackageUtil.addPeopleConfigForXML(peopleIdVsPeopleMail);
        }
    }

    private static List<?> getActivePeople(FacilioModule module, Class<?> clazz) throws Exception {
        List<FacilioField> selectableFields = new ArrayList<FacilioField>() {{
            add(FieldFactory.getField("id", "MAX(People.ID)", null, FieldType.NUMBER));
            add(FieldFactory.getField("name", "NAME", module, FieldType.STRING));
            add(FieldFactory.getField("email", "EMAIL", module, FieldType.STRING));
        }};

        SelectRecordsBuilder<ModuleBaseWithCustomFields> selectRecordBuilder = getGenericSelectRecordBuilder(module, clazz);
        selectRecordBuilder.select(selectableFields)
                .groupBy("People.EMAIL")
                .andCondition(CriteriaAPI.getCondition("EMAIL", "email", "", CommonOperators.IS_NOT_EMPTY));

        List<ModuleBaseWithCustomFields> propsList = selectRecordBuilder.get();
        return propsList;
    }

    private static List<?> getDeletedPeople(FacilioModule module, Class<?> clazz) throws Exception {
        List<FacilioField> selectableFields = new ArrayList<FacilioField>() {{
            add(FieldFactory.getIdField(module));
            add(FieldFactory.getField("name", "NAME", module, FieldType.STRING));
            add(FieldFactory.getField("email", "EMAIL", module, FieldType.STRING));
        }};

        SelectRecordsBuilder<ModuleBaseWithCustomFields> selectRecordBuilder = getGenericSelectRecordBuilder(module, clazz);
        selectRecordBuilder.select(selectableFields)
                .andCondition(CriteriaAPI.getCondition("EMAIL", "email", "", CommonOperators.IS_EMPTY));

        List<ModuleBaseWithCustomFields> propsList = selectRecordBuilder.get();
        return propsList;
    }

    private static SelectRecordsBuilder<ModuleBaseWithCustomFields> getGenericSelectRecordBuilder(FacilioModule module, Class<?> clazz) throws Exception {
        SelectRecordsBuilder<ModuleBaseWithCustomFields> selectRecordBuilder = new SelectRecordsBuilder<>()
                .beanClass((Class<ModuleBaseWithCustomFields>) clazz)
                .table(module.getTableName())
                .module(module)
                .fetchDeleted()
                ;

        return selectRecordBuilder;
    }
}
