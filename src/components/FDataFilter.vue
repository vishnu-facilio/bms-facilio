<template>
  <div class="data-filter-container">
    <div v-if="canShowMatchConditions">
      <el-radio-group v-model="matchingCondition">
        <el-radio :label="1" class="fc-radio-btn">{{
          $t('datafilter.conditions.match_all')
        }}</el-radio>
        <el-radio :label="0" class="fc-radio-btn">{{
          $t('datafilter.conditions.match_any')
        }}</el-radio>
      </el-radio-group>
    </div>
    <div v-if="!$validation.isEmpty(rules)" class="data-criteria-container">
      <div v-for="(rule, index) in rules" :key="index" class="criteria-item">
        <div class="criteria-index">
          <div class="label">{{ index + 1 }}</div>
        </div>
        <div class="criteria-resource">
          <FLookupField
            :key="`${index}`"
            :model.sync="rule.resourceId"
            :field="rule.field"
            :disabled="(rule.field || {}).isDisabled"
            :fetchOptionsOnLoad="loadAssetsInitial"
            :fetchOptionsMethod="fetchOptions"
            @recordSelected="handleRecordSelected"
            @showLookupWizard="showLookupWizard"
          ></FLookupField>
        </div>
        <div class="criteria-reading">
          <el-select
            v-model="rule.fieldId"
            :loading="rule.field.isDataLoading"
            filterable
            :placeholder="$t('datafilter.conditions.reading')"
            class="el-input-textbox-full-border"
            @change="
              onReadingChange({
                index,
                fieldId: rule.fieldId,
                resourceId: rule.resourceId,
              })
            "
          >
            <el-option
              v-for="(field, index) in readingsMap[rule.resourceId]"
              :key="index"
              :label="field.label"
              :value="field.value"
            >
            </el-option>
          </el-select>
        </div>
        <div class="criteria-operator">
          <el-select
            v-model="rule.operatorId"
            :loading="rule.field.isDataLoading"
            filterable
            :placeholder="$t('datafilter.conditions.operator')"
            class="el-input-textbox-full-border"
            @change="onOperatorChange(index)"
          >
            <el-option
              v-for="(field, index) in rule.operators"
              :key="index"
              :label="field.label"
              :value="field.value"
            >
            </el-option>
          </el-select>
        </div>
        <div v-if="rule.valueNeeded" class="criteria-value">
          <el-input
            v-if="
              rule.dataTypeEnumName === 'DECIMAL' ||
                rule.dataTypeEnumName === 'NUMBER'
            "
            v-model="rule.value"
            class="fc-input-full-border-select2"
          ></el-input>
          <el-select
            v-else-if="rule.dataTypeEnumName === 'BOOLEAN'"
            class="el-input-textbox-full-border"
            v-model="rule.value"
          >
            <el-option :label="rule.trueVal" :value="1"> </el-option>
            <el-option :label="rule.falseVal" :value="0"> </el-option>
          </el-select>
          <el-select
            v-else-if="rule.dataTypeEnumName === 'ENUM'"
            v-model="rule.value"
            class="el-input-textbox-full-border"
          >
            <el-option
              v-for="(option, index) in rule.enumArr"
              :key="index"
              :label="option.label"
              :value="option.value"
            >
            </el-option>
          </el-select>
          <el-input
            v-else
            v-model="rule.value"
            class="fc-input-full-border-select2"
          ></el-input>
        </div>
        <div v-else class="criteria-value">
          <el-input
            placeholder="Input not needed"
            disabled
            class="fc-input-full-border-select2"
          ></el-input>
        </div>
        <div class="d-flex">
          <div
            class="self-center cursor-pointer"
            v-if="canShowAddRule"
            @click="addRule(index)"
          >
            <inline-svg
              src="add-icon"
              class="vertical-middle"
              iconClass="icon icon-md mR5"
            ></inline-svg>
          </div>
          <div
            class="self-center cursor-pointer mL10"
            v-if="canShowRemoveRule"
            @click="removeRule(index)"
          >
            <inline-svg
              src="remove-icon"
              class="vertical-middle"
              iconClass="icon icon-md mR5"
            ></inline-svg>
          </div>
        </div>
      </div>
    </div>
    <div v-if="canShowLookupWizard">
      <FLookupFieldWizard
        :canShowLookupWizard.sync="canShowLookupWizard"
        :selectedLookupField="selectedLookupField"
        :withReadings="true"
        @setLookupFieldValue="setLookupFieldValue"
      ></FLookupFieldWizard>
    </div>
  </div>
</template>
<script>
import FLookupField from '@/forms/FLookupField'
import FLookupFieldWizard from '@/FLookupFieldWizard'
import cloneDeep from 'lodash/cloneDeep'
import { isEmpty } from '@facilio/utils/validation'
import { getFieldOptions } from 'util/picklist'

const rulesObj = {
  resourceId: null,
  fieldId: null,
  operatorId: null,
  operators: [],
  valueNeeded: true,
  dataTypeEnumName: null,
  value: null,
  trueVal: 'True',
  falseVal: 'False',
  enumArr: [],
  field: {
    isDataLoading: false,
    options: [],
    lookupModuleName: 'asset',
    field: {
      lookupModule: {
        name: 'asset',
        displayName: 'Asset',
      },
    },
    forceFetchAlways: true,
    filters: {},
    isDisabled: false,
  },
}

export default {
  components: {
    FLookupField,
    FLookupFieldWizard,
  },
  props: {
    excludeOperators: {
      type: Array,
      default: () => [],
    },
    rulesArr: {
      type: Array,
      default: () => [],
    },
    existingOptions: {
      type: Array,
      default: () => [],
    },
    canShowMatchConditions: {
      type: Boolean,
      default: true,
    },
    loadAssetsInitial: {
      type: Boolean,
      default: true,
    },
    withReadings: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      matchingCondition: 1,
      rules: [],
      selectedLookupField: null,
      canShowLookupWizard: false,
      readingsMap: {},
      operatorsMap: {},
    }
  },
  computed: {
    canShowAddRule() {
      let { rules } = this
      return rules.length <= 10
    },
    canShowRemoveRule() {
      let { rules } = this
      return rules.length > 1
    },
  },
  created() {
    this.init()
  },
  methods: {
    init() {
      let { rulesArr, excludeOperators, existingOptions } = this
      this.readingsPromiseMap = {}
      let finalRules = []
      if (!isEmpty(rulesArr)) {
        rulesArr.forEach((rule, index) => {
          let clonedRulesObj = cloneDeep(rulesObj)
          let obj = cloneDeep(Object.assign(clonedRulesObj, rule))
          obj.field.name = `${index}`
          obj.field.isDataLoading = true
          if (!isEmpty(obj.resourceId)) {
            this.fetchReadingsAndOperators(obj.resourceId).then(options => {
              let operatorsArr = []
              if (!isEmpty(options)) {
                let selectedOption =
                  options.find(option => option.value === obj.fieldId) || {}
                if (!isEmpty(selectedOption)) {
                  let {
                    operators,
                    _name,
                    trueVal,
                    falseVal,
                    enumArr,
                  } = selectedOption
                  operatorsArr = operators.filter(operator => {
                    return !excludeOperators.includes(operator.id)
                  })
                  obj.dataTypeEnumName = _name
                  obj.trueVal = trueVal
                  obj.falseVal = falseVal
                  obj.enumArr = enumArr
                }
              }
              obj.field.isDataLoading = false
              obj.operators = operatorsArr
              if (!isEmpty(obj.operatorId) && !isEmpty(operatorsArr)) {
                let selectedOperator =
                  operatorsArr.find(
                    operator => operator.value === obj.operatorId
                  ) || {}
                let { valueNeeded } = selectedOperator
                obj.valueNeeded = !!valueNeeded
              }
            })
          }
          if (!isEmpty(obj.resourceName) && !isEmpty(obj.resourceId)) {
            let selectedResourceObj = {
              label: obj.resourceName,
              value: obj.resourceId,
            }
            existingOptions.push(selectedResourceObj)
            obj.field.selectedItems = [
              {
                name: obj.resourceName,
                id: obj.resourceId,
              },
            ]
          }
          finalRules.push(obj)
        })
      } else {
        finalRules = [cloneDeep(rulesObj)]
        finalRules[0].field.name = '0'
      }
      if (!isEmpty(existingOptions)) {
        finalRules[0].field.options = existingOptions
      }
      this.$set(this, 'rules', finalRules)
    },
    async fetchOptions(props) {
      let { existingOptions } = this
      let options = []
      let { options: optionsArr = [], error } = await getFieldOptions(props)

      if (error) {
        let { message } = error || {}
        this.$message.error(message || 'Error Occured')
      } else {
        options = optionsArr
      }

      if (!isEmpty(existingOptions)) {
        let existingOptionIds = existingOptions.map(option => option.value)
        let filteredOptions = options.filter(
          option => !existingOptionIds.includes(option.value)
        )
        existingOptions.forEach(option => {
          filteredOptions.unshift(option)
        })
        options = filteredOptions
      }

      return { options }
    },
    fetchReadingsAndOperators(resourceId) {
      let { readingsPromiseMap, readingsMap, excludeOperators } = this
      let cachedOptions = readingsMap[resourceId]
      let isAlreadyCached = !isEmpty(cachedOptions)
      if (!isAlreadyCached) {
        let promise
        let isPromiseAvailable = !isEmpty(readingsPromiseMap[resourceId])
        if (!isPromiseAvailable) {
          promise = new Promise(resolve => {
            return this.$util
              .loadAssetReadingFields(resourceId)
              .then(readingFields => {
                let operatorsArr = []
                let enumArr = []
                let options = (readingFields || []).map(field => {
                  let {
                    displayName,
                    id,
                    dataTypeEnum: { operators, _name },
                    trueVal,
                    falseVal,
                    values,
                  } = field
                  if (isEmpty(trueVal)) {
                    trueVal = 'True'
                  }
                  if (isEmpty(falseVal)) {
                    falseVal = 'False'
                  }
                  if (!isEmpty(values)) {
                    enumArr = values.map(obj => {
                      let { value, index } = obj
                      return {
                        label: value,
                        value: index,
                      }
                    })
                  }
                  if (!isEmpty(operators)) {
                    operatorsArr = Object.entries(operators).map(
                      ([key, value]) => {
                        return {
                          label: key,
                          value: value.operatorId,
                          valueNeeded: value.valueNeeded,
                        }
                      }
                    )
                    operatorsArr = operatorsArr.filter(operator => {
                      return !excludeOperators.includes(operator.value)
                    })
                  }
                  return {
                    label: displayName,
                    value: id,
                    operators: operatorsArr,
                    _name,
                    trueVal,
                    falseVal,
                    enumArr,
                  }
                })
                this.$set(this.readingsMap, `${resourceId}`, options || [])
                if (!isEmpty(readingsPromiseMap[resourceId])) {
                  delete readingsPromiseMap[resourceId]
                }
                resolve(options)
              })
          })
        } else {
          promise = readingsPromiseMap[resourceId]
        }
        return promise
      }
      return Promise.resolve(cachedOptions)
    },
    handleRecordSelected(option, field) {
      let { name } = field
      let { value } = option || {}
      let index = Number(name)
      let { rules } = this
      let currentRule = rules[index]
      let { resourceId } = currentRule
      if (!isEmpty(value)) {
        let { existingOptions } = this
        let isAlreadyExists = false
        if (!isEmpty(existingOptions)) {
          isAlreadyExists = !isEmpty(
            existingOptions.find(option => option.value === value)
          )
        }
        if (!isAlreadyExists) {
          existingOptions.unshift(option)
          this.$emit('update:existingOptions', existingOptions)
        }
        currentRule.field.isDataLoading = true
        this.fetchReadingsAndOperators(resourceId).then(
          () => (currentRule.field.isDataLoading = false)
        )
      }
      this.resetRowData({ index, resourceId })
    },
    resetRowData(props) {
      let { index, resourceId } = props
      let { rules } = this
      let currentRule = rules[index]
      currentRule.resourceId = resourceId
      currentRule.fieldId = null
      currentRule.operatorId = null
      currentRule.valueNeeded = true
      currentRule.value = null
      currentRule.dataTypeEnumName = null
      rules[index] = currentRule
      this.$set(this, 'rules', rules)
    },
    showLookupWizard(field, canShow) {
      this.$set(this, 'selectedLookupField', field)
      this.$set(this, 'canShowLookupWizard', canShow)
    },
    setLookupFieldValue(props) {
      let { field } = props
      let { selectedItems } = field || {}
      let [selectedItem] = selectedItems || []
      let { value } = selectedItem
      let { selectedLookupField, rules } = this
      let { name, options } = selectedLookupField
      if (!isEmpty(options)) {
        let isAlreadyExists = !isEmpty(
          options.find(option => option.value === selectedItem.value)
        )
        if (!isAlreadyExists) {
          options.unshift(selectedItem)
        }
      } else {
        options.push(selectedItem)
      }
      this.$set(this.selectedLookupField, 'options', options)
      if (!isEmpty(name)) {
        rules[Number(name)].resourceId = value
        rules[Number(name)].field.isDataLoading = true
        this.fetchReadingsAndOperators(value).then(
          () => (rules[Number(name)].field.isDataLoading = false)
        )
        this.resetRowData({ index: Number(name), resourceId: value })
        this.$set(this, 'rules', rules)
      }
    },
    onReadingChange(props) {
      let { excludeOperators, rules } = this
      let operatorsArr = []
      let { index, fieldId, resourceId } = props
      let currentRule = rules[index]
      let { readingsMap } = this
      let options = readingsMap[resourceId]
      if (!isEmpty(options)) {
        let selectedOption = options.find(option => option.value === fieldId)
        let { operators, _name, trueVal, falseVal, enumArr } = selectedOption
        if (!isEmpty(operators)) {
          operatorsArr = operators.filter(operator => {
            return !excludeOperators.includes(operator.value)
          })
        }
        this.$set(currentRule, 'dataTypeEnumName', _name)
        this.$set(currentRule, 'trueVal', trueVal)
        this.$set(currentRule, 'falseVal', falseVal)
        this.$set(currentRule, 'enumArr', enumArr)
      }
      this.$set(currentRule, 'operators', operatorsArr)
    },
    onOperatorChange(index) {
      let { rules } = this
      let valueNeeded = false
      let currentRule = rules[index] || {}
      let { operatorId, operators } = currentRule
      if (!isEmpty(operators)) {
        let selectedOperator =
          operators.find(operator => operator.value === operatorId) || {}
        valueNeeded = !!selectedOperator.valueNeeded
      }
      this.$set(currentRule, 'valueNeeded', valueNeeded)
    },
    addRule(index) {
      let { rules, existingOptions } = this
      let newRule = cloneDeep(rulesObj)
      if (!isEmpty(existingOptions)) {
        newRule.field.options = existingOptions
      }
      rules.splice(index + 1, 0, newRule)
      rules.forEach((rule, index) => {
        rule.field.name = `${index}`
      })
      this.$set(this, 'rules', rules)
    },
    removeRule(index) {
      let { rules } = this
      rules.splice(index, 1)
      rules.forEach((rule, index) => {
        rule.field.name = `${index}`
      })
      this.$set(this, 'rules', rules)
    },
    saveRules() {
      let { rules } = this
      let serializedRule = []
      if (!isEmpty(rules)) {
        rules.forEach(rule => {
          let { resourceId, fieldId, field, operatorId, value } = rule
          serializedRule.push({
            resourceId,
            fieldId,
            field,
            operatorId,
            value,
          })
        })
      }
      return serializedRule
    },
  },
}
</script>
<style lang="scss">
.data-filter-container {
  // temp
  padding-bottom: 50px;
  .data-criteria-container {
    .criteria-item {
      display: flex;
      margin-top: 20px;
      .criteria-index {
        border: solid 1px #d0d9e2;
        background-color: #f4f8fc;
        padding: 8px;
        border-radius: 50%;
        display: flex;
        width: 25px;
        height: 25px;
        align-self: center;
        margin-right: 15px;
        .label {
          align-self: center;
          font-size: 12px;
          letter-spacing: 0.46px;
          text-align: center;
          color: #778898;
        }
      }
      .criteria-resource {
        .fc-input-full-border-select2 {
          .el-input__inner {
            height: 40px !important;
          }
          &.resource-search {
            .el-input {
              .el-input__prefix {
                right: 5px;
                left: 95%;
                z-index: 10;
                .fc-lookup-icon {
                  margin-top: 10px;
                }
              }
              .el-input__suffix {
                .el-icon-circle-close {
                  padding-left: 30px;
                }
              }
            }
          }
        }
      }
      .criteria-reading,
      .criteria-operator,
      .criteria-resource,
      .criteria-value {
        margin-right: 15px;
      }
    }
  }
}
</style>
