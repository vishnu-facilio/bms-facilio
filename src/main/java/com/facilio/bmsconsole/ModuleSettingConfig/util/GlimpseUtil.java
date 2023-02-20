package com.facilio.bmsconsole.ModuleSettingConfig.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.ModuleSettingConfig.context.GlimpseContext;
import com.facilio.bmsconsole.ModuleSettingConfig.context.GlimpseFieldContext;
import com.facilio.bmsconsole.context.ModuleSettingContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.*;
import java.util.stream.Collectors;

public class GlimpseUtil {

    public static void deleteGlimpseFields(Long moduleId) throws Exception {

        FacilioModule glimpseModule = ModuleFactory.getGlimpseModule();
        FacilioModule glimpseFieldModule = ModuleFactory.getGlimpseFieldsModule();

        GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder()
                .table(glimpseFieldModule.getTableName())
                .innerJoin(glimpseModule.getTableName(), false)
                .on("Glimpse_Fields.GLIMPSE_ID=Glimpse.ID")
                .andCondition(CriteriaAPI.getCondition("MODULE_ID", "moduleId", String.valueOf(moduleId), NumberOperators.EQUALS));

        deleteRecordBuilder.delete();

    }

    public static List<FacilioField> getGlimpseSelectableFields() throws Exception{

        Map<String,FacilioField> glimpseFieldsFieldsMap = FieldFactory.getAsMap(FieldFactory.getGlimpseFieldsFields());
        Map<String,FacilioField> glimpseFieldsMap = FieldFactory.getAsMap(FieldFactory.getGlimpseFields());

        FacilioField glimpseSequenceNumberField = glimpseFieldsFieldsMap.get("sequenceNumber");
        FacilioField glimpseFieldIdField = glimpseFieldsFieldsMap.get("fieldId");
        FacilioField glimpseModifiedTimeField = glimpseFieldsMap.get("modifiedTime");
        FacilioField glimpseFieldNameField = glimpseFieldsFieldsMap.get("fieldName");
        FacilioField glimpseIdField = glimpseFieldsMap.get("glimpseId");
        FacilioField glimpseActiveField = glimpseFieldsMap.get("active");

        List<FacilioField> selectFields = new ArrayList<>();
        selectFields.add(glimpseSequenceNumberField);
        selectFields.add(glimpseModifiedTimeField);
        selectFields.add(glimpseFieldIdField);
        selectFields.add(glimpseFieldNameField);
        selectFields.add(glimpseIdField);
        selectFields.add(glimpseActiveField);

        return selectFields;

    }

    private static boolean validateGlimpseFields(List<GlimpseFieldContext> glimpseFields, String moduleName) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> allFields = modBean.getAllFields(moduleName);

        List<Long> fieldIds = allFields.stream().map(FacilioField::getFieldId).collect(Collectors.toList());

        for(GlimpseFieldContext glimpseFieldContext : glimpseFields){
            if(glimpseFieldContext.getFieldId()>0 && !fieldIds.contains(glimpseFieldContext.getFieldId())){
                return false;
            }else if (glimpseFieldContext.getFieldId()<0 && glimpseFieldContext.getFieldName() != null && !glimpseFieldContext.getFieldName().equals("siteId")){
                return false;
            }
        }

        return true;
    }

    public static List<GlimpseContext> addGlimpse(String moduleName, List<GlimpseContext> glimpse) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        FacilioUtil.throwIllegalArgumentException(module == null, "Invalid module while getting module summary");
        FacilioModule glimpseModule = ModuleFactory.getGlimpseModule();
        FacilioModule glimpseFieldsModule = ModuleFactory.getGlimpseFieldsModule();
        List<FacilioField> glimpseFieldsList = FieldFactory.getGlimpseFields();
        List<FacilioField> glimpseFieldsFields = FieldFactory.getGlimpseFieldsFields();

        long moduleId = module.getModuleId();

        FacilioUtil.throwIllegalArgumentException(!checkIsGlimpseEnabled(moduleId), "Quick Summary is not enabled for " +moduleName+" module");

        for(GlimpseContext glimpseContext : glimpse) {

            List<GlimpseFieldContext> glimpseFields = new ArrayList<>(glimpseContext.getConfigurationFields());

            if (CollectionUtils.isEmpty(glimpseFields)) {
                return new ArrayList<>();
            }
            GlimpseUtil.deleteGlimpseFields(moduleId);

            glimpseContext.setModifiedTime(System.currentTimeMillis());
            glimpseContext.setModuleId(moduleId);


            final long glimpseId;

            boolean isValidate = GlimpseUtil.validateGlimpseFields(glimpseFields, moduleName);
            FacilioUtil.throwIllegalArgumentException(!isValidate, "Invalid module field while getting module summary");

            GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                    .select(glimpseFieldsList)
                    .table(glimpseModule.getTableName())
                    .andCondition(CriteriaAPI.getCondition("MODULE_ID", "moduleId", String.valueOf(moduleId), NumberOperators.EQUALS));
            List<Map<String, Object>> summaryProps = selectRecordBuilder.get();

            if (CollectionUtils.isNotEmpty(summaryProps)) {
                List<GlimpseContext> glimpseContexts = FieldUtil.getAsBeanListFromMapList(summaryProps, GlimpseContext.class);
                glimpseId = glimpseContexts.get(0).getGlimpseId();
            } else {
                GenericInsertRecordBuilder glimpseInsertRecordBuilder = new GenericInsertRecordBuilder()
                        .table(glimpseModule.getTableName())
                        .fields(glimpseFieldsList);
                Map<String, Object> summaryInsertProps = FieldUtil.getAsProperties(glimpseContext);
                glimpseId = glimpseInsertRecordBuilder.insert(summaryInsertProps);
            }

            glimpseFields.forEach(glimpseFieldContext -> glimpseFieldContext.setGlimpseId(glimpseId));

            GenericInsertRecordBuilder glimpseFieldsInsertRecordBuilder = new GenericInsertRecordBuilder()
                    .table(glimpseFieldsModule.getTableName())
                    .fields(glimpseFieldsFields);
            List<Map<String, Object>> summaryFieldsProps = FieldUtil.getAsMapList(glimpseFields, FacilioField.class);
            glimpseFieldsInsertRecordBuilder.addRecords(summaryFieldsProps);
            glimpseFieldsInsertRecordBuilder.save();

            List<Map<String, Object>> glimpseInsertRecordBuilderValue = glimpseFieldsInsertRecordBuilder.getRecords();
            List<GlimpseFieldContext> glimpseFieldsContext = FieldUtil.getAsBeanListFromMapList(glimpseInsertRecordBuilderValue, GlimpseFieldContext.class);

            glimpseContext.setConfigurationFields(glimpseFieldsContext);
            glimpseContext.setGlimpseId(glimpseId);
            glimpseContext.setOrgId(AccountUtil.getCurrentOrg().getOrgId());

        }

        return glimpse;
    }

    private static boolean checkIsGlimpseEnabled(long moduleId) throws Exception {

        FacilioModule moduleConfigurationModule = ModuleFactory.getModuleConfigurationModule();
        List<FacilioField> moduleConfigurationFields = FieldFactory.getModuleConfigurationFields();

        GenericSelectRecordBuilder moduleConfigurationBuilder = new GenericSelectRecordBuilder()
                .table(moduleConfigurationModule.getTableName())
                .select(moduleConfigurationFields)
                .andCondition(CriteriaAPI.getCondition("MODULE_ID", "moduleId", String.valueOf(moduleId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("CONFIGURATION_NAME", "configurationName", "glimpse", StringOperators.IS))
                .andCondition(CriteriaAPI.getCondition("STATUS", "status", String.valueOf(Boolean.TRUE), BooleanOperators.IS));

        Map<String, Object> moduleConfigProps = moduleConfigurationBuilder.fetchFirst();

        return MapUtils.isNotEmpty(moduleConfigProps);
    }

    public static GlimpseContext getNewGlimpse(List<String> fieldNames,String moduleName) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Map<String,FacilioField> fieldsMap = FieldFactory.getAsMap(modBean.getAllFields(moduleName));

        GlimpseContext glimpse = new GlimpseContext();
        glimpse.setActive(true);

        List<GlimpseFieldContext> glimpseFields = new ArrayList<>();
        long i = 1;
        for(String fieldName : fieldNames){
            GlimpseFieldContext glimpseField = new GlimpseFieldContext();
            FacilioField field = fieldsMap.get(fieldName);
            if(field!=null) {
                glimpseField.setFieldId(field.getFieldId());
                glimpseField.setFieldName(field.getName());
                glimpseField.setSequenceNumber(i++);

                glimpseFields.add(glimpseField);

            }else if(Objects.equals(fieldName, "site")){
                FacilioField siteField = FieldFactory.getSiteIdField();
                glimpseField.setSequenceNumber(i++);
                glimpseField.setFieldName(siteField.getName());
                glimpseFields.add(glimpseField);
            }
        }

        glimpse.setConfigurationFields(glimpseFields);

        return glimpse;
    }

    public static void insertGlimpseForDefaultModules(String moduleName,List<GlimpseContext> glimpse) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);

        ModuleSettingContext setting = new ModuleSettingContext();
        setting.setName(FacilioConstants.ContextNames.GLIMPSE);
        setting.setConfigurationName(FacilioConstants.ContextNames.GLIMPSE);
        setting.setModuleId(module.getModuleId());
        setting.setStatus(true);

        ModuleSettingConfigUtil.insertModuleConfiguration(setting);

        addGlimpse(moduleName,glimpse);

    }
}
