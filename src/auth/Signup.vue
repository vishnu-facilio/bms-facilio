<template>
  <component :is="loginComponent"></component>
</template>
<script>
import FacilioSignup from './FacilioSignup'
import PortalSignup from './PortalSignup'
import { isPortalDomain } from 'util/utility-methods'

export default {
  components: {
    FacilioSignup,
    PortalSignup,
  },
  computed: {
    loginComponent() {
      if (isPortalDomain()) {
        return 'PortalSignup'
      }
      return 'FacilioSignup'
    },
  },
}
</script>
