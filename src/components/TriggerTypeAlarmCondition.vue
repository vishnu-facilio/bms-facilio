<template>
  <el-row class="mT25">
    <new-criteria-builder
      :exrule="trigger.criteria"
      @condition="assignCriteria"
      module="alarm"
    ></new-criteria-builder>
  </el-row>
</template>
<script>
import NewCriteriaBuilder from '@/NewCriteriaBuilder'
export default {
  props: ['trigger'],
  components: { NewCriteriaBuilder },
  methods: {
    assignCriteria(val) {
      this.trigger.criteria = val
      this.$forceUpdate()
    },
  },
}
</script>
<style></style>
