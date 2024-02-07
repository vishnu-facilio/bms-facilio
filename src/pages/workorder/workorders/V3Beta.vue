<template>
  <div></div>
</template>
<script>
import helpers from './../../../util/helpers'
// import V3Edit from 'src/pages/workorder/workorders/v3/WoV3EditForm.vue'
// import V2Edit from 'src/pages/workorder/workorders/v1/WorkOrderEditForm'

export default {
  title() {
    'V3Beta'
  },
  // components: { V3Edit, V2Edit },
  computed: {
    betaLicenseEnabled() {
      const { isLicenseEnabled } = helpers
      const woV3BetaLicense = isLicenseEnabled('WOV3_BETA')
      // console.log('beta license: ', woV3BetaLicense)
      return woV3BetaLicense
    },
  },
}
</script>
