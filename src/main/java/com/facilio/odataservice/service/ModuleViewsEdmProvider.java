package com.facilio.odataservice.service;

import com.facilio.modules.fields.FacilioField;
import com.facilio.odataservice.util.ODATAUtil;
import com.facilio.odataservice.util.ODataModuleViewsUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.*;
import org.apache.olingo.commons.api.ex.ODataException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ModuleViewsEdmProvider extends CsdlAbstractEdmProvider {

    public static String NAMESPACE = "MODULE RECORDS";
    public String CONTAINER_NAME ;
    public FullQualifiedName CONTAINER;
    public  List<String> EntityTypesofViewsList;
    public  List<FullQualifiedName> ET_ViewsList_FQN;
    public  List<FullQualifiedName> ES_ViewsList_FQN;
    public  List<String> EntitySetsofViewsList;
    private static final Logger LOGGER = LogManager.getLogger(ModuleViewsEdmProvider.class.getName());

    public ModuleViewsEdmProvider(String moduleName) throws Exception {
        CONTAINER_NAME = moduleName;
        CONTAINER = new FullQualifiedName(NAMESPACE, CONTAINER_NAME);
        EntityTypesofViewsList = ODataModuleViewsUtil.getViews(moduleName);
        EntitySetsofViewsList = EntityTypesofViewsList;
        ET_ViewsList_FQN = ODATAUtil.getFQNLIST(EntityTypesofViewsList,NAMESPACE);
        ES_ViewsList_FQN = ODATAUtil.getFQNLIST(EntitySetsofViewsList, NAMESPACE);
    }
    public CsdlEntityType getEntityType(FullQualifiedName entityTypeName) throws ODataException {
        String viewName = ODATAUtil.splitFQN(entityTypeName);
        CsdlEntityType entityType;
        List<CsdlProperty> csdlPropertyList = new ArrayList<>();
        try {
            Map<String, FacilioField> fieldMap = ODataModuleViewsUtil.getFields(viewName, CONTAINER_NAME);
            csdlPropertyList = ODATAUtil.getEntityTypeProperties(fieldMap, csdlPropertyList, false,0,CONTAINER_NAME);
            // configure EntityType
            entityType = new CsdlEntityType();
            entityType.setName(viewName);
            entityType.setProperties(csdlPropertyList);
        }catch (Exception e){
            LOGGER.error("field map is null for "+viewName);
            return null;
        }
        return entityType;
    }
    @Override
    public CsdlEntitySet getEntitySet(FullQualifiedName entityContainer, String entitySetName) {
        if(entityContainer.equals(CONTAINER)){
            CsdlEntitySet entitySet = new CsdlEntitySet();
            entitySet.setName(entitySetName);
            entitySet.setType(ODATAUtil.getFQN(entitySetName,NAMESPACE));
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
        schema.setNamespace(NAMESPACE);
        List<CsdlEntityType> entityTypes = new ArrayList<>();
        for(FullQualifiedName entityType : ET_ViewsList_FQN) {
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
        for (String view : EntitySetsofViewsList){
            entitySets.add(getEntitySet(CONTAINER, view));
        }
        CsdlEntityContainer entityContainer = new CsdlEntityContainer();
        entityContainer.setName(CONTAINER_NAME);
        entityContainer.setEntitySets(entitySets);
        return entityContainer;
    }
}
