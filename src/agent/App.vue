<template>
  <div>
    <div v-if="serverNotReachable">
      <server-not-reachable></server-not-reachable>
    </div>
    <div v-else-if="!hasMetaLoaded">
      <loader></loader>
    </div>
    <div v-else class="layout fc-main fc-white-theme">
      <AgentHome></AgentHome>
    </div>
  </div>
</template>
<script>
import { mapState } from 'vuex'
import HomeMixin from 'pages/HomeMixin.js'
import AgentHome from './Home'

export default {
  mixins: [HomeMixin],
  components: { AgentHome },
  async created() {
    this.$store.dispatch('loadOrgs')
    await this.$store.dispatch('getCurrentAccount')
    await this.$store.dispatch('getFeatureLicenses')
    await this.initializeMeta()
  },
  computed: {
    ...mapState({
      account: state => state.account,
    }),
  },
}
</script>
