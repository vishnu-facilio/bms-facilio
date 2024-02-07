<template>
  <div
    :class="[
      isAdjustments ? 'baseline-expression' : 'baseline-expression baselines',
    ]"
  >
    <div v-if="isAdjustments" class="baseline-header f14 bold">
      {{ expressionTitle }}
    </div>
    <div
      :class="[
        isAdjustments
          ? 'baseline-content adjustments'
          : 'baseline-content baselines',
      ]"
    >
      <el-form
        ref="baseline-form"
        :model="expression"
        label-position="left"
        label-width="100px"
      >
        <el-form-item
          v-if="isAdjustments"
          :label="$t('mv.summary.name')"
          label-width="150px"
          :required="true"
        >
          <el-input
            class="fc-input-full-border2 mT10"
            v-model="expression.name"
          ></el-input>
        </el-form-item>
        <el-form-item
          :label="formulaFieldLabel"
          label-width="150px"
          :required="true"
        >
          <el-radio-group v-if="isAdjustments" v-model="expression.isConstant">
            <el-radio class="fc-radio-btn" :label="true">Constant</el-radio>
            <el-radio class="fc-radio-btn" :label="false">Formula</el-radio>
          </el-radio-group>
          <f-formula-builder
            v-if="canShowFormulaBuilder(expression.isConstant)"
            v-model="expression.formulaField.workflow"
            module="formulaField"
            :title="''"
            :renderInLeft="true"
            :hideModeChange="isAdjustments"
            :isMv="true"
          ></f-formula-builder>
          <el-input
            :required="true"
            v-if="expression.isConstant"
            class="fc-input-full-border2 mT10"
            type="number"
            v-model="expression.constant"
          ></el-input>
        </el-form-item>
        <el-form-item
          :label="periodLabel"
          :required="true"
          label-width="150px"
          class="baseline-datepicker"
        >
          <FDatePicker
            v-model="expression.baseLinePeriod"
            :type="'daterange'"
            class="fc-input-full-border2 form-date-picker"
          ></FDatePicker>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import FFormulaBuilder from '@/workflow/FFormulaBuilder'
import FDatePicker from 'pages/assets/overview/FDatePicker'

export default {
  components: {
    FFormulaBuilder,
    FDatePicker,
  },
  props: {
    expression: {
      type: Object,
    },
    index: {
      type: Number,
    },
    isAdjustments: {
      type: Boolean,
    },
    sharedData: {
      type: Object,
    },
    sectionTitle: {
      type: String,
    },
  },
  computed: {
    periodLabel() {
      let { isAdjustments } = this
      let label = isAdjustments
        ? `${this.$t('mv.summary.adjustments_period')}`
        : `${this.$t('mv.summary.baselineperiod')}`
      return label
    },
    formulaFieldLabel() {
      let { isAdjustments } = this
      let label = isAdjustments
        ? 'Equation'
        : `${this.$t('mv.summary.baseline_equation')}`
      return label
    },
    expressionTitle() {
      let { index, sectionTitle } = this
      return `${sectionTitle} ${index}`
    },
  },
  methods: {
    canShowFormulaBuilder(isConstant) {
      let { isAdjustments } = this
      if (isAdjustments) {
        return !isConstant
      }
      return true
    },
  },
}
</script>

<style lang="scss">
.baseline-expression {
  margin-left: 30px;
  margin-right: 40px;
  &.baselines {
    margin-left: 50px;
    margin-right: 120px;
    .el-form-item {
      margin-bottom: 45px;
      .el-form-item__label {
        line-height: 22px;
        margin-top: 8px;
      }
    }
    .baseline-datepicker {
      &.el-form-item {
        .el-form-item__label {
          margin-top: 0px;
        }
      }
    }
    .fbContainer {
      margin-top: 10px;
    }
    .fbVariableLegendContainer {
      padding-top: 20px;
    }
  }
  .baseline-header {
    margin: 20px 0px 10px;
    letter-spacing: 0.8px;
    color: #8ca1ad;
  }
  .baseline-content {
    &.adjustments {
      padding: 30px;
      box-shadow: 0 2px 11px 2px rgba(223, 226, 232, 0.14);
      border: solid 1px #ebedf4;
      background-color: #ffffff;
    }
    .fbVariableLegendContainer {
      line-height: normal;
    }
  }
}
</style>
