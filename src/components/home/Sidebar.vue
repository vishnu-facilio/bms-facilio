<template>
  <aside class="fc-layout-aside fc-layout-aside-new fc-wt-sidebar">
    <div class="fc-sidebar-list-con">
      <ul class="fc-sidebar d-flex flex-direction-column sidebar-ht">
        <router-link
          tag="li"
          v-for="item in activeTabGroups"
          :key="item.route"
          :to="getTabRoute(item)"
          :class="[isTabActive(item) ? 'active pointer' : 'pointer']"
        >
          <a
            :id="`sidebar-icon-${item.id}`"
            @click="removeFocus"
            class="sidebar-icon fc-icon-hover"
            :class="getFcIconClass(item)"
          >
            <el-tooltip
              effect="dark"
              :content="item.name"
              v-if="!isTabsEnabled"
              placement="right"
            >
              <template v-if="!$validation.isEmpty(item.iconTypeEnum)">
                <fc-icon
                  :name="item.iconTypeEnum"
                  size="20"
                  group="webtabs"
                  class="fc-icon"
                ></fc-icon>
              </template>
              <template v-else>
                <InlineSvg
                  :title="item.name"
                  :aria-label="item.name"
                  :src="getIconPath(item.iconType)"
                  :iconClass="getIconClass(item.iconType)"
                  :tabindex="-1"
                ></InlineSvg>
              </template>
            </el-tooltip>
            <el-popover
              v-else
              placement="right"
              trigger="hover"
              popper-class="fc-popover-p02 sidebar-panel"
              :open-delay="0"
              :disabled="!isTabsEnabled"
              :tabindex="-1"
            >
              <slot :slot="'reference'">
                <template v-if="!$validation.isEmpty(item.iconTypeEnum)">
                  <span class="icon-position">
                    <fc-icon
                      :name="item.iconTypeEnum"
                      size="20"
                      group="webtabs"
                      :color="isIconActive(item)"
                      class="fc-icon"
                    ></fc-icon>
                  </span>
                </template>
                <template v-else>
                  <InlineSvg
                    :src="getIconPath(item.iconType)"
                    :title="item.name"
                    :aria-label="item.name"
                    :iconClass="getIconClass(item.iconType)"
                  ></InlineSvg>
                </template>
              </slot>
              <div class="tabs-header">{{ item.name }}</div>
              <div
                v-for="(tab, index) in getOrderedTabs(item.webTabs)"
                :key="index"
                @click="redirectToTab(tab.id)"
                :class="[
                  'tabs',
                  selectedTab && selectedTab.id === tab.id && 'active',
                ]"
              >
                {{ tab.name }}
              </div>
            </el-popover>
          </a>
        </router-link>

        <li v-if="!$validation.isEmpty(moreTabGroups)" class="more-icon-list">
          <el-popover
            placement="right"
            trigger="hover"
            popper-class="fc-popover-p02 sidebar-panel"
          >
            <slot :slot="'reference'">
              <el-button slot="reference" class="more-icon">
                <i class="el-icon-more sidebar-more-icon"></i>
              </el-button>
            </slot>
            <div
              v-for="group in moreTabGroups"
              :key="`${group.name}`"
              @click="redirectToTabFromGroup(group)"
              class="groups flex-middle"
            >
              <template v-if="!$validation.isEmpty(group.iconTypeEnum)">
                <fc-icon
                  :name="group.iconTypeEnum"
                  size="20"
                  group="webtabs"
                ></fc-icon>
              </template>
              <template v-else>
                <InlineSvg
                  :src="getIconPath(group.iconType)"
                  :title="group.name"
                  :aria-label="group.name"
                  class="pR10"
                  :iconClass="getIconClass(group.iconType)"
                ></InlineSvg>
              </template>
              {{ group.name }}
            </div>
          </el-popover>
        </li>

        <template
          v-if="$helpers.isLicenseEnabled('NEW_SETUP') && isTabsEnabled"
        >
          <router-link
            v-if="permissionSetup"
            tag="li"
            :to="'/setup/' + appLinkName + '/home'"
            class="bottom-15 position-absolute"
          >
            <a
              role="navigation"
              target="_blank"
              :aria-label="$t('setup.setup.settings')"
              id="sidebar-icon-setting"
              @click="removeFocus"
              class="sidebar-icon fc-icon-hover"
            >
              <el-tooltip
                effect="dark"
                :content="$t('setup.setup.settings')"
                placement="right"
              >
                <span class="icon-position">
                  <fc-icon
                    group="default"
                    name="settings"
                    size="20"
                    class="fc-icon"
                  ></fc-icon>
                </span>
              </el-tooltip>
            </a>
          </router-link>
        </template>
        <template v-else-if="canShowSettings">
          <router-link
            tag="li"
            :to="settingsTab.route(appName)"
            class="bottom-15 position-absolute"
          >
            <a
              role="navigation"
              :aria-label="settingsTab.name"
              id="sidebar-icon-setting"
              @click="removeFocus"
              class="sidebar-icon icon-hover"
            >
              <el-tooltip
                effect="dark"
                :content="settingsTab.name"
                placement="right"
              >
                <InlineSvg
                  :title="settingsTab.name"
                  :aria-label="settingsTab.name"
                  src="sidebar/settings-new"
                  iconClass="icon new-icon vertical-baseline icon-lg"
                  :tabindex="-1"
                ></InlineSvg>
              </el-tooltip>
            </a>
          </router-link>
        </template>
      </ul>
    </div>
  </aside>
</template>
<script>
import sortBy from 'lodash/sortBy'
import { mapState, mapGetters } from 'vuex'
import { isEmpty, isUndefined } from '@facilio/utils/validation'
import { findRouteForTab, isWebTabsEnabled, getApp } from '@facilio/router'
import icons from 'newapp/webtab-icons.js'
import Vue from 'vue'
import { tabGroup } from './defaultapp-groups.js'

export default {
  data() {
    return {
      maxActiveSidebarModuleCount: 10,
      menu: tabGroup.map(m => ({ ...m, name: this.$t(m.name) })),
    }
  },
  created() {
    if (isWebTabsEnabled()) {
      document.addEventListener('keydown', this.keyDownHandler)
    }
  },

  beforeDestroy() {
    if (isWebTabsEnabled()) {
      document.removeEventListener('keydown', this.keyDownHandler)
    }
  },
  watch: {
    $route: {
      handler() {
        if (!isWebTabsEnabled()) this.loadDefaultProduct()
      },
      immediate: true,
    },
  },
  computed: {
    ...mapState({
      selectedTabGroup: state => state.webtabs.selectedTabGroup,
      selectedTab: state => state.webtabs.selectedTab,
      account: state => state.account,
    }),
    ...mapGetters('webtabs', ['getTabGroups']),
    tabGroups() {
      let { getTabGroups } = this
      return sortBy(getTabGroups(), ['order']).filter(
        g => g.route !== 'setup' && !isEmpty(g.webTabs)
      )
    },
    isTabsEnabled() {
      return isWebTabsEnabled()
    },
    accessibleMenu() {
      let { menu, $helpers, $hasPermission, $org } = this
      return menu.filter(menuItem => {
        let { license, permission, isSetupTab, enabledOrgId } = menuItem || {}
        let result = !license || $helpers.isLicenseEnabled(license)
        let canShow

        if (!result) {
          canShow = false
        } else if (isUndefined(permission)) {
          canShow = !isEmpty(menuItem)
        } else if (!isSetupTab) {
          canShow = permission.some(permissionKey =>
            $hasPermission(permissionKey)
          )
          if (enabledOrgId) {
            if (!enabledOrgId.includes($org.id)) canShow = true
          }
        }
        return canShow
      })
    },
    activeTabGroups() {
      let { selectedTabGroup, tabGroups, accessibleMenu } = this
      let activeGroupsCount = 10 //No of active groups shown in sidebar
      let activeGroups = tabGroups.slice(0, activeGroupsCount)
      if (!isWebTabsEnabled()) {
        return accessibleMenu
          .slice(0, activeGroupsCount)
          .map((m, index) => ({ ...m, id: index }))
      } else {
        if (!isEmpty(selectedTabGroup)) {
          let isCurrentGroupInActive = activeGroups.some(
            group => group.id === selectedTabGroup.id
          )

          if (!isCurrentGroupInActive) {
            let nonSelectedActiveGroups = activeGroups.slice(
              0,
              activeGroupsCount - 1
            )
            activeGroups = [...nonSelectedActiveGroups, selectedTabGroup]
          }
        }
        return activeGroups
      }
    },
    moreTabGroups() {
      if (!isWebTabsEnabled()) {
        let { accessibleMenu, maxActiveSidebarModuleCount } = this
        let moreModulesList = accessibleMenu.slice(maxActiveSidebarModuleCount)
        let filteredMoreSideList = moreModulesList
        return filteredMoreSideList
      } else {
        let { activeTabGroups, tabGroups } = this
        let activeGroupsId = activeTabGroups.map(grp => grp.id)
        return (
          tabGroups.filter(group => !activeGroupsId.includes(group.id)) || []
        )
      }
    },
    canShowSettings() {
      if (isWebTabsEnabled()) {
        let app = getApp()
        let { user } = this.account || {}
        let { role } = user || {}

        if (app && app.hasSetupPermission) return app.hasSetupPermission
        else if (role) return role.isPrevileged
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
    appLinkName() {
      let { linkName } = getApp()
      return linkName
    },
    appName() {
      return window.location.pathname.slice(1).split('/')[0]
    },
    permissionSetup() {
      return getApp().hasSetupPermission === true
    },
  },
  methods: {
    loadDefaultProduct() {
      let currentPath = this.$route.path
      if (currentPath === '/app' || currentPath === '/app/') {
        let prd = this.accessibleMenu[0]
        let lastaccessedpage = Vue.cookie.get('lastvisit')
        if (lastaccessedpage) {
          this.$router.push(lastaccessedpage).catch(() => {})
        } else {
          this.$router.push({ path: prd.route }).catch(() => {})
        }
      } else {
        let { menu, activeTabGroups, maxActiveSidebarModuleCount } = this
        let currentMenu = activeTabGroups.find(tabGroup =>
          currentPath.startsWith(tabGroup.route)
        )
        if (!currentMenu) {
          let currentMenuIndex
          menu.forEach((tabGroup, index) => {
            if (currentPath.startsWith(tabGroup.route)) {
              currentMenuIndex = index
              currentMenu = tabGroup
            }
          })
          if (currentMenuIndex) {
            menu.splice(currentMenuIndex, 1)
            menu.splice(maxActiveSidebarModuleCount, 0, currentMenu)
          }
        }
      }
    },
    getTabRoute(item) {
      if (isWebTabsEnabled()) {
        let tabs = this.getOrderedTabs(item.webTabs)
        return findRouteForTab(tabs[0].id)
      } else {
        return { path: item.route }
      }
    },
    getIconPath(type) {
      let iconType = icons[type] || {}
      return iconType.icon || icons[0].icon
    },

    getIconClass(type) {
      let iconType = icons[type] || {}
      return (
        `icon new-icon vertical-baseline ${iconType.class}` ||
        'icon new-icon vertical-baseline'
      )
    },

    isTabActive(item) {
      let { selectedTabGroup } = this
      if (isEmpty(selectedTabGroup)) return false
      return item.id === selectedTabGroup.id
    },

    getOrderedTabs(tabs) {
      if (!tabs) {
        return []
      } else {
        return sortBy(tabs, ['order'])
      }
    },

    async redirectToTabFromGroup(group) {
      if (!this.isTabsEnabled) {
        let { menu, maxActiveSidebarModuleCount } = this

        let selectedModuleIndex = menu.findIndex(m => m.name === group.name)
        menu.splice(selectedModuleIndex, 1)
        menu.splice(maxActiveSidebarModuleCount, 0, group)
        if (!isEmpty(group)) {
          this.$router
            .push({
              path: group.route,
            })
            .catch(() => {})
        }
      } else {
        let { $router } = this
        let tabId = group.webTabs[0].id
        let route = findRouteForTab(tabId)
        if (route) $router.push(route).catch(console.warn)
        else $router.push({ path: '/*' }).catch(() => {})
      }
    },
    async redirectToTab(tabId) {
      let { $router } = this
      let route = findRouteForTab(tabId)

      if (route) $router.push(route).catch(console.warn)
      else $router.push({ path: '/*' }).catch(() => {})
    },
    removeFocus() {
      document.activeElement.blur()
    },
    getFcIconClass(item) {
      isEmpty(item.iconTypeEnum) ? 'fc-icon-hover' : 'icon-hover'
    },
    isIconActive(item) {
      return this.isTabActive(item) ? '#FF3184' : 'black'
    },
    keyDownHandler(e) {
      if (e.shiftKey && e.key === 'G') {
        let { id: selectedGrpId } = this.selectedTabGroup || {}
        let elementId = ''

        if (selectedGrpId) elementId = `sidebar-icon-${selectedGrpId}`
        else elementId = 'sidebar-icon-setting'
        if (e.target.localName === 'body') {
          document.getElementById(elementId).focus()
        }
      }
    },
  },
}
</script>
<style lang="scss">
// TODO CLEANUP AND SCOPE THESE STYLES
.icon-hover:hover,
.icon-hover:focus {
  background: rgb(202 212 216 / 0.5);
  padding: 4px 12px 18px 12px;
  margin-left: -12px;
  border-radius: 50%;
}
.fc-icon-hover:has(.fc-icon) {
  .icon-position {
    position: relative;
    top: 3px;
  }
}
.fc-icon-hover:hover:has(.fc-icon) {
  padding: 12px;
  background: rgb(202 212 216 / 0.5);
  margin-left: -12px;
  border-radius: 50%;
}
.fc-layout-aside {
  position: absolute;
  z-index: 12;
  height: 100%;
}
.sidebar-ht {
  height: calc(100vh - 60px) !important;
  box-shadow: none !important;
}
.fc-wt-sidebar {
  ul.fc-sidebar {
    width: 60px;
    float: left;
    height: 100vh;
    margin: 0;
    list-style: none;
    padding: 0;
    padding-bottom: 50px;
    overflow-y: scroll;
  }

  .fc-sidebar .active {
    border-left: 2px solid var(--fc-theme-color) !important;
  }

  .fc-sidebar li {
    padding: 15px 19px;
    font-size: 18px;
    cursor: pointer;
    text-align: center;
    border-left: 2px solid transparent;
    border-left: 2px solid transparent;
    background: none !important;
  }

  ul.fc-sidebar li.bottom {
    position: absolute;
    bottom: 0px;
  }

  ul.fc-sidebar li .svg-icon,
  ul.fc-sidebar li .svg-icon-active {
    width: 21.8px;
    height: 22px;
  }

  ul.fc-sidebar li.active .svg-icon {
    display: none;
  }
  ul.fc-sidebar li.active .svg-icon-active {
    display: inline-block;
    margin: 0 auto;
  }

  .fc-white-theme ul.fc-sidebar li.active {
    border-left: 2px solid var(--fc-theme-color) !important;
    background: none !important;
  }
}

.fc-white-theme ul.fc-sidebar li.active {
  border-left: 2px solid var(--fc-theme-color) !important;
  background: none !important;

  .icon {
    &.new-icon,
    &.sidebar-path-fill path {
      fill: #ff3184;
    }
    &.alarm-stroke-fill {
      fill: #ff3184;
      stroke: #ff3184;
    }
  }
}
.fc-white-theme ul.fc-sidebar li.active:hover .icon.new-icon {
  fill: #ff3184;
}
.fc-white-theme ul.fc-sidebar li.active:hover .icon.alarm-stroke-fill {
  fill: #a9aacb;
  stroke: #a9aacb;
}
.fc-white-theme ul.fc-sidebar li:hover .icon.new-icon,
.fc-white-theme ul.fc-sidebar li:hover .icon.sidebar-path-fill path {
  fill: #a9aacb;
}
.fc-white-theme ul.fc-sidebar li:hover .icon.alarm-stroke-fill {
  fill: #a9aacb;
  stroke: #a9aacb;
}

.icon.new-icon,
.icon.sidebar-path-fill path,
.fc-white-theme ul.fc-sidebar li.active:hover .icon.sidebar-path-fill path {
  fill: #a9aacb;
}
.alarm-stroke-fill {
  stroke: #a9aacb;
}

.sidebar-help-icon {
  position: absolute;
  bottom: 62px;
}
.more-icon-list {
  padding: 0px !important;

  .more-icon {
    border: none;
    padding: 15px 19px;
  }
  .more-icon:hover,
  .more-icon:active,
  .more-icon:focus {
    background: none;
  }
}
.sidebar-panel {
  padding: 5px 0px;

  .tabs {
    cursor: pointer;
    padding: 10px 20px;
    margin: 0px 1px;
    border-left: 3px solid transparent;
  }
  .tabs-header {
    cursor: default;
    font-size: 16px;
    color: #1d384e;
    border-bottom: 1px solid #e3e6e8;
    font-weight: bolder;
    padding: 10px 20px;
    margin: 0px 1px;
    border-left: 3px solid transparent;
  }
  .tabs:hover,
  .groups:hover {
    background: #f3f4f7;
  }

  .tabs.active {
    border-left: 3px solid #ef508f;
    background: #f3f4f7;
  }
  .groups {
    cursor: pointer;
    padding: 10px 30px 10px 20px;
    border-left: 3px solid transparent;
  }
}
.fc-sidebar-list-con {
  height: 100vh;
  background: white;
  box-shadow: 0 1px 1px rgb(0 0 0 / 10%);
  -webkit-box-shadow: 0 1px 1px rgb(0 0 0 / 10%);
}
</style>
