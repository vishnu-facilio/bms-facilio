package com.facilio.bmsconsole.modulemapping;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.modulemapping.constants.FieldTypeConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.MultiLookupField;
import com.facilio.modules.fields.NumberField;
import com.facilio.util.FacilioUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.*;

@Getter
@Setter
public class FieldMappingValidationUtil {
    FieldType sourceFieldType;
    FieldType targetFieldType;
    FacilioField sourceField;
    FacilioField targetField;
    FacilioModule sourceModule;

    public FieldMappingValidationUtil() {

    }

    @FunctionalInterface
    interface FieldValidationRule {
        boolean validate() throws Exception;
    }

    private Map<FieldType, FieldValidationRule> validationRules = new HashMap<>();

    public FieldMappingValidationUtil(FieldType sourceFieldType, FieldType targetFieldType, FacilioField sourceField, FacilioField targetField) {
        this.sourceFieldType = sourceFieldType;
        this.targetFieldType = targetFieldType;
        this.sourceField = sourceField;
        this.targetField = targetField;

        initializeValidationRules();
    }

    private void initializeValidationRules() {
        validationRules.put(FieldType.STRING, this::checkStringFieldtype);
        validationRules.put(FieldType.NUMBER, this::checkNumberFieldType);
        validationRules.put(FieldType.DECIMAL, this::checkDecimalFieldType);
        validationRules.put(FieldType.BOOLEAN, this::checkBooleanFieldType);
        validationRules.put(FieldType.DATE, this::checkDateFieldType);
        validationRules.put(FieldType.DATE_TIME, this::checkDateFieldType);
        validationRules.put(FieldType.LOOKUP, this::checkLookupFieldType);
        validationRules.put(FieldType.ENUM, this::checkEnumFieldType);
        validationRules.put(FieldType.ID, this::checkIdFieldType);
        validationRules.put(FieldType.SYSTEM_ENUM, this::checkEnumFieldType);
        validationRules.put(FieldType.MULTI_LOOKUP, this::checkMultiLookupFieldType);
        validationRules.put(FieldType.MULTI_ENUM, this::checkMultiEnumFieldType);
        validationRules.put(FieldType.LARGE_TEXT, this::checkStringFieldtype);
        validationRules.put(FieldType.BIG_STRING, this::checkStringFieldtype);
        validationRules.put(FieldType.STRING_SYSTEM_ENUM, this::checkStringSystemEnumFieldType);
        validationRules.put(FieldType.CURRENCY_FIELD, this::checkCurrencyFieldType);
        validationRules.put(FieldType.MULTI_CURRENCY_FIELD, this::checkMultiCurrencyFieldType);
        validationRules.put(FieldType.URL_FIELD, this::checkURLFieldType);
    }


    public void fieldMappingValidation() throws Exception {
        boolean isValid = validationRules.getOrDefault(sourceFieldType, this::areTypesCompatible).validate();
        if (!isValid) {
            dataTypeMisMatchError();
        }

    }

    private void dataTypeMisMatchError() {
        throw new IllegalArgumentException("Conversion from " + sourceField.getName() + " field to " + targetField.getName() + " field is not possible due to a data type mismatch. Unable to cast " + sourceFieldType.getTypeAsString() + " to " + targetFieldType.getTypeAsString() + ".");
    }

    private boolean checkURLFieldType() {
        return FieldTypeConstants.VALID_URL_FIELD_TYPE.contains(targetFieldType);
    }

    private boolean checkNumberFieldType() throws Exception {
        boolean isValidNumberFieldType = FieldTypeConstants.VALID_NUMBER_FIELD_TYPES.contains(targetFieldType);

        if (!isValidNumberFieldType) {
            return false;
        }

        if (FieldTypeConstants.LOOKUP_TYPES.contains(targetFieldType)) {
            isNumberFieldCompatibleForLookup();
        }
        return true;
    }

    public void isNumberFieldCompatibleForLookup() throws Exception {
        if (sourceField.getName().equals("siteId")) {

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            this.sourceModule = modBean.getModule("site");
            checkValidationForLookupSourceField();

        } else {
            dataTypeMisMatchError();
        }
    }

    private boolean checkStringFieldtype() {
        return FieldTypeConstants.STRING_FIELD_TYPES.contains(targetFieldType);
    }

    private boolean checkEnumFieldTypeCompatibility() {
        if (FieldTypeConstants.LOOKUP_TYPES.contains(targetFieldType)) {
            checkEnumAndLookupFieldsAreCompatible();
            return true;
        }
        return false;
    }

    private void checkEnumAndLookupFieldsAreCompatible() {
        if (targetFieldType == FieldType.LOOKUP) {

            LookupField targetLookupField = (LookupField) targetField;

            if (!(targetLookupField != null && (targetLookupField.getLookupModule().getType() == FacilioModule.ModuleType.PICK_LIST.getValue()))) {
                dataTypeMisMatchError();
            }
        } else if (targetFieldType == FieldType.MULTI_LOOKUP) {
            MultiLookupField targetLookupField = (MultiLookupField) targetField;

            if (!(targetLookupField != null && (targetLookupField.getLookupModule().getType() == FacilioModule.ModuleType.PICK_LIST.getValue()))) {
                dataTypeMisMatchError();
            }
        }
    }

    private boolean areTypesCompatible() {
        return sourceFieldType == targetFieldType;
    }

    private boolean checkMultiCurrencyFieldType() {
        return FieldTypeConstants.VALID_MULTI_CURRENCY_FIELD_TYPES.contains(targetFieldType);

    }

    private boolean checkCurrencyFieldType() {
        return FieldTypeConstants.VALID_CURRENCY_FIELD_TYPES.contains(targetFieldType);

    }

    private boolean checkStringSystemEnumFieldType() {
        return FieldTypeConstants.VALID_STRING_SYSTEM_ENUM_FIELD_TYPES.contains(targetFieldType);

    }

    private void checkScoreFieldType() {
        switch (targetFieldType) {
            case SCORE:
            case NUMBER:
            case COUNTER:
            case DECIMAL:
            case STRING:
            case BIG_STRING:
            case LARGE_TEXT:
                break;
            default:
                dataTypeMisMatchError();
        }
    }

    private boolean checkMultiEnumFieldType() {
        if (FieldTypeConstants.VALID_MULTI_ENUM_FIELD_TYPES.contains(targetFieldType)) {
            checkEnumFieldTypeCompatibility();
            return true;

        }
        return false;

    }

    private boolean checkMultiLookupFieldType() {
        if (FieldTypeConstants.VALID_MULTI_LOOKUP_FIELD_TYPES.contains(targetFieldType)) {
            if (targetFieldType == FieldType.MULTI_LOOKUP) {
                checkValidationForMultiLookupSourceField();

            } else if (targetFieldType == FieldType.MULTI_ENUM) {
                checkMultiLookupFieldValidationForMultiEnumSourceField();

            }
            return true;
        }
        return false;
    }

    private void checkMultiLookupFieldValidationForMultiEnumSourceField() {
        MultiLookupField sourceLookupField = (MultiLookupField) sourceField;

        boolean isValid = sourceLookupField.getLookupModule() != null && sourceLookupField.getLookupModule().getType() == FacilioModule.ModuleType.PICK_LIST.getValue();

        if (!isValid) {
            dataTypeMisMatchError();
        }
    }

    private void checkValidationForMultiLookupSourceField() {


        MultiLookupField sourceLookupField = null;

        MultiLookupField targetLookupField = null;

        if (sourceField.getDataTypeEnum() == FieldType.MULTI_LOOKUP) {
            sourceLookupField = (MultiLookupField) sourceField;
        }

        if (targetField.getDataTypeEnum() == FieldType.MULTI_LOOKUP) {
            targetLookupField = (MultiLookupField) targetField;
        }


        FacilioModule sourceFieldModule = sourceLookupField.getLookupModule();

        FacilioModule targetFieldModule = targetLookupField.getLookupModule();

        boolean isValid = sourceFieldModule.getName().equals(targetFieldModule.getName());

        if (!isValid) {
            boolean isValidForExtendedModule = validateLookupPairsInExtendedModule(sourceFieldModule, targetFieldModule);
            if (!isValidForExtendedModule) {
                throw new IllegalArgumentException("Cannot convert the " + sourceField.getName() + " field to the " + targetField.getName() + " field. They belong to different lookup modules: " + sourceFieldModule.getName() + " and " + targetFieldModule.getName());
            }
        }
    }

    private boolean checkIdFieldType() {
        if (FieldTypeConstants.VALID_ID_FIELD_TYPES.contains(targetFieldType)) {
            if (FieldTypeConstants.LOOKUP_TYPES.contains(targetFieldType)) {
                checkIdFieldForLookup();
                return true;
            }
            return true;
        }
        return false;
    }

    private void checkIdFieldForLookup() {

        LookupField targetLookupField = ModuleMappingValidationUtil.getFieldLookup(targetField);
        MultiLookupField targetMultiLookupField = ModuleMappingValidationUtil.getFieldMultiLookup(targetField);

        boolean isValid = false;

        if (targetLookupField != null) {
            isValid = targetLookupField.getLookupModule().getName().equals(sourceField.getModule().getName());
        } else if (targetMultiLookupField != null) {
            isValid = targetMultiLookupField.getLookupModule().getName().equals(sourceField.getModule().getName());
        }

        if (!isValid) {
            dataTypeMisMatchError();
        }

    }

    private boolean checkEnumFieldType() {
        if (FieldTypeConstants.VALID_ENUM_FIELD_TYPES.contains(targetFieldType)) {
            checkEnumFieldTypeCompatibility();
            return true;
        }
        return false;

    }

    private boolean checkLookupFieldType() {
        if (FieldTypeConstants.VALID_LOOKUP_FIELD_TYPES.contains(targetFieldType)) {
            if (FieldTypeConstants.LOOKUP_TYPES.contains(targetFieldType)) {
                checkValidationForLookupSourceField();
            } else if (FieldTypeConstants.ENUM_FIELD_TYPES.contains(targetFieldType)) {
                checkLookupFieldValidationForEnumSourceField();
            }
            return true;
        }
        return false;

    }

    private void checkLookupFieldValidationForEnumSourceField() {
        LookupField sourceLookupField = (LookupField) sourceField;

        boolean isValid = sourceLookupField.getLookupModule() != null && sourceLookupField.getLookupModule().getType() == FacilioModule.ModuleType.PICK_LIST.getValue();

        if (!isValid) {
            dataTypeMisMatchError();
        }

    }

    private void checkValidationForLookupSourceField() {

        if (targetField.getModule().getType() == FacilioModule.ModuleType.PICK_LIST.getValue() && sourceField.getModule().getType() == FacilioModule.ModuleType.PICK_LIST.getValue()) {
            return;
        }

        if (targetFieldType == FieldType.LOOKUP) {

            LookupField sourceLookupField = null;

            LookupField targetLookupField = null;

            if (sourceField.getDataTypeEnum() == FieldType.LOOKUP) {
                sourceLookupField = (LookupField) sourceField;
            }

            if (targetField.getDataTypeEnum() == FieldType.LOOKUP) {
                targetLookupField = (LookupField) targetField;
            }


            FacilioModule sourceFieldModule = null;

            if (this.sourceModule != null) {
                sourceFieldModule = this.sourceModule;
            } else {
                sourceFieldModule = sourceLookupField.getLookupModule();
            }

            FacilioModule targetFieldModule = targetLookupField.getLookupModule();

            boolean isValid = sourceFieldModule.getName().equals(targetFieldModule.getName());

            if (!isValid) {
                boolean isValidForExtendedModule = validateLookupPairsInExtendedModule(sourceFieldModule, targetFieldModule);
                if (!isValidForExtendedModule) {
                    throw new IllegalArgumentException("Cannot convert the " + sourceField.getName() + " field to the " + targetField.getName() + " field. They belong to different lookup modules: " + sourceFieldModule.getName() + " and " + targetFieldModule.getName());
                }
            }

        } else if (targetFieldType == FieldType.MULTI_LOOKUP) {
            if (sourceFieldType == FieldType.LOOKUP) {
                LookupField sourceLookupField = null;

                MultiLookupField targetLookupField = null;

                if (sourceField.getDataTypeEnum() == FieldType.LOOKUP) {
                    sourceLookupField = (LookupField) sourceField;
                }

                if (targetField.getDataTypeEnum() == FieldType.MULTI_LOOKUP) {
                    targetLookupField = (MultiLookupField) targetField;
                }


                FacilioModule sourceFieldModule = null;

                if (this.sourceModule != null) {
                    sourceFieldModule = this.sourceModule;
                } else {
                    sourceFieldModule = sourceLookupField.getLookupModule();
                }

                FacilioModule targetFieldModule = targetLookupField.getLookupModule();

                boolean isValid = sourceFieldModule.getName().equals(targetFieldModule.getName());

                if (!isValid) {
                    boolean isValidForExtendedModule = validateLookupPairsInExtendedModule(sourceFieldModule, targetFieldModule);
                    if (!isValidForExtendedModule) {
                        throw new IllegalArgumentException("Cannot convert the " + sourceField.getName() + " field to the " + targetField.getName() + " field. They belong to different lookup modules: " + sourceFieldModule.getName() + " and " + targetFieldModule.getName());
                    }
                }
            } else {
                checkValidationForNumberToMultiLookup();
            }

        }

    }

    private void checkValidationForNumberToMultiLookup() {
        MultiLookupField sourceLookupField = null;

        MultiLookupField targetLookupField = null;

        if (sourceField.getDataTypeEnum() == FieldType.MULTI_LOOKUP) {
            sourceLookupField = (MultiLookupField) sourceField;
        }

        if (targetField.getDataTypeEnum() == FieldType.MULTI_LOOKUP) {
            targetLookupField = (MultiLookupField) targetField;
        }


        FacilioModule sourceFieldModule = null;

        if (this.sourceModule != null) {
            sourceFieldModule = this.sourceModule;
        } else {
            sourceFieldModule = sourceLookupField.getLookupModule();
        }

        FacilioModule targetFieldModule = targetLookupField.getLookupModule();

        boolean isValid = sourceFieldModule.getName().equals(targetFieldModule.getName());

        if (!isValid) {
            boolean isValidForExtendedModule = validateLookupPairsInExtendedModule(sourceFieldModule, targetFieldModule);
            if (!isValidForExtendedModule) {
                throw new IllegalArgumentException("Cannot convert the " + sourceField.getName() + " field to the " + targetField.getName() + " field. They belong to different lookup modules: " + sourceFieldModule.getName() + " and " + targetFieldModule.getName());
            }
        }
    }

    private boolean validateLookupPairsInExtendedModule(FacilioModule sourceFieldModule, FacilioModule targetFieldModule) {

        FacilioModule extendedModule = sourceFieldModule.getExtendModule();

        if (extendedModule != null) {
            if (targetFieldModule.getName().equals(extendedModule.getName())) {
                return true;
            }
            return validateLookupPairsInExtendedModule(extendedModule, targetFieldModule);

        }
        return false;
    }

    private boolean checkDateFieldType() {
        return FieldTypeConstants.VALID_DATE_FIELD_TYPES.contains(targetFieldType);

    }

    private boolean checkBooleanFieldType() {
        return FieldTypeConstants.VALID_BOOLEAN_FIELD_TYPES.contains(targetFieldType);

    }

    private boolean checkDecimalFieldType() {
        return FieldTypeConstants.VALID_DECIMAL_FIELD_TYPES.contains(targetFieldType);
    }


}
