<template>
  <div class="fc-white-theme digital-log-book-container height100">
    <div class="space-info fc-border-1 p15  position-sticky top0">
      {{ currentAccount.data.device.associatedResource.name }}
    </div>
    <router-view> </router-view>
  </div>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'
export default {
  components: {},

  beforeRouteLeave(to, from, next) {
    //when navigating to summary page stop it and navigate to your summary page : )
    //  if(to.name.includes('summary'))
    //  {
    //navigate to wo summary in devices , same component

    //just switch between list and summary component
    //accessing router props directly throws type error
    let cp = {}
    Object.assign(cp, to)
    if (cp.name && cp.name.includes('summary')) {
      next({ name: 'deviceWoSummary', params: { id: cp.params.id } })
    } else {
      next()
    }
  },
  watch: {
    $route: {
      handler(newVal) {
        //when workorder list navigates to  summary page , redirect to summary within devices router
        if (newVal.name !== 'deviceWoSummary') {
          this.setQueryParams()
        }
      },
      deep: true,
      immediate: true,
    },
  },

  data() {
    return {
      currentView: 'open',
      showWOSummary: false,
    }
  },

  computed: {
    ...mapGetters(['currentAccount']),
    ...mapActions({
      loadViewDetail: 'view/loadViewDetail',
      savesorting: 'view/savesorting',
    }),
  },

  mounted() {
    this.loadViewss()
    this.getViewDetail()
    this.$store.dispatch('view/loadModuleMeta', 'workorder')
  },

  methods: {
    setQueryParams() {
      let spaceFilter = {
        resource: [
          {
            operatorId: 38,
            value: [this.currentAccount.data.device.associatedResource.id + ''],
          },
        ],
      }
      this.$router.replace({
        query: {
          ...this.$route.query,
          search: JSON.stringify(spaceFilter),
          includeParentFilter: true,
        },
      })
    },
    loadViewss() {
      let param = {
        moduleName: 'workorder',
      }
      this.$store.dispatch('view/loadGroupViews', param)
    },
    getViewDetail() {
      this.$store.dispatch('view/loadViewDetail', {
        viewName: this.currentView,
        moduleName: 'workorder',
      })
    },
  },
}
</script>

<style scoped>
.space-info {
  font-size: 22px;
  color: #324056;
  text-align: center;
  z-index: 5;
  margin: 10px;
  background-color: #fff;
}
</style>
