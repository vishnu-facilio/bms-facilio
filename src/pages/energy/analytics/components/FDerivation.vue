<template>
  <div>
    <div>
      <f-dialog
        v-if="visibility"
        :visible="visibility"
        :width="modelWidth"
        @save="save"
        @close="close"
        maxHeight="500px"
        class="derivation newderivation"
      >
        <div
          slot="content"
          class="left-container"
          v-bind:class="{ active: showRightView }"
        >
          <el-col class="left-conatiner-content" :span="24">
            <div>
              <div class="new-header-container">
                <div class="fc-setup-modal-title fL">
                  {{ isEdit ? 'Edit Derivation' : 'Add Derivation' }}
                </div>
                <div class="summation fR">
                  <el-button
                    type="button"
                    @click="showFunctions()"
                    class="formula-btn"
                  >
                    <img
                      src="~assets/summation_img.svg"
                      style="width: 10px; height: 10px; opacity: 0.9;"
                    />
                  </el-button>
                </div>
              </div>
              <div class="fc-input-label-txt mB10 clearboth">
                Derivation Name
              </div>
              <el-input
                v-model="newDerivation.name"
                :placeholder="$t('common._common.enter_name')"
                class="el-input-textbox-full-border"
              ></el-input>
              <el-row class="mT20">
                <el-col :span="18">
                  <div class="fc-input-label-txt mB10">Expression</div>
                  <el-input
                    v-model="newDerivation.expResult"
                    placeholder="Enter Expression"
                    class="el-input-textbox-full-border"
                  ></el-input>
                </el-col>
                <el-col :span="5" class="mL20">
                  <div class="fc-input-label-txt mB10">Unit</div>
                  <el-input
                    v-model="newDerivation.unitStr"
                    placeholder="Enter Unit"
                    class="el-input-textbox-full-border"
                  ></el-input>
                </el-col>
              </el-row>
              <div
                class="fbVariableLegendContainer mT20 formulalengendscontainer"
              >
                <table border="0" cellspacing="0" cellpadding="0">
                  <tr
                    v-for="(point, index) in dataPoints"
                    v-if="
                      !isEdit || !higherDerivationAliases.includes(point.alias)
                    "
                    :key="index"
                    class="fbVariableLegend"
                  >
                    <td class="fbVariableLegend">{{ point.alias }}</td>
                    <td class="fbVariableLegendInfo">
                      <span>{{ point.label }}</span>
                    </td>
                  </tr>
                </table>
              </div>
            </div>
            <!-- </el-col> -->
          </el-col>
        </div>

        <div slot="content" class="right-container" v-if="showRightView">
          <div class="build-functions-scroll">
            <div class="fc-setup-modal-title mB20">BUILT IN FUNCTIONS</div>
            <div
              class="list-data"
              v-for="fields in builtInFunctions"
              :key="fields.value"
            >
              <div class="fields">
                <img
                  class="selected-icon"
                  src="~assets/apply-icon.svg"
                  style="width: 15px; height: 15px;position: relative;top: 17px;left: 4px;"
                  @click="addFunction(fields.value)"
                />
                <div class="field-info">{{ fields.label }}</div>
                <div class="bottom">
                  <el-tooltip
                    effect="dark"
                    placement="bottom"
                    class="help-tooltip"
                    popper-class="element-tooltip"
                  >
                    <div slot="content">{{ fields.description }}</div>
                    <img
                      src="~assets/Help-icon-img.svg"
                      class="derivation-info-icon"
                    />
                  </el-tooltip>
                </div>
              </div>
            </div>
          </div>
        </div>
      </f-dialog>
    </div>
    <div></div>
  </div>
</template>
<script>
import FDialog from '@/FDialogNew'

const expRegex = /(NOT|IF|MIN|MAX|ROUND|ABS|FLOOR|CEILING|LOG|LOG10|SQRT|FACT|PI|TRUE|FALSE|true|false|NULL|null)|([A-Za-z][0-9]*\.(sum|avg|lastValue|min|max))|([A-Za-z][0-9]*)|([^A-Za-z][0-9]*)+/g
const alphaRegex = /[a-z]+|\d+/gi

export default {
  props: ['report', 'config', 'visibility', 'selectedPoint'],
  components: { FDialog },
  data() {
    return {
      modelWidth: '38%',
      showRightView: false,
      newDerivation: {
        name: '',
        expResult: '',
        unitStr: '',
        analyticsType: this.report.analyticsType,
      },
      expressionIndex: 0,
      builtInFunctions: [
        {
          value: 'NOT()',
          label: 'NOT(expression)',
          description:
            'Boolean negation, 1 (means true) if the expression is not zero',
        },
        {
          value: 'IF()',
          label: 'IF(condition,value_if_true,value_if_false)',
          description:
            'Returns one value if the condition evaluates to true or the other if it evaluates to false',
        },
        {
          value: 'MIN()',
          label: 'MIN(e1,e2, ...)',
          description: 'Returns the smallest of the given expressions',
        },
        {
          value: 'MAX()',
          label: 'MAX(e1,e2, ...)',
          description: 'Returns the biggest of the given expressions',
        },
        {
          value: 'ROUND()',
          label: 'ROUND(expression,precision)',
          description:
            'Rounds a value to a certain number of digits, uses the current rounding mode',
        },
        {
          value: 'ABS()',
          label: 'ABS(expression) ',
          description:
            'Returns the absolute (non-negative) value of the expression',
        },
        {
          value: 'FLOOR()',
          label: 'FLOOR(expression)',
          description: 'Rounds the value down to the nearest integer',
        },
        {
          value: 'CEILING()',
          label: 'CEILING(expression)',
          description: 'Rounds the value up to the nearest integer',
        },
        {
          value: 'LOG()',
          label: 'LOG(expression)',
          description:
            'Returns the natural logarithm (base e) of an expression',
        },
        {
          value: 'LOG10()',
          label: 'LOG10(expression)',
          description:
            'Returns the common logarithm (base 10) of an expression',
        },
        {
          value: 'SQRT()',
          label: 'SQRT(expression)',
          description: 'Returns the square root of an expression',
        },
        {
          value: 'FACT()',
          label: 'FACT(expression)',
          description:
            'Retuns the factorial value of an integer. Will return 1 for 0 or a negative number',
        },
      ],
    }
  },
  computed: {
    currentAlias() {
      let dpAlias = {
        lastAlias: 0,
        lastNum: 0,
      }
      let currentAlias
      if (!this.isEdit) {
        let aliases = []
        let dataPoints = this.$helpers.getDataPoints(
          this.report.options.dataPoints,
          [1, 2, 4],
          true
        )
        for (let dataPoint of dataPoints) {
          if (dataPoint.type === 'datapoint') {
            aliases.push(dataPoint)
          } else {
            for (let child of dataPoint.children) {
              aliases.push(child)
            }
          }
        }
        aliases.forEach(dp => {
          if (dp.alias.length > 1) {
            let num = dp.alias.match(alphaRegex)[1]
            dpAlias.lastNum = num > dpAlias.lastNum ? num : dpAlias.lastNum
          } else {
            dpAlias.lastAlias =
              dp.alias.charCodeAt(0) > dpAlias.lastAlias
                ? dp.alias.charCodeAt(0)
                : dpAlias.lastAlias
          }
        })
        currentAlias = this.$helpers.getNextAlias(dpAlias)
      } else {
        currentAlias = this.selectedPoint.alias
      }
      return currentAlias
    },
    configSelectedPoint() {
      if (this.isEdit) {
        return this.$helpers
          .getDataPoints(this.config.dataPoints, [1, 2, 4], true)
          .find(dp => dp.aliases.actual === this.selectedPoint.key)
      }
      return null
    },
    isEdit() {
      return this.selectedPoint !== undefined
    },
    higherDerivationAliases() {
      let aliases = []
      if (this.isEdit) {
        aliases = this.$helpers
          .getDataPoints(this.report.options.dataPoints, [1, 2, 4], true)
          .filter(dp => dp.pointType === 2 && this.isHigherAlias(dp.alias))
          .map(dp => dp.alias)
      }
      return aliases
    },
    dataPoints() {
      let dataPoints = []
      this.$helpers
        .getDataPoints(this.report.options.dataPoints, [1, 2, 4], true)
        .forEach(dp => {
          if (dp.type === 'group' || dp.type === 'rangeGroup') {
            dataPoints.push(...dp.children)
          } else {
            dataPoints.push(dp)
          }
        })
      return dataPoints.sort()
    },
  },
  mounted() {
    if (this.isEdit) {
      this.newDerivation.name = this.configSelectedPoint.name
      this.newDerivation.unitStr = this.configSelectedPoint.yAxis.unitStr

      let alias = this.currentAlias
      this.newDerivation.expResult = this.report.options.derivations[
        alias
      ].result
      this.expressionIndex =
        this.config.transformWorkflow.expressions[0].expressions.findIndex(
          exp =>
            exp.name === 'put' &&
            exp.defaultFunctionContext &&
            exp.defaultFunctionContext.params === this.getResultFuncParam(alias)
        ) - 1
    } else {
      this.expressionIndex = !this.config.transformWorkflow
        ? 0
        : this.config.transformWorkflow.expressions[0].expressions.length
    }
  },
  methods: {
    close() {
      this.$emit('update:visibility', false)
    },
    showFunctions() {
      this.showRightView = !this.showRightView
      if (this.showRightView) {
        this.modelWidth = '60%'
      } else {
        this.modelWidth = '38%'
      }
    },
    addFunction(fieldsList) {
      this.newDerivation.expResult += ' ' + fieldsList
    },
    save() {
      let aliasesUsed = [
        ...new Set(this.newDerivation.expResult.match(/[A-Z]+/g)),
      ]
      if (!this.validate(aliasesUsed)) {
        return
      }
      let alias = this.currentAlias

      if (!this.config.transformWorkflow) {
        this.config.transformWorkflow = {
          parameters: [
            { name: 'data', typeString: 'list' },
            { name: 'aggr', typeString: 'map' },
          ],
          expressions: [
            {
              workflowExpressionType: 2,
              iteratableVariable: 'data',
              loopVariableIndexName: 'index',
              loopVariableValueName: 'value',
              expressions: [],
            },
          ],
        }
      }
      this.setWorkflowExpression(
        alias,
        this.config.transformWorkflow.expressions[0].expressions
      )
      this.setDerivationDetails(alias, aliasesUsed)

      let point = {
        name: this.newDerivation.name,
        type: 2,
        aliases: { actual: alias },
        xAxis: {
          label: 'Timestamp',
        },
        yAxis: {
          dataType: 3,
          label: this.newDerivation.name,
          unitStr: this.newDerivation.unitStr,
        },
      }

      if (!this.isEdit) {
        this.config.dataPoints.push(point)
      } else {
        if (this?.report?.options?.dataPoints?.length) {
          let option_idx = this.report.options.dataPoints.findIndex(
            dp => dp.name === this.configSelectedPoint.name
          )
          this.$set(
            this.report.options.dataPoints[option_idx],
            'label',
            this.newDerivation.name
          )
          this.$set(
            this.report.options.dataPoints[option_idx],
            'name',
            this.newDerivation.name
          )
        }
        let idx = this.config.dataPoints.findIndex(
          dp => dp.name === this.configSelectedPoint.name
        )
        this.$set(this.config.dataPoints, idx, point)
      }
      this.close()
    },

    setWorkflowExpression(alias, itrExp) {
      let match = expRegex.exec(this.newDerivation.expResult)
      let result = ''
      let expressions = []
      while (match != null) {
        if (match[2]) {
          let name = `aggr${match[2].replace('.', '')}`
          if (![...itrExp, ...expressions].some(exp => exp.name === name)) {
            expressions.push({
              name: name,
              defaultFunctionContext: {
                nameSpace: 'map',
                functionName: 'get',
                params: "aggr, '" + match[2] + "'",
              },
            })
            match[0] = name
          }
        } else if (match[4]) {
          result += 'param'
          if (
            ![...itrExp, ...expressions].some(
              exp => exp.name === `param${match[4]}`
            )
          ) {
            let dp = this.dataPoints.find(dp => dp.alias === match[4])
            let value = "value, '" + match[4]
            if (dp.dataTypeId === 4 || dp.dataTypeId === 8) {
              value += '.value'
            }
            value += "'"
            expressions.push({
              name: `param${match[4]}`,
              defaultFunctionContext: {
                nameSpace: 'map',
                functionName: 'get',
                params: value,
              },
            })
          }
        }
        result += match[0]
        match = expRegex.exec(this.newDerivation.expResult)
      }

      expressions.push(
        { name: 'result', expr: result },
        {
          name: 'put', // Please change the expressionIndex finding also if name is changed
          defaultFunctionContext: {
            nameSpace: 'map',
            functionName: 'put',
            params: this.getResultFuncParam(alias),
          },
        }
      )

      itrExp.splice(this.expressionIndex, this.isEdit ? 2 : 0, ...expressions)
    },

    getResultFuncParam(alias) {
      return "value, '" + alias + "', result"
    },

    setDerivationDetails(alias, aliasesUsed) {
      if (!this.report.options.derivations) {
        this.report.options.derivations = {}
      }
      this.report.options.derivations[alias] = {
        result: this.newDerivation.expResult,
        // usedIn: [],
        usedAliases: aliasesUsed,
      }

      /* this.report.options.dataPoints.forEach(dp => {
        if (dp.pointType !== 2 && !this.report.options.derivations[dp.alias]) {
          this.report.options.derivations[dp.alias] = {
            usedIn: []
          }
        }
        let usedIn = this.report.options.derivations[dp.alias].usedIn
        if (aliasesUsed.includes(dp.alias) || aliasesUsed.some(au => usedIn.includes(au))) {
          if (!usedIn.includes(alias)) {
            usedIn.push(alias)
          }
        }
      }) */
    },

    validate(aliasesUsed) {
      let errorMessage
      let variables = aliasesUsed
      if (!this.newDerivation.name) {
        errorMessage = 'Please enter name'
      } else if (!this.newDerivation.expResult) {
        errorMessage = 'Please enter expression'
      } else if (
        variables.some(v => this.higherDerivationAliases.includes(v))
      ) {
        errorMessage = 'Some derivations cannot be used'
      } else {
        let brackets = this.newDerivation.expResult.match(/[\\(\\)]/g)
        if (brackets) {
          let openBrackets = brackets.filter(b => b === '(')
          let closedBrackets = brackets.filter(b => b === ')')
          if (openBrackets.length !== closedBrackets.length) {
            errorMessage = 'Please enter valid expression'
          }
        }
      }
      if (errorMessage) {
        this.$message.error(errorMessage)
        return false
      }
      return true
    },

    isHigherAlias(alias) {
      if (alias.length == 1 && this.currentAlias.length == 1) {
        return alias.charCodeAt(0) >= this.currentAlias.charCodeAt(0)
      } else if (alias.length == 1) {
        return false
      } else if (this.currentAlias.length == 1) {
        return true
      }
      return (
        alias.match(alphaRegex)[1] >= this.currentAlias.match(alphaRegex)[1]
      )
    },
  },
}
</script>
<style>
.fbVariableLegendContainer {
  padding-top: 30px;
  width: 100%;
}
.fbVariableLegendContainer table {
  width: 100%;
  border-collapse: collapse;
}
.fbVariableLegendContainer td {
  border: 1px solid #cae8ec;
}
.fbVariableLegendContainer td.fbVariableLegend {
  background: #f6fcfc;
  padding: 10px;
  width: 40px;
  text-align: center;
  color: #39b2c2;
  font-size: 18px;
  font-weight: 500;
}
.fbVariableLegendContainer td.fbVariableLegendInfo {
  padding-left: 10px;
}
.fbVariableLegendContainer tr td .fbVariableLegendDelete {
  display: none;
}
.fbVariableLegendContainer tr:hover td .fbVariableLegendDelete {
  display: block;
  padding-right: 10px;
  color: #e15e5e;
  font-size: 15px;
  cursor: pointer;
}
.derivation .el-icon-close {
  display: none;
}
.summation {
  margin-right: 40px;
  margin-top: -8px;
  position: relative;
  left: 335px;
}
.derivation .left-container.active {
  width: 64%;
}
.right-container .fields {
  display: flex;
  position: relative;
  margin: 0;
  color: #555;
  font-size: 14px;
  letter-spacing: 0.2px;
  text-transform: capitalize;
  cursor: pointer;
}
.list-data:nth-child(odd) {
  background: #fbfbfb;
}
.right-container .fields:hover {
  border: solid 1px #b0dbe1;
  box-shadow: 0 2px 6px 0 rgba(211, 211, 211, 0.5);
  background-color: #ffffff;
}
/* .list-data{
  padding-left: x;
    width: 105px;
    height: 26px;
    letter-spacing: 0.5px;
    padding-left: 5px;
} */
.right-container {
  width: 36%;
  float: right;
  padding-top: 30px;
  border-left: 1px solid #ededed;
  padding-left: 20px;
  padding-right: 20px;
  overflow: hidden;
  height: 100%;
  padding-bottom: 80px;
}
.derivation-info-icon {
  width: 12px;
  height: 15px;
  opacity: 0.4;
  position: absolute;
  right: 10px;
  cursor: pointer;
  top: 15px;
}
.field-info {
  width: 210px;
  max-width: 210px;
  word-break: break-all;
  padding-left: 10px;
  margin-left: 10px;
  border: solid 1px transparent;
  padding: 15px 10px;
}
.field-info:hover {
  border-left: 1px solid #b0dbe1;
}
.newderivation .el-dialog__header {
  display: none !important;
}
.newderivation .f-dialog-content {
  padding: 0px;
}
.newderivation .new-header-container {
  padding-left: 0px;
  display: flex;
}
.left-conatiner-content,
.right-conatiner-content {
  padding: 20px 25px;
}
.left-conatiner-content {
  overflow-y: scroll;
  overflow-x: hidden;
  height: 100%;
  padding-bottom: 80px;
}
.heading-name {
  padding: 10px;
}
.newderivation .new-header-container {
  border: none;
  padding-top: 10px;
}
.build-functions-scroll {
  overflow-y: scroll;
  overflow-x: hidden;
  height: 100%;
}
.formulalengendscontainer {
}
.formula-btn {
  padding: 8px 12px;
  border-radius: 2px;
  border: solid 1px #d0d9e2;
}
.formula-btn:hover {
  border: solid 1px #d0d9e2;
}
</style>
