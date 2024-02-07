<template>
  <div class="pivot-add-row">
    <el-dialog
      :visible="visibility"
      class="pivot-add-row-dialog"
      :show-close="false"
      :append-to-body="true"
      :title="$t('pivot.addFormula')"
      width="50%"
    >
      <div class="body">
        <el-row class="mT20">
          <el-row>
            {{ $t('pivot.formulaName') }}
          </el-row>
          <el-row>
            <el-input
              v-model="columnName"
              placeholder="Formula Name"
              class="fc-input-full-border2 mT10"
            >
            </el-input>
          </el-row>
        </el-row>
        <el-row class="mT20">
          <el-row>
            <el-col :span="18">
              {{ $t('pivot.expression') }}
            </el-col>
            <el-col :span="6">
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
          <div class="mB10">
            {{ $t('pivot.columns') }}
          </div>
          <table border="0" cellspacing="0" cellpadding="0">
            <tr
              v-for="(point, index) in pivotFieldMap"
              :key="index"
              class="fbVariableLegend"
            >
              <td class="fbVariableLegend mL10">{{ point.paramAlias }}</td>
              <td class="fbVariableLegefbVariableLegendContainerndInfo">
                <span>{{ point.label }}</span>
              </td>
            </tr>
          </table>
        </div>
      </div>
      <div class="dialog-save-cancel">
        <el-button class="modal-btn-cancel" @click="closeDialog">
          {{ $t('pivot.cancel') }}</el-button
        >
        <el-button type="primary" class="modal-btn-save mL0" @click="save">{{
          $t('pivot.done')
        }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { defaultColFormat } from './PivotDefaults'

export default {
  props: [
    'visibility',
    'editConfig',
    'pivotFieldMap',
    'index',
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
      this.columnName = this.formatConfig.label
      this.expression = this.editConfig.expression
      this.dataTypeEnum = this.editConfig.dataTypeEnum
    }
  },

  methods: {
    closeDialog() {
      if (this.editConfig) {
        this.$emit('updateCancel')
      }
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
          params: { data: config, formatting: formatting },
          index: this.index,
        })
      } else {
        this.$emit('save', { data: config, formatting: formatting })
      }
    },
  },
}
</script>

<style>
.fbVariableLegendContainer {
  padding: 0px !important;
}
.fbVariableLegendContainer td {
  border: 1px solid #cae8ec;
  padding-left: 10px;
}
</style>
