<template>
  <component :is="loginComponent"></component>
</template>
<script>
import FacilioEmailRegistration from './FacilioEmailRegistration'
import PortalEmailRegistration from './PortalEmailRegistration'
import { isPortalDomain } from 'util/utility-methods'

export default {
  components: {
    FacilioEmailRegistration,
    PortalEmailRegistration,
  },
  computed: {
    loginComponent() {
      if (isPortalDomain()) {
        return 'PortalEmailRegistration'
      }
      return 'FacilioEmailRegistration'
    },
  },
}
</script>
