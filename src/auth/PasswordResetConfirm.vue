<template>
  <component :is="loginComponent"></component>
</template>
<script>
import FacilioPasswordResetConfirm from './FacilioPasswordResetConfirm'
import PortalPasswordResetConfirm from './PortalPasswordResetConfirm'
import { isPortalDomain } from 'util/utility-methods'

export default {
  components: {
    FacilioPasswordResetConfirm,
    PortalPasswordResetConfirm,
  },
  computed: {
    loginComponent() {
      if (isPortalDomain()) {
        return 'PortalPasswordResetConfirm'
      }
      return 'FacilioPasswordResetConfirm'
    },
  },
}
</script>
