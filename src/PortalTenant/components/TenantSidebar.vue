<template>
  <el-aside width="250px" class="fc-v1-portal-sidebar pT0 d-flex flex-col">
    <div class="d-flex justify-center width100">
      <a @click="onLogoClick" class="mT15 pL30 pR30 pointer">
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
    <div class="d-flex justify-center">
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
            class="pointer list-item list-hover pT10 pB10 pL15 pR30"
            tag="li"
          >
            <InlineSvg
              :src="getIconPath(group.iconType)"
              iconClass="icon icon-md vertical-middle mR10"
            ></InlineSvg>
            {{ group.name }}
          </router-link>
        </template>
        <template v-else>
          <div :key="group.id" class="d-flex flex-col">
            <div
              class="list-item pT10 pB10 pL15 pR10 pointer"
              style="border: 3px solid transparent;"
              @click="expandGroup(group)"
            >
              <InlineSvg
                :src="getIconPath(group.iconType)"
                iconClass="icon icon-md vertical-middle mR10"
              ></InlineSvg>
              {{ group.name }}
              <i
                :class="{
                  'el-icon-arrow-up fR f12 mT5': true,
                  rotate180: group.id !== selectedGroupId,
                }"
              ></i>
            </div>
            <el-collapse-transition>
              <div
                class="d-flex flex-col pointer"
                v-show="group.id === selectedGroupId"
              >
                <router-link
                  v-for="tab in group.webTabs"
                  :key="tab.id"
                  :to="findRouteForTab(tab.id)"
                  class="pointer list-item list-item-small list-hover pT10 pB10 pL45 pR10"
                >
                  {{ tab.name }}
                </router-link>
              </div>
            </el-collapse-transition>
          </div>
        </template>
      </template>
    </ul>

    <div class="flex-grow flex-shrink" @click="expandGroup"></div>

    <div class="p30 pointer">
      <el-dropdown @command="profileAction">
        <user-avatar size="lg" :user="$portaluser"></user-avatar>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item v-if="canShowPrivacyPolicy" command="privacypolicy">
            <a
              :href="privacyPolicyLink"
              target="_blank"
              class="tenant-sidebar-link-text"
              >{{ $t('tenant.announcement.privacy_policy') }}</a
            >
          </el-dropdown-item>
          <el-dropdown-item
            v-if="canShowTermsOfService"
            command="termsofservice"
          >
            <a
              :href="termsOfServiceLink"
              target="_blank"
              class="tenant-sidebar-link-text"
              >{{ $t('tenant.announcement.terms_of_service') }}</a
            >
          </el-dropdown-item>
          <el-dropdown-item divided command="myprofile">
            {{ $t('tenant.announcement.my_profile') }}
          </el-dropdown-item>
          <el-dropdown-item command="logout" @change="logout">{{
            $t('tenant.announcement.logout')
          }}</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </el-aside>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
import { mapState, mapGetters } from 'vuex'
import sortBy from 'lodash/sortBy'
import { findTab } from '@facilio/router'
import icons from 'newapp/webtab-icons.js'
import {
  getApp,
  findRouteForModule,
  findRouteForTab,
  pageTypes,
  tabTypes,
} from '@facilio/router'
import homeMixin from 'src/PortalTenant/auth/portalHomeMixin'
import UserAvatar from '@/avatar/User'
import RebrandMixin from 'util/rebrandMixin'
import { API } from '@facilio/api'
import { getAppDomain } from 'src/util/helpers'

export default {
  mixins: [homeMixin, RebrandMixin],
  components: {
    UserAvatar,
  },
  data() {
    return {
      openGroupId: null,
      brandName: '',
      canShowPrivacyPolicy: false,
      canShowTermsOfService: false,
    }
  },
  mounted() {
    // if (window.rebrandInfo && window.rebrandInfo.brandName) {
    //   this.brandName = window.rebrandInfo.brandName
    // }
    if (window.brandConfig && window.brandConfig.name) {
      this.brandName = window.brandConfig.name
    }
    this.setCanShowPrivacyPolicy()
    this.setCanShowTermsOfService()
  },
  computed: {
    ...mapGetters('webtabs', ['getTabGroups', 'isAppPrefEnabled']),
    ...mapState('webtabs', ['selectedTabGroup']),
    appName() {
      return getApp().linkName || 'tenant'
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
    privacyPolicyLink() {
      return getAppDomain() + '/auth/privacy-policy'
    },
    termsOfServiceLink() {
      return getAppDomain() + '/auth/terms-of-service'
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
    expandGroup({ id } = {}) {
      if (!id || this.selectedGroupId === id) {
        this.selectedGroupId = null
      } else {
        this.selectedGroupId = id
      }
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
      let hasPermission = false

      if (tab === 'service_catelog') {
        return findRouteForTab(tabTypes.CUSTOM, {
          config: {
            type: 'serviceCatalog',
          },
        })
      } else if (tab === 'module') {
        let { tabId } = findTab(tabTypes.MODULE, { moduleName })
        hasPermission = this.$hasPermission(`${moduleName}:CREATE`, tabId)
        if (hasPermission) {
          return findRouteForModule(moduleName, pageTypes.CREATE)
        }
      } else {
        if (['tenant', 'service'].includes(this.appName)) {
          let { tabId } = findTab(tabTypes.MODULE, { moduleName: 'workorder' })
          if (!isEmpty(tabId)) {
            hasPermission = this.$hasPermission('workorder:CREATE', tabId)
            if (hasPermission) {
              return findRouteForModule('workorder', pageTypes.CREATE)
            }
          }
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
    async setCanShowPrivacyPolicy() {
      let url = '/integ/privacyPolicy'
      try {
        let { data } = await API.get(url)
        if (!isEmpty(data)) {
          let showInMenu = this.$getProperty(
            data,
            'result.privacyPolicy.showInMenu',
            false
          )
          this.canShowPrivacyPolicy = showInMenu
        }
      } catch (e) {
        this.canShowPrivacyPolicy = false
      }
    },
    async setCanShowTermsOfService() {
      let url = '/integ/termsOfService'
      try {
        let { data } = await API.get(url)
        if (!isEmpty(data)) {
          let showInMenu = this.$getProperty(
            data,
            'result.termsOfService.showInMenu',
            false
          )
          this.canShowTermsOfService = showInMenu
        }
      } catch (e) {
        this.canShowTermsOfService = false
      }
    },
  },
}
</script>
<style lang="scss" scoped>
.fc-v1-portal-sidebar {
  height: 100vh;
  overflow: hidden;
  background: #ffffff;
  box-sizing: border-box;
  position: relative;
  box-shadow: 0 5px 8px 0 rgba(0, 0, 0, 0.06);
  z-index: 10;

  .tenant-portal-sub-btn {
    background-color: #017aff;
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
    margin: 20px 0px;

    &:hover,
    &:active,
    &:focus {
      color: #ffffff;
      background-color: #017aff;
    }
  }

  .tenantportal-sidebar-list {
    overflow-y: scroll;
    list-style-type: none;
    padding-left: 0;
    margin-bottom: 0 !important;
    margin-top: 0 !important;

    .router-link-exact-active,
    .active {
      color: #017aff;
      background-color: rgba(1, 122, 255, 0.15);
      border-left: 3px solid #017aff !important;
    }

    li {
      font-size: 14px;
      font-weight: normal;
      line-height: normal;
      letter-spacing: 0.3px;
      color: #324056;

      &:hover {
        color: #017aff;
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
          color: #017aff;
          font-weight: 500;
        }
      }
    }

    .list-item {
      font-weight: normal;
      line-height: normal;
      letter-spacing: 0.44px;
      color: #324056;
      font-size: 15px;

      &.list-item-small {
        font-size: 13px;
      }
    }
    .list-hover {
      border: 3px solid transparent;

      &:hover {
        background-color: #f2f3f7;
        color: #324056;
        border-left: 3px solid #017aff;
        font-weight: 400;
      }
    }
  }
}
.tenant-sidebar-link-text {
  color: #324056 !important;
}
</style>
