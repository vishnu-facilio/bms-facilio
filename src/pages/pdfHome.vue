<template>
  <div>
    <div v-if="serverNotReachable">
      <server-not-reachable></server-not-reachable>
    </div>
    <div v-else-if="!hasMetaLoaded">
      <loader></loader>
    </div>
    <div
      v-else
      class="newhome"
      v-bind:class="{ 'tv-screen': isTvMode, normal: !isTvMode }"
      ref="productLayout"
    >
      <div v-if="serverNotReachable">
        <server-not-reachable></server-not-reachable>
      </div>

      <template v-else>
        <FeatureNavBanner
          :showCollapseData="showCollapseData"
          :bannerListData="bannerListData"
          @closeCollapseData="closeCollapseData"
          @openCollapseData="openCollapseData"
          :showCloseBanner="showCloseBanner"
        ></FeatureNavBanner>
        <div class="">
          <el-alert
            v-if="$account.user.proxy"
            :title="proxyBanner"
            type="error"
            :closable="false"
            effect="dark"
            class="fc-login-alert"
          >
          </el-alert>
        </div>
        <div :class="getCollapseClass()">
          <div
            ref="layout"
            view="HHr Lpr fFf"
            class="fc-main layout"
            :class="{
              themeClass,
            }"
            :style="getStyle()"
          >
            <div class="layout-page-container transition-generic">
              <main class="layout-page">
                <q-toolbar
                  slot="header"
                  class="fc-toolbar toolbar"
                  :class="{
                    'fc-honeywell-toolbar':
                      isHoneywell &&
                      account.data.orgInfo.toolbar_theme === 'honeywell',
                  }"
                >
                  <div
                    v-if="canShowAppLauncher"
                    id="app-launcher"
                    class="app-launcher"
                    tabindex="0"
                    @keypress="e => (dialogVisible = e.key === 'Enter')"
                  >
                    <img
                      src="~assets/google-menu.svg"
                      width="20"
                      height="20"
                      class="pointer"
                      @click="dialogVisible = true"
                    />
                  </div>
                  <router-link
                    to="/"
                    class="pointer"
                    :class="{ customlogolink: isBuildingsTalk }"
                    v-if="account.user.email !== 'tjacob@nxn.ae'"
                  >
                    <ProductLogo
                      :isHoneywell="isHoneywell"
                      :isBuildingsTalk="isBuildingsTalk"
                      :isMoro="isMoro"
                    ></ProductLogo>
                  </router-link>
                  <div v-else style="margin-left:10px;"></div>

                  <div v-if="isBuildingsTalk" class="buildings-talk-text">
                    <span>buildings</span>
                    <span style="color: #3eb359;">talk</span>
                  </div>
                  <div class="fc-header-container">
                    <modules-header
                      v-if="!isTabsEnabled"
                      :modulesList="modules"
                    ></modules-header>

                    <NavBar v-else></NavBar>

                    <MenuBar
                      :OfflineOnly="OfflineOnly"
                      :currentSite="currentSite"
                      :sites="site"
                      :setCurrentSite="setCurrentSite"
                      :newAlarms="newAlarms"
                      :resetAlarmUnseen="resetAlarmUnseen"
                      :account="account"
                      :toggleProfilePanel="
                        () => (profileContaioner = !profileContaioner)
                      "
                    ></MenuBar>
                  </div>
                </q-toolbar>

                <sidebar></sidebar>

                <portal-target name="app-banner" :slim="true"></portal-target>
                <router-view
                  class="pL60"
                  v-bind:class="{ ' tv-screen-layout': isTvMode }"
                  :style="getScreenHeight()"
                />

                <ProfilePanel
                  v-if="profileContaioner"
                  :close="() => (profileContaioner = false)"
                  :reportIssue="toggleReportIssue"
                  :account="account"
                  :isTvMode="isTvMode"
                  :switchAccount="switchAccount"
                  :switchScreen="switchScreen"
                  :switchTheme="switchTheme"
                  :orgs="orgs"
                  :screenTheame="screenTheame"
                  :logout="logout"
                ></ProfilePanel>
              </main>
            </div>
          </div>
        </div>
      </template>

      <AppLauncher
        v-if="dialogVisible"
        :close="() => (dialogVisible = false)"
      ></AppLauncher>

      <NewApp
        v-if="dialogFormVisible"
        :closeDialog="() => (dialogFormVisible = false)"
      ></NewApp>

      <ConnectedAppDialer></ConnectedAppDialer>

      <ReportIssue
        v-if="showReportIssuePopup"
        :onClose="() => toggleReportIssue(false)"
      ></ReportIssue>
      <Shortcuts />
    </div>
    <popup-view ref="dashboardPopupView"></popup-view>
    <quick-add-record ref="quickAddRecord"></quick-add-record>
  </div>
</template>
<script>
import Vue from 'vue'
import home from './HomeMixin.js'
import { mapGetters, mapState } from 'vuex'
import ModulesHeader from '@/ModulesHeader'
import Sidebar from '@/home/Sidebar'
import ProfilePanel from '@/home/ProfilePanel'
import AppLauncher from '@/home/AppLauncher'
import NewApp from '@/home/NewApp'
import ProductLogo from '@/home/ProductLogo'
import MenuBar from '@/home/MenuBar'
import NavBar from '@/home/NavBar'
import ReportIssue from '@/feedback/ReportIssue'
import { isWebTabsEnabled } from '@facilio/router'
import FeatureNavBanner from '@/home/FeatureReleaseBanner'
import { isEmpty } from '@facilio/utils/validation'
import ConnectedAppDialer from '@/home/ConnectedAppDialer'
import Shortcuts from '@/global/Shortcuts'
import moment from 'moment-timezone'
import { track } from 'src/track.js'

export default {
  mixins: [home],
  components: {
    MenuBar,
    Sidebar,
    ProfilePanel,
    ModulesHeader,
    AppLauncher,
    NewApp,
    ProductLogo,
    NavBar,
    ReportIssue,
    FeatureNavBanner,
    ConnectedAppDialer,
    Shortcuts,
  },
  data() {
    return {
      showReportIssuePopup: false,
      showCollapseData: true,
      showCloseBanner: true,
      keyPressed: [],
      counterPrefix: null,
      counter: null,
    }
  },
  async created() {
    await this.$store.dispatch('getCurrentAccount').then(() => {
      let { path } = this.$route || {}
      track(path, this.account)
    })
    await this.$store.dispatch('getFeatureLicenses')
    await this.initializeMeta()
    this.$store.dispatch('loadSite')
    this.$store.dispatch('loadOrgs')

    if (isWebTabsEnabled()) {
      document.addEventListener('keydown', this.keyDownHandler)
      document.addEventListener('keyup', this.keyUpHandler)
    }
    if (this.$account.sessionEndTime) {
      this.counterPrefix = `${this.$account.user.proxy} will be viewing as ${this.$account.user.userName} for `
      this.startTimer(this.$account.sessionEndTime)
    }
  },
  mounted() {
    try {
      if (this.cssVariables) {
        for (let cssvar in this.cssVariables) {
          if (document.documentElement && document.documentElement.style) {
            document.documentElement.style.setProperty(
              cssvar,
              this.cssVariables[cssvar]
            )
          }
        }
      }
      // eslint-disable-next-line no-empty
    } catch (err) {}
  },
  beforeDestroy() {
    if (isWebTabsEnabled()) {
      document.removeEventListener('keydown', this.keyDownHandler)
      document.removeEventListener('keyup', this.keyUpHandler)
    }
  },
  computed: {
    ...mapState({
      account: state => state.account,
    }),
    ...mapGetters('webtabs', ['isAppPrefEnabled']),
    isTabsEnabled() {
      return isWebTabsEnabled()
    },
    proxyBanner() {
      return this.counterPrefix + (this.counter || '---')
    },
    canShowAppLauncher() {
      const isDevMode = process.env.NODE_ENV === 'development'
      let isEnabled = false

      if (this.isTabsEnabled) {
        isEnabled = this.isAppPrefEnabled('canShowAppLauncher')
      } else {
        let { $org = {} } = this
        isEnabled =
          [321, 155, 173, 78, 174, 429, 17, 566, 569, 656, 486].includes(
            $org.id
          ) && !this.$helpers.isEtisalat()
      }

      return isDevMode || isEnabled
    },
  },
  watch: {
    account: function() {
      if (this.$refs['dashboardPopupView']) {
        Vue.prototype.$popupView = this.$refs['dashboardPopupView']

        if (this.$refs['quickAddRecord']) {
          Vue.prototype.$quickAdd = this.$refs['quickAddRecord']
        }
      } else {
        this.$nextTick(() => {
          Vue.prototype.$popupView = this.$refs['dashboardPopupView']

          if (this.$refs['quickAddRecord']) {
            Vue.prototype.$quickAdd = this.$refs['quickAddRecord']
          }
        })
      }
    },
  },
  methods: {
    startTimer(endTime) {
      let startTime = Date.now()
      if (startTime > endTime) {
        return
      }
      let duration = endTime - startTime
      let interval = 1000
      setInterval(() => {
        duration = moment.duration(duration - interval, 'milliseconds')
        this.counter = moment
          .utc(duration.as('milliseconds'))
          .format('HH:mm:ss')
      }, interval)
    },
    toggleReportIssue(val = false) {
      this.showReportIssuePopup = val
    },
    closeCollapseData() {
      this.showCollapseData = false
    },
    openCollapseData() {
      this.showCollapseData = true
    },
    getCollapseClass() {
      if (this.bannerListData.type === 1) {
        return {
          bannerOpenActive: this.showCloseBanner,
          bannerCloseActive: !this.showCloseBanner,
        }
      } else if (this.bannerListData.type === 2) {
        return {
          activeBanner: this.showCollapseData,
          inActive: !this.showCollapseData,
        }
      } else if (this.$account.user.proxy) {
        return 'activeBanner'
      }
    },
    keyDownHandler(e) {
      this.keyPressed.push(e.key)
    },
    keyUpHandler() {
      if (!isEmpty(this.keyPressed)) {
        let [shiftKey, aKey, pKey] = this.keyPressed || []

        if (shiftKey === 'Shift' && aKey === 'A' && pKey === 'P') {
          document.getElementById('app-launcher').focus()
        }
        this.keyPressed = []
      }
    },
  },
}
</script>
<style lang="scss">
.fc-maintenance-popover {
  max-height: fit-content !important;
  padding-bottom: 10px;
}
.notifications-unread-new {
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 100%;
  width: 30px;
  height: 30px;
  line-height: 26px;
  background-color: #404060;
  text-align: center;
  color: #fff;
  position: relative;
  top: -7px;
  .fa-bell-o {
    color: #fff;
  }
}
.building-hover {
  margin-top: 7px;
}
.building-hover:hover {
  border-radius: 17px;
}
.blue-round-plus {
  width: 30px;
  height: 30px;
  border-radius: 15px;
  background: #39b2c2;
  line-height: 32px;
  text-align: center;
  .el-icon-plus {
    color: #ffffff;
    font-size: 16px;
    font-weight: bolder;
    transition: color 0.2s ease, background-color 0.2s ease, transform 0.3s ease;
  }
}
.blue-round-plus.active .el-icon-plus {
  transform: rotate(46deg);
  cursor: pointer;
  box-shadow: none;
  transition: color 0.2s ease, background-color 0.2s ease, transform 0.3s ease;
}
.maintenace-menu-list {
  padding-left: 32px;
  padding-right: 30px;
  padding-top: 10px;
  padding-bottom: 10px;
  color: #324056;
  font-size: 14px;
  &:hover {
    background: #ff3184;
    cursor: pointer;
    font-weight: 500;
    color: #fff;
  }
  &:hover .maintenace-menu-hover-icon {
    fill: #ffffff;
  }
  .maintenace-menu-hover-icon {
    fill: #000000;
  }
}
.org-name {
  color: #324056;
  font-weight: 400;
}

.org-name:hover {
  color: #483a9e;
}

.org-name.active {
  color: #483a9e;
  font-weight: 500;
}

.profile-c-icon {
  width: 18px;
}

.fc-home-profile-container .el-button-group > .el-button {
  width: 43px;
  height: 32px;
  padding: 0px;
}

.fc-home-profile-container .el-button-group > .el-button--primary {
  background: #39b2c2;
  border-color: #39b2c2;
}

.profile-container-divider {
  margin-right: 10px;
  width: 1px;
  background: #fff;
  height: 20px;
  margin-left: 10px;
  opacity: 0.2;
}

.layout-page-container {
  background: #f7f8f9;
}

.q-toolbar {
  padding-top: 0px;
  padding-bottom: 0px;
}

.fc-toolbar .fc-logo {
  padding: 0px;
  position: relative;
}

.fc-toolbar .header-tabs {
  list-style: none;
  margin: 0;
  font-size: 15px;
}

.fc-toolbar .header-tabs li {
  float: left;
  padding: 16px;
  cursor: pointer;
}

.fc-toolbar .header-tabs li a {
  text-decoration: none;
  color: #90909a;
}

.fc-toolbar .header-tabs li:hover,
.fc-toolbar .header-tabs li:focus {
  background-color: #25243e;

  & a {
    color: #ffffff;
  }
}

.fc-toolbar .header-tabs li:focus {
  outline: 1px solid #39b2c2 !important;
}

.fc-toolbar .header-tabs li .selection-bar {
  border: 0.1em solid transparent;
  width: 25px;
  margin-top: 4px;
  position: absolute;
}

.fc-toolbar .header-tabs li.active {
  background-color: #25243e;
}

.fc-toolbar .header-tabs li.active a {
  color: #ffffff;
}

.fc-toolbar .header-tabs li.active .selection-bar {
  border: 1px solid #fb3685;
}

.fc-toolbar-right-btns {
  padding: 12px 20px;
  font-size: 20px;
}

.fc-toolbar-right-btns a {
  text-decoration: none;
  color: #90909a !important;
}

.fc-toolbar-right-btns a:hover {
  color: white !important;
}

.fc-toolbar-right-btns a.active {
  color: white !important;
}

.fc-header-container {
  width: 100%;
  max-height: 49px;
}

.fc-toolbar .header-tabs .dropdown-toggle:after {
  display: inline-block;
  width: 0;
  height: 0;
  margin-left: 0.3em;
  vertical-align: middle;
  content: '';
  border-top: 0.3em solid;
  border-right: 0.3em solid transparent;
  border-left: 0.3em solid transparent;
}

.fc-profile-popover {
  top: 49.5px !important;
  box-shadow: 0 5px 10px rgba(0, 0, 0, 0.12) !important;
}

.fc-profile-popover .q-list {
  padding: 3px 0;
}

.fc-profile-popover .q-item {
  font-size: 14px;
}

aside.layout-aside.layout-aside-left.scroll.on-layout.transition-generic {
  position: fixed;
  top: 50px !important;
  width: 50px;
  z-index: 10;
}

.sd-fire,
.sd-fire-active {
  height: 20px;
}

.fc-sidebar-icon {
  font-size: 20px;
  color: gray;
}

.fc-side-link.q-item.active > .fc-sidebar-icon,
.fc-side-link.q-item.active > .fc-sidebar-icon {
  color: white;
}

.fc-side-link {
  padding: 15px;
}

.fc-side-link.q-item.active {
  background-color: rgba(254, 55, 135, 0.71);
}

.q-toolbar.row.no-wrap.items-center.relative-position.q-toolbar-normal.fc-toolbar.toolbar {
  background: var(--fc-toolbar-bg);
}

.fc-fire-menu.active > .sd-fire-active {
  display: block !important;
}

.fc-fire-menu.active > .sd-fire {
  display: none !important;
}

ul.product-menu {
  list-style: none;
  padding: 2px 0 2px 25px;
  float: left;
}

ul.product-menu li {
  float: left;
  cursor: pointer;
}

ul.product-menu li a {
  color: #90909a;
  font-size: 13px;
  padding: 17px;
  border: 1px solid transparent;
}

ul.product-menu li .sh-selection-bar {
  border: 1px solid transparent;
  width: 25px;
  margin-top: 5px;
  position: absolute;
  margin-left: 17px;
}

ul.product-menu li.active .sh-selection-bar {
  border-right: 0px solid #e0e0e0;
  border-left: 0px solid #e0e0e0;
  border-color: #39b2c2;
}

ul.product-menu li.active a,
ul.product-menu li a:hover,
ul.product-menu li a:focus {
  color: #fff;
  background-color: #25243e;
}

ul.product-menu li a:focus {
  border: 1px solid #39b2c2;
}

ul.product-menu li:hover a {
  color: #fff;
}

ul.product-menu li.active a {
  color: #fff;
  font-weight: 500;
}

.fc-maintenance-popover {
  width: 250px;
  max-height: 283px;
  top: 49.5px !important;
  max-height: 490px;
  min-height: 150px;
  border-radius: 0;
  background-color: #fff;
  box-shadow: 0 8px 22px 0 rgba(234, 234, 234, 0.6);
  border: 1px solid #eaecf1;
  border-top: none;
}

.notifications-container .notifications-header {
  width: 100%;
  padding: 15px 14px;
  display: inline-block;
  border-bottom: 1px solid #e8e8e8;
  font-size: 12px;
  font-weight: bold;
  letter-spacing: 1.1px;
  text-align: left;
  color: #333333;
  text-transform: uppercase;
}

.notifications-container {
  border-top: transparent !important;
}
.notifications-unread-new .el-badge__content {
  font-size: 11px;
  padding: 0 4px;
  border: none;
}

.fc-custom-logo {
  max-height: 35px;
}

.customlogolink {
  width: 40px;
  overflow: hidden;
}

.switch-icon-dropdown {
  font-size: 12px;
  margin-left: 2px;
  color: #2e398e;
  position: relative;
  top: -2px;
}

.switchOrgTitle {
  padding: 2px 16px;
  font-size: 11px;
  color: #909399;
  border-bottom: 1px solid #fafafa;
  line-height: 30px;
  width: 100%;
}

.tv-select.active {
  font-weight: 600;
}

@keyframes blinker {
  from {
    opacity: 1;
  }

  to {
    opacity: 0;
  }
}

.notificationOnline {
  text-decoration: notificationOnline;
  animation-name: blinker;
  animation-duration: 1.4s;
  animation-iteration-count: infinite;
  animation-timing-function: ease-in-out;
  animation-direction: alternate;
}

.item {
  margin: 4px;
}

.fc-honeywell-toolbar {
  background: #fe0b0ac4 !important;
}

.fc-honeywell-toolbar ul.product-menu li a {
  color: #fafafa !important;
}

.fc-honeywell-toolbar .header-tabs li a {
  color: #f4f4f4 !important;
}

.fc-honeywell-toolbar ul.product-menu li.active .sh-selection-bar {
  border-color: #fff !important;
}

.fc-honeywell-toolbar ul.product-menu li.active a,
.fc-honeywell-toolbar ul.product-menu li a:hover,
.fc-honeywell-toolbar .header-tabs li:hover {
  background: #fe0c0b !important;
}

.fc-honeywell-toolbar .fc-avatar-md {
  background: #fff !important;
  color: #fe0c0b !important;
}

.header__current__site__display.new-home-display {
  color: #fff;
  font-size: 12px;
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  padding-top: 0;
  vertical-align: middle;
  padding-right: 0;
  text-align: right;
}

.newhome .header__current__site__switch {
  display: flex;
  align-items: baseline;
  flex-wrap: nowrap;
  background-color: #24233e;
  border-radius: 17px;
  text-align: center;
  padding: 5px 21px;
  line-height: 20px;
}

.newhome .header__current__site__switch:hover {
  border-radius: 17px;
}

.buildings-talk-text {
  margin-left: 10px;
  padding-top: 5px;
  position: relative;
  top: 0px;
  font-size: 20px;
  font-family: Panton-Regular, Open Sans, Helvetica, Arial, sans-serif !important;
  letter-spacing: 0.4px;
}
.app-launcher {
  padding: 13px;
  border-right: 1px solid transparent;
  border-left: 1px solid transparent;

  &:focus {
    background: #25243e;
    border-right: 1px solid #39b2c2;
    border-left: 1px solid #39b2c2;
  }
}

.fc-login-alert {
  width: 100%;
  height: 25px;
  position: fixed;
  top: 0;
  font-size: 12px;
  z-index: 500;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 500;
  border-radius: 0;
}

@media print {
  .fc-main {
    .pL60 {
      padding-left: 0 !important;
    }
  }
  .q-toolbar {
    display: none !important;
  }
}
</style>
