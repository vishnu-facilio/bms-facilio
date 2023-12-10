package com.facilio.componentpackage.implementation;

import com.facilio.beans.ModuleBean;
import com.facilio.beans.ValueGeneratorBean;
import com.facilio.bmsconsoleV3.context.scoping.ValueGeneratorCacheContext;
import com.facilio.bmsconsoleV3.context.scoping.ValueGeneratorContext;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.v3.context.Constants;
import com.facilio.xml.builder.XMLBuilder;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
@Log4j
public class ValueGeneratorPackageBeanImpl implements PackageBean<ValueGeneratorContext> {
    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        Map<Long, Long> valueGeneratorsIds = new HashMap<>();

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getValueGeneratorModule().getTableName())
                .select(FieldFactory.getValueGeneratorFields());
        List<Map<String, Object>> props = selectRecordBuilder.get();

        if (CollectionUtils.isNotEmpty(props)) {
            List<ValueGeneratorContext> valueGenerators = FieldUtil.getAsBeanListFromMapList(props, ValueGeneratorContext.class);
            for(ValueGeneratorContext valGen : valueGenerators) {
                valueGeneratorsIds.put(valGen.getId(), -1L);
            }
            return valueGeneratorsIds;
        }
        return null;
    }

    @Override
    public Map<Long, ValueGeneratorContext> fetchComponents(List<Long> ids) throws Exception {
        Map<Long, ValueGeneratorContext> valueGeneratorIdVsContext = new HashMap<>();

        ValueGeneratorBean valGenBean = (ValueGeneratorBean) BeanFactory.lookup("ValueGeneratorBean");
        List<ValueGeneratorCacheContext> valueGenerators = valGenBean.getValueGenerators(ids);

        valueGeneratorIdVsContext = valueGenerators.stream().filter(scope -> ids.contains(scope.getId())).collect(Collectors.toMap(ValueGeneratorContext::getId, Function.identity()));
        return valueGeneratorIdVsContext;
    }

    @Override
    public void convertToXMLComponent(ValueGeneratorContext component, XMLBuilder valueGeneratorElement) throws Exception {
        valueGeneratorElement.element(PackageConstants.ValueGeneratorConstants.DISPLAY_NAME).text(component.getDisplayName());
        valueGeneratorElement.element(PackageConstants.ValueGeneratorConstants.LINK_NAME).text(component.getLinkName());
        valueGeneratorElement.element(PackageConstants.ValueGeneratorConstants.VALUE_GENERATOR_TYPE).text(component.getValueGeneratorType().getValue());
        valueGeneratorElement.element(PackageConstants.ValueGeneratorConstants.IS_HIDDEN).text(String.valueOf(component.getIsHidden()));
        valueGeneratorElement.element(PackageConstants.ValueGeneratorConstants.IS_SYSTEM).text(String.valueOf(component.getIsSystem()));
        valueGeneratorElement.element(PackageConstants.ValueGeneratorConstants.IS_CONSTANT).text(String.valueOf(component.getIsConstant()));
        valueGeneratorElement.element(PackageConstants.ValueGeneratorConstants.SPECIAL_MODULE_NAME).text(component.getSpecialModuleName());
        valueGeneratorElement.element(PackageConstants.ValueGeneratorConstants.OPERATOR_ID).text(String.valueOf(component.getOperatorId()));

        Long moduleId = component.getModuleId();
        String moduleName = null;
        if(moduleId != null && moduleId>0) {
            FacilioModule module = Constants.getModBean().getModule(moduleId);
            moduleName = module != null ? module.getName() : "";
        }

        valueGeneratorElement.element(PackageConstants.ValueGeneratorConstants.MODULE_NAME).text(moduleName);
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
        ValueGeneratorBean valGenBean = (ValueGeneratorBean) BeanFactory.lookup("ValueGeneratorBean");
        Map<String, Long> uniqueIdentifierVsValueGeneratorId = new HashMap<>();
        List<String> defaultValueGenerator = PackageConstants.ValueGeneratorConstants.DEFAULT_CREATED_VALUE_GENERATOR;

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            String uniqueIdentifier = idVsData.getKey();
            XMLBuilder valueGeneratorElement = idVsData.getValue();
            String linkName = valueGeneratorElement.getElement(PackageConstants.ValueGeneratorConstants.LINK_NAME).getText();

            if (CollectionUtils.isNotEmpty(defaultValueGenerator) && defaultValueGenerator.contains(linkName)) {
                ValueGeneratorContext valueGenerator = valGenBean.getValueGenerator(linkName);
                if (valueGenerator != null) {
                    uniqueIdentifierVsValueGeneratorId.put(uniqueIdentifier, valueGenerator.getId());
                } else {
                    LOGGER.info("###Sandbox - ValueGenerator with linkName not found - " + linkName);
                }
            }
        }
        return uniqueIdentifierVsValueGeneratorId;
    }

    @Override
    public Map<String, Long> createComponentFromXML(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        ValueGeneratorBean valGenBean = (ValueGeneratorBean) BeanFactory.lookup("ValueGeneratorBean");
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Reflections reflections = new Reflections("com.facilio.modules");
        Set<Class<? extends ValueGenerator>> valueGeneratorClasses = reflections.getSubTypesOf(ValueGenerator.class);
        List<String> defaultCreatedValueGenerator = PackageConstants.ValueGeneratorConstants.DEFAULT_CREATED_VALUE_GENERATOR;

        Map<String,ValueGenerator> linkNameVsValueGeneratorClass = new HashMap<>();
        for (Class<? extends ValueGenerator> valueGenerator : valueGeneratorClasses) {
            ValueGenerator obj = valueGenerator.newInstance();
            linkNameVsValueGeneratorClass.put(obj.getLinkName(),obj);
        }

        Map<String, Long> uniqueIdentifierVsValueGeneratorIds = new HashMap<>();
        List<ValueGeneratorContext> ValueGeneratorList = new ArrayList<>();
        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder valueGeneratorElement = idVsData.getValue();
            if (valueGeneratorElement != null) {
                String linkName = valueGeneratorElement.getElement(PackageConstants.ValueGeneratorConstants.LINK_NAME).getText();

                if (!defaultCreatedValueGenerator.contains(linkName) && linkNameVsValueGeneratorClass.containsKey(linkName)) {
                    ValueGenerator valueGeneratorObject = linkNameVsValueGeneratorClass.get(linkName);

                    String moduleName = valueGeneratorElement.getElement(PackageConstants.ValueGeneratorConstants.MODULE_NAME).getText();
                    String specialModuleName = valueGeneratorElement.getElement(PackageConstants.ValueGeneratorConstants.SPECIAL_MODULE_NAME).getText();
                    if (StringUtils.isNotEmpty(moduleName) || StringUtils.isNotEmpty(specialModuleName)) {
                        ValueGeneratorContext valueGeneratorContext = new ValueGeneratorContext();
                        valueGeneratorContext.setLinkName(valueGeneratorObject.getLinkName());
                        valueGeneratorContext.setDisplayName(valueGeneratorObject.getValueGeneratorName());
                        valueGeneratorContext.setIsConstant(false);
                        valueGeneratorContext.setIsSystem(true);
                        valueGeneratorContext.setIsHidden(valueGeneratorObject.getIsHidden());
                        if (StringUtils.isNotEmpty(moduleName)){
                            FacilioModule module = modBean.getModule(moduleName);
                            if (module!=null && module.getModuleId() > 0) {
                                valueGeneratorContext.setModuleId(module.getModuleId());
                            }
                        } else if(StringUtils.isNotEmpty(specialModuleName)){
                            valueGeneratorContext.setSpecialModuleName(specialModuleName);
                        }
                        String valueGeneratorType = valueGeneratorElement.getElement(PackageConstants.ValueGeneratorConstants.VALUE_GENERATOR_TYPE).getText();
                        valueGeneratorContext.setValueGeneratorType(ValueGeneratorContext.ValueGeneratorType.valueOf(valueGeneratorType));
                        valueGeneratorContext.setOperatorId(valueGeneratorObject.getOperatorId());

                        ValueGeneratorList.add(valueGeneratorContext);
                    }
                }
            }
        }
        List<Map<String, Object>> insertedId = valGenBean.addValueGenerators(ValueGeneratorList);

        Map<String, Long> linkNameVsId = new HashMap<>();
        if(CollectionUtils.isNotEmpty(insertedId)){
            for(Map<String, Object> insertedProp : insertedId){
                String linkName = String.valueOf(insertedProp.get("linkName"));
                Long id = (Long) insertedProp.get("id");
                id = id!=null?id:-1L;
                linkNameVsId.put(linkName,id);
            }
        }
        if(MapUtils.isNotEmpty(linkNameVsId)) {
            for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
                XMLBuilder valueGeneratorElement = idVsData.getValue();
                if (valueGeneratorElement != null) {
                    String linkName = valueGeneratorElement.getElement(PackageConstants.ValueGeneratorConstants.LINK_NAME).getText();
                    if(linkNameVsId.containsKey(linkName) && linkNameVsId.get(linkName) > 0) {
                        uniqueIdentifierVsValueGeneratorIds.put(idVsData.getKey(), linkNameVsId.get(linkName));
                    }
                }
            }
        }

        return uniqueIdentifierVsValueGeneratorIds;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

    }

    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {

    }

}
