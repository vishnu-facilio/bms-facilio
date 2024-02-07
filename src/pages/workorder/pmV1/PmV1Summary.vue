<template>
  <div>
    <MultiSitePMv1Summary v-if="isMultiSitePm" />
    <SingleSitePMv1Summary v-else />
  </div>
</template>
<script>
import MultiSitePMv1Summary from 'pages/workorder/multisitepreventive/v1/Pmsummary.vue'
import SingleSitePMv1Summary from 'pages/workorder/preventive/v1/Pmsummary.vue'

export default {
  components: {
    MultiSitePMv1Summary,
    SingleSitePMv1Summary,
  },
  computed: {
    isMultiSitePm() {
      let { params } = this.$route
      let { pmCreationType = 'MULTI_SITE' } = params || {}
      return pmCreationType === 'MULTI_SITE'
    },
  },
}
</script>
