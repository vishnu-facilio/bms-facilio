<template>
  <component :is="loginComponent"></component>
</template>
<script>
import FacilioAcceptInvite from './FacilioAcceptInvite'
import PortalAcceptInvite from './PortalAcceptInvite'
import { isPortalDomain } from 'util/utility-methods'

export default {
  components: {
    FacilioAcceptInvite,
    PortalAcceptInvite,
  },
  computed: {
    loginComponent() {
      if (isPortalDomain()) {
        return 'PortalAcceptInvite'
      }
      return 'FacilioAcceptInvite'
    },
  },
}
</script>
