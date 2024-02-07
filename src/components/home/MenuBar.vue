<template>
  <ul class="header-tabs pull-right pL0 pR0">
    <li class="pL0 p0 building-hover p10">
      <SwitchValue v-if="showSwitchVariable"> </SwitchValue>
      <SiteSwitch
        v-else
        :currentSite="currentSite"
        :sites="sites"
        :setCurrentSite="setCurrentSite"
      >
      </SiteSwitch>
    </li>
    <li class="custom-top-menu">
      <portal-target name="custom-topbar-menu"></portal-target>
    </li>
    <router-link
      tag="li"
      v-if="newAlarms > 0"
      @click.native="resetAlarmUnseen"
      :to="{ path: '/app/fa/faults/active' }"
      style="padding: 16px 12px"
    >
      <el-badge :value="newAlarms" :max="99" class="notifications-unread-new">
        <a>
          <i class="material-icons alarm-icon">{{ $t('panel.panel.alarm') }}</i>
        </a>
      </el-badge>
    </router-link>

    <li style="padding: 10px 10px" v-if="canShowQuickAddMenu">
      <el-popover
        v-model="addbtn"
        placement="bottom-end"
        width="250"
        trigger="click"
        popper-class="fc-maintenance-popover p0 mT0"
        :visible-arrow="false"
        :tabindex="-1"
      >
        <div
          slot="reference"
          class="blue-round-plus"
          v-bind:class="{ active: addbtn }"
          role="img"
          aria-label="Add Icon"
        >
          <i class="el-icon-plus"></i>
        </div>
        <div class="notifications-container">
          <div class="fc-grey2 f11 bold mL30 mR30 mT20 pB10">
            {{ $t('panel.panel.maintenance') }}
          </div>
          <div class="flex-middle maintenace-menu-list" @click="redirectToWO()">
            <InlineSvg
              src="svgs/monitor"
              iconClass="icon icon-lg mR10 maintenace-menu-hover-icon"
            ></InlineSvg>
            {{ $t('panel.panel.work_order') }}
          </div>
          <div
            class="flex-middle maintenace-menu-list"
            @click="redirectToIR()"
            v-if="$helpers.isLicenseEnabled('INVENTORY')"
          >
            <InlineSvg
              src="svgs/monitor"
              iconClass="icon icon-lg mR10 maintenace-menu-hover-icon"
            ></InlineSvg>
            {{ $t('panel.panel.inventory_req') }}
          </div>
        </div>
      </el-popover>
    </li>

    <li style="padding: 16px 10px" v-if="canShowNotifications">
      <NotificationPopup ref="notification" />
    </li>

    <li style="padding: 10px 10px 12px" @click.stop="toggleProfilePanel">
      <avatar
        size="md"
        :user="account.user"
        :style="{
          border: '1px solid #666580',
          'border-radius': '16px',
        }"
        color="#473a9e"
      ></avatar>
    </li>
  </ul>
</template>
<script>
import Avatar from '@/Avatar'
import NotificationPopup from '@/notifications/NotificationPopup'
import {
  findRouteForModule,
  getApp,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'
import { mapGetters } from 'vuex'
import SiteSwitch from './SiteSwitch'
import SwitchValue from 'src/components/home/SwitchValue.vue'

export default {
  props: [
    'OfflineOnly',
    'currentSite',
    'sites',
    'setCurrentSite',
    'newAlarms',
    'resetAlarmUnseen',
    'account',
    'toggleProfilePanel',
  ],
  components: {
    Avatar,
    NotificationPopup,
    SiteSwitch,
    SwitchValue,
  },
  data() {
    return {
      addbtn: false,
    }
  },
  computed: {
    ...mapGetters('webtabs', ['isAppPrefEnabled']),

    canShowQuickAddMenu() {
      if (isWebTabsEnabled()) {
        return this.isAppPrefEnabled('canShowQuickCreate')
      } else {
        return !this.$helpers.isEtisalat()
      }
    },
    canShowNotifications() {
      if (isWebTabsEnabled()) {
        return this.isAppPrefEnabled('canShowNotifications')
      } else {
        return !this.$helpers.isEtisalat()
      }
    },
    canShowSites() {
      const canShow =
        this.$store.state.site &&
        this.$store.state.site.length > 1 &&
        !this.$route.path.startsWith('/app/setup')
      if (isWebTabsEnabled()) {
        return this.isAppPrefEnabled('canShowSitesSwitch') && canShow
      } else {
        return canShow
      }
    },

    showSwitchVariable() {
      return this.$helpers.isLicenseEnabled('SCOPE_VARIABLE')
    },
  },
  methods: {
    redirectToWO() {
      this.addbtn = false

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule('workorder', pageTypes.CREATE) || {}
        if (name) {
          this.$router.push({ name })
        }
      } else {
        this.$router.push({ path: '/app/wo/create' })
      }
    },
    redirectToIR() {
      this.addbtn = false
      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule('inventoryrequest', pageTypes.CREATE) || {}
        if (name) {
          this.$router.push({ name })
        }
      } else {
        this.$router.push({
          name: 'inventoryrequest-create',
          params: {
            moduleName: 'inventoryrequest',
          },
        })
      }
    },
    redirectToServiceCatalog() {
      let params = {}

      if (isWebTabsEnabled()) {
        let { linkName: appName } = getApp()
        params = { app: appName }
      } else {
        params = { app: 'app' }
      }

      this.addbtn = false
      this.$router.push({ name: 'service-catalog-list', params })
    },
  },
}
</script>
<style>
.custom-top-menu {
  padding: 6px 10px !important;
  font-size: 25px !important;
}
.custom-top-menu i {
  opacity: 0.8;
}
.custom-top-menu i:hover {
  opacity: 1;
}
</style>
