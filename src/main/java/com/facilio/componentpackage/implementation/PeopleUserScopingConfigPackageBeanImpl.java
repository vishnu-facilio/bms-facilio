package com.facilio.componentpackage.implementation;

import com.facilio.beans.UserScopeBean;
import com.facilio.bmsconsole.context.ScopingContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.componentpackage.utils.PackageUtil;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.xml.builder.XMLBuilder;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j
public class PeopleUserScopingConfigPackageBeanImpl implements PackageBean<Map<String, String>> {

    UserScopeBean userScopeBean = (UserScopeBean) BeanFactory.lookup("UserScopeBean");
    List<ScopingContext>allUserScopingList = userScopeBean.getUserScopingList(null,-1,-1);
    Map<Long, ScopingContext> scopingIdIdVsContext = allUserScopingList.stream().collect(Collectors.toMap(ScopingContext::getId, Function.identity()));

    public PeopleUserScopingConfigPackageBeanImpl() throws Exception {
    }

    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        Map<Long, Long> peopleScopingConfigIdVsUserScopingId = new HashMap<>();
        List<Long> existingPeople = V3PeopleAPI.getAllPeople().stream().map(V3PeopleContext::getId).collect(Collectors.toList());

        List<Map<String, Object>> props = getPeopleUserScopingIds(existingPeople,null);
        if (CollectionUtils.isNotEmpty(props)) {
            for(Map<String, Object> prop : props) {
                if (prop != null && prop.containsKey("userScopingId")) {
                    Long userScopingId = (Long) prop.get("userScopingId");
                    Long peopleUserScopingConfigId = (Long) prop.get("id");
                    if(userScopingId != null && userScopingId > 0 && peopleUserScopingConfigId != null && peopleUserScopingConfigId > 0) {
                        peopleScopingConfigIdVsUserScopingId.put(peopleUserScopingConfigId, userScopingId);
                    }
                }
            }
        }
        return peopleScopingConfigIdVsUserScopingId;
    }

    @Override
    public Map<Long, Map<String, String>> fetchComponents(List ids) throws Exception {
        Map<Long, Map<String, String>> peopleUserScopingConfigIdVsObject = new HashMap<>();
        List<Map<String, Object>> props = getPeopleUserScopingIds(null,ids);


        if(CollectionUtils.isNotEmpty(props)){
            for(Map<String, Object> prop : props){
                Map<String, String> configMap = new HashMap<>();
                if(prop != null && prop.containsKey("userScopingId") && prop.containsKey("peopleId")) {
                    Long userScopingId = Long.parseLong(prop.get("userScopingId").toString());
                    String linkName = scopingIdIdVsContext.get(userScopingId).getLinkName();
                    Long peopleId = Long.parseLong(prop.get("peopleId").toString());
                    String peopleMail = PackageUtil.getPeopleMail(peopleId);
                    Long id = (Long) prop.get("id");

                    configMap.put(PackageConstants.PeopleUserScopingConstants.PEOPLE_MAIL, StringUtils.isNotEmpty(peopleMail)? peopleMail : "" );
                    configMap.put(PackageConstants.PeopleUserScopingConstants.SCOPE_LINK_NAME, StringUtils.isNotEmpty(linkName)? linkName : "");

                    peopleUserScopingConfigIdVsObject.put(id,configMap);
                }
            }
        }
        return peopleUserScopingConfigIdVsObject;
    }

    @Override
    public void convertToXMLComponent(Map<String, String> peopleUserScopingComponent, XMLBuilder peopleUserScopingConfigElement) throws Exception {
        if(MapUtils.isNotEmpty(peopleUserScopingComponent)){
            String peopleMail = peopleUserScopingComponent.get(PackageConstants.PeopleUserScopingConstants.PEOPLE_MAIL);
            String scopeLinkName = peopleUserScopingComponent.get(PackageConstants.PeopleUserScopingConstants.SCOPE_LINK_NAME);

            peopleUserScopingConfigElement.element(PackageConstants.PeopleUserScopingConstants.PEOPLE_MAIL).text(peopleMail);
            peopleUserScopingConfigElement.element(PackageConstants.PeopleUserScopingConstants.SCOPE_LINK_NAME).text(scopeLinkName);
        }
    }

    @Override
    public Map<String, String> validateComponentToCreate(List components) throws Exception {
        return null;
    }
    @Override
    public List<Long> getDeletedComponentIds(List componentIds) throws Exception {
        return null;
    }

    @Override
    public Map<String, Long> getExistingIdsByXMLData(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
//        Map<String, Long> uniqueIdentifierVsPeopleUserScopingConfigId = new HashMap<>();
//
//        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
//            String uniqueIdentifier = idVsData.getKey();
//            XMLBuilder peopleUserScopingConfigXMLComponent = idVsData.getValue();
//            long peopleUserScopingConfigId = Long.parseLong(peopleUserScopingConfigXMLComponent.getElement(PackageConstants.PeopleUserScopingConstants.ID).getText());
//
//            if (peopleUserScopingConfigId > 0) {
//                uniqueIdentifierVsPeopleUserScopingConfigId.put(uniqueIdentifier, peopleUserScopingConfigId);
//            } else {
//                LOGGER.info("###Sandbox - PeopleUserScopingConfig with id not found - " + peopleUserScopingConfigId);
//            }
//        }
//        return uniqueIdentifierVsPeopleUserScopingConfigId;
        return null;
    }

    @Override
    public Map<String, Long> createComponentFromXML(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        Map<String, Long> uniqueIdentifierVsPeopleUserScopingConfigIds = new HashMap<>();
        UserScopeBean userScopeBean = (UserScopeBean) BeanFactory.lookup("UserScopeBean");
        for (Map.Entry<String, XMLBuilder> uniqueIdentifierVsXMLBuilder : uniqueIdVsXMLData.entrySet()) {
            String uniqueIdentifier = uniqueIdentifierVsXMLBuilder.getKey();
            XMLBuilder PeopleUserScopingConfigComponentElement = uniqueIdentifierVsXMLBuilder.getValue();

            String peopleMail = PeopleUserScopingConfigComponentElement.getElement(PackageConstants.PeopleUserScopingConstants.PEOPLE_MAIL).getText();
            Long peopleId = PackageUtil.getPeopleId(peopleMail);

            String scopeLinkName = PeopleUserScopingConfigComponentElement.getElement(PackageConstants.PeopleUserScopingConstants.SCOPE_LINK_NAME).getText();
            ScopingContext scope = userScopeBean.getScopingForLinkname(scopeLinkName);
            Long scopeId = scope !=null? scope.getId() : -1L;

            long recordId = userScopeBean.updatePeopleScoping(peopleId, scopeId);

            uniqueIdentifierVsPeopleUserScopingConfigIds.put(uniqueIdentifier, recordId);
        }
        return uniqueIdentifierVsPeopleUserScopingConfigIds;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        if(MapUtils.isNotEmpty(idVsXMLComponents)) {
            UserScopeBean userScopeBean = (UserScopeBean) BeanFactory.lookup("UserScopeBean");
            for (Map.Entry<Long, XMLBuilder> idVsXMLBuilder : idVsXMLComponents.entrySet()) {
                XMLBuilder PeopleUserScopingConfigComponentElement = idVsXMLBuilder.getValue();

                String peopleMail = PeopleUserScopingConfigComponentElement.element(PackageConstants.PeopleUserScopingConstants.PEOPLE_MAIL).getText();
                Long peopleId = PackageUtil.getPeopleId(peopleMail);

                String scopeLinkName = PeopleUserScopingConfigComponentElement.element(PackageConstants.PeopleUserScopingConstants.SCOPE_LINK_NAME).getText();
                ScopingContext scope = userScopeBean.getScopingForLinkname(scopeLinkName);
                Long scopeId = scope !=null? scope.getId() : -1L;

                userScopeBean.updatePeopleScoping(peopleId, scopeId);
            }
        }
    }
    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {
        FacilioModule module = ModuleFactory.getPeopleUserScopingModule();

        GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getPeopleUserScopingModule().getTableName())
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(module), ids,NumberOperators.EQUALS));

        deleteRecordBuilder.delete();
    }

    private List<Map<String, Object>> getPeopleUserScopingIds(List<Long> peopleIds, List<Long> recordIds) throws Exception {
        if(CollectionUtils.isNotEmpty(peopleIds) || CollectionUtils.isNotEmpty(recordIds)) {
            GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                    .select(FieldFactory.getPeopleUserScopingFields())
                    .table(ModuleFactory.getPeopleUserScopingModule().getTableName());

            if(CollectionUtils.isNotEmpty(peopleIds)) {
                selectRecordBuilder.andCondition(CriteriaAPI.getConditionFromList("PEOPLE_ID", "peopleId", peopleIds, NumberOperators.EQUALS));
            }else if(CollectionUtils.isNotEmpty(recordIds)){
                FacilioModule module = ModuleFactory.getPeopleUserScopingModule();

                selectRecordBuilder.andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(module), recordIds, NumberOperators.EQUALS));
            }

            List<Map<String, Object>> props = selectRecordBuilder.get();

            return props;
        }
        return new ArrayList<>();
    }
}
