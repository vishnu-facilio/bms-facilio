<template>
  <component :is="loginComponent"></component>
</template>
<script>
import FacilioPasswordReset from './FacilioPasswordReset'
import PortalPasswordReset from './PortalPasswordReset'
import { isPortalDomain } from 'util/utility-methods'

export default {
  components: {
    FacilioPasswordReset,
    PortalPasswordReset,
  },
  computed: {
    loginComponent() {
      if (isPortalDomain()) {
        return 'PortalPasswordReset'
      }
      return 'FacilioPasswordReset'
    },
  },
}
</script>
