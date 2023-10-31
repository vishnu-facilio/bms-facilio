package com.facilio.componentpackage.implementation;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.relation.context.RelationContext;
import com.facilio.relation.context.RelationMappingContext;
import com.facilio.relation.context.RelationRequestContext;
import com.facilio.v3.context.Constants;
import com.facilio.xml.builder.XMLBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


public class RelationshipPackageBeanImpl implements PackageBean<RelationRequestContext> {
    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        return getRelationIdVsModuleId();
    }

    @Override
    public Map<Long, RelationRequestContext> fetchComponents(List<Long> ids) throws Exception {
        List<RelationRequestContext> relations  = getRelationForIds(ids);
        Map<Long, RelationRequestContext> relationIdVsRelationMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(relations)) {
            relations.forEach(relationRequestContext -> relationIdVsRelationMap.put(relationRequestContext.getId(), relationRequestContext));
        }
        return relationIdVsRelationMap;
    }



    @Override
    public void convertToXMLComponent(RelationRequestContext component, XMLBuilder element) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        element.element(PackageConstants.RelationRequestConstants.NAME).text(component.getName());
        element.element(PackageConstants.RelationRequestConstants.DESCRIPTION).text(component.getDescription());
        element.element(PackageConstants.RelationRequestConstants.FORWARD_RELATION_NAME).text(component.getRelationName());
        element.element(PackageConstants.RelationRequestConstants.REVERSE_RELATION_NAME).text(component.getReverseRelationName());
        element.element(PackageConstants.RelationRequestConstants.RELATION_TYPE).text(String.valueOf(component.getRelationType()));
        if (CollectionUtils.isNotEmpty(Collections.singleton(component.getFromModuleId()))) {
            FacilioModule module = moduleBean.getModule(component.getFromModuleId());
            element.element(PackageConstants.RelationRequestConstants.FROM_MODULE_NAME).text(module.getName());
        }
        if (CollectionUtils.isNotEmpty(Collections.singleton(component.getToModuleId()))) {
            FacilioModule module = moduleBean.getModule(component.getToModuleId());
            element.element(PackageConstants.RelationRequestConstants.TO_MODULE_NAME).text(module.getName());
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
        return null;
    }

    @Override
    public Map<String, Long> createComponentFromXML(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        Map<String, Long> uniqueIdentifierVsComponentId = new HashMap<>();
        List<RelationContext> relations = FieldUtil.getAsBeanListFromMapList(getRelationProps(), RelationContext.class);
        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder relationRequestElement = idVsData.getValue();
            RelationRequestContext relationRequestContext = constructRelationRequestFromBuilder(relationRequestElement);
            boolean containsName = false;
            Long id = -1L;
            for(RelationContext relation : relations) {
                if (relation.getName().equals(relationRequestContext.getName())) {
                    id = relation.getId();
                    containsName = true;
                    break;
                }
            }
            if (!containsName) {
                long relationId = addRelationRequest(relationRequestContext);
                uniqueIdentifierVsComponentId.put(idVsData.getKey(), relationId);
            }else{
                relationRequestContext.setId(id);
                updateRelationRequest(relationRequestContext);
                uniqueIdentifierVsComponentId.put(idVsData.getKey(), id);
            }
        }
        return uniqueIdentifierVsComponentId;
    }

    private List<Map<String, Object>> getRelationProps() throws Exception {
        FacilioModule relationModule = ModuleFactory.getRelationModule();
        List<FacilioField> selectableFields = new ArrayList<FacilioField>() {{
            add(FieldFactory.getNumberField("id", "ID", relationModule));
            add(FieldFactory.getStringField("name", "NAME", relationModule));
        }};
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(relationModule.getTableName())
                .select(selectableFields);
        List<Map<String, Object>> props = builder.get();
        return props;
    }


    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        for (Map.Entry<Long, XMLBuilder> idVsData : idVsXMLComponents.entrySet()) {
            long relationId = idVsData.getKey();
            XMLBuilder relationRequestElement = idVsData.getValue();
            RelationRequestContext relationRequestContext = constructRelationRequestFromBuilder(relationRequestElement);
            relationRequestContext.setId(relationId);
            updateRelationRequest(relationRequestContext);
        }
    }

    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {
        FacilioChain chain = TransactionChainFactory.getDeleteRelationChain();
        for (Long id: ids) {
            FacilioContext context = chain.getContext();
            context.put(FacilioConstants.ContextNames.ID, id);
            chain.execute();
        }
    }

    private RelationRequestContext constructRelationRequestFromBuilder(XMLBuilder relationRequestElement) throws Exception {
        String name = relationRequestElement.getElement(PackageConstants.RelationRequestConstants.NAME).getText();
        String description = relationRequestElement.getElement(PackageConstants.RelationRequestConstants.DESCRIPTION).getText();
        String forward_relation_name = relationRequestElement.getElement(PackageConstants.RelationRequestConstants.FORWARD_RELATION_NAME).getText();
        String reverse_relation_name = relationRequestElement.getElement(PackageConstants.RelationRequestConstants.REVERSE_RELATION_NAME).getText();
        int relation_type = Integer.parseInt(relationRequestElement.getElement(PackageConstants.RelationRequestConstants.RELATION_TYPE).getText());
        RelationRequestContext relationRequestContext = new RelationRequestContext();
        relationRequestContext.setName(name);
        relationRequestContext.setDescription(description);
        relationRequestContext.setRelationName(forward_relation_name);
        relationRequestContext.setReverseRelationName(reverse_relation_name);
        relationRequestContext.setRelationType(relation_type);
        XMLBuilder fromModuleBuilder = relationRequestElement.getElement(PackageConstants.RelationRequestConstants.FROM_MODULE_NAME);
        ModuleBean modBean = Constants.getModBean();
        String fromModuleName = fromModuleBuilder.getText();
        if (modBean.getModule(fromModuleName)!=null) {
            FacilioModule forwardModule = modBean.getModule(fromModuleName);
            relationRequestContext.setFromModuleId(forwardModule.getModuleId());
        }
        XMLBuilder toModuleBuilder = relationRequestElement.getElement(PackageConstants.RelationRequestConstants.TO_MODULE_NAME);
        String toModuleName = toModuleBuilder.getText();
        if (modBean.getModule(toModuleName)!=null) {
            FacilioModule toModule = modBean.getModule(toModuleName);
            relationRequestContext.setToModuleId(toModule.getModuleId());
        }
        return relationRequestContext;
    }

    private Map<Long, Long> getRelationIdVsModuleId() throws Exception {
        Map<Long, Long> relationIdVsModuleId = new HashMap<>();
        List<RelationMappingContext> relationMappings = getRelationIdVsModuleIdProps();
        if (CollectionUtils.isNotEmpty(relationMappings)) {
            for (RelationMappingContext relationMapping : relationMappings) {
                relationIdVsModuleId.put(relationMapping.getRelationId(), relationMapping.getFromModuleId());
            }
        }
        return relationIdVsModuleId;
    }
    private List<RelationMappingContext> getRelationIdVsModuleIdProps() throws Exception {
        FacilioModule relationMappingModule = ModuleFactory.getRelationMappingModule();
        List<FacilioField> relationMappingFields = FieldFactory.getRelationMappingFields();
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .select(relationMappingFields)
                .table(relationMappingModule.getTableName())
                .andCondition(CriteriaAPI.getCondition("POSITION","position", "1", NumberOperators.EQUALS));
        List<RelationMappingContext> relationMappingRecord = FieldUtil.getAsBeanListFromMapList(selectRecordBuilder.get(),RelationMappingContext.class);
        return relationMappingRecord;
    }
    private List<RelationRequestContext> getRelationForIds(List<Long> ids) throws Exception {
        FacilioModule relationModule = ModuleFactory.getRelationModule();
        List<RelationRequestContext> relations = getRelationProps(ids,relationModule);
        return relations;
    }
    private List<RelationRequestContext> getRelationProps(List<Long> ids,FacilioModule module) throws Exception {
        List<FacilioField> relationFields = FieldFactory.getRelationFields();
        List<FacilioField> relationMappingFields = FieldFactory.getRelationMappingFields();
        FacilioModule relationMappingModule = ModuleFactory.getRelationMappingModule();
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(relationFields)
                .andCondition(CriteriaAPI.getIdCondition(ids,module));
        FacilioField relationIdField = FieldFactory.getNumberField("relationId","RELATION_ID",relationMappingModule);
        GenericSelectRecordBuilder mappingBuilder = new GenericSelectRecordBuilder()
                .table(relationMappingModule.getTableName())
                .select(relationMappingFields)
                .andCondition(CriteriaAPI.getCondition(relationIdField,ids,StringOperators.IS));
        List<RelationContext> relations = FieldUtil.getAsBeanListFromMapList(builder.get(), RelationContext.class);
        List<RelationMappingContext> relationMappings = FieldUtil.getAsBeanListFromMapList(mappingBuilder.get(), RelationMappingContext.class);
        Map<Long, RelationContext> relationIdVsRelation = relations.stream().collect(Collectors.toMap(RelationContext::getId, Function.identity()));
        Map<Long, RelationRequestContext> relationIdVsRelationRequest = new HashMap<>();
        for (RelationMappingContext relationMapping : relationMappings) {
            long relationId = relationMapping.getRelationId();
            RelationContext relation = relationIdVsRelation.get(relationId);
            RelationRequestContext relationRequestContext = null;

            if (relationIdVsRelationRequest.containsKey(relationId)) {
                relationRequestContext = relationIdVsRelationRequest.get(relationId);
            }
            if (relationRequestContext == null) {
                relationRequestContext = new RelationRequestContext();
                relationRequestContext.setId(relationId);
                relationRequestContext.setName(relation.getName());
                relationRequestContext.setDescription(relation.getDescription());
            }

            if (relationMapping.getPosition() == 1 && StringUtils.isNotEmpty(relationMapping.getRelationName())) {
                relationRequestContext.setRelationName(relationMapping.getRelationName());
                relationRequestContext.setFromModuleId(relationMapping.getFromModuleId());
                relationRequestContext.setToModuleId(relationMapping.getToModuleId());
                relationRequestContext.setRelationType(relationMapping.getRelationType());
            }

            if (relationMapping.getPosition() == 2 && StringUtils.isNotEmpty(relationMapping.getRelationName())) {
                relationRequestContext.setReverseRelationName(relationMapping.getRelationName());
                relationRequestContext.setFromModuleId(relationMapping.getToModuleId());
                relationRequestContext.setToModuleId(relationMapping.getFromModuleId());
            }

            relationIdVsRelationRequest.put(relationRequestContext.getId(), relationRequestContext);
        }

        List<RelationRequestContext> relationRequestRecords = new ArrayList<>(relationIdVsRelationRequest.values());
        return relationRequestRecords;
    }
    private long addRelationRequest(RelationRequestContext relationRequestContext) throws Exception {
        FacilioChain addAddOrUpdateRelationChain = TransactionChainFactory.getAddOrUpdateRelationChain();
        FacilioContext context = addAddOrUpdateRelationChain.getContext();
        context.put(FacilioConstants.ContextNames.RELATION, relationRequestContext);
        addAddOrUpdateRelationChain.execute();
        RelationRequestContext relationRequestFromContext = (RelationRequestContext) context.get("relation");
        long relationId = relationRequestFromContext.getId();
        return relationId;
    }
    private void updateRelationRequest(RelationRequestContext relationRequestContext) throws Exception {
        FacilioChain addAddOrUpdateRelationChain = TransactionChainFactory.getAddOrUpdateRelationChain();
        FacilioContext context = addAddOrUpdateRelationChain.getContext();
        context.put(FacilioConstants.ContextNames.RELATION, relationRequestContext);
        addAddOrUpdateRelationChain.execute();
    }
}
