package com.facilio.metamigration.util;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.metamigration.beans.MetaMigrationBean;
import org.apache.commons.collections.CollectionUtils;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.modules.fields.FacilioField;
import com.facilio.bmsconsole.util.FormRuleAPI;
import org.apache.commons.collections.MapUtils;
import com.facilio.constants.FacilioConstants;
import com.facilio.bmsconsole.util.FormsAPI;
import org.apache.commons.lang3.StringUtils;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.bmsconsole.context.*;
import com.facilio.chain.FacilioContext;
import com.facilio.bmsconsole.forms.*;
import com.facilio.chain.FacilioChain;
import com.facilio.beans.WebTabBean;
import com.facilio.modules.fields.*;
import com.facilio.beans.ModuleBean;
import com.facilio.util.FacilioUtil;
import com.facilio.fw.BeanFactory;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import com.facilio.modules.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j
public class MetaMigrationUtil {

    public static Map<Long, Long> getOldVsNewFieldIdMap(String moduleName, List<FacilioField> oldFields, List<FacilioField> newFields) {
        if (oldFields.size() == newFields.size()) {
            LOGGER.info("####MetaMigration - Fields count mismatch - ModuleName - " + moduleName);
        }
        Map<String, FacilioField> oldFieldsMap = FieldFactory.getAsMap(oldFields);
        Map<String, FacilioField> newFieldsMap = FieldFactory.getAsMap(newFields);

        Map<Long, Long> oldVsNewIds = new HashMap<>();
        for (String fieldName : oldFieldsMap.keySet()) {
            if (newFieldsMap.containsKey(fieldName)) {
                oldVsNewIds.put(oldFieldsMap.get(fieldName).getFieldId(), newFieldsMap.get(fieldName).getFieldId());
            } else {
                LOGGER.info("####MetaMigration - Field not present - ModuleName - " + moduleName + " FieldName - " + fieldName);
            }
        }
        return oldVsNewIds;
    }

    public static void updateSubFormSection(MetaMigrationBean sourceMetaMigration, MetaMigrationBean targetMetaMigration, ModuleBean sourceModuleBean, ModuleBean targetModuleBean, List<FormSection> oldSections, Map<Long, Long> oldVsNewFieldIdsMap) throws Exception {
        for (FormSection section : oldSections) {
            if (section.getSubFormId() > 0) {
                long oldSubFormId = section.getSubFormId();
                FacilioForm oldSubFormFromDB = sourceMetaMigration.getForm(oldSubFormId);
                FacilioModule facilioModule = targetModuleBean.getModule(oldSubFormFromDB.getModule().getName());
                FacilioForm newSubFormFromDB = targetMetaMigration.getForm(facilioModule.getName(), oldSubFormFromDB.getName());

                String lookupFieldModuleName = null;
                if (section.getLookupFieldId() > 0) {
                    long newLookupFieldId = -1;
                    long oldLookupFieldId = section.getLookupFieldId();
                    if (oldVsNewFieldIdsMap.containsKey(oldLookupFieldId)) {
                        lookupFieldModuleName = sourceModuleBean.getField(oldLookupFieldId).getModule().getName();
                        newLookupFieldId = oldVsNewFieldIdsMap.get(oldLookupFieldId);
                        section.setLookupFieldId(newLookupFieldId);
                    } else {
                        FacilioField oldField = sourceModuleBean.getField(oldLookupFieldId);
                        FacilioField newField = targetModuleBean.getField(oldField.getName(), oldField.getModule().getName());
                        lookupFieldModuleName = oldField.getModule().getName();
                        if (newField == null) {
                            LOGGER.info("####MetaMigration - LookupField is not created in moduleName - " + facilioModule.getName() + " old fieldId - " + oldLookupFieldId);
                        } else {
                            section.setLookupFieldId(newField.getFieldId());
                        }
                    }
                }

                if (newSubFormFromDB == null) {
                    Map<Long, Long> currOldVsNewFieldIdsMap = new HashMap<>();
                    if (StringUtils.isNotEmpty(lookupFieldModuleName)) {
                        List<FacilioField> oldOrgModuleFields = sourceModuleBean.getAllFields(lookupFieldModuleName);
                        List<FacilioField> newOrgModuleFields = targetModuleBean.getAllFields(lookupFieldModuleName);

                        if (CollectionUtils.isNotEmpty(oldOrgModuleFields) && CollectionUtils.isNotEmpty(newOrgModuleFields)) {
                            currOldVsNewFieldIdsMap = MetaMigrationUtil.getOldVsNewFieldIdMap(lookupFieldModuleName, oldOrgModuleFields, newOrgModuleFields);
                        }
                    }

                    List<FormField> formFieldsFromSections = FormsAPI.getFormFieldsFromSections(oldSubFormFromDB.getSections());
                    updateSubFormSection(sourceMetaMigration, targetMetaMigration, sourceModuleBean, targetModuleBean, oldSubFormFromDB.getSections(), currOldVsNewFieldIdsMap);
                    updateFormFieldFieldIds(formFieldsFromSections, currOldVsNewFieldIdsMap);
                    oldSubFormFromDB.setAppId(-1);
                    oldSubFormFromDB.setId(-1);

                    long newSubFormId = targetMetaMigration.createForm(oldSubFormFromDB, facilioModule);
                    section.setSubFormId(newSubFormId);
                }
            }
        }
    }

    public static void updateFormFieldFieldIds(List<FormField> formFields, Map<Long, Long> oldVsNewFieldIdsMap) {
        for (FormField formField : formFields) {
            long oldFieldId = formField.getFieldId();
            if (oldFieldId > 0) {
                if (MapUtils.isNotEmpty(oldVsNewFieldIdsMap) && oldVsNewFieldIdsMap.containsKey(oldFieldId)) {
                    long newFieldId = oldVsNewFieldIdsMap.get(oldFieldId);
                    formField.setFieldId(newFieldId);
                } else {
                    LOGGER.info("####MetaMigration - Field not found in new org - OldFieldName - " + formField.getName());
                }
            }
        }
    }

    public static JSONObject createNewField(ModuleBean sourceModuleBean, ModuleBean targetModuleBean, FacilioField oldField) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", oldField.getName());
        jsonObject.put("default", oldField.isDefault());
        jsonObject.put("required", oldField.isRequired());
        jsonObject.put("dataType", oldField.getDataType());
        jsonObject.put("isMainField", oldField.isMainField());
        jsonObject.put("displayName", oldField.getDisplayName());
        jsonObject.put("displayTypeInt", oldField.getDisplayTypeInt());

        switch (oldField.getDataTypeEnum()) {
            case STRING:
            case BIG_STRING:
                jsonObject.put("regex", ((StringField) oldField).getRegex());
                jsonObject.put("maxLength", ((StringField) oldField).getMaxLength());
                break;

            case NUMBER:
            case DECIMAL:
                jsonObject.put("unit", ((NumberField) oldField).getUnit());
                jsonObject.put("unitId", ((NumberField) oldField).getUnitId());
                jsonObject.put("metric", ((NumberField) oldField).getMetric());
                jsonObject.put("minValue", ((NumberField) oldField).getMinValue());
                jsonObject.put("maxValue", ((NumberField) oldField).getMaxValue());
                jsonObject.put("counterField", ((NumberField) oldField).getCounterField());
                break;

            case ENUM:
            case MULTI_ENUM:
                jsonObject.put("values", ((BaseEnumField) oldField).getValues());
                break;

            case SYSTEM_ENUM:
                jsonObject.put("enumName", ((SystemEnumField) oldField).getEnumName());
                break;

            case STRING_SYSTEM_ENUM:
                jsonObject.put("enumName", ((StringSystemEnumField) oldField).getEnumName());
                break;

            case URL_FIELD:
                jsonObject.put("target", ((UrlField) oldField).getTarget());
                jsonObject.put("showAlt", ((UrlField) oldField).getShowAlt());
                break;

            case DATE:
            case DATE_TIME:
                jsonObject.put("allowedDate", ((DateField) oldField).getAllowedDate());
                jsonObject.put("allowedDays", ((DateField) oldField).getAllowedDays());
                break;

            case BOOLEAN:
                jsonObject.put("trueVal", ((BooleanField) oldField).getTrueVal());
                jsonObject.put("falseVal", ((BooleanField) oldField).getFalseVal());
                break;

            case LARGE_TEXT:
                jsonObject.put("skipSizeCheck", ((LargeTextField)oldField).getSkipSizeCheck());
                break;

            case LOOKUP:
            case MULTI_LOOKUP:
                long oldModuleId = ((BaseLookupField) oldField).getLookupModuleId();
                if (oldModuleId > 0) {
                    FacilioModule newModule = getEquivalentModule(sourceModuleBean, targetModuleBean, oldModuleId);
                    FacilioUtil.throwIllegalArgumentException(newModule == null, "####MetaMigration - Module Not Found - OldModuleId - " + oldModuleId);
                    jsonObject.put("lookupModuleId", newModule.getModuleId());
                }
                jsonObject.put("specialType", ((BaseLookupField) oldField).getSpecialType());
                jsonObject.put("relatedListDisplayName", ((BaseLookupField) oldField).getRelatedListDisplayName());
                break;


            default:
                break;
        }
        return jsonObject;
    }

    public static FacilioModule getEquivalentModule(ModuleBean sourceModuleBean, ModuleBean targetModuleBean, long oldModuleId) throws Exception {
        FacilioModule oldModule = sourceModuleBean.getModule(oldModuleId);
        FacilioModule newModule = targetModuleBean.getModule(oldModule.getName());
        return newModule;
    }
}
