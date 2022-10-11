package com.facilio.bmsconsoleV3.util;

import com.facilio.beans.ModuleBean;
import com.facilio.beans.ValueGeneratorBean;
import com.facilio.bmsconsoleV3.context.scoping.ValueGeneratorContext;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.MultiFieldOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.BaseLookupField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.MultiLookupField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.util.Strings;
import org.reflections.Reflections;
import java.util.*;
import java.util.stream.Collectors;

import static com.facilio.util.FacilioStreamUtil.distinctByKey;

public class ScopingUtil {

    public static ValueGenerator getValueGeneratorForLinkName(String linkName) throws Exception{
        Reflections reflections = new Reflections(linkName);
        Set<Class<? extends ValueGenerator>> valueGeneratorClasses = reflections.getSubTypesOf(ValueGenerator.class);
        if(CollectionUtils.isNotEmpty(valueGeneratorClasses)) {
            for (Class<? extends ValueGenerator> valueGenerator : valueGeneratorClasses) {
                ValueGenerator obj = valueGenerator.newInstance();
                return obj;
            }
        }
        return null;
    }

    public static List<ValueGeneratorContext> getApplicableValueGenerators(String moduleName) throws Exception{
        if(StringUtils.isEmpty(moduleName)){
            throw new IllegalArgumentException("Modulename cannot be null");
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule applicableModule = modBean.getModule(moduleName);
        List<FacilioModule> joinModules = getJoinModules(applicableModule);
        joinModules.add(applicableModule);
        if(CollectionUtils.isNotEmpty(joinModules)){
            List<String> joinModulesName = new ArrayList<>();
            List<Long> joinModuleIds = new ArrayList<>();
            for(FacilioModule module:joinModules){
                if(module.getModuleId() > -1){
                    joinModuleIds.add(module.getModuleId());
                } else if(module.getName() != null){
                    joinModulesName.add(module.getName());
                }
            }
            ValueGeneratorBean valGenBean = (ValueGeneratorBean) BeanFactory.lookup("ValueGeneratorBean");
            return valGenBean.getValueGenerators(joinModulesName,joinModuleIds);
        }

        return null;
    }

    private static List<FacilioModule> getJoinModules(FacilioModule module){
        List<FacilioModule> joinModules = new ArrayList<>();
        if(module != null) {
            FacilioModule extendedModule = module.getExtendModule();
            while (extendedModule != null) {
                joinModules.add(extendedModule);
                extendedModule = extendedModule.getExtendModule();
            }
        }
        return joinModules;
    }

    public static List<Map<String,Object>> getFieldsMapDetails(List<FacilioModule> modules,String lookupModuleName) throws Exception {
        if(StringUtils.isEmpty(lookupModuleName)){
            throw new IllegalArgumentException("Lookup modulename cannot be null");
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<Map<String,Object>> moduleFieldsMap = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(modules)){
            if(CollectionUtils.isNotEmpty(modules)){
                modules = modules.stream()
                        .sorted(Comparator.comparing(FacilioModule::getDisplayName))
                        .collect(Collectors.toList());
            }
            for(FacilioModule module : modules){
                List<Map<String,Object>> fieldDetails = new ArrayList<>();
                List<FacilioField> fieldsList = modBean.getAllFields(module.getName());
                if(CollectionUtils.isNotEmpty(fieldsList)){
                    for(FacilioField field : fieldsList){
                        if(field != null) {
                            if((field instanceof BaseLookupField && ((BaseLookupField) field).getLookupModule().getName().equals(lookupModuleName))) {
                                Map<String,Object> fieldsMap = new HashMap<>();
                                fieldsMap.put("name", field.getName());
                                fieldsMap.put("displayName", field.getDisplayName());
                                fieldsMap.put("dataType", field.getDataType());
                                fieldDetails.add(fieldsMap);
                            }
                        }
                    }
                }
                //site ID field temp handling
                if(FieldUtil.isSiteIdFieldPresent(module) && lookupModuleName.equals("site")){
                    Map<String,Object> fieldsMap = new HashMap<>();
                    fieldsMap.put("name", "siteId");
                    fieldsMap.put("displayName", "Site");
                    fieldsMap.put("dataType", 7);
                    fieldDetails.add(fieldsMap);
                }
                if(CollectionUtils.isNotEmpty(fieldDetails)) {
                    Map<String,Object> moduleMap = new HashMap<>();
                    moduleMap.put("name", module.getName());
                    moduleMap.put("displayName", module.getDisplayName());
                    moduleMap.put("moduleId", module.getModuleId());
                    moduleMap.put("fieldsList", fieldDetails);
                    moduleFieldsMap.add(moduleMap);
                }
            }
        }
        return moduleFieldsMap;
    }

    public static List<FacilioModule> getLookupModulesListForModules(List<FacilioModule> modules) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<Long> moduleIds = new ArrayList<>();
        List<FacilioModule> moduleList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(modules)){
            for(FacilioModule module : modules){
                if(!moduleIds.contains(module.getModuleId()) && !module.isModuleHidden()) {
                    moduleList.add(module);
                    moduleIds.add(module.getModuleId());
                }
                if(module != null) {
                    List<FacilioField> fieldList = modBean.getAllFields(module.getName());
                    if(CollectionUtils.isNotEmpty(fieldList)){
                        for(FacilioField field : fieldList){
                            if(field instanceof BaseLookupField){
                                FacilioModule lookupModule = modBean.getModule(((BaseLookupField) field).getLookupModuleId());
                                if(lookupModule != null && lookupModule.getTypeEnum() == FacilioModule.ModuleType.BASE_ENTITY) {
                                    if(!moduleIds.contains(lookupModule.getModuleId()) && !module.isModuleHidden()) {
                                        moduleList.add(lookupModule);
                                        moduleIds.add(lookupModule.getModuleId());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return moduleList;
    }


    public static List<FacilioModule> getModulesList() throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioModule> moduleList = V3ModuleAPI.getSystemModuleWithFeatureLicenceCheck();

        List<String> otherModulesList = Arrays.asList("users","people");

        //temp handling - can be removed once all fields are moved to people
        for(String modName : otherModulesList){
            moduleList.add(modBean.getModule(modName));
        }
        List<FacilioModule> customModuleList = modBean.getModuleList(FacilioModule.ModuleType.BASE_ENTITY,true);
        if(CollectionUtils.isNotEmpty(customModuleList)) {
            moduleList.addAll(customModuleList);
        }
        if(CollectionUtils.isNotEmpty(moduleList)){
            moduleList = moduleList.stream()
                    .filter(distinctByKey(mod -> mod.getName()))
                    .sorted(Comparator.comparing(FacilioModule::getDisplayName))
                    .collect(Collectors.toList());
        }
        return moduleList;
    }
}