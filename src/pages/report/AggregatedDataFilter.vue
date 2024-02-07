<template>
  <div>
    <div class="criteria-container">
      <div
        class="criteria-condition-block"
        v-for="(filter, idx) in dataFilter"
        :key="idx"
      >
        <div class="criteria-alphabet-block pR5">
          <div class="alphabet-circle">
            {{ idx + 1 }}
          </div>
        </div>
        <div class="creteria-dropdown pR5">
          <el-select
            v-model="filter.field"
            filterable
            placeholder="Select"
            class="fc-border-select"
          >
            <el-option
              v-for="(field, idx1) in metrics"
              :key="idx1"
              :label="field.displayName"
              :value="field.name + '_' + field.fieldId"
            ></el-option>
          </el-select>
        </div>
        <div class="creteria-dropdown pR5 width20">
          <el-select
            :disabled="!filter.field || typeof filter.field === 'undefined'"
            v-model="filter.aggregateOperator"
            placeholder="Select aggregation"
            class="select-operator-input fc-border-select"
          >
            <el-option
              v-for="(operator, idx2) in aggregateOperators"
              :key="idx2"
              :label="operator.label"
              :value="operator.value"
            >
            </el-option>
          </el-select>
        </div>
        <div class="creteria-dropdown pR5 width20">
          <el-select
            :disabled="
              !filter.aggregateOperator ||
                typeof filter.aggregateOperator === 'undefined'
            "
            v-model="filter.operator"
            placeholder="Select operator"
            class="select-operator-input fc-border-select"
          >
            <el-option
              v-for="(operator, idx3) in NumberOperators"
              :key="idx3"
              :label="operator.operator"
              :value="operator.operatorId"
            >
            </el-option>
          </el-select>
        </div>

        <div class="creteria-input">
          <div>
            <el-input
              :disabled="
                !filter.operator || typeof filter.operator === 'undefined'
              "
              type="number"
              v-model="filter.value"
              class="fc-border-select"
            ></el-input>
          </div>
        </div>

        <div class="creteria-delete-icon flex-middle">
          <img
            src="~assets/add-icon.svg"
            v-if="Object.keys(dataFilter).length - 1 === idx"
            style="height:18px;width:18px;"
            class="delete-icon"
            @click="addFilter()"
          />
          <img
            src="~assets/remove-icon.svg"
            v-if="!(Object.keys(dataFilter).length === 0)"
            style="height:18px;width:18px;margin-right: 3px;"
            class="delete-icon"
            @click="removeFilter(idx)"
          />
        </div>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  data() {
    return {
      NumberOperators: {
        '<=': {
          dynamicOperator: false,
          operator: '<=',
          operatorId: 12,
          valueNeeded: true,
          _name: 'LESS_THAN_EQUAL',
        },
        // 'is not empty': {
        //   dynamicOperator: false,
        //   operator: 'is not empty',
        //   operatorId: 2,
        //   valueNeeded: false,
        //   _name: 'IS_NOT_EMPTY',
        // },
        // 'is empty': {
        //   dynamicOperator: false,
        //   operator: 'is empty',
        //   operatorId: 1,
        //   valueNeeded: false,
        //   _name: 'IS_EMPTY',
        // },
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
      aggregateOperators: [
        {
          label: 'Avg',
          value: 2,
        },
        {
          label: 'Sum',
          value: 3,
        },
        {
          label: 'Min',
          value: 4,
        },
        {
          label: 'Max',
          value: 5,
        },
      ],
      dataFilter: [],
    }
  },
  props: ['having', 'metrics'],
  mounted() {
    this.addFilter()
    this.initData()
  },
  methods: {
    addFilter() {
      this.dataFilter.push({
        field: undefined,
        aggregateOperator: undefined,
        operatorId: undefined,
        value: undefined,
      })
    },
    initData() {
      if (this.having && this.having.length > 0) {
        let filterArray = []
        for (let filter of this.having) {
          let temp = {}
          temp.field = filter.fieldName + '_' + filter.fieldId
          temp.aggregateOperator = filter.aggregateOperator
          temp.operator = filter.operator
          temp.value = filter.value
          filterArray.push(temp)
        }
        this.dataFilter = filterArray
      }
    },
    removeFilter(key) {
      this.$delete(this.dataFilter, key)
      if (this.dataFilter.length === 0) {
        this.addFilter()
      }
    },
  },
  watch: {
    dataFilter: {
      handler: function(after) {
        if (after && after.length > 0) {
          let filterArray = []
          for (let filter of after) {
            let temp = {}
            if (filter.field && typeof filter.field !== 'undefined') {
              let fieldValue = filter.field.split('_')
              temp.fieldName = fieldValue[0]
              temp.fieldId = parseInt(fieldValue[1])
              temp.aggregateOperator = filter.aggregateOperator
              temp.operator = filter.operator
              temp.value = filter.value
              if (
                temp.fieldName &&
                temp.fieldId &&
                temp.aggregateOperator &&
                temp.operator &&
                temp.value &&
                typeof temp.value !== 'undefined'
              ) {
                filterArray.push(temp)
              }
            }
          }
          this.$emit('setDataFilter', filterArray)
        } else {
          this.$emit('setDataFilter', [])
        }
      },
      deep: true,
    },
  },
}
</script>
<style></style>
