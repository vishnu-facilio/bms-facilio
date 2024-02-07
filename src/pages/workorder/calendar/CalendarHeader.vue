<template>
  <div class="d-flex flex-row">
    <ul class="subheader-tabs pull-left pB10">
      <template v-for="view in views">
        <router-link
          tag="li"
          :key="view.name"
          :to="`${parentPath}/${view.path}`"
          active-class="active"
        >
          <a>{{ view.label }}</a>
          <div class="sh-selection-bar"></div>
        </router-link>
      </template>
    </ul>
    <div class="mL-auto d-flex flex-row">
      <portal-target name="pmPagination" v-if="!showSearch"></portal-target>
      <template v-if="showWoFilters">
        <span class="separator" v-if="!showSearch">|</span>
        <el-tooltip
          effect="dark"
          :content="$t('common._common.search')"
          placement="left"
        >
          <AdvancedSearch
            :key="`${moduleName}-search`"
            :moduleName="moduleName"
            :moduleDisplayName="moduleDisplayName"
          >
          </AdvancedSearch>
        </el-tooltip>
      </template>
    </div>
  </div>
</template>
<script>
import { mapState, mapGetters } from 'vuex'
import { isWebTabsEnabled, findRouteForTab } from '@facilio/router'
import AdvancedSearch from 'newapp/components/search/AdvancedSearch'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['showWoFilters'],
  components: { AdvancedSearch },
  data() {
    return {
      moduleDisplayName: `${this.$t('common._common.workorder')}`,
      moduleName: 'workorder',
    }
  },
  computed: {
    ...mapGetters('webtabs', ['tabHasPermission']),
    ...mapState({
      currentTab: state => state.webtabs.selectedTab,
      showSearch: state => state.search.active,
    }),

    views() {
      let views = [
        {
          name: 'pmCalender',
          label: this.$t('maintenance.calender.calender_pm'),
          path: `planned`,
          permission: 'planned:CALENDAR',
          hideIfOrg: [274],
        },
        {
          name: 'pmPlanner',
          label: this.$t('maintenance.calender.asset_planner'),
          path: `pmplannernew`,
          permission: 'planned:PM_PLANNER',
          hide_if_license: 'PM_PLANNER',
        },
        {
          name: 'staffPlanner',
          label: this.$t('maintenance.calender.staff_planner'),
          path: `staffplannernew`,
          permission: 'planned:PM_PLANNER',
          hide_if_license: 'PM_PLANNER',
        },
        {
          name: 'spacePlanner',
          label: this.$t('maintenance.calender.space_planner'),
          path: `spaceplanner`,
          permission: 'planned:PM_PLANNER',
          hide_if_license: 'PM_PLANNER',
        },
      ]

      return views.filter(view => {
        let { hide_if_license } = view || {}
        let canHide = (view.hideIfOrg || []).includes(Number(this.$org.id))
        let isPmV2Enabled = !isEmpty(hide_if_license)
          ? this.$helpers.isLicenseEnabled(hide_if_license)
          : false
        /**
         * Hided the PM Calender views in apps other than main-app, for all the roles.
         */
        let hasPermission = false
        if (!isWebTabsEnabled()) {
          hasPermission = this.$hasPermission(view.permission)
        }
        //else can be removed after complete deprecation of planners in maintenance app
        else {
          hasPermission = this.tabHasPermission(
            view.permission,
            this.currentTab
          )
        }
        let canShowView = !canHide && hasPermission && !isPmV2Enabled

        return canShowView
      })
    },

    parentPath() {
      if (isWebTabsEnabled()) return this.findRoute()
      else return `/app/wo/calendar`
    },
  },
  watch: {
    '$route.path': {
      handler: 'openFirstView',
      immediate: true,
    },
  },
  methods: {
    findRoute() {
      let { currentTab = {}, $router } = this
      let route = findRouteForTab(currentTab.id) || {}

      return $router.resolve({ name: route.name }).href
    },
    openFirstView() {
      let availableViews = this.views.map(v => v.path)
      let isViewNotAvailable =
        !availableViews.some(path => this.$route.path.includes(path)) &&
        isEmpty(this.$route.params.viewname)

      if (isViewNotAvailable) {
        let path = availableViews[0] || {}
        path && this.$router.push(`${this.parentPath}/${path}`)
      }
    },
  },
}
</script>
<style lang="scss" scoped>
.subheader-tabs {
  list-style-type: none;
  margin: 0;
  padding: 0;
  overflow: hidden;
  background-color: transparent;

  li {
    float: left;
    padding: 0px 15px;
  }
  li.active a {
    font-weight: 500;
  }
  li.active .sh-selection-bar {
    border-right: 0px solid #e0e0e0;
    border-left: 0px solid #e0e0e0;
    border-color: var(--fc-theme-color);
  }
  li .sh-selection-bar {
    border: 1px solid transparent;
    width: 25px;
    margin-top: 7px;
    position: absolute;
  }
  &:not(.has-more-views) li:last-of-type {
    border-right: 0px;
  }
}
</style>
