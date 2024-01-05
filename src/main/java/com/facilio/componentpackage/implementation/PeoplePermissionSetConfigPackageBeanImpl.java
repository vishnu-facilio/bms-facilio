package com.facilio.componentpackage.implementation;

import com.facilio.beans.PermissionSetBean;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.componentpackage.utils.PackageUtil;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.permission.context.PermissionSetContext;
import com.facilio.permission.factory.PermissionSetFieldFactory;
import com.facilio.permission.factory.PermissionSetModuleFactory;
import com.facilio.xml.builder.XMLBuilder;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
@Log4j
public class PeoplePermissionSetConfigPackageBeanImpl implements PackageBean<Pair<Long, Long>> {

    PermissionSetBean permissionSetBean = (PermissionSetBean) BeanFactory.lookup("PermissionSetBean");
    List<PermissionSetContext> existingPermissionSetList = permissionSetBean.getPermissionSetsList(-1,-1,null,false);
    Map<Long, PermissionSetContext> idVsExistingPermissionSet = existingPermissionSetList.stream().collect(Collectors.toMap(PermissionSetContext::getId, Function.identity()));

    public PeoplePermissionSetConfigPackageBeanImpl() throws Exception {
    }

    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        Map<Long, Long> peoplePermissionSetConfigMap = new HashMap<>();
        List<Long> permissionSetIds = new ArrayList<>();
        for(PermissionSetContext permissionSet : existingPermissionSetList){
            if(permissionSet != null){
                permissionSetIds.add(permissionSet.getId());
            }
        }
        if(CollectionUtils.isNotEmpty(permissionSetIds)) {
            GenericSelectRecordBuilder selectRecordBuilder = getBuilder();
            selectRecordBuilder.andCondition(CriteriaAPI.getConditionFromList("PERMISSION_SET_ID", "permissionSetId", permissionSetIds, NumberOperators.EQUALS));

            List<Map<String, Object>> props = selectRecordBuilder.get();
            if (CollectionUtils.isNotEmpty(props)) {
                for (Map<String, Object> prop : props) {
                    if (MapUtils.isNotEmpty(prop)) {
                        Long recordId = (Long) prop.get("id");
                        Long permissionSetId = (Long) prop.get("permissionSetId");

                        peoplePermissionSetConfigMap.put(recordId > 0 ? recordId : -1L, permissionSetId > 0 ? permissionSetId : -1L);
                    }
                }
            }
        }
        return peoplePermissionSetConfigMap;
    }

    @Override
    public Map<Long, Pair<Long, Long>> fetchComponents(List<Long> ids) throws Exception {
        Map<Long, Pair<Long, Long>> getPeoplePermissionSetConfigIdVsConfig = new HashMap<>();
        GenericSelectRecordBuilder selectRecordBuilder = getBuilder();
        selectRecordBuilder.andCondition(CriteriaAPI.getConditionFromList("ID", "id", ids, NumberOperators.EQUALS));

        List<Map<String, Object>> props = selectRecordBuilder.get();

        if(CollectionUtils.isNotEmpty(props)){
            for (Map<String, Object> prop : props){
                Long recordId = (Long)prop.get("id");
                recordId = recordId > 0?recordId: -1L;

                Long peopleId = (Long)prop.get("peopleId");


                Long setId = (Long)prop.get("permissionSetId");


                Pair<Long, Long> peopleMailVsSetLinkName = Pair.of(peopleId,setId);
                getPeoplePermissionSetConfigIdVsConfig.put(recordId,peopleMailVsSetLinkName);
            }
        }
        return getPeoplePermissionSetConfigIdVsConfig;
    }

    @Override
    public void convertToXMLComponent(Pair<Long, Long> component, XMLBuilder peoplePermissionSetConfigElement) throws Exception {
        Long peopleId = component.getLeft();
        String peopleMail = PackageUtil.getPeopleMail(peopleId);
        peopleMail = StringUtils.isNotEmpty(peopleMail)? peopleMail : "";

        Long setId = component.getRight();
        PermissionSetContext permissionSet = idVsExistingPermissionSet.get(setId);
        String permissionSetLinkName = permissionSet != null? permissionSet.getLinkName(): "";

        peoplePermissionSetConfigElement.element(PackageConstants.PeoplePermissionSetConfig.PEOPLE_MAIL).text(peopleMail);
        peoplePermissionSetConfigElement.element(PackageConstants.PeoplePermissionSetConfig.PERMISSION_SET_LINK_NAME).text(permissionSetLinkName);
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
        Map<String, Long> uniqueIdentifierVsPeoplePermissionSetConfigIds = new HashMap<>();

        for (Map.Entry<String, XMLBuilder> uniqueIdentifierVsXMLBuilder : uniqueIdVsXMLData.entrySet()) {
            String uniqueIdentifier = uniqueIdentifierVsXMLBuilder.getKey();
            XMLBuilder PeoplePermissionSetConfigComponentElement = uniqueIdentifierVsXMLBuilder.getValue();
            String peopleMail = PeoplePermissionSetConfigComponentElement.getElement(PackageConstants.PeoplePermissionSetConfig.PEOPLE_MAIL).getText();
            String permissionSetLinkName = PeoplePermissionSetConfigComponentElement.getElement(PackageConstants.PeoplePermissionSetConfig.PERMISSION_SET_LINK_NAME).getText();

            Long peopleId = PackageUtil.getPeopleId(peopleMail);
            PermissionSetContext permissionSet = permissionSetBean.getPermissionSet(permissionSetLinkName);
            Long permissionSetId = permissionSet!=null? permissionSet.getId(): -1L;

            deleteRecordsFromPeoplePermissionSets(null, peopleId);

            Map<String,Object> prop = new HashMap<>();
            prop.put("peopleId",peopleId);
            prop.put("permissionSetId",permissionSetId);

            if(peopleId < 0  || permissionSetId < 0){
                LOGGER.info("####Sandbox - Skipping add PeoplePermissionSetConfig since peopleId "+peopleId+" or permissionSetId "+permissionSetId+" is not valid ");
                continue;
            }

            GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                    .table(PermissionSetModuleFactory.getPeoplePermissionSetModule().getTableName())
                    .fields(PermissionSetFieldFactory.getUserPermissionSetsFields());
            long id = insertBuilder.insert(prop);

            uniqueIdentifierVsPeoplePermissionSetConfigIds.put(uniqueIdentifier,id);
        }
        return uniqueIdentifierVsPeoplePermissionSetConfigIds;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        for (Map.Entry<Long, XMLBuilder> recordIdVsXMLBuilder : idVsXMLComponents.entrySet()) {
            Long recordId = recordIdVsXMLBuilder.getKey();
            XMLBuilder scopingConfigComponentElement = recordIdVsXMLBuilder.getValue();
            String peopleMail = scopingConfigComponentElement.getElement(PackageConstants.PeoplePermissionSetConfig.PEOPLE_MAIL).getText();
            String permissionSetLinkName = scopingConfigComponentElement.getElement(PackageConstants.PeoplePermissionSetConfig.PERMISSION_SET_LINK_NAME).getText();

            Long peopleId = PackageUtil.getPeopleId(peopleMail);
            PermissionSetContext permissionSet = permissionSetBean.getPermissionSet(permissionSetLinkName);
            Long permissionSetId = permissionSet!=null? permissionSet.getId(): -1L;

            Map<String,Object> prop = new HashMap<>();
            prop.put("peopleId",peopleId);
            prop.put("permissionSetId",permissionSetId);

            FacilioModule peoplePermissionSetModule = PermissionSetModuleFactory.getPeoplePermissionSetModule();
            GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                    .table(peoplePermissionSetModule.getTableName())
                    .fields(PermissionSetFieldFactory.getUserPermissionSetsFields())
                    .andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(peoplePermissionSetModule),String.valueOf(recordId), NumberOperators.EQUALS));

            updateRecordBuilder.update(prop);
        }
    }

    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {
        deleteRecordsFromPeoplePermissionSets(ids, null);
    }

    private GenericSelectRecordBuilder getBuilder(){
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .select(PermissionSetFieldFactory.getUserPermissionSetsFields())
                .table(PermissionSetModuleFactory.getPeoplePermissionSetModule().getTableName());

        return selectRecordBuilder;
    }

    private void deleteRecordsFromPeoplePermissionSets(List<Long> ids, Long peopleId) throws Exception{
        GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder()
                .table(PermissionSetModuleFactory.getPeoplePermissionSetModule().getTableName());
        if(CollectionUtils.isNotEmpty(ids)) {
            deleteRecordBuilder.andCondition(CriteriaAPI.getConditionFromList("ID", "id", ids, NumberOperators.EQUALS));
            deleteRecordBuilder.delete();
        }else if(peopleId > 0){
            deleteRecordBuilder.andCondition(CriteriaAPI.getCondition("PEOPLE_ID", "peopleId", String.valueOf(peopleId), NumberOperators.EQUALS));
            deleteRecordBuilder.delete();
        }
    }
}
