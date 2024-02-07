<template>
  <div>
    <el-row :gutter="20" class="mT20">
      <el-col :span="12">
        <div class="fc-input-label-txt">Function</div>
        <div class="add">
          <el-select
            v-model="values.function"
            style="width:100%"
            class="form-item fc-input-full-border-select2"
            placeholder="Choose Function"
          >
            <el-option
              v-for="(func, value, index) in functions"
              :key="index"
              :label="func.label"
              :value="value"
            ></el-option>
          </el-select>
        </div>
      </el-col>
      <el-col :span="12" v-if="values.function === 'allMatch'">
        <duration
          v-model="values.dateRange"
          label="Date Range"
          labelClass="new-label-text"
          format="h"
          size="l"
          :minHour="true"
        >
          <el-col :span="3" class="pT5" slot="prefix"
            ><span class="textcolor" style="position: relative;top: 5px;"
              >Last</span
            ></el-col
          >
        </duration>
      </el-col>
    </el-row>
  </div>
</template>
<script>
import Duration from '@/FDuration'
export default {
  props: ['metric', 'resource', 'value'],
  data() {
    return {
      functions: {
        allMatch: {
          label: 'All Match',
          // namespace: 'default'
        },
      },
      values: {
        function: null,
        dateRange: 1,
      },
    }
  },
  components: { Duration },
  methods: {
    init() {
      if (this.value.expressions) {
        let expr = this.value.expressions.find(
          expr => expr.defaultFunctionContext
        )
        if (!expr) {
          return
        }
        this.values.function = expr.defaultFunctionContext.functionName
        if (this.values.function === 'allMatch') {
          this.values.dateRange = parseInt(
            this.value.expressions[0].criteria.conditions['2'].value.split(
              ','
            )[0]
          )
        }
      }
    },
    setFunctionExpression() {
      let obj = {
        expressions: [],
        parameters: [],
      }
      let selectedFunc = this.functions[this.values.function]
      let exprNameValue = 96

      if (this.metric) {
        if (this.values.function === 'allMatch') {
          obj.expressions.push({
            name: String.fromCharCode(++exprNameValue),
            fieldName: this.metric.name,
            moduleName: this.metric.module.name,
            criteria: {
              pattern: '(1 and 2)',
              conditions: {
                1: {
                  fieldName: 'parentId',
                  operatorId: 9,
                  sequence: '1',
                  value: '${resourceId' + '}',
                },
                2: {
                  fieldName: 'ttime',
                  operatorId: 42,
                  sequence: '2',
                  value: this.values.dateRange + ',${ttime}', // eslint-disable-line no-template-curly-in-string
                },
              },
            },
          })
          obj.parameters.push({ name: 'resourceId', typeString: 'Number' })
          obj.parameters.push({ name: 'ttime', typeString: 'Number' })
        }

        obj.expressions.push({
          name: String.fromCharCode(++exprNameValue),
          defaultFunctionContext: {
            nameSpace: selectedFunc.nameSpace || 'default',
            functionName: this.values.function,
            params: obj.expressions.map(expr => expr.name).join(),
          },
        })
        obj.resultEvaluator = String.fromCharCode(exprNameValue)
      }
      this.$emit('input', obj)
    },
  },
  mounted() {
    let unwatch = this.$watch(
      'value',
      function(value) {
        if (value) {
          this.init()
          unwatch()
        }
      },
      { immediate: true }
    )
  },
  watch: {
    values: {
      handler() {
        this.setFunctionExpression()
      },
      deep: true,
    },
    metric() {
      this.setFunctionExpression()
    },
  },
}
</script>
