<template>
  <div class="setup-container" :class="[fcSetupSidebarHide]">
    <div class="setup-home-layout" v-if="isHomePage">
      <div class="setup-home-title">{{ $t('setup.setup.settings') }}</div>
      <div class="setup-home-group-container">
        <div
          class="setup-home-groups"
          v-for="(group, index) in setupMenu"
          :key="index"
        >
          <div class="setup-home-group-title">{{ group.label }}</div>
          <template v-for="(tab, index) in group.menu">
            <div
              :key="index"
              @click="checkAndRedirectToTab(tab, group.path)"
              class="setup-home-group-list"
            >
              {{ tab.label }}
            </div>
          </template>
        </div>
      </div>
    </div>
    <div v-else class="height-100">
      <div class="setup-layout">
        <div v-if="canShowSetupSideBar" class="setup-sidebar-container">
          <div class="setup-sidebar-title">
            <router-link :to="{ name: 'setup' }">
              {{ $t('setup.setup.settings') }}
            </router-link>
          </div>
          <div v-for="(group, index) in setupMenu" :key="index">
            <div
              class="setup-sidebar-group-title"
              :class="{ active: activeGroup === group.path }"
              @click="activeGroup = group.path"
            >
              {{ group.label }}
            </div>
            <transition name="slidedown" v-if="activeGroup === group.path">
              <div>
                <div
                  v-for="(tab, index) in group.menu"
                  :key="index"
                  @click="goToTab(tab, group.path)"
                  :class="[
                    'setup-sidebar-menu-list',
                    { active: isTabActive(tab.path, group.path) },
                  ]"
                >
                  {{ tab.label }}
                </div>
              </div>
            </transition>
          </div>
        </div>
        <div
          :class="[
            'fc-setup-content scrollable setup-grey-bg setup-right-container setup-tab-layout',
            backgroundClass,
            fcSetupHome,
          ]"
        >
          <div class="height100">
            <router-view></router-view>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import SetupMenu from './setupmenu'
import { mapState, mapGetters } from 'vuex'
import { isObject, isEmpty } from '@facilio/utils/validation'
import { tabGroup } from '../../components/home/defaultapp-groups.js'
import { isWebTabsEnabled, getApp } from '@facilio/router'

export default {
  data() {
    return {
      menu: tabGroup.map(m => ({ ...m, name: this.$t(m.name) })),
      openGroup: null,
    }
  },
  created() {
    this.setupPermissionCheck()
    let product = {
      code: 'setup',
      label: 'Setup',
      path: '/app/setup',
      modules: [],
    }

    this.$store.dispatch('switchProduct', product)
    this.init()
  },
  computed: {
    ...mapState(['canShowSetupSideBar']),
    ...mapState({ account: state => state.account }),
    ...mapGetters('automationSetup', ['getAutomationModulesList']),
    appName() {
      return window.location.pathname.slice(1).split('/')[0]
    },
    activeGroup: {
      get() {
        let { path } = this.$route || {}
        let groupPath = path.split('setup')[1].split('/')[1]

        return this.openGroup || groupPath
      },
      set(value) {
        this.openGroup = value
      },
    },
    isHomePage() {
      let { path } = this.$route
      return path.endsWith('setup/home')
    },
    isDemoMode() {
      return window.demoMode
    },
    currentModule() {
      let automationModulesList = this.getAutomationModulesList()
      let { $route } = this
      let { params } = $route || {}
      let { moduleName } = params || {}

      if (!isEmpty(moduleName)) {
        let selectedModule = automationModulesList.find(
          m => m.name === moduleName
        )
        return selectedModule ? selectedModule : { name: moduleName }
      }

      return isEmpty(automationModulesList) ? {} : automationModulesList[0]
    },
    backgroundClass() {
      let { $route } = this
      return {
        'setup-grey-white': [
          'general/companyprofile',
          'general/notification',
          'general/portal/samldetails',
          'controller/commissioning',
        ].some(path => $route.path.includes(path)),
      }
    },
    fcSetupHome() {
      let { $route } = this
      return {
        'fc-setup-page': [
          'automations/variables',
          'resource/users',
          'workordersettings/auditlog',
          'setup/customization/userscopes/list',
        ].some(path => $route.path.includes(path)),
      }
    },
    fcSetupSidebarHide() {
      let { $route } = this
      return {
        'fc-setup-sidebar-hide': [
          'general/portal/summary',
          'setup/customization/apps/summary',
          'developerspace/apisetup',
        ].some(path => $route.path.includes(path)),
      }
    },
    setupMenu() {
      let { $helpers, currentModule } = this
      let { name: moduleName } = currentModule || {}
      let accessibleMenu = SetupMenu.map(groupList => {
        let { hideMenu = [] } = groupList || {}
        let isGroupEnabled =
          !groupList.menuName || $helpers.isLicenseEnabled(groupList.menuName)
        let canHideGroup =
          !isEmpty(hideMenu) &&
          hideMenu.some(license => $helpers.isLicenseEnabled(license))

        if (!isGroupEnabled || canHideGroup) return null
        else {
          let tabs = groupList.menu
            .filter(tab => this.checkHasPermission(tab))
            .map(tab => {
              let { is_automation_tab, path } = tab
              if (is_automation_tab && isObject(path)) {
                path.params = { moduleName }
              }

              return tab
            })

          if (isEmpty(tabs)) return null
          return { ...groupList, menu: tabs }
        }
      }).filter(groupList => !isEmpty(groupList))

      return accessibleMenu
    },
    canShowSettings() {
      if (isWebTabsEnabled()) {
        let { user } = this.account || {}
        let { role } = user || {}

        if (
          this.$helpers.isLicenseEnabled('NEW_SETUP') &&
          getApp().hasSetupPermission
        ) {
          return true
        }

        if (role) return role.isPrevileged
        else return false
      } else {
        let { settingsTab, $hasPermission } = this
        let { permission } = settingsTab || {}

        return (permission || []).some(permissionKey =>
          $hasPermission(permissionKey)
        )
      }
    },
    settingsTab() {
      return this.menu.find(m => m.isSetupTab) || {}
    },
  },
  watch: {
    setupMenu: {
      handler(menuGroup = []) {
        let isAutomationEnabled = menuGroup.some(group =>
          ['Automation', 'Automation Plus', 'Process'].includes(group.label)
        )

        if (isAutomationEnabled) {
          this.$store.dispatch('automationSetup/fetchAutomationModule')
        }
      },
      immediate: true,
    },
    isHomePage(value) {
      if (value) {
        this.openGroup = null
      }
    },
  },
  methods: {
    init() {
      if (!this.isHomePage) {
        let { appName } = this
        let { path } = this.$route || {}

        this.setupMenu.forEach(menuGroup => {
          if (path.includes(menuGroup.path)) {
            let menuGroupPath = `/${appName}/setup/${menuGroup.path}`

            if (path === menuGroupPath) {
              this.goToTab(menuGroup.menu[0])
            }
          }
        })
      }
    },
    checkAndRedirectToTab(tab, groupPath) {
      if (this.isDemoMode) {
        this.$message({
          message: this.$t('setup.setup.disabled_for_account'),
          type: 'warning',
        })
      } else {
        this.goToTab(tab, groupPath)
      }
    },
    goToTab(tab, groupPath) {
      let { appName } = this
      let { path } = tab
      let routerPath

      if (isObject(path)) {
        routerPath = {
          ...path,
          params: { ...path.params, app: appName },
        }
      } else {
        routerPath = {
          path: `/${appName}/setup/${groupPath}/${path}`,
        }
      }

      this.$router
        .push(routerPath)
        .then(() => (this.openGroup = groupPath))
        .catch(() => {})
    },
    isTabActive(tabPath, groupPath) {
      let { path } = this.$route || {}

      if (isObject(tabPath)) {
        let tabRoute = this.$router.resolve(tabPath).href

        return path === tabRoute
      } else {
        return path.includes(`${groupPath}/${tabPath}`)
      }
    },
    checkHasPermission(group) {
      let { $hasPermission, $helpers } = this
      let hasPerm =
        isEmpty(group.permission) || $hasPermission(group.permission)
      let hasLicence =
        isEmpty(group.feature_license) ||
        group.feature_license.some(feature_license =>
          $helpers.isLicenseEnabled(feature_license)
        )

      let canHide =
        group.hide_if_license &&
        group.hide_if_license.some(license =>
          $helpers.isLicenseEnabled(license)
        )
      let canShowIfOrg =
        !group.show_if_org ||
        (group.show_if_org && group.show_if_org.includes(this.$org.id))

      return hasPerm && hasLicence && !canHide && canShowIfOrg
    },

    setupPermissionCheck() {
      if (!this.canShowSettings) {
        this.$router.replace({
          path: '/error/nopermission',
        })
      }
    },
  },
}
</script>

<style lang="scss">
.setup-container {
  height: 100%;
  overflow-y: scroll;

  .setup-home-layout {
    margin: 20px 40px 40px;
  }

  .setup-home-title {
    color: #333333;
    font-size: 18px;
    letter-spacing: 0.7px;
    margin-bottom: 20px;
  }

  .readingOverflow {
    overflow-y: hidden !important;
  }

  .setup-home-group-container {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    grid-auto-rows: 1fr;
    grid-gap: 20px;
  }

  .setup-home-groups {
    background: #fff;
    box-shadow: 0 2px 4px 0 rgba(232, 232, 232, 0.5);
    border: solid 1px #e8ebed;
    padding: 30px;
    line-height: 20px;
  }

  .setup-home-group-title {
    font-size: 11px;
    letter-spacing: 0.8px;
    color: var(--fc-theme-color);
    text-transform: uppercase;
    padding-bottom: 10px;
    font-weight: 700;
  }

  .setup-home-group-list {
    line-height: 35px;
    color: #333;
    cursor: pointer;
    letter-spacing: 0.6px;
    font-weight: 400;
  }

  .setup-home-group-list:hover {
    opacity: 0.7;
  }

  .setup-layout {
    display: flex;
    flex-direction: row;
    height: 100%;
    overflow: hidden;
  }

  .setup-tab-layout {
    height: 100%;
    flex: auto;
    // padding-bottom: 30px;
    box-shadow: 0 2px 7px 0 rgba(191, 191, 191, 0.5);
  }

  .setup-sidebar-container {
    width: 22%;
    background-color: #fff;
    border-left: 1px solid #e8e8e8;
    border-right: 1px solid #e7e7e7;
    overflow: scroll;
    flex: none;
  }

  .setup-sidebar-title {
    opacity: 0.5;
    font-size: 18px;
    letter-spacing: 0.7px;
    margin: 25px 0px 5px 45px;

    a {
      color: rgb(0, 0, 0);
    }
  }

  .setup-sidebar-group-title {
    font-size: 11px;
    font-weight: 500;
    letter-spacing: 0.9px;
    color: #2f2e49;
    text-transform: uppercase;
    padding: 10px 0px;
    cursor: pointer;
    margin-left: 45px;
  }

  .setup-sidebar-group-title.active {
    color: #ef508f;
    font-weight: 700;
  }

  .setup-sidebar-menu-list {
    padding: 10px 15px 10px 60px;
    color: #555;
    font-size: 14px;
    border-left: 3px solid transparent;
    letter-spacing: 0.2px;
    text-transform: capitalize;
    cursor: pointer;
  }

  .setup-sidebar-menu-list.active {
    border-left: 3px solid #ef508f;
    background: #f3f4f7;
  }

  .setup-sidebar-menu-list:hover {
    border-left: 3px solid transparent;
    background: #f3f4f7;
    opacity: 0.7;
    color: #555;
  }

  .setup-grey-bg {
    background-color: #f8f9fa;
  }

  .setup-grey-white {
    background-color: #fff;
  }
  .fc-setup-page {
    overflow-y: hidden;
  }
}
.fc-setup-sidebar-hide .setup-sidebar-container {
  display: none !important;
}
</style>
