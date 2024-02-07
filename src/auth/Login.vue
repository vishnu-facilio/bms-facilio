<template>
  <component :is="loginComponent"></component>
</template>

<script>
import RebrandMixin from 'util/rebrandMixin'
import FacilioLogin from './FacilioLogin'
import PortalLogin from './PortalLogin'

export default {
  mixins: [RebrandMixin],
  components: {
    FacilioLogin,
    PortalLogin,
  },
  computed: {
    loginComponent() {
      if (this.isWebServicePortal) {
        return 'PortalLogin'
      }
      return 'FacilioLogin'
    },
  },
}
</script>
