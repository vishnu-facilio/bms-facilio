<template>
  <div v-if="isLoading" class="flex">
    <FieldLoader :isLoading="isLoading" class="mr-2" />
    <FieldLoader :isLoading="isLoading" class="mr-2" />
    <FieldLoader :isLoading="isLoading" class="mr-2" />
    <FieldLoader :isLoading="isLoading" class="mr-2" />
    <FieldLoader :isLoading="showValueType && isLoading" />
  </div>
  <!-- eslint-disable-next-line vue/no-multiple-template-root -->
  <div v-else class="flex flex-col self-center criteria-builder m-2">
    <div v-for="({ condition, id }, index) in dataForConditions" :key="id">
      <div class="condition">
        <RcaCondition
          :key="`condition-${id}`"
          :ref="`condition-${id}`"
          :condition="condition"
          :operators="operators"
          :index="Number(index + 1)"
          :fields="filteredFields"
          @updateCondition="updateCondition"
          :account="account"
          :filterOperators="filterOperators"
          :showValueType="showValueType"
          :customValues="customValues"
          :disabled="disabled"
          :advancedFields="advancedFields"
        ></RcaCondition>
        <div class="add-remove-container">
          <span
            @click="addCriteria"
            v-if="conditionsLength == index + 1"
            class="flex items-center"
          >
            <AddIcon
              class="delete-icon ml-3 cursor-pointer"
              :class="disabled ? 'cursor-not-allowed	' : ''"
          /></span>
          <span @click="() => deleteCriteria(index)" class="flex items-center">
            <RemoveIcon
              v-if="showRemoveIcon"
              class="delete-icon ml-2 cursor-pointer"
              :class="disabled ? 'cursor-not-allowed	' : ''"
            />
          </span>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import RcaCriteriaBuilder from './RcaCriteriaBuilder'
import RcaCondition from './RcaCondition'
import cloneDeep from 'lodash/cloneDeep'
import { v4 as uuid } from 'uuid'
import { isEmpty } from '@facilio/utils/validation'
const defaultCriteria = {
  fieldName: null,
  operatorId: null,
  value: null,
  scoring: null,
}
export default {
  name: 'ConditionBuilder',
  extends: RcaCriteriaBuilder,
  components: { RcaCondition },
  computed: {
    dataForConditions() {
      let { ifCriteriaExists, serializedConditions, dummyCriteria } = this
      return ifCriteriaExists ? serializedConditions : dummyCriteria
    },
    conditionsLength() {
      let { conditionsKeys } = this || {}
      let { length = 0 } = conditionsKeys || {}
      return length
    },
  },
  methods: {
    serializeConditions() {
      let { criteria } = this
      let { conditions, pattern } = criteria
      let arr = []
      for (let key in conditions) {
        let conditionObj = conditions[key]
        let newCondition
        newCondition = {
          condition: conditionObj,
          id: uuid(),
        }
        let { fieldName } = conditionObj
        if (fieldName !== null) conditionObj.fieldName = `${fieldName}#parent`
        arr.push(newCondition)
      }
      this.serializedConditions = arr
      this.serializedPattern = pattern
    },
    constructCriteria() {
      let { value } = this
      if (isEmpty(value)) {
        // if the criteria is empty, then we're configurig a dummy criteria
        let defaultCriteriaObj = cloneDeep(defaultCriteria)
        delete defaultCriteriaObj.prevOperator
        this.dummyCriteria = [
          {
            condition: {
              conditions: { 1: defaultCriteria },
              pattern: '1',
            },
            id: 1,
          },
        ]
      } else {
        this.constructOperatorFromPattern()
      }
    },
    addCriteria() {
      let { criteria, isPatternReadonly, disabled } = this
      if (disabled) {
        this.$message.error(this.$t('rule.create.criteria_disabled'))
      } else if (!isPatternReadonly) {
        this.$message.warning(this.$t('rule.create.criteria_warning'))
      } else {
        let { pattern } = criteria
        let newCondition = cloneDeep(defaultCriteria)
        let condition = {
          condition: newCondition,
          id: uuid(),
        }
        this.serializedConditions.push(condition)
        this.serializedPattern = `( ${pattern} and ${this.serializedConditions.length} )`
        this.setCriteria()
      }
    },
    setCriteria() {
      let { serializedConditions, criteria, serializedPattern } = this
      let conditions = {}
      for (let i in serializedConditions) {
        let condition = { ...serializedConditions[i].condition }

        let index = parseInt(i) + 1
        conditions[index] = condition
      }
      let modifiedCriteria = {
        ...criteria,
        conditions,
        pattern: serializedPattern,
      }
      this.$set(this, 'criteria', modifiedCriteria)
    },
    updateCondition(props) {
      let { condition, index } = props || {}
      let key = parseInt(index) - 1
      let { conditionsLength, dummyCriteria } = this
      let isNullCondition = this.checkNullCondition(condition)
      if (isNullCondition && conditionsLength === 1) {
        this.criteria = null
        this.serializedConditions = []
        this.serializedPattern = ''
        let defaultCriteriaObj = cloneDeep(defaultCriteria)
        delete defaultCriteriaObj.prevOperator
        this.dummyCriteria = [
          {
            condition: {
              conditions: { 1: defaultCriteria },
              id: 1,
            },
          },
        ]

        return
      }
      if (!isEmpty(dummyCriteria)) {
        let { fieldName } = condition
        if (!isEmpty(fieldName)) {
          this.dummyCriteria = null
          this.criteria = {
            conditions: { 1: condition },
            pattern: '( 1 )',
          }
          let conditionObj = {
            condition: {
              ...condition,
            },
            id: uuid(),
          }
          this.serializedConditions = [
            ...this.serializedConditions,
            conditionObj,
          ]
          this.serializedPattern = '( 1 )'
        }
        return
      }
      this.serializedConditions[key].condition = condition
      this.setCriteria()
    },
    checkNullCondition(condition) {
      let { fieldName, operatorId, value, scoring } = condition
      let isEmptyCondition =
        isEmpty(fieldName) &&
        isEmpty(operatorId) &&
        isEmpty(value) &&
        isEmpty(scoring)
      return isEmptyCondition
    },
    deleteCriteria(index) {
      let { isPatternReadonly, disabled } = this
      if (disabled) {
        this.$message.error(this.$t('rule.create.criteria_disabled'))
      } else if (!isPatternReadonly) {
        this.$message.warning(this.$t('rule.create.criteria_warning'))
      } else {
        this.deleteKeyFromPattern(index)
        this.serializedConditions = this.serializedConditions.filter(
          (item, itemIndex) => itemIndex != index
        )
        this.setCriteria()
        setTimeout(() => {
          let { serializedConditions } = this
          serializedConditions.forEach(val => {
            let ref = this.$refs[`condition-${val.id}`]
            ref[0].initCondition()
          })
        })
      }
    },
  },
}
</script>
