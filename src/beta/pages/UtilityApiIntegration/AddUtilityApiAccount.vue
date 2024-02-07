<template>
  <FContainer
    v-if="utilityUrl"
    ref="utilityApiIframe"
    height="100%"
    width="99%"
    borderRadius="high"
    border="1px solid"
    borderColor="borderNeutralBaseSubtle"
    marginRight="containerLarge"
    tag="iframe"
    :src="utilityUrl"
    class="utility-api-site-container"
    id="utility-api-site"
  >
  </FContainer>
</template>
<script>
import { FContainer } from '@facilio/design-system'
export default {
  name: 'AddUtilityApiAccount',
  components: { FContainer },
  computed: {
    utilityUrl() {
      let { $route } = this
      let { query } = $route || {}
      let secretState = this.$getProperty(query, 'secretState', '')

      let { protocol, host } = window.location
      let domain = `${protocol}//${host}`

      let redirectUri = `${domain}/energy/utilityCustomer/authenticate`
      redirectUri = encodeURIComponent(redirectUri)

      let url = ''
      if (domain === 'http://localhost:9090') {
        url = `https://utilityapi.com/authorize/keerthana_facilio?redirect_uri=${redirectUri}&state=${secretState}`
      } else if (domain === 'https://stage.facilio.in') {
        url = `https://utilityapi.com/authorize/keerthana+stage_facilio?redirect_uri=${redirectUri}&state=${secretState}`
      } else if (domain === 'https://stage2.facilio.in') {
        url = `https://utilityapi.com/authorize/keerthana+stage2_facilio?redirect_uri=${redirectUri}&state=${secretState}`
      } else {
        url = ` https://utilityapi.com/authorize/facilio_data_request?redirect_uri=${redirectUri}&state=${secretState}`
      }

      return url
    },
  },
}
</script>
