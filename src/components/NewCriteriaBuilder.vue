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
              placeholder="Select Field"
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

              <el-input
                v-else-if="
                  criteria.operatorId === 106 || criteria.operatorId === 107
                "
                v-model="criteria.value"
                class="fc-border-select"
              >
              </el-input>

              <el-select
                v-else-if="criteria.operatorId === 85"
                v-model="criteria.valueArray"
                @change="chageDataType(criteria)"
                filterable
                multiple
                class="fc-select-multiple-tag fc-criteria-value-select fc-tag width100"
              >
                <el-option
                  v-for="(weekValue, weekKey) in $constants.WEEK_DAYS"
                  :key="weekKey"
                  :label="weekValue"
                  :value="weekKey"
                ></el-option>
              </el-select>

              <el-select
                v-else-if="criteria.operatorId === 84"
                v-model="criteria.valueArray"
                @change="chageDataType(criteria)"
                filterable
                multiple
                class="fc-select-multiple-tag fc-criteria-value-select fc-tag width100"
              >
                <el-option
                  v-for="(value, index) in $constants.MONTHS"
                  :key="index"
                  :label="value.label"
                  :value="value.value"
                ></el-option>
              </el-select>

              <el-select
                v-else-if="[101, 108, 103].includes(criteria.operatorId)"
                v-model="criteria.valueArray"
                @change="chageDataType(criteria)"
                filterable
                multiple
                class="fc-select-multiple-tag fc-criteria-value-select fc-tag width100"
              >
                <el-option
                  v-for="(value, index) in dateOperatorValues(criteria)"
                  :key="index"
                  :label="value"
                  :value="value"
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
                class="fc-border-select form-date-picker"
              ></f-date-picker>
              <f-date-picker
                v-else-if="criteria.operatorsDataType.dataType === 'DATE_TIME'"
                v-model="criteria.value"
                :type="
                  criteria.operator && criteria.operator.indexOf('between') >= 0
                    ? 'datetimerange'
                    : 'datetime'
                "
                class="fc-border-select form-date-picker"
              ></f-date-picker>

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
import { mapState, mapGetters } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import SpaceAssetMultiChooser from '@/SpaceAssetMultiChooser'
import FDatePicker from 'pages/assets/overview/FDatePicker'
import isString from 'lodash/isString'
import { getFieldOptions } from 'util/picklist'

export default {
  props: [
    'module',
    'exrule',
    'disable',
    'title',
    'isRendering',
    'lookupFieldModuleName',
    'showSiteField',
    'showFormIDField',
    'hideTitleSection',
    'index',
    'variables',
    'lookupModuleFieldsList',
  ],
  computed: {
    ...mapGetters(['getTicketStatusPickList']),
    ...mapState({
      ticketpriority: state => state.ticketPriority,
      siteList: state => state.sites,
      roles: state => state.roles,
    }),
  },
  created() {
    this.$store.dispatch('loadTicketPriority')
    this.$store.dispatch('loadSpaceCategory')
    this.$store.dispatch('loadSites')
    this.$store.dispatch('loadGroups')
    this.$store.dispatch('loadRoles')
  },
  watch: {
    conditionOperator: function() {
      let pat = Object.keys(this.rule.conditions).join(
        ` ${this.conditionOperator} `
      )
      this.rule.pattern = '(' + pat + ')'
    },
    rule: {
      handler: function(after, before) {
        let tokens = this.tokenize(after.pattern)
        let postfix
        try {
          this.err = ''
          postfix = this.postfix(tokens)
          let expressionTree = this.expressionTree(postfix)
          if (Object.keys(expressionTree).length === 0) {
            this.rule.pattern = ''
            if (typeof this.index !== 'undefined') {
              this.$emit('condition', this.rule, this.index)
            } else {
              this.$emit('condition', this.rule)
            }

            this.err = 'Pattern is mandatory'
            return
          }
          let infix = []
          this.infix(expressionTree, infix)
          this.rule.pattern = this.toPattern(infix)
          if (typeof this.index !== 'undefined') {
            this.$emit('condition', this.rule, this.index)
          } else {
            this.$emit('condition', this.rule)
          }
          this.$emit('hook', this.rule)
          this.$emit('moduleObj', this.moduleMetaObject)
        } catch (e) {
          this.err = e.message
        }
      },
      deep: true,
    },
    exrule: function(ruled) {
      this.loadModuleMeta(ruled)
      this.lookupFieldHandling()
    },
    module: function() {
      this.loadModuleMeta(this.exrule)
    },
  },
  data() {
    return {
      excludeOperators: [74, 75, 76, 77, 78, 79, 35],
      valueTypes: {
        1: 'Field',
        2: 'Value',
      },
      showLookupFieldForCriteria: {},
      woForms: [],
      moduleMeta: {},
      dataTypeToDisplayType: {
        TEXT: 'TEXTBOX',
        NUMBER: 'NUMBER',
        DECIMAL: 'DECIMAL',
        DATE_TIME: 'DATE_TIME',
        DATE: 'DATE',
      },
      Defaultoperators: {
        ENUM: {
          ' <= ': {
            dynamicOperator: false,
            operator: ' <= ',
            operatorId: 77,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'LESS_THAN_EQUAL',
          },
          'is empty': {
            dynamicOperator: false,
            operator: 'is empty',
            operatorId: 1,
            valueNeeded: false,
            _name: 'IS_EMPTY',
          },
          ' > ': {
            dynamicOperator: false,
            operator: ' > ',
            operatorId: 78,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'GREATER_THAN',
          },
          ' = ': {
            dynamicOperator: false,
            operator: ' = ',
            operatorId: 74,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'EQUAL',
          },
          ' < ': {
            dynamicOperator: false,
            operator: ' < ',
            operatorId: 76,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'LESS_THAN',
          },
          "value isn't": {
            dynamicOperator: false,
            operator: "value isn't",
            operatorId: 53,
            valueNeeded: true,
            _name: 'VALUE_ISN_T',
          },
          'value is': {
            dynamicOperator: false,
            operator: 'value is',
            operatorId: 52,
            valueNeeded: true,
            _name: 'VALUE_IS',
          },
          is: {
            dynamicOperator: false,
            operator: 'is',
            operatorId: 54,
            valueNeeded: true,
            _name: 'IS',
          },
          'is not empty': {
            dynamicOperator: false,
            operator: 'is not empty',
            operatorId: 2,
            valueNeeded: false,
            _name: 'IS_NOT_EMPTY',
          },
          ' != ': {
            dynamicOperator: false,
            operator: ' != ',
            operatorId: 75,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'NOT_EQUAL',
          },
          "isn't": {
            dynamicOperator: false,
            operator: "isn't",
            operatorId: 55,
            valueNeeded: true,
            _name: 'ISN_T',
          },
          ' >= ': {
            dynamicOperator: false,
            operator: ' >= ',
            operatorId: 79,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'GREATER_THAN_EQUAL',
          },
        },
        NUMBER: {
          '<=': {
            dynamicOperator: false,
            operator: '<=',
            operatorId: 12,
            valueNeeded: true,
            _name: 'LESS_THAN_EQUAL',
          },
          ' > ': {
            dynamicOperator: false,
            operator: ' > ',
            operatorId: 78,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'GREATER_THAN',
          },
          ' = ': {
            dynamicOperator: false,
            operator: ' = ',
            operatorId: 74,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'EQUAL',
          },
          ' < ': {
            dynamicOperator: false,
            operator: ' < ',
            operatorId: 76,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'LESS_THAN',
          },
          'is not empty': {
            dynamicOperator: false,
            operator: 'is not empty',
            operatorId: 2,
            valueNeeded: false,
            _name: 'IS_NOT_EMPTY',
          },
          ' != ': {
            dynamicOperator: false,
            operator: ' != ',
            operatorId: 75,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'NOT_EQUAL',
          },
          ' >= ': {
            dynamicOperator: false,
            operator: ' >= ',
            operatorId: 79,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'GREATER_THAN_EQUAL',
          },
          ' <= ': {
            dynamicOperator: false,
            operator: ' <= ',
            operatorId: 77,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'LESS_THAN_EQUAL',
          },
          'is empty': {
            dynamicOperator: false,
            operator: 'is empty',
            operatorId: 1,
            valueNeeded: false,
            _name: 'IS_EMPTY',
          },
          '!=': {
            dynamicOperator: false,
            operator: '!=',
            operatorId: 10,
            valueNeeded: true,
            _name: 'NOT_EQUALS',
          },
          '<': {
            dynamicOperator: false,
            operator: '<',
            operatorId: 11,
            valueNeeded: true,
            _name: 'LESS_THAN',
          },
          '=': {
            dynamicOperator: false,
            operator: '=',
            operatorId: 9,
            valueNeeded: true,
            _name: 'EQUALS',
          },
          '>': {
            dynamicOperator: false,
            operator: '>',
            operatorId: 13,
            valueNeeded: true,
            _name: 'GREATER_THAN',
          },
          '>=': {
            dynamicOperator: false,
            operator: '>=',
            operatorId: 14,
            valueNeeded: true,
            _name: 'GREATER_THAN_EQUAL',
          },
        },
        MISC: null,
        DECIMAL: {
          '<=': {
            dynamicOperator: false,
            operator: '<=',
            operatorId: 12,
            valueNeeded: true,
            _name: 'LESS_THAN_EQUAL',
          },
          ' > ': {
            dynamicOperator: false,
            operator: ' > ',
            operatorId: 78,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'GREATER_THAN',
          },
          ' = ': {
            dynamicOperator: false,
            operator: ' = ',
            operatorId: 74,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'EQUAL',
          },
          ' < ': {
            dynamicOperator: false,
            operator: ' < ',
            operatorId: 76,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'LESS_THAN',
          },
          'is not empty': {
            dynamicOperator: false,
            operator: 'is not empty',
            operatorId: 2,
            valueNeeded: false,
            _name: 'IS_NOT_EMPTY',
          },
          ' != ': {
            dynamicOperator: false,
            operator: ' != ',
            operatorId: 75,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'NOT_EQUAL',
          },
          ' >= ': {
            dynamicOperator: false,
            operator: ' >= ',
            operatorId: 79,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'GREATER_THAN_EQUAL',
          },
          ' <= ': {
            dynamicOperator: false,
            operator: ' <= ',
            operatorId: 77,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'LESS_THAN_EQUAL',
          },
          'is empty': {
            dynamicOperator: false,
            operator: 'is empty',
            operatorId: 1,
            valueNeeded: false,
            _name: 'IS_EMPTY',
          },
          '!=': {
            dynamicOperator: false,
            operator: '!=',
            operatorId: 10,
            valueNeeded: true,
            _name: 'NOT_EQUALS',
          },
          '<': {
            dynamicOperator: false,
            operator: '<',
            operatorId: 11,
            valueNeeded: true,
            _name: 'LESS_THAN',
          },
          '=': {
            dynamicOperator: false,
            operator: '=',
            operatorId: 9,
            valueNeeded: true,
            _name: 'EQUALS',
          },
          '>': {
            dynamicOperator: false,
            operator: '>',
            operatorId: 13,
            valueNeeded: true,
            _name: 'GREATER_THAN',
          },
          '>=': {
            dynamicOperator: false,
            operator: '>=',
            operatorId: 14,
            valueNeeded: true,
            _name: 'GREATER_THAN_EQUAL',
          },
        },
        BOOLEAN: {
          ' <= ': {
            dynamicOperator: false,
            operator: ' <= ',
            operatorId: 77,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'LESS_THAN_EQUAL',
          },
          'is empty': {
            dynamicOperator: false,
            operator: 'is empty',
            operatorId: 1,
            valueNeeded: false,
            _name: 'IS_EMPTY',
          },
          ' > ': {
            dynamicOperator: false,
            operator: ' > ',
            operatorId: 78,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'GREATER_THAN',
          },
          ' = ': {
            dynamicOperator: false,
            operator: ' = ',
            operatorId: 74,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'EQUAL',
          },
          ' < ': {
            dynamicOperator: false,
            operator: ' < ',
            operatorId: 76,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'LESS_THAN',
          },
          is: {
            dynamicOperator: false,
            operator: 'is',
            operatorId: 15,
            valueNeeded: true,
            _name: 'IS',
          },
          'is not empty': {
            dynamicOperator: false,
            operator: 'is not empty',
            operatorId: 2,
            valueNeeded: false,
            _name: 'IS_NOT_EMPTY',
          },
          ' != ': {
            dynamicOperator: false,
            operator: ' != ',
            operatorId: 75,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'NOT_EQUAL',
          },
          ' >= ': {
            dynamicOperator: false,
            operator: ' >= ',
            operatorId: 79,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'GREATER_THAN_EQUAL',
          },
        },
        DATE: {
          Upcoming: {
            baseLineSupported: false,
            currentOperator: false,
            dynamicOperator: false,
            operator: 'Upcoming',
            operatorId: 73,
            valueNeeded: false,
            _name: 'UPCOMING',
          },
          ' < ': {
            dynamicOperator: false,
            operator: ' < ',
            operatorId: 76,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'LESS_THAN',
          },
          'Current year upto last month': {
            baseLineSupported: true,
            currentOperator: true,
            dynamicOperator: true,
            operator: 'Current year upto last month',
            operatorId: 80,
            valueNeeded: false,
            _name: 'CURRENT_YEAR_UPTO_LAST_MONTH',
          },
          'is after': {
            baseLineSupported: false,
            currentOperator: false,
            dynamicOperator: false,
            operator: 'is after',
            operatorId: 19,
            valueNeeded: true,
            _name: 'IS_AFTER',
          },
          'Current N Year': {
            baseLineSupported: true,
            currentOperator: false,
            dynamicOperator: true,
            operator: 'Current N Year',
            operatorId: 65,
            valueNeeded: true,
            _name: 'CURRENT_N_YEAR',
          },
          'Last N Months': {
            baseLineSupported: true,
            currentOperator: true,
            dynamicOperator: true,
            operator: 'Last N Months',
            operatorId: 51,
            valueNeeded: true,
            _name: 'LAST_N_MONTHS',
          },
          ' <= ': {
            dynamicOperator: false,
            operator: ' <= ',
            operatorId: 77,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'LESS_THAN_EQUAL',
          },
          'Current Year': {
            baseLineSupported: true,
            currentOperator: true,
            dynamicOperator: true,
            operator: 'Current Year',
            operatorId: 44,
            valueNeeded: false,
            _name: 'CURRENT_YEAR',
          },
          'Current Year upto now': {
            baseLineSupported: true,
            currentOperator: true,
            dynamicOperator: true,
            operator: 'Current Year upto now',
            operatorId: 46,
            valueNeeded: false,
            _name: 'CURRENT_YEAR_UPTO_NOW',
          },
          'Within N Hours': {
            baseLineSupported: true,
            currentOperator: false,
            dynamicOperator: true,
            operator: 'Within N Hours',
            operatorId: 40,
            valueNeeded: true,
            _name: 'WITHIN_HOURS',
          },
          'In N Day': {
            baseLineSupported: true,
            currentOperator: false,
            dynamicOperator: true,
            operator: 'In N Day',
            operatorId: 58,
            valueNeeded: true,
            _name: 'IN_N_DAY',
          },
          'Current Week upto now': {
            baseLineSupported: true,
            currentOperator: true,
            dynamicOperator: true,
            operator: 'Current Week upto now',
            operatorId: 47,
            valueNeeded: false,
            _name: 'CURRENT_WEEK_UPTO_NOW',
          },
          'Age in Days': {
            baseLineSupported: false,
            currentOperator: false,
            dynamicOperator: false,
            operator: 'Age in Days',
            operatorId: 33,
            valueNeeded: true,
            _name: 'AGE_IN_DAYS',
          },
          between: {
            baseLineSupported: true,
            currentOperator: false,
            dynamicOperator: false,
            operator: 'between',
            operatorId: 20,
            valueNeeded: true,
            _name: 'BETWEEN',
          },
          ' = ': {
            dynamicOperator: false,
            operator: ' = ',
            operatorId: 74,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'EQUAL',
          },
          Yesterday: {
            baseLineSupported: true,
            currentOperator: false,
            dynamicOperator: true,
            operator: 'Yesterday',
            operatorId: 25,
            valueNeeded: false,
            _name: 'YESTERDAY',
          },
          'Current N Month': {
            baseLineSupported: true,
            currentOperator: false,
            dynamicOperator: true,
            operator: 'Current N Month',
            operatorId: 64,
            valueNeeded: true,
            _name: 'CURRENT_N_MONTH',
          },
          is: {
            baseLineSupported: false,
            currentOperator: false,
            dynamicOperator: false,
            operator: 'is',
            operatorId: 16,
            valueNeeded: true,
            _name: 'IS',
          },
          'Next N Weeks': {
            baseLineSupported: true,
            currentOperator: false,
            dynamicOperator: true,
            operator: 'Next N Weeks',
            operatorId: 60,
            valueNeeded: true,
            _name: 'NEXT_N_WEEKS',
          },
          Tomorrow: {
            baseLineSupported: true,
            currentOperator: false,
            dynamicOperator: true,
            operator: 'Tomorrow',
            operatorId: 23,
            valueNeeded: false,
            _name: 'TOMORROW',
          },
          ' >= ': {
            dynamicOperator: false,
            operator: ' >= ',
            operatorId: 79,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'GREATER_THAN_EQUAL',
          },
          'Current Week': {
            baseLineSupported: true,
            currentOperator: true,
            dynamicOperator: true,
            operator: 'Current Week',
            operatorId: 31,
            valueNeeded: false,
            _name: 'CURRENT_WEEK',
          },
          'Next N Months': {
            baseLineSupported: true,
            currentOperator: false,
            dynamicOperator: true,
            operator: 'Next N Months',
            operatorId: 59,
            valueNeeded: true,
            _name: 'NEXT_N_MONTHS',
          },
          'Current N Day': {
            baseLineSupported: true,
            currentOperator: false,
            dynamicOperator: true,
            operator: 'Current N Day',
            operatorId: 62,
            valueNeeded: true,
            _name: 'CURRENT_N_DAY',
          },
          'Till Yesterday': {
            baseLineSupported: false,
            currentOperator: false,
            dynamicOperator: true,
            operator: 'Till Yesterday',
            operatorId: 26,
            valueNeeded: false,
            _name: 'TILL_YESTERDAY',
          },
          'Last Quarter': {
            baseLineSupported: true,
            currentOperator: false,
            dynamicOperator: true,
            operator: 'Last Quarter',
            operatorId: 69,
            valueNeeded: false,
            _name: 'LAST_QUARTER',
          },
          'Last Month': {
            baseLineSupported: true,
            currentOperator: false,
            dynamicOperator: true,
            operator: 'Last Month',
            operatorId: 27,
            valueNeeded: false,
            _name: 'LAST_MONTH',
          },
          'Due in Days': {
            baseLineSupported: false,
            currentOperator: false,
            dynamicOperator: false,
            operator: 'Due in Days',
            operatorId: 34,
            valueNeeded: true,
            _name: 'DUE_IN_DAYS',
          },
          'Next N Days': {
            baseLineSupported: true,
            currentOperator: false,
            dynamicOperator: true,
            operator: 'Next N Days',
            operatorId: 61,
            valueNeeded: true,
            _name: 'NEXT_N_DAYS',
          },
          ' > ': {
            dynamicOperator: false,
            operator: ' > ',
            operatorId: 78,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'GREATER_THAN',
          },
          'Last N Hours': {
            baseLineSupported: true,
            currentOperator: true,
            dynamicOperator: true,
            operator: 'Last N Hours',
            operatorId: 42,
            valueNeeded: true,
            _name: 'LAST_N_HOURS',
          },
          'Last Months': {
            baseLineSupported: true,
            currentOperator: false,
            dynamicOperator: true,
            operator: 'Last Months',
            operatorId: 39,
            valueNeeded: true,
            _name: 'LAST_MONTHS',
          },
          'Last N Minutes': {
            baseLineSupported: true,
            currentOperator: true,
            dynamicOperator: true,
            operator: 'Last N Minutes',
            operatorId: 56,
            valueNeeded: true,
            _name: 'LAST_N_MINUTES',
          },
          'Current N Quarter': {
            baseLineSupported: true,
            currentOperator: false,
            dynamicOperator: true,
            operator: 'Current N Quarter',
            operatorId: 67,
            valueNeeded: true,
            _name: 'CURRENT_N_QUARTER',
          },
          'is not empty': {
            dynamicOperator: false,
            operator: 'is not empty',
            operatorId: 2,
            valueNeeded: false,
            _name: 'IS_NOT_EMPTY',
          },
          'Next N Hours': {
            baseLineSupported: true,
            currentOperator: false,
            dynamicOperator: true,
            operator: 'Next N Hours',
            operatorId: 41,
            valueNeeded: true,
            _name: 'NEXT_HOURS',
          },
          ' != ': {
            dynamicOperator: false,
            operator: ' != ',
            operatorId: 75,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'NOT_EQUAL',
          },
          "isn't": {
            baseLineSupported: false,
            currentOperator: false,
            dynamicOperator: false,
            operator: "isn't",
            operatorId: 17,
            valueNeeded: true,
            _name: 'ISN_T',
          },
          'is before': {
            baseLineSupported: false,
            currentOperator: false,
            dynamicOperator: false,
            operator: 'is before',
            operatorId: 18,
            valueNeeded: true,
            _name: 'IS_BEFORE',
          },
          'Starting Tomorrow': {
            baseLineSupported: false,
            currentOperator: false,
            dynamicOperator: true,
            operator: 'Starting Tomorrow',
            operatorId: 24,
            valueNeeded: false,
            _name: 'STARTING_TOMORROW',
          },
          'Last Week': {
            baseLineSupported: true,
            currentOperator: false,
            dynamicOperator: true,
            operator: 'Last Week',
            operatorId: 30,
            valueNeeded: false,
            _name: 'LAST_WEEK',
          },
          'Current N Week': {
            baseLineSupported: true,
            currentOperator: false,
            dynamicOperator: true,
            operator: 'Current N Week',
            operatorId: 63,
            valueNeeded: true,
            _name: 'CURRENT_N_WEEK',
          },
          'Hour Start Time': {
            baseLineSupported: false,
            currentOperator: false,
            dynamicOperator: false,
            operator: 'Hour Start Time',
            operatorId: 71,
            valueNeeded: true,
            _name: 'HOUR_START_TIME',
          },
          'Next Month': {
            baseLineSupported: true,
            currentOperator: false,
            dynamicOperator: true,
            operator: 'Next Month',
            operatorId: 29,
            valueNeeded: false,
            _name: 'NEXT_MONTH',
          },
          'not between': {
            baseLineSupported: false,
            currentOperator: false,
            dynamicOperator: false,
            operator: 'not between',
            operatorId: 21,
            valueNeeded: true,
            _name: 'NOT_BETWEEN',
          },
          'Past Nth day': {
            baseLineSupported: true,
            currentOperator: false,
            dynamicOperator: true,
            operator: 'Past Nth day',
            operatorId: 57,
            valueNeeded: true,
            _name: 'PAST_N_DAY',
          },
          'Last N Weeks': {
            baseLineSupported: true,
            currentOperator: true,
            dynamicOperator: true,
            operator: 'Last N Weeks',
            operatorId: 50,
            valueNeeded: true,
            _name: 'LAST_N_WEEKS',
          },
          'Last Year': {
            baseLineSupported: true,
            currentOperator: false,
            dynamicOperator: true,
            operator: 'Last Year',
            operatorId: 45,
            valueNeeded: false,
            _name: 'LAST_YEAR',
          },
          'today upto now': {
            baseLineSupported: true,
            currentOperator: true,
            dynamicOperator: true,
            operator: 'today upto now',
            operatorId: 43,
            valueNeeded: false,
            _name: 'TODAY_UPTO_NOW',
          },
          'This Quarter': {
            baseLineSupported: true,
            currentOperator: true,
            dynamicOperator: true,
            operator: 'This Quarter',
            operatorId: 68,
            valueNeeded: false,
            _name: 'THIS_QUARTER',
          },
          'Last N Days': {
            baseLineSupported: true,
            currentOperator: true,
            dynamicOperator: true,
            operator: 'Last N Days',
            operatorId: 49,
            valueNeeded: true,
            _name: 'LAST_N_DAYS',
          },
          'is empty': {
            dynamicOperator: false,
            operator: 'is empty',
            operatorId: 1,
            valueNeeded: false,
            _name: 'IS_EMPTY',
          },
          Today: {
            baseLineSupported: true,
            currentOperator: true,
            dynamicOperator: true,
            operator: 'Today',
            operatorId: 22,
            valueNeeded: false,
            _name: 'TODAY',
          },
          'This Month Till Yesterday': {
            baseLineSupported: true,
            currentOperator: false,
            dynamicOperator: true,
            operator: 'This Month Till Yesterday',
            operatorId: 66,
            valueNeeded: false,
            _name: 'THIS_MONTH_TILL_YESTERDAY',
          },
          'Till Now': {
            baseLineSupported: false,
            currentOperator: false,
            dynamicOperator: true,
            operator: 'Till Now',
            operatorId: 72,
            valueNeeded: false,
            _name: 'TILL_NOW',
          },
          'Current Month upto now': {
            baseLineSupported: true,
            currentOperator: true,
            dynamicOperator: true,
            operator: 'Current Month upto now',
            operatorId: 48,
            valueNeeded: false,
            _name: 'CURRENT_MONTH_UPTO_NOW',
          },
          'Next Week': {
            baseLineSupported: true,
            currentOperator: false,
            dynamicOperator: true,
            operator: 'Next Week',
            operatorId: 32,
            valueNeeded: false,
            _name: 'NEXT_WEEK',
          },
          'LAST N Quarters': {
            baseLineSupported: true,
            currentOperator: true,
            dynamicOperator: true,
            operator: 'LAST N Quarters',
            operatorId: 70,
            valueNeeded: true,
            _name: 'LAST_N_QUARTERS',
          },
          'Current Month': {
            baseLineSupported: true,
            currentOperator: true,
            dynamicOperator: true,
            operator: 'Current Month',
            operatorId: 28,
            valueNeeded: false,
            _name: 'CURRENT_MONTH',
          },
          'Hours of Day': {
            baseLineSupported: true,
            currentOperator: true,
            dynamicOperator: true,
            operator: 'Hours of Day',
            operatorId: 103,
            valueNeeded: true,
            _name: 'HOURS_OF_DAY',
          },
          'Day of Week': {
            baseLineSupported: true,
            currentOperator: true,
            dynamicOperator: true,
            operator: 'Day of Week',
            operatorId: 85,
            valueNeeded: true,
            _name: 'DAY_OF_WEEK',
          },
          'Day of Month': {
            baseLineSupported: true,
            currentOperator: true,
            dynamicOperator: true,
            operator: 'Day of Month',
            operatorId: 101,
            valueNeeded: true,
            _name: 'DAY_OF_MONTH',
          },
          Month: {
            baseLineSupported: true,
            currentOperator: true,
            dynamicOperator: true,
            operator: 'Month',
            operatorId: 84,
            valueNeeded: true,
            _name: 'MONTH',
          },
          'Week of Year': {
            baseLineSupported: true,
            currentOperator: true,
            dynamicOperator: true,
            operator: 'Week of Year',
            operatorId: 108,
            valueNeeded: true,
            _name: 'WEEK_OF_YEAR',
          },
          'After N Days': {
            baseLineSupported: true,
            currentOperator: true,
            dynamicOperator: true,
            operator: 'After N Days',
            operatorId: 107,
            valueNeeded: true,
            _name: 'AFTER_N_DAYS',
          },
          'Before N Days': {
            baseLineSupported: true,
            currentOperator: true,
            dynamicOperator: true,
            operator: 'Before N Days',
            operatorId: 106,
            valueNeeded: true,
            _name: 'BEFORE_N_DAYS',
          },
        },
        LOOKUP: {
          ' <= ': {
            dynamicOperator: false,
            operator: ' <= ',
            operatorId: 77,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'LESS_THAN_EQUAL',
          },
          lookup: {
            dynamicOperator: false,
            operator: 'lookup',
            operatorId: 35,
            valueNeeded: true,
            _name: 'LOOKUP',
          },
          'is empty': {
            dynamicOperator: false,
            operator: 'is empty',
            operatorId: 1,
            valueNeeded: false,
            _name: 'IS_EMPTY',
          },
          building_is: {
            dynamicOperator: true,
            operator: 'building_is',
            operatorId: 38,
            valueNeeded: true,
            _name: 'BUILDING_IS',
          },
          ' > ': {
            dynamicOperator: false,
            operator: ' > ',
            operatorId: 78,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'GREATER_THAN',
          },
          ' = ': {
            dynamicOperator: false,
            operator: ' = ',
            operatorId: 74,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'EQUAL',
          },
          ' < ': {
            dynamicOperator: false,
            operator: ' < ',
            operatorId: 76,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'LESS_THAN',
          },
          is: {
            dynamicOperator: true,
            operator: 'is',
            operatorId: 36,
            valueNeeded: true,
            _name: 'IS',
          },
          'is not empty': {
            dynamicOperator: false,
            operator: 'is not empty',
            operatorId: 2,
            valueNeeded: false,
            _name: 'IS_NOT_EMPTY',
          },
          ' != ': {
            dynamicOperator: false,
            operator: ' != ',
            operatorId: 75,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'NOT_EQUAL',
          },
          "isn't": {
            dynamicOperator: true,
            operator: "isn't",
            operatorId: 37,
            valueNeeded: true,
            _name: 'ISN_T',
          },
          ' >= ': {
            dynamicOperator: false,
            operator: ' >= ',
            operatorId: 79,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'GREATER_THAN_EQUAL',
          },
        },
        STRING: {
          ' > ': {
            dynamicOperator: false,
            operator: ' > ',
            operatorId: 78,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'GREATER_THAN',
          },
          ' = ': {
            dynamicOperator: false,
            operator: ' = ',
            operatorId: 74,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'EQUAL',
          },
          "doesn't contain": {
            dynamicOperator: false,
            operator: "doesn't contain",
            operatorId: 6,
            valueNeeded: true,
            _name: 'DOESNT_CONTAIN',
          },
          ' < ': {
            dynamicOperator: false,
            operator: ' < ',
            operatorId: 76,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'LESS_THAN',
          },
          'ends with': {
            dynamicOperator: false,
            operator: 'ends with',
            operatorId: 8,
            valueNeeded: true,
            _name: 'ENDS_WITH',
          },
          is: {
            dynamicOperator: false,
            operator: 'is',
            operatorId: 3,
            valueNeeded: true,
            _name: 'IS',
          },
          'is not empty': {
            dynamicOperator: false,
            operator: 'is not empty',
            operatorId: 2,
            valueNeeded: false,
            _name: 'IS_NOT_EMPTY',
          },
          ' != ': {
            dynamicOperator: false,
            operator: ' != ',
            operatorId: 75,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'NOT_EQUAL',
          },
          "isn't": {
            dynamicOperator: false,
            operator: "isn't",
            operatorId: 4,
            valueNeeded: true,
            _name: 'ISN_T',
          },
          ' >= ': {
            dynamicOperator: false,
            operator: ' >= ',
            operatorId: 79,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'GREATER_THAN_EQUAL',
          },
          ' <= ': {
            dynamicOperator: false,
            operator: ' <= ',
            operatorId: 77,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'LESS_THAN_EQUAL',
          },
          'is empty': {
            dynamicOperator: false,
            operator: 'is empty',
            operatorId: 1,
            valueNeeded: false,
            _name: 'IS_EMPTY',
          },
          contains: {
            dynamicOperator: false,
            operator: 'contains',
            operatorId: 5,
            valueNeeded: true,
            _name: 'CONTAINS',
          },
          'starts with': {
            dynamicOperator: false,
            operator: 'starts with',
            operatorId: 7,
            valueNeeded: true,
            _name: 'STARTS_WITH',
          },
        },
        DATE_TIME: {
          Upcoming: {
            baseLineSupported: false,
            currentOperator: false,
            dynamicOperator: false,
            operator: 'Upcoming',
            operatorId: 73,
            valueNeeded: false,
            _name: 'UPCOMING',
          },
          ' < ': {
            dynamicOperator: false,
            operator: ' < ',
            operatorId: 76,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'LESS_THAN',
          },
          'Current year upto last month': {
            baseLineSupported: true,
            currentOperator: true,
            dynamicOperator: true,
            operator: 'Current year upto last month',
            operatorId: 80,
            valueNeeded: false,
            _name: 'CURRENT_YEAR_UPTO_LAST_MONTH',
          },
          'is after': {
            baseLineSupported: false,
            currentOperator: false,
            dynamicOperator: false,
            operator: 'is after',
            operatorId: 19,
            valueNeeded: true,
            _name: 'IS_AFTER',
          },
          'Current N Year': {
            baseLineSupported: true,
            currentOperator: false,
            dynamicOperator: true,
            operator: 'Current N Year',
            operatorId: 65,
            valueNeeded: true,
            _name: 'CURRENT_N_YEAR',
          },
          'Last N Months': {
            baseLineSupported: true,
            currentOperator: true,
            dynamicOperator: true,
            operator: 'Last N Months',
            operatorId: 51,
            valueNeeded: true,
            _name: 'LAST_N_MONTHS',
          },
          ' <= ': {
            dynamicOperator: false,
            operator: ' <= ',
            operatorId: 77,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'LESS_THAN_EQUAL',
          },
          'Current Year': {
            baseLineSupported: true,
            currentOperator: true,
            dynamicOperator: true,
            operator: 'Current Year',
            operatorId: 44,
            valueNeeded: false,
            _name: 'CURRENT_YEAR',
          },
          'Current Year upto now': {
            baseLineSupported: true,
            currentOperator: true,
            dynamicOperator: true,
            operator: 'Current Year upto now',
            operatorId: 46,
            valueNeeded: false,
            _name: 'CURRENT_YEAR_UPTO_NOW',
          },
          'Within N Hours': {
            baseLineSupported: true,
            currentOperator: false,
            dynamicOperator: true,
            operator: 'Within N Hours',
            operatorId: 40,
            valueNeeded: true,
            _name: 'WITHIN_HOURS',
          },
          'In N Day': {
            baseLineSupported: true,
            currentOperator: false,
            dynamicOperator: true,
            operator: 'In N Day',
            operatorId: 58,
            valueNeeded: true,
            _name: 'IN_N_DAY',
          },
          'Current Week upto now': {
            baseLineSupported: true,
            currentOperator: true,
            dynamicOperator: true,
            operator: 'Current Week upto now',
            operatorId: 47,
            valueNeeded: false,
            _name: 'CURRENT_WEEK_UPTO_NOW',
          },
          'Age in Days': {
            baseLineSupported: false,
            currentOperator: false,
            dynamicOperator: false,
            operator: 'Age in Days',
            operatorId: 33,
            valueNeeded: true,
            _name: 'AGE_IN_DAYS',
          },
          between: {
            baseLineSupported: true,
            currentOperator: false,
            dynamicOperator: false,
            operator: 'between',
            operatorId: 20,
            valueNeeded: true,
            _name: 'BETWEEN',
          },
          ' = ': {
            dynamicOperator: false,
            operator: ' = ',
            operatorId: 74,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'EQUAL',
          },
          Yesterday: {
            baseLineSupported: true,
            currentOperator: false,
            dynamicOperator: true,
            operator: 'Yesterday',
            operatorId: 25,
            valueNeeded: false,
            _name: 'YESTERDAY',
          },
          'Current N Month': {
            baseLineSupported: true,
            currentOperator: false,
            dynamicOperator: true,
            operator: 'Current N Month',
            operatorId: 64,
            valueNeeded: true,
            _name: 'CURRENT_N_MONTH',
          },
          is: {
            baseLineSupported: false,
            currentOperator: false,
            dynamicOperator: false,
            operator: 'is',
            operatorId: 16,
            valueNeeded: true,
            _name: 'IS',
          },
          'Next N Weeks': {
            baseLineSupported: true,
            currentOperator: false,
            dynamicOperator: true,
            operator: 'Next N Weeks',
            operatorId: 60,
            valueNeeded: true,
            _name: 'NEXT_N_WEEKS',
          },
          Tomorrow: {
            baseLineSupported: true,
            currentOperator: false,
            dynamicOperator: true,
            operator: 'Tomorrow',
            operatorId: 23,
            valueNeeded: false,
            _name: 'TOMORROW',
          },
          ' >= ': {
            dynamicOperator: false,
            operator: ' >= ',
            operatorId: 79,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'GREATER_THAN_EQUAL',
          },
          'Current Week': {
            baseLineSupported: true,
            currentOperator: true,
            dynamicOperator: true,
            operator: 'Current Week',
            operatorId: 31,
            valueNeeded: false,
            _name: 'CURRENT_WEEK',
          },
          'Next N Months': {
            baseLineSupported: true,
            currentOperator: false,
            dynamicOperator: true,
            operator: 'Next N Months',
            operatorId: 59,
            valueNeeded: true,
            _name: 'NEXT_N_MONTHS',
          },
          'Current N Day': {
            baseLineSupported: true,
            currentOperator: false,
            dynamicOperator: true,
            operator: 'Current N Day',
            operatorId: 62,
            valueNeeded: true,
            _name: 'CURRENT_N_DAY',
          },
          'Till Yesterday': {
            baseLineSupported: false,
            currentOperator: false,
            dynamicOperator: true,
            operator: 'Till Yesterday',
            operatorId: 26,
            valueNeeded: false,
            _name: 'TILL_YESTERDAY',
          },
          'Last Quarter': {
            baseLineSupported: true,
            currentOperator: false,
            dynamicOperator: true,
            operator: 'Last Quarter',
            operatorId: 69,
            valueNeeded: false,
            _name: 'LAST_QUARTER',
          },
          'Last Month': {
            baseLineSupported: true,
            currentOperator: false,
            dynamicOperator: true,
            operator: 'Last Month',
            operatorId: 27,
            valueNeeded: false,
            _name: 'LAST_MONTH',
          },
          'Due in Days': {
            baseLineSupported: false,
            currentOperator: false,
            dynamicOperator: false,
            operator: 'Due in Days',
            operatorId: 34,
            valueNeeded: true,
            _name: 'DUE_IN_DAYS',
          },
          'Next N Days': {
            baseLineSupported: true,
            currentOperator: false,
            dynamicOperator: true,
            operator: 'Next N Days',
            operatorId: 61,
            valueNeeded: true,
            _name: 'NEXT_N_DAYS',
          },
          ' > ': {
            dynamicOperator: false,
            operator: ' > ',
            operatorId: 78,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'GREATER_THAN',
          },
          'Last N Hours': {
            baseLineSupported: true,
            currentOperator: true,
            dynamicOperator: true,
            operator: 'Last N Hours',
            operatorId: 42,
            valueNeeded: true,
            _name: 'LAST_N_HOURS',
          },
          'Last Months': {
            baseLineSupported: true,
            currentOperator: false,
            dynamicOperator: true,
            operator: 'Last Months',
            operatorId: 39,
            valueNeeded: true,
            _name: 'LAST_MONTHS',
          },
          'Last N Minutes': {
            baseLineSupported: true,
            currentOperator: true,
            dynamicOperator: true,
            operator: 'Last N Minutes',
            operatorId: 56,
            valueNeeded: true,
            _name: 'LAST_N_MINUTES',
          },
          'Current N Quarter': {
            baseLineSupported: true,
            currentOperator: false,
            dynamicOperator: true,
            operator: 'Current N Quarter',
            operatorId: 67,
            valueNeeded: true,
            _name: 'CURRENT_N_QUARTER',
          },
          'is not empty': {
            dynamicOperator: false,
            operator: 'is not empty',
            operatorId: 2,
            valueNeeded: false,
            _name: 'IS_NOT_EMPTY',
          },
          'Next N Hours': {
            baseLineSupported: true,
            currentOperator: false,
            dynamicOperator: true,
            operator: 'Next N Hours',
            operatorId: 41,
            valueNeeded: true,
            _name: 'NEXT_HOURS',
          },
          ' != ': {
            dynamicOperator: false,
            operator: ' != ',
            operatorId: 75,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'NOT_EQUAL',
          },
          "isn't": {
            baseLineSupported: false,
            currentOperator: false,
            dynamicOperator: false,
            operator: "isn't",
            operatorId: 17,
            valueNeeded: true,
            _name: 'ISN_T',
          },
          'is before': {
            baseLineSupported: false,
            currentOperator: false,
            dynamicOperator: false,
            operator: 'is before',
            operatorId: 18,
            valueNeeded: true,
            _name: 'IS_BEFORE',
          },
          'Starting Tomorrow': {
            baseLineSupported: false,
            currentOperator: false,
            dynamicOperator: true,
            operator: 'Starting Tomorrow',
            operatorId: 24,
            valueNeeded: false,
            _name: 'STARTING_TOMORROW',
          },
          'Last Week': {
            baseLineSupported: true,
            currentOperator: false,
            dynamicOperator: true,
            operator: 'Last Week',
            operatorId: 30,
            valueNeeded: false,
            _name: 'LAST_WEEK',
          },
          'Current N Week': {
            baseLineSupported: true,
            currentOperator: false,
            dynamicOperator: true,
            operator: 'Current N Week',
            operatorId: 63,
            valueNeeded: true,
            _name: 'CURRENT_N_WEEK',
          },
          'Hour Start Time': {
            baseLineSupported: false,
            currentOperator: false,
            dynamicOperator: false,
            operator: 'Hour Start Time',
            operatorId: 71,
            valueNeeded: true,
            _name: 'HOUR_START_TIME',
          },
          'Next Month': {
            baseLineSupported: true,
            currentOperator: false,
            dynamicOperator: true,
            operator: 'Next Month',
            operatorId: 29,
            valueNeeded: false,
            _name: 'NEXT_MONTH',
          },
          'not between': {
            baseLineSupported: false,
            currentOperator: false,
            dynamicOperator: false,
            operator: 'not between',
            operatorId: 21,
            valueNeeded: true,
            _name: 'NOT_BETWEEN',
          },
          'Past Nth day': {
            baseLineSupported: true,
            currentOperator: false,
            dynamicOperator: true,
            operator: 'Past Nth day',
            operatorId: 57,
            valueNeeded: true,
            _name: 'PAST_N_DAY',
          },
          'Last N Weeks': {
            baseLineSupported: true,
            currentOperator: true,
            dynamicOperator: true,
            operator: 'Last N Weeks',
            operatorId: 50,
            valueNeeded: true,
            _name: 'LAST_N_WEEKS',
          },
          'Last Year': {
            baseLineSupported: true,
            currentOperator: false,
            dynamicOperator: true,
            operator: 'Last Year',
            operatorId: 45,
            valueNeeded: false,
            _name: 'LAST_YEAR',
          },
          'today upto now': {
            baseLineSupported: true,
            currentOperator: true,
            dynamicOperator: true,
            operator: 'today upto now',
            operatorId: 43,
            valueNeeded: false,
            _name: 'TODAY_UPTO_NOW',
          },
          'This Quarter': {
            baseLineSupported: true,
            currentOperator: true,
            dynamicOperator: true,
            operator: 'This Quarter',
            operatorId: 68,
            valueNeeded: false,
            _name: 'THIS_QUARTER',
          },
          'Last N Days': {
            baseLineSupported: true,
            currentOperator: true,
            dynamicOperator: true,
            operator: 'Last N Days',
            operatorId: 49,
            valueNeeded: true,
            _name: 'LAST_N_DAYS',
          },
          'is empty': {
            dynamicOperator: false,
            operator: 'is empty',
            operatorId: 1,
            valueNeeded: false,
            _name: 'IS_EMPTY',
          },
          Today: {
            baseLineSupported: true,
            currentOperator: true,
            dynamicOperator: true,
            operator: 'Today',
            operatorId: 22,
            valueNeeded: false,
            _name: 'TODAY',
          },
          'This Month Till Yesterday': {
            baseLineSupported: true,
            currentOperator: false,
            dynamicOperator: true,
            operator: 'This Month Till Yesterday',
            operatorId: 66,
            valueNeeded: false,
            _name: 'THIS_MONTH_TILL_YESTERDAY',
          },
          'Till Now': {
            baseLineSupported: false,
            currentOperator: false,
            dynamicOperator: true,
            operator: 'Till Now',
            operatorId: 72,
            valueNeeded: false,
            _name: 'TILL_NOW',
          },
          'Current Month upto now': {
            baseLineSupported: true,
            currentOperator: true,
            dynamicOperator: true,
            operator: 'Current Month upto now',
            operatorId: 48,
            valueNeeded: false,
            _name: 'CURRENT_MONTH_UPTO_NOW',
          },
          'Next Week': {
            baseLineSupported: true,
            currentOperator: false,
            dynamicOperator: true,
            operator: 'Next Week',
            operatorId: 32,
            valueNeeded: false,
            _name: 'NEXT_WEEK',
          },
          'LAST N Quarters': {
            baseLineSupported: true,
            currentOperator: true,
            dynamicOperator: true,
            operator: 'LAST N Quarters',
            operatorId: 70,
            valueNeeded: true,
            _name: 'LAST_N_QUARTERS',
          },
          'Current Month': {
            baseLineSupported: true,
            currentOperator: true,
            dynamicOperator: true,
            operator: 'Current Month',
            operatorId: 28,
            valueNeeded: false,
            _name: 'CURRENT_MONTH',
          },
          'Hours of Day': {
            baseLineSupported: true,
            currentOperator: true,
            dynamicOperator: true,
            operator: 'Hours of Day',
            operatorId: 103,
            valueNeeded: true,
            _name: 'HOURS_OF_DAY',
          },
          'Day of Week': {
            baseLineSupported: true,
            currentOperator: true,
            dynamicOperator: true,
            operator: 'Day of Week',
            operatorId: 85,
            valueNeeded: true,
            _name: 'DAY_OF_WEEK',
          },
          'Day of Month': {
            baseLineSupported: true,
            currentOperator: true,
            dynamicOperator: true,
            operator: 'Day of Month',
            operatorId: 101,
            valueNeeded: true,
            _name: 'DAY_OF_MONTH',
          },
          Month: {
            baseLineSupported: true,
            currentOperator: true,
            dynamicOperator: true,
            operator: 'Month',
            operatorId: 84,
            valueNeeded: true,
            _name: 'MONTH',
          },
          'Week of Year': {
            baseLineSupported: true,
            currentOperator: true,
            dynamicOperator: true,
            operator: 'Week of Year',
            operatorId: 108,
            valueNeeded: true,
            _name: 'WEEK_OF_YEAR',
          },
          'After N Days': {
            baseLineSupported: true,
            currentOperator: true,
            dynamicOperator: true,
            operator: 'After N Days',
            operatorId: 107,
            valueNeeded: true,
            _name: 'AFTER_N_DAYS',
          },
          'Before N Days': {
            baseLineSupported: true,
            currentOperator: true,
            dynamicOperator: true,
            operator: 'Before N Days',
            operatorId: 106,
            valueNeeded: true,
            _name: 'BEFORE_N_DAYS',
          },
        },
        ID: {
          '<=': {
            dynamicOperator: false,
            operator: '<=',
            operatorId: 12,
            valueNeeded: true,
            _name: 'LESS_THAN_EQUAL',
          },
          ' > ': {
            dynamicOperator: false,
            operator: ' > ',
            operatorId: 78,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'GREATER_THAN',
          },
          ' = ': {
            dynamicOperator: false,
            operator: ' = ',
            operatorId: 74,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'EQUAL',
          },
          ' < ': {
            dynamicOperator: false,
            operator: ' < ',
            operatorId: 76,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'LESS_THAN',
          },
          'is not empty': {
            dynamicOperator: false,
            operator: 'is not empty',
            operatorId: 2,
            valueNeeded: false,
            _name: 'IS_NOT_EMPTY',
          },
          ' != ': {
            dynamicOperator: false,
            operator: ' != ',
            operatorId: 75,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'NOT_EQUAL',
          },
          ' >= ': {
            dynamicOperator: false,
            operator: ' >= ',
            operatorId: 79,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'GREATER_THAN_EQUAL',
          },
          ' <= ': {
            dynamicOperator: false,
            operator: ' <= ',
            operatorId: 77,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'LESS_THAN_EQUAL',
          },
          'is empty': {
            dynamicOperator: false,
            operator: 'is empty',
            operatorId: 1,
            valueNeeded: false,
            _name: 'IS_EMPTY',
          },
          '!=': {
            dynamicOperator: false,
            operator: '!=',
            operatorId: 10,
            valueNeeded: true,
            _name: 'NOT_EQUALS',
          },
          '<': {
            dynamicOperator: false,
            operator: '<',
            operatorId: 11,
            valueNeeded: true,
            _name: 'LESS_THAN',
          },
          '=': {
            dynamicOperator: false,
            operator: '=',
            operatorId: 9,
            valueNeeded: true,
            _name: 'EQUALS',
          },
          '>': {
            dynamicOperator: false,
            operator: '>',
            operatorId: 13,
            valueNeeded: true,
            _name: 'GREATER_THAN',
          },
          '>=': {
            dynamicOperator: false,
            operator: '>=',
            operatorId: 14,
            valueNeeded: true,
            _name: 'GREATER_THAN_EQUAL',
          },
        },
        FILE: {
          ' <= ': {
            dynamicOperator: false,
            operator: ' <= ',
            operatorId: 77,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'LESS_THAN_EQUAL',
          },
          'is empty': {
            dynamicOperator: false,
            operator: 'is empty',
            operatorId: 1,
            valueNeeded: false,
            _name: 'IS_EMPTY',
          },
          ' > ': {
            dynamicOperator: false,
            operator: ' > ',
            operatorId: 78,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'GREATER_THAN',
          },
          ' = ': {
            dynamicOperator: false,
            operator: ' = ',
            operatorId: 74,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'EQUAL',
          },
          ' < ': {
            dynamicOperator: false,
            operator: ' < ',
            operatorId: 76,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'LESS_THAN',
          },
          'is not empty': {
            dynamicOperator: false,
            operator: 'is not empty',
            operatorId: 2,
            valueNeeded: false,
            _name: 'IS_NOT_EMPTY',
          },
          ' != ': {
            dynamicOperator: false,
            operator: ' != ',
            operatorId: 75,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'NOT_EQUAL',
          },
          ' >= ': {
            dynamicOperator: false,
            operator: ' >= ',
            operatorId: 79,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'GREATER_THAN_EQUAL',
          },
        },
        COUNTER: {
          ' <= ': {
            dynamicOperator: false,
            operator: ' <= ',
            operatorId: 77,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'LESS_THAN_EQUAL',
          },
          'is empty': {
            dynamicOperator: false,
            operator: 'is empty',
            operatorId: 1,
            valueNeeded: false,
            _name: 'IS_EMPTY',
          },
          ' > ': {
            dynamicOperator: false,
            operator: ' > ',
            operatorId: 78,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'GREATER_THAN',
          },
          ' = ': {
            dynamicOperator: false,
            operator: ' = ',
            operatorId: 74,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'EQUAL',
          },
          ' < ': {
            dynamicOperator: false,
            operator: ' < ',
            operatorId: 76,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'LESS_THAN',
          },
          'is not empty': {
            dynamicOperator: false,
            operator: 'is not empty',
            operatorId: 2,
            valueNeeded: false,
            _name: 'IS_NOT_EMPTY',
          },
          ' != ': {
            dynamicOperator: false,
            operator: ' != ',
            operatorId: 75,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'NOT_EQUAL',
          },
          ' >= ': {
            dynamicOperator: false,
            operator: ' >= ',
            operatorId: 79,
            placeHoldersMandatory: true,
            valueNeeded: false,
            _name: 'GREATER_THAN_EQUAL',
          },
        },
        SYSTEM_ENUM: null,
      },
      selectedeResourceObj: [],
      siteLists: {},
      siteId: {
        displayName: 'Site',
        name: 'siteId',
        default: true,
        options: {},
        value: [],
        dataTypeEnum: {
          _name: 'LOOKUP',
        },
      },
      formId: {
        displayName: 'Template',
        name: 'formId',
        default: true,
        options: {},
        value: [],
        dataTypeEnum: {
          _name: 'LOOKUP',
        },
      },
      resourceOperator: [
        {
          label: '',
          options: [
            {
              name: 'isEmpty',
              dynamicOperator: false,
              operator: 'is empty',
              operatorId: 1,
              valueNeeded: false,
              _name: 'IS_EMPTY',
            },
            {
              name: 'is not empty',
              dynamicOperator: false,
              operator: 'is not empty',
              operatorId: 2,
              valueNeeded: false,
              _name: 'IS_NOT_EMPTY',
            },
          ],
        },
        {
          label: 'Space',
          options: [
            {
              name: "space_isn't",
              dynamicOperator: true,
              operator: "isn't",
              operatorId: 37,
              valueNeeded: true,
              _name: 'ISN_T',
            },
            {
              dynamicOperator: true,
              name: 'space_is',
              operator: 'is',
              operatorId: 36,
              valueNeeded: true,
              _name: 'IS',
            },
            {
              dynamicOperator: true,
              name: 'space_building_is',
              operator: 'building_is',
              operatorId: 38,
              valueNeeded: true,
              _name: 'BUILDING_IS',
            },
          ],
        },
        {
          label: 'Asset',
          options: [
            {
              name: "asset_isn't",
              dynamicOperator: true,
              operator: "isn't",
              operatorId: 37,
              valueNeeded: true,
              _name: 'ISN_T',
            },
            {
              name: 'asset_is',
              dynamicOperator: true,
              operator: 'is',
              operatorId: 36,
              valueNeeded: true,
              _name: 'IS',
            },
          ],
        },
      ],
      systemEnumOperator: {
        is: {
          dynamicOperator: true,
          operator: 'is',
          operatorId: 9,
          valueNeeded: true,
          _name: 'IS',
        },
        'is empty': {
          dynamicOperator: false,
          operator: 'is empty',
          operatorId: 1,
          valueNeeded: false,
          _name: 'IS_EMPTY',
        },
        'is not empty': {
          dynamicOperator: false,
          operator: 'is not empty',
          operatorId: 2,
          valueNeeded: false,
          _name: 'IS_NOT_EMPTY',
        },
        "isn't": {
          dynamicOperator: true,
          operator: "isn't",
          operatorId: 10,
          valueNeeded: true,
          _name: 'ISN_T',
        },
      },
      dummyAssertValue: [],
      err: '',
      isResourceOperator: false,
      type: null,
      chooserVisibility: false,
      quickSearchQuery: '',
      spaceAssetDisplayName: '',
      picklistOptions: {},
      valueNotNeed: [
        'is empty',
        'is not empty',
        'Today',
        'Tomorrow',
        'Yesterday',
        'Starting Tomorrow',
        'Till Yesterday',
        'Till Now',
        'Last Month',
        'Current Month',
        'Next Month',
        'Last Week',
        'Current Week',
        'Next Week',
        'is before',
        'is after',
        'Hours of Day',
        'Day of Month',
        'Day of Week',
        'Month',
        'Week of Year',
        'After N Days',
        'Before N Days',
      ],
      conditionOperator: 'and',
      moduleMetaObject: [],
      disabled: 1,
      resourceData: null,
      selectedIndexs: null,
      rule: {
        pattern: '',
        conditions: {},
      },
    }
  },
  components: {
    SpaceAssetMultiChooser,
    FDatePicker,
  },
  mounted() {
    this.addCreteria()
    this.loadWoForms()
    this.loadModuleMeta(this.exrule)
    // setTimeout(this.lookupFieldHandling, 5000)
    this.lookupFieldHandling()
  },
  methods: {
    dateOperatorValues(criteria) {
      let dateOperatorValues = []
      let operatorValueObj = {
        101: 31,
        108: 52,
      }
      if (criteria.operatorId === 103) {
        dateOperatorValues = Array(24)
          .fill()
          .map((_, i) => i)
      } else {
        dateOperatorValues = Array(operatorValueObj[criteria.operatorId])
          .fill()
          .map((_, i) => i + 1)
      }
      return dateOperatorValues
    },
    lookupFieldHandling() {
      let { exrule, lookupModuleFieldsList, lookupFieldModuleName } = this
      if (
        !isEmpty(lookupModuleFieldsList) &&
        !isEmpty(exrule) &&
        !isEmpty(exrule.conditions)
      ) {
        for (let key in exrule.conditions) {
          if (
            !isEmpty(exrule.conditions[key].value) &&
            exrule.conditions[key].value.includes(
              '${' + lookupFieldModuleName + '.'
            )
          ) {
            this.$set(this.showLookupFieldForCriteria, key, '1')
          } else {
            this.$set(this.showLookupFieldForCriteria, key, '2')
          }
        }
      }
    },
    toggleLookupFields(key, value) {
      this.$set(this.showLookupFieldForCriteria, key, value)
      this.rule.conditions[key].value = ''
      this.rule.conditions[key].valueArray = []
    },
    loadWoForms() {
      let url = `/v2/forms?moduleName=workorder`
      this.$http.get(url).then(response => {
        if (response.data.responseCode === 0) {
          this.woForms = (response.data.result.forms || []).filter(
            form => form.id > 0
          )
        }
      })
    },
    forceUpdate() {
      this.$forceUpdate()
    },
    selectedIndex(i) {
      this.selectedIndexs = i + 1
      let cond = this.rule.conditions[this.selectedIndexs]
      let value = cond.value.split(',')
      let splitName = cond.name.split('_')
      if (splitName[0] === 'space') {
        this.type = false
      } else if (splitName[0] === 'asset') {
        this.type = true
      }
      this.resourceData = {
        isIncludeResource: true,
        selectedResources: value.map(resource => ({
          id:
            resource && typeof resource === 'object'
              ? parseInt(resource.id)
              : parseInt(resource),
        })),
      }
    },
    resourceLabel(selectedResourceObject) {
      if (selectedResourceObject) {
        let message
        let selectedCount = selectedResourceObject.length
        let categoryName = this.type ? 'Asset' : 'Space'
        if (selectedCount) {
          message =
            selectedCount +
            ' ' +
            (selectedCount > 1 ? categoryName + 's' : categoryName)
        }
        return message
      }
    },
    associateResource(selectedObj) {
      if (selectedObj.resourceList && selectedObj.resourceList.length) {
        this.rule.conditions[this.selectedIndexs].value = []
        selectedObj.resourceList.forEach(element => {
          this.rule.conditions[this.selectedIndexs].value.push(element.id)
        })
        this.rule.conditions[
          this.selectedIndexs
        ].parseLabel = this.resourceLabel(
          this.rule.conditions[this.selectedIndexs].value
        )
        this.rule.conditions[this.selectedIndexs].value = this.rule.conditions[
          this.selectedIndexs
        ].value.toString()
      }
      this.chooserVisibility = false
    },
    initData(condEdit) {
      this.$emit('update:isRendering', false)
      if (condEdit) {
        if (condEdit.conditions) {
          let self = this
          Object.keys(condEdit.conditions).forEach(kessy => {
            let newConditionObject = {}
            let condArrat = condEdit.conditions[kessy]
            if (condArrat) {
              if (condArrat.operatorId == 90 || condArrat.operatorId == 91) {
                //TODO Change this check based on field type
                let name = condArrat.fieldName.split('.')
                if (name.length > 1) condArrat.fieldName = name[1]
              }
              newConditionObject.fieldName = condArrat.fieldName
              if (condArrat.fieldName === 'resource') {
                let self = this
                if (condArrat.value) {
                  let promise2 = self.$http.post(
                    'v2/resource/getResourcesDetails',
                    { resourceId: condArrat.value.split(',') }
                  )
                  Promise.all([promise2]).then(function(values) {
                    if (values[0].data.responseCode === 0) {
                      if (values[0].data.result.resource.length > 0) {
                        self.selectedeResourceObj =
                          values[0].data.result.resource
                        let resp = values[0].data.result.resource[0]
                        if (resp.resourceTypeEnum === 'SPACE') {
                          self.type = false
                        } else if (resp.resourceTypeEnum === 'ASSET') {
                          self.type = true
                        }
                      }
                    }
                    newConditionObject.value = condArrat.value
                    if (newConditionObject.value) {
                      if (isString(condArrat.value)) {
                        newConditionObject.valueArray = condArrat.value.split(
                          ','
                        )
                      } else {
                        newConditionObject.valueArray = [condArrat.value]
                      }
                    }
                  })
                }
                let tempOperator
                self.resourceOperator.find(d => {
                  if (self.type) {
                    if (d.label === 'Asset') {
                      d.options.find(dr => {
                        if (dr.operatorId === condArrat.operatorId) {
                          tempOperator = dr
                        }
                      })
                    }
                  } else {
                    if (d.label === 'Space') {
                      d.options.find(dr => {
                        if (dr.operatorId === condArrat.operatorId) {
                          tempOperator = dr
                        }
                      })
                    } else {
                      d.options.find(dr => {
                        if (dr.operatorId === condArrat.operatorId) {
                          tempOperator = dr
                        }
                      })
                    }
                  }
                })
                newConditionObject.name = tempOperator ? tempOperator.name : ''
                newConditionObject.operatorLabel = tempOperator.operator
                newConditionObject.parseLabel = self.resourceLabel(
                  condArrat.valueArray
                )
                newConditionObject.columnName = condArrat.columnName
                newConditionObject.operatorId = condArrat.operatorId
                self.statusFieldName(newConditionObject, null)
                self.rule.pattern = condEdit.pattern
              } else if (condArrat.operator) {
                newConditionObject.operatorLabel = condArrat.operator.operator
                newConditionObject.active = condArrat.operator.valueNeeded
              } else {
                if (condArrat.operatorId) {
                  let tempCond = this.getOperators(condArrat)
                  Object.keys(tempCond).forEach(d => {
                    if (tempCond[d].operatorId === condArrat.operatorId) {
                      newConditionObject.operatorLabel = tempCond[d].operator
                      newConditionObject.active = tempCond[d].valueNeeded
                    }
                  })
                }
              }
              newConditionObject.value = condArrat.value
              if (newConditionObject.value) {
                if (isString(condArrat.value)) {
                  newConditionObject.valueArray = condArrat.value.split(',')
                } else {
                  newConditionObject.valueArray = [condArrat.value]
                }
              }
              newConditionObject.columnName = condArrat.columnName
              newConditionObject.operatorId = condArrat.operatorId
              self.statusFieldName(newConditionObject, null)
            }
            this.$set(self.rule.conditions, kessy, newConditionObject)
          })
          self.rule.pattern = condEdit.pattern
        }
      }
    },
    chageDataType(criteria) {
      if (criteria.fieldName === 'siteId') {
        if (this.module === 'workorder') {
          criteria.columnName = 'WorkOrders.SITE_ID'
        } else if (this.module === 'asset') {
          criteria.columnName = 'Assets.SITE_ID'
        } else if (this.module === 'alarmoccurrence') {
          criteria.columnName = 'AlarmOccurrence.SITE_ID'
        } else {
          criteria.columnName = 'SITE_ID'
        }
      }
      if (criteria.fieldName === 'formId') {
        if (this.module === 'workorder') {
          criteria.columnName = 'Tickets.FORM_ID'
        }
      }
      if (criteria.valueArray.length > 0 && criteria.fieldName !== 'trigger') {
        let values = criteria.valueArray.join()
        criteria.value = values
      }
      if (criteria.fieldName === 'trigger') {
        criteria.fieldName = 'workorder.trigger'
        if (criteria.operatorId === 36) {
          criteria.operatorId = 35
        }
        let a = {
          conditions: {
            1: {
              fieldName: 'frequency',
              isExpressionValue: null,
              moduleName: 'trigger',
              operatorId: 9,
              parentCriteriaId: -1,
              sequence: 1,
              value: criteria.valueArray.join(),
            },
          },
          pattern: '(1)',
        }
        criteria.criteriaValue = a
        delete criteria.value
      }
      this.$forceUpdate()
    },
    fieldSelected(rule) {
      rule.isResourceOperator = false

      let currField = this.moduleMeta.fields.find(
        field => field.name === rule.fieldName
      )
      let { lookupModule } = currField || {}
      if (!isEmpty(lookupModule) && lookupModule.name === 'resource') {
        rule.isResourceOperator = true
      }
      rule.parseLabel = null
      rule.valueArray = []
      rule.operatorLabel = null
      this.$set(rule, 'value', null)
      rule.active = false
      this.$forceUpdate()
    },
    async statusFieldName(rule, key) {
      if (
        !isEmpty(this.lookupModuleFieldsList) &&
        !isEmpty(key) &&
        isEmpty(rule.value)
      ) {
        this.showLookupFieldForCriteria[key] = '2'
      }
      if (rule.fieldName && (rule.operatorLabel || rule.name)) {
        if (rule.isResourceOperator) {
          let splitName = rule.name.split('_')
          if (splitName[0] === 'space') {
            this.type = false
          } else if (splitName[0] === 'asset') {
            this.type = true
          }
          this.resourceOperator.find(d => {
            d.options.find(dr => {
              if (dr.name === rule.name) {
                rule.operatorLabel = dr.operator
              }
            })
          })
          rule.isSpacePicker = true
        }
        this.getValueDataType(rule)
        let field = this.moduleMeta.fields.filter(
          field => field.name === rule.fieldName
        )
        rule.isSpacePicker = false
        if (field.length > 0) {
          if (
            (field[0].name === 'alarmType' && this.module === 'alarm') ||
            (field[0].name === 'preRequestStatus' &&
              this.module === 'workorder')
          ) {
            let opId = null
            if (rule.operatorLabel === '=' || rule.operatorLabel === '!=') {
              opId = rule.operatorLabel === '=' ? 'is' : "isn't"
            } else {
              opId = rule.operatorLabel
            }
            rule.operatorId = this.systemEnumOperator[opId].operatorId
            rule.active = this.systemEnumOperator[opId].valueNeeded
          } else if (field[0].dataTypeEnum._name === 'SYSTEM_ENUM') {
            rule.operatorId = this.moduleMeta.operators['ENUM'][
              rule.operatorLabel
            ].operatorId
            rule.active = this.moduleMeta.operators['ENUM'][
              rule.operatorLabel
            ].valueNeeded
          } else {
            rule.operatorId = this.moduleMeta.operators[
              field[0].dataTypeEnum._name
            ][rule.operatorLabel].operatorId
            rule.active = this.moduleMeta.operators[
              field[0].dataTypeEnum._name
            ][rule.operatorLabel].valueNeeded
          }
          rule.columnName = field[0].completeColumnName
          if (
            field[0].dataTypeEnum._name === 'ENUM' ||
            field[0].dataTypeEnum._name === 'SYSTEM_ENUM' ||
            field[0].dataTypeEnum._name === 'MULTI_ENUM'
          ) {
            this.$set(this.picklistOptions, rule.fieldName, field[0].enumMap)
          }
          if (field[0].dataTypeEnum._name === 'BOOLEAN') {
            rule.trueVal = field[0].trueVal
            rule.falseVal = field[0].falseVal
          }
          if (
            ['LOOKUP', 'MULTI_LOOKUP'].includes(field[0].dataTypeEnum._name) &&
            field[0].specialType
          ) {
            if (rule.operatorLabel === 'role is') {
              let roles = this.roles.reduce((acc, role) => {
                acc[role.id] = role.name
                return acc
              }, {})
              this.$set(this.picklistOptions, rule.fieldName, roles)
            } else {
              this.loadSpecialTypePickList(field[0].specialType, rule.fieldName)
            }
          } else if (
            ['LOOKUP', 'MULTI_LOOKUP'].includes(field[0].dataTypeEnum._name) &&
            field[0].lookupModule
          ) {
            if (field[0].lookupModule.name === 'resource') {
              rule.isSpacePicker = true
            }
            if (
              field[0].lookupModule.name === 'basespace' &&
              field[0].name === 'space'
            ) {
              rule.isSpacePicker = true
            }
            if (field[0].lookupModule.name === 'ticketpriority') {
              // handling  key value pair to match existing flow pattern
              let priority = {}
              this.ticketpriority.forEach(d => {
                priority[d.id] = d.displayName
              })
              this.$set(this.picklistOptions, rule.fieldName, priority)
            } else if (field[0].lookupModule.name === 'ticketstatus') {
              let statusModule =
                this.module === 'workorderTimeLog' ? 'workorder' : this.module
              this.$store
                .dispatch('loadTicketStatus', statusModule)
                .then(() => {
                  this.$set(
                    this.picklistOptions,
                    rule.fieldName,
                    this.getTicketStatusPickList(statusModule)
                  )
                })
            } else if (field[0].lookupModule.name === 'readingrule') {
              let fetchOptions = {
                lookupModuleName: 'readingrule',
                skipDeserialize: true,
              }
              let { error, options } = await getFieldOptions({
                field: fetchOptions,
              })

              if (error) {
                this.$message.error(error.message || 'Error Occured')
              } else {
                this.$set(this.picklistOptions, rule.fieldName, options)
              }
            } else {
              this.loadPickList(field[0].lookupModule.name, rule.fieldName)
            }
          }
          if (rule.fieldName === 'formId') {
            let data = {}
            if (this.woForms) {
              this.woForms.forEach(form => {
                data[form.id] = form.displayName
              })
            }
            this.$set(this.picklistOptions, rule.fieldName, data)
          }
          if (rule.fieldName === 'siteId') {
            if (this.module === 'workorder') {
              rule.columnName = 'WorkOrders.SITE_ID'
            } else if (this.module === 'asset') {
              rule.columnName = 'Assets.SITE_ID'
            } else if (this.module === 'alarmoccurrence') {
              rule.columnName = 'AlarmOccurrence.SITE_ID'
            }
            let data = {}
            if (this.siteList) {
              this.siteList.forEach(sit => {
                data[sit.id] = sit.name
              })
            }
            this.$set(this.picklistOptions, rule.fieldName, data)
          }
          if (field[0].specialType && field[0].specialType === 'basespace') {
            let oprs = this.moduleMeta.operators[field[0].dataTypeEnum._name]
            oprs['is'] = 'BUILDING_IS'
            this.$forceUpdate()
            return oprs
          } else {
            if (
              (field[0].name === 'alarmType' && this.module === 'alarm') ||
              (field[0].name === 'preRequestStatus' &&
                this.module === 'workorder')
            ) {
              this.loadSpecialTypePickList(field[0].name, rule.fieldName)
            } else {
              this.$forceUpdate()
              return this.moduleMeta.operators[field[0].dataTypeEnum._name]
            }
          }
        }
      }
      this.$forceUpdate()
    },
    getValueDataType(rule) {
      if (rule.operatorLabel && rule.operatorLabel !== '') {
        let field = this.moduleMeta.fields.filter(
          field => field.name === rule.fieldName
        )
        if (
          field[0].dataTypeEnum._name === 'DATE' ||
          field[0].dataTypeEnum._name === 'DATE_TIME'
        ) {
          if (
            rule.operatorLabel === 'Age in Days' ||
            rule.operatorLabel === 'Due in Days'
          ) {
            rule.operatorsDataType = {
              dataType: 'NUMBER',
              displayType: 'NUMBER',
            }
            return rule.operatorsDataType
          }
        }
        if (
          (field[0].name === 'alarmType' && this.module === 'alarm') ||
          (field[0].name === 'preRequestStatus' && this.module === 'workorder')
        ) {
          rule.operatorsDataType = {
            dataType: 'LOOKUP',
            displayType: 'LOOKUP_SIMPLE',
          }
          return rule.operatorsDataType
        }
        if (field[0].name === 'siteId' || field[0].name === 'formId') {
          rule.operatorsDataType = {
            dataType: 'LOOKUP',
            displayType: 'LOOKUP_SIMPLE',
          }
          return rule.operatorsDataType
        }
        rule.operatorsDataType = {
          dataType: field[0].dataTypeEnum._name,
          displayType: field[0].displayType
            ? field[0].displayType._name
            : field[0].dataTypeEnum._name === 'LOOKUP'
            ? 'LOOKUP_SIMPLE'
            : null,
        }
        return rule.operatorsDataType
      }
      return null
    },
    setModuleMeta(variables) {
      if (variables && variables.length) {
        this.moduleMeta['operators'] = this.Defaultoperators
        this.moduleMeta['displayName'] = 'Graphics'
        this.moduleMeta['name'] = 'graphics'
        this.moduleMeta['fields'] = this.convertGraphicsVariablesToFileds(
          variables
        )
      }
      this.moduleMetaObject = this.moduleMeta
      this.loading = false
    },
    setCardBuilderMeta(variables) {
      if (variables && variables.length) {
        this.moduleMeta['operators'] = this.Defaultoperators
        this.moduleMeta['displayName'] = 'Card Builder'
        this.moduleMeta['name'] = 'cardbuilder'
        this.moduleMeta['fields'] = this.getCardBuilderFields(variables)
      }
      this.moduleMetaObject = this.moduleMeta
      this.loading = false
    },
    setTabularModuleMeta(variables) {
      if (variables && variables.length) {
        this.moduleMeta['operators'] = this.Defaultoperators
        this.moduleMeta['displayName'] = 'Tabular'
        this.moduleMeta['name'] = 'tabular'
        this.moduleMeta['fields'] = this.getTabularFields(variables)
      }
      this.moduleMetaObject = this.moduleMeta
      this.loading = false
    },
    setPivotBuilderMeta(variables) {
      if (variables && variables.length) {
        this.moduleMeta['operators'] = this.Defaultoperators
        this.moduleMeta['displayName'] = 'Pivot Builder'
        this.moduleMeta['name'] = 'pivotbuilder'
        this.moduleMeta['fields'] = variables
      }

      this.moduleMetaObject = this.moduleMeta
      this.loading = false
    },
    convertGraphicsVariablesToFileds(variables) {
      let fields = []
      variables.forEach(function(variable) {
        fields.push({
          name: variable.key,
          displayName: variable.label,
          dataTypeEnum: {
            _name: variable.dataType,
          },
          displayType: {
            _name: 'TEXTBOX',
          },
        })
      })
      return fields
    },
    getTabularFields(variables) {
      let fields = []
      let self = this
      variables.forEach(rt => {
        fields.push({
          name: rt.columnAlias,
          displayName: rt.columnLabel,
          dataTypeEnum: {
            _name: rt.dataTypeEnum,
          },
          displayType: {
            _name: self.dataTypeToDisplayType[rt.dataTypeEnum] || 'TEXTBOX',
          },
        })
      })
      return fields
    },
    getCardBuilderFields(variables) {
      let fields = []
      let self = this
      variables.forEach(rt => {
        fields.push({
          name: rt.name,
          displayName: rt.displayName,
          dataTypeEnum: {
            _name: rt.dataType,
          },
          displayType: {
            _name: self.dataTypeToDisplayType[rt.dataType] || 'TEXTBOX',
          },
        })
      })
      return fields
    },
    paternToArray(pattern) {
      return pattern
        .split('(')[1]
        .split(')')[0]
        .split(' ')
    },
    setConditionOperator(pattern) {
      if (
        this.paternToArray(pattern).filter(rt => rt === 'and').length &&
        !this.paternToArray(pattern).filter(rt => rt === 'or').length
      ) {
        this.conditionOperator = 'and'
      } else if (
        this.paternToArray(pattern).filter(rt => rt === 'or').length &&
        !this.paternToArray(pattern).filter(rt => rt === 'and').length
      ) {
        this.conditionOperator = 'or'
      }
    },
    loadModuleMeta(editCondi) {
      this.loading = true
      this.$emit('update:isRendering', true)

      if (this.module === 'graphicsbuilder' && this.variables) {
        this.setModuleMeta(this.variables)
        if (editCondi && editCondi.conditions) {
          this.setConditionOperator(editCondi.pattern)
          this.initData(editCondi)
        }
      } else if (this.module === 'cardbuilder' && this.variables) {
        this.setCardBuilderMeta(this.variables)
        if (editCondi && editCondi.conditions) {
          this.setConditionOperator(editCondi.pattern)
          this.initData(editCondi)
        }
      } else if (this.module === 'pivotbuilder' && this.variables) {
        this.setPivotBuilderMeta(this.variables)
        if (editCondi && editCondi.conditions) {
          this.setConditionOperator(editCondi.pattern)
          this.initData(editCondi)
        }
      } else if (this.module === 'tabular' && this.variables) {
        this.setTabularModuleMeta(this.variables)
        if (editCondi && editCondi.conditions) {
          this.setConditionOperator(editCondi.pattern)
          this.initData(editCondi)
        }
      } else if (
        this.threshold &&
        (this.module === 'asset' || this.module === 'space')
      ) {
        let url = null
        if (this.module === 'asset') {
          url =
            '/module/meta?moduleName=' +
            this.module +
            '&assetId=' +
            this.readingobj.id +
            '&categoryId=' +
            this.readingobj.category.id +
            '&resourceType=' +
            this.module
        } else if (this.module === 'space') {
          url =
            '/module/meta?moduleName=' +
            this.module +
            '&assetId=' +
            this.readingobj.id +
            '&categoryId=' +
            this.readingobj.spaceCategory.id +
            '&resourceType=' +
            this.module
        }
        this.$http.get(url).then(response => {
          this.moduleMeta = response.data.meta
          this.loading = false
          if (editCondi) {
            this.setConditionOperator(editCondi.pattern)
            this.initData(editCondi)
          }
        })
      } else {
        this.$http
          .get('/module/metafields?moduleName=' + this.module)
          .then(response => {
            let spaceObject
            let assetObject
            this.moduleMeta = response.data.meta
            this.moduleMetaObject = this.moduleMeta
            if (this.showSiteField) {
              this.moduleMetaObject.fields.push(this.siteId)
            }
            if (this.showFormIDField) {
              this.moduleMetaObject.fields.push(this.formId)
            }
            this.moduleMetaObject.fields.filter((d, i) => {
              if (d.name === 'resource') {
                spaceObject = Object.assign({}, d)
                assetObject = Object.assign({}, d)
              }
            })
            if (spaceObject === null) {
              spaceObject.displayName = 'Space'
              spaceObject.name = 'space'
            }
            if (assetObject === null) {
              assetObject.displayName = 'Asset'
              assetObject.name = 'asset'
            }
            this.loading = false
            if (editCondi) {
              this.setConditionOperator(editCondi.pattern)
              this.initData(editCondi)
            }
            this.$emit('moduleMetaObject', this.moduleMetaObject)
          })
      }
    },
    async loadSpecialTypePickList(specialType, fieldName) {
      let pickOption = {}
      if (specialType === 'users') {
        let userList = this.$store.state.users
        pickOption['$' + '{LOGGED_USER}'] = 'Current User'
        for (let user of userList) {
          pickOption[user.id] = user.name
        }
        this.$set(this.picklistOptions, fieldName, pickOption)
      } else if (specialType === 'trigger') {
        let triggerList = this.$constants.FACILIO_FREQUENCY
        this.$set(this.picklistOptions, fieldName, triggerList)
      } else if (specialType === 'groups') {
        let groupList = this.$store.state.groups
        for (let group of groupList) {
          pickOption[group.groupId] = group.name
        }
        this.$set(this.picklistOptions, fieldName, pickOption)
      } else if (specialType === 'requester') {
        this.$http
          .get('/setup/portalusers')
          .then(({ data }) => {
            let requesterlist = data.users

            this.loading = false
            let pickOption = {}
            for (let user of requesterlist) {
              pickOption[user.id] = user.name
            }
            this.$set(this.picklistOptions, fieldName, pickOption)
          })
          .catch(() => {})
      } else if (specialType === 'basespace') {
        let spaceList = this.$store.state.spaces
        for (let space of spaceList) {
          pickOption[space.id] = space.name
        }
        this.$set(this.picklistOptions, fieldName, pickOption)
      } else if (specialType === 'alarmType') {
        this.$set(
          this.picklistOptions,
          fieldName,
          this.$constants.AlarmCategory
        )
      } else if (specialType === 'preRequestStatus') {
        this.$set(
          this.picklistOptions,
          fieldName,
          this.$constants.PrerequestStatus
        )
      } else if (specialType === 'readingrule') {
        let fetchOptions = {
          lookupModuleName: 'readingrule',
          skipDeserialize: true,
        }
        let { error, options } = await getFieldOptions({ field: fetchOptions })

        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.$set(this.picklistOptions, fieldName, options)
        }
      }
    },
    async loadPickList(moduleName, fieldName) {
      let { error, options } = await getFieldOptions({
        field: { lookupModuleName: moduleName, skipDeserialize: true },
      })

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        if (moduleName === 'people') {
          options['$' + '{LOGGED_PEOPLE}'] = 'Current User'
        }
        this.$set(this.picklistOptions, fieldName, options)
      }
    },
    getOperators(criteria) {
      if (criteria.fieldName && criteria.fieldName !== '') {
        let field = this.moduleMeta.fields.filter(
          field => field.name === criteria.fieldName
        )
        if (!isEmpty(field) && field[0].name === 'siteId') {
          field[0].dataTypeEnum._name = 'LOOKUP'
        }
        if (!isEmpty(field) && field[0].dataTypeEnum._name) {
          let oprs = this.moduleMeta.operators[field[0].dataTypeEnum._name]
          for (let key in oprs) {
            if (this.excludeOperators.includes(oprs[key].operatorId)) {
              delete this.moduleMeta.operators[field[0].dataTypeEnum._name][key]
            }
          }
        }
        if (field.length) {
          if (
            field[0].dataTypeEnum._name === 'LOOKUP' &&
            field[0].lookupModule &&
            field[0].lookupModule.name === 'resource'
          ) {
            criteria.isResourceOperator = true
            return
          } else if (
            field[0].specialType &&
            field[0].specialType === 'basespace'
          ) {
            criteria.isResourceOperator = false
            let oprs = this.moduleMeta.operators[field[0].dataTypeEnum._name]
            oprs['is'] = 'BUILDING_IS'
            return oprs
          } else {
            if (
              field[0].dataTypeEnum._name === 'DATE' ||
              field[0].dataTypeEnum._name === 'DATE_TIME'
            ) {
              criteria.isResourceOperator = false
              let remove = []
              Object.keys(
                this.moduleMeta.operators[field[0].dataTypeEnum._name]
              ).forEach(d => {
                if (this.valueNotNeed.indexOf(d) === -1) {
                  remove.push(d)
                }
              })
              if (remove) {
                remove.filter(d => {
                  delete this.moduleMeta.operators[field[0].dataTypeEnum._name][
                    d
                  ]
                })
              }
            } else if (field[0].dataTypeEnum._name === 'ENUM') {
              delete this.moduleMeta.operators[field[0].dataTypeEnum._name][
                'value is'
              ]
              delete this.moduleMeta.operators[field[0].dataTypeEnum._name][
                "value isn't"
              ]
            } else if (
              field[0].dataType === 12 &&
              field[0].dataTypeEnum._name === 'SYSTEM_ENUM'
            ) {
              let operators = {}
              let oprs = this.moduleMeta.operators['ENUM']
              for (let key in oprs) {
                if (this.excludeOperators.includes(oprs[key].operatorId)) {
                  delete this.moduleMeta.operators['ENUM'][key]
                }
              }
              Object.keys(this.moduleMeta.operators['ENUM']).forEach(
                operator => {
                  if (!['value is', "value isn't"].includes(operator)) {
                    operators[operator] = this.moduleMeta.operators['ENUM'][
                      operator
                    ]
                  }
                }
              )
              return operators
            } else if (field[0].dataTypeEnum._name === 'LOOKUP') {
              let operators = {}

              if (
                field[0].name === 'space' ||
                (!isEmpty(field[0].lookupModule) &&
                  field[0].lookupModule.name === 'building')
              ) {
                Object.keys(
                  this.moduleMeta.operators[field[0].dataTypeEnum._name]
                ).forEach(operator => {
                  if (
                    operator !== 'role is' ||
                    field[0].specialType === 'users'
                  ) {
                    operators[operator] = this.moduleMeta.operators[
                      field[0].dataTypeEnum._name
                    ][operator]
                  }
                })
              } else {
                Object.keys(
                  this.moduleMeta.operators[field[0].dataTypeEnum._name]
                ).forEach(operator => {
                  if (
                    operator !== 'building_is' &&
                    (operator !== 'role is' || field[0].specialType === 'users')
                  ) {
                    operators[operator] = this.moduleMeta.operators[
                      field[0].dataTypeEnum._name
                    ][operator]
                  }
                })
              }
              return operators
            } else if (
              (field[0].name === 'alarmType' && this.module === 'alarm') ||
              (field[0].name === 'preRequestStatus' &&
                this.module === 'workorder')
            ) {
              return this.systemEnumOperator
            }
            return this.moduleMeta.operators[field[0].dataTypeEnum._name]
          }
        }
      }
      return null
    },
    addCreteria() {
      let keyId
      let conditionLength = Object.keys(this.rule.conditions).length
      if (conditionLength === 0) {
        keyId = 1
      } else {
        keyId =
          parseInt(Object.keys(this.rule.conditions)[conditionLength - 1]) + 1
      }
      this.$set(this.rule.conditions, keyId, {})
      let tokens = this.tokenize(this.rule.pattern)
      let postfix = this.postfix(tokens)
      this.addOperand(postfix, keyId)
      let expressionTree = this.expressionTree(postfix)
      let infix = []
      this.infix(expressionTree, infix)
      this.rule.resourceOperator = false
      this.rule.pattern = this.toPattern(infix)
    },
    deleteCreteria(index) {
      this.$delete(this.rule.conditions, index)
      let pat = Object.keys(this.rule.conditions).join(
        ` ${this.conditionOperator} `
      )
      this.rule.pattern = '(' + pat + ')'
    },
    addOperand(postfix, key) {
      let len = postfix.length
      postfix.push(key)
      if (len !== 0) {
        postfix.push(this.conditionOperator)
      }
    },
    toPattern(infix) {
      let pattern = ''
      infix.forEach(e => {
        if (e === 'and' || e === 'or') {
          pattern += ` ${e} `
        } else {
          pattern += e
        }
      })
      return pattern
    },
    expressionTree(postfix) {
      let stack = []
      postfix.forEach(e => {
        if (e === 'and' || e === 'or') {
          let right = stack.pop()
          let left = stack.pop()
          let node = { left, right, data: e, parent: null, isRightChild: null }
          right.parent = node
          right.isRightChild = true
          left.parent = node
          left.isRightChild = false
          stack.push(node)
        } else {
          stack.push({ data: e })
        }
      })
      if (stack.length === 0) {
        return {}
      }
      return stack[0]
    },
    infix(expressionTree, expression) {
      if (expressionTree) {
        if (
          !expressionTree.parent &&
          !expressionTree.left & !expressionTree.right
        ) {
          expression.push('(')
          expression.push(expressionTree.data)
          expression.push(')')
          return
        }
      } else {
        return
      }
      let addBracket =
        expressionTree &&
        /[a-z]+/.test(expressionTree.data) &&
        (!expressionTree.parent ||
          (expressionTree.data === 'or' &&
            expressionTree.parent.data === 'and'))
      if (addBracket) {
        expression.push('(')
      }
      this.infix(expressionTree.left, expression)
      expression.push(expressionTree.data)
      this.infix(expressionTree.right, expression)
      if (addBracket) {
        expression.push(')')
      }
    },
    postfix(tokens) {
      let stack = []
      let output = []
      let current = -1
      let prev = null
      tokens.forEach(e => {
        let pass =
          /\d+/.test(e) || e === 'and' || e === 'or' || e === ')' || e === '('
        if (!pass) {
          throw new Error('Invalid Character')
        }

        if (
          e === '(' &&
          prev !== null &&
          prev !== '(' &&
          prev !== 'and' &&
          prev !== 'or'
        ) {
          throw new Error('Invalid Expression')
        } else if (e === ')' && prev !== ')' && !/\d+/.test(prev)) {
          throw new Error('Invalid Expression')
        } else if (
          (e === 'and' || e === 'or') &&
          prev !== ')' &&
          !/\d+/.test(prev)
        ) {
          throw new Error('Invalid Expression')
        } else if (
          /\d+/.test(e) &&
          prev &&
          prev !== '(' &&
          prev !== 'and' &&
          prev !== 'or'
        ) {
          throw new Error('Invalid Expression')
        }
        if (e === '(') {
          stack[++current] = e
        } else if (/\d+/.test(e)) {
          if (!this.rule.conditions[e]) {
            throw new Error('Invalid Expression')
          }
          output.push(e)
        } else if (e === 'and') {
          while (
            current > -1 &&
            stack[current] !== '(' &&
            stack[current] !== 'or'
          ) {
            output.push(stack[current])
            current--
          }
          stack[++current] = e
        } else if (e === 'or') {
          while (current > -1 && stack[current] !== '(') {
            output.push(stack[current])
            current--
          }
          stack[++current] = e
        } else if (e === ')') {
          while (current > -1 && stack[current] !== '(') {
            output.push(stack[current])
            current--
          }
          if (stack[current] === '(') {
            current--
          }
        }
        prev = e
      })
      if (current !== -1) {
        throw new Error('Invalid Expression')
      }
      return output
    },
    tokenize(pattern) {
      let tokens = []
      let curr = ''
      for (let c of pattern) {
        if (c === '(') {
          tokens.push(c)
        } else if (c === ')') {
          if (curr !== '') {
            tokens.push(curr)
            curr = ''
          }
          tokens.push(c)
        } else if (/\d/.test(c)) {
          if (curr === 'and' || curr === 'or' || curr === '(') {
            tokens.push(curr)
            curr = ''
          } else if (curr === '') {
            curr += c
          } else {
            if (/\d/.test(curr.charAt(curr.length - 1))) {
              curr += c
            } else {
              tokens.push(curr)
              curr = c
            }
          }
        } else if (/[a-z]/.test(c)) {
          if (curr !== '' && /\d/.test(curr.charAt(curr.length - 1))) {
            tokens.push(curr)
            curr = ''
          }
          curr += c
          if (curr === 'and' || curr === 'or') {
            tokens.push(curr)
            curr = ''
          }
        } else if (/\s/.test(c)) {
          if (curr !== '') {
            tokens.push(curr)
            curr = ''
          }
        } else {
          if (curr !== '') {
            tokens.push(curr)
          }
          curr = ''
          tokens.push(c)
        }
      }
      if (curr !== '') {
        tokens.push(curr)
      }
      return tokens
    },
  },
}
</script>
<style lang="scss">
/* .el-radio-button__inner{
 box-shadow: 0 2px 4px 0 rgba(232, 229, 229, 0.5);
 background-color: #ffffff;
 border: solid 1px #e2e8ee;
 border-left: 0;
}
.el-radio-button__orig-radio:checked+.el-radio-button__inner{
 background-color: #f1fdff;
 border: solid 1px #39b2c2;
 font-size: 14px;
 font-weight: 500;
 letter-spacing: 0.5px;
 text-align: center;
 color: #30a0af;
 box-shadow: none;
 box-shadow: 0 2px 4px 0 rgba(232, 229, 229, 0.5);
} */
.criteria-container {
  width: 100%;
  max-width: 744px;
  border-radius: 5px;
  background-color: #ffffff;
  overflow-y: scroll;
}
.criteria-inner-container {
  width: 100%;
  overflow: hidden;
  position: relative;
}
/* .criteria-radio-block{
 padding: 29px 0 23px;
} */
.criteria-condition-block {
  width: 100%;
  display: flex;
  flex-direction: row;
  flex-wrap: nowrap;
  align-items: center;
  background: transparent;
  padding: 15px 20px 15px 10px;
  cursor: pointer;
  border-radius: 3px;
  transition: 0.3s all ease-in-out;
  -webkit-transition: 0.3s all ease-in-out;
}
.criteria-condition-block:hover {
  background-color: #f1f8fa;
  transition: 0.3s all ease-in-out;
  -webkit-transition: 0.3s all ease-in-out;
}
.criteria-condition-block .el-input.is-disabled .el-input__inner {
  background: none;
}
.creteria-dropdown {
  width: 32%;
  padding-right: 10px;
}
.creteria-input {
  width: 32%;
}
/* .creteria-input .fc-tag .el-select__tags {
  margin-top: 14px;
}*/
.criteria-condition-block .creteria-delete-icon {
  min-width: 45px;
}
.criteria-condition-block .delete-icon {
  position: relative;
  top: 0;
  left: 10px;
  margin-right: 5px;
}
.criteria-condition-block:hover .delete-icon {
  cursor: pointer;
  position: relative;
  visibility: visible;
}
.criteria-condition-block .el-input__inner {
  background: transparent;
  margin-bottom: 0;
}
// .criteria-container .el-input .el-input__inner,
// .criteria-container .el-textarea .el-textarea__inner {
//   height: 40px !important;
//   line-height: 40px !important;
//   padding-left: 10px !important;
//   padding-right: 10px !important;
// }
.creteria-footer {
  margin-top: 33px;
  padding-bottom: 20px;
}
.criteria-pattern-txt {
  font-size: 14px;
  font-weight: 500;
  letter-spacing: 0.5px;
  color: #415e7b;
  font-weight: 500;
}
.criteria-input .el-input__inner {
  font-weight: 600 !important;
}
/* .creteria-dropdown .el-input__suffix{
   right: 0;
 } */
.edittext .el-textarea__inner {
  resize: none;
}
.criteria-radio-block .el-radio__input.is-disabled .el-radio__inner,
.el-radio__input.is-disabled.is-checked .el-radio__inner {
  border-color: #c5cdd4 !important;
}
.criteria-radio-block
  .el-radio__input.is-disabled.is-checked
  .el-radio__inner::after {
  background-color: #c5cdd4 !important;
}
.criteria-radio-label {
  display: -webkit-inline-box !important;
}

.rule-disabled {
  position: absolute;
  width: 100%;
  height: 100%;
  background: #ffffff91;
  cursor: not-allowed;
  z-index: 100;
}

.check-box-label-font-size {
  font-size: 12px;
}

.check-box-height-widht-size .el-checkbox__inner {
  width: 15px;
  height: 15px;
}

.disable-overlay {
  position: absolute;
  width: 100%;
  height: 100%;
  background: transparent;
  z-index: 9999;
}
.fc-criteria-value-select {
  .el-select__tags {
    flex-wrap: wrap !important;
  }
}
</style>
