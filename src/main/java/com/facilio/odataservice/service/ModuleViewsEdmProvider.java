package com.facilio.odataservice.service;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.modules.fields.FacilioField;
import com.facilio.odataservice.util.ODATAUtil;
import com.facilio.odataservice.util.ODataModuleViewsUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.*;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlConstantExpression;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlExpression;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlPropertyValue;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlRecord;
import org.apache.olingo.commons.api.ex.ODataException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ModuleViewsEdmProvider extends CsdlAbstractEdmProvider {

    public static String NAMESPACE = "MODULE RECORDS";
    public static String CONTAINER_NAME ;
    public static FullQualifiedName CONTAINER;
    public static List<String> EntityTypesofViewsList;
    public static List<FullQualifiedName> ET_ViewsList_FQN;
    public static List<FullQualifiedName> ES_ViewsList_FQN;
    public static List<String> EntitySetsofViewsList;
    private static final Logger LOGGER = LogManager.getLogger(ModuleViewsEdmProvider.class.getName());

    public ModuleViewsEdmProvider(String moduleName) throws Exception {
        CONTAINER_NAME = moduleName;
        ODATAUtil.setModuleName(moduleName);
        ODataModuleViewsUtil.setModuleName(moduleName);
        CONTAINER = new FullQualifiedName(NAMESPACE, CONTAINER_NAME);
        EntityTypesofViewsList = ODataModuleViewsUtil.getViewNames(moduleName);
        EntitySetsofViewsList = EntityTypesofViewsList;
        ET_ViewsList_FQN = ODATAUtil.getFQNLIST(EntityTypesofViewsList);
        ES_ViewsList_FQN = ODATAUtil.getFQNLIST(EntitySetsofViewsList);
    }
    public CsdlEntityType getEntityType(FullQualifiedName entityTypeName) throws ODataException {

        String viewName = ODATAUtil.splitFQN(entityTypeName);
        CsdlEntityType entityType;
        List<CsdlProperty> csdlPropertyList = new ArrayList<>();
        try {
            Map<String, FacilioField> fieldMap = ODataModuleViewsUtil.getFields(viewName, CONTAINER_NAME);
            csdlPropertyList = ODATAUtil.getEntityTypeProperties(fieldMap, csdlPropertyList, false,0);
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
            entitySet.setType(ODATAUtil.getFQN(entitySetName));
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
