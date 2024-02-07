<template>
  <router-view :token="token" :companyLogoUrl="companyLogoUrl"></router-view>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'

export default {
  data() {
    return {
      companyLogoUrl: '',
    }
  },
  created() {
    this.setCompanyLogo()
  },
  computed: {
    token() {
      return this.$route.query.token
    },
  },
  methods: {
    async setCompanyLogo() {
      let url = 'v2/fetchAccount'
      let { error, data } = await API.get(url)

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        if (!isEmpty(data)) {
          let { account } = data || {}
          let { org } = account || {}
          let { logoUrl } = org || {}
          this.companyLogoUrl = logoUrl
        }
      }
    },
  },
}
</script>
<style lang="scss">
html,
body,
#q-app {
  height: 100%;
}
</style>
