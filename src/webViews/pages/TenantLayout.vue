<template>
  <router-view v-if="!isMetaLoading"></router-view>
</template>
<script>
import Vue from 'vue'
import Util from 'util/index'
import { constructBaseURL, setBaseURL } from 'util/baseUrl'
import { API } from '@facilio/api'
import { isObject } from '@facilio/utils/validation'
import { emitEvent, registerActions } from 'src/webViews/utils/mobileapps'
import { initGoogleAnalytics } from 'src/track'

const isDev = process.env.NODE_ENV === 'development'

export default {
  data() {
    return {
      isMetaLoading: true,
      linkName: 'tenant',
    }
  },
  async created() {
    setBaseURL(constructBaseURL(this.linkName))

    // Add handlers to window so that mobile call set account object
    registerActions('setAccount', (...args) => this.setAccount(...args))

    emitEvent('webviewLoaded')

    if (this.shouldFetchAccount) {
      await this.loadAccount()
    }
  },
  computed: {
    shouldFetchAccount() {
      const forceFetch =
        JSON.parse(localStorage.getItem('fc-webview-fetch')) || false

      return isDev || forceFetch
    },
  },
  methods: {
    async setAccount(account = {}) {
      this.isMetaLoading = true

      account = isObject(account) ? account : JSON.parse(account)

      console.warn(`SetAccount called with ${JSON.stringify(account)}`)

      this.$store.dispatch('setServicePortalAccount', account)
      Vue.prototype.$portaluser = account.user
      Vue.use(Util, { account })

      this.isMetaLoading = false
    },
    async loadAccount() {
      this.isMetaLoading = true
      let { data, error } = await API.get('/v2/v2portalaccount')

      if (!error) {
        let { account } = data
        this.$store.dispatch('setServicePortalAccount', account)

        Vue.prototype.$portaluser = account.user
        Vue.use(Util, { account })
        initGoogleAnalytics(account)
      }
      this.isMetaLoading = false
    },
  },
}
</script>
