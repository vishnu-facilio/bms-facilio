<template>
  <div>
    <base-dialog-box
      :visibility.sync="visibility"
      :onConfirm="save"
      :onCancel="closeDialog"
      :disableConfirm="!columnName || !expression || !dataTypeEnum"
      cancelText="Cancel"
      confirmText="Save"
      :title="$t('pivot.valueBased')"
      width="650px"
    >
      <div class="dialog-content-body" slot="body" style="margin-bottom:20px">
        <el-row class="mT20">
          <el-row class="pivot-formula-dialog-text">
            {{ $t('pivot.measureName') }}
          </el-row>
          <el-row>
            <el-input
              v-model="columnName"
              placeholder="Measure Name"
              class="fc-input-full-border2 mT10"
            >
            </el-input>
          </el-row>
        </el-row>
        <el-row class="mT20">
          <el-row>
            <el-col :span="18" class="pivot-formula-dialog-text">
              {{ $t('pivot.expression') }}
            </el-col>
            <el-col :span="6" class="pivot-formula-dialog-text">
              {{ $t('pivot.dataType') }}
            </el-col>
          </el-row>
          <el-row class="mB10">
            <el-col :span="18">
              <el-input
                v-model="expression"
                placeholder="A + B"
                class="fc-input-full-border2 mT10  width90"
              >
              </el-input>
            </el-col>
            <el-col :span="6">
              <el-select
                v-model="dataTypeEnum"
                filterable
                placeholder="Select"
                class="fc-input-full-border-select2 mT10 module-date-field-select"
              >
                <el-option
                  v-for="(data, index) in dataTypeOptions"
                  :key="'module-date-field-option' + index"
                  :label="data.displayName"
                  :value="data.value"
                ></el-option>
              </el-select>
            </el-col>
          </el-row>
        </el-row>
        <div class="fbVariableLegendContainer mT10 formulalengendscontainer">
          <div class="mB10 columns-title">
            {{ $t('pivot.columns') }}
          </div>
          <div
            v-for="(point, index) in pivotFieldMap"
            :key="index"
            class="fbVariableLegend"
          >
            <div class="variableAliasContainer">
              {{ point.paramAlias }}
            </div>
            <div class="variableLabelContainer">{{ point.label }}</div>
          </div>
        </div>
      </div>
    </base-dialog-box>
  </div>
</template>

<script>
import { defaultColFormat } from '../PivotDefaults'
import BaseDialogBox from './BaseDialogBox.vue'

export default {
  props: [
    'visibility',
    'editConfig',
    'pivotFieldMap',
    'index',
    'label',
    'moduleName',
    'formatConfig',
  ],
  computed: {
    paramsMap() {
      let params = {}
      this.pivotFieldMap.forEach(pivotField => {
        params[pivotField.paramAlias] = pivotField.alias
      })
      return params
    },
  },
  components: { BaseDialogBox },
  data() {
    return {
      meta: {},
      columnName: null,
      expression: null,
      dataTypeEnum: null,
      variablesUsedInExp: [],
      variableMap: {},
      dataTypeOptions: [
        {
          displayName: 'Date',
          value: 'DATE_TIME',
        },
        {
          displayName: 'String',
          value: 'STRING',
        },
        {
          displayName: 'Number',
          value: 'NUMBER',
        },
        {
          displayName: 'Decimal',
          value: 'DECIMAL',
        },
        {
          displayName: 'Boolean',
          value: 'BOOLEAN',
        },
      ],
    }
  },

  created() {
    if (this.editConfig) {
      this.columnName = this.label
      this.expression = this.editConfig.expression
      this.dataTypeEnum = this.editConfig.dataTypeEnum
    }
  },

  methods: {
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    initExpressionParams() {
      let params = Object.keys(this.paramsMap)
      let regExString = /\(|\)|\+|\-|\\|\*|\/|\ |\%|\~|\&|\^|\>|\<|\=|\!/
      let expressionArray = this.expression.split(regExString)

      expressionArray.forEach(variable => {
        variable = variable.replace(' ', '')
        let index = params.findIndex(p => p == variable)
        if (index != -1) {
          let alias = this.paramsMap[variable]
          this.variablesUsedInExp.push(alias)
          this.variableMap[alias] = variable
        }
      })

      return this.expression
    },
    save() {
      if (!(this.columnName && this.expression) && this.dataType == null) {
        return
      }
      this.initExpressionParams()

      let config = {
        columnName: this.columnName,
        dataTypeEnum: this.dataTypeEnum,
        moduleName: this.moduleName,
        expression: this.expression,
        variableMap: this.variableMap,
        field: null,
        criteria: null,
      }

      let formatting = {
        ...defaultColFormat.formulaColumn,
        label: this.columnName,
      }

      if (this.editConfig) {
        config.alias = this.editConfig.alias
        this.$emit('update', {
          params: { formula: config, formatting: formatting },
          index: this.index,
        })
      } else {
        this.$emit('save', { formula: config, formatting: formatting })
      }
    },
  },
}
</script>

<style scoped>
.fbVariableLegendContainer td {
  border: none;
}
.variableAliasContainer {
  background: #f6fcfc;
  height: 40px;
  display: flex;
  width: 40px;
  font-size: 14px;
  border: 1px solid #d5eaed;
  border-radius: 3px;
  align-items: center;
  justify-content: space-around;
}
.fbVariableLegend {
  display: flex;
  background: #fff;
  padding-top: 10px;
  padding-left: 0px;
}
.variableLabelContainer {
  border: 1px solid #d5eaed;
  border-radius: 3px;
  width: 100%;
  padding-left: 10px;
  display: flex;
  margin-left: 10px;
  color: #a6abb2;
  font-size: 12px;
  align-content: flex-end;
  align-items: center;
  justify-content: flex-start;
}
.columns-title,
.pivot-formula-dialog-text {
  font-size: 14px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: 0.5px;
  color: #324056;
}
</style>
