<template>
  <div>
    <el-tabs v-model="activeTab">
      <el-tab-pane
        v-for="tab in tabs"
        :key="tab.name"
        :name="tab.name"
        :label="tab.displayName"
        lazy
      >
        <list
          :tabType="tab.type"
          :url="tab.url"
          :isActive="tab.isActive"
          :portalName="widget.key + '-topbar'"
          :emptyStateMsg="emptyStateMsg"
          :goToSummary="goToSummary"
        ></list>
      </el-tab-pane>
    </el-tabs>

    <!-- portal for pagination and search -->
    <div class="widget-topbar-actions">
      <portal-target :name="widget.key + '-topbar'"></portal-target>
    </div>
    <!-- portal -->

    <!-- action area for section (assuming 1 widget per section)-->
    <portal :to="sectionKey + '-title-section'" slim>
      <span v-if="$hasPermission('planned:CREATE')">
        <a @click="goToCalendar()" class="f13 pR10 mR10 border-right">{{
          $t('asset.maintenance.show_calendar')
        }}</a>
        <a
          v-if="!decommission"
          @click="goToPmCreation()"
          class="f13 text-fc-pink"
          >+ {{ $t('asset.maintenance.new_pm') }}</a
        >
      </span>
    </portal>
    <!-- action area -->
  </div>
</template>
<script>
import List from '../../common/CommonWoList'
import {
  isWebTabsEnabled,
  findRouteForTab,
  findRouteForModule,
  pageTypes,
  tabTypes,
} from '@facilio/router'

export default {
  props: [
    'details',
    'layoutParams',
    'hideTitleSection',
    'sectionKey',
    'widget',
  ],
  data() {
    return {
      activeTab: 'upcoming_pm',
      emptyStateMsg: {
        title: this.$t('asset.maintenance.no_pms'),
        desc: this.$t('asset.maintenance.no_pms_desc'),
      },
    }
  },
  components: { List },
  computed: {
    decommission() {
      return this.$getProperty(this, 'details.decommission', false)
    },
    tabs() {
      return [
        {
          type: 'upcoming',
          name: 'upcoming_pm',
          displayName: this.$t('asset.maintenance.upcoming'),
          url: this.upcomingPmUrl,
          isActive: this.activeTab === 'upcoming_pm',
        },
        {
          type: 'open',
          name: 'open_pm',
          displayName: this.$t('asset.maintenance.open'),
          url: this.openPmUrl,
          isActive: this.activeTab === 'open_pm',
        },
        {
          type: 'closed',
          name: 'closed_pm',
          displayName: this.$t('asset.maintenance.closed'),
          url: this.closedPmUrl,
          isActive: this.activeTab === 'closed_pm',
        },
      ]
    },
    upcomingPmUrl() {
      let filters = encodeURIComponent(
        JSON.stringify({
          resource: { operatorId: 36, value: [this.details.id + ''] },
          sourceType: { operatorId: 9, value: ['5'] },
          createdTime: { operatorId: 73 },
        })
      )
      return `/v2/workorders/view/all?filters=${filters}&fetchAllType=true&orderBy=createdTime&orderType=asc&overrideViewOrderBy=true`
    },
    openPmUrl() {
      let filters = encodeURIComponent(
        JSON.stringify({
          resource: { operatorId: 36, value: [this.details.id + ''] },
        })
      )
      return `/v2/workorders/view/planned?filters=${filters}&includeParentFilter=true`
    },
    closedPmUrl() {
      let filters = encodeURIComponent(
        JSON.stringify({
          resource: { operatorId: 36, value: [this.details.id + ''] },
          sourceType: { operatorId: 9, value: ['5'] },
        })
      )
      return `/v2/workorders/view/closed?filters=${filters}&includeParentFilter=true`
    },
  },
  methods: {
    goToCalendar() {
      if (isWebTabsEnabled()) {
        let route = findRouteForTab(tabTypes.CUSTOM, {
          config: { type: 'pmCalendar' },
        })
        let routePath = this.$router.resolve({ name: route.name }).href

        this.$router.push({
          path: `${routePath}/pmplannernew`,
          query: { assetId: this.details.id },
        })
      } else {
        this.$router.push({
          path: '/app/wo/calendar/pmplannernew',
          query: { assetId: this.details.id },
        })
      }
    },
    goToPmCreation() {
      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule('preventivemaintenance', pageTypes.CREATE) || {}
        name && this.$router.push({ name })
      } else {
        if (this.$helpers.isLicenseEnabled('MULTISITEPM')) {
          this.$router.push({
            path: '/app/wo/multiplanned/new',
          })
        } else {
          this.$router.push({
            path: '/app/wo/planned/new',
          })
        }
      }
    },
    goToSummary(wo) {
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule('workorder', pageTypes.OVERVIEW) || {}

        if (name) {
          this.$router.push({
            name,
            params: { viewname: 'all', id: wo.id },
          })
        }
      } else {
        this.$router.push({ path: `/app/wo/orders/summary/${wo.id}` })
      }
    },
  },
}
</script>
