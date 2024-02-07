<template>
  <div class="w-full">
    <div class="flex w-full flex-nowrap relative h-14">
      <div class="index-container self-center">
        <div class="alphabet-circle">
          {{ index }}
        </div>
      </div>
      <div
        class="criteria-fields-container self-center w-full relative f-ui-forms"
      >
        <Cascader
          :options="fields"
          label="displayName"
          value="name"
          class="w-full f-ui-select f-cascader"
          v-model="conditionObj.fieldName"
          filterable
          clearable
          :key="`index ${conditionObj.fieldName} field`"
          :ref="`index-${conditionObj.fieldName}-field`"
          :props="cascaderProps"
          popper-class="cascader-popover"
          @clear="resetCondition"
          @change="setFieldValue"
          :filter-method="filterFields"
          :disabled="disabled"
        />
        <div v-if="canShowError" class="condition-error">
          {{ cascaderErrorMsg }}
        </div>
      </div>

      <div class="criteria-operator-container self-center w-full">
        <Select
          :options="fieldOperators"
          labelName="displayName"
          valueName="id"
          class="width-full"
          v-model="conditionObj.operatorId"
          filterable
          clearable
          :key="`index ${conditionObj.fieldName} Operator`"
          :ref="`index-${conditionObj.fieldName}-operator`"
          @clear="resetValue"
          @change="handleOperatorChange"
          :placeholder="operatorPlaceholder"
          :disabled="checkOperatorDisabled"
        />
      </div>

      <div
        v-if="showValueType"
        class="criteria-operator-container self-center w-full"
      >
        <Select
          :options="valueTypeOptions"
          class="width-full"
          v-model="valueType"
          filterable
          clearable
          :key="`index ${conditionObj.fieldName} Operator`"
          :ref="`index-${conditionObj.fieldName}-operator`"
          @clear="resetValue"
          @change="updateCustomValue"
          :disabled="checkOperatorDisabled"
        />
      </div>

      <ConditionValue
        v-if="showComputed"
        ref="conditionValue"
        :field.sync="field"
        v-model="conditionObj.value"
        :disableValueContainer="disableValueContainer"
        :account="account"
        :isSpecialOperator="isSpecialOperator"
        :operatorObj="currentOperatorObj"
      />
      <div
        class="criteria-fields-container self-center w-full"
        v-else-if="valueType !== 'value' && valueType !== 'custom'"
      >
        <Select
          :options="typeBasedValues[valueType]"
          class="width-full"
          v-model="conditionObj.value"
          :filterable="!isEmpty(allowCreate) ? allowCreate : true"
          clearable
          :key="`index ${conditionObj.fieldName} field`"
          :ref="`index-${conditionObj.fieldName}-field`"
          :allowCreate="allowCreate"
        />
      </div>
      <div class="criteria-operator-container self-center w-full">
        <el-input
          class="fc-input-full-border2"
          :class="disabledOpacityChange"
          placeholder="Enter a Score"
          v-model="conditionObj.scoring"
          type="number"
          :disabled="checkOperatorDisabled"
        ></el-input>
      </div>
    </div>
  </div>
</template>
<script>
import { Condition } from '@facilio/criteria'
import cloneDeep from 'lodash/cloneDeep'
import { isEmpty, isArray } from '@facilio/utils/validation'

const DEFAULT_CONDITION = {
  fieldName: null,
  operatorId: null,
  value: null,
  scoring: null,
}

const CONDITION_PROP_HASH = [
  'fieldName',
  'operatorId',
  'value',
  'prevOperator',
  'fieldNameStr',
  'scoring',
]

export default {
  name: 'RcaCondition',
  extends: Condition,
  data() {
    return {
      conditionObj: {
        fieldName: null,
        operatorId: null,
        value: null,
        scoring: null,
      },
      cascaderProps: {
        value: 'name',
        label: 'displayName',
        expandTrigger: 'hover',
      },
    }
  },
  computed: {
    cascaderErrorMsg() {
      let { selectedFieldExists } = this
      return !selectedFieldExists
        ? this.$t('rule.create.condition_field_existence')
        : this.$t('condition_operator_existence')
    },
    showComputed() {
      let { showValueType, valueType } = this
      return (showValueType && valueType === 'value') || valueType === 'value'
    },
    disabledOpacityChange() {
      let { checkOperatorDisabled } = this
      return checkOperatorDisabled ? 'opacity-50' : ''
    },
  },
  watch: {
    conditionObj: {
      handler(newVal) {
        let { index } = this
        let { value, fieldName: currentFieldName } = newVal
        if (isEmpty(currentFieldName)) {
          this.conditionObj.scoring = null
        }
        let clonedValue = cloneDeep(newVal)
        if (!isEmpty(value) && isArray(value)) {
          clonedValue.value = `${value}`
        }
        let { fieldName, operatorId, scoring } = clonedValue
        let serializedCondition = {
          fieldName: fieldName.split('#')[0],
          operatorId,
          value: clonedValue.value,
          scoring,
        }
        this.$emit('updateCondition', { condition: serializedCondition, index })
      },
      deep: true,
    },
  },

  methods: {
    initCondition() {
      let { condition, showValueType } = this
      let clonedCondition = cloneDeep(condition)
      let deserializedCondition = {}
      Object.keys(clonedCondition).map(key => {
        if (CONDITION_PROP_HASH.includes(key)) {
          deserializedCondition[key] = clonedCondition[key]
        }
      })

      if (!isEmpty(deserializedCondition)) {
        let { fieldName, value } = deserializedCondition || {}
        let actualFieldName = this.getFieldName(fieldName)
        deserializedCondition.fieldName = actualFieldName
        if (!isEmpty(actualFieldName) && actualFieldName.match(/#parent$/)) {
          actualFieldName = actualFieldName.split('#')[0]
        }
        let selectedField = this.getSelectedField(actualFieldName)
        this.setField(selectedField)
        if (showValueType) this.setValueType(value)

        deserializedCondition.value = this.getConditionValue(
          selectedField,
          value
        )
        this.conditionObj = deserializedCondition
      }
      if (showValueType) this.initCustomValues()
    },
    resetCondition() {
      let condition = DEFAULT_CONDITION
      this.conditionObj = cloneDeep(condition)
    },
  },
}
</script>
