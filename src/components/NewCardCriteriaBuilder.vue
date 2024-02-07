<template>
  <div>
    <div class="criteria-container">
      <div v-if="disable" class="disable-overlay"></div>
      <div class="criteria-inner-container">
        <label v-if="!hideTitleSection" for="name" class="fc-modal-sub-title">{{
          $t('setup.users_management.criteria')
        }}</label>
        <div v-if="!hideTitleSection" class="fc-sub-title-desc">
          {{
            title
              ? title
              : $t('setup.users_management.specify_rules_for_reading')
          }}
        </div>
        <div
          class="criteria-radio-block mT20 mL10 mB10"
          v-if="Object.keys(rule.conditions).length >= 2"
        >
          <el-radio-group
            v-model="conditionOperator"
            :disabled="disabled === 1 ? false : true"
            class="criteria-radio-label"
          >
            <el-radio class="fc-radio-btn" label="and">Match all </el-radio>
            <el-radio class="fc-radio-btn" label="or">Match any </el-radio>
          </el-radio-group>
        </div>
        <div
          class="criteria-condition-block"
          v-for="(criteria, key, i) in rule.conditions"
          :key="key"
        >
          <div style="display: none">{{ criteria }}</div>
          <div class="criteria-alphabet-block">
            <div class="alphabet-circle">
              {{ key }}
            </div>
          </div>
          <div class="creteria-dropdown">
            <el-select
              v-model="criteria.fieldName"
              filterable
              @change="fieldSelected(criteria)"
              placeholder="Select"
              class="fc-border-select"
            >
              <el-option
                v-for="(field, idx1) in moduleMetaObject.fields"
                :key="idx1"
                :label="field.displayName"
                :value="field.name"
              ></el-option>
            </el-select>
          </div>
          <div class="creteria-dropdown">
            <el-select
              v-if="criteria.isResourceOperator"
              v-model="criteria.name"
              @change="statusFieldName(criteria, key)"
              placeholder="Select operator"
              class="select-operator-input fc-border-select"
            >
              <el-option-group
                v-for="group in resourceOperator"
                :key="group.label"
                :label="group.label"
              >
                <el-option
                  v-for="(operator, idx2) in group.options"
                  :key="idx2 + group.label"
                  :label="operator.operator"
                  :value="operator.name"
                >
                </el-option>
              </el-option-group>
            </el-select>
            <el-select
              v-else
              v-model="criteria.operatorLabel"
              @change="statusFieldName(criteria, key)"
              placeholder="Select operator"
              class="select-operator-input fc-border-select"
            >
              <el-option
                v-for="(operator, idx2) in getOperators(criteria)"
                :key="idx2"
                :label="idx2"
                :value="idx2"
              ></el-option>
            </el-select>
          </div>

          <div
            class="creteria-dropdown"
            v-if="!$validation.isEmpty(lookupModuleFieldsList)"
          >
            <div v-if="!criteria.active">
              <el-input
                placeholder="Input not needed"
                disabled
                class="fc-border-select"
              ></el-input>
            </div>
            <div v-else>
              <el-select
                v-model="showLookupFieldForCriteria[key]"
                @change="
                  toggleLookupFields(key, showLookupFieldForCriteria[key])
                "
                class="select-operator-input fc-border-select"
              >
                <el-option
                  v-for="(valueType, idx3) in valueTypes"
                  :key="idx3"
                  :label="valueType"
                  :value="idx3"
                ></el-option>
              </el-select>
            </div>
          </div>

          <div
            v-bind:class="
              !$validation.isEmpty(lookupModuleFieldsList)
                ? 'creteria-input d-flex flex-direction-column'
                : 'creteria-input'
            "
          >
            <div v-if="!criteria.active">
              <el-input
                placeholder="Input not needed"
                disabled
                class="fc-border-select"
              ></el-input>
            </div>
            <div v-else-if="criteria.operatorsDataType">
              <div
                v-if="
                  !$validation.isEmpty(lookupModuleFieldsList) &&
                    !$validation.isEmpty(showLookupFieldForCriteria[key]) &&
                    showLookupFieldForCriteria[key] === '1'
                "
              >
                <el-select
                  @change="chageDataType(criteria)"
                  collapse-tags
                  filterable
                  v-model="criteria.value"
                  class="fc-select-multiple-tag fc-criteria-value-select fc-tag width100"
                >
                  <el-option
                    v-for="(value, key) in lookupModuleFieldsList"
                    :key="key"
                    :label="value.displayName"
                    :value="
                      '${' +
                        (lookupFieldModuleName ? lookupFieldModuleName : '') +
                        '.' +
                        value.name +
                        (!$validation.isEmpty(value.field) &&
                        value.field.dataTypeEnum == 'LOOKUP'
                          ? '.id'
                          : '') +
                        '}'
                    "
                  ></el-option>
                </el-select>
              </div>

              <div v-else-if="criteria.isSpacePicker" class="fc-tag">
                <el-input
                  v-model="criteria.parseLabel"
                  disabled
                  class="fc-border-select"
                >
                  <i
                    @click=";(chooserVisibility = true), selectedIndex(i)"
                    slot="suffix"
                    style="
                      line-height: 0px !important;
                      font-size: 16px !important;
                      cursor: pointer;
                    "
                    class="el-input__icon el-icon-search"
                  ></i>
                </el-input>
              </div>

              <el-select
                v-else-if="criteria.fieldName === 'formId'"
                @change="chageDataType(criteria)"
                multiple
                collapse-tags
                v-model="criteria.valueArray"
                class="fc-select-multiple-tag fc-criteria-value-select fc-tag width100"
              >
                <el-option
                  v-for="(value, key) in picklistOptions[criteria.fieldName]"
                  :key="key"
                  :label="value"
                  :value="key"
                ></el-option>
              </el-select>
              <el-input
                type="number"
                v-else-if="
                  criteria.operatorsDataType.dataType === 'NUMBER' ||
                    criteria.operatorsDataType.dataType === 'DECIMAL'
                "
                v-model="criteria.value"
                class="fc-border-select"
              ></el-input>
              <el-select
                @change="forceUpdate"
                v-else-if="criteria.operatorsDataType.dataType === 'BOOLEAN'"
                v-model="criteria.value"
                class="fc-border-select"
              >
                <el-option
                  :label="criteria.trueVal ? criteria.trueVal : 'Yes'"
                  value="true"
                ></el-option>
                <el-option
                  :label="criteria.falseVal ? criteria.falseVal : 'No'"
                  value="false"
                ></el-option>
              </el-select>
              <f-date-picker
                v-else-if="criteria.operatorsDataType.dataType === 'DATE'"
                v-model="criteria.value"
                :type="
                  criteria.operator && criteria.operator.indexOf('between') >= 0
                    ? 'daterange'
                    : 'date'
                "
              ></f-date-picker>
              <f-date-picker
                v-else-if="criteria.operatorsDataType.dataType === 'DATE_TIME'"
                v-model="criteria.value"
                :type="
                  criteria.operator && criteria.operator.indexOf('between') >= 0
                    ? 'datetimerange'
                    : 'datetime'
                "
              ></f-date-picker>
              <FLookupFieldWrapper
                v-else-if="
                  criteria.fieldName === 'lookup' &&
                    ((criteria.operatorsDataType.dataType === 'LOOKUP' &&
                      criteria.operatorsDataType.displayType ===
                        'LOOKUP_SIMPLE') ||
                      criteria.operatorsDataType.dataType === 'MULTI_LOOKUP')
                "
                v-model="criteria.valueArray"
                :label="''"
                @recordSelected="value => chageDataType(criteria, value)"
                :field="{
                  lookupModule: {
                    name: getLookupModuleName(criteria.fieldName),
                  },
                  multiple: true,
                }"
                class="fc-department-icon fc-department-input rm-arrow"
              ></FLookupFieldWrapper>

              <el-select
                filterable
                @change="chageDataType(criteria)"
                v-else-if="
                  (criteria.operatorsDataType.dataType === 'LOOKUP' &&
                    criteria.operatorsDataType.displayType ===
                      'LOOKUP_SIMPLE') ||
                    criteria.operatorsDataType.dataType === 'MULTI_LOOKUP'
                "
                multiple
                collapse-tags
                v-model="criteria.valueArray"
                class="fc-select-multiple-tag fc-criteria-value-select fc-tag width100"
              >
                <el-option
                  v-for="(value, key) in picklistOptions[criteria.fieldName]"
                  :key="key"
                  :label="value"
                  :value="key"
                ></el-option>
              </el-select>

              <el-select
                filterable
                @change="chageDataType(criteria)"
                v-else-if="
                  criteria.operatorsDataType.dataType === 'LOOKUP' &&
                    criteria.operatorsDataType.displayType === 'LOOKUP_POPUP'
                "
                multiple
                collapse-tags
                v-model="criteria.valueArray"
                class="fc-select-multiple-tag fc-criteria-value-select fc-tag width100"
              >
                <el-option
                  v-for="(value, key) in picklistOptions[criteria.fieldName]"
                  :key="key"
                  :label="value"
                  :value="key"
                ></el-option>
              </el-select>

              <el-select
                v-else-if="
                  criteria.operatorsDataType.dataType === 'ENUM' ||
                    criteria.operatorsDataType.dataType === 'SYSTEM_ENUM' ||
                    criteria.operatorsDataType.dataType === 'MULTI_ENUM'
                "
                @change="chageDataType(criteria)"
                multiple
                collapse-tags
                v-model="criteria.valueArray"
                class="fc-select-multiple-tag fc-criteria-value-select fc-tag width100"
              >
                <el-option
                  v-for="(value, key) in picklistOptions[criteria.fieldName]"
                  :key="key"
                  :label="value"
                  :value="key"
                ></el-option>
              </el-select>

              <el-select
                v-else-if="criteria.fieldName === 'siteId'"
                @change="chageDataType(criteria)"
                multiple
                collapse-tags
                v-model="criteria.valueArray"
                class="fc-select-multiple-tag fc-criteria-value-select fc-tag width100"
              >
                <el-option
                  v-for="(value, key) in picklistOptions[criteria.fieldName]"
                  :key="key"
                  :label="value"
                  :value="key"
                ></el-option>
              </el-select>

              <el-input
                v-else-if="
                  criteria.operatorsDataType.dataType === 'LOOKUP' &&
                    criteria.operatorsDataType.displayType === 'LOOKUP_POPUP1'
                "
                placeholder="Select records"
                :readonly="true"
                suffix-icon="el-icon-search"
                v-model="criteria.value"
                class="fc-border-select"
              ></el-input>
              <el-input
                v-else
                v-model="criteria.value"
                @change="forceUpdate"
                class="fc-border-select"
              ></el-input>
            </div>
          </div>

          <div class="creteria-delete-icon flex-middle">
            <img
              src="~assets/add-icon.svg"
              v-if="Object.keys(rule.conditions).length - 1 === i"
              style="height: 18px; width: 18px"
              class="delete-icon"
              @click="addCreteria"
            />
            <img
              src="~assets/remove-icon.svg"
              v-if="!(Object.keys(rule.conditions).length === 1)"
              style="height: 18px; width: 18px; margin-right: 3px"
              class="delete-icon"
              @click="deleteCreteria(key)"
            />
          </div>
        </div>

        <div
          v-if="Object.keys(rule.conditions).length > 2"
          class="creteria-footer"
        >
          <span class="criteria-pattern-txt">Criteria Pattern</span>
          <el-input
            placeholder="Please input"
            v-model="rule.pattern"
            :disabled="disabled === 1 ? true : false"
            class="criteria-input fc-border-select"
            style="width: 450px; margin-left: 20px"
          >
          </el-input>
          <span class="pointer" @click="disabled = false"
            ><i
              class="el-icon-edit"
              style="
                position: relative;
                top: 8px;
                right: -13px;
                cursor: pointer;
                color: #319aa8;
              "
            ></i
          ></span>
          <div v-show="err" class="err-msg">{{ err }}</div>
        </div>
        <div v-else>
          <div style="margin-top: 33px"></div>
        </div>
        <space-asset-multi-chooser
          v-if="chooserVisibility"
          @associate="associateResource"
          :visibility.sync="chooserVisibility"
          :initialValues="resourceData"
          :showAsset="type"
          :hideBanner="true"
          :selectedResource="selectedeResourceObj"
        ></space-asset-multi-chooser>
      </div>
    </div>
  </div>
</template>
<script>
import NewCriteriaBuilder from 'src/components/NewCriteriaBuilder.vue'
import FLookupFieldWrapper from '@/FLookupFieldWrapper'

export default {
  extends: NewCriteriaBuilder,
  components: { FLookupFieldWrapper },
  methods: {
    getLookupModuleName(fieldName) {
      if (
        fieldName &&
        this.moduleMeta?.fields &&
        this.moduleMeta?.fields.length
      ) {
        let field = this.moduleMeta.fields.find(rt => rt.name === fieldName)
        if (field?.lookupModule?.name) {
          return field.lookupModule.name
        }
      }
      return this.module
    },
  },
}
</script>
