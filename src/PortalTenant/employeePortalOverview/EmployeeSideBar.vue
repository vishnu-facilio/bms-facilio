<template>
  <el-aside
    :width="sidebarWidth"
    class="fc-v1-portal-sidebar pT0 d-flex flex-col"
    v-bind:class="{ collapsed: collapsed }"
  >
    <div
      class="collapse-icon show-hide-icon"
      @click="setStateForSidebar"
      v-bind:class="{ collapsed: collapsed }"
    >
      <InlineSvg
        :src="'svgs/employeePortal/collpase-icon'"
        iconClass="icon icon-sm17"
      ></InlineSvg>
    </div>
    <div class="d-flex justify-center width100">
      <a @click="onLogoClick" class="emp-sidebar-logo">
        <img
          v-if="$getProperty($account, 'org.logoUrl')"
          :src="getLogoUrl($account.org)"
          height="80"
          width="100%"
          class="object-scale-down"
        />
        <img
          height="80"
          width="220"
          class="object-scale-down"
          v-else-if="this.brandName == 'Moro'"
          src="~assets/facilio-moro.png"
        />
        <img
          v-else
          src="~assets/facilio-blue-logo.svg"
          height="80"
          width="100%"
          class="object-scale-down"
        />
      </a>
    </div>
    <div class="d-flex mT10 mB20 p10 justify-center" v-if="getRequestRoute()">
      <router-link
        v-if="getRequestRoute()"
        :to="getRequestRoute()"
        class="tenant-portal-sub-btn"
      >
        {{ $t('tenant.announcement.submit_req') }}
      </router-link>
    </div>

    <ul class="tenantportal-sidebar-list">
      <template v-for="group in filteredMenu">
        <template v-if="group.webTabs.length === 1">
          <router-link
            :key="group.webTabs[0].id"
            :to="findRouteForTab(group.webTabs[0].id)"
            class="pointer list-item list-hover pT10 pB10 pL15 pR15  "
            :class="{ 'textoverflow-ellipsis': !collapsed }"
            tag="li"
          >
            <InlineSvg
              :src="getIconPath(group.iconType)"
              iconClass="icon icon-xxll vertical-middle mR10"
            ></InlineSvg>

            {{ !collapsed && showNames ? group.name : '' }}
          </router-link>
        </template>
        <template v-else>
          <div :key="group.id" class="d-flex flex-col">
            <div
              v-if="!collapsed"
              class="list-item pT10 pB10 pL15 pR10 pointer m0"
              style="border-left: 3px solid transparent;"
              @click="expandGroup(group)"
            >
              <InlineSvg
                :src="getIconPath(group.iconType)"
                iconClass="icon icon-xxll vertical-middle mR10"
              ></InlineSvg>
              {{ !collapsed && showNames ? group.name : '' }}
              <i
                v-if="!collapsed"
                :class="{
                  'el-icon-arrow-up fR f12 mT5': true,
                  rotate180: group.id !== selectedGroupId,
                }"
              ></i>
            </div>
            <el-popover
              placement="right"
              width="200"
              trigger="hover"
              popper-class="p0 employee-portal-sidebar-popover"
              v-if="collapsed"
            >
              <div
                class="d-flex flex-col pointer popover-side-bar"
                v-show="group.id === selectedGroupId"
              >
                <router-link
                  v-for="tab in group.webTabs"
                  :key="tab.id"
                  :to="findRouteForTab(tab.id)"
                  style="border-bottom:1px solid #ebeff3"
                  class="pointer list-item list-item-small list-hover pT10 pB10 pL20 pR10 m0 "
                  :class="{ 'textoverflow-ellipsis': !collapsed }"
                >
                  {{ tab.name }}
                </router-link>
              </div>
              <div
                class="list-item pT10 pB10 pL15 pR10 pointer m0"
                style="border-left: 3px solid transparent;"
                @click="expandGroup(group)"
                @mouseenter="handleHoverexpandGroup(group)"
                slot="reference"
              >
                <InlineSvg
                  :src="getIconPath(group.iconType)"
                  iconClass="icon icon-xxll vertical-middle mR10"
                ></InlineSvg>
                {{ !collapsed && showNames ? group.name : '' }}
                <i
                  v-if="!collapsed"
                  :class="{
                    'el-icon-arrow-up fR f12 mT5': true,
                    rotate180: group.id !== selectedGroupId,
                  }"
                ></i>
              </div>
            </el-popover>
            <el-collapse-transition v-if="!collapsed">
              <div
                class="d-flex flex-col pointer"
                v-show="group.id === selectedGroupId"
              >
                <router-link
                  v-for="tab in group.webTabs"
                  :key="tab.id"
                  :to="findRouteForTab(tab.id)"
                  style="border-bottom:1px solid #ebeff3"
                  class="pointer list-item list-item-small list-hover pT10 pB10 pL52 pR10 m0 "
                  :class="{ 'textoverflow-ellipsis': !collapsed }"
                >
                  {{ !collapsed && showNames ? tab.name : '' }}
                </router-link>
              </div>
            </el-collapse-transition>
          </div>
        </template>
      </template>
    </ul>
  </el-aside>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
import { mapState, mapGetters } from 'vuex'
import sortBy from 'lodash/sortBy'
import icons from 'newapp/webtab-icons.js'
import {
  getApp,
  findRouteForModule,
  findRouteForTab,
  pageTypes,
  tabTypes,
} from '@facilio/router'
import homeMixin from 'src/PortalTenant/auth/portalHomeMixin'
import RebrandMixin from 'util/rebrandMixin'

export default {
  mixins: [homeMixin, RebrandMixin],

  data() {
    return {
      openGroupId: null,
      brandName: '',
      collapsed: false,
      showNames: true,
    }
  },
  mounted() {
    // if (window.rebrandInfo && window.rebrandInfo.brandName) {
    //   this.brandName = window.rebrandInfo.brandName
    // }
    if (window.brandConfig && window.brandConfig.name) {
      this.brandName = window.brandConfig.name
    }
    this.getStateForSideBar()
  },
  computed: {
    ...mapGetters('webtabs', ['getTabGroups', 'isAppPrefEnabled']),
    ...mapState('webtabs', ['selectedTabGroup']),
    appName() {
      return getApp().linkName || 'tenant'
    },
    sidebarWidth() {
      return this.collapsed ? '60px' : '200px'
    },
    filteredMenu() {
      let groups = sortBy(this.getTabGroups(), ['order'])

      return groups
        .map(group => {
          return {
            ...group,
            webTabs: sortBy(group.webTabs || [], ['order']),
          }
        })
        .filter(group => !isEmpty(group.webTabs))
    },
    selectedGroupId: {
      get() {
        return (
          this.openGroupId ||
          this.$getProperty(this.selectedTabGroup, 'id', null)
        )
      },
      set(value) {
        this.openGroupId = value
      },
    },
  },
  watch: {
    selectedTabGroup: {
      handler(value) {
        if (value) this.openGroup = value.id
      },
      immediate: true,
    },
  },
  methods: {
    findRouteForTab,
    getIconPath(type) {
      let iconType = icons[type] || {}
      return iconType.icon || icons[200].icon
    },
    getStateForSideBar() {
      let collapsed = window.localStorage.getItem('PORTAL_SIDEBAR_COLLPASE')
      if (collapsed === 'true') {
        this.collapsed = true
        this.showNames = false
      } else {
        this.collapsed = false
        this.showNames = true
      }
    },
    setStateForSidebar() {
      this.collapsed = !this.collapsed
      window.localStorage.setItem('PORTAL_SIDEBAR_COLLPASE', this.collapsed)
      setTimeout(() => {
        this.showNames = !this.showNames
      }, 500)
    },
    expandGroup({ id } = {}) {
      if (!id || this.selectedGroupId === id) {
        this.selectedGroupId = null
      } else {
        this.selectedGroupId = id
      }
    },
    handleHoverexpandGroup({ id } = {}) {
      this.selectedGroupId = id
    },
    getLogoUrl(org) {
      if (org && org.logoUrl) {
        return this.$prependBaseUrl(org.logoUrl)
      }
      return null
    },
    profileAction(command) {
      if (command === 'myprofile') {
        this.$router.push({
          path: `/${this.appName}/profile`,
        })
      } else if (command === 'logout') {
        this.logout()
      }
    },
    getRequestRoute() {
      let { tab, moduleName } =
        this.isAppPrefEnabled('canShowSubmitRequest') || {}

      if (tab === 'service_catelog') {
        return findRouteForTab(tabTypes.CUSTOM, {
          config: {
            type: 'serviceCatalog',
          },
        })
      } else if (tab === 'module') {
        return findRouteForModule(moduleName, pageTypes.CREATE)
      } else {
        if (['tenant', 'service'].includes(this.appName)) {
          return findRouteForModule('workorder', pageTypes.CREATE)
        }
        return false
      }
    },
    getRouteForFirstTab() {
      let initialGroup = this.filteredMenu[0]
      let { id: tabId } = initialGroup.webTabs[0]
      return findRouteForTab(tabId)
    },
    getLoginRoute() {
      return { name: 'login' }
    },
    onLogoClick() {
      let { name } = this.$portaluser
        ? this.getRouteForFirstTab()
        : this.getLoginRoute()

      name && this.$router.push({ name })
    },
  },
}
</script>
<style lang="scss" scoped>
.show-hide-icon {
  // display: none;
  opacity: 0;
  box-shadow: rgb(9 30 66 / 8%) 0px 0px 4px 1px;
}
.fc-v1-portal-sidebar:hover .show-hide-icon {
  opacity: 1;
  // display: inline-block;
  transition: opacity 0.3s linear;
}
.pL52 {
  padding-left: 52px;
}
.employee-portal-sidebar-popover .popover-side-bar,
.employee-portal-sidebar-popover .popover-side-bar .list-hover {
  font-size: 14px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: normal;
  text-align: justify;
  color: #12324a;
}
.employee-portal-sidebar-popover .popover-side-bar .list-hover:hover {
  font-size: 14px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: normal;
  text-align: justify;
  color: #0053cc;
  background-color: #f8fbff;
}
.collapse-icon:hover {
  background: rgb(232 234 235);
  color: #fff;
}
.emp-sidebar-logo {
  margin-top: 15px;
  padding-left: 30px;
  padding-right: 30px;
  cursor: pointer;
}
.collapsed .emp-sidebar-logo {
  margin-top: 0px;
  padding: 10px;
  cursor: pointer;
  padding-top: 0;
  padding-bottom: 0;
}
.collapse-icon {
  position: fixed;
  left: 190px;
  z-index: 2;
  width: 20px;
  height: 20px;
  top: 18px;
  background: #f0f0f0;
  border-radius: 10px;
  border: 1px solid #dcdcdc;
  cursor: pointer;
}
.collapse-icon.collapsed {
  left: 50px;
  transform: rotate(180deg);
}
.fc-v1-portal-sidebar {
  height: 100vh;
  overflow: hidden;
  background: #ffffff;
  box-sizing: border-box;
  position: relative;
  z-index: 2;
  border-right: 1px solid #ebedf4;
  transition: width 0.3s ease 0s;

  .tenant-portal-sub-btn {
    background-color: #deedff;
    font-size: 14px;
    font-weight: 500;
    font-stretch: normal;
    font-style: normal;
    line-height: normal;
    letter-spacing: 0.44px;
    color: #ffffff;
    text-transform: uppercase;
    text-decoration: none;
    padding: 15px 30px;
    border-radius: 3px;
    display: inline-block;

    &:hover,
    &:active,
    &:focus {
      color: #ffffff;
      background-color: #1d344d;
    }
  }

  .tenantportal-sidebar-list,
  .employee-portal-sidebar-popover {
    overflow-y: scroll;
    list-style-type: none;
    padding-left: 0;
    margin-bottom: 0 !important;
    margin-top: 0 !important;

    .router-link-exact-active,
    .active {
      color: #deedff;
      background-color: #deedff !important;
      border-left: 3px solid #0053cc !important;
    }

    li {
      font-size: 14px;
      font-weight: normal;
      line-height: normal;
      letter-spacing: 0.3px;
      color: #324056;

      &:hover {
        color: #deedff;
        font-weight: 500;
        cursor: pointer;
      }

      > a {
        font-size: 14px;
        font-weight: normal;
        line-height: normal;
        letter-spacing: 0.3px;
        color: #324056;

        &:hover {
          color: #deedff;
          font-weight: 500;
        }
      }
    }

    .list-item {
      font-size: 14px;
      font-weight: normal;
      font-stretch: normal;
      font-style: normal;
      line-height: normal;
      letter-spacing: normal;
      text-align: justify;
      color: #12324a;
      padding-bottom: 14.5px !important;
      padding-top: 14.5px !important;

      &.list-item-small {
        font-size: 14px;
      }

      &:hover {
        border-left: 3px solid #0053cc !important;
        font-size: 14px;
        font-weight: normal;
        font-stretch: normal;
        font-style: normal;
        line-height: normal;
        letter-spacing: normal;
        text-align: justify;
        color: #0053cc;
        background-color: #f8fbff;
      }
    }
    .list-hover {
      border-left: 3px solid transparent;

      &:hover {
        font-size: 14px;
        font-weight: normal;
        font-stretch: normal;
        font-style: normal;
        line-height: normal;
        letter-spacing: normal;
        text-align: justify;
        color: #0053cc;
        background-color: #f8fbff;
      }
    }
    .list-item.active {
      color: #12324a;
      font-weight: 500;
      path {
        fill: #12324a;
      }
    }
  }
  .bottom {
    position: absolute;
    bottom: 10px;
  }
}
</style>
