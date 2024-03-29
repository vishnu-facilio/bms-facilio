package com.facilio.odataservice.service;

import com.facilio.modules.fields.FacilioField;
import com.facilio.odataservice.util.ODATAUtil;
import com.facilio.odataservice.util.ODataReadingsUtil;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.*;
import org.apache.olingo.commons.api.ex.ODataException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReadingsEdmProvider extends CsdlAbstractEdmProvider {

    public String NAMESPACE = "READING RECORDS";
    public String CONTAINER_NAME = "Readings";
    public FullQualifiedName CONTAINER = new FullQualifiedName(NAMESPACE, CONTAINER_NAME);
    public List<String> EntityTypesofViewsList;
    public ReadingsEdmProvider(){
        try {
            EntityTypesofViewsList = ODataReadingsUtil.getReadingViewsList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public CsdlEntityType getEntityType(FullQualifiedName entityTypeName) throws ODataException {

        String viewName = ODATAUtil.splitFQN(entityTypeName);
        CsdlEntityType entityType;
        List<CsdlProperty> csdlPropertyList = new ArrayList<>();
        try {
            int readingType= ODataReadingsUtil.getReadingType(viewName);
            Map<String, FacilioField> fieldMap = ODataReadingsUtil.getFields(viewName);
            csdlPropertyList = ODATAUtil.getEntityTypeProperties(fieldMap, csdlPropertyList, true,readingType, CONTAINER_NAME);
            // configure EntityType
            entityType = new CsdlEntityType();
            entityType.setName(viewName);
            entityType.setProperties(csdlPropertyList);
        }catch (Exception e){
            return null;
        }
        return entityType;
    }
    @Override
    public CsdlEntitySet getEntitySet(FullQualifiedName entityContainer, String entitySetName) throws ODataException {
        if(entityContainer.equals(CONTAINER)){
            CsdlEntitySet entitySet = new CsdlEntitySet();
            entitySet.setName(entitySetName);
            entitySet.setType(ODATAUtil.getFQN(entitySetName, NAMESPACE));
            return entitySet;
        }
        return null;
    }

    @Override
    public CsdlEntityContainerInfo getEntityContainerInfo(FullQualifiedName entityContainerName) throws ODataException {
        if(entityContainerName == null || entityContainerName.equals(CONTAINER)){
            CsdlEntityContainerInfo entityContainerInfo = new CsdlEntityContainerInfo();
            entityContainerInfo.setContainerName(CONTAINER);
            return entityContainerInfo;
        }
        return null;
    }

    @Override
    public List<CsdlSchema> getSchemas() throws ODataException {
        CsdlSchema schema = new CsdlSchema();
        List<FullQualifiedName> ET_ViewsList_FQNn = ODATAUtil.getFQNLIST(EntityTypesofViewsList, NAMESPACE);
        schema.setNamespace(NAMESPACE);
        List<CsdlEntityType> entityTypes = new ArrayList<>();
        for(FullQualifiedName entityType : ET_ViewsList_FQNn) {
            entityTypes.add(getEntityType(entityType));
        }
        schema.setEntityTypes(entityTypes);
        schema.setEntityContainer(getEntityContainer());
        List<CsdlSchema> schemas = new ArrayList<>();
        schemas.add(schema);
        return schemas;
    }

    @Override
    public CsdlEntityContainer getEntityContainer() throws ODataException {
        List<CsdlEntitySet> entitySets = new ArrayList<CsdlEntitySet>();
        List<String> EntitySetsofViewsList = EntityTypesofViewsList;
        for (String view : EntitySetsofViewsList){
            entitySets.add(getEntitySet(CONTAINER, view));
        }
        CsdlEntityContainer entityContainer = new CsdlEntityContainer();
        entityContainer.setName(CONTAINER_NAME);
        entityContainer.setEntitySets(entitySets);
        return entityContainer;
    }
}
