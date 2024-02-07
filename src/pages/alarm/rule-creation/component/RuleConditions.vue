<template>
  <div>
    <div id="ruleconditions-header" class="section-header pB10">
      {{ $t('rule.create.alarm_conditions') }}
    </div>
    <FormulaBuilder
      :ruleCondition="ruleInfo"
      :moduleName="moduleName"
      @setCondition="setCondition"
      :isEditForm="isEditForm"
      :isV2="isV2"
      isMainApp="true"
    />
  </div>
</template>
<script>
import FormulaBuilder from 'src/pages/alarm/rule-creation/component/FormulaBuilder.vue'

export default {
  components: { FormulaBuilder },
  props: ['ruleInfo', 'isEditForm', 'moduleName', 'isV2'],
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
