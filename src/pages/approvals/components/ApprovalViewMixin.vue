<script>
import { isEmpty } from '@facilio/utils/validation'
import {
  isWebTabsEnabled,
  findRouteForTab,
  pageTypes,
  tabTypes,
} from '@facilio/router'
import { mapState } from 'vuex'
import { API } from '@facilio/api'

const activityView = {
  name: 'activities',
  displayName: 'History',
}
const pendingView = {
  name: 'pendingapproval',
  displayName: 'Pending Approval',
}
export default {
  data() {
    return {
      modules: [],
    }
  },
  computed: {
    ...mapState('webtabs', {
      currentTab: state => state.selectedTab,
    }),
    groupViews() {
      let currentModules = []

      if (isWebTabsEnabled()) {
        currentModules = this.moduleList || this.currentTab.modules
      } else {
        currentModules = this.modules
      }

      let moduleVsViewList = (currentModules || []).map(m => ({
        name: m.name,
        moduleName: m.name,
        displayName: m.displayName,
        id: m.moduleId,
        views: [pendingView, activityView],
      }))

      return moduleVsViewList
    },
    currentView() {
      let { viewname } = this.$route.params

      if (!isEmpty(viewname) && viewname !== 'activities') {
        return viewname
      } else if (this.isActivityView) {
        return 'activities'
      } else {
        return null
      }
    },
  },
  methods: {
    async loadApprovalModules() {
      if (isEmpty(this.moduleList)) {
        let { error, data } = await API.get('v2/approval/modules')

        if (!error) {
          this.modules = data.modules || []
        }
      } else {
        this.modules = this.moduleList
      }
    },
    getCriteria(viewname) {
      return viewname === 'activities'
        ? { pageType: pageTypes.APPROVAL_ACTIVITY }
        : { pageType: pageTypes.APPROVAL_LIST }
    },
    goToView(view, group = null) {
      if (!isEmpty(view)) {
        let { name: viewname } = view
        if (isWebTabsEnabled()) {
          let { getCriteria } = this
          let { moduleName } = group

          let { name } =
            findRouteForTab(tabTypes.APPROVAL, getCriteria(viewname)) || {}

          if (name) {
            this.$router
              .push({
                name,
                params: { moduleName, viewname },
              })
              .catch(error => console.warn('Could not switch view\n', error))
          }
        } else {
          let path = this.getPathForView(view, group)
          this.$router.push({ path }).catch(() => {})
        }
      }
    },
    getPathForView(view, group) {
      let { pathPrefix } = this
      let prefix =
        pathPrefix[pathPrefix.length - 1] === '/'
          ? pathPrefix.slice(0, -1)
          : pathPrefix

      return `${prefix}/${group.moduleName}/${view.name}`
    },
  },
}
</script>
