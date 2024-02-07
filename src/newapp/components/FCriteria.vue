<template>
  <div v-if="isLoading" class="d-flex">
    <FieldLoader
      :isLoading="isLoading"
      class="operator-container mR10"
    ></FieldLoader>
    <FieldLoader :isLoading="isLoading"></FieldLoader>
  </div>
  <!-- eslint-disable-next-line vue/valid-template-root -->
  <div v-else class="d-flex" :class="currencyRangeClass">
    <div class="operator-container">
      <el-select
        v-model="operatorValue"
        filterable
        clearable
        :disabled="field.isDisabled"
        :key="`${field.name} Operator`"
        :ref="`${field.name}-operator`"
        class="fc-input-full-border-select2 width100"
        @clear="resetValues"
        @change="handleOperatorChange"
      >
        <el-option
          v-for="(operator, index) in fieldOperators"
          :key="index"
          :label="operator.displayName"
          :value="operator.id"
        ></el-option>
      </el-select>
    </div>
    <div class="value-container">
      <template v-if="isPicklistTypeField || isBooleanTypeField">
        <el-select
          class="fc-input-full-border-select2 width100 fc-tag"
          :key="`${field.name} Value`"
          :ref="`${field.name}-value`"
          multiple
          :disabled="disableValueContainer"
          v-model="fieldValue"
        >
          <el-option
            v-for="(option, index) in field.options"
            :key="index"
            :label="option.label"
            :value="option.value"
          >
          </el-option>
        </el-select>
      </template>
      <template
        v-else-if="
          !disableValueContainer && (isLookupTypeField || isLookupPopupField)
        "
      >
        <FLookupField
          class="resource-field"
          :key="`${field.name} Value`"
          :ref="`${field.name}-value`"
          :model.sync="fieldValue"
          :field="field"
          :siteId="selectedSiteId"
          :hideLookupIcon="canHidePopupIcon"
          :hideDropDown="hideDropDown"
          @showLookupWizard="showLookupWizard"
        ></FLookupField>
      </template>
      <el-input
        v-else-if="operatorValue === 106 || operatorValue === 107"
        v-model="fieldValue0"
        class="fc-select-multiple-tag fc-criteria-value-select fc-tag width100"
      >
      </el-input>
      <el-select
        v-else-if="[101, 108, 103].includes(operatorValue)"
        v-model="fieldValue"
        multiple
        class="fc-select-multiple-tag fc-criteria-value-select fc-tag width100"
      >
        <el-option
          v-for="(value, index) in dateOperatorValues(operatorValue)"
          :key="index"
          :label="value"
          :value="value"
        ></el-option>
      </el-select>
      <el-select
        v-else-if="operatorValue === 85"
        v-model="fieldValue"
        multiple
        class="fc-select-multiple-tag fc-criteria-value-select fc-tag width100"
      >
        <el-option
          v-for="(weekValue, weekKey) in $constants.WEEK_DAYS"
          :key="weekKey"
          :label="weekValue"
          :value="parseInt(weekKey)"
        ></el-option>
      </el-select>
      <el-select
        v-else-if="operatorValue === 84"
        v-model="fieldValue"
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
      <template v-else-if="!disableValueContainer && isDateTypeField">
        <FDatePicker
          v-if="operatorValue === 20"
          :key="`${field.name} Value`"
          :ref="`${field.name}-value`"
          v-model="fieldValue"
          :type="dateType"
          class="fc-input-full-border2 form-date-picker"
        ></FDatePicker>
        <FDatePicker
          v-else
          :ref="`${field.name}-value`"
          v-model="fieldValue0"
          :type="dateType"
          class="fc-input-full-border2 form-date-picker"
        ></FDatePicker>
      </template>
      <template v-else-if="!disableValueContainer && isTimeField">
        <FTimePicker
          :disabled="disableValueContainer"
          v-model="fieldValue0"
          :placeholder="$t('fields.properties.time_picker_placeholder')"
          class="el-select fc-input-full-border-select2 width100"
        ></FTimePicker>
      </template>
      <template v-else-if="isCurrencyTypeField">
        <template v-if="!isRangeTypeOperator">
          <el-input
            type="number"
            class="fc-input-full-border2 fc-slot-input-prepend"
            :key="`${field.name} Value`"
            :ref="`${field.name}-value`"
            :disabled="disableValueContainer"
            v-model="fieldValue0"
          >
            <div class="currency-symbol" slot="prepend">
              {{ orgCurrency }}
            </div>
          </el-input>
        </template>
        <template v-else>
          <div class="d-flex">
            <el-input
              class="fc-input-full-border2 fc-slot-input-prepend"
              type="number"
              :key="`${field.name} Value Start`"
              :ref="`${field.name}-value-start`"
              :disabled="disableValueContainer"
              v-model="fieldValue0"
              placeholder="from"
            >
              <div class="currency-symbol" slot="prepend">
                {{ orgCurrency }}
              </div>
            </el-input>
            <div class="seperator">-</div>
            <el-input
              class="fc-input-full-border2 fc-slot-input-prepend"
              type="number"
              :key="`${field.name} Value End`"
              :ref="`${field.name}-value-end`"
              :disabled="disableValueContainer"
              v-model="fieldValue1"
              placeholder="to"
            >
              <div class="currency-symbol" slot="prepend">
                {{ orgCurrency }}
              </div>
            </el-input>
          </div>
        </template>
      </template>
      <template v-else>
        <el-input
          v-if="!isRangeTypeOperator"
          :type="isNumberTypeField ? 'number' : 'txt'"
          class="fc-input-full-border2"
          :key="`${field.name} Value`"
          :ref="`${field.name}-value`"
          :disabled="disableValueContainer"
          v-model="fieldValue0"
        ></el-input>
        <div v-else class="d-flex">
          <el-input
            class="fc-input-full-border2 "
            type="number"
            :key="`${field.name} Value Start`"
            :ref="`${field.name}-value-start`"
            :disabled="disableValueContainer"
            v-model="fieldValue0"
          >
          </el-input>
          <el-input
            class="fc-input-full-border2 pL10"
            type="number"
            :key="`${field.name} Value End`"
            :ref="`${field.name}-value-end`"
            :disabled="disableValueContainer"
            v-model="fieldValue1"
          >
          </el-input>
        </div>
      </template>
      <div v-if="field.isError" class="err-txt">{{ errorText }}</div>
    </div>
    <div v-if="canShowLookupWizard">
      <FLookupFieldWizard
        :canShowLookupWizard.sync="canShowLookupWizard"
        :selectedLookupField="selectedLookupField"
        :siteId="selectedSiteId"
        :disableTabSwitch="disableTabSwitch"
        :skipDecommission="true"
        @setLookupFieldValue="setLookupFieldValue"
      ></FLookupFieldWizard>
    </div>
  </div>
</template>
<script>
import { mapActions, mapGetters } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import isString from 'lodash/isString'
import FLookupField from '@/forms/FLookupField'
import FLookupFieldWizard from '@/FLookupFieldWizard'
import FDatePicker from 'pages/assets/overview/FDatePicker'
import FieldLoader from '@/forms/FieldLoader'
import FTimePicker from '@/FTimePicker'

/*
  81 - between,
  82 - not between
*/
const rangeTypeOperatorIds = [81, 82, 122, 123]
const numberTypeFields = ['DECIMAL', 'DURATION', 'NUMBER']

export default {
  name: 'f-criteria',
  props: ['field'],
  components: {
    FLookupField,
    FLookupFieldWizard,
    FDatePicker,
    FieldLoader,
    FTimePicker,
  },

  data() {
    return {
      operatorValue: null,
      operatorsList: [],
      canShowLookupWizard: false,
      selectedLookupField: null,
      selectedSiteId: null,
      filterObj: {},
      isLoading: false,
    }
  },
  computed: {
    ...mapGetters({
      getOperatorsList: 'getOperatorsList',
    }),

    fieldValue: {
      get() {
        let {
          filterObj,
          field,
          isDateTypeField,
          isLookupTypeField,
          isLookupPopupField,
          isPicklistTypeField,
          isStringSystemEnumField,
        } = this
        let { name } = field
        let { value = [] } = filterObj[name] || {}
        if (
          isDateTypeField ||
          isLookupTypeField ||
          isLookupPopupField ||
          isPicklistTypeField
        ) {
          if (!isStringSystemEnumField)
            value = value.map(value => {
              // regex to match ${LOREM_IPSUM}
              let regexValue = value.match(/\$\{[A-Z]+\_[A-Z]+\}/g)
              if (!isEmpty(regexValue)) {
                return value
              }
              return value ? Number(value) : null
            })
        }
        return value
      },
      set(value) {
        let { field } = this
        let { name } = field || {}
        if (isEmpty(value)) {
          this.$set(this.filterObj[name], 'value', [])
        } else {
          // Values should always be a string
          let serializedArr = value.map(val => {
            if (!isEmpty(val)) {
              return `${val}`
            }
            return ''
          })
          this.$set(this.filterObj[name], 'value', serializedArr)
          this.$emit('updateFilter', this.filterObj)
        }
      },
    },
    fieldValue0: {
      get() {
        let { fieldValue } = this
        return fieldValue[0] || null
      },
      set(value) {
        let { fieldValue } = this
        let [, secondIndexValue] = fieldValue
        if (secondIndexValue) {
          this.$set(this, 'fieldValue', [value, secondIndexValue])
        } else {
          this.$set(this, 'fieldValue', [value])
        }
      },
    },
    fieldValue1: {
      get() {
        let { fieldValue } = this
        return fieldValue[1] || null
      },
      set(value) {
        let { fieldValue } = this
        let [firstIndexValue] = fieldValue
        this.$set(this, 'fieldValue', [firstIndexValue, value])
      },
    },
    fieldOperators() {
      let { field, operatorsList } = this
      let { dataType, operators = [] } = field
      if (!isEmpty(operatorsList)) {
        let list = operatorsList[dataType] || []
        if (!isEmpty(operators)) {
          list = list.concat(operators)
        }
        /*
           Since some operator like next 7 days, next 2 days have same operatorId,
           we have to construct a unique id in client.
        */
        let finalList = list.map(operator => {
          let { operatorId, defaultValue } = operator
          let unqiueClientId = operatorId
          if (!isEmpty(defaultValue)) {
            unqiueClientId = `${operatorId}-${defaultValue}`
          }
          return {
            ...operator,
            id: unqiueClientId,
          }
        })
        return finalList
      }
      return []
    },
    disableValueContainer() {
      let { operatorValue, fieldOperators } = this
      let canDisable = true
      let selectedOperator = fieldOperators.find(
        operator => operator.id === operatorValue
      )
      if (!isEmpty(selectedOperator)) {
        canDisable = !(selectedOperator || {}).valueNeeded
      }
      return canDisable
    },
    orgCurrency() {
      let { $account } = this
      let { data } = $account
      let { currencyInfo } = data
      let { displaySymbol } = currencyInfo || {}

      return displaySymbol
    },
    isRangeTypeOperator() {
      let { operatorValue } = this
      return rangeTypeOperatorIds.includes(operatorValue)
    },
    isNumberTypeField() {
      let { field } = this
      let { displayType } = field || {}
      return numberTypeFields.includes(displayType)
    },
    isDateTypeField() {
      let { field } = this
      let { displayType } = field || {}
      return ['DATETIME', 'DATE'].includes(displayType)
    },
    isCurrencyTypeField() {
      let { field } = this
      let { displayType } = field || {}
      return ['CURRENCY'].includes(displayType)
    },
    isLookupTypeField() {
      let { field } = this
      let { displayType } = field || {}

      return ['LOOKUP_SIMPLE', 'MULTI_LOOKUP_SIMPLE'].includes(displayType)
    },
    isTimeField() {
      let { field } = this
      let { displayType } = field || {}
      return displayType === 'TIME'
    },
    isLookupPopupField() {
      let { field } = this
      let { displayType } = field || {}
      return ['LOOKUP_POPUP'].includes(displayType)
    },
    isPicklistTypeField() {
      let { field } = this
      let { displayType } = field || {}
      return ['SELECTBOX', 'MULTI_SELECTBOX'].includes(displayType)
    },
    isStringSystemEnumField() {
      let { field } = this
      let { dataType } = field || {}
      return ['STRING_SYSTEM_ENUM'].includes(dataType)
    },
    isBooleanTypeField() {
      let { field } = this
      let { displayType } = field || {}
      return ['DECISION_BOX'].includes(displayType)
    },
    isMultipleValuesField() {
      let {
        isPicklistTypeField,
        isBooleanTypeField,
        isDateTypeField,
        isRangeTypeOperator,
        isLookupTypeField,
        isLookupPopupField,
      } = this
      return (
        isPicklistTypeField ||
        isBooleanTypeField ||
        isDateTypeField ||
        isLookupTypeField ||
        isLookupPopupField ||
        isRangeTypeOperator
      )
    },
    dateType() {
      let { field, operatorValue } = this
      let isRange = operatorValue === 20
      let { displayType } = field
      let displayTypeValue = !isEmpty(displayType)
        ? displayType.toLowerCase()
        : 'date'
      if (isRange) {
        return `${displayTypeValue}range`
      } else {
        return displayTypeValue
      }
    },
    errorText() {
      let { field } = this
      let { displayName } = field || {}
      return `Please input ${displayName}`
    },
    canHidePopupIcon() {
      let { field } = this
      let { lookupModule } = field || {}
      let { showPopup = true } = lookupModule || {}
      return !showPopup
    },
    hideDropDown() {
      // Have to hide dropdown for resource type fields
      let { field } = this
      let { name } = field
      return name === 'resource'
    },
    disableTabSwitch() {
      // Have to disable tab switch, if 'within' operator is selected
      // This is done to make sure that within operator is applied only for space field
      let { field, filterObj } = this
      let { name } = field || {}
      let filterValue = filterObj[name]
      let { operatorId } = filterValue || {}
      return operatorId === 38 && name === 'resource'
    },
    currencyRangeClass() {
      let { isCurrencyTypeField, isRangeTypeOperator } = this
      if (isCurrencyTypeField && isRangeTypeOperator)
        return 'currency-container'
      return ''
    },
  },

  watch: {
    disableValueContainer(value) {
      if (value) {
        this.resetValues()
      }
    },
    filterObj: {
      handler(value) {
        this.$emit('updateFilter', value)
      },
      deep: true,
    },
    operatorValue(value) {
      let {
        field,
        disableValueContainer,
        filterObj,
        isMultipleValuesField,
      } = this
      let { name } = field || {}
      let obj = filterObj[name]
      let { value: valueArr = [] } = obj || {}
      // to handle special type for operators like next 2 days, next 7 days
      if (!isEmpty(value) && isString(value)) {
        let [operatorId, defaultValue] = value.split('-')
        this.$set(this.filterObj[name], 'operatorId', Number(operatorId))
        this.$set(this.filterObj[name], 'value', [`${defaultValue}`])
      } else {
        this.$set(this.filterObj[name], 'operatorId', value)
        /*
         between and not between operators will be array with two values,
         hence we have to remove the second element for not case
        */
        if (!isMultipleValuesField && !isEmpty(valueArr[1])) {
          delete this.filterObj[name].value.splice(1, 1)
        }
        if (disableValueContainer) {
          delete this.filterObj[name].value
        } else if (isEmpty(valueArr)) {
          this.$set(this.filterObj[name], 'value', [])
        }
      }
    },
    fieldValue(value) {
      let { disableValueContainer } = this
      if (!disableValueContainer && !isEmpty(value)) {
        this.$set(this.field, 'isError', false)
      }
    },
  },
  created() {
    this.$set(this, 'isLoading', true)
    this.loadOperators()
      .then(() => {
        let operatorsList = this.getOperatorsList()
        this.$set(this, 'operatorsList', operatorsList)
        this.initFilterObj()
      })
      .finally(() => {
        this.$set(this, 'isLoading', false)
      })
  },
  methods: {
    ...mapActions({
      loadOperators: 'loadOperators',
    }),

    initFilterObj() {
      let { field, fieldOperators } = this
      let { name, filterObj = {} } = field || {}

      if (isEmpty(filterObj)) {
        let filter = {
          operatorId: '',
          value: [],
        }
        let defaultOperatorId = null
        if (!isEmpty(fieldOperators)) {
          let defaultOperator =
            fieldOperators.find(operator => operator.defaultSelection) || {}
          defaultOperatorId = defaultOperator.operatorId
        }
        if (!isEmpty(defaultOperatorId)) {
          filter.operatorId = defaultOperatorId
          this.$set(this, 'operatorValue', defaultOperatorId)
        }
        filterObj[name] = filter
      } else {
        let { operatorId } = filterObj[name] || {}
        let operatorDefaultValue = this.getOperatorDefaultValue(operatorId)
        if (!isEmpty(operatorDefaultValue)) {
          operatorId = `${operatorId}-${operatorDefaultValue}`
        }
        this.$set(this, 'operatorValue', operatorId)
      }
      this.$set(this, 'filterObj', filterObj)
    },
    getOperatorDefaultValue(operatorId) {
      let { fieldOperators } = this
      // Operators with default values, for instance: Next 2 days, Next 7 days
      let selectedOperator = fieldOperators.find(
        operator => operator.operatorId === operatorId
      )
      return (selectedOperator || {}).defaultValue || ''
    },
    handleOperatorChange(value) {
      let {
        field,
        fieldOperators,
        isLookupTypeField,
        isLookupPopupField,
        isDateTypeField,
      } = this
      let selectedOperator = fieldOperators.find(
        operator => operator.operatorId === value
      )
      // Special handling for role is operator, have to fetch roles list in right side
      if (!isEmpty(selectedOperator)) {
        // Have to reset selected items array in field, if field is lookup type
        let canResetValue =
          isLookupTypeField || isLookupPopupField || isDateTypeField
        if (canResetValue) {
          this.resetValues()
          if (isLookupTypeField || isLookupPopupField)
            this.$set(this.field, 'selectedItems', [])
        }
        let { lookupModule = {} } = selectedOperator || {}
        this.$set(field, 'operatorLookupModule', lookupModule)
      }
    },
    resetValues() {
      this.$set(this, 'fieldValue', [])
    },
    showLookupWizard(field, canShow) {
      this.$set(this, 'selectedLookupField', field)
      this.$set(this, 'canShowLookupWizard', canShow)
    },
    setLookupFieldValue(props) {
      let { field } = props
      let { options = [], selectedItems = [] } = field
      let selectedItemIds = []
      if (!isEmpty(selectedItems)) {
        selectedItemIds = selectedItems.map(item => item.value)
        if (!isEmpty(options)) {
          let ids = options.map(item => item.value)
          // Have to push only new options that doesnt exists in field options
          let newOptions = selectedItems.filter(
            item => !ids.includes(item.value)
          )
          options.unshift(...newOptions)
        } else {
          options = [...selectedItems]
        }
      }
      this.$set(this, 'selectedLookupField', {})
      this.$set(this.field, 'options', options)
      this.$set(this, 'fieldValue', selectedItemIds)
    },
    validate() {
      let { disableValueContainer, fieldValue, isRangeTypeOperator } = this
      let isValid = true
      if (!disableValueContainer) {
        if (isRangeTypeOperator) {
          let isValidFieldValues = fieldValue.every(value => !isEmpty(value))
          if (isValidFieldValues && fieldValue.length === 2) {
            isValid = true
          } else {
            isValid = false
          }
        } else {
          isValid = !isEmpty(fieldValue)
        }
      }
      this.$set(this.field, 'isError', !isValid)
      return isValid
    },
    dateOperatorValues(operatorValue) {
      let dateOperatorValues = []
      let operatorValueObj = {
        101: 31,
        108: 52,
      }
      if (operatorValue === 103) {
        dateOperatorValues = Array(24)
          .fill()
          .map((_, i) => i)
      } else {
        dateOperatorValues = Array(operatorValueObj[operatorValue])
          .fill()
          .map((_, i) => i + 1)
      }
      return dateOperatorValues
    },
  },
}
</script>

<style lang="scss">
.currency-container {
  flex-direction: column;
  gap: 8px;
  .seperator {
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 4px;
  }
  .operator-container {
    justify-content: left;
  }
  .value-container {
    padding-left: 0px;
  }
}
.operator-container {
  flex: 0 0 35%;
}
.value-container {
  flex: 1;
  padding-left: 10px;
  .resource-field {
    .el-input__prefix {
      left: 83%;
      z-index: 10;
    }
  }
  .el-date-editor {
    &.el-input__inner {
      width: auto !important;
    }
  }
  .err-txt {
    color: #f56c6c;
    font-size: 12px;
    line-height: 1;
    margin-top: 4px;
  }
  .el-input-group__prepend:has(.currency-symbol) {
    border-right: none;
  }
  .el-input-group__prepend:has(.currency-symbol) + input[type='number'] {
    border-top-left-radius: 0 !important;
    border-bottom-left-radius: 0 !important;
  }
}
.criteria-loading {
  display: flex;
  .field-loading {
    height: 40px;
    flex: 0 0 50%;
    position: relative;
    border-radius: 3px;
    background-color: #f5f5f5;
    &::after {
      display: block;
      content: '';
      position: absolute;
      width: 100%;
      height: 100%;
      transform: translateX(-100%);
      background: linear-gradient(
        90deg,
        hsla(0, 0%, 100%, 0),
        hsla(0, 0%, 100%, 0.3),
        hsla(0, 0%, 100%, 0)
      );
      animation: loading 1.5s infinite;
    }
  }
}
@keyframes loading {
  100% {
    transform: translateX(100%);
  }
}
</style>
