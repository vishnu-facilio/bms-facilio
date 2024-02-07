<template>
  <div>
    <el-aside width="250px" class="fc-agent-sidebar">
      <div class="fc-agent-sidebar-inner">
        <ul class="fc-v1-agent-sidebar-list mT0 mB0">
          <router-link
            v-for="tab in filteredMenu"
            :key="tab.id"
            :to="`/${appName}/${tabGroupRoute}/${tab.route}`"
            class="pointer fc-agent-black-15 pT10 pB10 pL30 pR30"
            tag="li"
          >
            <InlineSvg
              :src="menu[tab.route].icon"
              :iconClass="menu[tab.route].iconClass"
            ></InlineSvg>
            {{ tab.name }}
          </router-link>
        </ul>
      </div>
    </el-aside>
  </div>
</template>
<script>
import { isNullOrUndefined } from '@facilio/utils/validation'
import { mapState, mapGetters } from 'vuex'
import { getApp } from '@facilio/router'
import Vue from 'vue'

import sortBy from 'lodash/sortBy'
export default {
  props: ['agentSidebar'],
  data() {
    return {
      menu: {
        overview: {
          icon: 'svgs/agent/overview',
          iconClass: 'icon icon-md vertical-sub mR10',
        },
        list: {
          icon: 'svgs/agent/agent',
          iconClass: 'icon icon-md vertical-sub mR10',
        },
        controllers: {
          icon: 'svgs/agent/controller',
          iconClass: 'icon icon-md vertical-sub mR10',
        },
        points: {
          icon: 'svgs/agent/points',
          iconClass: 'icon icon-md vertical-sub mR10',
        },
        commissioning: {
          icon: 'svgs/agent/commissioning',
          iconClass: 'icon icon-md vertical-sub mR10',
        },
        alarmmapping: {
          icon: 'svgs/agent/alarm-mapping',
          iconClass: 'icon icon-md vertical-sub mR10',
        },
        alarm: {
          icon: 'svgs/alarm',
          iconClass: 'icon icon-md vertical-sub mR10',
        },
        notification: {
          icon: 'svgs/agent/alarm-rules',
          iconClass: 'icon icon-md vertical-sub mR10',
        },
        metrics: {
          icon: 'svgs/agent/metrics',
          iconClass: 'icon icon-md vertical-sub mR10',
        },
        log: {
          icon: 'svgs/agent/auditlog',
          iconClass: 'icon icon-md vertical-sub mR10',
        },
        data: {
          icon: 'svgs/agent/agentData',
          iconClass: 'icon icon-md vertical-sub mR10',
        },
        trigger: {
          icon: 'svgs/agent/trigger',
          iconClass: 'icon icon-md vertical-sub mR10',
        },
        automation: {
          icon: 'svgs/agent/alarm-rules',
          iconClass: 'icon icon-md vertical-sub mR10',
        },
        logs: {
          icon: 'svgs/agent/agent-logs',
          iconClass: 'icon icon-md vertical-sub mR10',
        },
      },
    }
  },
  computed: {
    ...mapState('webtabs', ['selectedTabGroup']),
    ...mapGetters('webtabs', ['tabHasPermission']),
    ...mapGetters('webtabs', ['getTabGroups']),
    filteredMenu() {
      let [group] = sortBy(this.getTabGroups(), ['order']) || []

      let tabs = (group || {}).webTabs.filter(tab => {
        if (tab.route === 'device') return false
        let featureLicense = tab.featureLicense
        if (isNullOrUndefined(featureLicense) || featureLicense === 0) {
          return true
        } else {
          let licenseInt = Vue.prototype.$account.License
          let andOperatorOnLong = this.$helpers.andOperatorOnLong

          return (
            licenseInt &&
            Boolean(
              andOperatorOnLong(featureLicense, licenseInt) === featureLicense
            )
          )
        }
      })

      return sortBy(tabs, ['order'])
    },
    appName() {
      return getApp().linkName
    },
    tabGroupRoute() {
      let [group] = sortBy(this.getTabGroups(), ['order']) || []
      return group.route
    },
  },
}
</script>
<style lang="scss">
.fc-agent-sidebar {
  .fc-agent-sidebar-inner {
    .router-link-exact-active {
      color: #2e5bff;
      background-color: rgba(46, 91, 255, 0.3);
      color: #2e5bff;
      font-weight: 500;
      border-left: 3px solid #2e5bff !important;
    }
  }
}
</style>
