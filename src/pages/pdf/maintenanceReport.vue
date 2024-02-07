<template>
  <div v-if="isValidatingPermalink">
    Loading...
  </div>
  <div v-else-if="isInvalidPermalink">
    Link expired or invalid.
  </div>
</template>

<script>
import Vue from 'vue'
import axios from 'axios'
import http from 'util/http'
export default {
  data() {
    return {
      isValidatingPermalink: true,
      isInvalidPermalink: false,
    }
  },
  methods: {
    validateLoggedInUser() {
      let self = this
      http
        .get('/v2/fetchAccount')
        .then(function(response) {
          if (
            response.data.responseCode === 0 &&
            response.data.result.account
          ) {
            self.$router.push({
              path:
                '/app/wo/dashboard/' +
                self.$route.query.linkName +
                '?daterange=' +
                self.$route.query.daterange,
            })
            return
          } else {
            let params = self.getParamDetail
            self.$router.push({
              path:
                '/app/permalink/report?token=' + self.permalink + '&' + params,
            })
            return
          }
        })
        .catch(function(error) {
          if (error) {
            console.log('current account call failed...')
          }
        })
    },
  },
  computed: {
    permalink() {
      return this.$route.query.token
    },
    getParamDetail() {
      return (
        'dashboardId=' +
        this.$route.query.id +
        '&dashboardLinkName=' +
        this.$route.query.linkName +
        '&dashboardModuleName=' +
        this.$route.query.moduleName +
        '&dashboardSiteId=' +
        this.$route.query.siteId +
        '&dashboardName=' +
        this.$route.query.name +
        '&dashboardSiteName=' +
        this.$route.query.siteName +
        '&daterange=' +
        this.$route.query.daterange
      )
    },
  },
  mounted() {
    this.validateLoggedInUser()
  },
}
</script>
<style>
@import './../../assets/styles/common.css';
</style>
<style>
@import './../../charts/styles/chart.css';
</style>
<style>
@import './../../assets/styles/energy.css';
</style>
<style>
@import './../../assets/styles/c3.css';
</style>
