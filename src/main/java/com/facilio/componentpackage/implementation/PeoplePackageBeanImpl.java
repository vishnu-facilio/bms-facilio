package com.facilio.componentpackage.implementation;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.componentpackage.utils.PackageBeanUtil;
import com.facilio.componentpackage.utils.PackageUtil;
import org.apache.commons.collections4.CollectionUtils;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import com.facilio.bmsconsoleV3.context.*;
import com.facilio.accounts.bean.RoleBean;
import com.facilio.xml.builder.XMLBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.v3.context.Constants;
import com.facilio.chain.FacilioContext;
import com.facilio.chain.FacilioChain;
import com.facilio.accounts.dto.Role;
import com.facilio.beans.ModuleBean;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.util.V3Util;

import java.util.*;
import java.util.stream.Collectors;
import com.facilio.fw.BeanFactory;

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
        
        List<V3PeopleContext> props = (List<V3PeopleContext>) PackageBeanUtil.getModuleData(null, peopleModule, V3PeopleContext.class, true);
        if (CollectionUtils.isNotEmpty(props)) {
            for (V3PeopleContext peopleContext : props) {
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
        return peopleIdMap;
    }

    @Override
    public Map<Long, V3PeopleContextExtendedProps> fetchComponents(List<Long> ids) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule peopleModule = moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE);
        List<V3PeopleContext> peopleContexts = (List<V3PeopleContext>) PackageBeanUtil.getModuleDataListsForIds(ids, peopleModule, V3PeopleContext.class);

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

                peopleIdVsPeople.put(people.getId(), people);
            }
        }
        return peopleIdVsPeople;
    }

    @Override
    public void convertToXMLComponent(V3PeopleContextExtendedProps people, XMLBuilder element) throws Exception {
        V3PeopleContext.PeopleType peopleTypeEnum = people.getPeopleTypeEnum();
        peopleTypeEnum = peopleTypeEnum != null ? peopleTypeEnum : V3PeopleContext.PeopleType.OTHERS;

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
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        com.facilio.accounts.dto.User superAdminUser = AccountUtil.getOrgBean().getSuperAdmin(orgId);

        Map<String, Long> roleNameVsRoleId = getRoleNameVsId();

        List<V3PeopleContext> others = new ArrayList<>();
        List<String> employeeMails = new ArrayList<>();
        List<Map<String, Object>> tenants = new ArrayList<>();
        List<Map<String, Object>> vendors = new ArrayList<>();
        List<Map<String, Object>> clients = new ArrayList<>();
        Map<String, List<String>> tenantMailVsContactMails = new HashMap<>();
        Map<String, List<String>> vendorMailVsContactMails = new HashMap<>();
        Map<String, List<String>> clientMailVsContactMails = new HashMap<>();

        Map<String, Long> uniqueIdentifierVsComponentId = new HashMap<>();
        Map<String, String> peopleMailVsUniqueIdentifier = new HashMap<>();

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder element = idVsData.getValue();
            V3PeopleContext peopleContext = constructPeopleContextFromBuilder(element, roleNameVsRoleId);

            String keyForPeopleMailVsUniqueIdentifier = StringUtils.isNotEmpty(peopleContext.getEmail()) ? peopleContext.getEmail() : (StringUtils.isNotEmpty(peopleContext.getName()) ? peopleContext.getName() : null);
            if (StringUtils.isNotEmpty(keyForPeopleMailVsUniqueIdentifier)) {
                peopleMailVsUniqueIdentifier.put(keyForPeopleMailVsUniqueIdentifier, idVsData.getKey());
            }
            V3PeopleContext.PeopleType peopleTypeEnum = peopleContext.getPeopleTypeEnum();
            peopleTypeEnum = peopleTypeEnum != null ? peopleTypeEnum : V3PeopleContext.PeopleType.OTHERS;

            if (superAdminUser.getEmail().equals(peopleContext.getEmail())) {
                V3PeopleContext people = V3PeopleAPI.getPeople(peopleContext.getEmail());
                uniqueIdentifierVsComponentId.put(idVsData.getKey(), people != null ? people.getId() : -1);
            } else if (peopleTypeEnum.equals(V3PeopleContext.PeopleType.OTHERS)) {
                others.add(peopleContext);
            } else if (peopleTypeEnum.equals(V3PeopleContext.PeopleType.EMPLOYEE)){
                peopleContext.setPeopleType(V3PeopleContext.PeopleType.OTHERS.getIndex());
                employeeMails.add(peopleContext.getEmail());
                others.add(peopleContext);
            } else if (PEOPLE_TYPE_LIST.contains(peopleTypeEnum)){
                XMLBuilder additionalProp = element.getElement(PackageConstants.UserConstants.ADDITIONAL_PROPS);
                String primaryContactName = additionalProp.getElement(PackageConstants.UserConstants.PRIMARY_CONTACT_NAME).getText();
                String primaryContactEmail = additionalProp.getElement(PackageConstants.UserConstants.PRIMARY_CONTACT_EMAIL).getText();
                boolean primaryContact = Boolean.parseBoolean(additionalProp.getElement(PackageConstants.UserConstants.IS_PRIMARY_CONTACT).getText());

                Map<String, Object> dataProp = new HashMap<>();
                dataProp.put("primaryContactEmail", primaryContactEmail);
                dataProp.put("primaryContactName", primaryContactName);
                dataProp.put("primaryContactPhone", additionalProp.getElement(PackageConstants.UserConstants.PRIMARY_CONTACT_PHONE).getText());

                if (primaryContact) {
                    dataProp.put("name", additionalProp.getElement(PackageConstants.NAME).getText());
                    dataProp.put("description", additionalProp.getElement(PackageConstants.DESCRIPTION).getText());
                } else {
                    peopleContext.setPeopleType(V3PeopleContext.PeopleType.OTHERS.getIndex());
                    others.add(peopleContext);
                }

                switch (peopleTypeEnum) {
                    case TENANT_CONTACT:
                        if (primaryContact) {
                            tenants.add(dataProp);
                        } else {
                            constructParentMailVsContactMails(tenantMailVsContactMails, primaryContactEmail, primaryContactName, peopleContext);
                        }
                        break;

                    case VENDOR_CONTACT:
                        if (primaryContact) {
                            vendors.add(dataProp);
                        } else {
                            constructParentMailVsContactMails(vendorMailVsContactMails, primaryContactEmail, primaryContactName, peopleContext);
                        }
                        break;

                    case CLIENT_CONTACT:
                        if (primaryContact) {
                            clients.add(dataProp);
                        } else {
                            constructParentMailVsContactMails(clientMailVsContactMails, primaryContactEmail, primaryContactName, peopleContext);
                        }
                        break;

                    default:
                        break;
                }
            }
        }

        // PeopleMail & PeopleName are nullable columns - So Storing Mail/Name in peopleMailVsPeopleId
        // And handled similarly in all cases which involves PeopleMail

        // Add all people with OTHERS type
        List<Map<String, Object>> othersMapList = FieldUtil.getAsMapList(others, V3PeopleContext.class);
        Map<String, Long> peopleMailVsPeopleId = bulkAddModuleRecords(FacilioConstants.ContextNames.PEOPLE, othersMapList, V3PeopleContext.class, "email", "name");

        // Add Tenants, Vendors, Clients
        Map<String, Long> tenantsMap = bulkAddModuleRecords(FacilioConstants.ContextNames.TENANT, tenants, V3TenantContext.class, "primaryContactEmail", "primaryContactName");
        Map<String, Long> vendorsMap = bulkAddModuleRecords(FacilioConstants.ContextNames.VENDORS, vendors, V3VendorContext.class, "primaryContactEmail", "primaryContactName");
        Map<String, Long> clientsMap = bulkAddModuleRecords(FacilioConstants.ContextNames.CLIENT, clients, V3ClientContext.class, "primaryContactEmail", "primaryContactName");

        // Convert People to respective types
        convertPeopleToEmployee(peopleMailVsPeopleId, employeeMails);
        convertPeopleType(V3PeopleContext.PeopleType.TENANT_CONTACT, peopleMailVsPeopleId, tenantsMap, tenantMailVsContactMails);
        convertPeopleType(V3PeopleContext.PeopleType.VENDOR_CONTACT, peopleMailVsPeopleId, vendorsMap, vendorMailVsContactMails);
        convertPeopleType(V3PeopleContext.PeopleType.CLIENT_CONTACT, peopleMailVsPeopleId, clientsMap, clientMailVsContactMails);

        peopleMailVsPeopleId.putAll(tenantsMap);
        peopleMailVsPeopleId.putAll(vendorsMap);
        peopleMailVsPeopleId.putAll(clientsMap);

        // Construct UniqueIdentifier Vs ComponentId
        for (String peopleMail : peopleMailVsPeopleId.keySet()) {
            String uniqueId = peopleMailVsUniqueIdentifier.get(peopleMail);
            uniqueIdentifierVsComponentId.put(uniqueId, peopleMailVsPeopleId.get(peopleMail));
        }

        return uniqueIdentifierVsComponentId;
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
        List<Long> deletedPeopleIds = new ArrayList<>();
        for (Map.Entry<Long, XMLBuilder> idVsData : idVsXMLComponents.entrySet()) {
            Long peopleId = idVsData.getKey();
            XMLBuilder element = idVsData.getValue();
            long deletedTime = Long.parseLong(element.getElement(PackageConstants.UserConstants.DELETED_TIME).getText());
            if (deletedTime > 0) {
                deletedPeopleIds.add(peopleId);
            }
        }

        PackageBeanUtil.bulkDeleteV3Records(FacilioConstants.ContextNames.PEOPLE, deletedPeopleIds);
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
        List<V3PeopleContext> props = (List<V3PeopleContext>) PackageBeanUtil.getModuleData(null, peopleModule, V3PeopleContext.class, true);

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

    public static final List<V3PeopleContext.PeopleType> PEOPLE_TYPE_LIST = new ArrayList<V3PeopleContext.PeopleType>() {{
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
        peopleContext.setIsOccupantPortalAccess(hasOccupantAccess);
        peopleContext.setEmployeePortalAccess(hasEmployeePortalAccess);

        return peopleContext;
    }

    private static void constructParentMailVsContactMails(Map<String, List<String>> parentMailVsContactMails, String email, String name, V3PeopleContext peopleContext) {
        String key = StringUtils.isNotEmpty(email) ? email : (StringUtils.isNotEmpty(name) ? name : null);
        String value  = StringUtils.isNotEmpty(peopleContext.getEmail()) ? peopleContext.getEmail() : (StringUtils.isNotEmpty(peopleContext.getName()) ? peopleContext.getName() : null);

        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value)) {
            return;
        }

        parentMailVsContactMails.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
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

    private static Map<String, Long> bulkAddModuleRecords(String moduleName, List<Map<String, Object>> props, Class<?> clazz, String mailFieldName, String secondaryFieldName) throws Exception {
        if (CollectionUtils.isEmpty(props)) {
            return new HashMap<>();
        }

        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioContext context = V3Util.createRecordList(module, props, null, null);
        List<ModuleBaseWithCustomFields> addedRecords = Constants.getRecordList(context);

        Map<String, Long> recordMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(addedRecords)) {
            List<Map<String, Object>> addedRecordsMap = FieldUtil.getAsMapList(addedRecords, clazz);
            for (Map<String, Object> record : addedRecordsMap) {
                Long id = (Long) record.get("id");
                String primaryContactEmail = record.containsKey(mailFieldName) ? String.valueOf(record.get(mailFieldName)) : null;
                primaryContactEmail = (StringUtils.isEmpty(primaryContactEmail) && record.containsKey(secondaryFieldName)) ? String.valueOf(record.get(secondaryFieldName)) : primaryContactEmail;

                if (id != null && id > 0 && StringUtils.isNotEmpty(primaryContactEmail)) {
                    recordMap.put(primaryContactEmail, id);
                }
            }
        }
        return recordMap;
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

    private static void convertPeopleType(V3PeopleContext.PeopleType peopleType, Map<String, Long> peopleMailVsPeopleId, Map<String, Long> parentMailVsId, Map<String, List<String>> parentMailVsContactMails) throws Exception {
        if (MapUtils.isEmpty(parentMailVsId) || MapUtils.isEmpty(parentMailVsContactMails) || MapUtils.isEmpty(peopleMailVsPeopleId)) {
            return;
        }

        for (Map.Entry<String, List<String>> parentMailVsContactMail : parentMailVsContactMails.entrySet()) {
            String parentMail = parentMailVsContactMail.getKey();
            List<String> contactMails = parentMailVsContactMail.getValue();
            long lookupId = parentMailVsId.getOrDefault(parentMail, -1L);

            // tenant (or) vendor (or) client not added
            if (lookupId < 0) {
                continue;
            }

            List<Long> contactPeopleIds = new ArrayList<>();
            for (String mail : contactMails) {
                long peopleId = peopleMailVsPeopleId.getOrDefault(mail, -1L);
                if (peopleId > 0) {
                    contactPeopleIds.add(peopleId);
                }
            }

            convertPeopleTypeChain(peopleType, contactPeopleIds, lookupId);
        }
    }

    private static void convertPeopleToEmployee(Map<String, Long> peopleMailVsPeopleId, List<String> employeeMails) throws Exception {
        if (MapUtils.isEmpty(peopleMailVsPeopleId) || CollectionUtils.isEmpty(employeeMails)) {
            return;
        }

        List<Long> employeePeopleIds = new ArrayList<>();
        for (String mail : employeeMails) {
            long peopleId = peopleMailVsPeopleId.getOrDefault(mail, -1L);
            if (peopleId > 0) {
                employeePeopleIds.add(peopleId);
            }
        }

        convertPeopleTypeChain(V3PeopleContext.PeopleType.EMPLOYEE, employeePeopleIds, -1);
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
}
