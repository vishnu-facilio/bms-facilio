package com.facilio.bmsconsole.ModuleSettingConfig.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.ModuleSettingConfig.annotation.SettingConfig;
import com.facilio.bmsconsole.context.ModuleSettingContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.annotation.Config;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ModuleSettingConfigUtil {

    public static final List<String> COMMON_CONFIGURATION_LIST = Collections.unmodifiableList(initList());
    public static final List<String> HIDE_FROM_SETTING = Arrays.asList("pageBuilder");//the list here will restrict the settings from showing in the module settings setup page

    private static List<String> initList() {
        List<String> configurationNames = new ArrayList<>();

        configurationNames.add(FacilioConstants.SettingConfigurationContextNames.STATE_FLOW);
        configurationNames.add(FacilioConstants.ContextNames.GLIMPSE);

        return configurationNames;
    }

    private static final Map<String, Class> MODULE_CONFIG_MAP = new HashMap<>();
    public static void initModuleSettingHandler(String packageName) throws Exception {

        Reflections reflections = new Reflections(ClasspathHelper.forPackage(packageName), new MethodAnnotationsScanner());
        fillSettingConfigMap(reflections);
    }


    private static void fillSettingConfigMap(Reflections reflections) throws Exception {

        Set<Method> methodsAnnotatedWithSettingConfig = reflections.getMethodsAnnotatedWith(SettingConfig.class);
        for (Method method: methodsAnnotatedWithSettingConfig) {
            validateSettingConfigMethod(method);

            SettingConfig annotation = method.getAnnotation(SettingConfig.class);
            String configName = annotation.value().trim();
            if (configName.isEmpty()) {
                throw new IllegalStateException("Configuration name cannot be empty.");
            }

            Class config = (Class) method.invoke(null, null);
            if (MODULE_CONFIG_MAP.containsKey(configName)) {
                throw new IllegalStateException("Module config already present.");
            }

            MODULE_CONFIG_MAP.put(configName, config);
        }

    }

    public static void validateSettingConfigMethod(Method method) {

        Class<?> declaringClass = method.getDeclaringClass();
        boolean isPresent = declaringClass.isAnnotationPresent(Config.class);
        if (!isPresent) {
            throw new IllegalStateException("Configuration annotation should be part of " + declaringClass.getName() + " Config class.");
        }

        Class<?> returnType = method.getReturnType();

            if (!returnType.equals(Class.class)) {
            throw new IllegalStateException("Return type should be class object.");
        }
    }

    public static Class getModuleConfig(String name){
        return MODULE_CONFIG_MAP.get(name);
    }

    public static Object getModuleConfigurationDetails(String configurationName,Object... args) throws Exception {

        Class classObject = ModuleSettingEnum.moduleConfigMap.get(configurationName) != null ? ModuleSettingEnum.moduleConfigMap.get(configurationName).getConfigurationClass() : null;

        if(classObject==null){
            return null;
        }
        String methodName = "getModuleConfigDetails";
        Class[] parameter = new Class[1];

        parameter[0] = FacilioModule.class;
        Object invokeObj = classObject.newInstance();

        Method method = classObject.getMethod(methodName, parameter);
        Object configDetails =  method.invoke(invokeObj, args);

        return configDetails;
    }

    public static void updateModuleConfigurationStatus(Long moduleId, ModuleSettingContext setting) throws Exception{

        List<FacilioField> moduleConfigurationFields = FieldFactory.getModuleConfigurationFields();
        FacilioModule moduleConfiguration = ModuleFactory.getModuleConfigurationModule();
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(moduleConfigurationFields);
        FacilioField status = fieldsMap.get("status");
        setting.setModuleId(moduleId);

        GenericSelectRecordBuilder selectRecordsBuilder = new GenericSelectRecordBuilder()
                .select(moduleConfigurationFields)
                .table(moduleConfiguration.getTableName())
                .andCondition(CriteriaAPI.getCondition("MODULE_ID", "moduleId", String.valueOf(moduleId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("CONFIGURATION_NAME", "configurationName",setting.getConfigurationName() , StringOperators.IS));

        List<ModuleSettingContext> moduleSettings = FieldUtil.getAsBeanListFromMapList(selectRecordsBuilder.get(),ModuleSettingContext.class);

        if(CollectionUtils.isNotEmpty(moduleSettings)){

            GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                    .table(moduleConfiguration.getTableName())
                    .fields(Collections.singletonList(status))
                    .andCondition(CriteriaAPI.getCondition("MODULE_ID", "moduleId", String.valueOf(moduleId), NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition("CONFIGURATION_NAME", "configurationName",setting.getConfigurationName() , StringOperators.IS));

            Map<String, Object> props = FieldUtil.getAsProperties(setting);

            if(MapUtils.isNotEmpty(props) && props.get("status")==null){
                props.put("status",setting.isStatus());
            }

            updateRecordBuilder.update(props);

        }else {
            insertModuleConfiguration(setting);

        }
    }


    public static List<ModuleSettingContext> getModuleSettingConfigurations(FacilioModule module) throws Exception{

        FacilioModule moduleConfigurationModule = ModuleFactory.getModuleConfigurationModule();
        List<FacilioField> moduleConfigurationFields = FieldFactory.getModuleConfigurationFields();

        GenericSelectRecordBuilder moduleConfigurationBuilder = new GenericSelectRecordBuilder()
                .table(moduleConfigurationModule.getTableName())
                .select(moduleConfigurationFields)
                .andCondition(CriteriaAPI.getCondition("MODULE_ID", "moduleId", String.valueOf(module.getModuleId()), NumberOperators.EQUALS));
        List<Map<String, Object>> moduleConfigProps = moduleConfigurationBuilder.get();

        return FieldUtil.getAsBeanListFromMapList(moduleConfigProps, ModuleSettingContext.class);

    }

    public static void insertModuleConfiguration(ModuleSettingContext setting) throws Exception {

        List<FacilioField> moduleConfigurationFields = FieldFactory.getModuleConfigurationFields();
        FacilioModule moduleConfiguration = ModuleFactory.getModuleConfigurationModule();

        GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
                .fields(moduleConfigurationFields)
                .table(moduleConfiguration.getTableName());

        Map<String, Object> props = FieldUtil.getAsProperties(setting);

        if(MapUtils.isNotEmpty(props) && props.get("status")==null){
            props.put("status",setting.isStatus());
        }

        long moduleConfigurationId = insertRecordBuilder.insert(props);
        setting.setId(moduleConfigurationId);

    }

    public static List<ModuleSettingContext> getModuleConfigDetailsForConfigName(String configName) throws Exception {

        FacilioModule moduleConfigurationModule = ModuleFactory.getModuleConfigurationModule();
        List<FacilioField> moduleConfigurationFields = FieldFactory.getModuleConfigurationFields();

        GenericSelectRecordBuilder moduleConfigurationBuilder = new GenericSelectRecordBuilder()
                .table(moduleConfigurationModule.getTableName())
                .select(moduleConfigurationFields)
                .andCondition(CriteriaAPI.getCondition("CONFIGURATION_NAME", "configurationName", configName, StringOperators.IS));

        List<Map<String, Object>> moduleConfigProps = moduleConfigurationBuilder.get();

        if (CollectionUtils.isNotEmpty(moduleConfigProps)) {
            return FieldUtil.getAsBeanListFromMapList(moduleConfigProps, ModuleSettingContext.class);
        }
        return null;
    }

    public static List<FacilioModule> getConfigEnabledModules(String configName) throws Exception {
        FacilioModule moduleConfigurationModule = ModuleFactory.getModuleConfigurationModule();
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(FieldFactory.getModuleConfigurationFields());

        GenericSelectRecordBuilder moduleConfigurationBuilder = new GenericSelectRecordBuilder()
                .table(moduleConfigurationModule.getTableName())
                .select(Arrays.asList(fieldsMap.get("moduleId")))
                .andCondition(CriteriaAPI.getEqualsCondition(fieldsMap.get("status"), String.valueOf(true)))
                .andCondition(CriteriaAPI.getCondition("CONFIGURATION_NAME", "configurationName", configName, StringOperators.IS));

        List<Map<String, Object>> moduleConfigProps = moduleConfigurationBuilder.get();

        if (CollectionUtils.isNotEmpty(moduleConfigProps)) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            return moduleConfigProps.stream().map(f-> {
                try {
                    return modBean.getModule((long)f.get("moduleId"));
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }).collect(Collectors.toList());
        }
        return null;
    }
    public static void updateSettingContext(List<ModuleSettingContext> updateSettings) throws Exception {
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(FieldFactory.getModuleConfigurationFields());
        List<GenericUpdateRecordBuilder.BatchUpdateContext> batchUpdateList = new ArrayList<>();


        for (ModuleSettingContext setting : updateSettings) {

            GenericUpdateRecordBuilder.BatchUpdateContext updateVal = new GenericUpdateRecordBuilder.BatchUpdateContext();
            updateVal.addWhereValue("moduleId", setting.getModuleId());
            updateVal.addUpdateValue("status", setting.isStatus());
            batchUpdateList.add(updateVal);
        }

        FacilioField moduleIdField = fieldsMap.get("moduleId");
        List<FacilioField> whereFields = new ArrayList<>();
        whereFields.add(moduleIdField);

        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getModuleConfigurationModule().getTableName())
                .fields(Collections.singletonList(fieldsMap.get("status")))
                .andCondition(CriteriaAPI.getEqualsCondition(fieldsMap.get("configurationName"), FacilioConstants.SettingConfigurationContextNames.PAGE_BUILDER));
        updateRecordBuilder.batchUpdate(whereFields, batchUpdateList);
    }

    public static void insertSettingContext(List<ModuleSettingContext> newSettings) throws Exception{
        if(CollectionUtils.isNotEmpty(newSettings)) {
            List<Map<String, Object>> props = FieldUtil.getAsMapList(newSettings, ModuleSettingContext.class);

            GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getModuleConfigurationModule().getTableName())
                    .fields(FieldFactory.getModuleConfigurationFields())
                    .addRecords(props);
            builder.save();
        }
    }

    public static boolean isConfigEnabledForModule(String moduleName, String configName) throws Exception {
        ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modbean.getModule(moduleName);
        boolean status = false;

        if(module != null) {
            Map<Long, ModuleSettingContext> settingContextMap = getModuleListConfigDetailsForConfigName(Collections.singletonList(module.getModuleId()), configName);
            status = settingContextMap != null && settingContextMap.get(module.getModuleId()) != null && settingContextMap.get(module.getModuleId()).isStatus();

        }
        return status;
    }
    public static Map<Long, ModuleSettingContext> getModuleListConfigDetailsForConfigName(List<Long> moduleIds, String configName) throws Exception {

        FacilioModule moduleConfigurationModule = ModuleFactory.getModuleConfigurationModule();
        List<FacilioField> moduleConfigurationFields = FieldFactory.getModuleConfigurationFields();

        GenericSelectRecordBuilder moduleConfigurationBuilder = new GenericSelectRecordBuilder()
                .table(moduleConfigurationModule.getTableName())
                .select(moduleConfigurationFields)
                .andCondition(CriteriaAPI.getConditionFromList("MODULE_ID", "moduleId", moduleIds, NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("CONFIGURATION_NAME", "configurationName", configName, StringOperators.IS));

        List<Map<String, Object>> moduleConfigProps = moduleConfigurationBuilder.get();

        if(CollectionUtils.isNotEmpty(moduleConfigProps)) {
            List<ModuleSettingContext> settingContexts = FieldUtil.getAsBeanListFromMapList(moduleConfigProps, ModuleSettingContext.class);
            return settingContexts.stream().collect(Collectors.toMap(ModuleSettingContext::getModuleId, Function.identity(), (oldValue, newValue) -> newValue));
        }
        return null;
    }

}
