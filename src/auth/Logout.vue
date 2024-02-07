<template>
  <div></div>
</template>
<script>
import { API } from '@facilio/api'
import helpers from 'src/util/helpers.js'
import store from 'src/store/index'
import router from 'vue-router'

export default {
  beforeRouteEnter() {
    API.get('logout', null, { force: true })
      .then(() => {
        store.commit('LOGIN_REQUIRED', true)

        let { currentRoute: { query = {} } = {} } = router
        helpers.logout(query.redirect || null)
      })
      .catch(function(error) {
        if (error) {
          console.error(error)
        }
      })
  },
}
</script>
