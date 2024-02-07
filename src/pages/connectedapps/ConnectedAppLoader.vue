<template>
  <div>
    <div v-if="failed">Invalid connected app link.</div>
    <form
      :action="acsURL"
      method="POST"
      ref="formSubmit"
      style="display: none;"
    >
      <input type="hidden" name="SAMLResponse" :value="samlResponse" />
      <input type="hidden" name="RelayState" :value="relayState" />
    </form>
  </div>
</template>

<script>
import http from 'util/http'
export default {
  data() {
    return {
      loading: true,
      failed: false,
      acsURL: null,
      samlResponse: null,
      relayState: null,
    }
  },
  computed: {
    connectedAppLinkName() {
      return this.$route.params.linkName
    },
  },
  mounted() {
    this.loadConnectedApp()
  },
  watch: {
    connectedAppLinkName: function() {
      this.loadConnectedApp()
    },
  },
  methods: {
    loadConnectedApp() {
      this.loading = true

      let url = '/v2/connectedApps/' + this.connectedAppLinkName
      let params = this.$route.query ? this.$route.query : {}
      http
        .get(url, { params: params })
        .then(response => {
          this.loading = false
          if (response.data.result && response.data.result.viewUrl) {
            if (response.data.result.samlResponse) {
              this.acsURL = response.data.result.acsURL
              this.samlResponse = response.data.result.samlResponse
              this.relayState = response.data.result.relay
                ? response.data.result.relay
                : response.data.result.viewUrl

              this.$nextTick(() => {
                this.$refs.formSubmit.submit()
              })
            } else {
              window.location.href = response.data.result.viewUrl
            }
          } else if (response.data.responseCode === 1) {
            this.failed = true
          } else {
            this.failed = true
            this.$router.push({
              name: 'login',
              query: { redirect: this.$route.path },
            })
          }
        })
        .catch(error => {
          this.$router.push({
            name: 'login',
            query: { redirect: this.$route.path },
          })
          if (error) {
            this.failed = true
          }
        })
    },
  },
}
</script>

<style></style>
