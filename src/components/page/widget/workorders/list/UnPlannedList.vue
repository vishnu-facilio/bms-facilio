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
          :goToSummary="goToSummary"
          :portalName="widget.key + '-topbar'"
          :emptyStateMsg="emptyStateMsg"
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
      <!-- <a @click="goToLink('/app/wo/create')" class="f13 text-fc-pink"
        >+ {{ $t('asset.maintenance.new_wo') }}</a
      > -->
      <a
        v-if="$hasPermission('asset:CREATE') && !decommission"
        @click="openWoCreation()"
        class="f13 text-fc-pink"
        >+ {{ $t('asset.maintenance.new_wo') }}</a
      >
    </portal>
    <!-- action area -->
  </div>
</template>
<script>
import List from '../../common/CommonWoList'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import { isEmpty } from '@facilio/utils/validation'

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
      activeTab: 'open_wo',
      emptyStateMsg: {
        title: this.$t('asset.maintenance.no_wos'),
        desc: this.$t('asset.maintenance.no_wos_desc'),
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
          type: 'open',
          name: 'open_wo',
          displayName: this.$t('asset.maintenance.open'),
          url: this.openWoUrl,
          isActive: this.activeTab === 'open_wo',
        },
        {
          type: 'closed',
          name: 'closed_wo',
          displayName: this.$t('asset.maintenance.closed'),
          url: this.closeWoUrl,
          isActive: this.activeTab === 'closed_wo',
        },
      ]
    },
    openWoUrl() {
      let filters = encodeURIComponent(
        JSON.stringify({
          resource: { operatorId: 36, value: [this.details.id + ''] },
        })
      )
      return `/v2/workorders/view/unplanned?filters=${filters}&includeParentFilter=true`
    },
    closeWoUrl() {
      let filters = encodeURIComponent(
        JSON.stringify({
          resource: { operatorId: 36, value: [this.details.id + ''] },
          sourceType: { operatorId: 10, value: ['5'] },
        })
      )
      return `/v2/workorders/view/closed?filters=${filters}&includeParentFilter=true`
    },
  },
  methods: {
    openWoCreation() {
      let { details } = this
      let query = {
        resource: details.id,
        resourceLabel: details.name,
        resourceResourceType: 2,
      }
      let { siteId } = details
      if (!isEmpty(siteId)) {
        query.siteId = siteId
      }

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule('workorder', pageTypes.CREATE) || {}

        name && this.$router.push({ name, query })
      } else {
        this.$router.push({
          path: `/app/wo/create`,
          query,
        })
      }
    },
    // goToLink(path) {
    //   this.$router.push({ path })
    // },
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
