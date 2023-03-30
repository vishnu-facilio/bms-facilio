package com.facilio.bmsconsole.ModuleSettingConfig.impl;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.ModuleSettingConfig.bean.ModuleSettingConfig;
import com.facilio.bmsconsole.ModuleSettingConfig.context.GlimpseContext;
import com.facilio.bmsconsole.ModuleSettingConfig.context.GlimpseFieldContext;
import com.facilio.bmsconsole.ModuleSettingConfig.util.GlimpseUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class GlimpseConfiguration extends ModuleSettingConfig {

    @Override
    public GlimpseContext getModuleConfigDetails(FacilioModule module) throws Exception {

        FacilioModule glimpseModule = ModuleFactory.getGlimpseModule();
        FacilioModule glimpseFieldsModule = ModuleFactory.getGlimpseFieldsModule();

        List<GlimpseFieldContext> glimpseFieldsContext = new ArrayList<>();
        GlimpseContext glimpseContext = new GlimpseContext();

        List<FacilioField> selectFields = GlimpseUtil.getGlimpseSelectableFields();

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .select(selectFields)
                .table(glimpseFieldsModule.getTableName())
                .leftJoin(glimpseModule.getTableName())
                .on("Glimpse.ID = Glimpse_Fields.GLIMPSE_ID")
                .andCondition(CriteriaAPI.getCondition("Glimpse.MODULE_ID", "moduleId", String.valueOf(module.getModuleId()), NumberOperators.EQUALS))
                .orderBy("Glimpse_Fields.SEQUENCE_NUMBER");
        List<Map<String,Object>> fieldProps = selectRecordBuilder.get();

        long glimpseId = -1;
        long modifiedTime = -1;
        boolean isActive = false;
        List<Long> glimpseFieldsIds = new ArrayList<>();
        List<String> glimpseFieldsNames = new ArrayList<>();
        Map<Object,Long> fieldVsSequenceNumber = new HashMap<>();

        for(Map<String,Object> field : fieldProps){
            modifiedTime =(long) field.get("modifiedTime");
            glimpseId = (long )field.get("glimpseId");
            isActive = (boolean) field.get("active");
            if(field.get("fieldId") != null && (long) field.get("fieldId") >0 ) {
                glimpseFieldsIds.add((long) field.get("fieldId"));
                fieldVsSequenceNumber.put(field.get("fieldId"),(long)field.get("sequenceNumber"));
            }else {
                glimpseFieldsNames.add((String) field.get("fieldName"));
                fieldVsSequenceNumber.put(field.get("fieldName"),(long)field.get("sequenceNumber"));
            }
        }
        List<FacilioField> fieldList = new ArrayList<>();

        if(CollectionUtils.isNotEmpty(glimpseFieldsIds)) {

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            fieldList = modBean.getFields(glimpseFieldsIds);
        }
        if(CollectionUtils.isNotEmpty(glimpseFieldsNames)) {
            for (String fieldName : glimpseFieldsNames) {
                if (Objects.equals(fieldName, "siteId")) {
                    FacilioField siteField = FieldFactory.getSiteIdField(module);
                    fieldList.add(siteField);
                }
            }
        }

        if(CollectionUtils.isNotEmpty(fieldList)) {
            for (FacilioField facilioField : fieldList) {
                GlimpseFieldContext glimpseFieldContext = new GlimpseFieldContext();
                glimpseFieldContext.setFacilioField(facilioField);
                glimpseFieldContext.setFieldId(facilioField.getFieldId());
                glimpseFieldContext.setGlimpseId(glimpseId);
                if (facilioField.getFieldId() > 0) {
                    glimpseFieldContext.setFieldId(facilioField.getFieldId());
                    glimpseFieldContext.setSequenceNumber(fieldVsSequenceNumber.get(facilioField.getFieldId()));
                } else {
                    glimpseFieldContext.setFieldName(facilioField.getName());
                    glimpseFieldContext.setSequenceNumber(fieldVsSequenceNumber.get(facilioField.getName()));
                }
                glimpseFieldsContext.add(glimpseFieldContext);
            }
        }
        glimpseContext.setConfigurationFields(glimpseFieldsContext);
        glimpseContext.setModuleId(module.getModuleId());
        glimpseContext.setGlimpseId(glimpseId);
        glimpseContext.setModifiedTime(modifiedTime);
        glimpseContext.setActive(isActive);
        glimpseContext.setOrgId(AccountUtil.getCurrentOrg().getOrgId());

        return glimpseContext;
    }
}
