<template>
  <FContainer height="100%" width="99%" marginRight="containerLarge">
    <FSpinner :size="50" class="utility-customer-create-spinner" />
  </FContainer>
</template>
<script>
import { FContainer, FSpinner } from '@facilio/design-system'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import { findRouteForModule, pageTypes } from '@facilio/router'
import router from 'src/router'

export default {
  name: 'UtilityAuthentication',
  components: {
    FContainer,
    FSpinner,
  },
  created() {
    this.checkCanCreate()
  },
  methods: {
    async checkCanCreate() {
      let { $route } = this
      let { query } = $route || {}
      let { referral, state } = query || {}

      if (!isEmpty(referral) && !isEmpty(state)) {
        await this.addUtilityCustomer(referral, state)
      }
    },
    async addUtilityCustomer(referral, state) {
      let url = 'v3/utilityIntegration/fetchUtilityCustomerAndMeter'
      let params = { referral, state }
      let moduleName = 'utilityIntegrationCustomer'
      let { error } = await API.post(url, params)

      if (!error) {
        let { name } = findRouteForModule(moduleName, pageTypes.LIST) || {}
        let params = { viewname: 'all' }
        let route = { name, params }

        let { href } = router.resolve(route) || {}

        if (!isEmpty(href)) {
          window.parent.location.href = href
        }
        this.$message.success(
          this.$t(
            'common.utility.utility_account_has_been_successfully_associated'
          )
        )
      } else {
        this.$message.error(error.message || 'Error Occured')
      }
    },
  },
}
</script>
<style lang="scss" scoped>
.utility-customer-create-spinner {
  margin: 20% auto auto auto;
}
</style>
