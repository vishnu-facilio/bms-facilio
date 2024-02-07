<template>
  <div class="mv-equation">
    <div class="d-flex flex-direction-column">
      <div>
        <el-row>
          <el-col :span="5">
            <div class="f13 bold letter-spacing1 energy text-uppercase">
              {{ $t('mv.summary.name') }}
            </div>
          </el-col>
          <el-col :span="1">
            <span class="f13 bold letter-spacing1 energy">-</span>
          </el-col>
          <el-col :span="14">
            <div class="f13 formula-expression letter-spacing1">
              {{ adjustment.name }}
            </div>
          </el-col>
        </el-row>
      </div>
      <div class="mT20" v-if="!$validation.isEmpty(workFlow)">
        <el-row>
          <el-col :span="8">
            <div class="f13 bold letter-spacing1 energy text-uppercase">
              {{ $t('mv.summary.adjustments_equation') }}
            </div>
          </el-col>
          <el-col :span="1">
            <div class="f13 bold letter-spacing1 energy">-</div>
          </el-col>
          <el-col :span="12">
            <div class="f13 formula-expression letter-spacing1">
              {{ workFlow.resultEvaluator }}
            </div>
          </el-col>
        </el-row>
      </div>
      <div v-if="!$validation.isEmpty(workFlow)" class="mT20">
        <f-formula-builder
          v-model="workFlow"
          module="formulaField"
          :title="''"
          :renderInLeft="true"
          :showOnlyVarible="true"
        ></f-formula-builder>
      </div>
      <div v-else-if="adjustment.constant" class="mT20">
        <el-row>
          <el-col :span="5">
            <div class="f13 bold letter-spacing1 energy text-uppercase">
              {{ $t('mv.summary.adjustments_constant') }}
            </div>
          </el-col>
          <el-col :span="1">
            <div class="f13 bold letter-spacing1 energy">-</div>
          </el-col>
          <el-col :span="14">
            <div class="f13">{{ adjustment.constant }}</div>
          </el-col>
        </el-row>
      </div>
      <div class="mT20">
        <el-row>
          <el-col :span="5">
            <div class="f13 bold letter-spacing1 energy text-uppercase">
              {{ $t('mv.summary.date_range') }}
            </div>
          </el-col>
          <el-col :span="1">
            <div class="f13 bold letter-spacing1 energy">-</div>
          </el-col>
          <el-col :span="14">
            <span class="f13 formula-expression letter-spacing1">
              {{ adjustment.startTime | formatDate(true) }}
              <span class="mL15 mR15">-</span>
              {{ adjustment.endTime | formatDate(true) }}
            </span>
          </el-col>
        </el-row>
      </div>
    </div>
    <!-- action area for widget-title-section-->
    <portal :to="widget.key + '-title-section'">
      <div
        class="widget-header d-flex flex-direction-row justify-content-space mB15 mT5"
      >
        <div class="widget-header-name text-uppercase">{{ headerLabel }}</div>
      </div>
    </portal>
    <!-- action area -->
  </div>
</template>

<script>
import FFormulaBuilder from '@/workflow/FFormulaBuilder'

export default {
  props: ['widget', 'details'],
  components: {
    FFormulaBuilder,
  },
  computed: {
    adjustment() {
      let { details, widget } = this
      let { widgetParams = {} } = widget
      let { adjustmentIndex = 0 } = widgetParams
      let { adjustments = [] } = details
      return adjustments[adjustmentIndex]
    },
    workFlow() {
      let { adjustment = {} } = this
      let { formulaField = {} } = adjustment
      return (formulaField || {}).workflow || {}
    },
    headerLabel() {
      let { widget } = this
      let { widgetParams = {} } = widget
      let { adjustmentIndex = 0 } = widgetParams
      let label = this.$t('mv.summary.adjustments_equation')
      return `${label} ${adjustmentIndex + 1}`
    },
  },
}
</script>

<style></style>
