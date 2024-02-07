<template>
  <div>
    <div id="formulaBuilder-header" class="section-header pB10">
      {{ $t('kpi.kpi.kpi_formula_builder') }}
    </div>
    <FormulaBuilder
      :ruleCondition="kpiInfo"
      @setCondition="setCondition"
      :isEditForm="isEditForm"
      :preferredCodeBoilerPlate="codeBoilerPlate"
      :removeToggle="isDynamicKpi"
      moduleName="readingkpi"
    />
  </div>
</template>
<script>
import FormulaBuilder from 'src/pages/alarm/rule-creation/component/FormulaBuilder.vue'
export default {
  components: { FormulaBuilder },
  props: ['kpiInfo', 'isEditForm'],
  data() {
    return {
      codeBoilerPlate: 'Number test(){\n \nreturn  ; \n}',
    }
  },
  computed: {
    isDynamicKpi() {
      let { kpiType } = this.kpiInfo || {}
      return kpiType === 'dynamic' || kpiType === 3
    },
  },
  methods: {
    setCondition(condition) {
      this.$emit('onConditionsChange', condition)
    },
  },
}
</script>
<style lang="scss">
.rule-condition {
  .section-container {
    .section-desc {
      font-size: 14px;
      letter-spacing: 0.5px;
      color: #6b7e91;
    }
  }
}
</style>
