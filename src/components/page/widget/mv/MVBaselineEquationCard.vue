<template>
  <div class="mv-equation">
    <div class="d-flex">
      <div class="d-flex flex-direction-column form-two-column">
        <div>
          <span class="f13 bold letter-spacing1 energy text-uppercase">{{
            $t('mv.summary.baseline_equation')
          }}</span>
          <span class="mL15 mR15">-</span>
          <span
            v-if="!$validation.isEmpty(workFlow)"
            class="f13 formula-expression letter-spacing1"
            >{{ workFlow.resultEvaluator }}</span
          >
        </div>
        <div class="mT15">
          <f-formula-builder
            v-model="workFlow"
            module="formulaField"
            :title="''"
            :renderInLeft="true"
            :showOnlyVarible="true"
            :isMv="true"
          ></f-formula-builder>
        </div>
      </div>
    </div>
    <!-- action area for widget-title-section-->
    <portal :to="widget.key + '-title-section'">
      <div
        class="widget-header d-flex flex-direction-row justify-content-space mB15 mT5"
      >
        <div class="widget-header-name text-uppercase">
          {{ $t('mv.summary.baseline_equation') }}
        </div>
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
    baseline() {
      let { details } = this
      let { baselines = [] } = details
      return baselines[0]
    },
    workFlow() {
      let { baseline } = this
      let { formulaField = {} } = baseline
      return (formulaField || {}).workflow || {}
    },
  },
}
</script>

<style></style>
