<template>
  <div style="overflow:hidden; height: 100%;">
    <MultiSitePMv1Form v-if="isMultiSitePm" />
    <SingleSitePMv1Form v-else />
  </div>
</template>
<script>
import MultiSitePMv1Form from 'pages/workorder/multisitepreventive/pmnewform.vue'
import SingleSitePMv1Form from 'pages/workorder/preventive/pmnewform.vue'

export default {
  components: {
    MultiSitePMv1Form,
    SingleSitePMv1Form,
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
