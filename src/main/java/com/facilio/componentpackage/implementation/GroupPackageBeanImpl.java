package com.facilio.componentpackage.implementation;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.peoplegroup.V3PeopleGroupContext;
import com.facilio.bmsconsoleV3.context.peoplegroup.V3PeopleGroupMemberContext;
import com.facilio.chain.FacilioContext;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.componentpackage.utils.PackageBeanUtil;
import com.facilio.componentpackage.utils.PackageUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.ChainUtil;
import com.facilio.v3.util.V3Util;
import com.facilio.xml.builder.XMLBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.bouncycastle.crypto.util.Pack;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GroupPackageBeanImpl implements PackageBean<V3PeopleGroupContext> {
    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        return getGroupIdVsModuleId();
    }

    @Override
    public Map<Long, V3PeopleGroupContext> fetchComponents(List<Long> ids) throws Exception {
        List<V3PeopleGroupContext> peopleGroups  = getPeopleGroupForIds(ids);

        Map<Long, String> peopleGroupIdVsTeamName = new HashMap<>();
        Map<Long, V3PeopleGroupContext> peopleGroupIdVsPeopleGroupMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(peopleGroups)) {
            for (V3PeopleGroupContext peopleGroupContext : peopleGroups) {
                peopleGroupIdVsPeopleGroupMap.put(peopleGroupContext.getId(), peopleGroupContext);
                peopleGroupIdVsTeamName.put(peopleGroupContext.getGroupId(), peopleGroupContext.getName());
            }
        }
        PackageUtil.addTeamConfigForXML(peopleGroupIdVsTeamName);
        return peopleGroupIdVsPeopleGroupMap;
    }
    @Override
    public void convertToXMLComponent(V3PeopleGroupContext component, XMLBuilder element) throws Exception {
        element.element(PackageConstants.GroupConstants.NAME).text(component.getName());
        element.element(PackageConstants.GroupConstants.DESCRIPTION).text(component.getDescription());
        if(component.getSiteId()!=-1L) {
            element.element(PackageConstants.GroupConstants.SITE_NAME).text(String.valueOf(-1L));
        }
        element.element(PackageConstants.GroupConstants.EMAIL).text(component.getEmail());
        XMLBuilder peopleGroupMember = element.element(PackageConstants.GroupConstants.PEOPLE_GROUP_MEMBERS);
        if (component.getMembers()!=null) {
            for (V3PeopleGroupMemberContext member : component.getMembers()) {
                XMLBuilder memberBuilder = peopleGroupMember.element(PackageConstants.PeopleGroupMembersConstants.PEOPLE);
                memberBuilder.element(PackageConstants.PeopleGroupMembersConstants.EMAIL).text(String.valueOf(member.getPeople().getEmail()));
                peopleGroupMember.addElement(memberBuilder);
            }
        }
        element.element(PackageConstants.GroupConstants.IS_ACTIVE).text(String.valueOf(component.getIsActive()));
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
        return null;
    }

    @Override
    public Map<String, Long> createComponentFromXML(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule module = moduleBean.getModule(FacilioConstants.PeopleGroup.PEOPLE_GROUP);
        Map<String, Long> uniqueIdentifierVsComponentId = new HashMap<>();
        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder groupElement = idVsData.getValue();
            V3PeopleGroupContext peopleGroupContext = constructPeopleGroupFromBuilder(groupElement);
            Map<String, Object> data = addPeopleGroup(idVsData.getKey(), peopleGroupContext);
            FacilioContext context = V3Util.createRecord(module, data, null, null);
            Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
            List<V3PeopleGroupContext> peopleGroupsFromContext = recordMap.get(FacilioConstants.PeopleGroup.PEOPLE_GROUP);
            for (V3PeopleGroupContext peopleGroupFromContext : peopleGroupsFromContext) {
                uniqueIdentifierVsComponentId.put(idVsData.getKey(), peopleGroupFromContext.getId());
            }
        }
        return uniqueIdentifierVsComponentId;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule module = moduleBean.getModule(FacilioConstants.PeopleGroup.PEOPLE_GROUP);
        V3Config v3Config = ChainUtil.getV3Config(module);
        ModuleBaseWithCustomFields oldRecord;
        for (Map.Entry<Long, XMLBuilder> idVsData : idVsXMLComponents.entrySet()) {
            XMLBuilder groupElement = idVsData.getValue();
            long recordId = idVsData.getKey();
            List<?> records = PackageBeanUtil.getModuleDataForId(recordId, module, V3PeopleGroupContext.class);
            if (!records.isEmpty()) {
                oldRecord = (ModuleBaseWithCustomFields) records.get(0);
                V3PeopleGroupContext peopleGroupContext = constructPeopleGroupFromBuilder(groupElement);
                JSONObject jsonPeopleGroupData = updatePeopleGroup(peopleGroupContext);
                V3Util.updateSingleRecord(module, v3Config, oldRecord, jsonPeopleGroupData, null, recordId, null, null, null, null, null, null, null, null, false, null);
            }
        }
    }

    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

    }

    @Override
    public void addPickListConf() throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule peopleGroupModule = modBean.getModule("peopleGroup");
        List<V3PeopleGroupContext> props = (List<V3PeopleGroupContext>) PackageBeanUtil.getModuleData(null, peopleGroupModule, V3PeopleGroupContext.class,false);

        Map<String, Long> groupNameVsGroupId = new HashMap<>();
        if (CollectionUtils.isNotEmpty(props)) {
            props.forEach(group -> groupNameVsGroupId.put(group.getName(), group.getGroupId()));
        }
        PackageUtil.addTeamConfigForContext(groupNameVsGroupId);
    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {
        for (long id : ids) {
            JSONObject data = new JSONObject();
            data.put("peopleGroup", id);
            V3Util.deleteRecords("peopleGroup", data,null,null,false);
        }
    }
    private List<V3PeopleGroupContext> getPeopleGroupForIds(List<Long> ids) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule peopleGroupModule = modBean.getModule(FacilioConstants.PeopleGroup.PEOPLE_GROUP);
        FacilioModule peopleGroupMemberModule = modBean.getModule(FacilioConstants.PeopleGroup.PEOPLE_GROUP_MEMBER);
        FacilioModule peopleModule = modBean.getModule(FacilioConstants.ContextNames.PEOPLE);
        SelectRecordsBuilder<V3PeopleGroupContext> builder = new SelectRecordsBuilder<V3PeopleGroupContext>()
                .table(peopleGroupModule.getTableName())
                .module(peopleGroupModule)
                .beanClass(V3PeopleGroupContext.class)
                .select(modBean.getAllFields(peopleGroupModule.getName()))
                .andCondition(CriteriaAPI.getIdCondition(ids, peopleGroupModule));
        SelectRecordsBuilder<V3PeopleGroupMemberContext> memBuilder = new SelectRecordsBuilder<V3PeopleGroupMemberContext>()
                .table(peopleGroupMemberModule.getTableName())
                .module(peopleGroupMemberModule)
                .beanClass(V3PeopleGroupMemberContext.class)
                .select(modBean.getAllFields(peopleGroupMemberModule.getName()));
        SelectRecordsBuilder<V3PeopleContext> peopleBuilder = new SelectRecordsBuilder<V3PeopleContext>()
                .table(peopleModule.getTableName())
                .module(peopleModule)
                .beanClass(V3PeopleContext.class)
                .select(modBean.getAllFields(peopleModule.getName()));
        List<V3PeopleGroupContext> peopleGroups  = builder.get();
        Map< Long, V3PeopleGroupContext> groupIdVsPeopleGroupContext = peopleGroups.stream().collect(Collectors.toMap( V3PeopleGroupContext::getId, Function.identity()));
        List<V3PeopleGroupMemberContext> peopleGroupMembers = memBuilder.get();
        List<V3PeopleContext> peoples = peopleBuilder.get();
        Map< Long, V3PeopleContext> peopleIdVsPeopleContext = peoples.stream().collect(Collectors.toMap( V3PeopleContext::getId, Function.identity()));
        for (V3PeopleGroupMemberContext member : peopleGroupMembers) {
             if(groupIdVsPeopleGroupContext.containsKey(member.getGroup().getGroupId())) {
                 if(peopleIdVsPeopleContext.containsKey(member.getPeople().getId())) {
                     member.setPeople(peopleIdVsPeopleContext.get(member.getPeople().getId()));
                     if (groupIdVsPeopleGroupContext.get(member.getGroup().getGroupId()).getMembers() == null) {
                         groupIdVsPeopleGroupContext.get(member.getGroup().getGroupId()).setMembers(new ArrayList<>());
                     }
                     groupIdVsPeopleGroupContext.get(member.getGroup().getGroupId()).getMembers().add(member);
                 }
             }
        }
        List<V3PeopleGroupContext> peopleGroupList = new ArrayList<>(groupIdVsPeopleGroupContext.values());
        return peopleGroupList;
    }
    private Map<Long, Long> getGroupIdVsModuleId() throws Exception {
        Map<Long, Long> groupIdVsModuleId = new HashMap<>();
        ModuleBean modBean = Constants.getModBean();
        FacilioModule peopleGroupModule = modBean.getModule("peopleGroup");
        List<V3PeopleGroupContext> props = (List<V3PeopleGroupContext>) PackageBeanUtil.getModuleData(null, peopleGroupModule, V3PeopleGroupContext.class,false);
        if (CollectionUtils.isNotEmpty(props)) {
            for (V3PeopleGroupContext prop : props) {
                groupIdVsModuleId.put( prop.getId(), prop.getModuleId());
            }
        }
        return groupIdVsModuleId;
    }
    private Map<String,Object> addPeopleGroup(String xmlDataKey, V3PeopleGroupContext v3PeopleGroupContext) throws Exception {
        List<Object> peopleContexts = new ArrayList<>();
        for (V3PeopleGroupMemberContext groupMemberContext : v3PeopleGroupContext.getMembers()) {
            JSONObject peopleObject = new JSONObject();
            JSONObject peopleIdData = new JSONObject();
            peopleIdData.put("id", groupMemberContext.getPeople().getId());
            peopleObject.put("people", peopleIdData);
            peopleContexts.add(peopleObject);
        }
        Map<String, Object> peopleGroupData = FieldUtil.getAsProperties(v3PeopleGroupContext);
        peopleGroupData.put(FacilioConstants.PeopleGroup.PEOPLE_GROUP_MEMBER, peopleContexts);
        peopleGroupData.put("xmlDataKey", xmlDataKey);
        return peopleGroupData;
    }
    private JSONObject updatePeopleGroup(V3PeopleGroupContext v3PeopleGroupContext) throws Exception {
        List<Object> peopleContexts = new ArrayList<>();
        for (V3PeopleGroupMemberContext groupMemberContext : v3PeopleGroupContext.getMembers()) {
            JSONObject peopleObject = new JSONObject();
            JSONObject peopleIdData = new JSONObject();
            peopleIdData.put("id", groupMemberContext.getPeople().getId());
            peopleObject.put("people", peopleIdData);
            peopleContexts.add(peopleObject);
        }
        Map<String, Object> peopleGroupData = FieldUtil.getAsProperties(v3PeopleGroupContext);
        peopleGroupData.put(FacilioConstants.PeopleGroup.PEOPLE_GROUP_MEMBER, peopleContexts);
        JSONObject jsonPeopleGroupData = new JSONObject();
        jsonPeopleGroupData.putAll(peopleGroupData);
        return jsonPeopleGroupData;
    }

    private V3PeopleGroupContext constructPeopleGroupFromBuilder(XMLBuilder groupElement) throws Exception {
        String name = groupElement.getElement(PackageConstants.GroupConstants.NAME).getText();
        String description = groupElement.getElement(PackageConstants.GroupConstants.DESCRIPTION).getText();
        String email = groupElement.getElement(PackageConstants.GroupConstants.EMAIL).getText();
        boolean isActive = Boolean.parseBoolean(groupElement.getElement(PackageConstants.GroupConstants.IS_ACTIVE).getText());
        XMLBuilder siteBuilder = groupElement.getElement(PackageConstants.GroupConstants.SITE_NAME);
        long siteId= -1L;
        if(siteBuilder!=null){
            siteId = Long.parseLong(groupElement.getElement(PackageConstants.GroupConstants.SITE_NAME).getText());
        }
        V3PeopleGroupContext peopleGroupContext = new V3PeopleGroupContext();
        peopleGroupContext.setName(name);
        peopleGroupContext.setDescription(description);
        peopleGroupContext.setEmail(email);
        peopleGroupContext.setSiteId(siteId);
        peopleGroupContext.setIsActive(isActive);
        List<V3PeopleGroupMemberContext> peopleGroupMemberContexts = new ArrayList<>();
        XMLBuilder peopleGroupMembersList = groupElement.getElement(PackageConstants.GroupConstants.PEOPLE_GROUP_MEMBERS);
        if(peopleGroupMembersList!=null) {
            List<XMLBuilder> peopleGroupMembers = peopleGroupMembersList.getElementList(PackageConstants.PeopleGroupMembersConstants.PEOPLE);
            for (XMLBuilder people : peopleGroupMembers) {
                V3PeopleGroupMemberContext peopleGroupMemberContext = new V3PeopleGroupMemberContext();
                String peopleEmail = people.getElement(PackageConstants.PeopleGroupMembersConstants.EMAIL).getText();
                long peopleId = PackageUtil.getPeopleId(peopleEmail);
                V3PeopleContext peopleContext = new V3PeopleContext();
                peopleContext.setId(peopleId);
                peopleGroupMemberContext.setPeople(peopleContext);
                peopleGroupMemberContexts.add(peopleGroupMemberContext);
            }
        }
        peopleGroupContext.setMembers(peopleGroupMemberContexts);
        return peopleGroupContext;
    }
}
